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
package org.codice.ddf.spatial.ogc.wfs.catalog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetacardTypeEnhancerTest {

  private TestMetacardTypeEnhancer enhancer;

  @BeforeEach
  public void setUp() {
    enhancer = new TestMetacardTypeEnhancer();
  }

  @Test
  public void testGetFeatureName() {
    assertThat(enhancer.getFeatureName(), is("test-feature"));
  }

  @Test
  public void testGetAttributeDescriptors() {
    Set<AttributeDescriptor> descriptors = enhancer.getAttributeDescriptors();
    assertThat(descriptors, notNullValue());
    assertThat(descriptors.size(), is(3));
  }

  @Test
  public void testGetAttributeDescriptorsNotNull() {
    Set<AttributeDescriptor> descriptors = enhancer.getAttributeDescriptors();
    assertThat(descriptors, notNullValue());
  }

  @Test
  public void testGetFeatureNameNotNull() {
    assertThat(enhancer.getFeatureName(), notNullValue());
  }

  @Test
  public void testMultipleImplementations() {
    TestMetacardTypeEnhancer enhancer1 = new TestMetacardTypeEnhancer();
    TestMetacardTypeEnhancer enhancer2 = new TestMetacardTypeEnhancer();

    assertThat(enhancer1.getFeatureName(), is(enhancer2.getFeatureName()));
  }

  @Test
  public void testDifferentFeatureNames() {
    TestMetacardTypeEnhancer enhancer1 = new TestMetacardTypeEnhancer("feature1");
    TestMetacardTypeEnhancer enhancer2 = new TestMetacardTypeEnhancer("feature2");

    assertThat(enhancer1.getFeatureName(), is("feature1"));
    assertThat(enhancer2.getFeatureName(), is("feature2"));
  }

  @Test
  public void testEmptyAttributeDescriptors() {
    TestMetacardTypeEnhancer emptyEnhancer = new TestMetacardTypeEnhancer("empty", new HashSet<>());
    Set<AttributeDescriptor> descriptors = emptyEnhancer.getAttributeDescriptors();

    assertThat(descriptors, notNullValue());
    assertThat(descriptors.size(), is(0));
  }

  // Test implementation of MetacardTypeEnhancer
  private static class TestMetacardTypeEnhancer implements MetacardTypeEnhancer {
    private final String featureName;
    private final Set<AttributeDescriptor> attributeDescriptors;

    public TestMetacardTypeEnhancer() {
      this("test-feature");
    }

    public TestMetacardTypeEnhancer(String featureName) {
      this.featureName = featureName;
      this.attributeDescriptors = new HashSet<>();
      attributeDescriptors.add(
          new AttributeDescriptorImpl("title", true, true, false, false, BasicTypes.STRING_TYPE));
      attributeDescriptors.add(
          new AttributeDescriptorImpl("created", true, true, false, false, BasicTypes.DATE_TYPE));
      attributeDescriptors.add(
          new AttributeDescriptorImpl("location", true, true, false, false, BasicTypes.GEO_TYPE));
    }

    public TestMetacardTypeEnhancer(String featureName, Set<AttributeDescriptor> descriptors) {
      this.featureName = featureName;
      this.attributeDescriptors = descriptors;
    }

    @Override
    public String getFeatureName() {
      return featureName;
    }

    @Override
    public Set<AttributeDescriptor> getAttributeDescriptors() {
      return attributeDescriptors;
    }
  }
}
