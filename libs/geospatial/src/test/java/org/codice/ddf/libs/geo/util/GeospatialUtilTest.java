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
package org.codice.ddf.libs.geo.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThrows;

import org.codice.ddf.libs.geo.GeoFormatException;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

@ExtendWith(MockitoExtension.class)
public class GeospatialUtilTest {

  @Test
  public void testParseDMSLatitudeNorthPole() throws GeoFormatException {
    String dmsLat = "90:00:00.0N";
    Double degLat = GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(dmsLat);
    assertThat(degLat, is(90.0));
  }

  @Test
  public void testParseDMSLatitudeSouthPole() throws GeoFormatException {
    String dmsLat = "90:00:00.0S";
    Double degLat = GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(dmsLat);
    assertThat(degLat, is(-90.0));
  }

  @Test
  public void testParseDMSLatitudeEquator() throws GeoFormatException {
    String dmsLat = "00:00:00.0N";
    Double degLat = GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(dmsLat);
    assertThat(degLat, is(0.0));
  }

  @Test
  public void testParseDMSLatitudeWithWhitespace() throws GeoFormatException {
    String dmsLat = "  45:30:15.5N  ";
    Double degLat = GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(dmsLat);
    assertThat(degLat, closeTo(45.50430555, 0.000001));
  }

  @Test
  public void testParseDMSLatitudeZeroMinutesAndSeconds() throws GeoFormatException {
    String dmsLat = "45:00:00.0N";
    Double degLat = GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(dmsLat);
    assertThat(degLat, is(45.0));
  }

  @Test
  public void testParseDMSLatitudeHighPrecisionSeconds() throws GeoFormatException {
    String dmsLat = "45:30:15.123456N";
    Double degLat = GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(dmsLat);
    assertThat(degLat, closeTo(45.50420095, 0.000001));
  }

  @Test
  public void testParseDMSLatitudeOnlyDegreesNoColon() throws GeoFormatException {
    String dmsLat = "45N";
    Double degLat = GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(dmsLat);
    assertThat(degLat, nullValue()); // Returns null when no colon is found
  }

  @Test
  public void testParseDMSLatitudeInvalidHemisphereLowercase() {
    String invalidLat = "45:30:15.5E"; // E is for longitude
    assertThrows(
        GeoFormatException.class,
        () -> GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(invalidLat));
  }

  @Test
  public void testParseDMSLatitudeLowercaseHemisphere() throws GeoFormatException {
    String dmsLat = "45:30:15.5n";
    Double degLat = GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(dmsLat);
    assertThat(degLat, closeTo(45.50430555, 0.000001));
  }

  @Test
  public void testParseDMSLongitudePrimeMeridian() throws GeoFormatException {
    String dmsLon = "00:00:00.0E";
    Double degLon = GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(dmsLon);
    assertThat(degLon, is(0.0));
  }

  @Test
  public void testParseDMSLongitude180East() throws GeoFormatException {
    String dmsLon = "180:00:00.0E";
    Double degLon = GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(dmsLon);
    assertThat(degLon, is(180.0));
  }

  @Test
  public void testParseDMSLongitude180West() throws GeoFormatException {
    String dmsLon = "180:00:00.0W";
    Double degLon = GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(dmsLon);
    assertThat(degLon, is(-180.0));
  }

  @Test
  public void testParseDMSLongitudeWithWhitespace() throws GeoFormatException {
    String dmsLon = "  120:45:30.5E  ";
    Double degLon = GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(dmsLon);
    assertThat(degLon, closeTo(120.75847222, 0.000001));
  }

  @Test
  public void testParseDMSLongitudeHighPrecisionSeconds() throws GeoFormatException {
    String dmsLon = "120:45:30.987654E";
    Double degLon = GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(dmsLon);
    assertThat(degLon, closeTo(120.75860768, 0.000001));
  }

  @Test
  public void testParseDMSLongitudeLowercaseHemisphere() throws GeoFormatException {
    String dmsLon = "120:45:30.5w";
    Double degLon = GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(dmsLon);
    assertThat(degLon, closeTo(-120.75847222, 0.000001));
  }

