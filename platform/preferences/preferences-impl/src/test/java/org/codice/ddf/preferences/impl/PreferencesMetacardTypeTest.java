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
package org.codice.ddf.preferences.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.AttributeDescriptor;
import org.junit.jupiter.api.Test;

class PreferencesMetacardTypeTest {

  @Test
  void testConstants() {
    assertThat(PreferencesMetacardType.NAME, is("ddf.preferences"));
    assertThat(PreferencesMetacardType.TAG, is("ddf-preferences"));
    assertThat(PreferencesMetacardType.USER_ATTRIBUTE, is("user"));
  }

  @Test
  void testDescriptorsNotEmpty() {
    assertThat(PreferencesMetacardType.DESCRIPTORS, is(notNullValue()));
    assertThat(PreferencesMetacardType.DESCRIPTORS, hasSize(1));
  }

  @Test
  void testUserAttributeDescriptor() {
    AttributeDescriptor userDescriptor = PreferencesMetacardType.DESCRIPTORS.get(0);

    assertThat(userDescriptor.getName(), is(PreferencesMetacardType.USER_ATTRIBUTE));
    assertThat(userDescriptor.isIndexed(), is(true));
    assertThat(userDescriptor.isStored(), is(true));
    assertThat(userDescriptor.isTokenized(), is(false));
    assertThat(userDescriptor.isMultiValued(), is(false));
  }

  @Test
  void testConstructor() {
    PreferencesMetacardType metacardType = new PreferencesMetacardType();

    assertThat(metacardType.getName(), is(PreferencesMetacardType.NAME));
    assertThat(metacardType.getAttributeDescriptors(), is(notNullValue()));
    assertThat(
        metacardType.getAttributeDescriptor(PreferencesMetacardType.USER_ATTRIBUTE),
        is(notNullValue()));
  }

  @Test
  void testMetacardTypeHasCoreAttributes() {
    PreferencesMetacardType metacardType = new PreferencesMetacardType();

    assertThat(metacardType.getAttributeDescriptor("id"), is(notNullValue()));
    assertThat(metacardType.getAttributeDescriptor("title"), is(notNullValue()));
  }
}
