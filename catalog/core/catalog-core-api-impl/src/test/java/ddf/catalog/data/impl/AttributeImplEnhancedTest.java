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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import ddf.catalog.data.Attribute;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Enhanced comprehensive tests for AttributeImpl to increase coverage. Tests single/multi-value
 * attributes, all data types, edge cases, and operations.
 */
public class AttributeImplEnhancedTest {

  @Test
  public void testSingleValueConstructor() {
    AttributeImpl attribute = new AttributeImpl("title", "Test Title");

    assertThat(attribute.getName(), is("title"));
    assertThat(attribute.getValue(), is("Test Title"));
    assertThat(attribute.getValues(), hasSize(1));
    assertThat(attribute.getValues().get(0), is("Test Title"));
  }

  @Test
  public void testMultiValueConstructor() {
    List<Serializable> values = Arrays.asList("value1", "value2", "value3");
    AttributeImpl attribute = new AttributeImpl("tags", values);

    assertThat(attribute.getName(), is("tags"));
    assertThat(attribute.getValue(), is("value1"));
    assertThat(attribute.getValues(), hasSize(3));
    assertThat(attribute.getValues(), containsInAnyOrder("value1", "value2", "value3"));
  }

  @Test
  public void testCopyConstructor() {
    Attribute original = new AttributeImpl("original", "value");
    AttributeImpl copy = new AttributeImpl(original);

    assertThat(copy.getName(), is(original.getName()));
    assertThat(copy.getValue(), is(original.getValue()));
    assertThat(copy.getValues(), is(original.getValues()));
  }

  @Test
  public void testFromSingleValueFactoryMethod() {
    AttributeImpl attribute = AttributeImpl.fromSingleValue("name", "SingleValue");

    assertThat(attribute.getName(), is("name"));
    assertThat(attribute.getValue(), is("SingleValue"));
    assertThat(attribute.getValues(), hasSize(1));
  }

  @Test
  public void testFromMultipleValuesFactoryMethod() {
    List<Serializable> values = Arrays.asList(1, 2, 3, 4, 5);
    AttributeImpl attribute = AttributeImpl.fromMultipleValues("numbers", values);

    assertThat(attribute.getName(), is("numbers"));
    assertThat(attribute.getValue(), is(1));
    assertThat(attribute.getValues(), hasSize(5));
  }

  @Test
  public void testAddValue() {
    AttributeImpl attribute = new AttributeImpl("list", "initial");

    attribute.addValue("added1");
    attribute.addValue("added2");

    assertThat(attribute.getValues(), hasSize(3));
    assertThat(attribute.getValue(), is("initial"));
    assertThat(attribute.getValues(), contains("initial", "added1", "added2"));
  }

  @Test
  public void testClearValues() {
    AttributeImpl attribute = new AttributeImpl("test", "value");
    attribute.addValue("another");

    assertThat(attribute.getValues(), hasSize(2));

    attribute.clearValues();

    assertThat(attribute.getValues(), empty());
    assertThat(attribute.getValue(), nullValue());
  }

  @Test
  public void testGetValueFromEmptyList() {
    AttributeImpl attribute = new AttributeImpl("empty", "value");
    attribute.clearValues();

    assertThat(attribute.getValue(), nullValue());
  }

  @Test
  public void testStringAttribute() {
    AttributeImpl attribute = new AttributeImpl("string", "text value");

    assertThat(attribute.getValue(), is("text value"));
    assertTrue(attribute.getValue() instanceof String);
  }

  @Test
  public void testIntegerAttribute() {
    AttributeImpl attribute = new AttributeImpl("count", 42);

    assertThat(attribute.getValue(), is(42));
    assertTrue(attribute.getValue() instanceof Integer);
  }

  @Test
  public void testLongAttribute() {
    AttributeImpl attribute = new AttributeImpl("timestamp", 123456789L);

    assertThat(attribute.getValue(), is(123456789L));
    assertTrue(attribute.getValue() instanceof Long);
  }

  @Test
  public void testDoubleAttribute() {
    AttributeImpl attribute = new AttributeImpl("price", 99.99);

    assertThat(attribute.getValue(), is(99.99));
    assertTrue(attribute.getValue() instanceof Double);
  }

  @Test
  public void testFloatAttribute() {
    AttributeImpl attribute = new AttributeImpl("rating", 4.5f);

    assertThat(attribute.getValue(), is(4.5f));
    assertTrue(attribute.getValue() instanceof Float);
  }

  @Test
  public void testBooleanAttribute() {
    AttributeImpl attribute = new AttributeImpl("active", true);

    assertThat(attribute.getValue(), is(true));
    assertTrue(attribute.getValue() instanceof Boolean);
  }

