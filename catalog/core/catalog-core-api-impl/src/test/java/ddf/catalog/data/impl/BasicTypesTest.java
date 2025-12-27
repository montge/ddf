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
package ddf.catalog.data.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.AttributeType;
import ddf.catalog.data.AttributeType.AttributeFormat;
import java.io.Serializable;
import java.util.Date;
import org.junit.jupiter.api.Test;

class BasicTypesTest {

  @Test
  void testDateTypeNotNull() {
    assertThat(BasicTypes.DATE_TYPE, is(notNullValue()));
  }

  @Test
  void testDateTypeFormat() {
    assertThat(BasicTypes.DATE_TYPE.getAttributeFormat(), is(AttributeFormat.DATE));
  }

  @Test
  void testDateTypeBinding() {
    assertThat(BasicTypes.DATE_TYPE.getBinding(), equalTo(Date.class));
  }

  @Test
  void testStringTypeNotNull() {
    assertThat(BasicTypes.STRING_TYPE, is(notNullValue()));
  }

  @Test
  void testStringTypeFormat() {
    assertThat(BasicTypes.STRING_TYPE.getAttributeFormat(), is(AttributeFormat.STRING));
  }

  @Test
  void testStringTypeBinding() {
    assertThat(BasicTypes.STRING_TYPE.getBinding(), equalTo(String.class));
  }

  @Test
  void testXmlTypeNotNull() {
    assertThat(BasicTypes.XML_TYPE, is(notNullValue()));
  }

  @Test
  void testXmlTypeFormat() {
    assertThat(BasicTypes.XML_TYPE.getAttributeFormat(), is(AttributeFormat.XML));
  }

  @Test
  void testXmlTypeBinding() {
    assertThat(BasicTypes.XML_TYPE.getBinding(), equalTo(String.class));
  }

  @Test
  void testLongTypeNotNull() {
    assertThat(BasicTypes.LONG_TYPE, is(notNullValue()));
  }

  @Test
  void testLongTypeFormat() {
    assertThat(BasicTypes.LONG_TYPE.getAttributeFormat(), is(AttributeFormat.LONG));
  }

  @Test
  void testLongTypeBinding() {
    assertThat(BasicTypes.LONG_TYPE.getBinding(), equalTo(Long.class));
  }

  @Test
  void testBinaryTypeNotNull() {
    assertThat(BasicTypes.BINARY_TYPE, is(notNullValue()));
  }

  @Test
  void testBinaryTypeFormat() {
    assertThat(BasicTypes.BINARY_TYPE.getAttributeFormat(), is(AttributeFormat.BINARY));
  }

  @Test
  void testBinaryTypeBinding() {
    assertThat(BasicTypes.BINARY_TYPE.getBinding(), equalTo(byte[].class));
  }

  @Test
  void testGeoTypeNotNull() {
    assertThat(BasicTypes.GEO_TYPE, is(notNullValue()));
  }

  @Test
  void testGeoTypeFormat() {
    assertThat(BasicTypes.GEO_TYPE.getAttributeFormat(), is(AttributeFormat.GEOMETRY));
  }

  @Test
  void testGeoTypeBinding() {
    assertThat(BasicTypes.GEO_TYPE.getBinding(), equalTo(String.class));
  }

  @Test
  void testBooleanTypeNotNull() {
    assertThat(BasicTypes.BOOLEAN_TYPE, is(notNullValue()));
  }

  @Test
  void testBooleanTypeFormat() {
    assertThat(BasicTypes.BOOLEAN_TYPE.getAttributeFormat(), is(AttributeFormat.BOOLEAN));
  }

  @Test
  void testBooleanTypeBinding() {
    assertThat(BasicTypes.BOOLEAN_TYPE.getBinding(), equalTo(Boolean.class));
  }

  @Test
  void testDoubleTypeNotNull() {
    assertThat(BasicTypes.DOUBLE_TYPE, is(notNullValue()));
  }

  @Test
  void testDoubleTypeFormat() {
    assertThat(BasicTypes.DOUBLE_TYPE.getAttributeFormat(), is(AttributeFormat.DOUBLE));
  }

  @Test
  void testDoubleTypeBinding() {
    assertThat(BasicTypes.DOUBLE_TYPE.getBinding(), equalTo(Double.class));
  }

  @Test
  void testFloatTypeNotNull() {
    assertThat(BasicTypes.FLOAT_TYPE, is(notNullValue()));
  }

