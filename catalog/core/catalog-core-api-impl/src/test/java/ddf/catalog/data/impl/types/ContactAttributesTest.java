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
import ddf.catalog.data.types.Contact;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactAttributesTest {

  private ContactAttributes contactAttributes;

  @BeforeEach
  void setUp() {
    contactAttributes = new ContactAttributes();
  }

  @Test
  void testGetName() {
    assertThat(contactAttributes.getName(), is("contact"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsSixteenDescriptors() {
    Set<AttributeDescriptor> descriptors = contactAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(16));
  }

  @Test
  void testGetAttributeDescriptorForContributorAddress() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.CONTRIBUTOR_ADDRESS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.CONTRIBUTOR_ADDRESS));
  }

  @Test
  void testGetAttributeDescriptorForContributorEmail() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.CONTRIBUTOR_EMAIL);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.CONTRIBUTOR_EMAIL));
  }

  @Test
  void testGetAttributeDescriptorForContributorName() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.CONTRIBUTOR_NAME);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.CONTRIBUTOR_NAME));
  }

  @Test
  void testGetAttributeDescriptorForContributorPhone() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.CONTRIBUTOR_PHONE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.CONTRIBUTOR_PHONE));
  }

  @Test
  void testGetAttributeDescriptorForCreatorAddress() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.CREATOR_ADDRESS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.CREATOR_ADDRESS));
  }

  @Test
  void testGetAttributeDescriptorForCreatorEmail() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.CREATOR_EMAIL);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.CREATOR_EMAIL));
  }

  @Test
  void testGetAttributeDescriptorForCreatorName() {
    AttributeDescriptor descriptor = contactAttributes.getAttributeDescriptor(Contact.CREATOR_NAME);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.CREATOR_NAME));
  }

  @Test
  void testGetAttributeDescriptorForCreatorPhone() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.CREATOR_PHONE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.CREATOR_PHONE));
  }

  @Test
  void testGetAttributeDescriptorForPointOfContactAddress() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.POINT_OF_CONTACT_ADDRESS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.POINT_OF_CONTACT_ADDRESS));
  }

  @Test
  void testGetAttributeDescriptorForPointOfContactName() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.POINT_OF_CONTACT_NAME);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.POINT_OF_CONTACT_NAME));
  }

  @Test
  void testGetAttributeDescriptorForPublisherAddress() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.PUBLISHER_ADDRESS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.PUBLISHER_ADDRESS));
  }

  @Test
  void testGetAttributeDescriptorForPublisherName() {
    AttributeDescriptor descriptor =
        contactAttributes.getAttributeDescriptor(Contact.PUBLISHER_NAME);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Contact.PUBLISHER_NAME));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor = contactAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testAllAttributesAreMultivalued() {
    for (AttributeDescriptor descriptor : contactAttributes.getAttributeDescriptors()) {
      assertThat(
          descriptor.getName() + " should be multivalued", descriptor.isMultiValued(), is(true));
    }
  }

  @Test
  void testAllAttributesAreIndexed() {
    for (AttributeDescriptor descriptor : contactAttributes.getAttributeDescriptors()) {
      assertThat(descriptor.getName() + " should be indexed", descriptor.isIndexed(), is(true));
    }
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = contactAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
