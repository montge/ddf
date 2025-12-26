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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.operation.Highlight;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResultAttributeHighlightImplTest {

  @Test
  void testConstructorWithAttributeNameOnly() {
    ResultAttributeHighlightImpl highlight = new ResultAttributeHighlightImpl("title");

    assertThat(highlight.getAttributeName(), is("title"));
    assertThat(highlight.getHighlights(), is(empty()));
  }

  @Test
  void testConstructorWithAttributeNameAndHighlights() {
    List<Highlight> highlights = new ArrayList<>();
    highlights.add(mock(Highlight.class));
    highlights.add(mock(Highlight.class));

    ResultAttributeHighlightImpl result =
        new ResultAttributeHighlightImpl("description", highlights);

    assertThat(result.getAttributeName(), is("description"));
    assertThat(result.getHighlights(), hasSize(2));
  }

  @Test
  void testGetAttributeNameReturnsCorrectValue() {
    ResultAttributeHighlightImpl highlight = new ResultAttributeHighlightImpl("content");

    assertThat(highlight.getAttributeName(), is("content"));
  }

  @Test
  void testGetHighlightsReturnsUnmodifiableList() {
    List<Highlight> highlights = new ArrayList<>();
    highlights.add(mock(Highlight.class));

    ResultAttributeHighlightImpl result = new ResultAttributeHighlightImpl("attr", highlights);

    List<Highlight> returnedList = result.getHighlights();

    assertThrows(
        UnsupportedOperationException.class, () -> returnedList.add(mock(Highlight.class)));
  }

  @Test
  void testConstructorWithEmptyHighlightsList() {
    List<Highlight> emptyHighlights = new ArrayList<>();

    ResultAttributeHighlightImpl result = new ResultAttributeHighlightImpl("attr", emptyHighlights);

    assertThat(result.getHighlights(), is(empty()));
  }

  @Test
  void testConstructorWithNullAttributeName() {
    ResultAttributeHighlightImpl highlight = new ResultAttributeHighlightImpl(null);

    assertThat(highlight.getAttributeName(), is(nullValue()));
    assertThat(highlight.getHighlights(), is(empty()));
  }

  @Test
  void testOriginalListModificationDoesNotAffectResult() {
    List<Highlight> highlights = new ArrayList<>();
    Highlight highlight1 = mock(Highlight.class);
    highlights.add(highlight1);

    ResultAttributeHighlightImpl result = new ResultAttributeHighlightImpl("attr", highlights);

    // Modify the original list
    highlights.add(mock(Highlight.class));

    // Result should only have the original highlight
    assertThat(result.getHighlights(), hasSize(1));
  }

  @Test
  void testMultipleHighlights() {
    List<Highlight> highlights = new ArrayList<>();
    Highlight h1 = mock(Highlight.class);
    Highlight h2 = mock(Highlight.class);
    Highlight h3 = mock(Highlight.class);
    highlights.add(h1);
    highlights.add(h2);
    highlights.add(h3);

    ResultAttributeHighlightImpl result =
        new ResultAttributeHighlightImpl("searchTerm", highlights);

    assertThat(result.getHighlights(), hasSize(3));
    assertThat(result.getAttributeName(), is("searchTerm"));
  }
}
