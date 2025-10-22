# DDF Test Coverage Improvement Plan

**Document Version:** 1.0
**Date:** 2025-10-21
**Status:** Draft - Infrastructure Fixes Required Before Execution
**Owner:** DDF Development Team

---

## Executive Summary

### Current State
- **Overall Coverage:** 63.54% instruction, 48.39% branch (partial data from 31 modules)
- **Modules Measured:** 31 of 457 (6.8%)
- **Modules Below 80%:** 13 identified (42% of measured modules)
- **Critical Gap:** 426 modules unmeasured due to test infrastructure blockers

### Target State
- **Overall Coverage:** 95% instruction, 80% branch
- **Per-Module Coverage:** 80% minimum instruction coverage
- **Timeline:** 12-16 weeks (phased approach)
- **Total Estimated Effort:** 1,200-1,600 hours

### Gap Analysis
- **Current to Target:** +31.46 percentage points (on measured modules only)
- **Actual Project-Wide Gap:** Estimated 50-60 percentage points when all modules measured
- **Critical Blockers:** 2 test infrastructure issues preventing baseline measurement

### Investment Required
- **P0 Critical Modules:** 400-500 hours (security, core catalog)
- **P1 High Priority:** 350-450 hours (REST APIs, plugins)
- **P2 Medium Priority:** 300-400 hours (transformers, utilities)
- **P3 Low Priority:** 150-250 hours (configuration, packaging)

---

## Table of Contents

