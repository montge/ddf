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
package org.codice.ddf.spatial.ogc.wcs.catalog;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.HttpURLConnection;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link WcsException}. */
public class WcsExceptionTest {

  @Test
  public void testWcsExceptionWithMessage() {
    WcsException exception = new WcsException("Test WCS exception");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test WCS exception"));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testWcsExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Cause exception");
    WcsException exception = new WcsException(cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testWcsExceptionWithMessageAndHttpStatus() {
    WcsException exception =
        new WcsException("Test WCS exception", HttpURLConnection.HTTP_INTERNAL_ERROR);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test WCS exception"));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_INTERNAL_ERROR));
  }

  @Test
  public void testWcsExceptionWithThrowableAndHttpStatus() {
    Throwable cause = new IllegalArgumentException("Invalid argument");
    WcsException exception = new WcsException(cause, HttpURLConnection.HTTP_NOT_FOUND);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
  }

  @Test
  public void testWcsExceptionWithMessageAndThrowable() {
    Throwable cause = new RuntimeException("Root cause");
    WcsException exception = new WcsException("Test WCS exception", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test WCS exception"));
    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testWcsExceptionWithNullMessage() {
    WcsException exception = new WcsException((String) null);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testWcsExceptionWithEmptyMessage() {
    WcsException exception = new WcsException("");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(""));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testWcsExceptionWithNullThrowable() {
    WcsException exception = new WcsException("Test message", null);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test message"));
    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testWcsExceptionChaining() {
    Throwable rootCause = new IllegalArgumentException("Root cause");
    Throwable intermediateCause = new IllegalStateException("Intermediate cause", rootCause);
    WcsException exception = new WcsException("Top level exception", intermediateCause);

    assertThat(exception.getCause(), is(intermediateCause));
    assertThat(exception.getCause().getCause(), is(rootCause));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testSetHttpStatus() {
    WcsException exception = new WcsException("Test exception");
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));

    exception.setHttpStatus(HttpURLConnection.HTTP_FORBIDDEN);
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_FORBIDDEN));
  }

  @Test
  public void testHttpStatusVariations() {
    WcsException exception200 = new WcsException("OK", HttpURLConnection.HTTP_OK);
    assertThat(exception200.getHttpStatus(), is(HttpURLConnection.HTTP_OK));

    WcsException exception404 = new WcsException("Not found", HttpURLConnection.HTTP_NOT_FOUND);
    assertThat(exception404.getHttpStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));

    WcsException exception500 =
        new WcsException("Server error", HttpURLConnection.HTTP_INTERNAL_ERROR);
    assertThat(exception500.getHttpStatus(), is(HttpURLConnection.HTTP_INTERNAL_ERROR));

    WcsException exception503 = new WcsException("Unavailable", HttpURLConnection.HTTP_UNAVAILABLE);
    assertThat(exception503.getHttpStatus(), is(HttpURLConnection.HTTP_UNAVAILABLE));
  }

  @Test
  public void testSerializableWithSerialVersionUID() {
    // Verify that WcsException is properly serializable by checking it extends Exception
    WcsException exception = new WcsException("Serializable test");
    assertThat(exception instanceof Exception, is(true));
    assertThat(exception instanceof java.io.Serializable, is(true));
  }
}
