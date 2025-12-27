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
package org.codice.ddf.admin.application.service.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Version;

class JsonUtilsTest {

  @Test
  void testToJsonWithSimpleObject() {
    TestPojo pojo = new TestPojo("test", 42);
    String json = JsonUtils.toJson(pojo);

    assertThat(json, containsString("\"name\":\"test\""));
    assertThat(json, containsString("\"value\":42"));
  }

  @Test
  void testFromJsonWithSimpleObject() {
    String json = "{\"name\":\"hello\",\"value\":123}";
    TestPojo pojo = JsonUtils.fromJson(json, TestPojo.class);

    assertThat(pojo, is(notNullValue()));
    assertThat(pojo.getName(), is("hello"));
    assertThat(pojo.getValue(), is(123));
  }

  @Test
  void testToJsonWithNull() {
    String json = JsonUtils.toJson(null);
    assertThat(json, is("null"));
  }

  @Test
  void testFromJsonWithNullString() {
    TestPojo pojo = JsonUtils.fromJson("null", TestPojo.class);
    assertThat(pojo, is((TestPojo) null));
  }

  @Test
  void testToJsonWithEmptyList() {
    List<String> emptyList = new ArrayList<>();
    String json = JsonUtils.toJson(emptyList);

    assertThat(json, is("null"));
  }

  @Test
  void testToJsonWithNonEmptyList() {
    List<String> list = Arrays.asList("a", "b", "c");
    String json = JsonUtils.toJson(list);

    assertThat(json, containsString("\"a\""));
    assertThat(json, containsString("\"b\""));
    assertThat(json, containsString("\"c\""));
  }

  @Test
  void testToJsonWithEmptyMap() {
    Map<String, String> emptyMap = new HashMap<>();
    String json = JsonUtils.toJson(emptyMap);

    assertThat(json, is("null"));
  }

  @Test
  void testToJsonWithNonEmptyMap() {
    Map<String, Integer> map = new HashMap<>();
    map.put("key1", 1);
    map.put("key2", 2);
    String json = JsonUtils.toJson(map);

    assertThat(json, containsString("\"key1\":1"));
    assertThat(json, containsString("\"key2\":2"));
  }

  @Test
  void testToJsonWithVersion() {
    Version version = new Version("1.2.3");
    String json = JsonUtils.toJson(version);

    assertThat(json, containsString("1.2.3"));
  }

  @Test
  void testFromJsonWithType() {
    String json = "[\"item1\",\"item2\",\"item3\"]";
    Type listType = new TypeToken<List<String>>() {}.getType();
    List<String> result = JsonUtils.fromJson(json, listType);

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(3));
    assertThat(result.get(0), is("item1"));
  }

  @Test
  void testWriteValueToOutputStream() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    TestPojo pojo = new TestPojo("stream-test", 99);

    JsonUtils.writeValue(outputStream, pojo);

    String result = outputStream.toString();
    assertThat(result, containsString("\"name\":\"stream-test\""));
    assertThat(result, containsString("\"value\":99"));
  }

  @Test
  void testFromJsonWithValidatable() {
    String json = "{\"data\":\"valid\"}";
    ValidatablePojo pojo = JsonUtils.fromJson(json, ValidatablePojo.class);

    assertThat(pojo, is(notNullValue()));
    assertThat(pojo.getData(), is("valid"));
    assertThat(pojo.isValidated(), is(true));
  }

  @Test
  void testFromJsonWithInvalidValidatable() {
    String json = "{\"data\":null}";

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> JsonUtils.fromJson(json, ValidatablePojo.class));
  }

  @Test
  void testToJsonWithNestedObject() {
    Map<String, Object> nested = new HashMap<>();
    nested.put("inner", new TestPojo("nested", 1));
    String json = JsonUtils.toJson(nested);

    assertThat(json, containsString("\"inner\""));
    assertThat(json, containsString("\"name\":\"nested\""));
  }

  @Test
  void testFromJsonWithEmptyObject() {
    String json = "{}";
    TestPojo pojo = JsonUtils.fromJson(json, TestPojo.class);

    assertThat(pojo, is(notNullValue()));
    assertThat(pojo.getName(), is((String) null));
    assertThat(pojo.getValue(), is(0));
  }

  static class TestPojo {
    private String name;
    private int value;

    public TestPojo() {}

    public TestPojo(String name, int value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public int getValue() {
      return value;
    }
  }

  static class ValidatablePojo implements JsonValidatable {
    private String data;
    private transient boolean validated = false;

    public String getData() {
      return data;
    }

    public boolean isValidated() {
      return validated;
    }

    @Override
    public void validate() {
      if (data == null) {
        throw new IllegalArgumentException("data cannot be null");
      }
      validated = true;
    }
  }
}
