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
package org.codice.ddf.admin.configurator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class ConfiguratorExceptionTest {

  @Test
  void testExtendsRuntimeException() {
    ConfiguratorException exception = new ConfiguratorException("test");
    assertThat(exception, is(instanceOf(RuntimeException.class)));
  }

  @Test
  void testConstructorWithMessage() {
    String message = "Test error message";
    ConfiguratorException exception = new ConfiguratorException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testConstructorWithMessageAndCause() {
    String message = "Test error message";
    Throwable cause = new IllegalStateException("root cause");
    ConfiguratorException exception = new ConfiguratorException(message, cause);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testConstructorWithNullMessage() {
    ConfiguratorException exception = new ConfiguratorException(null);
    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  void testConstructorWithNullCause() {
    ConfiguratorException exception = new ConfiguratorException("message", null);
    assertThat(exception.getMessage(), is("message"));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testExceptionCanBeThrown() {
    try {
      throw new ConfiguratorException("thrown exception");
    } catch (ConfiguratorException e) {
      assertThat(e.getMessage(), is("thrown exception"));
    }
  }
}
