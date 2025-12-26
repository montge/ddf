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

class WfsParseExceptionTest {

  private static final String BASE_MSG = "Error reading Response";

  @Test
  void testMessageConstructor() {
    WfsParseException exception = new WfsParseException("Invalid XML format");

    assertThat(exception.getMessage(), containsString(BASE_MSG));
    assertThat(exception.getMessage(), containsString("Invalid XML format"));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    WfsParseException exception = new WfsParseException("Parsing failed", cause);

    assertThat(exception.getMessage(), containsString(BASE_MSG));
    assertThat(exception.getMessage(), containsString("Parsing failed"));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    WfsParseException exception = new WfsParseException(cause);

    assertThat(exception.getMessage(), is(BASE_MSG));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testExtendsWfsException() {
    WfsParseException exception = new WfsParseException("test");

    assertThat(exception, instanceOf(WfsException.class));
  }
}
