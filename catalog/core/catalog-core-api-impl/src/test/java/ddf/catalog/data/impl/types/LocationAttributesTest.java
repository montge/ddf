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
import ddf.catalog.data.types.Location;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationAttributesTest {

  private LocationAttributes locationAttributes;

  @BeforeEach
  void setUp() {
    locationAttributes = new LocationAttributes();
  }

  @Test
  void testGetName() {
    assertThat(locationAttributes.getName(), is("location"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsFourDescriptors() {
    Set<AttributeDescriptor> descriptors = locationAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(4));
  }

  @Test
  void testGetAttributeDescriptorForAltitude() {
    AttributeDescriptor descriptor = locationAttributes.getAttributeDescriptor(Location.ALTITUDE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Location.ALTITUDE));
  }

  @Test
  void testGetAttributeDescriptorForCountryCode() {
    AttributeDescriptor descriptor =
        locationAttributes.getAttributeDescriptor(Location.COUNTRY_CODE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Location.COUNTRY_CODE));
  }

  @Test
  void testGetAttributeDescriptorForCrsCode() {
    AttributeDescriptor descriptor =
        locationAttributes.getAttributeDescriptor(Location.COORDINATE_REFERENCE_SYSTEM_CODE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Location.COORDINATE_REFERENCE_SYSTEM_CODE));
  }

  @Test
  void testGetAttributeDescriptorForCrsName() {
    AttributeDescriptor descriptor =
        locationAttributes.getAttributeDescriptor(Location.COORDINATE_REFERENCE_SYSTEM_NAME);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Location.COORDINATE_REFERENCE_SYSTEM_NAME));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor = locationAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testAltitudeIsNotTokenized() {
    AttributeDescriptor descriptor = locationAttributes.getAttributeDescriptor(Location.ALTITUDE);

    assertThat(descriptor.isTokenized(), is(false));
  }

  @Test
  void testCountryCodeIsTokenized() {
    AttributeDescriptor descriptor =
        locationAttributes.getAttributeDescriptor(Location.COUNTRY_CODE);

    assertThat(descriptor.isTokenized(), is(true));
  }

  @Test
  void testAllAttributesAreMultivalued() {
    for (AttributeDescriptor descriptor : locationAttributes.getAttributeDescriptors()) {
      assertThat(
          descriptor.getName() + " should be multivalued", descriptor.isMultiValued(), is(true));
    }
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = locationAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
