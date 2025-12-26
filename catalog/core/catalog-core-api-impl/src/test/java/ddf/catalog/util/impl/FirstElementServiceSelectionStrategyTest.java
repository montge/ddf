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
package ddf.catalog.util.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.ServiceReference;

class FirstElementServiceSelectionStrategyTest {

  private FirstElementServiceSelectionStrategy<Object> strategy;

  @BeforeEach
  void setUp() {
    strategy = new FirstElementServiceSelectionStrategy<>();
  }

  @Test
  void testStrategyIsNotNull() {
    assertThat(strategy, is(notNullValue()));
  }

  @Test
  void testSelectServiceReturnsFirstElement() {
    @SuppressWarnings("unchecked")
    ServiceReference<Object> firstRef = mock(ServiceReference.class);
    @SuppressWarnings("unchecked")
    ServiceReference<Object> secondRef = mock(ServiceReference.class);

    SortedSet<ServiceReference<Object>> serviceSet =
        new TreeSet<>(Comparator.comparingInt(System::identityHashCode));
    serviceSet.add(firstRef);
    serviceSet.add(secondRef);

    ServiceReference<Object> result = strategy.selectService(serviceSet);

    assertThat(result, is(sameInstance(serviceSet.first())));
  }

  @Test
  void testSelectServiceWithSingleElement() {
    @SuppressWarnings("unchecked")
    ServiceReference<Object> singleRef = mock(ServiceReference.class);

    SortedSet<ServiceReference<Object>> serviceSet =
        new TreeSet<>(Comparator.comparingInt(System::identityHashCode));
    serviceSet.add(singleRef);

    ServiceReference<Object> result = strategy.selectService(serviceSet);

    assertThat(result, is(sameInstance(singleRef)));
  }

  @Test
  void testSelectServiceThrowsForEmptySet() {
    SortedSet<ServiceReference<Object>> emptySet =
        new TreeSet<>(Comparator.comparingInt(System::identityHashCode));

    assertThrows(NoSuchElementException.class, () -> strategy.selectService(emptySet));
  }

  @Test
  void testImplementsServiceSelectionStrategy() {
    assertThat(strategy instanceof ServiceSelectionStrategy, is(true));
  }
}
