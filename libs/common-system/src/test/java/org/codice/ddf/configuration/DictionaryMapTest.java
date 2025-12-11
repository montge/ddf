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
package org.codice.ddf.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DictionaryMapTest {

  private DictionaryMap<String, String> dictionaryMap;

  @BeforeEach
  public void setUp() {
    dictionaryMap = new DictionaryMap<>();
  }

  @Test
  public void testDefaultConstructor() {
    DictionaryMap<String, String> map = new DictionaryMap<>();
    assertThat(map, notNullValue());
    assertThat(map.size(), is(0));
    assertThat(map.isEmpty(), is(true));
  }

  @Test
  public void testConstructorWithInitialCapacity() {
    DictionaryMap<String, String> map = new DictionaryMap<>(10);
    assertThat(map, notNullValue());
    assertThat(map.size(), is(0));
  }

  @Test
  public void testConstructorWithInitialCapacityAndLoadFactor() {
    DictionaryMap<String, String> map = new DictionaryMap<>(10, 0.75f);
    assertThat(map, notNullValue());
    assertThat(map.size(), is(0));
  }

  @Test
  public void testConstructorWithMap() {
    Map<String, String> sourceMap = new HashMap<>();
    sourceMap.put("key1", "value1");
    sourceMap.put("key2", "value2");

    DictionaryMap<String, String> map = new DictionaryMap<>(sourceMap);
    assertThat(map.size(), is(2));
    assertThat(map.get("key1"), is("value1"));
    assertThat(map.get("key2"), is("value2"));
  }

  @Test
  public void testPutAndGet() {
    dictionaryMap.put("key1", "value1");
    assertThat(dictionaryMap.get("key1"), is("value1"));
    assertThat(dictionaryMap.size(), is(1));
  }

  @Test
  public void testPutReturnsOldValue() {
    dictionaryMap.put("key1", "value1");
    String oldValue = dictionaryMap.put("key1", "value2");

    assertThat(oldValue, is("value1"));
    assertThat(dictionaryMap.get("key1"), is("value2"));
  }

  @Test
  public void testPutNullValue() {
    dictionaryMap.put("key1", null);
    assertThat(dictionaryMap.containsKey("key1"), is(true));
    assertThat(dictionaryMap.get("key1"), nullValue());
  }

  @Test
  public void testRemove() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");

    String removedValue = dictionaryMap.remove("key1");
    assertThat(removedValue, is("value1"));
    assertThat(dictionaryMap.size(), is(1));
    assertThat(dictionaryMap.containsKey("key1"), is(false));
  }

  @Test
  public void testRemoveNonExistentKey() {
    String removedValue = dictionaryMap.remove("nonexistent");
    assertThat(removedValue, nullValue());
  }

  @Test
  public void testSize() {
    assertThat(dictionaryMap.size(), is(0));

    dictionaryMap.put("key1", "value1");
    assertThat(dictionaryMap.size(), is(1));

    dictionaryMap.put("key2", "value2");
    assertThat(dictionaryMap.size(), is(2));

    dictionaryMap.remove("key1");
    assertThat(dictionaryMap.size(), is(1));
  }

  @Test
  public void testIsEmpty() {
    assertThat(dictionaryMap.isEmpty(), is(true));

    dictionaryMap.put("key1", "value1");
    assertThat(dictionaryMap.isEmpty(), is(false));

    dictionaryMap.remove("key1");
    assertThat(dictionaryMap.isEmpty(), is(true));
  }

  @Test
  public void testContainsKey() {
    dictionaryMap.put("key1", "value1");

    assertThat(dictionaryMap.containsKey("key1"), is(true));
    assertThat(dictionaryMap.containsKey("key2"), is(false));
  }

  @Test
  public void testContainsValue() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");

    assertThat(dictionaryMap.containsValue("value1"), is(true));
    assertThat(dictionaryMap.containsValue("value3"), is(false));
  }

  @Test
  public void testPutAll() {
    Map<String, String> sourceMap = new HashMap<>();
    sourceMap.put("key1", "value1");
    sourceMap.put("key2", "value2");
    sourceMap.put("key3", "value3");

    dictionaryMap.putAll(sourceMap);

    assertThat(dictionaryMap.size(), is(3));
    assertThat(dictionaryMap.get("key1"), is("value1"));
    assertThat(dictionaryMap.get("key2"), is("value2"));
    assertThat(dictionaryMap.get("key3"), is("value3"));
  }

  @Test
  public void testClear() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");

    assertThat(dictionaryMap.size(), is(2));

    dictionaryMap.clear();

    assertThat(dictionaryMap.size(), is(0));
    assertThat(dictionaryMap.isEmpty(), is(true));
  }

  @Test
  public void testKeySet() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");
    dictionaryMap.put("key3", "value3");

    Set<String> keySet = dictionaryMap.keySet();

    assertThat(keySet, notNullValue());
    assertThat(keySet.size(), is(3));
    assertThat(keySet, containsInAnyOrder("key1", "key2", "key3"));
  }

  @Test
  public void testValues() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");
    dictionaryMap.put("key3", "value3");

    Collection<String> values = dictionaryMap.values();

    assertThat(values, notNullValue());
    assertThat(values.size(), is(3));
    assertThat(values, containsInAnyOrder("value1", "value2", "value3"));
  }

  @Test
  public void testEntrySet() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");

    Set<Map.Entry<String, String>> entrySet = dictionaryMap.entrySet();

    assertThat(entrySet, notNullValue());
    assertThat(entrySet.size(), is(2));
  }

  @Test
  public void testKeys() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");
    dictionaryMap.put("key3", "value3");

    Enumeration<String> keys = dictionaryMap.keys();

    assertThat(keys, notNullValue());
    int count = 0;
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      assertThat(dictionaryMap.containsKey(key), is(true));
      count++;
    }
    assertThat(count, is(3));
  }

  @Test
  public void testElements() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");
    dictionaryMap.put("key3", "value3");

    Enumeration<String> elements = dictionaryMap.elements();

    assertThat(elements, notNullValue());
    int count = 0;
    while (elements.hasMoreElements()) {
      String value = elements.nextElement();
      assertThat(dictionaryMap.containsValue(value), is(true));
      count++;
    }
    assertThat(count, is(3));
  }

  @Test
  public void testOfSingleEntry() {
    DictionaryMap<String, String> map = DictionaryMap.of("key1", "value1");

    assertThat(map, notNullValue());
    assertThat(map.size(), is(1));
    assertThat(map.get("key1"), is("value1"));
  }

  @Test
  public void testOfTwoEntries() {
    DictionaryMap<String, String> map = DictionaryMap.of("key1", "value1", "key2", "value2");

    assertThat(map, notNullValue());
    assertThat(map.size(), is(2));
    assertThat(map.get("key1"), is("value1"));
    assertThat(map.get("key2"), is("value2"));
  }

  @Test
  public void testOfThreeEntries() {
    DictionaryMap<String, String> map =
        DictionaryMap.of("key1", "value1", "key2", "value2", "key3", "value3");

    assertThat(map, notNullValue());
    assertThat(map.size(), is(3));
    assertThat(map.get("key1"), is("value1"));
    assertThat(map.get("key2"), is("value2"));
    assertThat(map.get("key3"), is("value3"));
  }

  @Test
  public void testMapInterfaceCompatibility() {
    Map<String, String> map = dictionaryMap;
    map.put("key1", "value1");

    assertThat(map.get("key1"), is("value1"));
    assertThat(map.containsKey("key1"), is(true));
  }

  @Test
  public void testDictionaryInterfaceCompatibility() {
    java.util.Dictionary<String, String> dict = dictionaryMap;
    dict.put("key1", "value1");

    assertThat(dict.get("key1"), is("value1"));
    assertThat(dict.size(), is(1));
  }

  @Test
  public void testWithIntegerTypes() {
    DictionaryMap<Integer, Integer> intMap = new DictionaryMap<>();
    intMap.put(1, 100);
    intMap.put(2, 200);

    assertThat(intMap.get(1), is(100));
    assertThat(intMap.get(2), is(200));
    assertThat(intMap.size(), is(2));
  }

  @Test
  public void testWithMixedTypes() {
    DictionaryMap<String, Object> mixedMap = new DictionaryMap<>();
    mixedMap.put("string", "value");
    mixedMap.put("integer", 42);
    mixedMap.put("boolean", true);

    assertThat(mixedMap.get("string"), is("value"));
    assertThat(mixedMap.get("integer"), is(42));
    assertThat(mixedMap.get("boolean"), is(true));
  }

  @Test
  public void testModifyThroughKeySet() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");

    Set<String> keySet = dictionaryMap.keySet();
    keySet.remove("key1");

    assertThat(dictionaryMap.size(), is(1));
    assertThat(dictionaryMap.containsKey("key1"), is(false));
  }

  @Test
  public void testModifyThroughEntrySet() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key2", "value2");

    Set<Map.Entry<String, String>> entrySet = dictionaryMap.entrySet();
    for (Map.Entry<String, String> entry : entrySet) {
      if (entry.getKey().equals("key1")) {
        entry.setValue("modified");
      }
    }

    assertThat(dictionaryMap.get("key1"), is("modified"));
  }

  @Test
  public void testLargeDataSet() {
    int numEntries = 1000;
    for (int i = 0; i < numEntries; i++) {
      dictionaryMap.put("key" + i, "value" + i);
    }

    assertThat(dictionaryMap.size(), is(numEntries));

    for (int i = 0; i < numEntries; i++) {
      assertThat(dictionaryMap.get("key" + i), is("value" + i));
    }
  }

  @Test
  public void testGetNonExistentKey() {
    assertThat(dictionaryMap.get("nonexistent"), nullValue());
  }

  @Test
  public void testMultipleUpdates() {
    dictionaryMap.put("key1", "value1");
    dictionaryMap.put("key1", "value2");
    dictionaryMap.put("key1", "value3");

    assertThat(dictionaryMap.get("key1"), is("value3"));
    assertThat(dictionaryMap.size(), is(1));
  }
}