  @Test
  public void testParseDMSLongitudeInvalidHemisphere() {
    String invalidLon = "120:45:30.5N"; // N is for latitude
    assertThrows(
        GeoFormatException.class,
        () -> GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(invalidLon));
  }

  @Test
  public void testTransformEPSG4326WithIdentityTransform()
      throws GeoFormatException, FactoryException {
    GeometryFactory gf = new GeometryFactory();
    Coordinate coord = new Coordinate(25.22, 33.45);
    Point point = gf.createPoint(coord);

    CoordinateReferenceSystem sourceCRS = CRS.decode(GeospatialUtil.EPSG_4326);
    Geometry result = GeospatialUtil.transformToEPSG4326LonLatFormat(point, sourceCRS);

    assertThat(result, notNullValue());
    // When source is EPSG:4326, should not transform
    assertThat(result.getCoordinates()[0].x, closeTo(25.22, 0.00001));
    assertThat(result.getCoordinates()[0].y, closeTo(33.45, 0.00001));
  }

  @Test
  public void testTransformWithProjectedCRS()
      throws GeoFormatException, FactoryException, TransformException {
    // Test with a projected CRS (UTM Zone 36N)
    double easting = 545328.48;
    double northing = 2789384.24;

    CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:32636");
    GeometryFactory gf = new GeometryFactory();
    Coordinate coord = new Coordinate(easting, northing);
    Point point = gf.createPoint(coord);

    Geometry result = GeospatialUtil.transformToEPSG4326LonLatFormat(point, sourceCRS);

    assertThat(result, notNullValue());
    // Should be transformed to lon/lat
    assertThat(result.getCoordinates()[0].x, closeTo(33.45, 0.00001));
    assertThat(result.getCoordinates()[0].y, closeTo(25.22, 0.00001));
  }

  @Test
  public void testCreateCirclePolygonWithSmallRadius() {
    double lat = 40.0;
    double lon = -105.0;
    double radius = 10.0; // 10 meters
    int maxVertices = 16;
    double distanceTolerance = 0.5;

    Geometry circle =
        GeospatialUtil.createCirclePolygon(lat, lon, radius, maxVertices, distanceTolerance);

    assertThat(circle, notNullValue());
    assertThat(circle.getCoordinates().length <= maxVertices + 1, is(true));
  }

  @Test
  public void testCreateCirclePolygonWithLargeRadius() {
    double lat = 40.0;
    double lon = -105.0;
    double radius = 1000000.0; // 1000 km
    int maxVertices = 32;
    double distanceTolerance = 100.0;

    Geometry circle =
        GeospatialUtil.createCirclePolygon(lat, lon, radius, maxVertices, distanceTolerance);

    assertThat(circle, notNullValue());
    assertThat(circle.getCoordinates().length <= maxVertices + 1, is(true));
  }

  @Test
  public void testCreateCirclePolygonAtEquator() {
    double lat = 0.0;
    double lon = 0.0;
    double radius = 100000.0; // 100 km
    int maxVertices = 24;
    double distanceTolerance = 50.0;

    Geometry circle =
        GeospatialUtil.createCirclePolygon(lat, lon, radius, maxVertices, distanceTolerance);

    assertThat(circle, notNullValue());
    assertThat(circle.getCoordinates().length <= maxVertices + 1, is(true));
  }

  @Test
  public void testCreateCirclePolygonAtPole() {
    double lat = 89.0; // Near pole
    double lon = 0.0;
    double radius = 10000.0; // 10 km
    int maxVertices = 16;
    double distanceTolerance = 10.0;

    Geometry circle =
        GeospatialUtil.createCirclePolygon(lat, lon, radius, maxVertices, distanceTolerance);

    assertThat(circle, notNullValue());
  }

