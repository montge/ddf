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
package ddf.security.http.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.SecurityConstants;
import ddf.security.audit.SecurityLogger;
import ddf.security.common.PrincipalHolder;
import ddf.security.http.SessionFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HttpSessionFactoryTest {

  private HttpSessionFactory factory;
  private SecurityLogger securityLogger;

  @BeforeEach
  public void setUp() {
    factory = new HttpSessionFactory();
    securityLogger = mock(SecurityLogger.class);
    factory.setSecurityLogger(securityLogger);
    factory.setExpirationTime(30);
  }

  @Test
  public void testImplementsSessionFactory() {
    assertThat(factory, is(instanceOf(SessionFactory.class)));
  }

  @Test
  public void testGetOrCreateSessionCreatesNewSession() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(null);
    when(session.getId()).thenReturn("session-id-123");
    when(request.getRemoteAddr()).thenReturn("192.168.1.1");

    HttpSession result = factory.getOrCreateSession(request);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(session));
  }

  @Test
  public void testGetOrCreateSessionSetsPrincipalHolder() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(null);
    when(session.getId()).thenReturn("session-id-456");
    when(request.getRemoteAddr()).thenReturn("10.0.0.1");

    factory.getOrCreateSession(request);

    verify(session)
        .setAttribute(
            eq(SecurityConstants.SECURITY_TOKEN_KEY),
            org.mockito.ArgumentMatchers.any(PrincipalHolder.class));
  }

  @Test
  public void testGetOrCreateSessionSetsMaxInactiveInterval() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(null);
    when(session.getId()).thenReturn("session-id-789");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");

    factory.setExpirationTime(60);
    factory.getOrCreateSession(request);

    // 60 minutes * 60 seconds = 3600 seconds
    verify(session).setMaxInactiveInterval(3600);
  }

  @Test
  public void testGetOrCreateSessionAuditsNewSession() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(null);
    when(session.getId()).thenReturn("test-session-id");
    when(request.getRemoteAddr()).thenReturn("172.16.0.1");

    factory.getOrCreateSession(request);

    verify(securityLogger).audit(anyString(), anyString(), anyString());
  }

  @Test
  public void testGetOrCreateSessionReusesExistingSession() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    PrincipalHolder existingHolder = new PrincipalHolder();
    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(existingHolder);

    HttpSession result = factory.getOrCreateSession(request);

    assertThat(result, is(session));
  }

  @Test
  public void testSetExpirationTime() {
    factory.setExpirationTime(45);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(null);
    when(session.getId()).thenReturn("session-id");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");

    factory.getOrCreateSession(request);

    // 45 minutes * 60 seconds = 2700 seconds
    verify(session).setMaxInactiveInterval(2700);
  }

  @Test
  public void testSetSecurityLogger() {
    HttpSessionFactory newFactory = new HttpSessionFactory();
    SecurityLogger mockLogger = mock(SecurityLogger.class);

    newFactory.setSecurityLogger(mockLogger);
    newFactory.setExpirationTime(10);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY)).thenReturn(null);
    when(session.getId()).thenReturn("new-session-id");
    when(request.getRemoteAddr()).thenReturn("192.168.0.1");

    newFactory.getOrCreateSession(request);

    verify(mockLogger).audit(anyString(), anyString(), anyString());
  }
}
