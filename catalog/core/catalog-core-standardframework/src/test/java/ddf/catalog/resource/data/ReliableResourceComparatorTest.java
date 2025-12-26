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
package ddf.catalog.resource.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReliableResourceComparatorTest {

  private ReliableResourceComparator comparator;

  @BeforeEach
  void setUp() {
    comparator = new ReliableResourceComparator();
  }

  @Test
  void testCompareWhenFirstIsOlder() {
    Map.Entry entry1 = createMockEntry(1000L);
    Map.Entry entry2 = createMockEntry(2000L);

    assertThat(comparator.compare(entry1, entry2), is(lessThan(0)));
  }

  @Test
  void testCompareWhenSecondIsOlder() {
    Map.Entry entry1 = createMockEntry(2000L);
    Map.Entry entry2 = createMockEntry(1000L);

    assertThat(comparator.compare(entry1, entry2), is(greaterThan(0)));
  }

  @Test
  void testCompareWhenEqual() {
    Map.Entry entry1 = createMockEntry(1000L);
    Map.Entry entry2 = createMockEntry(1000L);

    assertThat(comparator.compare(entry1, entry2), is(0));
  }

  @Test
  void testCompareWithZeroValues() {
    Map.Entry entry1 = createMockEntry(0L);
    Map.Entry entry2 = createMockEntry(0L);

    assertThat(comparator.compare(entry1, entry2), is(0));
  }

  @Test
  void testCompareWithLargeValues() {
    Map.Entry entry1 = createMockEntry(Long.MAX_VALUE - 1);
    Map.Entry entry2 = createMockEntry(Long.MAX_VALUE);

    assertThat(comparator.compare(entry1, entry2), is(lessThan(0)));
  }

  @SuppressWarnings("unchecked")
  private Map.Entry createMockEntry(long lastTouchedMillis) {
    Map.Entry entry = mock(Map.Entry.class);
    ReliableResource resource = mock(ReliableResource.class);
    when(entry.getValue()).thenReturn(resource);
    when(resource.getLastTouchedMillis()).thenReturn(lastTouchedMillis);
    return entry;
  }
}
