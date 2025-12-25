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
package ddf.sdk.plugin.storage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.content.data.ContentItem;
import ddf.catalog.content.operation.CreateStorageRequest;
import ddf.catalog.content.operation.UpdateStorageRequest;
import ddf.catalog.data.Metacard;
import ddf.catalog.plugin.PluginExecutionException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PreviewStoragePluginTest {

  private static final String TEST_ID = "test-metacard-id";
  private static final String TEST_TITLE = "Test Title";
  private static final byte[] TEST_THUMBNAIL = new byte[] {0x01, 0x02, 0x03};

  private PreviewStoragePlugin plugin;

  @BeforeEach
  void setUp() {
    plugin = new PreviewStoragePlugin();
  }

  @Test
  void testProcessCreateWithNullInputReturnsNull() throws PluginExecutionException {
    CreateStorageRequest result = plugin.process((CreateStorageRequest) null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testProcessUpdateWithNullInputReturnsNull() throws PluginExecutionException {
    UpdateStorageRequest result = plugin.process((UpdateStorageRequest) null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testProcessCreateWithEmptyContentItems() throws PluginExecutionException {
    CreateStorageRequest request = mock(CreateStorageRequest.class);
    when(request.getContentItems()).thenReturn(new ArrayList<>());

    CreateStorageRequest result = plugin.process(request);

    assertThat(result, is(sameInstance(request)));
  }

  @Test
  void testProcessUpdateWithEmptyContentItems() throws PluginExecutionException {
    UpdateStorageRequest request = mock(UpdateStorageRequest.class);
    when(request.getContentItems()).thenReturn(new ArrayList<>());

    UpdateStorageRequest result = plugin.process(request);

    assertThat(result, is(sameInstance(request)));
  }

  @Test
  void testProcessCreateWithItemWithoutThumbnailDoesNotAddPreview()
      throws PluginExecutionException {
    CreateStorageRequest request = mock(CreateStorageRequest.class);
    ContentItem contentItem = createContentItemWithoutThumbnail();
    List<ContentItem> items = new ArrayList<>();
    items.add(contentItem);
    when(request.getContentItems()).thenReturn(items);

    CreateStorageRequest result = plugin.process(request);

    assertThat(result.getContentItems(), hasSize(1));
  }

  @Test
  void testProcessCreateWithItemWithThumbnailAddsPreview() throws PluginExecutionException {
    CreateStorageRequest request = mock(CreateStorageRequest.class);
    ContentItem contentItem = createContentItemWithThumbnail();
    List<ContentItem> items = new ArrayList<>();
    items.add(contentItem);
    when(request.getContentItems()).thenReturn(items);

    CreateStorageRequest result = plugin.process(request);

    assertThat(result.getContentItems(), hasSize(2));
  }

  @Test
  void testProcessUpdateWithItemWithThumbnailAddsPreview() throws PluginExecutionException {
    UpdateStorageRequest request = mock(UpdateStorageRequest.class);
    ContentItem contentItem = createContentItemWithThumbnail();
    List<ContentItem> items = new ArrayList<>();
    items.add(contentItem);
    when(request.getContentItems()).thenReturn(items);

    UpdateStorageRequest result = plugin.process(request);

    assertThat(result.getContentItems(), hasSize(2));
  }

  @Test
  void testProcessCreateWithMixedItemsOnlyCreatesPreviewsForItemsWithThumbnails()
      throws PluginExecutionException {
    CreateStorageRequest request = mock(CreateStorageRequest.class);
    List<ContentItem> items = new ArrayList<>();
    items.add(createContentItemWithThumbnail());
    items.add(createContentItemWithoutThumbnail());
    items.add(createContentItemWithThumbnail());
    when(request.getContentItems()).thenReturn(items);

    CreateStorageRequest result = plugin.process(request);

    assertThat(result.getContentItems(), hasSize(5));
  }

  private ContentItem createContentItemWithThumbnail() {
    Metacard metacard = mock(Metacard.class);
    when(metacard.getThumbnail()).thenReturn(TEST_THUMBNAIL);
    when(metacard.getId()).thenReturn(TEST_ID);
    when(metacard.getTitle()).thenReturn(TEST_TITLE);

    ContentItem item = mock(ContentItem.class);
    when(item.getMetacard()).thenReturn(metacard);
    return item;
  }

  private ContentItem createContentItemWithoutThumbnail() {
    Metacard metacard = mock(Metacard.class);
    when(metacard.getThumbnail()).thenReturn(null);

    ContentItem item = mock(ContentItem.class);
    when(item.getMetacard()).thenReturn(metacard);
    return item;
  }
}
