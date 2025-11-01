# Top 20 Vulnerable Dependencies - Quick Reference

**Generated:** 2025-11-01 | **DDF Version:** 2.29.0-SNAPSHOT

## Summary Statistics

- **Total Vulnerabilities:** 199 (32 CRITICAL, 61 HIGH, 102 MEDIUM, 4 LOW)
- **Vulnerable Dependencies:** 90 of 1,153 scanned (7.8%)
- **HIGH/CRITICAL Dependencies:** 50 requiring immediate attention

---

## Top 20 Dependencies Ranked by Severity

| # | Dependency | Ver | CVSS | Crit | High | Total | Patchability | Effort |
|---|------------|-----|------|------|------|-------|--------------|--------|
| 1 | PlantUML (AsciidoctorJ) | 1.3.20 | 10.0 | 2 | 0 | 2 | Quick Win | 2h |
| 2 | gt-xml-24.6.jar | 24.6 | 9.8 | 3 | 0 | 3 | Medium | 12-20h |
| 3 | gt-xsd-core-24.6.jar | 24.6 | 9.8 | 3 | 0 | 3 | Medium | 12-20h |
| 4 | net.opengis.fes-24.6.jar | 24.6 | 9.8 | 3 | 0 | 3 | Medium | 12-20h |
| 5 | org.w3.xlink-24.6.jar | 24.6 | 9.8 | 3 | 0 | 3 | Medium | 12-20h |
| 6 | tomcat-embed-jasper | 9.0.98 | 9.8 | 2 | 9 | 11 | Investigate | 4-6h |
| 7 | log4j | 1.2.17 | 9.8 | 2 | 3 | 5 | High Effort | 8-16h |
| 8 | camel-core-engine | 3.18.8 | 9.8 | 1 | 2 | 3 | **BLOCKED** | 24-40h |
| 9 | snakeyaml (JRuby) | 1.26 | 9.8 | 1 | 1 | 2 | Quick Win | 2-4h |
| 10 | calcite-core | 1.26.0 | 9.8 | 1 | 0 | 1 | Medium | 4-6h |
| 11 | commons-text | 1.6 | 9.8 | 1 | 0 | 1 | ✅ **FIXED** | 1h |
| 12 | cxf-core | 3.5.11 | 9.8 | 1 | 0 | 1 | Medium | 4-6h |
| 13 | mina-core | 2.1.6 | 9.8 | 1 | 0 | 1 | Quick Win | 2-3h |
| 14 | quartz | 2.3.2 | 9.8 | 1 | 0 | 1 | Investigate | 2-4h |
| 15 | snakeyaml | 1.33 | 9.8 | 1 | 0 | 1 | Medium | 6-8h |
| 16 | hazelcast | 3.12.10 | 9.1 | 1 | 1 | 2 | High Effort | 16-24h |
| 17 | zookeeper | 3.9.1 | 9.1 | 1 | 0 | 1 | Quick Win | 2-3h |
| 18 | zookeeper-jute | 3.9.2 | 9.1 | 1 | 0 | 1 | Quick Win | 2-3h |
| 19 | solr-core | 9.8.1 | 8.1 | 0 | 2 | 2 | Medium | 8-12h |
| 20 | netty-transport | 4.1.x | 8.1 | 0 | 2 | 2 | Medium | 4-6h |

---

## Categorization by Patchability

### ✅ Quick Wins (8-15 hours total)

| Dependency | Current | Target | Effort | Impact |
|------------|---------|--------|--------|--------|
| PlantUML | 1.3.20 | 1.2024.7 | 2h | 2 CRIT (docs-only) |
| MINA | 2.1.6 | 2.1.10 | 2-3h | 1 CRIT |
| Zookeeper | 3.9.1/3.9.2 | 3.9.3 | 2-3h | 2 CRIT |
| Commons Text | 1.6 | 1.14.0 | 1h | 1 CRIT (verify) |
| SnakeYAML (JRuby) | 1.26 | Suppress | 2-4h | 2 CRIT (docs-only) |

**Total Impact:** 8 CRITICAL CVEs resolved in 8-15 hours

---

### ⚡ Medium Effort (40-60 hours total)

