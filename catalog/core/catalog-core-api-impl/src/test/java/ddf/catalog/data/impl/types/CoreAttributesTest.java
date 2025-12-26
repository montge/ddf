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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.types.Core;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoreAttributesTest {

  private CoreAttributes coreAttributes;

  @BeforeEach
  void setUp() {
    coreAttributes = new CoreAttributes();
  }

  @Test
  void testImplementsMetacardType() {
    assertThat(coreAttributes, instanceOf(MetacardType.class));
  }

  @Test
  void testImplementsCore() {
    assertThat(coreAttributes, instanceOf(Core.class));
  }

  @Test
  void testGetName() {
    assertThat(coreAttributes.getName(), is("core"));
  }

  @Test
  void testGetAttributeDescriptorsNotEmpty() {
    Set<AttributeDescriptor> descriptors = coreAttributes.getAttributeDescriptors();

    assertThat(descriptors, is(notNullValue()));
    assertThat(descriptors.size(), greaterThan(0));
  }

  @Test
  void testGetAttributeDescriptorForId() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.ID);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.ID));
  }

  @Test
  void testGetAttributeDescriptorForTitle() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.TITLE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.TITLE));
  }

  @Test
  void testGetAttributeDescriptorForCreated() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.CREATED);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.CREATED));
  }

  @Test
  void testGetAttributeDescriptorForModified() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.MODIFIED);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.MODIFIED));
  }

  @Test
  void testGetAttributeDescriptorForLocation() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.LOCATION);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.LOCATION));
  }

  @Test
  void testGetAttributeDescriptorForMetadata() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.METADATA);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.METADATA));
  }

  @Test
  void testGetAttributeDescriptorForThumbnail() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.THUMBNAIL);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.THUMBNAIL));
  }

  @Test
  void testGetAttributeDescriptorForResourceUri() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.RESOURCE_URI);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.RESOURCE_URI));
  }

  @Test
  void testGetAttributeDescriptorForDescription() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.DESCRIPTION);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.DESCRIPTION));
  }

  @Test
  void testGetAttributeDescriptorForChecksum() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.CHECKSUM);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.CHECKSUM));
  }

  @Test
  void testGetAttributeDescriptorForMetacardTags() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.METACARD_TAGS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Core.METACARD_TAGS));
    assertThat(descriptor.isMultiValued(), is(true));
  }

  @Test
  void testGetAttributeDescriptorForNonExistent() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor("nonexistent.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = coreAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }

  @Test
  void testContainsLanguageAttribute() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.LANGUAGE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.isMultiValued(), is(true));
  }

  @Test
  void testContainsDatatype() {
    AttributeDescriptor descriptor = coreAttributes.getAttributeDescriptor(Core.DATATYPE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.isMultiValued(), is(true));
  }
}
