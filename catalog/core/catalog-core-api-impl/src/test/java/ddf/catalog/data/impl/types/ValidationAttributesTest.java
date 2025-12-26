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
import ddf.catalog.data.types.Validation;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidationAttributesTest {

  private ValidationAttributes validationAttributes;

  @BeforeEach
  void setUp() {
    validationAttributes = new ValidationAttributes();
  }

  @Test
  void testGetName() {
    assertThat(validationAttributes.getName(), is("validation"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsFourDescriptors() {
    Set<AttributeDescriptor> descriptors = validationAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(4));
  }

  @Test
  void testGetAttributeDescriptorForValidationErrors() {
    AttributeDescriptor descriptor =
        validationAttributes.getAttributeDescriptor(Validation.VALIDATION_ERRORS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Validation.VALIDATION_ERRORS));
  }

  @Test
  void testGetAttributeDescriptorForValidationWarnings() {
    AttributeDescriptor descriptor =
        validationAttributes.getAttributeDescriptor(Validation.VALIDATION_WARNINGS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Validation.VALIDATION_WARNINGS));
  }

  @Test
  void testGetAttributeDescriptorForFailedValidatorsErrors() {
    AttributeDescriptor descriptor =
        validationAttributes.getAttributeDescriptor(Validation.FAILED_VALIDATORS_ERRORS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Validation.FAILED_VALIDATORS_ERRORS));
  }

  @Test
  void testGetAttributeDescriptorForFailedValidatorsWarnings() {
    AttributeDescriptor descriptor =
        validationAttributes.getAttributeDescriptor(Validation.FAILED_VALIDATORS_WARNINGS);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Validation.FAILED_VALIDATORS_WARNINGS));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor =
        validationAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testAllAttributesAreMultivalued() {
    for (AttributeDescriptor descriptor : validationAttributes.getAttributeDescriptors()) {
      assertThat(
          descriptor.getName() + " should be multivalued", descriptor.isMultiValued(), is(true));
    }
  }

  @Test
  void testAllAttributesAreTokenized() {
    for (AttributeDescriptor descriptor : validationAttributes.getAttributeDescriptors()) {
      assertThat(descriptor.getName() + " should be tokenized", descriptor.isTokenized(), is(true));
    }
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = validationAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
