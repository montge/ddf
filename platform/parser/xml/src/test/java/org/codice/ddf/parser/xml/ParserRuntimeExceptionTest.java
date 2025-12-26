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
package org.codice.ddf.parser.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class ParserRuntimeExceptionTest {

  private static final String TEST_MESSAGE = "XML parsing failed";

  @Test
  void testMessageAndCauseConstructor() {
    Exception cause = new RuntimeException("root cause");
    ParserRuntimeException exception = new ParserRuntimeException(TEST_MESSAGE, cause);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testExceptionIsRuntimeException() {
    ParserRuntimeException exception = new ParserRuntimeException(TEST_MESSAGE, null);

    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(true));
  }

  @Test
  void testNullMessage() {
    Exception cause = new RuntimeException("root cause");
    ParserRuntimeException exception = new ParserRuntimeException(null, cause);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testNullCause() {
    ParserRuntimeException exception = new ParserRuntimeException(TEST_MESSAGE, null);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }
}
