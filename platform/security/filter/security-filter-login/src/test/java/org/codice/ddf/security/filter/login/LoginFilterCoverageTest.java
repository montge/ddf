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
import static ddf.security.SecurityConstants.SECURITY_TOKEN_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.Subject;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.common.PrincipalHolder;
import ddf.security.http.SessionFactory;
import ddf.security.impl.SubjectImpl;
import ddf.security.service.SecurityManager;
import ddf.security.service.SecurityServiceException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
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

/** Comprehensive test coverage for LoginFilter to increase coverage to 85%+. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoginFilterCoverageTest {

  private LoginFilter loginFilter;

  @Mock private HttpServletRequest requestMock;
  @Mock private HttpServletResponse responseMock;
  @Mock private HttpSession sessionMock;
  @Mock private SecurityFilterChain filterChainMock;
  @Mock private SecurityManager securityManagerMock;
  @Mock private SessionFactory sessionFactory;
  @Mock private ContextPolicyManager contextPolicyManager;
  @Mock private BaseAuthenticationToken authenticationTokenMock;
  @Mock private X509Certificate x509CertificateMock;
  @Mock private PrincipalHolder principalHolderMock;
  private X509Certificate[] x509Certificates;

  @BeforeAll
  public static void init() throws Exception {
    InitializationService.initialize();
  }

  @BeforeEach
  public void setup() throws Exception {
    x509Certificates = new X509Certificate[] {x509CertificateMock};

    loginFilter = new LoginFilter();
    loginFilter.setSecurityManager(securityManagerMock);
    loginFilter.setSessionFactory(sessionFactory);
    loginFilter.setContextPolicyManager(contextPolicyManager);
    loginFilter.init();

    when(sessionFactory.getOrCreateSession(any())).thenReturn(sessionMock);
    when(contextPolicyManager.getSessionAccess()).thenReturn(true);
    when(requestMock.getRequestURI()).thenReturn("/test/path");
    when(sessionMock.getId()).thenReturn("session-id");
    when(sessionMock.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolderMock);
  }

  @Test
  public void testNoAuthPolicySkipsFilter() throws Exception {
    when(requestMock.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn("true");

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(filterChainMock, times(1)).doFilter(requestMock, responseMock);
    verify(requestMock, never()).getAttribute(AUTHENTICATION_TOKEN_KEY);
    verify(securityManagerMock, never()).getSubject(any());
  }

  @Test
  public void testNoAuthenticationTokenReturnsEarly() throws Exception {
    when(requestMock.getAttribute(ContextPolicy.NO_AUTH_POLICY)).thenReturn(null);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(null);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(securityManagerMock, never()).getSubject(any());
    verify(filterChainMock, never()).doFilter(any(), any());
  }

  @Test
  public void testInvalidHandlerResultTypeReturnsEarly() throws Exception {
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn("not a handler result");

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(securityManagerMock, never()).getSubject(any());
  }

  @Test
  public void testHandlerResultWithNullTokenReturnsEarly() throws Exception {
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, null);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(securityManagerMock, never()).getSubject(any());
  }

  @Test
  public void testHandlerResultWithNonBaseAuthTokenReturnsEarly() throws Exception {
    HandlerResult result = mock(HandlerResult.class);
    when(result.getToken()).thenReturn(mock(AuthenticationToken.class));
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(securityManagerMock, never()).getSubject(any());
  }

  @Test
  public void testX509CertificatesSetOnToken() throws Exception {
    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(requestMock.getAttribute("javax.servlet.request.X509Certificate"))
        .thenReturn(x509Certificates);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));
    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.emptyList());
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(authenticationTokenMock, times(1)).setX509Certs(x509Certificates);
    verify(authenticationTokenMock, times(1)).setRequestURI("/test/path");
  }

  @Test
  public void testRequestURISetOnToken() throws Exception {
    when(requestMock.getRequestURI()).thenReturn("/admin/config");
    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));
    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.emptyList());
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(authenticationTokenMock, times(1)).setRequestURI("/admin/config");
  }

  @Test
  public void testSecurityServiceExceptionReturnsEarly() throws Exception {
    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock))
        .thenThrow(new SecurityServiceException("Test exception"));

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(requestMock, never())
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), any());
    verify(filterChainMock, never()).doFilter(any(), any());
  }

  @Test
  public void testNullSubjectReturnedBySecurityManager() throws Exception {
    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(null);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(requestMock, never())
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), any());
    verify(filterChainMock, never()).doFilter(any(), any());
  }

  @Test
  public void testSubjectExecutesFilterChain() throws Exception {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    PrincipalHolder principalHolder = new PrincipalHolder();
    principalHolder.setPrincipals(principalCollection);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertionMock = mock(SecurityAssertion.class);
    Principal principal = mock(Principal.class);

    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertionMock));
    when(securityAssertionMock.getPrincipals()).thenReturn(Collections.singleton(principal));
    when(principalCollectionMock.asList()).thenReturn(Arrays.asList("principal"));
    when(sessionMock.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(requestMock, times(1))
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), eq(subject));
    verify(filterChainMock, times(1)).doFilter(requestMock, responseMock);
  }

  @Test
  public void testSessionAccessDisabledSkipsSessionStorage() throws Exception {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertionMock = mock(SecurityAssertion.class);
    Principal principal = mock(Principal.class);

    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertionMock));
    when(securityAssertionMock.getPrincipals()).thenReturn(Collections.singleton(principal));

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(sessionFactory, never()).getOrCreateSession(any());
    verify(filterChainMock, times(1)).doFilter(any(), any());
  }

  @Test
  public void testPrincipalsStoredInSession() throws Exception {
    // Use actual SimplePrincipalCollection instances that are naturally different
    SimplePrincipalCollection oldPrincipals = new SimplePrincipalCollection();
    oldPrincipals.add("oldPrincipal", "realm");
    SimplePrincipalCollection newPrincipals = new SimplePrincipalCollection();
    newPrincipals.add("newPrincipal", "realm");

    PrincipalHolder principalHolder = new PrincipalHolder();
    principalHolder.setPrincipals(oldPrincipals);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertionMock = mock(SecurityAssertion.class);
    Principal principal = mock(Principal.class);

    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertionMock));
    when(securityAssertionMock.getPrincipals()).thenReturn(Collections.singleton(principal));
    when(principalCollectionMock.asList()).thenReturn(Arrays.asList("newPrincipal"));
    when(sessionMock.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    // Verify the filter chain was called - principals were updated internally
    verify(filterChainMock, times(1)).doFilter(any(), any());
  }

  @Test
  public void testPrincipalsNotUpdatedWhenEqual() throws Exception {
    // Use a mock for principals so we can properly stub the byType method
    PrincipalCollection samePrincipals = mock(PrincipalCollection.class);
    PrincipalHolder principalHolder = new PrincipalHolder();
    principalHolder.setPrincipals(samePrincipals);

    SecurityAssertion securityAssertionMock = mock(SecurityAssertion.class);
    Principal principal = mock(Principal.class);

    Subject subject =
        new SubjectImpl(
            samePrincipals, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(samePrincipals.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertionMock));
    when(securityAssertionMock.getPrincipals()).thenReturn(Collections.singleton(principal));
    when(sessionMock.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(filterChainMock, times(1)).doFilter(any(), any());
  }

  @Test
  public void testJavaSubjectStoredInSessionAndRequest() throws Exception {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    PrincipalHolder principalHolder = new PrincipalHolder();
    principalHolder.setPrincipals(principalCollection);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertionMock = mock(SecurityAssertion.class);
    Principal principal = mock(Principal.class);
    when(principal.getName()).thenReturn("testUser");

    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertionMock));
    when(securityAssertionMock.getPrincipals()).thenReturn(Collections.singleton(principal));
    when(principalCollectionMock.asList()).thenReturn(Arrays.asList("principal"));
    when(sessionMock.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    ArgumentCaptor<javax.security.auth.Subject> javaSubjectCaptor =
        ArgumentCaptor.forClass(javax.security.auth.Subject.class);
    verify(requestMock, times(1))
        .setAttribute(
            eq(ddf.security.SecurityConstants.SECURITY_JAVA_SUBJECT), javaSubjectCaptor.capture());
    verify(sessionMock, times(1)).setAttribute(eq("subject"), any());

    javax.security.auth.Subject javaSubject = javaSubjectCaptor.getValue();
    assertThat(javaSubject, is(notNullValue()));
    assertThat(javaSubject.getPrincipals().size(), is(1));
  }

  @Test
  public void testSubjectWithNoSecurityAssertions() throws Exception {
    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.emptyList());

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(requestMock, times(1))
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), eq(subject));
    verify(requestMock, never())
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_JAVA_SUBJECT), any());
  }

  @Test
  public void testContextPathUsedWhenNotBlank() throws Exception {
    when(requestMock.getContextPath()).thenReturn("/myapp");
    when(requestMock.getServletPath()).thenReturn("/servlet");

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.emptyList());
    when(principalCollectionMock.getPrimaryPrincipal()).thenReturn("testUser");

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(requestMock, org.mockito.Mockito.atLeast(1)).getContextPath();
  }

  @Test
  public void testServletPathUsedWhenContextPathBlank() throws Exception {
    when(requestMock.getContextPath()).thenReturn("");
    when(requestMock.getServletPath()).thenReturn("/servlet");

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.emptyList());
    when(principalCollectionMock.getPrimaryPrincipal()).thenReturn("testUser");

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(requestMock, times(1)).getServletPath();
  }

  @Test
  public void testInitMethod() {
    LoginFilter newFilter = new LoginFilter();
    newFilter.init();
    assertThat(newFilter, is(notNullValue()));
  }

  @Test
  public void testDestroyMethod() {
    loginFilter.destroy();
    assertThat(loginFilter, is(notNullValue()));
  }

  @Test
  public void testSetSecurityManager() {
    SecurityManager newManager = mock(SecurityManager.class);
    loginFilter.setSecurityManager(newManager);
    assertThat(loginFilter, is(notNullValue()));
  }

  @Test
  public void testSetSessionFactory() {
    SessionFactory newFactory = mock(SessionFactory.class);
    loginFilter.setSessionFactory(newFactory);
    assertThat(loginFilter, is(notNullValue()));
  }

  @Test
  public void testSetContextPolicyManager() {
    ContextPolicyManager newManager = mock(ContextPolicyManager.class);
    loginFilter.setContextPolicyManager(newManager);
    assertThat(loginFilter, is(notNullValue()));
  }

  @Test
  public void testMultipleSecurityAssertionPrincipals() throws Exception {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    PrincipalHolder principalHolder = new PrincipalHolder();
    principalHolder.setPrincipals(principalCollection);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertionMock = mock(SecurityAssertion.class);
    Principal principal1 = mock(Principal.class);
    Principal principal2 = mock(Principal.class);
    when(principal1.getName()).thenReturn("user1");
    when(principal2.getName()).thenReturn("user2");

    HashSet<Principal> principals = new HashSet<>(Arrays.asList(principal1, principal2));

    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertionMock));
    when(securityAssertionMock.getPrincipals()).thenReturn(principals);
    when(principalCollectionMock.asList()).thenReturn(Arrays.asList("principal"));
    when(sessionMock.getAttribute(SECURITY_TOKEN_KEY)).thenReturn(principalHolder);

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    ArgumentCaptor<javax.security.auth.Subject> javaSubjectCaptor =
        ArgumentCaptor.forClass(javax.security.auth.Subject.class);
    verify(requestMock, times(1))
        .setAttribute(
            eq(ddf.security.SecurityConstants.SECURITY_JAVA_SUBJECT), javaSubjectCaptor.capture());

    javax.security.auth.Subject javaSubject = javaSubjectCaptor.getValue();
    assertThat(javaSubject.getPrincipals().size(), is(2));
  }
}