| Dependency | Current | Target | Effort | Impact |
|------------|---------|--------|--------|--------|
| **GeoTools** | **24.6** | **31.5** | **12-20h** | **12 CRIT** |
| CXF | 3.5.11 | 3.6.4 | 4-6h | 2 CRIT |
| SnakeYAML | 1.33 | 2.3 | 6-8h | 1 CRIT |
| Calcite | 1.26.0 | 1.38.0 | 4-6h | 1 CRIT |
| Tomcat Embed | 9.0.98 | Investigate | 4-6h | 11 (may be FP) |
| Quartz | 2.3.2 | Investigate | 2-4h | 1 (may be FP) |
| Solr | 9.8.1 | 9.8.1+ | 8-12h | 2 HIGH |
| Netty | 4.1.x | 4.1.115 | 4-6h | 2 HIGH |

**Total Impact:** 20-25 CVEs resolved in 40-60 hours

---

### 🔴 High Effort (50-120 hours total)

| Dependency | Current | Target | Effort | Impact | Blocker |
|------------|---------|--------|--------|--------|---------|
| Log4j 1.x | 1.2.17 | Remove | 8-16h | 5 CRIT | EOL software |
| Hazelcast | 3.12.10 | 5.x | 16-24h | 2 CRIT | Major upgrade |

**Total Impact:** 7 CRITICAL CVEs resolved in 24-40 hours

---

### 🚫 BLOCKED (Requires Platform Upgrade)

| Dependency | Current | Target | Effort | Blocker | Workaround |
|------------|---------|--------|--------|---------|------------|
| **Camel** | **3.18.8** | **3.22.2** | **24-40h** | **Requires Karaf 4.4+** | Plan Q1 2025 |

**Impact:** 3 CRITICAL CVEs (including RCE)

**Required Prerequisite:**
```xml
<karaf.version>4.4.7</karaf.version>  <!-- Currently 4.3.7 -->
```

---

## Recommended Priority Order

### Week 1-2: Quick Wins (P0)
1. ✅ Zookeeper 3.9.1/3.9.2 → 3.9.3 (authentication bypass)
2. ✅ MINA 2.1.6 → 2.1.10 (RCE)
3. ✅ PlantUML - suppress or upgrade (docs-only)
4. ✅ Commons Text - verify transitive dependency

**Total:** 8-15 hours | **Impact:** 8 CRITICAL CVEs

---

### Week 3-4: Core Libraries (P1)
5. ⚡ CXF 3.5.11 → 3.6.4
6. ⚡ SnakeYAML 1.33 → 2.3
7. ⚡ Calcite 1.26.0 → 1.38.0
8. ⚡ Tomcat Embed - investigate CVEs
9. ⚡ Quartz - investigate CVEs

**Total:** 20-30 hours | **Impact:** 15-16 CVEs

---

### Week 5-7: Major Upgrade (P1 - HIGHEST IMPACT)
10. ⚡ **GeoTools 24.6 → 31.5** ⭐ **PRIORITY**

**Total:** 12-20 hours | **Impact:** 12 CRITICAL CVEs

---

### Week 8-10: EOL Software Removal (P0)
11. 🔴 Log4j 1.2.17 - complete removal

**Total:** 8-16 hours | **Impact:** 5 CRITICAL CVEs

---

### Future: Deferred (P2)
12. 🔴 Hazelcast 3.12.10 → 5.x (16-24h)
13. 🔴 Solr 9.8.1 → 9.8.x (8-12h)
14. 🔴 Netty upgrade (4-6h)

---

### Strategic: Requires Platform Upgrade (Q1 2025)
15. 🔴 **Karaf 4.3.7 → 4.4.7** (prerequisite)
16. 🔴 **Camel 3.18.8 → 3.22.2** (after Karaf)

---

## Key Insights

### ⭐ Highest Impact Single Upgrade
**GeoTools 24.6 → 31.5** resolves **12 CRITICAL CVEs** (4 jars × 3 CVEs each)
- Estimated Effort: 12-20 hours
- Risk: Medium (major version upgrade)
- Testing: Extensive spatial functionality required

---

### ✅ Easiest Wins
1. **Zookeeper** - 2-3 hours, 2 CRITICAL CVEs
2. **MINA** - 2-3 hours, 1 CRITICAL CVE
3. **PlantUML** - 2 hours, 2 CRITICAL CVEs (docs-only)

**Total:** 6-8 hours → 5 CRITICAL CVEs resolved

---

### ⚠️ Critical Concerns

