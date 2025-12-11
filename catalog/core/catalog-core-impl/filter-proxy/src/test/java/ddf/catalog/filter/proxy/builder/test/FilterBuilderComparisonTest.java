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
package ddf.catalog.filter.proxy.builder.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import java.util.Date;
import org.geotools.filter.visitor.DefaultFilterVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;

/** Comprehensive tests for FilterBuilder comparison operators (greater than, less than, etc.) */
@ExtendWith(MockitoExtension.class)
public class FilterBuilderComparisonTest {

  private static final String TEST_ATTRIBUTE = "testAttribute";
  private FilterBuilder builder;

  @BeforeEach
  public void setUp() {
    builder = new GeotoolsFilterBuilder();
  }

  @Test
  public void testPropertyIsGreaterThan() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // Test with Integer
    Filter filter = builder.attribute(TEST_ATTRIBUTE).greaterThan().number(5);
    assertThat(filter, is(notNullValue()));
    assertThat(filter, instanceOf(PropertyIsGreaterThan.class));
    filter.accept(visitor, null);

    // Test with Long
    filter = builder.attribute(TEST_ATTRIBUTE).greaterThan().number(10L);
    filter.accept(visitor, null);

    // Test with Double
    filter = builder.attribute(TEST_ATTRIBUTE).greaterThan().number(5.5);
    filter.accept(visitor, null);

    // Test with Float
    filter = builder.attribute(TEST_ATTRIBUTE).greaterThan().number(3.14f);
    filter.accept(visitor, null);

