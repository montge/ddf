#!/usr/bin/env python3
"""
OWASP Dependency-Check Vulnerability Analyzer

Parses HTML/JSON reports to identify top priority security vulnerabilities.
"""

import json
import re
from pathlib import Path
from dataclasses import dataclass
from typing import List, Dict, Set
from collections import defaultdict
import sys

@dataclass
class Vulnerability:
    """Represents a CVE vulnerability"""
    cve: str
    cvss_score: float
    severity: str
    dependency: str
    current_version: str
    description: str
    is_direct: bool = False
    recommended_version: str = ""

    def __hash__(self):
        return hash(self.cve)

    def __eq__(self, other):
        return isinstance(other, Vulnerability) and self.cve == other.cve

@dataclass
class DependencyReport:
    """Aggregated vulnerability report for a dependency"""
    name: str
    current_version: str
    vulnerabilities: List[Vulnerability]
    is_direct: bool
    max_cvss: float
    critical_count: int
    high_count: int
    medium_count: int
    low_count: int
    recommended_version: str = ""
    compatibility_risk: str = "UNKNOWN"
    notes: str = ""


def parse_json_report(json_path: Path) -> List[Vulnerability]:
    """Parse JSON format dependency-check report"""
    vulnerabilities = []

    with open(json_path) as f:
        data = json.load(f)

    dependencies = data.get('dependencies', [])

    for dep in dependencies:
        dep_name = dep.get('fileName', 'unknown')

        # Extract version from fileName or filePath
        version_match = re.search(r'[-_](\d+\.\d+[\.\d]*)', dep_name)
        current_version = version_match.group(1) if version_match else 'unknown'

        # Check if direct dependency
        is_direct = dep.get('isVirtual', False) == False

        for vuln in dep.get('vulnerabilities', []):
            cve = vuln.get('name', 'NO-CVE')
            cvss_v3 = vuln.get('cvssv3', {})
            cvss_v2 = vuln.get('cvssv2', {})

            # Prefer CVSS v3, fallback to v2
            if cvss_v3:
                cvss_score = cvss_v3.get('baseScore', 0.0)
            elif cvss_v2:
                cvss_score = cvss_v2.get('score', 0.0)
            else:
                cvss_score = 0.0

            severity = vuln.get('severity', 'UNKNOWN')
            description = vuln.get('description', '')

            vulnerabilities.append(Vulnerability(
                cve=cve,
                cvss_score=float(cvss_score),
                severity=severity,
                dependency=dep_name,
                current_version=current_version,
                description=description,
                is_direct=is_direct
            ))

    return vulnerabilities


def categorize_severity(cvss_score: float) -> str:
    """Categorize CVSS score into severity"""
    if cvss_score >= 9.0:
        return "CRITICAL"
    elif cvss_score >= 7.0:
        return "HIGH"
    elif cvss_score >= 4.0:
        return "MEDIUM"
    elif cvss_score > 0:
        return "LOW"
    return "NONE"


def aggregate_by_dependency(vulnerabilities: List[Vulnerability]) -> List[DependencyReport]:
    """Group vulnerabilities by dependency and create summary reports"""
    dep_map = defaultdict(list)

    for vuln in vulnerabilities:
        key = (vuln.dependency, vuln.current_version)
        dep_map[key].append(vuln)

    reports = []
    for (dep_name, version), vulns in dep_map.items():
        critical = sum(1 for v in vulns if v.cvss_score >= 9.0)
        high = sum(1 for v in vulns if 7.0 <= v.cvss_score < 9.0)
        medium = sum(1 for v in vulns if 4.0 <= v.cvss_score < 7.0)
        low = sum(1 for v in vulns if 0 < v.cvss_score < 4.0)
        max_cvss = max(v.cvss_score for v in vulns)

        is_direct = any(v.is_direct for v in vulns)

        reports.append(DependencyReport(
            name=dep_name,
            current_version=version,
            vulnerabilities=vulns,
            is_direct=is_direct,
            max_cvss=max_cvss,
            critical_count=critical,
            high_count=high,
            medium_count=medium,
            low_count=low
        ))

    return reports


def assess_compatibility_risk(dep_name: str, current_version: str) -> str:
    """Assess compatibility risk for upgrading dependency"""

    # Known high-risk dependencies (tied to Karaf/Jetty versions)
    high_risk_patterns = [
        'jetty', 'karaf', 'cxf', 'camel'
    ]

    # Medium risk - commonly stable APIs
    medium_risk_patterns = [
        'jackson', 'commons', 'guava', 'slf4j'
    ]

    dep_lower = dep_name.lower()

    for pattern in high_risk_patterns:
        if pattern in dep_lower:
            return "HIGH"

    for pattern in medium_risk_patterns:
        if pattern in dep_lower:
            return "MEDIUM"

    return "LOW"


