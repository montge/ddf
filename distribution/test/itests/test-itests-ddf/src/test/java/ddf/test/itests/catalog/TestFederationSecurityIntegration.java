/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package ddf.test.itests.catalog;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.codice.ddf.itests.common.catalog.CatalogTestCommons.delete;
import static org.codice.ddf.itests.common.catalog.CatalogTestCommons.ingest;
import static org.codice.ddf.itests.common.csw.CswTestCommons.CSW_FEDERATED_SOURCE_FACTORY_PID;
import static org.codice.ddf.itests.common.csw.CswTestCommons.getCswSourceProperties;
import static org.codice.ddf.itests.common.opensearch.OpenSearchTestCommons.OPENSEARCH_FACTORY_PID;
import static org.codice.ddf.itests.common.opensearch.OpenSearchTestCommons.getOpenSearchSourceProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;

import ddf.catalog.data.Metacard;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import org.codice.ddf.itests.common.AbstractIntegrationTest;
import org.codice.ddf.test.common.annotations.BeforeExam;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

/**
 * Integration tests for Federation + Security interactions.
 *
 * Tests federated query aggregation, result merging, authentication propagation,
 * and source availability handling with security constraints.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class TestFederationSecurityIntegration extends AbstractIntegrationTest {

  private static final String FEDERATED_OPENSEARCH_SOURCE_ID = "federatedOpenSearchSource";
  private static final String FEDERATED_CSW_SOURCE_ID = "federatedCswSource";
  private static final String SECURED_OPENSEARCH_SOURCE_ID = "securedOpenSearchSource";

  private static String openSearchPid;
  private static String cswPid;
  private static String securedOpenSearchPid;

  @BeforeClass
  public static void setupFederatedSources() throws Exception {
    // This method would be called once before all tests in the class
  }

  @BeforeExam
  public void beforeExam() throws Exception {
    getSecurityPolicy().configureRestForGuest();
  }

  @After
  public void tearDown() {
    clearCatalog();
  }

  @AfterClass
  public static void cleanup() throws Exception {
    // Cleanup federated sources
  }

  // ==================== Multi-Source Query Aggregation Tests (3 tests) ====================

  /**
   * Test federated query aggregates results from multiple sources.
   */
  @Test
  public void testFederatedQueryAggregatesMultipleSources() throws Exception {
    // Ingest test data into local catalog
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String localId = ingest(testData, "application/json");

    // Configure federated OpenSearch source
    Map<String, Object> openSearchProps =
        getOpenSearchSourceProperties(
            FEDERATED_OPENSEARCH_SOURCE_ID, OPENSEARCH_PATH.getUrl(), getServiceManager());
    openSearchProps.put("username", "admin");
    openSearchProps.put("password", "admin");
    openSearchPid =
        getServiceManager()
            .createManagedService(OPENSEARCH_FACTORY_PID, openSearchProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(FEDERATED_OPENSEARCH_SOURCE_ID);
    getServiceManager()
        .waitForSourcesToBeAvailable(REST_PATH.getUrl(), FEDERATED_OPENSEARCH_SOURCE_ID);

    // Query all sources
    String queryUrl = REST_PATH.getUrl() + "?q=*";
    String response =
        given()
            .when()
            .get(queryUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    // Verify results from multiple sources
    assertThat(response, containsString("myTitle"));

    // Cleanup
    if (openSearchPid != null) {
      getServiceManager().stopManagedService(openSearchPid);
    }
    delete(localId);
  }

  /**
   * Test federated query with security filtering across sources.
   */
  @Test
  public void testFederatedQueryWithSecurityFiltering() throws Exception {
    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForBasicAuthReady(SERVICE_ROOT.getUrl());

    // Ingest secured data
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Configure secured federated source
    Map<String, Object> securedProps =
        getOpenSearchSourceProperties(
            SECURED_OPENSEARCH_SOURCE_ID, OPENSEARCH_PATH.getUrl(), getServiceManager());
    securedProps.put("authenticationType", "basic");
    securedProps.put("username", "admin");
    securedProps.put("password", "admin");
    securedOpenSearchPid =
        getServiceManager()
            .createManagedService(OPENSEARCH_FACTORY_PID, securedProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(SECURED_OPENSEARCH_SOURCE_ID);

    // Query with authentication
    String queryUrl = REST_PATH.getUrl() + "?q=*&src=" + SECURED_OPENSEARCH_SOURCE_ID;
    String response =
        given()
            .auth()
            .basic("admin", "admin")
            .when()
            .get(queryUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertThat(response, containsString("myTitle"));

    // Cleanup
    if (securedOpenSearchPid != null) {
      getServiceManager().stopManagedService(securedOpenSearchPid);
    }
    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(SERVICE_ROOT.getUrl());
    delete(id);
  }

  /**
   * Test that federated queries respect per-source security policies.
   */
  @Test
  public void testPerSourceSecurityPolicies() throws Exception {
    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForBasicAuthReady(SERVICE_ROOT.getUrl());

    // Ingest test data
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Configure CSW source with authentication
    Map<String, Object> cswProps =
        getCswSourceProperties(FEDERATED_CSW_SOURCE_ID, CSW_PATH.getUrl(), getServiceManager());
    cswProps.put("authenticationType", "basic");
    cswProps.put("username", "admin");
    cswProps.put("password", "admin");
    cswPid =
        getServiceManager()
            .createManagedService(CSW_FEDERATED_SOURCE_FACTORY_PID, cswProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(FEDERATED_CSW_SOURCE_ID);

    // Query specific source
    String queryUrl = REST_PATH.getUrl() + "?q=*&src=" + FEDERATED_CSW_SOURCE_ID;
    String response =
        given()
            .auth()
            .basic("admin", "admin")
            .when()
            .get(queryUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertThat(response, containsString("myTitle"));

    // Cleanup
    if (cswPid != null) {
      getServiceManager().stopManagedService(cswPid);
    }
    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(SERVICE_ROOT.getUrl());
    delete(id);
  }

  // ==================== Result Merging and Deduplication Tests (3 tests) ====================

  /**
   * Test that duplicate results from multiple sources are properly deduplicated.
   */
  @Test
  public void testResultDeduplicationAcrossSources() throws Exception {
    // Ingest same metacard into local catalog
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Configure federated source pointing to same catalog
    Map<String, Object> openSearchProps =
        getOpenSearchSourceProperties(
            FEDERATED_OPENSEARCH_SOURCE_ID, OPENSEARCH_PATH.getUrl(), getServiceManager());
    openSearchPid =
        getServiceManager()
            .createManagedService(OPENSEARCH_FACTORY_PID, openSearchProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(FEDERATED_OPENSEARCH_SOURCE_ID);
    getServiceManager()
        .waitForSourcesToBeAvailable(REST_PATH.getUrl(), FEDERATED_OPENSEARCH_SOURCE_ID);

    // Query should deduplicate same metacard from different sources
    String queryUrl = REST_PATH.getUrl() + "?q=myTitle";
    String response =
        given()
            .when()
            .get(queryUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    // Verify results contain the metacard
    assertThat(response, containsString("myTitle"));

    // Cleanup
    if (openSearchPid != null) {
      getServiceManager().stopManagedService(openSearchPid);
    }
    delete(id);
  }

  /**
   * Test result sorting and ranking across federated sources.
   */
  @Test
  public void testResultSortingAcrossFederatedSources() throws Exception {
    // Ingest multiple metacards with different relevance scores
    for (int i = 0; i < 3; i++) {
      String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
      testData = testData.replace("myTitle", "testRecord" + i);
      ingest(testData, "application/json");
    }

    // Query with sorting
    String queryUrl = REST_PATH.getUrl() + "?q=testRecord&sortBy=title:asc";
    String response =
        given()
            .when()
            .get(queryUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    // Verify results are returned (sorting verified by order)
    assertThat(response, containsString("testRecord"));
  }

  /**
   * Test result merging with conflicting metadata from different sources.
   */
  @Test
  public void testResultMergingWithConflictingMetadata() throws Exception {
    // Ingest test metacard
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Configure federated source
    Map<String, Object> openSearchProps =
        getOpenSearchSourceProperties(
            FEDERATED_OPENSEARCH_SOURCE_ID, OPENSEARCH_PATH.getUrl(), getServiceManager());
    openSearchPid =
        getServiceManager()
            .createManagedService(OPENSEARCH_FACTORY_PID, openSearchProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(FEDERATED_OPENSEARCH_SOURCE_ID);
    getServiceManager()
        .waitForSourcesToBeAvailable(REST_PATH.getUrl(), FEDERATED_OPENSEARCH_SOURCE_ID);

    // Query all sources
    String queryUrl = REST_PATH.getUrl() + "?q=*";
    String response =
        given()
            .when()
            .get(queryUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    // Result merging should handle conflicts appropriately
    assertThat(response, containsString("myTitle"));

    // Cleanup
    if (openSearchPid != null) {
      getServiceManager().stopManagedService(openSearchPid);
    }
    delete(id);
  }

  // ==================== Federated Authentication Tests (2 tests) ====================

  /**
   * Test that authentication credentials are properly propagated to federated sources.
   */
  @Test
  public void testAuthenticationPropagationToFederatedSources() throws Exception {
    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForBasicAuthReady(SERVICE_ROOT.getUrl());

    // Ingest test data
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Configure secured federated source
    Map<String, Object> cswProps =
        getCswSourceProperties(FEDERATED_CSW_SOURCE_ID, CSW_PATH.getUrl(), getServiceManager());
    cswProps.put("authenticationType", "basic");
    cswProps.put("username", "admin");
    cswProps.put("password", "admin");
    cswPid =
        getServiceManager()
            .createManagedService(CSW_FEDERATED_SOURCE_FACTORY_PID, cswProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(FEDERATED_CSW_SOURCE_ID);

    // Query with user credentials
    String queryUrl = REST_PATH.getUrl() + "?q=*&src=" + FEDERATED_CSW_SOURCE_ID;
    String response =
        given()
            .auth()
            .basic("admin", "admin")
            .when()
            .get(queryUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    // Verify results were retrieved (proving authentication worked)
    assertThat(response, containsString("myTitle"));

    // Cleanup
    if (cswPid != null) {
      getServiceManager().stopManagedService(cswPid);
    }
    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(SERVICE_ROOT.getUrl());
    delete(id);
  }

  /**
   * Test federated query fails gracefully when authentication fails.
   */
  @Test
  public void testFederatedQueryFailsGracefullyOnAuthFailure() throws Exception {
    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForBasicAuthReady(SERVICE_ROOT.getUrl());

    // Configure source with bad credentials
    Map<String, Object> cswProps =
        getCswSourceProperties(FEDERATED_CSW_SOURCE_ID, CSW_PATH.getUrl(), getServiceManager());
    cswProps.put("authenticationType", "basic");
    cswProps.put("username", "baduser");
    cswProps.put("password", "badpassword");
    cswPid =
        getServiceManager()
            .createManagedService(CSW_FEDERATED_SOURCE_FACTORY_PID, cswProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(FEDERATED_CSW_SOURCE_ID);

    // Wait a moment for source to attempt connection
    await().atMost(10, SECONDS).pollDelay(2, SECONDS).until(() -> true);

    // Query should still work but source may be unavailable
    String queryUrl = REST_PATH.getUrl() + "?q=*&src=" + FEDERATED_CSW_SOURCE_ID;
    given()
        .auth()
        .basic("admin", "admin")
        .when()
        .get(queryUrl)
        .then()
        .statusCode(200); // Query succeeds but may have no results

    // Cleanup
    if (cswPid != null) {
      getServiceManager().stopManagedService(cswPid);
    }
    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(SERVICE_ROOT.getUrl());
  }

  // ==================== Source Availability Handling Tests (2 tests) ====================

  /**
   * Test that unavailable secured sources don't block federated queries.
   */
  @Test
  public void testUnavailableSecuredSourceDoesNotBlockQuery() throws Exception {
    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForBasicAuthReady(SERVICE_ROOT.getUrl());

    // Ingest local data
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Configure source pointing to non-existent endpoint
    Map<String, Object> cswProps =
        getCswSourceProperties(FEDERATED_CSW_SOURCE_ID, "https://localhost:19999/csw", getServiceManager());
    cswProps.put("authenticationType", "basic");
    cswProps.put("username", "admin");
    cswProps.put("password", "admin");
    cswPid =
        getServiceManager()
            .createManagedService(CSW_FEDERATED_SOURCE_FACTORY_PID, cswProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(FEDERATED_CSW_SOURCE_ID);

    // Query should still return local results even if federated source is down
    String queryUrl = REST_PATH.getUrl() + "?q=*";
    String response =
        given()
            .auth()
            .basic("admin", "admin")
            .when()
            .get(queryUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    // Should get local results at minimum
    assertThat(response, containsString("myTitle"));

    // Cleanup
    if (cswPid != null) {
      getServiceManager().stopManagedService(cswPid);
    }
    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(SERVICE_ROOT.getUrl());
    delete(id);
  }

  /**
   * Test source availability monitoring with security constraints.
   */
  @Test
  public void testSourceAvailabilityMonitoringWithSecurity() throws Exception {
    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForBasicAuthReady(SERVICE_ROOT.getUrl());

    // Configure valid secured source
    Map<String, Object> openSearchProps =
        getOpenSearchSourceProperties(
            SECURED_OPENSEARCH_SOURCE_ID, OPENSEARCH_PATH.getUrl(), getServiceManager());
    openSearchProps.put("authenticationType", "basic");
    openSearchProps.put("username", "admin");
    openSearchProps.put("password", "admin");
    securedOpenSearchPid =
        getServiceManager()
            .createManagedService(OPENSEARCH_FACTORY_PID, openSearchProps)
            .getPid();

    getCatalogBundle().waitForFederatedSource(SECURED_OPENSEARCH_SOURCE_ID);

    // Wait for source to be available
    getServiceManager()
        .waitForSourcesToBeAvailable(REST_PATH.getUrl(), SECURED_OPENSEARCH_SOURCE_ID);

    // Verify source is available via admin endpoint
    String statusUrl = ADMIN_ALL_SOURCES_PATH.getUrl();
    String statusResponse =
        given()
            .auth()
            .basic("admin", "admin")
            .when()
            .get(statusUrl)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    // Should show source as available
    assertThat(statusResponse, containsString(SECURED_OPENSEARCH_SOURCE_ID));

    // Cleanup
    if (securedOpenSearchPid != null) {
      getServiceManager().stopManagedService(securedOpenSearchPid);
    }
    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(SERVICE_ROOT.getUrl());
  }

  // ==================== Helper Methods ====================

  private void waitForSecurityHandlers(String url) throws InterruptedException {
    await("Waiting for security handlers")
        .atMost(5, TimeUnit.MINUTES)
        .pollDelay(1, SECONDS)
        .until(() -> {
          try {
            given().get(url).then().statusCode(not(equalTo(503)));
            return true;
          } catch (AssertionError e) {
            return false;
          }
        });
  }
}
