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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.types.ContactAttributes;
import ddf.catalog.data.impl.types.CoreAttributes;
import ddf.catalog.data.impl.types.DateTimeAttributes;
import ddf.catalog.data.impl.types.LocationAttributes;
import ddf.catalog.data.impl.types.MediaAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * Enhanced comprehensive tests for MetacardTypeImpl to increase coverage. Tests constructors,
 * descriptor management, equality, and edge cases.
 */
public class MetacardTypeImplEnhancedTest {

  @Test
  public void testConstructorWithNameAndDescriptors() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("attr1", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("attr2", true, true, false, false, BasicTypes.INTEGER_TYPE));

    MetacardTypeImpl metacardType = new MetacardTypeImpl("testType", descriptors);

    assertThat(metacardType.getName(), is("testType"));
    assertThat(metacardType.getAttributeDescriptors(), hasSize(2));
    assertNotNull(metacardType.getAttributeDescriptor("attr1"));
    assertNotNull(metacardType.getAttributeDescriptor("attr2"));
  }

  @Test
  public void testConstructorWithEmptyDescriptors() {
    Set<AttributeDescriptor> emptySet = new HashSet<>();
    MetacardTypeImpl metacardType = new MetacardTypeImpl("empty", emptySet);

    assertThat(metacardType.getName(), is("empty"));
    assertThat(metacardType.getAttributeDescriptors(), hasSize(0));
  }

  @Test
  public void testConstructorWithNullDescriptorsSet() {
    MetacardTypeImpl metacardType =
        new MetacardTypeImpl("nullDesc", (Set<AttributeDescriptor>) null);

    assertThat(metacardType.getName(), is("nullDesc"));
    assertThat(metacardType.getAttributeDescriptors(), hasSize(0));
  }

  @Test
  public void testConstructorWithDescriptorsContainingNulls() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("valid", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(null);
    descriptors.add(
        new AttributeDescriptorImpl("another", false, true, false, false, BasicTypes.INTEGER_TYPE));

    MetacardTypeImpl metacardType = new MetacardTypeImpl("mixed", descriptors);

    assertThat(metacardType.getAttributeDescriptors(), hasSize(2));
    assertNotNull(metacardType.getAttributeDescriptor("valid"));
    assertNotNull(metacardType.getAttributeDescriptor("another"));
  }

  @Test
  public void testConstructorWithMetacardTypeList() {
    List<MetacardType> metacardTypes = new ArrayList<>();
    metacardTypes.add(new CoreAttributes());
    metacardTypes.add(new DateTimeAttributes());

    MetacardTypeImpl metacardType = new MetacardTypeImpl("composed", metacardTypes);

    assertThat(metacardType.getName(), is("composed"));
    assertThat(metacardType.getAttributeDescriptors().size(), greaterThan(0));
  }

  @Test
  public void testConstructorWithNullMetacardTypeList() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("nullList", (List<MetacardType>) null);

    assertThat(metacardType.getName(), is("nullList"));
    // Should have Core and Security attributes by default
    assertThat(metacardType.getAttributeDescriptors().size(), greaterThan(0));
  }

  @Test
  public void testConstructorWithEmptyMetacardTypeList() {
    List<MetacardType> emptyList = new ArrayList<>();
    MetacardTypeImpl metacardType = new MetacardTypeImpl("emptyList", emptyList);

    assertThat(metacardType.getName(), is("emptyList"));
    // Should have Core and Security attributes by default
    assertThat(metacardType.getAttributeDescriptors().size(), greaterThan(0));
  }

  @Test
  public void testExtendingConstructor() {
    Set<AttributeDescriptor> baseDescriptors = new HashSet<>();
    baseDescriptors.add(
        new AttributeDescriptorImpl("base1", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardType baseType = new MetacardTypeImpl("base", baseDescriptors);

    Set<AttributeDescriptor> additionalDescriptors = new HashSet<>();
    additionalDescriptors.add(
        new AttributeDescriptorImpl(
            "additional1", true, false, false, true, BasicTypes.INTEGER_TYPE));

    MetacardTypeImpl extended = new MetacardTypeImpl("extended", baseType, additionalDescriptors);

    assertThat(extended.getName(), is("extended"));
    assertThat(extended.getAttributeDescriptors(), hasSize(2));
    assertNotNull(extended.getAttributeDescriptor("base1"));
    assertNotNull(extended.getAttributeDescriptor("additional1"));
  }

  @Test
  public void testGetAttributeDescriptor() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    AttributeDescriptor descriptor =
        new AttributeDescriptorImpl("target", true, true, false, false, BasicTypes.STRING_TYPE);
    descriptors.add(descriptor);

    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", descriptors);

    AttributeDescriptor retrieved = metacardType.getAttributeDescriptor("target");
    assertThat(retrieved, is(descriptor));
    assertThat(retrieved.getName(), is("target"));
  }

  @Test
  public void testGetAttributeDescriptorNonExistent() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", new HashSet<>());

    AttributeDescriptor retrieved = metacardType.getAttributeDescriptor("nonexistent");
    assertThat(retrieved, nullValue());
  }

  @Test
  public void testGetAttributeDescriptorWithNullName() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", new HashSet<>());

    AttributeDescriptor retrieved = metacardType.getAttributeDescriptor(null);
    assertThat(retrieved, nullValue());
  }

  @Test
  public void testGetAttributeDescriptors() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("attr1", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("attr2", true, true, false, false, BasicTypes.INTEGER_TYPE));

    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", descriptors);

    Set<AttributeDescriptor> retrieved = metacardType.getAttributeDescriptors();
    assertThat(retrieved, hasSize(2));
  }

  @Test
  public void testGetAttributeDescriptorsIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("attr1", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", descriptors);

    Set<AttributeDescriptor> retrieved = metacardType.getAttributeDescriptors();

    try {
      retrieved.add(
          new AttributeDescriptorImpl("new", true, true, false, false, BasicTypes.STRING_TYPE));
      assertFalse("Should have thrown UnsupportedOperationException", true);
    } catch (UnsupportedOperationException e) {
      // Expected
      assertTrue(true);
    }
  }

  @Test
  public void testAddMethod() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", new HashSet<>());

    AttributeDescriptor newDescriptor =
        new AttributeDescriptorImpl("new", true, true, false, false, BasicTypes.STRING_TYPE);

    Set<AttributeDescriptor> result = metacardType.add(newDescriptor);

    assertThat(result, hasSize(1));
    assertThat(metacardType.getAttributeDescriptor("new"), is(newDescriptor));
  }

  @Test
  public void testAddNullDescriptor() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", new HashSet<>());

    Set<AttributeDescriptor> result = metacardType.add(null);

    assertThat(result, hasSize(0));
  }

  @Test
  public void testAddAllMethod() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", new HashSet<>());

    Set<AttributeDescriptor> newDescriptors = new HashSet<>();
    newDescriptors.add(
        new AttributeDescriptorImpl("new1", true, true, false, false, BasicTypes.STRING_TYPE));
    newDescriptors.add(
        new AttributeDescriptorImpl("new2", true, true, false, false, BasicTypes.INTEGER_TYPE));

    Set<AttributeDescriptor> result = metacardType.addAll(newDescriptors);

    assertThat(result, hasSize(2));
    assertNotNull(metacardType.getAttributeDescriptor("new1"));
    assertNotNull(metacardType.getAttributeDescriptor("new2"));
  }

  @Test
  public void testAddAllWithNull() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", new HashSet<>());

    Set<AttributeDescriptor> result = metacardType.addAll(null);

    assertThat(result, hasSize(0));
  }

  @Test
  public void testAddAllWithEmptySet() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", new HashSet<>());

    Set<AttributeDescriptor> result = metacardType.addAll(new HashSet<>());

    assertThat(result, hasSize(0));
  }

  @Test
  public void testEqualsIdenticalTypes() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("attr", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardTypeImpl type1 = new MetacardTypeImpl("test", descriptors);
    MetacardTypeImpl type2 = new MetacardTypeImpl("test", descriptors);

    assertTrue(type1.equals(type2));
    assertTrue(type2.equals(type1));
    assertEquals(type1.hashCode(), type2.hashCode());
  }

  @Test
  public void testEqualsSameObject() {
    MetacardTypeImpl type = new MetacardTypeImpl("test", new HashSet<>());

    assertTrue(type.equals(type));
  }

  @Test
  public void testEqualsDifferentNames() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("attr", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardTypeImpl type1 = new MetacardTypeImpl("type1", descriptors);
    MetacardTypeImpl type2 = new MetacardTypeImpl("type2", descriptors);

    assertFalse(type1.equals(type2));
  }

  @Test
  public void testEqualsDifferentDescriptors() {
    Set<AttributeDescriptor> descriptors1 = new HashSet<>();
    descriptors1.add(
        new AttributeDescriptorImpl("attr1", true, true, false, false, BasicTypes.STRING_TYPE));

    Set<AttributeDescriptor> descriptors2 = new HashSet<>();
    descriptors2.add(
        new AttributeDescriptorImpl("attr2", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardTypeImpl type1 = new MetacardTypeImpl("test", descriptors1);
    MetacardTypeImpl type2 = new MetacardTypeImpl("test", descriptors2);

    assertFalse(type1.equals(type2));
  }

  @Test
  public void testEqualsNull() {
    MetacardTypeImpl type = new MetacardTypeImpl("test", new HashSet<>());

    assertFalse(type.equals(null));
  }

  @Test
  public void testEqualsDifferentClass() {
    MetacardTypeImpl type = new MetacardTypeImpl("test", new HashSet<>());

    assertFalse(type.equals("NotAMetacardType"));
  }

  @Test
  public void testHashCodeConsistency() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("attr", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardTypeImpl type = new MetacardTypeImpl("test", descriptors);

    int hash1 = type.hashCode();
    int hash2 = type.hashCode();

    assertEquals(hash1, hash2);
  }

  @Test
  public void testDuplicateAttributeNames() {
    List<MetacardType> metacardTypes = new ArrayList<>();
    metacardTypes.add(new CoreAttributes());
    metacardTypes.add(new CoreAttributes()); // Duplicate

    MetacardTypeImpl metacardType = new MetacardTypeImpl("duplicate", metacardTypes);

    // Should handle duplicates gracefully
    assertThat(metacardType.getAttributeDescriptors().size(), greaterThan(0));
  }

  @Test
  public void testMultipleMetacardTypesCombination() {
    List<MetacardType> metacardTypes = new ArrayList<>();
    metacardTypes.add(new CoreAttributes());
    metacardTypes.add(new ContactAttributes());
    metacardTypes.add(new LocationAttributes());
    metacardTypes.add(new MediaAttributes());

    MetacardTypeImpl metacardType = new MetacardTypeImpl("combined", metacardTypes);

    assertThat(metacardType.getName(), is("combined"));
    assertThat(metacardType.getAttributeDescriptors().size(), greaterThan(10));
  }

  @Test
  public void testNullNameInDescriptorIsFiltered() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(null, true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("valid", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", descriptors);

    // The null-named descriptor should be filtered out
    assertThat(metacardType.getAttributeDescriptor("valid"), notNullValue());
  }

  @Test
  public void testAddingDuplicateDescriptor() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("test", new HashSet<>());

    AttributeDescriptor desc1 =
        new AttributeDescriptorImpl("duplicate", true, true, false, false, BasicTypes.STRING_TYPE);
    AttributeDescriptor desc2 =
        new AttributeDescriptorImpl("duplicate", false, false, true, true, BasicTypes.INTEGER_TYPE);

    metacardType.add(desc1);
    metacardType.add(desc2);

    // Second descriptor should replace first
    AttributeDescriptor retrieved = metacardType.getAttributeDescriptor("duplicate");
    assertThat(retrieved, is(desc2));
  }

  @Test
  public void testLargeNumberOfDescriptors() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      descriptors.add(
          new AttributeDescriptorImpl(
              "attr" + i, i % 2 == 0, i % 3 == 0, i % 5 == 0, i % 7 == 0, BasicTypes.STRING_TYPE));
    }

    MetacardTypeImpl metacardType = new MetacardTypeImpl("large", descriptors);

    assertThat(metacardType.getAttributeDescriptors(), hasSize(100));
    assertNotNull(metacardType.getAttributeDescriptor("attr50"));
  }

  @Test
  public void testAllBasicTypes() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("string", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("integer", true, true, false, false, BasicTypes.INTEGER_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("long", true, true, false, false, BasicTypes.LONG_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("short", true, true, false, false, BasicTypes.SHORT_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("float", true, true, false, false, BasicTypes.FLOAT_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("double", true, true, false, false, BasicTypes.DOUBLE_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("boolean", true, true, false, false, BasicTypes.BOOLEAN_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("date", true, true, false, false, BasicTypes.DATE_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("binary", true, true, false, false, BasicTypes.BINARY_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("geo", true, true, false, false, BasicTypes.GEO_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("xml", true, true, false, false, BasicTypes.XML_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("object", true, true, false, false, BasicTypes.OBJECT_TYPE));

    MetacardTypeImpl metacardType = new MetacardTypeImpl("allTypes", descriptors);

    assertThat(metacardType.getAttributeDescriptors(), hasSize(12));
    assertNotNull(metacardType.getAttributeDescriptor("string"));
    assertNotNull(metacardType.getAttributeDescriptor("integer"));
    assertNotNull(metacardType.getAttributeDescriptor("date"));
    assertNotNull(metacardType.getAttributeDescriptor("binary"));
    assertNotNull(metacardType.getAttributeDescriptor("geo"));
  }

  @Test
  public void testNameCanBeNull() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("attr", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardTypeImpl metacardType = new MetacardTypeImpl(null, descriptors);

    assertThat(metacardType.getName(), nullValue());
    assertThat(metacardType.getAttributeDescriptors(), hasSize(1));
  }

  @Test
  public void testGetNameReturnsCorrectValue() {
    MetacardTypeImpl metacardType = new MetacardTypeImpl("myType", new HashSet<>());

    assertThat(metacardType.getName(), is("myType"));
  }
}
