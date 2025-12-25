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
package ddf.test.catalog.input.transformer.stubs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.Metacard;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class InputTransformerStubTest {

  private static final String TEST_TITLE = "Test Title";
  private static final String TEST_ID = "test-id-123";

  @Test
  void testTransformWithIdSetsTitle() throws IOException, CatalogTransformerException {
    InputTransformerStub transformer = new InputTransformerStub(TEST_TITLE);
    InputStream input = new ByteArrayInputStream("test content".getBytes());

    Metacard metacard = transformer.transform(input, TEST_ID);

    assertThat(metacard.getTitle(), is(TEST_TITLE));
  }

  @Test
  void testTransformWithIdSetsId() throws IOException, CatalogTransformerException {
    InputTransformerStub transformer = new InputTransformerStub(TEST_TITLE);
    InputStream input = new ByteArrayInputStream("test content".getBytes());

    Metacard metacard = transformer.transform(input, TEST_ID);

    assertThat(metacard.getId(), is(TEST_ID));
  }

  @Test
  void testTransformWithIdSetsMetadata() throws IOException, CatalogTransformerException {
    InputTransformerStub transformer = new InputTransformerStub(TEST_TITLE);
    InputStream input = new ByteArrayInputStream("test content".getBytes());

    Metacard metacard = transformer.transform(input, TEST_ID);

    assertThat(metacard.getMetadata(), containsString("<title>" + TEST_TITLE + "</title>"));
    assertThat(metacard.getMetadata(), containsString("<xml>"));
    assertThat(metacard.getMetadata(), containsString("</xml>"));
  }

  @Test
  void testTransformWithNullIdReturnsMetacardWithNullId()
      throws IOException, CatalogTransformerException {
    InputTransformerStub transformer = new InputTransformerStub(TEST_TITLE);
    InputStream input = new ByteArrayInputStream("test content".getBytes());

    Metacard metacard = transformer.transform(input, null);

    assertThat(metacard.getId(), is(nullValue()));
    assertThat(metacard.getTitle(), is(TEST_TITLE));
  }

  @Test
  void testTransformWithoutIdDelegatesToTransformWithNullId()
      throws IOException, CatalogTransformerException {
    InputTransformerStub transformer = new InputTransformerStub(TEST_TITLE);
    InputStream input = new ByteArrayInputStream("test content".getBytes());

    Metacard metacard = transformer.transform(input);

    assertThat(metacard, is(notNullValue()));
    assertThat(metacard.getId(), is(nullValue()));
    assertThat(metacard.getTitle(), is(TEST_TITLE));
  }

  @Test
  void testTransformWithNullInputStream() throws IOException, CatalogTransformerException {
    InputTransformerStub transformer = new InputTransformerStub(TEST_TITLE);

    Metacard metacard = transformer.transform(null, TEST_ID);

    assertThat(metacard, is(notNullValue()));
    assertThat(metacard.getId(), is(TEST_ID));
  }

  @Test
  void testTransformPreservesOriginalTitle() throws IOException, CatalogTransformerException {
    String customTitle = "Custom Document Title";
    InputTransformerStub transformer = new InputTransformerStub(customTitle);
    InputStream input = new ByteArrayInputStream("content".getBytes());

    Metacard metacard = transformer.transform(input, TEST_ID);

    assertThat(metacard.getTitle(), is(customTitle));
    assertThat(metacard.getMetadata(), containsString(customTitle));
  }

  @Test
  void testMetacardIsNotNull() throws IOException, CatalogTransformerException {
    InputTransformerStub transformer = new InputTransformerStub(TEST_TITLE);
    InputStream input = new ByteArrayInputStream("test".getBytes());

    Metacard metacard = transformer.transform(input, TEST_ID);

    assertThat(metacard, is(notNullValue()));
  }
}
