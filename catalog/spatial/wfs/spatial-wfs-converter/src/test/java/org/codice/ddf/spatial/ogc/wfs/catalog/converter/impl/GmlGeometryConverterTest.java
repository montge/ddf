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
package org.codice.ddf.spatial.ogc.wfs.catalog.converter.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppReader;
import com.thoughtworks.xstream.io.xml.xppdom.XppFactory;
import java.io.StringReader;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.xmlpull.v1.XmlPullParser;

public class GmlGeometryConverterTest {

  private GmlGeometryConverter converter;

  private GeometryFactory geometryFactory;

  @Before
  public void setUp() {
    converter = new GmlGeometryConverter();
    geometryFactory = new GeometryFactory();
  }

  @Test
  public void testCanConvertGeometry() {
    assertThat(converter.canConvert(Geometry.class), is(true));
  }

  @Test
  public void testCanConvertPoint() {
    assertThat(converter.canConvert(Point.class), is(true));
  }

  @Test
  public void testCannotConvertString() {
    assertThat(converter.canConvert(String.class), is(false));
  }

  @Test(expected = NullPointerException.class)
  public void testCannotConvertNull() {
    converter.canConvert(null);
  }

  @Test
  public void testMarshalPoint() throws Exception {
    Point point = geometryFactory.createPoint(new Coordinate(1.0, 2.0));
    HierarchicalStreamWriter writer = mock(HierarchicalStreamWriter.class);
    MarshallingContext context = mock(MarshallingContext.class);

    // Verify no exception is thrown during marshalling
    converter.marshal(point, writer, context);
  }

  @Test
  public void testUnmarshalPointGml() throws Exception {
    String gml =
        "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:coordinates>1.0,2.0</gml:coordinates>"
            + "</gml:Point>";

    XmlPullParser parser = XppFactory.createDefaultParser();
    HierarchicalStreamReader reader = new XppReader(new StringReader(gml), parser);
    UnmarshallingContext context = mock(UnmarshallingContext.class);

    reader.moveDown();
    Object result = converter.unmarshal(reader, context);

    assertThat(result, notNullValue());
    assertThat(result instanceof String, is(true));
    assertThat((String) result, is("POINT (1 2)"));
  }

  @Test
  public void testUnmarshalInvalidGml() throws Exception {
    String invalidGml = "<invalid>not a geometry</invalid>";

    XmlPullParser parser = XppFactory.createDefaultParser();
    HierarchicalStreamReader reader = new XppReader(new StringReader(invalidGml), parser);
    UnmarshallingContext context = mock(UnmarshallingContext.class);

    reader.moveDown();
    Object result = converter.unmarshal(reader, context);

    assertThat(result, nullValue());
  }

  @Test
  public void testUnmarshalLineStringGml() throws Exception {
    String gml =
        "<gml:LineString xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:coordinates>0.0,0.0 1.0,1.0 2.0,2.0</gml:coordinates>"
            + "</gml:LineString>";

    XmlPullParser parser = XppFactory.createDefaultParser();
    HierarchicalStreamReader reader = new XppReader(new StringReader(gml), parser);
    UnmarshallingContext context = mock(UnmarshallingContext.class);

    reader.moveDown();
    Object result = converter.unmarshal(reader, context);

    assertThat(result, notNullValue());
    assertThat(result instanceof String, is(true));
    assertThat((String) result, is("LINESTRING (0 0, 1 1, 2 2)"));
  }

  @Test
  public void testUnmarshalPolygonGml() throws Exception {
    String gml =
        "<gml:Polygon xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:outerBoundaryIs>"
            + "<gml:LinearRing>"
            + "<gml:coordinates>0.0,0.0 10.0,0.0 10.0,10.0 0.0,10.0 0.0,0.0</gml:coordinates>"
            + "</gml:LinearRing>"
            + "</gml:outerBoundaryIs>"
            + "</gml:Polygon>";

    XmlPullParser parser = XppFactory.createDefaultParser();
    HierarchicalStreamReader reader = new XppReader(new StringReader(gml), parser);
    UnmarshallingContext context = mock(UnmarshallingContext.class);

    reader.moveDown();
    Object result = converter.unmarshal(reader, context);

    assertThat(result, notNullValue());
    assertThat(result instanceof String, is(true));
    // LinearRing is valid - GML reader may return it as-is
    assertThat((String) result, is("LINEARRING (0 0, 10 0, 10 10, 0 10, 0 0)"));
  }
}
