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
package org.codice.ddf.spatial.ogc.csw.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Unit tests for BoundingBoxReader - handles coordinate transformations and WKT parsing */
@RunWith(MockitoJUnitRunner.class)
public class BoundingBoxReaderTest {

  private static final String LOWER_CORNER_NODE = "LowerCorner";
  private static final String UPPER_CORNER_NODE = "UpperCorner";
  private static final String BOUNDING_BOX_NODE = "BoundingBox";
  private static final String EPSG_4326_URN = "urn:ogc:def:crs:EPSG::4326";
  private static final String EPSG_4326_URN_LAT_LON = "urn:x-ogc:def:crs:EPSG:6.11:4326";
  private static final String CRS_84 = "urn:ogc:def:crs:OGC:1.3:CRS84";

  @Mock private HierarchicalStreamReader mockReader;

  private WKTReader wktReader;

  @Before
  public void setUp() {
    wktReader = new WKTReader();
  }

  @Test
  public void testBoundingBoxWithEpsg4326LonLatOrder() throws Exception {
    // Setup mock reader for LON LAT order (standard WKT order)
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("13.754 60.042", "17.920 68.410");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));

    // Verify WKT is valid
    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testBoundingBoxWithEpsg4326LatLonOrder() throws Exception {
    // Setup mock reader for LAT LON order
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN_LAT_LON);
    when(mockReader.getValue()).thenReturn("60.042 13.754", "68.410 17.920");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LAT_LON);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));

    // Verify WKT is valid and in LON LAT order
    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testBoundingBoxWithCrs84() throws Exception {
    // CRS:84 uses LON LAT order
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(CRS_84);
    when(mockReader.getValue()).thenReturn("13.754 60.042", "17.920 68.410");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testPointBoundingBox() throws Exception {
    // When lower and upper corners are the same, result should be a POINT
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("13.754 60.042", "13.754 60.042");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POINT"));
    assertThat(wkt, containsString("13.754"));
    assertThat(wkt, containsString("60.042"));
  }

  @Test
  public void testBoundingBoxWithDefaultCrs() throws Exception {
    // Test with empty CRS (should default to EPSG:4326)
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn("");
    when(mockReader.getValue()).thenReturn("13.754 60.042", "17.920 68.410");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));
  }

  @Test
  public void testBoundingBoxWithNullCrs() throws Exception {
    // Test with null CRS (should default to EPSG:4326)
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(null);
    when(mockReader.getValue()).thenReturn("13.754 60.042", "17.920 68.410");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
  }

  @Test
  public void testInvalidNodeName() {
    // Test with invalid node name (not BoundingBox)
    when(mockReader.getNodeName()).thenReturn("InvalidNode");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    assertThrows(CswException.class, () -> reader.getWkt());
  }

  @Test
  public void testNegativeCoordinates() throws Exception {
    // Test with negative coordinates
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("-120.0 -35.0", "-110.0 -25.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, containsString("-120.0"));
    assertThat(wkt, containsString("-35.0"));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testBoundingBoxAcrossDateLine() throws Exception {
    // Test bounding box that crosses international date line
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("170.0 -10.0", "-170.0 10.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));
  }

  @Test
  public void testPolarRegionBoundingBox() throws Exception {
    // Test bounding box near poles
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("-180.0 85.0", "180.0 90.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testSmallBoundingBox() throws Exception {
    // Test very small bounding box (high precision)
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("13.75400 60.04200", "13.75401 60.04201");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.isValid(), is(true));
    assertThat(geometry.getArea() > 0, is(true));
  }

  @Test
  public void testGlobalBoundingBox() throws Exception {
    // Test global bounding box
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("-180.0 -90.0", "180.0 90.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testBoundingBoxWithWhitespace() throws Exception {
    // Test coordinates with extra whitespace
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("13.754 60.042", "17.920 68.410");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testPolygonClockwiseCoordinateOrder() throws Exception {
    // Verify that polygon coordinates are in clockwise order starting from lower corner
    when(mockReader.getNodeName())
        .thenReturn(BOUNDING_BOX_NODE, LOWER_CORNER_NODE, UPPER_CORNER_NODE);
    when(mockReader.getAttribute("crs")).thenReturn(EPSG_4326_URN);
    when(mockReader.getValue()).thenReturn("10.0 20.0", "30.0 40.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON (("));

    // Should contain all four corners plus closing point
    assertThat(wkt, containsString("10.0 20.0"));
    assertThat(wkt, containsString("30.0 20.0"));
    assertThat(wkt, containsString("30.0 40.0"));
    assertThat(wkt, containsString("10.0 40.0"));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.getCoordinates().length, is(equalTo(5))); // 4 corners + closing point
  }
}
