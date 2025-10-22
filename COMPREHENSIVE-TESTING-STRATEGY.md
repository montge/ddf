# DDF Comprehensive Testing Strategy

**Date:** 2025-10-21
**Status:** Implementation Plan
**Coverage Target:** 80% per-module, 90-95% overall
**Java Support:** LTS versions (Java 11, 17, 21)

---

## Executive Summary

This document outlines a comprehensive testing strategy for DDF that goes beyond CVE testing to include:
- **Unit Tests** - Component-level testing
- **Integration Tests** - Module interaction testing
- **End-to-End Tests** - Full system workflow testing
- **Requirements-based Tests** - Functional requirements validation
- **Java LTS Compatibility** - Java 11, 17, 21 support

---

## Java LTS Version Support Strategy

### Current Status
- **DDF Target:** Java 11 (LTS - EOL September 2026)
- **Runtime Environment:** Java 21 (Latest LTS - support until 2029)
- **Issue:** Test infrastructure now Java 21 compatible, but DDF officially targets Java 11

### Recommended Approach: Multi-Version Testing

**Strategy:** Support Java 11 (minimum) with Java 17 and 21 compatibility

```yaml
# CI/CD Matrix Strategy
java-versions:
  - 11  # Minimum supported (current target)
  - 17  # LTS (September 2021 - September 2029)
  - 21  # Latest LTS (September 2023 - September 2031)
```

### Java Version Compatibility Requirements

| Component | Java 11 | Java 17 | Java 21 | Notes |
|-----------|---------|---------|---------|-------|
| **Compilation** | ✅ Required | ✅ Should Work | ✅ Should Work | Target bytecode: Java 11 |
| **Unit Tests** | ✅ Required | ✅ Required | ✅ Required | All tests must pass |
| **Integration Tests** | ✅ Required | ✅ Required | ✅ Required | Cross-version validation |
| **Runtime** | ✅ Required | ✅ Supported | ✅ Supported | Forward compatibility |
| **Mockito** | 4.11.0 | 4.11.0 | 4.11.0 | Supports all 3 versions |
| **Groovy** | 4.0.23 | 4.0.23 | 4.0.23 | Supports all 3 versions |

### Implementation Steps

1. **Update Maven Compiler Plugin:**
```xml
<properties>
    <!-- Target Java 11 bytecode for maximum compatibility -->
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <maven.compiler.release>11</maven.compiler.release>
</properties>
```

2. **Add CI Matrix Testing:**
```yaml
# .github/workflows/test.yml
strategy:
  matrix:
    java: [11, 17, 21]
steps:
  - name: Set up JDK ${{ matrix.java }}
    uses: actions/setup-java@v3
    with:
      java-version: ${{ matrix.java }}
      distribution: 'temurin'
```

3. **Document Java Support Policy:**
- **Minimum:** Java 11 (required)
- **Recommended:** Java 17 (LTS)
- **Tested:** Java 21 (latest LTS)

---

## Testing Pyramid Strategy

```
           /\
          /  \     E2E Tests (5%)
         /____\    - Full system workflows
        /      \   - User scenarios
       /________\  Integration Tests (15%)
      /          \ - Module interactions
     /____________\
    /              \ Unit Tests (80%)
   /________________\ - Component testing
                      - Requirements validation
```

### Coverage Targets by Layer

| Test Layer | Coverage Target | Count Estimate | Effort (hours) |
|------------|-----------------|----------------|----------------|
| **Unit Tests** | 85-90% | 800-1000 tests | 400-500 |
| **Integration Tests** | 75-80% | 150-200 tests | 150-200 |
| **End-to-End Tests** | Key workflows | 30-50 tests | 100-150 |
| **Total** | 90-95% overall | 980-1250 tests | 650-850 |

---

## Unit Testing Strategy (80% of tests)

### Purpose
Test individual components in isolation with mocked dependencies.

### Coverage Areas

#### 1. **Business Logic Testing**
- ✅ All service methods
- ✅ All utility methods
- ✅ All transformers
- ✅ All validators

