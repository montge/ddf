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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OperationImplTest {

  // Concrete implementation for testing the abstract class
  private static class TestOperation extends OperationImpl {
    public TestOperation(Map<String, Serializable> properties) {
      super(properties);
    }
  }

  @Test
  void testConstructorWithProperties() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");
    properties.put("key2", 42);

    TestOperation operation = new TestOperation(properties);

    assertThat(operation.hasProperties(), is(true));
    assertThat(operation.getProperties(), hasEntry("key1", "value1"));
    assertThat(operation.getProperties(), hasEntry("key2", 42));
  }

  @Test
  void testConstructorWithNullPropertiesCreatesEmptyMap() {
    TestOperation operation = new TestOperation(null);

    assertThat(operation.hasProperties(), is(false));
    assertThat(operation.getProperties().isEmpty(), is(true));
  }

  @Test
  void testGetPropertyNames() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("name1", "value1");
    properties.put("name2", "value2");

    TestOperation operation = new TestOperation(properties);

    assertThat(operation.getPropertyNames(), containsInAnyOrder("name1", "name2"));
  }

  @Test
  void testGetPropertyValue() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("myKey", "myValue");

    TestOperation operation = new TestOperation(properties);

    assertThat(operation.getPropertyValue("myKey"), is("myValue"));
  }

  @Test
  void testGetPropertyValueReturnsNullForNonExistentKey() {
    Map<String, Serializable> properties = new HashMap<>();

    TestOperation operation = new TestOperation(properties);

    assertThat(operation.getPropertyValue("nonexistent"), is(nullValue()));
  }

  @Test
  void testContainsPropertyNameTrue() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("existingKey", "value");

    TestOperation operation = new TestOperation(properties);

    assertThat(operation.containsPropertyName("existingKey"), is(true));
  }

  @Test
  void testContainsPropertyNameFalse() {
    Map<String, Serializable> properties = new HashMap<>();

    TestOperation operation = new TestOperation(properties);

    assertThat(operation.containsPropertyName("missingKey"), is(false));
  }

  @Test
  void testHasPropertiesTrue() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key", "value");

    TestOperation operation = new TestOperation(properties);

    assertThat(operation.hasProperties(), is(true));
  }

  @Test
  void testHasPropertiesFalse() {
    Map<String, Serializable> properties = new HashMap<>();

    TestOperation operation = new TestOperation(properties);

    assertThat(operation.hasProperties(), is(false));
  }

  @Test
  void testGetProperties() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("prop1", "val1");
    properties.put("prop2", 100L);

    TestOperation operation = new TestOperation(properties);

    Map<String, Serializable> result = operation.getProperties();
    assertThat(result, hasEntry("prop1", "val1"));
    assertThat(result, hasEntry("prop2", 100L));
  }

  @Test
  void testSetProperties() {
    Map<String, Serializable> initialProps = new HashMap<>();
    initialProps.put("initial", "value");
    TestOperation operation = new TestOperation(initialProps);

    Map<String, Serializable> newProps = new HashMap<>();
    newProps.put("new", "newValue");
    operation.setProperties(newProps);

    assertThat(operation.getPropertyValue("new"), is("newValue"));
    assertThat(operation.containsPropertyName("initial"), is(false));
  }

  @Test
  void testEmptyPropertyNames() {
    TestOperation operation = new TestOperation(new HashMap<>());

    assertThat(operation.getPropertyNames().isEmpty(), is(true));
  }
}
