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
package org.codice.ddf.security.handler.basic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Test edge cases and error handling for BasicAuthenticationHandler. */
public class BasicAuthenticationHandlerEdgeCaseTest {

  private BasicAuthenticationHandler handler;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    handler = new BasicAuthenticationHandler();

    when(request.getServletPath()).thenReturn("/test/path");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
  }

  @Test
  public void testGetAuthenticationType() {
    assertThat(handler.getAuthenticationType(), is("BASIC"));
  }

  @Test
  public void testNoAuthorizationHeader() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
    assertThat(result.getSource(), is("BasicHandler"));
  }

  @Test
  public void testEmptyAuthorizationHeader() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testWhitespaceOnlyAuthorizationHeader() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("   ");

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testValidBasicAuthHeader() {
    String credentials =
        Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testBasicAuthHeaderWithExtraSpaces() {
    String credentials =
        Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("  Basic   " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testBasicAuthHeaderCaseInsensitive() {
    String credentials =
        Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testBasicAuthHeaderMixedCase() {
    String credentials =
        Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("BaSiC " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testInvalidAuthScheme() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token123");

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testMalformedAuthHeaderNoSpace() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("BasicNoSpace");

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testMalformedAuthHeaderMultipleSpaces() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic part1 part2 part3");

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testInvalidBase64Encoding() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic !!!invalid-base64!!!");

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testValidBase64ButNoColon() {
    String credentials =
        Base64.getEncoder().encodeToString("userpassword".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testEmptyUsername() {
    String credentials =
        Base64.getEncoder().encodeToString(":password".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testEmptyPassword() {
    String credentials =
        Base64.getEncoder().encodeToString("username:".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testUsernameWithColon() {
    String credentials =
        Base64.getEncoder().encodeToString("user:name:password".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testHandleError() {
    HandlerResult result = handler.handleError(request, response, filterChain);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
    assertThat(result.getSource(), is("BasicHandler"));
    verify(response, times(1)).setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic");
    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testAuthPromptIOException() throws IOException {
    doThrow(new IOException("Test exception")).when(response).flushBuffer();

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    // Exception should be caught and logged, not propagated
  }

  @Test
  public void testResolveFlagTrue() {
    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testResolveFlagFalse() {
    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testSpecialCharactersInPassword() {
    String credentials =
        Base64.getEncoder().encodeToString("user:p@$$w0rd!#%".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testUnicodeCharactersInCredentials() {
    String credentials =
        Base64.getEncoder()
            .encodeToString("user\u00E9:p\u00E4ssw\u00F6rd".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testVeryLongCredentials() {
    String longUsername = "user".repeat(100);
    String longPassword = "pass".repeat(100);
    String credentials =
        Base64.getEncoder()
            .encodeToString((longUsername + ":" + longPassword).getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testSourceIsSetCorrectly() {
    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getSource(), is("BasicHandler"));
  }

  @Test
  public void testMultipleColonsInCredentials() {
    String credentials =
        Base64.getEncoder().encodeToString("user:pass:word:extra".getBytes(StandardCharsets.UTF_8));
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " + credentials);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }
}