**Example: CatalogFramework**
```java
@Test
public void testCreateRequestWithValidMetacard() {
    // Unit test for create operation
    CreateRequest request = new CreateRequestImpl(metacard);
    CreateResponse response = framework.create(request);
    assertThat(response.getCreatedMetacards().size(), is(1));
}

@Test
public void testCreateRequestWithNullMetacard() {
    // Negative case
    CreateRequest request = new CreateRequestImpl(null);
    assertThrows(IllegalArgumentException.class,
        () -> framework.create(request));
}

@Test
public void testCreateRequestWithDuplicateId() {
    // Edge case
    // ...
}
```

#### 2. **Data Model Testing**
- ✅ Metacard serialization/deserialization
- ✅ Attribute type validation
- ✅ MetacardType registration
- ✅ Security attribute handling

**Example: MetacardImpl**
```java
@Test
public void testMetacardSerialization() {
    Metacard original = new MetacardImpl();
    original.setTitle("Test");

    // Serialize
    byte[] bytes = serialize(original);

    // Deserialize
    Metacard restored = deserialize(bytes);

    assertThat(restored.getTitle(), equalTo("Test"));
}
```

#### 3. **Security Testing (Beyond CVEs)**
- ✅ Authentication flows
- ✅ Authorization checks
- ✅ Role-based access control
- ✅ Token validation
- ✅ Session management

**Example: SecurityManagerImpl**
```java
@Test
public void testUserWithAdminRoleCanDeleteMetacard() {
    Subject subject = createSubjectWithRole("admin");
    DeleteRequest request = new DeleteRequestImpl("metacard-123");

    // Should succeed
    DeleteResponse response = securityManager.authorize(subject, request);
    assertThat(response.isAuthorized(), is(true));
}

@Test
public void testUserWithoutAdminRoleCannotDeleteMetacard() {
    Subject subject = createSubjectWithRole("user");
    DeleteRequest request = new DeleteRequestImpl("metacard-123");

    // Should fail
    assertThrows(UnauthorizedException.class,
        () -> securityManager.authorize(subject, request));
}
```

#### 4. **Requirements-Based Testing**

Based on functional requirements, create tests that validate:

**Requirement:** "The system shall support federated queries across multiple sources"
```java
@Test
public void testFederatedQueryExecutesOnAllSources() {
    // Setup 3 mock sources
    FederatedSource source1 = mock(FederatedSource.class);
    FederatedSource source2 = mock(FederatedSource.class);
    FederatedSource source3 = mock(FederatedSource.class);

    // Execute query
    QueryRequest request = new QueryRequestImpl(query);
    QueryResponse response = framework.query(request);

    // Verify all sources queried
    verify(source1, times(1)).query(any(QueryRequest.class));
    verify(source2, times(1)).query(any(QueryRequest.class));
    verify(source3, times(1)).query(any(QueryRequest.class));
}
```

**Requirement:** "The system shall return results within 30 seconds"
```java
@Test(timeout = 30000)
public void testQueryReturnsWithin30Seconds() {
    QueryRequest request = createLargeQuery();
    QueryResponse response = framework.query(request);
    assertThat(response.getResults().size(), greaterThan(0));
}
```

#### 5. **Edge Case Testing**
- ✅ Null inputs
- ✅ Empty collections
- ✅ Boundary values
- ✅ Invalid formats
- ✅ Concurrent access

**Example: Boundary Testing**
```java
@Test
public void testQueryWithMaxPageSize() {
    Query query = new QueryImpl(filter, 1, 1000, sortBy, false, 30000);
    // Should work
}

@Test
public void testQueryWithExcessivePageSize() {
    Query query = new QueryImpl(filter, 1, 10001, sortBy, false, 30000);
    assertThrows(IllegalArgumentException.class,
        () -> framework.query(new QueryRequestImpl(query)));
}
```

---

## Integration Testing Strategy (15% of tests)

### Purpose
Test interactions between multiple modules without full system deployment.

### Coverage Areas

#### 1. **Module Integration**
Test how modules communicate via OSGi services.

