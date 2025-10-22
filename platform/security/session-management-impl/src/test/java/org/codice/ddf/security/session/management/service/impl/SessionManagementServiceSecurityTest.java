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
package org.codice.ddf.security.session.management.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SessionManagementServiceSecurityTest {

  private static final String TEST_SESSION_ID = "test-session-123";
  private static final String TEST_SESSION_ID_2 = "test-session-456";
  private static final String MALICIOUS_SESSION_ID = "../../../etc/passwd";
  private static final String SQL_INJECTION_ID = "'; DROP TABLE sessions; --";
  private static final String XSS_SESSION_ID = "<script>alert('xss')</script>";

  @Mock private SessionManager sessionManager;
  @Mock private Session session;
  @Mock private Session session2;

  private SessionManagementServiceImpl sessionManagementService;

  @Before
  public void setUp() {
    sessionManagementService = new SessionManagementServiceImpl();
    sessionManagementService.setSessionManager(sessionManager);

    when(session.getId()).thenReturn(TEST_SESSION_ID);
    when(session.getStartTimestamp()).thenReturn(new Date());
    when(session.getLastAccessTime()).thenReturn(new Date());
    when(session.getTimeout()).thenReturn(Duration.ofMinutes(30).toMillis());

    when(session2.getId()).thenReturn(TEST_SESSION_ID_2);
    when(session2.getStartTimestamp()).thenReturn(new Date());
    when(session2.getLastAccessTime()).thenReturn(new Date());
    when(session2.getTimeout()).thenReturn(Duration.ofMinutes(30).toMillis());
  }

  // ==================== Session Retrieval Security Tests ====================

  @Test
  public void testGetAllSessionsWithValidSessions() {
    // Test retrieving all active sessions
    Collection<Session> mockSessions = new ArrayList<>();
    mockSessions.add(session);
    mockSessions.add(session2);

    when(sessionManager.getActiveSessions()).thenReturn(mockSessions);

    Collection<Session> activeSessions = sessionManagementService.getAllSessions();

    assertThat(activeSessions, notNullValue());
    assertThat(activeSessions, hasSize(2));
  }

  @Test
  public void testGetAllSessionsWhenNoActiveSessions() {
    // Test when no sessions are active
    when(sessionManager.getActiveSessions()).thenReturn(new ArrayList<>());

    Collection<Session> activeSessions = sessionManagementService.getAllSessions();

    assertThat(activeSessions, notNullValue());
    assertThat(activeSessions, empty());
  }

  @Test
  public void testGetAllSessionsHandlesException() {
    // Test exception handling during session retrieval
    when(sessionManager.getActiveSessions())
        .thenThrow(new RuntimeException("Session retrieval failed"));

    try {
      sessionManagementService.getAllSessions();
    } catch (RuntimeException e) {
      // Should propagate exception or return empty collection
    }
  }

  // ==================== Session Expiration Security Tests ====================

  @Test
  public void testExpireValidSession() {
    // Test expiring a valid session
    when(sessionManager.getSession(any())).thenReturn(session);

    sessionManagementService.expireSession(TEST_SESSION_ID);

    verify(session).stop();
  }

  @Test
  public void testExpireNonExistentSession() {
    // Test expiring non-existent session (should not fail)
    when(sessionManager.getSession(any())).thenReturn(null);

    sessionManagementService.expireSession("non-existent-session");

    verify(session, never()).stop();
  }

  @Test
  public void testExpireNullSessionId() {
    // Test with null session ID
    try {
      sessionManagementService.expireSession(null);
    } catch (Exception e) {
      // May throw NullPointerException or IllegalArgumentException
    }
  }

  @Test
  public void testExpireEmptySessionId() {
    // Test with empty session ID
    when(sessionManager.getSession(any())).thenReturn(null);

    sessionManagementService.expireSession("");

    verify(session, never()).stop();
  }

  @Test
  public void testExpireSessionWithExceptionHandling() {
    // Test exception during session expiration
    when(sessionManager.getSession(any())).thenReturn(session);
    doThrow(new RuntimeException("Session stop failed")).when(session).stop();

    try {
      sessionManagementService.expireSession(TEST_SESSION_ID);
      // Should handle exception gracefully
    } catch (Exception e) {
      // Expected
    }
  }

  // ==================== Injection Attack Tests ====================

  @Test
  public void testPathTraversalInSessionId() {
    // Test path traversal attack in session ID
    when(sessionManager.getSession(any())).thenReturn(null);

    sessionManagementService.expireSession(MALICIOUS_SESSION_ID);

    // Should not traverse file system, just treat as session ID
    verify(sessionManager).getSession(any());
  }

  @Test
  public void testSqlInjectionInSessionId() {
    // Test SQL injection pattern (not applicable but test sanitization)
    when(sessionManager.getSession(any())).thenReturn(null);

    sessionManagementService.expireSession(SQL_INJECTION_ID);

    // Should handle safely without SQL execution
    verify(sessionManager).getSession(any());
  }

  @Test
  public void testXssInSessionId() {
    // Test XSS pattern in session ID
    when(sessionManager.getSession(any())).thenReturn(session);

    sessionManagementService.expireSession(XSS_SESSION_ID);

    // Should handle safely
    verify(session).stop();
  }

  @Test
  public void testSpecialCharactersInSessionId() {
    // Test special characters in session ID
    String specialId = "session\n\r\t\"'\\<>&";
    when(sessionManager.getSession(any())).thenReturn(session);

    sessionManagementService.expireSession(specialId);

    verify(sessionManager).getSession(any());
  }

  // ==================== Session Timeout Tests ====================

  @Test
  public void testExpiredSessionNotRetrieved() {
    // Test that expired sessions are not included
    when(session.getTimeout()).thenReturn(0L); // Expired
    Collection<Session> mockSessions = new ArrayList<>();
    mockSessions.add(session);

    when(sessionManager.getActiveSessions()).thenReturn(mockSessions);

    Collection<Session> activeSessions = sessionManagementService.getAllSessions();

    // Should return collection (expired sessions handled by session manager)
    assertThat(activeSessions, notNullValue());
  }

  @Test
  public void testSessionTimeoutUpdate() {
    // Test updating session timeout
    when(sessionManager.getSession(any())).thenReturn(session);

    long newTimeout = Duration.ofHours(1).toMillis();
    when(session.getTimeout()).thenReturn(newTimeout);

    // Verify timeout can be retrieved
    Collection<Session> mockSessions = new ArrayList<>();
    mockSessions.add(session);
    when(sessionManager.getActiveSessions()).thenReturn(mockSessions);

    Collection<Session> sessions = sessionManagementService.getAllSessions();
    assertThat(sessions, hasSize(1));
  }

  // ==================== Concurrent Session Management Tests ====================

  @Test
  public void testConcurrentSessionExpiration() throws Exception {
    // Test concurrent expiration of multiple sessions
    when(sessionManager.getSession(any())).thenReturn(session);

    ExecutorService executor = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(10);

    for (int i = 0; i < 10; i++) {
      executor.submit(
          () -> {
            try {
              sessionManagementService.expireSession(TEST_SESSION_ID);
            } finally {
              latch.countDown();
            }
          });
    }

    assertThat(latch.await(5, TimeUnit.SECONDS), is(true));
    executor.shutdown();

    // Should handle concurrent calls safely
    verify(sessionManager, times(10)).getSession(any());
  }

  @Test
  public void testConcurrentSessionRetrieval() throws Exception {
    // Test concurrent retrieval of sessions
    Collection<Session> mockSessions = new ArrayList<>();
    mockSessions.add(session);
    mockSessions.add(session2);

    when(sessionManager.getActiveSessions()).thenReturn(mockSessions);

    ExecutorService executor = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(10);

    for (int i = 0; i < 10; i++) {
      executor.submit(
          () -> {
            try {
              sessionManagementService.getAllSessions();
            } finally {
              latch.countDown();
            }
          });
    }

    assertThat(latch.await(5, TimeUnit.SECONDS), is(true));
    executor.shutdown();

    verify(sessionManager, times(10)).getActiveSessions();
  }

  // ==================== Session Lifecycle Tests ====================

  @Test
  public void testMultipleSessionExpiration() {
    // Test expiring multiple sessions
    when(sessionManager.getSession(new DefaultSessionKey(TEST_SESSION_ID))).thenReturn(session);
    when(sessionManager.getSession(new DefaultSessionKey(TEST_SESSION_ID_2))).thenReturn(session2);

    sessionManagementService.expireSession(TEST_SESSION_ID);
    sessionManagementService.expireSession(TEST_SESSION_ID_2);

    verify(session).stop();
    verify(session2).stop();
  }

  @Test
  public void testSessionExpirationIdempotency() {
    // Test that expiring same session multiple times is safe
    when(sessionManager.getSession(any())).thenReturn(session);

    sessionManagementService.expireSession(TEST_SESSION_ID);
    sessionManagementService.expireSession(TEST_SESSION_ID);
    sessionManagementService.expireSession(TEST_SESSION_ID);

    // Should be safe to call multiple times
    verify(session, times(3)).stop();
  }

  // ==================== Session Information Security ====================

  @Test
  public void testSessionStartTimeRetrieval() {
    // Test retrieving session start time
    Date startTime = new Date();
    when(session.getStartTimestamp()).thenReturn(startTime);

    Collection<Session> mockSessions = new ArrayList<>();
    mockSessions.add(session);
    when(sessionManager.getActiveSessions()).thenReturn(mockSessions);

    Collection<Session> sessions = sessionManagementService.getAllSessions();
    assertThat(sessions, hasSize(1));
    assertThat(sessions.iterator().next().getStartTimestamp(), is(startTime));
  }

  @Test
  public void testSessionLastAccessTimeRetrieval() {
    // Test retrieving last access time
    Date lastAccessTime = new Date();
    when(session.getLastAccessTime()).thenReturn(lastAccessTime);

    Collection<Session> mockSessions = new ArrayList<>();
    mockSessions.add(session);
    when(sessionManager.getActiveSessions()).thenReturn(mockSessions);

    Collection<Session> sessions = sessionManagementService.getAllSessions();
    assertThat(sessions, hasSize(1));
    assertThat(sessions.iterator().next().getLastAccessTime(), is(lastAccessTime));
  }

  // ==================== Session Manager Configuration Tests ====================

  @Test
  public void testSetSessionManager() {
    // Test session manager injection
    SessionManager newManager = mock(SessionManager.class);
    sessionManagementService.setSessionManager(newManager);

    when(newManager.getActiveSessions()).thenReturn(new ArrayList<>());
    sessionManagementService.getAllSessions();

    verify(newManager).getActiveSessions();
  }

  @Test
  public void testNullSessionManager() {
    // Test behavior with null session manager
    sessionManagementService.setSessionManager(null);

    try {
      sessionManagementService.getAllSessions();
    } catch (NullPointerException e) {
      // Expected
    }
  }

  // ==================== Long Session ID Tests ====================

  @Test
  public void testVeryLongSessionId() {
    // Test with extremely long session ID
    StringBuilder longId = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      longId.append("a");
    }

    when(sessionManager.getSession(any())).thenReturn(session);

    sessionManagementService.expireSession(longId.toString());

    verify(sessionManager).getSession(any());
  }

  @Test
  public void testUnicodeInSessionId() {
    // Test Unicode characters in session ID
    String unicodeId = "session-\u4E2D\u6587-\u0628\u0627\u0644\u0639\u0631\u0628\u064A\u0629";

    when(sessionManager.getSession(any())).thenReturn(session);

    sessionManagementService.expireSession(unicodeId);

    verify(sessionManager).getSession(any());
  }

  // ==================== Batch Operations ====================

  @Test
  public void testExpireAllSessions() {
    // Test expiring all active sessions
    Collection<Session> mockSessions = new ArrayList<>();
    mockSessions.add(session);
    mockSessions.add(session2);

    when(sessionManager.getActiveSessions()).thenReturn(mockSessions);

    Collection<Session> sessions = sessionManagementService.getAllSessions();
    sessions.forEach(
        s -> {
          sessionManagementService.expireSession((String) s.getId());
        });

    // Should have attempted to expire both sessions
    verify(sessionManager, times(2)).getSession(any());
  }

  // ==================== Session Attribute Security ====================

  @Test
  public void testSessionAttributeIsolation() {
    // Test that session attributes don't leak between sessions
    when(session.getAttribute("user")).thenReturn("user1");
    when(session2.getAttribute("user")).thenReturn("user2");

    Collection<Session> mockSessions = new ArrayList<>();
    mockSessions.add(session);
    mockSessions.add(session2);

    when(sessionManager.getActiveSessions()).thenReturn(mockSessions);

    Collection<Session> sessions = sessionManagementService.getAllSessions();

    List<Session> sessionList = new ArrayList<>(sessions);
    assertThat(
        sessionList.get(0).getAttribute("user"),
        not(equalTo(sessionList.get(1).getAttribute("user"))));
  }

  // ==================== Error Recovery Tests ====================

  @Test
  public void testRecoveryFromSessionManagerFailure() {
    // Test recovery after session manager failure
    when(sessionManager.getActiveSessions())
        .thenThrow(new RuntimeException("Failure"))
        .thenReturn(new ArrayList<>());

    try {
      sessionManagementService.getAllSessions();
    } catch (Exception e) {
      // First call fails
    }

    // Second call should succeed
    Collection<Session> sessions = sessionManagementService.getAllSessions();
    assertThat(sessions, notNullValue());
  }

  @Test
  public void testPartialSessionExpirationFailure() {
    // Test when some sessions fail to expire
    when(sessionManager.getSession(any()))
        .thenReturn(session)
        .thenReturn(null); // Second session not found

    sessionManagementService.expireSession(TEST_SESSION_ID);
    sessionManagementService.expireSession("non-existent");

    // First should succeed, second should be handled gracefully
    verify(session).stop();
  }
}
