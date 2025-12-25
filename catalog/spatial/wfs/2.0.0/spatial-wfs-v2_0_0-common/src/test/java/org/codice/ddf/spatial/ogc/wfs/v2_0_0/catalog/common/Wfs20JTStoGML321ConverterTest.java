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
package org.codice.ddf.spatial.ogc.wfs.v2_0_0.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import net.opengis.gml.v_3_2_1.DirectPositionType;
import net.opengis.gml.v_3_2_1.LineStringType;
import net.opengis.gml.v_3_2_1.MultiCurveType;
import net.opengis.gml.v_3_2_1.MultiGeometryType;
import net.opengis.gml.v_3_2_1.MultiPointType;
import net.opengis.gml.v_3_2_1.MultiSurfaceType;
import net.opengis.gml.v_3_2_1.PointType;
import net.opengis.gml.v_3_2_1.PolygonType;
import net.opengis.gml.v_3_2_1.RingType;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

class Wfs20JTStoGML321ConverterTest {

  private static final String SRS_NAME = "EPSG:4326";
  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  @Test
  void testConvertToDirectPositionType2D() {
    Coordinate coord = new Coordinate(1.0, 2.0);

    DirectPositionType result =
        Wfs20JTStoGML321Converter.convertToDirectPositionType(coord, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), hasSize(2));
    assertThat(result.getValue().get(0), is(1.0));
    assertThat(result.getValue().get(1), is(2.0));
    assertThat(result.getSrsName(), is(SRS_NAME));
  }

  @Test
  void testConvertToDirectPositionType3D() {
    Coordinate coord = new Coordinate(1.0, 2.0, 3.0);

    DirectPositionType result =
        Wfs20JTStoGML321Converter.convertToDirectPositionType(coord, SRS_NAME);

    assertThat(result.getValue(), hasSize(3));
    assertThat(result.getValue().get(0), is(1.0));
    assertThat(result.getValue().get(1), is(2.0));
    assertThat(result.getValue().get(2), is(3.0));
  }

  @Test
  void testConvertToPointType() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(10.0, 20.0));

    PointType result = Wfs20JTStoGML321Converter.convertToPointType(point, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getSrsName(), is(SRS_NAME));
    assertThat(result.getPos().getValue().get(0), is(10.0));
    assertThat(result.getPos().getValue().get(1), is(20.0));
  }

  @Test
  void testConvertPointTypeToJAXB() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(10.0, 20.0));
    PointType pointType = Wfs20JTStoGML321Converter.convertToPointType(point, SRS_NAME);

    JAXBElement<PointType> result = Wfs20JTStoGML321Converter.convertPointTypeToJAXB(pointType);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is(pointType));
    assertThat(result.getName().getLocalPart(), is("Point"));
  }

  @Test
  void testConvertToLineStringType2D() {
    Coordinate[] coords =
        new Coordinate[] {
          new Coordinate(0.0, 0.0), new Coordinate(1.0, 1.0), new Coordinate(2.0, 2.0)
        };
    LineString lineString = GEOMETRY_FACTORY.createLineString(coords);

    LineStringType result = Wfs20JTStoGML321Converter.convertToLineStringType(lineString, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getSrsName(), is(SRS_NAME));
    assertThat(result.getCoordinates().getValue(), containsString("0.0,0.0"));
    assertThat(result.getCoordinates().getValue(), containsString("1.0,1.0"));
    assertThat(result.getCoordinates().getValue(), containsString("2.0,2.0"));
  }

  @Test
  void testConvertToLineStringType3D() {
    Coordinate[] coords =
        new Coordinate[] {new Coordinate(0.0, 0.0, 0.0), new Coordinate(1.0, 1.0, 1.0)};
    LineString lineString = GEOMETRY_FACTORY.createLineString(coords);

    LineStringType result = Wfs20JTStoGML321Converter.convertToLineStringType(lineString, SRS_NAME);

    assertThat(result.getCoordinates().getValue(), containsString("0.0,0.0,0.0"));
    assertThat(result.getCoordinates().getValue(), containsString("1.0,1.0,1.0"));
  }

  @Test
  void testConvertLineStringTypeToJAXB() {
    LineString lineString =
        GEOMETRY_FACTORY.createLineString(
            new Coordinate[] {new Coordinate(0.0, 0.0), new Coordinate(1.0, 1.0)});
    LineStringType lineStringType =
        Wfs20JTStoGML321Converter.convertToLineStringType(lineString, SRS_NAME);

    JAXBElement<LineStringType> result =
        Wfs20JTStoGML321Converter.convertLineStringTypeToJAXB(lineStringType);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName().getLocalPart(), is("LineString"));
  }

  @Test
  void testConvertToPolygonType() {
    Coordinate[] coords =
        new Coordinate[] {
          new Coordinate(0.0, 0.0),
          new Coordinate(10.0, 0.0),
          new Coordinate(10.0, 10.0),
          new Coordinate(0.0, 10.0),
          new Coordinate(0.0, 0.0)
        };
    LinearRing shell = GEOMETRY_FACTORY.createLinearRing(coords);
    Polygon polygon = GEOMETRY_FACTORY.createPolygon(shell);

    PolygonType result = Wfs20JTStoGML321Converter.convertToPolygonType(polygon, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getSrsName(), is(SRS_NAME));
    assertThat(result.getExterior(), is(notNullValue()));
  }

  @Test
  void testConvertToPolygonTypeWithHole() {
    Coordinate[] shellCoords =
        new Coordinate[] {
          new Coordinate(0.0, 0.0),
          new Coordinate(10.0, 0.0),
          new Coordinate(10.0, 10.0),
          new Coordinate(0.0, 10.0),
          new Coordinate(0.0, 0.0)
        };
    Coordinate[] holeCoords =
        new Coordinate[] {
          new Coordinate(2.0, 2.0),
          new Coordinate(8.0, 2.0),
          new Coordinate(8.0, 8.0),
          new Coordinate(2.0, 8.0),
          new Coordinate(2.0, 2.0)
        };
    LinearRing shell = GEOMETRY_FACTORY.createLinearRing(shellCoords);
    LinearRing hole = GEOMETRY_FACTORY.createLinearRing(holeCoords);
    Polygon polygon = GEOMETRY_FACTORY.createPolygon(shell, new LinearRing[] {hole});

    PolygonType result = Wfs20JTStoGML321Converter.convertToPolygonType(polygon, SRS_NAME);

    assertThat(result.getExterior(), is(notNullValue()));
    assertThat(result.getInterior(), hasSize(1));
  }

  @Test
  void testConvertPolygonTypeToJAXB() {
    Coordinate[] coords =
        new Coordinate[] {
          new Coordinate(0.0, 0.0),
          new Coordinate(1.0, 0.0),
          new Coordinate(1.0, 1.0),
          new Coordinate(0.0, 0.0)
        };
    LinearRing shell = GEOMETRY_FACTORY.createLinearRing(coords);
    Polygon polygon = GEOMETRY_FACTORY.createPolygon(shell);
    PolygonType polygonType = Wfs20JTStoGML321Converter.convertToPolygonType(polygon, SRS_NAME);

    JAXBElement<PolygonType> result =
        Wfs20JTStoGML321Converter.convertPolygonTypeToJAXB(polygonType);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName().getLocalPart(), is("Polygon"));
  }

  @Test
  void testConvertToMultiPointType() {
    Point[] points =
        new Point[] {
          GEOMETRY_FACTORY.createPoint(new Coordinate(1.0, 1.0)),
          GEOMETRY_FACTORY.createPoint(new Coordinate(2.0, 2.0)),
          GEOMETRY_FACTORY.createPoint(new Coordinate(3.0, 3.0))
        };
    MultiPoint multiPoint = GEOMETRY_FACTORY.createMultiPoint(points);

    MultiPointType result = Wfs20JTStoGML321Converter.convertToMultiPointType(multiPoint, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getSrsName(), is(SRS_NAME));
    assertThat(result.getPointMember(), hasSize(3));
  }

  @Test
  void testConvertMultiPointTypeToJAXB() {
    Point[] points =
        new Point[] {
          GEOMETRY_FACTORY.createPoint(new Coordinate(1.0, 1.0)),
          GEOMETRY_FACTORY.createPoint(new Coordinate(2.0, 2.0))
        };
    MultiPoint multiPoint = GEOMETRY_FACTORY.createMultiPoint(points);
    MultiPointType multiPointType =
        Wfs20JTStoGML321Converter.convertToMultiPointType(multiPoint, SRS_NAME);

    JAXBElement<MultiPointType> result =
        Wfs20JTStoGML321Converter.convertMultiPointTypeToJAXB(multiPointType);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName().getLocalPart(), is("MultiPoint"));
  }

  @Test
  void testConvertToRingType() {
    Coordinate[] coords =
        new Coordinate[] {
          new Coordinate(0.0, 0.0),
          new Coordinate(1.0, 0.0),
          new Coordinate(1.0, 1.0),
          new Coordinate(0.0, 0.0)
        };
    LinearRing linearRing = GEOMETRY_FACTORY.createLinearRing(coords);

    RingType result = Wfs20JTStoGML321Converter.convertToRingType(linearRing, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getCurveMember(), hasSize(1));
  }

  @Test
  void testConvertToMultiSurfaceType() {
    Coordinate[] coords1 =
        new Coordinate[] {
          new Coordinate(0.0, 0.0),
          new Coordinate(1.0, 0.0),
          new Coordinate(1.0, 1.0),
          new Coordinate(0.0, 0.0)
        };
    Coordinate[] coords2 =
        new Coordinate[] {
          new Coordinate(2.0, 2.0),
          new Coordinate(3.0, 2.0),
          new Coordinate(3.0, 3.0),
          new Coordinate(2.0, 2.0)
        };
    Polygon poly1 = GEOMETRY_FACTORY.createPolygon(coords1);
    Polygon poly2 = GEOMETRY_FACTORY.createPolygon(coords2);
    MultiPolygon multiPolygon = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {poly1, poly2});

    MultiSurfaceType result =
        Wfs20JTStoGML321Converter.convertToMultiSurfaceType(multiPolygon, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getSrsName(), is(SRS_NAME));
    assertThat(result.getSurfaceMember(), hasSize(2));
  }

  @Test
  void testConvertMultiSurfaceTypeToJAXB() {
    Polygon poly =
        GEOMETRY_FACTORY.createPolygon(
            new Coordinate[] {
              new Coordinate(0.0, 0.0),
              new Coordinate(1.0, 0.0),
              new Coordinate(1.0, 1.0),
              new Coordinate(0.0, 0.0)
            });
    MultiPolygon multiPolygon = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {poly});
    MultiSurfaceType multiSurfaceType =
        Wfs20JTStoGML321Converter.convertToMultiSurfaceType(multiPolygon, SRS_NAME);

    JAXBElement<MultiSurfaceType> result =
        Wfs20JTStoGML321Converter.convertMultiSurfaceTypeToJAXB(multiSurfaceType);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName().getLocalPart(), is("MultiSurface"));
  }

  @Test
  void testConvertToMultiLineStringType() {
    LineString line1 =
        GEOMETRY_FACTORY.createLineString(
            new Coordinate[] {new Coordinate(0.0, 0.0), new Coordinate(1.0, 1.0)});
    LineString line2 =
        GEOMETRY_FACTORY.createLineString(
            new Coordinate[] {new Coordinate(2.0, 2.0), new Coordinate(3.0, 3.0)});
    MultiLineString multiLineString =
        GEOMETRY_FACTORY.createMultiLineString(new LineString[] {line1, line2});

    MultiCurveType result =
        Wfs20JTStoGML321Converter.convertToMultiLineStringType(multiLineString, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getSrsName(), is(SRS_NAME));
    assertThat(result.getCurveMember(), hasSize(2));
  }

  @Test
  void testConvertMultiCurveTypeToJAXB() {
    LineString line =
        GEOMETRY_FACTORY.createLineString(
            new Coordinate[] {new Coordinate(0.0, 0.0), new Coordinate(1.0, 1.0)});
    MultiLineString multiLineString =
        GEOMETRY_FACTORY.createMultiLineString(new LineString[] {line});
    MultiCurveType multiCurveType =
        Wfs20JTStoGML321Converter.convertToMultiLineStringType(multiLineString, SRS_NAME);

    JAXBElement<MultiCurveType> result =
        Wfs20JTStoGML321Converter.convertMultiCurveTypeToJAXB(multiCurveType);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName().getLocalPart(), is("MultiCurve"));
  }

  @Test
  void testConvertToMultiGeometryType() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(1.0, 1.0));
    LineString line =
        GEOMETRY_FACTORY.createLineString(
            new Coordinate[] {new Coordinate(0.0, 0.0), new Coordinate(1.0, 1.0)});
    GeometryCollection geometryCollection =
        GEOMETRY_FACTORY.createGeometryCollection(
            new org.locationtech.jts.geom.Geometry[] {point, line});

    MultiGeometryType result =
        Wfs20JTStoGML321Converter.convertToMultiGeometryType(geometryCollection, SRS_NAME);

    assertThat(result, is(notNullValue()));
    assertThat(result.getGeometryMember(), hasSize(2));
  }

  @Test
  void testConvertMultiGeometryTypeToJAXB() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(1.0, 1.0));
    GeometryCollection geometryCollection =
        GEOMETRY_FACTORY.createGeometryCollection(new org.locationtech.jts.geom.Geometry[] {point});
    MultiGeometryType multiGeometryType =
        Wfs20JTStoGML321Converter.convertToMultiGeometryType(geometryCollection, SRS_NAME);

    JAXBElement<MultiGeometryType> result =
        Wfs20JTStoGML321Converter.convertMultiGeometryTypeToJAXB(multiGeometryType);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName().getLocalPart(), is("MultiGeometry"));
  }

  @Test
  void testConvertCoordinateToDirectPositionType2D() {
    Coordinate coord = new Coordinate(5.0, 10.0);

    DirectPositionType result =
        Wfs20JTStoGML321Converter.convertCoordinateToDirectPositionType(coord);

    assertThat(result.getValue(), hasSize(2));
    assertThat(result.getValue().get(0), is(5.0));
    assertThat(result.getValue().get(1), is(10.0));
  }

  @Test
  void testConvertCoordinateToDirectPositionType3D() {
    Coordinate coord = new Coordinate(5.0, 10.0, 15.0);

    DirectPositionType result =
        Wfs20JTStoGML321Converter.convertCoordinateToDirectPositionType(coord);

    assertThat(result.getValue(), hasSize(3));
    assertThat(result.getValue().get(2), is(15.0));
  }

  @Test
  void testConvertCoordinatesNull() {
    DirectPositionType[] result = Wfs20JTStoGML321Converter.convertCoordinates(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testConvertCoordinates() {
    Coordinate[] coords = new Coordinate[] {new Coordinate(1.0, 2.0), new Coordinate(3.0, 4.0)};

    DirectPositionType[] result = Wfs20JTStoGML321Converter.convertCoordinates(coords);

    assertThat(result.length, is(2));
    assertThat(result[0].getValue().get(0), is(1.0));
    assertThat(result[1].getValue().get(0), is(3.0));
  }

  @Test
  void testConvertGeometryToGML() throws JAXBException {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(1.0, 2.0));

    String result = Wfs20JTStoGML321Converter.convertGeometryToGML(point);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("Point"));
  }

  @Test
  void testConvertMultiGeometryWithPolygon() {
    Polygon poly =
        GEOMETRY_FACTORY.createPolygon(
            new Coordinate[] {
              new Coordinate(0.0, 0.0),
              new Coordinate(1.0, 0.0),
              new Coordinate(1.0, 1.0),
              new Coordinate(0.0, 0.0)
            });
    GeometryCollection collection =
        GEOMETRY_FACTORY.createGeometryCollection(new org.locationtech.jts.geom.Geometry[] {poly});

    MultiGeometryType result =
        Wfs20JTStoGML321Converter.convertToMultiGeometryType(collection, SRS_NAME);

    assertThat(result.getGeometryMember(), hasSize(1));
  }

  @Test
  void testConvertMultiGeometryWithMultiPoint() {
    Point[] points =
        new Point[] {
          GEOMETRY_FACTORY.createPoint(new Coordinate(1.0, 1.0)),
          GEOMETRY_FACTORY.createPoint(new Coordinate(2.0, 2.0))
        };
    MultiPoint multiPoint = GEOMETRY_FACTORY.createMultiPoint(points);
    GeometryCollection collection =
        GEOMETRY_FACTORY.createGeometryCollection(
            new org.locationtech.jts.geom.Geometry[] {multiPoint});

    MultiGeometryType result =
        Wfs20JTStoGML321Converter.convertToMultiGeometryType(collection, SRS_NAME);

    assertThat(result.getGeometryMember(), hasSize(1));
  }

  @Test
  void testConvertMultiGeometryWithMultiLineString() {
    LineString line =
        GEOMETRY_FACTORY.createLineString(
            new Coordinate[] {new Coordinate(0.0, 0.0), new Coordinate(1.0, 1.0)});
    MultiLineString multiLineString =
        GEOMETRY_FACTORY.createMultiLineString(new LineString[] {line});
    GeometryCollection collection =
        GEOMETRY_FACTORY.createGeometryCollection(
            new org.locationtech.jts.geom.Geometry[] {multiLineString});

    MultiGeometryType result =
        Wfs20JTStoGML321Converter.convertToMultiGeometryType(collection, SRS_NAME);

    assertThat(result.getGeometryMember(), hasSize(1));
  }

  @Test
  void testConvertMultiGeometryWithMultiPolygon() {
    Polygon poly =
        GEOMETRY_FACTORY.createPolygon(
            new Coordinate[] {
              new Coordinate(0.0, 0.0),
              new Coordinate(1.0, 0.0),
              new Coordinate(1.0, 1.0),
              new Coordinate(0.0, 0.0)
            });
    MultiPolygon multiPolygon = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {poly});
    GeometryCollection collection =
        GEOMETRY_FACTORY.createGeometryCollection(
            new org.locationtech.jts.geom.Geometry[] {multiPolygon});

    MultiGeometryType result =
        Wfs20JTStoGML321Converter.convertToMultiGeometryType(collection, SRS_NAME);

    assertThat(result.getGeometryMember(), hasSize(1));
  }

  @Test
  void testConvertNestedGeometryCollection() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(1.0, 1.0));
    GeometryCollection innerCollection =
        GEOMETRY_FACTORY.createGeometryCollection(new org.locationtech.jts.geom.Geometry[] {point});
    GeometryCollection outerCollection =
        GEOMETRY_FACTORY.createGeometryCollection(
            new org.locationtech.jts.geom.Geometry[] {innerCollection});

    MultiGeometryType result =
        Wfs20JTStoGML321Converter.convertToMultiGeometryType(outerCollection, SRS_NAME);

    assertThat(result.getGeometryMember(), hasSize(1));
  }
}
