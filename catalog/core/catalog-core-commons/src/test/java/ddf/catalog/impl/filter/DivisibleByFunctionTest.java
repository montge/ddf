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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotools.api.filter.expression.Expression;
import org.junit.jupiter.api.Test;

class DivisibleByFunctionTest {

  @Test
  void testConstructorWithValidParameters() {
    List<Expression> params = Arrays.asList(Expression.NIL, Expression.NIL);

    DivisibleByFunction function = new DivisibleByFunction(params, null);

    assertThat(function.getName(), is(DivisibleByFunction.FUNCTION_NAME_STRING));
  }

  @Test
  void testConstructorWithNullParametersThrowsException() {
    assertThrows(NullPointerException.class, () -> new DivisibleByFunction(null, null));
  }

  @Test
  void testConstructorWithTooFewParametersThrowsException() {
    List<Expression> params = Arrays.asList(Expression.NIL);

    assertThrows(IllegalArgumentException.class, () -> new DivisibleByFunction(params, null));
  }

  @Test
  void testConstructorWithEmptyParametersThrowsException() {
    List<Expression> params = new ArrayList<>();

    assertThrows(IllegalArgumentException.class, () -> new DivisibleByFunction(params, null));
  }

  @Test
  void testConstructorWithTooManyParametersThrowsException() {
    List<Expression> params = Arrays.asList(Expression.NIL, Expression.NIL, Expression.NIL);

    assertThrows(IllegalArgumentException.class, () -> new DivisibleByFunction(params, null));
  }

  @Test
  void testFunctionNameConstant() {
    assertThat(DivisibleByFunction.FUNCTION_NAME_STRING, is("divisibleBy"));
  }

  @Test
  void testNumParametersConstant() {
    assertThat(DivisibleByFunction.NUM_PARAMETERS, is(2));
  }

  @Test
  void testEqualsWithSameObject() {
    List<Expression> params = Arrays.asList(Expression.NIL, Expression.NIL);
    DivisibleByFunction function = new DivisibleByFunction(params, null);

    assertThat(function.equals(function), is(true));
  }

  @Test
  void testEqualsWithEqualObjects() {
    List<Expression> params1 = Arrays.asList(Expression.NIL, Expression.NIL);
    List<Expression> params2 = Arrays.asList(Expression.NIL, Expression.NIL);

    DivisibleByFunction function1 = new DivisibleByFunction(params1, null);
    DivisibleByFunction function2 = new DivisibleByFunction(params2, null);

    assertThat(function1.equals(function2), is(true));
  }

  @Test
  void testEqualsWithNull() {
    List<Expression> params = Arrays.asList(Expression.NIL, Expression.NIL);
    DivisibleByFunction function = new DivisibleByFunction(params, null);

    assertThat(function.equals(null), is(false));
  }

  @Test
  void testEqualsWithDifferentClass() {
    List<Expression> params = Arrays.asList(Expression.NIL, Expression.NIL);
    DivisibleByFunction function = new DivisibleByFunction(params, null);

    assertThat(function.equals("not a function"), is(false));
  }

  @Test
  void testHashCodeConsistentWithEquals() {
    List<Expression> params1 = Arrays.asList(Expression.NIL, Expression.NIL);
    List<Expression> params2 = Arrays.asList(Expression.NIL, Expression.NIL);

    DivisibleByFunction function1 = new DivisibleByFunction(params1, null);
    DivisibleByFunction function2 = new DivisibleByFunction(params2, null);

    assertThat(function1.hashCode(), is(function2.hashCode()));
  }

  @Test
  void testFunctionNameIsNotNull() {
    assertThat(DivisibleByFunction.FUNCTION_NAME, is(not((Object) null)));
  }
}
