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
package ddf.catalog.transformer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ddf.catalog.transform.CatalogTransformerException;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

class GeometryUtilsTest {

  @Test
  void testParseGeometryValidPolygon() throws CatalogTransformerException {
    String wkt = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";

    Geometry geometry = GeometryUtils.parseGeometry(wkt);

    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.getGeometryType(), is("Polygon"));
  }

  @Test
  void testParseGeometryValidPoint() throws CatalogTransformerException {
    String wkt = "POINT (10 20)";

    Geometry geometry = GeometryUtils.parseGeometry(wkt);

    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.getGeometryType(), is("Point"));
  }

  @Test
  void testParseGeometryValidLineString() throws CatalogTransformerException {
    String wkt = "LINESTRING (0 0, 1 1, 2 2)";

    Geometry geometry = GeometryUtils.parseGeometry(wkt);

    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.getGeometryType(), is("LineString"));
  }

  @Test
  void testParseGeometryNullThrowsException() {
    assertThrows(CatalogTransformerException.class, () -> GeometryUtils.parseGeometry(null));
  }

  @Test
  void testParseGeometryEmptyStringThrowsException() {
    assertThrows(CatalogTransformerException.class, () -> GeometryUtils.parseGeometry(""));
  }

  @Test
  void testParseGeometryBlankStringThrowsException() {
    assertThrows(CatalogTransformerException.class, () -> GeometryUtils.parseGeometry("   "));
  }

  @Test
  void testParseGeometryInvalidWktThrowsException() {
    assertThrows(
        CatalogTransformerException.class, () -> GeometryUtils.parseGeometry("INVALID WKT"));
  }

  @Test
  void testParseGeometryMalformedPolygonThrowsException() {
    assertThrows(Exception.class, () -> GeometryUtils.parseGeometry("POLYGON ((0 0, 1 0))"));
  }

  @Test
  void testCanHandleGeometryWithFiveCoordinates() throws CatalogTransformerException {
    String wkt = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
    Geometry geometry = GeometryUtils.parseGeometry(wkt);

    boolean result = GeometryUtils.canHandleGeometry(geometry);

    assertThat(result, is(true));
  }

  @Test
  void testCanHandleGeometryWithMoreThanFiveCoordinates() throws CatalogTransformerException {
    String wkt = "POLYGON ((0 0, 1 0, 1 1, 0.5 1.5, 0 1, 0 0))";
    Geometry geometry = GeometryUtils.parseGeometry(wkt);

    boolean result = GeometryUtils.canHandleGeometry(geometry);

    assertThat(result, is(false));
  }

  @Test
  void testCanHandleGeometryWithLessThanFiveCoordinates() throws CatalogTransformerException {
    String wkt = "LINESTRING (0 0, 1 1, 2 2)";
    Geometry geometry = GeometryUtils.parseGeometry(wkt);

    boolean result = GeometryUtils.canHandleGeometry(geometry);

    assertThat(result, is(false));
  }

  @Test
  void testCanHandleGeometryWithPoint() throws CatalogTransformerException {
    String wkt = "POINT (0 0)";
    Geometry geometry = GeometryUtils.parseGeometry(wkt);

    boolean result = GeometryUtils.canHandleGeometry(geometry);

    assertThat(result, is(false));
  }

  @Test
  void testCanHandleGeometryRectangle() throws CatalogTransformerException {
    String wkt = "POLYGON ((10 10, 20 10, 20 20, 10 20, 10 10))";
    Geometry geometry = GeometryUtils.parseGeometry(wkt);

    boolean result = GeometryUtils.canHandleGeometry(geometry);

    assertThat(result, is(true));
  }
}
