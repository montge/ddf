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
package ddf.catalog.transformer.html.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Date;
import org.junit.jupiter.api.Test;

class HtmlValueModelsTest {

  @Test
  void testHtmlBasicValueModelWithString() {
    HtmlBasicValueModel model = new HtmlBasicValueModel("test value");

    assertThat(model.getValue(), is("test value"));
  }

  @Test
  void testHtmlBasicValueModelWithInteger() {
    HtmlBasicValueModel model = new HtmlBasicValueModel(42);

    assertThat(model.getValue(), is("42"));
  }

  @Test
  void testHtmlBasicValueModelWithDouble() {
    HtmlBasicValueModel model = new HtmlBasicValueModel(3.14159);

    assertThat(model.getValue(), is("3.14159"));
  }

  @Test
  void testHtmlBasicValueModelWithBoolean() {
    HtmlBasicValueModel model = new HtmlBasicValueModel(true);

    assertThat(model.getValue(), is("true"));
  }

  @Test
  void testHtmlBasicValueModelWithDate() {
    Date date = new Date(0);
    HtmlBasicValueModel model = new HtmlBasicValueModel(date);

    assertThat(model.getValue(), is(notNullValue()));
    assertThat(model.getValue(), is(date.toString()));
  }

  @Test
  void testHtmlBasicValueModelWithEmptyString() {
    HtmlBasicValueModel model = new HtmlBasicValueModel("");

    assertThat(model.getValue(), is(""));
  }

  @Test
  void testHtmlEmptyValueModelReturnsPlaceholder() {
    HtmlEmptyValueModel model = new HtmlEmptyValueModel();

    assertThat(model.getValue(), is("--"));
  }

  @Test
  void testHtmlEmptyValueModelRenderTemplate() {
    HtmlEmptyValueModel model = new HtmlEmptyValueModel();

    String rendered = model.renderTemplate();

    assertThat(rendered, is(notNullValue()));
  }

  @Test
  void testHtmlBasicValueModelRenderTemplate() {
    HtmlBasicValueModel model = new HtmlBasicValueModel("test");

    String rendered = model.renderTemplate();

    assertThat(rendered, is(notNullValue()));
  }
}