  @Test
  public void testDateAttribute() {
    Date now = new Date();
    AttributeImpl attribute = new AttributeImpl("created", now);

    assertThat(attribute.getValue(), is(now));
    assertTrue(attribute.getValue() instanceof Date);
  }

  @Test
  public void testByteArrayAttribute() {
    byte[] bytes = new byte[] {1, 2, 3, 4, 5};
    AttributeImpl attribute = new AttributeImpl("thumbnail", bytes);

    assertThat(attribute.getValue(), is(bytes));
    assertTrue(attribute.getValue() instanceof byte[]);
  }

  @Test
  public void testShortAttribute() {
    Short shortValue = (short) 100;
    AttributeImpl attribute = new AttributeImpl("code", shortValue);

    assertThat(attribute.getValue(), is(shortValue));
    assertTrue(attribute.getValue() instanceof Short);
  }

  @Test
  public void testMultiValuedStringList() {
    List<Serializable> keywords = Arrays.asList("java", "ddf", "catalog", "metadata");
    AttributeImpl attribute = new AttributeImpl("keywords", keywords);

    assertThat(attribute.getValues(), hasSize(4));
    assertThat(attribute.getValue(), is("java"));
    assertThat(attribute.getValues(), contains("java", "ddf", "catalog", "metadata"));
  }

  @Test
  public void testMultiValuedNumericList() {
    List<Serializable> numbers = Arrays.asList(10, 20, 30, 40, 50);
    AttributeImpl attribute = new AttributeImpl("scores", numbers);

    assertThat(attribute.getValues(), hasSize(5));
    assertThat(attribute.getValue(), is(10));
  }

  @Test
  public void testEmptyList() {
    List<Serializable> emptyList = new ArrayList<>();
    AttributeImpl attribute = new AttributeImpl("empty", emptyList);

    assertThat(attribute.getValues(), empty());
    assertThat(attribute.getValue(), nullValue());
  }

  @Test
  public void testEqualsSameObject() {
    AttributeImpl attribute = new AttributeImpl("test", "value");

    assertTrue(attribute.equals(attribute));
  }

  @Test
  public void testEqualsIdenticalAttributes() {
    AttributeImpl attr1 = new AttributeImpl("test", "value");
    AttributeImpl attr2 = new AttributeImpl("test", "value");

    assertTrue(attr1.equals(attr2));
    assertTrue(attr2.equals(attr1));
    assertEquals(attr1.hashCode(), attr2.hashCode());
  }

  @Test
  public void testEqualsNullObject() {
    AttributeImpl attribute = new AttributeImpl("test", "value");

    assertFalse(attribute.equals(null));
  }

  @Test
  public void testEqualsDifferentClass() {
    AttributeImpl attribute = new AttributeImpl("test", "value");

    assertFalse(attribute.equals("NotAnAttribute"));
  }

  @Test
  public void testEqualsDifferentName() {
    AttributeImpl attr1 = new AttributeImpl("name1", "value");
    AttributeImpl attr2 = new AttributeImpl("name2", "value");

    assertFalse(attr1.equals(attr2));
    assertNotEquals(attr1.hashCode(), attr2.hashCode());
  }

  @Test
  public void testEqualsDifferentValue() {
    AttributeImpl attr1 = new AttributeImpl("test", "value1");
    AttributeImpl attr2 = new AttributeImpl("test", "value2");

    assertFalse(attr1.equals(attr2));
    assertNotEquals(attr1.hashCode(), attr2.hashCode());
  }

  @Test
  public void testEqualsDifferentValueCount() {
    AttributeImpl attr1 = new AttributeImpl("test", "value");
    AttributeImpl attr2 = new AttributeImpl("test", Arrays.asList("value", "extra"));

    assertFalse(attr1.equals(attr2));
  }

  @Test
  public void testEqualsMultiValuedAttributes() {
    List<Serializable> values = Arrays.asList("a", "b", "c");
    AttributeImpl attr1 = new AttributeImpl("multi", values);
    AttributeImpl attr2 = new AttributeImpl("multi", Arrays.asList("a", "b", "c"));

    assertTrue(attr1.equals(attr2));
    assertEquals(attr1.hashCode(), attr2.hashCode());
  }

  @Test
  public void testEqualsNullName() {
    AttributeImpl attr1 = new AttributeImpl(null, "value");
    AttributeImpl attr2 = new AttributeImpl(null, "value");

    assertTrue(attr1.equals(attr2));
  }

  @Test
  public void testHashCodeConsistency() {
    AttributeImpl attribute = new AttributeImpl("test", "value");

    int hash1 = attribute.hashCode();
    int hash2 = attribute.hashCode();

    assertEquals(hash1, hash2);
  }

