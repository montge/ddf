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
package org.codice.solr.query;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class SchemaFieldTest {

  @Test
  void testConstructorSetsNameAndType() {
    SchemaField field = new SchemaField("fieldName", "string");

    assertThat(field.getName(), is("fieldName"));
    assertThat(field.getType(), is("string"));
  }

  @Test
  void testConstructorLeavesSuffixNull() {
    SchemaField field = new SchemaField("fieldName", "string");

    assertThat(field.getSuffix(), is(nullValue()));
  }

  @Test
  void testSetName() {
    SchemaField field = new SchemaField("original", "string");

    field.setName("newName");

    assertThat(field.getName(), is("newName"));
  }

  @Test
  void testSetType() {
    SchemaField field = new SchemaField("fieldName", "string");

    field.setType("integer");

    assertThat(field.getType(), is("integer"));
  }

  @Test
  void testSetAndGetSuffix() {
    SchemaField field = new SchemaField("fieldName", "string");

    field.setSuffix("_txt");

    assertThat(field.getSuffix(), is("_txt"));
  }

  @Test
  void testToStringContainsFieldName() {
    SchemaField field = new SchemaField("testField", "text");

    String result = field.toString();

    assertThat(result, containsString("testField"));
  }

  @Test
  void testToStringContainsType() {
    SchemaField field = new SchemaField("testField", "text");

    String result = field.toString();

    assertThat(result, containsString("text"));
  }

  @Test
  void testToStringNotNull() {
    SchemaField field = new SchemaField("testField", "text");

    assertThat(field.toString(), is(notNullValue()));
  }

  @Test
  void testWithNullValues() {
    SchemaField field = new SchemaField(null, null);

    assertThat(field.getName(), is(nullValue()));
    assertThat(field.getType(), is(nullValue()));
  }
}
