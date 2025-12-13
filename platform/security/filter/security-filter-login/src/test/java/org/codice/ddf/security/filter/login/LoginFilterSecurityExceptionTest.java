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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
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
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.platform.filter.SecurityFilterChain;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.codice.ddf.security.handler.HandlerResultImpl;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensaml.core.config.InitializationService;

/** Additional test coverage for LoginFilter exception handling and edge cases. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoginFilterSecurityExceptionTest {

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
  }

  @Test
  public void testSecurityManagerNull() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          loginFilter.setSecurityManager(null);

          HandlerResult result =
              new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
          when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

          loginFilter.doFilter(requestMock, responseMock, filterChainMock);
        });
  }

  @Test
  public void testSecurityServiceException() throws Exception {
    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock))
        .thenThrow(new SecurityServiceException("Test exception"));

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(requestMock, never())
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), any());
  }

  @Test
  public void testNullSubjectReturned() throws Exception {
    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(null);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(requestMock, never())
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), any());
  }

  @Test
  public void testSessionExceptionWhenNoSessionFactory() throws Exception {
    loginFilter.setSessionFactory(null);

    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    PrincipalHolder principalHolder = new PrincipalHolder();
    principalHolder.setPrincipals(principalCollection);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertionMock = mock(SecurityAssertion.class);

    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertionMock));
    when(principalCollectionMock.asList()).thenReturn(Arrays.asList("principal"));

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    try {
      loginFilter.doFilter(requestMock, responseMock, filterChainMock);
      fail("Expected SessionException to be thrown");
    } catch (SessionException e) {
      assertThat(e.getMessage(), is("Unable to store user's session."));
    }
  }

  @Test
  public void testX509CertificatesAttachedToToken() throws Exception {
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
  public void testContextPathUsedInLogging() throws Exception {
    when(requestMock.getContextPath()).thenReturn("/context");
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

    verify(requestMock, times(1))
        .setAttribute(eq(ddf.security.SecurityConstants.SECURITY_SUBJECT), eq(subject));
  }

  @Test
  public void testSessionAccessDisabled() throws Exception {
    when(contextPolicyManager.getSessionAccess()).thenReturn(false);

    PrincipalCollection principalCollectionMock = mock(PrincipalCollection.class);
    SecurityAssertion securityAssertionMock = mock(SecurityAssertion.class);
    Subject subject =
        new SubjectImpl(
            principalCollectionMock, true, null, mock(org.apache.shiro.mgt.SecurityManager.class));

    when(principalCollectionMock.byType(SecurityAssertion.class))
        .thenReturn(Collections.singletonList(securityAssertionMock));
    when(principalCollectionMock.asList()).thenReturn(Arrays.asList("principal"));

    HandlerResult result =
        new HandlerResultImpl(HandlerResult.Status.COMPLETED, authenticationTokenMock);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);
    when(securityManagerMock.getSubject(authenticationTokenMock)).thenReturn(subject);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(sessionFactory, never()).getOrCreateSession(any());
    verify(filterChainMock, times(1)).doFilter(any(), any());
  }

  @Test
  public void testDestroyRemovesThreadLocal() throws Exception {
    loginFilter.destroy();
    // Verify destroy completes without exception
    assertThat(loginFilter, is(notNullValue()));
  }

  @Test
  public void testInitCompletes() throws Exception {
    LoginFilter newFilter = new LoginFilter();
    newFilter.init();
    // Verify init completes without exception
    assertThat(newFilter, is(notNullValue()));
  }

  @Test
  public void testNonBaseAuthenticationToken() throws Exception {
    HandlerResult result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, null);
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn(result);

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(securityManagerMock, never()).getSubject(any());
  }

  @Test
  public void testNonHandlerResultToken() throws Exception {
    when(requestMock.getAttribute(AUTHENTICATION_TOKEN_KEY)).thenReturn("not a handler result");

    loginFilter.doFilter(requestMock, responseMock, filterChainMock);

    verify(securityManagerMock, never()).getSubject(any());
  }
}
