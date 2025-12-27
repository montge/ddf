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
package org.codice.ddf.spatial.ogc.csw.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.filter.expression.Literal;
import org.geotools.filter.AttributeExpressionImpl;
import org.geotools.filter.LiteralExpressionImpl;
import org.junit.jupiter.api.Test;

class PropertyIsFuzzyFunctionTest {

  @Test
  void testFunctionNameString() {
    assertThat(PropertyIsFuzzyFunction.FUNCTION_NAME_STRING, is("PropertyIsFuzzy"));
  }

  @Test
  void testFunctionNameNotNull() {
    assertThat(PropertyIsFuzzyFunction.FUNCTION_NAME, is(notNullValue()));
  }

  @Test
  void testFunctionNameLocalName() {
    assertThat(PropertyIsFuzzyFunction.FUNCTION_NAME.getName(), is("PropertyIsFuzzy"));
  }

  @Test
  void testConstructorWithValidParameters() {
    Expression propertyName = new AttributeExpressionImpl("title");
    Expression literal = new LiteralExpressionImpl("searchTerm");
    List<Expression> parameters = Arrays.asList(propertyName, literal);

    PropertyIsFuzzyFunction function = new PropertyIsFuzzyFunction(parameters, null);

    assertThat(function, is(notNullValue()));
  }

  @Test
  void testGetPropertyName() {
    Expression propertyName = new AttributeExpressionImpl("title");
    Expression literal = new LiteralExpressionImpl("searchTerm");
    List<Expression> parameters = Arrays.asList(propertyName, literal);

    PropertyIsFuzzyFunction function = new PropertyIsFuzzyFunction(parameters, null);

    assertThat(function.getPropertyName(), is(sameInstance(propertyName)));
  }

  @Test
  void testGetLiteral() {
    Expression propertyName = new AttributeExpressionImpl("title");
    Expression literal = new LiteralExpressionImpl("searchTerm");
    List<Expression> parameters = Arrays.asList(propertyName, literal);

    PropertyIsFuzzyFunction function = new PropertyIsFuzzyFunction(parameters, null);

    assertThat(function.getLiteral(), is(sameInstance(literal)));
  }

  @Test
  void testConstructorWithNullParametersThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new PropertyIsFuzzyFunction(null, null));
  }

  @Test
  void testConstructorWithEmptyParametersThrowsException() {
    List<Expression> emptyParameters = Collections.emptyList();

    assertThrows(
        IllegalArgumentException.class, () -> new PropertyIsFuzzyFunction(emptyParameters, null));
  }

  @Test
  void testConstructorWithOneParameterThrowsException() {
    Expression propertyName = new AttributeExpressionImpl("title");
    List<Expression> oneParameter = Collections.singletonList(propertyName);

    assertThrows(
        IllegalArgumentException.class, () -> new PropertyIsFuzzyFunction(oneParameter, null));
  }

  @Test
  void testConstructorWithThreeParametersThrowsException() {
    Expression exp1 = new AttributeExpressionImpl("title");
    Expression exp2 = new LiteralExpressionImpl("term1");
    Expression exp3 = new LiteralExpressionImpl("term2");
    List<Expression> threeParameters = Arrays.asList(exp1, exp2, exp3);

    assertThrows(
        IllegalArgumentException.class, () -> new PropertyIsFuzzyFunction(threeParameters, null));
  }

  @Test
  void testHashCodeConsistency() {
    Expression propertyName = new AttributeExpressionImpl("title");
    Expression literal = new LiteralExpressionImpl("searchTerm");
    List<Expression> parameters = Arrays.asList(propertyName, literal);

    PropertyIsFuzzyFunction function = new PropertyIsFuzzyFunction(parameters, null);

    int hashCode1 = function.hashCode();
    int hashCode2 = function.hashCode();

    assertThat(hashCode1, is(hashCode2));
  }

  @Test
  void testEqualsWithSameObject() {
    Expression propertyName = new AttributeExpressionImpl("title");
    Expression literal = new LiteralExpressionImpl("searchTerm");
    List<Expression> parameters = Arrays.asList(propertyName, literal);

    PropertyIsFuzzyFunction function = new PropertyIsFuzzyFunction(parameters, null);

    assertThat(function.equals(function), is(true));
  }

  @Test
  void testConstructorWithFallbackLiteral() {
    Expression propertyName = new AttributeExpressionImpl("title");
    Expression literal = new LiteralExpressionImpl("searchTerm");
    Literal fallback = new LiteralExpressionImpl("default");
    List<Expression> parameters = Arrays.asList(propertyName, literal);

    PropertyIsFuzzyFunction function = new PropertyIsFuzzyFunction(parameters, fallback);

    assertThat(function, is(notNullValue()));
  }
}
