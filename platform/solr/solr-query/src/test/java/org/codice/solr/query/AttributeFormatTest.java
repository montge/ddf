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
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class AttributeFormatTest {

  @Test
  void testStringValue() {
    assertThat(AttributeFormat.STRING, is(notNullValue()));
    assertThat(AttributeFormat.STRING.name(), is("STRING"));
  }

  @Test
  void testBooleanValue() {
    assertThat(AttributeFormat.BOOLEAN, is(notNullValue()));
    assertThat(AttributeFormat.BOOLEAN.name(), is("BOOLEAN"));
  }

  @Test
  void testDateValue() {
    assertThat(AttributeFormat.DATE, is(notNullValue()));
    assertThat(AttributeFormat.DATE.name(), is("DATE"));
  }

  @Test
  void testShortValue() {
    assertThat(AttributeFormat.SHORT, is(notNullValue()));
    assertThat(AttributeFormat.SHORT.name(), is("SHORT"));
  }

  @Test
  void testIntegerValue() {
    assertThat(AttributeFormat.INTEGER, is(notNullValue()));
    assertThat(AttributeFormat.INTEGER.name(), is("INTEGER"));
  }

  @Test
  void testLongValue() {
    assertThat(AttributeFormat.LONG, is(notNullValue()));
    assertThat(AttributeFormat.LONG.name(), is("LONG"));
  }

  @Test
  void testFloatValue() {
    assertThat(AttributeFormat.FLOAT, is(notNullValue()));
    assertThat(AttributeFormat.FLOAT.name(), is("FLOAT"));
  }

  @Test
  void testDoubleValue() {
    assertThat(AttributeFormat.DOUBLE, is(notNullValue()));
    assertThat(AttributeFormat.DOUBLE.name(), is("DOUBLE"));
  }

  @Test
  void testGeometryValue() {
    assertThat(AttributeFormat.GEOMETRY, is(notNullValue()));
    assertThat(AttributeFormat.GEOMETRY.name(), is("GEOMETRY"));
  }

  @Test
  void testBinaryValue() {
    assertThat(AttributeFormat.BINARY, is(notNullValue()));
    assertThat(AttributeFormat.BINARY.name(), is("BINARY"));
  }

  @Test
  void testXmlValue() {
    assertThat(AttributeFormat.XML, is(notNullValue()));
    assertThat(AttributeFormat.XML.name(), is("XML"));
  }

  @Test
  void testObjectValue() {
    assertThat(AttributeFormat.OBJECT, is(notNullValue()));
    assertThat(AttributeFormat.OBJECT.name(), is("OBJECT"));
  }

  @Test
  void testEnumValueCount() {
    assertThat(AttributeFormat.values().length, is(12));
  }

  @Test
  void testEnumValuesContainsAll() {
    assertThat(
        AttributeFormat.values(),
        arrayContainingInAnyOrder(
            AttributeFormat.STRING,
            AttributeFormat.BOOLEAN,
            AttributeFormat.DATE,
            AttributeFormat.SHORT,
            AttributeFormat.INTEGER,
            AttributeFormat.LONG,
            AttributeFormat.FLOAT,
            AttributeFormat.DOUBLE,
            AttributeFormat.GEOMETRY,
            AttributeFormat.BINARY,
            AttributeFormat.XML,
            AttributeFormat.OBJECT));
  }

  @Test
  void testValueOfString() {
    assertThat(AttributeFormat.valueOf("STRING"), is(AttributeFormat.STRING));
  }

  @Test
  void testValueOfGeometry() {
    assertThat(AttributeFormat.valueOf("GEOMETRY"), is(AttributeFormat.GEOMETRY));
  }
}
