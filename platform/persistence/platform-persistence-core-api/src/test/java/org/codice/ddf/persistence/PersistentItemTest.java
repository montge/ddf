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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersistentItemTest {

  private PersistentItem item;

  @BeforeEach
  void setUp() {
    item = new PersistentItem();
  }

  @Test
  void testExtendsHashMap() {
    assertThat(item, instanceOf(HashMap.class));
  }

  @Test
  void testAddIdProperty() {
    item.addIdProperty("test-id-123");

    assertThat(item.getIdProperty(), is("test-id-123"));
  }

  @Test
  void testAddIdPropertyWithNull() {
    item.addIdProperty(null);

    assertThat(item.getIdProperty(), is(nullValue()));
  }

  @Test
  void testAddTextProperty() {
    item.addProperty("name", "John Doe");

    assertThat(item.getTextProperty("name"), is("John Doe"));
    assertThat(item.containsKey("name_txt"), is(true));
  }

  @Test
  void testAddXmlProperty() {
    String xml = "<root><child>value</child></root>";
    item.addXmlProperty("metadata", xml);

    assertThat(item.getXmlProperty("metadata"), is(xml));
    assertThat(item.containsKey("metadata_xml"), is(true));
  }

  @Test
  void testAddIntProperty() {
    item.addProperty("count", 42);

    assertThat(item.getIntProperty("count"), is(42));
    assertThat(item.containsKey("count_int"), is(true));
  }

  @Test
  void testAddLongProperty() {
    item.addProperty("timestamp", 1234567890123L);

    assertThat(item.getLongProperty("timestamp"), is(1234567890123L));
    assertThat(item.containsKey("timestamp_lng"), is(true));
  }

  @Test
  void testAddDateProperty() {
    Date date = new Date();
    item.addProperty("created", date);

    assertThat(item.getDateProperty("created"), is(date));
    assertThat(item.containsKey("created_tdt"), is(true));
  }

  @Test
  void testAddBinaryProperty() {
    byte[] data = new byte[] {1, 2, 3, 4, 5};
    item.addProperty("content", data);

    assertThat(item.containsKey("content_bin"), is(true));
  }

  @Test
  void testAddSetProperty() {
    Set<String> tags = new HashSet<>();
    tags.add("tag1");
    tags.add("tag2");
    item.addProperty("tags", tags);

    Set<String> result = item.getTextSetProperty("tags");
    assertThat(result, hasSize(2));
    assertThat(result, containsInAnyOrder("tag1", "tag2"));
  }

  @Test
  void testAddPropertyWithObjectString() {
    Object value = "string value";
    item.addProperty("field", value);

    assertThat(item.getTextProperty("field"), is("string value"));
  }

  @Test
  void testAddPropertyWithObjectInteger() {
    Object value = Integer.valueOf(100);
    item.addProperty("field", value);

    assertThat(item.getIntProperty("field"), is(100));
  }

  @Test
  void testAddPropertyWithObjectLong() {
    Object value = Long.valueOf(999999999999L);
    item.addProperty("field", value);

    assertThat(item.getLongProperty("field"), is(999999999999L));
  }

  @Test
  void testAddPropertyWithObjectDate() {
    Date date = new Date();
    Object value = date;
    item.addProperty("field", value);

    assertThat(item.getDateProperty("field"), is(date));
  }

  @Test
  void testAddPropertyWithNull() {
    item.addProperty("field", (Object) null);

    assertThat(item.isEmpty(), is(true));
  }

  @Test
  void testAddPropertyWithSuffixAlreadyPresent() {
    item.addProperty("name_txt", "_txt", "value");

    assertThat(item.containsKey("name_txt"), is(true));
    assertThat(item.get("name_txt"), is("value"));
  }

  @Test
  void testAddPropertyWithBlankName() {
    item.addProperty("", "value");

    assertThat(item.isEmpty(), is(true));
  }

  @Test
  void testGetPropertyWithBlankName() {
    Object result = item.getProperty("");

    assertThat(result, is(nullValue()));
  }

  @Test
  void testGetPropertyWithNullName() {
    Object result = item.getProperty(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testGetPropertyNames() {
    item.addProperty("field1", "value1");
    item.addProperty("field2", 123);

    Set<String> names = item.getPropertyNames();

    assertThat(names, hasSize(2));
    assertThat(names, containsInAnyOrder("field1_txt", "field2_int"));
  }

  @Test
  void testStripSuffixesRemovesTextSuffix() {
    Map<String, Object> input = new HashMap<>();
    input.put("name_txt", "John");

    Map<String, Object> result = PersistentItem.stripSuffixes(input);

    assertThat(result.containsKey("name"), is(true));
    assertThat(result.get("name"), is("John"));
  }

  @Test
  void testStripSuffixesRemovesIntSuffix() {
    Map<String, Object> input = new HashMap<>();
    input.put("count_int", 42);

    Map<String, Object> result = PersistentItem.stripSuffixes(input);

    assertThat(result.containsKey("count"), is(true));
    assertThat(result.get("count"), is(42));
  }

  @Test
  void testStripSuffixesRemovesLongSuffix() {
    Map<String, Object> input = new HashMap<>();
    input.put("value_lng", 123456789L);

    Map<String, Object> result = PersistentItem.stripSuffixes(input);

    assertThat(result.containsKey("value"), is(true));
  }

  @Test
  void testStripSuffixesRemovesDateSuffix() {
    Map<String, Object> input = new HashMap<>();
    Date date = new Date();
    input.put("created_tdt", date);

    Map<String, Object> result = PersistentItem.stripSuffixes(input);

    assertThat(result.containsKey("created"), is(true));
    assertThat(result.get("created"), is(date));
  }

  @Test
  void testStripSuffixesRemovesXmlSuffix() {
    Map<String, Object> input = new HashMap<>();
    input.put("metadata_xml", "<xml/>");

    Map<String, Object> result = PersistentItem.stripSuffixes(input);

    assertThat(result.containsKey("metadata"), is(true));
  }

  @Test
  void testStripSuffixesRemovesBinarySuffix() {
    Map<String, Object> input = new HashMap<>();
    input.put("data_bin", new byte[] {1, 2, 3});

    Map<String, Object> result = PersistentItem.stripSuffixes(input);

    assertThat(result.containsKey("data"), is(true));
  }

  @Test
  void testStripSuffixesWithNoSuffix() {
    Map<String, Object> input = new HashMap<>();
    input.put("nosuffix", "value");

    Map<String, Object> result = PersistentItem.stripSuffixes(input);

    assertThat(result.containsKey("nosuffix"), is(true));
  }

  @Test
  void testStripSuffixesWithEmptyMap() {
    Map<String, Object> input = new HashMap<>();

    Map<String, Object> result = PersistentItem.stripSuffixes(input);

    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void testConstantValues() {
    assertThat(PersistentItem.ID, is("id_txt"));
    assertThat(PersistentItem.TEXT_SUFFIX, is("_txt"));
    assertThat(PersistentItem.XML_SUFFIX, is("_xml"));
    assertThat(PersistentItem.INT_SUFFIX, is("_int"));
    assertThat(PersistentItem.LONG_SUFFIX, is("_lng"));
    assertThat(PersistentItem.DATE_SUFFIX, is("_tdt"));
    assertThat(PersistentItem.BINARY_SUFFIX, is("_bin"));
  }
}
