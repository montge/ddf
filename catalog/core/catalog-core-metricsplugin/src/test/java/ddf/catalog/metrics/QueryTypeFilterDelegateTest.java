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
package ddf.catalog.metrics;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import ddf.catalog.filter.impl.SimpleFilterDelegate.ComparisonPropertyOperation;
import ddf.catalog.filter.impl.SimpleFilterDelegate.LogicalPropertyOperation;
import ddf.catalog.filter.impl.SimpleFilterDelegate.SpatialPropertyOperation;
import ddf.catalog.filter.impl.SimpleFilterDelegate.TemporalPropertyOperation;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryTypeFilterDelegateTest {

  private QueryTypeFilterDelegate delegate;

  @BeforeEach
  void setUp() {
    delegate = new QueryTypeFilterDelegate();
  }

  @Test
  void testInitialStateFlagsAreFalse() {
    assertThat(delegate.isSpatial(), is(false));
    assertThat(delegate.isTemporal(), is(false));
    assertThat(delegate.isLogical(), is(false));
    assertThat(delegate.isFuzzy(), is(false));
    assertThat(delegate.isCaseSensitive(), is(false));
    assertThat(delegate.isComparison(), is(false));
    assertThat(delegate.isFunction(), is(false));
  }

  @Test
  void testSpatialOperationSetsSpatialFlag() {
    delegate.spatialOperation(
        "location", "POINT(0 0)", String.class, SpatialPropertyOperation.CONTAINS);

    assertThat(delegate.isSpatial(), is(true));
  }

  @Test
  void testTemporalOperationSetsTemporalFlag() {
    delegate.temporalOperation("created", new Date(), Date.class, TemporalPropertyOperation.AFTER);

    assertThat(delegate.isTemporal(), is(true));
  }

  @Test
  void testLogicalOperationSetsLogicalFlag() {
    delegate.logicalOperation(true, LogicalPropertyOperation.AND);

    assertThat(delegate.isLogical(), is(true));
  }

  @Test
  void testComparisonOperationSetsComparisonFlag() {
    delegate.comparisonOperation(
        "title", "test", String.class, ComparisonPropertyOperation.IS_EQUAL_TO);

    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testPropertyIsEqualToWithCaseSensitive() {
    delegate.propertyIsEqualTo("title", "value", true);

    assertThat(delegate.isCaseSensitive(), is(true));
    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testPropertyIsEqualToWithoutCaseSensitive() {
    delegate.propertyIsEqualTo("title", "value", false);

    assertThat(delegate.isCaseSensitive(), is(false));
    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testPropertyIsNotEqualToWithCaseSensitive() {
    delegate.propertyIsNotEqualTo("title", "value", true);

    assertThat(delegate.isCaseSensitive(), is(true));
    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testPropertyIsNotEqualToWithoutCaseSensitive() {
    delegate.propertyIsNotEqualTo("title", "value", false);

    assertThat(delegate.isCaseSensitive(), is(false));
    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testPropertyIsLikeWithCaseSensitive() {
    delegate.propertyIsLike("title", "test*", true);

    assertThat(delegate.isCaseSensitive(), is(true));
    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testPropertyIsLikeWithoutCaseSensitive() {
    delegate.propertyIsLike("title", "test*", false);

    assertThat(delegate.isCaseSensitive(), is(false));
    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testPropertyIsFuzzySetsFuzzyAndComparisonFlags() {
    delegate.propertyIsFuzzy("title", "fuzzy");

    assertThat(delegate.isFuzzy(), is(true));
    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testPropertyIsEqualToWithFunctionSetsFunctionFlag() {
    delegate.propertyIsEqualTo("function", Arrays.asList("arg1", "arg2"), "literal");

    assertThat(delegate.isFunction(), is(true));
  }

  @Test
  void testMultipleOperationsSetMultipleFlags() {
    delegate.spatialOperation(
        "location", "POINT(0 0)", String.class, SpatialPropertyOperation.CONTAINS);
    delegate.temporalOperation("created", new Date(), Date.class, TemporalPropertyOperation.AFTER);
    delegate.propertyIsFuzzy("title", "test");

    assertThat(delegate.isSpatial(), is(true));
    assertThat(delegate.isTemporal(), is(true));
    assertThat(delegate.isFuzzy(), is(true));
    assertThat(delegate.isComparison(), is(true));
  }

  @Test
  void testSpatialOperationReturnsTrue() {
    Boolean result =
        delegate.spatialOperation(
            "location", "POINT(0 0)", String.class, SpatialPropertyOperation.CONTAINS);

    assertThat(result, is(true));
  }

  @Test
  void testTemporalOperationReturnsTrue() {
    Boolean result =
        delegate.temporalOperation(
            "created", new Date(), Date.class, TemporalPropertyOperation.AFTER);

    assertThat(result, is(true));
  }

  @Test
  void testLogicalOperationReturnsTrue() {
    Boolean result = delegate.logicalOperation(true, LogicalPropertyOperation.AND);

    assertThat(result, is(true));
  }
}
