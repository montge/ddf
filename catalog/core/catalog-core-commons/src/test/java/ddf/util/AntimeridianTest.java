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
package ddf.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;

import java.util.List;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class AntimeridianTest {

  private static final WKTReader WKT_READER = new WKTReader();
  private static final GeometryFactory GEO_FACTORY = new GeometryFactory();

  private Geometry getBoundaryFromWkt(String wkt) {
    try {
      Geometry geo = WKT_READER.read(wkt);
      return geo.getBoundary();
    } catch (ParseException e) {
      throw new IllegalArgumentException("Unable to parse WKT", e);
    }
  }

  @Test
  public void testWktMultipolyToSinglePolygons() {
    String inputWkt =
        "MULTIPOLYGON (((-90 26, -90 70, -44 70, -44 26, -90 26)), ((180 70, 180 26, 162 26, 162 70, 180 70)))";
    String expectedA = "POLYGON ((-90 26, -90 70, -44 70, -44 26, -90 26))";
    String expectedB = "POLYGON ((180 70, 180 26, 162 26, 162 70, 180 70))";

    List<String> resultWkt = Antimeridian.wktMultipolyToSinglePolygons(inputWkt);
    assertThat(resultWkt, hasItem(expectedA));
    assertThat(resultWkt, hasItem(expectedB));
  }

  @Test
  public void testValidCoordinatesAreNotChanged() {
    String inputWkt = "POLYGON ((170 33, 179 44, 101 55, 111 44))";
    String resultWkt = Antimeridian.normalizeWkt(inputWkt);
    assertThat(resultWkt, is(inputWkt));
  }

  @Test
  public void testInvalidCoordinatesAreNormalized() {
    String originalWkt =
        "POLYGON ((182.421875 68.656555, 226.757813 69.037142, 226.757813 26.431228, 194.6875 27.994401, 182.421875 68.656555))";
    String expectedWkt =
        "POLYGON ((-177.578125 68.656555, -133.242187 69.037142, -133.242187 26.431228, -165.3125 27.994401, -177.578125 68.656555))";
    String actualWkt = Antimeridian.normalizeWkt(originalWkt);
    assertThat(actualWkt, is(expectedWkt));
  }

  @Test
  public void testAntimeridianCrossingIsSplit() {
    String originalWkt = "POLYGON ((162 70, 226 70, 226 26, 162 26, 162 70))";
    String expectedWkt =
        "MULTIPOLYGON (((-180 26, -180 70, -134 70, -134 26, -180 26)), ((180 70, 180 26, 162 26, 162 70, 180 70)))";
    String actualWkt = Antimeridian.unwrapAndSplitWkt(originalWkt);
    assertThat(actualWkt, is(expectedWkt));
    Geometry originalBounds = getBoundaryFromWkt(originalWkt);
    Geometry updatedBounds = getBoundaryFromWkt(actualWkt);
    assertThat(originalBounds.getArea(), is(updatedBounds.getArea()));
  }

  @Test
  public void testNonCrossingShapesAreNotSplit() {
    String inputWkt =
        "POLYGON ((-98.085938 42.55308, -113.90625 35.173808, -90 34.307144, -98.085938 42.55308))";
    String outputWkt = Antimeridian.unwrapAndSplitWkt(inputWkt);
    assertThat(outputWkt, is(inputWkt));
  }

  @Test
  public void testWktIsMultipolygonTrue() {
    String multiPolygonWkt =
        "MULTIPOLYGON (((-90 26, -90 70, -44 70, -44 26, -90 26)), ((180 70, 180 26, 162 26, 162 70, 180 70)))";
    assertThat(Antimeridian.wktIsMultipolygon(multiPolygonWkt), is(true));
  }

  @Test
  public void testWktIsMultipolygonFalse() {
    String polygonWkt = "POLYGON ((-90 26, -90 70, -44 70, -44 26, -90 26))";
    assertThat(Antimeridian.wktIsMultipolygon(polygonWkt), is(false));
  }

  @Test
  public void testWktIsMultipolygonCaseInsensitive() {
    String lowerCaseWkt = "multipolygon (((-90 26, -90 70, -44 70, -44 26, -90 26)))";
    assertThat(Antimeridian.wktIsMultipolygon(lowerCaseWkt), is(true));
  }

  @Test
  public void testWktMultipolyToSinglePolygonsWithInvalidWkt() {
    String invalidWkt = "INVALID WKT";
    List<String> result = Antimeridian.wktMultipolyToSinglePolygons(invalidWkt);
    assertThat(result, is(empty()));
  }

  @Test
  public void testNormalizeWktWithNegativeOutOfRange() {
    String originalWkt = "POLYGON ((-200 40, -190 50, -185 30, -200 40))";
    String resultWkt = Antimeridian.normalizeWkt(originalWkt);
    assertThat(resultWkt, is(notNullValue()));
    // -200 + 360 = 160, -190 + 360 = 170, -185 + 360 = 175
    assertThat(resultWkt.contains("160"), is(true));
  }

  @Test
  public void testNormalizeWktWithInvalidWkt() {
    String invalidWkt = "NOT A VALID WKT";
    String result = Antimeridian.normalizeWkt(invalidWkt);
    // Should return original WKT when parsing fails
    assertThat(result, is(invalidWkt));
  }

  @Test
  public void testUnwrapAndSplitWktWithInvalidWkt() {
    String invalidWkt = "INVALID";
    String result = Antimeridian.unwrapAndSplitWkt(invalidWkt);
    // Should return original WKT when parsing fails
    assertThat(result, is(invalidWkt));
  }

  @Test
  public void testUnwrapAntimeridianSmallGeometry() throws ParseException {
    // Geometry with width less than 180 degrees should not be modified
    String smallWkt = "POLYGON ((10 10, 20 10, 20 20, 10 20, 10 10))";
    Geometry geo = WKT_READER.read(smallWkt);
    Geometry result = Antimeridian.unwrapAntimeridian(geo);
    assertThat(result, is(geo));
  }

  @Test
  public void testUnwrapAntimeridianMultiPoint() throws ParseException {
    // MultiPoint should not be modified
    String multiPointWkt = "MULTIPOINT ((-170 40), (170 50), (-160 30))";
    Geometry geo = WKT_READER.read(multiPointWkt);
    assertThat(geo instanceof MultiPoint, is(true));
    Geometry result = Antimeridian.unwrapAntimeridian(geo);
    assertThat(result, is(geo));
  }

  @Test
  public void testUnwrapGeoCollectionWithSmallGeometries() throws ParseException {
    String wkt =
        "GEOMETRYCOLLECTION (POLYGON ((10 10, 20 10, 20 20, 10 20, 10 10)), POINT (15 15))";
    Geometry geo = WKT_READER.read(wkt);
    assertThat(geo instanceof GeometryCollection, is(true));
    Geometry result = Antimeridian.unwrapGeoCollection(geo);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testUnwrapLineStringShortLine() throws ParseException {
    // Line with single point should return 0
    String lineWkt = "LINESTRING (10 10)";
    // Note: JTS requires at least 2 points for a valid LineString
    // Let's use 2 points that are close together
    String validLineWkt = "LINESTRING (10 10, 11 11)";
    Geometry geo = WKT_READER.read(validLineWkt);
    assertThat(geo instanceof LineString, is(true));
    int result = Antimeridian.unwrapLineString((LineString) geo);
    assertThat(result, is(0));
  }

  @Test
  public void testUnwrapLineStringCrossingAntimeridian() throws ParseException {
    // Line crossing antimeridian
    String lineWkt = "LINESTRING (170 40, -170 40)";
    Geometry geo = WKT_READER.read(lineWkt);
    assertThat(geo instanceof LineString, is(true));
    int result = Antimeridian.unwrapLineString((LineString) geo);
    assertThat(result >= 0, is(true));
  }

  @Test
  public void testCutUnwrappedGeomInto360ValidGeometry() throws ParseException {
    String wkt = "POLYGON ((170 40, 190 40, 190 50, 170 50, 170 40))";
    Geometry geo = WKT_READER.read(wkt);
    Geometry result = Antimeridian.cutUnwrappedGeomInto360(geo);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testCutUnwrappedGeomInto360WithinBounds() throws ParseException {
    // Geometry already within -180 to 180 should not be modified
    String wkt = "POLYGON ((10 10, 20 10, 20 20, 10 20, 10 10))";
    Geometry geo = WKT_READER.read(wkt);
    Geometry result = Antimeridian.cutUnwrappedGeomInto360(geo);
    assertThat(result, is(geo));
  }

  @Test
  public void testCutUnwrappedGeomInto360InvalidGeometry() throws ParseException {
    // Create an invalid geometry (self-intersecting polygon)
    String invalidWkt = "POLYGON ((0 0, 10 10, 0 10, 10 0, 0 0))";
    Geometry geo = WKT_READER.read(invalidWkt);
    assertThat(geo.isValid(), is(false));
    assertThrows(IllegalArgumentException.class, () -> Antimeridian.cutUnwrappedGeomInto360(geo));
  }

  @Test
  public void testShiftGeomByXZeroShift() throws ParseException {
    String wkt = "POLYGON ((10 10, 20 10, 20 20, 10 20, 10 10))";
    Geometry geo = WKT_READER.read(wkt);
    Geometry original = geo.copy();
    Antimeridian.shiftGeomByX(geo, 0);
    // Should not be modified
    assertThat(geo.equals(original), is(true));
  }

  @Test
  public void testShiftGeomByXPositiveShift() throws ParseException {
    String wkt = "POLYGON ((10 10, 20 10, 20 20, 10 20, 10 10))";
    Geometry geo = WKT_READER.read(wkt);
    Antimeridian.shiftGeomByX(geo, 360);
    // Check that coordinates were shifted
    assertThat(geo.getCoordinates()[0].x, is(370.0));
  }

  @Test
  public void testShiftGeomByXNegativeShift() throws ParseException {
    String wkt = "POLYGON ((10 10, 20 10, 20 20, 10 20, 10 10))";
    Geometry geo = WKT_READER.read(wkt);
    Antimeridian.shiftGeomByX(geo, -360);
    // Check that coordinates were shifted
    assertThat(geo.getCoordinates()[0].x, is(-350.0));
  }

  @Test
  public void testUnwrapGeoWithLineString() throws ParseException {
    // Large line that spans more than 180 degrees
    String lineWkt = "LINESTRING (-170 40, 170 40)";
    Geometry geo = WKT_READER.read(lineWkt);
    Geometry result = Antimeridian.unwrapGeo(geo);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testUnwrapPolygonNoInteriorRings() throws ParseException {
    // Simple polygon with no crossing
    String polygonWkt = "POLYGON ((160 30, 170 30, 170 50, 160 50, 160 30))";
    Geometry geo = WKT_READER.read(polygonWkt);
    int result = Antimeridian.unwrapPolygon((org.locationtech.jts.geom.Polygon) geo);
    assertThat(result, is(0));
  }
}
