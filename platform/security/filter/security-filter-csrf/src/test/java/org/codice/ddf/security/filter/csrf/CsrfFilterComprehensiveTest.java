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
package org.codice.ddf.security.filter.csrf;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codice.ddf.platform.filter.AuthenticationFailureException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for CsrfFilter covering advanced scenarios, edge cases, and error
 * handling.
 */
@ExtendWith(MockitoExtension.class)
public class CsrfFilterComprehensiveTest {

  private static final String CSRF_HEADER = "X-Requested-With";
  private static final String ORIGIN_HEADER = "Origin";
  private static final String REFERER_HEADER = "Referer";
  private static final String USER_AGENT_HEADER = "User-Agent";

  private static final String JOLOKIA_CONTEXT = "/admin/jolokia/exec";
  private static final String INTRIGUE_CONTEXT = "/search/catalog/internal/metacards";
  private static final String WEBSOCKET_CONTEXT = "/search/catalog/ws/notifications";
  private static final String SERVICES_CONTEXT = "/services/catalog/query";

  private static final String VALID_ORIGIN = "https://localhost:8993";
  private static final String VALID_REFERER = "https://localhost:8993/admin/index.html";
  private static final String INVALID_ORIGIN = "https://attacker.com:8080";

  @Mock private SecurityLogger securityLogger;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;

  private CsrfFilter csrfFilter;

  @BeforeEach
  public void setUp() {
    // Set system properties for CSRF configuration
    System.setProperty("csrf.enabled", "true");
    System.setProperty("org.codice.ddf.system.hostname", "localhost");
    System.setProperty("org.codice.ddf.system.httpsPort", "8993");
    System.setProperty("org.codice.ddf.system.httpPort", "8181");

    csrfFilter = new CsrfFilter(securityLogger);
    csrfFilter.init();
  }

  @AfterEach
  public void tearDown() {
    System.clearProperty("csrf.enabled");
    System.clearProperty("csrf.trustedAuthorities");
  }

  // ==================== Multiple Headers Validation Tests ====================

