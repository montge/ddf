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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Before;
import org.junit.Test;

public class EnhancedStaxWriterTest {

  private QNameMap qnameMap;

  private NoNameCoder nameCoder;

  @Before
  public void setUp() {
    qnameMap = new QNameMap();
    nameCoder = new NoNameCoder();
  }

  @Test
  public void testConstructorWithAllParameters() throws XMLStreamException {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer =
        new EnhancedStaxWriter(qnameMap, xmlStreamWriter, true, true, nameCoder);

    assertThat(writer, notNullValue());
  }

  @Test
  public void testConstructorWithoutNameCoder() throws XMLStreamException {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = new EnhancedStaxWriter(qnameMap, xmlStreamWriter, true, true);

    assertThat(writer, notNullValue());
  }

  @Test
  public void testConstructorWithNameCoder() throws XMLStreamException {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = new EnhancedStaxWriter(qnameMap, xmlStreamWriter, nameCoder);

    assertThat(writer, notNullValue());
  }

  @Test
  public void testConstructorMinimal() throws XMLStreamException {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = new EnhancedStaxWriter(qnameMap, xmlStreamWriter);

    assertThat(writer, notNullValue());
  }

  @Test
  public void testWriteCdata() throws XMLStreamException {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = new EnhancedStaxWriter(qnameMap, xmlStreamWriter);
    writer.startNode("root");
    writer.writeCdata("Test CDATA content");
    writer.endNode();
    writer.flush();

    String output = stringWriter.toString();
    assertThat(output, containsString("<![CDATA[Test CDATA content]]>"));
  }

  @Test
  public void testWriteCdataWithSpecialCharacters() throws XMLStreamException {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = new EnhancedStaxWriter(qnameMap, xmlStreamWriter);
    writer.startNode("root");
    writer.writeCdata("<xml>&special</xml>");
    writer.endNode();
    writer.flush();

    String output = stringWriter.toString();
    assertThat(output, containsString("<![CDATA[<xml>&special</xml>]]>"));
  }

  @Test
  public void testWriteCdataEmpty() throws XMLStreamException {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = new EnhancedStaxWriter(qnameMap, xmlStreamWriter);
    writer.startNode("root");
    writer.writeCdata("");
    writer.endNode();
    writer.flush();

    String output = stringWriter.toString();
    assertThat(output, containsString("<![CDATA[]]>"));
  }
}
