# DDF 2.29 Security & Modernization Tasks

**Status:** In Progress (Last updated: 2025-12-13)
**Vulnerabilities:** 912 (4 critical, 120 high) - down from 1798

## Phase 1: Security Hardening

### 1.1 Vulnerability Triage
- [ ] 1.1.1 Run fresh OWASP dependency-check scan
- [ ] 1.1.2 Create suppression file for false positives
- [ ] 1.1.3 Document test-only dependencies to suppress
- [x] 1.1.4 GeoTools CVE-2025-30220 ✅ Fixed in 34.1 upgrade
- [x] 1.1.5 Verify Zookeeper CVE status ✅ Upgraded 3.9.3 -> 3.9.4 (CVE-2025-58457 fixed)

### 1.2 Critical Dependency Upgrades
- [x] 1.2.1 Hazelcast: Evaluate removal vs upgrade to 5.x ✅ **REMOVED** (2025-12-12)
- [x] 1.2.2 If removing Hazelcast: identify replacement strategy ✅ PersistenceStore interface
- [x] 1.2.3 If upgrading Hazelcast: plan API migration ✅ N/A (removed)
- [x] 1.2.4 Jetty 9.4.57 -> 9.4.58 (CVE-2025-5115 HTTP/2 DDoS) ✅ (2025-12-12)
- [x] 1.2.5 Netty 4.1.124 -> 4.1.128 (CVE-2025-55163 + security fixes) ✅ (2025-12-12)
- [x] 1.2.6 Log4j 2.25.0 -> 2.25.1 (concurrency fixes) ✅ (2025-12-12)
- [x] 1.2.7 GeoTools 33.1 -> 34.1 ✅ (2025-12-12) - Java 17+ version, latest security fixes
- [x] 1.2.8 Apache Batik 1.14 -> 1.17+ (7 HIGH CVEs) ✅ Already at 1.18
- [x] 1.2.9 Jackson 2.18.5 -> 2.19.4 ✅ (2025-12-12)
- [x] 1.2.10 OWASP HTML Sanitizer 20220608.1 -> 20240325.1 ✅ (2025-12-12)
- [x] 1.2.11 Commons Exec 1.3 -> 1.5.0 ✅ (2025-12-12)
- [x] 1.2.12 Commons Validator 1.6 -> 1.10.0 ✅ (2025-12-12)
- [x] 1.2.13 Jersey 2.35 -> 2.47 (CVE-2025-25193 race condition) ✅ (2025-12-12)
- [x] 1.2.14 Protobuf 4.28.2 -> 4.33.2 (DoS vulnerabilities) ✅ (2025-12-12)
- [x] 1.2.15 ZooKeeper 3.9.3 -> 3.9.4 (CVE-2025-58457) ✅ (2025-12-12)
- [x] 1.2.16 SLF4J 1.7.36 -> 2.0.17 + Logback 1.2.13 -> 1.5.21 (CVE-2025-11226) ✅ (2025-12-12)
  - Note: logback-access pinned to 1.2.13 (Jetty 9.x compatibility - 2.0.x requires Jetty 11+)

### 1.3 Security Test Coverage
- [ ] 1.3.1 security-core-impl tests (46.88% -> 80%) - Added 74 tests (2025-12-12) incl. 36 PAOS tests
- [x] 1.3.2 security-core-services tests (45.77% -> 80%) ✅ Added 51 tests (2025-12-12) incl. 30 SecurityLoggerImpl tests
- [x] 1.3.3 security-handler-impl tests (13.84% -> 80%) ✅ **98.2%** instruction coverage (2025-12-12)
- [x] 1.3.4 platform-security-core-api tests ✅ Mostly interfaces (30+), concrete classes tested (2025-12-12)
- [x] 1.3.5 security-filter-csrf tests (0% -> 80%) ✅ 99.1% instruction, 88.5% branch, 98.6% line coverage
- [x] 1.3.6 catalog-security-logging tests (0% -> 80%) ✅ **Already at 100%** - 45 tests, full coverage

---

## Phase 2: Jakarta EE Migration

### 2.1 Eclipse Transformer Setup
- [x] 2.1.1 Add Eclipse Transformer plugin to build ✅ v1.0.0 in `jakarta` profile (2025-12-12)
- [x] 2.1.2 Configure javax->jakarta transformation rules ✅ jakartaDefaults enabled (2025-12-12)
- [ ] 2.1.3 Create transformation test pipeline
- [ ] 2.1.4 Validate transformed artifacts work in OSGi

