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
package org.codice.ddf.security.handler.pki;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import java.io.IOException;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.OcspService;
import org.codice.ddf.security.handler.AuthenticationTokenFactory;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Test certificate validation and revocation checking for PKIHandler. */
@ExtendWith(MockitoExtension.class)
public class PKIHandlerCertificateValidationTest {

  private PKIHandler handler;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;
  @Mock private AuthenticationTokenFactory tokenFactory;
  @Mock private CrlChecker crlChecker;
  @Mock private OcspService ocspService;
  @Mock private SecurityLogger securityLogger;
  @Mock private X509Certificate[] certificates;
  @Mock private AuthenticationToken authToken;

  @BeforeEach
  public void setup() {
    handler = new PKIHandler();
    handler.tokenFactory = tokenFactory;
    handler.crlChecker = crlChecker;
    handler.ocspService = ocspService;

    when(request.getServletPath()).thenReturn("/test/path");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
  }

  @Test
  public void testGetAuthenticationType() {
    assertThat(handler.getAuthenticationType(), is("PKI"));
  }

  @Test
  public void testNoCertificates() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(null);
    when(tokenFactory.fromCertificates(null, "127.0.0.1")).thenReturn(null);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
    assertThat(result.getSource(), is("PKIHandler"));
  }

  @Test
  public void testValidCertificates() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(true);
    when(ocspService.passesOcspCheck(certificates)).thenReturn(true);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
    assertThat(result.getSource(), is("PKIHandler"));
  }

  @Test
  public void testCertificateFailsCrlCheck() throws Exception {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(false);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
    verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
  }

  @Test
  public void testCertificateFailsOcspCheck() throws Exception {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(true);
    when(ocspService.passesOcspCheck(certificates)).thenReturn(false);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
    verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
  }

  @Test
  public void testCrlCheckerInitializedWhenNull() {
    handler.crlChecker = null;

    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(ocspService.passesOcspCheck(certificates)).thenReturn(true);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(handler.crlChecker, is(notNullValue()));
  }

  @Test
  public void testNullHttpResponse() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(true);
    when(ocspService.passesOcspCheck(certificates)).thenReturn(true);

    HandlerResult result = handler.getNormalizedToken(request, null, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
  }

  @Test
  public void testNullHttpResponseWithResolveFlag() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(null);

    HandlerResult result = handler.getNormalizedToken(request, null, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testRevokedCertificateWithNullResponse() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(false);

    HandlerResult result = handler.getNormalizedToken(request, null, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testIOExceptionDuringSendError() throws IOException {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(false);
    doThrow(new IOException("Test exception"))
        .when(response)
        .sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testHandleError() {
    HandlerResult result = handler.handleError(request, response, filterChain);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
    assertThat(result.getSource(), is("PKIHandler"));
  }

  @Test
  public void testResolveFlagTrue() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(true);
    when(ocspService.passesOcspCheck(certificates)).thenReturn(true);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
  }

  @Test
  public void testResolveFlagFalse() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(true);
    when(ocspService.passesOcspCheck(certificates)).thenReturn(true);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
  }

  @Test
  public void testCertificateArrayWithMultipleCerts() {
    X509Certificate[] multipleCerts = new X509Certificate[3];
    multipleCerts[0] = mock(X509Certificate.class);
    multipleCerts[1] = mock(X509Certificate.class);
    multipleCerts[2] = mock(X509Certificate.class);

    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(multipleCerts);
    when(tokenFactory.fromCertificates(multipleCerts, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(multipleCerts)).thenReturn(true);
    when(ocspService.passesOcspCheck(multipleCerts)).thenReturn(true);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
  }

  @Test
  public void testSourceIsSetCorrectly() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(null);
    when(tokenFactory.fromCertificates(null, "127.0.0.1")).thenReturn(null);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getSource(), is("PKIHandler"));
  }

  @Test
  public void testDifferentRemoteAddress() {
    when(request.getRemoteAddr()).thenReturn("192.168.1.100");
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "192.168.1.100")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(true);
    when(ocspService.passesOcspCheck(certificates)).thenReturn(true);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    verify(tokenFactory, times(1)).fromCertificates(certificates, "192.168.1.100");
  }

  @Test
  public void testBothCrlAndOcspFail() {
    when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certificates);
    when(tokenFactory.fromCertificates(certificates, "127.0.0.1")).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certificates)).thenReturn(false);
    when(ocspService.passesOcspCheck(certificates)).thenReturn(false);

    HandlerResult result = handler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
    verify(ocspService, never()).passesOcspCheck(any());
  }
}
