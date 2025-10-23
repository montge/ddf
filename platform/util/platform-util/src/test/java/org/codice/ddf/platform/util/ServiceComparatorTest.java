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
package org.codice.ddf.platform.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.ServiceReference;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ServiceComparatorTest {

  private ServiceComparator comparator;

  @Mock private ServiceReference ref1;

  @Mock private ServiceReference ref2;

  @Before
  public void setUp() {
    comparator = new ServiceComparator();
  }

  @Test
  public void testComparatorCreation() {
    assertThat(comparator, notNullValue());
  }

  @Test
  public void testCompareWithEqualReferences() {
    when(ref1.compareTo(ref2)).thenReturn(0);
    when(ref2.compareTo(ref1)).thenReturn(0);

    int result = comparator.compare(ref1, ref2);
    assertThat(result, is(0));
  }

  @Test
  public void testCompareWhenFirstIsLess() {
    // ref2.compareTo(ref1) returns positive means ref2 > ref1
    // So our compare(ref1, ref2) should return positive
    when(ref2.compareTo(ref1)).thenReturn(1);

    int result = comparator.compare(ref1, ref2);
    assertThat(result, greaterThan(0));
  }

  @Test
  public void testCompareWhenFirstIsGreater() {
    // ref2.compareTo(ref1) returns negative means ref2 < ref1
    // So our compare(ref1, ref2) should return negative
    when(ref2.compareTo(ref1)).thenReturn(-1);

    int result = comparator.compare(ref1, ref2);
    assertThat(result, lessThan(0));
  }

  @Test
  public void testCompareInvertsOrder() {
    // ServiceComparator reverses the natural order by calling ref2.compareTo(ref1)
    // instead of ref1.compareTo(ref2)
    when(ref2.compareTo(ref1)).thenReturn(5);

    int result = comparator.compare(ref1, ref2);
    assertThat(result, is(5));
  }

  @Test
  public void testCompareIsConsistent() {
    when(ref2.compareTo(ref1)).thenReturn(3);

    int result1 = comparator.compare(ref1, ref2);
    int result2 = comparator.compare(ref1, ref2);

    assertThat(result1, is(result2));
  }

  @Test
  public void testCompareSymmetry() {
    when(ref1.compareTo(ref2)).thenReturn(-2);
    when(ref2.compareTo(ref1)).thenReturn(2);

    int result1 = comparator.compare(ref1, ref2);
    int result2 = comparator.compare(ref2, ref1);

    // Due to the reversal, results should be opposite
    assertThat(result1, is(-result2));
  }

  @Test
  public void testCompareWithSameReference() {
    when(ref1.compareTo(ref2)).thenReturn(0);
    when(ref2.compareTo(ref1)).thenReturn(0);

    int result = comparator.compare(ref1, ref2);
    assertThat(result, is(0));
  }

  @Test
  public void testCompareMultipleReferences() {
    ServiceReference ref3 = mock(ServiceReference.class);

    when(ref2.compareTo(ref1)).thenReturn(1);
    when(ref3.compareTo(ref1)).thenReturn(-1);
    when(ref3.compareTo(ref2)).thenReturn(-2);

    int result1 = comparator.compare(ref1, ref2);
    int result2 = comparator.compare(ref1, ref3);

    assertThat(result1, greaterThan(0));
    assertThat(result2, lessThan(0));
  }

  @Test
  public void testComparatorCanBeUsedInSorting() {
    // Test that the comparator can be used with standard Java sorting mechanisms
    ServiceReference refA = mock(ServiceReference.class);
    ServiceReference refB = mock(ServiceReference.class);

    when(refB.compareTo(refA)).thenReturn(-1);
    when(refA.compareTo(refB)).thenReturn(1);

    int result = comparator.compare(refA, refB);
    assertThat(result, lessThan(0));
  }

  @Test
  public void testCompareWithLargeValues() {
    when(ref2.compareTo(ref1)).thenReturn(Integer.MAX_VALUE);

    int result = comparator.compare(ref1, ref2);
    assertThat(result, is(Integer.MAX_VALUE));
  }

  @Test
  public void testCompareWithNegativeValues() {
    when(ref2.compareTo(ref1)).thenReturn(Integer.MIN_VALUE);

    int result = comparator.compare(ref1, ref2);
    assertThat(result, is(Integer.MIN_VALUE));
  }

  @Test
  public void testComparatorImplementsComparator() {
    assertThat(comparator instanceof java.util.Comparator, is(true));
  }

  @Test
  public void testComparatorImplementsSerializable() {
    assertThat(comparator instanceof java.io.Serializable, is(true));
  }

  @Test
  public void testCompareTransitivity() {
    ServiceReference ref3 = mock(ServiceReference.class);

    when(ref2.compareTo(ref1)).thenReturn(1);
    when(ref3.compareTo(ref2)).thenReturn(1);
    when(ref3.compareTo(ref1)).thenReturn(2);

    int result1 = comparator.compare(ref1, ref2);
    int result2 = comparator.compare(ref2, ref3);
    int result3 = comparator.compare(ref1, ref3);

    // If ref1 < ref2 and ref2 < ref3, then ref1 < ref3
    assertThat(result1, greaterThan(0));
    assertThat(result2, greaterThan(0));
    assertThat(result3, greaterThan(0));
  }
}
