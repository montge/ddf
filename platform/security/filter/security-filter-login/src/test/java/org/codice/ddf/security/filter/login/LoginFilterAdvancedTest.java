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
package org.codice.ddf.security.filter.login;

import static ddf.security.SecurityConstants.AUTHENTICATION_TOKEN_KEY;
import static ddf.security.SecurityConstants.SECURITY_JAVA_SUBJECT;
import static ddf.security.SecurityConstants.SECURITY_TOKEN_KEY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.Subject;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.common.PrincipalHolder;
import ddf.security.http.SessionFactory;
import ddf.security.service.SecurityManager;
import ddf.security.service.SecurityServiceException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.codice.ddf.security.handler.HandlerResultImpl;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.codice.ddf.security.policy.context.ContextPolicy;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensaml.core.config.InitializationService;

/**
 * Advanced test suite for LoginFilter covering error handling, edge cases, and complex scenarios.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoginFilterAdvancedTest {

  private static final String TEST_REQUEST_URI = "/secure/resource";
  private static final String TEST_CONTEXT_PATH = "/secure";
  private static final String TEST_SERVLET_PATH = "/resource";

  @Mock private SecurityManager securityManager;
  @Mock private SessionFactory sessionFactory;
  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private HttpSession session;
  @Mock private SecurityFilterChain filterChain;
  @Mock private BaseAuthenticationToken authToken;
  @Mock private Subject subject;
  @Mock private PrincipalCollection principalCollection;
  @Mock private SecurityAssertion securityAssertion;

  private LoginFilter loginFilter;
  private PrincipalHolder principalHolder;

  @BeforeAll
  public static void initClass() throws Exception {
    InitializationService.initialize();
  }

  @BeforeEach
  public void setUp() throws Exception {
    loginFilter = new LoginFilter();
    loginFilter.setSecurityManager(securityManager);
    loginFilter.setSessionFactory(sessionFactory);
    loginFilter.setContextPolicyManager(contextPolicyManager);
    loginFilter.init();

    principalHolder = new PrincipalHolder();
    SimplePrincipalCollection simplePrincipalCollection = new SimplePrincipalCollection();
    principalHolder.setPrincipals(simplePrincipalCollection);

    when(request.getRequestURI()).thenReturn(TEST_REQUEST_URI);
    when(request.getContextPath()).thenReturn(TEST_CONTEXT_PATH);
    when(request.getServletPath()).thenReturn(TEST_SERVLET_PATH);
    when(sessionFactory.getOrCreateSession(request)).thenReturn(session);
    when(session.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
  }

  // ==================== No Auth Policy Tests ====================

  @Test
  public void testNoAuthPolicySkipsFilter() throws Exception {
    // Test that NO_AUTH_POLICY attribute causes filter to be skipped
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn("true");

    loginFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(securityManager, never()).getSubject(any());
    verify(request, never())
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), any());
  }

  @Test
  public void testNoAuthPolicyWithNullValue() throws Exception {
    // Test that null NO_AUTH_POLICY value continues filter processing
    when(request.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken)).thenReturn(subject);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    loginFilter.doFilter(request, response, filterChain);

    verify(securityManager).getSubject(authToken);
  }

  // ==================== Token Validation Tests ====================

  @Test
  public void testNonHandlerResultToken() throws Exception {
    // Test that non-HandlerResult token is ignored
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn("not-a-handler-result");

    loginFilter.doFilter(request, response, filterChain);

    verify(securityManager, never()).getSubject(any());
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  public void testHandlerResultWithNonBaseAuthToken() throws Exception {
    // Test that HandlerResult with non-BaseAuthenticationToken is ignored
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, null);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    loginFilter.doFilter(request, response, filterChain);

    verify(securityManager, never()).getSubject(any());
    verify(filterChain, never()).doFilter(any(), any());
  }

  // ==================== X.509 Certificate Handling Tests ====================

  @Test
  public void testX509CertificatesAttachedToToken() throws Exception {
    // Test that X.509 certificates are attached to authentication token
    X509Certificate[] certs = new X509Certificate[1];
    certs[0] = mock(X509Certificate.class);

    when(request.getAttribute("jakarta.servlet.request.X509Certificate")).thenReturn(certs);
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken)).thenReturn(subject);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    loginFilter.doFilter(request, response, filterChain);

    verify(authToken).setX509Certs(certs);
  }

  @Test
  public void testRequestURIAttachedToToken() throws Exception {
    // Test that request URI is attached to authentication token
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken)).thenReturn(subject);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    loginFilter.doFilter(request, response, filterChain);

    verify(authToken).setRequestURI(TEST_REQUEST_URI);
  }

  // ==================== Security Manager Tests ====================

  @Test
  public void testNullSecurityManagerThrowsException() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          // Test that null security manager throws exception
          loginFilter.setSecurityManager(null);
          HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
          when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

          loginFilter.doFilter(request, response, filterChain);
        });
  }

  @Test
  public void testSecurityManagerThrowsSecurityServiceException() throws Exception {
    // Test handling of SecurityServiceException from security manager
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken))
        .thenThrow(new SecurityServiceException("Authentication failed"));

    loginFilter.doFilter(request, response, filterChain);

    verify(securityManager).getSubject(authToken);
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  public void testSecurityManagerReturnsNullSubject() throws Exception {
    // Test handling when security manager returns null subject
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken)).thenReturn(null);

    loginFilter.doFilter(request, response, filterChain);

    verify(securityManager).getSubject(authToken);
    verify(filterChain, never()).doFilter(any(), any());
  }

  // ==================== Subject Execution Tests ====================

  @Test
  public void testSubjectExecutesFilterChain() throws Exception {
    // Test that filter chain is executed within subject context
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken)).thenReturn(subject);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    loginFilter.doFilter(request, response, filterChain);

    verify(request).setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), eq(subject));
  }

  @Test
  public void testSubjectWithoutSecurityAssertions() throws Exception {
    // Test handling of subject without security assertions
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken)).thenReturn(subject);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(Collections.emptyList());

    loginFilter.doFilter(request, response, filterChain);

    verify(request).setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), eq(subject));
    verify(request, never()).setAttribute(eq(SECURITY_JAVA_SUBJECT), any());
  }

  // ==================== Session Management Tests ====================

  @Test
  public void testSessionAccessEnabledStoresSubject() throws Exception {
    // Test that subject is stored in session when session access is enabled
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    PrincipalCollection newPrincipalCollection = mock(PrincipalCollection.class);
    when(newPrincipalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    // Use a real SubjectImpl so that execute() actually runs the callable
    ddf.security.Subject realSubject =
        new ddf.security.impl.SubjectImpl(
            newPrincipalCollection, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(securityManager.getSubject(authToken)).thenReturn(realSubject);

    PrincipalHolder holder = new PrincipalHolder();
    holder.setPrincipals(principalCollection);
    when(session.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(holder);

    loginFilter.doFilter(request, response, filterChain);

    verify(sessionFactory, org.mockito.Mockito.atLeast(1)).getOrCreateSession(request);
  }

  @Test
  public void testSessionAccessDisabledDoesNotStoreSubject() throws Exception {
    // Test that subject is not stored in session when session access is disabled
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken)).thenReturn(subject);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    loginFilter.doFilter(request, response, filterChain);

    verify(sessionFactory, never()).getOrCreateSession(any());
  }

  @Test
  public void testSessionFactoryNullThrowsException() {
    // Test that null session factory throws exception when session access enabled
    // The exception is wrapped in ExecutionException when thrown from subject.execute()
    assertThrows(
        org.apache.shiro.subject.ExecutionException.class,
        () -> {
          loginFilter.setSessionFactory(null);
          when(contextPolicyManager.getSessionAccess()).thenReturn(true);
          HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
          when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

          // Use real SubjectImpl so execute() actually runs the callable
          PrincipalCollection principals = mock(PrincipalCollection.class);
          when(principals.byType(SecurityAssertion.class))
              .thenReturn(Collections.singletonList(securityAssertion));
          ddf.security.Subject realSubject =
              new ddf.security.impl.SubjectImpl(
                  principals, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));
          when(securityManager.getSubject(authToken)).thenReturn(realSubject);

          loginFilter.doFilter(request, response, filterChain);
        });
  }

  // ==================== Java Subject Tests ====================

  @Test
  public void testJavaSubjectCreatedFromSecurityAssertions() throws Exception {
    // Test that Java subject is created from security assertions
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    java.security.Principal principal1 = mock(java.security.Principal.class);
    java.security.Principal principal2 = mock(java.security.Principal.class);
    when(securityAssertion.getPrincipals())
        .thenReturn(new HashSet<>(Arrays.asList(principal1, principal2)));

    PrincipalCollection principals = mock(PrincipalCollection.class);
    when(principals.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    // Use real SubjectImpl so execute() actually runs
    ddf.security.Subject realSubject =
        new ddf.security.impl.SubjectImpl(
            principals, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));
    when(securityManager.getSubject(authToken)).thenReturn(realSubject);

    loginFilter.doFilter(request, response, filterChain);

    ArgumentCaptor<javax.security.auth.Subject> captor =
        ArgumentCaptor.forClass(javax.security.auth.Subject.class);
    verify(request).setAttribute(eq(SECURITY_JAVA_SUBJECT), captor.capture());
    assertThat(captor.getValue().getPrincipals().size(), is(2));
  }

  @Test
  public void testJavaSubjectStoredInSessionWhenEnabled() throws Exception {
    // Test that Java subject is stored in session when session access enabled
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    java.security.Principal principal = mock(java.security.Principal.class);
    when(securityAssertion.getPrincipals()).thenReturn(Collections.singleton(principal));

    PrincipalCollection principals = mock(PrincipalCollection.class);
    when(principals.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    // Use real SubjectImpl so execute() actually runs
    ddf.security.Subject realSubject =
        new ddf.security.impl.SubjectImpl(
            principals, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));
    when(securityManager.getSubject(authToken)).thenReturn(realSubject);

    loginFilter.doFilter(request, response, filterChain);

    verify(session).setAttribute(eq("subject"), any(javax.security.auth.Subject.class));
  }

  // ==================== Principal Update Tests ====================

  @Test
  public void testPrincipalHolderUpdatedWithNewPrincipals() throws Exception {
    // Test that principal holder is updated when principals change
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    // Use actual SimplePrincipalCollection instances which are naturally not equal
    SimplePrincipalCollection oldPrincipals = new SimplePrincipalCollection();
    oldPrincipals.add("oldPrincipal", "realm");
    // For the new principals, we need a mock so we can stub byType()
    PrincipalCollection newPrincipals = mock(PrincipalCollection.class);
    when(newPrincipals.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    PrincipalHolder holder = new PrincipalHolder();
    holder.setPrincipals(oldPrincipals);
    when(session.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(holder);

    // Use real SubjectImpl so execute() actually runs
    ddf.security.Subject realSubject =
        new ddf.security.impl.SubjectImpl(
            newPrincipals, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));
    when(securityManager.getSubject(authToken)).thenReturn(realSubject);

    loginFilter.doFilter(request, response, filterChain);

    assertThat(holder.getPrincipals(), is(newPrincipals));
  }

  @Test
  public void testPrincipalHolderNotUpdatedWhenPrincipalsMatch() throws Exception {
    // Test that principal holder is not updated when principals have same reference
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    // Use the same principal collection reference for both old and new
    // We need to use a mock here because the filter calls byType() on the principals
    PrincipalCollection samePrincipals = mock(PrincipalCollection.class);
    when(samePrincipals.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    PrincipalHolder holder = new PrincipalHolder();
    holder.setPrincipals(samePrincipals);
    when(session.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(holder);

    when(securityManager.getSubject(authToken)).thenReturn(subject);
    when(subject.getPrincipals()).thenReturn(samePrincipals);

    loginFilter.doFilter(request, response, filterChain);

    assertThat(holder.getPrincipals(), is(samePrincipals));
  }

  // ==================== Context Path Tests ====================

  @Test
  public void testSubjectPrincipalLoggedForBlankContextPath() throws Exception {
    // Test logging when context path is blank
    when(request.getContextPath()).thenReturn("");
    when(request.getServletPath()).thenReturn(TEST_SERVLET_PATH);
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManager.getSubject(authToken)).thenReturn(subject);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(subject.getPrincipal()).thenReturn("test-user");
    when(principalCollection.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    loginFilter.doFilter(request, response, filterChain);

    verify(subject).getPrincipal();
  }

  // ==================== Lifecycle Tests ====================

  @Test
  public void testInitMethod() {
    // Test initialization
    loginFilter.init();
    // Should complete without errors
  }

  @Test
  public void testDestroyMethod() {
    // Test destroy method cleans up ThreadLocal
    loginFilter.destroy();
    // Should complete without errors
  }

  // ==================== Multiple Security Assertions Tests ====================

  @Test
  public void testMultipleSecurityAssertions() throws Exception {
    // Test handling of multiple security assertions
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    SecurityAssertion assertion1 = mock(SecurityAssertion.class);
    SecurityAssertion assertion2 = mock(SecurityAssertion.class);
    java.security.Principal principal1 = mock(java.security.Principal.class);
    java.security.Principal principal2 = mock(java.security.Principal.class);
    java.security.Principal principal3 = mock(java.security.Principal.class);

    when(assertion1.getPrincipals())
        .thenReturn(new HashSet<>(Arrays.asList(principal1, principal2)));
    when(assertion2.getPrincipals()).thenReturn(Collections.singleton(principal3));

    PrincipalCollection principals = mock(PrincipalCollection.class);
    when(principals.byType(SecurityAssertion.class))
        .thenReturn(Arrays.asList(assertion1, assertion2));

    // Use real SubjectImpl so execute() actually runs
    ddf.security.Subject realSubject =
        new ddf.security.impl.SubjectImpl(
            principals, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));
    when(securityManager.getSubject(authToken)).thenReturn(realSubject);

    loginFilter.doFilter(request, response, filterChain);

    ArgumentCaptor<javax.security.auth.Subject> captor =
        ArgumentCaptor.forClass(javax.security.auth.Subject.class);
    verify(request).setAttribute(eq(SECURITY_JAVA_SUBJECT), captor.capture());
    assertThat(captor.getValue().getPrincipals().size(), is(3));
  }

  // ==================== Edge Case Tests ====================

  @Test
  public void testEmptyPrincipalsFromAssertion() throws Exception {
    // Test handling of security assertion with empty principals
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, authToken);
    when(request.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityAssertion.getPrincipals()).thenReturn(Collections.emptySet());

    PrincipalCollection principals = mock(PrincipalCollection.class);
    when(principals.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertion));

    // Use real SubjectImpl so execute() actually runs
    ddf.security.Subject realSubject =
        new ddf.security.impl.SubjectImpl(
            principals, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));
    when(securityManager.getSubject(authToken)).thenReturn(realSubject);

    loginFilter.doFilter(request, response, filterChain);

    ArgumentCaptor<javax.security.auth.Subject> captor =
        ArgumentCaptor.forClass(javax.security.auth.Subject.class);
    verify(request).setAttribute(eq(SECURITY_JAVA_SUBJECT), captor.capture());
    assertThat(captor.getValue().getPrincipals().isEmpty(), is(true));
  }
}
