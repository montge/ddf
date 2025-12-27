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
package org.codice.ddf.transformer.xml.streaming.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.HashMap;
import java.util.Map;
import org.codice.ddf.transformer.xml.streaming.SaxEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class XmlSaxEventHandlerFactoryImplTest {

  private XmlSaxEventHandlerFactoryImpl factory;

  @BeforeEach
  void setUp() {
    factory = new XmlSaxEventHandlerFactoryImpl();
  }

  @Test
  void testGetVersion() {
    assertThat(factory.getVersion(), is("1.0"));
  }

  @Test
  void testGetId() {
    assertThat(factory.getId(), is("xml-handler"));
  }

  @Test
  void testGetTitle() {
    assertThat(factory.getTitle(), is("XMLSaxEventHandler Factory"));
  }

  @Test
  void testGetDescription() {
    assertThat(
        factory.getDescription(),
        is("Factory that returns a SaxEventHandler to help parse XML Metacards"));
  }

  @Test
  void testGetOrganization() {
    assertThat(factory.getOrganization(), is("Codice"));
  }

  @Test
  void testGetNewSaxEventHandlerWithoutMapping() {
    SaxEventHandler handler = factory.getNewSaxEventHandler();

    assertThat(handler, is(notNullValue()));
    assertThat(handler, instanceOf(XmlSaxEventHandlerImpl.class));
  }

  @Test
  void testGetNewSaxEventHandlerWithMapping() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("xmlElement", "metacardAttribute");
    factory.setXmlToMetacardMapping(mapping);

    SaxEventHandler handler = factory.getNewSaxEventHandler();

    assertThat(handler, is(notNullValue()));
    assertThat(handler, instanceOf(XmlSaxEventHandlerImpl.class));
  }

  @Test
  void testSetXmlToMetacardMapping() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("title", "ddf.title");
    mapping.put("date", "ddf.date");

    factory.setXmlToMetacardMapping(mapping);

    assertThat(factory.getXmlToMetacardMapping(), is(sameInstance(mapping)));
  }

  @Test
  void testGetXmlToMetacardMappingDefaultIsNull() {
    assertThat(factory.getXmlToMetacardMapping(), is(nullValue()));
  }

  @Test
  void testGetXmlToMetacardMappingAfterSet() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("key", "value");
    factory.setXmlToMetacardMapping(mapping);

    Map<String, String> result = factory.getXmlToMetacardMapping();

    assertThat(result, hasEntry("key", "value"));
  }

  @Test
  void testMultipleHandlerInstancesAreUnique() {
    SaxEventHandler handler1 = factory.getNewSaxEventHandler();
    SaxEventHandler handler2 = factory.getNewSaxEventHandler();

    assertThat(handler1, is(notNullValue()));
    assertThat(handler2, is(notNullValue()));
    assertThat(handler1 != handler2, is(true));
  }

  @Test
  void testSetMappingToNull() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("key", "value");
    factory.setXmlToMetacardMapping(mapping);

    factory.setXmlToMetacardMapping(null);

    assertThat(factory.getXmlToMetacardMapping(), is(nullValue()));
  }

  @Test
  void testHandlerCreationWithEmptyMapping() {
    Map<String, String> emptyMapping = new HashMap<>();
    factory.setXmlToMetacardMapping(emptyMapping);

    SaxEventHandler handler = factory.getNewSaxEventHandler();

    assertThat(handler, is(notNullValue()));
    assertThat(handler, instanceOf(XmlSaxEventHandlerImpl.class));
  }
}