**Example: Catalog + Security Integration**
```java
@Test
public void testSecurityFilteringIntegratesWithCatalogQuery() {
    // Create secured metacard
    Metacard metacard = new MetacardImpl();
    metacard.setSecurity(securityAttributes("SECRET"));

    catalogProvider.create(new CreateRequestImpl(metacard));

    // Query as user without SECRET clearance
    Subject userSubject = createSubjectWithClearance("CONFIDENTIAL");
    QueryResponse response = executeAsSubject(userSubject, query);

    // Metacard should be filtered out
    assertThat(response.getResults().size(), is(0));

    // Query as user with SECRET clearance
    Subject adminSubject = createSubjectWithClearance("SECRET");
    response = executeAsSubject(adminSubject, query);

    // Metacard should be included
    assertThat(response.getResults().size(), is(1));
}
```

#### 2. **Plugin Chain Integration**
Test plugin execution order and data flow.

**Example: Pre/Post Plugin Chain**
```java
@Test
public void testPreIngestPluginsExecuteInOrder() {
    // Register plugins with different priorities
    PreIngestPlugin plugin1 = new ValidationPlugin(); // priority 100
    PreIngestPlugin plugin2 = new SecurityPlugin();   // priority 50

    registerService(plugin1, 100);
    registerService(plugin2, 50);

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateResponse response = framework.create(request);

    // Verify execution order: plugin2 then plugin1
    InOrder inOrder = inOrder(plugin2, plugin1);
    inOrder.verify(plugin2).process(any(CreateRequest.class));
    inOrder.verify(plugin1).process(any(CreateRequest.class));
}
```

#### 3. **Federation Integration**
Test source federation and result aggregation.

**Example: Multi-Source Federation**
```java
@Test
public void testFederatedQueryAggregatesResultsFromMultipleSources() {
    // Setup sources with different result sets
    FederatedSource localSource = createSourceWithResults(5);
    FederatedSource remoteSource1 = createSourceWithResults(3);
    FederatedSource remoteSource2 = createSourceWithResults(2);

    registerSources(localSource, remoteSource1, remoteSource2);

    QueryRequest request = new QueryRequestImpl(query);
    QueryResponse response = framework.query(request);

    // Should aggregate all results
    assertThat(response.getResults().size(), is(10));

    // Verify result ordering by relevance
    List<Result> results = response.getResults();
    assertThat(results.get(0).getRelevanceScore(),
               greaterThanOrEqualTo(results.get(9).getRelevanceScore()));
}
```

#### 4. **Transformer Integration**
Test transformer chains and format conversions.

**Example: Metacard → JSON → Metacard**
```java
@Test
public void testMetacardJsonTransformerRoundTrip() {
    // Original metacard
    Metacard original = createTestMetacard();

    // Transform to JSON
    MetacardTransformer jsonTransformer = getTransformer("json");
    BinaryContent jsonContent = jsonTransformer.transform(original, null);

    // Transform back to metacard
    InputTransformer inputTransformer = getInputTransformer("json");
    Metacard restored = inputTransformer.transform(
        jsonContent.getInputStream());

    // Should match original
    assertThat(restored.getId(), equalTo(original.getId()));
    assertThat(restored.getTitle(), equalTo(original.getTitle()));
    assertThat(restored.getAttribute("location"),
               equalTo(original.getAttribute("location")));
}
```

#### 5. **Database Integration**
Test persistence layer interactions.

**Example: Solr Integration**
```java
@Test
public void testSolrProviderPersistsAndRetrievesMetacard() {
    SolrCatalogProvider provider = new SolrCatalogProvider(solrClient);

    // Create metacard
    Metacard metacard = new MetacardImpl();
    metacard.setTitle("Integration Test");

    CreateResponse createResponse = provider.create(
        new CreateRequestImpl(metacard));

    String id = createResponse.getCreatedMetacards().get(0).getId();

    // Retrieve metacard
    Query query = new QueryImpl(
        filterBuilder.attribute(Metacard.ID).is().equalTo().text(id));
    QueryResponse queryResponse = provider.query(
        new QueryRequestImpl(query));

    // Verify persistence
    assertThat(queryResponse.getResults().size(), is(1));
    assertThat(queryResponse.getResults().get(0).getMetacard().getTitle(),
               equalTo("Integration Test"));
}
```

---

## End-to-End Testing Strategy (5% of tests)

### Purpose
Test complete user workflows with real system deployment (or embedded container).

### Coverage Areas

#### 1. **User Workflows**

