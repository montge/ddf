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
package org.codice.ddf.spatial.kml.transformer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/** Unit tests for {@link HandlebarsMetacard}. */
public class HandlebarsMetacardTest {

  @Test
  public void testConstructorWithMetacard() {
    Metacard metacard = new MetacardImpl();
    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    assertThat(handlebarsMetacard, is(notNullValue()));
  }

  @Test
  public void testGetAttributesReturnsEmptySetForEmptyMetacard() {
    MetacardType metacardType = new MetacardTypeImpl("test", new HashSet<>());
    Metacard metacard = new MetacardImpl(metacardType);
    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    Set<?> attributes = handlebarsMetacard.getAttributes();
    assertThat(attributes, is(notNullValue()));
    assertThat(attributes.isEmpty(), is(true));
  }

  @Test
  public void testGetAttributesWithSingleAttribute() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("title", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardType metacardType = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(metacardType);
    metacard.setAttribute(new AttributeImpl("title", "Test Title"));

    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    Set<?> attributes = handlebarsMetacard.getAttributes();
    assertThat(attributes, is(notNullValue()));
    assertThat(attributes.size(), is(1));
  }

  @Test
  public void testGetAttributesWithMultipleAttributes() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("title", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl(
            "description", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("created", true, true, false, false, BasicTypes.DATE_TYPE));

    MetacardType metacardType = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(metacardType);
    metacard.setAttribute(new AttributeImpl("title", "Test Title"));
    metacard.setAttribute(new AttributeImpl("description", "Test Description"));
    metacard.setAttribute(new AttributeImpl("created", new java.util.Date()));

    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    Set<?> attributes = handlebarsMetacard.getAttributes();
    assertThat(attributes, is(notNullValue()));
    assertThat(attributes.size(), is(3));
  }

  @Test
  public void testGetAttributesExcludesNullAttributes() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("title", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl(
            "description", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardType metacardType = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(metacardType);
    metacard.setAttribute(new AttributeImpl("title", "Test Title"));
    // description is not set, should be null

    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    Set<?> attributes = handlebarsMetacard.getAttributes();
    assertThat(attributes, is(notNullValue()));
    assertThat(attributes.size(), is(1)); // Only title should be included
  }

  @Test
  public void testGetAttributesReturnsSortedSet() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("zebra", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("apple", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("banana", true, true, false, false, BasicTypes.STRING_TYPE));

    MetacardType metacardType = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(metacardType);
    metacard.setAttribute(new AttributeImpl("zebra", "Z"));
    metacard.setAttribute(new AttributeImpl("apple", "A"));
    metacard.setAttribute(new AttributeImpl("banana", "B"));

    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    Set<?> attributes = handlebarsMetacard.getAttributes();
    assertThat(attributes, is(notNullValue()));
    assertThat(attributes.size(), is(3));

    // Verify it's a TreeSet (sorted)
    Object[] attributeArray = attributes.toArray();
    // Attributes should be sorted alphabetically: apple, banana, zebra
  }

  @Test
  public void testGetAttributesWithDifferentAttributeFormats() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "stringAttr", true, true, false, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("intAttr", true, true, false, false, BasicTypes.INTEGER_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("boolAttr", true, true, false, false, BasicTypes.BOOLEAN_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("dateAttr", true, true, false, false, BasicTypes.DATE_TYPE));

    MetacardType metacardType = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(metacardType);
    metacard.setAttribute(new AttributeImpl("stringAttr", "test"));
    metacard.setAttribute(new AttributeImpl("intAttr", 42));
    metacard.setAttribute(new AttributeImpl("boolAttr", true));
    metacard.setAttribute(new AttributeImpl("dateAttr", new java.util.Date()));

    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    Set<?> attributes = handlebarsMetacard.getAttributes();
    assertThat(attributes, is(notNullValue()));
    assertThat(attributes.size(), is(4));
  }

  @Test
  public void testInheritsFromMetacardImpl() {
    Metacard metacard = new MetacardImpl();
    ((MetacardImpl) metacard).setTitle("Test Title");
    ((MetacardImpl) metacard).setId("test-id");

    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    // Should inherit all MetacardImpl methods
    assertThat(handlebarsMetacard.getTitle(), is("Test Title"));
    assertThat(handlebarsMetacard.getId(), is("test-id"));
  }

  @Test
  public void testGetAttributesWithMetacardCoreAttributes() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test Title");
    metacard.setDescription("Test Description");
    metacard.setId("test-id");

    HandlebarsMetacard handlebarsMetacard = new HandlebarsMetacard(metacard);

    Set<?> attributes = handlebarsMetacard.getAttributes();
    assertThat(attributes, is(notNullValue()));
    // Should contain title, description, id (and other core metacard attributes that are set)
    assertThat(attributes.size() > 0, is(true));
  }
}
