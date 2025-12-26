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
import ddf.catalog.data.types.Version;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VersionAttributesTest {

  private VersionAttributes versionAttributes;

  @BeforeEach
  void setUp() {
    versionAttributes = new VersionAttributes();
  }

  @Test
  void testGetName() {
    assertThat(versionAttributes.getName(), is("history"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsSevenDescriptors() {
    Set<AttributeDescriptor> descriptors = versionAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(7));
  }

  @Test
  void testGetAttributeDescriptorForAction() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.ACTION);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Version.ACTION));
  }

  @Test
  void testGetAttributeDescriptorForEditedBy() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.EDITED_BY);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Version.EDITED_BY));
  }

  @Test
  void testGetAttributeDescriptorForId() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.ID);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Version.ID));
  }

  @Test
  void testGetAttributeDescriptorForTags() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.TAGS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Version.TAGS));
  }

  @Test
  void testGetAttributeDescriptorForType() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.TYPE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Version.TYPE));
  }

  @Test
  void testGetAttributeDescriptorForTypeBinary() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.TYPE_BINARY);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Version.TYPE_BINARY));
  }

  @Test
  void testGetAttributeDescriptorForVersionedBy() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.VERSIONED_BY);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Version.VERSIONED_BY));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testIdIsNotMultivalued() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.ID);

    assertThat(descriptor.isMultiValued(), is(false));
  }

  @Test
  void testActionIsMultivalued() {
    AttributeDescriptor descriptor = versionAttributes.getAttributeDescriptor(Version.ACTION);

    assertThat(descriptor.isMultiValued(), is(true));
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = versionAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