**Workflow: Ingest → Search → Retrieve**
```java
@Test
public void testCompleteIngestSearchRetrieveWorkflow() {
    // 1. INGEST: User uploads a file via REST endpoint
    Response ingestResponse = given()
        .multiPart("file", testFile)
        .when()
        .post("/catalog")
        .then()
        .statusCode(201)
        .extract().response();

    String metacardId = ingestResponse.path("id");

    // 2. SEARCH: User searches for the ingested file
    Response searchResponse = given()
        .queryParam("q", "title:TestDocument")
        .when()
        .get("/catalog/query")
        .then()
        .statusCode(200)
        .extract().response();

    assertThat(searchResponse.jsonPath().getList("results").size(),
               greaterThan(0));

    // 3. RETRIEVE: User downloads the file
    Response retrieveResponse = given()
        .when()
        .get("/catalog/" + metacardId + "/resource")
        .then()
        .statusCode(200)
        .extract().response();

    assertThat(retrieveResponse.getBody().asByteArray().length,
               greaterThan(0));
}
```

**Workflow: Security - Login → Search → Logout**
```java
@Test
public void testSecureSearchWorkflow() {
    // 1. LOGIN: User authenticates
    String token = given()
        .auth().basic("testuser", "password")
        .when()
        .post("/login")
        .then()
        .statusCode(200)
        .extract().path("token");

    // 2. SEARCH: User performs authorized search
    given()
        .header("Authorization", "Bearer " + token)
        .queryParam("q", "*")
        .when()
        .get("/catalog/query")
        .then()
        .statusCode(200);

    // 3. LOGOUT: User logs out
    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .post("/logout")
        .then()
        .statusCode(200);

    // 4. VERIFY: Token no longer valid
    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/catalog/query")
        .then()
        .statusCode(401);
}
```

#### 2. **Federation Workflows**

**Workflow: Multi-Source Federated Search**
```java
@Test
public void testFederatedSearchAcrossMultipleSources() {
    // Setup: Start 3 DDF instances
    DdfInstance local = startDdfInstance(8993);
    DdfInstance remote1 = startDdfInstance(8994);
    DdfInstance remote2 = startDdfInstance(8995);

    // Configure federation
    local.configureFederatedSource("Remote1", "https://localhost:8994");
    local.configureFederatedSource("Remote2", "https://localhost:8995");

    // Ingest data to each source
    remote1.ingest("remote1-data.xml");
    remote2.ingest("remote2-data.xml");

    // Execute federated query from local
    Response response = local.query("*");

    // Should aggregate results from all sources
    assertThat(response.jsonPath().getList("results").size(), is(2));

    // Cleanup
    local.shutdown();
    remote1.shutdown();
    remote2.shutdown();
}
```

#### 3. **Performance Testing**

**Workflow: Load Test - 1000 Concurrent Queries**
```java
@Test
public void testSystemHandles1000ConcurrentQueries() {
    ExecutorService executor = Executors.newFixedThreadPool(100);
    List<Future<Response>> futures = new ArrayList<>();

    // Submit 1000 queries
    for (int i = 0; i < 1000; i++) {
        futures.add(executor.submit(() ->
            given()
                .queryParam("q", "*")
                .when()
                .get("/catalog/query")
        ));
    }

    // Collect results
    int successCount = 0;
    for (Future<Response> future : futures) {
        Response response = future.get(60, TimeUnit.SECONDS);
        if (response.statusCode() == 200) {
            successCount++;
        }
    }

    // 95% should succeed (allowing for some timeouts)
    assertThat(successCount, greaterThan(950));

    executor.shutdown();
}
```

#### 4. **Disaster Recovery**

**Workflow: Node Failure Recovery**
```java
@Test
public void testFederationResilientToNodeFailure() {
    DdfInstance local = startDdfInstance(8993);
    DdfInstance remote1 = startDdfInstance(8994);
    DdfInstance remote2 = startDdfInstance(8995);

    local.configureFederatedSource("Remote1", "https://localhost:8994");
    local.configureFederatedSource("Remote2", "https://localhost:8995");

    // Initial query - all sources available
    Response response1 = local.query("*");
    assertThat(response1.jsonPath().getList("results").size(), is(X));

    // Simulate node failure
    remote1.shutdown();

    // Query should still work with remaining sources
    Response response2 = local.query("*");
    assertThat(response2.statusCode(), is(200));
    assertThat(response2.jsonPath().getList("results").size(),
               greaterThan(0));

    // Cleanup
    local.shutdown();
    remote2.shutdown();
}
```

