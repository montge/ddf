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
package org.codice.ddf.spatial.ogc.wfs.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.net.HttpURLConnection;
import org.junit.jupiter.api.Test;

class WfsExceptionTest {

  private static final String TEST_MESSAGE = "WFS error occurred";

  @Test
  void testMessageConstructor() {
    WfsException exception = new WfsException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  void testCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    WfsException exception = new WfsException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  void testMessageAndHttpStatusConstructor() {
    WfsException exception = new WfsException(TEST_MESSAGE, HttpURLConnection.HTTP_NOT_FOUND);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
  }

  @Test
  void testCauseAndHttpStatusConstructor() {
    Throwable cause = new RuntimeException("root cause");
    WfsException exception = new WfsException(cause, HttpURLConnection.HTTP_INTERNAL_ERROR);

    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_INTERNAL_ERROR));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    WfsException exception = new WfsException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  void testSetHttpStatus() {
    WfsException exception = new WfsException(TEST_MESSAGE);

    exception.setHttpStatus(HttpURLConnection.HTTP_UNAUTHORIZED);

    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_UNAUTHORIZED));
  }

  @Test
  void testIsCheckedException() {
    WfsException exception = new WfsException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
