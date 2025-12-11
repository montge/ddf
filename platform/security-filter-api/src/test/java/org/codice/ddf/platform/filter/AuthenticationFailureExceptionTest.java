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
package org.codice.ddf.platform.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthenticationFailureExceptionTest {

  @Test
  public void testDefaultConstructor() {
    AuthenticationFailureException exception = new AuthenticationFailureException();

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessage() {
    String message = "Authentication failed";
    AuthenticationFailureException exception = new AuthenticationFailureException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessage() {
    AuthenticationFailureException exception = new AuthenticationFailureException((String) null);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithEmptyMessage() {
    AuthenticationFailureException exception = new AuthenticationFailureException("");

    assertThat(exception.getMessage(), is(""));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithCause() {
    Throwable cause = new IllegalArgumentException("Failure cause");
    AuthenticationFailureException exception = new AuthenticationFailureException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
    assertThat(exception.getMessage(), is(cause.toString()));
  }

  @Test
  public void testConstructorWithNullCause() {
    AuthenticationFailureException exception = new AuthenticationFailureException((Throwable) null);

    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndCause() {
    String message = "Authentication failed";
    Throwable cause = new IllegalStateException("Failure cause");
    AuthenticationFailureException exception = new AuthenticationFailureException(message, cause);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
  }

  @Test
  public void testConstructorWithMessageAndNullCause() {
    String message = "Authentication failed";
    AuthenticationFailureException exception = new AuthenticationFailureException(message, null);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessageAndCause() {
    Throwable cause = new RuntimeException("Failure cause");
    AuthenticationFailureException exception = new AuthenticationFailureException(null, cause);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new AuthenticationFailureException("Test failure exception");
    } catch (AuthenticationFailureException e) {
      assertThat(e.getMessage(), is("Test failure exception"));
    }
  }

  @Test
  public void testExceptionInheritance() {
    AuthenticationFailureException exception = new AuthenticationFailureException("Test");

    assertThat(exception instanceof AuthenticationException, is(true));
    assertThat(exception instanceof Exception, is(true));
    assertThat(exception instanceof Throwable, is(true));
  }

  @Test
  public void testExceptionCanBeCaughtAsAuthenticationException() {
    try {
      throw new AuthenticationFailureException("Failure");
    } catch (AuthenticationException e) {
      assertThat(e instanceof AuthenticationFailureException, is(true));
      assertThat(e.getMessage(), is("Failure"));
    }
  }

  @Test
  public void testExceptionWithNestedCauses() {
    Throwable deepCause = new IllegalArgumentException("Deep cause");
    Throwable middleCause = new IllegalStateException("Middle cause", deepCause);
    AuthenticationFailureException exception =
        new AuthenticationFailureException("Top message", middleCause);

    assertThat(exception.getMessage(), is("Top message"));
    assertThat(exception.getRootCause(), is(sameInstance(middleCause)));
    assertThat(exception.getCause(), is(sameInstance(middleCause)));
    assertThat(exception.getCause().getCause(), is(sameInstance(deepCause)));
  }

  @Test
  public void testGetRootCauseInherited() {
    Throwable rootCause = new Exception("Root cause");
    AuthenticationFailureException exception =
        new AuthenticationFailureException("Test", rootCause);

    assertThat(exception.getRootCause(), is(sameInstance(rootCause)));
  }
}
