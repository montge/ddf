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
package org.codice.ddf.commands.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class SpatialOperationsTest {

  @Test
  void testContainsValue() {
    assertThat(SpatialOperations.CONTAINS, is(notNullValue()));
    assertThat(SpatialOperations.CONTAINS.name(), is("CONTAINS"));
  }

  @Test
  void testIntersectsValue() {
    assertThat(SpatialOperations.INTERSECTS, is(notNullValue()));
    assertThat(SpatialOperations.INTERSECTS.name(), is("INTERSECTS"));
  }

  @Test
  void testEqualsValue() {
    assertThat(SpatialOperations.EQUALS, is(notNullValue()));
    assertThat(SpatialOperations.EQUALS.name(), is("EQUALS"));
  }

  @Test
  void testDisjointValue() {
    assertThat(SpatialOperations.DISJOINT, is(notNullValue()));
    assertThat(SpatialOperations.DISJOINT.name(), is("DISJOINT"));
  }

  @Test
  void testTouchesValue() {
    assertThat(SpatialOperations.TOUCHES, is(notNullValue()));
    assertThat(SpatialOperations.TOUCHES.name(), is("TOUCHES"));
  }

  @Test
  void testCrossesValue() {
    assertThat(SpatialOperations.CROSSES, is(notNullValue()));
    assertThat(SpatialOperations.CROSSES.name(), is("CROSSES"));
  }

  @Test
  void testWithinValue() {
    assertThat(SpatialOperations.WITHIN, is(notNullValue()));
    assertThat(SpatialOperations.WITHIN.name(), is("WITHIN"));
  }

  @Test
  void testOverlapsValue() {
    assertThat(SpatialOperations.OVERLAPS, is(notNullValue()));
    assertThat(SpatialOperations.OVERLAPS.name(), is("OVERLAPS"));
  }

  @Test
  void testRadiusValue() {
    assertThat(SpatialOperations.RADIUS, is(notNullValue()));
    assertThat(SpatialOperations.RADIUS.name(), is("RADIUS"));
  }

  @Test
  void testNnValue() {
    assertThat(SpatialOperations.NN, is(notNullValue()));
    assertThat(SpatialOperations.NN.name(), is("NN"));
  }

  @Test
  void testEnumValueCount() {
    assertThat(SpatialOperations.values().length, is(10));
  }

  @Test
  void testEnumValuesContainsAll() {
    assertThat(
        SpatialOperations.values(),
        arrayContainingInAnyOrder(
            SpatialOperations.CONTAINS,
            SpatialOperations.INTERSECTS,
            SpatialOperations.EQUALS,
            SpatialOperations.DISJOINT,
            SpatialOperations.TOUCHES,
            SpatialOperations.CROSSES,
            SpatialOperations.WITHIN,
            SpatialOperations.OVERLAPS,
            SpatialOperations.RADIUS,
            SpatialOperations.NN));
  }

  @Test
  void testValueOfContains() {
    assertThat(SpatialOperations.valueOf("CONTAINS"), is(SpatialOperations.CONTAINS));
  }

  @Test
  void testValueOfIntersects() {
    assertThat(SpatialOperations.valueOf("INTERSECTS"), is(SpatialOperations.INTERSECTS));
  }
}
