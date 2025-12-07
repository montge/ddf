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
package ddf.catalog.pubsub.predicate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ddf.catalog.pubsub.internal.PubSubConstants;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.osgi.service.event.Event;

public class EntryPredicateTest {

  private static final String TEST_CATALOG_ID = "test-catalog-id-123";
  private static final String DIFFERENT_CATALOG_ID = "different-catalog-id-456";

  @Test
  public void testDefaultConstructor() {
    EntryPredicate predicate = new EntryPredicate();

    assertThat(predicate.getCatalogId(), is(nullValue()));
    assertThat(predicate.getDad(), is(nullValue()));
  }

  @Test
  public void testConstructorWithCatalogId() {
    EntryPredicate predicate = new EntryPredicate(TEST_CATALOG_ID);

    assertThat(predicate.getCatalogId(), is(equalTo(TEST_CATALOG_ID)));
    assertThat(predicate.getDad(), is(nullValue()));
  }

  @Test
  public void testConstructorWithDad() throws Exception {
    URI testUri = new URI("http://example.com/resource");
    EntryPredicate predicate = new EntryPredicate(testUri);

    assertThat(predicate.getCatalogId(), is(nullValue()));
    assertThat(predicate.getDad(), is(equalTo(testUri)));
  }

  @Test
  public void testMatchesWithMatchingCatalogId() {
    EntryPredicate predicate = new EntryPredicate(TEST_CATALOG_ID);

    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_ID_KEY, TEST_CATALOG_ID);
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.CREATE);

    Event event = new Event("topic", properties);
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesWithNonMatchingCatalogId() {
    EntryPredicate predicate = new EntryPredicate(TEST_CATALOG_ID);

    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_ID_KEY, DIFFERENT_CATALOG_ID);
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.CREATE);

    Event event = new Event("topic", properties);
    assertFalse(predicate.matches(event));
  }

  @Test
  public void testMatchesWithDeleteOperation() {
    EntryPredicate predicate = new EntryPredicate(TEST_CATALOG_ID);

    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_ID_KEY, TEST_CATALOG_ID);
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.DELETE);

    Map<String, Object> contextualMap = new HashMap<>();
    contextualMap.put("METADATA", PubSubConstants.METADATA_DELETED);
    properties.put(PubSubConstants.HEADER_CONTEXTUAL_KEY, contextualMap);

    Event event = new Event("topic", properties);
    // Should return true when DELETE operation with METADATA_DELETED
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesWithMatchingDad() throws Exception {
    URI testUri = new URI("http://example.com/resource");
    EntryPredicate predicate = new EntryPredicate(testUri);

    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_DAD_KEY, "http://example.com/resource");
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.CREATE);

    Event event = new Event("topic", properties);
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesWithNonMatchingDad() throws Exception {
    URI testUri = new URI("http://example.com/resource");
    EntryPredicate predicate = new EntryPredicate(testUri);

    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_DAD_KEY, "http://example.com/different-resource");
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.CREATE);

    Event event = new Event("topic", properties);
    assertFalse(predicate.matches(event));
  }

  @Test
  public void testMatchesWithInvalidDadUri() throws Exception {
    URI testUri = new URI("http://example.com/resource");
    EntryPredicate predicate = new EntryPredicate(testUri);

    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_DAD_KEY, "not a valid uri ://");
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.CREATE);

    Event event = new Event("topic", properties);
    assertFalse(predicate.matches(event));
  }

  @Test
  public void testMatchesWithNullCatalogIdAndNullDad() {
    EntryPredicate predicate = new EntryPredicate();

    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_ID_KEY, TEST_CATALOG_ID);
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.CREATE);

    Event event = new Event("topic", properties);
    assertFalse(predicate.matches(event));
  }

  @Test
  public void testSetDad() throws Exception {
    EntryPredicate predicate = new EntryPredicate();
    assertThat(predicate.getDad(), is(nullValue()));

    URI testUri = new URI("http://example.com/resource");
    predicate.setDad(testUri);

    assertThat(predicate.getDad(), is(equalTo(testUri)));
  }

  @Test
  public void testToStringWithCatalogId() {
    EntryPredicate predicate = new EntryPredicate(TEST_CATALOG_ID);

    String result = predicate.toString();

    assertThat(result, is(notNullValue()));
    assertTrue(result.contains("catalogId"));
    assertTrue(result.contains(TEST_CATALOG_ID));
    assertTrue(result.contains("dad"));
  }

  @Test
  public void testToStringWithDad() throws Exception {
    URI testUri = new URI("http://example.com/resource");
    EntryPredicate predicate = new EntryPredicate(testUri);

    String result = predicate.toString();

    assertThat(result, is(notNullValue()));
    assertTrue(result.contains("catalogId"));
    assertTrue(result.contains("dad"));
    assertTrue(result.contains(testUri.toString()));
  }

  @Test
  public void testToStringWithNullValues() {
    EntryPredicate predicate = new EntryPredicate();

    String result = predicate.toString();

    assertThat(result, is(notNullValue()));
    assertTrue(result.contains("catalogId"));
    assertTrue(result.contains("dad"));
  }
}
