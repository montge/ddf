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
package ddf.camel.component.catalog.framework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class IngestTimeoutExceptionTest {

  private static final String TEST_MESSAGE = "Ingest operation timed out";

  @Test
  void testDefaultConstructor() {
    IngestTimeoutException exception = new IngestTimeoutException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageConstructor() {
    IngestTimeoutException exception = new IngestTimeoutException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    IngestTimeoutException exception = new IngestTimeoutException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    IngestTimeoutException exception = new IngestTimeoutException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testExtendsFrameworkProducerException() {
    IngestTimeoutException exception = new IngestTimeoutException();

    assertThat(exception, instanceOf(FrameworkProducerException.class));
  }

  @Test
  void testIsCheckedException() {
    IngestTimeoutException exception = new IngestTimeoutException();

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  @Test
  void testWithNullMessage() {
    IngestTimeoutException exception = new IngestTimeoutException((String) null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  void testWithNullCause() {
    IngestTimeoutException exception = new IngestTimeoutException((Throwable) null);

    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testWithEmptyMessage() {
    IngestTimeoutException exception = new IngestTimeoutException("");

    assertThat(exception.getMessage(), is(""));
  }

  @Test
  void testWithNestedCause() {
    Throwable rootCause = new IllegalStateException("root");
    Throwable intermediateCause = new RuntimeException("intermediate", rootCause);
    IngestTimeoutException exception = new IngestTimeoutException(TEST_MESSAGE, intermediateCause);

    assertThat(exception.getCause(), is(sameInstance(intermediateCause)));
    assertThat(exception.getCause().getCause(), is(sameInstance(rootCause)));
  }
}
