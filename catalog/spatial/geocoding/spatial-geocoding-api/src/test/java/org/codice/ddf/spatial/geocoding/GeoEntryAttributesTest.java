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
package org.codice.ddf.spatial.geocoding;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link GeoEntryAttributes}. */
public class GeoEntryAttributesTest {

  // Previous tests removed - GeoEntryAttributes was refactored from a constants holder
  // to a MetacardType implementation. The old constants (GAZETTEER_TO_METACARD map,
  // FEATURE_CODE_KEY, LATITUDE_KEY, etc.) no longer exist.

  @Test
  public void testAttributeNameConstants() {
    // Test the actual constants that exist in the refactored class
    assertThat(GeoEntryAttributes.FEATURE_CODE_ATTRIBUTE_NAME, is("ext.feature-code"));
    assertThat(GeoEntryAttributes.FEATURE_CLASS_ATTRIBUTE_NAME, is("ext.feature-class"));
    assertThat(GeoEntryAttributes.POPULATION_ATTRIBUTE_NAME, is("ext.population"));
    assertThat(GeoEntryAttributes.IMPORT_LOCATION, is("ext.import-location"));
    assertThat(GeoEntryAttributes.GAZETTEER_SORT_VALUE, is("ext.gazetteer-sort-value"));
  }

  @Test
  public void testMetacardTypeImplementation() {
    GeoEntryAttributes attributes = new GeoEntryAttributes();
    assertThat(attributes.getName(), is(notNullValue()));
    assertThat(attributes.getAttributeDescriptors(), is(notNullValue()));
    assertThat(attributes.getAttributeDescriptors().isEmpty(), is(false));
  }
}
