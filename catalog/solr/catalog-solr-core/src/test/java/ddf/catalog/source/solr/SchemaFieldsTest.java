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
package ddf.catalog.source.solr;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.AttributeType.AttributeFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchemaFieldsTest {

  private SchemaFields schemaFields;

  @BeforeEach
  void setUp() {
    schemaFields = new SchemaFields();
  }

  // Suffix constant tests

  @Test
  void testObjectSuffix() {
    assertThat(SchemaFields.OBJECT_SUFFIX, is("_obj"));
  }

  @Test
  void testLongSuffix() {
    assertThat(SchemaFields.LONG_SUFFIX, is("_lng"));
  }

  @Test
  void testIntegerSuffix() {
    assertThat(SchemaFields.INTEGER_SUFFIX, is("_int"));
  }

  @Test
  void testShortSuffix() {
    assertThat(SchemaFields.SHORT_SUFFIX, is("_shr"));
  }

  @Test
  void testFloatSuffix() {
    assertThat(SchemaFields.FLOAT_SUFFIX, is("_flt"));
  }

  @Test
  void testDoubleSuffix() {
    assertThat(SchemaFields.DOUBLE_SUFFIX, is("_dbl"));
  }

  @Test
  void testBooleanSuffix() {
    assertThat(SchemaFields.BOOLEAN_SUFFIX, is("_bln"));
  }

  @Test
  void testGeoSuffix() {
    assertThat(SchemaFields.GEO_SUFFIX, is("_geo"));
  }

  @Test
  void testTextSuffix() {
    assertThat(SchemaFields.TEXT_SUFFIX, is("_txt"));
  }

  @Test
  void testXmlSuffix() {
    assertThat(SchemaFields.XML_SUFFIX, is("_xml"));
  }

  @Test
  void testDateSuffix() {
    assertThat(SchemaFields.DATE_SUFFIX, is("_tdt"));
  }

  @Test
  void testBinarySuffix() {
    assertThat(SchemaFields.BINARY_SUFFIX, is("_bin"));
  }

  @Test
  void testTokenizedSuffix() {
    assertThat(SchemaFields.TOKENIZED, is("_tokenized"));
  }

  @Test
  void testPhoneticsSuffix() {
    assertThat(SchemaFields.PHONETICS, is("_phonetics"));
  }

  @Test
  void testHasCaseSuffix() {
    assertThat(SchemaFields.HAS_CASE, is("_has_case"));
  }

  @Test
  void testTextPathSuffix() {
    assertThat(SchemaFields.TEXT_PATH, is("_tpt"));
  }

  @Test
  void testIndexedSuffix() {
    assertThat(SchemaFields.INDEXED, is("_index"));
  }

  @Test
  void testSortSuffix() {
    assertThat(SchemaFields.SORT_SUFFIX, is("_sort"));
  }

  @Test
  void testMetacardTypeFieldName() {
    assertThat(SchemaFields.METACARD_TYPE_FIELD_NAME, is("metacard_type_name_txt"));
  }

  @Test
  void testMetacardTypeObjectFieldName() {
    assertThat(SchemaFields.METACARD_TYPE_OBJECT_FIELD_NAME, is("metacard_type_obj"));
  }

  // getFormat tests

  @Test
  void testGetFormatGeometry() {
    assertThat(schemaFields.getFormat(SchemaFields.GEO_SUFFIX), is(AttributeFormat.GEOMETRY));
  }

  @Test
  void testGetFormatDate() {
    assertThat(schemaFields.getFormat(SchemaFields.DATE_SUFFIX), is(AttributeFormat.DATE));
  }

  @Test
  void testGetFormatBinary() {
    assertThat(schemaFields.getFormat(SchemaFields.BINARY_SUFFIX), is(AttributeFormat.BINARY));
  }

  @Test
  void testGetFormatXml() {
    assertThat(schemaFields.getFormat(SchemaFields.XML_SUFFIX), is(AttributeFormat.XML));
  }

  @Test
  void testGetFormatString() {
    assertThat(schemaFields.getFormat(SchemaFields.TEXT_SUFFIX), is(AttributeFormat.STRING));
  }

  @Test
  void testGetFormatBoolean() {
    assertThat(schemaFields.getFormat(SchemaFields.BOOLEAN_SUFFIX), is(AttributeFormat.BOOLEAN));
  }

  @Test
  void testGetFormatDouble() {
    assertThat(schemaFields.getFormat(SchemaFields.DOUBLE_SUFFIX), is(AttributeFormat.DOUBLE));
  }

  @Test
  void testGetFormatFloat() {
    assertThat(schemaFields.getFormat(SchemaFields.FLOAT_SUFFIX), is(AttributeFormat.FLOAT));
  }

  @Test
  void testGetFormatInteger() {
    assertThat(schemaFields.getFormat(SchemaFields.INTEGER_SUFFIX), is(AttributeFormat.INTEGER));
  }

  @Test
  void testGetFormatLong() {
    assertThat(schemaFields.getFormat(SchemaFields.LONG_SUFFIX), is(AttributeFormat.LONG));
  }

  @Test
  void testGetFormatShort() {
    assertThat(schemaFields.getFormat(SchemaFields.SHORT_SUFFIX), is(AttributeFormat.SHORT));
  }

  @Test
  void testGetFormatObject() {
    assertThat(schemaFields.getFormat(SchemaFields.OBJECT_SUFFIX), is(AttributeFormat.OBJECT));
  }

  @Test
  void testGetFormatUnknownSuffixReturnsNull() {
    assertThat(schemaFields.getFormat("_unknown"), is(nullValue()));
  }

  // getFieldSuffix tests

  @Test
  void testGetFieldSuffixGeometry() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.GEOMETRY), is(SchemaFields.GEO_SUFFIX));
  }

  @Test
  void testGetFieldSuffixDate() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.DATE), is(SchemaFields.DATE_SUFFIX));
  }

  @Test
  void testGetFieldSuffixBinary() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.BINARY), is(SchemaFields.BINARY_SUFFIX));
  }

  @Test
  void testGetFieldSuffixXml() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.XML), is(SchemaFields.XML_SUFFIX));
  }

  @Test
  void testGetFieldSuffixString() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.STRING), is(SchemaFields.TEXT_SUFFIX));
  }

  @Test
  void testGetFieldSuffixBoolean() {
    assertThat(
        schemaFields.getFieldSuffix(AttributeFormat.BOOLEAN), is(SchemaFields.BOOLEAN_SUFFIX));
  }

  @Test
  void testGetFieldSuffixDouble() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.DOUBLE), is(SchemaFields.DOUBLE_SUFFIX));
  }

  @Test
  void testGetFieldSuffixFloat() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.FLOAT), is(SchemaFields.FLOAT_SUFFIX));
  }

  @Test
  void testGetFieldSuffixInteger() {
    assertThat(
        schemaFields.getFieldSuffix(AttributeFormat.INTEGER), is(SchemaFields.INTEGER_SUFFIX));
  }

  @Test
  void testGetFieldSuffixLong() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.LONG), is(SchemaFields.LONG_SUFFIX));
  }

  @Test
  void testGetFieldSuffixShort() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.SHORT), is(SchemaFields.SHORT_SUFFIX));
  }

  @Test
  void testGetFieldSuffixObject() {
    assertThat(schemaFields.getFieldSuffix(AttributeFormat.OBJECT), is(SchemaFields.OBJECT_SUFFIX));
  }

  // Bidirectional mapping tests

  @Test
  void testBidirectionalMappingConsistency() {
    for (AttributeFormat format : AttributeFormat.values()) {
      String suffix = schemaFields.getFieldSuffix(format);
      if (suffix != null) {
        AttributeFormat retrievedFormat = schemaFields.getFormat(suffix);
        assertThat(retrievedFormat, is(format));
      }
    }
  }
}
