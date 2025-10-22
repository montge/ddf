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
package org.codice.ddf.platform.error.handler.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultErrorHandlerTest {

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  private DefaultErrorHandler errorHandler;

  private ByteArrayOutputStream outputStream;

  private ServletOutputStream servletOutputStream;

  @Before
  public void setUp() throws IOException {
    outputStream = new ByteArrayOutputStream();
    servletOutputStream =
        new ServletOutputStream() {
          @Override
          public void write(int b) throws IOException {
            outputStream.write(b);
          }

          @Override
          public boolean isReady() {
            return true;
          }

          @Override
          public void setWriteListener(WriteListener writeListener) {}
        };

    when(mockResponse.getOutputStream()).thenReturn(servletOutputStream);
    errorHandler = new DefaultErrorHandler();
  }

  @Test
  public void testHandleError404() {
    errorHandler.handleError(404, "Not Found", "", null, "/test/path", mockRequest, mockResponse);

    verify(mockResponse).setStatus(404);
    verify(mockResponse).setContentType("text/html");

    String output = outputStream.toString();
    assertThat(output, containsString("404"));
    assertThat(output, containsString("Not Found"));
  }

  @Test
  public void testHandleError500() {
    errorHandler.handleError(
        500, "Internal Server Error", "", null, "/test/path", mockRequest, mockResponse);

    verify(mockResponse).setStatus(500);
    verify(mockResponse).setContentType("text/html");

    String output = outputStream.toString();
    assertThat(output, containsString("500"));
    assertThat(output, containsString("Internal Server Error"));
  }

  @Test
  public void testHandleError400() {
    errorHandler.handleError(400, "Bad Request", "", null, "/test/path", mockRequest, mockResponse);

    verify(mockResponse).setStatus(400);

    String output = outputStream.toString();
    assertThat(output, containsString("400"));
    assertThat(output, containsString("Bad Request"));
  }

  @Test
  public void testHandleError401() {
    errorHandler.handleError(
        401, "Unauthorized", "", null, "/test/path", mockRequest, mockResponse);

    verify(mockResponse).setStatus(401);

    String output = outputStream.toString();
    assertThat(output, containsString("401"));
    assertThat(output, containsString("Unauthorized"));
  }

  @Test
  public void testHandleError403() {
    errorHandler.handleError(403, "Forbidden", "", null, "/test/path", mockRequest, mockResponse);

    verify(mockResponse).setStatus(403);

    String output = outputStream.toString();
    assertThat(output, containsString("403"));
    assertThat(output, containsString("Forbidden"));
  }

  @Test
  public void testHandleError502() {
    errorHandler.handleError(502, "Bad Gateway", "", null, "/test/path", mockRequest, mockResponse);

    verify(mockResponse).setStatus(502);

    String output = outputStream.toString();
    assertThat(output, containsString("502"));
    assertThat(output, containsString("Bad Gateway"));
  }

  @Test
  public void testHandleError503() {
    errorHandler.handleError(
        503, "Service Unavailable", "", null, "/test/path", mockRequest, mockResponse);

    verify(mockResponse).setStatus(503);

    String output = outputStream.toString();
    assertThat(output, containsString("503"));
    assertThat(output, containsString("Service Unavailable"));
  }

  @Test
  public void testHandleErrorWithCodeMessage() {
    errorHandler.handleError(404, "Not Found", "", null, "/test/path", mockRequest, mockResponse);

    String output = outputStream.toString();
    // Should include the code message from properties file
    assertThat(output, containsString("could not be found"));
  }

  @Test
  public void testHandleErrorWithUri() {
    String testUri = "/admin/console/test";
    errorHandler.handleError(404, "Not Found", "", null, testUri, mockRequest, mockResponse);

    // URI is replaced in template but we can verify the handler was called
    verify(mockResponse).setStatus(404);
  }

  @Test
  public void testHandleErrorWithException() {
    RuntimeException exception = new RuntimeException("Test exception");
    errorHandler.handleError(
        500,
        "Internal Server Error",
        "RuntimeException",
        exception,
        "/test",
        mockRequest,
        mockResponse);

    verify(mockResponse).setStatus(500);

    String output = outputStream.toString();
    assertThat(output, containsString("500"));
  }

  @Test
  public void testHandleErrorWithNullMessage() {
    errorHandler.handleError(500, null, "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(500);
    verify(mockResponse).setContentType("text/html");
  }

  @Test
  public void testHandleErrorWithNullUri() {
    errorHandler.handleError(404, "Not Found", "", null, null, mockRequest, mockResponse);

    verify(mockResponse).setStatus(404);

    String output = outputStream.toString();
    assertThat(output, containsString("404"));
  }

  @Test
  public void testHandleErrorWithLongMessage() {
    String longMessage = "Error message ".repeat(50);
    errorHandler.handleError(500, longMessage, "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(500);

    String output = outputStream.toString();
    assertThat(output, containsString(longMessage));
  }

  @Test
  public void testHandleErrorWithSpecialCharactersInMessage() {
    String message = "Error: <script>alert('xss')</script>";
    errorHandler.handleError(500, message, "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(500);

    String output = outputStream.toString();
    assertThat(output, containsString(message));
  }

  @Test
  public void testHandleErrorWithExceptionType() {
    errorHandler.handleError(
        500,
        "Internal Server Error",
        "java.lang.NullPointerException",
        null,
        "/test",
        mockRequest,
        mockResponse);

    verify(mockResponse).setStatus(500);
  }

  @Test
  public void testHandleErrorSetsContentType() {
    errorHandler.handleError(404, "Not Found", "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setContentType("text/html");
  }

  @Test
  public void testHandleErrorMultipleTimes() {
    errorHandler.handleError(404, "Not Found", "", null, "/test1", mockRequest, mockResponse);
    errorHandler.handleError(500, "Server Error", "", null, "/test2", mockRequest, mockResponse);

    // Verify both calls set status correctly
    verify(mockResponse).setStatus(404);
    verify(mockResponse).setStatus(500);
  }

  @Test
  public void testHandleErrorWithZeroCode() {
    errorHandler.handleError(0, "Unknown Error", "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(0);
  }

  @Test
  public void testHandleErrorWith405Code() {
    errorHandler.handleError(
        405, "Method Not Allowed", "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(405);

    String output = outputStream.toString();
    assertThat(output, containsString("405"));
  }

  @Test
  public void testHandleErrorWith406Code() {
    errorHandler.handleError(406, "Not Acceptable", "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(406);

    String output = outputStream.toString();
    assertThat(output, containsString("406"));
  }

  @Test
  public void testHandleErrorWith501Code() {
    errorHandler.handleError(501, "Not Implemented", "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(501);

    String output = outputStream.toString();
    assertThat(output, containsString("501"));
  }

  @Test
  public void testHandleErrorWith504Code() {
    errorHandler.handleError(504, "Gateway Timeout", "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(504);

    String output = outputStream.toString();
    assertThat(output, containsString("504"));
  }

  @Test
  public void testHandleErrorReplacesCodePlaceholder() {
    errorHandler.handleError(404, "Not Found", "", null, "/test", mockRequest, mockResponse);

    String output = outputStream.toString();
    assertThat(output, containsString("404"));
  }

  @Test
  public void testHandleErrorReplacesMessagePlaceholder() {
    errorHandler.handleError(500, "Server Error", "", null, "/test", mockRequest, mockResponse);

    String output = outputStream.toString();
    assertThat(output, containsString("Server Error"));
  }

  @Test
  public void testHandleErrorIncludesErrorHtml() {
    errorHandler.handleError(404, "Not Found", "", null, "/test", mockRequest, mockResponse);

    String output = outputStream.toString();
    // Verify HTML structure is present
    assertThat(output, containsString("error-code"));
    assertThat(output, containsString("error-message"));
  }

  @Test
  public void testHandleErrorLoadsCodeMessages() {
    errorHandler.handleError(400, "Bad Request", "", null, "/test", mockRequest, mockResponse);

    String output = outputStream.toString();
    // Verify code message from properties file is included
    assertThat(output, containsString("Bad request"));
  }

  @Test
  public void testHandleErrorWith403IncludesLogoutLink() {
    errorHandler.handleError(403, "Forbidden", "", null, "/admin/test", mockRequest, mockResponse);

    String output = outputStream.toString();
    // 403 message includes logout link
    assertThat(output, containsString("log out"));
  }

  @Test
  public void testHandleErrorWithEmptyMessage() {
    errorHandler.handleError(404, "", "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(404);

    String output = outputStream.toString();
    assertThat(output, containsString("404"));
  }

  @Test
  public void testHandleErrorWithEmptyType() {
    errorHandler.handleError(500, "Error", "", null, "/test", mockRequest, mockResponse);

    verify(mockResponse).setStatus(500);
  }

  @Test
  public void testHandleErrorWithComplexUri() {
    String complexUri = "/admin/console?param1=value1&param2=value2#section";
    errorHandler.handleError(404, "Not Found", "", null, complexUri, mockRequest, mockResponse);

    verify(mockResponse).setStatus(404);
  }

  @Test
  public void testHandleErrorWithServletException() {
    Exception servletException = new Exception("Servlet processing error");
    errorHandler.handleError(
        500,
        "Internal Server Error",
        "javax.servlet.ServletException",
        servletException,
        "/test",
        mockRequest,
        mockResponse);

    verify(mockResponse).setStatus(500);
  }

  @Test
  public void testHandleErrorWithIOException() {
    IOException ioException = new IOException("IO error");
    errorHandler.handleError(
        500,
        "Internal Server Error",
        "java.io.IOException",
        ioException,
        "/test",
        mockRequest,
        mockResponse);

    verify(mockResponse).setStatus(500);
  }

  @Test
  public void testHandleErrorWithNestedExceptions() {
    Exception rootCause = new IllegalArgumentException("Root cause");
    RuntimeException nestedException = new RuntimeException("Nested exception", rootCause);

    errorHandler.handleError(
        500,
        "Internal Server Error",
        "java.lang.RuntimeException",
        nestedException,
        "/test",
        mockRequest,
        mockResponse);

    verify(mockResponse).setStatus(500);
  }

  @Test
  public void testConstructorLoadsCodeMessages() {
    // Constructor is called in setUp
    // Verify that error handler can handle different error codes with proper messages
    errorHandler.handleError(404, "Not Found", "", null, "/test", mockRequest, mockResponse);

    String output = outputStream.toString();
    assertThat(output, containsString("could not be found"));
  }

  @Test
  public void testHandleErrorWritesOutputToStream() {
    errorHandler.handleError(500, "Test Error", "", null, "/test", mockRequest, mockResponse);

    // Verify output was written
    assertThat(outputStream.size() > 0, is(true));
  }

  @Test
  public void testHandleErrorIncludesAllPlaceholders() {
    errorHandler.handleError(500, "Test Message", "", null, "/test/uri", mockRequest, mockResponse);

    String output = outputStream.toString();
    // All placeholders should be replaced
    assertThat(output, containsString("500"));
    assertThat(output, containsString("Test Message"));
  }
}
