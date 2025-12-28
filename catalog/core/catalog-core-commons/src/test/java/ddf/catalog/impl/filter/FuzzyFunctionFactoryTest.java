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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class FuzzyFunctionFactoryTest {

  @Test
  public void testFuzzyFunctionFactoryExtendsGeoToolsFunctionFactory() {
    FuzzyFunctionFactory factory = new FuzzyFunctionFactory();

    assertThat(factory, is(notNullValue()));
    assertThat(factory, instanceOf(GeoToolsFunctionFactory.class));
  }

  @Test
  public void testGetFunctionNames() {
    FuzzyFunctionFactory factory = new FuzzyFunctionFactory();

    assertThat(factory.getFunctionNames(), is(notNullValue()));
  }
}
