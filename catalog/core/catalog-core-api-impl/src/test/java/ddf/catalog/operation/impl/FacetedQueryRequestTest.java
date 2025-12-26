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

import static ddf.catalog.Constants.EXPERIMENTAL_FACET_PROPERTIES_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import ddf.catalog.operation.Query;
import ddf.catalog.operation.TermFacetProperties;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FacetedQueryRequestTest {

  @Test
  void testConstructorWithQueryAndFacetAttributes() {
    Query query = mock(Query.class);
    Set<String> facetAttributes = new HashSet<>(Arrays.asList("title", "author"));

    FacetedQueryRequest request = new FacetedQueryRequest(query, facetAttributes);

    assertThat(request.getQuery(), is(query));
    assertThat(request.isEnterprise(), is(false));
    assertThat(request.getSourceIds().isEmpty(), is(true));
    assertThat(request.getProperties().containsKey(EXPERIMENTAL_FACET_PROPERTIES_KEY), is(true));
  }

  @Test
  void testConstructorWithQueryAndTermFacetProperties() {
    Query query = mock(Query.class);
    TermFacetProperties termFacetProperties = mock(TermFacetProperties.class);

    FacetedQueryRequest request = new FacetedQueryRequest(query, termFacetProperties);

    assertThat(request.getQuery(), is(query));
    assertThat(request.isEnterprise(), is(false));
    assertThat(
        request.getProperties().get(EXPERIMENTAL_FACET_PROPERTIES_KEY), is(termFacetProperties));
  }

  @Test
  void testFullConstructor() {
    Query query = mock(Query.class);
    Set<String> sourceIds = new HashSet<>(Arrays.asList("source1", "source2"));
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("customKey", "customValue");
    TermFacetProperties facetProperties = mock(TermFacetProperties.class);

    FacetedQueryRequest request =
        new FacetedQueryRequest(query, true, sourceIds, properties, facetProperties);

    assertThat(request.getQuery(), is(query));
    assertThat(request.isEnterprise(), is(true));
    assertThat(request.getSourceIds(), containsInAnyOrder("source1", "source2"));
    assertThat(request.getProperties(), hasEntry("customKey", "customValue"));
    assertThat(request.getProperties().get(EXPERIMENTAL_FACET_PROPERTIES_KEY), is(facetProperties));
  }

  @Test
  void testFacetPropertiesAreStoredInQueryProperties() {
    Query query = mock(Query.class);
    Set<String> facetAttributes = new HashSet<>(Collections.singletonList("category"));

    FacetedQueryRequest request = new FacetedQueryRequest(query, facetAttributes);

    Object facetProps = request.getProperties().get(EXPERIMENTAL_FACET_PROPERTIES_KEY);
    assertThat(facetProps, is(notNullValue()));
    assertThat(facetProps, instanceOf(TermFacetProperties.class));
  }

  @Test
  void testConstructorWithEmptySourceIds() {
    Query query = mock(Query.class);
    TermFacetProperties facetProperties = mock(TermFacetProperties.class);

    FacetedQueryRequest request =
        new FacetedQueryRequest(
            query, false, Collections.emptySet(), Collections.emptyMap(), facetProperties);

    assertThat(request.getSourceIds().isEmpty(), is(true));
  }

  @Test
  void testConstructorWithEmptyProperties() {
    Query query = mock(Query.class);
    TermFacetProperties facetProperties = mock(TermFacetProperties.class);

    FacetedQueryRequest request =
        new FacetedQueryRequest(
            query, false, Collections.emptySet(), Collections.emptyMap(), facetProperties);

    // Should only have the facet properties key
    assertThat(request.getProperties().containsKey(EXPERIMENTAL_FACET_PROPERTIES_KEY), is(true));
  }

  @Test
  void testEnterpriseQueryFlag() {
    Query query = mock(Query.class);
    TermFacetProperties facetProperties = mock(TermFacetProperties.class);

    FacetedQueryRequest enterpriseRequest =
        new FacetedQueryRequest(
            query, true, Collections.emptySet(), Collections.emptyMap(), facetProperties);

    FacetedQueryRequest nonEnterpriseRequest =
        new FacetedQueryRequest(
            query, false, Collections.emptySet(), Collections.emptyMap(), facetProperties);

    assertThat(enterpriseRequest.isEnterprise(), is(true));
    assertThat(nonEnterpriseRequest.isEnterprise(), is(false));
  }
}
