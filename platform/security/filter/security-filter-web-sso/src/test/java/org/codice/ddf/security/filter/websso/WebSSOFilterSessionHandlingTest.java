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
package org.codice.ddf.security.filter.websso;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.SecurityConstants;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.audit.SecurityLogger;
import ddf.security.common.PrincipalHolder;
import ddf.security.http.SessionFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.codice.ddf.platform.filter.AuthenticationChallengeException;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.platform.filter.AuthenticationFailureException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.handler.api.AuthenticationHandler;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Test session handling and edge cases for WebSSOFilter. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WebSSOFilterSessionHandlingTest {

  private WebSSOFilter filter;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;
  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private SessionFactory sessionFactory;
  @Mock private SecurityLogger securityLogger;
  @Mock private HttpSession session;
  @Mock private AuthenticationHandler handler;

  @BeforeEach
  public void setup() {
    filter = new WebSSOFilter();
    filter.setContextPolicyManager(contextPolicyManager);
    filter.setSessionFactory(sessionFactory);
    filter.setSecurityLogger(securityLogger);
    filter.init();

    when(request.getRequestURI()).thenReturn("/test/path");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(contextPolicyManager.isWhiteListed(anyString())).thenReturn(false);
  }

  @Test
  public void testWhitelistedPath() throws IOException, AuthenticationException {
    when(contextPolicyManager.isWhiteListed("/test/path")).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(request, times(1))
        .setAttribute(
            eq(org.codice.ddf.security.policy.context.ContextPolicy.NO_AUTH_POLICY), eq(true));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testSessionWithValidPrincipals() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn("session-id");
    when(request.getSession(false)).thenReturn(session);
    when(session.getId()).thenReturn("session-id");

    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    PrincipalCollection principalCollection = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertion = mock(SecurityAssertion.class);

    when(principalHolder.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    filter.doFilter(request, response, filterChain);

    verify(request, times(1))
        .setAttribute(eq(ddf.security.SecurityConstants.AUTHENTICATION_TOKEN_KEY), any());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testSessionWithExpiredPrincipals() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn("session-id");
    when(request.getSession(false)).thenReturn(session);

    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    when(principalHolder.getPrincipals()).thenReturn(null);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    filter.doFilter(request, response, filterChain);

    verify(securityLogger, times(1)).audit(anyString(), anyString());
  }

  @Test
  public void testSessionWithEmptyAssertions() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn("session-id");
    when(request.getSession(false)).thenReturn(session);

    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    PrincipalCollection principalCollection = mock(PrincipalCollection.class);

    when(principalHolder.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(Collections.emptyList());
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    filter.doFilter(request, response, filterChain);

    verify(principalHolder, times(1)).remove();
  }

  @Test
  public void testSessionFactoryNullWhenSessionNeeded() {
    filter.setSessionFactory(null);

    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn("session-id");
    when(request.getSession(false)).thenReturn(null);

    assertThrows(
        SessionException.class,
        () -> {
          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testNoSessionAccess() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(request, never()).getRequestedSessionId();
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testXForwardedForHeader() throws IOException, AuthenticationException {
    when(request.getHeader("X-FORWARDED-FOR")).thenReturn("192.168.1.1");
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testNoHandlersAndGuestDisabled() {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(false);

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testNoHandlersButGuestEnabled() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(request, times(1))
        .setAttribute(eq(ddf.security.SecurityConstants.AUTHENTICATION_TOKEN_KEY), any());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testHandlerRedirects() throws Exception {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);

    HandlerResult handlerResult = mock(HandlerResult.class);
    when(handlerResult.getStatus()).thenReturn(HandlerResult.Status.REDIRECTED);
    when(handler.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(handlerResult);
    when(handler.getAuthenticationType()).thenReturn("test");

    filter.setHandlerList(Collections.singletonList(handler));

    assertThrows(
        AuthenticationChallengeException.class,
        () -> {
          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testHandlerNoActionWithGuestDisabled() throws Exception {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(false);

    HandlerResult handlerResult = mock(HandlerResult.class);
    when(handlerResult.getStatus()).thenReturn(HandlerResult.Status.NO_ACTION);
    when(handler.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(handlerResult);

    filter.setHandlerList(Collections.singletonList(handler));

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testHandlerCompletedWithToken() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);

    HandlerResult handlerResult = mock(HandlerResult.class);
    when(handlerResult.getStatus()).thenReturn(HandlerResult.Status.COMPLETED);
    when(handlerResult.getToken())
        .thenReturn(mock(org.codice.ddf.security.handler.BaseAuthenticationToken.class));
    when(handler.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(handlerResult);

    filter.setHandlerList(Collections.singletonList(handler));

    filter.doFilter(request, response, filterChain);

    verify(request, times(1))
        .setAttribute(eq(ddf.security.SecurityConstants.AUTHENTICATION_TOKEN_KEY), any());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testHandlerCompletedWithoutToken() throws Exception {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);

    HandlerResult handlerResult = mock(HandlerResult.class);
    when(handlerResult.getStatus()).thenReturn(HandlerResult.Status.COMPLETED);
    when(handlerResult.getToken()).thenReturn(null);
    when(handler.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(handlerResult);

    filter.setHandlerList(Collections.singletonList(handler));

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testFilterChainException() throws Exception {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);

    HandlerResult handlerResult = mock(HandlerResult.class);
    when(handlerResult.getStatus()).thenReturn(HandlerResult.Status.COMPLETED);
    when(handlerResult.getToken())
        .thenReturn(mock(org.codice.ddf.security.handler.BaseAuthenticationToken.class));
    when(handler.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(handlerResult);

    HandlerResult errorResult = mock(HandlerResult.class);
    when(errorResult.getStatus()).thenReturn(HandlerResult.Status.NO_ACTION);
    when(handler.handleError(any(), any(), any())).thenReturn(errorResult);

    filter.setHandlerList(Collections.singletonList(handler));

    doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(any(), any());

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testMultipleHandlers() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);

    AuthenticationHandler handler1 = mock(AuthenticationHandler.class);
    AuthenticationHandler handler2 = mock(AuthenticationHandler.class);

    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(HandlerResult.Status.NO_ACTION);

    HandlerResult completedResult = mock(HandlerResult.class);
    when(completedResult.getStatus()).thenReturn(HandlerResult.Status.COMPLETED);
    when(completedResult.getToken())
        .thenReturn(mock(org.codice.ddf.security.handler.BaseAuthenticationToken.class));

    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(noActionResult);
    when(handler2.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(completedResult);

    filter.setHandlerList(Arrays.asList(handler1, handler2));

    filter.doFilter(request, response, filterChain);

    verify(handler1, times(1)).getNormalizedToken(any(), any(), any(), eq(false));
    verify(handler2, times(1)).getNormalizedToken(any(), any(), any(), eq(false));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testNoAuthPolicyAttributeCleared() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(request, times(1))
        .setAttribute(
            eq(org.codice.ddf.security.policy.context.ContextPolicy.NO_AUTH_POLICY), eq(null));
  }

  @Test
  public void testDestroyFilter() {
    filter.destroy();
    // Verify destroy completes without exception
  }
}
