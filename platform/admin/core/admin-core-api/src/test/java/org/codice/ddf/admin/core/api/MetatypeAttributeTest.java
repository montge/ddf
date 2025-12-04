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
package org.codice.ddf.admin.core.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

/** Tests for {@link MetatypeAttribute} interface constants and default methods. */
public class MetatypeAttributeTest {

  private TestMetatypeAttribute attribute;

  @Before
  public void setUp() {
    attribute = new TestMetatypeAttribute();
  }

  @Test
  public void testIdConstant() {
    assertThat(MetatypeAttribute.ID, is("id"));
  }

  @Test
  public void testNameConstant() {
    assertThat(MetatypeAttribute.NAME, is("name"));
  }

  @Test
  public void testCardinalityConstant() {
    assertThat(MetatypeAttribute.CARDINALITY, is("cardinality"));
  }

  @Test
  public void testDefaultValueConstant() {
    assertThat(MetatypeAttribute.DEFAULT_VALUE, is("defaultValue"));
  }

  @Test
  public void testDescriptionConstant() {
    assertThat(MetatypeAttribute.DESCRIPTION, is("description"));
  }

  @Test
  public void testTypeConstant() {
    assertThat(MetatypeAttribute.TYPE, is("type"));
  }

  @Test
  public void testOptionLabelsConstant() {
    assertThat(MetatypeAttribute.OPTION_LABELS, is("optionLabels"));
  }

  @Test
  public void testOptionValuesConstant() {
    assertThat(MetatypeAttribute.OPTION_VALUES, is("optionValues"));
  }

  @Test
  public void testGetIdReturnsNull() {
    assertThat(attribute.getId(), is(nullValue()));
  }

  @Test
  public void testSetAndGetId() {
    attribute.setId("attr-id");
    assertThat(attribute.getId(), is("attr-id"));
  }

  @Test
  public void testGetNameReturnsNull() {
    assertThat(attribute.getName(), is(nullValue()));
  }

  @Test
  public void testSetAndGetName() {
    attribute.setName("Attribute Name");
    assertThat(attribute.getName(), is("Attribute Name"));
  }

  @Test
  public void testSetAndGetCardinality() {
    attribute.setCardinality(5);
    assertThat(attribute.getCardinality(), is(5));
  }

  @Test
  public void testSetCardinalityNegative() {
    attribute.setCardinality(-1);
    assertThat(attribute.getCardinality(), is(-1));
  }

  @Test
  public void testSetCardinalityZero() {
    attribute.setCardinality(0);
    assertThat(attribute.getCardinality(), is(0));
  }

  @Test
  public void testGetDefaultValueReturnsNull() {
    assertThat(attribute.getDefaultValue(), is(nullValue()));
  }

  @Test
  public void testSetAndGetDefaultValue() {
    String[] defaults = new String[] {"default1", "default2"};
    attribute.setDefaultValue(defaults);
    assertThat(attribute.getDefaultValue(), arrayContaining("default1", "default2"));
  }

  @Test
  public void testGetDescriptionReturnsNull() {
    assertThat(attribute.getDescription(), is(nullValue()));
  }

  @Test
  public void testSetAndGetDescription() {
    attribute.setDescription("An attribute description");
    assertThat(attribute.getDescription(), is("An attribute description"));
  }

  @Test
  public void testSetAndGetType() {
    attribute.setType(1);
    assertThat(attribute.getType(), is(1));
  }

  @Test
  public void testGetOptionLabelsReturnsNull() {
    assertThat(attribute.getOptionLabels(), is(nullValue()));
  }

  @Test
  public void testSetAndGetOptionLabels() {
    String[] labels = new String[] {"Label 1", "Label 2", "Label 3"};
    attribute.setOptionLabels(labels);
    assertThat(attribute.getOptionLabels(), arrayContaining("Label 1", "Label 2", "Label 3"));
  }

  @Test
  public void testGetOptionValuesReturnsNull() {
    assertThat(attribute.getOptionValues(), is(nullValue()));
  }

  @Test
  public void testSetAndGetOptionValues() {
    String[] values = new String[] {"value1", "value2", "value3"};
    attribute.setOptionValues(values);
    assertThat(attribute.getOptionValues(), arrayContaining("value1", "value2", "value3"));
  }

  @Test
  public void testIdIsStoredInMap() {
    attribute.setId("stored-id");
    assertThat(attribute.get(MetatypeAttribute.ID), is("stored-id"));
  }

  @Test
  public void testCardinalityIsStoredInMap() {
    attribute.setCardinality(10);
    assertThat(attribute.get(MetatypeAttribute.CARDINALITY), is(10));
  }

  @Test
  public void testTypeIsStoredInMap() {
    attribute.setType(2);
    assertThat(attribute.get(MetatypeAttribute.TYPE), is(2));
  }

  /** Test implementation of MetatypeAttribute that extends HashMap. */
  private static class TestMetatypeAttribute extends HashMap<String, Object>
      implements MetatypeAttribute {}
}
