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
package ddf.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ddf.catalog.data.Result;
import ddf.catalog.event.EventProcessor;
import org.junit.Test;

/** Test class for {@link Constants} to validate all constant values are properly defined */
public class ConstantsTest {

  /** Validates that metacard and subject property constants are correctly defined */
  @Test
  public void testPropertyConstants() {
    assertNotNull(Constants.METACARD_PROPERTY);
    assertEquals("metacard", Constants.METACARD_PROPERTY);

    assertNotNull(Constants.SUBJECT_PROPERTY);
    assertEquals("subject", Constants.SUBJECT_PROPERTY);
  }

  /** Validates that service-related constants are correctly defined */
  @Test
  public void testServiceConstants() {
    assertNotNull(Constants.SERVICE_SHORTNAME);
    assertEquals("shortname", Constants.SERVICE_SHORTNAME);

    assertNotNull(Constants.SERVICE_ID);
    assertEquals("id", Constants.SERVICE_ID);

    assertNotNull(Constants.SERVICE_TITLE);
    assertEquals("title", Constants.SERVICE_TITLE);

    assertNotNull(Constants.SERVICE_DESCRIPTION);
    assertEquals("description", Constants.SERVICE_DESCRIPTION);
  }

  /** Validates that HTTP-related constants are correctly defined */
  @Test
  public void testHttpConstants() {
    assertNotNull(Constants.HTTP_INVOCATION_ABSOLUTE_PATH_URI);
    assertEquals("http-absolute-path", Constants.HTTP_INVOCATION_ABSOLUTE_PATH_URI);
  }

  /** Validates that federated site state constants are correctly defined */
  @Test
  public void testFederatedSiteStateConstants() {
    assertNotNull(Constants.FEDERATED_SITE_STATE);
    assertEquals("federated-site-state", Constants.FEDERATED_SITE_STATE);

    assertNotNull(Constants.FEDERATED_SITE_STATE_ACTIVE);
    assertEquals("active", Constants.FEDERATED_SITE_STATE_ACTIVE);

    assertNotNull(Constants.FEDERATED_SITE_STATE_INACTIVE);
    assertEquals("inactive", Constants.FEDERATED_SITE_STATE_INACTIVE);
  }

  /** Validates that deprecated event constants delegate to EventProcessor correctly */
  @Test
  public void testDeprecatedEventConstants() {
    assertEquals(EventProcessor.EVENT_METACARD, Constants.EVENTS_ENTRY_NAME);
    assertEquals(EventProcessor.EVENT_TIME, Constants.EVENTS_TIME_NAME);
    assertEquals(EventProcessor.EVENTS_TOPIC_CREATED, Constants.EVENTS_TOPIC_CREATED);
    assertEquals(EventProcessor.EVENTS_TOPIC_UPDATED, Constants.EVENTS_TOPIC_UPDATED);
    assertEquals(EventProcessor.EVENTS_TOPIC_DELETED, Constants.EVENTS_TOPIC_DELETED);
  }

  /** Validates that pagination constants are correctly defined */
  @Test
  public void testPaginationConstants() {
    assertNotNull(Constants.DEFAULT_PAGE_SIZE);
    assertEquals(Integer.valueOf(20), Constants.DEFAULT_PAGE_SIZE);

    assertNotNull(Constants.DEFAULT_START_INDEX);
    assertEquals(Integer.valueOf(0), Constants.DEFAULT_START_INDEX);
  }

  /** Validates that deprecated sort policy constants delegate to Result correctly */
  @Test
  public void testDeprecatedSortPolicyConstants() {
    assertEquals(Result.RELEVANCE, Constants.SORT_POLICY_VALUE_RELEVANCE);
    assertEquals(Result.DISTANCE, Constants.SORT_POLICY_VALUE_DISTANCE);
    assertEquals("TEMPORAL", Constants.SORT_POLICY_VALUE_TEMPORAL);
  }

  /** Validates that subscription constant is correctly defined */
  @Test
  public void testSubscriptionConstant() {
    assertNotNull(Constants.SUBSCRIPTION_KEY);
    assertEquals("subscription", Constants.SUBSCRIPTION_KEY);
  }

  /** Validates that MIME type constants are correctly defined */
  @Test
  public void testMimeTypeConstants() {
    assertNotNull(Constants.MIME_TYPE_JPEG);
    assertEquals("image/jpeg", Constants.MIME_TYPE_JPEG);
  }

