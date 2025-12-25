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
package org.codice.ddf.spatial.ogc.wfs.v110.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

class Wfs11ConstantsTest {

  @Test
  void testVersionConstant() {
    assertThat(Wfs11Constants.VERSION_1_1_0, is("1.1.0"));
  }

  @Test
  void testRequestConstants() {
    assertThat(Wfs11Constants.GET_FEATURE, is("GetFeature"));
  }

  @Test
  void testNamespaceConstants() {
    assertThat(Wfs11Constants.GML_3_1_1_NAMESPACE, is("http://www.opengis.net/gml"));
    assertThat(Wfs11Constants.WFS_NAMESPACE, is("http://www.opengis.net/wfs"));
  }

  @Test
  void testPolygonQName() {
    assertThat(Wfs11Constants.POLYGON, is(notNullValue()));
    assertThat(Wfs11Constants.POLYGON.getLocalPart(), is("Polygon"));
    assertThat(Wfs11Constants.POLYGON.getNamespaceURI(), is(Wfs11Constants.GML_3_1_1_NAMESPACE));
  }

  @Test
  void testLinestringQName() {
    assertThat(Wfs11Constants.LINESTRING, is(notNullValue()));
    assertThat(Wfs11Constants.LINESTRING.getLocalPart(), is("LineString"));
    assertThat(Wfs11Constants.LINESTRING.getNamespaceURI(), is(Wfs11Constants.GML_3_1_1_NAMESPACE));
  }

  @Test
  void testPointQName() {
    assertThat(Wfs11Constants.POINT, is(notNullValue()));
    assertThat(Wfs11Constants.POINT.getLocalPart(), is("Point"));
    assertThat(Wfs11Constants.POINT.getNamespaceURI(), is(Wfs11Constants.GML_3_1_1_NAMESPACE));
  }

  @Test
  void testMultiPointQName() {
    assertThat(Wfs11Constants.MULTI_POINT, is(notNullValue()));
    assertThat(Wfs11Constants.MULTI_POINT.getLocalPart(), is("MultiPoint"));
    assertThat(
        Wfs11Constants.MULTI_POINT.getNamespaceURI(), is(Wfs11Constants.GML_3_1_1_NAMESPACE));
  }

  @Test
  void testMultiLinestringQName() {
    assertThat(Wfs11Constants.MULTI_LINESTRING, is(notNullValue()));
    assertThat(Wfs11Constants.MULTI_LINESTRING.getLocalPart(), is("MultiLineString"));
    assertThat(
        Wfs11Constants.MULTI_LINESTRING.getNamespaceURI(), is(Wfs11Constants.GML_3_1_1_NAMESPACE));
  }

  @Test
  void testMultiPolygonQName() {
    assertThat(Wfs11Constants.MULTI_POLYGON, is(notNullValue()));
    assertThat(Wfs11Constants.MULTI_POLYGON.getLocalPart(), is("MultiPolygon"));
    assertThat(
        Wfs11Constants.MULTI_POLYGON.getNamespaceURI(), is(Wfs11Constants.GML_3_1_1_NAMESPACE));
  }

  @Test
  void testGeometryCollectionQName() {
    assertThat(Wfs11Constants.GEOMETRY_COLLECTION, is(notNullValue()));
    assertThat(Wfs11Constants.GEOMETRY_COLLECTION.getLocalPart(), is("GeometryCollection"));
    assertThat(
        Wfs11Constants.GEOMETRY_COLLECTION.getNamespaceURI(),
        is(Wfs11Constants.GML_3_1_1_NAMESPACE));
  }

  @Test
  void testWktOperandsAsListSize() {
    List<QName> wktOperands = Wfs11Constants.wktOperandsAsList();
    assertThat(wktOperands, is(notNullValue()));
    assertThat(wktOperands, hasSize(11));
  }

  @Test
  void testWktOperandsContainsGeometryTypes() {
    List<QName> wktOperands = Wfs11Constants.wktOperandsAsList();

    assertThat(
        wktOperands,
        containsInAnyOrder(
            new QName(Wfs11Constants.GML_3_1_1_NAMESPACE, "LinearRing"),
            Wfs11Constants.POLYGON,
            new QName(Wfs11Constants.GML_3_1_1_NAMESPACE, "Envelope"),
            Wfs11Constants.LINESTRING,
            new QName(Wfs11Constants.GML_3_1_1_NAMESPACE, "lineStringMember"),
            Wfs11Constants.POINT,
            new QName(Wfs11Constants.GML_3_1_1_NAMESPACE, "pointMember"),
            Wfs11Constants.MULTI_POINT,
            Wfs11Constants.MULTI_LINESTRING,
            Wfs11Constants.MULTI_POLYGON,
            Wfs11Constants.GEOMETRY_COLLECTION));
  }

  @Test
  void testSpatialOperatorsEnumValues() {
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.BBOX.getValue(), is("BBOX"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.BEYOND.getValue(), is("Beyond"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.CONTAINS.getValue(), is("Contains"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.CROSSES.getValue(), is("Crosses"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.DISJOINT.getValue(), is("Disjoint"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.DWITHIN.getValue(), is("DWithin"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.INTERSECTS.getValue(), is("Intersects"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.EQUALS.getValue(), is("Equals"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.OVERLAPS.getValue(), is("Overlaps"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.TOUCHES.getValue(), is("Touches"));
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.WITHIN.getValue(), is("Within"));
  }

  @Test
  void testSpatialOperatorsEnumCount() {
    assertThat(Wfs11Constants.SPATIAL_OPERATORS.values().length, is(11));
  }

  @Test
  void testInheritedWfsConstants() {
    assertThat(Wfs11Constants.WFS, is("WFS"));
    assertThat(Wfs11Constants.ESCAPE, is("!"));
    assertThat(Wfs11Constants.WILD_CARD, is("*"));
    assertThat(Wfs11Constants.METERS, is("METERS"));
  }
}
