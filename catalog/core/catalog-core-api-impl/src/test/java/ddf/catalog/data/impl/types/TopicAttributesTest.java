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
import ddf.catalog.data.types.Topic;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TopicAttributesTest {

  private TopicAttributes topicAttributes;

  @BeforeEach
  void setUp() {
    topicAttributes = new TopicAttributes();
  }

  @Test
  void testGetName() {
    assertThat(topicAttributes.getName(), is("topic"));
  }

  @Test
  void testGetAttributeDescriptorsReturnsThreeDescriptors() {
    Set<AttributeDescriptor> descriptors = topicAttributes.getAttributeDescriptors();

    assertThat(descriptors, hasSize(3));
  }

  @Test
  void testGetAttributeDescriptorForCategory() {
    AttributeDescriptor descriptor = topicAttributes.getAttributeDescriptor(Topic.CATEGORY);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Topic.CATEGORY));
  }

  @Test
  void testGetAttributeDescriptorForKeyword() {
    AttributeDescriptor descriptor = topicAttributes.getAttributeDescriptor(Topic.KEYWORD);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Topic.KEYWORD));
  }

  @Test
  void testGetAttributeDescriptorForVocabulary() {
    AttributeDescriptor descriptor = topicAttributes.getAttributeDescriptor(Topic.VOCABULARY);

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getName(), is(Topic.VOCABULARY));
  }

  @Test
  void testGetAttributeDescriptorWithUnknownNameReturnsNull() {
    AttributeDescriptor descriptor = topicAttributes.getAttributeDescriptor("unknown.attribute");

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testGetAttributeDescriptorWithNullNameReturnsNull() {
    AttributeDescriptor descriptor = topicAttributes.getAttributeDescriptor(null);

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  void testAllAttributesAreMultivalued() {
    for (AttributeDescriptor descriptor : topicAttributes.getAttributeDescriptors()) {
      assertThat(
          descriptor.getName() + " should be multivalued", descriptor.isMultiValued(), is(true));
    }
  }

  @Test
  void testAllAttributesAreTokenized() {
    for (AttributeDescriptor descriptor : topicAttributes.getAttributeDescriptors()) {
      assertThat(descriptor.getName() + " should be tokenized", descriptor.isTokenized(), is(true));
    }
  }

  @Test
  void testAttributeDescriptorsSetIsUnmodifiable() {
    Set<AttributeDescriptor> descriptors = topicAttributes.getAttributeDescriptors();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> descriptors.add(null));
  }
}
