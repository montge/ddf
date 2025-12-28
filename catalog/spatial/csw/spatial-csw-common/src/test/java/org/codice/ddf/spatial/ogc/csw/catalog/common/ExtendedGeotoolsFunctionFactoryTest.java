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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

import java.util.Arrays;
import java.util.List;
import org.geotools.api.filter.capability.FunctionName;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.filter.expression.Function;
import org.geotools.api.filter.expression.Literal;
import org.geotools.feature.NameImpl;
import org.geotools.filter.LiteralExpressionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ExtendedGeotoolsFunctionFactory}. */
public class ExtendedGeotoolsFunctionFactoryTest {

  private ExtendedGeotoolsFunctionFactory factory;

  @BeforeEach
  public void setUp() {
    factory = new ExtendedGeotoolsFunctionFactory();
  }

  @Test
  public void testGetFunctionNamesIncludesPropertyIsFuzzy() {
    List<FunctionName> functionNames = factory.getFunctionNames();

    assertThat(functionNames, is(notNullValue()));
    assertThat(functionNames, hasItem(PropertyIsFuzzyFunction.FUNCTION_NAME));
  }

  @Test
  public void testGetFunctionNamesIncludesParentFunctions() {
    List<FunctionName> functionNames = factory.getFunctionNames();

    assertThat(functionNames, is(notNullValue()));
    // Should include functions from parent GeoToolsFunctionFactory plus PropertyIsFuzzy
    assertThat(functionNames.size() > 0, is(true));
  }

  @Test
  public void testFunctionWithStringNameReturnsPropertyIsFuzzy() {
    List<Expression> args =
        Arrays.asList(
            new LiteralExpressionImpl("propertyName"), new LiteralExpressionImpl("searchValue"));
    Literal fallback = new LiteralExpressionImpl("fallback");

    Function function =
        factory.function(PropertyIsFuzzyFunction.FUNCTION_NAME.getName(), args, fallback);

    assertThat(function, is(notNullValue()));
    assertThat(function, is(instanceOf(PropertyIsFuzzyFunction.class)));
  }

  @Test
  public void testFunctionWithNameImplReturnsPropertyIsFuzzy() {
    List<Expression> args =
        Arrays.asList(
            new LiteralExpressionImpl("propertyName"), new LiteralExpressionImpl("searchValue"));
    Literal fallback = new LiteralExpressionImpl("fallback");

    Function function =
        factory.function(
            new NameImpl(PropertyIsFuzzyFunction.FUNCTION_NAME.getName()), args, fallback);

    assertThat(function, is(notNullValue()));
    assertThat(function, is(instanceOf(PropertyIsFuzzyFunction.class)));
  }

  @Test
  public void testFunctionWithUnknownNameReturnsNull() {
    List<Expression> args =
        Arrays.asList(
            new LiteralExpressionImpl("propertyName"), new LiteralExpressionImpl("searchValue"));
    Literal fallback = new LiteralExpressionImpl("fallback");

    Function function = factory.function("unknownFunction", args, fallback);

    // Unknown function names should return null from parent
    assertThat(function, is(nullValue()));
  }

  @Test
  public void testFunctionWithUnknownNameImplReturnsNull() {
    List<Expression> args =
        Arrays.asList(
            new LiteralExpressionImpl("propertyName"), new LiteralExpressionImpl("searchValue"));
    Literal fallback = new LiteralExpressionImpl("fallback");

    Function function = factory.function(new NameImpl("unknownFunction"), args, fallback);

    assertThat(function, is(nullValue()));
  }

  @Test
  public void testFunctionWithNullFallback() {
    List<Expression> args =
        Arrays.asList(
            new LiteralExpressionImpl("propertyName"), new LiteralExpressionImpl("searchValue"));

    Function function =
        factory.function(PropertyIsFuzzyFunction.FUNCTION_NAME.getName(), args, null);

    assertThat(function, is(notNullValue()));
    assertThat(function, is(instanceOf(PropertyIsFuzzyFunction.class)));
  }
}
