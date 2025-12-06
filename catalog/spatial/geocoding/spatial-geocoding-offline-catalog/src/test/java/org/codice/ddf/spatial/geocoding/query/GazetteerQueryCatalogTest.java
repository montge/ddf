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
package org.codice.ddf.spatial.geocoding.query;

import static ddf.catalog.Constants.SUGGESTION_RESULT_KEY;
import static org.codice.ddf.spatial.geocoding.GeoCodingConstants.GAZETTEER_METACARD_TAG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.data.impl.types.LocationAttributes;
import ddf.catalog.data.types.Core;
import ddf.catalog.data.types.Location;
import ddf.catalog.federation.FederationException;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.operation.impl.QueryResponseImpl;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.codice.ddf.spatial.geocoding.GeoCodingConstants;
import org.codice.ddf.spatial.geocoding.GeoEntry;
import org.codice.ddf.spatial.geocoding.GeoEntryAttributes;
import org.codice.ddf.spatial.geocoding.GeoEntryQueryException;
import org.codice.ddf.spatial.geocoding.Suggestion;
import org.codice.ddf.spatial.geocoding.context.NearbyLocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GazetteerQueryCatalogTest {

  private static final int RADIUS_IN_KM = 100;

  private static final int MAX_RESULTS = 10;

  private static final String BOSTON_WKT = "POINT (-71.0595703125 42.35771940022451)";

  private static final String NEAR_BOSTON_WKT = "POINT(-71.07124328613281 42.353671973455704)";

  private static final String BAD_WKT = "badWkt";

  private static final String QUERY_STRING = "queryString";

  private static final String BOSTON = "Boston";

  private static final String USA_COUNTRY_CODE = "USA";

  private static final MetacardType GEO_ENTRY_METACARD_TYPE =
      new MetacardTypeImpl(
          "GeoEntryAttributes", Arrays.asList(new LocationAttributes(), new GeoEntryAttributes()));

  private static final FilterBuilder FILTER_BUILDER = new GeotoolsFilterBuilder();

  private GazetteerQueryCatalog queryCatalog;

  private CatalogFramework catalogFramework;

  @Before
  public void setUp() throws Exception {
    catalogFramework = mock(CatalogFramework.class);
    QueryResponse queryResponse = generateQueryResponseFromMetacard(generateGeoNamesMetacard());
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    queryCatalog = new GazetteerQueryCatalog(catalogFramework, FILTER_BUILDER);
  }

  @Test
  public void testQuery() throws Exception {
    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);
    assertThat(geoEntryList.size(), is(1));
    GeoEntry geoEntry = geoEntryList.get(0);
    assertThat(geoEntry.getCountryCode(), is(USA_COUNTRY_CODE));
    assertThat(geoEntry.getName(), is(BOSTON));
    assertThat(geoEntry.getFeatureCode(), is("PPL"));
    assertThat(geoEntry.getLatitude(), is(42.35771940022451));
    assertThat(geoEntry.getLongitude(), is(-71.0595703125));
    assertThat(geoEntry.getPopulation(), is(123456789L));
  }

  @Test
  public void testQueryById() throws Exception {
    GeoEntry geoEntry = queryCatalog.queryById(QUERY_STRING);
    assertThat(geoEntry.getCountryCode(), is(USA_COUNTRY_CODE));
    assertThat(geoEntry.getName(), is(BOSTON));
    assertThat(geoEntry.getFeatureCode(), is("PPL"));
    assertThat(geoEntry.getLatitude(), is(42.35771940022451));
    assertThat(geoEntry.getLongitude(), is(-71.0595703125));
    assertThat(geoEntry.getPopulation(), is(123456789L));
  }

  @Test
  public void testQueryEmptyMetacard() throws Exception {
    QueryResponse queryResponse = generateQueryResponseFromMetacard(generateEmptyMetacard());
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);
    GeoEntry geoEntry = geoEntryList.get(0);
    assertThat(geoEntry.getName(), nullValue());
  }

  @Test
  public void testQueryInvalidStringAttributeOnMetacard() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(new AttributeImpl(Core.TITLE, 1L));
    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);
    GeoEntry geoEntry = geoEntryList.get(0);
    assertThat(geoEntry.getName(), nullValue());
  }

  @Test
  public void testQueryInvalidPopulationOnMetacard() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(
        new AttributeImpl(GeoEntryAttributes.POPULATION_ATTRIBUTE_NAME, "notALong"));
    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);
    assertThat(geoEntryList.size(), is(1));
    GeoEntry geoEntry = geoEntryList.get(0);
    assertThat(geoEntry.getPopulation(), is(0L));
  }

  @Test
  public void testQueryNoLocationOnMetacard() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(new AttributeImpl(Core.LOCATION, ""));
    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);
    assertThat(geoEntryList.size(), is(1));
    GeoEntry geoEntry = geoEntryList.get(0);
    assertThat(geoEntry.getLatitude(), nullValue());
    assertThat(geoEntry.getLongitude(), nullValue());
  }

  @Test
  public void testQueryInvalidLocationOnMetacard() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(new AttributeImpl(Core.LOCATION, BAD_WKT));
    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);
    assertThat(geoEntryList.size(), is(1));
    GeoEntry geoEntry = geoEntryList.get(0);
    assertThat(geoEntry.getLatitude(), nullValue());
    assertThat(geoEntry.getLongitude(), nullValue());
  }

  @Test
  public void testQueryNoCountryCodeOnMetacard() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(new AttributeImpl(Location.COUNTRY_CODE, ""));
    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);
    assertThat(geoEntryList.size(), is(1));
    GeoEntry geoEntry = geoEntryList.get(0);
    assertThat(geoEntry.getCountryCode(), nullValue());
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testQueryUnsupportedQueryException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(UnsupportedQueryException.class);
    queryCatalog.query(QUERY_STRING, 1);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testQuerySourceUnavailableException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(SourceUnavailableException.class);
    queryCatalog.query(QUERY_STRING, 1);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testQueryFederationException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class))).thenThrow(FederationException.class);
    queryCatalog.query(QUERY_STRING, 1);
  }

  @Test
  public void testGetNearestCities() throws Exception {
    List<NearbyLocation> nearbyLocations =
        queryCatalog.getNearestCities(NEAR_BOSTON_WKT, RADIUS_IN_KM, MAX_RESULTS);
    assertThat(nearbyLocations.size(), is(1));
    NearbyLocation nearbyLocation = nearbyLocations.get(0);
    assertThat(nearbyLocation.getCardinalDirection(), is("S"));
    assertThat(nearbyLocation.getDistance(), is(closeTo(1.3, .01)));
    assertThat(nearbyLocation.getName(), is(BOSTON));
  }

  @Test
  public void testGetNearestCitiesMissingLocation() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(new AttributeImpl(Core.LOCATION, ""));
    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<NearbyLocation> nearbyLocations =
        queryCatalog.getNearestCities(NEAR_BOSTON_WKT, RADIUS_IN_KM, MAX_RESULTS);
    assertThat(nearbyLocations.size(), is(0));
  }

  @Test
  public void testGetNearestCitiesInvalidLocation() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(new AttributeImpl(Core.LOCATION, BAD_WKT));
    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<NearbyLocation> nearbyLocations =
        queryCatalog.getNearestCities(NEAR_BOSTON_WKT, RADIUS_IN_KM, MAX_RESULTS);
    assertThat(nearbyLocations.size(), is(0));
  }

  @Test
  public void testGetNearestCitiesMissingTitle() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(new AttributeImpl(Core.TITLE, ""));
    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    List<NearbyLocation> nearbyLocations =
        queryCatalog.getNearestCities(NEAR_BOSTON_WKT, RADIUS_IN_KM, MAX_RESULTS);
    assertThat(nearbyLocations.size(), is(0));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetNearestCitiesSourceUnavailableException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(SourceUnavailableException.class);
    queryCatalog.getNearestCities(NEAR_BOSTON_WKT, RADIUS_IN_KM, MAX_RESULTS);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetNearestCitiesUnsupportedQueryException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(UnsupportedQueryException.class);
    queryCatalog.getNearestCities(NEAR_BOSTON_WKT, RADIUS_IN_KM, MAX_RESULTS);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetNearestCitiesFederationException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class))).thenThrow(FederationException.class);
    queryCatalog.getNearestCities(NEAR_BOSTON_WKT, RADIUS_IN_KM, MAX_RESULTS);
  }

  @Test
  public void testGetCountryCode() throws Exception {
    Optional<String> countryCode = queryCatalog.getCountryCode(NEAR_BOSTON_WKT, RADIUS_IN_KM);
    assertThat(countryCode.isPresent(), is(true));
    assertThat(countryCode.get(), is(USA_COUNTRY_CODE));
  }

  @Test
  public void testGetCountryCodeNoResults() throws Exception {
    QueryResponse queryResponse =
        new QueryResponseImpl(mock(QueryRequest.class), Collections.emptyList(), 0);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    Optional<String> countryCode = queryCatalog.getCountryCode(NEAR_BOSTON_WKT, RADIUS_IN_KM);
    assertThat(countryCode.isPresent(), is(false));
  }

  @Test
  public void testGetCountryCodeBadWkt() throws Exception {
    Optional<String> countryCode = queryCatalog.getCountryCode(BAD_WKT, RADIUS_IN_KM);
    assertThat(countryCode.isPresent(), is(false));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetCountryCodeFederationException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class))).thenThrow(FederationException.class);
    queryCatalog.getCountryCode(NEAR_BOSTON_WKT, RADIUS_IN_KM);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetCountryCodeUnsupportedQueryException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(UnsupportedQueryException.class);
    queryCatalog.getCountryCode(NEAR_BOSTON_WKT, RADIUS_IN_KM);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetCountryCodeSourceUnavailableException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(SourceUnavailableException.class);
    queryCatalog.getCountryCode(NEAR_BOSTON_WKT, RADIUS_IN_KM);
  }

  private Metacard generateEmptyMetacard() {
    return new MetacardImpl(GEO_ENTRY_METACARD_TYPE);
  }

  private Metacard generateGeoNamesMetacard() {
    Metacard metacard = new MetacardImpl(GEO_ENTRY_METACARD_TYPE);
    metacard.setAttribute(new AttributeImpl(Core.TITLE, BOSTON));
    metacard.setAttribute(new AttributeImpl(Location.COUNTRY_CODE, USA_COUNTRY_CODE));
    metacard.setAttribute(new AttributeImpl(GeoEntryAttributes.FEATURE_CODE_ATTRIBUTE_NAME, "PPL"));
    metacard.setAttribute(
        new AttributeImpl(GeoEntryAttributes.POPULATION_ATTRIBUTE_NAME, 123456789L));
    metacard.setAttribute(new AttributeImpl(Core.LOCATION, BOSTON_WKT));
    metacard.setAttribute(
        new AttributeImpl(
            Core.METACARD_TAGS,
            Arrays.asList(GAZETTEER_METACARD_TAG, GeoCodingConstants.GEONAMES_TAG)));
    return metacard;
  }

  private QueryResponse generateQueryResponseFromMetacard(Metacard metacard) {
    return new QueryResponseImpl(
        mock(QueryRequest.class), Collections.singletonList(new ResultImpl(metacard)), 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testQueryByIdWithNullId() throws Exception {
    queryCatalog.queryById(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testQueryByIdWithEmptyId() throws Exception {
    queryCatalog.queryById("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testQueryByIdWithBlankId() throws Exception {
    queryCatalog.queryById("   ");
  }

  @Test
  public void testQueryByIdNoResults() throws Exception {
    QueryResponse queryResponse =
        new QueryResponseImpl(mock(QueryRequest.class), Collections.emptyList(), 0);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    GeoEntry geoEntry = queryCatalog.queryById("nonexistent");

    assertThat(geoEntry, nullValue());
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testQueryByIdUnsupportedQueryException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(UnsupportedQueryException.class);
    queryCatalog.queryById("testId");
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testQueryByIdSourceUnavailableException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(SourceUnavailableException.class);
    queryCatalog.queryById("testId");
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testQueryByIdFederationException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class))).thenThrow(FederationException.class);
    queryCatalog.queryById("testId");
  }

  @Test
  public void testGetSuggestedNames() throws Exception {
    List<Map.Entry<String, String>> suggestionList = new ArrayList<>();
    suggestionList.add(new AbstractMap.SimpleEntry<>("1", "Boston"));
    suggestionList.add(new AbstractMap.SimpleEntry<>("2", "Boston, MA"));
    suggestionList.add(new AbstractMap.SimpleEntry<>("3", "Boston, UK"));

    QueryResponse suggestionResponse = mock(QueryResponse.class);
    doReturn(suggestionList).when(suggestionResponse).getPropertyValue(SUGGESTION_RESULT_KEY);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(suggestionResponse);

    List<Suggestion> suggestions = queryCatalog.getSuggestedNames("Bos", 3);

    assertThat(suggestions, is(notNullValue()));
    assertThat(suggestions, hasSize(3));
    assertThat(suggestions.get(0).getId(), is("1"));
    assertThat(suggestions.get(0).getName(), is("Boston"));
    assertThat(suggestions.get(1).getId(), is("2"));
    assertThat(suggestions.get(1).getName(), is("Boston, MA"));
    assertThat(suggestions.get(2).getId(), is("3"));
    assertThat(suggestions.get(2).getName(), is("Boston, UK"));
  }

  @Test
  public void testGetSuggestedNamesWithLimit() throws Exception {
    List<Map.Entry<String, String>> suggestionList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      suggestionList.add(new AbstractMap.SimpleEntry<>("City" + i, String.valueOf(i)));
    }

    QueryResponse suggestionResponse = mock(QueryResponse.class);
    doReturn(suggestionList).when(suggestionResponse).getPropertyValue(SUGGESTION_RESULT_KEY);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(suggestionResponse);

    List<Suggestion> suggestions = queryCatalog.getSuggestedNames("City", 5);

    assertThat(suggestions, is(notNullValue()));
    assertThat(suggestions, hasSize(5));
  }

  @Test
  public void testGetSuggestedNamesNoResults() throws Exception {
    QueryResponse suggestionResponse = mock(QueryResponse.class);
    doReturn(Collections.emptyList())
        .when(suggestionResponse)
        .getPropertyValue(SUGGESTION_RESULT_KEY);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(suggestionResponse);

    List<Suggestion> suggestions = queryCatalog.getSuggestedNames("NonExistent", 10);

    assertThat(suggestions, is(notNullValue()));
    assertThat(suggestions, is(empty()));
  }

  @Test
  public void testGetSuggestedNamesNoSuggestionProperty() throws Exception {
    QueryResponse suggestionResponse = mock(QueryResponse.class);
    when(suggestionResponse.getPropertyValue(SUGGESTION_RESULT_KEY)).thenReturn(null);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(suggestionResponse);

    List<Suggestion> suggestions = queryCatalog.getSuggestedNames("Test", 10);

    assertThat(suggestions, is(notNullValue()));
    assertThat(suggestions, is(empty()));
  }

  @Test
  public void testGetSuggestedNamesInvalidPropertyType() throws Exception {
    QueryResponse suggestionResponse = mock(QueryResponse.class);
    when(suggestionResponse.getPropertyValue(SUGGESTION_RESULT_KEY)).thenReturn("not a list");
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(suggestionResponse);

    List<Suggestion> suggestions = queryCatalog.getSuggestedNames("Test", 10);

    assertThat(suggestions, is(notNullValue()));
    assertThat(suggestions, is(empty()));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetSuggestedNamesSourceUnavailableException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(SourceUnavailableException.class);
    queryCatalog.getSuggestedNames("Test", 10);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetSuggestedNamesFederationException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class))).thenThrow(FederationException.class);
    queryCatalog.getSuggestedNames("Test", 10);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = GeoEntryQueryException.class)
  public void testGetSuggestedNamesUnsupportedQueryException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(UnsupportedQueryException.class);
    queryCatalog.getSuggestedNames("Test", 10);
  }

  @Test
  public void testQueryWithMultipleResults() throws Exception {
    Metacard metacard1 = generateGeoNamesMetacard();
    Metacard metacard2 = generateGeoNamesMetacard();
    metacard2.setAttribute(new AttributeImpl(Core.TITLE, "Cambridge"));

    QueryResponse queryResponse =
        new QueryResponseImpl(
            mock(QueryRequest.class),
            Arrays.asList(new ResultImpl(metacard1), new ResultImpl(metacard2)),
            2);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 10);

    assertThat(geoEntryList.size(), is(2));
    assertThat(geoEntryList.get(0).getName(), is(BOSTON));
    assertThat(geoEntryList.get(1).getName(), is("Cambridge"));
  }

  @Test
  public void testQueryWithGazetteerSortValue() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(
        new AttributeImpl(GeoEntryAttributes.GAZETTEER_SORT_VALUE, Integer.valueOf(100)));

    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);

    assertThat(geoEntryList.size(), is(1));
  }

  @Test
  public void testQueryWithNoGazetteerSortValue() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();

    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    List<GeoEntry> geoEntryList = queryCatalog.query(QUERY_STRING, 1);

    assertThat(geoEntryList.size(), is(1));
  }

  @Test
  public void testGetCountryCodeWithNullCountryCode() throws Exception {
    Metacard metacard = generateGeoNamesMetacard();
    metacard.setAttribute(new AttributeImpl(Location.COUNTRY_CODE, (String) null));

    QueryResponse queryResponse = generateQueryResponseFromMetacard(metacard);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    Optional<String> countryCode = queryCatalog.getCountryCode(NEAR_BOSTON_WKT, RADIUS_IN_KM);

    assertThat(countryCode.isPresent(), is(false));
  }

  @Test
  public void testGetNearestCitiesWithMultipleResults() throws Exception {
    Metacard metacard1 = generateGeoNamesMetacard();
    Metacard metacard2 = generateGeoNamesMetacard();
    metacard2.setAttribute(new AttributeImpl(Core.TITLE, "Cambridge"));
    metacard2.setAttribute(new AttributeImpl(Core.LOCATION, "POINT(-71.1097 42.3736)"));

    QueryResponse queryResponse =
        new QueryResponseImpl(
            mock(QueryRequest.class),
            Arrays.asList(new ResultImpl(metacard1), new ResultImpl(metacard2)),
            2);
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    List<NearbyLocation> nearbyLocations =
        queryCatalog.getNearestCities(NEAR_BOSTON_WKT, RADIUS_IN_KM, MAX_RESULTS);

    assertThat(nearbyLocations.size(), is(2));
    assertThat(nearbyLocations.get(0).getName(), is(BOSTON));
    assertThat(nearbyLocations.get(1).getName(), is("Cambridge"));
  }

  @Test
  public void testQueryVerifiesCatalogFrameworkCalled() throws Exception {
    queryCatalog.query(QUERY_STRING, 1);

    verify(catalogFramework).query(any(QueryRequest.class));
  }
}
