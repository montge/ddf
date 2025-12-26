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
package ddf.catalog.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class IngestExceptionTest {

  private static final String TEST_MESSAGE = "Ingest failed";

  @Test
  void testDefaultConstructor() {
    IngestException exception = new IngestException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageConstructor() {
    IngestException exception = new IngestException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    IngestException exception = new IngestException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testCauseOnlyConstructor() {
    Throwable cause = new RuntimeException("root cause");
    IngestException exception = new IngestException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testIsCheckedException() {
    IngestException exception = new IngestException();

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  // InternalIngestException tests

  @Test
  void testInternalIngestExceptionDefaultConstructor() {
    InternalIngestException exception = new InternalIngestException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testInternalIngestExceptionMessageConstructor() {
    InternalIngestException exception = new InternalIngestException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testInternalIngestExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    InternalIngestException exception = new InternalIngestException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testInternalIngestExceptionCauseOnlyConstructor() {
    Throwable cause = new RuntimeException("root cause");
    InternalIngestException exception = new InternalIngestException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testInternalIngestExceptionExtendsIngestException() {
    InternalIngestException exception = new InternalIngestException();

    assertThat(exception, instanceOf(IngestException.class));
  }

  // UnsupportedQueryException tests

  @Test
  void testUnsupportedQueryExceptionDefaultConstructor() {
    UnsupportedQueryException exception = new UnsupportedQueryException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testUnsupportedQueryExceptionMessageConstructor() {
    UnsupportedQueryException exception = new UnsupportedQueryException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testUnsupportedQueryExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    UnsupportedQueryException exception = new UnsupportedQueryException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testUnsupportedQueryExceptionCauseOnlyConstructor() {
    Throwable cause = new RuntimeException("root cause");
    UnsupportedQueryException exception = new UnsupportedQueryException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testUnsupportedQueryExceptionIsCheckedException() {
    UnsupportedQueryException exception = new UnsupportedQueryException();

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
