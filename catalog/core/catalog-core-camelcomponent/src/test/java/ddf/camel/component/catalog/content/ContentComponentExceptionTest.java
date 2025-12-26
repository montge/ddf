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
package ddf.camel.component.catalog.content;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class ContentComponentExceptionTest {

  private static final String TEST_MESSAGE = "Content component error";

  @Test
  void testMessageConstructor() {
    ContentComponentException exception = new ContentComponentException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    ContentComponentException exception = new ContentComponentException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    ContentComponentException exception = new ContentComponentException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testIsCheckedException() {
    ContentComponentException exception = new ContentComponentException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
