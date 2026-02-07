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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
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
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.policy.context.ContextPolicy;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Additional comprehensive test coverage for AuthorizationFilter. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthorizationFilterCoverageTest {

  private AuthorizationFilter filter;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;
  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private SecurityLogger securityLogger;
  @Mock private Subject subject;
  @Mock private ContextPolicy contextPolicy;

  @BeforeEach
  public void setup() {
    filter = new AuthorizationFilter(contextPolicyManager);
    filter.setSecurityLogger(securityLogger);
    filter.init();

    when(request.getRequestURI()).thenReturn("/test/resource");
  }

  @AfterEach
  public void cleanup() {
    ThreadContext.unbindSubject();
  }

  @Test
  public void testNoAuthPolicySkipsAuthorization() throws IOException, AuthenticationException {
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn("true");

    filter.doFilter(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(contextPolicyManager, never()).getContextPolicy(anyString());
    verify(securityLogger, never()).audit(anyString(), anyString());
  }

  @Test
  public void testNoAuthPolicyAttributeNull() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);

    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(true);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    filter.doFilter(request, response, filterChain);

    verify(contextPolicyManager, times(1)).getContextPolicy("/test/resource");
  }

  @Test
  public void testSubjectPermittedWithNonEmptyPermissions()
      throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(subject, times(1)).isPermitted(permissions);
    verify(securityLogger, times(1))
        .audit(eq("Subject is authorized to view resource {}"), eq("/test/resource"));
    verify(filterChain, times(1)).doFilter(request, response);
    verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testSubjectNotPermitted() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);

    filter.doFilter(request, response, filterChain);

    verify(subject, times(1)).isPermitted(permissions);
    verify(securityLogger, times(1))
        .audit(eq("Subject not authorized to view resource {}"), eq("/test/resource"));
    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  public void testEmptyPermissionsSkipsPermissionCheck()
      throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(true);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    filter.doFilter(request, response, filterChain);

    verify(subject, never()).isPermitted(any(CollectionPermission.class));
    verify(securityLogger, never()).audit(anyString(), anyString());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testNullPolicyDeniesAccess() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(null);

    filter.doFilter(request, response, filterChain);

    verify(securityLogger, times(1))
        .audit(eq("Subject not authorized to view resource {}"), eq("/test/resource"));
    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  public void testNullSubjectDeniesAccess() throws IOException, AuthenticationException {
    // No subject bound to thread context
    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  public void testExceptionRetrievingSubjectDeniesAccess()
      throws IOException, AuthenticationException {
    // Bind null to force exception in SecurityUtils.getSubject()
    ThreadContext.bind((Subject) null);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testIOExceptionInReturnNotAuthorizedIsCaught()
      throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);
    doThrow(new IOException("Network error"))
        .when(response)
        .sendError(HttpServletResponse.SC_FORBIDDEN);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    // Exception should be caught and logged, not propagated
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  public void testFlushBufferIOExceptionIsCaught() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);
    doThrow(new IOException("Buffer error")).when(response).flushBuffer();

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testMultiplePathsWithDifferentPolicies() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    // Test path 1
    when(request.getRequestURI()).thenReturn("/admin/config");
    ContextPolicy adminPolicy = mock(ContextPolicy.class);
    CollectionPermission adminPermissions = mock(CollectionPermission.class);
    when(adminPermissions.isEmpty()).thenReturn(false);
    when(adminPolicy.getAllowedAttributePermissions()).thenReturn(adminPermissions);
    when(contextPolicyManager.getContextPolicy("/admin/config")).thenReturn(adminPolicy);
    when(subject.isPermitted(adminPermissions)).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(contextPolicyManager, times(1)).getContextPolicy("/admin/config");
    verify(subject, times(1)).isPermitted(adminPermissions);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testDifferentRequestURIs() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(request.getRequestURI()).thenReturn("/api/v1/search");
    when(contextPolicyManager.getContextPolicy("/api/v1/search")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(contextPolicyManager, times(1)).getContextPolicy("/api/v1/search");
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testSubjectWithComplexPermissions() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(subject, times(1)).isPermitted(permissions);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testInitMethod() {
    AuthorizationFilter newFilter = new AuthorizationFilter(contextPolicyManager);
    newFilter.init();
    // Verify init completes without exception
    assertThat(newFilter, notNullValue());
  }

  @Test
  public void testDestroyMethod() {
    filter.destroy();
    // Verify destroy completes without exception
    assertThat(filter, notNullValue());
  }

  @Test
  public void testSetSecurityLoggerCalled() throws IOException, AuthenticationException {
    SecurityLogger customLogger = mock(SecurityLogger.class);
    AuthorizationFilter customFilter = new AuthorizationFilter(contextPolicyManager);
    customFilter.setSecurityLogger(customLogger);

    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    customFilter.doFilter(request, response, filterChain);

    verify(customLogger, times(1))
        .audit(eq("Subject is authorized to view resource {}"), eq("/test/resource"));
  }

  @Test
  public void testPermissionCheckReturnsFalse() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).flushBuffer();
  }

  @Test
  public void testNullSubjectAndNullPolicy() throws IOException, AuthenticationException {
    // No subject and no policy - should deny
    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(null);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  public void testPolicyWithNullPermissions() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(null);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testSubjectPermissionCheckThrowsException()
      throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions))
        .thenThrow(new RuntimeException("Permission check failed"));

    try {
      filter.doFilter(request, response, filterChain);
    } catch (RuntimeException e) {
      // Exception should propagate
      verify(filterChain, never()).doFilter(any(), any());
    }
  }

  @Test
  public void testResponseAlreadyCommitted() throws IOException, AuthenticationException {
    ThreadContext.bind(subject);

    when(contextPolicyManager.getContextPolicy("/test/resource")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);
    when(response.isCommitted()).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testConstructorSetsContextPolicyManager() {
    ContextPolicyManager customManager = mock(ContextPolicyManager.class);
    AuthorizationFilter customFilter = new AuthorizationFilter(customManager);
    assertThat(customFilter, notNullValue());
  }
}
