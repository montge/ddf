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
package org.codice.ddf.catalog.download.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class ResourceDownloadActionExceptionTest {

  private static final String TEST_MESSAGE = "Resource download failed";

  @Test
  void testMessageConstructor() {
    ResourceDownloadActionException exception = new ResourceDownloadActionException(TEST_MESSAGE);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    ResourceDownloadActionException exception =
        new ResourceDownloadActionException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testExceptionIsRuntimeException() {
    ResourceDownloadActionException exception = new ResourceDownloadActionException(TEST_MESSAGE);

    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(true));
  }

  @Test
  void testNullMessage() {
    ResourceDownloadActionException exception = new ResourceDownloadActionException(null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  void testNullCause() {
    ResourceDownloadActionException exception =
        new ResourceDownloadActionException(TEST_MESSAGE, null);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }
}
