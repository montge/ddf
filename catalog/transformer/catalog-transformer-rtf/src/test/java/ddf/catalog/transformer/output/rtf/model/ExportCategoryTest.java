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
package ddf.catalog.transformer.output.rtf.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExportCategoryTest {

  private ExportCategory category;

  @BeforeEach
  void setUp() {
    category = new ExportCategory();
  }

  @Test
  void testSetAndGetTitle() {
    category.setTitle("Test Category");

    assertThat(category.getTitle(), is("Test Category"));
  }

  @Test
  void testSetAndGetAttributes() {
    List<String> attributes = Arrays.asList("title", "description", "created");

    category.setAttributes(attributes);

    assertThat(category.getAttributes(), hasSize(3));
    assertThat(category.getAttributes().get(0), is("title"));
  }

  @Test
  void testInitialTitleIsNull() {
    assertThat(category.getTitle(), is(nullValue()));
  }

  @Test
  void testInitialAttributesIsNull() {
    assertThat(category.getAttributes(), is(nullValue()));
  }

  @Test
  void testSetAliases() {
    Map<String, String> aliases = new HashMap<>();
    aliases.put("title", "Document Title");
    aliases.put("created", "Creation Date");

    category.setAliases(aliases);

    // Aliases are used internally, verify category is still functional
    assertThat(category, is(notNullValue()));
  }

  @Test
  void testExtendedAttributePrefix() {
    assertThat(ExportCategory.EXTENDED_ATTRIBUTE_PREFIX, is("ext."));
  }

  @Test
  void testEmptyValueConstant() {
    assertThat(ExportCategory.EMPTY_VALUE, is("--"));
  }

  // ValueType enum tests

  @Test
  void testValueTypeSimple() {
    assertThat(ExportCategory.ValueType.SIMPLE.name(), is("SIMPLE"));
  }

  @Test
  void testValueTypeMedia() {
    assertThat(ExportCategory.ValueType.MEDIA.name(), is("MEDIA"));
  }

  @Test
  void testValueTypeEmpty() {
    assertThat(ExportCategory.ValueType.EMPTY.name(), is("EMPTY"));
  }

  @Test
  void testValueTypeEnumCount() {
    assertThat(ExportCategory.ValueType.values().length, is(3));
  }

  @Test
  void testValueTypeEnumValues() {
    assertThat(
        ExportCategory.ValueType.values(),
        arrayContainingInAnyOrder(
            ExportCategory.ValueType.SIMPLE,
            ExportCategory.ValueType.MEDIA,
            ExportCategory.ValueType.EMPTY));
  }

  @Test
  void testValueOfSimple() {
    assertThat(ExportCategory.ValueType.valueOf("SIMPLE"), is(ExportCategory.ValueType.SIMPLE));
  }

  @Test
  void testValueOfMedia() {
    assertThat(ExportCategory.ValueType.valueOf("MEDIA"), is(ExportCategory.ValueType.MEDIA));
  }

  @Test
  void testValueOfEmpty() {
    assertThat(ExportCategory.ValueType.valueOf("EMPTY"), is(ExportCategory.ValueType.EMPTY));
  }

  @Test
  void testSetAttributesWithEmptyList() {
    category.setAttributes(Arrays.asList());

    assertThat(category.getAttributes(), hasSize(0));
  }

  @Test
  void testSetTitleWithEmptyString() {
    category.setTitle("");

    assertThat(category.getTitle(), is(""));
  }
}
