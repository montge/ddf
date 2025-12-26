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
package org.codice.ddf.transformer.xml.streaming.lib;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.AttributeDescriptor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DynamicMetacardTypeTest {

  @Test
  void testConstructorAppendsMetacardSuffix() {
    DynamicMetacardType type = new DynamicMetacardType(Collections.emptySet(), "custom");

    assertThat(type.getName(), is("custom.metacard"));
  }

  @Test
  void testGetNameEndsWithMetacardSuffix() {
    DynamicMetacardType type = new DynamicMetacardType(Collections.emptySet(), "test");

    assertThat(type.getName(), endsWith(".metacard"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsEmptySet() {
    DynamicMetacardType type = new DynamicMetacardType(Collections.emptySet(), "test");

    assertThat(type.getAttributeDescriptors(), is(notNullValue()));
    assertThat(type.getAttributeDescriptors(), hasSize(0));
  }

  @Test
  void testGetAttributeDescriptorsReturnsProvidedSet() {
    AttributeDescriptor attr1 = mock(AttributeDescriptor.class);
    when(attr1.getName()).thenReturn("attr1");
    AttributeDescriptor attr2 = mock(AttributeDescriptor.class);
    when(attr2.getName()).thenReturn("attr2");

    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(attr1);
    descriptors.add(attr2);

    DynamicMetacardType type = new DynamicMetacardType(descriptors, "test");

    assertThat(type.getAttributeDescriptors(), hasSize(2));
    assertThat(type.getAttributeDescriptors(), containsInAnyOrder(attr1, attr2));
  }

  @Test
  void testGetAttributeDescriptorByName() {
    AttributeDescriptor attr = mock(AttributeDescriptor.class);
    when(attr.getName()).thenReturn("title");

    Set<AttributeDescriptor> descriptors = Collections.singleton(attr);

    DynamicMetacardType type = new DynamicMetacardType(descriptors, "test");

    assertThat(type.getAttributeDescriptor("title"), is(attr));
  }

  @Test
  void testGetAttributeDescriptorReturnsNullForUnknownAttribute() {
    DynamicMetacardType type = new DynamicMetacardType(Collections.emptySet(), "test");

    assertThat(type.getAttributeDescriptor("unknown"), is(nullValue()));
  }

  @Test
  void testGetAttributeDescriptorWithMultipleDescriptors() {
    AttributeDescriptor attr1 = mock(AttributeDescriptor.class);
    when(attr1.getName()).thenReturn("attr1");
    AttributeDescriptor attr2 = mock(AttributeDescriptor.class);
    when(attr2.getName()).thenReturn("attr2");
    AttributeDescriptor attr3 = mock(AttributeDescriptor.class);
    when(attr3.getName()).thenReturn("attr3");

    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(attr1);
    descriptors.add(attr2);
    descriptors.add(attr3);

    DynamicMetacardType type = new DynamicMetacardType(descriptors, "test");

    assertThat(type.getAttributeDescriptor("attr2"), is(attr2));
  }

  @Test
  void testNameWithEmptyString() {
    DynamicMetacardType type = new DynamicMetacardType(Collections.emptySet(), "");

    assertThat(type.getName(), is(".metacard"));
  }
}
