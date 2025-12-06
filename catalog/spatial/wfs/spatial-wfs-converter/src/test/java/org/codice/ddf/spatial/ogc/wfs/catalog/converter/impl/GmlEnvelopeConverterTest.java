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
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;

public class GmlEnvelopeConverterTest {

  private GmlEnvelopeConverter converter;

  @Before
  public void setUp() {
    converter = new GmlEnvelopeConverter();
  }

  @Test
  public void testCanConvertEnvelope() {
    assertThat(converter.canConvert(Envelope.class), is(true));
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
  public void testMarshalEnvelope() {
    Envelope envelope = new Envelope(1.0, 10.0, 2.0, 20.0);
    HierarchicalStreamWriter writer = mock(HierarchicalStreamWriter.class);
    MarshallingContext context = mock(MarshallingContext.class);

    converter.marshal(envelope, writer, context);

    verify(writer).startNode("gml:Box");
    verify(writer).addAttribute("srsName", "EPSG:4326");
    verify(writer).startNode("gml:coordinates");
    verify(writer).setValue("1.0,2.0 10.0,20.0");
  }

  @Test
  public void testMarshalEnvelopeWithNegativeCoordinates() {
    Envelope envelope = new Envelope(-10.0, -1.0, -20.0, -2.0);
    HierarchicalStreamWriter writer = mock(HierarchicalStreamWriter.class);
    MarshallingContext context = mock(MarshallingContext.class);

    converter.marshal(envelope, writer, context);

    verify(writer).startNode("gml:Box");
    verify(writer).addAttribute("srsName", "EPSG:4326");
    verify(writer).startNode("gml:coordinates");
    verify(writer).setValue("-10.0,-20.0 -1.0,-2.0");
  }

  @Test
  public void testMarshalEnvelopeWithZeroCoordinates() {
    Envelope envelope = new Envelope(0.0, 0.0, 0.0, 0.0);
    HierarchicalStreamWriter writer = mock(HierarchicalStreamWriter.class);
    MarshallingContext context = mock(MarshallingContext.class);

    converter.marshal(envelope, writer, context);

    verify(writer).startNode("gml:Box");
    verify(writer).addAttribute("srsName", "EPSG:4326");
    verify(writer).startNode("gml:coordinates");
    verify(writer).setValue("0.0,0.0 0.0,0.0");
  }

  @Test
  public void testUnmarshalReturnsNull() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    UnmarshallingContext context = mock(UnmarshallingContext.class);

    Object result = converter.unmarshal(reader, context);

    assertThat(result, nullValue());
  }
}
