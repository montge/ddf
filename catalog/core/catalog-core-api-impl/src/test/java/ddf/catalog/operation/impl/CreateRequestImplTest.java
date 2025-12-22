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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Comprehensive test suite for {@link CreateRequestImpl} class.
 *
 * <p>Tests all constructors, getters, store IDs, and properties handling.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateRequestImplTest {

  @Mock private Metacard mockMetacard;

  private Map<String, Serializable> properties;

  @BeforeEach
  public void setUp() {
    properties = new HashMap<>();
    properties.put("key1", "value1");
    properties.put("key2", 100);
  }

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with single metacard.
   *
   * <p>Verifies basic construction with one metacard.
   */
  @Test
  public void testConstructorWithSingleMetacard() {
    CreateRequestImpl request = new CreateRequestImpl(mockMetacard);

    assertThat(request, is(notNullValue()));
    assertThat(request.getMetacards(), hasSize(1));
    assertThat(request.getMetacards().get(0), is(equalTo(mockMetacard)));
    assertThat(request.getStoreIds(), is(notNullValue()));
    assertThat(request.getStoreIds(), is(empty()));
    assertThat(request.hasProperties(), is(false));
  }

  /**
   * Tests constructor with list of metacards.
   *
   * <p>Verifies construction with multiple metacards.
   */
  @Test
  public void testConstructorWithMetacardList() {
    Metacard mc1 = mock(Metacard.class);
    Metacard mc2 = mock(Metacard.class);
    Metacard mc3 = mock(Metacard.class);
    List<Metacard> metacards = Arrays.asList(mc1, mc2, mc3);

    CreateRequestImpl request = new CreateRequestImpl(metacards);

    assertThat(request.getMetacards(), hasSize(3));
    assertThat(request.getMetacards().get(0), is(equalTo(mc1)));
    assertThat(request.getMetacards().get(1), is(equalTo(mc2)));
    assertThat(request.getMetacards().get(2), is(equalTo(mc3)));
  }

  /**
   * Tests constructor with metacards and properties.
   *
   * <p>Verifies properties are stored correctly.
   */
  @Test
  public void testConstructorWithMetacardsAndProperties() {
    List<Metacard> metacards = Arrays.asList(mockMetacard);

    CreateRequestImpl request = new CreateRequestImpl(metacards, properties);

    assertThat(request.getMetacards(), hasSize(1));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("key1"), is(equalTo("value1")));
    assertThat(request.getPropertyValue("key2"), is(equalTo(100)));
  }

  /**
   * Tests constructor with metacards, properties, and destinations.
   *
   * <p>Verifies all parameters are properly initialized.
   */
  @Test
  public void testFullConstructor() {
    List<Metacard> metacards = Arrays.asList(mockMetacard);
    Set<String> destinations = new HashSet<>(Arrays.asList("store1", "store2"));

    CreateRequestImpl request = new CreateRequestImpl(metacards, properties, destinations);

    assertThat(request.getMetacards(), hasSize(1));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getStoreIds(), hasSize(2));
    assertThat(request.getStoreIds(), containsInAnyOrder("store1", "store2"));
  }

  /**
   * Tests constructor with null metacard.
   *
   * <p>Verifies null metacard is accepted.
   */
  @Test
  public void testConstructorWithNullMetacard() {
    CreateRequestImpl request = new CreateRequestImpl((Metacard) null);

    assertThat(request.getMetacards(), hasSize(1));
    assertThat(request.getMetacards().get(0), is(nullValue()));
  }

  /**
   * Tests constructor with null metacard list.
   *
   * <p>Verifies null list is stored as is.
   */
  @Test
  public void testConstructorWithNullMetacardList() {
    CreateRequestImpl request = new CreateRequestImpl((List<Metacard>) null);

    assertThat(request.getMetacards(), is(nullValue()));
  }

  /**
   * Tests constructor with null properties.
   *
   * <p>Verifies null properties are handled.
   */
  @Test
  public void testConstructorWithNullProperties() {
    List<Metacard> metacards = Arrays.asList(mockMetacard);

    CreateRequestImpl request = new CreateRequestImpl(metacards, null);

    assertThat(request.hasProperties(), is(false));
  }

  /**
   * Tests constructor with null destinations.
   *
   * <p>Verifies null destinations result in empty set.
   */
  @Test
  public void testConstructorWithNullDestinations() {
    List<Metacard> metacards = Arrays.asList(mockMetacard);

    CreateRequestImpl request = new CreateRequestImpl(metacards, null, null);

    assertThat(request.getStoreIds(), is(notNullValue()));
    assertThat(request.getStoreIds(), is(empty()));
  }

  /**
   * Tests constructor with empty metacard list.
   *
   * <p>Verifies empty list is stored correctly.
   */
  @Test
  public void testConstructorWithEmptyMetacardList() {
    List<Metacard> emptyList = new ArrayList<>();

    CreateRequestImpl request = new CreateRequestImpl(emptyList);

    assertThat(request.getMetacards(), is(notNullValue()));
    assertThat(request.getMetacards(), is(empty()));
  }

  /**
   * Tests constructor with empty destinations set.
   *
   * <p>Verifies empty destinations set is handled.
   */
  @Test
  public void testConstructorWithEmptyDestinations() {
    List<Metacard> metacards = Arrays.asList(mockMetacard);
    Set<String> emptySet = new HashSet<>();

    CreateRequestImpl request = new CreateRequestImpl(metacards, null, emptySet);

    assertThat(request.getStoreIds(), is(empty()));
  }

  // ====================
  // Metacards Tests
  // ====================

  /**
   * Tests getMetacards returns correct list.
   *
   * <p>Verifies metacard retrieval.
   */
  @Test
  public void testGetMetacards() {
    Metacard mc1 = mock(Metacard.class);
    Metacard mc2 = mock(Metacard.class);
    List<Metacard> metacards = Arrays.asList(mc1, mc2);

    CreateRequestImpl request = new CreateRequestImpl(metacards);

    assertThat(request.getMetacards(), hasSize(2));
    assertThat(request.getMetacards().get(0), is(equalTo(mc1)));
    assertThat(request.getMetacards().get(1), is(equalTo(mc2)));
  }

  /**
   * Tests getMetacards with large list.
   *
   * <p>Verifies handling of multiple metacards.
   */
  @Test
  public void testGetMetacardsWithLargeList() {
    List<Metacard> metacards = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      metacards.add(mock(Metacard.class));
    }

    CreateRequestImpl request = new CreateRequestImpl(metacards);

    assertThat(request.getMetacards(), hasSize(100));
  }

  // ====================
  // Store IDs Tests
  // ====================

  /**
   * Tests getStoreIds with no destinations.
   *
   * <p>Verifies default empty set.
   */
  @Test
  public void testGetStoreIdsWithNoDestinations() {
    CreateRequestImpl request = new CreateRequestImpl(mockMetacard);

    assertThat(request.getStoreIds(), is(empty()));
  }

  /**
   * Tests getStoreIds with single destination.
   *
   * <p>Verifies single destination retrieval.
   */
  @Test
  public void testGetStoreIdsWithSingleDestination() {
    Set<String> destinations = new HashSet<>(Arrays.asList("store1"));
    CreateRequestImpl request =
        new CreateRequestImpl(Arrays.asList(mockMetacard), null, destinations);

    assertThat(request.getStoreIds(), hasSize(1));
    assertThat(request.getStoreIds(), containsInAnyOrder("store1"));
  }

  /**
   * Tests getStoreIds with multiple destinations.
   *
   * <p>Verifies multiple destinations retrieval.
   */
  @Test
  public void testGetStoreIdsWithMultipleDestinations() {
    Set<String> destinations = new HashSet<>(Arrays.asList("store1", "store2", "store3"));
    CreateRequestImpl request =
        new CreateRequestImpl(Arrays.asList(mockMetacard), null, destinations);

    assertThat(request.getStoreIds(), hasSize(3));
    assertThat(request.getStoreIds(), containsInAnyOrder("store1", "store2", "store3"));
  }

  // ====================
  // Properties Tests
  // ====================

  /**
   * Tests properties handling.
   *
   * <p>Verifies property storage and retrieval.
   */
  @Test
  public void testPropertiesHandling() {
    Map<String, Serializable> props = new HashMap<>();
    props.put("timeout", 5000);
    props.put("skipValidation", false);

    CreateRequestImpl request = new CreateRequestImpl(Arrays.asList(mockMetacard), props);

    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("timeout"), is(equalTo(5000)));
    assertThat(request.getPropertyValue("skipValidation"), is(equalTo(false)));
  }

  /**
   * Tests getPropertyValue with non-existent key.
   *
   * <p>Verifies missing property returns null.
   */
  @Test
  public void testGetPropertyValueNonExistent() {
    CreateRequestImpl request = new CreateRequestImpl(Arrays.asList(mockMetacard), properties);

    assertThat(request.getPropertyValue("nonExistent"), is(nullValue()));
  }

  /**
   * Tests containsPropertyName.
   *
   * <p>Verifies property name checking.
   */
  @Test
  public void testContainsPropertyName() {
    CreateRequestImpl request = new CreateRequestImpl(Arrays.asList(mockMetacard), properties);

    assertThat(request.containsPropertyName("key1"), is(true));
    assertThat(request.containsPropertyName("missing"), is(false));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete create request scenario.
   *
   * <p>Simulates a real create operation.
   */
  @Test
  public void testCompleteCreateRequest() {
    Metacard mc1 = mock(Metacard.class);
    Metacard mc2 = mock(Metacard.class);
    List<Metacard> metacards = Arrays.asList(mc1, mc2);
    Set<String> destinations = new HashSet<>(Arrays.asList("local", "remote"));
    Map<String, Serializable> props = new HashMap<>();
    props.put("user", "admin");
    props.put("skipIndexing", false);

    CreateRequestImpl request = new CreateRequestImpl(metacards, props, destinations);

    assertThat(request.getMetacards(), hasSize(2));
    assertThat(request.getStoreIds(), hasSize(2));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("user"), is(equalTo("admin")));
  }

  /**
   * Tests create request with single metacard to single store.
   *
   * <p>Verifies simple create scenario.
   */
  @Test
  public void testSingleMetacardToSingleStore() {
    Set<String> destinations = new HashSet<>(Arrays.asList("catalog"));
    CreateRequestImpl request =
        new CreateRequestImpl(Arrays.asList(mockMetacard), null, destinations);

    assertThat(request.getMetacards(), hasSize(1));
    assertThat(request.getStoreIds(), hasSize(1));
    assertThat(request.getStoreIds().contains("catalog"), is(true));
  }
}
