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
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HighlightImplTest {

  @Test
  void testDefaultConstructor() {
    HighlightImpl highlight = new HighlightImpl();

    assertThat(highlight.getValueIndex(), is(0));
    assertThat(highlight.getBeginIndex(), is(0));
    assertThat(highlight.getEndIndex(), is(0));
  }

  @Test
  void testConstructorWithBeginAndEndIndex() {
    HighlightImpl highlight = new HighlightImpl(5, 10);

    assertThat(highlight.getBeginIndex(), is(5));
    assertThat(highlight.getEndIndex(), is(10));
    assertThat(highlight.getValueIndex(), is(0));
  }

  @Test
  void testConstructorWithAllParameters() {
    HighlightImpl highlight = new HighlightImpl(3, 7, 2);

    assertThat(highlight.getBeginIndex(), is(3));
    assertThat(highlight.getEndIndex(), is(7));
    assertThat(highlight.getValueIndex(), is(2));
  }

  @Test
  void testSetValueIndex() {
    HighlightImpl highlight = new HighlightImpl();
    highlight.setValueIndex(5);

    assertThat(highlight.getValueIndex(), is(5));
  }

  @Test
  void testSetBeginIndex() {
    HighlightImpl highlight = new HighlightImpl();
    highlight.setBeginIndex(10);

    assertThat(highlight.getBeginIndex(), is(10));
  }

  @Test
  void testSetEndIndex() {
    HighlightImpl highlight = new HighlightImpl();
    highlight.setEndIndex(20);

    assertThat(highlight.getEndIndex(), is(20));
  }

  @Test
  void testApplyToString() {
    HighlightImpl highlight = new HighlightImpl(5, 10);
    String input = "Hello World Test";

    String result = highlight.apply(input, "<b>", "</b>");

    assertThat(result, is("Hello<b> Worl</b>d Test"));
  }

  @Test
  void testApplyToStringAtBeginning() {
    HighlightImpl highlight = new HighlightImpl(0, 5);
    String input = "Hello World";

    String result = highlight.apply(input, "[", "]");

    assertThat(result, is("[Hello] World"));
  }

  @Test
  void testApplyToStringAtEnd() {
    HighlightImpl highlight = new HighlightImpl(6, 11);
    String input = "Hello World";

    String result = highlight.apply(input, "<em>", "</em>");

    assertThat(result, is("Hello <em>World</em>"));
  }

  @Test
  void testApplyToListModifiesCorrectValue() {
    HighlightImpl highlight = new HighlightImpl(0, 5, 1);
    List<String> values = new ArrayList<>();
    values.add("first");
    values.add("second");
    values.add("third");

    highlight.apply(values, "<b>", "</b>");

    assertThat(values.get(0), is("first"));
    assertThat(values.get(1), is("<b>secon</b>d"));
    assertThat(values.get(2), is("third"));
  }

  @Test
  void testApplyToListWithIndexOutOfBounds() {
    HighlightImpl highlight = new HighlightImpl(0, 5, 10);
    List<String> values = new ArrayList<>();
    values.add("only");

    // Should not modify anything if valueIndex is out of bounds
    highlight.apply(values, "<b>", "</b>");

    assertThat(values.get(0), is("only"));
    assertThat(values.size(), is(1));
  }

  @Test
  void testApplyToListWithEmptyList() {
    HighlightImpl highlight = new HighlightImpl(0, 5, 0);
    List<String> values = new ArrayList<>();

    // Should not throw exception with empty list
    highlight.apply(values, "<b>", "</b>");

    assertThat(values.isEmpty(), is(true));
  }
}
