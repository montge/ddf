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
package ddf.catalog.operation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

/** Tests for {@link TermFacetProperties} interface and its SortFacetsBy enum. */
public class TermFacetPropertiesTest {

  @Test
  public void testSortFacetsByIndexExists() {
    assertThat(TermFacetProperties.SortFacetsBy.INDEX, is(notNullValue()));
  }

  @Test
  public void testSortFacetsByCountExists() {
    assertThat(TermFacetProperties.SortFacetsBy.COUNT, is(notNullValue()));
  }

  @Test
  public void testSortFacetsByValuesCount() {
    assertThat(TermFacetProperties.SortFacetsBy.values().length, is(2));
  }

  @Test
  public void testSortFacetsByValuesOrder() {
    assertThat(
        TermFacetProperties.SortFacetsBy.values(),
        arrayContaining(
            TermFacetProperties.SortFacetsBy.INDEX, TermFacetProperties.SortFacetsBy.COUNT));
  }

  @Test
  public void testSortFacetsByIndexOrdinal() {
    assertThat(TermFacetProperties.SortFacetsBy.INDEX.ordinal(), is(0));
  }

  @Test
  public void testSortFacetsByCountOrdinal() {
    assertThat(TermFacetProperties.SortFacetsBy.COUNT.ordinal(), is(1));
  }

  @Test
  public void testSortFacetsByIndexName() {
    assertThat(TermFacetProperties.SortFacetsBy.INDEX.name(), is("INDEX"));
  }

  @Test
  public void testSortFacetsByCountName() {
    assertThat(TermFacetProperties.SortFacetsBy.COUNT.name(), is("COUNT"));
  }

  @Test
  public void testSortFacetsByValueOfIndex() {
    assertThat(
        TermFacetProperties.SortFacetsBy.valueOf("INDEX"),
        is(TermFacetProperties.SortFacetsBy.INDEX));
  }

  @Test
  public void testSortFacetsByValueOfCount() {
    assertThat(
        TermFacetProperties.SortFacetsBy.valueOf("COUNT"),
        is(TermFacetProperties.SortFacetsBy.COUNT));
  }
}
