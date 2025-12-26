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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import java.util.Comparator;
import org.geotools.api.filter.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CaseInsensitiveIfStringComparatorTest {

  private static final String SORT_TYPE = "title";

  private Comparator<Result> fallbackComparator;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    fallbackComparator = mock(Comparator.class);
  }

  @Test
  void testCompareStringsCaseInsensitiveAscending() {
    CaseInsensitiveIfStringComparator comparator =
        new CaseInsensitiveIfStringComparator(SortOrder.ASCENDING, SORT_TYPE, fallbackComparator);

    Result result1 = createResultWithStringAttribute("apple");
    Result result2 = createResultWithStringAttribute("Banana");

    int comparison = comparator.compare(result1, result2);

    assertThat(comparison, is(lessThan(0)));
  }

  @Test
  void testCompareStringsCaseInsensitiveDescending() {
    CaseInsensitiveIfStringComparator comparator =
        new CaseInsensitiveIfStringComparator(SortOrder.DESCENDING, SORT_TYPE, fallbackComparator);

    Result result1 = createResultWithStringAttribute("apple");
    Result result2 = createResultWithStringAttribute("Banana");

    int comparison = comparator.compare(result1, result2);

    assertThat(comparison, is(greaterThan(0)));
  }

  @Test
  void testCompareEqualStringsCaseInsensitive() {
    CaseInsensitiveIfStringComparator comparator =
        new CaseInsensitiveIfStringComparator(SortOrder.ASCENDING, SORT_TYPE, fallbackComparator);

    Result result1 = createResultWithStringAttribute("Test");
    Result result2 = createResultWithStringAttribute("test");

    int comparison = comparator.compare(result1, result2);

    assertThat(comparison, is(0));
  }

  @Test
  void testCompareWithNullAttributeUsesFallback() {
    when(fallbackComparator.compare(
            org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
        .thenReturn(5);

    CaseInsensitiveIfStringComparator comparator =
        new CaseInsensitiveIfStringComparator(SortOrder.ASCENDING, SORT_TYPE, fallbackComparator);

    Result result1 = createResultWithNullAttribute();
    Result result2 = createResultWithStringAttribute("test");

    int comparison = comparator.compare(result1, result2);

    assertThat(comparison, is(5));
    verify(fallbackComparator).compare(result1, result2);
  }

  @Test
  void testCompareWithEmptySortTypeUsesFallback() {
    when(fallbackComparator.compare(
            org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
        .thenReturn(-3);

    CaseInsensitiveIfStringComparator comparator =
        new CaseInsensitiveIfStringComparator(SortOrder.ASCENDING, "", fallbackComparator);

    Result result1 = createResultWithStringAttribute("test");
    Result result2 = createResultWithStringAttribute("test2");

    int comparison = comparator.compare(result1, result2);

    assertThat(comparison, is(-3));
    verify(fallbackComparator).compare(result1, result2);
  }

  @Test
  void testCompareNonStringValuesUsesFallback() {
    when(fallbackComparator.compare(
            org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
        .thenReturn(10);

    CaseInsensitiveIfStringComparator comparator =
        new CaseInsensitiveIfStringComparator(SortOrder.ASCENDING, SORT_TYPE, fallbackComparator);

    Result result1 = createResultWithIntegerAttribute(1);
    Result result2 = createResultWithIntegerAttribute(2);

    int comparison = comparator.compare(result1, result2);

    assertThat(comparison, is(10));
    verify(fallbackComparator).compare(result1, result2);
  }

  @Test
  void testConstructorWithNullSortOrderUsesDescending() {
    CaseInsensitiveIfStringComparator comparator =
        new CaseInsensitiveIfStringComparator(null, SORT_TYPE, fallbackComparator);

    Result result1 = createResultWithStringAttribute("apple");
    Result result2 = createResultWithStringAttribute("banana");

    int comparison = comparator.compare(result1, result2);

    assertThat(comparison, is(greaterThan(0)));
  }

  private Result createResultWithStringAttribute(String value) {
    Result result = mock(Result.class);
    Metacard metacard = mock(Metacard.class);
    Attribute attribute = mock(Attribute.class);

    when(result.getMetacard()).thenReturn(metacard);
    when(metacard.getAttribute(SORT_TYPE)).thenReturn(attribute);
    when(attribute.getValue()).thenReturn(value);

    return result;
  }

  private Result createResultWithNullAttribute() {
    Result result = mock(Result.class);
    Metacard metacard = mock(Metacard.class);

    when(result.getMetacard()).thenReturn(metacard);
    when(metacard.getAttribute(SORT_TYPE)).thenReturn(null);

    return result;
  }

  private Result createResultWithIntegerAttribute(Integer value) {
    Result result = mock(Result.class);
    Metacard metacard = mock(Metacard.class);
    Attribute attribute = mock(Attribute.class);

    when(result.getMetacard()).thenReturn(metacard);
    when(metacard.getAttribute(SORT_TYPE)).thenReturn(attribute);
    when(attribute.getValue()).thenReturn(value);

    return result;
  }
}
