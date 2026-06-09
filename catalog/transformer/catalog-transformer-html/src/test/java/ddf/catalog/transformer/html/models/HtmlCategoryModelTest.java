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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.types.Core;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HtmlCategoryModelTest {

  @Test
  void testDefaultConstructor() {
    HtmlCategoryModel model = new HtmlCategoryModel();

    assertThat(model.getTitle(), is(""));
    assertThat(model.getAttributes(), is(empty()));
    assertThat(model.getAttributeMappings(), is(notNullValue()));
  }

  @Test
  void testConstructorWithTitleAndAttributes() {
    List<String> attributes = Arrays.asList("attr1", "attr2");

    HtmlCategoryModel model = new HtmlCategoryModel("Test Title", attributes);

    assertThat(model.getTitle(), is("Test Title"));
    assertThat(model.getAttributes(), hasSize(2));
  }

  @Test
  void testSetAndGetTitle() {
    HtmlCategoryModel model = new HtmlCategoryModel();

    model.setTitle("New Title");

    assertThat(model.getTitle(), is("New Title"));
  }

  @Test
  void testSetAndGetAttributes() {
    HtmlCategoryModel model = new HtmlCategoryModel();
    List<String> attributes = Arrays.asList("attr1", "attr2", "attr3");

    model.setAttributes(attributes);

    assertThat(model.getAttributes(), hasSize(3));
  }

  @Test
  void testApplyAttributeMappingsWithNullAttribute() {
    HtmlCategoryModel model = new HtmlCategoryModel("Test", Arrays.asList("missingAttr"));
    Metacard metacard = mock(Metacard.class);
    when(metacard.getAttribute("missingAttr")).thenReturn(null);

    model.applyAttributeMappings(metacard);

    Map<String, HtmlValueModel> mappings = model.getAttributeMappings();
    assertThat(mappings, hasKey("Missingattr"));
    assertThat(mappings.get("Missingattr"), instanceOf(HtmlEmptyValueModel.class));
  }

  @Test
  void testApplyAttributeMappingsWithBasicAttribute() {
    HtmlCategoryModel model = new HtmlCategoryModel("Test", Arrays.asList("title"));
    Metacard metacard = mock(Metacard.class);
    Attribute attribute = mock(Attribute.class);
    when(metacard.getAttribute("title")).thenReturn(attribute);
    when(attribute.getValue()).thenReturn("Test Value");

    model.applyAttributeMappings(metacard);

    Map<String, HtmlValueModel> mappings = model.getAttributeMappings();
    assertThat(mappings, hasKey("Title"));
    assertThat(mappings.get("Title"), instanceOf(HtmlBasicValueModel.class));
  }

  @Test
  void testApplyAttributeMappingsWithThumbnail() {
    HtmlCategoryModel model = new HtmlCategoryModel("Test", Arrays.asList(Core.THUMBNAIL));
    Metacard metacard = mock(Metacard.class);
    Attribute attribute = mock(Attribute.class);
    when(metacard.getAttribute(Core.THUMBNAIL)).thenReturn(attribute);
    when(attribute.getValue()).thenReturn(new byte[] {1, 2, 3});

    model.applyAttributeMappings(metacard);

    Map<String, HtmlValueModel> mappings = model.getAttributeMappings();
    assertThat(mappings, hasKey("Thumbnail"));
    assertThat(mappings.get("Thumbnail"), instanceOf(HtmlMediaModel.class));
  }

  @Test
  void testHumanReadableAttributeWithDottedName() {
    HtmlCategoryModel model = new HtmlCategoryModel("Test", Arrays.asList("ext.some-attribute"));
    Metacard metacard = mock(Metacard.class);
    when(metacard.getAttribute("ext.some-attribute")).thenReturn(null);

    model.applyAttributeMappings(metacard);

    Map<String, HtmlValueModel> mappings = model.getAttributeMappings();
    assertThat(mappings, hasKey("Some Attribute"));
  }

  @Test
  void testHumanReadableAttributeWithHyphens() {
    HtmlCategoryModel model = new HtmlCategoryModel("Test", Arrays.asList("my-custom-attr"));
    Metacard metacard = mock(Metacard.class);
    when(metacard.getAttribute("my-custom-attr")).thenReturn(null);

    model.applyAttributeMappings(metacard);

    Map<String, HtmlValueModel> mappings = model.getAttributeMappings();
    assertThat(mappings, hasKey("My Custom Attr"));
  }

  @Test
  void testInitDoesNotThrow() {
    HtmlCategoryModel model = new HtmlCategoryModel();

    assertDoesNotThrow(model::init);
  }

  @Test
  void testDestroyDoesNotThrow() {
    HtmlCategoryModel model = new HtmlCategoryModel();

    assertDoesNotThrow(() -> model.destroy(0));
  }

  @Test
  void testApplyAttributeMappingsClearsExisting() {
    HtmlCategoryModel model = new HtmlCategoryModel("Test", Arrays.asList("attr1"));
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    when(metacard1.getAttribute("attr1")).thenReturn(null);
    when(metacard2.getAttribute("attr1")).thenReturn(null);

    model.applyAttributeMappings(metacard1);
    int sizeAfterFirst = model.getAttributeMappings().size();

    model.applyAttributeMappings(metacard2);
    int sizeAfterSecond = model.getAttributeMappings().size();

    assertThat(sizeAfterFirst, is(sizeAfterSecond));
  }

  @Test
  void testSetAttributesWithMap() {
    HtmlCategoryModel model = new HtmlCategoryModel();
    Map<String, HtmlValueModel> mappings =
        Collections.singletonMap("key", new HtmlEmptyValueModel());

    model.setAttributes(mappings);

    assertThat(model.getAttributeMappings(), hasKey("key"));
  }
}