---

## Requirements-Based Testing

### Functional Requirements Coverage

For each requirement in the requirements document, create test(s):

**Example Requirements:**

| Requirement ID | Description | Test Type | Test Method |
|----------------|-------------|-----------|-------------|
| REQ-001 | System shall support metadata ingest | Unit | `testMetadataIngestWithValidData()` |
| REQ-002 | System shall validate metadata schema | Unit | `testSchemaValidationRejectsInvalid()` |
| REQ-003 | System shall index searchable fields | Integration | `testFieldIndexingEnablesSearch()` |
| REQ-004 | System shall return results < 30s | E2E | `testQueryPerformanceUnder30Seconds()` |
| REQ-005 | System shall support 100 concurrent users | E2E | `testConcurrentUserLoad()` |

### Non-Functional Requirements Coverage

| Category | Requirement | Test Approach |
|----------|-------------|---------------|
| **Performance** | Queries < 30s | Performance tests with timers |
| **Scalability** | 100 concurrent users | Load testing with JMeter/Gatling |
| **Security** | Role-based access | Security integration tests |
| **Reliability** | 99.9% uptime | Chaos engineering tests |
| **Maintainability** | 80% code coverage | JaCoCo coverage reports |

---

## Test Organization Structure

```
src/test/java/
├── unit/                          # Unit tests (80%)
│   ├── catalog/
│   │   ├── core/
│   │   │   ├── CatalogFrameworkTest.java
│   │   │   ├── QueryOperationsTest.java
│   │   │   └── CreateOperationsTest.java
│   │   ├── data/
│   │   │   ├── MetacardImplTest.java
│   │   │   └── MetacardTypeTest.java
│   │   └── transformer/
│   │       ├── JsonTransformerTest.java
│   │       └── XmlTransformerTest.java
│   ├── security/
│   │   ├── SecurityManagerTest.java
│   │   ├── AuthenticationTest.java
│   │   └── AuthorizationTest.java
│   └── platform/
│       ├── solr/
│       └── persistence/
├── integration/                   # Integration tests (15%)
│   ├── catalog/
│   │   ├── CatalogSecurityIntegrationTest.java
│   │   ├── PluginChainIntegrationTest.java
│   │   └── FederationIntegrationTest.java
│   ├── transformer/
│   │   └── TransformerChainIntegrationTest.java
│   └── solr/
│       └── SolrPersistenceIntegrationTest.java
├── e2e/                           # End-to-end tests (5%)
│   ├── workflows/
│   │   ├── IngestSearchRetrieveWorkflowTest.java
│   │   ├── SecureSearchWorkflowTest.java
│   │   └── FederatedSearchWorkflowTest.java
│   ├── performance/
│   │   ├── ConcurrentQueryLoadTest.java
│   │   └── BulkIngestPerformanceTest.java
│   └── reliability/
│       └── NodeFailureRecoveryTest.java
└── security/                      # Security-specific tests
    ├── cve/
    │   ├── Log4ShellVulnerabilityTest.java
    │   ├── CommonsFileUploadSecurityTest.java
    │   ├── JacksonDeserializationSecurityTest.java
    │   └── ApacheCxfSecurityTest.java
    └── auth/
        ├── SamlAuthenticationTest.java
        ├── OAuthAuthenticationTest.java
        └── X509AuthenticationTest.java
```

---

## Coverage Measurement Strategy

### Tools

1. **JaCoCo** - Line/branch coverage
2. **Sonar** - Quality metrics
3. **Mutation Testing (PIT)** - Test quality

### Coverage Targets by Module Type

| Module Type | Target | Rationale |
|-------------|--------|-----------|
| **Core Catalog** | 90% | Critical business logic |
| **Security** | 95% | Security-critical |
| **Transformers** | 85% | Format conversion |
| **REST APIs** | 90% | External interfaces |
| **Utilities** | 80% | Helper functions |
| **UI** | 60% | UI testing expensive |

