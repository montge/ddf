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
package ddf.catalog.data.defaultvalues;

import static ddf.catalog.data.Metacard.POINT_OF_CONTACT;
import static ddf.catalog.data.Metacard.TITLE;
import static ddf.catalog.data.impl.MetacardImpl.BASIC_METACARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ddf.catalog.data.DefaultAttributeValueRegistry;
import java.io.Serializable;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultAttributeValueRegistryImplTest {
  private static final String BASIC_METACARD_NAME = BASIC_METACARD.getName();

  private static final String OTHER_METACARD_NAME = "other";

  private static final String DEFAULT_1 = "foo";

  private static final String DEFAULT_2 = "bar";

  private static final String DEFAULT_3 = "foobar";

  private DefaultAttributeValueRegistry registry;

  @BeforeEach
  public void setUp() {
    registry = new DefaultAttributeValueRegistryImpl();
  }

  @Test
  public void testSetDefaultValue() {
    registry.setDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_1);
    verifyRegistryDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_1);
  }

  @Test
  public void testSetDefaultValuesForDifferentMetacardTypes() {
    registry.setDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_1);
    registry.setDefaultValue(OTHER_METACARD_NAME, TITLE, DEFAULT_2);

    verifyRegistryDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_1);
    verifyRegistryDefaultValue(OTHER_METACARD_NAME, TITLE, DEFAULT_2);
  }

  @Test
  public void testOverwriteDefaultValue() {
    registry.setDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_1);
    registry.setDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_2);
    verifyRegistryDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_2);
  }

  @Test
  public void testGetUnregisteredMetacardType() {
    verifyRegistryDefaultValueNotPresent(BASIC_METACARD_NAME, TITLE);
  }

  @Test
  public void testGetUnregisteredAttribute() {
    registry.setDefaultValue(BASIC_METACARD_NAME, POINT_OF_CONTACT, DEFAULT_1);
    verifyRegistryDefaultValueNotPresent(BASIC_METACARD_NAME, TITLE);
  }

  @Test
  public void testRemoveSingleDefaultValue() {
    registry.setDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_1);
    registry.setDefaultValue(BASIC_METACARD_NAME, POINT_OF_CONTACT, DEFAULT_2);

    registry.removeDefaultValue(BASIC_METACARD_NAME, TITLE);

    verifyRegistryDefaultValue(BASIC_METACARD_NAME, POINT_OF_CONTACT, DEFAULT_2);
    verifyRegistryDefaultValueNotPresent(BASIC_METACARD_NAME, TITLE);
  }

  @Test
  public void testRemoveAllDefaultValuesForMetacardType() {
    registry.setDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_1);
    registry.setDefaultValue(BASIC_METACARD_NAME, POINT_OF_CONTACT, DEFAULT_2);
    registry.setDefaultValue(OTHER_METACARD_NAME, TITLE, DEFAULT_3);

    registry.removeDefaultValues(BASIC_METACARD_NAME);

    verifyRegistryDefaultValueNotPresent(BASIC_METACARD_NAME, TITLE);
    verifyRegistryDefaultValueNotPresent(BASIC_METACARD_NAME, POINT_OF_CONTACT);
    verifyRegistryDefaultValue(OTHER_METACARD_NAME, TITLE, DEFAULT_3);
  }

  @Test
  public void testGlobalDefaults() {
    registry.setDefaultValue(TITLE, DEFAULT_1);
    registry.setDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_3);
    registry.setDefaultValue(POINT_OF_CONTACT, DEFAULT_2);

    verifyRegistryDefaultValue(BASIC_METACARD_NAME, TITLE, DEFAULT_3);
    verifyRegistryDefaultValue(OTHER_METACARD_NAME, TITLE, DEFAULT_1);
    verifyRegistryDefaultValue(BASIC_METACARD_NAME, POINT_OF_CONTACT, DEFAULT_2);
    verifyRegistryDefaultValue(OTHER_METACARD_NAME, POINT_OF_CONTACT, DEFAULT_2);
  }

  @Test
  public void testRemoveGlobalDefault() {
    registry.setDefaultValue(TITLE, DEFAULT_1);
    registry.setDefaultValue(POINT_OF_CONTACT, DEFAULT_2);

    registry.removeDefaultValue(TITLE);

    verifyRegistryDefaultValue(BASIC_METACARD_NAME, POINT_OF_CONTACT, DEFAULT_2);
    verifyRegistryDefaultValueNotPresent(BASIC_METACARD_NAME, TITLE);
  }

  @Test
  public void testRemoveAllGlobalDefaults() {
    registry.setDefaultValue(TITLE, DEFAULT_1);
    registry.setDefaultValue(POINT_OF_CONTACT, DEFAULT_2);

    registry.removeDefaultValues();

    verifyRegistryDefaultValueNotPresent(BASIC_METACARD_NAME, TITLE);
    verifyRegistryDefaultValueNotPresent(BASIC_METACARD_NAME, POINT_OF_CONTACT);
  }

  private void verifyRegistryDefaultValue(
      String metacardTypeName, String attributeName, String expectedValue) {
    final Optional<Serializable> defaultValueOptional =
        registry.getDefaultValue(metacardTypeName, attributeName);
    assertThat(defaultValueOptional.isPresent(), is(true));
    assertThat(defaultValueOptional.get(), is(expectedValue));
  }

  private void verifyRegistryDefaultValueNotPresent(String metacardTypeName, String attributeName) {
    final Optional<Serializable> defaultValueOptional =
        registry.getDefaultValue(metacardTypeName, attributeName);
    assertThat(defaultValueOptional.isPresent(), is(false));
  }

  @Test
  public void testRegisterGlobalDefaultNullAttribute() {
    assertThrows(IllegalArgumentException.class, () -> registry.setDefaultValue(null, DEFAULT_1));
  }

  @Test
  public void testRegisterGlobalDefaultNullValue() {
    assertThrows(IllegalArgumentException.class, () -> registry.setDefaultValue(TITLE, null));
  }

  @Test
  public void testRegisterMetacardDefaultNullMetacardType() {
    assertThrows(
        IllegalArgumentException.class, () -> registry.setDefaultValue(null, TITLE, DEFAULT_1));
  }

  @Test
  public void testRegisterMetacardDefaultNullAttribute() {
    assertThrows(
        IllegalArgumentException.class,
        () -> registry.setDefaultValue(BASIC_METACARD_NAME, null, DEFAULT_1));
  }

  @Test
  public void testRegisterMetacardDefaultNullValue() {
    assertThrows(
        IllegalArgumentException.class,
        () -> registry.setDefaultValue(BASIC_METACARD_NAME, TITLE, null));
  }

  @Test
  public void testGetDefaultValueNullMetacardType() {
    assertThrows(IllegalArgumentException.class, () -> registry.getDefaultValue(null, TITLE));
  }

  @Test
  public void testGetDefaultValueNullAttribute() {
    assertThrows(
        IllegalArgumentException.class, () -> registry.getDefaultValue(BASIC_METACARD_NAME, null));
  }

  @Test
  public void testRemoveGlobalDefaultValueNullAttribute() {
    assertThrows(IllegalArgumentException.class, () -> registry.removeDefaultValue(null));
  }

  @Test
  public void testRemoveMetacardDefaultNullMetacardType() {
    assertThrows(IllegalArgumentException.class, () -> registry.removeDefaultValue(null, TITLE));
  }

  @Test
  public void testRemoveMetacardDefaultNullAttribute() {
    assertThrows(
        IllegalArgumentException.class,
        () -> registry.removeDefaultValue(BASIC_METACARD_NAME, null));
  }

  @Test
  public void testRemoveAllMetacardDefaultsNullMetacardType() {
    assertThrows(IllegalArgumentException.class, () -> registry.removeDefaultValues(null));
  }
}