  @Test
  void testFloatTypeFormat() {
    assertThat(BasicTypes.FLOAT_TYPE.getAttributeFormat(), is(AttributeFormat.FLOAT));
  }

  @Test
  void testFloatTypeBinding() {
    assertThat(BasicTypes.FLOAT_TYPE.getBinding(), equalTo(Float.class));
  }

  @Test
  void testIntegerTypeNotNull() {
    assertThat(BasicTypes.INTEGER_TYPE, is(notNullValue()));
  }

  @Test
  void testIntegerTypeFormat() {
    assertThat(BasicTypes.INTEGER_TYPE.getAttributeFormat(), is(AttributeFormat.INTEGER));
  }

  @Test
  void testIntegerTypeBinding() {
    assertThat(BasicTypes.INTEGER_TYPE.getBinding(), equalTo(Integer.class));
  }

  @Test
  void testObjectTypeNotNull() {
    assertThat(BasicTypes.OBJECT_TYPE, is(notNullValue()));
  }

  @Test
  void testObjectTypeFormat() {
    assertThat(BasicTypes.OBJECT_TYPE.getAttributeFormat(), is(AttributeFormat.OBJECT));
  }

  @Test
  void testObjectTypeBinding() {
    assertThat(BasicTypes.OBJECT_TYPE.getBinding(), equalTo(Serializable.class));
  }

  @Test
  void testShortTypeNotNull() {
    assertThat(BasicTypes.SHORT_TYPE, is(notNullValue()));
  }

  @Test
  void testShortTypeFormat() {
    assertThat(BasicTypes.SHORT_TYPE.getAttributeFormat(), is(AttributeFormat.SHORT));
  }

  @Test
  void testShortTypeBinding() {
    assertThat(BasicTypes.SHORT_TYPE.getBinding(), equalTo(Short.class));
  }

  @Test
  void testGetAttributeTypeForDate() {
    AttributeType type = BasicTypes.getAttributeType("DATE");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.DATE));
  }

  @Test
  void testGetAttributeTypeForString() {
    AttributeType type = BasicTypes.getAttributeType("STRING");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.STRING));
  }

  @Test
  void testGetAttributeTypeForXml() {
    AttributeType type = BasicTypes.getAttributeType("XML");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.XML));
  }

  @Test
  void testGetAttributeTypeForLong() {
    AttributeType type = BasicTypes.getAttributeType("LONG");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.LONG));
  }

  @Test
  void testGetAttributeTypeForBinary() {
    AttributeType type = BasicTypes.getAttributeType("BINARY");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.BINARY));
  }

  @Test
  void testGetAttributeTypeForGeometry() {
    AttributeType type = BasicTypes.getAttributeType("GEOMETRY");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.GEOMETRY));
  }

  @Test
  void testGetAttributeTypeForBoolean() {
    AttributeType type = BasicTypes.getAttributeType("BOOLEAN");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.BOOLEAN));
  }

  @Test
  void testGetAttributeTypeForDouble() {
    AttributeType type = BasicTypes.getAttributeType("DOUBLE");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.DOUBLE));
  }

  @Test
  void testGetAttributeTypeForFloat() {
    AttributeType type = BasicTypes.getAttributeType("FLOAT");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.FLOAT));
  }

  @Test
  void testGetAttributeTypeForInteger() {
    AttributeType type = BasicTypes.getAttributeType("INTEGER");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.INTEGER));
  }

  @Test
  void testGetAttributeTypeForObject() {
    AttributeType type = BasicTypes.getAttributeType("OBJECT");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.OBJECT));
  }

  @Test
  void testGetAttributeTypeForShort() {
    AttributeType type = BasicTypes.getAttributeType("SHORT");
    assertThat(type, is(notNullValue()));
    assertThat(type.getAttributeFormat(), is(AttributeFormat.SHORT));
  }

  @Test
  void testGetAttributeTypeForUnknownReturnsNull() {
    AttributeType type = BasicTypes.getAttributeType("UNKNOWN");
    assertThat(type, is(nullValue()));
  }

  @Test
  void testGetAttributeTypeForNullReturnsNull() {
    AttributeType type = BasicTypes.getAttributeType(null);
    assertThat(type, is(nullValue()));
  }

  @Test
  void testGetAttributeTypeIsCaseSensitive() {
    AttributeType type = BasicTypes.getAttributeType("date");
    assertThat(type, is(nullValue()));
  }
}
