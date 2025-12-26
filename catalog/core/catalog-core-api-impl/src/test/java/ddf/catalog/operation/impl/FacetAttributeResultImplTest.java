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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import ddf.catalog.operation.FacetAttributeResult;
import ddf.catalog.operation.FacetValueCount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class FacetAttributeResultImplTest {

  private static final String ATTRIBUTE_NAME = "testAttribute";

  @Test
  void testImplementsFacetAttributeResult() {
    FacetAttributeResultImpl result =
        new FacetAttributeResultImpl(
            ATTRIBUTE_NAME, Collections.emptyList(), Collections.emptyList());

    assertThat(result, instanceOf(FacetAttributeResult.class));
  }

  @Test
  void testConstructorWithListsAndGetAttributeName() {
    List<String> values = Arrays.asList("value1", "value2");
    List<Long> counts = Arrays.asList(10L, 20L);

    FacetAttributeResultImpl result = new FacetAttributeResultImpl(ATTRIBUTE_NAME, values, counts);

    assertThat(result.getAttributeName(), is(ATTRIBUTE_NAME));
  }

  @Test
  void testGetFacetValuesWithMatchedLists() {
    List<String> values = Arrays.asList("value1", "value2", "value3");
    List<Long> counts = Arrays.asList(10L, 20L, 30L);

    FacetAttributeResultImpl result = new FacetAttributeResultImpl(ATTRIBUTE_NAME, values, counts);

    List<FacetValueCount> facetValues = result.getFacetValues();

    assertThat(facetValues, hasSize(3));
    assertThat(facetValues.get(0).getValue(), is("value1"));
    assertThat(facetValues.get(0).getCount(), is(10L));
    assertThat(facetValues.get(1).getValue(), is("value2"));
    assertThat(facetValues.get(1).getCount(), is(20L));
    assertThat(facetValues.get(2).getValue(), is("value3"));
    assertThat(facetValues.get(2).getCount(), is(30L));
  }

  @Test
  void testConstructorWithUnmatchedListSizes() {
    List<String> values = Arrays.asList("value1", "value2", "value3");
    List<Long> counts = Arrays.asList(10L, 20L);

    FacetAttributeResultImpl result = new FacetAttributeResultImpl(ATTRIBUTE_NAME, values, counts);

    List<FacetValueCount> facetValues = result.getFacetValues();

    assertThat(facetValues, hasSize(2));
  }

  @Test
  void testConstructorWithMoreCountsThanValues() {
    List<String> values = Arrays.asList("value1");
    List<Long> counts = Arrays.asList(10L, 20L, 30L);

    FacetAttributeResultImpl result = new FacetAttributeResultImpl(ATTRIBUTE_NAME, values, counts);

    List<FacetValueCount> facetValues = result.getFacetValues();

    assertThat(facetValues, hasSize(1));
    assertThat(facetValues.get(0).getValue(), is("value1"));
    assertThat(facetValues.get(0).getCount(), is(10L));
  }

  @Test
  void testConstructorWithEmptyLists() {
    FacetAttributeResultImpl result =
        new FacetAttributeResultImpl(
            ATTRIBUTE_NAME, Collections.emptyList(), Collections.emptyList());

    assertThat(result.getFacetValues(), is(empty()));
  }

  @Test
  void testConstructorWithPairList() {
    List<Pair<String, Long>> pairs = new ArrayList<>();
    pairs.add(new ImmutablePair<>("value1", 100L));
    pairs.add(new ImmutablePair<>("value2", 200L));

    FacetAttributeResultImpl result = new FacetAttributeResultImpl(ATTRIBUTE_NAME, pairs);

    assertThat(result.getAttributeName(), is(ATTRIBUTE_NAME));
    List<FacetValueCount> facetValues = result.getFacetValues();
    assertThat(facetValues, hasSize(2));
    assertThat(facetValues.get(0).getValue(), is("value1"));
    assertThat(facetValues.get(0).getCount(), is(100L));
    assertThat(facetValues.get(1).getValue(), is("value2"));
    assertThat(facetValues.get(1).getCount(), is(200L));
  }

  @Test
  void testConstructorWithEmptyPairList() {
    FacetAttributeResultImpl result =
        new FacetAttributeResultImpl(ATTRIBUTE_NAME, Collections.emptyList());

    assertThat(result.getFacetValues(), is(empty()));
  }

  @Test
  void testGetFacetValuesReturnsCopy() {
    List<Pair<String, Long>> pairs = new ArrayList<>();
    pairs.add(new ImmutablePair<>("value1", 100L));

    FacetAttributeResultImpl result = new FacetAttributeResultImpl(ATTRIBUTE_NAME, pairs);

    List<FacetValueCount> facetValues1 = result.getFacetValues();
    List<FacetValueCount> facetValues2 = result.getFacetValues();

    assertThat(facetValues1, is(not(sameInstance(facetValues2))));
  }

  @Test
  void testWithNullAttributeName() {
    List<String> values = Arrays.asList("value1");
    List<Long> counts = Arrays.asList(10L);

    FacetAttributeResultImpl result = new FacetAttributeResultImpl(null, values, counts);

    assertThat(result.getAttributeName(), is((String) null));
  }
}
