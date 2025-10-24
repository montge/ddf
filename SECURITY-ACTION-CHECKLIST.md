# DDF Security Vulnerability Action Checklist
**Date:** October 23, 2025
**Priority:** IMMEDIATE ACTION REQUIRED

---

## Current Status Summary

### ✅ FIXED (8 vulnerabilities - Recent commits)
- [x] Commons Text 1.14.0 (CVE-2022-42889 "Text4Shell" RCE)
- [x] Logback 1.2.13 (CVE-2021-42550 JNDI Injection)
- [x] Jetty 9.4.58 (Multiple HTTP/2 DoS vulnerabilities)
- [x] Jackson 2.17.3 (Multiple deserialization issues)
- [x] Spring 5.3.39 (25 security patches)
- [x] Apache Shiro 1.13.0 (CVE-2022-40664 Auth Bypass)
- [x] Apache CXF 3.5.8 (CVE-2022-46364 SSRF)
- [x] Groovy 4.0.23 (CVE-2016-6814 Deserialization)
- [x] OIDC at_hash validation (Token substitution attack)

### 🔴 CRITICAL - Remaining (5 vulnerabilities)
- [ ] Log4j 1.x removal (3 RCE CVEs - EOL)
- [ ] GeoTools 24.6 → 28.6.1 (XXE + XPath RCE)
- [ ] Apache Karaf 4.3.7 → 4.3.8 (Code Injection)
- [ ] Apache Solr 9.0.0 → 9.4.1 (ConfigSet RCE)
- [ ] Commons Configuration2 2.7 → 2.8.0 (Code Injection)

---

## Week 1: Quick Wins (2-4 days total)

### Day 1: Apache Karaf Upgrade ⚡ EASY
**Effort:** 2-4 hours
**Impact:** Removes 11 critical vulnerabilities

```bash
# 1. Create branch
git checkout -b security/karaf-4.3.8

# 2. Edit pom.xml
sed -i 's/<karaf.version>4.3.7<\/karaf.version>/<karaf.version>4.3.8<\/karaf.version>/' pom.xml

# 3. Build and test
mvn clean install -DskipTests
mvn test

# 4. Verify upgrade
mvn dependency:tree | grep "karaf.*4.3.8"

# 5. Commit
git add pom.xml
git commit -m "Security: Upgrade Apache Karaf to 4.3.8 (CVE-2022-40145)"
```

**Testing Checklist:**
- [ ] Clean build succeeds
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Karaf container starts
- [ ] Features install correctly
- [ ] Admin Console accessible

---

### Day 2: Commons Configuration2 Upgrade ⚡ EASY
**Effort:** 2-4 hours
**Impact:** Removes 1 critical vulnerability

```bash
# 1. Create branch (or continue on same branch)
git checkout -b security/commons-config2-2.8.0

# 2. Edit pom.xml
sed -i 's/<commons-configuration2.version>2.7<\/commons-configuration2.version>/<commons-configuration2.version>2.8.0<\/commons-configuration2.version>/' pom.xml

# 3. Build and test
mvn clean install -DskipTests
mvn test

# 4. Verify upgrade
mvn dependency:tree | grep "commons-configuration2.*2.8.0"

# 5. Commit
git add pom.xml
git commit -m "Security: Upgrade Commons Configuration2 to 2.8.0 (CVE-2022-33980)"
```

**Testing Checklist:**
- [ ] Clean build succeeds
- [ ] Unit tests pass
- [ ] Configuration loading works
- [ ] Property substitution works
- [ ] Admin UI configuration management works

---

### Day 3-4: Audit Log4j 1.x Dependencies
**Effort:** 8-16 hours
**Impact:** Preparation for removal (120 vulnerability alerts)

