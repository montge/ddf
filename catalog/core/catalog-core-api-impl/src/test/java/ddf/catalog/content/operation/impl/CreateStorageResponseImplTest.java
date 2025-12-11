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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.content.data.ContentItem;
import ddf.catalog.content.operation.CreateStorageRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link CreateStorageResponseImpl} class.
 *
 * <p>Tests constructors, getters, properties handling, and toString method.
 */
@ExtendWith(MockitoExtension.class)
public class CreateStorageResponseImplTest {

  @Mock private CreateStorageRequest mockRequest;

  private Map<String, Serializable> properties;

  @BeforeEach
  public void setUp() {
    properties = new HashMap<>();
    properties.put("key1", "value1");
    properties.put("key2", 42);
  }

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with request and properties.
   *
   * <p>Verifies basic construction with properties.
   */
  @Test
  public void testConstructorWithRequestAndProperties() {
    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, properties);

    assertThat(response, is(notNullValue()));
    assertThat(response.getRequest(), is(equalTo(mockRequest)));
    assertThat(response.getStorageRequest(), is(equalTo(mockRequest)));
    assertThat(response.hasProperties(), is(true));
    assertThat(response.getPropertyValue("key1"), is(equalTo("value1")));
  }

  /**
   * Tests constructor with request and null properties.
   *
   * <p>Verifies null properties are handled.
   */
  @Test
  public void testConstructorWithNullProperties() {
    CreateStorageResponseImpl response =
        new CreateStorageResponseImpl(mockRequest, (Map<String, Serializable>) null);

    assertThat(response.getRequest(), is(equalTo(mockRequest)));
    assertThat(response.hasProperties(), is(false));
  }

  /**
   * Tests constructor with request and created content items.
   *
   * <p>Verifies content items are properly stored.
   */
  @Test
  public void testConstructorWithContentItems() {
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item1, item2);

    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, items);

    assertThat(response.getCreatedContentItems(), hasSize(2));
    assertThat(response.getCreatedContentItems().get(0), is(equalTo(item1)));
    assertThat(response.getCreatedContentItems().get(1), is(equalTo(item2)));
    assertThat(response.getRequest(), is(equalTo(mockRequest)));
  }

  /**
   * Tests constructor with null request.
   *
   * <p>Verifies null request is accepted.
   */
  @Test
  public void testConstructorWithNullRequest() {
    CreateStorageResponseImpl response = new CreateStorageResponseImpl(null, new HashMap<>());

    assertThat(response.getRequest(), is(nullValue()));
  }

  /**
   * Tests constructor with null content items.
   *
   * <p>Verifies null content items are handled.
   */
  @Test
  public void testConstructorWithNullContentItems() {
    CreateStorageResponseImpl response =
        new CreateStorageResponseImpl(mockRequest, (List<ContentItem>) null);

    assertThat(response.getCreatedContentItems(), is(nullValue()));
  }

  /**
   * Tests constructor with empty content items list.
   *
   * <p>Verifies empty list is stored correctly.
   */
  @Test
  public void testConstructorWithEmptyContentItems() {
    List<ContentItem> emptyList = new ArrayList<>();
    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, emptyList);

    assertThat(response.getCreatedContentItems(), is(notNullValue()));
    assertThat(response.getCreatedContentItems().size(), is(equalTo(0)));
  }

  // ====================
  // Getter Tests
  // ====================

  /**
   * Tests getCreatedContentItems returns correct items.
   *
   * <p>Verifies created content items retrieval.
   */
  @Test
  public void testGetCreatedContentItems() {
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    ContentItem item3 = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item1, item2, item3);

    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, items);

    assertThat(response.getCreatedContentItems(), hasSize(3));
    assertThat(response.getCreatedContentItems().get(0), is(equalTo(item1)));
    assertThat(response.getCreatedContentItems().get(1), is(equalTo(item2)));
    assertThat(response.getCreatedContentItems().get(2), is(equalTo(item3)));
  }

  /**
   * Tests getStorageRequest returns the original request.
   *
   * <p>Verifies storage request retrieval.
   */
  @Test
  public void testGetStorageRequest() {
    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, properties);

    assertThat(response.getStorageRequest(), is(equalTo(mockRequest)));
  }

  /**
   * Tests getRequest returns the original request.
   *
   * <p>Verifies request retrieval from parent class.
   */
  @Test
  public void testGetRequest() {
    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, properties);

    assertThat(response.getRequest(), is(equalTo(mockRequest)));
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
    Map<String, Serializable> props = new HashMap<>();
    props.put("status", "success");
    props.put("count", 5);

    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, props);

    assertThat(response.hasProperties(), is(true));
    assertThat(response.getPropertyValue("status"), is(equalTo("success")));
    assertThat(response.getPropertyValue("count"), is(equalTo(5)));
  }

  /**
   * Tests response with no properties.
   *
   * <p>Verifies behavior when no properties exist.
   */
  @Test
  public void testNoProperties() {
    CreateStorageResponseImpl response =
        new CreateStorageResponseImpl(mockRequest, (List<ContentItem>) null);

    assertThat(response.hasProperties(), is(false));
  }

  // ====================
  // ToString Tests
  // ====================

  /**
   * Tests toString with null content items.
   *
   * <p>Verifies toString handles null content items.
   */
  @Test
  public void testToStringWithNullContentItems() {
    CreateStorageResponseImpl response =
        new CreateStorageResponseImpl(mockRequest, (List<ContentItem>) null);

    String result = response.toString();

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("createdContentItems=null"));
  }

  /**
   * Tests toString with content items.
   *
   * <p>Verifies toString formats content items correctly.
   */
  @Test
  public void testToStringWithContentItems() {
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    when(item1.toString()).thenReturn("ContentItem1");
    when(item2.toString()).thenReturn("ContentItem2");
    List<ContentItem> items = Arrays.asList(item1, item2);

    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, items);

    String result = response.toString();

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("CreateStorageResponseImpl"));
    assertThat(result, containsString("ContentItem1"));
    assertThat(result, containsString("ContentItem2"));
  }

  /**
   * Tests toString with empty content items.
   *
   * <p>Verifies toString handles empty list.
   */
  @Test
  public void testToStringWithEmptyContentItems() {
    List<ContentItem> emptyList = new ArrayList<>();
    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, emptyList);

    String result = response.toString();

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("CreateStorageResponseImpl"));
    assertThat(result, containsString("[]"));
  }

  /**
   * Tests toString with null content item in list.
   *
   * <p>Verifies toString filters out null items.
   */
  @Test
  public void testToStringWithNullItemInList() {
    ContentItem item1 = mock(ContentItem.class);
    when(item1.toString()).thenReturn("ContentItem1");
    List<ContentItem> items = Arrays.asList(item1, null);

    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, items);

    String result = response.toString();

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("ContentItem1"));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete create storage response scenario.
   *
   * <p>Simulates a real storage response with all data.
   */
  @Test
  public void testCompleteCreateStorageResponse() {
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    List<ContentItem> items = Arrays.asList(item1, item2);

    CreateStorageResponseImpl response = new CreateStorageResponseImpl(mockRequest, items);

    assertThat(response.getCreatedContentItems(), hasSize(2));
    assertThat(response.getRequest(), is(equalTo(mockRequest)));
    assertThat(response.getStorageRequest(), is(equalTo(mockRequest)));
  }
}
