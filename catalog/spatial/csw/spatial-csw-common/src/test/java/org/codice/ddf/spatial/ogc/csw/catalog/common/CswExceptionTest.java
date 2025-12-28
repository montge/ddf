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
package org.codice.ddf.spatial.ogc.csw.catalog.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.HttpURLConnection;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link CswException}. */
public class CswExceptionTest {

  private static final String TEST_MESSAGE = "Test exception message";
  private static final String EXCEPTION_CODE = "InvalidParameterValue";
  private static final String LOCATOR = "service";
  private static final int CUSTOM_HTTP_STATUS = HttpURLConnection.HTTP_NOT_FOUND;

  @Test
  public void testCswExceptionWithMessage() {
    CswException exception = new CswException("Test CSW exception");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test CSW exception"));
  }

  @Test
  public void testCswExceptionWithMessageAndCause() {
    Throwable cause = new RuntimeException("Cause exception");
    CswException exception = new CswException("Test CSW exception", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test CSW exception"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testCswExceptionWithNullMessage() {
    CswException exception = new CswException((String) null);
    assertThat(exception, is(notNullValue()));
  }

  @Test
  public void testCswExceptionWithEmptyMessage() {
    CswException exception = new CswException("");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(""));
  }

  @Test
  public void testCswExceptionWithNullCause() {
    CswException exception = new CswException("Test message", null);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test message"));
    // When null is passed as cause, getCause() returns null
  }

  @Test
  public void testCswExceptionChaining() {
    Throwable rootCause = new IllegalArgumentException("Root cause");
    Throwable intermediateCause = new IllegalStateException("Intermediate cause", rootCause);
    CswException exception = new CswException("Top level exception", intermediateCause);

    assertThat(exception.getCause(), is(intermediateCause));
    assertThat(exception.getCause().getCause(), is(rootCause));
  }

  @Test
  public void testConstructorWithThrowable() {
    Throwable cause = new RuntimeException("Root cause");
    CswException exception = new CswException(cause);

    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    assertThat(exception.getExceptionCode(), is(nullValue()));
    assertThat(exception.getLocator(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndHttpStatus() {
    CswException exception = new CswException(TEST_MESSAGE, CUSTOM_HTTP_STATUS);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getHttpStatus(), is(CUSTOM_HTTP_STATUS));
    assertThat(exception.getExceptionCode(), is(nullValue()));
    assertThat(exception.getLocator(), is(nullValue()));
  }

  @Test
  public void testConstructorWithThrowableAndHttpStatus() {
    Throwable cause = new RuntimeException("Root cause");
    CswException exception = new CswException(cause, CUSTOM_HTTP_STATUS);

    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getHttpStatus(), is(CUSTOM_HTTP_STATUS));
    assertThat(exception.getExceptionCode(), is(nullValue()));
    assertThat(exception.getLocator(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageExceptionCodeAndLocator() {
    CswException exception = new CswException(TEST_MESSAGE, EXCEPTION_CODE, LOCATOR);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getExceptionCode(), is(EXCEPTION_CODE));
    assertThat(exception.getLocator(), is(LOCATOR));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testConstructorWithThrowableExceptionCodeAndLocator() {
    Throwable cause = new RuntimeException("Root cause");
    CswException exception = new CswException(cause, EXCEPTION_CODE, LOCATOR);

    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getExceptionCode(), is(EXCEPTION_CODE));
    assertThat(exception.getLocator(), is(LOCATOR));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testConstructorWithMessageHttpStatusExceptionCodeAndLocator() {
    CswException exception =
        new CswException(TEST_MESSAGE, CUSTOM_HTTP_STATUS, EXCEPTION_CODE, LOCATOR);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getHttpStatus(), is(CUSTOM_HTTP_STATUS));
    assertThat(exception.getExceptionCode(), is(EXCEPTION_CODE));
    assertThat(exception.getLocator(), is(LOCATOR));
  }

  @Test
  public void testConstructorWithThrowableHttpStatusExceptionCodeAndLocator() {
    Throwable cause = new RuntimeException("Root cause");
    CswException exception = new CswException(cause, CUSTOM_HTTP_STATUS, EXCEPTION_CODE, LOCATOR);

    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getHttpStatus(), is(CUSTOM_HTTP_STATUS));
    assertThat(exception.getExceptionCode(), is(EXCEPTION_CODE));
    assertThat(exception.getLocator(), is(LOCATOR));
  }

  @Test
  public void testConstructorWithMessageThrowableExceptionCodeAndLocator() {
    Throwable cause = new RuntimeException("Root cause");
    CswException exception = new CswException(TEST_MESSAGE, cause, EXCEPTION_CODE, LOCATOR);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getExceptionCode(), is(EXCEPTION_CODE));
    assertThat(exception.getLocator(), is(LOCATOR));
    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
  }

  @Test
  public void testSetHttpStatus() {
    CswException exception = new CswException(TEST_MESSAGE);
    exception.setHttpStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);

    assertThat(exception.getHttpStatus(), is(HttpURLConnection.HTTP_INTERNAL_ERROR));
  }

  @Test
  public void testSetExceptionCode() {
    CswException exception = new CswException(TEST_MESSAGE);
    exception.setExceptionCode("MissingParameterValue");

    assertThat(exception.getExceptionCode(), is("MissingParameterValue"));
  }

  @Test
  public void testSetLocator() {
    CswException exception = new CswException(TEST_MESSAGE);
    exception.setLocator("request");

    assertThat(exception.getLocator(), is("request"));
  }

  @Test
  public void testDefaultHttpStatus() {
    CswException exception = new CswException(TEST_MESSAGE);

    // Default should be HTTP_BAD_REQUEST (400)
    assertThat(exception.getHttpStatus(), is(400));
  }
}
