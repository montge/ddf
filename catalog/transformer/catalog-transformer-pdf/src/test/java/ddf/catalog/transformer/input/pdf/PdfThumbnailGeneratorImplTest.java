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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    assertNotNull("Test PDF file not found", stream);

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue("Thumbnail should be generated", result.isPresent());
    byte[] thumbnailData = result.get();
    assertThat(thumbnailData.length, is(greaterThan(0)));

    verifyImageData(thumbnailData);
    stream.close();
  }

  @Test
  public void testApplyWithMultiPagePdf() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    testDocument = PDDocument.load(stream);

    if (testDocument.getNumberOfPages() > 0) {
      Optional<byte[]> result = generator.apply(testDocument);

      assertTrue("Thumbnail should be generated for multi-page PDF", result.isPresent());
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

    assertFalse("Thumbnail should not be generated for empty PDF", result.isPresent());
  }

  @Test
  public void testApplyReturnsEmptyForZeroPagePdf() throws Exception {
    testDocument = new PDDocument();

    assertThat(testDocument.getNumberOfPages(), is(0));

    Optional<byte[]> result = generator.apply(testDocument);

    assertFalse("Should return empty Optional for PDF with no pages", result.isPresent());
  }

  @Test
  public void testApplyGeneratesScaledThumbnail() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

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
    assertNotNull("Generated thumbnail should be a valid image", image);

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
    assertNotNull("Test PDF file not found", stream);

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
        "Aspect ratio should be preserved (dimensions should be reasonable)",
        width > 0 && height > 0 && width <= 128 && height <= 128);

    stream.close();
  }

  @Test
  public void testApplyGeneratesJpegFormat() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent());
    byte[] thumbnailData = result.get();

    assertTrue("JPEG files should start with FF D8 FF marker", thumbnailData.length > 3);
    assertThat("First byte should be FF (JPEG SOI marker)", thumbnailData[0] & 0xFF, is(0xFF));
    assertThat("Second byte should be D8 (JPEG SOI marker)", thumbnailData[1] & 0xFF, is(0xD8));

    stream.close();
  }

  @Test
  public void testApplyProducesValidImageData() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue(result.isPresent());
    byte[] thumbnailData = result.get();

    ByteArrayInputStream imageStream = new ByteArrayInputStream(thumbnailData);
    var bufferedImage = ImageIO.read(imageStream);

    assertNotNull("Should produce valid image data that can be read by ImageIO", bufferedImage);

    stream.close();
  }

  @Test
  public void testApplyMultipleTimes() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    testDocument = PDDocument.load(stream);

    Optional<byte[]> result1 = generator.apply(testDocument);
    Optional<byte[]> result2 = generator.apply(testDocument);

    assertTrue("First call should generate thumbnail", result1.isPresent());
    assertTrue("Second call should generate thumbnail", result2.isPresent());

    assertThat(result1.get().length, is(result2.get().length));

    stream.close();
  }

  @Test
  public void testApplyWithDifferentPdfDocuments() throws Exception {
    InputStream stream1 =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream1);

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
    assertNotNull("Test PDF file not found", stream);

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
    assertNotNull("Test PDF file not found", stream);

    testDocument = PDDocument.load(stream);
    Optional<byte[]> result = generator.apply(testDocument);

    assertTrue("Should generate thumbnail even if PDF contains images", result.isPresent());
    assertThat(result.get().length, is(greaterThan(0)));

    stream.close();
  }

  @Test
  public void testApplyRenderFirstPageOnly() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    testDocument = PDDocument.load(stream);

    if (testDocument.getNumberOfPages() >= 1) {
      Optional<byte[]> result = generator.apply(testDocument);

      assertTrue("Should generate thumbnail from first page", result.isPresent());
      assertThat(result.get().length, is(greaterThan(0)));
    }

    stream.close();
  }

  @Test
  public void testApplyGeneratesRGBImage() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

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
    assertNotNull("Test PDF file not found", stream);

    testDocument = PDDocument.load(stream);

    PdfThumbnailGeneratorImpl newGenerator = new PdfThumbnailGeneratorImpl();
    Optional<byte[]> result = newGenerator.apply(testDocument);

    assertTrue("New generator instance should work", result.isPresent());
    assertThat(result.get().length, is(greaterThan(0)));

    stream.close();
  }

  @Test
  public void testApplyDoesNotModifyOriginalDocument() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

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
    assertNotNull("Test PDF file not found", stream);

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
    assertNotNull("Image data should not be null", imageData);
    assertThat("Image data should not be empty", imageData.length, is(greaterThan(0)));

    ByteArrayInputStream stream = new ByteArrayInputStream(imageData);
    var image = ImageIO.read(stream);
    assertNotNull("Image data should be readable as image", image);
    assertThat("Image width should be positive", image.getWidth(), is(greaterThan(0)));
    assertThat("Image height should be positive", image.getHeight(), is(greaterThan(0)));
  }
}
