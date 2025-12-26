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
package ddf.catalog.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class ResourceExceptionsTest {

  private static final String TEST_MESSAGE = "Resource operation failed";

  // ResourceNotFoundException tests

  @Test
  void testResourceNotFoundExceptionDefaultConstructor() {
    ResourceNotFoundException exception = new ResourceNotFoundException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testResourceNotFoundExceptionMessageConstructor() {
    ResourceNotFoundException exception = new ResourceNotFoundException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testResourceNotFoundExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    ResourceNotFoundException exception = new ResourceNotFoundException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testResourceNotFoundExceptionCauseOnlyConstructor() {
    Throwable cause = new RuntimeException("root cause");
    ResourceNotFoundException exception = new ResourceNotFoundException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testResourceNotFoundExceptionIsCheckedException() {
    ResourceNotFoundException exception = new ResourceNotFoundException();

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  // ResourceNotSupportedException tests

  @Test
  void testResourceNotSupportedExceptionDefaultConstructor() {
    ResourceNotSupportedException exception = new ResourceNotSupportedException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testResourceNotSupportedExceptionMessageConstructor() {
    ResourceNotSupportedException exception = new ResourceNotSupportedException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testResourceNotSupportedExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    ResourceNotSupportedException exception =
        new ResourceNotSupportedException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testResourceNotSupportedExceptionCauseOnlyConstructor() {
    Throwable cause = new RuntimeException("root cause");
    ResourceNotSupportedException exception = new ResourceNotSupportedException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testResourceNotSupportedExceptionIsCheckedException() {
    ResourceNotSupportedException exception = new ResourceNotSupportedException();

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
