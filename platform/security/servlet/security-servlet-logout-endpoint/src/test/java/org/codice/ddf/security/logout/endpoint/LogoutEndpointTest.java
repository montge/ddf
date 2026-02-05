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
package org.codice.ddf.security.logout.endpoint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.service.SecurityServiceException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.codice.ddf.security.logout.service.LogoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link LogoutEndpoint} class. */
@ExtendWith(MockitoExtension.class)
public class LogoutEndpointTest {

  @Mock private LogoutService logoutService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  private LogoutEndpoint endpoint;

  @BeforeEach
  public void setUp() {
    endpoint = new LogoutEndpoint(logoutService);
  }

  @Test
  public void testConstructor() {
    assertThat(endpoint, is(notNullValue()));
  }

  @Test
  public void testIsHttpServlet() {
    assertThat(endpoint instanceof HttpServlet, is(true));
  }

  @Test
  public void testDoGetCallsLogoutService() throws Exception {
    String jsonResponse = "{\"actions\":[]}";
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);
    when(response.getWriter()).thenReturn(printWriter);

    endpoint.doGet(request, response);

    verify(logoutService).getActionProviders(request, response);
  }

  @Test
  public void testDoGetSetsCacheControlHeader() throws Exception {
    String jsonResponse = "{\"actions\":[]}";
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);
    when(response.getWriter()).thenReturn(printWriter);

    endpoint.doGet(request, response);

    verify(response).setHeader("Cache-Control", "no-cache, no-store");
  }

  @Test
  public void testDoGetSetsPragmaHeader() throws Exception {
    String jsonResponse = "{\"actions\":[]}";
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);
    when(response.getWriter()).thenReturn(printWriter);

    endpoint.doGet(request, response);

    verify(response).setHeader("Pragma", "no-cache");
  }

  @Test
  public void testDoGetWritesJsonToResponse() throws Exception {
    String jsonResponse = "{\"test\":\"value\"}";
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);
    when(response.getWriter()).thenReturn(printWriter);

    endpoint.doGet(request, response);
    printWriter.flush();

    assertThat(stringWriter.toString(), is(jsonResponse));
  }

  @Test
  public void testDoGetThrowsRuntimeExceptionOnSecurityServiceException() throws Exception {
    when(logoutService.getActionProviders(request, response))
        .thenThrow(new SecurityServiceException("Test error"));

    assertThrows(RuntimeException.class, () -> endpoint.doGet(request, response));
  }

  @Test
  public void testDoGetThrowsRuntimeExceptionOnIOException() throws Exception {
    String jsonResponse = "{\"actions\":[]}";
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);
    when(response.getWriter()).thenThrow(new IOException("Test error"));

    assertThrows(RuntimeException.class, () -> endpoint.doGet(request, response));
  }
}
