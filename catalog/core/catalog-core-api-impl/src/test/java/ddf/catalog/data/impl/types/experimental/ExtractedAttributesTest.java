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
package ddf.catalog.data.impl.types.experimental;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.types.experimental.Extracted;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtractedAttributesTest {

  private ExtractedAttributes extractedAttributes;

  @BeforeEach
  void setUp() {
    extractedAttributes = new ExtractedAttributes();
  }

  @Test
  void testImplementsMetacardType() {
    assertThat(extractedAttributes, instanceOf(MetacardType.class));
  }

  @Test
  void testImplementsExtracted() {
    assertThat(extractedAttributes, instanceOf(Extracted.class));
  }

  @Test
  void testGetNameReturnsNull() {
    assertThat(extractedAttributes.getName(), is(nullValue()));
  }

  @Test
  void testGetAttributeDescriptors() {
    Set<AttributeDescriptor> descriptors = extractedAttributes.getAttributeDescriptors();

    assertThat(descriptors, is(notNullValue()));
    assertThat(descriptors, hasSize(1));
  }

  @Test
  void testGetAttributeDescriptorForExtractedText() {
    AttributeDescriptor descriptor =
        extractedAttributes.getAttributeDescriptor(Extracted.EXTRACTED_TEXT);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Extracted.EXTRACTED_TEXT));
  }

  @Test
  void testExtractedTextIsIndexed() {
    AttributeDescriptor descriptor =
        extractedAttributes.getAttributeDescriptor(Extracted.EXTRACTED_TEXT);

    assertThat(descriptor.isIndexed(), is(true));
  }

  @Test
  void testExtractedTextIsStored() {
    AttributeDescriptor descriptor =
        extractedAttributes.getAttributeDescriptor(Extracted.EXTRACTED_TEXT);

    assertThat(descriptor.isStored(), is(true));
  }

  @Test
  void testExtractedTextIsTokenized() {
    AttributeDescriptor descriptor =
        extractedAttributes.getAttributeDescriptor(Extracted.EXTRACTED_TEXT);

    assertThat(descriptor.isTokenized(), is(true));
  }

  @Test
  void testExtractedTextIsNotMultivalued() {
    AttributeDescriptor descriptor =
        extractedAttributes.getAttributeDescriptor(Extracted.EXTRACTED_TEXT);

    assertThat(descriptor.isMultiValued(), is(false));
  }

  @Test
  void testGetAttributeDescriptorForNonExistent() {
    AttributeDescriptor descriptor =
        extractedAttributes.getAttributeDescriptor("nonexistent.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = extractedAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
