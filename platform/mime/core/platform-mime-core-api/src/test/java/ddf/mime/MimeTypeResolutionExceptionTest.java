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
package ddf.mime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class MimeTypeResolutionExceptionTest {

  private static final String TEST_MESSAGE = "Test error message";

  @Test
  void testDefaultConstructor() {
    MimeTypeResolutionException exception = new MimeTypeResolutionException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageConstructor() {
    MimeTypeResolutionException exception = new MimeTypeResolutionException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testThrowableConstructor() {
    Throwable cause = new RuntimeException("root cause");
    MimeTypeResolutionException exception = new MimeTypeResolutionException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testMessageAndThrowableConstructor() {
    Throwable cause = new RuntimeException("root cause");
    MimeTypeResolutionException exception = new MimeTypeResolutionException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testExceptionIsCheckedException() {
    MimeTypeResolutionException exception = new MimeTypeResolutionException();

    assertThat(exception instanceof Exception, is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
