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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codice.ddf.platform.filter.AuthenticationFailureException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CsrfFilterTest {

  private static final String CSRF_HEADER = "X-Requested-With";
  private static final String ORIGIN_HEADER = "Origin";
  private static final String REFERER_HEADER = "Referer";
  private static final String USER_AGENT_HEADER = "User-Agent";

  private static final String JOLOKIA_CONTEXT = "/admin/jolokia";
  private static final String INTRIGUE_CONTEXT = "/search/catalog/internal";
  private static final String WEBSOCKET_CONTEXT = "/search/catalog/ws";
  private static final String SERVICES_CONTEXT = "/services/catalog";
  private static final String CATALOG_CONTEXT = "/search/catalog/internal/catalog/sources";
  private static final String OAUTH_CONTEXT = "/search/catalog/internal/oauth";

  private static final String VALID_ORIGIN = "https://localhost:8993";
  private static final String VALID_REFERER = "https://localhost:8993/admin";
  private static final String INVALID_ORIGIN = "https://evil.com:8080";

  @Mock private SecurityLogger securityLogger;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;

  private CsrfFilter csrfFilter;

  @BeforeEach
  public void setUp() {
    // Set system property to enable CSRF filtering
    System.setProperty("csrf.enabled", "true");
    System.setProperty("org.codice.ddf.system.hostname", "localhost");
    System.setProperty("org.codice.ddf.system.httpsPort", "8993");
    System.setProperty("org.codice.ddf.system.httpPort", "8181");
    // Add default HTTP and HTTPS ports as trusted authorities for testing
    System.setProperty("csrf.trustedAuthorities", "localhost:80,localhost:443");

    csrfFilter = new CsrfFilter(securityLogger);
    csrfFilter.init();
  }

  // ==================== Browser Protection Tests ====================

  @Test
  public void testValidRequestWithOriginAndCsrfHeader() throws Exception {
    // Test legitimate browser request with valid origin and CSRF header
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testValidRequestWithRefererAndCsrfHeader() throws Exception {
    // Test legitimate request with valid referer and CSRF header
    when(request.getRequestURI()).thenReturn(INTRIGUE_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(REFERER_HEADER)).thenReturn(VALID_REFERER);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testRequestMissingOriginAndReferer() throws Exception {
    // Test attack: request without Origin or Referer header
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

    verify(filterChain, never()).doFilter(any(), any());
    verify(securityLogger)
        .audit(
            eq(
                "Cross-site check failure: Incoming request did not have an Origin or Referer header."));
  }

  @Test
  public void testRequestWithInvalidOrigin() throws Exception {
    // Test attack: request from untrusted origin
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(INVALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });

    verify(filterChain, never()).doFilter(any(), any());
    verify(securityLogger)
        .audit(anyString()); // Should audit the failure with appropriate message about untrusted
    // origin
  }

  @Test
  public void testRequestMissingCsrfHeader() throws Exception {
    // Test attack: request without X-Requested-With header
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn(null);

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });

    verify(filterChain, never()).doFilter(any(), any());
    verify(securityLogger)
        .audit(
            eq("Cross-site check failure: Request did not have required X-Requested-With header."));
  }

  @Test
  public void testWebSocketGetRequestBypassesCsrfHeaderCheck() throws Exception {
    // WebSocket API does not allow custom headers, authority check is sufficient
    when(request.getRequestURI()).thenReturn(WEBSOCKET_CONTEXT);
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn(null); // No CSRF header

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testCatalogDownloadGetRequestBypassesCsrfHeaderCheck() throws Exception {
    // Downloading does not allow adding headers, authority check is sufficient
    when(request.getRequestURI()).thenReturn(CATALOG_CONTEXT);
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn(null); // No CSRF header

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testOAuthRedirectBypassesAllChecks() throws Exception {
    // OAuth provider redirect does not include headers
    when(request.getRequestURI()).thenReturn(OAUTH_CONTEXT);
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(null);
    when(request.getHeader(REFERER_HEADER)).thenReturn(null);
    when(request.getHeader(CSRF_HEADER)).thenReturn(null);

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testProtectedContextPostWithoutCsrfHeader() {
    // Test POST to protected context without CSRF header
    when(request.getRequestURI()).thenReturn(INTRIGUE_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn(VALID_ORIGIN);
    when(request.getHeader(CSRF_HEADER)).thenReturn(null);

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  // ==================== System Protection Tests ====================

  @Test
  public void testServicesContextWithNonBrowserUserAgent() throws Exception {
    // Test legitimate API call from non-browser client
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER)).thenReturn("Java/11.0.1");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testServicesContextWithBrowserUserAgent() throws Exception {
    // Test attack: browser attempting to access /services directly
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });

    verify(filterChain, never()).doFilter(any(), any());
    verify(securityLogger).audit(eq("Cross-site check failure: Request was made from a browser."));
  }

  @Test
  public void testServicesContextWsdlQueryBypassesBrowserCheck() throws Exception {
    // Test WSDL access from browser (legitimate use case)
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("GET");
    when(request.getQueryString()).thenReturn("wsdl");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testServicesContextWithChromeUserAgent() {
    // Test browser detection: Chrome
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/91.0.4472.124");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testServicesContextWithSafariUserAgent() {
    // Test browser detection: Safari
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/537.36");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testServicesContextWithFirefoxUserAgent() {
    // Test browser detection: Firefox
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testServicesContextWithEdgeUserAgent() {
    // Test browser detection: Edge
    when(request.getRequestURI()).thenReturn(SERVICES_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Edge/91.0.864.59");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  // ==================== Bypass and Edge Cases ====================

  @Test
  public void testCsrfDisabledSystemProperty() throws Exception {
    // Test with CSRF protection disabled (insecure but valid for testing)
    System.setProperty("csrf.enabled", "false");
    CsrfFilter disabledFilter = new CsrfFilter(securityLogger);
    disabledFilter.init();

    // When CSRF is disabled, these stubbings are not used but kept for test consistency
    lenient().when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    lenient().when(request.getMethod()).thenReturn("POST");
    // No headers set - filter should not check them when disabled

    disabledFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);

    // Reset for other tests
    System.setProperty("csrf.enabled", "true");
  }

  @Test
  public void testUnprotectedContextBypassesAllChecks() throws Exception {
    // Test unprotected context (not in protectedContexts list)
    when(request.getRequestURI()).thenReturn("/public/api/test");
    when(request.getMethod()).thenReturn("POST");
    // No headers needed

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testTrustedAuthorityWithCustomPort() throws Exception {
    // Test with custom port in trusted authorities
    System.setProperty("csrf.trustedAuthorities", "trusted.example.com:9443");
    CsrfFilter customFilter = new CsrfFilter(securityLogger);
    customFilter.init();

    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("https://trusted.example.com:9443");
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    customFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);

    // Reset
    System.clearProperty("csrf.trustedAuthorities");
  }

  @Test
  public void testMalformedOriginUrl() {
    // Test attack: malformed origin URL
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("not-a-valid-url");
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testOriginWithDefaultHttpPort() throws Exception {
    // Test origin with default HTTP port (80)
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("http://localhost"); // No port specified
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testOriginWithDefaultHttpsPort() throws Exception {
    // Test origin with default HTTPS port (443)
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("https://localhost"); // No port specified
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testBlankOriginAndReferer() {
    // Test with blank (empty string) origin and referer
    when(request.getRequestURI()).thenReturn(JOLOKIA_CONTEXT);
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(ORIGIN_HEADER)).thenReturn("");
    when(request.getHeader(REFERER_HEADER)).thenReturn("");
    when(request.getHeader(CSRF_HEADER)).thenReturn("XMLHttpRequest");

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          csrfFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testWhitelistConfigurationForServicesContext() throws Exception {
    // Test whitelist configuration
    csrfFilter.setWhiteListContexts(
        java.util.Arrays.asList("/services/test=GET", "/services/test=POST"));

    when(request.getRequestURI()).thenReturn("/services/test");
    when(request.getMethod()).thenReturn("POST");
    when(request.getHeader(USER_AGENT_HEADER))
        .thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/91.0.4472.124");

    csrfFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testDestroy() {
    // Test filter cleanup
    csrfFilter.destroy();
    // Should complete without errors
  }
}
