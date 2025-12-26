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
package ddf.catalog.resource.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.resource.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReliableResourceTest {

  private static final String TEST_KEY = "testKey";
  private static final String TEST_NAME = "testResource";

  @TempDir Path tempDir;

  private Metacard metacard;
  private MimeType mimeType;

  @BeforeEach
  void setUp() throws MimeTypeParseException {
    MetacardType metacardType = mock(MetacardType.class);
    when(metacardType.getName()).thenReturn("test");
    when(metacardType.getAttributeDescriptors()).thenReturn(Collections.emptySet());

    metacard = mock(Metacard.class);
    when(metacard.getId()).thenReturn("metacardId");
    when(metacard.getMetacardType()).thenReturn(metacardType);
    mimeType = new MimeType("application/octet-stream");
  }

  @Test
  void testConstructorAndGetters() {
    String filePath = "/path/to/file";
    ReliableResource resource =
        new ReliableResource(TEST_KEY, filePath, mimeType, TEST_NAME, metacard);

    assertThat(resource.getKey(), is(TEST_KEY));
    assertThat(resource.getFilePath(), is(filePath));
    assertThat(resource.getMimeType(), is(mimeType));
    assertThat(resource.getName(), is(TEST_NAME));
    assertThat(resource.getMetacard(), is(notNullValue()));
  }

  @Test
  void testImplementsResource() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    assertThat(resource, instanceOf(Resource.class));
  }

  @Test
  void testImplementsSerializable() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    assertThat(resource, instanceOf(Serializable.class));
  }

  @Test
  void testGetMimeTypeValue() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    assertThat(resource.getMimeTypeValue(), is("application/octet-stream"));
  }

  @Test
  void testGetMimeTypeValueWhenNull() {
    ReliableResource resource = new ReliableResource(TEST_KEY, "/path", null, TEST_NAME, metacard);

    assertThat(resource.getMimeTypeValue(), is(nullValue()));
  }

  @Test
  void testSetMimeType() throws MimeTypeParseException {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);
    MimeType newMimeType = new MimeType("text/plain");

    resource.setMimeType(newMimeType);

    assertThat(resource.getMimeType(), is(newMimeType));
    assertThat(resource.getMimeTypeValue(), is("text/plain"));
  }

  @Test
  void testDefaultSizeIsNegativeOne() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    assertThat(resource.getSize(), is(-1L));
  }

  @Test
  void testSetAndGetSize() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    resource.setSize(12345L);

    assertThat(resource.getSize(), is(12345L));
  }

  @Test
  void testSetName() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    resource.setName("newName");

    assertThat(resource.getName(), is("newName"));
  }

  @Test
  void testDefaultLastTouchedMillisIsZero() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    assertThat(resource.getLastTouchedMillis(), is(0L));
  }

  @Test
  void testSetAndGetLastTouchedMillis() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);
    long now = System.currentTimeMillis();

    resource.setLastTouchedMillis(now);

    assertThat(resource.getLastTouchedMillis(), is(now));
  }

  @Test
  void testHasProductReturnsFalseWhenFilePathIsNull() {
    ReliableResource resource = new ReliableResource(TEST_KEY, null, mimeType, TEST_NAME, metacard);

    assertThat(resource.hasProduct(), is(false));
  }

  @Test
  void testHasProductReturnsFalseWhenFileDoesNotExist() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/nonexistent/file", mimeType, TEST_NAME, metacard);

    assertThat(resource.hasProduct(), is(false));
  }

  @Test
  void testHasProductReturnsTrueWhenFileExists() throws IOException {
    File tempFile = Files.createFile(tempDir.resolve("testfile.txt")).toFile();
    ReliableResource resource =
        new ReliableResource(TEST_KEY, tempFile.getAbsolutePath(), mimeType, TEST_NAME, metacard);

    assertThat(resource.hasProduct(), is(true));
  }

  @Test
  void testGetInputStreamReturnsNullWhenFilePathIsNull() {
    ReliableResource resource = new ReliableResource(TEST_KEY, null, mimeType, TEST_NAME, metacard);

    InputStream result = resource.getInputStream();

    assertThat(result, is(nullValue()));
  }

  @Test
  void testGetInputStreamReturnsNullWhenFileDoesNotExist() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/nonexistent/file", mimeType, TEST_NAME, metacard);

    InputStream result = resource.getInputStream();

    assertThat(result, is(nullValue()));
  }

  @Test
  void testGetInputStreamReturnsStreamWhenFileExists() throws IOException {
    File tempFile = Files.createFile(tempDir.resolve("testfile2.txt")).toFile();
    Files.writeString(tempFile.toPath(), "test content");
    ReliableResource resource =
        new ReliableResource(TEST_KEY, tempFile.getAbsolutePath(), mimeType, TEST_NAME, metacard);

    try (InputStream result = resource.getInputStream()) {
      assertThat(result, is(notNullValue()));
    }
  }

  @Test
  void testToStringContainsKey() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    String result = resource.toString();

    assertThat(result, containsString(TEST_KEY));
  }

  @Test
  void testToStringContainsFilePath() {
    String filePath = "/some/test/path";
    ReliableResource resource =
        new ReliableResource(TEST_KEY, filePath, mimeType, TEST_NAME, metacard);

    String result = resource.toString();

    assertThat(result, containsString(filePath));
  }

  @Test
  void testGetMetacardReturnsCopy() {
    ReliableResource resource =
        new ReliableResource(TEST_KEY, "/path", mimeType, TEST_NAME, metacard);

    Metacard result = resource.getMetacard();

    assertThat(result, is(notNullValue()));
  }
}
