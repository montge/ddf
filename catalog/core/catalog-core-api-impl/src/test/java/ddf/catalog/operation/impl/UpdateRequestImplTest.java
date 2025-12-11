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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.UpdateRequest;
import java.io.Serializable;
import java.net.URI;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link UpdateRequestImpl} class.
 *
 * <p>Tests all constructors, update entries, attribute names, and store IDs.
 */
@ExtendWith(MockitoExtension.class)
public class UpdateRequestImplTest {

  private static final String ID_1 = "id-123";
  private static final String ID_2 = "id-456";

  @Mock private Metacard mockMetacard1;
  @Mock private Metacard mockMetacard2;

  private Map<String, Serializable> properties;

  @BeforeEach
  public void setUp() {
    properties = new HashMap<>();
    properties.put("key1", "value1");
  }

  // ====================
  // Constructor Tests - ID-based
  // ====================

  /**
   * Tests constructor with single ID and metacard.
   *
   * <p>Verifies basic construction with one update.
   */
  @Test
  public void testConstructorWithSingleIdAndMetacard() {
    UpdateRequestImpl request = new UpdateRequestImpl(ID_1, mockMetacard1);

    assertThat(request, is(notNullValue()));
    assertThat(request.getAttributeName(), is(equalTo(UpdateRequest.UPDATE_BY_ID)));
    assertThat(request.getUpdates(), hasSize(1));
    assertThat(request.getUpdates().get(0).getKey(), is(equalTo(ID_1)));
    assertThat(request.getUpdates().get(0).getValue(), is(equalTo(mockMetacard1)));
    assertThat(request.getStoreIds(), is(empty()));
  }

  /**
   * Tests constructor with ID array and metacard list.
   *
   * <p>Verifies multiple updates.
   */
  @Test
  public void testConstructorWithIdArrayAndMetacardList() {
    String[] ids = {ID_1, ID_2};
    List<Metacard> metacards = Arrays.asList(mockMetacard1, mockMetacard2);

    UpdateRequestImpl request = new UpdateRequestImpl(ids, metacards);

    assertThat(request.getAttributeName(), is(equalTo(UpdateRequest.UPDATE_BY_ID)));
    assertThat(request.getUpdates(), hasSize(2));
    assertThat(request.getUpdates().get(0).getKey(), is(equalTo(ID_1)));
    assertThat(request.getUpdates().get(0).getValue(), is(equalTo(mockMetacard1)));
    assertThat(request.getUpdates().get(1).getKey(), is(equalTo(ID_2)));
    assertThat(request.getUpdates().get(1).getValue(), is(equalTo(mockMetacard2)));
  }

  /**
   * Tests constructor throws exception when ID and metacard list sizes don't match.
   *
   * <p>Verifies validation of matching list sizes.
   */
  @Test
  public void testConstructorThrowsExceptionWhenSizesMismatch() {
    String[] ids = {ID_1, ID_2};
    List<Metacard> metacards = Arrays.asList(mockMetacard1);

    assertThrows(IllegalArgumentException.class, () -> new UpdateRequestImpl(ids, metacards));
  }

  /**
   * Tests constructor with empty arrays.
   *
   * <p>Verifies empty update handling.
   */
  @Test
  public void testConstructorWithEmptyArrays() {
    String[] ids = {};
    List<Metacard> metacards = new ArrayList<>();

    UpdateRequestImpl request = new UpdateRequestImpl(ids, metacards);

    assertThat(request.getUpdates(), is(empty()));
  }

  // ====================
  // Constructor Tests - URI-based
  // ====================

  /**
   * Tests constructor with URI array and metacards.
   *
   * <p>Verifies URI-based updates.
   */
  @Test
  public void testConstructorWithUriArrayAndMetacards() throws Exception {
    URI uri1 = new URI("content:abc");
    URI uri2 = new URI("content:def");
    URI[] uris = {uri1, uri2};
    List<Metacard> metacards = Arrays.asList(mockMetacard1, mockMetacard2);

    UpdateRequestImpl request = new UpdateRequestImpl(uris, metacards);

    assertThat(request.getAttributeName(), is(equalTo(UpdateRequest.UPDATE_BY_PRODUCT_URI)));
    assertThat(request.getUpdates(), hasSize(2));
    assertThat(request.getUpdates().get(0).getKey(), is(equalTo(uri1)));
    assertThat(request.getUpdates().get(1).getKey(), is(equalTo(uri2)));
  }

