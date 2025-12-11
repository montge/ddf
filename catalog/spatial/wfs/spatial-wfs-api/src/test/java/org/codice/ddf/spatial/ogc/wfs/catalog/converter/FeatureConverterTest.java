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
package org.codice.ddf.spatial.ogc.wfs.catalog.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FeatureConverterTest {

  private TestFeatureConverter converter;

  private MetacardType metacardType;

  @BeforeEach
  public void setUp() {
    converter = new TestFeatureConverter();
    metacardType =
        new MetacardTypeImpl("test-type", MetacardImpl.BASIC_METACARD.getAttributeDescriptors());
  }

  @Test
  public void testSetAndGetSourceId() {
    converter.setSourceId("test-source");
    assertThat(converter.getSourceId(), is("test-source"));
  }

  @Test
  public void testSetSourceIdNull() {
    converter.setSourceId(null);
    assertThat(converter.getSourceId(), nullValue());
  }

  @Test
  public void testSetSourceIdEmpty() {
    converter.setSourceId("");
    assertThat(converter.getSourceId(), is(""));
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
  public void testSetMetacardTypeNull() {
    converter.setMetacardType(null);
    assertThat(converter.getMetacardType(), nullValue());
  }

  @Test
  public void testSetAndGetWfsUrl() {
    converter.setWfsUrl("http://example.com/wfs");
    assertThat(converter.getWfsUrl(), is("http://example.com/wfs"));
  }

  @Test
  public void testSetWfsUrlNull() {
    converter.setWfsUrl(null);
    assertThat(converter.getWfsUrl(), nullValue());
  }

  @Test
  public void testSetWfsUrlEmpty() {
    converter.setWfsUrl("");
    assertThat(converter.getWfsUrl(), is(""));
  }

  @Test
  public void testSetAndGetCoordinateOrder() {
    converter.setCoordinateOrder("LAT_LON");
    assertThat(converter.getCoordinateOrder(), is("LAT_LON"));
  }

  @Test
  public void testSetCoordinateOrderLonLat() {
    converter.setCoordinateOrder("LON_LAT");
    assertThat(converter.getCoordinateOrder(), is("LON_LAT"));
  }

  @Test
  public void testSetCoordinateOrderNull() {
    converter.setCoordinateOrder(null);
    assertThat(converter.getCoordinateOrder(), nullValue());
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
  public void testMultipleSettersInSequence() {
    converter.setSourceId("source1");
    converter.setMetacardType(metacardType);
    converter.setWfsUrl("http://test.com/wfs");
    converter.setCoordinateOrder("LAT_LON");

    assertThat(converter.getSourceId(), is("source1"));
    assertThat(converter.getMetacardType(), is(metacardType));
    assertThat(converter.getWfsUrl(), is("http://test.com/wfs"));
    assertThat(converter.getCoordinateOrder(), is("LAT_LON"));
  }

  @Test
  public void testOverwriteSourceId() {
    converter.setSourceId("source1");
    converter.setSourceId("source2");
    assertThat(converter.getSourceId(), is("source2"));
  }

  @Test
  public void testOverwriteMetacardType() {
    MetacardType type1 =
        new MetacardTypeImpl("type1", MetacardImpl.BASIC_METACARD.getAttributeDescriptors());
    MetacardType type2 =
        new MetacardTypeImpl("type2", MetacardImpl.BASIC_METACARD.getAttributeDescriptors());

    converter.setMetacardType(type1);
    converter.setMetacardType(type2);
    assertThat(converter.getMetacardType(), is(type2));
  }

  @Test
  public void testOverwriteWfsUrl() {
    converter.setWfsUrl("http://url1.com");
    converter.setWfsUrl("http://url2.com");
    assertThat(converter.getWfsUrl(), is("http://url2.com"));
  }

  @Test
  public void testOverwriteCoordinateOrder() {
    converter.setCoordinateOrder("LAT_LON");
    converter.setCoordinateOrder("LON_LAT");
    assertThat(converter.getCoordinateOrder(), is("LON_LAT"));
  }

  // Test implementation of FeatureConverter
  private static class TestFeatureConverter implements FeatureConverter {
    private String sourceId;
    private MetacardType metacardType;
    private String wfsUrl;
    private String coordinateOrder;

    @Override
    public void setSourceId(String id) {
      this.sourceId = id;
    }

    public String getSourceId() {
      return sourceId;
    }

    @Override
    public MetacardType getMetacardType() {
      return metacardType;
    }

    @Override
    public void setMetacardType(MetacardType metacardType) {
      this.metacardType = metacardType;
    }

    @Override
    public void setWfsUrl(String wfsUrl) {
      this.wfsUrl = wfsUrl;
    }

    public String getWfsUrl() {
      return wfsUrl;
    }

    @Override
    public void setCoordinateOrder(String coordinateOrder) {
      this.coordinateOrder = coordinateOrder;
    }

    public String getCoordinateOrder() {
      return coordinateOrder;
    }

    @Override
    public boolean canConvert(Class type) {
      return Metacard.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(
        Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
      // Stub implementation
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
      // Stub implementation
      return null;
    }
  }
}
