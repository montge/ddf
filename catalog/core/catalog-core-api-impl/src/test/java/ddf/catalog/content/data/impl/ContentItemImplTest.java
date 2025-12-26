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
package ddf.catalog.content.data.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import com.google.common.io.ByteSource;
import ddf.catalog.content.data.ContentItem;
import ddf.catalog.data.Metacard;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class ContentItemImplTest {

  @Test
  void testBasicConstructor() {
    ByteSource byteSource = ByteSource.wrap("test content".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("item-123", byteSource, "text/plain", metacard);

    assertThat(item.getId(), is("item-123"));
    assertThat(item.getMimeTypeRawData(), is("text/plain"));
    assertThat(item.getMetacard(), is(sameInstance(metacard)));
    assertThat(item.getFilename(), is(ContentItem.DEFAULT_FILE_NAME));
  }

  @Test
  void testConstructorWithFilenameAndSize() throws IOException {
    ByteSource byteSource = ByteSource.wrap("content".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item =
        new ContentItemImpl(
            "id-456", byteSource, "application/pdf", "document.pdf", 1024, metacard);

    assertThat(item.getId(), is("id-456"));
    assertThat(item.getFilename(), is("document.pdf"));
    assertThat(item.getSize(), is(1024L));
    assertThat(item.getMimeTypeRawData(), is("application/pdf"));
  }

  @Test
  void testConstructorWithQualifier() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item =
        new ContentItemImpl("id-789", "thumbnail", byteSource, "image/png", metacard);

    assertThat(item.getId(), is("id-789"));
    assertThat(item.getQualifier(), is("thumbnail"));
    assertThat(item.getUri(), containsString("thumbnail"));
  }

  @Test
  void testFullConstructor() throws IOException {
    ByteSource byteSource = ByteSource.wrap("full content".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item =
        new ContentItemImpl(
            "full-id", "preview", byteSource, "video/mp4", "video.mp4", 2048, metacard);

    assertThat(item.getId(), is("full-id"));
    assertThat(item.getQualifier(), is("preview"));
    assertThat(item.getFilename(), is("video.mp4"));
    assertThat(item.getSize(), is(2048L));
    assertThat(item.getMimeTypeRawData(), is("video/mp4"));
  }

  @Test
  void testDefaultFilenameWhenNull() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("id", byteSource, "text/plain", null, 100, metacard);

    assertThat(item.getFilename(), is(ContentItem.DEFAULT_FILE_NAME));
  }

  @Test
  void testDefaultMimeTypeWhenNull() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("id", byteSource, null, metacard);

    assertThat(item.getMimeTypeRawData(), is(ContentItem.DEFAULT_MIME_TYPE));
  }

  @Test
  void testDefaultMimeTypeWhenBlank() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("id", byteSource, "   ", metacard);

    assertThat(item.getMimeTypeRawData(), is(ContentItem.DEFAULT_MIME_TYPE));
  }

  @Test
  void testBlankQualifierTreatedAsNull() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("id", "   ", byteSource, "text/plain", metacard);

    assertThat(item.getQualifier(), is(nullValue()));
  }

  @Test
  void testEmptyQualifierTreatedAsNull() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("id", "", byteSource, "text/plain", metacard);

    assertThat(item.getQualifier(), is(nullValue()));
  }

  @Test
  void testGetInputStream() throws IOException {
    byte[] content = "test input stream".getBytes();
    ByteSource byteSource = ByteSource.wrap(content);
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("id", byteSource, "text/plain", metacard);

    try (InputStream inputStream = item.getInputStream()) {
      assertThat(inputStream, is(notNullValue()));
      byte[] readContent = inputStream.readAllBytes();
      assertThat(readContent, is(content));
    }
  }

  @Test
  void testGetUri() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("my-id", byteSource, "text/plain", metacard);

    assertThat(item.getUri(), containsString(ContentItem.CONTENT_SCHEME));
    assertThat(item.getUri(), containsString("my-id"));
  }

  @Test
  void testGetUriWithQualifier() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item =
        new ContentItemImpl("my-id", "derived", byteSource, "text/plain", metacard);

    assertThat(item.getUri(), containsString(ContentItem.CONTENT_SCHEME));
    assertThat(item.getUri(), containsString("my-id"));
    assertThat(item.getUri(), containsString("derived"));
  }

  @Test
  void testMimeTypeIsValid() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("id", byteSource, "application/json", metacard);

    assertThat(item.getMimeType(), is(notNullValue()));
    assertThat(item.getMimeType().getBaseType(), is("application/json"));
  }

  @Test
  void testInvalidMimeTypeHandled() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item = new ContentItemImpl("id", byteSource, "invalid-mime-type", metacard);

    // Invalid mime type should be stored but MimeType object should be null
    assertThat(item.getMimeTypeRawData(), is("invalid-mime-type"));
    assertThat(item.getMimeType(), is(nullValue()));
  }

  @Test
  void testToString() {
    ByteSource byteSource = ByteSource.wrap("data".getBytes());
    Metacard metacard = mock(Metacard.class);

    ContentItemImpl item =
        new ContentItemImpl("test-id", byteSource, "text/plain", "test.txt", 100, metacard);

    String result = item.toString();

    assertThat(result, containsString("test-id"));
    assertThat(result, containsString("test.txt"));
    assertThat(result, containsString("text/plain"));
  }
}
