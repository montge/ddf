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
package org.codice.ddf.admin.core.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.osgi.service.metatype.AttributeDefinition;

public class MetatypeAttributeImplTest {

  @Test
  public void testConstructorWithAttributeDefinition() {
    AttributeDefinition definition = mock(AttributeDefinition.class);
    when(definition.getID()).thenReturn("test.id");
    when(definition.getName()).thenReturn("Test Name");
    when(definition.getCardinality()).thenReturn(0);
    when(definition.getDefaultValue()).thenReturn(new String[] {"default"});
    when(definition.getDescription()).thenReturn("Test Description");
    when(definition.getType()).thenReturn(AttributeDefinition.STRING);
    when(definition.getOptionLabels()).thenReturn(new String[] {"Label1", "Label2"});
    when(definition.getOptionValues()).thenReturn(new String[] {"value1", "value2"});

    MetatypeAttributeImpl attribute = new MetatypeAttributeImpl(definition);

    assertThat(attribute.getId(), is(equalTo("test.id")));
    assertThat(attribute.getName(), is(equalTo("Test Name")));
    assertThat(attribute.getCardinality(), is(equalTo(0)));
    assertThat(attribute.getDefaultValue(), is(equalTo(new String[] {"default"})));
    assertThat(attribute.getDescription(), is(equalTo("Test Description")));
    assertThat(attribute.getType(), is(equalTo(AttributeDefinition.STRING)));
    assertThat(attribute.getOptionLabels(), is(equalTo(new String[] {"Label1", "Label2"})));
    assertThat(attribute.getOptionValues(), is(equalTo(new String[] {"value1", "value2"})));
  }

  @Test
  public void testConstructorWithNullValues() {
    AttributeDefinition definition = mock(AttributeDefinition.class);
    when(definition.getID()).thenReturn(null);
    when(definition.getName()).thenReturn(null);
    when(definition.getCardinality()).thenReturn(0);
    when(definition.getDefaultValue()).thenReturn(null);
    when(definition.getDescription()).thenReturn(null);
    when(definition.getType()).thenReturn(0);
    when(definition.getOptionLabels()).thenReturn(null);
    when(definition.getOptionValues()).thenReturn(null);

    MetatypeAttributeImpl attribute = new MetatypeAttributeImpl(definition);

    assertThat(attribute.getId(), is(equalTo(null)));
    assertThat(attribute.getName(), is(equalTo(null)));
    assertThat(attribute.getCardinality(), is(equalTo(0)));
    assertThat(attribute.getDefaultValue(), is(equalTo(null)));
    assertThat(attribute.getDescription(), is(equalTo(null)));
    assertThat(attribute.getType(), is(equalTo(0)));
    assertThat(attribute.getOptionLabels(), is(equalTo(null)));
    assertThat(attribute.getOptionValues(), is(equalTo(null)));
  }

  @Test
  public void testSettersAndGetters() {
    AttributeDefinition definition = mock(AttributeDefinition.class);
    when(definition.getID()).thenReturn("original.id");
    when(definition.getName()).thenReturn("Original Name");
    when(definition.getCardinality()).thenReturn(0);
    when(definition.getDefaultValue()).thenReturn(new String[] {"original"});
    when(definition.getDescription()).thenReturn("Original Description");
    when(definition.getType()).thenReturn(AttributeDefinition.STRING);
    when(definition.getOptionLabels()).thenReturn(new String[] {"OrigLabel"});
    when(definition.getOptionValues()).thenReturn(new String[] {"origValue"});

    MetatypeAttributeImpl attribute = new MetatypeAttributeImpl(definition);

    String newId = "new.id";
    String newName = "New Name";
    int newCardinality = 5;
    String[] newDefaultValue = new String[] {"new1", "new2"};
    String newDescription = "New Description";
    int newType = AttributeDefinition.INTEGER;
    String[] newOptionLabels = new String[] {"NewLabel1", "NewLabel2"};
    String[] newOptionValues = new String[] {"newValue1", "newValue2"};

    attribute.setId(newId);
    attribute.setName(newName);
    attribute.setCardinality(newCardinality);
    attribute.setDefaultValue(newDefaultValue);
    attribute.setDescription(newDescription);
    attribute.setType(newType);
    attribute.setOptionLabels(newOptionLabels);
    attribute.setOptionValues(newOptionValues);

    assertThat(attribute.getId(), is(equalTo(newId)));
    assertThat(attribute.getName(), is(equalTo(newName)));
    assertThat(attribute.getCardinality(), is(equalTo(newCardinality)));
    assertThat(attribute.getDefaultValue(), is(equalTo(newDefaultValue)));
    assertThat(attribute.getDescription(), is(equalTo(newDescription)));
    assertThat(attribute.getType(), is(equalTo(newType)));
    assertThat(attribute.getOptionLabels(), is(equalTo(newOptionLabels)));
    assertThat(attribute.getOptionValues(), is(equalTo(newOptionValues)));
  }

