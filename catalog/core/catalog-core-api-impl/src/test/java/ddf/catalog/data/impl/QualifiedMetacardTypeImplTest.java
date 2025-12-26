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
package ddf.catalog.data.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.QualifiedMetacardType;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QualifiedMetacardTypeImplTest {

  @Test
  void testConstructorWithNamespaceNameAndDescriptors() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(mock(AttributeDescriptor.class));

    QualifiedMetacardTypeImpl type =
        new QualifiedMetacardTypeImpl("http://example.com/ns", "TestType", descriptors);

    assertThat(type.getNamespace(), is("http://example.com/ns"));
    assertThat(type.getName(), is("TestType"));
  }

  @Test
  void testConstructorWithNullNamespaceUsesDefault() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();

    QualifiedMetacardTypeImpl type = new QualifiedMetacardTypeImpl(null, "TestType", descriptors);

    assertThat(type.getNamespace(), is(QualifiedMetacardType.DEFAULT_METACARD_TYPE_NAMESPACE));
  }

  @Test
  void testConstructorWithEmptyNamespaceUsesDefault() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();

    QualifiedMetacardTypeImpl type = new QualifiedMetacardTypeImpl("", "TestType", descriptors);

    assertThat(type.getNamespace(), is(QualifiedMetacardType.DEFAULT_METACARD_TYPE_NAMESPACE));
  }

  @Test
  void testConstructorWithMetacardType() {
    MetacardType mt = mock(MetacardType.class);
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    when(mt.getName()).thenReturn("MockType");
    when(mt.getAttributeDescriptors()).thenReturn(descriptors);

    QualifiedMetacardTypeImpl type = new QualifiedMetacardTypeImpl(mt);

    assertThat(type.getName(), is("MockType"));
    assertThat(type.getNamespace(), is(QualifiedMetacardType.DEFAULT_METACARD_TYPE_NAMESPACE));
  }

  @Test
  void testConstructorWithNamespaceAndMetacardType() {
    MetacardType mt = mock(MetacardType.class);
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    when(mt.getName()).thenReturn("MockType");
    when(mt.getAttributeDescriptors()).thenReturn(descriptors);

    QualifiedMetacardTypeImpl type = new QualifiedMetacardTypeImpl("http://custom.ns", mt);

    assertThat(type.getName(), is("MockType"));
    assertThat(type.getNamespace(), is("http://custom.ns"));
  }

  @Test
  void testGetNamespace() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();

    QualifiedMetacardTypeImpl type =
        new QualifiedMetacardTypeImpl("http://test.namespace", "Type", descriptors);

    assertThat(type.getNamespace(), is("http://test.namespace"));
  }

  @Test
  void testEqualsWithSameObject() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    QualifiedMetacardTypeImpl type =
        new QualifiedMetacardTypeImpl("http://ns", "Type", descriptors);

    assertThat(type.equals(type), is(true));
  }

  @Test
  void testEqualsWithEqualObjects() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();

    QualifiedMetacardTypeImpl type1 =
        new QualifiedMetacardTypeImpl("http://ns", "Type", descriptors);
    QualifiedMetacardTypeImpl type2 =
        new QualifiedMetacardTypeImpl("http://ns", "Type", descriptors);

    assertThat(type1, equalTo(type2));
  }

  @Test
  void testEqualsWithDifferentNamespace() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();

    QualifiedMetacardTypeImpl type1 =
        new QualifiedMetacardTypeImpl("http://ns1", "Type", descriptors);
    QualifiedMetacardTypeImpl type2 =
        new QualifiedMetacardTypeImpl("http://ns2", "Type", descriptors);

    assertThat(type1, is(not(equalTo(type2))));
  }

  @Test
  void testEqualsWithDifferentName() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();

    QualifiedMetacardTypeImpl type1 =
        new QualifiedMetacardTypeImpl("http://ns", "Type1", descriptors);
    QualifiedMetacardTypeImpl type2 =
        new QualifiedMetacardTypeImpl("http://ns", "Type2", descriptors);

    assertThat(type1, is(not(equalTo(type2))));
  }

  @Test
  void testEqualsWithNull() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    QualifiedMetacardTypeImpl type =
        new QualifiedMetacardTypeImpl("http://ns", "Type", descriptors);

    assertThat(type.equals(null), is(false));
  }

  @Test
  void testHashCodeConsistentWithEquals() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();

    QualifiedMetacardTypeImpl type1 =
        new QualifiedMetacardTypeImpl("http://ns", "Type", descriptors);
    QualifiedMetacardTypeImpl type2 =
        new QualifiedMetacardTypeImpl("http://ns", "Type", descriptors);

    assertThat(type1.hashCode(), is(type2.hashCode()));
  }

  @Test
  void testHashCodeDifferentForDifferentNamespace() {
    Set<AttributeDescriptor> descriptors = new HashSet<>();

    QualifiedMetacardTypeImpl type1 =
        new QualifiedMetacardTypeImpl("http://ns1", "Type", descriptors);
    QualifiedMetacardTypeImpl type2 =
        new QualifiedMetacardTypeImpl("http://ns2", "Type", descriptors);

    assertThat(type1.hashCode(), is(not(type2.hashCode())));
  }
}