  @Test
  public void testCreateCirclePolygonWithMinVertices() {
    double lat = 40.0;
    double lon = -105.0;
    double radius = 100.0;
    int maxVertices = 4;
    double distanceTolerance = 1.0;

    Geometry circle =
        GeospatialUtil.createCirclePolygon(lat, lon, radius, maxVertices, distanceTolerance);

    assertThat(circle, notNullValue());
    assertThat(circle.getCoordinates().length <= maxVertices + 1, is(true));
  }

  @Test
  public void testCreateCirclePolygonWithMaxVertices() {
    double lat = 40.0;
    double lon = -105.0;
    double radius = 1000.0;
    int maxVertices = 128;
    double distanceTolerance = 0.1;

    Geometry circle =
        GeospatialUtil.createCirclePolygon(lat, lon, radius, maxVertices, distanceTolerance);

    assertThat(circle, notNullValue());
  }

  @Test
  public void testTransformWithNullSourceCRSString() throws GeoFormatException {
    GeometryFactory gf = new GeometryFactory();
    Coordinate coord = new Coordinate(25.22, 33.45);
    Point point = gf.createPoint(coord);

    Geometry result = GeospatialUtil.transformToEPSG4326LonLatFormat(point, (String) null);

    // Should return original geometry
    assertThat(result, is(point));
  }

  @Test
  public void testTransformWithEmptyIdentifiers() throws GeoFormatException, FactoryException {
    GeometryFactory gf = new GeometryFactory();
    Coordinate coord = new Coordinate(25.22, 33.45);
    Point point = gf.createPoint(coord);

    // Create a CRS and then pass null CRS to test the null check
    Geometry result =
        GeospatialUtil.transformToEPSG4326LonLatFormat(point, (CoordinateReferenceSystem) null);

    // Should return original geometry when CRS is null
    assertThat(result, is(point));
  }

  @Test
  public void testConstants() {
    assertThat(GeospatialUtil.EPSG_4326, is("EPSG:4326"));
    assertThat(GeospatialUtil.EPSG_4326_URN, is("urn:ogc:def:crs:EPSG::4326"));
    assertThat(GeospatialUtil.LAT_LON_ORDER, is("LAT_LON"));
    assertThat(GeospatialUtil.LON_LAT_ORDER, is("LON_LAT"));
  }

  @Test
  public void testParseDMSLatitudeExactBoundary() throws GeoFormatException {
    // Test exact boundary values
    String northPole = "90:00:00.0N";
    String southPole = "90:00:00.0S";

    assertThat(GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(northPole), is(90.0));
    assertThat(GeospatialUtil.parseDMSLatitudeWithDecimalSeconds(southPole), is(-90.0));
  }

  @Test
  public void testParseDMSLongitudeExactBoundary() throws GeoFormatException {
    // Test exact boundary values
    String dateline = "180:00:00.0E";
    String datelineWest = "180:00:00.0W";

    assertThat(GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(dateline), is(180.0));
    assertThat(GeospatialUtil.parseDMSLongitudeWithDecimalSeconds(datelineWest), is(-180.0));
  }

  @Test
  public void testCreateCirclePolygonWithZeroTolerance() {
    double lat = 40.0;
    double lon = -105.0;
    double radius = 1000.0;
    int maxVertices = 32;
    double distanceTolerance = 0.0;

    Geometry circle =
        GeospatialUtil.createCirclePolygon(lat, lon, radius, maxVertices, distanceTolerance);

    assertThat(circle, notNullValue());
  }

  @Test
  public void testTransformComplexGeometry()
      throws GeoFormatException, FactoryException, TransformException {
    // Test with an envelope/polygon instead of just a point
    GeometryFactory gf = new GeometryFactory();
    Coordinate c1 = new Coordinate(545000, 2789000);
    Coordinate c2 = new Coordinate(546000, 2790000);
    Envelope envelope = new Envelope(c1, c2);
    Geometry geometry = JTS.toGeometry(envelope);

    CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:32636");
    Geometry result = GeospatialUtil.transformToEPSG4326LonLatFormat(geometry, sourceCRS);

    assertThat(result, notNullValue());
    assertThat(result.getNumPoints() > 0, is(true));
  }
}