    // Test with Date (using milliseconds as numerical value)
    filter = builder.attribute(TEST_ATTRIBUTE).greaterThan().number(new Date().getTime());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsGreaterThan.class), any());
  }

  @Test
  public void testPropertyIsGreaterThanWithIsOperator() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter filter = builder.attribute(TEST_ATTRIBUTE).is().greaterThan().number(100);
    assertThat(filter, is(notNullValue()));
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsGreaterThan.class), any());
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualTo() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // Test with Integer
    Filter filter = builder.attribute(TEST_ATTRIBUTE).greaterThanOrEqualTo().number(5);
    assertThat(filter, is(notNullValue()));
    assertThat(filter, instanceOf(PropertyIsGreaterThanOrEqualTo.class));
    filter.accept(visitor, null);

    // Test with Long
    filter = builder.attribute(TEST_ATTRIBUTE).greaterThanOrEqualTo().number(10L);
    filter.accept(visitor, null);

    // Test with Double
    filter = builder.attribute(TEST_ATTRIBUTE).greaterThanOrEqualTo().number(5.5);
    filter.accept(visitor, null);

    // Test with Float
    filter = builder.attribute(TEST_ATTRIBUTE).greaterThanOrEqualTo().number(3.14f);
    filter.accept(visitor, null);

    // Test with Date (using milliseconds as numerical value)
    filter = builder.attribute(TEST_ATTRIBUTE).greaterThanOrEqualTo().number(new Date().getTime());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsGreaterThanOrEqualTo.class), any());
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToWithIsOperator() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter filter = builder.attribute(TEST_ATTRIBUTE).is().greaterThanOrEqualTo().number(100);
    assertThat(filter, is(notNullValue()));
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsGreaterThanOrEqualTo.class), any());
  }

  @Test
  public void testPropertyIsLessThan() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // Test with Integer
    Filter filter = builder.attribute(TEST_ATTRIBUTE).lessThan().number(5);
    assertThat(filter, is(notNullValue()));
    assertThat(filter, instanceOf(PropertyIsLessThan.class));
    filter.accept(visitor, null);

    // Test with Long
    filter = builder.attribute(TEST_ATTRIBUTE).lessThan().number(10L);
    filter.accept(visitor, null);

    // Test with Double
    filter = builder.attribute(TEST_ATTRIBUTE).lessThan().number(5.5);
    filter.accept(visitor, null);

    // Test with Float
    filter = builder.attribute(TEST_ATTRIBUTE).lessThan().number(3.14f);
    filter.accept(visitor, null);

    // Test with Date (using milliseconds as numerical value)
    filter = builder.attribute(TEST_ATTRIBUTE).lessThan().number(new Date().getTime());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsLessThan.class), any());
  }

  @Test
  public void testPropertyIsLessThanWithIsOperator() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter filter = builder.attribute(TEST_ATTRIBUTE).is().lessThan().number(100);
    assertThat(filter, is(notNullValue()));
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLessThan.class), any());
  }

  @Test
  public void testPropertyIsLessThanOrEqualTo() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    // Test with Integer
    Filter filter = builder.attribute(TEST_ATTRIBUTE).lessThanOrEqualTo().number(5);
    assertThat(filter, is(notNullValue()));
    assertThat(filter, instanceOf(PropertyIsLessThanOrEqualTo.class));
    filter.accept(visitor, null);

    // Test with Long
    filter = builder.attribute(TEST_ATTRIBUTE).lessThanOrEqualTo().number(10L);
    filter.accept(visitor, null);

    // Test with Double
    filter = builder.attribute(TEST_ATTRIBUTE).lessThanOrEqualTo().number(5.5);
    filter.accept(visitor, null);

    // Test with Float
    filter = builder.attribute(TEST_ATTRIBUTE).lessThanOrEqualTo().number(3.14f);
    filter.accept(visitor, null);

    // Test with Date (using milliseconds as numerical value)
    filter = builder.attribute(TEST_ATTRIBUTE).lessThanOrEqualTo().number(new Date().getTime());
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(5)).visit(isA(PropertyIsLessThanOrEqualTo.class), any());
  }

  @Test
  public void testPropertyIsLessThanOrEqualToWithIsOperator() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    Filter filter = builder.attribute(TEST_ATTRIBUTE).is().lessThanOrEqualTo().number(100);
    assertThat(filter, is(notNullValue()));
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor, times(1)).visit(isA(PropertyIsLessThanOrEqualTo.class), any());
  }

  @Test
  @SuppressWarnings("NullArgumentForNonNullParameter")
  public void testGreaterThanWithNullNumber() {
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.attribute(TEST_ATTRIBUTE).greaterThan().number((Integer) null));
  }

  @Test
  @SuppressWarnings("NullArgumentForNonNullParameter")
  public void testGreaterThanOrEqualToWithNullNumber() {
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.attribute(TEST_ATTRIBUTE).greaterThanOrEqualTo().number((Integer) null));
  }

  @Test
  @SuppressWarnings("NullArgumentForNonNullParameter")
  public void testLessThanWithNullNumber() {
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.attribute(TEST_ATTRIBUTE).lessThan().number((Integer) null));
  }

  @Test
  @SuppressWarnings("NullArgumentForNonNullParameter")
  public void testLessThanOrEqualToWithNullNumber() {
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.attribute(TEST_ATTRIBUTE).lessThanOrEqualTo().number((Integer) null));
  }

  @Test
  public void testComparisonWithShortNumbers() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    short shortValue = 10;
    Filter filter = builder.attribute(TEST_ATTRIBUTE).greaterThan().number(shortValue);
    filter.accept(visitor, null);

    filter = builder.attribute(TEST_ATTRIBUTE).lessThan().number(shortValue);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor).visit(isA(PropertyIsGreaterThan.class), any());
    inOrder.verify(visitor).visit(isA(PropertyIsLessThan.class), any());
  }

  @Test
  public void testComparisonWithByte() {
    FilterVisitor visitor = spy(new DefaultFilterVisitor() {});

    byte byteValue = 5;
    Filter filter = builder.attribute(TEST_ATTRIBUTE).greaterThan().number(byteValue);
    filter.accept(visitor, null);

    filter = builder.attribute(TEST_ATTRIBUTE).lessThanOrEqualTo().number(byteValue);
    filter.accept(visitor, null);

    InOrder inOrder = inOrder(visitor);
    inOrder.verify(visitor).visit(isA(PropertyIsGreaterThan.class), any());
    inOrder.verify(visitor).visit(isA(PropertyIsLessThanOrEqualTo.class), any());
  }
}
