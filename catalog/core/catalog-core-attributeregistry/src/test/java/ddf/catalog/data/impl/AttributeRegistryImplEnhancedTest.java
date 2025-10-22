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
import static org.hamcrest.Matchers.is;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;

/**
 * Enhanced comprehensive tests for AttributeRegistryImpl to increase coverage. Tests registration,
 * deregistration, lookups, concurrent access, and edge cases.
 */
public class AttributeRegistryImplEnhancedTest {

  private AttributeRegistryImpl registry;

  @Before
  public void setUp() {
    registry = new AttributeRegistryImpl();
  }

  @Test
  public void testRegisterSingleDescriptor() {
    AttributeDescriptor descriptor =
        new AttributeDescriptorImpl("testAttr", true, true, false, false, BasicTypes.STRING_TYPE);

    registry.register(descriptor);

    Optional<AttributeDescriptor> retrieved = registry.lookup("testAttr");
    assertThat(retrieved.isPresent(), is(true));
    assertThat(retrieved.get(), is(descriptor));
  }

  @Test
  public void testDeregisterSingleDescriptor() {
    AttributeDescriptor descriptor =
        new AttributeDescriptorImpl("testAttr", true, true, false, false, BasicTypes.STRING_TYPE);

    registry.register(descriptor);
    assertThat(registry.lookup("testAttr").isPresent(), is(true));

    registry.deregister(descriptor);
    assertThat(registry.lookup("testAttr").isPresent(), is(false));
  }

