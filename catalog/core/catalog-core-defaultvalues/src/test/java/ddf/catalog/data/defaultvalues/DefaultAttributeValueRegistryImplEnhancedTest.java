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
package ddf.catalog.data.defaultvalues;

import static ddf.catalog.data.Metacard.POINT_OF_CONTACT;
import static ddf.catalog.data.Metacard.TAGS;
import static ddf.catalog.data.Metacard.TITLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ddf.catalog.data.DefaultAttributeValueRegistry;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Enhanced comprehensive tests for DefaultAttributeValueRegistryImpl to increase coverage. Tests
 * global/metacard-specific defaults, concurrent access, all data types, and edge cases.
 */
public class DefaultAttributeValueRegistryImplEnhancedTest {

  private static final String METACARD_TYPE_1 = "type1";
  private static final String METACARD_TYPE_2 = "type2";
  private static final String DEFAULT_VALUE_1 = "default1";
  private static final String DEFAULT_VALUE_2 = "default2";
  private static final String DEFAULT_VALUE_3 = "default3";

  private DefaultAttributeValueRegistry registry;

  @BeforeEach
  public void setUp() {
    registry = new DefaultAttributeValueRegistryImpl();
  }

  @Test
  public void testSetGlobalDefaultValue() {
    registry.setDefaultValue(TITLE, DEFAULT_VALUE_1);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(DEFAULT_VALUE_1));
  }

  @Test
  public void testSetMetacardSpecificDefaultValue() {
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_1);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(DEFAULT_VALUE_1));
  }

  @Test
  public void testMetacardSpecificOverridesGlobal() {
    registry.setDefaultValue(TITLE, DEFAULT_VALUE_1);
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_2);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(DEFAULT_VALUE_2));
  }

  @Test
  public void testGlobalDefaultUsedWhenNoMetacardSpecific() {
    registry.setDefaultValue(TITLE, DEFAULT_VALUE_1);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(DEFAULT_VALUE_1));
  }

  @Test
  public void testMultipleMetacardTypesDifferentDefaults() {
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_1);
    registry.setDefaultValue(METACARD_TYPE_2, TITLE, DEFAULT_VALUE_2);

    Optional<Serializable> result1 = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    Optional<Serializable> result2 = registry.getDefaultValue(METACARD_TYPE_2, TITLE);

    assertThat(result1.get(), is(DEFAULT_VALUE_1));
    assertThat(result2.get(), is(DEFAULT_VALUE_2));
  }

  @Test
  public void testMultipleAttributesSameMetacardType() {
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_1);
    registry.setDefaultValue(METACARD_TYPE_1, POINT_OF_CONTACT, DEFAULT_VALUE_2);
    registry.setDefaultValue(METACARD_TYPE_1, TAGS, DEFAULT_VALUE_3);

    assertThat(registry.getDefaultValue(METACARD_TYPE_1, TITLE).get(), is(DEFAULT_VALUE_1));
    assertThat(
        registry.getDefaultValue(METACARD_TYPE_1, POINT_OF_CONTACT).get(), is(DEFAULT_VALUE_2));
    assertThat(registry.getDefaultValue(METACARD_TYPE_1, TAGS).get(), is(DEFAULT_VALUE_3));
  }

  @Test
  public void testOverwriteGlobalDefault() {
    registry.setDefaultValue(TITLE, DEFAULT_VALUE_1);
    registry.setDefaultValue(TITLE, DEFAULT_VALUE_2);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.get(), is(DEFAULT_VALUE_2));
  }

  @Test
  public void testOverwriteMetacardSpecificDefault() {
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_1);
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_2);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.get(), is(DEFAULT_VALUE_2));
  }

  @Test
  public void testRemoveGlobalDefaultValue() {
    registry.setDefaultValue(TITLE, DEFAULT_VALUE_1);
    registry.removeDefaultValue(TITLE);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void testRemoveMetacardSpecificDefaultValue() {
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_1);
    registry.removeDefaultValue(METACARD_TYPE_1, TITLE);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void testRemoveAllGlobalDefaults() {
    registry.setDefaultValue(TITLE, DEFAULT_VALUE_1);
    registry.setDefaultValue(POINT_OF_CONTACT, DEFAULT_VALUE_2);
    registry.setDefaultValue(TAGS, DEFAULT_VALUE_3);

    registry.removeDefaultValues();

    assertThat(registry.getDefaultValue(METACARD_TYPE_1, TITLE).isPresent(), is(false));
    assertThat(registry.getDefaultValue(METACARD_TYPE_1, POINT_OF_CONTACT).isPresent(), is(false));
    assertThat(registry.getDefaultValue(METACARD_TYPE_1, TAGS).isPresent(), is(false));
  }

  @Test
  public void testRemoveAllDefaultsForMetacardType() {
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_1);
    registry.setDefaultValue(METACARD_TYPE_1, POINT_OF_CONTACT, DEFAULT_VALUE_2);
    registry.setDefaultValue(METACARD_TYPE_2, TITLE, DEFAULT_VALUE_3);

    registry.removeDefaultValues(METACARD_TYPE_1);

    assertThat(registry.getDefaultValue(METACARD_TYPE_1, TITLE).isPresent(), is(false));
    assertThat(registry.getDefaultValue(METACARD_TYPE_1, POINT_OF_CONTACT).isPresent(), is(false));
    assertThat(registry.getDefaultValue(METACARD_TYPE_2, TITLE).get(), is(DEFAULT_VALUE_3));
  }

  @Test
  public void testRemoveMetacardDefaultFallsBackToGlobal() {
    registry.setDefaultValue(TITLE, DEFAULT_VALUE_1);
    registry.setDefaultValue(METACARD_TYPE_1, TITLE, DEFAULT_VALUE_2);

    registry.removeDefaultValue(METACARD_TYPE_1, TITLE);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.get(), is(DEFAULT_VALUE_1));
  }

  @Test
  public void testGetNonExistentDefault() {
    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, "nonexistent");
    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void testSetGlobalDefaultWithNullAttribute() {
    assertThrows(
        IllegalArgumentException.class, () -> registry.setDefaultValue(null, DEFAULT_VALUE_1));
  }

  @Test
  public void testSetGlobalDefaultWithNullValue() {
    assertThrows(IllegalArgumentException.class, () -> registry.setDefaultValue(TITLE, null));
  }

  @Test
  public void testSetMetacardDefaultWithNullMetacardType() {
    assertThrows(
        IllegalArgumentException.class,
        () -> registry.setDefaultValue(null, TITLE, DEFAULT_VALUE_1));
  }

  @Test
  public void testSetMetacardDefaultWithNullAttribute() {
    assertThrows(
        IllegalArgumentException.class,
        () -> registry.setDefaultValue(METACARD_TYPE_1, null, DEFAULT_VALUE_1));
  }

  @Test
  public void testSetMetacardDefaultWithNullValue() {
    assertThrows(
        IllegalArgumentException.class,
        () -> registry.setDefaultValue(METACARD_TYPE_1, TITLE, null));
  }

  @Test
  public void testGetDefaultWithNullMetacardType() {
    assertThrows(IllegalArgumentException.class, () -> registry.getDefaultValue(null, TITLE));
  }

  @Test
  public void testGetDefaultWithNullAttribute() {
    assertThrows(
        IllegalArgumentException.class, () -> registry.getDefaultValue(METACARD_TYPE_1, null));
  }

  @Test
  public void testRemoveGlobalDefaultWithNullAttribute() {
    assertThrows(IllegalArgumentException.class, () -> registry.removeDefaultValue(null));
  }

  @Test
  public void testRemoveMetacardDefaultWithNullMetacardType() {
    assertThrows(IllegalArgumentException.class, () -> registry.removeDefaultValue(null, TITLE));
  }

  @Test
  public void testRemoveMetacardDefaultWithNullAttribute() {
    assertThrows(
        IllegalArgumentException.class, () -> registry.removeDefaultValue(METACARD_TYPE_1, null));
  }

  @Test
  public void testRemoveAllMetacardDefaultsWithNullMetacardType() {
    assertThrows(IllegalArgumentException.class, () -> registry.removeDefaultValues(null));
  }

  @Test
  public void testIntegerDefaultValue() {
    Integer intValue = 42;
    registry.setDefaultValue(METACARD_TYPE_1, "count", intValue);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, "count");
    assertThat(result.get(), is(intValue));
  }

  @Test
  public void testLongDefaultValue() {
    Long longValue = 123456789L;
    registry.setDefaultValue(METACARD_TYPE_1, "timestamp", longValue);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, "timestamp");
    assertThat(result.get(), is(longValue));
  }

  @Test
  public void testDoubleDefaultValue() {
    Double doubleValue = 3.14159;
    registry.setDefaultValue(METACARD_TYPE_1, "pi", doubleValue);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, "pi");
    assertThat(result.get(), is(doubleValue));
  }

  @Test
  public void testBooleanDefaultValue() {
    Boolean boolValue = true;
    registry.setDefaultValue(METACARD_TYPE_1, "active", boolValue);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, "active");
    assertThat(result.get(), is(boolValue));
  }

  @Test
  public void testDateDefaultValue() {
    Date dateValue = new Date();
    registry.setDefaultValue(METACARD_TYPE_1, "created", dateValue);

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, "created");
    assertThat(result.get(), is(dateValue));
  }

  @Test
  public void testConcurrentSetGlobalDefaults() throws InterruptedException {
    int threadCount = 10;
    int valuesPerThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      executor.submit(
          () -> {
            try {
              for (int i = 0; i < valuesPerThread; i++) {
                registry.setDefaultValue("attr_" + threadId + "_" + i, "value_" + i);
              }
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    // Verify all defaults are set
    for (int t = 0; t < threadCount; t++) {
      for (int i = 0; i < valuesPerThread; i++) {
        Optional<Serializable> result =
            registry.getDefaultValue(METACARD_TYPE_1, "attr_" + t + "_" + i);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("value_" + i));
      }
    }
  }

  @Test
  public void testConcurrentSetMetacardDefaults() throws InterruptedException {
    int threadCount = 10;
    int valuesPerThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      executor.submit(
          () -> {
            try {
              for (int i = 0; i < valuesPerThread; i++) {
                registry.setDefaultValue(
                    "type_" + threadId, "attr_" + i, "value_" + threadId + "_" + i);
              }
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    // Verify all defaults are set
    for (int t = 0; t < threadCount; t++) {
      for (int i = 0; i < valuesPerThread; i++) {
        Optional<Serializable> result = registry.getDefaultValue("type_" + t, "attr_" + i);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("value_" + t + "_" + i));
      }
    }
  }

  @Test
  public void testConcurrentReads() throws InterruptedException {
    // Set up defaults
    for (int i = 0; i < 50; i++) {
      registry.setDefaultValue(METACARD_TYPE_1, "attr_" + i, "value_" + i);
    }

    int threadCount = 20;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger(0);

    for (int t = 0; t < threadCount; t++) {
      executor.submit(
          () -> {
            try {
              for (int i = 0; i < 50; i++) {
                Optional<Serializable> result =
                    registry.getDefaultValue(METACARD_TYPE_1, "attr_" + i);
                if (result.isPresent() && result.get().equals("value_" + i)) {
                  successCount.incrementAndGet();
                }
              }
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    assertThat(successCount.get(), is(threadCount * 50));
  }

  @Test
  public void testConcurrentMixedOperations() throws InterruptedException {
    int threadCount = 15;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      executor.submit(
          () -> {
            try {
              for (int i = 0; i < 50; i++) {
                String attr = "mixed_attr_" + (threadId % 5);
                String value = "value_" + threadId + "_" + i;

                // Set default
                if (i % 3 == 0) {
                  registry.setDefaultValue(attr, value);
                } else if (i % 3 == 1) {
                  registry.setDefaultValue(METACARD_TYPE_1, attr, value);
                }

                // Read default
                registry.getDefaultValue(METACARD_TYPE_1, attr);

                // Remove some
                if (i % 7 == 0) {
                  registry.removeDefaultValue(attr);
                }
              }
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await(15, TimeUnit.SECONDS);
    executor.shutdown();

    // Just verify no exceptions occurred
    assertTrue(true);
  }

  @Test
  public void testLargeNumberOfDefaults() {
    int count = 1000;
    for (int i = 0; i < count; i++) {
      registry.setDefaultValue("attr_" + i, "value_" + i);
    }

    for (int i = 0; i < count; i++) {
      Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, "attr_" + i);
      assertThat(result.isPresent(), is(true));
      assertThat(result.get(), is("value_" + i));
    }
  }

  @Test
  public void testRemoveNonExistentGlobalDefault() {
    // Should not throw exception
    registry.removeDefaultValue("nonexistent");
  }

  @Test
  public void testRemoveNonExistentMetacardDefault() {
    // Should not throw exception
    registry.removeDefaultValue(METACARD_TYPE_1, "nonexistent");
  }

  @Test
  public void testRemoveAllFromNonExistentMetacardType() {
    // Should not throw exception
    registry.removeDefaultValues("nonexistent");
  }

  @Test
  public void testComplexDefaultValueScenario() {
    // Set global defaults
    registry.setDefaultValue(TITLE, "Global Title");
    registry.setDefaultValue(POINT_OF_CONTACT, "Global Contact");

    // Override for type1
    registry.setDefaultValue("type1", TITLE, "Type1 Title");

    // Override for type2
    registry.setDefaultValue("type2", POINT_OF_CONTACT, "Type2 Contact");

    // Verify
    assertThat(registry.getDefaultValue("type1", TITLE).get(), is("Type1 Title"));
    assertThat(registry.getDefaultValue("type1", POINT_OF_CONTACT).get(), is("Global Contact"));
    assertThat(registry.getDefaultValue("type2", TITLE).get(), is("Global Title"));
    assertThat(registry.getDefaultValue("type2", POINT_OF_CONTACT).get(), is("Type2 Contact"));
    assertThat(registry.getDefaultValue("type3", TITLE).get(), is("Global Title"));
    assertThat(registry.getDefaultValue("type3", POINT_OF_CONTACT).get(), is("Global Contact"));
  }

  @Test
  public void testEmptyRegistryBehavior() {
    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void testMultipleUpdatesToSameDefault() {
    registry.setDefaultValue(TITLE, "value1");
    registry.setDefaultValue(TITLE, "value2");
    registry.setDefaultValue(TITLE, "value3");
    registry.setDefaultValue(TITLE, "value4");

    Optional<Serializable> result = registry.getDefaultValue(METACARD_TYPE_1, TITLE);
    assertThat(result.get(), is("value4"));
  }
}
