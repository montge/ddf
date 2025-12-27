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
package ddf.camel.component.catalog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import ddf.camel.component.catalog.content.ContentComponentException;
import ddf.camel.component.catalog.framework.FrameworkProducerException;
import ddf.camel.component.catalog.framework.IngestTimeoutException;
import ddf.camel.component.catalog.transformer.TransformerTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CamelExceptionsTest {

  private static final String TEST_MESSAGE = "Test error message";

  private Throwable testCause;

  @BeforeEach
  void setUp() {
    testCause = new RuntimeException("root cause");
  }

  @Test
  void testFrameworkProducerExceptionDefaultConstructor() {
    FrameworkProducerException exception = new FrameworkProducerException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testFrameworkProducerExceptionMessageConstructor() {
    FrameworkProducerException exception = new FrameworkProducerException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testFrameworkProducerExceptionThrowableConstructor() {
    FrameworkProducerException exception = new FrameworkProducerException(testCause);

    assertThat(exception.getCause(), is(sameInstance(testCause)));
  }

  @Test
  void testFrameworkProducerExceptionMessageAndThrowableConstructor() {
    FrameworkProducerException exception = new FrameworkProducerException(TEST_MESSAGE, testCause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(testCause)));
  }

  @Test
  void testFrameworkProducerExceptionIsCheckedException() {
    FrameworkProducerException exception = new FrameworkProducerException();

    assertThat(Exception.class.isAssignableFrom(exception.getClass()), is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  @Test
  void testContentComponentExceptionMessageConstructor() {
    ContentComponentException exception = new ContentComponentException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testContentComponentExceptionThrowableConstructor() {
    ContentComponentException exception = new ContentComponentException(testCause);

    assertThat(exception.getCause(), is(sameInstance(testCause)));
  }

  @Test
  void testContentComponentExceptionMessageAndThrowableConstructor() {
    ContentComponentException exception = new ContentComponentException(TEST_MESSAGE, testCause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(testCause)));
  }

  @Test
  void testContentComponentExceptionIsCheckedException() {
    ContentComponentException exception = new ContentComponentException(TEST_MESSAGE);

    assertThat(Exception.class.isAssignableFrom(exception.getClass()), is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  @Test
  void testTransformerTimeoutExceptionDefaultConstructor() {
    TransformerTimeoutException exception = new TransformerTimeoutException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testTransformerTimeoutExceptionMessageConstructor() {
    TransformerTimeoutException exception = new TransformerTimeoutException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testTransformerTimeoutExceptionThrowableConstructor() {
    TransformerTimeoutException exception = new TransformerTimeoutException(testCause);

    assertThat(exception.getCause(), is(sameInstance(testCause)));
  }

  @Test
  void testTransformerTimeoutExceptionMessageAndThrowableConstructor() {
    TransformerTimeoutException exception =
        new TransformerTimeoutException(TEST_MESSAGE, testCause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(testCause)));
  }

  @Test
  void testTransformerTimeoutExceptionIsRuntimeException() {
    TransformerTimeoutException exception = new TransformerTimeoutException();

    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(true));
  }

  // IngestTimeoutException tests

  @Test
  void testIngestTimeoutExceptionDefaultConstructor() {
    IngestTimeoutException exception = new IngestTimeoutException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testIngestTimeoutExceptionMessageConstructor() {
    IngestTimeoutException exception = new IngestTimeoutException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testIngestTimeoutExceptionThrowableConstructor() {
    IngestTimeoutException exception = new IngestTimeoutException(testCause);

    assertThat(exception.getCause(), is(sameInstance(testCause)));
  }

  @Test
  void testIngestTimeoutExceptionMessageAndThrowableConstructor() {
    IngestTimeoutException exception = new IngestTimeoutException(TEST_MESSAGE, testCause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(testCause)));
  }

  @Test
  void testIngestTimeoutExceptionExtendsFrameworkProducerException() {
    IngestTimeoutException exception = new IngestTimeoutException();

    assertThat(FrameworkProducerException.class.isAssignableFrom(exception.getClass()), is(true));
  }

  @Test
  void testIngestTimeoutExceptionIsCheckedException() {
    IngestTimeoutException exception = new IngestTimeoutException();

    assertThat(Exception.class.isAssignableFrom(exception.getClass()), is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
