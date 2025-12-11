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
package ddf.catalog.transformer.xml.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AdaptedListTest {

  @Test
  public void testAdaptedListWithStrings() {
    List<String> source = Arrays.asList("one", "two", "three");

    AdaptedList<String, String> adaptedList = new AdaptedList<>(source, String.class);

    assertThat(adaptedList.size(), is(3));
    assertThat(adaptedList.get(0), is("one"));
    assertThat(adaptedList.get(1), is("two"));
    assertThat(adaptedList.get(2), is("three"));
  }

  @Test
  public void testAdaptedListWithIntegers() {
    List<Integer> source = Arrays.asList(1, 2, 3, 4, 5);

    AdaptedList<Integer, Integer> adaptedList = new AdaptedList<>(source, Integer.class);

    assertThat(adaptedList.size(), is(5));
    assertThat(adaptedList.get(0), is(1));
    assertThat(adaptedList.get(4), is(5));
  }

  @Test
  public void testAdaptedListWithNullElements() {
    List<String> source = Arrays.asList("one", null, "three");

    AdaptedList<String, String> adaptedList = new AdaptedList<>(source, String.class);

    assertThat(adaptedList.size(), is(3));
    assertThat(adaptedList.get(0), is("one"));
    assertThat(adaptedList.get(1), is((String) null));
    assertThat(adaptedList.get(2), is("three"));
  }

  @Test
  public void testAdaptedListWithEmptySource() {
    List<String> source = Arrays.asList();

    AdaptedList<String, String> adaptedList = new AdaptedList<>(source, String.class);

    assertThat(adaptedList.size(), is(0));
    assertThat(adaptedList.isEmpty(), is(true));
  }

  @Test
  public void testAdaptedListWithSuperType() {
    List<Object> source = Arrays.asList("string1", "string2", "string3");

    AdaptedList<Object, String> adaptedList = new AdaptedList<>(source, String.class);

    assertThat(adaptedList.size(), is(3));
    assertThat(adaptedList.get(0), is("string1"));
  }

  @Test
  public void testAdaptedListWithMixedTypes() {
    List<Number> source = Arrays.asList(1, 2L, 3);

    AdaptedList<Number, Integer> adaptedList = new AdaptedList<>(source, Integer.class);

    // Only Integer types should be added (1 and 3, not 2L which is Long)
    assertThat(adaptedList.size(), is(2));
  }

  @Test
  public void testAdaptedListIsSerializable() {
    List<String> source = Arrays.asList("one", "two");

    AdaptedList<String, String> adaptedList = new AdaptedList<>(source, String.class);

    // AdaptedList extends LinkedList which implements Serializable
    assertThat(adaptedList instanceof java.io.Serializable, is(true));
  }
}
