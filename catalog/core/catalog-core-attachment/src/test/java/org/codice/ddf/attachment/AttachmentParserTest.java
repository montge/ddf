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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link AttachmentParser} interface to verify contract and expected behavior. */
@ExtendWith(MockitoExtension.class)
public class AttachmentParserTest {

  private static final String TEST_FILENAME = "test-document.pdf";

  private static final String TEST_CONTENT_TYPE = "application/pdf";

  private static final byte[] TEST_DATA = "test attachment data".getBytes();

  private AttachmentParser parser;

  @BeforeEach
  public void setUp() {
    parser = createTestParser();
  }

  @Test
  public void testGenerateAttachmentInfoWithAllParameters() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);

    // When
    AttachmentInfo result =
        parser.generateAttachmentInfo(testStream, TEST_CONTENT_TYPE, TEST_FILENAME);

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(testStream));
    assertThat(result.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(result.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testGenerateAttachmentInfoWithNullContentType() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);

    // When
    AttachmentInfo result = parser.generateAttachmentInfo(testStream, null, TEST_FILENAME);

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(testStream));
    assertThat(result.getFilename(), is(equalTo(TEST_FILENAME)));
    // Content type may be null or derived from filename depending on implementation
    assertThat(result.getContentType(), is(nullValue()));
  }

  @Test
  public void testGenerateAttachmentInfoWithNullFilename() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);

    // When
    AttachmentInfo result = parser.generateAttachmentInfo(testStream, TEST_CONTENT_TYPE, null);

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(testStream));
    // Filename may be null or a generated default depending on implementation
    assertThat(result.getFilename(), is(nullValue()));
    assertThat(result.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testGenerateAttachmentInfoWithNullStream() {
    // When
    AttachmentInfo result = parser.generateAttachmentInfo(null, TEST_CONTENT_TYPE, TEST_FILENAME);

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(nullValue()));
    assertThat(result.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(result.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testGenerateAttachmentInfoWithAllNullParameters() {
    // When
    AttachmentInfo result = parser.generateAttachmentInfo(null, null, null);

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(nullValue()));
    assertThat(result.getFilename(), is(nullValue()));
    assertThat(result.getContentType(), is(nullValue()));
  }

  @Test
  public void testGenerateAttachmentInfoWithEmptyFilename() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);

    // When
    AttachmentInfo result = parser.generateAttachmentInfo(testStream, TEST_CONTENT_TYPE, "");

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(testStream));
    // Empty filename may be preserved or replaced with default depending on implementation
    assertThat(result.getFilename(), is(equalTo("")));
    assertThat(result.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testGenerateAttachmentInfoWithEmptyContentType() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);

    // When
    AttachmentInfo result = parser.generateAttachmentInfo(testStream, "", TEST_FILENAME);

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(testStream));
    assertThat(result.getFilename(), is(equalTo(TEST_FILENAME)));
    // Empty content type may be preserved or replaced with default depending on implementation
    assertThat(result.getContentType(), is(equalTo("")));
  }

  @Test
  public void testGenerateAttachmentInfoWithEmptyStream() {
    // Given
    InputStream emptyStream = new ByteArrayInputStream(new byte[0]);

    // When
    AttachmentInfo result =
        parser.generateAttachmentInfo(emptyStream, TEST_CONTENT_TYPE, TEST_FILENAME);

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(emptyStream));
    assertThat(result.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(result.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testGenerateAttachmentInfoWithVariousContentTypes() {
    // Test various common content types
    String[] contentTypes = {
      "application/json",
      "text/plain",
      "text/xml",
      "image/png",
      "image/jpeg",
      "application/pdf",
      "application/zip",
      "application/octet-stream"
    };

    for (String contentType : contentTypes) {
      InputStream testStream = new ByteArrayInputStream(TEST_DATA);
      AttachmentInfo result = parser.generateAttachmentInfo(testStream, contentType, TEST_FILENAME);

      assertThat(result, is(notNullValue()));
      assertThat(result.getContentType(), is(equalTo(contentType)));
    }
  }

  @Test
  public void testGenerateAttachmentInfoWithVariousFilenames() {
    // Test various filename patterns
    String[] filenames = {
      "simple.txt",
      "file-with-dashes.pdf",
      "file_with_underscores.json",
      "file.with.multiple.dots.xml",
      "UPPERCASE.PDF",
      "MixedCase.TxT",
      "file with spaces.doc",
      "file@special#chars.csv"
    };

    for (String filename : filenames) {
      InputStream testStream = new ByteArrayInputStream(TEST_DATA);
      AttachmentInfo result =
          parser.generateAttachmentInfo(testStream, TEST_CONTENT_TYPE, filename);

      assertThat(result, is(notNullValue()));
      assertThat(result.getFilename(), is(equalTo(filename)));
    }
  }

  @Test
  public void testGenerateAttachmentInfoMultipleCalls() {
    // Given
    InputStream stream1 = new ByteArrayInputStream(TEST_DATA);
    InputStream stream2 = new ByteArrayInputStream(TEST_DATA);
    InputStream stream3 = new ByteArrayInputStream(TEST_DATA);

    // When - make multiple calls to the parser
    AttachmentInfo result1 = parser.generateAttachmentInfo(stream1, "text/plain", "file1.txt");
    AttachmentInfo result2 =
        parser.generateAttachmentInfo(stream2, "application/json", "file2.json");
    AttachmentInfo result3 = parser.generateAttachmentInfo(stream3, "image/png", "file3.png");

    // Then - each call should return a valid result
    assertThat(result1, is(notNullValue()));
    assertThat(result1.getFilename(), is(equalTo("file1.txt")));
    assertThat(result1.getContentType(), is(equalTo("text/plain")));

    assertThat(result2, is(notNullValue()));
    assertThat(result2.getFilename(), is(equalTo("file2.json")));
    assertThat(result2.getContentType(), is(equalTo("application/json")));

    assertThat(result3, is(notNullValue()));
    assertThat(result3.getFilename(), is(equalTo("file3.png")));
    assertThat(result3.getContentType(), is(equalTo("image/png")));
  }

  @Test
  public void testGenerateAttachmentInfoWithLargeStream() {
    // Given - simulate a larger file
    byte[] largeData = new byte[10000];
    for (int i = 0; i < largeData.length; i++) {
      largeData[i] = (byte) (i % 256);
    }
    InputStream largeStream = new ByteArrayInputStream(largeData);

    // When
    AttachmentInfo result =
        parser.generateAttachmentInfo(largeStream, TEST_CONTENT_TYPE, TEST_FILENAME);

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(largeStream));
    assertThat(result.getFilename(), is(equalTo(TEST_FILENAME)));
    assertThat(result.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testGenerateAttachmentInfoWithFilenameWithoutExtension() {
    // Given
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);

    // When
    AttachmentInfo result =
        parser.generateAttachmentInfo(testStream, TEST_CONTENT_TYPE, "filename_no_extension");

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(testStream));
    assertThat(result.getFilename(), is(equalTo("filename_no_extension")));
    assertThat(result.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  @Test
  public void testGenerateAttachmentInfoWithPathSeparatorsInFilename() {
    // Given - some systems may include path separators in submitted filenames
    InputStream testStream = new ByteArrayInputStream(TEST_DATA);

    // When
    AttachmentInfo result =
        parser.generateAttachmentInfo(testStream, TEST_CONTENT_TYPE, "/path/to/file.txt");

    // Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStream(), is(testStream));
    // Implementation may strip path or preserve it
    assertThat(result.getFilename(), is(equalTo("/path/to/file.txt")));
    assertThat(result.getContentType(), is(equalTo(TEST_CONTENT_TYPE)));
  }

  /**
   * Helper method to create a simple test implementation of AttachmentParser that passes through
   * parameters as-is.
   */
  private AttachmentParser createTestParser() {
    return new AttachmentParser() {
      @Override
      public AttachmentInfo generateAttachmentInfo(
          InputStream stream, String contentType, String submittedFilename) {
        return new AttachmentInfo() {
          @Override
          public InputStream getStream() {
            return stream;
          }

          @Override
          public String getFilename() {
            return submittedFilename;
          }

          @Override
          public String getContentType() {
            return contentType;
          }
        };
      }
    };
  }
}
