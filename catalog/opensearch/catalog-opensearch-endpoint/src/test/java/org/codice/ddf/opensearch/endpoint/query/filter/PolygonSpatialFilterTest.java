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
package org.codice.ddf.opensearch.endpoint.query.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for PolygonSpatialFilter */
@ExtendWith(MockitoExtension.class)
public class PolygonSpatialFilterTest {

  @Test
  public void testValidTriangle() {
    // Polygon: lat1,lon1,lat2,lon2,lat3,lon3,lat1,lon1 (closed polygon)
    String polygon = "10.0,20.0,30.0,40.0,50.0,60.0,10.0,20.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), notNullValue());
    assertThat(filter.getGeometryWkt(), startsWith("POLYGON(("));
    assertThat(filter.getGeometryWkt(), endsWith("))"));
  }

  @Test
  public void testValidSquare() {
    // Square polygon
    String polygon = "0.0,0.0,0.0,10.0,10.0,10.0,10.0,0.0,0.0,0.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    // WKT uses lon,lat order (reversed from input)
    assertThat(filter.getGeometryWkt(), containsString("0.0 0.0"));
    assertThat(filter.getGeometryWkt(), containsString("10.0 0.0"));
    assertThat(filter.getGeometryWkt(), containsString("10.0 10.0"));
    assertThat(filter.getGeometryWkt(), containsString("0.0 10.0"));
  }

  @Test
  public void testValidPolygonNegativeCoordinates() {
    String polygon = "-10.0,-20.0,-30.0,-40.0,-50.0,-60.0,-10.0,-20.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("-20.0 -10.0"));
    assertThat(filter.getGeometryWkt(), containsString("-40.0 -30.0"));
    assertThat(filter.getGeometryWkt(), containsString("-60.0 -50.0"));
  }

  @Test
  public void testValidPolygonMixedCoordinates() {
    String polygon = "-10.0,20.0,30.0,-40.0,50.0,60.0,-10.0,20.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testPolygonWKTCoordinateOrder() {
    // Input is lat,lon pairs but WKT needs lon,lat
    String polygon = "1.0,2.0,3.0,4.0,5.0,6.0,1.0,2.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    String wkt = filter.getGeometryWkt();
    assertThat(wkt, notNullValue());
    // Should convert lat,lon to lon,lat
    assertThat(wkt, containsString("2.0 1.0")); // First point
    assertThat(wkt, containsString("4.0 3.0")); // Second point
    assertThat(wkt, containsString("6.0 5.0")); // Third point
  }

  @Test
  public void testPolygonWithFourPoints() {
    String polygon = "10.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0,10.0,20.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testPolygonWithManyPoints() {
    String polygon = "0,0,10,0,20,0,30,0,40,0,50,0,50,10,40,10,30,10,20,10,10,10,0,10,0,0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testPolygonClosedShape() {
    // First and last points should be the same
    String polygon = "5.0,10.0,15.0,20.0,25.0,30.0,5.0,10.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    String wkt = filter.getGeometryWkt();
    assertThat(wkt, startsWith("POLYGON(("));
    assertThat(wkt, endsWith("))"));
    // First and last coordinates should match in WKT
    assertThat(wkt, containsString("10.0 5.0"));
  }

  @Test
  public void testPolygonWithDecimalCoordinates() {
    String polygon = "12.3456,78.9012,34.5678,90.1234,56.7890,12.3456,12.3456,78.9012";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("78.9012"));
    assertThat(filter.getGeometryWkt(), containsString("12.3456"));
  }

  @Test
  public void testPolygonWithZeroCoordinates() {
    String polygon = "0.0,0.0,0.0,1.0,1.0,1.0,1.0,0.0,0.0,0.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("0.0 0.0"));
  }

  @Test
  public void testPolygonWKTFormat() {
    String polygon = "1.0,2.0,3.0,4.0,5.0,6.0,1.0,2.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    String wkt = filter.getGeometryWkt();
    assertThat(wkt, startsWith("POLYGON(("));
    assertThat(wkt, endsWith("))"));
    // Check comma separation between points
    String[] parts = wkt.substring("POLYGON((".length(), wkt.length() - 2).split(",");
    assertThat(parts.length, is(4)); // 4 points
  }

  @Test
  public void testPolygonEmptyString() {
    String polygon = "";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    // Should create a WKT with no points
    assertThat(filter.getGeometryWkt(), is("POLYGON(())"));
  }

  @Test
  public void testPolygonSinglePoint() {
    String polygon = "10.0,20.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("20.0 10.0"));
  }

  @Test
  public void testPolygonTwoPoints() {
    String polygon = "10.0,20.0,30.0,40.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("20.0 10.0"));
    assertThat(filter.getGeometryWkt(), containsString("40.0 30.0"));
  }

  @Test
  public void testPolygonWithVerySmallDecimals() {
    String polygon = "0.000001,0.000002,0.000003,0.000004,0.000005,0.000006,0.000001,0.000002";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testPolygonWithLargeNumbers() {
    String polygon =
        "1000000.0,2000000.0,3000000.0,4000000.0,5000000.0,6000000.0,1000000.0,2000000.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testPolygonWorldBounds() {
    String polygon = "-90.0,-180.0,90.0,-180.0,90.0,180.0,-90.0,180.0,-90.0,-180.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("-180.0 -90.0"));
    assertThat(filter.getGeometryWkt(), containsString("180.0 90.0"));
  }

  @Test
  public void testPolygonComplexShape() {
    // 6-sided polygon (hexagon)
    String polygon = "0,10,5,15,15,15,20,10,15,5,5,5,0,10";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testPolygonGeometryWktNotNull() {
    String polygon = "10.0,20.0,30.0,40.0,50.0,60.0,10.0,20.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter.getGeometryWkt(), notNullValue());
    assertThat(filter.getGeometryWkt().length() > 0, is(true));
  }

  @Test
  public void testPolygonWithNegativeZero() {
    String polygon = "-0.0,-0.0,0.0,0.0,1.0,1.0,-0.0,-0.0";

    PolygonSpatialFilter filter = new PolygonSpatialFilter(polygon);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }
}
