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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
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
import ddf.security.permission.impl.CollectionPermissionImpl;
import ddf.security.permission.impl.KeyValuePermissionImpl;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

/**
 * Advanced test suite for AuthorizationFilter covering edge cases, error handling, and complex
 * permission scenarios.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationFilterAdvancedTest {

  private static final String SECURE_PATH = "/secure/resource";
  private static final String ADMIN_PATH = "/admin/config";
  private static final String PUBLIC_PATH = "/public/resource";

  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private SecurityLogger securityLogger;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;
  @Mock private ContextPolicy contextPolicy;
  @Mock private Subject subject;

  private AuthorizationFilter authorizationFilter;
  private boolean filterChainCalled;

  @Before
  public void setUp() {
    authorizationFilter = new AuthorizationFilter(contextPolicyManager);
    authorizationFilter.setSecurityLogger(securityLogger);
    authorizationFilter.init();
    filterChainCalled = false;
  }

  @After
  public void tearDown() {
    ThreadContext.unbindSubject();
  }

  // ==================== No Auth Policy Tests ====================

  @Test
  public void testNoAuthPolicySkipsAuthorization() throws Exception {
    // Test that NO_AUTH_POLICY attribute skips authorization check
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn("true");

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
    verify(contextPolicyManager, never()).getContextPolicy(anyString());
  }

  @Test
  public void testNoAuthPolicyWithNullValue() throws Exception {
    // Test that null NO_AUTH_POLICY value continues authorization
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    when(subject.isPermitted(permissions)).thenReturn(true);
    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
  }

  // ==================== Subject Retrieval Tests ====================

  @Test
  public void testNullSubjectDeniesAccess() throws Exception {
    // Test that null subject denies access
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    // No subject bound to thread
    SecurityFilterChain chain =
        (req, res) -> fail("Should not call filter chain without valid subject");
    authorizationFilter.doFilter(request, response, chain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testSubjectRetrievalException() throws Exception {
    // Test handling of exception during subject retrieval
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    // Unbind subject to ensure exception occurs
    ThreadContext.unbindSubject();

    SecurityFilterChain chain =
        (req, res) -> fail("Should not call filter chain when subject retrieval fails");
    authorizationFilter.doFilter(request, response, chain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  // ==================== Permission Validation Tests ====================

  @Test
  public void testNullPermissionsDeniesAccess() throws Exception {
    // Test that null permissions deny access
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(null);

    ThreadContext.bind(subject);

    SecurityFilterChain chain =
        (req, res) -> fail("Should not call filter chain with null permissions");
    authorizationFilter.doFilter(request, response, chain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(securityLogger).audit(anyString(), anyString());
  }

  @Test
  public void testEmptyPermissionsAllowsAccess() throws Exception {
    // Test that empty permissions collection allows access
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission emptyPermissions = mock(CollectionPermission.class);
    when(emptyPermissions.isEmpty()).thenReturn(true);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(emptyPermissions);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
    verify(subject, never()).isPermitted(any(CollectionPermission.class));
  }

  @Test
  public void testNonEmptyPermissionsRequireCheck() throws Exception {
    // Test that non-empty permissions require permission check
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("user")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
    verify(subject).isPermitted(permissions);
  }

  // ==================== Policy Configuration Tests ====================

  @Test
  public void testNullContextPolicyDeniesAccess() throws Exception {
    // Test that null context policy denies access
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(null);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> fail("Should not call filter chain with null policy");
    authorizationFilter.doFilter(request, response, chain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(securityLogger).audit(anyString(), anyString());
  }

  // ==================== Permission Check Results Tests ====================

  @Test
  public void testSubjectPermittedAllowsAccess() throws Exception {
    // Test that permitted subject is allowed access
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
    verify(securityLogger).audit(anyString(), anyString());
  }

  @Test
  public void testSubjectNotPermittedDeniesAccess() throws Exception {
    // Test that unpermitted subject is denied access
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);

    ThreadContext.bind(subject);

    SecurityFilterChain chain =
        (req, res) -> fail("Should not call filter chain when subject not permitted");
    authorizationFilter.doFilter(request, response, chain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(securityLogger).audit(anyString(), anyString());
  }

  // ==================== Multiple Permission Tests ====================

  @Test
  public void testMultiplePermissionsAllRequired() throws Exception {
    // Test that subject must satisfy all permissions in collection
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(ADMIN_PATH);
    when(contextPolicyManager.getContextPolicy(ADMIN_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
    verify(subject).isPermitted(permissions);
  }

  // ==================== Response Handling Tests ====================

  @Test
  public void testForbiddenResponseCorrectlySet() throws Exception {
    // Test that 403 response is correctly set on authorization failure
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);

    ThreadContext.bind(subject);

    SecurityFilterChain chain =
        (req, res) -> fail("Should not call filter chain when unauthorized");
    authorizationFilter.doFilter(request, response, chain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(response).flushBuffer();
  }

  @Test
  public void testIOExceptionDuringResponseHandling() throws Exception {
    // Test handling of IOException during response
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);
    doThrow(new IOException("Network error"))
        .when(response)
        .sendError(HttpServletResponse.SC_FORBIDDEN);

    ThreadContext.bind(subject);

    SecurityFilterChain chain =
        (req, res) -> fail("Should not call filter chain when unauthorized");
    authorizationFilter.doFilter(request, response, chain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    // Exception should be caught and logged, not re-thrown
  }

  // ==================== Audit Logging Tests ====================

  @Test
  public void testAuditLogOnAuthorizationSuccess() throws Exception {
    // Test that successful authorization is audited
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("user")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    verify(securityLogger).audit(anyString(), eq(SECURE_PATH));
  }

  @Test
  public void testAuditLogOnAuthorizationFailure() throws Exception {
    // Test that failed authorization is audited
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);

    ThreadContext.bind(subject);

    SecurityFilterChain chain =
        (req, res) -> fail("Should not call filter chain when unauthorized");
    authorizationFilter.doFilter(request, response, chain);

    verify(securityLogger).audit(anyString(), eq(SECURE_PATH));
  }

  // ==================== Path Sanitization Tests ====================

  @Test
  public void testPathWithSpecialCharactersSanitized() throws Exception {
    // Test that paths with special characters are sanitized in logs
    String pathWithSpecialChars = "/secure/<script>alert('xss')</script>";
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(pathWithSpecialChars);
    when(contextPolicyManager.getContextPolicy(pathWithSpecialChars)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            pathWithSpecialChars,
            new KeyValuePermissionImpl(pathWithSpecialChars, Collections.singleton("admin")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    verify(securityLogger).audit(anyString(), anyString());
  }

  // ==================== Concurrent Access Tests ====================

  @Test
  public void testConcurrentAuthorizationRequests() throws Exception {
    // Test that filter can handle concurrent requests
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH, new KeyValuePermissionImpl(SECURE_PATH, Collections.singleton("user")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> {};
    for (int i = 0; i < 5; i++) {
      authorizationFilter.doFilter(request, response, chain);
    }

    verify(subject, times(5)).isPermitted(permissions);
  }

  // ==================== Lifecycle Tests ====================

  @Test
  public void testInitMethod() {
    // Test initialization
    authorizationFilter.init();
    // Should complete without errors
  }

  @Test
  public void testDestroyMethod() {
    // Test destroy method
    authorizationFilter.destroy();
    // Should complete without errors
  }

  // ==================== Different Permission Types Tests ====================

  @Test
  public void testKeyValuePermissionAuthorization() throws Exception {
    // Test authorization with KeyValuePermission
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(SECURE_PATH);
    when(contextPolicyManager.getContextPolicy(SECURE_PATH)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            SECURE_PATH,
            new KeyValuePermissionImpl("role", Collections.singleton("administrator")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
  }

  // ==================== Edge Case Path Tests ====================

  @Test
  public void testRootPathAuthorization() throws Exception {
    // Test authorization for root path
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn("/");
    when(contextPolicyManager.getContextPolicy("/")).thenReturn(contextPolicy);

    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(true);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
  }

  @Test
  public void testDeepNestedPathAuthorization() throws Exception {
    // Test authorization for deeply nested path
    String deepPath = "/level1/level2/level3/level4/resource";
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(request.getRequestURI()).thenReturn(deepPath);
    when(contextPolicyManager.getContextPolicy(deepPath)).thenReturn(contextPolicy);

    CollectionPermission permissions =
        new CollectionPermissionImpl(
            deepPath, new KeyValuePermissionImpl(deepPath, Collections.singleton("user")));
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    ThreadContext.bind(subject);

    SecurityFilterChain chain = (req, res) -> filterChainCalled = true;
    authorizationFilter.doFilter(request, response, chain);

    assertThat(filterChainCalled, is(true));
  }
}
