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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import org.apache.shiro.authc.AuthenticationToken;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthenticationHandlerEnhancedTest {

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private SecurityFilterChain chain;

  private BasicAuthenticationHandler handler;

  @Before
  public void setUp() {
    handler = new BasicAuthenticationHandler();
    when(request.getServletPath()).thenReturn("/services/catalog");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
  }

  @Test
  public void testGetAuthenticationType() {
    assertThat(handler.getAuthenticationType(), is("BASIC"));
  }

  @Test
  public void testHandleError() throws IOException {
    HandlerResult result = handler.handleError(request, response, chain);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getSource(), is("BasicHandler"));

    verify(response).setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic");
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentLength(0);
    verify(response).flushBuffer();
  }

  @Test
  public void testHandleErrorWithIOException() throws IOException {
    doThrow(new IOException("Connection closed")).when(response).flushBuffer();

    HandlerResult result = handler.handleError(request, response, chain);

    // Should not throw exception, should handle gracefully
    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
  }

  @Test
  public void testExtractAuthInfoWithEmptyUsername() {
    String credentials = ":" + "password";
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    // Should return null for empty username
    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithEmptyPassword() {
    String credentials = "username" + ":";
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    // Should return null for empty password
    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithPasswordContainingColon() {
    String username = "admin";
    String password = "pass:word:with:colons";
    String credentials = username + ":" + password;
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    assertThat(token, is(notNullValue()));
    // Password reconstruction should handle colons correctly
  }

  @Test
  public void testExtractAuthInfoWithInvalidBase64() {
    String authHeader = "Basic not-valid-base64!@#$%";

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithMalformedHeader() {
    String authHeader = "BasicNoSpace";

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithWrongScheme() {
    String credentials = "admin:password";
    String authHeader = "Bearer " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithWhitespace() {
    String credentials = "admin:password";
    String authHeader = "  Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    // Should handle leading whitespace
    assertThat(token, is(notNullValue()));
  }

  @Test
  public void testExtractAuthInfoCaseInsensitiveScheme() {
    String credentials = "admin:password";
    String authHeader = "basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    // Should handle lowercase "basic"
    assertThat(token, is(notNullValue()));
  }

  @Test
  public void testExtractAuthInfoWithSpecialCharactersInPassword() {
    String username = "user";
    String password = "p@ssw0rd!#$%^&*()";
    String credentials = username + ":" + password;
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    assertThat(token, is(notNullValue()));
  }

  @Test
  public void testExtractAuthInfoWithUnicodeCharacters() {
    String username = "user";
    String password = "пароль"; // Russian for "password"
    String credentials = username + ":" + password;
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    assertThat(token, is(notNullValue()));
  }

  @Test
  public void testExtractAuthenticationInfoWithNullHeader() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    AuthenticationToken token = handler.extractAuthenticationInfo(request);

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthenticationInfoWithEmptyHeader() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");

    AuthenticationToken token = handler.extractAuthenticationInfo(request);

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthenticationInfoWithWhitespaceHeader() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("   ");

    AuthenticationToken token = handler.extractAuthenticationInfo(request);

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testGetNormalizedTokenNoResolveNoCredentials() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    HandlerResult result = handler.getNormalizedToken(request, response, chain, false);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getSource(), is("BasicHandler"));
  }

  @Test
  public void testGetNormalizedTokenWithIpv6Address() {
    String credentials = "admin:password";
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
    when(request.getRemoteAddr()).thenReturn("::1");

    HandlerResult result = handler.getNormalizedToken(request, response, chain, true);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(notNullValue()));
  }

  @Test
  public void testExtractAuthInfoWithOnlyColon() {
    String credentials = ":";
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithNoColon() {
    String credentials = "usernameonly";
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithMultipleSpacesInHeader() {
    String credentials = "admin:password";
    String authHeader = "Basic  " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    // Should not handle multiple spaces (split on single space)
    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithExtraHeaderParts() {
    String credentials = "admin:password";
    String authHeader =
        "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes()) + " extra";

    AuthenticationToken token = handler.extractAuthInfo(authHeader, "127.0.0.1");

    // Should only accept two parts
    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithEmptyString() {
    AuthenticationToken token = handler.extractAuthInfo("", "127.0.0.1");

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testExtractAuthInfoWithNullIp() {
    String credentials = "admin:password";
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    AuthenticationToken token = handler.extractAuthInfo(authHeader, null);

    assertThat(token, is(notNullValue()));
  }

  @Test
  public void testHandlerResultSourceIsSet() {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    HandlerResult result = handler.getNormalizedToken(request, response, chain, true);

    assertThat(result.getSource(), is("BasicHandler"));
  }

  @Test
  public void testGetNormalizedTokenWithDifferentPaths() {
    String credentials = "admin:password";
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);

    String[] paths = {"/services/catalog", "/admin", "/search/catalog/query", "/", "/services/csw"};

    for (String path : paths) {
      when(request.getServletPath()).thenReturn(path);

      HandlerResult result = handler.getNormalizedToken(request, response, chain, true);

      assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    }
  }
}
