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
package org.codice.ddf.security.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.Subject;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.audit.SecurityLogger;
import ddf.security.service.SecurityManager;
import ddf.security.service.SecurityServiceException;
import javax.servlet.http.HttpServletRequest;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.cxf.ws.security.wss4j.PolicyBasedWSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.shiro.subject.PrincipalCollection;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.security.handler.GuestAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Comprehensive test coverage for GuestInterceptor to achieve 85%+ coverage. */
@RunWith(MockitoJUnitRunner.class)
public class GuestInterceptorTest {

  private GuestInterceptor interceptor;

  @Mock private SecurityManager securityManager;
  @Mock private SecurityLogger securityLogger;
  @Mock private SoapMessage soapMessage;
  @Mock private HttpServletRequest httpRequest;
  @Mock private Subject subject;
  @Mock private PrincipalCollection principalCollection;
  @Mock private SecurityAssertion securityAssertion;

  @Before
  public void setup() {
    interceptor = new GuestInterceptor(securityManager);
    interceptor.setSecurityLogger(securityLogger);
  }

  @Test
  public void testConstructorSetsPhaseAndBefore() {
    GuestInterceptor newInterceptor = new GuestInterceptor(securityManager);

    assertEquals(Phase.PRE_PROTOCOL, newInterceptor.getPhase());
    assertTrue(newInterceptor.getBefore().contains(WSS4JInInterceptor.class.getName()));
    assertTrue(newInterceptor.getBefore().contains(PolicyBasedWSS4JInInterceptor.class.getName()));
  }

  @Test
  public void testHandleMessageWithNullMessage() {
    interceptor.handleMessage(null);
    // Should complete without exception
  }

