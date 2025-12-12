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
package ddf.security.audit.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.codice.ddf.system.alerts.SystemNotice;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class LogFailoverAlertAppenderTest {

  @Test
  public void testConstructorWithNullEventAdminThrowsException() {
    assertThrows(NullPointerException.class, () -> new LogFailoverAlertAppender(null));
  }

  @Test
  public void testFirstAppendPostsEvent() {
    EventAdmin eventAdmin = mock(EventAdmin.class);
    PaxLoggingEvent loggingEvent = mock(PaxLoggingEvent.class);
    LogFailoverAlertAppender appender = new LogFailoverAlertAppender(eventAdmin);

    appender.doAppend(loggingEvent);

    verify(eventAdmin).postEvent(any(Event.class));
  }

  @Test
  public void testMultipleAppendsWithinThrottleOnlyPostsOnce() {
    EventAdmin eventAdmin = mock(EventAdmin.class);
    PaxLoggingEvent loggingEvent = mock(PaxLoggingEvent.class);
    LogFailoverAlertAppender appender = new LogFailoverAlertAppender(eventAdmin);

    // First call should post
    appender.doAppend(loggingEvent);
    // Second call within 5 seconds should be throttled
    appender.doAppend(loggingEvent);
    // Third call within 5 seconds should also be throttled
    appender.doAppend(loggingEvent);

    // Only one event should be posted
    verify(eventAdmin, times(1)).postEvent(any(Event.class));
  }

  @Test
  public void testEventTopicContainsAudit() {
    EventAdmin eventAdmin = mock(EventAdmin.class);
    PaxLoggingEvent loggingEvent = mock(PaxLoggingEvent.class);
    LogFailoverAlertAppender appender = new LogFailoverAlertAppender(eventAdmin);

    appender.doAppend(loggingEvent);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventAdmin).postEvent(eventCaptor.capture());

    Event event = eventCaptor.getValue();
    assertThat(event.getTopic(), containsString(SystemNotice.SYSTEM_NOTICE_BASE_TOPIC));
    assertThat(event.getTopic(), containsString("audit"));
  }

  @Test
  public void testEventContainsCorrectProperties() {
    EventAdmin eventAdmin = mock(EventAdmin.class);
    PaxLoggingEvent loggingEvent = mock(PaxLoggingEvent.class);
    LogFailoverAlertAppender appender = new LogFailoverAlertAppender(eventAdmin);

    appender.doAppend(loggingEvent);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventAdmin).postEvent(eventCaptor.capture());

    Event event = eventCaptor.getValue();
    // Verify event has SystemNotice properties
    assertThat(event.getProperty("title").toString(), containsString("Audit Logging Failure"));
  }

  @Test
  public void testConstructorAcceptsValidEventAdmin() {
    EventAdmin eventAdmin = mock(EventAdmin.class);

    LogFailoverAlertAppender appender = new LogFailoverAlertAppender(eventAdmin);

    // Should not throw - just verify we can create the appender
    verify(eventAdmin, never()).postEvent(any(Event.class));
  }
}