def filter_priority_vulnerabilities(reports: List[DependencyReport],
                                   min_cvss: float = 7.0) -> List[DependencyReport]:
    """Filter for CRITICAL and HIGH severity vulnerabilities"""
    return [r for r in reports if r.max_cvss >= min_cvss]


def generate_markdown_report(reports: List[DependencyReport], output_path: Path):
    """Generate detailed markdown report"""

    # Sort by severity, then by number of CVEs
    sorted_reports = sorted(
        reports,
        key=lambda r: (-r.max_cvss, -(r.critical_count + r.high_count))
    )

    with open(output_path, 'w') as f:
        f.write("# OWASP Dependency-Check Vulnerability Analysis\n\n")
        f.write(f"**Total Dependencies with Vulnerabilities:** {len(reports)}\n\n")

        total_critical = sum(r.critical_count for r in reports)
        total_high = sum(r.high_count for r in reports)
        total_medium = sum(r.medium_count for r in reports)

        f.write(f"**Severity Breakdown:**\n")
        f.write(f"- CRITICAL (CVSS >= 9.0): {total_critical}\n")
        f.write(f"- HIGH (CVSS 7.0-8.9): {total_high}\n")
        f.write(f"- MEDIUM (CVSS 4.0-6.9): {total_medium}\n\n")

        f.write("---\n\n")
        f.write("## Top 10 Priority Vulnerabilities to Fix\n\n")

        for i, report in enumerate(sorted_reports[:10], 1):
            # Assess compatibility risk
            report.compatibility_risk = assess_compatibility_risk(
                report.name, report.current_version
            )

            f.write(f"### {i}. {report.name}\n\n")
            f.write(f"**Current Version:** `{report.current_version}`\n\n")
            f.write(f"**Dependency Type:** {'Direct' if report.is_direct else 'Transitive'}\n\n")
            f.write(f"**Highest CVSS Score:** {report.max_cvss}\n\n")
            f.write(f"**Vulnerability Count:**\n")
            if report.critical_count:
                f.write(f"- CRITICAL: {report.critical_count}\n")
            if report.high_count:
                f.write(f"- HIGH: {report.high_count}\n")
            if report.medium_count:
                f.write(f"- MEDIUM: {report.medium_count}\n")
            f.write("\n")

            f.write(f"**CVEs:**\n")
            for vuln in sorted(report.vulnerabilities, key=lambda v: -v.cvss_score)[:5]:
                f.write(f"- {vuln.cve} (CVSS {vuln.cvss_score}) - {vuln.severity}\n")
            if len(report.vulnerabilities) > 5:
                f.write(f"- ... and {len(report.vulnerabilities) - 5} more\n")
            f.write("\n")

            f.write(f"**Upgrade Compatibility Risk:** {report.compatibility_risk}\n\n")

            f.write(f"**Recommendation:**\n")
            if report.compatibility_risk == "HIGH":
                f.write("⚠️ High risk upgrade - requires thorough testing. ")
                f.write("May be blocked by platform constraints (Karaf/Jetty versions).\n\n")
            elif report.compatibility_risk == "MEDIUM":
                f.write("⚡ Medium risk - review API changes before upgrading.\n\n")
            else:
                f.write("✅ Low risk - likely safe to upgrade with standard testing.\n\n")

            f.write("---\n\n")


def main():
    base_path = Path(__file__).parent
    report_path = base_path / "target" / "dependency-check-report.json"

    if not report_path.exists():
        print(f"❌ Report not found: {report_path}")
        print("Run: mvn org.owasp:dependency-check-maven:aggregate -Dformats=JSON")
        sys.exit(1)

    print(f"📊 Parsing report: {report_path}")
    vulnerabilities = parse_json_report(report_path)

    if not vulnerabilities:
        print("✅ No vulnerabilities found!")
        return

    print(f"Found {len(vulnerabilities)} total vulnerabilities")

    # Aggregate by dependency
    reports = aggregate_by_dependency(vulnerabilities)
    print(f"Affecting {len(reports)} dependencies")

    # Filter for high/critical only
    priority_reports = filter_priority_vulnerabilities(reports, min_cvss=7.0)
    print(f"Priority (CVSS >= 7.0): {len(priority_reports)} dependencies")

    # Generate report
    output_path = base_path / "vulnerability-priority-analysis.md"
    generate_markdown_report(priority_reports, output_path)

    print(f"\n✅ Report generated: {output_path}")

    # Print summary
    print("\n" + "="*60)
    print("TOP 10 PRIORITY FIXES")
    print("="*60)

    for i, report in enumerate(sorted(priority_reports,
                                      key=lambda r: (-r.max_cvss, -r.critical_count))[:10], 1):
        risk = assess_compatibility_risk(report.name, report.current_version)
        print(f"{i:2}. {report.name} v{report.current_version}")
        print(f"    CVSS: {report.max_cvss} | C:{report.critical_count} H:{report.high_count} | Risk: {risk}")
        print()


if __name__ == "__main__":
    main()
