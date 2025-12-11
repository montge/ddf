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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import ddf.catalog.data.AttributeType.AttributeFormat;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import java.io.Serializable;
import java.util.Date;
import org.codice.ddf.libs.geo.util.GeospatialUtil;
import org.codice.ddf.spatial.ogc.wfs.catalog.mapper.MetacardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractFeatureConverterTest {

  private TestableAbstractFeatureConverter converter;

  private MetacardType metacardType;

  private MetacardMapper metacardMapper;

  @BeforeEach
  public void setUp() {
    converter = new TestableAbstractFeatureConverter();
    metacardType =
        new MetacardTypeImpl("test-type", MetacardImpl.BASIC_METACARD.getAttributeDescriptors());
    metacardMapper = mock(MetacardMapper.class);
  }

  @Test
  public void testDefaultConstructor() {
    TestableAbstractFeatureConverter newConverter = new TestableAbstractFeatureConverter();
    assertThat(newConverter, notNullValue());
  }

  @Test
  public void testConstructorWithMetacardMapper() {
    TestableAbstractFeatureConverter newConverter =
        new TestableAbstractFeatureConverter(metacardMapper);
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
  public void testSetAndGetSourceId() {
    converter.setSourceId("test-source");
    assertThat(converter.getSourceId(), is("test-source"));
  }

  @Test
  public void testSetAndGetWfsUrl() {
    converter.setWfsUrl("http://example.com/wfs");
    assertThat(converter.getWfsUrl(), is("http://example.com/wfs"));
  }

  @Test
  public void testSetAndGetMetacardType() {
    converter.setMetacardType(metacardType);
    assertThat(converter.getMetacardType(), is(metacardType));
  }

  @Test
  public void testSetAndGetCoordinateOrder() {
    converter.setCoordinateOrder("LAT_LON");
    assertThat(converter.getCoordinateOrder(), is("LAT_LON"));
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
  public void testGetMetacardTypeBeforeSet() {
    assertThat(converter.getMetacardType(), nullValue());
  }

  @Test
  public void testGetSourceIdBeforeSet() {
    assertThat(converter.getSourceId(), nullValue());
  }

  @Test
  public void testGetWfsUrlBeforeSet() {
    assertThat(converter.getWfsUrl(), nullValue());
  }

  @Test
  public void testGetCoordinateOrderBeforeSet() {
    assertThat(converter.getCoordinateOrder(), nullValue());
  }

  @Test
  public void testGetValueForMetacardAttributeBoolean() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("true");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.BOOLEAN, reader);

    assertThat(result, is(Boolean.TRUE));
  }

  @Test
  public void testGetValueForMetacardAttributeInteger() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("42");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.INTEGER, reader);

    assertThat(result, is(42));
  }

  @Test
  public void testGetValueForMetacardAttributeLong() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("123456789");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.LONG, reader);

    assertThat(result, is(123456789L));
  }

  @Test
  public void testGetValueForMetacardAttributeDouble() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("3.14");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.DOUBLE, reader);

    assertThat(result, is(3.14));
  }

  @Test
  public void testGetValueForMetacardAttributeFloat() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("2.5");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.FLOAT, reader);

    assertThat(result, is(2.5f));
  }

  @Test
  public void testGetValueForMetacardAttributeShort() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("100");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.SHORT, reader);

    assertThat(result, is((short) 100));
  }

  @Test
  public void testGetValueForMetacardAttributeString() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("test value");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.STRING, reader);

    assertThat(result, is("test value"));
  }

  @Test
  public void testGetValueForMetacardAttributeXml() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("<xml>content</xml>");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.XML, reader);

    assertThat(result, is("<xml>content</xml>"));
  }

  @Test
  public void testGetValueForMetacardAttributeBinary() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("test");

    Serializable result = converter.getValueForMetacardAttribute(AttributeFormat.BINARY, reader);

    assertThat(result, notNullValue());
    assertThat(result instanceof byte[], is(true));
  }

  @Test
  public void testParseDateFromXmlValidDate() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("2023-01-15");
    when(reader.hasMoreChildren()).thenReturn(false);

    Date result = converter.parseDateFromXml(reader);

    assertThat(result, notNullValue());
  }

  @Test
  public void testParseDateFromXmlValidDateTime() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("2023-01-15T10:30:00Z");
    when(reader.hasMoreChildren()).thenReturn(false);

    Date result = converter.parseDateFromXml(reader);

    assertThat(result, notNullValue());
  }

  @Test
  public void testParseDateFromXmlBlankValue() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("");
    when(reader.hasMoreChildren()).thenReturn(false);

    Date result = converter.parseDateFromXml(reader);

    assertThat(result, nullValue());
  }

  @Test
  public void testParseDateFromXmlInvalidDateUsesCurrentDate() {
    HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
    when(reader.getValue()).thenReturn("invalid-date");
    when(reader.hasMoreChildren()).thenReturn(false);

    Date result = converter.parseDateFromXml(reader);

    assertThat(result, notNullValue());
  }

  // Concrete implementation for testing
  private static class TestableAbstractFeatureConverter extends AbstractFeatureConverter {

    public TestableAbstractFeatureConverter() {
      super();
    }

    public TestableAbstractFeatureConverter(MetacardMapper metacardMapper) {
      super(metacardMapper);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
      // Stub implementation for testing
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
      // Stub implementation for testing
      return null;
    }

    // Expose protected fields for testing
    public String getSourceId() {
      return this.sourceId;
    }

    public String getWfsUrl() {
      return this.wfsUrl;
    }

    public String getCoordinateOrder() {
      return this.coordinateOrder;
    }
  }
}
