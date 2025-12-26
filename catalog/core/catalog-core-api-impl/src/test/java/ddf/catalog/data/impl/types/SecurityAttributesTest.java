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
import ddf.catalog.data.types.Security;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityAttributesTest {

  private SecurityAttributes securityAttributes;

  @BeforeEach
  void setUp() {
    securityAttributes = new SecurityAttributes();
  }

  @Test
  void testGetName() {
    assertThat(securityAttributes.getName(), is("security"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsFiveDescriptors() {
    Set<AttributeDescriptor> descriptors = securityAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(5));
  }

  @Test
  void testGetAttributeDescriptorForAccessAdministrators() {
    AttributeDescriptor descriptor =
        securityAttributes.getAttributeDescriptor(Security.ACCESS_ADMINISTRATORS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Security.ACCESS_ADMINISTRATORS));
  }

  @Test
  void testGetAttributeDescriptorForAccessGroups() {
    AttributeDescriptor descriptor =
        securityAttributes.getAttributeDescriptor(Security.ACCESS_GROUPS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Security.ACCESS_GROUPS));
  }

  @Test
  void testGetAttributeDescriptorForAccessGroupsRead() {
    AttributeDescriptor descriptor =
        securityAttributes.getAttributeDescriptor(Security.ACCESS_GROUPS_READ);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Security.ACCESS_GROUPS_READ));
  }

  @Test
  void testGetAttributeDescriptorForAccessIndividuals() {
    AttributeDescriptor descriptor =
        securityAttributes.getAttributeDescriptor(Security.ACCESS_INDIVIDUALS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Security.ACCESS_INDIVIDUALS));
  }

  @Test
  void testGetAttributeDescriptorForAccessIndividualsRead() {
    AttributeDescriptor descriptor =
        securityAttributes.getAttributeDescriptor(Security.ACCESS_INDIVIDUALS_READ);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Security.ACCESS_INDIVIDUALS_READ));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor = securityAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testAllAttributesAreMultivalued() {
    for (AttributeDescriptor descriptor : securityAttributes.getAttributeDescriptors()) {
      assertThat(
          descriptor.getName() + " should be multivalued", descriptor.isMultiValued(), is(true));
    }
  }

  @Test
  void testAllAttributesAreIndexed() {
    for (AttributeDescriptor descriptor : securityAttributes.getAttributeDescriptors()) {
      assertThat(descriptor.getName() + " should be indexed", descriptor.isIndexed(), is(true));
    }
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = securityAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
