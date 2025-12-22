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
package org.codice.ddf.catalog.plugin.gazetteer;

import static ddf.catalog.Constants.SUGGESTION_BUILD_KEY;
import static ddf.catalog.Constants.SUGGESTION_CONTEXT_KEY;
import static ddf.catalog.Constants.SUGGESTION_DICT_KEY;
import static ddf.catalog.Constants.SUGGESTION_QUERY_KEY;
import static org.codice.ddf.spatial.geocoding.GeoCodingConstants.GAZETTEER_METACARD_TAG;
import static org.codice.ddf.spatial.geocoding.GeoCodingConstants.SUGGEST_PLACE_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.federation.FederationException;
import ddf.catalog.filter.AttributeBuilder;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;
import java.io.Serializable;
import java.util.Map;
import org.geotools.api.filter.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Tests for {@link BuildSuggesterIndex} class. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BuildSuggesterIndexTest {

  @Mock private CatalogFramework catalogFramework;

  @Mock private FilterBuilder filterBuilder;

  @Mock private QueryResponse queryResponse;

  @Mock private AttributeBuilder attributeBuilder;

  @Mock private Filter filter;

  @Captor private ArgumentCaptor<QueryRequest> queryRequestCaptor;

  private BuildSuggesterIndex buildSuggesterIndex;

  @BeforeEach
  public void setUp() {
    when(filterBuilder.attribute(anyString())).thenReturn(attributeBuilder);
    when(attributeBuilder.text(anyString())).thenReturn(filter);
    buildSuggesterIndex = new BuildSuggesterIndex(catalogFramework, filterBuilder);
  }

  @Test
  public void testImplementsRunnable() {
    assertThat(buildSuggesterIndex instanceof Runnable, is(true));
  }

  @Test
  public void testRunQueriesCatalogFramework() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    buildSuggesterIndex.run();

    verify(catalogFramework).query(any(QueryRequest.class));
  }

  @Test
  public void testRunSendsCorrectQueryProperties() throws Exception {
    when(catalogFramework.query(queryRequestCaptor.capture())).thenReturn(queryResponse);

    buildSuggesterIndex.run();

    QueryRequest capturedRequest = queryRequestCaptor.getValue();
    assertThat(capturedRequest, is(notNullValue()));

    Map<String, Serializable> properties = capturedRequest.getProperties();
    assertThat(properties.get(SUGGESTION_QUERY_KEY), is("anything"));
    assertThat(properties.get(SUGGESTION_CONTEXT_KEY), is(GAZETTEER_METACARD_TAG));
    assertThat(properties.get(SUGGESTION_DICT_KEY), is(SUGGEST_PLACE_KEY));
    assertThat(properties.get(SUGGESTION_BUILD_KEY), is(true));
  }

  @Test
  public void testRunHandlesSourceUnavailableException() throws Exception {
    doThrow(new SourceUnavailableException("test"))
        .when(catalogFramework)
        .query(any(QueryRequest.class));

    // Should not throw - exception is handled internally
    buildSuggesterIndex.run();

    verify(catalogFramework).query(any(QueryRequest.class));
  }

  @Test
  public void testRunHandlesFederationException() throws Exception {
    doThrow(new FederationException("test")).when(catalogFramework).query(any(QueryRequest.class));

    // Should not throw - exception is handled internally
    buildSuggesterIndex.run();

    verify(catalogFramework).query(any(QueryRequest.class));
  }

  @Test
  public void testRunHandlesUnsupportedQueryException() throws Exception {
    doThrow(new UnsupportedQueryException("test"))
        .when(catalogFramework)
        .query(any(QueryRequest.class));

    // Should not throw - exception is handled internally
    buildSuggesterIndex.run();

    verify(catalogFramework).query(any(QueryRequest.class));
  }

  @Test
  public void testQueryRequestHasQuery() throws Exception {
    when(catalogFramework.query(queryRequestCaptor.capture())).thenReturn(queryResponse);

    buildSuggesterIndex.run();

    QueryRequest capturedRequest = queryRequestCaptor.getValue();
    assertThat(capturedRequest.getQuery(), is(notNullValue()));
  }
}