  @Test
  public void testMapBehavior() {
    AttributeDefinition definition = mock(AttributeDefinition.class);
    when(definition.getID()).thenReturn("map.test.id");
    when(definition.getName()).thenReturn("Map Test");
    when(definition.getCardinality()).thenReturn(1);
    when(definition.getDefaultValue()).thenReturn(new String[] {"default"});
    when(definition.getDescription()).thenReturn("Description");
    when(definition.getType()).thenReturn(AttributeDefinition.BOOLEAN);
    when(definition.getOptionLabels()).thenReturn(new String[] {"Label"});
    when(definition.getOptionValues()).thenReturn(new String[] {"value"});

    MetatypeAttributeImpl attribute = new MetatypeAttributeImpl(definition);

    assertThat(attribute.containsKey("id"), is(true));
    assertThat(attribute.containsKey("name"), is(true));
    assertThat(attribute.containsKey("cardinality"), is(true));
    assertThat(attribute.containsKey("defaultValue"), is(true));
    assertThat(attribute.containsKey("description"), is(true));
    assertThat(attribute.containsKey("type"), is(true));
    assertThat(attribute.containsKey("optionLabels"), is(true));
    assertThat(attribute.containsKey("optionValues"), is(true));
    assertThat(attribute.size(), is(8));
  }

  @Test
  public void testDifferentAttributeTypes() {
    AttributeDefinition stringDef = mock(AttributeDefinition.class);
    when(stringDef.getID()).thenReturn("string.id");
    when(stringDef.getName()).thenReturn("String Attribute");
    when(stringDef.getCardinality()).thenReturn(0);
    when(stringDef.getDefaultValue()).thenReturn(new String[] {"stringDefault"});
    when(stringDef.getDescription()).thenReturn("String Description");
    when(stringDef.getType()).thenReturn(AttributeDefinition.STRING);
    when(stringDef.getOptionLabels()).thenReturn(null);
    when(stringDef.getOptionValues()).thenReturn(null);

    MetatypeAttributeImpl stringAttr = new MetatypeAttributeImpl(stringDef);
    assertThat(stringAttr.getType(), is(equalTo(AttributeDefinition.STRING)));

    AttributeDefinition intDef = mock(AttributeDefinition.class);
    when(intDef.getID()).thenReturn("int.id");
    when(intDef.getName()).thenReturn("Integer Attribute");
    when(intDef.getCardinality()).thenReturn(0);
    when(intDef.getDefaultValue()).thenReturn(new String[] {"42"});
    when(intDef.getDescription()).thenReturn("Integer Description");
    when(intDef.getType()).thenReturn(AttributeDefinition.INTEGER);
    when(intDef.getOptionLabels()).thenReturn(null);
    when(intDef.getOptionValues()).thenReturn(null);

    MetatypeAttributeImpl intAttr = new MetatypeAttributeImpl(intDef);
    assertThat(intAttr.getType(), is(equalTo(AttributeDefinition.INTEGER)));
  }

  @Test
  public void testCardinalityValues() {
    AttributeDefinition definition = mock(AttributeDefinition.class);
    when(definition.getID()).thenReturn("test.id");
    when(definition.getName()).thenReturn("Test");
    when(definition.getCardinality()).thenReturn(10);
    when(definition.getDefaultValue()).thenReturn(null);
    when(definition.getDescription()).thenReturn("Test");
    when(definition.getType()).thenReturn(AttributeDefinition.STRING);
    when(definition.getOptionLabels()).thenReturn(null);
    when(definition.getOptionValues()).thenReturn(null);

    MetatypeAttributeImpl attribute = new MetatypeAttributeImpl(definition);
    assertThat(attribute.getCardinality(), is(equalTo(10)));

    attribute.setCardinality(-5);
    assertThat(attribute.getCardinality(), is(equalTo(-5)));

    attribute.setCardinality(0);
    assertThat(attribute.getCardinality(), is(equalTo(0)));
  }

  @Test
  public void testEmptyArrays() {
    AttributeDefinition definition = mock(AttributeDefinition.class);
    when(definition.getID()).thenReturn("test.id");
    when(definition.getName()).thenReturn("Test");
    when(definition.getCardinality()).thenReturn(0);
    when(definition.getDefaultValue()).thenReturn(new String[] {});
    when(definition.getDescription()).thenReturn("Test");
    when(definition.getType()).thenReturn(AttributeDefinition.STRING);
    when(definition.getOptionLabels()).thenReturn(new String[] {});
    when(definition.getOptionValues()).thenReturn(new String[] {});

    MetatypeAttributeImpl attribute = new MetatypeAttributeImpl(definition);

    assertThat(attribute.getDefaultValue().length, is(0));
    assertThat(attribute.getOptionLabels().length, is(0));
    assertThat(attribute.getOptionValues().length, is(0));
  }
}