  @Test
  public void testLookupNonExistentAttribute() {
    Optional<AttributeDescriptor> result = registry.lookup("nonexistent");

    assertThat(result.isPresent(), is(false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRegisterNullDescriptor() {
    registry.register(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRegisterDescriptorWithNullName() {
    AttributeDescriptor descriptor =
        new AttributeDescriptorImpl(null, true, true, false, false, BasicTypes.STRING_TYPE);
    registry.register(descriptor);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeregisterNullDescriptor() {
    registry.deregister(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeregisterDescriptorWithNullName() {
    AttributeDescriptor descriptor =
        new AttributeDescriptorImpl(null, true, true, false, false, BasicTypes.STRING_TYPE);
    registry.deregister(descriptor);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLookupWithNullName() {
    registry.lookup(null);
  }

  @Test
  public void testRegisterMultipleDescriptorsWithSameName() {
    AttributeDescriptor descriptor1 =
        new AttributeDescriptorImpl("sameName", true, true, false, false, BasicTypes.STRING_TYPE);
    AttributeDescriptor descriptor2 =
        new AttributeDescriptorImpl("sameName", false, false, true, true, BasicTypes.INTEGER_TYPE);

    registry.register(descriptor1);
    registry.register(descriptor2);

    Optional<AttributeDescriptor> retrieved = registry.lookup("sameName");
    assertThat(retrieved.isPresent(), is(true));
    // Should return the first registered descriptor
    assertThat(retrieved.get(), is(descriptor1));
  }

  @Test
  public void testRegisterDuplicateDescriptor() {
    AttributeDescriptor descriptor =
        new AttributeDescriptorImpl("duplicate", true, true, false, false, BasicTypes.STRING_TYPE);

    registry.register(descriptor);
    registry.register(descriptor);

    Optional<AttributeDescriptor> retrieved = registry.lookup("duplicate");
    assertThat(retrieved.isPresent(), is(true));
  }

  @Test
  public void testDeregisterOneOfMultipleSameNameDescriptors() {
    AttributeDescriptor descriptor1 =
        new AttributeDescriptorImpl("sameName", true, true, false, false, BasicTypes.STRING_TYPE);
    AttributeDescriptor descriptor2 =
        new AttributeDescriptorImpl("sameName", false, false, true, true, BasicTypes.INTEGER_TYPE);

    registry.register(descriptor1);
    registry.register(descriptor2);

    registry.deregister(descriptor1);

    Optional<AttributeDescriptor> retrieved = registry.lookup("sameName");
    assertThat(retrieved.isPresent(), is(true));
    assertThat(retrieved.get(), is(descriptor2));
  }

  @Test
  public void testRegisterMetacardType() {
    MetacardType metacardType = new CoreAttributes();

    registry.registerMetacardType(metacardType);

    // Verify all attributes from CoreAttributes are registered
    metacardType
        .getAttributeDescriptors()
        .forEach(
            descriptor -> {
              Optional<AttributeDescriptor> retrieved = registry.lookup(descriptor.getName());
              assertThat(retrieved.isPresent(), is(true));
            });
  }

  @Test
  public void testDeregisterMetacardType() {
    MetacardType metacardType = new ContactAttributes();

    registry.registerMetacardType(metacardType);

    // Verify attributes are registered
    String firstAttributeName = metacardType.getAttributeDescriptors().iterator().next().getName();
    assertThat(registry.lookup(firstAttributeName).isPresent(), is(true));

    registry.deregisterMetacardType(metacardType);

    // Verify all attributes are deregistered
    metacardType
        .getAttributeDescriptors()
        .forEach(
            descriptor -> {
              Optional<AttributeDescriptor> retrieved = registry.lookup(descriptor.getName());
              assertThat(retrieved.isPresent(), is(false));
            });
  }

  @Test
  public void testRegisterNullMetacardType() {
    // Should not throw exception
    registry.registerMetacardType(null);
  }

  @Test
  public void testDeregisterNullMetacardType() {
    // Should not throw exception
    registry.deregisterMetacardType(null);
  }

  @Test
  public void testRegisterMetacardTypeWithNullDescriptors() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("valid", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(null);
    descriptors.add(
        new AttributeDescriptorImpl(null, true, true, false, false, BasicTypes.INTEGER_TYPE));

    MetacardType metacardType = new MetacardTypeImpl("withNulls", descriptors);

    registry.registerMetacardType(metacardType);

    // Only the valid descriptor should be registered
    assertThat(registry.lookup("valid").isPresent(), is(true));
  }

  @Test
  public void testRegisterMultipleMetacardTypes() {
    MetacardType type1 = new CoreAttributes();
    MetacardType type2 = new DateTimeAttributes();
    MetacardType type3 = new LocationAttributes();

    registry.registerMetacardType(type1);
    registry.registerMetacardType(type2);
    registry.registerMetacardType(type3);

    // Verify attributes from all types are registered
    type1
        .getAttributeDescriptors()
        .forEach(desc -> assertThat(registry.lookup(desc.getName()).isPresent(), is(true)));
    type2
        .getAttributeDescriptors()
        .forEach(desc -> assertThat(registry.lookup(desc.getName()).isPresent(), is(true)));
    type3
        .getAttributeDescriptors()
        .forEach(desc -> assertThat(registry.lookup(desc.getName()).isPresent(), is(true)));
  }

  @Test
  public void testRegisterAndDeregisterMixedOperations() {
    AttributeDescriptor desc1 =
        new AttributeDescriptorImpl("attr1", true, true, false, false, BasicTypes.STRING_TYPE);
    AttributeDescriptor desc2 =
        new AttributeDescriptorImpl("attr2", true, true, false, false, BasicTypes.INTEGER_TYPE);
    MetacardType metacardType = new MediaAttributes();

    registry.register(desc1);
    registry.registerMetacardType(metacardType);
    registry.register(desc2);

    assertThat(registry.lookup("attr1").isPresent(), is(true));
    assertThat(registry.lookup("attr2").isPresent(), is(true));

    registry.deregister(desc1);
    assertThat(registry.lookup("attr1").isPresent(), is(false));
    assertThat(registry.lookup("attr2").isPresent(), is(true));

    registry.deregisterMetacardType(metacardType);
    metacardType
        .getAttributeDescriptors()
        .forEach(
            descriptor -> {
              assertThat(registry.lookup(descriptor.getName()).isPresent(), is(false));
            });
  }

  @Test
  public void testConcurrentRegistration() throws InterruptedException {
    int threadCount = 10;
    int descriptorsPerThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      executor.submit(
          () -> {
            try {
              for (int i = 0; i < descriptorsPerThread; i++) {
                AttributeDescriptor descriptor =
                    new AttributeDescriptorImpl(
                        "thread" + threadId + "_attr" + i,
                        true,
                        true,
                        false,
                        false,
                        BasicTypes.STRING_TYPE);
                registry.register(descriptor);
              }
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    // Verify all descriptors are registered
    for (int t = 0; t < threadCount; t++) {
      for (int i = 0; i < descriptorsPerThread; i++) {
        assertThat(registry.lookup("thread" + t + "_attr" + i).isPresent(), is(true));
      }
    }
  }

  @Test
  public void testConcurrentDeregistration() throws InterruptedException {
    // First, register descriptors
    int count = 100;
    List<AttributeDescriptor> descriptors = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      AttributeDescriptor descriptor =
          new AttributeDescriptorImpl("attr" + i, true, true, false, false, BasicTypes.STRING_TYPE);
      descriptors.add(descriptor);
      registry.register(descriptor);
    }

    // Now deregister concurrently
    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger index = new AtomicInteger(0);

    for (int t = 0; t < threadCount; t++) {
      executor.submit(
          () -> {
            try {
              int idx;
              while ((idx = index.getAndIncrement()) < count) {
                registry.deregister(descriptors.get(idx));
              }
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    // Verify all descriptors are deregistered
    for (int i = 0; i < count; i++) {
      assertThat(registry.lookup("attr" + i).isPresent(), is(false));
    }
  }

  @Test
  public void testConcurrentLookups() throws InterruptedException {
    // Register descriptors
    for (int i = 0; i < 50; i++) {
      AttributeDescriptor descriptor =
          new AttributeDescriptorImpl(
              "lookup" + i, true, true, false, false, BasicTypes.STRING_TYPE);
      registry.register(descriptor);
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
                Optional<AttributeDescriptor> result = registry.lookup("lookup" + i);
                if (result.isPresent()) {
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

    // Each thread should successfully lookup all 50 descriptors
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
                // Register
                AttributeDescriptor descriptor =
                    new AttributeDescriptorImpl(
                        "mixed_" + threadId + "_" + i,
                        true,
                        true,
                        false,
                        false,
                        BasicTypes.STRING_TYPE);
                registry.register(descriptor);

                // Lookup
                registry.lookup("mixed_" + threadId + "_" + i);

                // Deregister some
                if (i % 3 == 0) {
                  registry.deregister(descriptor);
                }
              }
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await(15, TimeUnit.SECONDS);
    executor.shutdown();

    // Verify that descriptors not divisible by 3 are still registered
    for (int t = 0; t < threadCount; t++) {
      for (int i = 0; i < 50; i++) {
        boolean shouldBePresent = (i % 3 != 0);
        assertThat(registry.lookup("mixed_" + t + "_" + i).isPresent(), is(shouldBePresent));
      }
    }
  }

  @Test
  public void testAllBasicTypes() {
    registry.register(
        new AttributeDescriptorImpl("string", true, true, false, false, BasicTypes.STRING_TYPE));
    registry.register(
        new AttributeDescriptorImpl("integer", true, true, false, false, BasicTypes.INTEGER_TYPE));
    registry.register(
        new AttributeDescriptorImpl("long", true, true, false, false, BasicTypes.LONG_TYPE));
    registry.register(
        new AttributeDescriptorImpl("short", true, true, false, false, BasicTypes.SHORT_TYPE));
    registry.register(
        new AttributeDescriptorImpl("float", true, true, false, false, BasicTypes.FLOAT_TYPE));
    registry.register(
        new AttributeDescriptorImpl("double", true, true, false, false, BasicTypes.DOUBLE_TYPE));
    registry.register(
        new AttributeDescriptorImpl("boolean", true, true, false, false, BasicTypes.BOOLEAN_TYPE));
    registry.register(
        new AttributeDescriptorImpl("date", true, true, false, false, BasicTypes.DATE_TYPE));
    registry.register(
        new AttributeDescriptorImpl("binary", false, true, false, false, BasicTypes.BINARY_TYPE));
    registry.register(
        new AttributeDescriptorImpl("geo", true, true, false, false, BasicTypes.GEO_TYPE));
    registry.register(
        new AttributeDescriptorImpl("xml", true, true, false, false, BasicTypes.XML_TYPE));
    registry.register(
        new AttributeDescriptorImpl("object", true, true, false, false, BasicTypes.OBJECT_TYPE));

    // Verify all types are registered
    assertTrue(registry.lookup("string").isPresent());
    assertTrue(registry.lookup("integer").isPresent());
    assertTrue(registry.lookup("long").isPresent());
    assertTrue(registry.lookup("short").isPresent());
    assertTrue(registry.lookup("float").isPresent());
    assertTrue(registry.lookup("double").isPresent());
    assertTrue(registry.lookup("boolean").isPresent());
    assertTrue(registry.lookup("date").isPresent());
    assertTrue(registry.lookup("binary").isPresent());
    assertTrue(registry.lookup("geo").isPresent());
    assertTrue(registry.lookup("xml").isPresent());
    assertTrue(registry.lookup("object").isPresent());
  }

  @Test
  public void testLargeNumberOfDescriptors() {
    int count = 1000;
    for (int i = 0; i < count; i++) {
      registry.register(
          new AttributeDescriptorImpl(
              "attr_" + i, i % 2 == 0, i % 3 == 0, i % 5 == 0, i % 7 == 0, BasicTypes.STRING_TYPE));
    }

    // Verify all are registered
    for (int i = 0; i < count; i++) {
      assertThat(registry.lookup("attr_" + i).isPresent(), is(true));
    }
  }

  @Test
  public void testDeregisterNonExistentDescriptor() {
    AttributeDescriptor descriptor =
        new AttributeDescriptorImpl(
            "nonexistent", true, true, false, false, BasicTypes.STRING_TYPE);

    // Should not throw exception
    registry.deregister(descriptor);

    assertThat(registry.lookup("nonexistent").isPresent(), is(false));
  }

  @Test
  public void testRegisterDescriptorWithDifferentPropertiesSameName() {
    AttributeDescriptor indexed =
        new AttributeDescriptorImpl("attr", true, false, false, false, BasicTypes.STRING_TYPE);
    AttributeDescriptor notIndexed =
        new AttributeDescriptorImpl("attr", false, true, false, false, BasicTypes.STRING_TYPE);
    AttributeDescriptor multiValued =
        new AttributeDescriptorImpl("attr", true, true, false, true, BasicTypes.STRING_TYPE);

    registry.register(indexed);
    registry.register(notIndexed);
    registry.register(multiValued);

    // Should return the first one registered
    Optional<AttributeDescriptor> result = registry.lookup("attr");
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(indexed));
  }

  @Test
  public void testMultipleRegistrationsAndLookups() {
    for (int i = 0; i < 100; i++) {
      registry.register(
          new AttributeDescriptorImpl(
              "test_" + i, true, true, false, false, BasicTypes.STRING_TYPE));

      // Verify immediately after registration
      assertThat(registry.lookup("test_" + i).isPresent(), is(true));
    }

    // Verify all still present
    for (int i = 0; i < 100; i++) {
      assertThat(registry.lookup("test_" + i).isPresent(), is(true));
    }
  }
}
