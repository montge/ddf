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
package org.codice.ddf.spatial.ogc.wfs.catalog.source.reader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link XmlSchemaMessageBodyReader} class. */
public class XmlSchemaMessageBodyReaderTest {

  private static final String VALID_SCHEMA =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
          + "targetNamespace=\"http://example.com/test\">\n"
          + "  <xsd:element name=\"test\" type=\"xsd:string\"/>\n"
          + "</xsd:schema>";

  private static final String INVALID_SCHEMA = "<root><notASchema/></root>";

  private XmlSchemaMessageBodyReader reader;

  @BeforeEach
  public void setUp() {
    reader = new XmlSchemaMessageBodyReader();
  }

  @Test
  public void testImplementsMessageBodyReader() {
    assertThat(reader instanceof MessageBodyReader, is(true));
  }

  @Test
  public void testIsReadableForXmlSchemaClass() {
    assertThat(
        reader.isReadable(XmlSchema.class, null, null, MediaType.APPLICATION_XML_TYPE), is(true));
  }

  @Test
  public void testIsReadableReturnsFalseForOtherClass() {
    assertThat(
        reader.isReadable(String.class, null, null, MediaType.APPLICATION_XML_TYPE), is(false));
  }

  @Test
  public void testIsReadableReturnsFalseForObjectClass() {
    assertThat(
        reader.isReadable(Object.class, null, null, MediaType.APPLICATION_XML_TYPE), is(false));
  }

  @Test
  public void testReadFromWithValidSchema() throws Exception {
    InputStream inputStream =
        new ByteArrayInputStream(VALID_SCHEMA.getBytes(StandardCharsets.UTF_8));

    XmlSchema result = reader.readFrom(XmlSchema.class, null, null, null, null, inputStream);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testReadFromWithInvalidSchemaReturnsNull() throws Exception {
    InputStream inputStream =
        new ByteArrayInputStream(INVALID_SCHEMA.getBytes(StandardCharsets.UTF_8));

    XmlSchema result = reader.readFrom(XmlSchema.class, null, null, null, null, inputStream);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testReadFromWithEmptyContentThrowsException() {
    InputStream inputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

    assertThrows(
        XmlSchemaException.class,
        () -> reader.readFrom(XmlSchema.class, null, null, null, null, inputStream));
  }
}
