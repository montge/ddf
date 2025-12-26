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
package ddf.catalog.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class EventExceptionsTest {

  private static final String TEST_MESSAGE = "Test exception message";

  @Test
  void testEventExceptionDefaultConstructor() {
    EventException exception = new EventException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testEventExceptionMessageConstructor() {
    EventException exception = new EventException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testEventExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    EventException exception = new EventException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testEventExceptionCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    EventException exception = new EventException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testEventExceptionIsCheckedException() {
    EventException exception = new EventException();

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  @Test
  void testSubscriptionExistsExceptionDefaultConstructor() {
    SubscriptionExistsException exception = new SubscriptionExistsException();

    assertThat(exception, is(notNullValue()));
  }

  @Test
  void testSubscriptionExistsExceptionExtendsEventException() {
    SubscriptionExistsException exception = new SubscriptionExistsException();

    assertThat(exception, instanceOf(EventException.class));
  }

  @Test
  void testSubscriptionExistsExceptionIsCheckedException() {
    SubscriptionExistsException exception = new SubscriptionExistsException();

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
