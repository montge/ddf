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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Before;
import org.junit.Test;

public class EnhancedStaxDriverTest {

  private QNameMap qnameMap;

  private NoNameCoder nameCoder;

  @Before
  public void setUp() {
    qnameMap = new QNameMap();
    nameCoder = new NoNameCoder();
  }

  @Test
  public void testDefaultConstructor() {
    EnhancedStaxDriver driver = new EnhancedStaxDriver();
    assertThat(driver, notNullValue());
  }

  @Test
  public void testConstructorWithQNameMap() {
    EnhancedStaxDriver driver = new EnhancedStaxDriver(qnameMap);
    assertThat(driver, notNullValue());
  }

  @Test
  public void testConstructorWithNameCoder() {
    EnhancedStaxDriver driver = new EnhancedStaxDriver(nameCoder);
    assertThat(driver, notNullValue());
  }

  @Test
  public void testConstructorWithQNameMapAndNameCoder() {
    EnhancedStaxDriver driver = new EnhancedStaxDriver(qnameMap, nameCoder);
    assertThat(driver, notNullValue());
  }

  @Test
  public void testCreateStaxWriter() throws XMLStreamException {
    EnhancedStaxDriver driver = new EnhancedStaxDriver();
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = driver.createStaxWriter(xmlStreamWriter, true);

    assertThat(writer, notNullValue());
    assertThat(writer, instanceOf(EnhancedStaxWriter.class));
  }

  @Test
  public void testCreateStaxWriterWithoutDocument() throws XMLStreamException {
    EnhancedStaxDriver driver = new EnhancedStaxDriver();
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = driver.createStaxWriter(xmlStreamWriter, false);

    assertThat(writer, notNullValue());
    assertThat(writer, instanceOf(EnhancedStaxWriter.class));
  }

  @Test
  public void testCreateStaxWriterWithCustomQNameMap() throws XMLStreamException {
    QNameMap customQNameMap = new QNameMap();
    EnhancedStaxDriver driver = new EnhancedStaxDriver(customQNameMap);
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter =
        XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

    EnhancedStaxWriter writer = driver.createStaxWriter(xmlStreamWriter, true);

    assertThat(writer, notNullValue());
    assertThat(writer, instanceOf(EnhancedStaxWriter.class));
  }
}
