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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationChallengeExceptionTest {

  @Test
  public void testDefaultConstructor() {
    AuthenticationChallengeException exception = new AuthenticationChallengeException();

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessage() {
    String message = "Authentication challenge required";
    AuthenticationChallengeException exception = new AuthenticationChallengeException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessage() {
    AuthenticationChallengeException exception =
        new AuthenticationChallengeException((String) null);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithEmptyMessage() {
    AuthenticationChallengeException exception = new AuthenticationChallengeException("");

    assertThat(exception.getMessage(), is(""));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithCause() {
    Throwable cause = new IllegalArgumentException("Challenge cause");
    AuthenticationChallengeException exception = new AuthenticationChallengeException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
    assertThat(exception.getMessage(), is(cause.toString()));
  }

  @Test
  public void testConstructorWithNullCause() {
    AuthenticationChallengeException exception =
        new AuthenticationChallengeException((Throwable) null);

    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndCause() {
    String message = "Challenge required";
    Throwable cause = new IllegalStateException("Challenge cause");
    AuthenticationChallengeException exception =
        new AuthenticationChallengeException(message, cause);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
  }

  @Test
  public void testConstructorWithMessageAndNullCause() {
    String message = "Challenge required";
    AuthenticationChallengeException exception =
        new AuthenticationChallengeException(message, null);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessageAndCause() {
    Throwable cause = new RuntimeException("Challenge cause");
    AuthenticationChallengeException exception = new AuthenticationChallengeException(null, cause);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new AuthenticationChallengeException("Test challenge exception");
    } catch (AuthenticationChallengeException e) {
      assertThat(e.getMessage(), is("Test challenge exception"));
    }
  }

  @Test
  public void testExceptionInheritance() {
    AuthenticationChallengeException exception = new AuthenticationChallengeException("Test");

    assertThat(exception instanceof AuthenticationException, is(true));
    assertThat(exception instanceof Exception, is(true));
    assertThat(exception instanceof Throwable, is(true));
  }

  @Test
  public void testExceptionCanBeCaughtAsAuthenticationException() {
    try {
      throw new AuthenticationChallengeException("Challenge");
    } catch (AuthenticationException e) {
      assertThat(e instanceof AuthenticationChallengeException, is(true));
      assertThat(e.getMessage(), is("Challenge"));
    }
  }

  @Test
  public void testExceptionWithNestedCauses() {
    Throwable deepCause = new IllegalArgumentException("Deep cause");
    Throwable middleCause = new IllegalStateException("Middle cause", deepCause);
    AuthenticationChallengeException exception =
        new AuthenticationChallengeException("Top message", middleCause);

    assertThat(exception.getMessage(), is("Top message"));
    assertThat(exception.getRootCause(), is(sameInstance(middleCause)));
    assertThat(exception.getCause(), is(sameInstance(middleCause)));
    assertThat(exception.getCause().getCause(), is(sameInstance(deepCause)));
  }

  @Test
  public void testGetRootCauseInherited() {
    Throwable rootCause = new Exception("Root cause");
    AuthenticationChallengeException exception =
        new AuthenticationChallengeException("Test", rootCause);

    assertThat(exception.getRootCause(), is(sameInstance(rootCause)));
  }
}