**Implementation Notes (2025-12-12):**
- Use `mvn install -Djakarta` to enable bytecode transformation
- For OSGi bundles: Recommend **OpenRewrite** for source code transformation first
- Eclipse Transformer best for transforming third-party dependencies
- OpenRewrite recipe: `org.openrewrite.java.migrate.jakarta.JakartaEE10`
- See: [OmniFish Guide](https://omnifish.ee/upgrade-to-jakarta-ee-10-transform-application-source-code/)

### 2.2 Core Namespace Migration
- [x] 2.2.1 Identify all javax.servlet usages ✅ **334 occurrences in 127 files** (2025-12-12)
- [x] 2.2.2 Identify all javax.ws.rs usages ✅ **421 occurrences in 129 files** (2025-12-12)
- [x] 2.2.3 Identify all javax.xml.bind usages ✅ **195 occurrences in 89 files** (2025-12-12)
- [x] 2.2.4 Create migration plan per module ✅ See `jakarta-migration-plan.md` (2025-12-12)
- [ ] 2.2.5 Migrate platform/security modules first ⏸️ **BLOCKED** on CXF 4.x
- [ ] 2.2.6 Migrate catalog modules ⏸️ **BLOCKED** on CXF 4.x
- [ ] 2.2.7 Migrate remaining modules ⏸️ **BLOCKED** on CXF 4.x

**Additional Analysis (2025-12-12):**
- javax.annotation: 82 occurrences in 77 files
- javax.inject: 11 occurrences in 11 files
- **Total: ~1,043 javax imports requiring migration**

**Critical Finding (2025-12-12):**
- OpenRewrite successfully transforms source code (tested on catalog-rest-api)
- **BLOCKER:** CXF 3.5.x provides javax.ws.rs; migration requires CXF 4.x first
- Migration order: CXF 4.x upgrade → Then OpenRewrite on modules

### 2.3 Spring 6.x Upgrade
- [x] 2.3.1 Review Spring 5.3 -> 6.0 migration guide ✅ Already at 6.2.14!
- [x] 2.3.2 Update Spring dependencies ✅ Already at 6.2.14 (Jakarta-compatible)
- [ ] 2.3.3 Fix deprecated API usages
- [ ] 2.3.4 Update Blueprint configurations if needed
- [ ] 2.3.5 Validate OSGi bundle resolution

**Note:** DDF already uses Spring 6.2.14 which is Jakarta EE compatible!

---

## Phase 3: Core Library Upgrades

### 3.1 Apache CXF 4.x
- [x] 3.1.1 Review CXF 3.x -> 4.x migration guide ✅ (2025-12-12)
- [ ] 3.1.2 Update CXF dependencies ⏸️ **BLOCKED** - OSGi/Karaf support removed
- [ ] 3.1.3 Migrate JAX-RS annotations ⏸️ **BLOCKED**
- [ ] 3.1.4 Migrate JAX-WS annotations ⏸️ **BLOCKED**
- [ ] 3.1.5 Test all REST endpoints ⏸️ **BLOCKED**
- [ ] 3.1.6 Test CSW/WFS SOAP services ⏸️ **BLOCKED**

**Critical Finding (2025-12-12):**
- ⛔ **CXF 4.x removed OSGi support** - Blueprint extension, Karaf features.xml eliminated
- DDF runs on Apache Karaf (OSGi container) - cannot upgrade without OSGi support
- **JIRA CXF-9086**: "Bring back OSGi support" - proposed `cxf-karaf` repository (like camel-karaf)
- **Timeline:** Q2 2025 estimated for cxf-karaf availability
- **Current CXF:** 3.6.8 (latest with OSGi support)
- **Strategy:** Wait for cxf-karaf, then migrate CXF → then Jakarta
- **Tracking:** https://issues.apache.org/jira/browse/CXF-9086

### 3.2 Apache Camel Upgrade
- [x] 3.2.1 Review Camel 3.18 -> 3.22 migration guide ✅ Already at 3.22.4 (final 3.x EOL)
- [x] 3.2.2 Update Camel dependencies ✅ Already at 3.22.4
- [x] 3.2.3 Fix route compatibility issues ✅ N/A
- [ ] 3.2.4 Test catalog:// component
- [ ] 3.2.5 Test directory monitor routes
- [ ] 3.2.6 Plan Camel 4.x upgrade (requires Jakarta EE) ⏸️ **BLOCKED** on CXF 4.x

### 3.3 Logback 1.5.x (CVE-2025-11226 fix)
- [x] 3.3.1 Update SLF4J to 2.x ✅ 1.7.36 -> 2.0.17 (2025-12-12)
- [x] 3.3.2 Update Logback to 1.5.x ✅ 1.2.13 -> 1.5.21 (2025-12-12)
- [ ] 3.3.3 Migrate configuration files
- [ ] 3.3.4 Test logging in all modules

---

## Phase 4: Test Coverage Expansion

### 4.1 Critical Untested Modules
- [x] 4.1.1 catalog-rest-service tests (1,315 LOC) ✅ 27 tests (2025-12-12)
- [x] 4.1.2 admin-core-api tests (1,117 LOC) ✅ Already has 6 test classes (806 lines)
- [x] 4.1.3 catalog-core-definitionparser tests (967 LOC) ✅ Already has 903 lines Spock tests
- [x] 4.1.4 catalog-solr-offline-gazetteer tests (961 LOC) ✅ Already has 2 test files
- [x] 4.1.5 spatial-wfs-converter tests (901 LOC) ✅ Already has 6 test files

### 4.1.6 Actually Untested Modules (Found 2025-12-12)
- [ ] admin-configurator-actions-api (374 LOC, 8 classes) - API interfaces only
- [x] admin-configurator-api (271 LOC, 7 classes) ✅ 14 tests for Status enum + ConfiguratorException (2025-12-12)
- [x] admin-modules-application (55 LOC, 1 class) ✅ 6 tests (2025-12-12)
- [x] admin-core-configpidplugin (49 LOC, 1 class) ✅ 6 tests (2025-12-12)

### 4.2 Core API Coverage
- [ ] 4.2.1 catalog-core-api coverage (7.58% -> 80%)
- [ ] 4.2.2 Interface contract tests
- [ ] 4.2.3 Data model tests
- [ ] 4.2.4 Exception handling tests

---

## Phase 5: Validation & Release

### 5.1 Integration Testing
- [ ] 5.1.1 Build full distribution
- [ ] 5.1.2 Start DDF with SolrCloud
- [ ] 5.1.3 Test SAML authentication flow
- [ ] 5.1.4 Test OIDC authentication flow
- [ ] 5.1.5 Test catalog create/query/delete
- [ ] 5.1.6 Test federation to remote source

### 5.2 CI/CD Verification
- [x] 5.2.1 All GitHub Actions workflows pass ✅ Fixed integration-tests job (2025-12-12)
- [ ] 5.2.2 CodeQL analysis clean
- [ ] 5.2.3 OWASP scan at target thresholds
- [ ] 5.2.4 Coverage reports generated

### 5.3 Documentation
- [ ] 5.3.1 Update CHANGELOG.md
- [ ] 5.3.2 Update migration guide
- [ ] 5.3.3 Archive OpenSpec change folder

---

## Phase 6: UI Modernization

### 6.1 E2E Testing Infrastructure
- [x] 6.1.1 Add Playwright to Search UI module ✅ (2025-12-12)
- [x] 6.1.2 Create basic E2E test for search workflow ✅ (2025-12-12)
- [x] 6.1.3 Add Playwright to CI pipeline ✅ (2025-12-12)
- [ ] 6.1.4 Migrate CasperJS tests to Playwright
- [ ] 6.1.5 Retire CasperJS/PhantomJS

### 6.2 Bootstrap Upgrade
- [ ] 6.2.1 Audit Bootstrap 3.4.1 usage across UI modules
- [ ] 6.2.2 Create Bootstrap 5 migration plan
- [ ] 6.2.3 Update CSS framework to Bootstrap 5.3
- [ ] 6.2.4 Fix responsive layout issues

### 6.3 Build Modernization
- [x] 6.3.1 Add package.json to UI modules ✅ (2025-12-12) (Search UI)
- [ ] 6.3.2 Integrate Vite for bundling
- [ ] 6.3.3 Add TypeScript compilation
- [ ] 6.3.4 Replace YUI Compressor with modern minification

---

## Quick Reference

### Priority Order
1. ~~Security test coverage (enables safe refactoring)~~ ✅ Major progress
2. ~~OWASP suppression file (reduces noise)~~ ✅ Already comprehensive
3. ~~Hazelcast decision (removes 4 CVEs)~~ ✅ **REMOVED**
4. ~~Jakarta transformation setup~~ ✅ OpenRewrite verified, migration plan created
5. ~~CXF 4.x upgrade~~ ⏸️ **BLOCKED** (OSGi removed, waiting for cxf-karaf Q2 2025)
6. Security test coverage expansion ← **CURRENT** (unblocked work)
7. Non-blocking dependency upgrades
8. Jakarta namespace migration (after CXF 4.x + cxf-karaf)

### Commands
```bash
# Run OWASP scan
mvn org.owasp:dependency-check-maven:aggregate -Dformats=HTML,JSON -DskipTests

# Build fast
mvn install -Dfast

# Build with Jakarta transformation (bytecode)
mvn install -Djakarta

# Run OpenRewrite Jakarta migration (source code)
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.jakarta.JakartaEE10

# Run tests for module
cd platform/security/core/security-core-impl && mvn test

# Format code
mvn fmt:format
```

### GitHub Issues
- #60 Jakarta EE modernization
- #62 Pax Web 10.x
- #63 Eclipse Transformer
- #64 javax->jakarta
- #65 Spring 6.x
- #66 CXF 4.x
- #67 Logback 1.4.x
- #68 Nightly build
