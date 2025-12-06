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
package ddf.catalog.content.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import ddf.catalog.content.data.ContentItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Comprehensive test suite for {@link UpdateStorageRequestImpl} class.
 *
 * <p>Tests all constructors, getters, ID generation, and edge cases.
 */
@RunWith(MockitoJUnitRunner.class)
public class UpdateStorageRequestImplTest {

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with properties only.
   *
   * <p>Verifies ID is auto-generated and content items list is empty.
   */
  @Test
  public void testConstructorWithPropertiesOnly() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");

    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(properties);

    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getContentItems(), is(notNullValue()));
    assertThat(request.getContentItems(), is(empty()));
    assertThat(request.hasProperties(), is(true));
  }

  /**
   * Tests constructor with null properties.
   *
   * <p>Verifies null properties are handled gracefully.
   */
  @Test
  public void testConstructorWithNullProperties() {
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(null);

    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getContentItems(), is(empty()));
  }

  /**
   * Tests constructor with content items and properties.
   *
   * <p>Verifies content items are stored and ID is auto-generated.
   */
  @Test
  public void testConstructorWithContentItemsAndProperties() {
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item1, item2);
    Map<String, Serializable> properties = new HashMap<>();

    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(items, properties);

    assertThat(request.getContentItems(), hasSize(2));
    assertThat(request.getContentItems().get(0), is(equalTo(item1)));
    assertThat(request.getContentItems().get(1), is(equalTo(item2)));
    assertThat(request.getId(), is(notNullValue()));
  }

  /**
   * Tests constructor with null content items.
   *
   * <p>Verifies null content items result in empty list.
   */
  @Test
  public void testConstructorWithNullContentItems() {
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(null, new HashMap<>());

    assertThat(request.getContentItems(), is(notNullValue()));
    assertThat(request.getContentItems(), is(empty()));
  }

  /**
   * Tests full constructor with content items, ID, and properties.
   *
   * <p>Verifies all parameters are properly initialized.
   */
  @Test
  public void testFullConstructor() {
    ContentItem item = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item);
    String customId = "update-id-789";
    Map<String, Serializable> properties = new HashMap<>();

    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(items, customId, properties);

    assertThat(request.getContentItems(), hasSize(1));
    assertThat(request.getId(), is(equalTo(customId)));
  }

  /**
   * Tests constructor with null ID generates UUID.
   *
   * <p>Verifies UUID generation when ID is null.
   */
  @Test
  public void testConstructorWithNullIdGeneratesUuid() {
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(null, null, null);

    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getId().length(), is(equalTo(36)));
  }

  /**
   * Tests constructor with empty ID generates UUID.
   *
   * <p>Verifies UUID generation when ID is empty.
   */
  @Test
  public void testConstructorWithEmptyIdGeneratesUuid() {
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(null, "", null);

    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getId().length(), is(equalTo(36)));
  }

  /**
   * Tests constructor with whitespace ID is preserved.
   *
   * <p>Verifies whitespace ID is kept (StringUtils.isEmpty doesn't trim).
   */
  @Test
  public void testConstructorWithWhitespaceIdIsPreserved() {
    String whitespaceId = "   ";
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(null, whitespaceId, null);

    assertThat(request.getId(), is(equalTo(whitespaceId)));
  }

  /**
   * Tests constructor with custom ID is preserved.
   *
   * <p>Verifies provided ID is used.
   */
  @Test
  public void testConstructorWithCustomIdIsPreserved() {
    String customId = "my-update-id";
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(null, customId, null);

    assertThat(request.getId(), is(equalTo(customId)));
  }

  // ====================
  // Content Items Tests
  // ====================

  /**
   * Tests getContentItems returns correct list.
   *
   * <p>Verifies content items retrieval.
   */
  @Test
  public void testGetContentItems() {
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    ContentItem item3 = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item1, item2, item3);

    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(items, null);

    assertThat(request.getContentItems(), hasSize(3));
    assertThat(request.getContentItems().get(0), is(equalTo(item1)));
    assertThat(request.getContentItems().get(1), is(equalTo(item2)));
    assertThat(request.getContentItems().get(2), is(equalTo(item3)));
  }

  /**
   * Tests getContentItems with empty list.
   *
   * <p>Verifies empty content items list.
   */
  @Test
  public void testGetContentItemsEmpty() {
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(new ArrayList<>(), null);

    assertThat(request.getContentItems(), is(empty()));
  }

  // ====================
  // ID Tests
  // ====================

  /**
   * Tests getId returns correct ID.
   *
   * <p>Verifies ID retrieval.
   */
  @Test
  public void testGetIdWithCustomId() {
    String customId = "test-update-id";
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(null, customId, null);

    assertThat(request.getId(), is(equalTo(customId)));
  }

  /**
   * Tests getId returns generated UUID.
   *
   * <p>Verifies auto-generated ID has UUID format.
   */
  @Test
  public void testGetIdWithGeneratedUuid() {
    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(null);

    String id = request.getId();
    assertThat(id, is(notNullValue()));
    assertThat(
        id.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"), is(true));
  }

  /**
   * Tests each request gets unique ID.
   *
   * <p>Verifies ID uniqueness across requests.
   */
  @Test
  public void testEachRequestGetsUniqueId() {
    UpdateStorageRequestImpl req1 = new UpdateStorageRequestImpl(null);
    UpdateStorageRequestImpl req2 = new UpdateStorageRequestImpl(null);

    assertThat(req1.getId().equals(req2.getId()), is(false));
  }

  // ====================
  // Properties Tests
  // ====================

  /**
   * Tests properties are properly handled.
   *
   * <p>Verifies property storage and retrieval.
   */
  @Test
  public void testPropertiesHandling() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("overwrite", true);
    properties.put("version", "2.0");

    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(properties);

    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("overwrite"), is(equalTo(true)));
    assertThat(request.getPropertyValue("version"), is(equalTo("2.0")));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete update storage request.
   *
   * <p>Simulates a real update storage scenario.
   */
  @Test
  public void testCompleteUpdateStorageRequest() {
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item1, item2);
    String requestId = "upd-req-001";
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("createVersions", true);

    UpdateStorageRequestImpl request = new UpdateStorageRequestImpl(items, requestId, properties);

    assertThat(request.getId(), is(equalTo(requestId)));
    assertThat(request.getContentItems(), hasSize(2));
    assertThat(request.getPropertyValue("createVersions"), is(equalTo(true)));
  }
}