  /**
   * Tests URI constructor throws exception when sizes don't match.
   *
   * <p>Verifies URI list size validation.
   */
  @Test
  public void testUriConstructorThrowsExceptionWhenSizesMismatch() throws Exception {
    URI[] uris = {new URI("content:test")};
    List<Metacard> metacards = Arrays.asList(mockMetacard1, mockMetacard2);

    assertThrows(IllegalArgumentException.class, () -> new UpdateRequestImpl(uris, metacards));
  }

  // ====================
  // Constructor Tests - Entry List
  // ====================

  /**
   * Tests constructor with entry list.
   *
   * <p>Verifies direct entry list construction.
   */
  @Test
  public void testConstructorWithEntryList() {
    List<Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new SimpleEntry<>(ID_1, mockMetacard1));
    updates.add(new SimpleEntry<>(ID_2, mockMetacard2));

    UpdateRequestImpl request =
        new UpdateRequestImpl(updates, UpdateRequest.UPDATE_BY_ID, properties);

    assertThat(request.getUpdates(), hasSize(2));
    assertThat(request.getAttributeName(), is(equalTo(UpdateRequest.UPDATE_BY_ID)));
    assertThat(request.hasProperties(), is(true));
  }

  /**
   * Tests constructor with entry list and destinations.
   *
   * <p>Verifies store IDs are properly set.
   */
  @Test
  public void testConstructorWithEntryListAndDestinations() {
    List<Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new SimpleEntry<>(ID_1, mockMetacard1));
    Set<String> destinations = new HashSet<>(Arrays.asList("store1", "store2"));

    UpdateRequestImpl request =
        new UpdateRequestImpl(updates, UpdateRequest.UPDATE_BY_ID, properties, destinations);

    assertThat(request.getStoreIds(), hasSize(2));
    assertThat(request.getStoreIds(), containsInAnyOrder("store1", "store2"));
  }

  /**
   * Tests constructor with null destinations.
   *
   * <p>Verifies null destinations result in empty set.
   */
  @Test
  public void testConstructorWithNullDestinations() {
    List<Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new SimpleEntry<>(ID_1, mockMetacard1));

    UpdateRequestImpl request =
        new UpdateRequestImpl(updates, UpdateRequest.UPDATE_BY_ID, null, null);

    assertThat(request.getStoreIds(), is(notNullValue()));
    assertThat(request.getStoreIds(), is(empty()));
  }

  // ====================
  // Attribute Tests
  // ====================

  /**
   * Tests getAttributeName for ID-based update.
   *
   * <p>Verifies UPDATE_BY_ID attribute name.
   */
  @Test
  public void testGetAttributeNameForIdUpdate() {
    UpdateRequestImpl request = new UpdateRequestImpl(ID_1, mockMetacard1);

    assertThat(request.getAttributeName(), is(equalTo(UpdateRequest.UPDATE_BY_ID)));
  }

  /**
   * Tests getAttributeName for URI-based update.
   *
   * <p>Verifies UPDATE_BY_PRODUCT_URI attribute name.
   */
  @Test
  public void testGetAttributeNameForUriUpdate() throws Exception {
    URI uri = new URI("content:test");
    UpdateRequestImpl request =
        new UpdateRequestImpl(new URI[] {uri}, Arrays.asList(mockMetacard1));

    assertThat(request.getAttributeName(), is(equalTo(UpdateRequest.UPDATE_BY_PRODUCT_URI)));
  }

  /**
   * Tests getAttributeName with custom attribute.
   *
   * <p>Verifies custom attribute names.
   */
  @Test
  public void testGetAttributeNameCustom() {
    List<Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new SimpleEntry<>("custom", mockMetacard1));
    String customAttr = "custom.attribute";

    UpdateRequestImpl request = new UpdateRequestImpl(updates, customAttr, null);

    assertThat(request.getAttributeName(), is(equalTo(customAttr)));
  }

  // ====================
  // Update Tests
  // ====================

  /**
   * Tests getUpdates returns correct entries.
   *
   * <p>Verifies update retrieval.
   */
  @Test
  public void testGetUpdates() {
    String[] ids = {ID_1, ID_2};
    List<Metacard> metacards = Arrays.asList(mockMetacard1, mockMetacard2);

    UpdateRequestImpl request = new UpdateRequestImpl(ids, metacards);

    List<Entry<Serializable, Metacard>> updates = request.getUpdates();
    assertThat(updates, hasSize(2));
    assertThat(updates.get(0).getKey(), is(equalTo(ID_1)));
    assertThat(updates.get(0).getValue(), is(equalTo(mockMetacard1)));
    assertThat(updates.get(1).getKey(), is(equalTo(ID_2)));
    assertThat(updates.get(1).getValue(), is(equalTo(mockMetacard2)));
  }

  /**
   * Tests getUpdates with large list.
   *
   * <p>Verifies handling of many updates.
   */
  @Test
  public void testGetUpdatesWithLargeList() {
    List<String> ids = new ArrayList<>();
    List<Metacard> metacards = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      ids.add("id-" + i);
      metacards.add(mock(Metacard.class));
    }

    UpdateRequestImpl request = new UpdateRequestImpl(ids.toArray(new String[0]), metacards);

    assertThat(request.getUpdates(), hasSize(100));
  }

  // ====================
  // Store IDs Tests
  // ====================

  /**
   * Tests getStoreIds with no destinations.
   *
   * <p>Verifies default empty set.
   */
  @Test
  public void testGetStoreIdsWithNoDestinations() {
    UpdateRequestImpl request = new UpdateRequestImpl(ID_1, mockMetacard1);

    assertThat(request.getStoreIds(), is(empty()));
  }

  /**
   * Tests getStoreIds with single destination.
   *
   * <p>Verifies single destination retrieval.
   */
  @Test
  public void testGetStoreIdsWithSingleDestination() {
    List<Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new SimpleEntry<>(ID_1, mockMetacard1));
    Set<String> destinations = new HashSet<>(Arrays.asList("catalog"));

    UpdateRequestImpl request =
        new UpdateRequestImpl(updates, UpdateRequest.UPDATE_BY_ID, null, destinations);

    assertThat(request.getStoreIds(), hasSize(1));
    assertThat(request.getStoreIds().contains("catalog"), is(true));
  }

  /**
   * Tests getStoreIds with multiple destinations.
   *
   * <p>Verifies multiple destinations retrieval.
   */
  @Test
  public void testGetStoreIdsWithMultipleDestinations() {
    List<Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new SimpleEntry<>(ID_1, mockMetacard1));
    Set<String> destinations = new HashSet<>(Arrays.asList("store1", "store2", "store3"));

    UpdateRequestImpl request =
        new UpdateRequestImpl(updates, UpdateRequest.UPDATE_BY_ID, null, destinations);

    assertThat(request.getStoreIds(), hasSize(3));
    assertThat(request.getStoreIds(), containsInAnyOrder("store1", "store2", "store3"));
  }

  // ====================
  // Properties Tests
  // ====================

  /**
   * Tests properties handling.
   *
   * <p>Verifies property storage and retrieval.
   */
  @Test
  public void testPropertiesHandling() {
    List<Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new SimpleEntry<>(ID_1, mockMetacard1));
    Map<String, Serializable> props = new HashMap<>();
    props.put("skipIndexing", true);

    UpdateRequestImpl request = new UpdateRequestImpl(updates, UpdateRequest.UPDATE_BY_ID, props);

    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("skipIndexing"), is(equalTo(true)));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete update request by ID.
   *
   * <p>Simulates a real update scenario.
   */
  @Test
  public void testCompleteUpdateRequestById() {
    String[] ids = {ID_1, ID_2};
    List<Metacard> metacards = Arrays.asList(mockMetacard1, mockMetacard2);

    UpdateRequestImpl request = new UpdateRequestImpl(ids, metacards);

    assertThat(request.getAttributeName(), is(equalTo(UpdateRequest.UPDATE_BY_ID)));
    assertThat(request.getUpdates(), hasSize(2));
    assertThat(request.getStoreIds(), is(empty()));
  }

  /**
   * Tests federated update request.
   *
   * <p>Simulates update across multiple stores.
   */
  @Test
  public void testFederatedUpdateRequest() {
    List<Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new SimpleEntry<>(ID_1, mockMetacard1));
    Set<String> destinations = new HashSet<>(Arrays.asList("local", "remote"));
    Map<String, Serializable> props = new HashMap<>();
    props.put("user", "admin");

    UpdateRequestImpl request =
        new UpdateRequestImpl(updates, UpdateRequest.UPDATE_BY_ID, props, destinations);

    assertThat(request.getUpdates(), hasSize(1));
    assertThat(request.getStoreIds(), hasSize(2));
    assertThat(request.getPropertyValue("user"), is(equalTo("admin")));
  }
}
