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
package ddf.catalog.util.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DescribableImplTest {

  private TestDescribable describable;

  // Concrete implementation for testing the abstract class
  private static class TestDescribable extends DescribableImpl {}

  @BeforeEach
  void setUp() {
    describable = new TestDescribable();
  }

  @Test
  void testInitialValuesAreNull() {
    assertThat(describable.getVersion(), is(nullValue()));
    assertThat(describable.getId(), is(nullValue()));
    assertThat(describable.getTitle(), is(nullValue()));
    assertThat(describable.getDescription(), is(nullValue()));
    assertThat(describable.getOrganization(), is(nullValue()));
  }

  @Test
  void testSetAndGetVersion() {
    describable.setVersion("1.0.0");

    assertThat(describable.getVersion(), is("1.0.0"));
  }

  @Test
  void testSetAndGetId() {
    describable.setId("test-id");

    assertThat(describable.getId(), is("test-id"));
  }

  @Test
  void testSetAndGetTitle() {
    describable.setTitle("Test Title");

    assertThat(describable.getTitle(), is("Test Title"));
  }

  @Test
  void testSetAndGetDescription() {
    describable.setDescription("Test Description");

    assertThat(describable.getDescription(), is("Test Description"));
  }

  @Test
  void testSetAndGetOrganization() {
    describable.setOrganization("Test Organization");

    assertThat(describable.getOrganization(), is("Test Organization"));
  }

  @Test
  @SuppressWarnings("deprecation")
  void testSetShortnameUpdatesId() {
    describable.setShortname("shortname-id");

    assertThat(describable.getId(), is("shortname-id"));
  }

  @Test
  void testSetVersionWithNull() {
    describable.setVersion("1.0.0");
    describable.setVersion(null);

    assertThat(describable.getVersion(), is(nullValue()));
  }

  @Test
  void testSetIdWithNull() {
    describable.setId("some-id");
    describable.setId(null);

    assertThat(describable.getId(), is(nullValue()));
  }

  @Test
  void testSetTitleWithNull() {
    describable.setTitle("Some Title");
    describable.setTitle(null);

    assertThat(describable.getTitle(), is(nullValue()));
  }

  @Test
  void testSetDescriptionWithNull() {
    describable.setDescription("Some Description");
    describable.setDescription(null);

    assertThat(describable.getDescription(), is(nullValue()));
  }

  @Test
  void testSetOrganizationWithNull() {
    describable.setOrganization("Some Organization");
    describable.setOrganization(null);

    assertThat(describable.getOrganization(), is(nullValue()));
  }

  @Test
  void testSetAllProperties() {
    describable.setVersion("2.0.0");
    describable.setId("my-id");
    describable.setTitle("My Title");
    describable.setDescription("My Description");
    describable.setOrganization("My Organization");

    assertThat(describable.getVersion(), is("2.0.0"));
    assertThat(describable.getId(), is("my-id"));
    assertThat(describable.getTitle(), is("My Title"));
    assertThat(describable.getDescription(), is("My Description"));
    assertThat(describable.getOrganization(), is("My Organization"));
  }
}
