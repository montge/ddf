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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import org.codice.ddf.transformer.xml.streaming.Gml3ToWkt;
import org.codice.ddf.transformer.xml.streaming.SaxEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GmlHandlerFactoryTest {

  private GmlHandlerFactory factory;

  @BeforeEach
  void setUp() {
    factory = new GmlHandlerFactory();
  }

  @Test
  void testGetVersion() {
    assertThat(factory.getVersion(), is("1.0"));
  }

  @Test
  void testGetId() {
    assertThat(factory.getId(), is("gml-handler"));
  }

  @Test
  void testGetTitle() {
    assertThat(factory.getTitle(), is("GML Sax Event Handler Factory"));
  }

  @Test
  void testGetDescription() {
    assertThat(
        factory.getDescription(),
        is("Factory that returns a SaxEventHandler to help parse GML portions of Metacards"));
  }

  @Test
  void testGetOrganization() {
    assertThat(factory.getOrganization(), is("Codice"));
  }

  @Test
  void testGetNewSaxEventHandler() {
    SaxEventHandler handler = factory.getNewSaxEventHandler();

    assertThat(handler, is(notNullValue()));
    assertThat(handler, instanceOf(GmlHandler.class));
  }

  @Test
  void testGetNewSaxEventHandlerWithGml3ToWkt() {
    Gml3ToWkt gml3ToWkt = mock(Gml3ToWkt.class);
    factory.setGml3ToWkt(gml3ToWkt);

    SaxEventHandler handler = factory.getNewSaxEventHandler();

    assertThat(handler, is(notNullValue()));
    assertThat(handler, instanceOf(GmlHandler.class));
  }

  @Test
  void testSetGml3ToWkt() {
    Gml3ToWkt gml3ToWkt = mock(Gml3ToWkt.class);

    factory.setGml3ToWkt(gml3ToWkt);

    SaxEventHandler handler = factory.getNewSaxEventHandler();
    assertThat(handler, is(notNullValue()));
  }

  @Test
  void testMultipleHandlerInstancesAreUnique() {
    SaxEventHandler handler1 = factory.getNewSaxEventHandler();
    SaxEventHandler handler2 = factory.getNewSaxEventHandler();

    assertThat(handler1, is(notNullValue()));
    assertThat(handler2, is(notNullValue()));
    assertThat(handler1 != handler2, is(true));
  }
}
