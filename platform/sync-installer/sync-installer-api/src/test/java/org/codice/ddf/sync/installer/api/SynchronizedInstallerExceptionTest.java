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
package org.codice.ddf.sync.installer.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class SynchronizedInstallerExceptionTest {

  private static final String TEST_MESSAGE = "Test exception message";

  @Test
  void testDefaultConstructor() {
    SynchronizedInstallerException exception = new SynchronizedInstallerException();

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception, is(instanceOf(RuntimeException.class)));
  }

  @Test
  void testMessageConstructor() {
    SynchronizedInstallerException exception = new SynchronizedInstallerException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new IllegalStateException("Root cause");
    SynchronizedInstallerException exception =
        new SynchronizedInstallerException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  void testCauseConstructor() {
    Throwable cause = new IllegalStateException("Root cause");
    SynchronizedInstallerException exception = new SynchronizedInstallerException(cause);

    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getMessage(), is(cause.toString()));
  }

  @Test
  void testIsRuntimeException() {
    SynchronizedInstallerException exception = new SynchronizedInstallerException();

    assertThat(exception, is(instanceOf(RuntimeException.class)));
  }

  @Test
  void testCanBeThrown() {
    try {
      throw new SynchronizedInstallerException(TEST_MESSAGE);
    } catch (SynchronizedInstallerException e) {
      assertThat(e.getMessage(), is(TEST_MESSAGE));
    }
  }
}
