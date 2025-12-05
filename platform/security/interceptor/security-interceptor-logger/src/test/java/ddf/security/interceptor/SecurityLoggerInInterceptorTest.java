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
package ddf.security.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ddf.security.SubjectOperations;
import ddf.security.audit.SecurityLogger;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link SecurityLoggerInInterceptor} class. */
@RunWith(MockitoJUnitRunner.class)
public class SecurityLoggerInInterceptorTest {

  private static final String TEST_URL = "https://example.com/test";

  @Mock private SubjectOperations subjectOperations;

  @Mock private SecurityLogger securityLogger;

  @Mock private Message message;

  @Mock private Subject subject;

  private SecurityLoggerInInterceptor interceptor;

  @Before
  public void setUp() {
    interceptor = new SecurityLoggerInInterceptor();
    interceptor.setSubjectOperations(subjectOperations);
    interceptor.setSecurityLogger(securityLogger);

    when(message.get(Message.REQUEST_URL)).thenReturn(TEST_URL);
  }

  @After
  public void tearDown() {
    ThreadContext.unbindSubject();
  }

  @Test
  public void testConstructorSetsInvokePhase() {
    assertThat(interceptor.getPhase(), is(Phase.INVOKE));
  }

  @Test
  public void testHandleMessageWithSubjectLogsUsername() {
    // When REQUESTOR_ROLE is null or false, isRequestor returns false
    // meaning !isRequestor is true and the interceptor will log
    when(message.get(Message.REQUESTOR_ROLE)).thenReturn(null);
    when(subjectOperations.getName(subject)).thenReturn("testuser");
    ThreadContext.bind(subject);

    interceptor.handleMessage(message);

    verify(securityLogger).audit("{} is making an inbound request to {}.", "testuser", TEST_URL);
  }

  @Test
  public void testHandleMessageWithoutSubjectLogsNoSubject() {
    when(message.get(Message.REQUESTOR_ROLE)).thenReturn(null);

    interceptor.handleMessage(message);

    verify(securityLogger).audit("No subject associated with inbound request to {}.", TEST_URL);
  }

  @Test
  public void testHandleMessageAsRequestorDoesNotLog() {
    // When REQUESTOR_ROLE is true, isRequestor returns true
    // meaning !isRequestor is false and the interceptor will NOT log
    when(message.get(Message.REQUESTOR_ROLE)).thenReturn(Boolean.TRUE);

    interceptor.handleMessage(message);

    verifyNoInteractions(securityLogger);
  }

  @Test(expected = IllegalStateException.class)
  public void testHandleMessageWithSubjectButNoSubjectOperationsThrows() {
    interceptor.setSubjectOperations(null);
    when(message.get(Message.REQUESTOR_ROLE)).thenReturn(null);
    ThreadContext.bind(subject);

    interceptor.handleMessage(message);
  }

  @Test
  public void testSetSubjectOperations() {
    SubjectOperations newOps = mock(SubjectOperations.class);
    interceptor.setSubjectOperations(newOps);
    assertThat(interceptor.getPhase(), is(Phase.INVOKE));
  }

  @Test
  public void testSetSecurityLogger() {
    SecurityLogger newLogger = mock(SecurityLogger.class);
    interceptor.setSecurityLogger(newLogger);
    assertThat(interceptor.getPhase(), is(Phase.INVOKE));
  }
}
