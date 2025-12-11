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
package org.codice.ddf.admin.core.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class ConfigurationPropertiesImplTest {

  @Test
  public void testDefaultConstructor() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    assertThat(properties.isEmpty(), is(true));
    assertThat(properties.size(), is(0));
  }

  @Test
  public void testPutAndGet() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    properties.put("key1", "value1");
    properties.put("key2", 42);
    properties.put("key3", true);

    assertThat(properties.get("key1"), is(equalTo("value1")));
    assertThat(properties.get("key2"), is(equalTo(42)));
    assertThat(properties.get("key3"), is(equalTo(true)));
    assertThat(properties.size(), is(3));
  }

  @Test
  public void testMapBehavior() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    properties.put("testKey", "testValue");

    assertThat(properties.containsKey("testKey"), is(true));
    assertThat(properties.containsKey("nonExistentKey"), is(false));
    assertThat(properties.get("testKey"), is(equalTo("testValue")));
    assertThat(properties.get("nonExistentKey"), is(nullValue()));
  }

  @Test
  public void testRemove() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    properties.put("key1", "value1");
    properties.put("key2", "value2");

    assertThat(properties.size(), is(2));

    Object removed = properties.remove("key1");

    assertThat(removed, is(equalTo("value1")));
    assertThat(properties.size(), is(1));
    assertThat(properties.containsKey("key1"), is(false));
    assertThat(properties.containsKey("key2"), is(true));
  }

  @Test
  public void testClear() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    properties.put("key1", "value1");
    properties.put("key2", "value2");
    properties.put("key3", "value3");

    assertThat(properties.size(), is(3));

    properties.clear();

    assertThat(properties.isEmpty(), is(true));
    assertThat(properties.size(), is(0));
  }

  @Test
  public void testOverwriteValue() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    properties.put("key", "originalValue");
    assertThat(properties.get("key"), is(equalTo("originalValue")));

    properties.put("key", "updatedValue");
    assertThat(properties.get("key"), is(equalTo("updatedValue")));
    assertThat(properties.size(), is(1));
  }

  @Test
  public void testNullKey() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    properties.put(null, "value");

    assertThat(properties.containsKey(null), is(true));
    assertThat(properties.get(null), is(equalTo("value")));
  }

  @Test
  public void testNullValue() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    properties.put("key", null);

    assertThat(properties.containsKey("key"), is(true));
    assertThat(properties.get("key"), is(nullValue()));
  }

  @Test
  public void testDifferentObjectTypes() {
    ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    properties.put("stringKey", "stringValue");
    properties.put("intKey", 123);
    properties.put("longKey", 123L);
    properties.put("boolKey", false);
    properties.put("doubleKey", 3.14);

    assertThat(properties.get("stringKey"), is(equalTo("stringValue")));
    assertThat(properties.get("intKey"), is(equalTo(123)));
    assertThat(properties.get("longKey"), is(equalTo(123L)));
    assertThat(properties.get("boolKey"), is(equalTo(false)));
    assertThat(properties.get("doubleKey"), is(equalTo(3.14)));
  }
}
