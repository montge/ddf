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
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geotools.api.filter.FilterVisitor;
import org.geotools.api.filter.MultiValuedFilter.MatchAction;
import org.geotools.api.filter.expression.Literal;
import org.geotools.api.filter.expression.PropertyName;
import org.junit.jupiter.api.Test;

class PropertyIsEqualToLiteralTest {

  @Test
  void testConstructor() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter, is(notNullValue()));
  }

  @Test
  void testGetExpression1ReturnsPropertyName() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.getExpression1(), is(sameInstance(propertyName)));
  }

  @Test
  void testGetExpression2ReturnsLiteral() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.getExpression2(), is(sameInstance(literal)));
  }

  @Test
  void testIsMatchingCaseReturnsTrue() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.isMatchingCase(), is(true));
  }

  @Test
  void testGetMatchActionReturnsAny() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.getMatchAction(), is(MatchAction.ANY));
  }

  @Test
  void testEvaluateWithMatchingValue() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);
    when(literal.getValue()).thenReturn("testValue");

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.evaluate("testValue"), is(true));
  }

  @Test
  void testEvaluateWithNonMatchingValue() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);
    when(literal.getValue()).thenReturn("expectedValue");

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.evaluate("differentValue"), is(false));
  }

  @Test
  void testEvaluateWithNullObject() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);
    when(literal.getValue()).thenReturn("testValue");

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.evaluate(null), is(false));
  }

  @Test
  void testEvaluateWithNullLiteral() {
    PropertyName propertyName = mock(PropertyName.class);

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, null);

    assertThat(filter.evaluate("testValue"), is(false));
  }

  @Test
  void testEvaluateWithNullLiteralValue() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);
    when(literal.getValue()).thenReturn(null);

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.evaluate("testValue"), is(false));
  }

  @Test
  void testEvaluateWithIntegerValues() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);
    when(literal.getValue()).thenReturn(42);

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);

    assertThat(filter.evaluate(42), is(true));
    assertThat(filter.evaluate(43), is(false));
  }

  @Test
  void testAcceptCallsVisitor() {
    PropertyName propertyName = mock(PropertyName.class);
    Literal literal = mock(Literal.class);
    FilterVisitor visitor = mock(FilterVisitor.class);
    Object extraData = new Object();

    PropertyIsEqualToLiteral filter = new PropertyIsEqualToLiteral(propertyName, literal);
    filter.accept(visitor, extraData);

    verify(visitor).visit(filter, extraData);
  }
}
