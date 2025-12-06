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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.operation.ResourceRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Comprehensive test suite for {@link ResourceRequestById} class.
 *
 * <p>Tests all constructors, getters, and properties handling.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceRequestByIdTest {

  private static final String RESOURCE_ID = "resource-123";

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with ID only.
   *
   * <p>Verifies basic construction with ID.
   */
  @Test
  public void testConstructorWithIdOnly() {
    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID);

    assertThat(request, is(notNullValue()));
    assertThat(request.getAttributeValue(), is(equalTo(RESOURCE_ID)));
    assertThat(request.getAttributeName(), is(equalTo(ResourceRequest.GET_RESOURCE_BY_ID)));
    assertThat(request.hasProperties(), is(false));
  }

  /**
   * Tests constructor with ID and properties.
   *
   * <p>Verifies properties are stored correctly.
   */
  @Test
  public void testConstructorWithIdAndProperties() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("timeout", 5000);
    properties.put("source", "remote");

    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID, properties);

    assertThat(request.getAttributeValue(), is(equalTo(RESOURCE_ID)));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("timeout"), is(equalTo(5000)));
    assertThat(request.getPropertyValue("source"), is(equalTo("remote")));
  }

  /**
   * Tests constructor with null ID.
   *
   * <p>Verifies null ID is accepted.
   */
  @Test
  public void testConstructorWithNullId() {
    ResourceRequestById request = new ResourceRequestById(null);

    assertThat(request, is(notNullValue()));
    assertThat(request.getAttributeValue(), is(nullValue()));
    assertThat(request.getAttributeName(), is(equalTo(ResourceRequest.GET_RESOURCE_BY_ID)));
  }

  /**
   * Tests constructor with null properties.
   *
   * <p>Verifies null properties are handled.
   */
  @Test
  public void testConstructorWithNullProperties() {
    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID, null);

    assertThat(request.getAttributeValue(), is(equalTo(RESOURCE_ID)));
    assertThat(request.hasProperties(), is(false));
  }

  /**
   * Tests constructor with empty ID.
   *
   * <p>Verifies empty string ID is accepted.
   */
  @Test
  public void testConstructorWithEmptyId() {
    ResourceRequestById request = new ResourceRequestById("");

    assertThat(request.getAttributeValue(), is(equalTo("")));
  }

  // ====================
  // Attribute Tests
  // ====================

  /**
   * Tests getAttributeName returns correct constant.
   *
   * <p>Verifies attribute name is GET_RESOURCE_BY_ID.
   */
  @Test
  public void testGetAttributeName() {
    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID);

    assertThat(request.getAttributeName(), is(equalTo(ResourceRequest.GET_RESOURCE_BY_ID)));
  }

  /**
   * Tests getAttributeValue returns correct ID.
   *
   * <p>Verifies ID retrieval.
   */
  @Test
  public void testGetAttributeValue() {
    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID);

    assertThat(request.getAttributeValue(), is(equalTo(RESOURCE_ID)));
  }

  /**
   * Tests getAttributeValue with different IDs.
   *
   * <p>Verifies various ID formats.
   */
  @Test
  public void testGetAttributeValueWithVariousIds() {
    String uuid = "550e8400-e29b-41d4-a716-446655440000";
    ResourceRequestById request1 = new ResourceRequestById(uuid);
    assertThat(request1.getAttributeValue(), is(equalTo(uuid)));

    String alphanumeric = "abc123def456";
    ResourceRequestById request2 = new ResourceRequestById(alphanumeric);
    assertThat(request2.getAttributeValue(), is(equalTo(alphanumeric)));

    String withDashes = "resource-id-123-456";
    ResourceRequestById request3 = new ResourceRequestById(withDashes);
    assertThat(request3.getAttributeValue(), is(equalTo(withDashes)));
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
    properties.put("cacheEnabled", true);
    properties.put("maxRetries", 3);
    properties.put("contentType", "image/jpeg");

    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID, properties);

    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("cacheEnabled"), is(equalTo(true)));
    assertThat(request.getPropertyValue("maxRetries"), is(equalTo(3)));
    assertThat(request.getPropertyValue("contentType"), is(equalTo("image/jpeg")));
  }

  /**
   * Tests getPropertyValue with non-existent key.
   *
   * <p>Verifies missing property returns null.
   */
  @Test
  public void testGetPropertyValueNonExistent() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("existingKey", "value");

    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID, properties);

    assertThat(request.getPropertyValue("nonExistentKey"), is(nullValue()));
  }

  /**
   * Tests containsPropertyName.
   *
   * <p>Verifies property name checking.
   */
  @Test
  public void testContainsPropertyName() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");

    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID, properties);

    assertThat(request.containsPropertyName("key1"), is(true));
    assertThat(request.containsPropertyName("missing"), is(false));
  }

  /**
   * Tests request with no properties.
   *
   * <p>Verifies hasProperties returns false.
   */
  @Test
  public void testNoProperties() {
    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID);

    assertThat(request.hasProperties(), is(false));
    assertThat(request.getProperties(), is(notNullValue()));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete resource request scenario.
   *
   * <p>Simulates a real resource retrieval request.
   */
  @Test
  public void testCompleteResourceRequest() {
    String resourceId = "file-12345";
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("sourceId", "local-catalog");
    properties.put("timeout", 30000);

    ResourceRequestById request = new ResourceRequestById(resourceId, properties);

    assertThat(request.getAttributeName(), is(equalTo(ResourceRequest.GET_RESOURCE_BY_ID)));
    assertThat(request.getAttributeValue(), is(equalTo(resourceId)));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("sourceId"), is(equalTo("local-catalog")));
    assertThat(request.getPropertyValue("timeout"), is(equalTo(30000)));
  }

  /**
   * Tests minimal resource request.
   *
   * <p>Verifies request can be created with minimal parameters.
   */
  @Test
  public void testMinimalResourceRequest() {
    ResourceRequestById request = new ResourceRequestById(RESOURCE_ID);

    assertThat(request, is(notNullValue()));
    assertThat(request.getAttributeValue(), is(equalTo(RESOURCE_ID)));
    assertThat(request.hasProperties(), is(false));
  }
}
