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

import org.junit.Test;

/** Unit tests for {@link GeoCodingConstants}. */
public class GeoCodingConstantsTest {

  @Test
  public void testSuggestionContextKey() {
    assertThat(GeoCodingConstants.SUGGESTION_CONTEXT_KEY, is(notNullValue()));
    assertThat(GeoCodingConstants.SUGGESTION_CONTEXT_KEY, is("suggestion-context"));
  }

  @Test
  public void testSuggestionQueryKey() {
    assertThat(GeoCodingConstants.SUGGESTION_QUERY_KEY, is(notNullValue()));
    assertThat(GeoCodingConstants.SUGGESTION_QUERY_KEY, is("q"));
  }

  @Test
  public void testSuggestionCountKey() {
    assertThat(GeoCodingConstants.SUGGESTION_COUNT_KEY, is(notNullValue()));
    assertThat(GeoCodingConstants.SUGGESTION_COUNT_KEY, is("count"));
  }
}
