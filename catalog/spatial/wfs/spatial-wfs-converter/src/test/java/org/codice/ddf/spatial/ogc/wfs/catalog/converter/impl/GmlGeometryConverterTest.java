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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GmlGeometryConverterTest {

  private GmlGeometryConverter converter;

  private GeometryFactory geometryFactory;

  @BeforeEach
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

  @Test
  public void testCannotConvertNull() {
    assertThrows(NullPointerException.class, () -> converter.canConvert(null));
  }

  @Test
  public void testMarshalPoint() throws Exception {
    Point point = geometryFactory.createPoint(new Coordinate(1.0, 2.0));
    HierarchicalStreamWriter writer = mock(HierarchicalStreamWriter.class);
    MarshallingContext context = mock(MarshallingContext.class);

    converter.marshal(point, writer, context);

    // Marshalling a geometry serializes it as GML, which copies at least the root
    // node into the writer. If marshalling regressed to producing no output, this fails.
    verify(writer, atLeastOnce()).startNode(anyString());
  }

  @Test
  public void testUnmarshalHandlesInvalidGML() {
    // A bare mocked reader supplies no node name and a null attribute-name iterator,
    // so reconstructing the XML node during unmarshalling fails with an NPE. This pins
    // the current contract for malformed/empty reader input. Complex GML parsing
    // requires full XML setup which is better tested in integration tests.
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    UnmarshallingContext context = mock(UnmarshallingContext.class);

    assertThrows(NullPointerException.class, () -> converter.unmarshal(reader, context));
  }
}