```bash
# 1. Find all Log4j 1.x dependencies
mvn dependency:tree | grep "log4j:log4j" > /tmp/log4j-1x-deps.txt

# 2. Analyze which dependencies pull in Log4j 1.x
cat /tmp/log4j-1x-deps.txt

# 3. Create exclusion plan for each dependency
# Example:
# If you find: some-library → log4j:log4j:1.2.17
# Then add exclusion:
<dependency>
  <groupId>some-library</groupId>
  <artifactId>some-artifact</artifactId>
  <version>x.y.z</version>
  <exclusions>
    <exclusion>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
    </exclusion>
  </exclusions>
</dependency>

# 4. Document findings
echo "Found $(grep -c 'log4j:log4j' /tmp/log4j-1x-deps.txt) Log4j 1.x references"
```

**Deliverable:**
- [ ] Complete list of dependencies pulling in Log4j 1.x
- [ ] Exclusion strategy documented
- [ ] Risk assessment completed

---

## Week 2-3: Apache Solr Upgrade (5-7 days)

### Apache Solr 9.0.0 → 9.4.1
**Effort:** 1 week
**Impact:** Multiple high-severity fixes including ConfigSet RCE

```bash
# 1. Create branch
git checkout -b security/solr-9.4.1

# 2. Edit pom.xml
sed -i 's/<solr.version>9.0.0<\/solr.version>/<solr.version>9.4.1<\/solr.version>/' pom.xml

# 3. Build
mvn clean install -DskipTests

# 4. Check for conflicts
mvn dependency:tree | grep solr

# 5. Run tests
mvn test -pl catalog/solr

# 6. Integration testing
# - Start SolrCloud (see distribution/docker/solrcloud/README.md)
# - Run catalog integration tests
mvn verify -pl catalog/core/catalog-core-standardframework

# 7. Commit
git add pom.xml
git commit -m "Security: Upgrade Apache Solr to 9.4.1 (CVE-2023-50386)"
```

**Testing Checklist:**
- [ ] Clean build succeeds
- [ ] SolrCatalogProvider tests pass
- [ ] Catalog indexing works (create, update, delete)
- [ ] Full-text search works
- [ ] Spatial queries work (WKT geometries)
- [ ] Faceted search works
- [ ] SolrCloud connectivity works
- [ ] Performance acceptable (query latency benchmarks)

---

## Week 3-5: GeoTools Major Upgrade (2-3 weeks) ⚠️ HIGH EFFORT

### GeoTools 24.6 → 28.6.1
**Effort:** 2-3 weeks
**Impact:** Removes ~50 critical/high vulnerabilities (XXE + XPath RCE)

```bash
# 1. Create dedicated branch
git checkout -b security/geotools-28.6.1

# 2. Review breaking changes
# - Check GeoTools 24 → 25 → 26 → 27 → 28 release notes
# - Document API changes

# 3. Edit pom.xml
sed -i 's/<org.geotools.version>24.6<\/org.geotools.version>/<org.geotools.version>28.6.1<\/org.geotools.version>/' pom.xml

# 4. Build (expect compilation errors)
mvn clean install -DskipTests 2>&1 | tee /tmp/geotools-build.log

# 5. Fix compilation errors
# - Update API calls to match GeoTools 28.x
# - Update spatial query interfaces
# - Update coordinate system handling

# 6. Run tests iteratively
mvn test -pl catalog/spatial

# 7. Integration testing
mvn verify -pl catalog/spatial
```

**API Changes to Watch For:**
- Geometry factory initialization
- Coordinate reference system (CRS) handling
- Filter encoding/decoding
- WFS protocol versions
- GML parsing

**Testing Checklist (EXTENSIVE):**
- [ ] All spatial query types work:
  - [ ] Point queries
  - [ ] Polygon queries
  - [ ] Bounding box queries
  - [ ] Buffer queries
  - [ ] Distance queries
- [ ] WFS protocol compliance:
  - [ ] WFS 1.0.0
  - [ ] WFS 1.1.0
  - [ ] WFS 2.0.0
- [ ] GML parsing and generation
- [ ] Coordinate transformations:
  - [ ] WGS84 (EPSG:4326)
  - [ ] Web Mercator (EPSG:3857)
  - [ ] UTM zones
