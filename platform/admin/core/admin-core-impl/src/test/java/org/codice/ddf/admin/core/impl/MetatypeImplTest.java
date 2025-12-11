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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import org.codice.ddf.admin.core.api.MetatypeAttribute;
import org.junit.jupiter.api.Test;

public class MetatypeImplTest {

  @Test
  public void testConstructorWithAllParameters() {
    String id = "test.id";
    String name = "Test Metatype";
    MetatypeAttribute attr1 = mock(MetatypeAttribute.class);
    MetatypeAttribute attr2 = mock(MetatypeAttribute.class);
    List<MetatypeAttribute> attributes = Arrays.asList(attr1, attr2);

    MetatypeImpl metatype = new MetatypeImpl(id, name, attributes);

    assertThat(metatype.getId(), is(equalTo(id)));
    assertThat(metatype.getName(), is(equalTo(name)));
    assertThat(metatype.getAttributeDefinitions(), is(equalTo(attributes)));
    assertThat(metatype.getAttributeDefinitions().size(), is(2));
  }

  @Test
  public void testConstructorWithNullValues() {
    MetatypeImpl metatype = new MetatypeImpl(null, null, null);

    assertThat(metatype.getId(), is(nullValue()));
    assertThat(metatype.getName(), is(nullValue()));
    assertThat(metatype.getAttributeDefinitions(), is(nullValue()));
  }

  @Test
  public void testSettersAndGetters() {
    MetatypeImpl metatype = new MetatypeImpl("id", "name", null);

    String newId = "new.id";
    String newName = "New Name";
    MetatypeAttribute attr1 = mock(MetatypeAttribute.class);
    List<MetatypeAttribute> newAttributes = Arrays.asList(attr1);

    metatype.setId(newId);
    metatype.setName(newName);
    metatype.setAttributeDefinitions(newAttributes);

    assertThat(metatype.getId(), is(equalTo(newId)));
    assertThat(metatype.getName(), is(equalTo(newName)));
    assertThat(metatype.getAttributeDefinitions(), is(equalTo(newAttributes)));
    assertThat(metatype.getAttributeDefinitions().size(), is(1));
  }

  @Test
  public void testEmptyAttributeList() {
    List<MetatypeAttribute> emptyList = Arrays.asList();
    MetatypeImpl metatype = new MetatypeImpl("id", "name", emptyList);

    assertThat(metatype.getAttributeDefinitions(), is(equalTo(emptyList)));
    assertThat(metatype.getAttributeDefinitions().size(), is(0));
  }

  @Test
  public void testMapBehavior() {
    MetatypeAttribute attr = mock(MetatypeAttribute.class);
    List<MetatypeAttribute> attributes = Arrays.asList(attr);
    MetatypeImpl metatype = new MetatypeImpl("test.id", "Test Name", attributes);

    assertThat(metatype.containsKey("id"), is(true));
    assertThat(metatype.containsKey("name"), is(true));
    assertThat(metatype.containsKey("metatype"), is(true));
    assertThat(metatype.size(), is(3));
  }

  @Test
  public void testMultipleAttributeDefinitions() {
    MetatypeAttribute attr1 = mock(MetatypeAttribute.class);
    MetatypeAttribute attr2 = mock(MetatypeAttribute.class);
    MetatypeAttribute attr3 = mock(MetatypeAttribute.class);
    List<MetatypeAttribute> attributes = Arrays.asList(attr1, attr2, attr3);

    MetatypeImpl metatype = new MetatypeImpl("id", "name", attributes);

    assertThat(metatype.getAttributeDefinitions().size(), is(3));
    assertThat(metatype.getAttributeDefinitions().get(0), is(attr1));
    assertThat(metatype.getAttributeDefinitions().get(1), is(attr2));
    assertThat(metatype.getAttributeDefinitions().get(2), is(attr3));
  }
}
