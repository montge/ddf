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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import ddf.catalog.operation.TermFacetProperties.SortFacetsBy;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TermFacetPropertiesImplTest {

  @Test
  void testConstructorWithAttributesOnly() {
    Set<String> attributes = new HashSet<>();
    attributes.add("title");
    attributes.add("created");

    TermFacetPropertiesImpl properties = new TermFacetPropertiesImpl(attributes);

    assertThat(properties.getFacetAttributes(), containsInAnyOrder("title", "created"));
    assertThat(properties.getSortKey(), is(TermFacetPropertiesImpl.DEFAULT_SORT_KEY));
    assertThat(properties.getFacetLimit(), is(TermFacetPropertiesImpl.DEFAULT_FACET_LIMIT));
    assertThat(properties.getMinFacetCount(), is(TermFacetPropertiesImpl.DEFAULT_MIN_FACET_COUNT));
  }

  @Test
  void testConstructorWithAttributesAndSortKey() {
    Set<String> attributes = new HashSet<>();
    attributes.add("category");

    TermFacetPropertiesImpl properties =
        new TermFacetPropertiesImpl(attributes, SortFacetsBy.INDEX);

    assertThat(properties.getFacetAttributes(), containsInAnyOrder("category"));
    assertThat(properties.getSortKey(), is(SortFacetsBy.INDEX));
    assertThat(properties.getFacetLimit(), is(TermFacetPropertiesImpl.DEFAULT_FACET_LIMIT));
    assertThat(properties.getMinFacetCount(), is(TermFacetPropertiesImpl.DEFAULT_MIN_FACET_COUNT));
  }

  @Test
  void testConstructorWithAllParameters() {
    Set<String> attributes = new HashSet<>();
    attributes.add("type");

    TermFacetPropertiesImpl properties =
        new TermFacetPropertiesImpl(attributes, SortFacetsBy.COUNT, 50, 5);

    assertThat(properties.getFacetAttributes(), containsInAnyOrder("type"));
    assertThat(properties.getSortKey(), is(SortFacetsBy.COUNT));
    assertThat(properties.getFacetLimit(), is(50));
    assertThat(properties.getMinFacetCount(), is(5));
  }

  @Test
  void testConstructorWithNullAttributes() {
    TermFacetPropertiesImpl properties =
        new TermFacetPropertiesImpl(null, SortFacetsBy.COUNT, 10, 1);

    assertThat(properties.getFacetAttributes(), is(empty()));
  }

  @Test
  void testGetFacetAttributesReturnsDefensiveCopy() {
    Set<String> attributes = new HashSet<>();
    attributes.add("original");

    TermFacetPropertiesImpl properties = new TermFacetPropertiesImpl(attributes);
    Set<String> returnedAttributes = properties.getFacetAttributes();

    // Modify the returned set
    returnedAttributes.add("modified");

    // Original should not be affected
    assertThat(properties.getFacetAttributes(), containsInAnyOrder("original"));
    assertThat(properties.getFacetAttributes(), not(sameInstance(returnedAttributes)));
  }

  @Test
  void testDefaultConstants() {
    assertThat(TermFacetPropertiesImpl.DEFAULT_FACET_LIMIT, is(100));
    assertThat(TermFacetPropertiesImpl.DEFAULT_MIN_FACET_COUNT, is(1));
    assertThat(TermFacetPropertiesImpl.DEFAULT_SORT_KEY, is(SortFacetsBy.COUNT));
  }

  @Test
  void testEmptyAttributeSet() {
    Set<String> emptyAttributes = new HashSet<>();

    TermFacetPropertiesImpl properties = new TermFacetPropertiesImpl(emptyAttributes);

    assertThat(properties.getFacetAttributes(), is(empty()));
  }
}
