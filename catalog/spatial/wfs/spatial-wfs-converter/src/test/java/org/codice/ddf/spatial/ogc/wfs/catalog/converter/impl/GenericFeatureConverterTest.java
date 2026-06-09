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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import org.codice.ddf.libs.geo.util.GeospatialUtil;
import org.codice.ddf.spatial.ogc.wfs.catalog.mapper.MetacardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericFeatureConverterTest {

  private GenericFeatureConverter converter;

  private MetacardMapper metacardMapper;

  private MetacardType metacardType;

  @BeforeEach
  public void setUp() {
    converter = new GenericFeatureConverter();
    metacardMapper = mock(MetacardMapper.class);
    metacardType =
        new MetacardTypeImpl("test-type", MetacardImpl.BASIC_METACARD.getAttributeDescriptors());
  }

  @Test
  public void testDefaultConstructor() {
    GenericFeatureConverter newConverter = new GenericFeatureConverter();
    assertThat(newConverter, notNullValue());
  }

  @Test
  public void testConstructorWithSrs() {
    GenericFeatureConverter newConverter = new GenericFeatureConverter("EPSG:4326");
    assertThat(newConverter, notNullValue());
    assertThat(newConverter.getSrs(), is("EPSG:4326"));
  }

  @Test
  public void testConstructorWithMetacardMapper() {
    GenericFeatureConverter newConverter = new GenericFeatureConverter(metacardMapper);
    assertThat(newConverter, notNullValue());
  }

  @Test
  public void testCanConvertMetacard() {
    assertThat(converter.canConvert(Metacard.class), is(true));
  }

  @Test
  public void testCanConvertMetacardImpl() {
    assertThat(converter.canConvert(MetacardImpl.class), is(true));
  }

  @Test
  public void testCannotConvertString() {
    assertThat(converter.canConvert(String.class), is(false));
  }

  @Test
  public void testSetSourceId() {
    // No public getter exists for sourceId; the setter only stores the value for later
    // use during conversion, so the meaningful contract is that it completes without error.
    assertDoesNotThrow(() -> converter.setSourceId("test-source"));
  }

  @Test
  public void testSetAndGetMetacardType() {
    converter.setMetacardType(metacardType);
    assertThat(converter.getMetacardType(), is(metacardType));
  }

  @Test
  public void testGetMetacardTypeBeforeSet() {
    assertThat(converter.getMetacardType(), nullValue());
  }

  @Test
  public void testSetWfsUrl() {
    // No public getter exists for wfsUrl; the setter only stores the value, so the
    // meaningful contract is that it completes without error.
    assertDoesNotThrow(() -> converter.setWfsUrl("http://example.com/wfs"));
  }

  @Test
  public void testSetCoordinateOrder() {
    // No public getter exists for coordinateOrder; the setter only stores the value, so
    // the meaningful contract is that it completes without error.
    assertDoesNotThrow(() -> converter.setCoordinateOrder("LAT_LON"));
  }

  @Test
  public void testSetAndGetSrs() {
    converter.setSrs("EPSG:3857");
    assertThat(converter.getSrs(), is("EPSG:3857"));
  }

  @Test
  public void testGetSrsDefaultValue() {
    assertThat(converter.getSrs(), is(GeospatialUtil.EPSG_4326));
  }

  @Test
  public void testSetSrsToNull() {
    converter.setSrs(null);
    assertThat(converter.getSrs(), nullValue());
  }

  @Test
  public void testSetSrsToEmptyString() {
    converter.setSrs("");
    assertThat(converter.getSrs(), is(""));
  }

  @Test
  public void testMultipleSettersInSequence() {
    converter.setSourceId("source1");
    converter.setMetacardType(metacardType);
    converter.setWfsUrl("http://test.com/wfs");
    converter.setCoordinateOrder("LON_LAT");
    converter.setSrs("EPSG:4326");

    assertThat(converter.getMetacardType(), is(metacardType));
    assertThat(converter.getSrs(), is("EPSG:4326"));
  }
}
