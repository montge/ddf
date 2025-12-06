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
package org.codice.ddf.attachment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/** Unit tests for {@link AttachmentInfo} interface to verify contract and expected behavior. */
@RunWith(MockitoJUnitRunner.class)
public class AttachmentInfoTest {

  private static final String TEST_FILENAME = "test-document.pdf";

  private static final String TEST_CONTENT_TYPE = "application/pdf";

  private static final byte[] TEST_DATA = "test attachment data".getBytes();

  @Test
  public void testAttachmentInfoWithValidData() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);
    AttachmentInfo attachmentInfo =
        createAttachmentInfo(testStream, TEST_FILENAME, TEST_CONTENT_TYPE);

    // Then
    assertThat(attachmentInfo.getStream(), is(notNullValue()));
    assertThat(attachmentInfo.getStream(), is(testStream));
    assertThat(attachmentInfo.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(attachmentInfo.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testAttachmentInfoWithNullStream() {
    // Given
    AttachmentInfo attachmentInfo = createAttachmentInfo(null, TEST_FILENAME, TEST_CONTENT_TYPE);

    // Then
    assertThat(attachmentInfo.getStream(), is(nullValue()));
    assertThat(attachmentInfo.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(attachmentInfo.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testAttachmentInfoWithNullFilename() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);
    AttachmentInfo attachmentInfo = createAttachmentInfo(testStream, null, TEST_CONTENT_TYPE);

    // Then
    assertThat(attachmentInfo.getStream(), is(notNullValue()));
    assertThat(attachmentInfo.getFilename(), is(nullValue()));
    assertThat(attachmentInfo.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testAttachmentInfoWithNullContentType() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);
    AttachmentInfo attachmentInfo = createAttachmentInfo(testStream, TEST_FILENAME, null);

    // Then
    assertThat(attachmentInfo.getStream(), is(notNullValue()));
    assertThat(attachmentInfo.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(attachmentInfo.getContentType(), is(nullValue()));
  }

  @Test
  public void testAttachmentInfoWithEmptyFilename() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);
    AttachmentInfo attachmentInfo = createAttachmentInfo(testStream, "", TEST_CONTENT_TYPE);

    // Then
    assertThat(attachmentInfo.getStream(), is(notNullValue()));
    assertThat(attachmentInfo.getFilename(), is(equalTo("")));
    assertThat(attachmentInfo.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testAttachmentInfoWithEmptyContentType() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);
    AttachmentInfo attachmentInfo = createAttachmentInfo(testStream, TEST_FILENAME, "");

    // Then
    assertThat(attachmentInfo.getStream(), is(notNullValue()));
    assertThat(attachmentInfo.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(attachmentInfo.getContentType(), is(equalTo("")));
  }

  @Test
  public void testAttachmentInfoWithDifferentContentTypes() {
    // Test various content types
    String[] contentTypes = {
      "application/json",
      "text/plain",
      "image/png",
      "application/xml",
      "text/html",
      "application/octet-stream"
    };

    for (String contentType : contentTypes) {
      InputStream testStream = new ByteArrayInputStream(TEST_DATA);
      AttachmentInfo attachmentInfo = createAttachmentInfo(testStream, TEST_FILENAME, contentType);

      assertThat(attachmentInfo.getContentType(), is(equalTo(contentType)));
    }
  }

  @Test
  public void testAttachmentInfoWithDifferentFilenames() {
    // Test various filename patterns
    String[] filenames = {
      "document.pdf",
      "file.txt",
      "image.png",
      "archive.zip",
      "file-with-dashes.xml",
      "file_with_underscores.json",
      "file.with.multiple.dots.csv"
    };

    for (String filename : filenames) {
      InputStream testStream = new ByteArrayInputStream(TEST_DATA);
      AttachmentInfo attachmentInfo = createAttachmentInfo(testStream, filename, TEST_CONTENT_TYPE);

      assertThat(attachmentInfo.getFilename(), is(equalTo(filename)));
    }
  }

  @Test
  public void testAttachmentInfoWithEmptyStream() {
    // Given
    InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
    AttachmentInfo attachmentInfo =
        createAttachmentInfo(emptyStream, TEST_FILENAME, TEST_CONTENT_TYPE);

    // Then
    assertThat(attachmentInfo.getStream(), is(notNullValue()));
    assertThat(attachmentInfo.getStream(), is(emptyStream));
    assertThat(attachmentInfo.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(attachmentInfo.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testAttachmentInfoMultipleAccessToGetters() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);
    AttachmentInfo attachmentInfo =
        createAttachmentInfo(testStream, TEST_FILENAME, TEST_CONTENT_TYPE);

    // Then - verify getters return consistent values on multiple calls
    assertThat(attachmentInfo.getStream(), is(testStream));
    assertThat(attachmentInfo.getStream(), is(testStream));

    assertThat(attachmentInfo.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(attachmentInfo.getFilename(), is(equalTo(TEST_FILENAME)));

    assertThat(attachmentInfo.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
    assertThat(attachmentInfo.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  /** Helper method to create a simple test implementation of AttachmentInfo. */
  private AttachmentInfo createAttachmentInfo(
      final InputStream stream, final String filename, final String contentType) {
    return new AttachmentInfo() {
      @Override
      public InputStream getStream() {
        return stream;
      }

      @Override
      public String getFilename() {
        return filename;
      }

      @Override
      public String getContentType() {
        return contentType;
      }
    };
  }
}
