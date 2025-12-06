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
package org.codice.ddf.opensearch.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for the ContextualSearch class */
@RunWith(MockitoJUnitRunner.class)
public class ContextualSearchTest {

  @Test
  public void testConstructor() {
    Map<String, String> searchPhraseMap = new HashMap<>();
    searchPhraseMap.put("q", "test search");

    ContextualSearch contextualSearch =
        new ContextualSearch("/xpath/selector", searchPhraseMap, true);

    assertThat(contextualSearch, notNullValue());
    assertThat(contextualSearch.getSelectors(), is("/xpath/selector"));
    assertThat(contextualSearch.getSearchPhraseMap(), is(searchPhraseMap));
    assertThat(contextualSearch.isCaseSensitive(), is(true));
  }

  @Test
  public void testConstructorCaseInsensitive() {
    Map<String, String> searchPhraseMap = new HashMap<>();
    searchPhraseMap.put("q", "case insensitive search");

    ContextualSearch contextualSearch = new ContextualSearch(null, searchPhraseMap, false);

    assertThat(contextualSearch.getSelectors(), nullValue());
    assertThat(contextualSearch.isCaseSensitive(), is(false));
  }

  @Test
  public void testSetSearchPhraseMap() {
    Map<String, String> initialMap = new HashMap<>();
    initialMap.put("q", "initial");

    ContextualSearch contextualSearch = new ContextualSearch(null, initialMap, false);

    Map<String, String> newMap = new HashMap<>();
    newMap.put("q", "updated");
    newMap.put("title", "test title");

    contextualSearch.setSearchPhraseMap(newMap);

    assertThat(contextualSearch.getSearchPhraseMap(), is(newMap));
    assertThat(contextualSearch.getSearchPhraseMap(), hasEntry("q", "updated"));
    assertThat(contextualSearch.getSearchPhraseMap(), hasEntry("title", "test title"));
  }

  @Test
  public void testSetSelectors() {
    ContextualSearch contextualSearch = new ContextualSearch(null, new HashMap<>(), false);

    contextualSearch.setSelectors("/new/xpath");

    assertThat(contextualSearch.getSelectors(), is("/new/xpath"));
  }

  @Test
  public void testSetCaseSensitive() {
    ContextualSearch contextualSearch = new ContextualSearch(null, new HashMap<>(), false);

    assertThat(contextualSearch.isCaseSensitive(), is(false));

    contextualSearch.setCaseSensitive(true);

    assertThat(contextualSearch.isCaseSensitive(), is(true));
  }

  @Test
  public void testMultipleSearchPhrases() {
    Map<String, String> searchPhraseMap = new HashMap<>();
    searchPhraseMap.put("q", "keyword search");
    searchPhraseMap.put("title", "document title");
    searchPhraseMap.put("description", "document description");

    ContextualSearch contextualSearch = new ContextualSearch("/xpath", searchPhraseMap, true);

    assertThat(contextualSearch.getSearchPhraseMap().size(), is(3));
    assertThat(contextualSearch.getSearchPhraseMap(), hasEntry("q", "keyword search"));
    assertThat(contextualSearch.getSearchPhraseMap(), hasEntry("title", "document title"));
    assertThat(
        contextualSearch.getSearchPhraseMap(), hasEntry("description", "document description"));
  }

  @Test
  public void testEmptySearchPhraseMap() {
    Map<String, String> emptyMap = new HashMap<>();

    ContextualSearch contextualSearch = new ContextualSearch(null, emptyMap, false);

    assertThat(contextualSearch.getSearchPhraseMap(), notNullValue());
    assertThat(contextualSearch.getSearchPhraseMap().isEmpty(), is(true));
  }

  @Test
  public void testSearchPhraseMapModification() {
    Map<String, String> searchPhraseMap = new HashMap<>();
    searchPhraseMap.put("q", "original");

    ContextualSearch contextualSearch = new ContextualSearch(null, searchPhraseMap, false);

    // Modify the original map
    searchPhraseMap.put("q", "modified");

    // The contextual search should reflect the modification (same reference)
    assertThat(contextualSearch.getSearchPhraseMap().get("q"), equalTo("modified"));
  }
}
