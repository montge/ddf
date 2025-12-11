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
package org.codice.ddf.platform.error.servlet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codice.ddf.platform.error.handler.ErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ErrorServletTest {

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  @Mock private ErrorHandler mockErrorHandler;

  private ErrorServlet errorServlet;

  @BeforeEach
  public void setUp() {
    errorServlet = new ErrorServlet();
  }

  @Test
  public void testDoGetDelegatesToHandleError() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(404);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Not Found");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test/path");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(404),
            eq("Not Found"),
            eq(""),
            isNull(),
            eq("/test/path"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testDoPostDelegatesToHandleError() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(500);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Internal Server Error");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test/path");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(500),
            eq("Internal Server Error"),
            eq(""),
            isNull(),
            eq("/test/path"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorWithAllAttributes() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(500);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Internal Server Error");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_EXCEPTION_TYPE))
        .thenReturn("java.lang.RuntimeException");
    Throwable exception = new RuntimeException("Test exception");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_EXCEPTION)).thenReturn(exception);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test/path");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(500),
            eq("Internal Server Error"),
            eq("java.lang.RuntimeException"),
            eq(exception),
            eq("/test/path"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorWithNullStatusCode() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(null);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Error");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    // Should default to 0 when status code is null
    verify(mockErrorHandler)
        .handleError(
            eq(0), eq("Error"), eq(""), isNull(), eq("/test"), eq(mockRequest), eq(mockResponse));
  }

  @Test
  public void testHandleErrorWithNullMessage() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(404);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn(null);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    // Should default to empty string when message is null
    verify(mockErrorHandler)
        .handleError(
            eq(404), eq(""), eq(""), isNull(), eq("/test"), eq(mockRequest), eq(mockResponse));
  }

  @Test
  public void testHandleErrorWithNullExceptionType() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(500);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Error");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_EXCEPTION_TYPE)).thenReturn(null);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    // Should default to empty string when exception type is null
    verify(mockErrorHandler)
        .handleError(
            eq(500), eq("Error"), eq(""), isNull(), eq("/test"), eq(mockRequest), eq(mockResponse));
  }

  @Test
  public void testHandleErrorWithNullException() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(404);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Not Found");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_EXCEPTION)).thenReturn(null);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(404),
            eq("Not Found"),
            eq(""),
            isNull(),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorParsesIntegerStatusCode() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn("404");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Not Found");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(404),
            eq("Not Found"),
            eq(""),
            isNull(),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorDefaultsTo500ForInvalidStatusCode() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn("invalid");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Error");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    // Should default to 500 for invalid status code
    verify(mockErrorHandler)
        .handleError(
            eq(500), eq("Error"), eq(""), isNull(), eq("/test"), eq(mockRequest), eq(mockResponse));
  }

  @Test
  public void testHandleErrorWith400StatusCode() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(400);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Bad Request");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(400),
            eq("Bad Request"),
            eq(""),
            isNull(),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorWith401StatusCode() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(401);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Unauthorized");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(401),
            eq("Unauthorized"),
            eq(""),
            isNull(),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorWith403StatusCode() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(403);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Forbidden");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(403),
            eq("Forbidden"),
            eq(""),
            isNull(),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorWith502StatusCode() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(502);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Bad Gateway");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(502),
            eq("Bad Gateway"),
            eq(""),
            isNull(),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorWithServletException() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    ServletException servletException = new ServletException("Servlet error");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(500);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Servlet Exception");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_EXCEPTION)).thenReturn(servletException);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(500),
            eq("Servlet Exception"),
            eq(""),
            eq(servletException),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorWithIOException() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    Exception ioException = new Exception("IO error");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(500);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("IO Exception");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_EXCEPTION)).thenReturn(ioException);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(500),
            eq("IO Exception"),
            eq(""),
            eq(ioException),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testErrorServletConstants() {
    assertThat(ErrorServlet.ERROR_EXCEPTION, is("javax.servlet.error.exception"));
    assertThat(ErrorServlet.ERROR_EXCEPTION_TYPE, is("javax.servlet.error.exception_type"));
    assertThat(ErrorServlet.ERROR_MESSAGE, is("javax.servlet.error.message"));
    assertThat(ErrorServlet.ERROR_REQUEST_URI, is("javax.servlet.error.request_uri"));
    assertThat(ErrorServlet.ERROR_SERVLET_NAME, is("javax.servlet.error.servlet_name"));
    assertThat(ErrorServlet.ERROR_STATUS_CODE, is("javax.servlet.error.status_code"));
  }

  @Test
  public void testHandleErrorWithComplexExceptionChain() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    Exception rootCause = new IllegalArgumentException("Root cause");
    RuntimeException wrappedException = new RuntimeException("Wrapped exception", rootCause);

    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(500);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Wrapped Exception");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_EXCEPTION)).thenReturn(wrappedException);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_EXCEPTION_TYPE))
        .thenReturn("java.lang.RuntimeException");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn("/test");

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(500),
            eq("Wrapped Exception"),
            eq("java.lang.RuntimeException"),
            eq(wrappedException),
            eq("/test"),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testHandleErrorWithLongUri() throws Exception {
    ErrorServlet servlet = new ErrorServletWithHandler(mockErrorHandler);

    String longUri = "/very/long/path/".repeat(50);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_STATUS_CODE)).thenReturn(404);
    when(mockRequest.getAttribute(ErrorServlet.ERROR_MESSAGE)).thenReturn("Not Found");
    when(mockRequest.getAttribute(ErrorServlet.ERROR_REQUEST_URI)).thenReturn(longUri);

    servlet.doGet(mockRequest, mockResponse);

    verify(mockErrorHandler)
        .handleError(
            eq(404),
            eq("Not Found"),
            eq(""),
            isNull(),
            eq(longUri),
            eq(mockRequest),
            eq(mockResponse));
  }

  @Test
  public void testInit() throws Exception {
    errorServlet.init();
    // Init should complete without error
    assertThat(errorServlet, is(notNullValue()));
  }

  /** Helper class to inject error handler for testing */
  private static class ErrorServletWithHandler extends ErrorServlet {
    private final ErrorHandler testErrorHandler;

    ErrorServletWithHandler(ErrorHandler errorHandler) {
      this.testErrorHandler = errorHandler;
    }

    @Override
    public void init() {
      // Skip OSGi service lookup in tests
    }

    @Override
    protected ErrorHandler getErrorHandler() {
      return testErrorHandler;
    }
  }
}
