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
package ddf.catalog.transformer.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.InputStream;
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

  @Mock private Parser parser;

  @Mock private ParserConfigurator parserConfigurator;

  private GeometryTransformer transformer;

  @Before
  public void setUp() {
    when(parser.configureParser(any(), any())).thenReturn(parserConfigurator);
    when(parserConfigurator.setHandler(any())).thenReturn(parserConfigurator);
    transformer = new GeometryTransformer(parser);
  }

  @Test
  public void testTransformGeometryAttribute() throws Exception {
    String wkt = "POINT (1 2)";
    Attribute attribute = new AttributeImpl("location", wkt);

    String xmlGeometry =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gml:Point xmlns:gml=\"http://www.opengis.net/gml\"></gml:Point>";
    byte[] xmlBytes = xmlGeometry.getBytes();

    org.mockito.Mockito.doAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              os.write(xmlBytes);
              return null;
            })
        .when(parser)
        .marshal(eq(parserConfigurator), any(), any(OutputStream.class));

    BinaryContent result = transformer.transform(attribute);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeTypeValue(), is("text/xml"));

    InputStream is = result.getInputStream();
    assertThat(is, is(notNullValue()));

    byte[] resultBytes = new byte[xmlBytes.length];
    is.read(resultBytes);
    assertThat(new String(resultBytes), containsString("gml:Point"));
  }

  @Test
  public void testTransformWithParserException() throws Exception {
    Attribute attribute = new AttributeImpl("location", "POINT (1 2)");

    org.mockito.Mockito.doThrow(new ParserException("Parser error"))
        .when(parser)
        .marshal(eq(parserConfigurator), any(), any(OutputStream.class));

    assertThrows(CatalogTransformerException.class, () -> transformer.transform(attribute));
  }

  @Test
  public void testTransformWithNullAttribute() {
    // Transform should throw NullPointerException when given null attribute
    assertThrows(NullPointerException.class, () -> transformer.transform(null));
  }

  @Test
  public void testTransformReturnsCorrectMimeType() throws Exception {
    Attribute attribute = new AttributeImpl("location", "POINT (1 2)");

    org.mockito.Mockito.doAnswer(
            invocation -> {
              OutputStream os = invocation.getArgument(2);
              os.write("<test/>".getBytes());
              return null;
            })
        .when(parser)
        .marshal(eq(parserConfigurator), any(), any(OutputStream.class));

    BinaryContent result = transformer.transform(attribute);

    assertThat(result.getMimeType().toString(), is("text/xml"));
  }
}
