# Quick Security Fixes (No Karaf/Jetty Constraints)

**Status:** Can be implemented immediately (Phase 1)
**Estimated Time:** 20-30 hours total
**Impact:** Fixes ~50 HIGH/CRITICAL CVEs

---

## 1. Tomcat Embed Jasper (CRITICAL - 26 CVEs)

**Current:** 9.0.37
**Target:** 9.0.98

```xml
<!-- pom.xml -->
<properties>
    <tomcat.version>9.0.98</tomcat.version>
</properties>

<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <version>${tomcat.version}</version>
</dependency>
```

**Test Commands:**
```bash
# Build and test
mvn clean install -Dfast
mvn test -pl platform/admin

# Verify JSP rendering
bin/ddf
# Access: https://localhost:8993/admin
```

---

## 2. Apache Commons Compress (HIGH - 3 CVEs)

**Current:** 1.23.0
**Target:** 1.27.1

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-compress</artifactId>
    <version>1.27.1</version>
</dependency>
```

---

## 3. Handlebars.js (CRITICAL - 5 CVEs)

**Current:** 1.0.0
**Target:** 4.7.8

```bash
# Update package.json in UI modules
cd ui/search/simple/
npm install handlebars@4.7.8 --save

cd ../../admin/
npm install handlebars@4.7.8 --save

# Rebuild UI
npm run build
```

**Breaking Changes:**
- Template syntax: `{{#each}}` helper behavior changed
- Partials: Registration API updated

---

## 4. Netty (HIGH - 4 CVEs)

**Current:** 4.1.92.Final
**Target:** 4.1.115.Final

```xml
<netty.version>4.1.115.Final</netty.version>
```

**Test:**
```bash
# Test HTTP/2 connections
mvn test -pl platform/admin
mvn test -pl catalog/rest
```

---

## 5. PlantUML (CRITICAL - Docs Only)

**Option A: Suppress (Recommended if docs not built in prod)**

```xml
<!-- dependency-check-maven-config.xml -->
<suppress>
    <notes>
        PlantUML is only used for documentation generation (asciidoctorj).
        Not included in runtime distribution. CVE-2023-3432, CVE-2022-1379
        affect diagram rendering only.
    </notes>
    <packageUrl regex="true">^pkg:maven/org\.asciidoctor/asciidoctorj-diagram.*@.*$</packageUrl>
    <cve>CVE-2023-3432</cve>
    <cve>CVE-2022-1379</cve>
    <cve>CVE-2022-1231</cve>
    <cve>CVE-2023-3431</cve>
</suppress>
```

**Option B: Upgrade**

```xml
<dependency>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctorj-diagram</artifactId>
    <version>2.3.1</version>
    <scope>test</scope>
</dependency>
```

---

## Build & Test All Fixes

```bash
# 1. Apply all dependency upgrades to pom.xml
vim pom.xml

# 2. Clean build
mvn clean install -Dfast

# 3. Run security scan to verify
mvn org.owasp:dependency-check-maven:aggregate -Dformats=HTML -DfailBuildOnCVSS=11

# 4. Check report
open target/dependency-check-report.html
# Expected: ~50 fewer HIGH/CRITICAL CVEs

# 5. Run full test suite
mvn clean install

# 6. Integration testing
cd distribution/ddf/target/ddf-2.29.0-SNAPSHOT
bin/ddf
# Test admin UI, catalog queries, security features
```

---

## Verification Checklist

- [ ] Tomcat Embed Jasper → 9.0.98 (verify Admin UI loads)
- [ ] Commons Compress → 1.27.1 (verify file uploads work)
- [ ] Handlebars.js → 4.7.8 (verify Search UI renders)
- [ ] Netty → 4.1.115 (verify catalog REST endpoints)
- [ ] PlantUML → suppressed or upgraded
- [ ] OWASP scan shows reduction in CVEs
- [ ] All unit tests pass: `mvn test`
- [ ] Integration tests pass: `mvn verify`
- [ ] Manual smoke testing complete

---

## Expected Results

**Before:**
- Total HIGH/CRITICAL: 122 (39 CRITICAL + 83 HIGH)

**After Phase 1:**
- Total HIGH/CRITICAL: ~70 (estimated 50 CVE reduction)
- Quick win fixes completed
- No platform constraints violated
- Ready for Phase 2 (GeoTools, Bouncy Castle)

---

## Rollback Plan

If issues arise, revert pom.xml changes:

```bash
git diff pom.xml > security-fixes.patch
# If needed:
git checkout pom.xml
mvn clean install
```

---

**Next Steps:** See `TOP-10-SECURITY-PRIORITIES.md` for Phase 2 & 3 planning.
