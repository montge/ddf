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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.types.Core;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.InputTransformer;
import ddf.catalog.transformer.input.tika.TikaInputTransformer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class PptxInputTransformerTest {

  private final InputTransformer inputTransformer =
      new TikaInputTransformer(null, mock(MetacardType.class));

  private InputStream getResource(String resourceName) {
    return PptxInputTransformerTest.class.getResourceAsStream(resourceName);
  }

  @Test
  public void testBadCopy() throws IOException {
    IOException ioe = new IOException();
    PptxInputTransformer t = new PptxInputTransformer(inputTransformer);
    InputStream is = mock(InputStream.class);
    when(is.read(any())).thenThrow(ioe);

    CatalogTransformerException e =
        assertThrows(CatalogTransformerException.class, () -> t.transform(is));
    assertThat(e.getCause(), is(ioe));
  }

  @Test
  public void testTransformNullInput() {
    PptxInputTransformer t = new PptxInputTransformer(inputTransformer);
    assertThrows(CatalogTransformerException.class, () -> t.transform(null));
  }

  @Test
  public void testPasswordProtected() throws IOException, CatalogTransformerException {
    PptxInputTransformer t = new PptxInputTransformer(inputTransformer);
    try (InputStream is = getResource("/password-powerpoint.pptx")) {
      Metacard metacard = t.transform(is);
      assertThat(metacard, notNullValue());
      assertThat(metacard.getThumbnail(), nullValue());
      MatcherAssert.assertThat(
          metacard.getAttribute(Core.DATATYPE).getValue(), Matchers.is("Text"));
    }
  }

  @Test
  public void testOle2() throws IOException, CatalogTransformerException {
    PptxInputTransformer t = new PptxInputTransformer(inputTransformer);
    try (InputStream is = getResource("/empty.ppt")) {
      Metacard mc = t.transform(is);
      assertThat(mc.getThumbnail(), is(nullValue()));
    }
  }

  @Test
  public void testThumbnailWithEmptySlideShow()
      throws IOException, CatalogTransformerException, InterruptedException {

    try (XMLSlideShow ss = new XMLSlideShow()) {
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        ss.write(os);

        try (ByteArrayInputStream inStr = new ByteArrayInputStream(os.toByteArray())) {
          PptxInputTransformer t = new PptxInputTransformer(inputTransformer);
          Metacard m = t.transform(inStr);
          assertThat(m.getThumbnail(), is(nullValue()));
        }
      }
    }
  }

  @Test
  public void testWithTitle()
      throws IOException, CatalogTransformerException, InterruptedException {

    try (XMLSlideShow ss = new XMLSlideShow()) {
      ss.createSlide();
      ss.getProperties().getCoreProperties().setTitle("TheTitle");
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        ss.write(os);

        try (ByteArrayInputStream inStr = new ByteArrayInputStream(os.toByteArray())) {
          TikaInputTransformer realTransformer =
              new TikaInputTransformer(null, mock(MetacardType.class));
          realTransformer.setUseResourceTitleAsTitle(true);
          PptxInputTransformer t = new PptxInputTransformer(realTransformer);
          Metacard m = t.transform(inStr);
          assertThat(m.getTitle(), is("TheTitle"));
        }
      }
    }
  }

  @Test
  public void testTitleAsMetadataTitle()
      throws IOException, CatalogTransformerException, InterruptedException {

    try (XMLSlideShow ss = new XMLSlideShow()) {
      ss.createSlide();
      ss.getProperties().getCoreProperties().setTitle("TheTitle");
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        ss.write(os);

        try (ByteArrayInputStream inStr = new ByteArrayInputStream(os.toByteArray())) {
          TikaInputTransformer realTransformer =
              new TikaInputTransformer(null, mock(MetacardType.class));
          realTransformer.setUseResourceTitleAsTitle(false);
          PptxInputTransformer t = new PptxInputTransformer(realTransformer);
          Metacard m = t.transform(inStr);
          assertThat(m.getTitle(), nullValue());
        }
      }
    }
  }

  private Date createOneSecondPrecisionDate() {
    return new Date(
        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(new Date().getTime())));
  }

  @Test
  public void testCreatedDate()
      throws IOException, CatalogTransformerException, InterruptedException {

    try (XMLSlideShow ss = new XMLSlideShow()) {
      ss.createSlide();
      Date d = createOneSecondPrecisionDate();

      ss.getProperties().getCoreProperties().setCreated(Optional.ofNullable(d));
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        ss.write(os);

        try (ByteArrayInputStream inStr = new ByteArrayInputStream(os.toByteArray())) {
          PptxInputTransformer t = new PptxInputTransformer(inputTransformer);
          Metacard m = t.transform(inStr);
          assertThat(m.getCreatedDate().getTime(), is(d.getTime()));
        }
      }
    }
  }

  @Test
  public void testModifiedDate()
      throws IOException, CatalogTransformerException, InterruptedException {

    try (XMLSlideShow ss = new XMLSlideShow()) {
      ss.createSlide();
      Date d = createOneSecondPrecisionDate();

      ss.getProperties().getCoreProperties().setModified(Optional.ofNullable(d));
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        ss.write(os);

        try (ByteArrayInputStream inStr = new ByteArrayInputStream(os.toByteArray())) {
          PptxInputTransformer t = new PptxInputTransformer(inputTransformer);
          Metacard m = t.transform(inStr);
          assertThat(m.getModifiedDate().getTime(), is(d.getTime()));
        }
      }
    }
  }
}
