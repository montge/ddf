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
package org.codice.ddf.commands.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class CatalogCommandExceptionTest {

  private static final String TEST_MESSAGE = "Command failed";

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    CatalogCommandException exception = new CatalogCommandException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testIsCheckedException() {
    Throwable cause = new RuntimeException("root cause");
    CatalogCommandException exception = new CatalogCommandException(TEST_MESSAGE, cause);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  @Test
  void testWithNullCause() {
    CatalogCommandException exception = new CatalogCommandException(TEST_MESSAGE, null);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is((Throwable) null));
  }
}
