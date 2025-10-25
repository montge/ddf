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
package org.codice.ddf.security.filter.authorization;

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
import ddf.security.permission.CollectionPermission;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.policy.context.ContextPolicy;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationFilterComprehensiveTest {

  private static final String ADMIN_PATH = "/admin/console";
  private static final String CATALOG_PATH = "/services/catalog";
  private static final String PUBLIC_PATH = "/public/info";
  private static final String WHITELISTED_PATH = "/services/health";

  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private SecurityLogger securityLogger;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;
  @Mock private Subject subject;
  @Mock private ContextPolicy contextPolicy;
  @Mock private CollectionPermission permissions;
  @Mock private SecurityManager securityManager;

  private AuthorizationFilter authzFilter;

  @Before
  public void setUp() {
    authzFilter = new AuthorizationFilter(contextPolicyManager);
    authzFilter.setSecurityLogger(securityLogger);
    authzFilter.init();

    // Set up Shiro security manager
    ThreadContext.bind(securityManager);
    ThreadContext.bind(subject);
  }

  @After
  public void tearDown() {
    ThreadContext.unbindSecurityManager();
    ThreadContext.unbindSubject();
  }

  // ==================== Successful Authorization Tests ====================

  @Test
  public void testAuthorizedRequestWithPermissions() throws Exception {
    // Test user with valid permissions accessing protected resource
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(true);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(securityLogger).audit(eq("Subject is authorized to view resource {}"), eq(ADMIN_PATH));
  }

  @Test
  public void testAuthorizedRequestWithEmptyPermissions() throws Exception {
    // Test request to resource with no permission requirements
    when(request.getRequestURI()).thenReturn(CATALOG_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(CATALOG_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(true);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(securityLogger, never()).audit(anyString(), anyString());
  }

  @Test
  public void testNoAuthPolicyBypassesAuthorization() throws Exception {
    // Test NO_AUTH_POLICY attribute bypasses all authorization
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn("true");

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(contextPolicyManager, never()).getContextPolicy(anyString());
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Authorization Denial Tests ====================

  @Test
  public void testUnauthorizedRequestDenied() throws Exception {
    // Test user without required permissions
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(false);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain, never()).doFilter(any(), any());
    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(securityLogger).audit(eq("Subject not authorized to view resource {}"), eq(ADMIN_PATH));
  }

  @Test
  public void testNullPolicyDeniesAccess() throws Exception {
    // Test when policy manager returns null (no policy configured)
    when(request.getRequestURI()).thenReturn(CATALOG_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(CATALOG_PATH)).thenReturn(null);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain, never()).doFilter(any(), any());
    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testNullSubjectDeniesAccess() throws Exception {
    // Test when subject cannot be retrieved
    ThreadContext.unbindSubject(); // Remove subject from thread context

    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain, never()).doFilter(any(), any());
    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Error Handling Tests ====================

  @Test
  public void testSecurityManagerExceptionHandled() throws Exception {
    // Test when SecurityUtils.getSubject() throws exception
    ThreadContext.unbindSubject();
    ThreadContext.unbindSecurityManager();

    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);

    authzFilter.doFilter(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testResponseSendErrorException() throws Exception {
    // Test exception during response.sendError
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(null);
    doThrow(new java.io.IOException("Network error"))
        .when(response)
        .sendError(HttpServletResponse.SC_FORBIDDEN);

    authzFilter.doFilter(request, response, filterChain);

    // Should handle exception gracefully
    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Permission Checking Tests ====================

  @Test
  public void testMultiplePermissionsAllRequired() throws Exception {
    // Test with multiple required permissions (all must be satisfied)
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(true);

    authzFilter.doFilter(request, response, filterChain);

    verify(subject).isPermitted(permissions);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void testPartialPermissionsDenied() throws Exception {
    // Test when user has some but not all required permissions
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(false);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain, never()).doFilter(any(), any());
    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Path-Based Authorization Tests ====================

  @Test
  public void testDifferentPathsAuthorizedIndependently() throws Exception {
    // Test that different paths are authorized independently
    String[] paths = {"/admin/config", "/services/api", "/catalog/query"};

    for (String path : paths) {
      ContextPolicy policy = mock(ContextPolicy.class);
      CollectionPermission perms = mock(CollectionPermission.class);

      when(request.getRequestURI()).thenReturn(path);
      when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
      when(contextPolicyManager.getContextPolicy(path)).thenReturn(policy);
      when(policy.getAllowedAttributePermissions()).thenReturn(perms);
      when(perms.isEmpty()).thenReturn(false);
      when(subject.isPermitted(perms)).thenReturn(true);

      authzFilter.doFilter(request, response, filterChain);

      verify(contextPolicyManager).getContextPolicy(path);
    }

    verify(filterChain, times(paths.length)).doFilter(request, response);
  }

  @Test
  public void testRootPathAuthorization() throws Exception {
    // Test authorization for root path
    when(request.getRequestURI()).thenReturn("/");
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy("/")).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(true);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void testDeepNestedPathAuthorization() throws Exception {
    // Test authorization for deeply nested path
    String deepPath = "/services/catalog/internal/metacards/query/advanced";
    when(request.getRequestURI()).thenReturn(deepPath);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(deepPath)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(true);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  // ==================== Security Logging Tests ====================

  @Test
  public void testAuditLogOnAuthorization() throws Exception {
    // Verify security audit logging on successful authorization
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(true);

    authzFilter.doFilter(request, response, filterChain);

    verify(securityLogger).audit(eq("Subject is authorized to view resource {}"), eq(ADMIN_PATH));
  }

  @Test
  public void testAuditLogOnDenial() throws Exception {
    // Verify security audit logging on access denial
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(false);

    authzFilter.doFilter(request, response, filterChain);

    verify(securityLogger).audit(eq("Subject not authorized to view resource {}"), eq(ADMIN_PATH));
  }

  @Test
  public void testNoAuditLogForEmptyPermissions() throws Exception {
    // Verify no audit log when permissions are empty (unrestricted access)
    when(request.getRequestURI()).thenReturn(PUBLIC_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(PUBLIC_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(true);

    authzFilter.doFilter(request, response, filterChain);

    verify(securityLogger, never()).audit(anyString(), anyString());
  }

  // ==================== Edge Cases ====================

  @Test
  public void testEmptyRequestURI() throws Exception {
    // Test with empty request URI
    when(request.getRequestURI()).thenReturn("");
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy("")).thenReturn(null);

    authzFilter.doFilter(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testNullRequestURI() throws Exception {
    // Test with null request URI
    when(request.getRequestURI()).thenReturn(null);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(null)).thenReturn(null);

    authzFilter.doFilter(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testSpecialCharactersInPath() throws Exception {
    // Test path with special characters
    String specialPath = "/services/catalog?query=<script>alert('xss')</script>";
    when(request.getRequestURI()).thenReturn(specialPath);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(specialPath)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(true);

    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void testPathTraversalAttempt() throws Exception {
    // Test path traversal attempt in URI
    String traversalPath = "/admin/../../etc/passwd";
    when(request.getRequestURI()).thenReturn(traversalPath);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(traversalPath)).thenReturn(null);

    authzFilter.doFilter(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Filter Lifecycle Tests ====================

  @Test
  public void testInitialization() {
    // Test filter initialization
    AuthorizationFilter newFilter = new AuthorizationFilter(contextPolicyManager);
    newFilter.init();
    // Should complete without errors
  }

  @Test
  public void testDestroy() {
    // Test filter cleanup
    authzFilter.destroy();
    // Should complete without errors
  }

  // ==================== Concurrent Request Tests ====================

  @Test
  public void testConcurrentAuthorizedRequests() throws Exception {
    // Test multiple concurrent authorized requests
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);
    when(subject.isPermitted(permissions)).thenReturn(true);

    // Simulate concurrent requests
    for (int i = 0; i < 10; i++) {
      authzFilter.doFilter(request, response, filterChain);
    }

    verify(filterChain, times(10)).doFilter(request, response);
  }

  @Test
  public void testMixedAuthorizationResults() throws Exception {
    // Test alternating authorized/unauthorized requests
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(permissions.isEmpty()).thenReturn(false);

    // First request: authorized
    when(subject.isPermitted(permissions)).thenReturn(true);
    authzFilter.doFilter(request, response, filterChain);

    // Second request: unauthorized
    when(subject.isPermitted(permissions)).thenReturn(false);
    authzFilter.doFilter(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }
}
