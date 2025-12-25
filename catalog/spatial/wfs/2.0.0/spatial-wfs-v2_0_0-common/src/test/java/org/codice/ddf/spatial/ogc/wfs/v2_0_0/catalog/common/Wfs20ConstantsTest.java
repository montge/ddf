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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class Wfs20ConstantsTest {

  @Test
  void testConstantValues() {
    assertThat(Wfs20Constants.GML_MIME_TYPE, is("application/gml+xml"));
    assertThat(Wfs20Constants.GET_CAPABILITIES, is("GetCapabilities"));
    assertThat(Wfs20Constants.GET_PROPERTY_VALUE, is("GetPropertyValue"));
    assertThat(Wfs20Constants.DESCRIBE_FEATURE_TYPE, is("DescribeFeatureType"));
    assertThat(Wfs20Constants.GET_FEATURE, is("GetFeature"));
    assertThat(Wfs20Constants.VERSION_2_0_0, is("2.0.0"));
    assertThat(Wfs20Constants.WFS_2_0_NAMESPACE, is("http://www.opengis.net/wfs/2.0"));
    assertThat(Wfs20Constants.GML_3_2_NAMESPACE, is("http://www.opengis.net/gml/3.2"));
  }

  @Test
  void testQNameConstants() {
    assertThat(Wfs20Constants.POLYGON.getLocalPart(), is("Polygon"));
    assertThat(Wfs20Constants.POLYGON.getNamespaceURI(), is(Wfs20Constants.GML_3_2_NAMESPACE));
    assertThat(Wfs20Constants.ENVELOPE.getLocalPart(), is("Envelope"));
    assertThat(Wfs20Constants.BOX.getLocalPart(), is("Box"));
    assertThat(Wfs20Constants.LINESTRING.getLocalPart(), is("LineString"));
    assertThat(Wfs20Constants.POINT.getLocalPart(), is("Point"));
    assertThat(Wfs20Constants.GEOMETRY_COLLECTION.getLocalPart(), is("GeometryCollection"));
    assertThat(Wfs20Constants.MULTI_POINT.getLocalPart(), is("MultiPoint"));
    assertThat(Wfs20Constants.MULTI_LINE_STRING.getLocalPart(), is("MultiLineString"));
    assertThat(Wfs20Constants.MULTI_POLYGON.getLocalPart(), is("MultiPolygon"));
  }

  @ParameterizedTest
  @EnumSource(Wfs20Constants.SPATIAL_OPERATORS.class)
  void testSpatialOperatorsGetValue(Wfs20Constants.SPATIAL_OPERATORS op) {
    assertThat(op.getValue(), is(notNullValue()));
    assertThat(op.toString(), is(op.getValue()));
  }

  @Test
  void testSpatialOperatorsGetEnum() {
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("BBOX"),
        is(Wfs20Constants.SPATIAL_OPERATORS.BBOX));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Beyond"),
        is(Wfs20Constants.SPATIAL_OPERATORS.BEYOND));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Contains"),
        is(Wfs20Constants.SPATIAL_OPERATORS.CONTAINS));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Crosses"),
        is(Wfs20Constants.SPATIAL_OPERATORS.CROSSES));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Disjoint"),
        is(Wfs20Constants.SPATIAL_OPERATORS.DISJOINT));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("DWithin"),
        is(Wfs20Constants.SPATIAL_OPERATORS.D_WITHIN));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Intersects"),
        is(Wfs20Constants.SPATIAL_OPERATORS.INTERSECTS));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Equals"),
        is(Wfs20Constants.SPATIAL_OPERATORS.EQUALS));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Overlaps"),
        is(Wfs20Constants.SPATIAL_OPERATORS.OVERLAPS));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Touches"),
        is(Wfs20Constants.SPATIAL_OPERATORS.TOUCHES));
    assertThat(
        Wfs20Constants.SPATIAL_OPERATORS.getEnum("Within"),
        is(Wfs20Constants.SPATIAL_OPERATORS.WITHIN));
  }

  @Test
  void testSpatialOperatorsGetEnumThrowsForInvalidValue() {
    assertThrows(
        IllegalArgumentException.class, () -> Wfs20Constants.SPATIAL_OPERATORS.getEnum("Invalid"));
  }

  @ParameterizedTest
  @EnumSource(Wfs20Constants.CONFORMANCE_CONSTRAINTS.class)
  void testConformanceConstraintsGetValue(Wfs20Constants.CONFORMANCE_CONSTRAINTS constraint) {
    assertThat(constraint.getValue(), is(notNullValue()));
    assertThat(constraint.toString(), is(constraint.getValue()));
  }

  @Test
  void testConformanceConstraintsGetEnum() {
    assertThat(
        Wfs20Constants.CONFORMANCE_CONSTRAINTS.getEnum("ImplementsQuery"),
        is(Wfs20Constants.CONFORMANCE_CONSTRAINTS.IMPLEMENTS_QUERY));
    assertThat(
        Wfs20Constants.CONFORMANCE_CONSTRAINTS.getEnum("ImplementsAdHocQuery"),
        is(Wfs20Constants.CONFORMANCE_CONSTRAINTS.IMPLEMENTS_AD_HOC_QUERY));
    assertThat(
        Wfs20Constants.CONFORMANCE_CONSTRAINTS.getEnum("ImplementsSorting"),
        is(Wfs20Constants.CONFORMANCE_CONSTRAINTS.IMPLEMENTS_SORTING));
  }

  @Test
  void testConformanceConstraintsGetEnumThrowsForInvalidValue() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Wfs20Constants.CONFORMANCE_CONSTRAINTS.getEnum("Invalid"));
  }

  @ParameterizedTest
  @EnumSource(Wfs20Constants.COMPARISON_OPERATORS.class)
  void testComparisonOperatorsGetValue(Wfs20Constants.COMPARISON_OPERATORS op) {
    assertThat(op.getValue(), is(notNullValue()));
    assertThat(op.toString(), is(op.getValue()));
  }

  @Test
  void testComparisonOperatorsGetEnum() {
    assertThat(
        Wfs20Constants.COMPARISON_OPERATORS.getEnum("PropertyIsEqualTo"),
        is(Wfs20Constants.COMPARISON_OPERATORS.PROPERTY_IS_EQUAL_TO));
    assertThat(
        Wfs20Constants.COMPARISON_OPERATORS.getEnum("PropertyIsNotEqualTo"),
        is(Wfs20Constants.COMPARISON_OPERATORS.PROPERTY_IS_NOT_EQUAL_TO));
    assertThat(
        Wfs20Constants.COMPARISON_OPERATORS.getEnum("PropertyIsLessThan"),
        is(Wfs20Constants.COMPARISON_OPERATORS.PROPERTY_IS_LESS_THAN));
    assertThat(
        Wfs20Constants.COMPARISON_OPERATORS.getEnum("PropertyIsLike"),
        is(Wfs20Constants.COMPARISON_OPERATORS.PROPERTY_IS_LIKE));
    assertThat(
        Wfs20Constants.COMPARISON_OPERATORS.getEnum("PropertyIsBetween"),
        is(Wfs20Constants.COMPARISON_OPERATORS.PROPERTY_IS_BETWEEN));
  }

  @Test
  void testComparisonOperatorsGetEnumThrowsForInvalidValue() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Wfs20Constants.COMPARISON_OPERATORS.getEnum("Invalid"));
  }

  @ParameterizedTest
  @EnumSource(Wfs20Constants.TEMPORAL_OPERATORS.class)
  void testTemporalOperatorsGetValue(Wfs20Constants.TEMPORAL_OPERATORS op) {
    assertThat(op.getValue(), is(notNullValue()));
    assertThat(op.toString(), is(op.getValue()));
  }

  @Test
  void testTemporalOperatorsGetEnum() {
    assertThat(
        Wfs20Constants.TEMPORAL_OPERATORS.getEnum("After"),
        is(Wfs20Constants.TEMPORAL_OPERATORS.AFTER));
    assertThat(
        Wfs20Constants.TEMPORAL_OPERATORS.getEnum("Before"),
        is(Wfs20Constants.TEMPORAL_OPERATORS.BEFORE));
    assertThat(
        Wfs20Constants.TEMPORAL_OPERATORS.getEnum("During"),
        is(Wfs20Constants.TEMPORAL_OPERATORS.DURING));
    assertThat(
        Wfs20Constants.TEMPORAL_OPERATORS.getEnum("TEquals"),
        is(Wfs20Constants.TEMPORAL_OPERATORS.T_EQUALS));
  }

  @Test
  void testTemporalOperatorsGetEnumThrowsForInvalidValue() {
    assertThrows(
        IllegalArgumentException.class, () -> Wfs20Constants.TEMPORAL_OPERATORS.getEnum("Invalid"));
  }
}