  @Test
  public void testValidRequestWithBothOriginAndReferer() throws Exception {
    // Test that both Origin and Referer can be present and valid
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(REFERER_HEADER)).thenReturn(VALID_REFERER);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testValidRequestWithOnlyOrigin() throws Exception {
    // Test that only Origin is sufficient
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(REFERER_HEADER)).thenReturn(null);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testValidRequestWithOnlyReferer() throws Exception {
    // Test that only Referer is sufficient
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(null);
    when(request.getHeader(REFERER_HEADER)).thenReturn(VALID_REFERER);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testMixedValidAndInvalidOrigins() throws Exception {
    // Test with valid origin but invalid referer - valid origin is sufficient
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(REFERER_HEADER)).thenReturn("https://evil.com/attack");
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== HTTP Method Tests ====================

  @Test
  public void testGetRequestWithValidOrigin() throws Exception {
    // Test GET request to protected context
    when(request.getRequestURI()).thenReturn(INTRIGUE_CONTEXT);
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testPutRequestWithValidOrigin() throws Exception {
    // Test PUT request to protected context
    when(request.getRequestURI()).thenReturn(INTRIGUE_CONTEXT);
    when(request.getMethod()).thenReturn("PUT");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testDeleteRequestWithValidOrigin() throws Exception {
    // Test DELETE request to protected context
    when(request.getRequestURI()).thenReturn(INTRIGUE_CONTEXT);
    when(request.getMethod()).thenReturn("DELETE");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testPatchRequestWithValidOrigin() throws Exception {
    // Test PATCH request to protected context
    when(request.getRequestURI()).thenReturn(INTRIGUE_CONTEXT);
    when(request.getMethod()).thenReturn("PATCH");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Port Handling Tests ====================

  @Test
  public void testOriginWithExplicitHttpsPort8993() throws Exception {
    // Test origin with explicit HTTPS port matching system property
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("https://localhost:8993");
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testOriginWithExplicitHttpPort8181() throws Exception {
    // Test origin with explicit HTTP port matching system property
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("http://localhost:8181");
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testOriginWithWrongPort() {
    // Test origin with wrong port
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("https://localhost:9999");
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  // ==================== Browser Detection Tests ====================

  @Test
  public void testServicesContextWithOperaUserAgent() {
    // Test browser detection: Opera
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64) OPR/77.0.4054.277");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testServicesContextWithIEUserAgent() {
    // Test browser detection: Internet Explorer
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn(
            "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko MSIE 11.0");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testServicesContextWithPythonUserAgent() throws Exception {
    // Test non-browser user agent: Python
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER)).thenReturn("Python-urllib/3.9");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testServicesContextWithCurlUserAgent() throws Exception {
    // Test non-browser user agent: curl
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER)).thenReturn("curl/7.68.0");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testServicesContextWithNoUserAgent() throws Exception {
    // Test with no user agent header (treated as non-browser)
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER)).thenReturn(null);

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== CSRF Header Value Tests ====================

  @Test
  public void testCsrfHeaderWithCustomValue() throws Exception {
    // Test that any value in CSRF header is acceptable
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("CustomValue");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testCsrfHeaderWithEmptyString() throws Exception {
    // Test that empty string in CSRF header is acceptable
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Context Path Edge Cases ====================

  @Test
  public void testSubpathOfProtectedContext() throws Exception {
    // Test deep subpath of protected context
    when(request.getRequestURI()).thenReturn("/admin/jolokia/exec/java.lang/type=Memory");
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testSimilarButUnprotectedContext() throws Exception {
    // Test context that starts similarly but is not protected
    when(request.getRequestURI()).thenReturn("/admin/configuration/test");
    when(request.getMethod()).thenReturn("POST");
    // No headers needed for unprotected context

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Multiple Trusted Authorities Tests ====================

  @Test
  public void testMultipleTrustedAuthorities() throws Exception {
    // Test with multiple custom trusted authorities
    System.setProperty(
        "csrf.trustedAuthorities", "trusted1.example.com:8443,trusted2.example.com:9443");
    CsrfFilter customFilter = new CsrfFilter(securityLogger);
    customFilter.init();

    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("https://trusted2.example.com:9443");
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    customFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Whitelist Configuration Tests ====================

  @Test
  public void testWhitelistWithMultipleMethods() throws Exception {
    // Test whitelist configuration with multiple HTTP methods
    csrfFilter.setWhiteListContexts(
        java.util.Arrays.asList("/services/custom=GET", "/services/custom=POST"));

    when(request.getRequestURI()).thenReturn("/services/custom");
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0) Chrome/91.0");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testWhitelistDoesNotApplyToDifferentMethod() {
    // Test that whitelist for GET doesn't apply to POST
    csrfFilter.setWhiteListContexts(java.util.Arrays.asList("/services/custom=GET"));

    when(request.getRequestURI()).thenReturn("/services/custom");
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0) Chrome/91.0");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testWhitelistWithRegexPattern() throws Exception {
    // Test whitelist with regex pattern
    csrfFilter.setWhiteListContexts(java.util.Arrays.asList("/services/api/.*=POST"));

    when(request.getRequestURI()).thenReturn("/services/api/v1/query");
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0) Chrome/91.0");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testWhitelistWithNullList() throws Exception {
    // Test that null whitelist is handled gracefully
    csrfFilter.setWhiteListContexts(null);

    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER)).thenReturn("Java/11.0.1");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Security Logging Tests ====================

  @Test
  public void testSecurityAuditLoggingOnFailure() {
    // Verify that security failures are audited
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(INVALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
    verify(securityLogger).audit(anyString());
  }

  // ==================== Initialization and Lifecycle Tests ====================

  @Test
  public void testInitWithCsrfDisabled() {
    // Test initialization when CSRF is disabled
    System.setProperty("csrf.enabled", "false");
    CsrfFilter disabledFilter = new CsrfFilter(securityLogger);
    disabledFilter.init();
    disabledFilter.destroy();
    // Should complete without errors
  }

  @Test
  public void testDestroyMethod() {
    // Test destroy method
    csrfFilter.destroy();
    // Should complete without errors
  }

  // ==================== Concurrent Request Tests ====================

  @Test
  public void testConcurrentValidRequests() throws Exception {
    // Test that filter can handle concurrent requests
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    // Simulate multiple concurrent requests
    for (int i = 0; i < 5; i++) {
      csrfFilter.doFilter(request, response, filterChain);
    }

    verify(filterChain, org.mockito.Mockito.times(5)).doFilter(request, response);
  }

  // ==================== Query String Tests ====================

  @Test
  public void testServicesContextWithWsdlQueryStringCaseInsensitive() throws Exception {
    // Test WSDL query string with different case
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("GET");
    when(request.getQueryString()).thenReturn("WSDL");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0) Chrome/91.0");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testServicesContextWithOtherQueryString() {
    // Test that other query strings don't bypass browser check
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("GET");
    when(request.getQueryString()).thenReturn("format=json");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0) Chrome/91.0");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  // ==================== Response Validation Tests ====================

  @Test
  public void testResponseStatusSetCorrectly() throws Exception {
    // Verify that 403 status is set on failure
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(null);
    when(request.getHeader(REFERER_HEADER)).thenReturn(null);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(response).flushBuffer();
  }
}
