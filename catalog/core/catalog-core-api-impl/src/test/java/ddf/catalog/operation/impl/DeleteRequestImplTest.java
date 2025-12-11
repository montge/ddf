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

import ddf.catalog.operation.DeleteRequest;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link DeleteRequestImpl} class.
 *
 * <p>Tests all constructors, attribute handling, store IDs, and properties.
 */
@ExtendWith(MockitoExtension.class)
public class DeleteRequestImplTest {

  private static final String ID_1 = "id-123";
  private static final String ID_2 = "id-456";
  private static final String ID_3 = "id-789";

  // ====================
  // Constructor Tests - String IDs
  // ====================

  /**
   * Tests constructor with single ID.
   *
   * <p>Verifies basic construction with one ID.
   */
  @Test
  public void testConstructorWithSingleId() {
    DeleteRequestImpl request = new DeleteRequestImpl(ID_1);

    assertThat(request, is(notNullValue()));
    assertThat(request.getAttributeName(), is(equalTo(DeleteRequest.DELETE_BY_ID)));
    assertThat(request.getAttributeValues(), hasSize(1));
    assertThat(request.getAttributeValues().get(0), is(equalTo(ID_1)));
    assertThat(request.getStoreIds(), is(empty()));
  }

  /**
   * Tests constructor with array of IDs.
   *
   * <p>Verifies multiple ID handling.
   */
  @Test
  public void testConstructorWithIdArray() {
    String[] ids = {ID_1, ID_2, ID_3};
    DeleteRequestImpl request = new DeleteRequestImpl(ids);

    assertThat(request.getAttributeName(), is(equalTo(DeleteRequest.DELETE_BY_ID)));
    assertThat(request.getAttributeValues(), hasSize(3));
    assertThat((String) request.getAttributeValues().get(0), is(equalTo(ID_1)));
    assertThat((String) request.getAttributeValues().get(1), is(equalTo(ID_2)));
    assertThat((String) request.getAttributeValues().get(2), is(equalTo(ID_3)));
  }

