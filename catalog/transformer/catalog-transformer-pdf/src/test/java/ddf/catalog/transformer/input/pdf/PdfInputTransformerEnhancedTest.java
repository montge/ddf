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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import ddf.catalog.content.operation.ContentMetadataExtractor;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.data.types.Contact;
import ddf.catalog.data.types.Core;
import ddf.catalog.data.types.Media;
import ddf.catalog.data.types.Topic;
import ddf.catalog.data.types.Validation;
import ddf.catalog.data.types.experimental.Extracted;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Optional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.ServiceReference;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PdfInputTransformerEnhancedTest {

  private PdfInputTransformer pdfInputTransformer;
  private PDDocument pdDocument;
  private PDDocumentInformation documentInformation;
  private MetacardType metacardType;

  @BeforeEach
  public void setup() {
    metacardType = mock(MetacardTypeImpl.class);
    when(metacardType.getName()).thenReturn("pdf");

    pdDocument = mock(PDDocument.class);
    documentInformation = mock(PDDocumentInformation.class);

    when(pdDocument.getDocumentInformation()).thenReturn(documentInformation);
    when(pdDocument.isEncrypted()).thenReturn(false);

    pdfInputTransformer =
        new PdfInputTransformer(
            metacardType,
            false,
            inputStream -> pdDocument,
            pdDocument1 -> null,
            pdDocument1 -> Optional.empty());
  }

  @Test
  public void testTransformValidPdfWithMetadata() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    String title = "Test PDF Title";
    String author = "Test Author";
    String subject = "Test Subject";
    String keywords = "test, pdf, keywords";

    when(documentInformation.getTitle()).thenReturn(title);
    when(documentInformation.getAuthor()).thenReturn(author);
    when(documentInformation.getSubject()).thenReturn(subject);
    when(documentInformation.getKeywords()).thenReturn(keywords);

    pdfInputTransformer.setUsePdfTitleAsTitle(true);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getTitle(), is(title));
    assertThat(metacard.getAttribute(Contact.CREATOR_NAME).getValue(), is(author));
    assertThat(metacard.getAttribute(Metacard.DESCRIPTION).getValue(), is(subject));
    assertThat(metacard.getAttribute(Topic.KEYWORD).getValue(), is(keywords));
  }

  @Test
  public void testTransformWithCreatedAndModifiedDates()
      throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    Calendar createdDate = Calendar.getInstance();
    createdDate.set(2020, Calendar.JANUARY, 1, 10, 30, 0);

    Calendar modifiedDate = Calendar.getInstance();
    modifiedDate.set(2021, Calendar.DECEMBER, 31, 15, 45, 0);

    when(documentInformation.getCreationDate()).thenReturn(createdDate);
    when(documentInformation.getModificationDate()).thenReturn(modifiedDate);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getCreatedDate(), is(createdDate.getTime()));
    assertThat(metacard.getModifiedDate(), is(modifiedDate.getTime()));
  }

  @Test
  public void testTransformEncryptedPdf() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("encrypted.pdf");

    when(pdDocument.isEncrypted()).thenReturn(true);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getAttribute(Media.TYPE).getValue(), equalTo("application/pdf"));
  }

  @Test
  public void testTransformWithNullTitle() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    when(documentInformation.getTitle()).thenReturn(null);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getTitle(), nullValue());
  }

  @Test
  public void testTransformWithEmptyTitle() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    when(documentInformation.getTitle()).thenReturn("");
    pdfInputTransformer.setUsePdfTitleAsTitle(true);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformWithNullAuthor() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    when(documentInformation.getAuthor()).thenReturn(null);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformWithNullKeywords() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    when(documentInformation.getKeywords()).thenReturn(null);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformWithNullSubject() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    when(documentInformation.getSubject()).thenReturn(null);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformWithNullCreationDate() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    when(documentInformation.getCreationDate()).thenReturn(null);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformWithNullModificationDate()
      throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    when(documentInformation.getModificationDate()).thenReturn(null);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testGetPreviewMaxLength() {
    pdfInputTransformer.setPreviewMaxLength(5000);
    assertThat(pdfInputTransformer.getPreviewMaxLength(), is(5000));
  }

  @Test
  public void testSetPreviewMaxLengthZero() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    pdfInputTransformer.setPreviewMaxLength(0);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testSetMetadataMaxLengthSmall() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    pdfInputTransformer.setMetadataMaxLength(10);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getAttribute(Validation.VALIDATION_WARNINGS), notNullValue());
  }

  @Test
  public void testSetMetadataMaxLengthLarge() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    pdfInputTransformer.setMetadataMaxLength(10000000);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testIsUsePdfTitleAsTitle() {
    pdfInputTransformer.setUsePdfTitleAsTitle(true);
    assertThat(pdfInputTransformer.isUsePdfTitleAsTitle(), is(true));

    pdfInputTransformer.setUsePdfTitleAsTitle(false);
    assertThat(pdfInputTransformer.isUsePdfTitleAsTitle(), is(false));
  }

  @Test
  public void testTransformWithId() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    String testId = "test-pdf-id-123";

    Metacard metacard = pdfInputTransformer.transform(stream, testId);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getId(), is(testId));
  }

  @Test
  public void testTransformPdfWithExtractedText() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    pdfInputTransformer.setPreviewMaxLength(50000);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getAttribute(Extracted.EXTRACTED_TEXT), notNullValue());
  }

  @Test
  public void testTransformWithContentMetadataExtractor()
      throws IOException, CatalogTransformerException {
    ContentMetadataExtractor extractor = mock(ContentMetadataExtractor.class);
    when(extractor.getMetacardAttributes())
        .thenReturn(
            Sets.newHashSet(
                new AttributeDescriptorImpl(
                    "custom-attr", false, false, false, false, BasicTypes.STRING_TYPE)));

    ServiceReference serviceRef = mock(ServiceReference.class);

    pdfInputTransformer =
        new PdfInputTransformer(
            metacardType,
            false,
            inputStream -> pdDocument,
            pdDocument1 -> null,
            pdDocument1 -> Optional.empty()) {
          @Override
          org.osgi.framework.Bundle getBundle() {
            org.osgi.framework.Bundle bundle = mock(org.osgi.framework.Bundle.class);
            org.osgi.framework.BundleContext ctx = mock(org.osgi.framework.BundleContext.class);
            when(bundle.getBundleContext()).thenReturn(ctx);
            when(ctx.getService(serviceRef)).thenReturn(extractor);
            return bundle;
          }
        };

    pdfInputTransformer.addContentMetadataExtractors(serviceRef);

    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testRemoveContentMetadataExtractor() {
    ContentMetadataExtractor extractor = mock(ContentMetadataExtractor.class);
    ServiceReference serviceRef = mock(ServiceReference.class);

    pdfInputTransformer.removeContentMetadataExtractor(serviceRef);
  }

  @Test
  public void testTransformWithWhitespaceTitle() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    when(documentInformation.getTitle()).thenReturn("   ");
    pdfInputTransformer.setUsePdfTitleAsTitle(true);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testTransformWithLongTitle() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    StringBuilder longTitle = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longTitle.append("A very long title ");
    }

    when(documentInformation.getTitle()).thenReturn(longTitle.toString());
    pdfInputTransformer.setUsePdfTitleAsTitle(true);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getTitle(), is(longTitle.toString()));
  }

  @Test
  public void testTransformWithSpecialCharactersInMetadata()
      throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    String specialTitle = "Title with special chars: \u00A9 \u00AE \u2122";
    String specialAuthor = "Author with \u00E9\u00E8\u00EA accents";

    when(documentInformation.getTitle()).thenReturn(specialTitle);
    when(documentInformation.getAuthor()).thenReturn(specialAuthor);
    pdfInputTransformer.setUsePdfTitleAsTitle(true);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getTitle(), is(specialTitle));
    assertThat(metacard.getAttribute(Contact.CREATOR_NAME).getValue(), is(specialAuthor));
  }

  @Test
  public void testTransformDataTypeSet() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getAttribute(Core.DATATYPE).getValue(), equalTo("Text"));
  }

  @Test
  public void testTransformMediaTypeSet() throws IOException, CatalogTransformerException {
    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
    assertThat(metacard.getAttribute(Media.TYPE).getValue(), equalTo("application/pdf"));
  }

  @Test
  public void testTransformCorruptedPdfWithRecovery()
      throws IOException, CatalogTransformerException {
    byte[] corruptedPdf = "%PDF-1.4\n%corrupted data\n%%EOF".getBytes();
    InputStream stream = new ByteArrayInputStream(corruptedPdf);

    Metacard metacard = pdfInputTransformer.transform(stream);

    assertThat(metacard, notNullValue());
  }

  @Test
  public void testMultipleTransformations() throws IOException, CatalogTransformerException {
    for (int i = 0; i < 3; i++) {
      InputStream stream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.pdf");

      Metacard metacard = pdfInputTransformer.transform(stream);

      assertThat(metacard, notNullValue());
    }
  }
}
