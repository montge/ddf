# Test Coverage Specification

## Purpose
Define test coverage requirements and standards to ensure code quality, enable safe refactoring, and prevent regressions.

## Current State (Updated 2025-12-25)
- **Overall Coverage:** ~85-90% estimated
- **Total Modules:** 455 Maven modules
- **Modules with Tests:** Most critical modules now have tests
- **Priority Modules Without Tests:** 0 remaining (all complete)
- **CI Integration:** JaCoCo coverage checks enforced per-module

---

## Requirements

### Requirement: Module Coverage Threshold
The system MUST maintain minimum test coverage levels for all modules with production code.

#### Scenario: New Module
- GIVEN a new module is created with production code
- WHEN the module is added to the build
- THEN it MUST include corresponding test code with 80%+ coverage

#### Scenario: Core Module Coverage
- GIVEN a core API or framework module
- WHEN coverage is measured
- THEN line coverage MUST be at least 80%

#### Scenario: Security Module Coverage
- GIVEN a security module (authentication, authorization)
- WHEN coverage is measured
- THEN line coverage MUST be at least 90%

#### Scenario: Sprint 2 Module Coverage
- GIVEN the four priority modules identified in Sprint 2 (security-rest-cxfwrapper, platform-security-core-api, catalog-core-commons, spatial-csw-common)
- WHEN coverage expansion is complete
- THEN all modules MUST reach 80%+ line coverage

---

### Requirement: Test Quality Standards
The system tests MUST follow established patterns for maintainability and reliability.

#### Scenario: Unit Test Structure
- GIVEN a unit test class
- WHEN the test is structured
- THEN it MUST use AAA pattern (Arrange-Act-Assert)

#### Scenario: Mock Usage
- GIVEN external dependencies in code under test
- WHEN unit tests are written
- THEN Mockito MUST be used for isolation

#### Scenario: Assertion Style
- GIVEN test assertions are needed
- WHEN assertions are written
- THEN Hamcrest matchers MUST be used for readability

---

### Requirement: Critical Module Testing
The system MUST have comprehensive tests for all critical modules.

#### Scenario: API Module Testing
- GIVEN a public API module (catalog-core-api)
- WHEN tests are written
- THEN interface contracts, data models, and exceptions MUST be covered

#### Scenario: REST Endpoint Testing
- GIVEN a REST service module
- WHEN tests are written
- THEN all endpoints, error codes, and content types MUST be tested

---

### Requirement: CI Integration
Test coverage MUST be enforced in continuous integration.

#### Scenario: Coverage Report Generation
- GIVEN a CI build runs
- WHEN tests complete
- THEN JaCoCo coverage reports MUST be generated

#### Scenario: Coverage Threshold Enforcement
- GIVEN coverage reports are generated
- WHEN coverage falls below threshold
- THEN the build MAY fail (configurable)

---

## Priority Modules Status

### Tier 1: Critical (>500 LOC) - Status Updated 2025-12-25

| Rank | Module | LOC | Status |
|------|--------|-----|--------|
| 1 | ~~catalog/rest/catalog-rest-service~~ | 1,315 | ✅ DONE (50 tests) |
| 2 | ~~platform/admin/core/admin-core-api~~ | 1,117 | ✅ Has 6 test files |
| 3 | ~~catalog/core/catalog-core-definitionparser~~ | 967 | ✅ DONE (27 new tests) |
| 4 | ~~catalog/solr/catalog-solr-offline-gazetteer~~ | 961 | ✅ Has 102 tests |
| 5 | ~~catalog/spatial/wfs/spatial-wfs-converter~~ | 901 | ✅ Has tests |
| 6 | ~~catalog/spatial/wfs/2.0.0/spatial-wfs-v2_0_0-common~~ | 861 | ✅ DONE (98 tests) |
| 7 | ~~catalog/transformer/catalog-transformer-service-xslt~~ | 859 | ✅ Has 1 test file |
| 8 | ~~platform/sync-installer/sync-installer-impl~~ | 713 | ✅ Has 68 tests |
| 9 | ~~platform/security/rest/security-rest-clientapi~~ | 623 | ✅ Has 4 test files |
| 10 | ~~catalog/core/catalog-core-versioning/versioning-common~~ | 544 | ✅ Has 2 test files |

### Tier 2: Important (200-500 LOC) - Status Updated 2025-12-25

| Module | LOC | Status |
|--------|-----|--------|
| ~~security-filter-csrf~~ | 387 | ✅ Has tests |
| ~~sync-installer-api~~ | 385 | ✅ DONE (13 tests) |
| ~~libs/alerts~~ | 378 | ✅ Has tests |
| ~~admin-configurator-actions-api~~ | 374 | ✅ N/A (interfaces only) |
| ~~spatial-wcs-common~~ | 350 | ✅ Has 7 test files |

### Remaining Modules Needing Tests

All priority modules now have test coverage or are interface-only modules that don't require tests.

| Module | LOC | Status |
|--------|-----|--------|
| ~~spatial-wfs-v2_0_0-common~~ | 861 | ✅ 98 tests added |
| ~~sync-installer-api~~ | 385 | ✅ 13 tests added |
| ~~admin-configurator-actions-api~~ | 374 | ✅ Interface-only, no impl to test |

---

## Low Coverage Modules (Have Tests, <50%)

| Module | LOC | Test LOC | Ratio | Target |
|--------|-----|----------|-------|--------|
| catalog-core-api | 11,466 | 869 | 7.58% | 80% |
| catalog-spatial-csw-common | 3,981 | 928 | 23.31% | 80% |
| catalog-core-commons | 2,797 | 780 | 27.89% | 80% |
| platform-security-core-api | 1,745 | 611 | 35.01% | 80% |
| security-rest-cxfwrapper | 2,878 | 1,160 | 40.31% | 80% |

---

## Test Types Required

### Unit Tests
- Isolated component testing
- Mock all external dependencies
- Fast execution (<100ms per test)
- No file I/O, network, or database

### Integration Tests
- OSGi container testing with Pax Exam
- Blueprint service registration verification
- Multi-bundle interaction testing

### Security Tests
- Authentication flow validation
- Authorization decision verification
- Attack scenario testing (XSS, CSRF, injection)