  /** Validates that OAuth-related constants are correctly defined */
  @Test
  public void testOAuthConstants() {
    assertNotNull(Constants.OAUTH_RESOURCE_OWNER_USERNAME);
    assertEquals("resource.owner.username", Constants.OAUTH_RESOURCE_OWNER_USERNAME);
  }

  /** Validates that logger name constants are correctly defined */
  @Test
  public void testLoggerNameConstants() {
    assertNotNull(Constants.INGEST_LOGGER_NAME);
    assertEquals("ingestLogger", Constants.INGEST_LOGGER_NAME);

    assertNotNull(Constants.CDM_LOGGER_NAME);
    assertEquals("cdmLogger", Constants.CDM_LOGGER_NAME);
  }

  /** Validates that destination key constants are correctly defined */
  @Test
  public void testDestinationKeyConstants() {
    assertNotNull(Constants.REMOTE_DESTINATION_KEY);
    assertEquals("remote-destination", Constants.REMOTE_DESTINATION_KEY);

    assertNotNull(Constants.LOCAL_DESTINATION_KEY);
    assertEquals("local-destination", Constants.LOCAL_DESTINATION_KEY);
  }

  /** Validates that operation transaction constant is correctly defined */
  @Test
  public void testOperationTransactionConstant() {
    assertNotNull(Constants.OPERATION_TRANSACTION_KEY);
    assertEquals("operation-transaction", Constants.OPERATION_TRANSACTION_KEY);
  }

  /** Validates that content paths constant is correctly defined */
  @Test
  public void testContentPathsConstant() {
    assertNotNull(Constants.CONTENT_PATHS);
    assertEquals("content-paths", Constants.CONTENT_PATHS);
  }

  /** Validates that attribute-related constants are correctly defined */
  @Test
  public void testAttributeConstants() {
    assertNotNull(Constants.ATTRIBUTE_OVERRIDES_KEY);
    assertEquals("attributeOverrides", Constants.ATTRIBUTE_OVERRIDES_KEY);

    assertNotNull(Constants.ATTRIBUTE_UPDATE_MAP_KEY);
    assertEquals("attributeUpdateMap", Constants.ATTRIBUTE_UPDATE_MAP_KEY);
  }

  /** Validates that store reference constant is correctly defined */
  @Test
  public void testStoreReferenceConstant() {
    assertNotNull(Constants.STORE_REFERENCE_KEY);
    assertEquals("storeReference", Constants.STORE_REFERENCE_KEY);
  }

  /** Validates that experimental facet constants are correctly defined */
  @Test
  public void testFacetConstants() {
    assertNotNull(Constants.EXPERIMENTAL_FACET_PROPERTIES_KEY);
    assertEquals("facet-properties", Constants.EXPERIMENTAL_FACET_PROPERTIES_KEY);

    assertNotNull(Constants.EXPERIMENTAL_FACET_RESULTS_KEY);
    assertEquals("facet-results", Constants.EXPERIMENTAL_FACET_RESULTS_KEY);
  }

  /** Validates that query highlight constant is correctly defined */
  @Test
  public void testQueryHighlightConstant() {
    assertNotNull(Constants.QUERY_HIGHLIGHT_KEY);
    assertEquals("highlight", Constants.QUERY_HIGHLIGHT_KEY);
  }

  /** Validates that suggestion-related constants are correctly defined */
  @Test
  public void testSuggestionConstants() {
    assertNotNull(Constants.SUGGESTION_QUERY_KEY);
    assertEquals("suggestion-query", Constants.SUGGESTION_QUERY_KEY);

    assertNotNull(Constants.SUGGESTION_CONTEXT_KEY);
    assertEquals("suggestion-context", Constants.SUGGESTION_CONTEXT_KEY);

    assertNotNull(Constants.SUGGESTION_DICT_KEY);
    assertEquals("suggestion-dictionary", Constants.SUGGESTION_DICT_KEY);

    assertNotNull(Constants.SUGGESTION_RESULT_KEY);
    assertEquals("suggestion-result", Constants.SUGGESTION_RESULT_KEY);

    assertNotNull(Constants.SUGGESTION_BUILD_KEY);
    assertEquals("suggestion-build", Constants.SUGGESTION_BUILD_KEY);
  }

  /** Validates that additional sort bys constant is correctly defined */
  @Test
  public void testAdditionalSortBysConstant() {
    assertNotNull(Constants.ADDITIONAL_SORT_BYS);
    assertEquals("additional-sort-bys", Constants.ADDITIONAL_SORT_BYS);
  }
}
