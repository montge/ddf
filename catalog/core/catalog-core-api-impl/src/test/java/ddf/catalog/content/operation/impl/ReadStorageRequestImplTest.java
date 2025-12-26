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
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReadStorageRequestImplTest {

  @Test
  void testConstructorWithPropertiesOnly() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key", "value");

    ReadStorageRequestImpl request = new ReadStorageRequestImpl(properties);

    assertThat(request.getResourceUri(), is(nullValue()));
    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getProperties(), hasEntry("key", "value"));
  }

  @Test
  void testConstructorWithUriAndProperties() throws URISyntaxException {
    URI uri = new URI("content:abc123");
    Map<String, Serializable> properties = new HashMap<>();

    ReadStorageRequestImpl request = new ReadStorageRequestImpl(uri, properties);

    assertThat(request.getResourceUri(), is(uri));
    assertThat(request.getId(), is(notNullValue()));
  }

  @Test
  void testConstructorWithAllParameters() throws URISyntaxException {
    URI uri = new URI("content:xyz789");
    String id = "my-request-id";
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("prop1", "val1");

    ReadStorageRequestImpl request = new ReadStorageRequestImpl(uri, id, properties);

    assertThat(request.getResourceUri(), is(uri));
    assertThat(request.getId(), is("my-request-id"));
    assertThat(request.getProperties(), hasEntry("prop1", "val1"));
  }

  @Test
  void testIdGeneratedWhenEmpty() throws URISyntaxException {
    URI uri = new URI("content:test");

    ReadStorageRequestImpl request = new ReadStorageRequestImpl(uri, "", null);

    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getId().length(), is(36)); // UUID length
  }

  @Test
  void testIdGeneratedWhenNull() throws URISyntaxException {
    URI uri = new URI("content:test");

    ReadStorageRequestImpl request = new ReadStorageRequestImpl(uri, null, null);

    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getId().length(), is(36)); // UUID length
  }

  @Test
  void testIdUsedWhenProvided() throws URISyntaxException {
    URI uri = new URI("content:test");
    String providedId = "custom-id-123";

    ReadStorageRequestImpl request = new ReadStorageRequestImpl(uri, providedId, null);

    assertThat(request.getId(), is("custom-id-123"));
  }

  @Test
  void testGetResourceUri() throws URISyntaxException {
    URI uri = new URI("http://example.com/resource");

    ReadStorageRequestImpl request = new ReadStorageRequestImpl(uri, null);

    assertThat(request.getResourceUri(), is(uri));
  }

  @Test
  void testNullResourceUri() {
    ReadStorageRequestImpl request = new ReadStorageRequestImpl(null, null);

    assertThat(request.getResourceUri(), is(nullValue()));
    assertThat(request.getId(), is(notNullValue()));
  }

  @Test
  void testNullProperties() throws URISyntaxException {
    URI uri = new URI("content:test");

    ReadStorageRequestImpl request = new ReadStorageRequestImpl(uri, "id", null);

    assertThat(request.getResourceUri(), is(uri));
    assertThat(request.getId(), is("id"));
  }
}
