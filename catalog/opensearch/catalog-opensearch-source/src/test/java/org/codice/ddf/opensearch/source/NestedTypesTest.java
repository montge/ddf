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
package org.codice.ddf.opensearch.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class NestedTypesTest {

  @Test
  void testAndValue() {
    assertThat(NestedTypes.AND, is(notNullValue()));
    assertThat(NestedTypes.AND.name(), is("AND"));
  }

  @Test
  void testOrValue() {
    assertThat(NestedTypes.OR, is(notNullValue()));
    assertThat(NestedTypes.OR.name(), is("OR"));
  }

  @Test
  void testNotValue() {
    assertThat(NestedTypes.NOT, is(notNullValue()));
    assertThat(NestedTypes.NOT.name(), is("NOT"));
  }

  @Test
  void testEnumValueCount() {
    assertThat(NestedTypes.values().length, is(3));
  }

  @Test
  void testEnumValuesContainsAll() {
    assertThat(
        NestedTypes.values(),
        arrayContainingInAnyOrder(NestedTypes.AND, NestedTypes.OR, NestedTypes.NOT));
  }

  @Test
  void testValueOfAnd() {
    assertThat(NestedTypes.valueOf("AND"), is(NestedTypes.AND));
  }

  @Test
  void testValueOfOr() {
    assertThat(NestedTypes.valueOf("OR"), is(NestedTypes.OR));
  }

  @Test
  void testValueOfNot() {
    assertThat(NestedTypes.valueOf("NOT"), is(NestedTypes.NOT));
  }
}
