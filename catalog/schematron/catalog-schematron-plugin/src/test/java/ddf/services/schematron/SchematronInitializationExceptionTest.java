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
package ddf.services.schematron;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class SchematronInitializationExceptionTest {

  private static final String TEST_MESSAGE = "Schematron initialization failed";

  @Test
  void testDefaultConstructor() {
    SchematronInitializationException exception = new SchematronInitializationException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageConstructor() {
    SchematronInitializationException exception =
        new SchematronInitializationException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageAndThrowableConstructor() {
    Throwable cause = new RuntimeException("root cause");
    SchematronInitializationException exception =
        new SchematronInitializationException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testThrowableConstructor() {
    Throwable cause = new RuntimeException("root cause");
    SchematronInitializationException exception = new SchematronInitializationException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testExceptionIsCheckedException() {
    SchematronInitializationException exception = new SchematronInitializationException();

    assertThat(Exception.class.isAssignableFrom(exception.getClass()), is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
