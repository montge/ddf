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
package org.codice.ddf.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersistenceItemTest {

  private PersistentItem persistentItem;

  @BeforeEach
  public void setUp() {
    persistentItem = new PersistentItem();
  }

  @Test
  public void testStripSuffixes() {
    Map<String, Object> inMap = new HashMap<String, Object>();
    inMap.put("key1" + PersistentItem.TEXT_SUFFIX, "key1_value");
    inMap.put("key2" + PersistentItem.DATE_SUFFIX, new Date());

    Map<String, Object> outMap = PersistentItem.stripSuffixes(inMap);
    assertTrue(outMap.size() == inMap.size());
    assertTrue(outMap.containsKey("key1"));
    assertFalse(outMap.containsKey("key1_" + PersistentItem.TEXT_SUFFIX));
    assertTrue(outMap.containsKey("key2"));
    assertFalse(outMap.containsKey("key2_" + PersistentItem.DATE_SUFFIX));
  }

  @Test
  public void testStripSuffixesWithAllSuffixTypes() {
    Map<String, Object> inMap = new HashMap<>();
    inMap.put("text" + PersistentItem.TEXT_SUFFIX, "text_value");
    inMap.put("xml" + PersistentItem.XML_SUFFIX, "<xml>data</xml>");
    inMap.put("num" + PersistentItem.INT_SUFFIX, 42);
    inMap.put("big" + PersistentItem.LONG_SUFFIX, 1000L);
    inMap.put("when" + PersistentItem.DATE_SUFFIX, new Date());
    inMap.put("data" + PersistentItem.BINARY_SUFFIX, new byte[] {1, 2, 3});

    Map<String, Object> outMap = PersistentItem.stripSuffixes(inMap);

    assertThat(outMap.size(), is(6));
    assertTrue(outMap.containsKey("text"));
    assertTrue(outMap.containsKey("xml"));
    assertTrue(outMap.containsKey("num"));
    assertTrue(outMap.containsKey("big"));
    assertTrue(outMap.containsKey("when"));
    assertTrue(outMap.containsKey("data"));
  }

  @Test
  public void testStripSuffixesWithNoSuffix() {
    Map<String, Object> inMap = new HashMap<>();
    inMap.put("nosuffix", "value");

    Map<String, Object> outMap = PersistentItem.stripSuffixes(inMap);

    assertThat(outMap.size(), is(1));
    assertTrue(outMap.containsKey("nosuffix"));
    assertThat(outMap.get("nosuffix"), is("value"));
  }

  @Test
  public void testStripSuffixesWithEmptyMap() {
    Map<String, Object> inMap = new HashMap<>();
    Map<String, Object> outMap = PersistentItem.stripSuffixes(inMap);
    assertThat(outMap.size(), is(0));
  }

  @Test
  public void testSuffixCreation() {
    Map<String, Object> inMap = new HashMap<>();
    inMap.put("string", "value");
    inMap.put("int", 1);
    inMap.put("long", 1L);
    inMap.put("binary", new byte[1]);
    inMap.put("date", new Date());
    HashSet<String> set = new HashSet<>();
    set.add("foo");
    set.add("bar");
    inMap.put("set", set);
    PersistentItem item = new PersistentItem();
    inMap.forEach((name, value) -> item.addProperty(name, value));
    assertTrue(item.getPropertyNames().contains("string_txt"));
    assertTrue(item.getPropertyNames().contains("int_int"));
    assertTrue(item.getPropertyNames().contains("long_lng"));
    assertTrue(item.getPropertyNames().contains("binary_bin"));
    assertTrue(item.getPropertyNames().contains("date_tdt"));
    assertTrue(item.getPropertyNames().contains("set_txt"));
  }

  @Test
  public void testAddIdProperty() {
    String testId = "test-id-123";
    persistentItem.addIdProperty(testId);
    assertThat(persistentItem.get(PersistentItem.ID), is(testId));
  }

  @Test
  public void testAddIdPropertyWithNull() {
    persistentItem.addIdProperty(null);
    assertFalse(persistentItem.containsKey(PersistentItem.ID));
  }

  @Test
  public void testAddPropertyString() {
    String value = "test value";
    persistentItem.addProperty("testKey", value);
    assertThat(persistentItem.get("testKey" + PersistentItem.TEXT_SUFFIX), is(value));
  }

  @Test
  public void testAddXmlProperty() {
    String xmlValue = "<root><element>value</element></root>";
    persistentItem.addXmlProperty("xmlKey", xmlValue);
    assertThat(persistentItem.get("xmlKey" + PersistentItem.XML_SUFFIX), is(xmlValue));
  }

  @Test
  public void testAddPropertyInt() {
    int value = 42;
    persistentItem.addProperty("intKey", value);
    assertThat(persistentItem.get("intKey" + PersistentItem.INT_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyLong() {
    long value = 9876543210L;
    persistentItem.addProperty("longKey", value);
    assertThat(persistentItem.get("longKey" + PersistentItem.LONG_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertySet() {
    Set<String> value = new HashSet<>();
    value.add("item1");
    value.add("item2");
    persistentItem.addProperty("setKey", value);
    assertThat(persistentItem.get("setKey" + PersistentItem.TEXT_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyDate() {
    Date value = new Date();
    persistentItem.addProperty("dateKey", value);
    assertThat(persistentItem.get("dateKey" + PersistentItem.DATE_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyByteArray() {
    byte[] value = new byte[] {1, 2, 3, 4, 5};
    persistentItem.addProperty("binaryKey", value);
    assertThat(persistentItem.get("binaryKey" + PersistentItem.BINARY_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyObjectWithString() {
    String value = "object string";
    persistentItem.addProperty("objKey", (Object) value);
    assertThat(persistentItem.get("objKey" + PersistentItem.TEXT_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyObjectWithDate() {
    Date value = new Date();
    persistentItem.addProperty("objKey", (Object) value);
    assertThat(persistentItem.get("objKey" + PersistentItem.DATE_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyObjectWithSet() {
    Set<String> value = new HashSet<>();
    value.add("test");
    persistentItem.addProperty("objKey", (Object) value);
    assertThat(persistentItem.get("objKey" + PersistentItem.TEXT_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyObjectWithLong() {
    Long value = 123L;
    persistentItem.addProperty("objKey", (Object) value);
    assertThat(persistentItem.get("objKey" + PersistentItem.LONG_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyObjectWithInteger() {
    Integer value = 456;
    persistentItem.addProperty("objKey", (Object) value);
    assertThat(persistentItem.get("objKey" + PersistentItem.INT_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyObjectWithByteArray() {
    byte[] value = new byte[] {9, 8, 7};
    persistentItem.addProperty("objKey", (Object) value);
    assertThat(persistentItem.get("objKey" + PersistentItem.BINARY_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyObjectWithCustomObject() {
    Object value =
        new Object() {
          @Override
          public String toString() {
            return "custom_value";
          }
        };
    persistentItem.addProperty("objKey", value);
    assertThat(persistentItem.get("objKey" + PersistentItem.TEXT_SUFFIX), is("custom_value"));
  }

  @Test
  public void testAddPropertyObjectWithNull() {
    persistentItem.addProperty("objKey", (Object) null);
    assertFalse(persistentItem.containsKey("objKey" + PersistentItem.TEXT_SUFFIX));
  }

  @Test
  public void testAddPropertyWithSuffix() {
    String value = "test value";
    persistentItem.addProperty("customKey", PersistentItem.TEXT_SUFFIX, value);
    assertThat(persistentItem.get("customKey" + PersistentItem.TEXT_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyWithSuffixAlreadyPresent() {
    String value = "test value";
    persistentItem.addProperty(
        "customKey" + PersistentItem.TEXT_SUFFIX, PersistentItem.TEXT_SUFFIX, value);
    assertThat(persistentItem.get("customKey" + PersistentItem.TEXT_SUFFIX), is(value));
  }

  @Test
  public void testAddPropertyWithSuffixBlankName() {
    persistentItem.addProperty("", PersistentItem.TEXT_SUFFIX, "value");
    assertFalse(persistentItem.containsKey(PersistentItem.TEXT_SUFFIX));
  }

  @Test
  public void testAddPropertyWithSuffixNullName() {
    persistentItem.addProperty(null, PersistentItem.TEXT_SUFFIX, "value");
    assertFalse(persistentItem.containsKey(PersistentItem.TEXT_SUFFIX));
  }

  @Test
  public void testAddPropertyWithSuffixBlankSuffix() {
    persistentItem.addProperty("key", "", "value");
    assertFalse(persistentItem.containsKey("key"));
  }

  @Test
  public void testAddPropertyWithSuffixNullSuffix() {
    persistentItem.addProperty("key", null, "value");
    assertFalse(persistentItem.containsKey("key"));
  }

  @Test
  public void testGetIdProperty() {
    String id = "test-id-456";
    persistentItem.put(PersistentItem.ID, id);
    assertThat(persistentItem.getIdProperty(), is(id));
  }

  @Test
  public void testGetIdPropertyWhenNotSet() {
    assertNull(persistentItem.getIdProperty());
  }

  @Test
  public void testGetTextProperty() {
    String value = "text value";
    persistentItem.put("key" + PersistentItem.TEXT_SUFFIX, value);
    assertThat(persistentItem.getTextProperty("key"), is(value));
  }

  @Test
  public void testGetTextPropertyWhenNotSet() {
    assertNull(persistentItem.getTextProperty("key"));
  }

  @Test
  public void testGetXmlProperty() {
    String xmlValue = "<xml>test</xml>";
    persistentItem.put("key" + PersistentItem.XML_SUFFIX, xmlValue);
    assertThat(persistentItem.getXmlProperty("key"), is(xmlValue));
  }

  @Test
  public void testGetXmlPropertyWhenNotSet() {
    assertNull(persistentItem.getXmlProperty("key"));
  }

  @Test
  public void testGetIntProperty() {
    Integer value = 100;
    persistentItem.put("key" + PersistentItem.INT_SUFFIX, value);
    assertThat(persistentItem.getIntProperty("key"), is(value));
  }

  @Test
  public void testGetIntPropertyWhenNotSet() {
    assertNull(persistentItem.getIntProperty("key"));
  }

  @Test
  public void testGetLongProperty() {
    Long value = 999999L;
    persistentItem.put("key" + PersistentItem.LONG_SUFFIX, value);
    assertThat(persistentItem.getLongProperty("key"), is(value));
  }

  @Test
  public void testGetLongPropertyWhenNotSet() {
    assertNull(persistentItem.getLongProperty("key"));
  }

  @Test
  public void testGetDateProperty() {
    Date value = new Date();
    persistentItem.put("key" + PersistentItem.DATE_SUFFIX, value);
    assertThat(persistentItem.getDateProperty("key"), is(value));
  }

  @Test
  public void testGetDatePropertyWhenNotSet() {
    assertNull(persistentItem.getDateProperty("key"));
  }

  @Test
  public void testGetBinaryProperty() {
    String value = "binary data";
    persistentItem.put("key" + PersistentItem.BINARY_SUFFIX, value);
    assertThat(persistentItem.getBinaryProperty("key"), is(value));
  }

  @Test
  public void testGetBinaryPropertyWhenNotSet() {
    assertNull(persistentItem.getBinaryProperty("key"));
  }

  @Test
  public void testGetTextSetProperty() {
    Set<String> value = new HashSet<>();
    value.add("a");
    value.add("b");
    persistentItem.put("key" + PersistentItem.TEXT_SUFFIX, value);
    assertThat(persistentItem.getTextSetProperty("key"), is(value));
  }

  @Test
  public void testGetTextSetPropertyWhenNotSet() {
    assertNull(persistentItem.getTextSetProperty("key"));
  }

  @Test
  public void testGetProperty() {
    String value = "test value";
    persistentItem.put("testKey", value);
    assertThat(persistentItem.getProperty("testKey"), is(value));
  }

  @Test
  public void testGetPropertyWithBlankName() {
    assertNull(persistentItem.getProperty(""));
  }

  @Test
  public void testGetPropertyWithNullName() {
    assertNull(persistentItem.getProperty(null));
  }

  @Test
  public void testGetPropertyWhenNotSet() {
    assertNull(persistentItem.getProperty("nonExistent"));
  }

  @Test
  public void testGetPropertyNames() {
    persistentItem.put("key1", "value1");
    persistentItem.put("key2", "value2");
    persistentItem.put("key3", "value3");

    Set<String> names = persistentItem.getPropertyNames();
    assertThat(names.size(), is(3));
    assertTrue(names.contains("key1"));
    assertTrue(names.contains("key2"));
    assertTrue(names.contains("key3"));
  }

  @Test
  public void testGetPropertyNamesWhenEmpty() {
    Set<String> names = persistentItem.getPropertyNames();
    assertNotNull(names);
    assertThat(names.size(), is(0));
  }

  @Test
  public void testPersistentItemIsHashMap() {
    assertTrue(persistentItem instanceof HashMap);
  }

  @Test
  public void testConstants() {
    assertThat(PersistentItem.ID, is("id_txt"));
    assertThat(PersistentItem.TEXT_SUFFIX, is("_txt"));
    assertThat(PersistentItem.XML_SUFFIX, is("_xml"));
    assertThat(PersistentItem.INT_SUFFIX, is("_int"));
    assertThat(PersistentItem.LONG_SUFFIX, is("_lng"));
    assertThat(PersistentItem.DATE_SUFFIX, is("_tdt"));
    assertThat(PersistentItem.BINARY_SUFFIX, is("_bin"));
  }

  @Test
  public void testCompleteWorkflow() {
    // Create an item with all types of properties
    persistentItem.addIdProperty("workflow-123");
    persistentItem.addProperty("title", "Test Workflow");
    persistentItem.addXmlProperty("metadata", "<meta>data</meta>");
    persistentItem.addProperty("count", 10);
    persistentItem.addProperty("timestamp", 1234567890L);
    persistentItem.addProperty("created", new Date());
    persistentItem.addProperty("data", new byte[] {1, 2, 3});

    Set<String> tags = new HashSet<>();
    tags.add("tag1");
    tags.add("tag2");
    persistentItem.addProperty("tags", tags);

    // Verify all properties are set
    assertThat(persistentItem.getIdProperty(), is("workflow-123"));
    assertThat(persistentItem.getTextProperty("title"), is("Test Workflow"));
    assertThat(persistentItem.getXmlProperty("metadata"), is("<meta>data</meta>"));
    assertThat(persistentItem.getIntProperty("count"), is(10));
    assertThat(persistentItem.getLongProperty("timestamp"), is(1234567890L));
    assertNotNull(persistentItem.getDateProperty("created"));
    assertArrayEquals(new byte[] {1, 2, 3}, (byte[]) persistentItem.get("data_bin"));
    assertThat(persistentItem.getTextSetProperty("tags"), is(tags));
  }
}
