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

import static ddf.security.SecurityConstants.AUTHENTICATION_TOKEN_KEY;
import static ddf.security.SecurityConstants.SECURITY_TOKEN_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.assertion.SecurityAssertion;
import ddf.security.audit.SecurityLogger;
import ddf.security.common.PrincipalHolder;
import ddf.security.http.SessionFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.codice.ddf.platform.filter.AuthenticationChallengeException;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.platform.filter.AuthenticationFailureException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.codice.ddf.security.handler.api.AuthenticationHandler;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.codice.ddf.security.handler.api.HandlerResult.Status;
import org.codice.ddf.security.policy.context.ContextPolicy;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Comprehensive test coverage for WebSSOFilter edge cases and error handling. */
@ExtendWith(MockitoExtension.class)
public class WebSSOFilterCoverageTest {

  private WebSSOFilter filter;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private SecurityFilterChain filterChain;
  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private SessionFactory sessionFactory;
  @Mock private SecurityLogger securityLogger;
  @Mock private HttpSession session;
  @Mock private AuthenticationHandler handler1;
  @Mock private AuthenticationHandler handler2;
  @Mock private ContextPolicy contextPolicy;

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
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(false);
  }

  @Test
  public void testToString() {
    String result = filter.toString();
    assertThat(result, is(WebSSOFilter.class.getName()));
  }

  @Test
  public void testGettersAndSetters() {
    List<AuthenticationHandler> handlers = new ArrayList<>();
    filter.setHandlerList(handlers);
    assertThat(filter.getHandlerList(), is(handlers));

    ContextPolicyManager policyManager = mock(ContextPolicyManager.class);
    filter.setContextPolicyManager(policyManager);
    assertThat(filter.getContextPolicyManager(), is(policyManager));
  }

  @Test
  public void testDestroy() {
    filter.destroy();
    // Should complete without exception
  }

  @Test
  public void testNoAuthPolicyAttributeClearedOnNonWhitelistedPath()
      throws IOException, AuthenticationException {
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(request, times(1)).setAttribute(eq(ContextPolicy.NO_AUTH_POLICY), eq(null));
  }

  @Test
  public void testNoHandlersNoGuestAccessReturns503() {
    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          when(contextPolicyManager.getGuestAccess()).thenReturn(false);

          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testHandlerReturnsCompletedWithGuestToken()
      throws IOException, AuthenticationException {
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.NO_ACTION);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);
    when(handler1.getAuthenticationType()).thenReturn("test");

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("test"));

    filter.setHandlerList(Collections.singletonList(handler1));

    filter.doFilter(request, response, filterChain);

    verify(request, times(1)).setAttribute(eq(AUTHENTICATION_TOKEN_KEY), any());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testHandlerReturnsCompletedButNullToken() {
    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          HandlerResult result = mock(HandlerResult.class);
          when(result.getStatus()).thenReturn(Status.COMPLETED);
          when(result.getToken()).thenReturn(null);
          when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);
          when(handler1.getAuthenticationType()).thenReturn("test");

          when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
          when(contextPolicy.getAuthenticationMethods())
              .thenReturn(Collections.singletonList("test"));

          filter.setHandlerList(Collections.singletonList(handler1));

          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testHandlerReturnsCompletedWithValidToken()
      throws IOException, AuthenticationException {
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.COMPLETED);
    when(result.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(result);
    when(handler1.getAuthenticationType()).thenReturn("test");

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("test"));

    filter.setHandlerList(Collections.singletonList(handler1));

    filter.doFilter(request, response, filterChain);

    verify(token, times(1)).setAllowGuest(false);
    verify(request, times(1)).setAttribute(eq(AUTHENTICATION_TOKEN_KEY), any());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testMultipleHandlersFirstReturnsNoActionSecondCompletes()
      throws IOException, AuthenticationException {
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);

    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(Status.NO_ACTION);

    HandlerResult completedResult = mock(HandlerResult.class);
    when(completedResult.getStatus()).thenReturn(Status.COMPLETED);
    when(completedResult.getToken()).thenReturn(token);

    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(noActionResult);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(true))).thenReturn(noActionResult);
    when(handler1.getAuthenticationType()).thenReturn("basic");

    when(handler2.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(completedResult);
    when(handler2.getAuthenticationType()).thenReturn("saml");

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Arrays.asList("basic", "saml"));

    filter.setHandlerList(Arrays.asList(handler1, handler2));

    filter.doFilter(request, response, filterChain);

    verify(handler1, times(1)).getNormalizedToken(any(), any(), any(), eq(false));
    verify(handler2, times(1)).getNormalizedToken(any(), any(), any(), eq(false));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testHandlerSecondPassResolvesCalled() throws IOException, AuthenticationException {
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);

    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(Status.NO_ACTION);

    HandlerResult completedResult = mock(HandlerResult.class);
    when(completedResult.getStatus()).thenReturn(Status.COMPLETED);
    when(completedResult.getToken()).thenReturn(token);

    // First pass returns NO_ACTION, second pass (resolve = true) returns COMPLETED
    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(noActionResult);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(true))).thenReturn(completedResult);
    when(handler1.getAuthenticationType()).thenReturn("test");

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("test"));

    filter.setHandlerList(Collections.singletonList(handler1));

    filter.doFilter(request, response, filterChain);

    verify(handler1, times(1)).getNormalizedToken(any(), any(), any(), eq(false));
    verify(handler1, times(1)).getNormalizedToken(any(), any(), any(), eq(true));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testHandlerReturnsRedirected() {
    assertThrows(
        AuthenticationChallengeException.class,
        () -> {
          HandlerResult result = mock(HandlerResult.class);
          when(result.getStatus()).thenReturn(Status.REDIRECTED);
          when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(result);
          when(handler1.getAuthenticationType()).thenReturn("test");

          when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
          when(contextPolicy.getAuthenticationMethods())
              .thenReturn(Collections.singletonList("test"));

          filter.setHandlerList(Collections.singletonList(handler1));

          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testXForwardedForHeaderUsed() throws IOException, AuthenticationException {
    when(request.getHeader("X-FORWARDED-FOR")).thenReturn("192.168.1.100");
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testSessionWithValidPrincipalsReusesSession()
      throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn("session-123");
    when(request.getSession(false)).thenReturn(session);
    when(session.getId()).thenReturn("session-123");

    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    PrincipalCollection principalCollection = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertion = mock(SecurityAssertion.class);

    when(principalHolder.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));
    when(session.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    filter.doFilter(request, response, filterChain);

    verify(handler1, never()).getNormalizedToken(any(), any(), any(), anyBoolean());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testSessionWithExpiredPrincipalsRemovesCalled()
      throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn("session-123");
    when(request.getSession(false)).thenReturn(session);

    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    PrincipalCollection principalCollection = mock(PrincipalCollection.class);

    when(principalHolder.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(Collections.emptyList());
    when(session.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    filter.doFilter(request, response, filterChain);

    verify(principalHolder, times(1)).remove();
  }

  @Test
  public void testSessionFactoryNullThrowsSessionException() {
    assertThrows(
        SessionException.class,
        () -> {
          filter.setSessionFactory(null);

          when(contextPolicyManager.getSessionAccess()).thenReturn(true);
          when(request.getRequestedSessionId()).thenReturn("session-123");
          when(request.getSession(false)).thenReturn(null);

          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testSessionFactoryCreatesNewSession() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn("session-123");
    when(request.getSession(false)).thenReturn(null);
    when(sessionFactory.getOrCreateSession(request)).thenReturn(session);

    filter.doFilter(request, response, filterChain);

    verify(sessionFactory, times(1)).getOrCreateSession(request);
  }

  @Test
  public void testSessionWithNullPrincipals() throws IOException, AuthenticationException {
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn("session-123");
    when(request.getSession(false)).thenReturn(session);

    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    when(principalHolder.getPrincipals()).thenReturn(null);
    when(session.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    filter.doFilter(request, response, filterChain);

    verify(securityLogger, times(1)).audit(anyString(), anyString());
  }

  @Test
  public void testFilterChainThrowsExceptionHandlerHandlesError() {
    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
          HandlerResult completedResult = mock(HandlerResult.class);
          when(completedResult.getStatus()).thenReturn(Status.COMPLETED);
          when(completedResult.getToken()).thenReturn(token);
          when(handler1.getNormalizedToken(any(), any(), any(), eq(false)))
              .thenReturn(completedResult);
          when(handler1.getAuthenticationType()).thenReturn("test");

          HandlerResult errorResult = mock(HandlerResult.class);
          when(errorResult.getStatus()).thenReturn(Status.NO_ACTION);
          when(handler1.handleError(any(), any(), any())).thenReturn(errorResult);

          when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
          when(contextPolicy.getAuthenticationMethods())
              .thenReturn(Collections.singletonList("test"));

          filter.setHandlerList(Collections.singletonList(handler1));

          doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(any(), any());

          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testFilterChainExceptionHandlerReturnsRedirected() {
    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
          HandlerResult completedResult = mock(HandlerResult.class);
          when(completedResult.getStatus()).thenReturn(Status.COMPLETED);
          when(completedResult.getToken()).thenReturn(token);
          when(handler1.getNormalizedToken(any(), any(), any(), eq(false)))
              .thenReturn(completedResult);
          when(handler1.getAuthenticationType()).thenReturn("test");

          HandlerResult errorResult = mock(HandlerResult.class);
          when(errorResult.getStatus()).thenReturn(Status.REDIRECTED);
          when(handler1.handleError(any(), any(), any())).thenReturn(errorResult);

          when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
          when(contextPolicy.getAuthenticationMethods())
              .thenReturn(Collections.singletonList("test"));

          filter.setHandlerList(Collections.singletonList(handler1));

          doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(any(), any());

          filter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testNoPolicyManagerReturnsAllHandlers() throws IOException, AuthenticationException {
    filter.setContextPolicyManager(null);

    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    HandlerResult completedResult = mock(HandlerResult.class);
    when(completedResult.getStatus()).thenReturn(Status.COMPLETED);
    when(completedResult.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(completedResult);

    filter.setHandlerList(Arrays.asList(handler1, handler2));

    filter.doFilter(request, response, filterChain);

    verify(handler1, times(1)).getNormalizedToken(any(), any(), any(), eq(false));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testPolicyWithNoAuthMethodsReturnsNoHandlers()
      throws IOException, AuthenticationException {
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.emptyList());

    filter.setHandlerList(Arrays.asList(handler1, handler2));

    filter.doFilter(request, response, filterChain);

    verify(handler1, never()).getNormalizedToken(any(), any(), any(), anyBoolean());
    verify(handler2, never()).getNormalizedToken(any(), any(), any(), anyBoolean());
  }

  @Test
  public void testHandlerAuthTypeMatchesPolicy() throws IOException, AuthenticationException {
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    HandlerResult completedResult = mock(HandlerResult.class);
    when(completedResult.getStatus()).thenReturn(Status.COMPLETED);
    when(completedResult.getToken()).thenReturn(token);

    when(handler1.getAuthenticationType()).thenReturn("basic");
    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(completedResult);

    when(handler2.getAuthenticationType()).thenReturn("saml");

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));

    filter.setHandlerList(Arrays.asList(handler1, handler2));

    filter.doFilter(request, response, filterChain);

    verify(handler1, times(1)).getNormalizedToken(any(), any(), any(), eq(false));
    verify(handler2, never()).getNormalizedToken(any(), any(), any(), anyBoolean());
  }

  @Test
  public void testSecurityLoggerAuditsAuthenticationFailure()
      throws IOException, AuthenticationException {
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    HandlerResult completedResult = mock(HandlerResult.class);
    when(completedResult.getStatus()).thenReturn(Status.COMPLETED);
    when(completedResult.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(completedResult);
    when(handler1.getAuthenticationType()).thenReturn("test");

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("test"));

    filter.setHandlerList(Collections.singletonList(handler1));

    RuntimeException rootCause = new RuntimeException("Root cause");
    AuthenticationFailureException exception =
        new AuthenticationFailureException("Auth failed", rootCause);

    doThrow(exception).when(filterChain).doFilter(any(), any());

    HandlerResult errorResult = mock(HandlerResult.class);
    when(errorResult.getStatus()).thenReturn(Status.NO_ACTION);
    when(handler1.handleError(any(), any(), any())).thenReturn(errorResult);

    try {
      filter.doFilter(request, response, filterChain);
    } catch (AuthenticationFailureException e) {
      verify(securityLogger, times(1))
          .audit(eq("Authentication failed. Error message: '{}'"), eq("Root cause"));
    }
  }

  @Test
  public void testReturnSimpleResponseHandlesIOException() throws Exception {
    // This tests the private returnSimpleResponse method indirectly
    when(contextPolicyManager.getGuestAccess()).thenReturn(false);
    doThrow(new IOException("Test exception"))
        .when(response)
        .sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

    filter.doFilter(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
  }

  @Test
  public void testNoAuthPolicySetOnWhitelistedPath() throws IOException, AuthenticationException {
    when(contextPolicyManager.isWhiteListed("/test/path")).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    verify(request, times(1)).setAttribute(eq(ContextPolicy.NO_AUTH_POLICY), eq(true));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testGuestAccessCreatesGuestToken() throws IOException, AuthenticationException {
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(Status.NO_ACTION);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(noActionResult);
    when(handler1.getAuthenticationType()).thenReturn("test");

    when(contextPolicyManager.getContextPolicy("/test/path")).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("test"));

    filter.setHandlerList(Collections.singletonList(handler1));

    filter.doFilter(request, response, filterChain);

    verify(request, times(1)).setAttribute(eq(AUTHENTICATION_TOKEN_KEY), any());
  }
}
