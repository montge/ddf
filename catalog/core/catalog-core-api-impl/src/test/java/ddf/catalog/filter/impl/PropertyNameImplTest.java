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
package ddf.catalog.filter.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geotools.api.filter.expression.ExpressionVisitor;
import org.geotools.api.filter.expression.PropertyName;
import org.junit.jupiter.api.Test;

class PropertyNameImplTest {

  @Test
  void testConstructorSetsPropertyName() {
    String name = "title";
    PropertyNameImpl propertyName = new PropertyNameImpl(name);

    assertThat(propertyName.getPropertyName(), is(name));
  }

  @Test
  void testGetPropertyName() {
    String name = "description";
    PropertyNameImpl propertyName = new PropertyNameImpl(name);

    assertThat(propertyName.getPropertyName(), is(name));
  }

  @Test
  void testGetNamespaceContextReturnsNull() {
    PropertyNameImpl propertyName = new PropertyNameImpl("name");

    assertThat(propertyName.getNamespaceContext(), is(nullValue()));
  }

  @Test
  void testEvaluateWithNullObjectReturnsNull() {
    PropertyNameImpl propertyName = new PropertyNameImpl("name");

    Object result = propertyName.evaluate(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testEvaluateWithContextMatchingPropertyNameType() {
    String name = "property";
    PropertyNameImpl propertyName = new PropertyNameImpl(name);

    String result = propertyName.evaluate("anything", String.class);

    assertThat(result, is(name));
  }

  @Test
  void testEvaluateWithStringContextReturnsPropertyName() {
    // Since propertyName is a String, context=String.class matches the type check first
    // and returns the propertyName rather than object.toString()
    Object object = new Object();
    PropertyNameImpl propertyName = new PropertyNameImpl("prop");

    String result = propertyName.evaluate(object, String.class);

    assertThat(result, is(notNullValue()));
    assertThat(result, is("prop"));
  }

  @Test
  void testEvaluateWithNonMatchingContextReturnsNull() {
    PropertyNameImpl propertyName = new PropertyNameImpl("name");

    Integer result = propertyName.evaluate("object", Integer.class);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testEvaluateWithNullContext() {
    PropertyNameImpl propertyName = new PropertyNameImpl("name");

    Object result = propertyName.evaluate("object", null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testAcceptCallsVisitor() {
    PropertyNameImpl propertyName = new PropertyNameImpl("name");
    ExpressionVisitor visitor = mock(ExpressionVisitor.class);
    Object extraData = new Object();
    Object expectedResult = "visited";
    when(visitor.visit(any(PropertyName.class), eq(extraData))).thenReturn(expectedResult);

    Object result = propertyName.accept(visitor, extraData);

    verify(visitor).visit(propertyName, extraData);
    assertThat(result, is(expectedResult));
  }

  @Test
  void testImplementsPropertyNameInterface() {
    PropertyNameImpl propertyName = new PropertyNameImpl("name");

    assertThat(propertyName instanceof PropertyName, is(true));
  }
}
