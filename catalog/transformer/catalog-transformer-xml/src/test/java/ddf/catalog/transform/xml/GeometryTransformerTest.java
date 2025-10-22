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
package ddf.catalog.transform.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.IOException;
import java.io.OutputStream;
import org.codice.ddf.parser.Parser;
import org.codice.ddf.parser.ParserConfigurator;
import org.codice.ddf.parser.ParserException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeometryTransformerTest {

  @Mock private Parser mockParser;

  @Mock private ParserConfigurator mockConfigurator;

  private GeometryTransformer transformer;

  @Before
  public void setUp() {
    when(mockParser.configureParser(anyList(), any(ClassLoader.class)))
        .thenReturn(mockConfigurator);
    when(mockConfigurator.setHandler(any())).thenReturn(mockConfigurator);
    transformer = new GeometryTransformer(mockParser);
  }

  @Test
  public void testTransformPoint()
      throws CatalogTransformerException, ParserException, IOException {
    Attribute geoAttribute = new AttributeImpl("location", "POINT (1 2)");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              String gmlXml =
                  "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\"><gml:pos>2.0 1.0</gml:pos></gml:Point>";
              os.write(gmlXml.getBytes());
              return null;
            });

    BinaryContent result = transformer.transform(geoAttribute);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeTypeValue(), is("text/xml"));
    assertThat(result.getByteArray().length > 0, is(true));

    String content = new String(result.getByteArray());
    assertThat(content, containsString("gml:Point"));
  }

  @Test
  public void testTransformLineString() throws CatalogTransformerException, ParserException {
    Attribute geoAttribute = new AttributeImpl("location", "LINESTRING (30 10, 10 30, 40 40)");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              String gmlXml =
                  "<gml:LineString xmlns:gml=\"http://www.opengis.net/gml\"><gml:posList>10.0 30.0 30.0 10.0 40.0 40.0</gml:posList></gml:LineString>";
              os.write(gmlXml.getBytes());
              return null;
            });

    BinaryContent result = transformer.transform(geoAttribute);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeTypeValue(), is("text/xml"));
  }

  @Test
  public void testTransformPolygon() throws CatalogTransformerException, ParserException {
    Attribute geoAttribute =
        new AttributeImpl("location", "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              String gmlXml =
                  "<gml:Polygon xmlns:gml=\"http://www.opengis.net/gml\"><gml:exterior><gml:LinearRing><gml:posList>10.0 30.0 20.0 10.0 40.0 20.0 40.0 40.0 10.0 30.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon>";
              os.write(gmlXml.getBytes());
              return null;
            });

    BinaryContent result = transformer.transform(geoAttribute);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeTypeValue(), is("text/xml"));
  }

  @Test
  public void testTransformMultiPoint() throws CatalogTransformerException, ParserException {
    Attribute geoAttribute =
        new AttributeImpl("location", "MULTIPOINT ((10 40), (40 30), (20 20))");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              String gmlXml =
                  "<gml:MultiPoint xmlns:gml=\"http://www.opengis.net/gml\"><gml:pointMember><gml:Point><gml:pos>40.0 10.0</gml:pos></gml:Point></gml:pointMember></gml:MultiPoint>";
              os.write(gmlXml.getBytes());
              return null;
            });

    BinaryContent result = transformer.transform(geoAttribute);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeTypeValue(), is("text/xml"));
  }

  @Test(expected = CatalogTransformerException.class)
  public void testTransformWithParserException()
      throws CatalogTransformerException, ParserException {
    Attribute geoAttribute = new AttributeImpl("location", "POINT (1 2)");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenThrow(new ParserException("Parser failed"));

    transformer.transform(geoAttribute);
  }

  @Test(expected = CatalogTransformerException.class)
  public void testTransformWithIOException() throws CatalogTransformerException, ParserException {
    Attribute geoAttribute = new AttributeImpl("location", "POINT (1 2)");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              // Simulate IOException by throwing it
              throw new IOException("Failed to write");
            });

    transformer.transform(geoAttribute);
  }

  @Test
  public void testTransformWithEmptyGeometry() throws CatalogTransformerException, ParserException {
    Attribute geoAttribute = new AttributeImpl("location", "");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              String gmlXml = "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\"/>";
              os.write(gmlXml.getBytes());
              return null;
            });

    BinaryContent result = transformer.transform(geoAttribute);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testTransformWithNullAttribute() throws CatalogTransformerException, ParserException {
    Attribute geoAttribute = new AttributeImpl("location", null);

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              String gmlXml = "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\"/>";
              os.write(gmlXml.getBytes());
              return null;
            });

    BinaryContent result = transformer.transform(geoAttribute);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testTransformMultiPolygon() throws CatalogTransformerException, ParserException {
    Attribute geoAttribute =
        new AttributeImpl(
            "location",
            "MULTIPOLYGON (((30 10, 10 20, 20 40, 40 40, 30 10)),((15 5, 40 10, 10 20, 5 10, 15 5)))");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              String gmlXml =
                  "<gml:MultiPolygon xmlns:gml=\"http://www.opengis.net/gml\"><gml:polygonMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>10.0 30.0 20.0 10.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:polygonMember></gml:MultiPolygon>";
              os.write(gmlXml.getBytes());
              return null;
            });

    BinaryContent result = transformer.transform(geoAttribute);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeTypeValue(), is("text/xml"));
  }

  @Test
  public void testTransformGeometryCollection()
      throws CatalogTransformerException, ParserException {
    Attribute geoAttribute =
        new AttributeImpl("location", "GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))");

    when(mockParser.marshal(any(), any(), any(OutputStream.class)))
        .thenAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              String gmlXml =
                  "<gml:GeometryCollection xmlns:gml=\"http://www.opengis.net/gml\"><gml:geometryMember><gml:Point><gml:pos>6.0 4.0</gml:pos></gml:Point></gml:geometryMember></gml:GeometryCollection>";
              os.write(gmlXml.getBytes());
              return null;
            });

    BinaryContent result = transformer.transform(geoAttribute);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeTypeValue(), is("text/xml"));
  }
}