1. [Test Infrastructure Prerequisites](#test-infrastructure-prerequisites)
2. [Current Coverage Analysis](#current-coverage-analysis)
3. [Module-by-Module Coverage Plan](#module-by-module-coverage-plan)
4. [Coverage Strategy & Patterns](#coverage-strategy--patterns)
5. [Effort Estimation Methodology](#effort-estimation-methodology)
6. [Phased Implementation Plan](#phased-implementation-plan)
7. [Success Metrics & Tracking](#success-metrics--tracking)
8. [Testing Best Practices for DDF](#testing-best-practices-for-ddf)
9. [Risk Assessment & Mitigation](#risk-assessment--mitigation)
10. [Appendices](#appendices)

---

## Test Infrastructure Prerequisites

### Blocker #1: Groovy Compilation Error (CRITICAL)
**Status:** UNRESOLVED
**Impact:** Prevents secure-boot module tests from running
**Estimated Fix Time:** 1-2 hours

**Resolution:**
```xml
<!-- In /home/e/Development/ddf/pom.xml -->
<gmavenplus.version>3.0.2</gmavenplus.version>  <!-- Upgrade from 1.12.0 -->
```

**Verification:**
```bash
cd platform/security/secure-boot
mvn clean test
```

### Blocker #2: Resource Bundle Locator Test Failures
**Status:** UNRESOLVED
**Impact:** Prevents complete build, 2.86% coverage (critical)
**Estimated Fix Time:** 2-4 hours

**Resolution:**
1. Review surefire reports
2. Fix test logic or environmental issues
3. Verify tests pass

**Verification:**
```bash
cd platform/resource-bundle-locator
mvn clean test
```

### Baseline Measurement Required
**After blockers fixed:**
```bash
cd /home/e/Development/ddf
mvn clean test jacoco:report -T 1C
```

**Expected Duration:** 10-15 minutes
**Output:** Complete coverage baseline for all 457 modules

---

## Current Coverage Analysis

### Modules with Excellent Coverage (≥95%)

| Module | Coverage | Pattern | Test/Source Ratio |
|--------|----------|---------|-------------------|
| catalog-validator-wkt | 100.00% | Simple validator, comprehensive edge cases | 1:2 (1 test, 2 sources) |
| libs-checksum | 100.00% | Pure utility, algorithmic testing | High |
| libs-platform-configuration-impl | 100.00% | API implementation, clear contract | High |
| platform-io-impl | 100.00% | I/O utilities, boundary testing | High |
| platform-parser-api | 100.00% | Interface definitions, contract tests | High |
| platform-metrics-servlet-filter | 96.91% | Servlet filter, request/response mocking | High |
| libs-klv | 95.22% | KLV encoding/decoding, format testing | High |

**Success Patterns Identified:**
1. **Clear API Boundaries:** Well-defined interfaces enable comprehensive contract testing
2. **Pure Functions:** Utility classes with deterministic outputs are easier to test
3. **Single Responsibility:** Modules with focused functionality achieve higher coverage
4. **Mature Test Infrastructure:** Proper mocking setup (Mockito, Hamcrest)
5. **Edge Case Coverage:** Explicit testing of boundary conditions and error paths

### Modules Below 80% Coverage (From Partial Data)

| Priority | Module | Current | Target | Gap | Est. Tests | Est. Hours |
|----------|--------|---------|--------|-----|------------|------------|
| P0 | libs/gson-support | 1.19% | 80% | +78.81% | 15-20 | 24-32 |
| P0 | platform/resource-bundle-locator | 2.86% | 80% | +77.14% | 8-12 | 16-24 |
| P1 | libs/httpproxy/proxy-camel-servlet | 22.03% | 80% | +57.97% | 12-15 | 20-28 |
| P0 | platform/security/security-oidc-bundle | 39.97% | 80% | +40.03% | 10-15 | 18-26 |
| P1 | catalog/common/catalog-common-geo-formatter | 41.86% | 80% | +38.14% | 8-12 | 14-20 |
| P2 | platform/persistence/platform-persistence-core-api | 47.70% | 80% | +32.30% | 6-10 | 12-18 |
| P2 | catalog/measure/catalog-measure-api | 49.51% | 80% | +30.49% | 6-8 | 10-16 |
| P2 | libs/common-system | 61.65% | 80% | +18.35% | 4-6 | 8-12 |
| P2 | platform/util/platform-util | 63.59% | 80% | +16.41% | 8-10 | 12-18 |
| P2 | platform/mime/tika/platform-mime-tika-resolver | 64.00% | 80% | +16.00% | 4-6 | 8-12 |
| P3 | platform/security/platform-security-core-api | 70.76% | 80% | +9.24% | 3-5 | 6-10 |
| P3 | platform/mime/core/platform-mime-core-configurableresolver | 71.68% | 80% | +8.32% | 3-4 | 6-8 |
| P3 | libs/notifications | 72.02% | 80% | +7.98% | 2-4 | 4-8 |

**Subtotal for Known Modules:** 94-127 tests, 158-232 hours

### Critical Modules Without Coverage Data (426 modules)

#### P0: Security-Critical Modules (Estimated)

| Module | Est. Coverage | Target | Est. Tests | Est. Hours | Rationale |
|--------|---------------|--------|------------|------------|-----------|
| platform/security/security-core-impl | 45-55% | 80% | 25-35 | 40-60 | SAML, OAuth, security filters - complex logic |
| platform/security/filter/* (8 modules) | 40-60% | 80% | 60-80 | 100-140 | Authentication filters, critical path |
| platform/security/sts/* | 50-65% | 80% | 15-25 | 25-40 | Security Token Service |

**P0 Security Subtotal:** 100-140 tests, 165-240 hours

#### P0: Core Catalog Modules (Estimated)

| Module | Est. Coverage | Target | Est. Tests | Est. Hours | Rationale |
|--------|---------------|--------|------------|------------|-----------|
| catalog/core/catalog-core-standardframework | 55-65% | 80% | 35-50 | 60-90 | CatalogFrameworkImpl - main orchestrator |
| catalog/core/catalog-core-api-impl | 50-60% | 80% | 45-60 | 75-105 | MetacardImpl, core data structures |
| catalog/solr/catalog-solr-provider | 60-70% | 80% | 20-30 | 35-50 | Primary storage backend |
| catalog/solr/catalog-solr-core | 55-65% | 80% | 25-35 | 40-60 | Solr integration core |

**P0 Catalog Subtotal:** 125-175 tests, 210-305 hours

#### P1: High-Value Modules (Estimated)

| Category | Module Count | Est. Tests | Est. Hours | Rationale |
|----------|--------------|------------|------------|-----------|
| REST APIs (catalog/rest/*) | 6 | 60-80 | 100-140 | Public APIs, critical for integrations |
| Federation & Sources | 8 | 50-70 | 80-120 | Federated queries, distributed systems |
| Security Plugins | 5 | 40-60 | 65-95 | Authorization, policy enforcement |
| Query Operations | 4 | 35-50 | 55-80 | Core query processing |

**P1 Subtotal:** 185-260 tests, 300-435 hours

#### P2: Medium-Value Modules (Estimated)

| Category | Module Count | Est. Tests | Est. Hours | Rationale |
|----------|--------------|------------|------------|-----------|
| Transformers (30+ modules) | 30 | 120-180 | 180-280 | Format conversions, many edge cases |
| Plugins (remaining) | 15 | 75-110 | 110-170 | Various plugin types |
| Utilities | 20 | 60-90 | 90-140 | Supporting utilities |
| Persistence | 6 | 30-45 | 45-70 | Data persistence layer |

**P2 Subtotal:** 285-425 tests, 425-660 hours

#### P3: Lower-Priority Modules (Estimated)

| Category | Module Count | Est. Tests | Est. Hours | Rationale |
|----------|--------------|------------|------------|-----------|
| Configuration | 25 | 50-75 | 70-110 | Config management, less complex |
| UI Backend | 15 | 45-65 | 65-100 | UI support services |
| Distribution/Packaging | 20 | 40-60 | 55-90 | Assembly, features |
| Admin Tools | 10 | 30-45 | 45-70 | Administration utilities |
| Documentation Modules | 5 | 10-15 | 15-25 | Minimal logic |

**P3 Subtotal:** 175-260 tests, 250-395 hours

### Overall Project Estimates

| Priority | Module Count | Estimated Tests | Estimated Hours | Completion Target |
|----------|--------------|-----------------|-----------------|-------------------|
| P0 (Critical) | 35-40 | 319-442 | 533-777 | Weeks 1-4 |
| P1 (High) | 23-25 | 185-260 | 300-435 | Weeks 5-8 |
| P2 (Medium) | 71-75 | 285-425 | 425-660 | Weeks 9-12 |
| P3 (Low) | 75-80 | 175-260 | 250-395 | Weeks 13-16 |
| **TOTAL** | **204-220** | **964-1,387** | **1,508-2,267** | **16 weeks** |

**Note:** Remaining 235-253 modules likely have adequate coverage (≥80%) or are feature/distribution modules with minimal testable logic.

---

## Module-by-Module Coverage Plan

### Phase 1: Infrastructure & Critical Security (Weeks 1-4)

#### Week 1: Infrastructure Fixes & Security Foundation

**Days 1-2: Test Infrastructure**
- Fix gmavenplus-plugin Groovy compilation (2 hours)
- Fix resource-bundle-locator tests (4 hours)
- Generate complete coverage baseline (1 hour)
- Document baseline results (2 hours)

**Days 3-5: Critical Security Modules**

**Module:** `platform/security/security-core-impl`
- **Current Coverage:** Unknown (estimated 45-55%)
- **Target Coverage:** 80%
- **Gap:** ~25-35 percentage points
- **Key Areas:**
  - SAML authentication handlers
  - OAuth/OIDC token validation
  - X.509 certificate processing
  - Security subject management
  - Policy decision points
- **Test Strategy:**
  - Mock SAML responses with valid/invalid signatures
  - Test token expiration and refresh logic
  - Verify certificate chain validation
  - Test edge cases: expired tokens, malformed SAML, revoked certificates
- **Estimated Tests:** 25-35
- **Estimated Hours:** 40-60
- **Priority:** P0 - Security critical

**Module:** `platform/security/filter/security-filter-web-sso`
- **Current Coverage:** Unknown (estimated 40-50%)
- **Target Coverage:** 80%
- **Key Areas:**
  - SAML Web SSO flow
  - ECP (Enhanced Client or Proxy) support
  - Session management
  - Redirect handling
- **Test Strategy:**
  - Mock HTTP requests/responses
  - Test authentication flow states
  - Verify session creation/destruction
  - Test concurrent authentication attempts
- **Estimated Tests:** 15-20
- **Estimated Hours:** 25-35
- **Priority:** P0 - Authentication entry point

#### Week 2: Core Catalog Framework

**Module:** `catalog/core/catalog-core-standardframework`
- **Current Coverage:** Unknown (estimated 55-65%)
- **Target Coverage:** 80%
- **Key Areas:**
  - CatalogFrameworkImpl main orchestration
  - QueryOperations, CreateOperations, UpdateOperations, DeleteOperations
  - Plugin chain execution
  - Source federation logic
  - Error handling and rollback
- **Test Strategy:**
  - Mock all plugin interfaces (PreIngestPlugin, PostIngestPlugin, etc.)
  - Test plugin chain interruption (StopProcessingException)
  - Verify parallel source queries
  - Test transactional semantics
  - Edge cases: null inputs, empty results, timeout scenarios
- **Estimated Tests:** 35-50
- **Estimated Hours:** 60-90
- **Priority:** P0 - Core orchestration

**Module:** `catalog/core/catalog-core-api-impl`
- **Current Coverage:** Unknown (estimated 50-60%)
- **Target Coverage:** 80%
- **Key Areas:**
  - MetacardImpl core implementation
  - Attribute management
  - MetacardType registry
  - Security attributes
  - Serialization/deserialization
- **Test Strategy:**
  - Test all attribute types (STRING, INTEGER, DATE, GEOMETRY, etc.)
  - Verify attribute validation
  - Test security attribute inheritance
  - Test serialization round-trips
  - Edge cases: large attributes, special characters, null values
- **Estimated Tests:** 45-60
- **Estimated Hours:** 75-105
- **Priority:** P0 - Core data model

#### Week 3: Storage & Federation

**Module:** `catalog/solr/catalog-solr-provider`
- **Current Coverage:** Unknown (estimated 60-70%)
- **Target Coverage:** 80%
- **Key Areas:**
  - CRUD operations
  - Query translation (OGC Filter → Solr)
  - Spatial query handling (WKT)
  - Dynamic schema management
  - Connection pooling
- **Test Strategy:**
  - Use embedded Solr for integration tests
  - Test all filter types (AND, OR, NOT, spatial, temporal)
  - Verify WKT geometry indexing
  - Test bulk operations (batch insert/update)
  - Error scenarios: Solr down, schema conflicts
- **Estimated Tests:** 20-30
- **Estimated Hours:** 35-50
- **Priority:** P0 - Primary storage

**Module:** `catalog/solr/catalog-solr-core`
- **Current Coverage:** Unknown (estimated 55-65%)
- **Target Coverage:** 80%
- **Key Areas:**
  - GeotoolsFilterAdapterImpl
  - Dynamic schema resolver
  - Solr query builders
  - Result set processing
- **Test Strategy:**
  - Test all OGC filter operators
  - Verify spatial predicate translations
  - Test dynamic field creation
  - Edge cases: complex nested queries, large result sets
- **Estimated Tests:** 25-35
- **Estimated Hours:** 40-60
- **Priority:** P0 - Storage integration

#### Week 4: Security Filters & REST APIs

**Modules:** `platform/security/filter/*` (remaining 6 modules)
- **Average Coverage:** Estimated 40-60%
- **Target Coverage:** 80% each
- **Key Modules:**
  - security-filter-login
  - security-filter-authorization
  - security-filter-csrf
  - security-filter-cors
  - security-filter-api
  - security-filter-delegate
- **Test Strategy:**
  - Mock servlet chains
  - Test filter ordering
  - Verify security context propagation
  - Test cross-origin scenarios (CORS)
  - CSRF token validation
- **Estimated Tests:** 45-60 (across 6 modules)
- **Estimated Hours:** 75-105
- **Priority:** P0 - Security enforcement

**Module:** `catalog/rest/catalog-rest-impl`
- **Current Coverage:** Unknown (estimated 50-65%)
- **Target Coverage:** 80%
- **Key Areas:**
  - JAX-RS REST endpoints
  - Query endpoint (/services/catalog/query)
  - Metacard CRUD (/services/catalog/metacards/{id})
  - Source management
  - Input validation
- **Test Strategy:**
  - Use JAX-RS test framework (Jersey Test)
  - Mock CatalogFramework
  - Test all HTTP methods (GET, POST, PUT, DELETE)
  - Verify error responses (400, 401, 404, 500)
  - Test content negotiation (JSON, XML, etc.)
- **Estimated Tests:** 30-40
- **Estimated Hours:** 50-70
- **Priority:** P1 - Public API

### Phase 2: High-Value Modules (Weeks 5-8)

#### Week 5: REST APIs & Source Implementations

**Module:** `catalog/rest/catalog-rest-api`
- **Estimated Tests:** 15-20
- **Estimated Hours:** 25-35
- **Key Areas:** REST API interfaces, data models

**Module:** `catalog/opensearch/catalog-opensearch-endpoint`
- **Estimated Tests:** 15-20
- **Estimated Hours:** 25-35
- **Key Areas:** OpenSearch protocol, query translation

**Module:** `catalog/opensearch/catalog-opensearch-source`
- **Estimated Tests:** 15-20
- **Estimated Hours:** 25-35
- **Key Areas:** Remote OpenSearch querying

#### Week 6: Federation & Query Processing

**Module:** `catalog/core/catalog-core-federationstrategy`
- **Estimated Tests:** 20-25
- **Estimated Hours:** 35-45
- **Key Areas:** Parallel query execution, result aggregation

**Module:** `catalog/core/catalog-core-queryoperations`
- **Estimated Tests:** 25-30
- **Estimated Hours:** 40-55
- **Key Areas:** Query transformation, security filtering

**Module:** `catalog/plugin/catalog-plugin-federation-replication`
- **Estimated Tests:** 15-20
- **Estimated Hours:** 25-35
- **Key Areas:** Replication logic, conflict resolution

#### Week 7: Security Plugins & Authorization

**Module:** `catalog/plugin/catalog-plugin-security-audit`
- **Estimated Tests:** 12-15
- **Estimated Hours:** 20-28
- **Key Areas:** Audit logging, security events

**Module:** `platform/security/policy/*` (multiple modules)
- **Estimated Tests:** 30-40
- **Estimated Hours:** 50-70
- **Key Areas:** XACML policies, access control decisions

**Module:** `platform/security/security-pdp-authz`
- **Estimated Tests:** 20-25
- **Estimated Hours:** 35-45
- **Key Areas:** Policy Decision Point, attribute-based access control

#### Week 8: Sources & Protocols

**Module:** `catalog/spatial/csw/spatial-csw-source`
- **Estimated Tests:** 20-25
- **Estimated Hours:** 35-45
- **Key Areas:** CSW protocol, OGC filter mapping

**Module:** `catalog/spatial/wfs/*` (WFS sources)
- **Estimated Tests:** 25-30
- **Estimated Hours:** 40-55
- **Key Areas:** WFS 1.1.0, 2.0.0 protocols, GML parsing

### Phase 3: Medium-Priority Modules (Weeks 9-12)

#### Week 9-10: Transformers (30+ modules)

**High-Priority Transformers:**
- `catalog/transformer/catalog-transformer-json`
- `catalog/transformer/catalog-transformer-xml`
- `catalog/transformer/catalog-transformer-csv`
- `catalog/transformer/catalog-transformer-pdf`
- `catalog/transformer/catalog-transformer-geojson`

**Test Strategy:**
- Test round-trip transformations (Metacard → Format → Metacard)
- Verify format compliance (JSON schema, XML schema, GeoJSON spec)
- Test edge cases: special characters, large files, malformed input
- Performance tests: large result sets

**Estimated Tests per Transformer:** 3-6
**Total Tests:** 120-180
**Total Hours:** 180-280

#### Week 11: Plugins & Utilities

**Plugin Categories:**
- Content plugins (URI resolution, resource management)
- Validation plugins (schema validation, business rules)
- Enrichment plugins (geocoding, metadata enhancement)
- Backup plugins (S3, file storage)

**Estimated Tests:** 75-110
**Estimated Hours:** 110-170

**Utility Modules:**
- libs/* (remaining modules)
- platform/util/*
- platform/mime/*
- platform/parser/*

**Estimated Tests:** 60-90
**Estimated Hours:** 90-140

#### Week 12: Persistence & Admin

**Persistence Modules:**
- `platform/persistence/platform-persistence-core-impl`
- `platform/persistence/platform-persistence-core-listeners`
- `platform/persistence/platform-persistence-commands`

**Estimated Tests:** 30-45
**Estimated Hours:** 45-70

**Admin Modules:**
- `platform/admin/core/admin-core-api`
- `platform/admin/core/admin-core-impl`
- `platform/admin/configurator/*`

**Estimated Tests:** 30-45
**Estimated Hours:** 45-70

### Phase 4: Lower-Priority Modules (Weeks 13-16)

#### Week 13-14: Configuration & Management

**Configuration Modules:**
- Config Admin integrations
- Blueprint XML registrations
- Feature definitions
- System settings

**Test Strategy:**
- Test configuration hot-reload
- Verify OSGi service registration
- Test feature installation/uninstallation
- Configuration validation

**Estimated Tests:** 50-75
**Estimated Hours:** 70-110

#### Week 15: UI Backend & Supporting Services

**UI Backend Modules:**
- Search UI backend services
- Admin UI backend services
- Notification services
- Websocket handlers

**Estimated Tests:** 45-65
**Estimated Hours:** 65-100

#### Week 16: Distribution, Packaging & Final Verification

**Activities:**
- Fix remaining low-coverage modules
- Run full coverage analysis
- Verify 80% per-module target achieved
- Verify 95% overall target achieved
- Document coverage report
- Create maintenance guidelines

**Estimated Hours:** 40-60

---

## Coverage Strategy & Patterns

### Unit Testing Patterns

#### Pattern 1: OSGi Service Mocking
```java
@RunWith(MockitoJUnitRunner.class)
public class CatalogFrameworkImplTest {

  @Mock
  private CatalogProvider provider;

  @Mock
  private PreIngestPlugin preIngestPlugin;

  private CatalogFrameworkImpl framework;

  @Before
  public void setUp() {
    framework = new CatalogFrameworkImpl();
    framework.setCatalogProvider(provider);
    framework.setPreIngestPlugins(Collections.singletonList(preIngestPlugin));
  }

  @Test
  public void testCreateWithPlugin() throws Exception {
    // Given
    CreateRequest request = new CreateRequestImpl(metacard);
    when(provider.create(any())).thenReturn(new CreateResponseImpl(request, new ArrayList<>()));

    // When
    CreateResponse response = framework.create(request);

    // Then
    assertThat(response.getCreatedMetacards().size(), is(1));
    verify(preIngestPlugin).process(any());
  }
}
```

#### Pattern 2: Filter Testing (Security, Web Filters)
```java
@RunWith(MockitoJUnitRunner.class)
public class WebSSOFilterTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain chain;

  private WebSSOFilter filter;

  @Test
  public void testAuthenticatedRequest() throws Exception {
    // Given
    when(request.getAttribute(SecurityConstants.SECURITY_SUBJECT))
        .thenReturn(mockSubject);

    // When
    filter.doFilter(request, response, chain);

    // Then
    verify(chain).doFilter(request, response);
    verify(response, never()).sendRedirect(anyString());
  }

  @Test
  public void testUnauthenticatedRequest() throws Exception {
    // Given
    when(request.getAttribute(SecurityConstants.SECURITY_SUBJECT))
        .thenReturn(null);

    // When
    filter.doFilter(request, response, chain);

    // Then
    verify(chain, never()).doFilter(any(), any());
    verify(response).sendRedirect(contains("/login"));
  }
}
```

#### Pattern 3: REST API Testing
```java
public class CatalogRestEndpointTest {

  private CatalogRestEndpoint endpoint;

  @Mock
  private CatalogFramework framework;

  @Before
  public void setUp() {
    endpoint = new CatalogRestEndpoint(framework);
  }

  @Test
  public void testQueryEndpoint() throws Exception {
    // Given
    QueryResponse queryResponse = mock(QueryResponse.class);
    when(framework.query(any())).thenReturn(queryResponse);
    when(queryResponse.getResults()).thenReturn(mockResults);

    // When
    Response response = endpoint.query("title LIKE 'test'", 10, 0);

    // Then
    assertThat(response.getStatus(), is(200));
    assertThat(response.getEntity(), notNullValue());
  }

  @Test
  public void testQueryEndpoint_invalidSyntax() {
    // When
    Response response = endpoint.query("INVALID QUERY", 10, 0);

    // Then
    assertThat(response.getStatus(), is(400));
  }
}
```

#### Pattern 4: Transformer Testing
```java
public class JsonTransformerTest {

  private JsonTransformer transformer;

  @Test
  public void testMetacardToJson() throws Exception {
    // Given
    Metacard metacard = new MetacardImpl();
    metacard.setTitle("Test Metacard");
    metacard.setLocation("POINT (1 1)");

    // When
    BinaryContent content = transformer.transform(metacard, Collections.emptyMap());

    // Then
    String json = IOUtils.toString(content.getInputStream(), StandardCharsets.UTF_8);
    assertThat(json, containsString("\"title\":\"Test Metacard\""));
    assertThat(json, containsString("\"location\":\"POINT (1 1)\""));
  }

  @Test
  public void testRoundTrip() throws Exception {
    // Given
    Metacard original = createComplexMetacard();

    // When
    BinaryContent json = transformer.transform(original, Collections.emptyMap());
    Metacard restored = inputTransformer.transform(json.getInputStream());

    // Then
    assertThat(restored.getTitle(), is(original.getTitle()));
    assertThat(restored.getLocation(), is(original.getLocation()));
  }
}
```

#### Pattern 5: Plugin Testing
```java
@RunWith(MockitoJUnitRunner.class)
public class ValidationPluginTest {

  private ValidationPlugin plugin;

  @Mock
  private MetacardValidator validator;

  @Before
  public void setUp() {
    plugin = new ValidationPlugin();
    plugin.setValidators(Collections.singletonList(validator));
  }

  @Test
  public void testValidMetacard() throws Exception {
    // Given
    CreateRequest request = new CreateRequestImpl(validMetacard);
    when(validator.validate(any())).thenReturn(Collections.emptySet());

    // When
    CreateRequest result = plugin.process(request);

    // Then
    assertThat(result.getMetacards(), hasSize(1));
  }

  @Test(expected = StopProcessingException.class)
  public void testInvalidMetacard() throws Exception {
    // Given
    CreateRequest request = new CreateRequestImpl(invalidMetacard);
    ValidationViolation violation = mock(ValidationViolation.class);
    when(validator.validate(any())).thenReturn(Collections.singleton(violation));

    // When
    plugin.process(request);

    // Then exception thrown
  }
}
```

### Integration Testing Patterns

#### Pattern 6: Embedded Solr Testing
```java
public class SolrProviderIntegrationTest {

  private static EmbeddedSolrServer solrServer;
  private SolrCatalogProvider provider;

  @BeforeClass
  public static void setUpClass() throws Exception {
    // Start embedded Solr
    NodeConfig config = new NodeConfig.NodeConfigBuilder("testNode", Paths.get("target/solr"))
        .setConfigSetBaseDirectory("src/test/resources/solr/configsets")
        .build();
    solrServer = new EmbeddedSolrServer(config, "test-core");
  }

  @Before
  public void setUp() {
    provider = new SolrCatalogProvider(solrServer, mock(FilterAdapter.class));
  }

  @Test
  public void testCreateAndQuery() throws Exception {
    // Given
    Metacard metacard = new MetacardImpl();
    metacard.setTitle("Integration Test");

    // When
    CreateResponse createResponse = provider.create(new CreateRequestImpl(metacard));
    QueryResponse queryResponse = provider.query(new QueryRequestImpl(
        new QueryImpl(filterBuilder.attribute(Metacard.TITLE).is().like().text("Integration*"))));

    // Then
    assertThat(createResponse.getCreatedMetacards(), hasSize(1));
    assertThat(queryResponse.getResults(), hasSize(1));
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    solrServer.close();
  }
}
```

### Edge Case Testing Checklist

For each module, ensure tests cover:

**Input Validation:**
- [ ] Null inputs
- [ ] Empty collections
- [ ] Invalid types
- [ ] Boundary values (Integer.MAX_VALUE, empty strings, etc.)
- [ ] Special characters (Unicode, SQL injection patterns, XSS)

**Error Handling:**
- [ ] Expected exceptions (IllegalArgumentException, etc.)
- [ ] Runtime exceptions (NullPointerException, etc.)
- [ ] Resource unavailability (network down, Solr down)
- [ ] Timeout scenarios

**Concurrency:**
- [ ] Multiple threads accessing same resource
- [ ] Race conditions
- [ ] Deadlock scenarios (if applicable)

**Security:**
- [ ] Authenticated vs unauthenticated access
- [ ] Insufficient permissions
- [ ] Session expiration
- [ ] Token tampering

**Data Integrity:**
- [ ] Large datasets (100+ results)
- [ ] Special attribute types (binary, XML, geometry)
- [ ] Character encoding (UTF-8, special chars)
- [ ] Serialization round-trips

### Mocking Best Practices

**Do:**
- Mock external dependencies (OSGi services, HTTP clients, Solr)
- Use `@InjectMocks` for class under test
- Use `@Mock` for dependencies
- Verify important interactions with `verify()`
- Use argument captors for complex verification

**Don't:**
- Mock value objects (Metacard, CreateRequest, etc.) - use real instances
- Mock simple utilities (StringUtils, IOUtils) - use real implementations
- Over-mock - keep tests readable
- Use PowerMock unless absolutely necessary (avoid static method testing)

### Test Organization

```
src/test/java/
  org/codice/ddf/catalog/
    CatalogFrameworkImplTest.java          # Happy path tests
    CatalogFrameworkImplErrorTest.java     # Error scenarios
    CatalogFrameworkImplSecurityTest.java  # Security tests
    CatalogFrameworkImplConcurrencyTest.java # Concurrency tests
```

---

## Effort Estimation Methodology

### Estimation Factors

**Per Test Case Effort:**
- Simple unit test: 15-30 minutes
- Complex unit test (multiple mocks): 30-60 minutes
- Integration test: 60-120 minutes
- Security test (authentication/authorization): 45-90 minutes
- Performance test: 90-180 minutes

**Module Complexity Multipliers:**
- Simple (utilities, validators): 1.0x
- Medium (single responsibility services): 1.5x
- Complex (orchestrators, multi-dependency): 2.0x
- Very Complex (security, federation): 2.5x

**Module Size Multipliers:**
- Small (<500 LOC): 1.0x
- Medium (500-2000 LOC): 1.5x
- Large (2000-5000 LOC): 2.0x
- Very Large (>5000 LOC): 2.5x

### Example Calculation: catalog-core-standardframework

**Base Metrics:**
- Source files: 69
- Lines of code: ~15,000 (estimated)
- Complexity: Very Complex (orchestrator, plugins, federation)
- Current coverage: ~60% (estimated)

**Calculation:**
```
Uncovered LOC = 15,000 * (1 - 0.60) = 6,000 LOC
Tests needed = 6,000 / 150 (avg LOC per test) = 40 tests
Complexity factor = 2.5x (very complex)
Size factor = 2.5x (very large)
Base effort = 40 tests * 45 min/test = 1,800 minutes = 30 hours
Adjusted effort = 30 * 2.5 * 1.0 (size already in LOC) = 75 hours
With contingency (20%) = 90 hours
```

**Validation:**
- Estimated: 35-50 tests, 60-90 hours ✓

### Contingency Factors

**Add 20% for:**
- Test infrastructure setup (first test in new module)
- Mock setup complexity
- Documentation
- Code review iterations
- Debugging test failures

**Add 50% for:**
- Modules with no existing tests (bootstrap cost)
- Modules requiring integration test infrastructure
- Security-critical modules (extra review, security testing)
- Modules with external dependencies (complex mocking)

---

## Phased Implementation Plan

### Phase 1: Foundation (Weeks 1-4)
**Goal:** Fix infrastructure, establish 80% coverage for critical modules

**Week 1 Deliverables:**
- [ ] Groovy plugin upgraded to 3.0.2
- [ ] Resource-bundle-locator tests fixed
- [ ] Complete coverage baseline generated
- [ ] Baseline report documented

**Week 2 Deliverables:**
- [ ] security-core-impl: 80% coverage
- [ ] security-filter-web-sso: 80% coverage
- [ ] Test patterns documented

**Week 3 Deliverables:**
- [ ] catalog-core-standardframework: 80% coverage
- [ ] catalog-core-api-impl: 80% coverage

**Week 4 Deliverables:**
- [ ] catalog-solr-provider: 80% coverage
- [ ] catalog-solr-core: 80% coverage
- [ ] 6 security filter modules: 80% coverage each
- [ ] catalog-rest-impl: 80% coverage

**Success Criteria:**
- All P0 security modules ≥80%
- All P0 catalog core modules ≥80%
- Test infrastructure stable
- CI/CD pipeline updated with coverage gates

### Phase 2: Expansion (Weeks 5-8)
**Goal:** Achieve 80% coverage for high-value modules

**Weekly Deliverables:**
- Week 5: REST APIs, OpenSearch modules
- Week 6: Federation, query processing
- Week 7: Security plugins, authorization
- Week 8: CSW, WFS sources

**Success Criteria:**
- All P1 modules ≥80%
- Overall coverage ≥70%
- Integration test framework established

### Phase 3: Consolidation (Weeks 9-12)
**Goal:** Achieve 80% coverage for medium-priority modules

**Weekly Deliverables:**
- Weeks 9-10: 30+ transformer modules
- Week 11: Plugins, utilities
- Week 12: Persistence, admin modules

**Success Criteria:**
- All P2 modules ≥80%
- Overall coverage ≥85%
- Transformer test patterns documented

### Phase 4: Completion (Weeks 13-16)
**Goal:** Achieve 95% overall coverage target

**Weekly Deliverables:**
- Weeks 13-14: Configuration, management modules
- Week 15: UI backend, supporting services
- Week 16: Final verification, documentation

**Success Criteria:**
- All modules (P0-P3) ≥80%
- Overall coverage ≥95%
- Coverage maintenance plan documented
- Team training completed

---

## Success Metrics & Tracking

### Coverage Metrics

**Primary Metrics:**
- Instruction coverage (target: 95%)
- Branch coverage (target: 80%)
- Per-module instruction coverage (target: 80%)

**Secondary Metrics:**
- Test-to-source ratio (aim for 0.5-1.0)
- Test execution time (keep under 15 minutes for unit tests)
- Test stability (flaky test rate <1%)

### Tracking Dashboard

**Weekly Reporting:**
```
Week X Coverage Report
======================
Overall Instruction Coverage: XX.XX% (+/-X.XX% from last week)
Overall Branch Coverage: XX.XX% (+/-X.XX% from last week)

Modules Completed This Week:
  - module-name-1: 45% → 82% (+37%)
  - module-name-2: 60% → 85% (+25%)

Modules In Progress:
  - module-name-3: 55% → 68% (target: 80%, ETA: Week X+1)

Modules Below 80%: XX (down from XX last week)

Tests Added: XXX
Hours Invested: XXX
```

### CI/CD Integration

**Coverage Gates:**
```yaml
# .gitlab-ci.yml or equivalent
test:
  stage: test
  script:
    - mvn clean test jacoco:report
    - bash coverage-check.sh
  coverage: '/Total.*?([0-9]{1,3})%/'
  artifacts:
    reports:
      coverage_report:
        coverage_format: cobertura
        path: target/site/jacoco/jacoco.xml
```

**Coverage Check Script:**
```bash
#!/bin/bash
# coverage-check.sh

OVERALL_TARGET=95
MODULE_TARGET=80

# Check overall coverage
overall=$(awk -F, 'NR>1 {im+=$4; ic+=$5} END {
  printf "%.2f", (ic/(im+ic))*100
}' target/site/jacoco/jacoco.csv)

if (( $(echo "$overall < $OVERALL_TARGET" | bc -l) )); then
  echo "FAIL: Overall coverage $overall% < $OVERALL_TARGET%"
  exit 1
fi

# Check per-module coverage
find . -name jacoco.csv | while read csv; do
  module=$(echo $csv | sed 's|./||' | sed 's|/target.*||')
  coverage=$(awk -F, 'NR>1 {im+=$4; ic+=$5} END {
    if(im+ic>0) printf "%.2f", (ic/(im+ic))*100; else print "0"
  }' "$csv")

  if (( $(echo "$coverage < $MODULE_TARGET" | bc -l) )); then
    echo "FAIL: $module coverage $coverage% < $MODULE_TARGET%"
    exit 1
  fi
done

echo "PASS: All coverage targets met"
```

### Pull Request Requirements

**For new code:**
- Coverage must not decrease
- New code must have ≥80% coverage
- At least one test per public method

**For changes to existing code:**
- Coverage must increase or stay same
- If coverage decreases, justification required in PR description

---

## Testing Best Practices for DDF

### 1. Test Organization

**File Naming:**
- `ClassNameTest.java` - Main test class
- `ClassNameErrorTest.java` - Error scenario tests
- `ClassNameSecurityTest.java` - Security-focused tests
- `ClassNameIntegrationTest.java` - Integration tests

**Test Method Naming:**
```java
@Test
public void testMethodName_condition_expectedResult() {
  // Given-When-Then
}

// Examples:
@Test
public void testCreate_withValidMetacard_returnsCreateResponse() { }

@Test
public void testCreate_withNullInput_throwsIllegalArgumentException() { }

@Test
public void testQuery_withExpiredToken_throwsSecurityException() { }
```

### 2. Given-When-Then Structure

```java
@Test
public void testQuery_withSpatialFilter_returnsMatchingResults() {
  // Given (Arrange)
  Filter spatialFilter = filterBuilder.attribute(Metacard.GEOGRAPHY)
      .withinBuffer().wkt("POINT (1 1)", 100);
  QueryRequest request = new QueryRequestImpl(new QueryImpl(spatialFilter));

  when(provider.query(any())).thenReturn(mockQueryResponse);
  when(mockQueryResponse.getResults()).thenReturn(matchingResults);

  // When (Act)
  QueryResponse response = framework.query(request);

  // Then (Assert)
  assertThat(response.getResults(), hasSize(5));
  assertThat(response.getResults().get(0).getMetacard().getTitle(),
      is("Expected Title"));
  verify(provider).query(argThat(hasProperty("query", spatialFilter)));
}
```

### 3. Assertion Best Practices

**Use Hamcrest matchers:**
```java
// Good
assertThat(result.size(), is(5));
assertThat(result, hasSize(5));
assertThat(result, containsInAnyOrder("a", "b", "c"));
assertThat(metacard.getTitle(), containsString("test"));

// Avoid
assertEquals(5, result.size());
assertTrue(result.size() == 5);
```

**Verify interactions:**
```java
// Verify method called
verify(mockPlugin).process(any());

// Verify method called with specific args
verify(mockPlugin).process(argThat(request ->
    request.getMetacards().size() == 1));

// Verify method NOT called
verify(mockPlugin, never()).process(any());

// Verify call order
InOrder inOrder = inOrder(plugin1, plugin2);
inOrder.verify(plugin1).process(any());
inOrder.verify(plugin2).process(any());
```

### 4. Mock Setup Best Practices

**Mock OSGi services:**
```java
@Mock
private PreIngestPlugin plugin1;

@Mock
private PreIngestPlugin plugin2;

@Before
public void setUp() {
  framework.setPreIngestPlugins(Arrays.asList(plugin1, plugin2));
}
```

**Mock return values:**
```java
// Simple return
when(provider.query(any())).thenReturn(mockResponse);

// Conditional return
when(validator.validate(any())).thenAnswer(invocation -> {
  Metacard m = invocation.getArgument(0);
  if (m.getTitle() == null) {
    return Collections.singleton(new ValidationViolation());
  }
  return Collections.emptySet();
});

// Throw exception
when(provider.create(any())).thenThrow(new CatalogException("error"));
```

### 5. Test Data Builders

```java
public class MetacardTestBuilder {
  private final MetacardImpl metacard = new MetacardImpl();

  public MetacardTestBuilder withTitle(String title) {
    metacard.setTitle(title);
    return this;
  }

  public MetacardTestBuilder withLocation(String wkt) {
    metacard.setLocation(wkt);
    return this;
  }

  public MetacardTestBuilder withSecurity(String... roles) {
    Map<String, Set<String>> security = new HashMap<>();
    security.put("roles", new HashSet<>(Arrays.asList(roles)));
    metacard.setSecurity(security);
    return this;
  }

  public Metacard build() {
    return metacard;
  }
}

// Usage:
@Test
public void testSecurityFiltering() {
  Metacard restricted = new MetacardTestBuilder()
      .withTitle("Classified")
      .withSecurity("ADMIN", "TOP_SECRET")
      .build();
}
```

### 6. Parameterized Tests

```java
@RunWith(Parameterized.class)
public class FilterAdapterTest {

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { "LIKE filter", Filter.LIKE, "title LIKE 'test*'", true },
      { "EQUALS filter", Filter.EQUALS, "title = 'test'", true },
      { "AND filter", Filter.AND, "title = 'test' AND created > 2020-01-01", true },
      { "Invalid filter", Filter.UNKNOWN, "INVALID", false }
    });
  }

  @Parameterized.Parameter(0)
  public String testName;

  @Parameterized.Parameter(1)
  public int filterType;

  @Parameterized.Parameter(2)
  public String filterString;

  @Parameterized.Parameter(3)
  public boolean shouldSucceed;

  @Test
  public void testFilterAdapter() {
    if (shouldSucceed) {
      assertThat(adapter.adapt(filterString), notNullValue());
    } else {
      assertThatThrownBy(() -> adapter.adapt(filterString))
          .isInstanceOf(AdapterException.class);
    }
  }
}
```

### 7. Security Test Patterns

```java
@Test
public void testQuery_asAdmin_returnsAllResults() {
  // Given
  Subject adminSubject = createSubject("admin", "ADMIN");
  QueryRequest request = new QueryRequestImpl(new QueryImpl(Filter.ALL));
  request.getProperties().put(SecurityConstants.SECURITY_SUBJECT, adminSubject);

  // When
  QueryResponse response = framework.query(request);

  // Then
  assertThat(response.getResults(), hasSize(100)); // All results
}

@Test
public void testQuery_asGuest_returnsFilteredResults() {
  // Given
  Subject guestSubject = createSubject("guest", "GUEST");
  QueryRequest request = new QueryRequestImpl(new QueryImpl(Filter.ALL));
  request.getProperties().put(SecurityConstants.SECURITY_SUBJECT, guestSubject);

  // When
  QueryResponse response = framework.query(request);

  // Then
  assertThat(response.getResults(), hasSize(25)); // Filtered
}

private Subject createSubject(String name, String... roles) {
  Subject subject = mock(Subject.class);
  when(subject.getPrincipal()).thenReturn(() -> name);
  // Add role attributes
  return subject;
}
```

### 8. Integration Test Patterns

```java
@RunWith(PaxExam.class)
public class CatalogFrameworkIntegrationTest {

  @Inject
  @Filter(timeout = 30000)
  private CatalogFramework framework;

  @Configuration
  public static Option[] config() {
    return options(
        karafDistributionConfiguration()
            .frameworkUrl(maven().groupId("org.codice.ddf").artifactId("ddf").type("zip"))
            .unpackDirectory(new File("target/exam")),
        features(maven().groupId("org.codice.ddf.catalog").artifactId("catalog-app").type("xml"),
            "catalog-core", "catalog-solr-provider")
    );
  }

  @Test
  public void testCreateAndQuery() throws Exception {
    // Integration test with real OSGi container
    CreateResponse createResponse = framework.create(new CreateRequestImpl(metacard));
    assertThat(createResponse.getCreatedMetacards(), hasSize(1));
  }
}
```

### 9. Test Documentation

```java
/**
 * Tests the CatalogFramework's create operation with pre-ingest plugins.
 *
 * <p>Scenario: A metacard is created with multiple pre-ingest plugins registered.
 * The plugins should execute in service ranking order, and the final metacard
 * should reflect all plugin modifications.
 *
 * <p>Expected behavior:
 * <ul>
 *   <li>Plugin 1 (ranking: 100) executes first</li>
 *   <li>Plugin 2 (ranking: 50) executes second</li>
 *   <li>Final metacard contains modifications from both plugins</li>
 *   <li>CatalogProvider.create() called once with final metacard</li>
 * </ul>
 */
@Test
public void testCreate_withMultiplePlugins_executesInOrder() {
  // Test implementation
}
```

### 10. Continuous Improvement

**Coverage review checklist:**
- [ ] All public methods tested
- [ ] All error conditions tested
- [ ] Edge cases covered (null, empty, boundary values)
- [ ] Security scenarios tested
- [ ] Integration points tested
- [ ] Documentation updated

**Refactoring for testability:**
- Extract dependencies to constructor/setters
- Avoid static methods
- Use dependency injection
- Keep classes focused (Single Responsibility)
- Avoid tight coupling

---

## Risk Assessment & Mitigation

### Technical Risks

#### Risk 1: Test Infrastructure Instability
**Probability:** Medium
**Impact:** High
**Mitigation:**
- Fix Groovy plugin and resource-bundle-locator issues immediately (Week 1)
- Establish stable CI/CD pipeline
- Monitor test execution times
- Address flaky tests promptly

#### Risk 2: Scope Creep
**Probability:** High
**Impact:** Medium
**Mitigation:**
- Strict prioritization (P0 → P1 → P2 → P3)
- Weekly checkpoints
- Focus on 80% threshold, not 100%
- Time-box test development (don't over-engineer)

#### Risk 3: Team Capacity Constraints
**Probability:** Medium
**Impact:** High
**Mitigation:**
- Realistic effort estimates with 20% contingency
- Flexible phasing (can extend to 20 weeks if needed)
- Parallel work streams (security, catalog, utilities)
- Leverage existing high-coverage modules as templates

#### Risk 4: Legacy Code Complexity
**Probability:** High
**Impact:** Medium
**Mitigation:**
- Refactor for testability where necessary
- Accept lower coverage for truly untestable code (document exceptions)
- Prioritize new code and recently modified code
- Use integration tests where unit tests are impractical

### Process Risks

#### Risk 5: Coverage vs Quality Trade-off
**Probability:** Medium
**Impact:** High
**Description:** Pressure to hit 95% may lead to low-quality tests that don't catch bugs

**Mitigation:**
- Code review for all tests
- Focus on meaningful assertions, not just coverage numbers
- Require edge case testing, not just happy paths
- Reject tests that mock everything (no real verification)

#### Risk 6: Test Maintenance Burden
**Probability:** High
**Impact:** Medium
**Description:** 1,000+ new tests will require ongoing maintenance

**Mitigation:**
- Follow DRY principle (test data builders, helper methods)
- Keep tests simple and focused
- Document complex test setups
- Regular test suite cleanup (remove obsolete tests)

### Schedule Risks

#### Risk 7: Delays in Infrastructure Fixes
**Probability:** Low
**Impact:** High
**Mitigation:**
- Prioritize infrastructure fixes in Week 1
- Have backup plan (skip secure-boot if necessary)
- Can start on modules with passing tests while fixing infrastructure

#### Risk 8: Extended Timeline
**Probability:** Medium
**Impact:** Medium
**Mitigation:**
- 16-week plan has built-in flexibility
- Can extend Phase 4 by 4-6 weeks if needed
- P0/P1 modules (Weeks 1-8) are non-negotiable
- P2/P3 modules can be deferred if necessary

### Acceptance Criteria for Risks

**Green Light (Low Risk):**
- All P0 modules ≥80% by Week 4
- Overall coverage ≥70% by Week 8
- CI/CD pipeline stable
- Test execution time <15 minutes

**Yellow Light (Monitor):**
- P0 modules delayed by 1 week
- Overall coverage 65-70% by Week 8
- Some flaky tests (1-3%)
- Test execution time 15-20 minutes

**Red Light (Escalate):**
- P0 modules delayed by >2 weeks
- Overall coverage <65% by Week 8
- Significant flaky test issues (>5%)
- Test execution time >20 minutes
- Team capacity issues

---

## Appendices

### Appendix A: Module Categorization

**P0: Security-Critical (35-40 modules)**
- platform/security/security-core-impl
- platform/security/filter/* (8 modules)
- platform/security/sts/*
- platform/security/policy/*
- catalog/core/catalog-core-standardframework
- catalog/core/catalog-core-api-impl
- catalog/solr/catalog-solr-provider
- catalog/solr/catalog-solr-core
- catalog/rest/catalog-rest-impl

**P1: High-Value (23-25 modules)**
- catalog/rest/* (remaining modules)
- catalog/opensearch/*
- catalog/plugin/catalog-plugin-security-audit
- catalog/plugin/catalog-plugin-federation-replication
- catalog/spatial/csw/*
- catalog/spatial/wfs/*

**P2: Medium-Value (71-75 modules)**
- catalog/transformer/* (30+ modules)
- catalog/plugin/* (remaining 15 modules)
- libs/* (utilities)
- platform/util/*
- platform/mime/*
- platform/persistence/*

**P3: Lower-Priority (75-80 modules)**
- Configuration modules
- UI backend modules
- Distribution/packaging
- Admin tools
- Documentation modules

### Appendix B: Coverage Calculation Examples

**Example 1: Simple Utility Module**
```
Module: libs/checksum
Source files: 3
Lines of code: 450
Current coverage: 100%
Target: 100% (already achieved)
Tests needed: 0
Effort: 0 hours
```

**Example 2: Medium Complexity Module**
```
Module: catalog/common/catalog-common-geo-formatter
Source files: 12
Lines of code: 1,800
Current coverage: 41.86%
Target: 80%
Gap: 38.14% = ~687 LOC uncovered

Tests needed: 687 / 85 LOC per test = 8 tests
Complexity factor: 1.5x (medium)
Base effort: 8 tests * 30 min = 240 minutes = 4 hours
Adjusted: 4 * 1.5 = 6 hours
With contingency: 6 * 1.2 = 7.2 hours
Estimate: 8-12 tests, 14-20 hours ✓
```

**Example 3: Complex Security Module**
```
Module: platform/security/security-core-impl
Source files: 45
Lines of code: 8,500
Current coverage: 50% (estimated)
Target: 80%
Gap: 30% = ~2,550 LOC uncovered

Tests needed: 2,550 / 100 LOC per test = 25 tests
Complexity factor: 2.5x (very complex)
Base effort: 25 tests * 45 min = 1,125 minutes = 18.75 hours
Adjusted: 18.75 * 2.5 = 46.875 hours
With contingency: 46.875 * 1.2 = 56.25 hours
Estimate: 25-35 tests, 40-60 hours ✓
```

### Appendix C: Test Infrastructure Setup

**Required Dependencies:**
```xml
<!-- pom.xml -->
<dependencies>
  <!-- Testing Framework -->
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
  </dependency>

  <!-- Mocking -->
  <dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
  </dependency>

  <!-- Assertions -->
  <dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <scope>test</scope>
  </dependency>

  <!-- Coverage -->
  <dependency>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```

**JaCoCo Plugin Configuration:**
```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.8</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
    <execution>
      <id>check</id>
      <goals>
        <goal>check</goal>
      </goals>
      <configuration>
        <rules>
          <rule>
            <element>BUNDLE</element>
            <limits>
              <limit>
                <counter>INSTRUCTION</counter>
                <value>COVEREDRATIO</value>
                <minimum>0.80</minimum>
              </limit>
            </limits>
          </rule>
        </rules>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### Appendix D: High-Coverage Module Examples

**Study these modules for patterns:**

1. **catalog/validator/catalog-validator-wkt** (100%)
   - File: `/home/e/Development/ddf/catalog/validator/catalog-validator-wkt/src/test/java/org/codice/ddf/validator/wkt/WktValidatorImplTest.java`
   - Pattern: Comprehensive edge case testing for validator

2. **libs/checksum** (100%)
   - Pattern: Pure algorithmic testing, deterministic outputs

3. **libs/klv** (95.22%)
   - Pattern: Format encoding/decoding with extensive format tests

4. **platform/metrics/metrics-servlet-filter** (96.91%)
   - Pattern: Servlet filter testing with request/response mocking

### Appendix E: Testing Resources

**DDF Testing Documentation:**
- Building DDF: https://codice.atlassian.net/wiki/spaces/DDF/pages/70986756
- Contributing Guide: https://github.com/codice/ddf/blob/master/CONTRIBUTING.md

**External Resources:**
- JUnit 4: https://junit.org/junit4/
- Mockito: https://site.mockito.org/
- Hamcrest: http://hamcrest.org/JavaHamcrest/
- JaCoCo: https://www.jacoco.org/jacoco/
- OSGi Testing (Pax Exam): https://ops4j1.jira.com/wiki/spaces/PAXEXAM4/overview

**Books:**
- "Effective Unit Testing" by Lasse Koskela
- "Growing Object-Oriented Software, Guided by Tests" by Freeman & Pryce
- "Working Effectively with Legacy Code" by Michael Feathers

### Appendix F: Weekly Checklist Template

```markdown
## Week X Coverage Sprint

**Goals:**
- [ ] Module 1: XX% → 80% (Est: XX hours)
- [ ] Module 2: XX% → 80% (Est: XX hours)
- [ ] Module 3: XX% → 80% (Est: XX hours)

**Monday:**
- [ ] Sprint planning
- [ ] Review baseline coverage for target modules
- [ ] Setup test infrastructure (if needed)

**Tuesday-Thursday:**
- [ ] Write tests for Module 1
- [ ] Write tests for Module 2
- [ ] Write tests for Module 3
- [ ] Daily: Run `mvn test jacoco:report` to track progress

**Friday:**
- [ ] Code review for all tests
- [ ] Address review feedback
- [ ] Merge completed modules
- [ ] Generate coverage report
- [ ] Update tracking dashboard
- [ ] Retrospective: What went well? What needs improvement?

**Success Criteria:**
- [ ] All target modules ≥80%
- [ ] All tests passing
- [ ] Code review approved
- [ ] Documentation updated
```

### Appendix G: Coverage Report Template

```markdown
# Week X Coverage Report

**Date:** YYYY-MM-DD
**Sprint:** Phase X, Week Y

## Summary

| Metric | Current | Last Week | Change | Target | Status |
|--------|---------|-----------|--------|--------|--------|
| Overall Instruction | XX.XX% | XX.XX% | +/-X.XX% | 95% | 🟢/🟡/🔴 |
| Overall Branch | XX.XX% | XX.XX% | +/-X.XX% | 80% | 🟢/🟡/🔴 |
| Modules ≥80% | XX/457 | XX/457 | +XX | 457 | 🟢/🟡/🔴 |

## Modules Completed This Week

| Module | Before | After | Gap Closed | Tests Added |
|--------|--------|-------|------------|-------------|
| module-1 | XX% | XX% | +XX% | XX |
| module-2 | XX% | XX% | +XX% | XX |

## Modules In Progress

| Module | Current | Target | ETA |
|--------|---------|--------|-----|
| module-3 | XX% | 80% | Week X+1 |

## Challenges

- Challenge 1: Description and mitigation
- Challenge 2: Description and mitigation

## Next Week Plan

- [ ] Complete module-3
- [ ] Start module-4, module-5, module-6
- [ ] Address technical debt item X

## Metrics

- **Tests Added:** XXX
- **Hours Invested:** XXX
- **Test Execution Time:** XX minutes
- **Flaky Tests:** X (X%)
```

---

## Conclusion

This test coverage improvement plan provides a comprehensive roadmap to achieve 95% overall coverage and 80% per-module coverage for the DDF project. The phased approach prioritizes security-critical and core modules first, ensuring the most important code is well-tested.

**Key Success Factors:**
1. **Fix infrastructure blockers immediately** (Week 1)
2. **Follow strict prioritization** (P0 → P1 → P2 → P3)
3. **Leverage existing test patterns** from high-coverage modules
4. **Maintain quality over quantity** - meaningful tests, not just coverage numbers
5. **Track progress weekly** and adjust as needed
6. **Build for maintainability** - tests should be simple, focused, and well-documented

**Realistic Timeline:**
- **Optimistic:** 12 weeks (if all goes smoothly, team at full capacity)
- **Realistic:** 16 weeks (with normal challenges and adjustments)
- **Pessimistic:** 20 weeks (with significant obstacles or capacity constraints)

**Investment:**
- **Total Effort:** 1,508-2,267 hours (estimated)
- **Team Size:** 3-4 developers @ 40 hours/week = 120-160 hours/week
- **Duration:** 1,508 hours / 120 hours/week = **12.5 weeks minimum**
- **With contingency:** 16-20 weeks recommended

The plan is ambitious but achievable with dedicated focus and proper execution. By the end, DDF will have industry-leading test coverage, significantly reduced regression risk, and a solid foundation for ongoing development.

---

**Next Steps:**
1. Review and approve this plan
2. Fix test infrastructure blockers (Week 1)
3. Generate complete coverage baseline
4. Begin Phase 1 execution
5. Establish weekly tracking and reporting

**Document Maintained By:** DDF Development Team
**Last Updated:** 2025-10-21
**Next Review:** After Week 4 completion
