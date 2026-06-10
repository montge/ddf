# DDF 2.31 Security Remediation

## Overview
Systematically remediate the open dependency and code-scanning vulnerabilities on this hardened DDF fork after the upstream sync to `2.31.0-SNAPSHOT` (JDK 21, Karaf 4.4.10). The 4,730 Dependabot alerts dedupe to **90 unique advisories across 41 packages**; CodeQL reports 3 critical + 24 high actionable SAST findings.

## Goals
1. **Eliminate critical/high dependency CVEs** that are reachable at runtime.
2. **Apply fixes as direct root-pom property bumps** (atomic, lockstep families), superseding the overlapping Dependabot PRs.
3. **Neutralize transitive vulns** via `dependencyManagement` pins / exclusions; remove dead dependencies.
4. **Resolve the hard cases** — Shiro 1.13→2.1 major, the Spring ServiceMix OSGi-bundle gap — or track them explicitly with a documented decision.
5. **Clear stale alerts** (already fixed by the merge) via re-scan.

## Rationale
- The merge already fixed several CVEs (neethi 3.2.2, xmlsec 2.3.4, jackson 2.21.1, jetty 9.4.58…); those alerts only need a re-scan.
- Most remaining criticals/highs are single-property bumps with low risk (mina, tomcat-embed, bouncycastle, zookeeper, netty, solr).
- A handful are genuine engineering work: Shiro 2.x is a breaking major; Spring's runtime fix is blocked on an unpublished ServiceMix bundle; the Jetty 9.4 EOL line can only be fixed by the Pax Web 11 / Jetty 12 migration on the `pax-web-jakarta-servlet-upgrade` branch.

## Success Criteria
- [ ] Dependabot: 0 critical, 0 reachable high (excluding items tracked to other branches) after re-scan.
- [ ] `mvn install -Dfast` stays green (code-complete; docs blocker tracked separately).
- [ ] Every applied bump verified to build; lockstep families moved together (BouncyCastle, Netty, Solr/SolrJ).
- [ ] CodeQL critical findings triaged (fix or documented suppression).
- [ ] Each deferred/blocked item has an explicit decision recorded in `design.md`.

## Scope

### In Scope
- Direct root-pom version-property bumps (P0–P2).
- Transitive `dependencyManagement` pins + exclusions (junrar, not-yet-commons-ssl).
- Drop-unused dependencies (hibernate-core, commons-httpclient, commons-lang 2.6, jgit suppression).
- Shiro 1.13→2.1 major upgrade (full scope per decision).
- Spring 6.2.11 source bump + investigate building a local ServiceMix `spring-core 6.2.11` OSGi wrapper.
- Closing redundant Dependabot PRs once bumps land directly.

### Out of Scope
- Jetty 9.4 → 12 (handled by `pax-web-jakarta-servlet-upgrade`).
- pac4j 5.7.7 → 6.x major (separate change; needed for CVE-2026-40458 and the jakartaee adapter).
- The `distribution/docs` empty-archive build blocker (separate fix; prerequisite for the full distribution zip).
- New features, UI work, performance.

## Source
Plan synthesized by the `ddf-security-remediation-plan` workflow (64 agents, per-package analysis + adversarial verification). Full grouped plan archived in `design.md`. Dependabot PR mapping reconciled against `montge/ddf` ground truth (the workflow's verify layer mis-checked `codice/ddf`).
