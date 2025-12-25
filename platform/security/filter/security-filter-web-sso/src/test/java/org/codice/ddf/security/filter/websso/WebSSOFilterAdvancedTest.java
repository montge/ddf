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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.SecurityConstants;
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
import org.codice.ddf.platform.filter.AuthenticationFailureException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.codice.ddf.security.handler.GuestAuthenticationToken;
import org.codice.ddf.security.handler.api.AuthenticationHandler;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.codice.ddf.security.handler.api.HandlerResult.Status;
import org.codice.ddf.security.policy.context.ContextPolicy;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Advanced test suite for WebSSOFilter covering complex scenarios, error handling, and edge cases.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WebSSOFilterAdvancedTest {

  private static final String TEST_CONTEXT = "/secure/resource";
  private static final String REMOTE_IP = "192.168.1.100";
  private static final String X_FORWARDED_FOR_IP = "10.0.0.50";
  private static final String SESSION_ID = "test-session-id";

  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private SessionFactory sessionFactory;
  @Mock private SecurityLogger securityLogger;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private HttpSession session;
  @Mock private SecurityFilterChain filterChain;
  @Mock private ContextPolicy contextPolicy;
  @Mock private AuthenticationHandler handler1;
  @Mock private AuthenticationHandler handler2;

  private WebSSOFilter webSSOFilter;

  @BeforeEach
  public void setUp() {
    webSSOFilter = new WebSSOFilter();
    webSSOFilter.setContextPolicyManager(contextPolicyManager);
    webSSOFilter.setSessionFactory(sessionFactory);
    webSSOFilter.setSecurityLogger(securityLogger);

    when(request.getRequestURI()).thenReturn(TEST_CONTEXT);
    when(request.getRemoteAddr()).thenReturn(REMOTE_IP);
    when(session.getId()).thenReturn(SESSION_ID);
  }

  // ==================== IP Address Resolution Tests ====================

  @Test
  public void testIpAddressFromXForwardedForHeader() throws Exception {
    // Test that X-Forwarded-For header is used when present
    when(request.getHeader("X-FORWARDED-FOR")).thenReturn(X_FORWARDED_FOR_IP);
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(result.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));
    webSSOFilter.doFilter(request, response, filterChain);

    verify(request).getHeader("X-FORWARDED-FOR");
  }

  @Test
  public void testIpAddressFromRemoteAddrWhenNoXForwardedFor() throws Exception {
    // Test that remote address is used when X-Forwarded-For is absent
    when(request.getHeader("X-FORWARDED-FOR")).thenReturn(null);
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(result.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));
    webSSOFilter.doFilter(request, response, filterChain);

    verify(request).getRemoteAddr();
  }

  // ==================== Session Handling Tests ====================

  @Test
  public void testSessionWithValidPrincipals() throws Exception {
    // Test retrieving existing session with valid principals
    PrincipalCollection principalCollection = mock(PrincipalCollection.class);
    SecurityAssertion assertion = mock(SecurityAssertion.class);
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(assertion));

    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    when(principalHolder.getPrincipals()).thenReturn(principalCollection);

    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn(SESSION_ID);
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    webSSOFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(request).setAttribute(eq(AUTHENTICATION_TOKEN_KEY), any(HandlerResult.class));
  }

  @Test
  public void testSessionWithNoPrincipals() throws Exception {
    // Test session with no principals falls back to handlers
    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    when(principalHolder.getPrincipals()).thenReturn(null);

    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));
    when(request.getRequestedSessionId()).thenReturn(SESSION_ID);
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(result.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));
    webSSOFilter.doFilter(request, response, filterChain);

    verify(handler1).getNormalizedToken(any(), any(), any(), anyBoolean());
  }

  @Test
  public void testSessionNotFoundCreatesNewSession() throws Exception {
    // Test that new session is created when requested session doesn't exist
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));
    when(request.getRequestedSessionId()).thenReturn(SESSION_ID);
    when(request.getSession(false)).thenReturn(null);
    when(sessionFactory.getOrCreateSession(request)).thenReturn(session);

    PrincipalHolder principalHolder = mock(PrincipalHolder.class);
    when(principalHolder.getPrincipals()).thenReturn(null);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(result.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));
    webSSOFilter.doFilter(request, response, filterChain);

    verify(sessionFactory).getOrCreateSession(request);
  }

  @Test
  public void testSessionFactoryNullThrowsException() {
    // Test that null session factory throws SessionException
    webSSOFilter.setSessionFactory(null);
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(request.getRequestedSessionId()).thenReturn(SESSION_ID);
    when(request.getSession(false)).thenReturn(null);

    assertThrows(
        SessionException.class,
        () -> {
          webSSOFilter.doFilter(request, response, filterChain);
        });
  }

  // ==================== Multi-Handler Tests ====================

  @Test
  public void testMultipleHandlersFirstSucceeds() throws Exception {
    // Test that first handler succeeds and second is not called
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods())
        .thenReturn(Arrays.asList("handler1", "handler2"));

    when(handler1.getAuthenticationType()).thenReturn("handler1");
    HandlerResult successResult = mock(HandlerResult.class);
    when(successResult.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(successResult.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(successResult);

    when(handler2.getAuthenticationType()).thenReturn("handler2");

    webSSOFilter.setHandlerList(Arrays.asList(handler1, handler2));
    webSSOFilter.doFilter(request, response, filterChain);

    verify(handler1).getNormalizedToken(any(), any(), any(), eq(false));
    verify(handler2, never()).getNormalizedToken(any(), any(), any(), anyBoolean());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void testMultipleHandlersFirstFailsSecondSucceeds() throws Exception {
    // Test that second handler is called when first returns NO_ACTION
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods())
        .thenReturn(Arrays.asList("handler1", "handler2"));

    when(handler1.getAuthenticationType()).thenReturn("handler1");
    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(Status.NO_ACTION);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(noActionResult);

    when(handler2.getAuthenticationType()).thenReturn("handler2");
    HandlerResult successResult = mock(HandlerResult.class);
    when(successResult.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(successResult.getToken()).thenReturn(token);
    when(handler2.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(successResult);

    webSSOFilter.setHandlerList(Arrays.asList(handler1, handler2));
    webSSOFilter.doFilter(request, response, filterChain);

    verify(handler1).getNormalizedToken(any(), any(), any(), eq(false));
    verify(handler2).getNormalizedToken(any(), any(), any(), eq(false));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void testHandlerResolvingOnSecondPassWithResolve() throws Exception {
    // Test that handlers are called with resolve=true on second pass
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(Status.NO_ACTION);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(noActionResult);

    HandlerResult redirectedResult = mock(HandlerResult.class);
    when(redirectedResult.getStatus()).thenReturn(Status.REDIRECTED);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(true))).thenReturn(redirectedResult);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));

    try {
      webSSOFilter.doFilter(request, response, filterChain);
    } catch (AuthenticationChallengeException e) {
      // Expected
    }

    verify(handler1).getNormalizedToken(any(), any(), any(), eq(false));
    verify(handler1).getNormalizedToken(any(), any(), any(), eq(true));
    verify(filterChain, never()).doFilter(any(), any());
  }

  // ==================== Guest Access Tests ====================

  @Test
  public void testGuestAccessEnabledWithNoHandlers() throws Exception {
    // Test that guest token is created when guest access enabled and no handlers
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    webSSOFilter.setHandlerList(Collections.emptyList());
    webSSOFilter.doFilter(request, response, filterChain);

    ArgumentCaptor<HandlerResult> captor = ArgumentCaptor.forClass(HandlerResult.class);
    verify(request).setAttribute(eq(AUTHENTICATION_TOKEN_KEY), captor.capture());
    assertThat(captor.getValue().getToken(), is(notNullValue()));
    assertThat(captor.getValue().getToken() instanceof GuestAuthenticationToken, is(true));
  }

  @Test
  public void testGuestAccessEnabledWithNoActionResult() throws Exception {
    // Test that guest token is created when handlers return NO_ACTION and guest access enabled
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(Status.NO_ACTION);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(noActionResult);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));
    webSSOFilter.doFilter(request, response, filterChain);

    ArgumentCaptor<HandlerResult> captor = ArgumentCaptor.forClass(HandlerResult.class);
    verify(request).setAttribute(eq(AUTHENTICATION_TOKEN_KEY), captor.capture());
    assertThat(captor.getValue().getToken() instanceof GuestAuthenticationToken, is(true));
  }

  @Test
  public void testGuestAccessDisabledWithNoActionResult() throws Exception {
    // Test that failure occurs when guest access disabled and handlers return NO_ACTION
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(false);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult noActionResult = mock(HandlerResult.class);
    when(noActionResult.getStatus()).thenReturn(Status.NO_ACTION);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(noActionResult);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          webSSOFilter.doFilter(request, response, filterChain);
        });
  }

  // ==================== Error Handling Tests ====================

  @Test
  public void testFilterChainThrowsException() throws Exception {
    // Test that exceptions in filter chain are handled and re-thrown
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(result.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);

    // Mock handleError to return NO_ACTION result to prevent NPE
    HandlerResult errorResult = mock(HandlerResult.class);
    when(errorResult.getStatus()).thenReturn(Status.NO_ACTION);
    when(handler1.handleError(any(), any(), any())).thenReturn(errorResult);

    SecurityFilterChain throwingChain =
        (req, res) -> {
          throw new IOException("Filter chain error");
        };

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));

    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          webSSOFilter.doFilter(request, response, throwingChain);
        });
  }

  @Test
  public void testHandlerErrorRecoveryNoAction() {
    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          // Test error recovery when handler returns NO_ACTION
          when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
          when(contextPolicyManager.getSessionAccess()).thenReturn(false);
          when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
          when(contextPolicy.getAuthenticationMethods())
              .thenReturn(Collections.singletonList("basic"));

          when(handler1.getAuthenticationType()).thenReturn("basic");
          HandlerResult result = mock(HandlerResult.class);
          when(result.getStatus()).thenReturn(Status.COMPLETED);
          BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
          when(result.getToken()).thenReturn(token);
          when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);

          HandlerResult noActionError = mock(HandlerResult.class);
          when(noActionError.getStatus()).thenReturn(Status.NO_ACTION);
          when(handler1.handleError(any(), any(), any())).thenReturn(noActionError);

          SecurityFilterChain throwingChain =
              (req, res) -> {
                throw new IOException("Chain error");
              };

          webSSOFilter.setHandlerList(Collections.singletonList(handler1));
          webSSOFilter.doFilter(request, response, throwingChain);
        });
  }

  // ==================== Status Code Tests ====================

  @Test
  public void testCompletedStatusWithNullToken() {
    assertThrows(
        AuthenticationFailureException.class,
        () -> {
          // Test that COMPLETED status with null token is rejected
          when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
          when(contextPolicyManager.getSessionAccess()).thenReturn(false);
          when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
          when(contextPolicy.getAuthenticationMethods())
              .thenReturn(Collections.singletonList("basic"));

          when(handler1.getAuthenticationType()).thenReturn("basic");
          HandlerResult result = mock(HandlerResult.class);
          when(result.getStatus()).thenReturn(Status.COMPLETED);
          when(result.getToken()).thenReturn(null);
          when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);

          webSSOFilter.setHandlerList(Collections.singletonList(handler1));
          webSSOFilter.doFilter(request, response, filterChain);
        });
  }

  // ==================== Context Policy Tests ====================

  @Test
  public void testNoContextPolicyWithGuestAccessCreatesGuestToken() throws Exception {
    // When no context policy exists, no handlers match, but guest access creates guest token
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(null);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);

    when(handler1.getAuthenticationType()).thenReturn("handler1");
    when(handler2.getAuthenticationType()).thenReturn("handler2");

    webSSOFilter.setHandlerList(Arrays.asList(handler1, handler2));
    webSSOFilter.doFilter(request, response, filterChain);

    // No handlers should be called since none match the null policy
    verify(handler1, never()).getNormalizedToken(any(), any(), any(), anyBoolean());
    verify(handler2, never()).getNormalizedToken(any(), any(), any(), anyBoolean());
    // Guest token should be created
    verify(request).setAttribute(eq(AUTHENTICATION_TOKEN_KEY), any(HandlerResult.class));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void testNullContextPolicyManagerThrowsNPE() throws Exception {
    // When contextPolicyManager is null, an NPE is thrown from handleRequest
    // because contextPolicyManager.getSessionAccess() is called without null check
    webSSOFilter.setContextPolicyManager(null);

    when(handler1.getAuthenticationType()).thenReturn("handler1");
    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(result.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), eq(false))).thenReturn(result);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));

    assertThrows(
        NullPointerException.class, () -> webSSOFilter.doFilter(request, response, filterChain));
  }

  // ==================== Allow Guest Flag Tests ====================

  @Test
  public void testAllowGuestFlagSetOnToken() throws Exception {
    // Test that allowGuest flag is set on BaseAuthenticationToken
    when(contextPolicyManager.isWhiteListed(TEST_CONTEXT)).thenReturn(false);
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    when(contextPolicyManager.getGuestAccess()).thenReturn(true);
    when(contextPolicyManager.getContextPolicy(TEST_CONTEXT)).thenReturn(contextPolicy);
    when(contextPolicy.getAuthenticationMethods()).thenReturn(Collections.singletonList("basic"));

    when(handler1.getAuthenticationType()).thenReturn("basic");
    HandlerResult result = mock(HandlerResult.class);
    when(result.getStatus()).thenReturn(Status.COMPLETED);
    BaseAuthenticationToken token = mock(BaseAuthenticationToken.class);
    when(result.getToken()).thenReturn(token);
    when(handler1.getNormalizedToken(any(), any(), any(), anyBoolean())).thenReturn(result);

    webSSOFilter.setHandlerList(Collections.singletonList(handler1));
    webSSOFilter.doFilter(request, response, filterChain);

    verify(token).setAllowGuest(true);
  }

  // ==================== Lifecycle Tests ====================

  @Test
  public void testInitMethod() {
    // Test initialization
    webSSOFilter.init();
    // Should complete without errors
  }

  @Test
  public void testDestroyMethod() {
    // Test destroy method
    webSSOFilter.destroy();
    // Should complete without errors
  }

  @Test
  public void testToStringMethod() {
    // Test toString returns class name
    String result = webSSOFilter.toString();
    assertThat(result, is(WebSSOFilter.class.getName()));
  }

  // ==================== Getters and Setters Tests ====================

  @Test
  public void testHandlerListGetterSetter() {
    // Test handler list getter and setter
    List<AuthenticationHandler> handlers = new ArrayList<>();
    handlers.add(handler1);
    webSSOFilter.setHandlerList(handlers);
    assertThat(webSSOFilter.getHandlerList(), is(handlers));
  }

  @Test
  public void testContextPolicyManagerGetterSetter() {
    // Test context policy manager getter and setter
    webSSOFilter.setContextPolicyManager(contextPolicyManager);
    assertThat(webSSOFilter.getContextPolicyManager(), is(contextPolicyManager));
  }
}