  @Test
  public void testHashCodeWithMultipleValues() {
    List<Serializable> values = Arrays.asList("x", "y", "z");
    AttributeImpl attribute = new AttributeImpl("multi", values);

    int hashCode = attribute.hashCode();
    // Verify hashCode is consistent
    assertThat(attribute.hashCode(), is(hashCode));
  }

  @Test
  public void testToString() {
    AttributeImpl attribute = new AttributeImpl("testAttr", "testValue");

    String toString = attribute.toString();
    assertThat(toString, notNullValue());
    assertThat(toString.contains("testAttr"), is(true));
    assertThat(toString.contains("testValue"), is(true));
  }

  @Test
  public void testToStringWithMultipleValues() {
    List<Serializable> values = Arrays.asList("val1", "val2");
    AttributeImpl attribute = new AttributeImpl("multi", values);

    String toString = attribute.toString();
    assertThat(toString, notNullValue());
    assertThat(toString.contains("multi"), is(true));
  }

  @Test
  public void testAddNullValue() {
    AttributeImpl attribute = new AttributeImpl("test", "value");

    attribute.addValue(null);

    assertThat(attribute.getValues(), hasSize(2));
    assertThat(attribute.getValues().get(1), nullValue());
  }

  @Test
  public void testConstructorWithNullValue() {
    AttributeImpl attribute = new AttributeImpl("test", (Serializable) null);

    assertThat(attribute.getName(), is("test"));
    assertThat(attribute.getValue(), nullValue());
    assertThat(attribute.getValues(), hasSize(1));
  }

  @Test
  public void testListOfListsAsValue() {
    List<Serializable> innerList = new ArrayList<>(Arrays.asList("a", "b"));
    AttributeImpl attribute = new AttributeImpl("nested", (Serializable) innerList);

    // When passed as List, the constructor unpacks it into individual elements
    assertThat(attribute.getValue(), is("a"));
    assertThat(attribute.getValues(), hasSize(2));
    assertThat(attribute.getValues(), contains("a", "b"));
  }

  @Test
  public void testMixedTypeMultiValue() {
    List<Serializable> mixed = Arrays.asList("string", 42, 3.14, true, new Date());
    AttributeImpl attribute = new AttributeImpl("mixed", mixed);

    assertThat(attribute.getValues(), hasSize(5));
    assertThat(attribute.getValue(), is("string"));
    assertThat(attribute.getValues().get(1), is(42));
    assertThat(attribute.getValues().get(2), is(3.14));
    assertThat(attribute.getValues().get(3), is(true));
    assertTrue(attribute.getValues().get(4) instanceof Date);
  }

  @Test
  public void testAddValueAfterClear() {
    AttributeImpl attribute = new AttributeImpl("test", "initial");
    attribute.clearValues();
    attribute.addValue("new");

    assertThat(attribute.getValues(), hasSize(1));
    assertThat(attribute.getValue(), is("new"));
  }

  @Test
  public void testCopyConstructorWithMultipleValues() {
    List<Serializable> values = Arrays.asList("one", "two", "three");
    Attribute original = new AttributeImpl("multi", values);
    AttributeImpl copy = new AttributeImpl(original);

    assertThat(copy.getName(), is("multi"));
    assertThat(copy.getValues(), hasSize(3));
    assertThat(copy.getValues(), contains("one", "two", "three"));
  }

  @Test
  public void testModifyingValuesDoesNotAffectOriginal() {
    List<Serializable> originalList = new ArrayList<>(Arrays.asList("a", "b"));
    AttributeImpl attribute = new AttributeImpl("test", originalList);

    // Modify original list
    originalList.add("c");

    // Attribute should only have original 2 values
    assertThat(attribute.getValues(), hasSize(2));
  }

  @Test
  public void testLargeMultiValueList() {
    List<Serializable> largeList = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      largeList.add("value" + i);
    }

    AttributeImpl attribute = new AttributeImpl("large", largeList);

    assertThat(attribute.getValues(), hasSize(1000));
    assertThat(attribute.getValue(), is("value0"));
  }

  @Test
  public void testAddMultipleValuesSequentially() {
    AttributeImpl attribute = new AttributeImpl("seq", "first");

    for (int i = 1; i <= 10; i++) {
      attribute.addValue("value" + i);
    }

    assertThat(attribute.getValues(), hasSize(11));
    assertThat(attribute.getValue(), is("first"));
  }

  @Test
  public void testValueListIsNotNull() {
    AttributeImpl attribute = new AttributeImpl("test", "value");

    assertThat(attribute.getValues(), notNullValue());
  }
}
