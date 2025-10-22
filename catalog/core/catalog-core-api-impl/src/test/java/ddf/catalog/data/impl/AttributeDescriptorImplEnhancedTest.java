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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import ddf.catalog.data.AttributeType;
import org.junit.Test;

/**
 * Enhanced comprehensive tests for AttributeDescriptorImpl to increase coverage. Tests all
 * attribute types, edge cases, equality, and serialization.
 */
public class AttributeDescriptorImplEnhancedTest {

  @Test
  public void testConstructorWithAllParameters() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("testAttr", true, true, true, true, BasicTypes.STRING_TYPE);

    assertThat(descriptor.getName(), is("testAttr"));
    assertTrue(descriptor.isIndexed());
    assertTrue(descriptor.isStored());
    assertTrue(descriptor.isTokenized());
    assertTrue(descriptor.isMultiValued());
    assertThat(descriptor.getType(), is(BasicTypes.STRING_TYPE));
  }

  @Test
  public void testConstructorWithAllFalseFlags() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl(
            "notIndexed", false, false, false, false, BasicTypes.INTEGER_TYPE);

    assertThat(descriptor.getName(), is("notIndexed"));
    assertFalse(descriptor.isIndexed());
    assertFalse(descriptor.isStored());
    assertFalse(descriptor.isTokenized());
    assertFalse(descriptor.isMultiValued());
    assertThat(descriptor.getType(), is(BasicTypes.INTEGER_TYPE));
  }

  @Test
  public void testAllAttributeTypes() {
    // Test STRING_TYPE
    AttributeDescriptorImpl stringDesc =
        new AttributeDescriptorImpl("string", true, true, false, false, BasicTypes.STRING_TYPE);
    assertThat(stringDesc.getType().getBinding(), equalTo(String.class));

    // Test INTEGER_TYPE
    AttributeDescriptorImpl intDesc =
        new AttributeDescriptorImpl("integer", true, true, false, false, BasicTypes.INTEGER_TYPE);
    assertThat(intDesc.getType().getBinding(), equalTo(Integer.class));

    // Test LONG_TYPE
    AttributeDescriptorImpl longDesc =
        new AttributeDescriptorImpl("long", true, true, false, false, BasicTypes.LONG_TYPE);
    assertThat(longDesc.getType().getBinding(), equalTo(Long.class));

    // Test SHORT_TYPE
    AttributeDescriptorImpl shortDesc =
        new AttributeDescriptorImpl("short", true, true, false, false, BasicTypes.SHORT_TYPE);
    assertThat(shortDesc.getType().getBinding(), equalTo(Short.class));

    // Test FLOAT_TYPE
    AttributeDescriptorImpl floatDesc =
        new AttributeDescriptorImpl("float", true, true, false, false, BasicTypes.FLOAT_TYPE);
    assertThat(floatDesc.getType().getBinding(), equalTo(Float.class));

    // Test DOUBLE_TYPE
    AttributeDescriptorImpl doubleDesc =
        new AttributeDescriptorImpl("double", true, true, false, false, BasicTypes.DOUBLE_TYPE);
    assertThat(doubleDesc.getType().getBinding(), equalTo(Double.class));

    // Test BOOLEAN_TYPE
    AttributeDescriptorImpl boolDesc =
        new AttributeDescriptorImpl("boolean", true, true, false, false, BasicTypes.BOOLEAN_TYPE);
    assertThat(boolDesc.getType().getBinding(), equalTo(Boolean.class));

    // Test DATE_TYPE
    AttributeDescriptorImpl dateDesc =
        new AttributeDescriptorImpl("date", true, true, false, false, BasicTypes.DATE_TYPE);
    assertThat(dateDesc.getType().getBinding(), equalTo(java.util.Date.class));

    // Test BINARY_TYPE
    AttributeDescriptorImpl binaryDesc =
        new AttributeDescriptorImpl("binary", true, true, false, false, BasicTypes.BINARY_TYPE);
    assertThat(binaryDesc.getType().getBinding(), equalTo(byte[].class));

    // Test GEO_TYPE
    AttributeDescriptorImpl geoDesc =
        new AttributeDescriptorImpl("geo", true, true, false, false, BasicTypes.GEO_TYPE);
    assertThat(geoDesc.getType().getBinding(), equalTo(String.class));

    // Test XML_TYPE
    AttributeDescriptorImpl xmlDesc =
        new AttributeDescriptorImpl("xml", true, true, false, false, BasicTypes.XML_TYPE);
    assertThat(xmlDesc.getType().getBinding(), equalTo(String.class));

    // Test OBJECT_TYPE
    AttributeDescriptorImpl objectDesc =
        new AttributeDescriptorImpl("object", true, true, false, false, BasicTypes.OBJECT_TYPE);
    assertThat(objectDesc.getType().getBinding(), equalTo(java.io.Serializable.class));
  }

  @Test
  public void testEqualsSameObject() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);

    assertTrue(descriptor.equals(descriptor));
  }

  @Test
  public void testEqualsIdenticalDescriptors() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);

    assertTrue(descriptor1.equals(descriptor2));
    assertTrue(descriptor2.equals(descriptor1));
    assertThat(descriptor1.hashCode(), is(descriptor2.hashCode()));
  }

  @Test
  public void testEqualsNullObject() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);

    assertFalse(descriptor.equals(null));
  }

  @Test
  public void testEqualsDifferentClass() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);

    assertFalse(descriptor.equals("NotAnAttributeDescriptor"));
  }

  @Test
  public void testEqualsDifferentName() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl("name1", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("name2", true, true, true, true, BasicTypes.STRING_TYPE);

    assertFalse(descriptor1.equals(descriptor2));
    assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());
  }

  @Test
  public void testEqualsNullNames() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl(null, true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl(null, true, true, true, true, BasicTypes.STRING_TYPE);

    assertTrue(descriptor1.equals(descriptor2));
    assertThat(descriptor1.hashCode(), is(descriptor2.hashCode()));
  }

  @Test
  public void testEqualsOneNullName() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl(null, true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("name", true, true, true, true, BasicTypes.STRING_TYPE);

    assertFalse(descriptor1.equals(descriptor2));
    assertFalse(descriptor2.equals(descriptor1));
  }

  @Test
  public void testEqualsDifferentIndexedFlag() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("test", false, true, true, true, BasicTypes.STRING_TYPE);

    assertFalse(descriptor1.equals(descriptor2));
    assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());
  }

  @Test
  public void testEqualsDifferentStoredFlag() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("test", true, false, true, true, BasicTypes.STRING_TYPE);

    assertFalse(descriptor1.equals(descriptor2));
    assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());
  }

  @Test
  public void testEqualsDifferentTokenizedFlag() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("test", true, true, false, true, BasicTypes.STRING_TYPE);

    assertFalse(descriptor1.equals(descriptor2));
    assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());
  }

  @Test
  public void testEqualsDifferentMultivaluedFlag() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("test", true, true, true, false, BasicTypes.STRING_TYPE);

    assertFalse(descriptor1.equals(descriptor2));
    assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());
  }

  @Test
  public void testEqualsDifferentAttributeFormat() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.XML_TYPE);

    assertFalse(descriptor1.equals(descriptor2));
    assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());
  }

  @Test
  public void testEqualsDifferentBinding() {
    AttributeDescriptorImpl descriptor1 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.INTEGER_TYPE);
    AttributeDescriptorImpl descriptor2 =
        new AttributeDescriptorImpl("test", true, true, true, true, BasicTypes.LONG_TYPE);

    assertFalse(descriptor1.equals(descriptor2));
    assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());
  }

  @Test
  public void testHashCodeConsistency() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("test", true, false, true, false, BasicTypes.DOUBLE_TYPE);

    int hashCode1 = descriptor.hashCode();
    int hashCode2 = descriptor.hashCode();

    assertThat(hashCode1, is(hashCode2));
  }

  @Test
  public void testHashCodeWithNullName() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl(null, true, true, true, true, BasicTypes.STRING_TYPE);

    // Should not throw NPE
    int hashCode = descriptor.hashCode();
    assertThat(hashCode, is(notNullValue()));
  }

  @Test
  public void testToString() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("testName", true, true, false, false, BasicTypes.STRING_TYPE);

    String toString = descriptor.toString();
    assertThat(toString, is(notNullValue()));
    assertThat(toString.contains("testName"), is(true));
  }

  @Test
  public void testMultiValuedAttributeDescriptor() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("multiVal", false, true, false, true, BasicTypes.STRING_TYPE);

    assertTrue(descriptor.isMultiValued());
    assertFalse(descriptor.isIndexed());
    assertTrue(descriptor.isStored());
    assertFalse(descriptor.isTokenized());
  }

  @Test
  public void testTokenizedAttributeDescriptor() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("tokenized", true, true, true, false, BasicTypes.STRING_TYPE);

    assertTrue(descriptor.isTokenized());
    assertTrue(descriptor.isIndexed());
    assertTrue(descriptor.isStored());
    assertFalse(descriptor.isMultiValued());
  }

  @Test
  public void testGeometryAttributeDescriptor() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("location", true, true, false, false, BasicTypes.GEO_TYPE);

    assertThat(descriptor.getName(), is("location"));
    assertThat(
        descriptor.getType().getAttributeFormat(), is(AttributeType.AttributeFormat.GEOMETRY));
    assertThat(descriptor.getType().getBinding(), equalTo(String.class));
  }

  @Test
  public void testBinaryAttributeDescriptor() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("thumbnail", false, true, false, false, BasicTypes.BINARY_TYPE);

    assertThat(descriptor.getName(), is("thumbnail"));
    assertThat(descriptor.getType().getAttributeFormat(), is(AttributeType.AttributeFormat.BINARY));
    assertThat(descriptor.getType().getBinding(), equalTo(byte[].class));
    assertFalse(descriptor.isIndexed());
    assertTrue(descriptor.isStored());
  }

  @Test
  public void testDateAttributeDescriptor() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("created", true, true, false, false, BasicTypes.DATE_TYPE);

    assertThat(descriptor.getName(), is("created"));
    assertThat(descriptor.getType().getAttributeFormat(), is(AttributeType.AttributeFormat.DATE));
    assertThat(descriptor.getType().getBinding(), equalTo(java.util.Date.class));
  }

  @Test
  public void testXmlAttributeDescriptor() {
    AttributeDescriptorImpl descriptor =
        new AttributeDescriptorImpl("metadata", true, true, false, false, BasicTypes.XML_TYPE);

    assertThat(descriptor.getName(), is("metadata"));
    assertThat(descriptor.getType().getAttributeFormat(), is(AttributeType.AttributeFormat.XML));
    assertThat(descriptor.getType().getBinding(), equalTo(String.class));
  }

  @Test
  public void testNumericAttributeDescriptors() {
    // Test all numeric types
    AttributeDescriptorImpl intDescriptor =
        new AttributeDescriptorImpl("count", true, true, false, false, BasicTypes.INTEGER_TYPE);
    assertThat(intDescriptor.getType().getBinding(), equalTo(Integer.class));

    AttributeDescriptorImpl longDescriptor =
        new AttributeDescriptorImpl("timestamp", true, true, false, false, BasicTypes.LONG_TYPE);
    assertThat(longDescriptor.getType().getBinding(), equalTo(Long.class));

    AttributeDescriptorImpl shortDescriptor =
        new AttributeDescriptorImpl("code", true, true, false, false, BasicTypes.SHORT_TYPE);
    assertThat(shortDescriptor.getType().getBinding(), equalTo(Short.class));

    AttributeDescriptorImpl floatDescriptor =
        new AttributeDescriptorImpl("rating", true, true, false, false, BasicTypes.FLOAT_TYPE);
    assertThat(floatDescriptor.getType().getBinding(), equalTo(Float.class));

    AttributeDescriptorImpl doubleDescriptor =
        new AttributeDescriptorImpl("price", true, true, false, false, BasicTypes.DOUBLE_TYPE);
    assertThat(doubleDescriptor.getType().getBinding(), equalTo(Double.class));
  }

  @Test
  public void testEqualsWithAllFlagCombinations() {
    // Test that changing any single flag makes descriptors unequal
    AttributeDescriptorImpl base =
        new AttributeDescriptorImpl("base", true, true, true, true, BasicTypes.STRING_TYPE);

    AttributeDescriptorImpl diffIndexed =
        new AttributeDescriptorImpl("base", false, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl diffStored =
        new AttributeDescriptorImpl("base", true, false, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl diffTokenized =
        new AttributeDescriptorImpl("base", true, true, false, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl diffMultivalued =
        new AttributeDescriptorImpl("base", true, true, true, false, BasicTypes.STRING_TYPE);

    assertFalse(base.equals(diffIndexed));
    assertFalse(base.equals(diffStored));
    assertFalse(base.equals(diffTokenized));
    assertFalse(base.equals(diffMultivalued));
  }

  @Test
  public void testHashCodeDifferentForDifferentDescriptors() {
    AttributeDescriptorImpl desc1 =
        new AttributeDescriptorImpl("attr1", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl desc2 =
        new AttributeDescriptorImpl("attr2", true, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl desc3 =
        new AttributeDescriptorImpl("attr1", false, true, true, true, BasicTypes.STRING_TYPE);
    AttributeDescriptorImpl desc4 =
        new AttributeDescriptorImpl("attr1", true, true, true, true, BasicTypes.INTEGER_TYPE);

    assertNotEquals(desc1.hashCode(), desc2.hashCode());
    assertNotEquals(desc1.hashCode(), desc3.hashCode());
    assertNotEquals(desc1.hashCode(), desc4.hashCode());
  }
}
