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
package ddf.security.config.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.cm.ConfigurationEvent;

public class ConfigurationSecurityLoggerTest {

  private ConfigurationSecurityLogger logger;
  private SecurityLogger securityLogger;
  private ConfigurationEvent event;

  @BeforeEach
  public void setUp() {
    logger = new ConfigurationSecurityLogger();
    securityLogger = mock(SecurityLogger.class);
    logger.setSecurityLogger(securityLogger);
    event = mock(ConfigurationEvent.class);
    when(event.getPid()).thenReturn("test.pid");
  }

  @AfterEach
  public void tearDown() {
    ThreadContext.unbindSubject();
  }

  @Test
  public void testConfigurationEventWithSubjectLogsAudit() {
    Subject subject = mock(Subject.class);
    ThreadContext.bind(subject);
    when(event.getType()).thenReturn(1); // CM_UPDATED

    logger.configurationEvent(event);

    verify(securityLogger).audit(eq("Configuration {} for {}."), eq("updated"), eq("test.pid"));
  }

  @Test
  public void testConfigurationEventWithoutSubjectLogsAuditWarn() {
    // No subject bound
    when(event.getType()).thenReturn(1); // CM_UPDATED

    logger.configurationEvent(event);

    verify(securityLogger)
        .auditWarn(eq("Configuration {} via filesystem for {}."), eq("updated"), eq("test.pid"));
  }

  @Test
  public void testEventTypeUpdated() {
    Subject subject = mock(Subject.class);
    ThreadContext.bind(subject);
    when(event.getType()).thenReturn(1); // CM_UPDATED

    logger.configurationEvent(event);

    verify(securityLogger).audit(anyString(), eq("updated"), anyString());
  }

  @Test
  public void testEventTypeDeleted() {
    Subject subject = mock(Subject.class);
    ThreadContext.bind(subject);
    when(event.getType()).thenReturn(2); // CM_DELETED

    logger.configurationEvent(event);

    verify(securityLogger).audit(anyString(), eq("deleted"), anyString());
  }

  @Test
  public void testEventTypeLocationChanged() {
    Subject subject = mock(Subject.class);
    ThreadContext.bind(subject);
    when(event.getType()).thenReturn(3); // CM_LOCATION_CHANGED

    logger.configurationEvent(event);

    verify(securityLogger).audit(anyString(), eq("location changed"), anyString());
  }

  @Test
  public void testEventTypeUnknown() {
    Subject subject = mock(Subject.class);
    ThreadContext.bind(subject);
    when(event.getType()).thenReturn(99); // Unknown type

    logger.configurationEvent(event);

    verify(securityLogger).audit(anyString(), eq("unknown"), anyString());
  }

  @Test
  public void testSetSecurityLogger() {
    ConfigurationSecurityLogger newLogger = new ConfigurationSecurityLogger();
    SecurityLogger mockLogger = mock(SecurityLogger.class);

    newLogger.setSecurityLogger(mockLogger);

    // Setup event and subject
    Subject subject = mock(Subject.class);
    ThreadContext.bind(subject);
    ConfigurationEvent testEvent = mock(ConfigurationEvent.class);
    when(testEvent.getType()).thenReturn(1);
    when(testEvent.getPid()).thenReturn("another.pid");

    newLogger.configurationEvent(testEvent);

    verify(mockLogger).audit(anyString(), eq("updated"), eq("another.pid"));
  }
}
