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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for BBoxSpatialFilter */
@ExtendWith(MockitoExtension.class)
public class BBoxSpatialFilterTest {

  @Test
  public void testValidBBox() {
    String bbox = "-10.0,-20.0,10.0,20.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testValidBBoxPositiveCoordinates() {
    String bbox = "10.5,20.5,30.5,40.5";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("10.5"));
    assertThat(filter.getGeometryWkt(), containsString("20.5"));
    assertThat(filter.getGeometryWkt(), containsString("30.5"));
    assertThat(filter.getGeometryWkt(), containsString("40.5"));
  }

  @Test
  public void testValidBBoxNegativeCoordinates() {
    String bbox = "-180.0,-90.0,-170.0,-80.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("-180.0"));
    assertThat(filter.getGeometryWkt(), containsString("-90.0"));
    assertThat(filter.getGeometryWkt(), containsString("-170.0"));
    assertThat(filter.getGeometryWkt(), containsString("-80.0"));
  }

  @Test
  public void testValidBBoxMixedCoordinates() {
    String bbox = "-10.0,20.0,10.0,40.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testValidBBoxIntegerCoordinates() {
    String bbox = "0,0,100,100";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testValidBBoxZeroCoordinates() {
    String bbox = "0.0,0.0,0.0,0.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testValidBBoxWorldCoverage() {
    String bbox = "-180.0,-90.0,180.0,90.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
    assertThat(filter.getGeometryWkt(), containsString("-180.0"));
    assertThat(filter.getGeometryWkt(), containsString("180.0"));
    assertThat(filter.getGeometryWkt(), containsString("-90.0"));
    assertThat(filter.getGeometryWkt(), containsString("90.0"));
  }

  @Test
  public void testWKTFormat() {
    String bbox = "1.0,2.0,3.0,4.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    String wkt = filter.getGeometryWkt();
    assertThat(wkt, notNullValue());
    // WKT should be: POLYGON((minX minY,minX maxY,maxX maxY,maxX minY,minX minY))
    assertThat(wkt, containsString("POLYGON((1.0 2.0,1.0 4.0,3.0 4.0,3.0 2.0,1.0 2.0))"));
  }

  @Test
  public void testInvalidBBoxTooFewValues() {
    String bbox = "10.0,20.0,30.0";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testInvalidBBoxTooManyValues() {
    String bbox = "10.0,20.0,30.0,40.0,50.0";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testInvalidBBoxNonNumeric() {
    String bbox = "a,b,c,d";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testInvalidBBoxMixedValidInvalid() {
    String bbox = "10.0,20.0,abc,40.0";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testInvalidBBoxEmpty() {
    String bbox = "";

    assertThrows(Exception.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testInvalidBBoxOnlyCommas() {
    String bbox = ",,,";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testInvalidBBoxSpaces() {
    String bbox = "10.0 20.0 30.0 40.0";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testInvalidBBoxMissingValue() {
    String bbox = "10.0,20.0,,40.0";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testInvalidBBoxExtraComma() {
    String bbox = "10.0,20.0,30.0,40.0,";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testBBoxWithVerySmallDecimal() {
    String bbox = "0.000001,0.000002,0.000003,0.000004";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testBBoxWithVeryLargeNumbers() {
    String bbox = "1000000.0,2000000.0,3000000.0,4000000.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }

  @Test
  public void testBBoxWithScientificNotation() {
    // Scientific notation is not supported by the pattern
    String bbox = "1.0e2,2.0e2,3.0e2,4.0e2";

    assertThrows(IllegalArgumentException.class, () -> new BBoxSpatialFilter(bbox));
  }

  @Test
  public void testBBoxGeometryWktNotNull() {
    String bbox = "-10.0,-20.0,10.0,20.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter.getGeometryWkt(), notNullValue());
    assertThat(filter.getGeometryWkt().length() > 0, is(true));
  }

  @Test
  public void testBBoxWithNegativeZero() {
    String bbox = "-0.0,-0.0,0.0,0.0";

    BBoxSpatialFilter filter = new BBoxSpatialFilter(bbox);

    assertThat(filter, notNullValue());
    assertThat(filter.getGeometryWkt(), containsString("POLYGON"));
  }
}
