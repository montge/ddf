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
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.content.data.ContentItem;
import ddf.catalog.content.operation.ReadStorageRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReadStorageResponseImplTest {

  @Test
  void testConstructorWithRequestOnly() {
    ReadStorageRequest request = mock(ReadStorageRequest.class);

    ReadStorageResponseImpl response = new ReadStorageResponseImpl(request);

    assertThat(response.getStorageRequest(), is(sameInstance(request)));
    assertThat(response.getContentItem(), is(nullValue()));
  }

  @Test
  void testConstructorWithRequestAndProperties() {
    ReadStorageRequest request = mock(ReadStorageRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key", "value");

    ReadStorageResponseImpl response = new ReadStorageResponseImpl(request, properties);

    assertThat(response.getStorageRequest(), is(sameInstance(request)));
    assertThat(response.getProperties(), hasEntry("key", "value"));
    assertThat(response.getContentItem(), is(nullValue()));
  }

  @Test
  void testConstructorWithRequestAndContentItem() {
    ReadStorageRequest request = mock(ReadStorageRequest.class);
    ContentItem contentItem = mock(ContentItem.class);

    ReadStorageResponseImpl response = new ReadStorageResponseImpl(request, contentItem);

    assertThat(response.getStorageRequest(), is(sameInstance(request)));
    assertThat(response.getContentItem(), is(sameInstance(contentItem)));
  }

  @Test
  void testGetContentItemReturnsNull() {
    ReadStorageRequest request = mock(ReadStorageRequest.class);

    ReadStorageResponseImpl response = new ReadStorageResponseImpl(request);

    assertThat(response.getContentItem(), is(nullValue()));
  }

  @Test
  void testGetStorageRequestReturnsRequest() {
    ReadStorageRequest request = mock(ReadStorageRequest.class);

    ReadStorageResponseImpl response = new ReadStorageResponseImpl(request);

    assertThat(response.getStorageRequest(), is(sameInstance(request)));
  }

  @Test
  void testConstructorWithNullRequest() {
    ReadStorageResponseImpl response = new ReadStorageResponseImpl(null);

    assertThat(response.getStorageRequest(), is(nullValue()));
  }

  @Test
  void testConstructorWithNullProperties() {
    ReadStorageRequest request = mock(ReadStorageRequest.class);

    ReadStorageResponseImpl response =
        new ReadStorageResponseImpl(request, (Map<String, Serializable>) null);

    assertThat(response.getStorageRequest(), is(sameInstance(request)));
  }
}
