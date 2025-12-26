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
import ddf.catalog.data.types.DateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateTimeAttributesTest {

  private DateTimeAttributes dateTimeAttributes;

  @BeforeEach
  void setUp() {
    dateTimeAttributes = new DateTimeAttributes();
  }

  @Test
  void testGetName() {
    assertThat(dateTimeAttributes.getName(), is("datetime"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsThreeDescriptors() {
    Set<AttributeDescriptor> descriptors = dateTimeAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(3));
  }

  @Test
  void testGetAttributeDescriptorsContainsStart() {
    AttributeDescriptor descriptor = dateTimeAttributes.getAttributeDescriptor(DateTime.START);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(DateTime.START));
  }

  @Test
  void testGetAttributeDescriptorsContainsEnd() {
    AttributeDescriptor descriptor = dateTimeAttributes.getAttributeDescriptor(DateTime.END);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(DateTime.END));
  }

  @Test
  void testGetAttributeDescriptorsContainsName() {
    AttributeDescriptor descriptor = dateTimeAttributes.getAttributeDescriptor(DateTime.NAME);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(DateTime.NAME));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor = dateTimeAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testGetAttributeDescriptorWithNullNameReturnsNull() {
    AttributeDescriptor descriptor = dateTimeAttributes.getAttributeDescriptor(null);

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testStartAttributeIsIndexed() {
    AttributeDescriptor descriptor = dateTimeAttributes.getAttributeDescriptor(DateTime.START);

    assertThat(descriptor.isIndexed(), is(true));
  }

  @Test
  void testStartAttributeIsStored() {
    AttributeDescriptor descriptor = dateTimeAttributes.getAttributeDescriptor(DateTime.START);

    assertThat(descriptor.isStored(), is(true));
  }

  @Test
  void testStartAttributeIsMultivalued() {
    AttributeDescriptor descriptor = dateTimeAttributes.getAttributeDescriptor(DateTime.START);

    assertThat(descriptor.isMultiValued(), is(true));
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = dateTimeAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
