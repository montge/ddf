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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.ServiceReference;

class ServiceComparatorTest {

  private ServiceComparator comparator;

  @BeforeEach
  void setUp() {
    comparator = new ServiceComparator();
  }

  @Test
  void testComparatorIsSerializable() {
    assertThat(comparator instanceof Serializable, is(true));
  }

  @Test
  void testCompareReturnsZeroForEqualReferences() {
    ServiceReference<?> ref1 = mock(ServiceReference.class);
    ServiceReference<?> ref2 = mock(ServiceReference.class);
    when(ref2.compareTo(ref1)).thenReturn(0);

    int result = comparator.compare(ref1, ref2);

    assertThat(result, is(0));
  }

  @Test
  void testCompareReversesOrder() {
    ServiceReference<?> ref1 = mock(ServiceReference.class);
    ServiceReference<?> ref2 = mock(ServiceReference.class);
    when(ref2.compareTo(ref1)).thenReturn(1);

    int result = comparator.compare(ref1, ref2);

    assertThat(result, greaterThan(0));
  }

  @Test
  void testCompareReversesNegativeOrder() {
    ServiceReference<?> ref1 = mock(ServiceReference.class);
    ServiceReference<?> ref2 = mock(ServiceReference.class);
    when(ref2.compareTo(ref1)).thenReturn(-1);

    int result = comparator.compare(ref1, ref2);

    assertThat(result, lessThan(0));
  }

  @Test
  void testCompareDelegatestoRef2CompareTo() {
    ServiceReference<?> ref1 = mock(ServiceReference.class);
    ServiceReference<?> ref2 = mock(ServiceReference.class);
    when(ref2.compareTo(ref1)).thenReturn(42);

    int result = comparator.compare(ref1, ref2);

    assertThat(result, is(42));
  }
}
