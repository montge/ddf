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
package ddf.catalog.solr.offlinegazetteer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.types.Core;
import ddf.catalog.data.types.Location;
import org.codice.ddf.spatial.geocoding.GeoEntryAttributes;
import org.junit.Test;

/** Tests for {@link GazetteerConstants} class. */
public class GazetteerConstantsTest {

  @Test
  public void testGazetteerRequestHandler() {
    assertThat(GazetteerConstants.GAZETTEER_REQUEST_HANDLER, is("/gazetteer"));
  }

  @Test
  public void testCollectionName() {
    assertThat(GazetteerConstants.COLLECTION_NAME, is("gazetteer"));
  }

  @Test
  public void testSuggestKeys() {
    assertThat(GazetteerConstants.SUGGEST_Q_KEY, is("suggest.q"));
    assertThat(GazetteerConstants.SUGGEST_BUILD_KEY, is("suggest.build"));
    assertThat(GazetteerConstants.SUGGEST_DICT_KEY, is("suggest.dictionary"));
    assertThat(GazetteerConstants.SUGGEST_DICT, is("gazetteerSuggest"));
    assertThat(GazetteerConstants.SUGGEST_COUNT_KEY, is("suggest.count"));
  }

  @Test
  public void testFieldNames() {
    assertThat(GazetteerConstants.DESCRIPTION, is("description_txt"));
    assertThat(GazetteerConstants.FEATURE_CODE, is("feature-code_txt"));
    assertThat(GazetteerConstants.NAME, is("name_txt"));
    assertThat(GazetteerConstants.ID, is("id_txt"));
    assertThat(GazetteerConstants.COUNTRY_CODE, is("country-code_txt"));
    assertThat(GazetteerConstants.LOCATION, is("location_geo"));
    assertThat(GazetteerConstants.POPULATION, is("population_lng"));
    assertThat(GazetteerConstants.SORT_VALUE, is("sort-value_int"));
  }

  @Test
  public void testGazetteerToCatalogMapIsNotNull() {
    assertThat(GazetteerConstants.GAZETTEER_TO_CATALOG, is(notNullValue()));
  }

  @Test
  public void testGazetteerToCatalogMapContainsDescriptionMapping() {
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.get(GazetteerConstants.DESCRIPTION),
        is(Core.DESCRIPTION));
  }

  @Test
  public void testGazetteerToCatalogMapContainsNameMapping() {
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.get(GazetteerConstants.NAME), is(Core.TITLE));
  }

  @Test
  public void testGazetteerToCatalogMapContainsIdMapping() {
    assertThat(GazetteerConstants.GAZETTEER_TO_CATALOG.get(GazetteerConstants.ID), is(Core.ID));
  }

  @Test
  public void testGazetteerToCatalogMapContainsCountryCodeMapping() {
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.get(GazetteerConstants.COUNTRY_CODE),
        is(Location.COUNTRY_CODE));
  }

  @Test
  public void testGazetteerToCatalogMapContainsLocationMapping() {
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.get(GazetteerConstants.LOCATION),
        is(Core.LOCATION));
  }

  @Test
  public void testGazetteerToCatalogMapContainsFeatureCodeMapping() {
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.get(GazetteerConstants.FEATURE_CODE),
        is(GeoEntryAttributes.FEATURE_CODE_ATTRIBUTE_NAME));
  }

  @Test
  public void testGazetteerToCatalogMapContainsPopulationMapping() {
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.get(GazetteerConstants.POPULATION),
        is(GeoEntryAttributes.POPULATION_ATTRIBUTE_NAME));
  }

  @Test
  public void testGazetteerToCatalogMapContainsSortValueMapping() {
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.get(GazetteerConstants.SORT_VALUE),
        is(GeoEntryAttributes.GAZETTEER_SORT_VALUE));
  }

  @Test
  public void testGazetteerToCatalogMapIsBidirectional() {
    // Test inverse mapping
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.inverse().get(Core.DESCRIPTION),
        is(GazetteerConstants.DESCRIPTION));
    assertThat(
        GazetteerConstants.GAZETTEER_TO_CATALOG.inverse().get(Core.TITLE),
        is(GazetteerConstants.NAME));
  }
}
