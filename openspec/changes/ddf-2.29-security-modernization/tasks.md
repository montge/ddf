# DDF 2.29 Security & Modernization Tasks

**Status:** In Progress (Last updated: 2025-12-12)
**Vulnerabilities:** 1177 (4 critical, 107 high) - down from 1798

## Phase 1: Security Hardening

### 1.1 Vulnerability Triage
- [ ] 1.1.1 Run fresh OWASP dependency-check scan
- [ ] 1.1.2 Create suppression file for false positives
- [ ] 1.1.3 Document test-only dependencies to suppress
- [x] 1.1.4 GeoTools CVE-2025-30220 ✅ Fixed in 34.1 upgrade
- [ ] 1.1.5 Verify Zookeeper 3.9.2 CVE status

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

### 1.3 Security Test Coverage
- [ ] 1.3.1 security-core-impl tests (46.88% -> 80%) - Added 38 tests (2025-12-12)
- [ ] 1.3.2 security-core-services tests (45.77% -> 80%) - Added 21 tests (2025-12-12)
- [x] 1.3.3 security-handler-impl tests (13.84% -> 80%) ✅ **98.2%** instruction coverage (2025-12-12)
- [x] 1.3.4 platform-security-core-api tests ✅ Mostly interfaces (30+), concrete classes tested (2025-12-12)
- [x] 1.3.5 security-filter-csrf tests (0% -> 80%) ✅ 99.1% instruction, 88.5% branch, 98.6% line coverage
- [x] 1.3.6 catalog-security-logging tests (0% -> 80%) ✅ **Already at 100%** - 45 tests, full coverage

---

## Phase 2: Jakarta EE Migration

### 2.1 Eclipse Transformer Setup
- [ ] 2.1.1 Add Eclipse Transformer plugin to build
- [ ] 2.1.2 Configure javax->jakarta transformation rules
- [ ] 2.1.3 Create transformation test pipeline
- [ ] 2.1.4 Validate transformed artifacts work in OSGi

### 2.2 Core Namespace Migration
- [ ] 2.2.1 Identify all javax.servlet usages
- [ ] 2.2.2 Identify all javax.ws.rs usages
- [ ] 2.2.3 Identify all javax.xml.bind usages
- [ ] 2.2.4 Create migration plan per module
- [ ] 2.2.5 Migrate platform/security modules first
- [ ] 2.2.6 Migrate catalog modules
- [ ] 2.2.7 Migrate remaining modules

### 2.3 Spring 6.x Upgrade
- [ ] 2.3.1 Review Spring 5.3 -> 6.0 migration guide
- [ ] 2.3.2 Update Spring dependencies
- [ ] 2.3.3 Fix deprecated API usages
- [ ] 2.3.4 Update Blueprint configurations if needed
- [ ] 2.3.5 Validate OSGi bundle resolution

---

## Phase 3: Core Library Upgrades

### 3.1 Apache CXF 4.x
- [ ] 3.1.1 Review CXF 3.x -> 4.x migration guide
- [ ] 3.1.2 Update CXF dependencies
- [ ] 3.1.3 Migrate JAX-RS annotations
- [ ] 3.1.4 Migrate JAX-WS annotations
- [ ] 3.1.5 Test all REST endpoints
- [ ] 3.1.6 Test CSW/WFS SOAP services

### 3.2 Apache Camel Upgrade
- [ ] 3.2.1 Review Camel 3.18 -> 3.22 migration guide
- [ ] 3.2.2 Update Camel dependencies
- [ ] 3.2.3 Fix route compatibility issues
- [ ] 3.2.4 Test catalog:// component
- [ ] 3.2.5 Test directory monitor routes

### 3.3 Logback 1.4.x
- [ ] 3.3.1 Update SLF4J to 2.x
- [ ] 3.3.2 Update Logback to 1.4.x
- [ ] 3.3.3 Migrate configuration files
- [ ] 3.3.4 Test logging in all modules

---

## Phase 4: Test Coverage Expansion

### 4.1 Critical Untested Modules
- [ ] 4.1.1 catalog-rest-service tests (1,315 LOC)
- [ ] 4.1.2 admin-core-api tests (1,117 LOC)
- [ ] 4.1.3 catalog-core-definitionparser tests (967 LOC)
- [ ] 4.1.4 catalog-solr-offline-gazetteer tests (961 LOC)
- [ ] 4.1.5 spatial-wfs-converter tests (901 LOC)

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
- [ ] 5.2.1 All GitHub Actions workflows pass
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
1. Security test coverage (enables safe refactoring)
2. OWASP suppression file (reduces noise)
3. Hazelcast decision (removes 4 CVEs)
4. Jakarta transformation setup (unblocks upgrades)
5. Core library upgrades (fixes remaining CVEs)

### Commands
```bash
# Run OWASP scan
mvn org.owasp:dependency-check-maven:aggregate -Dformats=HTML,JSON -DskipTests

# Build fast
mvn install -Dfast

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
