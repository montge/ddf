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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.content.data.ContentItem;
import ddf.catalog.content.operation.DeleteStorageRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DeleteStorageResponseImplTest {

  @Test
  void testConstructorWithProperties() {
    DeleteStorageRequest request = mock(DeleteStorageRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");

    DeleteStorageResponseImpl response = new DeleteStorageResponseImpl(request, properties);

    assertThat(response.getRequest(), is(sameInstance(request)));
    assertThat(response.getProperties(), hasEntry("key1", "value1"));
    assertThat(response.getDeletedContentItems(), is(nullValue()));
  }

  @Test
  void testConstructorWithContentItems() {
    DeleteStorageRequest request = mock(DeleteStorageRequest.class);
    ContentItem item1 = mock(ContentItem.class);
    ContentItem item2 = mock(ContentItem.class);
    List<ContentItem> contentItems = Arrays.asList(item1, item2);

    DeleteStorageResponseImpl response = new DeleteStorageResponseImpl(request, contentItems);

    assertThat(response.getRequest(), is(sameInstance(request)));
    assertThat(response.getDeletedContentItems(), hasSize(2));
    assertThat(response.getDeletedContentItems().get(0), is(sameInstance(item1)));
    assertThat(response.getDeletedContentItems().get(1), is(sameInstance(item2)));
  }

  @Test
  void testGetStorageRequest() {
    DeleteStorageRequest request = mock(DeleteStorageRequest.class);
    DeleteStorageResponseImpl response =
        new DeleteStorageResponseImpl(request, (Map<String, Serializable>) null);

    assertThat(response.getStorageRequest(), is(sameInstance(request)));
  }

  @Test
  void testConstructorWithNullProperties() {
    DeleteStorageRequest request = mock(DeleteStorageRequest.class);

    DeleteStorageResponseImpl response =
        new DeleteStorageResponseImpl(request, (Map<String, Serializable>) null);

    assertThat(response.getRequest(), is(sameInstance(request)));
    assertThat(response.hasProperties(), is(false));
  }

  @Test
  void testConstructorWithEmptyContentItems() {
    DeleteStorageRequest request = mock(DeleteStorageRequest.class);
    List<ContentItem> contentItems = Arrays.asList();

    DeleteStorageResponseImpl response = new DeleteStorageResponseImpl(request, contentItems);

    assertThat(response.getDeletedContentItems(), hasSize(0));
  }

  @Test
  void testGetDeletedContentItemsWithNull() {
    DeleteStorageRequest request = mock(DeleteStorageRequest.class);
    Map<String, Serializable> properties = new HashMap<>();

    DeleteStorageResponseImpl response = new DeleteStorageResponseImpl(request, properties);

    assertThat(response.getDeletedContentItems(), is(nullValue()));
  }
}
