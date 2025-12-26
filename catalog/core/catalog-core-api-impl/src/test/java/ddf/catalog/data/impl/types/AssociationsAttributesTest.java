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
package ddf.catalog.data.impl.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.types.Associations;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssociationsAttributesTest {

  private AssociationsAttributes associationsAttributes;

  @BeforeEach
  void setUp() {
    associationsAttributes = new AssociationsAttributes();
  }

  @Test
  void testGetName() {
    assertThat(associationsAttributes.getName(), is("associations"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsThreeDescriptors() {
    Set<AttributeDescriptor> descriptors = associationsAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(3));
  }

  @Test
  void testGetAttributeDescriptorForDerived() {
    AttributeDescriptor descriptor =
        associationsAttributes.getAttributeDescriptor(Associations.DERIVED);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Associations.DERIVED));
  }

  @Test
  void testGetAttributeDescriptorForRelated() {
    AttributeDescriptor descriptor =
        associationsAttributes.getAttributeDescriptor(Associations.RELATED);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Associations.RELATED));
  }

  @Test
  void testGetAttributeDescriptorForExternal() {
    AttributeDescriptor descriptor =
        associationsAttributes.getAttributeDescriptor(Associations.EXTERNAL);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Associations.EXTERNAL));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor =
        associationsAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testAllAttributesAreMultivalued() {
    for (AttributeDescriptor descriptor : associationsAttributes.getAttributeDescriptors()) {
      assertThat(
          descriptor.getName() + " should be multivalued", descriptor.isMultiValued(), is(true));
    }
  }

  @Test
  void testAllAttributesAreNotTokenized() {
    for (AttributeDescriptor descriptor : associationsAttributes.getAttributeDescriptors()) {
      assertThat(
          descriptor.getName() + " should not be tokenized", descriptor.isTokenized(), is(false));
    }
  }

  @Test
  void testAllAttributesAreIndexed() {
    for (AttributeDescriptor descriptor : associationsAttributes.getAttributeDescriptors()) {
      assertThat(descriptor.getName() + " should be indexed", descriptor.isIndexed(), is(true));
    }
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = associationsAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
