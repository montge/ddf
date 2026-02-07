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
package org.codice.ddf.security.handler.oidc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.configuration.SystemBaseUrl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OidcCallbackEndpointTest {
  private static final int HTTP_REDIRECT = Status.SEE_OTHER.getStatusCode();

  private static OidcCallbackEndpointWithMockClient callbackEndpoint;

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  @Mock private HttpSession mockSession;

  private Response response;

  @BeforeAll
  public static void setupClass() {
    callbackEndpoint = new OidcCallbackEndpointWithMockClient(mock(SecurityLogger.class));
    callbackEndpoint.setRedirectUri("/logout");
  }

  @Test
  public void logoutWithNullRequest() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          response = callbackEndpoint.logout(null, mockResponse);
        });
  }

  @Test
  public void logoutWithNullResponse() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          response = callbackEndpoint.logout(mockRequest, null);
        });
  }

  @Test
  public void logoutWitNullSession() {
    when(mockRequest.getSession(any(Boolean.class))).thenReturn(null);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          response = callbackEndpoint.logout(mockRequest, mockResponse);
        });
  }

  @Test
  public void logout() throws Exception {
    when(mockRequest.getSession(any(Boolean.class))).thenReturn(mockSession);

    Response r = mock(Response.class);
    when(r.getStatus()).thenReturn(302);
    when(callbackEndpoint.webClient.get()).thenReturn(r);

    response = callbackEndpoint.logout(mockRequest, mockResponse);

    verify(mockSession, times(1)).invalidate();
    assertThat(response.getStatus(), is(HTTP_REDIRECT));
    assertThat(
        response.getHeaderString("Location"), is(SystemBaseUrl.EXTERNAL.constructUrl("/logout")));
  }

  static class OidcCallbackEndpointWithMockClient extends OidcCallbackEndpoint {
    WebClient webClient = mock(WebClient.class);

    public OidcCallbackEndpointWithMockClient(SecurityLogger securityLogger) {
      super(securityLogger);
    }

    @Override
    WebClient getWebClient(String logoutUrlString) {
      return webClient;
    }
  }
}
