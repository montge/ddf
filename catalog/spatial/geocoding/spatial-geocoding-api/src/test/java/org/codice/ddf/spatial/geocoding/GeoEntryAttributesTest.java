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
package org.codice.ddf.spatial.geocoding;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/** Unit tests for {@link GeoEntryAttributes} constants. */
public class GeoEntryAttributesTest {

  @Test
  public void testGeoEntryAttributeConstants() {
    assertThat(GeoEntryAttributes.GAZETTEER_TO_METACARD, is(notNullValue()));
    assertThat(GeoEntryAttributes.GAZETTEER_TO_METACARD.isEmpty(), is(false));
  }

  @Test
  public void testGazetteerToMetacardMappingContainsExpectedKeys() {
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.containsKey(GeoEntryAttributes.FEATURE_CODE_KEY),
        is(true));
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.containsKey(GeoEntryAttributes.LATITUDE_KEY),
        is(true));
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.containsKey(GeoEntryAttributes.LONGITUDE_KEY),
        is(true));
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.containsKey(GeoEntryAttributes.POPULATION_KEY),
        is(true));
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.containsKey(GeoEntryAttributes.COUNTRY_CODE_KEY),
        is(true));
  }

  @Test
  public void testAttributeKeyConstants() {
    assertThat(GeoEntryAttributes.FEATURE_CODE_KEY, is("feature_code"));
    assertThat(GeoEntryAttributes.LATITUDE_KEY, is("latitude"));
    assertThat(GeoEntryAttributes.LONGITUDE_KEY, is("longitude"));
    assertThat(GeoEntryAttributes.POPULATION_KEY, is("population"));
    assertThat(GeoEntryAttributes.ALTERNATE_NAMES_KEY, is("alternate_names"));
    assertThat(GeoEntryAttributes.COUNTRY_CODE_KEY, is("country_code"));
    assertThat(GeoEntryAttributes.GAZETTEER_SORT_KEY, is("gazetteer_sort_value"));
    assertThat(GeoEntryAttributes.IMPORT_LOCATION, is("import_location"));
  }

  @Test
  public void testGazetteerToMetacardMappingValues() {
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.get(GeoEntryAttributes.FEATURE_CODE_KEY),
        is("location.feature-code"));
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.get(GeoEntryAttributes.LATITUDE_KEY),
        is("location.latitude"));
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.get(GeoEntryAttributes.LONGITUDE_KEY),
        is("location.longitude"));
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.get(GeoEntryAttributes.POPULATION_KEY),
        is("location.population"));
    assertThat(
        GeoEntryAttributes.GAZETTEER_TO_METACARD.get(GeoEntryAttributes.COUNTRY_CODE_KEY),
        is("location.country-code"));
  }
}
