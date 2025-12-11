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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opengis.filter.expression.Expression;

public class ProximityFunctionTest {

  @Test
  public void testVerifyProximityFunctionCannotBeCalledWithNull() {
    assertThrows(NullPointerException.class, () -> new ProximityFunction(null, null));
  }

  @Test
  public void testVerifyProximityFunctionCannotBeCalledWithMoreThanOneParameter() {
    List<Expression> exprs = new ArrayList<>();
    exprs.add(Expression.NIL);
    exprs.add(Expression.NIL);
    assertThrows(IllegalArgumentException.class, () -> new ProximityFunction(exprs, null));
  }

  @Test
  public void testVerifyProximityFunction() {
    List<Expression> exprs = new ArrayList<>();
    exprs.add(Expression.NIL);
    exprs.add(Expression.NIL);
    exprs.add(Expression.NIL);
    ProximityFunction func = new ProximityFunction(exprs, null);
    assertThat(func.getName(), is(ProximityFunction.FUNCTION_NAME.getName()));
  }
}
