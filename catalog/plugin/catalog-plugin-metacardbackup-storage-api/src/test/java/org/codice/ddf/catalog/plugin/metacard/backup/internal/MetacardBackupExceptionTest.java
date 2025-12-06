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
package org.codice.ddf.catalog.plugin.metacard.backup.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.Test;

/** Tests for {@link MetacardBackupException} class. */
public class MetacardBackupExceptionTest {

  private static final String TEST_MESSAGE = "Test exception message";
  private static final String TEST_CAUSE_MESSAGE = "Test cause message";

  @Test
  public void testNoArgConstructor() {
    MetacardBackupException exception = new MetacardBackupException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testStringConstructor() {
    MetacardBackupException exception = new MetacardBackupException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testStringConstructorWithNull() {
    MetacardBackupException exception = new MetacardBackupException((String) null);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testStringConstructorWithEmptyString() {
    MetacardBackupException exception = new MetacardBackupException("");

    assertThat(exception.getMessage(), is(""));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testThrowableConstructor() {
    Throwable cause = new RuntimeException(TEST_CAUSE_MESSAGE);
    MetacardBackupException exception = new MetacardBackupException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getCause().getMessage(), is(TEST_CAUSE_MESSAGE));
  }

  @Test
  public void testThrowableConstructorWithNull() {
    MetacardBackupException exception = new MetacardBackupException((Throwable) null);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testMessageAndThrowableConstructor() {
    Throwable cause = new IllegalArgumentException(TEST_CAUSE_MESSAGE);
    MetacardBackupException exception = new MetacardBackupException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getCause().getMessage(), is(TEST_CAUSE_MESSAGE));
  }

  @Test
  public void testMessageAndThrowableConstructorWithNullMessage() {
    Throwable cause = new IllegalStateException(TEST_CAUSE_MESSAGE);
    MetacardBackupException exception = new MetacardBackupException(null, cause);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  public void testMessageAndThrowableConstructorWithNullThrowable() {
    MetacardBackupException exception = new MetacardBackupException(TEST_MESSAGE, null);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testMessageAndThrowableConstructorWithBothNull() {
    MetacardBackupException exception = new MetacardBackupException(null, null);

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testExceptionIsInstanceOfException() {
    MetacardBackupException exception = new MetacardBackupException();

    assertThat(exception, is(instanceOf(Exception.class)));
  }

  @Test
  public void testExceptionIsThrowable() {
    MetacardBackupException exception = new MetacardBackupException();

    assertThat(exception, is(instanceOf(Throwable.class)));
  }

  @Test
  public void testExceptionExtendsExceptionNotRuntimeException() {
    // Verify that MetacardBackupException extends Exception (checked exception)
    // by ensuring it's an Exception but the superclass is not RuntimeException
    assertThat(Exception.class.isAssignableFrom(MetacardBackupException.class), is(true));
    assertThat(RuntimeException.class.isAssignableFrom(MetacardBackupException.class), is(false));
  }

  @Test
  public void testSerialVersionUIDExists() {
    // This test verifies that the serialVersionUID field exists and can be accessed
    // indirectly by ensuring the class is serializable
    MetacardBackupException exception = new MetacardBackupException(TEST_MESSAGE);

    assertThat(exception, is(instanceOf(java.io.Serializable.class)));
  }

  @Test
  public void testExceptionCanBeThrown() throws MetacardBackupException {
    boolean exceptionThrown = false;
    try {
      throw new MetacardBackupException(TEST_MESSAGE);
    } catch (MetacardBackupException e) {
      exceptionThrown = true;
      assertThat(e.getMessage(), is(TEST_MESSAGE));
    }
    assertThat(exceptionThrown, is(true));
  }

  @Test
  public void testExceptionWithNestedCause() {
    Throwable rootCause = new IllegalArgumentException("Root cause");
    Throwable intermediateCause = new IllegalStateException("Intermediate", rootCause);
    MetacardBackupException exception =
        new MetacardBackupException(TEST_MESSAGE, intermediateCause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(intermediateCause)));
    assertThat(exception.getCause().getCause(), is(sameInstance(rootCause)));
  }

  @Test
  public void testExceptionMessagePreservation() {
    String longMessage =
        "This is a longer message with special characters: !@#$%^&*()_+-=[]{}|;':\",./<>?";
    MetacardBackupException exception = new MetacardBackupException(longMessage);

    assertThat(exception.getMessage(), is(longMessage));
  }

  @Test
  public void testMultipleExceptionsAreIndependent() {
    MetacardBackupException exception1 = new MetacardBackupException("Message 1");
    MetacardBackupException exception2 = new MetacardBackupException("Message 2");

    assertThat(exception1.getMessage(), is("Message 1"));
    assertThat(exception2.getMessage(), is("Message 2"));
    assertThat(exception1 == exception2, is(false));
  }
}
