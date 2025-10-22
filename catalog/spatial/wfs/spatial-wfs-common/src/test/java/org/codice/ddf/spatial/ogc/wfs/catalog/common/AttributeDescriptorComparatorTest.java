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
package org.codice.ddf.spatial.ogc.wfs.catalog.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.AttributeDescriptor;
import org.junit.Before;
import org.junit.Test;

/** Unit tests for {@link AttributeDescriptorComparator}. */
public class AttributeDescriptorComparatorTest {

  private AttributeDescriptorComparator comparator;

  @Before
  public void setUp() {
    comparator = new AttributeDescriptorComparator();
  }

  @Test
  public void testCompareEqualNames() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("testAttribute");
    when(descriptor2.getName()).thenReturn("testAttribute");

    int result = comparator.compare(descriptor1, descriptor2);
    assertThat(result, is(0));
  }

  @Test
  public void testCompareFirstBeforeSecond() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("attributeA");
    when(descriptor2.getName()).thenReturn("attributeB");

    int result = comparator.compare(descriptor1, descriptor2);
    assertThat(result < 0, is(true));
  }

  @Test
  public void testCompareSecondBeforeFirst() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("attributeZ");
    when(descriptor2.getName()).thenReturn("attributeA");

    int result = comparator.compare(descriptor1, descriptor2);
    assertThat(result > 0, is(true));
  }

  @Test
  public void testCompareCaseSensitive() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("Attribute");
    when(descriptor2.getName()).thenReturn("attribute");

    int result = comparator.compare(descriptor1, descriptor2);
    // Case-sensitive comparison: 'A' < 'a' in ASCII
    assertThat(result < 0, is(true));
  }

  @Test
  public void testCompareWithSpecialCharacters() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("attribute_1");
    when(descriptor2.getName()).thenReturn("attribute_2");

    int result = comparator.compare(descriptor1, descriptor2);
    assertThat(result < 0, is(true));
  }

  @Test
  public void testCompareWithNumericSuffixes() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("attribute10");
    when(descriptor2.getName()).thenReturn("attribute2");

    int result = comparator.compare(descriptor1, descriptor2);
    // Lexicographic: "attribute10" < "attribute2" ('1' < '2')
    assertThat(result < 0, is(true));
  }

  @Test
  public void testCompareWithDifferentLengths() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("attr");
    when(descriptor2.getName()).thenReturn("attribute");

    int result = comparator.compare(descriptor1, descriptor2);
    assertThat(result < 0, is(true));
  }

  @Test
  public void testCompareWithEmptyString() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("");
    when(descriptor2.getName()).thenReturn("attribute");

    int result = comparator.compare(descriptor1, descriptor2);
    assertThat(result < 0, is(true));
  }

  @Test
  public void testCompareBothEmptyStrings() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("");
    when(descriptor2.getName()).thenReturn("");

    int result = comparator.compare(descriptor1, descriptor2);
    assertThat(result, is(0));
  }

  @Test
  public void testCompareWithUnicodeCharacters() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("attributé");
    when(descriptor2.getName()).thenReturn("attribute");

    int result = comparator.compare(descriptor1, descriptor2);
    assertThat(result > 0, is(true)); // 'é' > 'e' in Unicode
  }
}
