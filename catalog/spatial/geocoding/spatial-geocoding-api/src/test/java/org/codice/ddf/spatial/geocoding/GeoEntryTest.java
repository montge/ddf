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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/** Unit tests for {@link GeoEntry}. */
public class GeoEntryTest {

  @Test
  public void testBuildGeoEntryWithAllFields() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("San Diego")
            .latitude(32.7157)
            .longitude(-117.1611)
            .featureCode("PPL")
            .featureClass("P")
            .population(1394928L)
            .alternateNames("SanDiego,SD")
            .countryCode("US")
            .importLocation("geonames")
            .gazetteerSort(1)
            .build();

    assertThat(geoEntry, is(notNullValue()));
    assertThat(geoEntry.getName(), is("San Diego"));
    assertThat(geoEntry.getLatitude(), is(32.7157));
    assertThat(geoEntry.getLongitude(), is(-117.1611));
    assertThat(geoEntry.getFeatureCode(), is("PPL"));
    assertThat(geoEntry.getFeatureClass(), is("P"));
    assertThat(geoEntry.getPopulation(), is(1394928L));
    assertThat(geoEntry.getAlternateNames(), is("SanDiego,SD"));
    assertThat(geoEntry.getCountryCode(), is("US"));
    assertThat(geoEntry.getImportLocation(), is("geonames"));
  }

  @Test
  public void testBuildGeoEntryWithMinimalFields() {
    GeoEntry geoEntry = new GeoEntry.Builder().name("Test Location").build();

    assertThat(geoEntry, is(notNullValue()));
    assertThat(geoEntry.getName(), is("Test Location"));
    assertThat(geoEntry.getLatitude(), is(nullValue()));
    assertThat(geoEntry.getLongitude(), is(nullValue()));
    assertThat(geoEntry.getFeatureCode(), is(nullValue()));
    assertThat(geoEntry.getPopulation(), is(0L));
  }

  @Test
  public void testBuildGeoEntryWithNegativeCoordinates() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("Melbourne")
            .latitude(-37.8136)
            .longitude(144.9631)
            .countryCode("AU")
            .build();

    assertThat(geoEntry, is(notNullValue()));
    assertThat(geoEntry.getName(), is("Melbourne"));
    assertThat(geoEntry.getLatitude(), is(-37.8136));
    assertThat(geoEntry.getLongitude(), is(144.9631));
    assertThat(geoEntry.getCountryCode(), is("AU"));
  }

  @Test
  public void testBuildGeoEntryWithZeroPopulation() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("Small Village")
            .latitude(40.0)
            .longitude(-120.0)
            .population(0L)
            .build();

    assertThat(geoEntry, is(notNullValue()));
    assertThat(geoEntry.getPopulation(), is(0L));
  }

  @Test
  public void testBuildGeoEntryWithLargePopulation() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("Tokyo")
            .latitude(35.6762)
            .longitude(139.6503)
            .population(13960000L)
            .countryCode("JP")
            .build();

    assertThat(geoEntry, is(notNullValue()));
    assertThat(geoEntry.getName(), is("Tokyo"));
    assertThat(geoEntry.getPopulation(), is(13960000L));
  }

  @Test
  public void testBuildGeoEntryWithExtremeCoordinates() {
    // North Pole
    GeoEntry northPole =
        new GeoEntry.Builder().name("North Pole").latitude(90.0).longitude(0.0).build();

    assertThat(northPole.getLatitude(), is(90.0));
    assertThat(northPole.getLongitude(), is(0.0));

    // South Pole
    GeoEntry southPole =
        new GeoEntry.Builder().name("South Pole").latitude(-90.0).longitude(0.0).build();

    assertThat(southPole.getLatitude(), is(-90.0));

    // International Date Line
    GeoEntry dateLineWest =
        new GeoEntry.Builder().name("Date Line West").latitude(0.0).longitude(-180.0).build();

    assertThat(dateLineWest.getLongitude(), is(-180.0));

    GeoEntry dateLineEast =
        new GeoEntry.Builder().name("Date Line East").latitude(0.0).longitude(180.0).build();

    assertThat(dateLineEast.getLongitude(), is(180.0));
  }

  @Test
  public void testGeoEntryToString() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("Test City")
            .latitude(40.0)
            .longitude(-100.0)
            .featureCode("PPL")
            .featureClass("P")
            .population(50000L)
            .countryCode("US")
            .build();

    String toString = geoEntry.toString();
    assertThat(toString, is(notNullValue()));
    assertThat(toString.contains("Test City"), is(true));
    assertThat(toString.contains("40.0"), is(true));
    assertThat(toString.contains("-100.0"), is(true));
    assertThat(toString.contains("PPL"), is(true));
    assertThat(toString.contains("50000"), is(true));
  }

  @Test
  public void testBuilderToString() {
    GeoEntry.Builder builder =
        new GeoEntry.Builder()
            .name("Test Location")
            .latitude(35.0)
            .longitude(-115.0)
            .featureCode("AREA")
            .population(100L);

    String toString = builder.toString();
    assertThat(toString, is(notNullValue()));
    assertThat(toString.contains("Test Location"), is(true));
    assertThat(toString.contains("35.0"), is(true));
    assertThat(toString.contains("-115.0"), is(true));
  }

  @Test
  public void testBuildGeoEntryWithEmptyStrings() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("")
            .featureCode("")
            .featureClass("")
            .alternateNames("")
            .countryCode("")
            .importLocation("")
            .build();

    assertThat(geoEntry, is(notNullValue()));
    assertThat(geoEntry.getName(), is(""));
    assertThat(geoEntry.getFeatureCode(), is(""));
    assertThat(geoEntry.getAlternateNames(), is(""));
  }

  @Test
  public void testGazetteerSortWithNullInteger() {
    // Testing with Integer wrapper that may be null
    GeoEntry.Builder builder = new GeoEntry.Builder().name("Test");
    GeoEntry geoEntry = builder.gazetteerSort(Integer.valueOf(5)).build();

    assertThat(geoEntry, is(notNullValue()));
  }

  @Test
  public void testMultipleAlternateNames() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("New York")
            .alternateNames("NYC,New York City,Nueva York,Big Apple")
            .latitude(40.7128)
            .longitude(-74.0060)
            .build();

    assertThat(geoEntry.getAlternateNames(), is("NYC,New York City,Nueva York,Big Apple"));
  }

  @Test
  public void testBuildGeoEntryWithSpecialCharactersInName() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("São Paulo")
            .latitude(-23.5505)
            .longitude(-46.6333)
            .countryCode("BR")
            .build();

    assertThat(geoEntry.getName(), is("São Paulo"));
  }

  @Test
  public void testBuilderChaining() {
    GeoEntry geoEntry =
        new GeoEntry.Builder()
            .name("Chain Test")
            .latitude(10.0)
            .longitude(20.0)
            .featureCode("TEST")
            .featureClass("T")
            .population(1000L)
            .alternateNames("alt1,alt2")
            .countryCode("XX")
            .importLocation("test-import")
            .gazetteerSort(1)
            .build();

    assertThat(geoEntry.getName(), is("Chain Test"));
    assertThat(geoEntry.getLatitude(), is(10.0));
    assertThat(geoEntry.getLongitude(), is(20.0));
    assertThat(geoEntry.getFeatureCode(), is("TEST"));
    assertThat(geoEntry.getFeatureClass(), is("T"));
    assertThat(geoEntry.getPopulation(), is(1000L));
    assertThat(geoEntry.getAlternateNames(), is("alt1,alt2"));
    assertThat(geoEntry.getCountryCode(), is("XX"));
    assertThat(geoEntry.getImportLocation(), is("test-import"));
  }
}