### Per-Module Coverage Report

```bash
# Generate coverage for all modules
mvn clean test jacoco:report

# Generate aggregate report
mvn jacoco:report-aggregate

# View coverage
open target/site/jacoco-aggregate/index.html
```

### Coverage Gates

**Pre-commit checks:**
- New code must have 80%+ coverage
- Cannot merge if coverage drops below threshold

**Build pipeline:**
```xml
<jacoco>
    <rules>
        <rule>
            <element>BUNDLE</element>
            <limits>
                <limit>
                    <counter>INSTRUCTION</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.80</minimum>
                </limit>
                <limit>
                    <counter>BRANCH</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.75</minimum>
                </limit>
            </limits>
        </rule>
    </rules>
</jacoco>
```

---

## Test Execution Strategy

### Local Development

```bash
# Run all unit tests
mvn test

# Run integration tests
mvn integration-test

# Run specific test
mvn test -Dtest=CatalogFrameworkTest

# Run with coverage
mvn test jacoco:report
```

### CI/CD Pipeline

```yaml
# .github/workflows/test.yml
name: Test Suite

on: [push, pull_request]

jobs:
  unit-tests:
    strategy:
      matrix:
        java: [11, 17, 21]  # Test on all LTS versions
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK ${{ matrix.java }}
        uses: actions/setup-java@v3
        with:
          java-version: ${{ matrix.java }}
      - name: Run unit tests
        run: mvn test
      - name: Generate coverage
        run: mvn jacoco:report
      - name: Upload coverage
        uses: codecov/codecov-action@v3

  integration-tests:
    needs: unit-tests
    runs-on: ubuntu-latest
    steps:
      - name: Start SolrCloud
        run: docker-compose up -d solr
      - name: Run integration tests
        run: mvn integration-test

  e2e-tests:
    needs: integration-tests
    runs-on: ubuntu-latest
    steps:
      - name: Deploy DDF
        run: ./scripts/deploy-ddf.sh
      - name: Run E2E tests
        run: mvn verify -Pe2e-tests
```

---

## Next Steps: Implementation Plan

### Week 1-2: Infrastructure Setup
- ✅ Java LTS compatibility matrix (11, 17, 21)
- ✅ Update maven-compiler-plugin for Java 11 target
- ✅ Configure CI/CD matrix testing
- □ Setup coverage reporting infrastructure

### Week 3-4: Unit Test Expansion
- □ Identify modules below 80% coverage (18 modules found)
- □ Create unit tests for catalog-core-api (17% → 85%)
- □ Create unit tests for security modules (40-70% → 85%)
- □ Create unit tests for platform modules

### Week 5-6: Integration Test Creation
- □ Catalog + Security integration (50 tests)
- □ Plugin chain integration (30 tests)
- □ Federation integration (40 tests)
- □ Transformer integration (30 tests)

### Week 7-8: E2E Test Development
- □ Ingest workflows (10 tests)
- □ Search workflows (10 tests)
- □ Security workflows (10 tests)
- □ Performance tests (10 tests)

### Week 9-10: Requirements Validation
- □ Map all requirements to tests
- □ Create missing requirement tests
- □ Validate coverage of functional requirements
- □ Validate coverage of non-functional requirements

### Week 11-12: Finalization
- □ Achieve 90-95% overall coverage
- □ Document test strategy
- □ Train team on testing practices
- □ Establish coverage gates

---

## Success Metrics

| Metric | Current | Target | Timeline |
|--------|---------|--------|----------|
| **Overall Coverage** | 65.98% | 90-95% | 12 weeks |
| **Module Coverage** | 18/48 below 80% | 0/455 below 80% | 12 weeks |
| **Unit Tests** | ~500 | 1000 | 8 weeks |
| **Integration Tests** | ~50 | 150-200 | 10 weeks |
| **E2E Tests** | ~10 | 30-50 | 12 weeks |
| **Java LTS Support** | Java 11 only | Java 11, 17, 21 | 2 weeks |
| **CI/CD Integration** | Partial | Full matrix | 2 weeks |

---

## Document Version: 1.0
**Status:** Ready for Implementation
**Next Action:** Begin infrastructure setup and Java LTS compatibility work