  /**
   * Tests constructor with IDs and properties.
   *
   * <p>Verifies properties are stored correctly.
   */
  @Test
  public void testConstructorWithIdsAndProperties() {
    String[] ids = {ID_1, ID_2};
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");

    DeleteRequestImpl request = new DeleteRequestImpl(ids, properties);

    assertThat(request.getAttributeValues(), hasSize(2));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("key1"), is(equalTo("value1")));
  }

  /**
   * Tests constructor with empty ID array.
   *
   * <p>Verifies empty array handling.
   */
  @Test
  public void testConstructorWithEmptyIdArray() {
    String[] ids = {};
    DeleteRequestImpl request = new DeleteRequestImpl(ids);

    assertThat(request.getAttributeValues(), is(empty()));
  }

  // ====================
  // Constructor Tests - URIs
  // ====================

  /**
   * Tests constructor with single URI.
   *
   * <p>Verifies URI-based delete.
   */
  @Test
  public void testConstructorWithSingleUri() throws Exception {
    URI uri = new URI("content:abc123");
    DeleteRequestImpl request = new DeleteRequestImpl(uri);

    assertThat(request.getAttributeName(), is(equalTo(DeleteRequest.DELETE_BY_PRODUCT_URI)));
    assertThat(request.getAttributeValues(), hasSize(1));
    assertThat(request.getAttributeValues().get(0), is(equalTo(uri)));
  }

  /**
   * Tests constructor with array of URIs.
   *
   * <p>Verifies multiple URI handling.
   */
  @Test
  public void testConstructorWithUriArray() throws Exception {
    URI uri1 = new URI("content:abc");
    URI uri2 = new URI("content:def");
    URI[] uris = {uri1, uri2};

    DeleteRequestImpl request = new DeleteRequestImpl(uris);

    assertThat(request.getAttributeName(), is(equalTo(DeleteRequest.DELETE_BY_PRODUCT_URI)));
    assertThat(request.getAttributeValues(), hasSize(2));
    assertThat(request.getAttributeValues().get(0), is(equalTo(uri1)));
    assertThat(request.getAttributeValues().get(1), is(equalTo(uri2)));
  }

  /**
   * Tests constructor with URIs and properties.
   *
   * <p>Verifies URI and properties handling.
   */
  @Test
  public void testConstructorWithUrisAndProperties() throws Exception {
    URI uri = new URI("content:test");
    URI[] uris = {uri};
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("source", "remote");

    DeleteRequestImpl request = new DeleteRequestImpl(uris, properties);

    assertThat(request.getAttributeValues(), hasSize(1));
    assertThat(request.getPropertyValue("source"), is(equalTo("remote")));
  }

  // ====================
  // Constructor Tests - Custom Attributes
  // ====================

  /**
   * Tests full constructor with custom attribute name.
   *
   * <p>Verifies custom attribute handling.
   */
  @Test
  public void testFullConstructorWithCustomAttribute() {
    List<Serializable> values = Arrays.asList("val1", "val2");
    String attributeName = "custom.attribute";
    Map<String, Serializable> properties = new HashMap<>();

    DeleteRequestImpl request = new DeleteRequestImpl(values, attributeName, properties);

    assertThat(request.getAttributeName(), is(equalTo(attributeName)));
    assertThat(request.getAttributeValues(), hasSize(2));
    assertThat(request.getAttributeValues().get(0), is(equalTo("val1")));
    assertThat(request.getAttributeValues().get(1), is(equalTo("val2")));
  }

  /**
   * Tests constructor with destinations.
   *
   * <p>Verifies store IDs are properly set.
   */
  @Test
  public void testConstructorWithDestinations() {
    List<Serializable> values = Arrays.asList(ID_1);
    Set<String> destinations = new HashSet<>(Arrays.asList("store1", "store2"));

    DeleteRequestImpl request =
        new DeleteRequestImpl(values, DeleteRequest.DELETE_BY_ID, null, destinations);

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
    List<Serializable> values = Arrays.asList(ID_1);

    DeleteRequestImpl request =
        new DeleteRequestImpl(values, DeleteRequest.DELETE_BY_ID, null, null);

    assertThat(request.getStoreIds(), is(notNullValue()));
    assertThat(request.getStoreIds(), is(empty()));
  }

  // ====================
  // Attribute Tests
  // ====================

  /**
   * Tests getAttributeName for ID-based delete.
   *
   * <p>Verifies DELETE_BY_ID attribute name.
   */
  @Test
  public void testGetAttributeNameForIdDelete() {
    DeleteRequestImpl request = new DeleteRequestImpl(ID_1);

    assertThat(request.getAttributeName(), is(equalTo(DeleteRequest.DELETE_BY_ID)));
  }

  /**
   * Tests getAttributeName for URI-based delete.
   *
   * <p>Verifies DELETE_BY_PRODUCT_URI attribute name.
   */
  @Test
  public void testGetAttributeNameForUriDelete() throws Exception {
    URI uri = new URI("content:test");
    DeleteRequestImpl request = new DeleteRequestImpl(uri);

    assertThat(request.getAttributeName(), is(equalTo(DeleteRequest.DELETE_BY_PRODUCT_URI)));
  }

  /**
   * Tests getAttributeValues returns correct values.
   *
   * <p>Verifies attribute values retrieval.
   */
  @Test
  public void testGetAttributeValues() {
    String[] ids = {ID_1, ID_2, ID_3};
    DeleteRequestImpl request = new DeleteRequestImpl(ids);

    List<Serializable> values = request.getAttributeValues();
    assertThat(values, hasSize(3));
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
    DeleteRequestImpl request = new DeleteRequestImpl(ID_1);

    assertThat(request.getStoreIds(), is(empty()));
  }

  /**
   * Tests getStoreIds with single destination.
   *
   * <p>Verifies single destination retrieval.
   */
  @Test
  public void testGetStoreIdsWithSingleDestination() {
    Set<String> destinations = new HashSet<>(Arrays.asList("catalog"));
    DeleteRequestImpl request =
        new DeleteRequestImpl(Arrays.asList(ID_1), DeleteRequest.DELETE_BY_ID, null, destinations);

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
    Set<String> destinations = new HashSet<>(Arrays.asList("store1", "store2", "store3"));
    DeleteRequestImpl request =
        new DeleteRequestImpl(Arrays.asList(ID_1), DeleteRequest.DELETE_BY_ID, null, destinations);

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
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("cascade", true);
    properties.put("reason", "cleanup");

    DeleteRequestImpl request = new DeleteRequestImpl(new String[] {ID_1}, properties);

    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("cascade"), is(equalTo(true)));
    assertThat(request.getPropertyValue("reason"), is(equalTo("cleanup")));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete delete request by ID.
   *
   * <p>Simulates a real delete by ID scenario.
   */
  @Test
  public void testCompleteDeleteRequestById() {
    String[] ids = {ID_1, ID_2};
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("user", "admin");

    DeleteRequestImpl request = new DeleteRequestImpl(ids, properties);

    assertThat(request.getAttributeName(), is(equalTo(DeleteRequest.DELETE_BY_ID)));
    assertThat(request.getAttributeValues(), hasSize(2));
    assertThat(request.getPropertyValue("user"), is(equalTo("admin")));
  }

  /**
   * Tests complete delete request by URI.
   *
   * <p>Simulates a real delete by URI scenario.
   */
  @Test
  public void testCompleteDeleteRequestByUri() throws Exception {
    URI uri1 = new URI("content:file1");
    URI uri2 = new URI("content:file2");
    URI[] uris = {uri1, uri2};
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("force", false);

    DeleteRequestImpl request = new DeleteRequestImpl(uris, properties);

    assertThat(request.getAttributeName(), is(equalTo(DeleteRequest.DELETE_BY_PRODUCT_URI)));
    assertThat(request.getAttributeValues(), hasSize(2));
    assertThat(request.getPropertyValue("force"), is(equalTo(false)));
  }

  /**
   * Tests federated delete request.
   *
   * <p>Simulates delete across multiple stores.
   */
  @Test
  public void testFederatedDeleteRequest() {
    String[] ids = {ID_1};
    Set<String> destinations = new HashSet<>(Arrays.asList("local", "remote1", "remote2"));

    DeleteRequestImpl request =
        new DeleteRequestImpl(
            Arrays.asList((Serializable[]) ids), DeleteRequest.DELETE_BY_ID, null, destinations);

    assertThat(request.getStoreIds(), hasSize(3));
    assertThat(request.getAttributeValues(), hasSize(1));
  }
}
