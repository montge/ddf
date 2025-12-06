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
public class AuthenticationExceptionTest {

  @Test
  public void testDefaultConstructor() {
    AuthenticationException exception = new AuthenticationException();

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessage() {
    String message = "Authentication failed";
    AuthenticationException exception = new AuthenticationException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessage() {
    AuthenticationException exception = new AuthenticationException((String) null);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithEmptyMessage() {
    AuthenticationException exception = new AuthenticationException("");

    assertThat(exception.getMessage(), is(""));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithCause() {
    Throwable cause = new IllegalArgumentException("Root cause");
    AuthenticationException exception = new AuthenticationException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
    assertThat(exception.getMessage(), is(cause.toString()));
  }

  @Test
  public void testConstructorWithNullCause() {
    AuthenticationException exception = new AuthenticationException((Throwable) null);

    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndCause() {
    String message = "Authentication failed";
    Throwable cause = new IllegalStateException("Root cause");
    AuthenticationException exception = new AuthenticationException(message, cause);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
  }

  @Test
  public void testConstructorWithMessageAndNullCause() {
    String message = "Authentication failed";
    AuthenticationException exception = new AuthenticationException(message, null);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessageAndCause() {
    Throwable cause = new RuntimeException("Root cause");
    AuthenticationException exception = new AuthenticationException(null, cause);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getRootCause(), is(sameInstance(cause)));
  }

  @Test
  public void testGetRootCauseReturnsStoredCause() {
    Throwable rootCause = new Exception("Original cause");
    AuthenticationException exception = new AuthenticationException("Test", rootCause);

    assertThat(exception.getRootCause(), is(sameInstance(rootCause)));
  }

  @Test
  public void testGetRootCauseReturnsNullWhenNotSet() {
    AuthenticationException exception = new AuthenticationException("Test message");

    assertThat(exception.getRootCause(), is(nullValue()));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new AuthenticationException("Test authentication exception");
    } catch (AuthenticationException e) {
      assertThat(e.getMessage(), is("Test authentication exception"));
    }
  }

  @Test
  public void testExceptionInheritance() {
    AuthenticationException exception = new AuthenticationException("Test");

    assertThat(exception instanceof Exception, is(true));
    assertThat(exception instanceof Throwable, is(true));
  }

  @Test
  public void testExceptionWithNestedCauses() {
    Throwable deepCause = new IllegalArgumentException("Deep cause");
    Throwable middleCause = new IllegalStateException("Middle cause", deepCause);
    AuthenticationException exception = new AuthenticationException("Top message", middleCause);

    assertThat(exception.getMessage(), is("Top message"));
    assertThat(exception.getRootCause(), is(sameInstance(middleCause)));
    assertThat(exception.getCause(), is(sameInstance(middleCause)));
    assertThat(exception.getCause().getCause(), is(sameInstance(deepCause)));
  }
}
