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
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.operation.ResourceRequest;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ResourceRequestByProductUriTest {

  @Test
  void testConstructorWithUriOnly() throws URISyntaxException {
    URI uri = new URI("content:abc123");

    ResourceRequestByProductUri request = new ResourceRequestByProductUri(uri);

    assertThat(request.getAttributeValue(), is(uri));
    assertThat(request.getAttributeName(), is(ResourceRequest.GET_RESOURCE_BY_PRODUCT_URI));
  }

  @Test
  void testConstructorWithUriAndProperties() throws URISyntaxException {
    URI uri = new URI("content:xyz789");
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");
    properties.put("key2", 42);

    ResourceRequestByProductUri request = new ResourceRequestByProductUri(uri, properties);

    assertThat(request.getAttributeValue(), is(uri));
    assertThat(request.getAttributeName(), is(ResourceRequest.GET_RESOURCE_BY_PRODUCT_URI));
    assertThat(request.getProperties(), hasEntry("key1", "value1"));
    assertThat(request.getProperties(), hasEntry("key2", 42));
  }

  @Test
  void testConstructorWithNullProperties() throws URISyntaxException {
    URI uri = new URI("content:test");

    ResourceRequestByProductUri request = new ResourceRequestByProductUri(uri, null);

    assertThat(request.getAttributeValue(), is(uri));
    assertThat(request.getAttributeName(), is(ResourceRequest.GET_RESOURCE_BY_PRODUCT_URI));
  }

  @Test
  void testConstructorWithNullUri() {
    ResourceRequestByProductUri request = new ResourceRequestByProductUri(null);

    assertThat(request.getAttributeValue(), is(nullValue()));
    assertThat(request.getAttributeName(), is(ResourceRequest.GET_RESOURCE_BY_PRODUCT_URI));
  }

  @Test
  void testGetAttributeNameReturnsCorrectConstant() throws URISyntaxException {
    URI uri = new URI("http://example.com/resource");

    ResourceRequestByProductUri request = new ResourceRequestByProductUri(uri);

    assertThat(request.getAttributeName(), is(ResourceRequest.GET_RESOURCE_BY_PRODUCT_URI));
  }

  @Test
  void testGetAttributeValueReturnsUri() throws URISyntaxException {
    URI uri = new URI("file:///path/to/file.txt");

    ResourceRequestByProductUri request = new ResourceRequestByProductUri(uri);

    assertThat(request.getAttributeValue(), is(uri));
  }

  @Test
  void testEmptyProperties() throws URISyntaxException {
    URI uri = new URI("content:empty");
    Map<String, Serializable> properties = new HashMap<>();

    ResourceRequestByProductUri request = new ResourceRequestByProductUri(uri, properties);

    assertThat(request.getProperties().isEmpty(), is(true));
  }
}
