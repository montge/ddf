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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BoundingBoxReaderTest {

  private static final String EPSG_4326_URN = "urn:ogc:def:crs:EPSG::4326";
  private static final String EPSG_4326_URN_LAT_LON = "urn:x-ogc:def:crs:EPSG:6.11:4326";
  private static final String CRS_84 = "urn:ogc:def:crs:OGC:1.3:CRS84";

  @Mock private HierarchicalStreamReader mockReader;

  private WKTReader wktReader;

  @BeforeEach
  public void setUp() {
    wktReader = new WKTReader();
  }

  /**
   * Sets up mock reader to simulate XML navigation. BoundingBoxReader calls: 1. getNodeName() -
   * expects "BoundingBox" 2. getAttribute("crs") - get CRS 3. moveDown() - move to LowerCorner 4.
   * getNodeName() - expects "LowerCorner" 5. getValue() - get lower corner coords 6. moveUp() -
   * back to BoundingBox 7. moveDown() - move to UpperCorner 8. getNodeName() - expects
   * "UpperCorner" 9. getValue() - get upper corner coords 10. moveUp() - back to BoundingBox
   */
  private void setupMockReader(
      String crs, String lowerCorner, String upperCorner, String... nodeNames) {
    // Track position in XML tree: 0=BoundingBox, 1=LowerCorner, 2=UpperCorner
    AtomicInteger position = new AtomicInteger(0);

    // Default node names if not specified
    String boundingBoxNode = nodeNames.length > 0 ? nodeNames[0] : "ows:BoundingBox";
    String lowerCornerNode = nodeNames.length > 1 ? nodeNames[1] : "ows:LowerCorner";
    String upperCornerNode = nodeNames.length > 2 ? nodeNames[2] : "ows:UpperCorner";

    when(mockReader.getNodeName())
        .thenAnswer(
            inv -> {
              switch (position.get()) {
                case 0:
                  return boundingBoxNode;
                case 1:
                  return lowerCornerNode;
                case 2:
                  return upperCornerNode;
                default:
                  return boundingBoxNode;
              }
            });

    when(mockReader.getAttribute("crs")).thenReturn(crs);

    when(mockReader.getValue())
        .thenAnswer(
            inv -> {
              if (position.get() == 1) {
                return lowerCorner;
              } else if (position.get() == 2) {
                return upperCorner;
              }
              return "";
            });

    // moveDown goes from BoundingBox(0) -> LowerCorner(1); the second moveDown is reached at
    // position 2 (set by the preceding moveUp), so no transition is needed there.
    doAnswer(
            inv -> {
              if (position.get() == 0) {
                position.set(1); // First moveDown goes to LowerCorner
              }
              return null;
            })
        .when(mockReader)
        .moveDown();

    // moveUp goes back to parent, and next moveDown goes to next sibling
    doAnswer(
            inv -> {
              if (position.get() == 1) {
                position.set(2); // After moveUp from LowerCorner, next moveDown goes to UpperCorner
              } else {
                position.set(0); // Go back to BoundingBox
              }
              return null;
            })
        .when(mockReader)
        .moveUp();
  }

  @Test
  public void testBoundingBoxWithEpsg4326LonLatOrder() throws Exception {
    setupMockReader(EPSG_4326_URN, "13.754 60.042", "17.920 68.410");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry, is(notNullValue()));
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testBoundingBoxWithCrs84() throws Exception {
    setupMockReader(CRS_84, "13.754 60.042", "17.920 68.410");

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
    setupMockReader(EPSG_4326_URN, "13.754 60.042", "13.754 60.042");

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
    setupMockReader("", "13.754 60.042", "17.920 68.410");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));
  }

  @Test
  public void testBoundingBoxWithNullCrs() throws Exception {
    // Test with null CRS (should default to EPSG:4326)
    setupMockReader(null, "13.754 60.042", "17.920 68.410");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
  }

  @Test
  public void testInvalidNodeName() {
    // Setup mock with invalid node name
    when(mockReader.getNodeName()).thenReturn("InvalidNode");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    assertThrows(CswException.class, () -> reader.getWkt());
  }

  @Test
  public void testNegativeCoordinates() throws Exception {
    setupMockReader(EPSG_4326_URN, "-120.0 -35.0", "-110.0 -25.0");

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
    setupMockReader(EPSG_4326_URN, "170.0 -10.0", "-170.0 10.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));
  }

  @Test
  public void testPolarRegionBoundingBox() throws Exception {
    setupMockReader(EPSG_4326_URN, "-180.0 85.0", "180.0 90.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testSmallBoundingBox() throws Exception {
    setupMockReader(EPSG_4326_URN, "13.75400 60.04200", "13.75401 60.04201");

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
    setupMockReader(EPSG_4326_URN, "-180.0 -90.0", "180.0 90.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON"));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.isValid(), is(true));
  }

  @Test
  public void testPolygonCoordinateOrder() throws Exception {
    setupMockReader(EPSG_4326_URN, "10.0 20.0", "30.0 40.0");

    BoundingBoxReader reader = new BoundingBoxReader(mockReader, CswAxisOrder.LON_LAT);
    String wkt = reader.getWkt();

    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, startsWith("POLYGON (("));

    // Should contain all four corners
    assertThat(wkt, containsString("10.0 20.0"));
    assertThat(wkt, containsString("30.0 20.0"));
    assertThat(wkt, containsString("30.0 40.0"));
    assertThat(wkt, containsString("10.0 40.0"));

    Geometry geometry = wktReader.read(wkt);
    assertThat(geometry.getCoordinates().length, is(5)); // 4 corners + closing point
  }
}
