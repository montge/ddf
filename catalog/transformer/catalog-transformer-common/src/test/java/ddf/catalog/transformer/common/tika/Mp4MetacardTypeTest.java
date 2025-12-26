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
package ddf.catalog.transformer.common.tika;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardTypeImpl;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class Mp4MetacardTypeTest {

  @Test
  void testDefaultConstructor() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    assertThat(mp4Type.getName(), is("mp4"));
  }

  @Test
  void testExtendsMetacardTypeImpl() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    assertThat(mp4Type, instanceOf(MetacardTypeImpl.class));
  }

  @Test
  void testImplementsMetacardType() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    assertThat(mp4Type, instanceOf(MetacardType.class));
  }

  @Test
  void testConstructorWithCustomName() {
    Mp4MetacardType mp4Type = new Mp4MetacardType("customMp4", null);

    assertThat(mp4Type.getName(), is("customMp4"));
  }

  @Test
  void testConstructorWithDescriptors() {
    Mp4MetacardType mp4Type = new Mp4MetacardType("mp4", Collections.emptySet());

    assertThat(mp4Type.getName(), is("mp4"));
  }

  @Test
  void testContainsAudioSampleRateAttribute() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    AttributeDescriptor descriptor =
        mp4Type.getAttributeDescriptor(Mp4MetacardType.AUDIO_SAMPLE_RATE);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Mp4MetacardType.AUDIO_SAMPLE_RATE));
  }

  @Test
  void testAudioSampleRateAttributePrefix() {
    assertThat(Mp4MetacardType.AUDIO_SAMPLE_RATE, is("ext.mp4.audio-sample-rate"));
  }

  @Test
  void testGetAttributeDescriptors() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    Set<AttributeDescriptor> descriptors = mp4Type.getAttributeDescriptors();

    assertThat(descriptors, is(notNullValue()));
    assertThat(descriptors.size(), greaterThan(0));
  }

  @Test
  void testAudioSampleRateIsIndexed() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    AttributeDescriptor descriptor =
        mp4Type.getAttributeDescriptor(Mp4MetacardType.AUDIO_SAMPLE_RATE);

    assertThat(descriptor.isIndexed(), is(true));
  }

  @Test
  void testAudioSampleRateIsStored() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    AttributeDescriptor descriptor =
        mp4Type.getAttributeDescriptor(Mp4MetacardType.AUDIO_SAMPLE_RATE);

    assertThat(descriptor.isStored(), is(true));
  }

  @Test
  void testAudioSampleRateIsNotMultivalued() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    AttributeDescriptor descriptor =
        mp4Type.getAttributeDescriptor(Mp4MetacardType.AUDIO_SAMPLE_RATE);

    assertThat(descriptor.isMultiValued(), is(false));
  }

  @Test
  void testAudioSampleRateIsNotTokenized() {
    Mp4MetacardType mp4Type = new Mp4MetacardType();

    AttributeDescriptor descriptor =
        mp4Type.getAttributeDescriptor(Mp4MetacardType.AUDIO_SAMPLE_RATE);

    assertThat(descriptor.isTokenized(), is(false));
  }
}
