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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FeatureConverterFactoryTest {

  private TestFeatureConverterFactory factory;

  @BeforeEach
  public void setUp() {
    factory = new TestFeatureConverterFactory();
  }

  @Test
  public void testCreateConverter() {
    FeatureConverter converter = factory.createConverter();
    assertThat(converter, notNullValue());
  }

  @Test
  public void testCreateConverterReturnsFeatureConverter() {
    FeatureConverter converter = factory.createConverter();
    assertThat(converter, instanceOf(FeatureConverter.class));
  }

  @Test
  public void testGetFeatureType() {
    String featureType = factory.getFeatureType();
    assertThat(featureType, is("test-feature-type"));
  }

  @Test
  public void testGetFeatureTypeNotNull() {
    assertThat(factory.getFeatureType(), notNullValue());
  }

  @Test
  public void testCreateMultipleConverters() {
    FeatureConverter converter1 = factory.createConverter();
    FeatureConverter converter2 = factory.createConverter();

    assertThat(converter1, notNullValue());
    assertThat(converter2, notNullValue());
  }

  @Test
  public void testDifferentFeatureTypes() {
    TestFeatureConverterFactory factory1 = new TestFeatureConverterFactory("type1");
    TestFeatureConverterFactory factory2 = new TestFeatureConverterFactory("type2");

    assertThat(factory1.getFeatureType(), is("type1"));
    assertThat(factory2.getFeatureType(), is("type2"));
  }

  @Test
  public void testCreateConverterAfterMultipleCalls() {
    factory.createConverter();
    factory.createConverter();
    FeatureConverter converter = factory.createConverter();

    assertThat(converter, notNullValue());
  }

  @Test
  public void testFactoryWithNullFeatureType() {
    TestFeatureConverterFactory nullFactory = new TestFeatureConverterFactory(null);
    assertThat(nullFactory.getFeatureType(), is((String) null));
  }

  @Test
  public void testFactoryWithEmptyFeatureType() {
    TestFeatureConverterFactory emptyFactory = new TestFeatureConverterFactory("");
    assertThat(emptyFactory.getFeatureType(), is(""));
  }

  // Test implementation of FeatureConverterFactory
  private static class TestFeatureConverterFactory implements FeatureConverterFactory {
    private final String featureType;

    public TestFeatureConverterFactory() {
      this("test-feature-type");
    }

    public TestFeatureConverterFactory(String featureType) {
      this.featureType = featureType;
    }

    @Override
    public FeatureConverter createConverter() {
      return new TestFeatureConverter();
    }

    @Override
    public String getFeatureType() {
      return featureType;
    }
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

    @Override
    public void setCoordinateOrder(String coordinateOrder) {
      this.coordinateOrder = coordinateOrder;
    }

    @Override
    public boolean canConvert(Class type) {
      return Metacard.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(
        Object source, HierarchicalStreamWriter writer, MarshallingContext context) {}

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
      return null;
    }
  }
}
