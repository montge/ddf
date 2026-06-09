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
package org.codice.ddf.platform.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@ExtendWith(MockitoExtension.class)
public class SortedServiceListTest {

  private SortedServiceList<String> sortedServiceList;

  @BeforeEach
  public void setUp() {
    sortedServiceList = new SortedServiceList<>();
  }

  @Test
  public void testConstructor() {
    assertThat(sortedServiceList, notNullValue());
  }

  @Test
  public void testIsEmptyWhenNew() {
    assertThat(sortedServiceList.isEmpty(), is(true));
  }

  @Test
  public void testSizeWhenEmpty() {
    assertThat(sortedServiceList.size(), is(0));
  }

  @Test
  public void testContainsWhenEmpty() {
    assertThat(sortedServiceList.contains("test"), is(false));
  }

  @Test
  public void testIndexOfWhenEmpty() {
    assertThat(sortedServiceList.indexOf("test"), is(-1));
  }

  @Test
  public void testLastIndexOfWhenEmpty() {
    assertThat(sortedServiceList.lastIndexOf("test"), is(-1));
  }

  @Test
  public void testToArrayWhenEmpty() {
    Object[] array = sortedServiceList.toArray();
    assertThat(array, notNullValue());
    assertThat(array.length, is(0));
  }

  @Test
  public void testToArrayWithTypeWhenEmpty() {
    String[] array = sortedServiceList.toArray(new String[0]);
    assertThat(array, notNullValue());
    assertThat(array.length, is(0));
  }

  @Test
  public void testIteratorWhenEmpty() {
    Iterator<String> iterator = sortedServiceList.iterator();
    assertThat(iterator, notNullValue());
    assertThat(iterator.hasNext(), is(false));
  }

  @Test
  public void testListIteratorWhenEmpty() {
    ListIterator<String> iterator = sortedServiceList.listIterator();
    assertThat(iterator, notNullValue());
    assertThat(iterator.hasNext(), is(false));
  }

  @Test
  public void testListIteratorWithIndexWhenEmpty() {
    ListIterator<String> iterator = sortedServiceList.listIterator(0);
    assertThat(iterator, notNullValue());
    assertThat(iterator.hasNext(), is(false));
  }

  @Test
  public void testSubListWhenEmpty() {
    List<String> subList = sortedServiceList.subList(0, 0);
    assertThat(subList, notNullValue());
    assertThat(subList.isEmpty(), is(true));
  }

  @Test
  public void testGetWhenEmpty() {
    assertThrows(IndexOutOfBoundsException.class, () -> sortedServiceList.get(0));
  }

  @Test
  public void testContainsAllWhenEmpty() {
    List<String> list = Arrays.asList("test1", "test2");
    assertThat(sortedServiceList.containsAll(list), is(false));
  }

  @Test
  public void testContainsAllWithEmptyList() {
    List<String> emptyList = new ArrayList<>();
    assertThat(sortedServiceList.containsAll(emptyList), is(true));
  }

  @Test
  public void testAddThrowsException() {
    assertThrows(UnsupportedOperationException.class, () -> sortedServiceList.add("test"));
  }

  @Test
  public void testAddWithIndexThrowsException() {
    assertThrows(UnsupportedOperationException.class, () -> sortedServiceList.add(0, "test"));
  }

  @Test
  public void testAddAllThrowsException() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> sortedServiceList.addAll(Arrays.asList("test1", "test2")));
  }

  @Test
  public void testAddAllWithIndexThrowsException() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> sortedServiceList.addAll(0, Arrays.asList("test1", "test2")));
  }

  @Test
  public void testClearThrowsException() {
    assertThrows(UnsupportedOperationException.class, () -> sortedServiceList.clear());
  }

  @Test
  public void testRemoveObjectThrowsException() {
    assertThrows(UnsupportedOperationException.class, () -> sortedServiceList.remove("test"));
  }

  @Test
  public void testRemoveIndexThrowsException() {
    assertThrows(UnsupportedOperationException.class, () -> sortedServiceList.remove(0));
  }

  @Test
  public void testRemoveAllThrowsException() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> sortedServiceList.removeAll(Arrays.asList("test1", "test2")));
  }

  @Test
  public void testRetainAllThrowsException() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> sortedServiceList.retainAll(Arrays.asList("test1", "test2")));
  }

  @Test
  public void testSetThrowsException() {
    assertThrows(UnsupportedOperationException.class, () -> sortedServiceList.set(0, "test"));
  }

  @Test
  public void testBindPluginWithNullContext() {
    ServiceReference ref = mock(ServiceReference.class);

    // Create a custom list that returns null for context
    SortedServiceList<String> customList =
        new SortedServiceList<String>() {
          @Override
          protected BundleContext getContext() {
            return null;
          }
        };

    // Should not throw exception
    customList.bindPlugin(ref);
    assertThat(customList.size(), is(0));
  }

  @Test
  public void testUnbindPluginWhenEmpty() {
    ServiceReference ref = mock(ServiceReference.class);

    // Should not throw exception
    sortedServiceList.unbindPlugin(ref);
    assertThat(sortedServiceList.size(), is(0));
  }

  @Test
  public void testGetContextWhenBundleIsNull() {
    // Outside an OSGi container FrameworkUtil.getBundle(...) returns null, so getContext()
    // must return null rather than throwing a NullPointerException.
    BundleContext context = sortedServiceList.getContext();
    assertThat(context, nullValue());
  }

  @Test
  public void testToArrayWithExactSizeArray() {
    String[] array = new String[0];
    String[] result = sortedServiceList.toArray(array);
    assertThat(result, notNullValue());
  }

  @Test
  public void testIteratorMultipleCalls() {
    Iterator<String> iterator1 = sortedServiceList.iterator();
    Iterator<String> iterator2 = sortedServiceList.iterator();

    assertThat(iterator1, notNullValue());
    assertThat(iterator2, notNullValue());
  }

  @Test
  public void testIndexOfNull() {
    assertThat(sortedServiceList.indexOf(null), is(-1));
  }

  @Test
  public void testLastIndexOfNull() {
    assertThat(sortedServiceList.lastIndexOf(null), is(-1));
  }

  @Test
  public void testContainsNull() {
    assertThat(sortedServiceList.contains(null), is(false));
  }
}
