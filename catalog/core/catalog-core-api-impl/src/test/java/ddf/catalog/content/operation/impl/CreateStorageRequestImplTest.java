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
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import ddf.catalog.content.data.ContentItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link CreateStorageRequestImpl} class.
 *
 * <p>Tests all constructors, getters, ID generation, and properties handling.
 */
@ExtendWith(MockitoExtension.class)
public class CreateStorageRequestImplTest {

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with properties only.
   *
   * <p>Verifies that ID is auto-generated and content items list is empty.
   */
  @Test
  public void testConstructorWithPropertiesOnly() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");

    CreateStorageRequestImpl request = new CreateStorageRequestImpl(properties);

    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getContentItems(), is(notNullValue()));
    assertThat(request.getContentItems(), is(empty()));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("key1"), is(equalTo("value1")));
  }

  /**
   * Tests constructor with null properties.
   *
   * <p>Verifies that null properties are handled gracefully.
   */
  @Test
  public void testConstructorWithNullProperties() {
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null);

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
    properties.put("timeout", 5000);

    CreateStorageRequestImpl request = new CreateStorageRequestImpl(items, properties);

    assertThat(request.getContentItems(), hasSize(2));
    assertThat(request.getContentItems().get(0), is(equalTo(item1)));
    assertThat(request.getContentItems().get(1), is(equalTo(item2)));
    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getPropertyValue("timeout"), is(equalTo(5000)));
  }

  /**
   * Tests constructor with null content items.
   *
   * <p>Verifies that null content items results in empty list.
   */
  @Test
  public void testConstructorWithNullContentItems() {
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null, new HashMap<>());

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
    String customId = "custom-id-12345";
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("source", "remote");

    CreateStorageRequestImpl request = new CreateStorageRequestImpl(items, customId, properties);

    assertThat(request.getContentItems(), hasSize(1));
    assertThat(request.getContentItems().get(0), is(equalTo(item)));
    assertThat(request.getId(), is(equalTo(customId)));
    assertThat(request.getPropertyValue("source"), is(equalTo("remote")));
  }

  /**
   * Tests constructor with null ID generates UUID.
   *
   * <p>Verifies that when ID is null, a UUID is auto-generated.
   */
  @Test
  public void testConstructorWithNullIdGeneratesUuid() {
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null, null, null);

    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getId().length(), is(equalTo(36))); // UUID format
  }

  /**
   * Tests constructor with empty ID generates UUID.
   *
   * <p>Verifies that when ID is empty string, a UUID is auto-generated.
   */
  @Test
  public void testConstructorWithEmptyIdGeneratesUuid() {
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null, "", null);

    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getId().length(), is(equalTo(36))); // UUID format
  }

  /**
   * Tests constructor with whitespace ID is preserved.
   *
   * <p>Verifies that whitespace-only ID is kept (StringUtils.isEmpty doesn't trim).
   */
  @Test
  public void testConstructorWithWhitespaceIdIsPreserved() {
    String whitespaceId = "   ";
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null, whitespaceId, null);

    assertThat(request.getId(), is(equalTo(whitespaceId)));
  }

  /**
   * Tests constructor with custom ID is preserved.
   *
   * <p>Verifies that provided non-empty ID is used.
   */
  @Test
  public void testConstructorWithCustomIdIsPreserved() {
    String customId = "my-custom-id";
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null, customId, null);

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

    CreateStorageRequestImpl request = new CreateStorageRequestImpl(items, null);

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
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(new ArrayList<>(), null);

    assertThat(request.getContentItems(), is(empty()));
  }

  // ====================
  // ID Tests
  // ====================

  /**
   * Tests getId returns correct ID.
   *
   * <p>Verifies ID retrieval with custom ID.
   */
  @Test
  public void testGetIdWithCustomId() {
    String customId = "test-id-789";
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null, customId, null);

    assertThat(request.getId(), is(equalTo(customId)));
  }

  /**
   * Tests getId returns generated UUID.
   *
   * <p>Verifies auto-generated ID retrieval.
   */
  @Test
  public void testGetIdWithGeneratedUuid() {
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null);

    String id = request.getId();
    assertThat(id, is(notNullValue()));
    assertThat(
        id.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"), is(true));
  }

  /**
   * Tests each request gets unique ID.
   *
   * <p>Verifies that multiple requests have different auto-generated IDs.
   */
  @Test
  public void testEachRequestGetsUniqueId() {
    CreateStorageRequestImpl request1 = new CreateStorageRequestImpl(null);
    CreateStorageRequestImpl request2 = new CreateStorageRequestImpl(null);
    CreateStorageRequestImpl request3 = new CreateStorageRequestImpl(null);

    assertThat(request1.getId(), is(notNullValue()));
    assertThat(request2.getId(), is(notNullValue()));
    assertThat(request3.getId(), is(notNullValue()));
    assertThat(request1.getId().equals(request2.getId()), is(false));
    assertThat(request1.getId().equals(request3.getId()), is(false));
    assertThat(request2.getId().equals(request3.getId()), is(false));
  }

  // ====================
  // Properties Tests
  // ====================

  /**
   * Tests properties are properly stored and retrieved.
   *
   * <p>Verifies property handling from OperationImpl.
   */
  @Test
  public void testPropertiesHandling() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("prop1", "value1");
    properties.put("prop2", 100);
    properties.put("prop3", true);

    CreateStorageRequestImpl request = new CreateStorageRequestImpl(properties);

    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("prop1"), is(equalTo("value1")));
    assertThat(request.getPropertyValue("prop2"), is(equalTo(100)));
    assertThat(request.getPropertyValue("prop3"), is(equalTo(true)));
  }

  /**
   * Tests request with no properties.
   *
   * <p>Verifies hasProperties returns false when no properties exist.
   */
  @Test
  public void testNoProperties() {
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null);

    assertThat(request.hasProperties(), is(false));
    assertThat(request.getProperties(), is(notNullValue()));
  }

  /**
   * Tests getPropertyValue with non-existent key returns null.
   *
   * <p>Verifies missing property handling.
   */
  @Test
  public void testGetPropertyValueNonExistent() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("existingKey", "value");

    CreateStorageRequestImpl request = new CreateStorageRequestImpl(properties);

    assertThat(request.getPropertyValue("nonExistentKey"), is(nullValue()));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete create storage request with all parameters.
   *
   * <p>Simulates a real storage creation scenario.
   */
  @Test
  public void testCompleteCreateStorageRequest() {
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item1, item2);
    String requestId = "create-req-001";
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("skipValidation", false);
    properties.put("timeout", 30000);

    CreateStorageRequestImpl request = new CreateStorageRequestImpl(items, requestId, properties);

    assertThat(request.getId(), is(equalTo(requestId)));
    assertThat(request.getContentItems(), hasSize(2));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("skipValidation"), is(equalTo(false)));
    assertThat(request.getPropertyValue("timeout"), is(equalTo(30000)));
  }

  /**
   * Tests minimal create storage request.
   *
   * <p>Verifies request can be created with minimal parameters.
   */
  @Test
  public void testMinimalCreateStorageRequest() {
    CreateStorageRequestImpl request = new CreateStorageRequestImpl(null);

    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getContentItems(), is(empty()));
    assertThat(request.hasProperties(), is(false));
  }

  /**
   * Tests request with single content item.
   *
   * <p>Verifies single item storage request.
   */
  @Test
  public void testSingleContentItemRequest() {
    ContentItem item = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item);

    CreateStorageRequestImpl request = new CreateStorageRequestImpl(items, null);

    assertThat(request.getContentItems(), hasSize(1));
    assertThat(request.getContentItems().get(0), is(equalTo(item)));
  }
}
