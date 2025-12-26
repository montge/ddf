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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.operation.ResultAttributeHighlight;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResultHighlightImplTest {

  @Test
  void testConstructorWithResultIdOnly() {
    ResultHighlightImpl result = new ResultHighlightImpl("result-123");

    assertThat(result.getResultId(), is("result-123"));
    assertThat(result.getAttributeHighlights(), is(empty()));
  }

  @Test
  void testConstructorWithResultIdAndHighlights() {
    List<ResultAttributeHighlight> highlights = new ArrayList<>();
    highlights.add(mock(ResultAttributeHighlight.class));
    highlights.add(mock(ResultAttributeHighlight.class));

    ResultHighlightImpl result = new ResultHighlightImpl("result-456", highlights);

    assertThat(result.getResultId(), is("result-456"));
    assertThat(result.getAttributeHighlights(), hasSize(2));
  }

  @Test
  void testGetResultIdReturnsCorrectValue() {
    ResultHighlightImpl result = new ResultHighlightImpl("my-result-id");

    assertThat(result.getResultId(), is("my-result-id"));
  }

  @Test
  void testGetAttributeHighlightsReturnsUnmodifiableList() {
    List<ResultAttributeHighlight> highlights = new ArrayList<>();
    highlights.add(mock(ResultAttributeHighlight.class));

    ResultHighlightImpl result = new ResultHighlightImpl("result", highlights);

    List<ResultAttributeHighlight> returnedList = result.getAttributeHighlights();

    assertThrows(
        UnsupportedOperationException.class,
        () -> returnedList.add(mock(ResultAttributeHighlight.class)));
  }

  @Test
  void testConstructorWithEmptyHighlightsList() {
    List<ResultAttributeHighlight> emptyHighlights = new ArrayList<>();

    ResultHighlightImpl result = new ResultHighlightImpl("result", emptyHighlights);

    assertThat(result.getAttributeHighlights(), is(empty()));
  }

  @Test
  void testConstructorWithNullResultId() {
    ResultHighlightImpl result = new ResultHighlightImpl(null);

    assertThat(result.getResultId(), is((String) null));
  }

  @Test
  void testOriginalListModificationDoesNotAffectResult() {
    List<ResultAttributeHighlight> highlights = new ArrayList<>();
    ResultAttributeHighlight highlight1 = mock(ResultAttributeHighlight.class);
    highlights.add(highlight1);

    ResultHighlightImpl result = new ResultHighlightImpl("result", highlights);

    // Modify the original list
    highlights.add(mock(ResultAttributeHighlight.class));

    // Result should only have the original highlight
    assertThat(result.getAttributeHighlights(), hasSize(1));
  }
}
