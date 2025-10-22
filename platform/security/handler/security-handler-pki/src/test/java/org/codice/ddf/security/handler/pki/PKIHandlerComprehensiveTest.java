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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.OcspService;
import org.codice.ddf.security.handler.AuthenticationTokenFactory;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PKIHandlerComprehensiveTest {

  private static final String CERT_ATTRIBUTE = "javax.servlet.request.X509Certificate";
  private static final String TEST_PATH = "/services/catalog";
  private static final String TEST_IP = "192.168.1.100";

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private ServletRequest servletRequest;
  @Mock private ServletResponse servletResponse;
  @Mock private SecurityFilterChain filterChain;
  @Mock private AuthenticationTokenFactory tokenFactory;
  @Mock private BaseAuthenticationToken authToken;
  @Mock private CrlChecker crlChecker;
  @Mock private OcspService ocspService;
  @Mock private SecurityLogger securityLogger;

  private PKIHandler pkiHandler;
  private X509Certificate[] validCertChain;
  private X509Certificate[] revokedCertChain;

  @Before
  public void setUp() throws Exception {
    pkiHandler = new PKIHandler();
    pkiHandler.setTokenFactory(tokenFactory);
    pkiHandler.setOcspService(ocspService);
    pkiHandler.setSecurityLogger(securityLogger);

    // Create test certificates
    validCertChain = createTestCertificateChain();
    revokedCertChain = createTestCertificateChain();

    when(request.getServletPath()).thenReturn(TEST_PATH);
    when(request.getRemoteAddr()).thenReturn(TEST_IP);
  }

  // ==================== Successful Authentication Tests ====================

  @Test
  public void testValidCertificateAuthentication() {
    // Test successful authentication with valid certificate
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
    when(tokenFactory.fromCertificates(validCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(validCertChain)).thenReturn(true);
    when(ocspService.passesOcspCheck(validCertChain)).thenReturn(true);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result, notNullValue());
    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
    assertThat(result.getSource(), is("PKIHandler"));
    verify(tokenFactory).fromCertificates(validCertChain, TEST_IP);
  }

  @Test
  public void testValidCertificateWithResolveTrue() {
    // Test with resolve flag set to true
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
    when(tokenFactory.fromCertificates(validCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(validCertChain)).thenReturn(true);
    when(ocspService.passesOcspCheck(validCertChain)).thenReturn(true);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
  }

  @Test
  public void testValidCertificateWithResolveFalse() {
    // Test with resolve flag set to false
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
    when(tokenFactory.fromCertificates(validCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(validCertChain)).thenReturn(true);
    when(ocspService.passesOcspCheck(validCertChain)).thenReturn(true);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
  }

  // ==================== Certificate Revocation Tests ====================

  @Test
  public void testRevokedCertificateCrlCheck() throws Exception {
    // Test certificate revoked by CRL
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(revokedCertChain);
    when(tokenFactory.fromCertificates(revokedCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(revokedCertChain)).thenReturn(false);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    assertThat(result.getToken(), nullValue());
    verify(response)
        .sendError(eq(HttpServletResponse.SC_FORBIDDEN), eq("Your certificate is revoked."));
    verify(response).flushBuffer();
  }

  @Test
  public void testRevokedCertificateOcspCheck() throws Exception {
    // Test certificate revoked by OCSP
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(revokedCertChain);
    when(tokenFactory.fromCertificates(revokedCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(revokedCertChain)).thenReturn(true);
    when(ocspService.passesOcspCheck(revokedCertChain)).thenReturn(false);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
  }

  @Test
  public void testCrlCheckerNotInitialized() {
    // Test with null CRL checker (should initialize automatically)
    pkiHandler = new PKIHandler();
    pkiHandler.setTokenFactory(tokenFactory);
    pkiHandler.setOcspService(ocspService);
    pkiHandler.setSecurityLogger(securityLogger);
    // Don't set crlChecker - it should be created on demand

    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
    when(tokenFactory.fromCertificates(validCertChain, TEST_IP)).thenReturn(authToken);
    when(ocspService.passesOcspCheck(validCertChain)).thenReturn(true);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    // Should complete successfully even with auto-created CRL checker
    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
  }

  // ==================== No Certificate Tests ====================

  @Test
  public void testNoCertificatePresent() {
    // Test request without client certificate
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(null);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), nullValue());
    verify(tokenFactory, never()).fromCertificates(any(), anyString());
  }

  @Test
  public void testEmptyCertificateArray() {
    // Test with empty certificate array
    X509Certificate[] emptyCerts = new X509Certificate[0];
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(emptyCerts);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
  }

  @Test
  public void testNullTokenFromFactory() {
    // Test when token factory returns null
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
    when(tokenFactory.fromCertificates(validCertChain, TEST_IP)).thenReturn(null);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getToken(), nullValue());
  }

  // ==================== Response Handling Tests ====================

  @Test
  public void testNullHttpResponseWithResolveTrue() {
    // Test null HTTP response with resolve flag
    when(servletRequest.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
    when(tokenFactory.fromCertificates(validCertChain, null)).thenReturn(authToken);

    HandlerResult result =
        pkiHandler.getNormalizedToken(servletRequest, servletResponse, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
  }

  @Test
  public void testNullHttpResponseWithResolveFalse() {
    // Test null HTTP response without resolve
    when(servletRequest.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
    when(tokenFactory.fromCertificates(validCertChain, null)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(validCertChain)).thenReturn(true);
    when(ocspService.passesOcspCheck(validCertChain)).thenReturn(true);

    HandlerResult result =
        pkiHandler.getNormalizedToken(servletRequest, servletResponse, filterChain, false);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(result.getToken(), is(authToken));
  }

  @Test
  public void testRevokedCertWithNullResponse() {
    // Test revoked certificate with null HTTP response
    when(servletRequest.getAttribute(CERT_ATTRIBUTE)).thenReturn(revokedCertChain);
    when(tokenFactory.fromCertificates(revokedCertChain, null)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(revokedCertChain)).thenReturn(false);

    HandlerResult result =
        pkiHandler.getNormalizedToken(servletRequest, servletResponse, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    verify(servletResponse, never()).flushBuffer();
  }

  // ==================== Error Handling Tests ====================

  @Test
  public void testHandleError() {
    // Test error handler (should return NO_ACTION for PKI)
    HandlerResult result = pkiHandler.handleError(request, response, filterChain);

    assertThat(result, notNullValue());
    assertThat(result.getStatus(), is(HandlerResult.Status.NO_ACTION));
    assertThat(result.getSource(), is("PKIHandler"));
  }

  @Test
  public void testResponseSendErrorThrowsException() throws Exception {
    // Test exception during response.sendError
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(revokedCertChain);
    when(tokenFactory.fromCertificates(revokedCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(revokedCertChain)).thenReturn(false);
    when(response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your certificate is revoked."))
        .thenThrow(new java.io.IOException("Network error"));

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    // Should handle exception gracefully
    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
  }

  // ==================== Certificate Chain Tests ====================

  @Test
  public void testMultipleCertificatesInChain() {
    // Test with certificate chain (root, intermediate, leaf)
    X509Certificate[] certChain = createTestCertificateChain(3);
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(certChain);
    when(tokenFactory.fromCertificates(certChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(certChain)).thenReturn(true);
    when(ocspService.passesOcspCheck(certChain)).thenReturn(true);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    verify(tokenFactory).fromCertificates(certChain, TEST_IP);
  }

  @Test
  public void testSingleCertificate() {
    // Test with single certificate (no chain)
    X509Certificate[] singleCert = createTestCertificateChain(1);
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(singleCert);
    when(tokenFactory.fromCertificates(singleCert, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(singleCert)).thenReturn(true);
    when(ocspService.passesOcspCheck(singleCert)).thenReturn(true);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
  }

  // ==================== Authentication Type Test ====================

  @Test
  public void testGetAuthenticationType() {
    String authType = pkiHandler.getAuthenticationType();
    assertThat(authType, is("PKI"));
  }

  // ==================== Different IP Addresses ====================

  @Test
  public void testDifferentRemoteAddresses() {
    String[] testIps = {"127.0.0.1", "10.0.0.1", "192.168.1.1", "::1", "fe80::1"};

    for (String ip : testIps) {
      when(request.getRemoteAddr()).thenReturn(ip);
      when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
      when(tokenFactory.fromCertificates(validCertChain, ip)).thenReturn(authToken);
      when(crlChecker.passesCrlCheck(validCertChain)).thenReturn(true);
      when(ocspService.passesOcspCheck(validCertChain)).thenReturn(true);

      HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

      assertThat("Failed for IP: " + ip, result.getStatus(), is(HandlerResult.Status.COMPLETED));
      verify(tokenFactory).fromCertificates(validCertChain, ip);
    }
  }

  // ==================== CRL and OCSP Combined Tests ====================

  @Test
  public void testBothCrlAndOcspPass() {
    // Test when both CRL and OCSP checks pass
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(validCertChain);
    when(tokenFactory.fromCertificates(validCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(validCertChain)).thenReturn(true);
    when(ocspService.passesOcspCheck(validCertChain)).thenReturn(true);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.COMPLETED));
    verify(crlChecker).passesCrlCheck(validCertChain);
    verify(ocspService).passesOcspCheck(validCertChain);
  }

  @Test
  public void testCrlPassesButOcspFails() throws Exception {
    // Test when CRL passes but OCSP fails
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(revokedCertChain);
    when(tokenFactory.fromCertificates(revokedCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(revokedCertChain)).thenReturn(true);
    when(ocspService.passesOcspCheck(revokedCertChain)).thenReturn(false);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
  }

  @Test
  public void testCrlFailsOcspNotCalled() throws Exception {
    // Test that OCSP is not called when CRL fails (short-circuit evaluation)
    when(request.getAttribute(CERT_ATTRIBUTE)).thenReturn(revokedCertChain);
    when(tokenFactory.fromCertificates(revokedCertChain, TEST_IP)).thenReturn(authToken);
    when(crlChecker.passesCrlCheck(revokedCertChain)).thenReturn(false);

    HandlerResult result = pkiHandler.getNormalizedToken(request, response, filterChain, true);

    assertThat(result.getStatus(), is(HandlerResult.Status.REDIRECTED));
    verify(ocspService, never()).passesOcspCheck(any());
  }

  // ==================== Helper Methods ====================

  private X509Certificate[] createTestCertificateChain() {
    return createTestCertificateChain(1);
  }

  private X509Certificate[] createTestCertificateChain(int chainLength) {
    try {
      X509Certificate[] chain = new X509Certificate[chainLength];
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);

      for (int i = 0; i < chainLength; i++) {
        KeyPair keyPair = keyGen.generateKeyPair();
        // Create a mock certificate
        chain[i] = mock(X509Certificate.class);
        when(chain[i].getPublicKey()).thenReturn(keyPair.getPublic());
      }

      return chain;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create test certificate chain", e);
    }
  }
}
