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

/** Unit tests for {@link GeoCodingConstants}. */
public class GeoCodingConstantsTest {

  // Tests removed - the constants SUGGESTION_CONTEXT_KEY, SUGGESTION_QUERY_KEY,
  // and SUGGESTION_COUNT_KEY no longer exist in GeoCodingConstants.
  // The class now only contains feature codes and classification constants.

  @Test
  public void testBasicConstants() {
    // Verify some of the existing constants are present
    assertThat(GeoCodingConstants.GAZETTEER_METACARD_TAG, is(notNullValue()));
    assertThat(GeoCodingConstants.GAZETTEER_METACARD_TAG, is("gazetteer"));
  }
}
