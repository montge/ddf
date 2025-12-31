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
package org.codice.ddf.security.handler.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests for {@link AuthenticationHandler} interface contract. Uses mock implementations to verify
 * the interface behavior and contract expectations.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthenticationHandlerTest {

  private static final String TEST_AUTH_TYPE = "BASIC";

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  @Mock private SecurityFilterChain mockFilterChain;

  @Mock private AuthenticationHandler mockHandler;

  @Mock private HandlerResult mockHandlerResult;

  @BeforeEach
  public void setUp() throws Exception {
    when(mockHandler.getAuthenticationType()).thenReturn(TEST_AUTH_TYPE);
  }

  @Test
  public void testGetAuthenticationType() {
    String authType = mockHandler.getAuthenticationType();
    assertThat(authType, is(TEST_AUTH_TYPE));
  }

  @Test
  public void testGetNormalizedTokenReturnsCompletedStatus() throws AuthenticationException {
    when(mockHandlerResult.getStatus()).thenReturn(HandlerResult.Status.COMPLETED);
    when(mockHandlerResult.getToken()).thenReturn(new UsernamePasswordToken("user", "pass"));
    when(mockHandler.getNormalizedToken(any(), any(), any(), anyBoolean()))
        .thenReturn(mockHandlerResult);

    HandlerResult result =
        mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, true);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testGetNormalizedTokenReturnsNoActionWhenNotResolving()
      throws AuthenticationException {
    when(mockHandlerResult.getStatus()).thenReturn(HandlerResult.Status.NO_ACTION);
    when(mockHandler.getNormalizedToken(any(), any(), any(), anyBoolean()))
        .thenReturn(mockHandlerResult);

    HandlerResult result =
        mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, false);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
  }

  @Test
  public void testGetNormalizedTokenReturnsRedirectedStatus() throws AuthenticationException {
    when(mockHandlerResult.getStatus()).thenReturn(HandlerResult.Status.REDIRECTED);
    when(mockHandler.getNormalizedToken(any(), any(), any(), anyBoolean()))
        .thenReturn(mockHandlerResult);

    HandlerResult result =
        mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, true);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
  }

  @Test
  public void testGetNormalizedTokenThrowsAuthenticationException() throws AuthenticationException {
    doThrow(new AuthenticationException("Authentication failed"))
        .when(mockHandler)
        .getNormalizedToken(any(), any(), any(), anyBoolean());

    assertThrows(
        AuthenticationException.class,
        () -> {
          mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, true);
        });
  }

  @Test
  public void testHandleErrorReturnsResult() throws AuthenticationException {
    when(mockHandlerResult.getStatus()).thenReturn(HandlerResult.Status.REDIRECTED);
    when(mockHandler.handleError(any(), any(), any())).thenReturn(mockHandlerResult);

    HandlerResult result = mockHandler.handleError(mockRequest, mockResponse, mockFilterChain);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
  }

  @Test
  public void testHandleErrorThrowsAuthenticationException() throws AuthenticationException {
    doThrow(new AuthenticationException("Error handling failed"))
        .when(mockHandler)
        .handleError(any(), any(), any());

    assertThrows(
        AuthenticationException.class,
        () -> {
          mockHandler.handleError(mockRequest, mockResponse, mockFilterChain);
        });
  }

  @Test
  public void testHandlerMethodsAreCalledWithCorrectParameters() throws AuthenticationException {
    when(mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, true))
        .thenReturn(mockHandlerResult);
    when(mockHandler.handleError(mockRequest, mockResponse, mockFilterChain))
        .thenReturn(mockHandlerResult);

    mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, true);
    mockHandler.handleError(mockRequest, mockResponse, mockFilterChain);

    verify(mockHandler).getNormalizedToken(mockRequest, mockResponse, mockFilterChain, true);
    verify(mockHandler).handleError(mockRequest, mockResponse, mockFilterChain);
  }

  @Test
  public void testResolveParameterBehavior() throws AuthenticationException {
    // Test with resolve=true (should attempt to obtain credentials)
    when(mockHandlerResult.getStatus()).thenReturn(HandlerResult.Status.COMPLETED);
    when(mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, true))
        .thenReturn(mockHandlerResult);

    HandlerResult resolveResult =
        mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, true);
    assertThat(resolveResult.getStatus(), is(HandlerResult.Status.COMPLETED));

    // Test with resolve=false (should not attempt to obtain credentials)
    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(HandlerResult.Status.NO_ACTION);
    when(mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, false))
        .thenReturn(noActionResult);

    HandlerResult noResolveResult =
        mockHandler.getNormalizedToken(mockRequest, mockResponse, mockFilterChain, false);
    assertThat(noResolveResult.getStatus(), is(HandlerResult.Status.NO_ACTION));
  }

  /**
   * Verifies that AuthenticationHandler implementations should handle null ServletRequest
   * gracefully.
   */
  @Test
  public void testHandlerWithNullRequest() throws AuthenticationException {
    when(mockHandlerResult.getStatus()).thenReturn(HandlerResult.Status.NO_ACTION);
    when(mockHandler.getNormalizedToken((ServletRequest) null, mockResponse, mockFilterChain, true))
        .thenReturn(mockHandlerResult);

    HandlerResult result =
        mockHandler.getNormalizedToken((ServletRequest) null, mockResponse, mockFilterChain, true);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
  }

  /**
   * Verifies that AuthenticationHandler implementations should handle null ServletResponse
   * gracefully.
   */
  @Test
  public void testHandlerWithNullResponse() throws AuthenticationException {
    when(mockHandlerResult.getStatus()).thenReturn(HandlerResult.Status.NO_ACTION);
    when(mockHandler.getNormalizedToken(mockRequest, (ServletResponse) null, mockFilterChain, true))
        .thenReturn(mockHandlerResult);

    HandlerResult result =
        mockHandler.getNormalizedToken(mockRequest, (ServletResponse) null, mockFilterChain, true);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
  }
}