1. **Log4j 1.2.17** - EOL software, must be removed
   - Used only by Zookeeper
   - Zookeeper 3.9.x uses Log4j2
   - Action: Remove completely

2. **Apache Camel RCE** - BLOCKED by Karaf 4.3.7
   - CVE-2024-23114 (CVSS 9.8) - Remote Code Execution
   - Requires Karaf 4.4+ to upgrade Camel
   - Action: Plan Karaf upgrade for Q1 2025

3. **GeoTools 24.6** - EOL version (2021)
   - 12 CRITICAL CVEs across 4 jars
   - Current LTS: 31.5 (2024)
   - Action: Upgrade ASAP (highest impact)

---

## Version Upgrade Matrix

| Dependency | Current | Latest | Recommended | Notes |
|------------|---------|--------|-------------|-------|
| GeoTools | 24.6 | 31.5 | 31.5 | LTS, major upgrade |
| Log4j 1.x | 1.2.17 | EOL | Remove | Migrate to Log4j2 |
| Camel | 3.18.8 | 4.8.x | 3.22.2 | Blocked by Karaf |
| CXF | 3.5.11 | 4.0.5 | 3.6.4 | Minor safe, 4.x = Jakarta |
| Zookeeper | 3.9.1 | 3.9.3 | 3.9.3 | Patch version |
| MINA | 2.1.6 | 2.1.10 | 2.1.10 | Patch version |
| SnakeYAML | 1.33 | 2.3 | 2.3 | Breaking changes |
| Hazelcast | 3.12.10 | 5.5.x | 5.5.0 | Major upgrade |
| Calcite | 1.26.0 | 1.38.0 | 1.38.0 | Minor upgrade |
| Commons Text | 1.6 | 1.14.0 | 1.14.0 | Already in pom.xml |

---

## Expected CVE Reduction

| Phase | Effort | CVEs Fixed | Remaining |
|-------|--------|------------|-----------|
| **Baseline** | - | - | **93** HIGH/CRIT |
| After Quick Wins | 8-15h | -8 | **85** |
| After Core Libs | 20-30h | -15 | **70** |
| After GeoTools | 12-20h | -12 | **58** |
| After Log4j Removal | 8-16h | -5 | **53** |
| After Hazelcast (optional) | 16-24h | -2 | **51** |
| After Karaf/Camel (Q1 2025) | 24-40h | -3 | **48** |

**Total Effort:** 88-145 hours to reduce from 93 → 48 HIGH/CRITICAL CVEs (48% reduction)

---

## Current Version Status in pom.xml

✅ **Already Upgraded:**
- Commons Text: 1.14.0 (Text4Shell fixed)
- Solr: 9.8.1 (recent version)
- Jetty: 9.4.58 (latest 9.x)
- Jackson: 2.17.3 (recent)
- Spring: 5.3.39 (latest 5.x)

⚠️ **Needs Upgrade:**
- GeoTools: 24.6 → 31.5 (⭐ PRIORITY)
- Zookeeper: 3.9.1 → 3.9.3
- CXF: 3.5.11 → 3.6.4
- Camel: 3.18.8 → 3.22.2 (BLOCKED)
- Hazelcast: 3.12.10 → 5.x

🔴 **Needs Removal:**
- Log4j 1.2.17 (EOL, 5 CRITICAL CVEs)

---

## Next Steps

1. **Immediate (This Week):**
   - [ ] Upgrade Zookeeper to 3.9.3
   - [ ] Upgrade MINA to 2.1.10
   - [ ] Suppress PlantUML (docs-only)
   - [ ] Verify Commons Text transitive dependency

2. **Short Term (This Month):**
   - [ ] **Upgrade GeoTools 24.6 → 31.5** (⭐ PRIORITY)
   - [ ] Upgrade CXF to 3.6.4
   - [ ] Upgrade SnakeYAML to 2.3
   - [ ] Remove Log4j 1.2.17 completely

3. **Strategic (Q1 2025):**
   - [ ] Plan Karaf 4.3.7 → 4.4.7 upgrade
   - [ ] Upgrade Camel after Karaf
   - [ ] Evaluate Hazelcast necessity

---

**Generated by:** Claude Code (Anthropic)
**Full Report:** `/home/e/Development/ddf/VULNERABILITY-ANALYSIS-2025-11-01.md`
**OWASP Report:** `/home/e/Development/ddf/target/dependency-check-report.html`
