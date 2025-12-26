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
package ddf.catalog.data.impl.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.types.Media;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaAttributesTest {

  private MediaAttributes mediaAttributes;

  @BeforeEach
  void setUp() {
    mediaAttributes = new MediaAttributes();
  }

  @Test
  void testGetName() {
    assertThat(mediaAttributes.getName(), is("media"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsFourteenDescriptors() {
    Set<AttributeDescriptor> descriptors = mediaAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(14));
  }

  @Test
  void testGetAttributeDescriptorForFormat() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.FORMAT);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.FORMAT));
  }

  @Test
  void testGetAttributeDescriptorForFormatVersion() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.FORMAT_VERSION);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.FORMAT_VERSION));
  }

  @Test
  void testGetAttributeDescriptorForBitsPerSecond() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.BITS_PER_SECOND);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.BITS_PER_SECOND));
  }

  @Test
  void testGetAttributeDescriptorForBitsPerSample() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.BITS_PER_SAMPLE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.BITS_PER_SAMPLE));
  }

  @Test
  void testGetAttributeDescriptorForCompression() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.COMPRESSION);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.COMPRESSION));
  }

  @Test
  void testGetAttributeDescriptorForEncoding() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.ENCODING);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.ENCODING));
  }

  @Test
  void testGetAttributeDescriptorForFrameCenter() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.FRAME_CENTER);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.FRAME_CENTER));
  }

  @Test
  void testGetAttributeDescriptorForFramesPerSecond() {
    AttributeDescriptor descriptor =
        mediaAttributes.getAttributeDescriptor(Media.FRAMES_PER_SECOND);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.FRAMES_PER_SECOND));
  }

  @Test
  void testGetAttributeDescriptorForHeight() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.HEIGHT);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.HEIGHT));
  }

  @Test
  void testGetAttributeDescriptorForWidth() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.WIDTH);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.WIDTH));
  }

  @Test
  void testGetAttributeDescriptorForDuration() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.DURATION);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Media.DURATION));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testEncodingAttributeIsMultivalued() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.ENCODING);

    assertThat(descriptor.isMultiValued(), is(true));
  }

  @Test
  void testFormatAttributeIsNotMultivalued() {
    AttributeDescriptor descriptor = mediaAttributes.getAttributeDescriptor(Media.FORMAT);

    assertThat(descriptor.isMultiValued(), is(false));
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = mediaAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
