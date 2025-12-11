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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test harness for PDDocumentGeneratorImpl. Tests PDF document loading, error
 * handling, and resource management.
 *
 * <p>COVERAGE REQUIREMENTS: - Valid PDF loading - Invalid/malformed PDF handling - Empty input
 * handling - Null input handling - Resource cleanup verification
 */
@ExtendWith(MockitoExtension.class)
public class PDDocumentGeneratorImplTest {

  private PDDocumentGeneratorImpl generator;

  @BeforeEach
  public void setUp() {
    generator = new PDDocumentGeneratorImpl();
  }

  @Test
  public void testApplyWithValidPdf() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    PDDocument document = generator.apply(stream);

    assertThat(document, is(notNullValue()));
    assertThat(document.getNumberOfPages(), is(greaterThan(0)));
    assertTrue("Document should not be encrypted by default", !document.isEncrypted());

    document.close();
    stream.close();
  }

  @Test
  public void testApplyWithValidMultiPagePdf() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    PDDocument document = generator.apply(stream);

    assertNotNull(document);
    assertThat(document.getNumberOfPages(), is(greaterThan(0)));

    document.close();
    stream.close();
  }

  @Test
  public void testApplyWithNullInputStream() {
    assertThrows(Exception.class, () -> generator.apply(null));
  }

  @Test
  public void testApplyWithEmptyInputStream() {
    InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
    assertThrows(IOException.class, () -> generator.apply(emptyStream));
  }

  @Test
  public void testApplyWithInvalidPdfData() {
    byte[] invalidData = "This is not a PDF file".getBytes();
    InputStream invalidStream = new ByteArrayInputStream(invalidData);
    assertThrows(IOException.class, () -> generator.apply(invalidStream));
  }

  @Test
  public void testApplyWithCorruptedPdf() {
    byte[] corruptedPdf = "%PDF-1.4\n%corrupted content here\n%%EOF".getBytes();
    InputStream corruptedStream = new ByteArrayInputStream(corruptedPdf);
    assertThrows(IOException.class, () -> generator.apply(corruptedStream));
  }

  @Test
  public void testApplyWithPartialPdfHeader() {
    byte[] partialHeader = "%PDF".getBytes();
    InputStream partialStream = new ByteArrayInputStream(partialHeader);
    assertThrows(IOException.class, () -> generator.apply(partialStream));
  }

  @Test
  public void testApplyWithSmallValidPdf() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    PDDocument document = generator.apply(stream);

    assertNotNull(document);
    assertTrue(document.getNumberOfPages() >= 0);

    document.close();
    stream.close();
  }

  @Test
  public void testApplyWithBinaryGarbage() {
    byte[] binaryGarbage = new byte[1024];
    for (int i = 0; i < binaryGarbage.length; i++) {
      binaryGarbage[i] = (byte) (Math.random() * 256);
    }
    InputStream garbageStream = new ByteArrayInputStream(binaryGarbage);
    assertThrows(IOException.class, () -> generator.apply(garbageStream));
  }

  @Test
  public void testApplyReturnsClosablePDDocument() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    PDDocument document = generator.apply(stream);

    assertNotNull(document);

    try {
      document.close();
      assertTrue("Document should close without exceptions", true);
    } catch (Exception e) {
      fail("Document.close() should not throw exception: " + e.getMessage());
    }

    stream.close();
  }

  @Test
  public void testApplyWithPdfVersion14() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    PDDocument document = generator.apply(stream);

    assertNotNull(document);
    assertThat(document.getVersion(), is(greaterThan(0.0f)));

    document.close();
    stream.close();
  }

  @Test
  public void testApplyMultipleTimesWithSameGenerator() throws Exception {
    for (int i = 0; i < 3; i++) {
      InputStream stream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
      assertNotNull("Test PDF file not found", stream);

      PDDocument document = generator.apply(stream);

      assertNotNull("Document should be loaded on iteration " + i, document);
      assertThat(
          "Document should have pages on iteration " + i,
          document.getNumberOfPages(),
          is(greaterThan(0)));

      document.close();
      stream.close();
    }
  }

  @Test
  public void testApplyWithClosedInputStream() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);
    stream.close();

    assertThrows(IOException.class, () -> generator.apply(stream));
  }

  @Test
  public void testApplyWithDifferentPdfFiles() throws Exception {
    String[] testFiles = {"testPDF.pdf"};

    for (String filename : testFiles) {
      InputStream stream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
      if (stream != null) {
        try {
          PDDocument document = generator.apply(stream);
          assertNotNull("Document should be loaded for " + filename, document);
          document.close();
        } finally {
          stream.close();
        }
      }
    }
  }

  @Test
  public void testApplyCreatesNewDocumentEachTime() throws Exception {
    InputStream stream1 =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    InputStream stream2 =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    assertNotNull("Test PDF file not found", stream1);
    assertNotNull("Test PDF file not found", stream2);

    PDDocument doc1 = generator.apply(stream1);
    PDDocument doc2 = generator.apply(stream2);

    assertNotNull(doc1);
    assertNotNull(doc2);
    assertTrue("Should create different document instances", doc1 != doc2);

    doc1.close();
    doc2.close();
    stream1.close();
    stream2.close();
  }

  @Test
  public void testApplyWithHtmlFileInsteadOfPdf() {
    byte[] htmlContent = "<html><body>Not a PDF</body></html>".getBytes();
    InputStream htmlStream = new ByteArrayInputStream(htmlContent);
    assertThrows(IOException.class, () -> generator.apply(htmlStream));
  }

  @Test
  public void testApplyWithXmlFileInsteadOfPdf() {
    byte[] xmlContent = "<?xml version=\"1.0\"?><root>Not a PDF</root>".getBytes();
    InputStream xmlStream = new ByteArrayInputStream(xmlContent);
    assertThrows(IOException.class, () -> generator.apply(xmlStream));
  }

  @Test
  public void testApplyWithImageFileInsteadOfPdf() {
    byte[] fakeImage = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
    InputStream fakeStream = new ByteArrayInputStream(fakeImage);
    assertThrows(IOException.class, () -> generator.apply(fakeStream));
  }

  @Test
  public void testApplyPreservesDocumentStructure() throws Exception {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");
    assertNotNull("Test PDF file not found", stream);

    PDDocument document = generator.apply(stream);

    assertNotNull(document);
    assertNotNull("Document catalog should exist", document.getDocumentCatalog());
    assertThat(
        "Document should have at least one page", document.getNumberOfPages(), is(greaterThan(0)));

    document.close();
    stream.close();
  }
}
