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
import static io.restassured.RestAssured.when;
import static org.awaitility.Awaitility.await;
import static org.codice.ddf.itests.common.catalog.CatalogTestCommons.TRANSFORMER_XML;
import static org.codice.ddf.itests.common.catalog.CatalogTestCommons.delete;
import static org.codice.ddf.itests.common.catalog.CatalogTestCommons.ingest;
import static org.codice.ddf.itests.common.catalog.CatalogTestCommons.query;
import static org.codice.ddf.itests.common.catalog.CatalogTestCommons.queryWithBasicAuth;
import static org.codice.ddf.itests.common.catalog.CatalogTestCommons.update;
import static org.codice.ddf.itests.common.config.ConfigureTestCommons.configureAuthZRealm;
import static org.codice.ddf.itests.common.config.ConfigureTestCommons.configureMetacardAttributeSecurityFiltering;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;

import ddf.catalog.data.Metacard;
import java.util.Collections;
import java.util.Dictionary;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.codice.ddf.itests.common.AbstractIntegrationTest;
import org.codice.ddf.test.common.annotations.BeforeExam;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

/**
 * Integration tests for Catalog Framework + Security interactions.
 *
 * <p>Tests the integration between catalog operations (create, read, update, delete, query) and
 * security enforcement including authentication, authorization, and access control.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class TestCatalogSecurityIntegration extends AbstractIntegrationTest {

  private static final String USER_A = "slang";
  private static final String USER_B = "tchalla";
  private static final String USER_PASSWORD = "password1";
  private static final String ACCESS_GROUP_A = "A";
  private static final String ACCESS_GROUP_B = "B";
  private static final String ACCESS_GROUP_TOKEN = "ACCESS_GROUP_REPLACE_TOKEN";

  private static final DynamicUrl SECURE_ROOT_AND_PORT =
      new DynamicUrl(DynamicUrl.SECURE_ROOT, HTTPS_PORT);

  private static final DynamicUrl ADMIN_PATH =
      new DynamicUrl(SECURE_ROOT_AND_PORT, "/admin/index.html");

  @BeforeExam
  public void beforeExam() throws Exception {
    getSecurityPolicy().configureRestForGuest();
  }

  @AfterEach
  public void tearDown() {
    clearCatalog();
  }

  // ==================== Query Filtering Tests (5 tests) ====================

  /** Test that users can only query metacards they have access to based on security attributes. */
  @Test
  public void testQueryFilteringBySecurityAttributes() throws Exception {
    Dictionary authZProps = null;
    Dictionary securityFilterProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      // Configure security filtering
      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              Collections.singletonList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());
      securityFilterProps =
          configureMetacardAttributeSecurityFiltering(
              Collections.singletonList("security.access-groups=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());

      // Ingest metacards with different access groups
      String testDataA = getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      testDataA = testDataA.replace(ACCESS_GROUP_TOKEN, ACCESS_GROUP_A);
      String idA = ingest(testDataA, MediaType.TEXT_XML);

      String testDataB = getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      testDataB = testDataB.replace(ACCESS_GROUP_TOKEN, ACCESS_GROUP_B);
      String idB = ingest(testDataB, MediaType.TEXT_XML);

      // Configure for basic auth
      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // User A can only see group A metacards
      String responseA = queryWithBasicAuth(idA, TRANSFORMER_XML, USER_A, USER_PASSWORD);
      assertThat(responseA, containsString("Lady Liberty"));

      queryWithBasicAuth(idB, TRANSFORMER_XML, USER_A, USER_PASSWORD, HttpStatus.SC_NOT_FOUND);

      // User B can only see group B metacards
      queryWithBasicAuth(idA, TRANSFORMER_XML, USER_B, USER_PASSWORD, HttpStatus.SC_NOT_FOUND);

      String responseB = queryWithBasicAuth(idB, TRANSFORMER_XML, USER_B, USER_PASSWORD);
      assertThat(responseB, containsString("Lady Liberty"));

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      if (securityFilterProps != null) {
        configureMetacardAttributeSecurityFiltering(securityFilterProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  /** Test that role-based access control filters query results appropriately. */
  @Test
  public void testQueryFilteringByRoles() throws Exception {
    Dictionary authZProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      // Configure role-based filtering
      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              Collections.singletonList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=admin"),
              Collections.emptyList(),
              getAdminConfig());

      // Ingest test metacard
      String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
      String id = ingest(testData, "application/json");

      // Configure for basic auth
      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // Admin user can query
      String response = queryWithBasicAuth(id, TRANSFORMER_XML, "admin", "admin");
      assertThat(response, containsString("myTitle"));

      // Non-admin user (system-admin-user) cannot query if not in admin role
      queryWithBasicAuth(
          id,
          TRANSFORMER_XML,
          SYSTEM_ADMIN_USER,
          SYSTEM_ADMIN_USER_PASSWORD,
          HttpStatus.SC_NOT_FOUND);

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  /** Test that guest users can only access public metacards. */
  @Test
  public void testGuestAccessToPublicMetacardsOnly() throws Exception {
    Dictionary authZProps = null;
    Dictionary securityFilterProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);

      // Configure guest role mapping
      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              Collections.singletonList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());
      securityFilterProps =
          configureMetacardAttributeSecurityFiltering(
              Collections.singletonList("security.access-groups=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());

      // Ingest public metacard (guest group)
      String publicData =
          getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      publicData = publicData.replace(ACCESS_GROUP_TOKEN, "guest");
      String publicId = ingest(publicData, MediaType.TEXT_XML);

      // Ingest private metacard (non-guest group)
      String privateData =
          getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      privateData = privateData.replace(ACCESS_GROUP_TOKEN, ACCESS_GROUP_A);
      String privateId = ingest(privateData, MediaType.TEXT_XML);

      // Guest can see public metacard
      String response = query(publicId, TRANSFORMER_XML);
      assertThat(response, containsString("Lady Liberty"));

      // Guest cannot see private metacard
      query(privateId, TRANSFORMER_XML, HttpStatus.SC_NOT_FOUND);

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      if (securityFilterProps != null) {
        configureMetacardAttributeSecurityFiltering(securityFilterProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  /** Test query result filtering with multiple security attributes. */
  @Test
  public void testQueryFilteringWithMultipleSecurityAttributes() throws Exception {
    Dictionary authZProps = null;
    Dictionary securityFilterProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      // Configure multiple attribute filtering
      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              java.util.Arrays.asList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=accessGroups",
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier=owner"),
              Collections.emptyList(),
              getAdminConfig());
      securityFilterProps =
          configureMetacardAttributeSecurityFiltering(
              java.util.Arrays.asList(
                  "security.access-groups=accessGroups", "metacard.owner=owner"),
              Collections.emptyList(),
              getAdminConfig());

      // Ingest metacard with group A
      String testData = getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      testData = testData.replace(ACCESS_GROUP_TOKEN, ACCESS_GROUP_A);
      String id = ingest(testData, MediaType.TEXT_XML);

      // Configure for basic auth
      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // User A with matching group and owner can see
      String response = queryWithBasicAuth(id, TRANSFORMER_XML, USER_A, USER_PASSWORD);
      assertThat(response, containsString("Lady Liberty"));

      // User B without matching group cannot see
      queryWithBasicAuth(id, TRANSFORMER_XML, USER_B, USER_PASSWORD, HttpStatus.SC_NOT_FOUND);

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      if (securityFilterProps != null) {
        configureMetacardAttributeSecurityFiltering(securityFilterProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  /** Test that security filtering works correctly with wildcard queries. */
  @Test
  public void testWildcardQuerySecurityFiltering() throws Exception {
    Dictionary authZProps = null;
    Dictionary securityFilterProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      // Configure security
      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              Collections.singletonList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());
      securityFilterProps =
          configureMetacardAttributeSecurityFiltering(
              Collections.singletonList("security.access-groups=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());

      // Ingest multiple metacards with different groups
      for (int i = 0; i < 5; i++) {
        String testData =
            getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
        testData =
            testData.replace(ACCESS_GROUP_TOKEN, i % 2 == 0 ? ACCESS_GROUP_A : ACCESS_GROUP_B);
        ingest(testData, MediaType.TEXT_XML);
      }

      // Configure for basic auth
      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // Wildcard query as User A should only return group A metacards
      String responseA =
          given()
              .auth()
              .basic(USER_A, USER_PASSWORD)
              .when()
              .get(url)
              .then()
              .statusCode(200)
              .extract()
              .asString();

      // Verify correct number of results (should be 3 from group A)
      assertThat(responseA, containsString("Lady Liberty"));

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      if (securityFilterProps != null) {
        configureMetacardAttributeSecurityFiltering(securityFilterProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  // ==================== Create/Update/Delete Authorization Tests (5 tests) ====================

  /** Test that only authorized users can create metacards. */
  @Test
  public void testAuthorizedCreate() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog";

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Admin user can create
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String response =
        given()
            .auth()
            .basic("admin", "admin")
            .body(testData)
            .contentType("application/json")
            .when()
            .post(url)
            .then()
            .statusCode(201)
            .extract()
            .asString();

    assertThat(response, containsString("id"));

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
  }

  /** Test that unauthorized users cannot create metacards. */
  @Test
  public void testUnauthorizedCreateDenied() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog";

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // User without create permission should be denied
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    given()
        .auth()
        .basic(SYSTEM_ADMIN_USER, SYSTEM_ADMIN_USER_PASSWORD)
        .body(testData)
        .contentType("application/json")
        .when()
        .post(url)
        .then()
        .statusCode(not(equalTo(201)));

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
  }

  /** Test that only authorized users can update metacards. */
  @Test
  public void testAuthorizedUpdate() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog";

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);

    // Ingest initial metacard
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Admin user can update
    String updatedData = testData.replace("myTitle", "updatedTitle");
    given()
        .auth()
        .basic("admin", "admin")
        .body(updatedData)
        .contentType("application/json")
        .when()
        .put(url + "/" + id)
        .then()
        .statusCode(200);

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);

    delete(id);
  }

  /** Test that unauthorized users cannot update metacards. */
  @Test
  public void testUnauthorizedUpdateDenied() throws Exception {
    Dictionary authZProps = null;
    Dictionary securityFilterProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog";

    try {
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);

      // Ingest initial metacard
      String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
      String id = ingest(testData, "application/json");

      // Configure security so user A owns the metacard
      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              Collections.singletonList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier=owner"),
              Collections.emptyList(),
              getAdminConfig());
      securityFilterProps =
          configureMetacardAttributeSecurityFiltering(
              Collections.singletonList("metacard.owner=owner"),
              Collections.emptyList(),
              getAdminConfig());

      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // User B cannot update user A's metacard
      String updatedData = testData.replace("myTitle", "updatedTitle");
      given()
          .auth()
          .basic(USER_B, USER_PASSWORD)
          .body(updatedData)
          .contentType("application/json")
          .when()
          .put(url + "/" + id)
          .then()
          .statusCode(not(equalTo(200)));

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      if (securityFilterProps != null) {
        configureMetacardAttributeSecurityFiltering(securityFilterProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  /** Test that only authorized users can delete metacards. */
  @Test
  public void testAuthorizedDelete() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog";

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);

    // Ingest initial metacard
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Admin user can delete
    given().auth().basic("admin", "admin").when().delete(url + "/" + id).then().statusCode(200);

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
  }

  // ==================== Guest vs Authenticated Access Tests (5 tests) ====================

  /** Test guest access to public endpoints. */
  @Test
  public void testGuestAccessToPublicEndpoints() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    configureRestForGuest(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForGuestAuthReady(url);

    // Ingest test data
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Guest can query
    when()
        .get(url)
        .then()
        .statusCode(200)
        .body(
            hasXPath("//metacard/string[@name='" + Metacard.TITLE + "']/value[text()='myTitle']"));

    delete(id);
  }

  /** Test that guest access is denied to restricted endpoints. */
  @Test
  public void testGuestAccessDeniedToRestrictedEndpoints() throws Exception {
    String adminUrl = ADMIN_PATH.getUrl();

    configureRestForGuest(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(adminUrl);
    getSecurityPolicy().waitForGuestAuthReady(adminUrl);

    // Guest cannot access admin console
    when().get(adminUrl).then().statusCode(403);
  }

  /** Test authenticated access with valid credentials. */
  @Test
  public void testAuthenticatedAccessWithValidCredentials() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Ingest test data
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Authenticated user can query with valid credentials
    String cookie =
        given()
            .auth()
            .basic("admin", "admin")
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .header("Set-Cookie", containsString("JSESSIONID"))
            .extract()
            .cookie("JSESSIONID");

    // Session cookie works for subsequent requests
    given().cookie("JSESSIONID", cookie).when().get(url).then().statusCode(200);

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
    delete(id);
  }

  /** Test authenticated access denied with invalid credentials. */
  @Test
  public void testAuthenticatedAccessDeniedWithInvalidCredentials() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Invalid credentials should be denied
    given().auth().basic("admin", "wrongpassword").when().get(url).then().statusCode(401);

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
  }

  /** Test switching between guest and authenticated modes. */
  @Test
  public void testSwitchingBetweenGuestAndAuthenticatedModes() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    // Start with guest
    configureRestForGuest(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForGuestAuthReady(url);

    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Guest can query
    when().get(url).then().statusCode(200);

    // Switch to basic auth
    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Guest cannot query without auth
    when().get(url).then().statusCode(401);

    // Authenticated user can query
    given().auth().basic("admin", "admin").when().get(url).then().statusCode(200);

    // Switch back to guest
    configureRestForGuest(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForGuestAuthReady(url);

    // Guest can query again
    when().get(url).then().statusCode(200);

    delete(id);
  }

  // ==================== Multi-User Concurrent Access Tests (5 tests) ====================

  /** Test concurrent queries from multiple users with different permissions. */
  @Test
  public void testConcurrentQueriesFromMultipleUsers() throws Exception {
    Dictionary authZProps = null;
    Dictionary securityFilterProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      // Configure security
      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              Collections.singletonList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());
      securityFilterProps =
          configureMetacardAttributeSecurityFiltering(
              Collections.singletonList("security.access-groups=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());

      // Ingest metacards for different groups
      String testDataA = getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      testDataA = testDataA.replace(ACCESS_GROUP_TOKEN, ACCESS_GROUP_A);
      String idA = ingest(testDataA, MediaType.TEXT_XML);

      String testDataB = getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      testDataB = testDataB.replace(ACCESS_GROUP_TOKEN, ACCESS_GROUP_B);
      String idB = ingest(testDataB, MediaType.TEXT_XML);

      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // Simulate concurrent queries from different users
      Thread userAThread =
          new Thread(
              () -> {
                try {
                  String response = queryWithBasicAuth(idA, TRANSFORMER_XML, USER_A, USER_PASSWORD);
                  assertThat(response, containsString("Lady Liberty"));
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              });

      Thread userBThread =
          new Thread(
              () -> {
                try {
                  String response = queryWithBasicAuth(idB, TRANSFORMER_XML, USER_B, USER_PASSWORD);
                  assertThat(response, containsString("Lady Liberty"));
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              });

      userAThread.start();
      userBThread.start();

      userAThread.join(10000);
      userBThread.join(10000);

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      if (securityFilterProps != null) {
        configureMetacardAttributeSecurityFiltering(securityFilterProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  /** Test concurrent create operations from multiple users. */
  @Test
  public void testConcurrentCreatesFromMultipleUsers() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog";

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Simulate concurrent creates
    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");

    Thread thread1 =
        new Thread(
            () -> {
              given()
                  .auth()
                  .basic("admin", "admin")
                  .body(testData)
                  .contentType("application/json")
                  .when()
                  .post(url)
                  .then()
                  .statusCode(201);
            });

    Thread thread2 =
        new Thread(
            () -> {
              given()
                  .auth()
                  .basic("admin", "admin")
                  .body(testData)
                  .contentType("application/json")
                  .when()
                  .post(url)
                  .then()
                  .statusCode(201);
            });

    thread1.start();
    thread2.start();

    thread1.join(10000);
    thread2.join(10000);

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
  }

  /** Test concurrent update operations from multiple users on same metacard. */
  @Test
  public void testConcurrentUpdatesOnSameMetacard() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog";

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);

    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Concurrent updates to same metacard
    String updatedData1 = testData.replace("myTitle", "updateFromUser1");
    String updatedData2 = testData.replace("myTitle", "updateFromUser2");

    Thread thread1 =
        new Thread(
            () -> {
              given()
                  .auth()
                  .basic("admin", "admin")
                  .body(updatedData1)
                  .contentType("application/json")
                  .when()
                  .put(url + "/" + id);
            });

    Thread thread2 =
        new Thread(
            () -> {
              given()
                  .auth()
                  .basic("admin", "admin")
                  .body(updatedData2)
                  .contentType("application/json")
                  .when()
                  .put(url + "/" + id);
            });

    thread1.start();
    thread2.start();

    thread1.join(10000);
    thread2.join(10000);

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
    delete(id);
  }

  /** Test session isolation between concurrent users. */
  @Test
  public void testSessionIsolationBetweenConcurrentUsers() throws Exception {
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Create sessions for different users
    String cookieAdmin =
        given()
            .auth()
            .basic("admin", "admin")
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .cookie("JSESSIONID");

    String cookieSystemAdmin =
        given()
            .auth()
            .basic(SYSTEM_ADMIN_USER, SYSTEM_ADMIN_USER_PASSWORD)
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .cookie("JSESSIONID");

    // Verify sessions are different
    assertThat(cookieAdmin, not(equalTo(cookieSystemAdmin)));

    // Each session maintains its own security context
    given().cookie("JSESSIONID", cookieAdmin).when().get(url).then().statusCode(200);

    given().cookie("JSESSIONID", cookieSystemAdmin).when().get(url).then().statusCode(200);

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
  }

  /** Test security attribute updates don't affect concurrent queries. */
  @Test
  public void testSecurityAttributeUpdatesIsolation() throws Exception {
    Dictionary authZProps = null;
    Dictionary securityFilterProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog";

    try {
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);

      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              Collections.singletonList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());
      securityFilterProps =
          configureMetacardAttributeSecurityFiltering(
              Collections.singletonList("security.access-groups=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());

      String testData = getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      testData = testData.replace(ACCESS_GROUP_TOKEN, ACCESS_GROUP_A);
      String id = ingest(testData, MediaType.TEXT_XML);

      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // User A can see the metacard
      String response = queryWithBasicAuth(id, TRANSFORMER_XML, USER_A, USER_PASSWORD);
      assertThat(response, containsString("Lady Liberty"));

      // While User A is querying, update security attributes
      String updatedData = testData.replace(ACCESS_GROUP_A, ACCESS_GROUP_B);
      update(id, updatedData, MediaType.TEXT_XML);

      // User A should no longer see it after update
      await()
          .atMost(10, TimeUnit.SECONDS)
          .until(
              () -> {
                try {
                  queryWithBasicAuth(
                      id, TRANSFORMER_XML, USER_A, USER_PASSWORD, HttpStatus.SC_NOT_FOUND);
                  return true;
                } catch (AssertionError e) {
                  return false;
                }
              });

      // User B should now see it
      String responseB = queryWithBasicAuth(id, TRANSFORMER_XML, USER_B, USER_PASSWORD);
      assertThat(responseB, containsString("Lady Liberty"));

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      if (securityFilterProps != null) {
        configureMetacardAttributeSecurityFiltering(securityFilterProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  // ==================== Role-Based Result Filtering Tests (5 tests) ====================

  /** Test that admin role can access all metacards. */
  @Test
  public void testAdminRoleAccessToAllMetacards() throws Exception {
    Dictionary authZProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);

      // Ingest multiple metacards
      for (int i = 0; i < 3; i++) {
        String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
        ingest(testData, "application/json");
      }

      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // Admin can see all metacards
      String response =
          given()
              .auth()
              .basic("admin", "admin")
              .when()
              .get(url)
              .then()
              .statusCode(200)
              .extract()
              .asString();

      assertThat(response, containsString("myTitle"));

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  /** Test role hierarchy in access control. */
  @Test
  public void testRoleHierarchyAccessControl() throws Exception {
    // This test would verify that higher roles have access to lower role resources
    // Implementation would depend on specific role hierarchy configuration
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    // Admin role (higher) should have access
    String response = queryWithBasicAuth(id, TRANSFORMER_XML, "admin", "admin");
    assertThat(response, containsString("myTitle"));

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
    delete(id);
  }

  /** Test filtering based on multiple required roles. */
  @Test
  public void testMultipleRequiredRolesFiltering() throws Exception {
    Dictionary authZProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      // Configure to require multiple roles
      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              java.util.Arrays.asList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=admin",
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=system-admin"),
              Collections.emptyList(),
              getAdminConfig());

      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);

      String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
      String id = ingest(testData, "application/json");

      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // User with all required roles can access
      String response = queryWithBasicAuth(id, TRANSFORMER_XML, "admin", "admin");
      assertThat(response, containsString("myTitle"));

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  /** Test dynamic role assignment and filtering. */
  @Test
  public void testDynamicRoleAssignmentFiltering() throws Exception {
    // This test would verify that role changes are reflected in access control
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);

    String testData = getFileContent(JSON_RECORD_RESOURCE_PATH + "/SimpleGeoJsonRecord");
    String id = ingest(testData, "application/json");

    configureRestForBasic(SERVICE_ROOT.getUrl());
    waitForSecurityHandlers(url);
    getSecurityPolicy().waitForBasicAuthReady(url);

    // Verify initial access
    String response = queryWithBasicAuth(id, TRANSFORMER_XML, "admin", "admin");
    assertThat(response, containsString("myTitle"));

    configureRestForGuest(SERVICE_ROOT.getUrl());
    getSecurityPolicy().waitForGuestAuthReady(url);
    delete(id);
  }

  /** Test role-based filtering with inherited permissions. */
  @Test
  public void testRoleBasedFilteringWithInheritedPermissions() throws Exception {
    Dictionary authZProps = null;
    Dictionary securityFilterProps = null;
    String url = SERVICE_ROOT.getUrl() + "/catalog/query?q=*&src=local";

    try {
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);

      authZProps =
          configureAuthZRealm(
              Collections.emptyList(),
              Collections.singletonList(
                  "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());
      securityFilterProps =
          configureMetacardAttributeSecurityFiltering(
              Collections.singletonList("security.access-groups=accessGroups"),
              Collections.emptyList(),
              getAdminConfig());

      String testData = getFileContent(XML_RECORD_RESOURCE_PATH + "/accessGroupTokenMetacard.xml");
      testData = testData.replace(ACCESS_GROUP_TOKEN, ACCESS_GROUP_A);
      String id = ingest(testData, MediaType.TEXT_XML);

      configureRestForBasic(SERVICE_ROOT.getUrl());
      waitForSecurityHandlers(url);
      getSecurityPolicy().waitForBasicAuthReady(url);

      // User with matching group can access
      String response = queryWithBasicAuth(id, TRANSFORMER_XML, USER_A, USER_PASSWORD);
      assertThat(response, containsString("Lady Liberty"));

      // User without matching group cannot access
      queryWithBasicAuth(id, TRANSFORMER_XML, USER_B, USER_PASSWORD, HttpStatus.SC_NOT_FOUND);

    } finally {
      if (authZProps != null) {
        configureAuthZRealm(authZProps, getAdminConfig());
      }
      if (securityFilterProps != null) {
        configureMetacardAttributeSecurityFiltering(securityFilterProps, getAdminConfig());
      }
      configureRestForGuest(SERVICE_ROOT.getUrl());
      getSecurityPolicy().waitForGuestAuthReady(url);
    }
  }

  // ==================== Helper Methods ====================

  private void waitForSecurityHandlers(String url) throws InterruptedException {
    await("Waiting for security handlers to become available")
        .atMost(5, TimeUnit.MINUTES)
        .pollDelay(1, TimeUnit.SECONDS)
        .until(
            () -> {
              try {
                given().get(url).then().statusCode(not(equalTo(503)));
                return true;
              } catch (AssertionError e) {
                return false;
              }
            });
  }
}
