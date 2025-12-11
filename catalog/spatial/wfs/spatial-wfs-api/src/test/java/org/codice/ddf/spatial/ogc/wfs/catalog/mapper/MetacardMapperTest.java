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
package org.codice.ddf.spatial.ogc.wfs.catalog.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetacardMapperTest {

  private TestMetacardMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new TestMetacardMapper();
  }

  @Test
  public void testGetFeatureProperty() {
    String featureProperty = mapper.getFeatureProperty("title");
    assertThat(featureProperty, is("wfs:title"));
  }

  @Test
  public void testGetFeaturePropertyNotFound() {
    String featureProperty = mapper.getFeatureProperty("unknown");
    assertThat(featureProperty, nullValue());
  }

  @Test
  public void testGetMetacardAttribute() {
    String metacardAttribute = mapper.getMetacardAttribute("wfs:title");
    assertThat(metacardAttribute, is("title"));
  }

  @Test
  public void testGetMetacardAttributeNotFound() {
    String metacardAttribute = mapper.getMetacardAttribute("unknown");
    assertThat(metacardAttribute, nullValue());
  }

  @Test
  public void testGetFeatureType() {
    assertThat(mapper.getFeatureType(), is("test-feature-type"));
  }

  @Test
  public void testGetSortByTemporalFeatureProperty() {
    assertThat(mapper.getSortByTemporalFeatureProperty(), is("wfs:modified"));
  }

  @Test
  public void testGetSortByRelevanceFeatureProperty() {
    assertThat(mapper.getSortByRelevanceFeatureProperty(), is("wfs:relevance"));
  }

  @Test
  public void testGetSortByDistanceFeatureProperty() {
    assertThat(mapper.getSortByDistanceFeatureProperty(), is("wfs:distance"));
  }

  @Test
  public void testGetDataUnit() {
    assertThat(mapper.getDataUnit(), is("MB"));
  }

  @Test
  public void testBidirectionalMapping() {
    String featureProperty = mapper.getFeatureProperty("title");
    String metacardAttribute = mapper.getMetacardAttribute(featureProperty);
    assertThat(metacardAttribute, is("title"));
  }

  @Test
  public void testMultipleMappings() {
    assertThat(mapper.getFeatureProperty("title"), is("wfs:title"));
    assertThat(mapper.getFeatureProperty("created"), is("wfs:created"));
    assertThat(mapper.getFeatureProperty("modified"), is("wfs:modified"));
  }

  @Test
  public void testNullInputs() {
    assertThat(mapper.getFeatureProperty(null), nullValue());
    assertThat(mapper.getMetacardAttribute(null), nullValue());
  }

  @Test
  public void testEmptyStringInputs() {
    assertThat(mapper.getFeatureProperty(""), nullValue());
    assertThat(mapper.getMetacardAttribute(""), nullValue());
  }

  @Test
  public void testAllMethodsReturnNonNull() {
    assertThat(mapper.getFeatureType(), notNullValue());
    assertThat(mapper.getSortByTemporalFeatureProperty(), notNullValue());
    assertThat(mapper.getSortByRelevanceFeatureProperty(), notNullValue());
    assertThat(mapper.getSortByDistanceFeatureProperty(), notNullValue());
    assertThat(mapper.getDataUnit(), notNullValue());
  }

  @Test
  public void testDifferentDataUnits() {
    TestMetacardMapper kbMapper = new TestMetacardMapper("KB");
    TestMetacardMapper gbMapper = new TestMetacardMapper("GB");

    assertThat(kbMapper.getDataUnit(), is("KB"));
    assertThat(gbMapper.getDataUnit(), is("GB"));
  }

  @Test
  public void testCaseSensitiveMapping() {
    assertThat(mapper.getFeatureProperty("Title"), nullValue());
    assertThat(mapper.getFeatureProperty("title"), is("wfs:title"));
  }

  // Test implementation of MetacardMapper
  private static class TestMetacardMapper implements MetacardMapper {
    private final Map<String, String> metacardToFeature;
    private final Map<String, String> featureToMetacard;
    private final String dataUnit;

    public TestMetacardMapper() {
      this("MB");
    }

    public TestMetacardMapper(String dataUnit) {
      this.dataUnit = dataUnit;
      this.metacardToFeature = new HashMap<>();
      this.featureToMetacard = new HashMap<>();

      // Setup bidirectional mapping
      addMapping("title", "wfs:title");
      addMapping("created", "wfs:created");
      addMapping("modified", "wfs:modified");
      addMapping("location", "wfs:location");
    }

    private void addMapping(String metacardAttr, String featureProp) {
      metacardToFeature.put(metacardAttr, featureProp);
      featureToMetacard.put(featureProp, metacardAttr);
    }

    @Override
    public String getFeatureProperty(String metacardAttribute) {
      if (metacardAttribute == null || metacardAttribute.isEmpty()) {
        return null;
      }
      return metacardToFeature.get(metacardAttribute);
    }

    @Override
    public String getMetacardAttribute(String featureProperty) {
      if (featureProperty == null || featureProperty.isEmpty()) {
        return null;
      }
      return featureToMetacard.get(featureProperty);
    }

    @Override
    public String getFeatureType() {
      return "test-feature-type";
    }

    @Override
    public String getSortByTemporalFeatureProperty() {
      return "wfs:modified";
    }

    @Override
    public String getSortByRelevanceFeatureProperty() {
      return "wfs:relevance";
    }

    @Override
    public String getSortByDistanceFeatureProperty() {
      return "wfs:distance";
    }

    @Override
    public String getDataUnit() {
      return dataUnit;
    }
  }
}
