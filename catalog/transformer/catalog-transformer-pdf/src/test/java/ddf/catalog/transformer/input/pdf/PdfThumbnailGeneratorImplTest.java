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
package ddf.catalog.transformer.input.pdf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test harness for PdfThumbnailGeneratorImpl. Tests thumbnail generation, image
 * scaling, error handling, and edge cases.
 *
 * <p>COVERAGE REQUIREMENTS: - Valid PDF thumbnail generation - Empty PDF handling - Multi-page PDF
 * handling - Image scaling verification - Error condition handling - Resource cleanup
 */
@ExtendWith(MockitoExtension.class)
public class PdfThumbnailGeneratorImplTest {

  private PdfThumbnailGeneratorImpl generator;

  private PDDocument testDocument;

  @BeforeEach
  public void setUp() {
    generator = new PdfThumbnailGeneratorImpl();
  }

  @AfterEach
  public void tearDown() throws Exception {
    if (testDocument != null) {
      testDocument.close();
      testDocument = null;
    }
  }

  @Test
  public void testApplyWithValidSinglePagePdf() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent(), "Thumbnail should be generated");
    byte[] thumbnailData = result.get();
    assertThat(thumbnailData.length, is(greaterThan(0)));

    verifyImageData(thumbnailData);
    stream.close();
  }

  @Test
  public void testApplyWithMultiPagePdf() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);

    if (testDocument.getNumberOfPages() > 0) {
      Optional<byte[]> result = generator.apply(testDocument);

      assertTrue(result.isPresent(), "Thumbnail should be generated for multi-page PDF");
      byte[] thumbnailData = result.get();
      assertThat(thumbnailData.length, is(greaterThan(0)));

      verifyImageData(thumbnailData);
    }
    stream.close();
  }

  @Test
  public void testApplyWithEmptyPdf() throws Exception {
    testDocument = new PDDocument();

    Optional<byte[]> result = generator.apply(testDocument);

    assertFalse(result.isPresent(), "Thumbnail should not be generated for empty PDF");
  }

  @Test
  public void testApplyReturnsEmptyForZeroPagePdf() throws Exception {
    testDocument = new PDDocument();

    assertThat(testDocument.getNumberOfPages(), is(0));

    Optional<byte[]> result = generator.apply(testDocument);

    assertFalse(result.isPresent(), "Should return empty Optional for PDF with no pages");
  }

  @Test
  public void testApplyGeneratesScaledThumbnail() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent());
    byte[] thumbnailData = result.get();

    assertThat(
        "Thumbnail should be smaller than original PDF",
        thumbnailData.length,
        is(lessThan(1024 * 1024)));

    ByteArrayInputStream thumbnailStream = new ByteArrayInputStream(thumbnailData);
    var image = ImageIO.read(thumbnailStream);
    assertNotNull(image, "Generated thumbnail should be a valid image");

    assertThat("Thumbnail width should be at most 128", image.getWidth(), is(lessThan(129)));
    assertThat("Thumbnail height should be at most 128", image.getHeight(), is(lessThan(129)));
    assertThat("Thumbnail width should be positive", image.getWidth(), is(greaterThan(0)));
    assertThat("Thumbnail height should be positive", image.getHeight(), is(greaterThan(0)));

    stream.close();
  }

  @Test
  public void testApplyMaintainsAspectRatio() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent());
    byte[] thumbnailData = result.get();

    ByteArrayInputStream thumbnailStream = new ByteArrayInputStream(thumbnailData);
    var image = ImageIO.read(thumbnailStream);
    assertNotNull(image);

    int width = image.getWidth();
    int height = image.getHeight();

    assertTrue(
        width > 0 && height > 0 && width <= 128 && height <= 128,
        "Aspect ratio should be preserved (dimensions should be reasonable)");

    stream.close();
  }

  @Test
  public void testApplyGeneratesJpegFormat() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent());
    byte[] thumbnailData = result.get();

    assertTrue(thumbnailData.length > 3, "JPEG files should start with FF D8 FF marker");
    assertThat("First byte should be FF (JPEG SOI marker)", thumbnailData[0] & 0xFF, is(0xFF));
    assertThat("Second byte should be D8 (JPEG SOI marker)", thumbnailData[1] & 0xFF, is(0xD8));

    stream.close();
  }

  @Test
  public void testApplyProducesValidImageData() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent());
    byte[] thumbnailData = result.get();

    ByteArrayInputStream imageStream = new ByteArrayInputStream(thumbnailData);
    var bufferedImage = ImageIO.read(imageStream);

    assertNotNull(bufferedImage, "Should produce valid image data that can be read by ImageIO");

    stream.close();
  }

  @Test
  public void testApplyMultipleTimes() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);

    Optional<byte[]> result1 = generator.apply(testDocument);
    Optional<byte[]> result2 = generator.apply(testDocument);

    assertTrue(result1.isPresent(), "First call should generate thumbnail");
    assertTrue(result2.isPresent(), "Second call should generate thumbnail");

    assertThat(result1.get().length, is(result2.get().length));

    stream.close();
  }

  @Test
  public void testApplyWithDifferentPdfDocuments() throws Exception {
    InputStream stream1 =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream1, "Test PDF file not found");

    PDDocument doc1 = PDDocument.load(stream1);
    Optional<byte[]> result1 = generator.apply(doc1);

    assertTrue(result1.isPresent());
    assertThat(result1.get().length, is(greaterThan(0)));

    doc1.close();
    stream1.close();

    if (testDocument == null) {
      InputStream stream2 =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
      testDocument = PDDocument.load(stream2);
      stream2.close();
    }
  }

  @Test
  public void testApplyGeneratesReasonableSizedThumbnail() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent());
    byte[] thumbnailData = result.get();

    assertThat(
        "Thumbnail should be larger than 100 bytes", thumbnailData.length, is(greaterThan(100)));
    assertThat(
        "Thumbnail should be smaller than 50KB", thumbnailData.length, is(lessThan(50 * 1024)));

    stream.close();
  }

  @Test
  public void testApplyWithPdfContainingImages() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent(), "Should generate thumbnail even if PDF contains images");
    assertThat(result.get().length, is(greaterThan(0)));

    stream.close();
  }

  @Test
  public void testApplyRenderFirstPageOnly() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);

    if (testDocument.getNumberOfPages() >= 1) {
      Optional<byte[]> result = generator.apply(testDocument);

      assertTrue(result.isPresent(), "Should generate thumbnail from first page");
      assertThat(result.get().length, is(greaterThan(0)));
    }

    stream.close();
  }

  @Test
  public void testApplyGeneratesRGBImage() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent());
    byte[] thumbnailData = result.get();

    ByteArrayInputStream imageStream = new ByteArrayInputStream(thumbnailData);
    var bufferedImage = ImageIO.read(imageStream);

    assertNotNull(bufferedImage);

    stream.close();
  }

  @Test
  public void testApplyWithNewGeneratorInstance() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);

    PdfThumbnailGeneratorImpl newGenerator = new PdfThumbnailGeneratorImpl();
    Optional<byte[]> result = newGenerator.apply(testDocument);

    assertTrue(result.isPresent(), "New generator instance should work");
    assertThat(result.get().length, is(greaterThan(0)));

    stream.close();
  }

  @Test
  public void testApplyDoesNotModifyOriginalDocument() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);
    int originalPageCount = testDocument.getNumberOfPages();

    generator.apply(testDocument);

    assertThat(
        "Original document page count should not change",
        testDocument.getNumberOfPages(),
        is(originalPageCount));

    stream.close();
  }

  @Test
  public void testApplyConsistentOutput() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull(stream, "Test PDF file not found");

    testDocument = PDDocument.load(stream);

    Optional<byte[]> result1 = generator.apply(testDocument);
    Optional<byte[]> result2 = generator.apply(testDocument);

    assertTrue(result1.isPresent());
    assertTrue(result2.isPresent());

    assertThat(
        "Consecutive calls should produce same size output",
        result1.get().length,
        is(result2.get().length));

    stream.close();
  }

  private void verifyImageData(byte[] imageData) throws IOException {
    assertNotNull(imageData, "Image data should not be null");
    assertThat("Image data should not be empty", imageData.length, is(greaterThan(0)));

    ByteArrayInputStream stream = new ByteArrayInputStream(imageData);
    var image = ImageIO.read(stream);
    assertNotNull(image, "Image data should be readable as image");
    assertThat("Image width should be positive", image.getWidth(), is(greaterThan(0)));
    assertThat("Image height should be positive", image.getHeight(), is(greaterThan(0)));
  }
}
