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
package ddf.catalog.impl.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

class SpatialFilterTest {

  private static final String POINT_WKT = "POINT (30 10)";
  private static final String POLYGON_WKT = "POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))";

  @Test
  void testDefaultConstructor() {
    SpatialFilter filter = new SpatialFilter();

    assertThat(filter.getGeometryWkt(), is(nullValue()));
  }

  @Test
  void testConstructorWithWkt() {
    SpatialFilter filter = new SpatialFilter(POINT_WKT);

    assertThat(filter.getGeometryWkt(), is(POINT_WKT));
  }

  @Test
  void testSetAndGetGeometryWkt() {
    SpatialFilter filter = new SpatialFilter();

    filter.setGeometryWkt(POLYGON_WKT);

    assertThat(filter.getGeometryWkt(), is(POLYGON_WKT));
  }

  @Test
  void testGetGeometryWithValidPoint() {
    SpatialFilter filter = new SpatialFilter(POINT_WKT);

    Geometry geometry = filter.getGeometry();

    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.getGeometryType(), is("Point"));
  }

  @Test
  void testGetGeometryWithValidPolygon() {
    SpatialFilter filter = new SpatialFilter(POLYGON_WKT);

    Geometry geometry = filter.getGeometry();

    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.getGeometryType(), is("Polygon"));
  }

  @Test
  void testGetGeometryWithInvalidWkt() {
    SpatialFilter filter = new SpatialFilter("INVALID WKT");

    Geometry geometry = filter.getGeometry();

    assertThat(geometry, is(nullValue()));
  }

  @Test
  void testConstructorWithNullWkt() {
    SpatialFilter filter = new SpatialFilter(null);

    // Geometry WKT should be null when constructed with null
    assertThat(filter.getGeometryWkt(), is(nullValue()));
  }

  @Test
  void testUpdateWktAfterConstruction() {
    SpatialFilter filter = new SpatialFilter(POINT_WKT);
    filter.setGeometryWkt(POLYGON_WKT);

    assertThat(filter.getGeometryWkt(), is(POLYGON_WKT));
    assertThat(filter.getGeometry().getGeometryType(), is("Polygon"));
  }

  @Test
  void testGetGeometryWithLineString() {
    SpatialFilter filter = new SpatialFilter("LINESTRING (30 10, 10 30, 40 40)");

    Geometry geometry = filter.getGeometry();

    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.getGeometryType(), is("LineString"));
  }
}
