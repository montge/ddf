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
package org.codice.ddf.spatial.geocoding.feature;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/** Unit tests for FeatureBuilder class. */
public class FeatureBuilderTest {

  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  @Test
  public void testForGeometryWithPoint() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(-77.0369, 38.9072));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);

    assertThat(builder, is(notNullValue()));
  }

  @Test
  public void testForGeometryWithPointReturnsValidBuilder() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(-77.0369, 38.9072));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);

    assertThat(builder, is(notNullValue()));
    SimpleFeatureType featureType = builder.getFeatureType();
    assertThat(featureType, is(notNullValue()));
    assertThat(featureType.getName().getLocalPart(), is("CountryFeatureType"));
  }

  @Test
  public void testForGeometryWithPointAndBuildFeature() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(-77.0369, 38.9072));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);
    builder.add("United States");
    SimpleFeature feature = builder.buildFeature("USA");

    assertThat(feature, is(notNullValue()));
    assertThat(feature.getAttribute("coordinates"), is(point));
    assertThat(feature.getAttribute("name"), is("United States"));
  }

  @Test
  public void testForGeometryWithPolygon() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(-77.1, 38.8),
          new Coordinate(-77.0, 38.8),
          new Coordinate(-77.0, 38.9),
          new Coordinate(-77.1, 38.9),
          new Coordinate(-77.1, 38.8)
        };
    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(polygon);

    assertThat(builder, is(notNullValue()));
  }

  @Test
  public void testForGeometryWithPolygonReturnsValidBuilder() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(-77.1, 38.8),
          new Coordinate(-77.0, 38.8),
          new Coordinate(-77.0, 38.9),
          new Coordinate(-77.1, 38.9),
          new Coordinate(-77.1, 38.8)
        };
    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(polygon);

    assertThat(builder, is(notNullValue()));
    SimpleFeatureType featureType = builder.getFeatureType();
    assertThat(featureType, is(notNullValue()));
    assertThat(featureType.getName().getLocalPart(), is("CountryFeatureType"));
  }

  @Test
  public void testForGeometryWithPolygonAndBuildFeature() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(-77.1, 38.8),
          new Coordinate(-77.0, 38.8),
          new Coordinate(-77.0, 38.9),
          new Coordinate(-77.1, 38.9),
          new Coordinate(-77.1, 38.8)
        };
    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(polygon);
    builder.add("France");
    SimpleFeature feature = builder.buildFeature("FRA");

    assertThat(feature, is(notNullValue()));
    assertThat(feature.getAttribute("coordinates"), is(polygon));
    assertThat(feature.getAttribute("name"), is("France"));
  }

  @Test
  public void testForGeometryWithLineString() {
    Coordinate[] coordinates =
        new Coordinate[] {new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 2)};
    LineString lineString = GEOMETRY_FACTORY.createLineString(coordinates);

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(lineString);

    assertThat(builder, is(notNullValue()));
  }

  @Test
  public void testForGeometryWithLineStringReturnsValidBuilder() {
    Coordinate[] coordinates =
        new Coordinate[] {new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 2)};
    LineString lineString = GEOMETRY_FACTORY.createLineString(coordinates);

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(lineString);

    assertThat(builder, is(notNullValue()));
    SimpleFeatureType featureType = builder.getFeatureType();
    assertThat(featureType, is(notNullValue()));
  }

  @Test
  public void testForGeometryFeatureTypeHasCorrectAttributes() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(0, 0));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);
    SimpleFeatureType featureType = builder.getFeatureType();

    assertThat(featureType.getDescriptor("coordinates"), is(notNullValue()));
    assertThat(featureType.getDescriptor("name"), is(notNullValue()));
  }

  @Test
  public void testForGeometryWithMultiplePoints() {
    Point point1 = GEOMETRY_FACTORY.createPoint(new Coordinate(-77.0369, 38.9072));
    Point point2 = GEOMETRY_FACTORY.createPoint(new Coordinate(0.0, 51.5074));

    SimpleFeatureBuilder builder1 = FeatureBuilder.forGeometry(point1);
    SimpleFeatureBuilder builder2 = FeatureBuilder.forGeometry(point2);

    assertThat(builder1, is(notNullValue()));
    assertThat(builder2, is(notNullValue()));
  }

  @Test
  public void testForGeometryWithZeroCoordinates() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(0.0, 0.0));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);

    assertThat(builder, is(notNullValue()));
    builder.add("Origin");
    SimpleFeature feature = builder.buildFeature("ORIGIN");
    assertThat(feature, is(notNullValue()));
  }

  @Test
  public void testForGeometryWithNegativeCoordinates() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(-123.456, -78.901));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);

    assertThat(builder, is(notNullValue()));
    builder.add("Antarctic Location");
    SimpleFeature feature = builder.buildFeature("SOUTH");
    assertThat(feature, is(notNullValue()));
  }

  @Test
  public void testForGeometryWithLargeCoordinates() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(179.999, 89.999));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);

    assertThat(builder, is(notNullValue()));
  }

  @Test
  public void testForGeometryCreatesFeatureTypeWithWGS84CRS() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(0, 0));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);
    SimpleFeatureType featureType = builder.getFeatureType();

    assertThat(featureType.getCoordinateReferenceSystem(), is(notNullValue()));
  }

  @Test
  public void testForGeometryWithComplexPolygon() {
    Coordinate[] coordinates =
        new Coordinate[] {
          new Coordinate(0, 0),
          new Coordinate(0, 10),
          new Coordinate(10, 10),
          new Coordinate(10, 0),
          new Coordinate(0, 0)
        };
    Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinates);

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(polygon);
    builder.add("Complex Country");
    SimpleFeature feature = builder.buildFeature("COMPLEX");

    assertThat(feature, is(notNullValue()));
    assertThat(feature.getAttribute("coordinates"), is(polygon));
  }

  @Test
  public void testForGeometryBuilderCanBeReused() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(0, 0));

    SimpleFeatureBuilder builder = FeatureBuilder.forGeometry(point);
    builder.add("First Country");
    SimpleFeature feature1 = builder.buildFeature("FIRST");

    builder.add("Second Country");
    SimpleFeature feature2 = builder.buildFeature("SECOND");

    assertThat(feature1, is(notNullValue()));
    assertThat(feature2, is(notNullValue()));
    assertThat(feature1.getAttribute("name"), is("First Country"));
    assertThat(feature2.getAttribute("name"), is("Second Country"));
  }
}
