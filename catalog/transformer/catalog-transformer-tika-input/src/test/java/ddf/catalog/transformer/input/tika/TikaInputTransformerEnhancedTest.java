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
package ddf.catalog.transformer.input.tika;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.types.Core;
import ddf.catalog.data.types.experimental.Extracted;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TikaInputTransformerEnhancedTest {

  private TikaInputTransformer tikaInputTransformer;

  @BeforeEach
  public void setup() {
    tikaInputTransformer = new TikaInputTransformer(null, mock(MetacardType.class));
    tikaInputTransformer.setUseResourceTitleAsTitle(true);
  }

  @Test
  public void testTransformNullInputStreamThrowsException() {
    assertThrows(CatalogTransformerException.class, () -> tikaInputTransformer.transform(null));
  }

  @Test
  public void testTransformNullInputStreamWithIdThrowsException() {
    assertThrows(
        CatalogTransformerException.class, () -> tikaInputTransformer.transform(null, "test-id"));
  }

  @Test
  public void testTransformEmptyInputStream() throws IOException, CatalogTransformerException {
    InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
    Metacard metacard = tikaInputTransformer.transform(emptyStream);
    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformCorruptedInputStream() throws IOException, CatalogTransformerException {
    byte[] corruptedData = new byte[] {0, 0, 0, 1, 2, 3, 4, 5};
    InputStream corruptedStream = new ByteArrayInputStream(corruptedData);
    Metacard metacard = tikaInputTransformer.transform(corruptedStream);
    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformPlainTextWithId() throws IOException, CatalogTransformerException {
    String testId = "test-metacard-id-123";
    String content = "This is plain text content for testing.";
    InputStream stream = new ByteArrayInputStream(content.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream, testId);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getId(), is(testId));
    assertThat(
        metacard.getAttribute(Extracted.EXTRACTED_TEXT).getValue().toString(),
        containsString(content));
  }

  @Test
  public void testTransformWithZeroPreviewMaxLength()
      throws IOException, CatalogTransformerException {
    tikaInputTransformer.setPreviewMaxLength(0);
    String content = "This is test content that should be truncated.";
    InputStream stream = new ByteArrayInputStream(content.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    // With previewMaxLength=0, extracted text attribute may be null or empty
    // Just verify the transform completes successfully
  }

  @Test
  public void testTransformWithZeroMetadataMaxLength()
      throws IOException, CatalogTransformerException {
    tikaInputTransformer.setMetadataMaxLength(0);
    String content = "Test content for zero metadata length.";
    InputStream stream = new ByteArrayInputStream(content.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformJsonContent() throws IOException, CatalogTransformerException {
    String jsonContent = "{\"key\": \"value\", \"number\": 123}";
    InputStream stream = new ByteArrayInputStream(jsonContent.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(
        metacard.getAttribute(Extracted.EXTRACTED_TEXT).getValue().toString(),
        containsString("value"));
  }

  @Test
  public void testTransformXmlContent() throws IOException, CatalogTransformerException {
    String xmlContent = "<?xml version=\"1.0\"?><root><element>Test Value</element></root>";
    InputStream stream = new ByteArrayInputStream(xmlContent.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(
        metacard.getAttribute(Extracted.EXTRACTED_TEXT).getValue().toString(),
        containsString("Test Value"));
  }

  @Test
  public void testTransformHtmlContent() throws IOException, CatalogTransformerException {
    String htmlContent =
        "<html><head><title>Test HTML</title></head><body><h1>Hello World</h1></body></html>";
    InputStream stream = new ByteArrayInputStream(htmlContent.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(
        metacard.getAttribute(Extracted.EXTRACTED_TEXT).getValue().toString(),
        containsString("Hello World"));
  }

  @Test
  public void testTransformCsvContent() throws IOException, CatalogTransformerException {
    String csvContent = "Name,Age,City\nJohn,30,New York\nJane,25,Los Angeles";
    InputStream stream = new ByteArrayInputStream(csvContent.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(
        metacard.getAttribute(Extracted.EXTRACTED_TEXT).getValue().toString(),
        containsString("John"));
  }

  @Test
  public void testGetPreviewMaxLength() {
    tikaInputTransformer.setPreviewMaxLength(5000);
    assertThat(tikaInputTransformer.getPreviewMaxLength(), is(5000));
  }

  @Test
  public void testSetPreviewMaxLengthNegative() throws IOException, CatalogTransformerException {
    tikaInputTransformer.setPreviewMaxLength(-1);
    String content = "Test content";
    InputStream stream = new ByteArrayInputStream(content.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testSetMetadataMaxLengthVeryLarge() throws IOException, CatalogTransformerException {
    tikaInputTransformer.setMetadataMaxLength(10000000);
    String content = "Test content with very large metadata length limit.";
    InputStream stream = new ByteArrayInputStream(content.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformWithUseResourceTitleAsTitleDisabled()
      throws IOException, CatalogTransformerException {
    tikaInputTransformer.setUseResourceTitleAsTitle(false);
    String content = "Content without using resource title";
    InputStream stream = new ByteArrayInputStream(content.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformLargeTextFile() throws IOException, CatalogTransformerException {
    StringBuilder largeContent = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      largeContent.append("This is line ").append(i).append(" of the large text file.\n");
    }

    InputStream stream = new ByteArrayInputStream(largeContent.toString().getBytes());
    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getAttribute(Extracted.EXTRACTED_TEXT), notNullValue());
  }

  @Test
  public void testTransformBinaryDataStream() throws IOException, CatalogTransformerException {
    byte[] binaryData = new byte[1024];
    for (int i = 0; i < binaryData.length; i++) {
      binaryData[i] = (byte) (i % 256);
    }

    InputStream stream = new ByteArrayInputStream(binaryData);
    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformWithSpecialCharacters() throws IOException, CatalogTransformerException {
    String content = "Content with special chars: \u00A9 \u00AE \u2122 \u20AC";
    InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformMarkdownContent() throws IOException, CatalogTransformerException {
    String markdownContent =
        "# Heading\n\n## Subheading\n\n- List item 1\n- List item 2\n\n**Bold text**";
    InputStream stream = new ByteArrayInputStream(markdownContent.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(
        metacard.getAttribute(Extracted.EXTRACTED_TEXT).getValue().toString(),
        containsString("Heading"));
  }

  @Test
  public void testTransformRtfContent() throws IOException, CatalogTransformerException {
    String rtfContent =
        "{\\rtf1\\ansi\\deff0 {\\fonttbl {\\f0 Times New Roman;}} \\f0\\fs60 Test RTF Document}";
    InputStream stream = new ByteArrayInputStream(rtfContent.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformEmptyPdfLikeStructure() throws IOException, CatalogTransformerException {
    String fakePdf = "%PDF-1.4\n%%EOF";
    InputStream stream = new ByteArrayInputStream(fakePdf.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testDataTypeDetectionForUnknownFormat()
      throws IOException, CatalogTransformerException {
    byte[] unknownData = {(byte) 0x4D, (byte) 0x5A, (byte) 0x90, 0x00}; // Executable header
    InputStream stream = new ByteArrayInputStream(unknownData);

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getAttribute(Core.DATATYPE), notNullValue());
  }

  @Test
  public void testResourceSizeAttributeSet() throws IOException, CatalogTransformerException {
    String content = "Test content for resource size";
    InputStream stream = new ByteArrayInputStream(content.getBytes());

    Metacard metacard = tikaInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getAttribute(Core.RESOURCE_SIZE), notNullValue());
  }

  @Test
  public void testMultipleTransformationsWithSameTransformer()
      throws IOException, CatalogTransformerException {
    for (int i = 0; i < 5; i++) {
      String content = "Test content iteration " + i;
      InputStream stream = new ByteArrayInputStream(content.getBytes());
      Metacard metacard = tikaInputTransformer.transform(stream);
      assertThat(metacard, notNullValue());
    }
  }
}