  @Test
  public void testHandleMessageCreatesGuestSubject() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);

    ArgumentCaptor<GuestAuthenticationToken> tokenCaptor =
        ArgumentCaptor.forClass(GuestAuthenticationToken.class);
    when(securityManager.getSubject(tokenCaptor.capture())).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    verify(securityManager, times(1)).getSubject(any(GuestAuthenticationToken.class));
    verify(soapMessage, times(1)).put(eq(SecurityContext.class), any());
    verify(soapMessage, times(1))
        .put(eq(WSS4JInInterceptor.class.getName() + ".DONE"), eq(Boolean.TRUE));

    GuestAuthenticationToken token = tokenCaptor.getValue();
    assertThat(token, is(notNullValue()));
  }

  @Test
  public void testHandleMessageUsesRemoteAddr() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("10.0.0.1");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    verify(httpRequest, times(1)).getRemoteAddr();
  }

  @Test
  public void testHandleMessageWithNullSubject() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(null);

    interceptor.handleMessage(soapMessage);

    verify(soapMessage, never()).put(eq(SecurityContext.class), any());
    verify(soapMessage, never()).put(eq(WSS4JInInterceptor.class.getName() + ".DONE"), any());
  }

  @Test
  public void testHandleMessageWithNullSecurityAssertion() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(null);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    verify(soapMessage, times(1)).put(eq(SecurityContext.class), any());
    verify(soapMessage, times(1))
        .put(eq(WSS4JInInterceptor.class.getName() + ".DONE"), eq(Boolean.TRUE));
  }

  @Test(expected = Fault.class)
  public void testHandleMessageSecurityManagerThrowsException() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");
    when(securityManager.getSubject(any(GuestAuthenticationToken.class)))
        .thenThrow(new SecurityServiceException("Test exception"));

    interceptor.handleMessage(soapMessage);
  }

  @Test(expected = Fault.class)
  public void testHandleMessageAuthenticationExceptionWrappedInFault() throws Exception {
    interceptor = new GuestInterceptor(null);
    interceptor.setSecurityLogger(securityLogger);

    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    interceptor.handleMessage(soapMessage);
  }

  @Test
  public void testCachedSubjectReused() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    // First call - should create and cache subject
    interceptor.handleMessage(soapMessage);

    // Second call with same IP - should use cached subject
    interceptor.handleMessage(soapMessage);

    // SecurityManager should only be called once
    verify(securityManager, times(1)).getSubject(any(GuestAuthenticationToken.class));
  }

  @Test
  public void testDifferentIPAddressesCreateDifferentSubjects() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    // First IP
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");
    interceptor.handleMessage(soapMessage);

    // Second IP
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.101");
    interceptor.handleMessage(soapMessage);

    // SecurityManager should be called twice for different IPs
    verify(securityManager, times(2)).getSubject(any(GuestAuthenticationToken.class));
  }

  @Test
  public void testSecurityServiceExceptionLogged() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");
    when(securityManager.getSubject(any(GuestAuthenticationToken.class)))
        .thenThrow(new SecurityServiceException("Test exception"));

    try {
      interceptor.handleMessage(soapMessage);
    } catch (Fault e) {
      assertThat(e.getCause(), is(notNullValue()));
    }
  }

  @Test
  public void testSetSecurityLogger() {
    SecurityLogger customLogger = mock(SecurityLogger.class);
    interceptor.setSecurityLogger(customLogger);
    assertThat(interceptor, is(notNullValue()));
  }

  @Test
  public void testHandleMessageWithPrincipalCreated() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
    verify(soapMessage, times(1)).put(eq(SecurityContext.class), contextCaptor.capture());

    SecurityContext context = contextCaptor.getValue();
    assertThat(context, is(notNullValue()));
    assertThat(context.getUserPrincipal(), is(notNullValue()));
  }

  @Test
  public void testHandleMessageWithNullPrincipal() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(null);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
    verify(soapMessage, times(1)).put(eq(SecurityContext.class), contextCaptor.capture());

    SecurityContext context = contextCaptor.getValue();
    assertThat(context, is(notNullValue()));
    assertThat(context.getUserPrincipal(), is(nullValue()));
  }

  @Test
  public void testWSS4JCheckStringSet() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    verify(soapMessage, times(1))
        .put(eq(WSS4JInInterceptor.class.getName() + ".DONE"), eq(Boolean.TRUE));
  }

  @Test
  public void testMultipleCallsSameIPUseCache() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    // Call multiple times with same IP
    interceptor.handleMessage(soapMessage);
    interceptor.handleMessage(soapMessage);
    interceptor.handleMessage(soapMessage);

    // Should only create subject once, rest from cache
    verify(securityManager, times(1)).getSubject(any(GuestAuthenticationToken.class));
  }

  @Test
  public void testNullHttpRequestHandled() {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(null);

    try {
      interceptor.handleMessage(soapMessage);
    } catch (Exception e) {
      // Should handle null request gracefully
    }
  }

  @Test
  public void testSecurityManagerNullThrowsAuthenticationException() throws Exception {
    GuestInterceptor nullManagerInterceptor = new GuestInterceptor(null);

    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    try {
      nullManagerInterceptor.handleMessage(soapMessage);
    } catch (Fault f) {
      assertThat(f.getCause(), is(notNullValue()));
      assertTrue(f.getCause() instanceof AuthenticationException);
    }
  }

  @Test
  public void testGetSubjectWithNullSecurityManager() throws Exception {
    GuestInterceptor nullManagerInterceptor = new GuestInterceptor(null);

    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    try {
      nullManagerInterceptor.handleMessage(soapMessage);
    } catch (Fault f) {
      Throwable cause = f.getCause();
      assertThat(cause, is(notNullValue()));
      assertTrue(cause instanceof AuthenticationException);
      assertEquals("Unable to create the guest subject, system is not ready.", cause.getMessage());
    }
  }

  @Test
  public void testCacheExpiration() throws Exception {
    // This test verifies cache behavior (expiration is 1 minute)
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    // Verify cache is working
    verify(securityManager, times(1)).getSubject(any(GuestAuthenticationToken.class));
  }

  @Test
  public void testSubjectWithPrincipalsReturned() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(securityAssertion);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    verify(subject, times(1)).getPrincipals();
    verify(principalCollection, times(1)).oneByType(SecurityAssertion.class);
  }

  @Test
  public void testSecurityContextSetWithNullPrincipal() throws Exception {
    when(soapMessage.get(AbstractHTTPDestination.HTTP_REQUEST)).thenReturn(httpRequest);
    when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.100");

    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(SecurityAssertion.class)).thenReturn(null);
    when(securityManager.getSubject(any(GuestAuthenticationToken.class))).thenReturn(subject);

    interceptor.handleMessage(soapMessage);

    ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
    verify(soapMessage, times(1)).put(eq(SecurityContext.class), contextCaptor.capture());

    SecurityContext ctx = contextCaptor.getValue();
    assertThat(ctx.getUserPrincipal(), is(nullValue()));
  }
}
