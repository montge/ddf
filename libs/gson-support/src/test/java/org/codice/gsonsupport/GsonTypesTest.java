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
package org.codice.gsonsupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GsonTypesTest {

  @Test
  void testCheckNotNullWithNonNullObject() {
    String value = "test";
    String result = GsonTypes.checkNotNull(value);

    assertThat(result, is(value));
  }

  @Test
  void testCheckNotNullWithNullThrowsException() {
    assertThrows(NullPointerException.class, () -> GsonTypes.checkNotNull(null));
  }

  @Test
  void testCheckArgumentWithTrueCondition() {
    // Should not throw any exception
    GsonTypes.checkArgument(true);
  }

  @Test
  void testCheckArgumentWithFalseConditionThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> GsonTypes.checkArgument(false));
  }

  @Test
  void testGetRawTypeWithClass() {
    Class<?> result = GsonTypes.getRawType(String.class);

    assertSame(String.class, result);
  }

  @Test
  void testGetRawTypeWithParameterizedType() throws NoSuchFieldException {
    // Get a parameterized type from a field declaration
    class Holder {
      List<String> stringList;
    }
    Type type = Holder.class.getDeclaredField("stringList").getGenericType();

    Class<?> result = GsonTypes.getRawType(type);

    assertSame(List.class, result);
  }

  @Test
  void testNewParameterizedTypeWithOwner() {
    ParameterizedType result =
        GsonTypes.newParameterizedTypeWithOwner(null, List.class, String.class);

    assertThat(result, is(notNullValue()));
    assertEquals(List.class, result.getRawType());
    assertEquals(String.class, result.getActualTypeArguments()[0]);
  }

  @Test
  void testArrayOf() {
    GenericArrayType result = GsonTypes.arrayOf(String.class);

    assertThat(result, is(notNullValue()));
    assertEquals(String.class, result.getGenericComponentType());
  }

  @Test
  void testSubtypeOf() {
    WildcardType result = GsonTypes.subtypeOf(CharSequence.class);

    assertThat(result, is(notNullValue()));
    assertEquals(CharSequence.class, result.getUpperBounds()[0]);
    assertThat(result.getLowerBounds().length, is(0));
  }

  @Test
  void testSupertypeOf() {
    WildcardType result = GsonTypes.supertypeOf(String.class);

    assertThat(result, is(notNullValue()));
    assertEquals(Object.class, result.getUpperBounds()[0]);
    assertEquals(String.class, result.getLowerBounds()[0]);
  }

  @Test
  void testCanonicalizeWithClass() {
    Type result = GsonTypes.canonicalize(String.class);

    assertEquals(String.class, result);
  }

  @Test
  void testCanonicalizeWithArrayClass() {
    Type result = GsonTypes.canonicalize(String[].class);

    assertThat(result, is(notNullValue()));
    assertThat(result instanceof GenericArrayType, is(true));
  }

  @Test
  void testEqualsWithSameType() {
    assertThat(GsonTypes.equals(String.class, String.class), is(true));
  }

  @Test
  void testEqualsWithDifferentTypes() {
    assertThat(GsonTypes.equals(String.class, Integer.class), is(false));
  }

  @Test
  void testEqualsWithNullTypes() {
    assertThat(GsonTypes.equals(null, null), is(true));
  }

  @Test
  void testEqualsWithOneNullType() {
    assertThat(GsonTypes.equals(String.class, null), is(false));
  }

  @Test
  void testTypeToStringWithClass() {
    String result = GsonTypes.typeToString(String.class);

    assertThat(result, is("java.lang.String"));
  }

  @Test
  void testGetArrayComponentType() {
    Type result = GsonTypes.getArrayComponentType(String[].class);

    assertEquals(String.class, result);
  }

  @Test
  void testGetArrayComponentTypeWithGenericArrayType() {
    GenericArrayType arrayType = GsonTypes.arrayOf(String.class);

    Type result = GsonTypes.getArrayComponentType(arrayType);

    assertEquals(String.class, result);
  }

  @Test
  void testGetMapKeyAndValueTypes() throws NoSuchFieldException {
    class Holder {
      Map<String, Integer> map;
    }
    Type type = Holder.class.getDeclaredField("map").getGenericType();

    Type[] result = GsonTypes.getMapKeyAndValueTypes(type, Map.class);

    assertThat(result.length, is(2));
    assertEquals(String.class, result[0]);
    assertEquals(Integer.class, result[1]);
  }

  @Test
  void testParameterizedTypeEquality() {
    ParameterizedType type1 =
        GsonTypes.newParameterizedTypeWithOwner(null, List.class, String.class);
    ParameterizedType type2 =
        GsonTypes.newParameterizedTypeWithOwner(null, List.class, String.class);

    assertThat(GsonTypes.equals(type1, type2), is(true));
  }

  @Test
  void testGenericArrayTypeEquality() {
    GenericArrayType type1 = GsonTypes.arrayOf(String.class);
    GenericArrayType type2 = GsonTypes.arrayOf(String.class);

    assertThat(GsonTypes.equals(type1, type2), is(true));
  }

  @Test
  void testWildcardTypeEquality() {
    WildcardType type1 = GsonTypes.subtypeOf(CharSequence.class);
    WildcardType type2 = GsonTypes.subtypeOf(CharSequence.class);

    assertThat(GsonTypes.equals(type1, type2), is(true));
  }
}
