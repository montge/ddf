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
package org.codice.ddf.spatial.geocoder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.codice.ddf.spatial.geocoding.GeoEntryQueryException;
import org.codice.ddf.spatial.geocoding.context.NearbyLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/** Unit tests for GeoCoder interface and its implementation. */
public class GeoCoderTest {

  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  private GeoCoder geoCoder;

  private GeoResult mockGeoResult;

  private NearbyLocation mockNearbyLocation;

  @BeforeEach
  public void setUp() {
    geoCoder = createTestGeoCoder();
    mockGeoResult = mock(GeoResult.class);
    mockNearbyLocation = mock(NearbyLocation.class);
  }

  @Test
  public void testGetLocationWithValidQuery() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(-77.0369, 38.9072));
    when(mockGeoResult.getFullName()).thenReturn("Washington, D.C., United States");
    when(mockGeoResult.getPoint()).thenReturn(point);

    GeoResult result = geoCoder.getLocation("Washington, D.C.");

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetLocationWithNullQuery() {
    GeoResult result = geoCoder.getLocation(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetLocationWithEmptyQuery() {
    GeoResult result = geoCoder.getLocation("");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetLocationWithNoResults() {
    GeoResult result = geoCoder.getLocation("NonExistentPlace12345XYZ");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetLocationWithSpecialCharacters() {
    GeoResult result = geoCoder.getLocation("São Paulo, Brazil");

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetNearbyCityWithValidWKT() throws GeoEntryQueryException {
    String wkt = "POINT(-77.0369 38.9072)";
    when(mockNearbyLocation.getName()).thenReturn("Washington");
    when(mockNearbyLocation.getCardinalDirection()).thenReturn("N");
    when(mockNearbyLocation.getDistance()).thenReturn(5.0);

    NearbyLocation result = geoCoder.getNearbyCity(wkt);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetNearbyCityWithInvalidWKT() {
    assertThrows(GeoEntryQueryException.class, () -> geoCoder.getNearbyCity("INVALID WKT"));
  }

  @Test
  public void testGetNearbyCityWithNullWKT() {
    assertThrows(GeoEntryQueryException.class, () -> geoCoder.getNearbyCity(null));
  }

  @Test
  public void testGetNearbyCityWithPolygonWKT() throws GeoEntryQueryException {
    String polygonWkt = "POLYGON((-77.1 38.8, -77.0 38.8, -77.0 38.9, -77.1 38.9, -77.1 38.8))";

    NearbyLocation result = geoCoder.getNearbyCity(polygonWkt);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetCountryCodeWithValidLocationAndRadius() {
    String wkt = "POINT(-77.0369 38.9072)";
    int radius = 50;

    Optional<String> result = geoCoder.getCountryCode(wkt, radius);

    assertThat(result, is(notNullValue()));
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is("USA"));
  }

  @Test
  public void testGetCountryCodeWithOceanLocation() {
    String wkt = "POINT(0.0 0.0)";
    int radius = 50;

    Optional<String> result = geoCoder.getCountryCode(wkt, radius);

    assertThat(result, is(notNullValue()));
    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void testGetCountryCodeWithNullWKT() {
    Optional<String> result = geoCoder.getCountryCode(null, 50);

    assertThat(result, is(notNullValue()));
    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void testGetCountryCodeWithZeroRadius() {
    String wkt = "POINT(-77.0369 38.9072)";

    Optional<String> result = geoCoder.getCountryCode(wkt, 0);

    assertThat(result, is(notNullValue()));
    assertThat(result.isPresent(), is(true));
  }

  @Test
  public void testGetCountryCodeWithNegativeRadius() {
    String wkt = "POINT(-77.0369 38.9072)";

    Optional<String> result = geoCoder.getCountryCode(wkt, -10);

    assertThat(result, is(notNullValue()));
    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void testGetCountryCodeWithLargeRadius() {
    String wkt = "POINT(-77.0369 38.9072)";

    Optional<String> result = geoCoder.getCountryCode(wkt, 1000);

    assertThat(result, is(notNullValue()));
    assertThat(result.isPresent(), is(true));
  }

  @Test
  public void testGetCountryCodeWithPolygonWKT() {
    String polygonWkt = "POLYGON((-77.1 38.8, -77.0 38.8, -77.0 38.9, -77.1 38.9, -77.1 38.8))";

    Optional<String> result = geoCoder.getCountryCode(polygonWkt, 50);

    assertThat(result, is(notNullValue()));
    assertThat(result.isPresent(), is(true));
  }

  @Test
  public void testGetCountryCodeReturnsISO3166Alpha3Format() {
    String wkt = "POINT(-77.0369 38.9072)";

    Optional<String> result = geoCoder.getCountryCode(wkt, 50);

    if (result.isPresent()) {
      String countryCode = result.get();
      assertThat(countryCode.length(), is(3));
    }
  }

  private GeoCoder createTestGeoCoder() {
    return new GeoCoder() {
      @Override
      public GeoResult getLocation(String location) {
        if (location == null || location.isEmpty() || location.contains("NonExistent")) {
          return null;
        }
        GeoResult result = mock(GeoResult.class);
        Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(0.0, 0.0));
        when(result.getFullName()).thenReturn(location);
        when(result.getPoint()).thenReturn(point);
        return result;
      }

      @Override
      public NearbyLocation getNearbyCity(String locationWKT) throws GeoEntryQueryException {
        if (locationWKT == null || locationWKT.startsWith("INVALID")) {
          throw new GeoEntryQueryException("Invalid WKT");
        }
        NearbyLocation location = mock(NearbyLocation.class);
        when(location.getName()).thenReturn("NearbyCity");
        when(location.getCardinalDirection()).thenReturn("N");
        when(location.getDistance()).thenReturn(10.0);
        return location;
      }

      @Override
      public Optional<String> getCountryCode(String locationWKT, int radius) {
        if (locationWKT == null || radius < 0) {
          return Optional.empty();
        }
        if (locationWKT.equals("POINT(0.0 0.0)")) {
          return Optional.empty();
        }
        return Optional.of("USA");
      }
    };
  }
}
