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
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.AttributeType.AttributeFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link AttributeDescriptorComparator}. */
public class AttributeDescriptorComparatorTest {

  private AttributeDescriptorComparator comparator;

  @BeforeEach
  public void setUp() {
    comparator = new AttributeDescriptorComparator();
  }

  /**
   * Stubs the attribute type chain that the comparator inspects once two descriptors have equal
   * names. All descriptors stubbed this way share the same format and binding, so the type-based
   * tie-breaking yields 0.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static void stubIdenticalType(AttributeDescriptor descriptor) {
    AttributeType type = mock(AttributeType.class);
    when(type.getAttributeFormat()).thenReturn(AttributeFormat.STRING);
    when(type.getBinding()).thenReturn((Class) String.class);
    when(descriptor.getType()).thenReturn(type);
  }

  @Test
  public void testCompareEqualNames() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    when(descriptor1.getName()).thenReturn("testAttribute");
    when(descriptor2.getName()).thenReturn("testAttribute");
    // When names are equal, the comparator falls through to compare the attribute type, so the
    // type chain must be stubbed identically on both descriptors for the result to remain 0.
    stubIdenticalType(descriptor1);
    stubIdenticalType(descriptor2);

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
    // The comparator uses a locale-sensitive Collator first, which sorts the uppercase
    // "Attribute" after the lowercase "attribute", so the result is positive.
    assertThat(result > 0, is(true));
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
    // Equal (empty) names cause the comparator to fall through to the attribute type comparison.
    stubIdenticalType(descriptor1);
    stubIdenticalType(descriptor2);

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
