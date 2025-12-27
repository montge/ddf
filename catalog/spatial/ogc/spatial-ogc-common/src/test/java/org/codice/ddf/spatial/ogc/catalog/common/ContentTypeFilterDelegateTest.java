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
package org.codice.ddf.spatial.ogc.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import ddf.catalog.data.ContentType;
import ddf.catalog.data.Metacard;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentTypeFilterDelegateTest {

  private ContentTypeFilterDelegate delegate;

  @BeforeEach
  void setUp() {
    delegate = new ContentTypeFilterDelegate();
  }

  @Test
  void testDefaultOperationReturnsEmptyList() {
    List<ContentType> result = delegate.defaultOperation("property", "literal", String.class, null);

    assertThat(result, is(empty()));
  }

  @Test
  void testPropertyIsEqualToWithContentTypeProperty() {
    List<ContentType> result =
        delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "application/xml", true);

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getName(), is("application/xml"));
  }

  @Test
  void testPropertyIsEqualToWithNonContentTypeProperty() {
    List<ContentType> result = delegate.propertyIsEqualTo("other-property", "value", true);

    assertThat(result, is(empty()));
  }

  @Test
  void testPropertyIsEqualToCaseInsensitive() {
    List<ContentType> result =
        delegate.propertyIsEqualTo("METADATA-CONTENT-TYPE", "text/plain", false);

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getName(), is("text/plain"));
  }

  @Test
  void testPropertyIsLikeDelegatestoPropertyIsEqualTo() {
    List<ContentType> result = delegate.propertyIsLike(Metacard.CONTENT_TYPE, "image/png", true);

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getName(), is("image/png"));
  }

  @Test
  void testPropertyIsLikeWithNonContentTypeProperty() {
    List<ContentType> result = delegate.propertyIsLike("other-property", "pattern", false);

    assertThat(result, is(empty()));
  }

  @Test
  void testAndCombinesLists() {
    List<List<ContentType>> operands = new ArrayList<>();

    List<ContentType> list1 =
        delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "application/json", true);
    List<ContentType> list2 =
        delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "application/xml", true);

    operands.add(list1);
    operands.add(list2);

    List<ContentType> result = delegate.and(operands);

    assertThat(result, hasSize(2));
  }

  @Test
  void testOrCombinesLists() {
    List<List<ContentType>> operands = new ArrayList<>();

    List<ContentType> list1 = delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "text/html", true);
    List<ContentType> list2 = delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "text/plain", true);

    operands.add(list1);
    operands.add(list2);

    List<ContentType> result = delegate.or(operands);

    assertThat(result, hasSize(2));
  }

  @Test
  void testAndWithNullOperands() {
    List<ContentType> result = delegate.and(null);

    assertThat(result, is(empty()));
  }

  @Test
  void testOrWithNullOperands() {
    List<ContentType> result = delegate.or(null);

    assertThat(result, is(empty()));
  }

  @Test
  void testAndWithEmptyOperands() {
    List<ContentType> result = delegate.and(new ArrayList<>());

    assertThat(result, is(empty()));
  }

  @Test
  void testOrWithEmptyOperands() {
    List<ContentType> result = delegate.or(new ArrayList<>());

    assertThat(result, is(empty()));
  }

  @Test
  void testPropertyIsEqualToThrowsExceptionForEmptyPropertyName() {
    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> delegate.propertyIsEqualTo("", "value", true));
  }

  @Test
  void testPropertyIsEqualToThrowsExceptionForNullPropertyName() {
    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> delegate.propertyIsEqualTo(null, "value", true));
  }

  @Test
  void testPropertyIsEqualToThrowsExceptionForEmptyLiteral() {
    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "", true));
  }

  @Test
  void testPropertyIsEqualToThrowsExceptionForNullLiteral() {
    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, null, true));
  }

  @Test
  void testPropertyIsLikeThrowsExceptionForEmptyPropertyName() {
    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> delegate.propertyIsLike("", "pattern", true));
  }

  @Test
  void testPropertyIsLikeThrowsExceptionForEmptyPattern() {
    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsLike(Metacard.CONTENT_TYPE, "", true));
  }

  @Test
  void testContentTypeVersionIsEmpty() {
    List<ContentType> result =
        delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "application/pdf", true);

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getVersion(), is(""));
  }

  @Test
  void testMultipleAndOperations() {
    List<List<ContentType>> operands = new ArrayList<>();

    operands.add(delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "type1", true));
    operands.add(delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "type2", true));
    operands.add(delegate.propertyIsEqualTo(Metacard.CONTENT_TYPE, "type3", true));

    List<ContentType> result = delegate.and(operands);

    assertThat(result, hasSize(3));
  }
}
