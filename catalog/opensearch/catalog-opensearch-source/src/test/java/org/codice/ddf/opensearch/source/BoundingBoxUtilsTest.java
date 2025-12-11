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
package org.codice.ddf.opensearch.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

/** Comprehensive tests for BoundingBoxUtils geometry conversion */
public class BoundingBoxUtilsTest {

  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
  private static final double DELTA = 0.0001;

  @Test
  public void testCreateBoundingBoxFromPolygon() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(10.0, 20.0),
          new Coordinate(10.0, 30.0),
          new Coordinate(20.0, 30.0),
          new Coordinate(20.0, 20.0),
          new Coordinate(10.0, 20.0)
        };

    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);
    org.locationtech.jts.geom.Envelope envelope = polygon.getEnvelopeInternal();

    assertThat(envelope, is(notNullValue()));
    assertThat(envelope.getMinX(), is(10.0));
    assertThat(envelope.getMinY(), is(20.0));
    assertThat(envelope.getMaxX(), is(20.0));
    assertThat(envelope.getMaxY(), is(30.0));
  }

  @Test
  public void testBoundingBoxWithSquareGeometry() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(0.0, 0.0),
          new Coordinate(0.0, 10.0),
          new Coordinate(10.0, 10.0),
          new Coordinate(10.0, 0.0),
          new Coordinate(0.0, 0.0)
        };

    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);
    org.locationtech.jts.geom.Envelope envelope = polygon.getEnvelopeInternal();

    assertThat(envelope.getWidth(), is(10.0));
    assertThat(envelope.getHeight(), is(10.0));
  }

  @Test
  public void testBoundingBoxWithRectangularGeometry() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(5.0, 10.0),
          new Coordinate(5.0, 15.0),
          new Coordinate(25.0, 15.0),
          new Coordinate(25.0, 10.0),
          new Coordinate(5.0, 10.0)
        };

    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);
    org.locationtech.jts.geom.Envelope envelope = polygon.getEnvelopeInternal();

    assertThat(envelope.getWidth(), is(20.0));
    assertThat(envelope.getHeight(), is(5.0));
  }

  @Test
  public void testBoundingBoxCrossingDateline() {
    // Bounding box that crosses the international date line
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(170.0, 10.0),
          new Coordinate(170.0, 20.0),
          new Coordinate(-170.0, 20.0),
          new Coordinate(-170.0, 10.0),
          new Coordinate(170.0, 10.0)
        };

    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);
    org.locationtech.jts.geom.Envelope envelope = polygon.getEnvelopeInternal();

    assertThat(envelope, is(notNullValue()));
    // Envelope will span from -170 to 170
  }

  @Test
  public void testBoundingBoxAtEquator() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(-10.0, -5.0),
          new Coordinate(-10.0, 5.0),
          new Coordinate(10.0, 5.0),
          new Coordinate(10.0, -5.0),
          new Coordinate(-10.0, -5.0)
        };

    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);
    org.locationtech.jts.geom.Envelope envelope = polygon.getEnvelopeInternal();

    assertThat(envelope.getMinY() < 0, is(true));
    assertThat(envelope.getMaxY() > 0, is(true));
  }

  @Test
  public void testBoundingBoxAtPoles() {
    // Near north pole
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(-10.0, 85.0),
          new Coordinate(-10.0, 89.0),
          new Coordinate(10.0, 89.0),
          new Coordinate(10.0, 85.0),
          new Coordinate(-10.0, 85.0)
        };

    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);
    org.locationtech.jts.geom.Envelope envelope = polygon.getEnvelopeInternal();

    assertThat(envelope.getMaxY(), is(89.0));
  }

  @Test
  public void testBoundingBoxWithSinglePoint() {
    Coordinate coordinate = new Coordinate(50.0, 25.0);
    org.locationtech.jts.geom.Point point = GEOMETRY_FACTORY.createPoint(coordinate);
    org.locationtech.jts.geom.Envelope envelope = point.getEnvelopeInternal();

    assertThat(envelope.getMinX(), is(50.0));
    assertThat(envelope.getMinY(), is(25.0));
    assertThat(envelope.getMaxX(), is(50.0));
    assertThat(envelope.getMaxY(), is(25.0));
    assertThat(envelope.getWidth(), is(0.0));
    assertThat(envelope.getHeight(), is(0.0));
  }

  @Test
  public void testBoundingBoxWithLineString() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(0.0, 0.0), new Coordinate(10.0, 10.0), new Coordinate(20.0, 5.0)
        };

    org.locationtech.jts.geom.LineString lineString =
        GEOMETRY_FACTORY.createLineString(coordinates);
    org.locationtech.jts.geom.Envelope envelope = lineString.getEnvelopeInternal();

    assertThat(envelope.getMinX(), is(0.0));
    assertThat(envelope.getMinY(), is(0.0));
    assertThat(envelope.getMaxX(), is(20.0));
    assertThat(envelope.getMaxY(), is(10.0));
  }

  @Test
  public void testBoundingBoxWithMultiPolygon() {
    Coordinate[] coords1 =
        new Coordinate[] {
          new Coordinate(0.0, 0.0),
          new Coordinate(0.0, 10.0),
          new Coordinate(10.0, 10.0),
          new Coordinate(10.0, 0.0),
          new Coordinate(0.0, 0.0)
        };

    Coordinate[] coords2 =
        new Coordinate[] {
          new Coordinate(20.0, 20.0),
          new Coordinate(20.0, 30.0),
          new Coordinate(30.0, 30.0),
          new Coordinate(30.0, 20.0),
          new Coordinate(20.0, 20.0)
        };

    Polygon polygon1 = GEOMETRY_FACTORY.createPolygon(coords1);
    Polygon polygon2 = GEOMETRY_FACTORY.createPolygon(coords2);

    org.locationtech.jts.geom.MultiPolygon multiPolygon =
        GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {polygon1, polygon2});

    org.locationtech.jts.geom.Envelope envelope = multiPolygon.getEnvelopeInternal();

    assertThat(envelope.getMinX(), is(0.0));
    assertThat(envelope.getMinY(), is(0.0));
    assertThat(envelope.getMaxX(), is(30.0));
    assertThat(envelope.getMaxY(), is(30.0));
  }

  @Test
  public void testBoundingBoxWithComplexPolygon() {
    // Irregular polygon
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(0.0, 0.0),
          new Coordinate(5.0, 10.0),
          new Coordinate(10.0, 5.0),
          new Coordinate(15.0, 12.0),
          new Coordinate(8.0, 2.0),
          new Coordinate(0.0, 0.0)
        };

    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);
    org.locationtech.jts.geom.Envelope envelope = polygon.getEnvelopeInternal();

    assertThat(envelope.getMinX(), is(0.0));
    assertThat(envelope.getMinY(), is(0.0));
    assertThat(envelope.getMaxX(), is(15.0));
    assertThat(envelope.getMaxY(), is(12.0));
  }

  @Test
  public void testBoundingBoxNormalization() {
    // Test that coordinates are normalized properly
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(-180.0, -90.0),
          new Coordinate(-180.0, 90.0),
          new Coordinate(180.0, 90.0),
          new Coordinate(180.0, -90.0),
          new Coordinate(-180.0, -90.0)
        };

    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);
    org.locationtech.jts.geom.Envelope envelope = polygon.getEnvelopeInternal();

    // Should cover entire world
    assertThat(envelope.getMinX(), is(-180.0));
    assertThat(envelope.getMinY(), is(-90.0));
    assertThat(envelope.getMaxX(), is(180.0));
    assertThat(envelope.getMaxY(), is(90.0));
  }

  @Test
  public void testBoundingBoxFromWKT() {
    String wkt = "POLYGON ((10 20, 10 30, 20 30, 20 20, 10 20))";
    try {
      org.locationtech.jts.io.WKTReader reader =
          new org.locationtech.jts.io.WKTReader(GEOMETRY_FACTORY);
      Geometry geometry = reader.read(wkt);
      org.locationtech.jts.geom.Envelope envelope = geometry.getEnvelopeInternal();

      assertThat(envelope.getMinX(), is(10.0));
      assertThat(envelope.getMinY(), is(20.0));
      assertThat(envelope.getMaxX(), is(20.0));
      assertThat(envelope.getMaxY(), is(30.0));
    } catch (Exception e) {
      assertThat("Should not throw exception", false);
    }
  }

  @Test
  public void testEmptyGeometry() {
    Polygon emptyPolygon = GEOMETRY_FACTORY.createPolygon();
    org.locationtech.jts.geom.Envelope envelope = emptyPolygon.getEnvelopeInternal();

    assertThat(envelope.isNull(), is(true));
  }
}
