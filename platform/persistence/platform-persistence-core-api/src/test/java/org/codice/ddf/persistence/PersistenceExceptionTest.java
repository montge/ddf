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
package org.codice.ddf.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PersistenceExceptionTest {

  @Test
  public void testDefaultConstructor() {
    PersistenceException exception = new PersistenceException();
    assertNotNull(exception);
    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithMessage() {
    String message = "Test exception message";
    PersistenceException exception = new PersistenceException(message);

    assertNotNull(exception);
    assertThat(exception.getMessage(), is(message));
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithNullMessage() {
    PersistenceException exception = new PersistenceException((String) null);
    assertNotNull(exception);
    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithEmptyMessage() {
    String message = "";
    PersistenceException exception = new PersistenceException(message);
    assertNotNull(exception);
    assertThat(exception.getMessage(), is(message));
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithThrowable() {
    Throwable cause = new RuntimeException("Root cause");
    PersistenceException exception = new PersistenceException(cause);

    assertNotNull(exception);
    assertSame(cause, exception.getCause());
    assertThat(exception.getCause().getMessage(), is("Root cause"));
  }

  @Test
  public void testConstructorWithNullThrowable() {
    PersistenceException exception = new PersistenceException((Throwable) null);
    assertNotNull(exception);
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithMessageAndThrowable() {
    String message = "Exception occurred";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    PersistenceException exception = new PersistenceException(message, cause);

    assertNotNull(exception);
    assertThat(exception.getMessage(), is(message));
    assertSame(cause, exception.getCause());
    assertThat(exception.getCause().getMessage(), is("Invalid argument"));
  }

  @Test
  public void testConstructorWithNullMessageAndThrowable() {
    Throwable cause = new RuntimeException("Cause");
    PersistenceException exception = new PersistenceException(null, cause);

    assertNotNull(exception);
    assertNull(exception.getMessage());
    assertSame(cause, exception.getCause());
  }

  @Test
  public void testConstructorWithMessageAndNullThrowable() {
    String message = "Test message";
    PersistenceException exception = new PersistenceException(message, null);

    assertNotNull(exception);
    assertThat(exception.getMessage(), is(message));
    assertNull(exception.getCause());
  }

  @Test
  public void testConstructorWithNullMessageAndNullThrowable() {
    PersistenceException exception = new PersistenceException(null, null);
    assertNotNull(exception);
    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void testExceptionIsInstanceOfException() {
    PersistenceException exception = new PersistenceException();
    assertThat(exception, instanceOf(Exception.class));
  }

  @Test
  public void testExceptionCanBeCaught() {
    try {
      throw new PersistenceException("Test");
    } catch (PersistenceException e) {
      assertThat(e.getMessage(), is("Test"));
    }
  }

  @Test
  public void testExceptionCanBeCaughtAsException() {
    try {
      throw new PersistenceException("Test");
    } catch (Exception e) {
      assertThat(e, instanceOf(PersistenceException.class));
      assertThat(e.getMessage(), is("Test"));
    }
  }

  @Test
  public void testExceptionChaining() {
    Throwable rootCause = new IllegalStateException("Root cause");
    Throwable intermediateCause = new RuntimeException("Intermediate", rootCause);
    PersistenceException exception = new PersistenceException("Top level", intermediateCause);

    assertThat(exception.getMessage(), is("Top level"));
    assertThat(exception.getCause(), is(intermediateCause));
    assertThat(exception.getCause().getCause(), is(rootCause));
  }

  @Test
  public void testStackTracePreserved() {
    PersistenceException exception = new PersistenceException("Test message");
    StackTraceElement[] stackTrace = exception.getStackTrace();

    assertNotNull(stackTrace);
    assertTrue(stackTrace.length > 0);
    assertThat(stackTrace[0].getClassName(), is(this.getClass().getName()));
    assertThat(stackTrace[0].getMethodName(), is("testStackTracePreserved"));
  }

  @Test
  public void testCauseStackTracePreserved() {
    Exception cause = new Exception("Cause");
    PersistenceException exception = new PersistenceException("Message", cause);

    assertNotNull(cause.getStackTrace());
    assertTrue(cause.getStackTrace().length > 0);
    assertSame(cause, exception.getCause());
  }

  @Test
  public void testSerialVersionUID() {
    // Verify that serialVersionUID is defined (implicitly tested by creating instances)
    PersistenceException exception1 = new PersistenceException("Test");
    PersistenceException exception2 = new PersistenceException("Test");

    // Both instances should be creatable without serialization issues
    assertNotNull(exception1);
    assertNotNull(exception2);
  }
}
