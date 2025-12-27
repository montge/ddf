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
package ddf.catalog.pubsub.criteria.geospatial;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SpatialOperatorTest {

  @Test
  void testEnumValuesCount() {
    SpatialOperator[] values = SpatialOperator.values();
    assertThat(values.length, is(9));
  }

  @Test
  void testEnumContainsAllExpectedValues() {
    SpatialOperator[] values = SpatialOperator.values();
    assertThat(
        values,
        arrayContainingInAnyOrder(
            SpatialOperator.UNKNOWN,
            SpatialOperator.EQUALS,
            SpatialOperator.DISJOINT,
            SpatialOperator.INTERSECTS,
            SpatialOperator.TOUCHES,
            SpatialOperator.CROSSES,
            SpatialOperator.WITHIN,
            SpatialOperator.CONTAINS,
            SpatialOperator.OVERLAPS));
  }

  @Test
  void testUnknownValueOf() {
    assertThat(SpatialOperator.valueOf("UNKNOWN"), is(SpatialOperator.UNKNOWN));
  }

  @Test
  void testEqualsValueOf() {
    assertThat(SpatialOperator.valueOf("EQUALS"), is(SpatialOperator.EQUALS));
  }

  @Test
  void testDisjointValueOf() {
    assertThat(SpatialOperator.valueOf("DISJOINT"), is(SpatialOperator.DISJOINT));
  }

  @Test
  void testIntersectsValueOf() {
    assertThat(SpatialOperator.valueOf("INTERSECTS"), is(SpatialOperator.INTERSECTS));
  }

  @Test
  void testTouchesValueOf() {
    assertThat(SpatialOperator.valueOf("TOUCHES"), is(SpatialOperator.TOUCHES));
  }

  @Test
  void testCrossesValueOf() {
    assertThat(SpatialOperator.valueOf("CROSSES"), is(SpatialOperator.CROSSES));
  }

  @Test
  void testWithinValueOf() {
    assertThat(SpatialOperator.valueOf("WITHIN"), is(SpatialOperator.WITHIN));
  }

  @Test
  void testContainsValueOf() {
    assertThat(SpatialOperator.valueOf("CONTAINS"), is(SpatialOperator.CONTAINS));
  }

  @Test
  void testOverlapsValueOf() {
    assertThat(SpatialOperator.valueOf("OVERLAPS"), is(SpatialOperator.OVERLAPS));
  }

  @Test
  void testInvalidValueOfThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> SpatialOperator.valueOf("INVALID"));
  }

  @Test
  void testToStringReturnsEnumName() {
    assertThat(SpatialOperator.INTERSECTS.toString(), is("INTERSECTS"));
    assertThat(SpatialOperator.UNKNOWN.toString(), is("UNKNOWN"));
  }

  @Test
  void testOrdinalValues() {
    assertThat(SpatialOperator.UNKNOWN.ordinal(), is(0));
    assertThat(SpatialOperator.EQUALS.ordinal(), is(1));
    assertThat(SpatialOperator.DISJOINT.ordinal(), is(2));
    assertThat(SpatialOperator.INTERSECTS.ordinal(), is(3));
    assertThat(SpatialOperator.TOUCHES.ordinal(), is(4));
    assertThat(SpatialOperator.CROSSES.ordinal(), is(5));
    assertThat(SpatialOperator.WITHIN.ordinal(), is(6));
    assertThat(SpatialOperator.CONTAINS.ordinal(), is(7));
    assertThat(SpatialOperator.OVERLAPS.ordinal(), is(8));
  }

  @Test
  void testNameMethod() {
    assertThat(SpatialOperator.WITHIN.name(), is("WITHIN"));
  }

  @Test
  void testEnumValuesAreNotNull() {
    for (SpatialOperator operator : SpatialOperator.values()) {
      assertThat(operator, is(notNullValue()));
    }
  }
}
