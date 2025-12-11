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

/** Unit tests for Suggestion interface and its implementation. */
public class SuggestionTest {

  @Test
  public void testSuggestionImplementation() {
    Suggestion suggestion = createTestSuggestion("12345", "Washington, D.C.");

    assertThat(suggestion, is(notNullValue()));
    assertThat(suggestion.getId(), is("12345"));
    assertThat(suggestion.getName(), is("Washington, D.C."));
  }

  @Test
  public void testSuggestionWithNullId() {
    Suggestion suggestion = createTestSuggestion(null, "Paris");

    assertThat(suggestion, is(notNullValue()));
    assertThat(suggestion.getId(), is((String) null));
    assertThat(suggestion.getName(), is("Paris"));
  }

  @Test
  public void testSuggestionWithNullName() {
    Suggestion suggestion = createTestSuggestion("67890", null);

    assertThat(suggestion, is(notNullValue()));
    assertThat(suggestion.getId(), is("67890"));
    assertThat(suggestion.getName(), is((String) null));
  }

  @Test
  public void testSuggestionWithEmptyValues() {
    Suggestion suggestion = createTestSuggestion("", "");

    assertThat(suggestion, is(notNullValue()));
    assertThat(suggestion.getId(), is(""));
    assertThat(suggestion.getName(), is(""));
  }

  @Test
  public void testSuggestionWithSpecialCharacters() {
    String specialId = "id-123_456";
    String specialName = "São Paulo, Brazil";

    Suggestion suggestion = createTestSuggestion(specialId, specialName);

    assertThat(suggestion, is(notNullValue()));
    assertThat(suggestion.getId(), is(specialId));
    assertThat(suggestion.getName(), is(specialName));
  }

  @Test
  public void testMultipleSuggestionsIndependence() {
    Suggestion suggestion1 = createTestSuggestion("1", "London");
    Suggestion suggestion2 = createTestSuggestion("2", "Tokyo");

    assertThat(suggestion1.getId(), is("1"));
    assertThat(suggestion1.getName(), is("London"));
    assertThat(suggestion2.getId(), is("2"));
    assertThat(suggestion2.getName(), is("Tokyo"));
  }

  private Suggestion createTestSuggestion(final String id, final String name) {
    return new Suggestion() {
      @Override
      public String getId() {
        return id;
      }

      @Override
      public String getName() {
        return name;
      }
    };
  }
}
