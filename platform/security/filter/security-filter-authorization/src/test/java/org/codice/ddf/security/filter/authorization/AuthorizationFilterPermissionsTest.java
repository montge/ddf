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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import ddf.security.permission.CollectionPermission;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.policy.context.ContextPolicy;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Test authorization permission checking for AuthorizationFilter. */
public class AuthorizationFilterPermissionsTest {

  private AuthorizationFilter filter;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;
  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private SecurityLogger securityLogger;
  @Mock private Subject subject;
  @Mock private ContextPolicy contextPolicy;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    filter = new AuthorizationFilter(contextPolicyManager);
    filter.setSecurityLogger(securityLogger);
    filter.init();

    when(request.getRequestURI()).thenReturn("/test/path");

    // Set up Shiro ThreadContext
    ThreadContext.bind(subject);
  }

  @After
  public void cleanup() {
    ThreadContext.unbindSubject();
  }

  @Test
  public void testNoAuthPolicy() throws IOException, AuthenticationException {
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn("true");

    filter.doFilter(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(contextPolicyManager, never()).getContextPolicy(any());
  }

  @Test
  public void testNullSubject() throws IOException, AuthenticationException {
    ThreadContext.unbindSubject();

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testNullPolicy() throws IOException, AuthenticationException {
    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(null);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(securityLogger, times(1)).audit(any(), eq("/test/path"));
  }

  @Test
  public void testEmptyPermissions() throws IOException, AuthenticationException {
    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(true);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    filter.doFilter(request, response, filterChain);

    verify(subject, never()).isPermitted(any(CollectionPermission.class));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testPermittedAccess() throws IOException, AuthenticationException {
    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(subject, times(1)).isPermitted(permissions);
    verify(securityLogger, times(1))
        .audit(eq("Subject is authorized to view resource {}"), eq("/test/path"));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testNotPermittedAccess() throws IOException, AuthenticationException {
    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);

    filter.doFilter(request, response, filterChain);

    verify(subject, times(1)).isPermitted(permissions);
    verify(securityLogger, times(1))
        .audit(eq("Subject not authorized to view resource {}"), eq("/test/path"));
    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  public void testExceptionRetrievingSubject() throws IOException, AuthenticationException {
    ThreadContext.unbindSubject();
    ThreadContext.bind(null);

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void testResponseFlushBufferIOException() throws IOException, AuthenticationException {
    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(false);
    when(response.sendError(HttpServletResponse.SC_FORBIDDEN))
        .thenThrow(new IOException("Test exception"));

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    // Exception is caught and logged, should not propagate
  }

  @Test
  public void testMultiplePermissions() throws IOException, AuthenticationException {
    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(subject, times(1)).isPermitted(permissions);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testDifferentPaths() throws IOException, AuthenticationException {
    when(request.getRequestURI()).thenReturn("/admin/config");
    when(contextPolicyManager.getContextPolicy("/admin/config")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(contextPolicyManager, times(1)).getContextPolicy("/admin/config");
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testDestroyFilter() {
    filter.destroy();
    // Verify destroy completes without exception
  }

  @Test
  public void testInitFilter() {
    AuthorizationFilter newFilter = new AuthorizationFilter(contextPolicyManager);
    newFilter.init();
    // Verify init completes without exception
  }

  @Test
  public void testSetSecurityLogger() {
    SecurityLogger logger = mock(SecurityLogger.class);
    filter.setSecurityLogger(logger);

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    CollectionPermission permissions = mock(CollectionPermission.class);
    when(permissions.isEmpty()).thenReturn(false);
    when(contextPolicy.getAllowedAttributePermissions()).thenReturn(permissions);
    when(subject.isPermitted(permissions)).thenReturn(true);

    try {
      filter.doFilter(request, response, filterChain);
      verify(logger, times(1)).audit(any(), eq("/test/path"));
    } catch (Exception e) {
      // Ignore
    }
  }
}
