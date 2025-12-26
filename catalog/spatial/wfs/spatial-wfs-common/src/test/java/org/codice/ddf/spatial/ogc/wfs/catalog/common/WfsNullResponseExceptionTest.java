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
package org.codice.ddf.spatial.ogc.wfs.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class WfsNullResponseExceptionTest {

  @Test
  void testDefaultConstructor() {
    WfsNullResponseException exception = new WfsNullResponseException();

    assertThat(exception.getMessage(), containsString("response is null"));
  }

  @Test
  void testMessageConstructor() {
    WfsNullResponseException exception =
        new WfsNullResponseException("Custom null response message");

    assertThat(exception.getMessage(), is("Custom null response message"));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    WfsNullResponseException exception = new WfsNullResponseException("Null response error", cause);

    assertThat(exception.getMessage(), is("Null response error"));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testExtendsWfsException() {
    WfsNullResponseException exception = new WfsNullResponseException();

    assertThat(exception, instanceOf(WfsException.class));
  }
}
