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
package ddf.catalog.security.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.Response;
import org.junit.jupiter.api.Test;

class FilterResultImplTest {

  @Test
  void testConstructorWithProcessedTrue() {
    FilterResultImpl result = new FilterResultImpl(true);

    assertThat(result.processed(), is(true));
    assertThat(result.metacard(), is(nullValue()));
    assertThat(result.response(), is(nullValue()));
  }

  @Test
  void testConstructorWithProcessedFalse() {
    FilterResultImpl result = new FilterResultImpl(false);

    assertThat(result.processed(), is(false));
    assertThat(result.metacard(), is(nullValue()));
    assertThat(result.response(), is(nullValue()));
  }

  @Test
  void testConstructorWithAllParameters() {
    Metacard metacard = mock(Metacard.class);
    Response response = mock(Response.class);

    FilterResultImpl result = new FilterResultImpl(metacard, response, true);

    assertThat(result.metacard(), is(sameInstance(metacard)));
    assertThat(result.response(), is(sameInstance(response)));
    assertThat(result.processed(), is(true));
  }

  @Test
  void testConstructorWithNullMetacardAndResponse() {
    FilterResultImpl result = new FilterResultImpl(null, null, false);

    assertThat(result.metacard(), is(nullValue()));
    assertThat(result.response(), is(nullValue()));
    assertThat(result.processed(), is(false));
  }

  @Test
  void testMetacardReturnsCorrectValue() {
    Metacard metacard = mock(Metacard.class);

    FilterResultImpl result = new FilterResultImpl(metacard, null, true);

    assertThat(result.metacard(), is(sameInstance(metacard)));
  }

  @Test
  void testResponseReturnsCorrectValue() {
    Response response = mock(Response.class);

    FilterResultImpl result = new FilterResultImpl(null, response, true);

    assertThat(result.response(), is(sameInstance(response)));
  }

  @Test
  void testProcessedReturnsCorrectValue() {
    FilterResultImpl resultTrue = new FilterResultImpl(true);
    FilterResultImpl resultFalse = new FilterResultImpl(false);

    assertThat(resultTrue.processed(), is(true));
    assertThat(resultFalse.processed(), is(false));
  }
}