- [ ] Integration with Solr spatial indexing
- [ ] Federation spatial queries
- [ ] Performance benchmarking (compare 24.6 vs 28.6.1)
- [ ] Memory usage profiling

**Rollback Plan:**
If GeoTools 28.6.1 has breaking changes that require extensive refactoring:
1. Consider intermediate upgrade to 26.x or 27.x
2. Break into smaller incremental upgrades
3. Budget additional 1-2 weeks per intermediate version

---

## Week 6-7: Log4j 1.x Complete Removal (1-2 weeks)

### Remove All Log4j 1.x Dependencies
**Effort:** 1-2 weeks
**Impact:** Removes 120 critical/high vulnerabilities

```bash
# 1. Use audit results from Week 1
cat /tmp/log4j-1x-deps.txt

# 2. Add exclusions to all affected dependencies
# Example structure:
<dependency>
  <groupId>affected-library</groupId>
  <artifactId>artifact</artifactId>
  <version>x.y.z</version>
  <exclusions>
    <exclusion>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
    </exclusion>
  </exclusions>
</dependency>

# 3. Build and verify
mvn clean install -DskipTests

# 4. Verify Log4j 1.x is completely gone
mvn dependency:tree | grep -c "log4j:log4j"  # Should return 0

# 5. Run full test suite
mvn test

# 6. Test logging functionality
# - Check all log files are created
# - Verify log rotation works
# - Test different log levels
# - Verify MDC (Mapped Diagnostic Context) works

# 7. Commit
git add pom.xml
git commit -m "Security: Remove all Log4j 1.x dependencies (CVE-2022-23307, CVE-2022-23305, CVE-2019-17571)"
```

**Testing Checklist:**
- [ ] mvn dependency:tree shows zero log4j:log4j references
- [ ] Application starts successfully
- [ ] Logging works (all log files created)
- [ ] Log levels work (DEBUG, INFO, WARN, ERROR)
- [ ] Log rotation works
- [ ] MDC works (thread-local logging context)
- [ ] Performance acceptable (logging overhead)
- [ ] No "ClassNotFoundException: org.apache.log4j" errors

---

## Week 8: Secondary Upgrades (optional but recommended)

### Netty 4.1.100 → 4.1.128
**Effort:** 3-5 days
**Impact:** 28 versions of HTTP/2 and SSL fixes

```bash
# Edit pom.xml
sed -i 's/<netty.version>4.1.100.Final<\/netty.version>/<netty.version>4.1.128.Final<\/netty.version>/' pom.xml

mvn clean install
mvn test
```

**Testing:**
- [ ] HTTP/2 protocol compliance
- [ ] SSL/TLS connections
- [ ] Solr communication (uses Netty)
- [ ] Load testing for DoS regression

---

### Commons BeanUtils 1.9.4 → 1.11.0
**Effort:** 2-3 days
**Impact:** CVE-2025-48734 access control issue

```bash
# Edit pom.xml
sed -i 's/<commons-beanutils.version>1.9.4<\/commons-beanutils.version>/<commons-beanutils.version>1.11.0<\/commons-beanutils.version>/' pom.xml

mvn clean install
mvn test
```

---

### TestNG (Test Scope Only)
**Effort:** 1 day
**Impact:** 449 high alerts (test scope only, low risk)

```bash
# Find current TestNG version
mvn dependency:tree | grep testng

# Upgrade to 7.7.0+
# (Add explicit version in dependencyManagement if needed)
```

---

## Verification Commands

### After Each Upgrade

```bash
# 1. Verify version in dependency tree
mvn dependency:tree | grep {package-name}

# 2. Check for CVE resolution
# Option A: Manual NVD check
# Visit https://nvd.nist.gov/vuln/detail/{CVE-ID}
# Verify your version is not in vulnerable range

# Option B: OWASP Dependency Check
mvn org.owasp:dependency-check-maven:check
grep "CVSS >= 7" target/dependency-check-report.html

# 3. Run security tests
mvn test -Dtest=*Security*Test

# 4. Check for new vulnerabilities introduced
mvn dependency:tree > /tmp/after-upgrade.txt
# Compare with baseline
diff /tmp/before-upgrade.txt /tmp/after-upgrade.txt
```

