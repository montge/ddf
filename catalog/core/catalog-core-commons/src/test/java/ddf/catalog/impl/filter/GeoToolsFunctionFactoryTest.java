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
package ddf.catalog.impl.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;

public class GeoToolsFunctionFactoryTest {
  GeoToolsFunctionFactory toTest;

  @BeforeEach
  public void setUp() {
    toTest = new GeoToolsFunctionFactory();
  }

  @Test
  public void testGetFunctionNames() {
    List<FunctionName> functionNames = toTest.getFunctionNames();
    assertThat(functionNames, hasSize(3));
    assertThat(functionNames.get(0).getName(), is(FuzzyFunction.FUNCTION_NAME.getName()));
    assertThat(functionNames.get(1).getName(), is(ProximityFunction.FUNCTION_NAME.getName()));
    assertThat(functionNames.get(2).getName(), is(DivisibleByFunction.FUNCTION_NAME.getName()));
  }

  @Test
  public void testFunctionForUnimplementedName() {
    assertThat(toTest.function("", null, null), nullValue());
  }

  @Test
  public void testFunctionForValidNameWithNullExpressionList() {
    assertThrows(
        NullPointerException.class,
        () -> toTest.function(FuzzyFunction.FUNCTION_NAME.getName(), null, null));
  }

  @Test
  public void testFunctionForValidNameWithTooManyExpressions() {
    List<Expression> expr = new ArrayList<>();
    expr.add(Expression.NIL);
    expr.add(Expression.NIL);
    assertThrows(
        IllegalArgumentException.class,
        () -> toTest.function(FuzzyFunction.FUNCTION_NAME.getName(), expr, null));
  }

  @Test
  public void testFuzzyFunction() {
    List<Expression> expr = new ArrayList<>();
    expr.add(Expression.NIL);
    Function result = toTest.function(FuzzyFunction.FUNCTION_NAME.getName(), expr, null);
    assertThat(result.getName(), is(FuzzyFunction.FUNCTION_NAME.getName()));
  }

  @Test
  public void testProximityFunction() {
    List<Expression> expr = new ArrayList<>();
    expr.add(Expression.NIL);
    expr.add(Expression.NIL);
    expr.add(Expression.NIL);
    Function result = toTest.function(ProximityFunction.FUNCTION_NAME.getName(), expr, null);
    assertThat(result.getName(), is(ProximityFunction.FUNCTION_NAME.getName()));
  }
}
