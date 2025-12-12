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
package ddf.security.samlp.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

public class ValidationExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "Validation failed";

    ValidationException exception = new ValidationException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndCause() {
    String message = "Validation failed";
    Exception cause = new IllegalArgumentException("Invalid input");

    ValidationException exception = new ValidationException(message, cause);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  public void testExceptionIsCheckedException() {
    ValidationException exception = new ValidationException("test");

    assertThat(exception, is(instanceOf(Exception.class)));
  }

  @Test
  public void testConstructorWithNullMessage() {
    ValidationException exception = new ValidationException(null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullCause() {
    ValidationException exception = new ValidationException("message", null);

    assertThat(exception.getMessage(), is("message"));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithEmptyMessage() {
    ValidationException exception = new ValidationException("");

    assertThat(exception.getMessage(), is(""));
  }

  @Test
  public void testExceptionChaining() {
    Exception rootCause = new RuntimeException("root cause");
    ValidationException exception = new ValidationException("validation error", rootCause);

    assertThat(exception.getCause(), is(sameInstance(rootCause)));
    assertThat(exception.getCause().getMessage(), is("root cause"));
  }
}
