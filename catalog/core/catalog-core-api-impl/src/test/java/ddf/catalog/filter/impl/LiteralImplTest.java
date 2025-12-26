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
import org.geotools.api.filter.expression.Literal;
import org.junit.jupiter.api.Test;

class LiteralImplTest {

  @Test
  void testConstructorSetsValue() {
    String testValue = "test";
    LiteralImpl literal = new LiteralImpl(testValue);

    assertThat(literal.getValue(), is(testValue));
  }

  @Test
  void testGetValue() {
    Integer value = 42;
    LiteralImpl literal = new LiteralImpl(value);

    assertThat(literal.getValue(), is(value));
  }

  @Test
  void testEvaluateWithNullObjectReturnsNull() {
    LiteralImpl literal = new LiteralImpl("value");

    Object result = literal.evaluate(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testEvaluateWithContextMatchingValueType() {
    String value = "test";
    LiteralImpl literal = new LiteralImpl(value);

    String result = literal.evaluate("anything", String.class);

    assertThat(result, is(value));
  }

  @Test
  void testEvaluateWithStringContextReturnsObjectToString() {
    Object object = new Object();
    LiteralImpl literal = new LiteralImpl(123);

    String result = literal.evaluate(object, String.class);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(object.toString()));
  }

  @Test
  void testEvaluateWithNonMatchingContextReturnsNull() {
    LiteralImpl literal = new LiteralImpl("string value");

    Integer result = literal.evaluate("object", Integer.class);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testEvaluateWithNullContext() {
    LiteralImpl literal = new LiteralImpl("value");

    Object result = literal.evaluate("object", null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testAcceptCallsVisitor() {
    LiteralImpl literal = new LiteralImpl("value");
    ExpressionVisitor visitor = mock(ExpressionVisitor.class);
    Object extraData = new Object();
    Object expectedResult = "visited";
    when(visitor.visit(any(Literal.class), eq(extraData))).thenReturn(expectedResult);

    Object result = literal.accept(visitor, extraData);

    verify(visitor).visit(literal, extraData);
    assertThat(result, is(expectedResult));
  }

  @Test
  void testImplementsLiteralInterface() {
    LiteralImpl literal = new LiteralImpl("value");

    assertThat(literal instanceof Literal, is(true));
  }
}
