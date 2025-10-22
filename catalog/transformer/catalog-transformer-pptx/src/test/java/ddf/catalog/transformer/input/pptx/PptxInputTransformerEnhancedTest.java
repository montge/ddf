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
package ddf.catalog.transformer.input.pptx;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.InputTransformer;
import ddf.catalog.transformer.input.tika.TikaInputTransformer;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PptxInputTransformerEnhancedTest {

  private InputTransformer mockInputTransformer;
  private PptxInputTransformer pptxInputTransformer;

  @Before
  public void setUp() {
    mockInputTransformer = mock(InputTransformer.class);
    pptxInputTransformer = new PptxInputTransformer(mockInputTransformer);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullTransformer() {
    new PptxInputTransformer(null);
  }

  @Test(expected = CatalogTransformerException.class)
  public void testTransformNullInputStream() throws IOException, CatalogTransformerException {
    pptxInputTransformer.transform(null);
  }

  @Test(expected = CatalogTransformerException.class)
  public void testTransformNullInputStreamWithId() throws IOException, CatalogTransformerException {
    pptxInputTransformer.transform(null, "test-id");
  }

  @Test
  public void testTransformWithValidPptxAndId() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class), any(String.class)))
        .thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());
    String testId = "test-pptx-id-123";

    Metacard result = pptxInputTransformer.transform(is, testId);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformEmptySlideShow() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
    assertThat(result.getThumbnail(), nullValue());
  }

  @Test
  public void testTransformSlideShowWithOneSlide() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
    assertThat(result.getThumbnail(), notNullValue());
  }

  @Test
  public void testTransformSlideShowWithMultipleSlides()
      throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();
    slideShow.createSlide();
    slideShow.createSlide();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
    assertThat(result.getThumbnail(), notNullValue());
  }

  @Test
  public void testTransformSlideShowWithText() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    XSLFSlide slide = slideShow.createSlide();

    XSLFTextBox textBox = slide.createTextBox();
    XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
    XSLFTextRun run = paragraph.addNewTextRun();
    run.setText("Test Slide Content");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
    assertThat(result.getThumbnail(), notNullValue());
  }

  @Test
  public void testTransformSlideShowWithComplexFormatting()
      throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    XSLFSlide slide = slideShow.createSlide();

    XSLFTextBox textBox = slide.createTextBox();
    XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
    XSLFTextRun run = paragraph.addNewTextRun();
    run.setText("Formatted Text");
    run.setBold(true);
    run.setItalic(true);
    run.setFontColor(Color.BLUE);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
    assertThat(result.getThumbnail(), notNullValue());
  }

  @Test
  public void testTransformWithMetadata() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();

    slideShow.getProperties().getCoreProperties().setTitle("Test Presentation");
    slideShow.getProperties().getCoreProperties().setCreator("Test Creator");
    slideShow.getProperties().getCoreProperties().setSubject("Test Subject");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithCreatedDate() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();

    Date testDate = new Date();
    slideShow.getProperties().getCoreProperties().setCreated(Optional.of(testDate));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithModifiedDate() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();

    Date testDate = new Date();
    slideShow.getProperties().getCoreProperties().setModified(Optional.of(testDate));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformSlideShowWithCustomSize()
      throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    XSLFSlide slide = slideShow.createSlide();

    slideShow.setPageSize(new java.awt.Dimension(800, 600));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformLargePptx() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();

    for (int i = 0; i < 50; i++) {
      XSLFSlide slide = slideShow.createSlide();
      XSLFTextBox textBox = slide.createTextBox();
      XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
      XSLFTextRun run = paragraph.addNewTextRun();
      run.setText("Slide " + (i + 1));
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
    assertThat(result.getThumbnail(), notNullValue());
  }

  @Test(expected = CatalogTransformerException.class)
  public void testTransformWhenInnerTransformerThrowsException()
      throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    when(mockInputTransformer.transform(any(InputStream.class)))
        .thenThrow(new CatalogTransformerException("Test exception"));

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    pptxInputTransformer.transform(is);
  }

  @Test(expected = CatalogTransformerException.class)
  public void testTransformCorruptedPptxStream() throws IOException, CatalogTransformerException {
    byte[] corruptedData = new byte[] {0x50, 0x4B, 0x03, 0x04, 0x00, 0x00};
    InputStream is = new ByteArrayInputStream(corruptedData);

    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(new MetacardImpl());

    pptxInputTransformer.transform(is);
  }

  @Test
  public void testTransformWithRealTikaTransformer()
      throws IOException, CatalogTransformerException {
    TikaInputTransformer realTransformer = new TikaInputTransformer(null, mock(MetacardType.class));
    realTransformer.setUseResourceTitleAsTitle(true);

    PptxInputTransformer transformer = new PptxInputTransformer(realTransformer);

    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();
    slideShow.getProperties().getCoreProperties().setTitle("Real Test");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = transformer.transform(is);

    assertThat(result, notNullValue());
    assertThat(result.getThumbnail(), notNullValue());
  }

  @Test
  public void testTransformSlideShowWithMultipleParagraphs()
      throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    XSLFSlide slide = slideShow.createSlide();

    XSLFTextBox textBox = slide.createTextBox();

    for (int i = 0; i < 5; i++) {
      XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
      XSLFTextRun run = paragraph.addNewTextRun();
      run.setText("Paragraph " + (i + 1));
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithUnicodeCharacters() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    XSLFSlide slide = slideShow.createSlide();

    XSLFTextBox textBox = slide.createTextBox();
    XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
    XSLFTextRun run = paragraph.addNewTextRun();
    run.setText("Unicode: \u00E9\u00E8\u00EA \u4E2D\u6587 \u0410\u0411\u0412");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformMultipleTimes() throws IOException, CatalogTransformerException {
    for (int i = 0; i < 3; i++) {
      XMLSlideShow slideShow = new XMLSlideShow();
      slideShow.createSlide();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      slideShow.write(baos);
      slideShow.close();

      Metacard mockMetacard = new MetacardImpl();
      when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

      InputStream is = new ByteArrayInputStream(baos.toByteArray());

      Metacard result = pptxInputTransformer.transform(is);

      assertThat(result, notNullValue());
    }
  }

  @Test
  public void testTransformSlideShowWithComplexLayout()
      throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    XSLFSlide slide = slideShow.createSlide();

    for (int i = 0; i < 3; i++) {
      XSLFTextBox textBox = slide.createTextBox();
      textBox.setAnchor(new java.awt.Rectangle(100 * i, 100 * i, 200, 100));
      XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
      XSLFTextRun run = paragraph.addNewTextRun();
      run.setText("Text box " + (i + 1));
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithKeywords() throws IOException, CatalogTransformerException {
    XMLSlideShow slideShow = new XMLSlideShow();
    slideShow.createSlide();

    slideShow.getProperties().getCoreProperties().setKeywords("test, pptx, presentation");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    slideShow.write(baos);
    slideShow.close();

    Metacard mockMetacard = new MetacardImpl();
    when(mockInputTransformer.transform(any(InputStream.class))).thenReturn(mockMetacard);

    InputStream is = new ByteArrayInputStream(baos.toByteArray());

    Metacard result = pptxInputTransformer.transform(is);

    assertThat(result, notNullValue());
  }
}