---

## Success Criteria

### After All Upgrades Complete

```bash
# Run OWASP check
mvn org.owasp:dependency-check-maven:check

# Expected result:
# "Total High/Critical Vulnerabilities (CVSS >= 7): 0"
```

**Final Checklist:**
- [ ] Zero critical vulnerabilities
- [ ] <5 high vulnerabilities
- [ ] All tests passing
- [ ] No new dependency conflicts
- [ ] Application starts and runs correctly
- [ ] Admin Console accessible
- [ ] REST APIs responding
- [ ] Catalog operations working
- [ ] Authentication/authorization working
- [ ] Federation queries working

---

## Emergency Rollback Procedure

If any upgrade causes critical issues:

```bash
# 1. Revert commit
git revert HEAD

# 2. Or reset to previous state
git reset --hard HEAD~1

# 3. Rebuild
mvn clean install

# 4. Verify rollback successful
mvn test

# 5. Document issue
# Create JIRA ticket with:
# - Dependency name and versions
# - Error messages
# - Test failures
# - Root cause analysis
# - Alternative approaches
```

---

## Progress Tracking

### Week-by-Week Goals

- [ ] **Week 1:** Karaf + Commons Config2 + Log4j audit (Quick wins)
- [ ] **Week 2-3:** Solr upgrade + testing
- [ ] **Week 3-5:** GeoTools upgrade + extensive testing
- [ ] **Week 6-7:** Log4j 1.x complete removal
- [ ] **Week 8:** Secondary upgrades (Netty, BeanUtils, TestNG)

### Milestone Celebrations 🎉
- 🎯 Week 1 Complete: 12 critical vulnerabilities removed
- 🎯 Week 3 Complete: Solr secured
- 🎯 Week 5 Complete: GeoTools secured (~50 vulnerabilities removed)
- 🎯 Week 7 Complete: Log4j 1.x eradicated (120 vulnerabilities removed)
- 🎉 Week 8 Complete: ZERO CRITICAL VULNERABILITIES!

---

## Communication Plan

### Weekly Status Updates

**Template:**
```
Subject: DDF Security Remediation - Week X Status

Progress This Week:
- [✅] Completed: {upgrades}
- [🔄] In Progress: {upgrades}
- [⏸️] Blocked: {issues}

Vulnerabilities Resolved:
- Critical: X → Y (Z removed)
- High: X → Y (Z removed)

Next Week Plan:
- {planned upgrades}

Issues/Risks:
- {any blockers or concerns}

Testing Status:
- Unit tests: PASS/FAIL
- Integration tests: PASS/FAIL
- Security scan: X critical, Y high

ETA to Zero Critical: X weeks
```

---

## Final Deliverables

Upon completion, deliver:

1. **Updated pom.xml** with all security upgrades
2. **SECURITY-VULNERABILITY-REPORT.md** (updated status)
3. **OWASP Dependency Check Report** (zero critical/high)
4. **Test Results Summary** (all tests passing)
5. **Performance Benchmark Report** (before/after comparisons)
6. **Security Audit Report** (CVEs addressed)
7. **Lessons Learned Document** (challenges and solutions)

---

## Questions or Issues?

**Contact:**
- Security Team: security@codice.org
- Development Lead: montge@mianetworks.net
- JIRA: Create ticket with label `security`

**Resources:**
- Full Analysis: SECURITY-VULNERABILITY-PRIORITY-ANALYSIS.md
- Executive Summary: SECURITY-EXECUTIVE-SUMMARY.md
- Quick Start: SECURITY-QUICK-ACTION-PLAN.md

---

**Last Updated:** October 23, 2025
**Next Review:** Weekly during remediation
**Owner:** Development Team
