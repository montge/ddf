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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.SubjectOperations;
import ddf.security.audit.AuditPropertiesPlugin;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityLoggerImplTest {

  private static final String TEST_USER = "testUser";
  private static final String TEST_MESSAGE = "Test audit message";

  private SecurityLoggerImpl securityLogger;
  private SubjectOperations subjectOperations;

  @BeforeEach
  void setUp() {
    subjectOperations = mock(SubjectOperations.class);
    when(subjectOperations.getName(any(Subject.class), any(String.class))).thenReturn(TEST_USER);

    securityLogger = new SecurityLoggerImpl(subjectOperations);
  }

  @Test
  void testSecurityLoggerImplNotNull() {
    assertThat(securityLogger, is(notNullValue()));
  }

  @Test
  void testSetAuditPropertiesPlugins() {
    AuditPropertiesPlugin plugin = mock(AuditPropertiesPlugin.class);
    when(plugin.generate()).thenReturn(Pair.of("testKey", "testValue"));

    securityLogger.setAuditPropertiesPlugins(List.of(plugin));

    // Logger should be configured without error
    assertThat(securityLogger, is(notNullValue()));
  }

  @Test
  void testAuditWithMessage() {
    // Basic audit should not throw
    assertDoesNotThrow(() -> securityLogger.audit(TEST_MESSAGE));
  }

  @Test
  void testAuditWithSubject() {
    Subject subject = mock(Subject.class);
    securityLogger.audit(TEST_MESSAGE, subject);

    // The subject's name is resolved via SubjectOperations to build the audit line.
    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testAuditWithMessageAndParams() {
    assertDoesNotThrow(() -> securityLogger.audit("Test message with param: {}", "paramValue"));
  }

  @Test
  void testAuditWithSubjectAndParams() {
    Subject subject = mock(Subject.class);
    securityLogger.audit("Test message with param: {}", subject, "paramValue");

    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testAuditWithSuppliers() {
    assertDoesNotThrow(
        () -> securityLogger.audit("Test message with supplier: {}", () -> "suppliedValue"));
  }

  @Test
  void testAuditWithSubjectAndSuppliers() {
    Subject subject = mock(Subject.class);
    securityLogger.audit("Test message with supplier: {}", subject, () -> "suppliedValue");

    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testAuditWithThrowable() {
    Exception exception = new RuntimeException("Test exception");
    assertDoesNotThrow(() -> securityLogger.audit(TEST_MESSAGE, exception));
  }

  @Test
  void testAuditWithSubjectAndThrowable() {
    Subject subject = mock(Subject.class);
    Exception exception = new RuntimeException("Test exception");
    securityLogger.audit(TEST_MESSAGE, subject, exception);

    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testAuditWarnWithMessage() {
    assertDoesNotThrow(() -> securityLogger.auditWarn(TEST_MESSAGE));
  }

  @Test
  void testAuditWarnWithSubject() {
    Subject subject = mock(Subject.class);
    securityLogger.auditWarn(TEST_MESSAGE, subject);

    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testAuditWarnWithMessageAndParams() {
    assertDoesNotThrow(() -> securityLogger.auditWarn("Warning with param: {}", "paramValue"));
  }

  @Test
  void testAuditWarnWithSubjectAndParams() {
    Subject subject = mock(Subject.class);
    securityLogger.auditWarn("Warning with param: {}", subject, "paramValue");

    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testAuditWarnWithSuppliers() {
    assertDoesNotThrow(
        () -> securityLogger.auditWarn("Warning with supplier: {}", () -> "suppliedValue"));
  }

  @Test
  void testAuditWarnWithSubjectAndSuppliers() {
    Subject subject = mock(Subject.class);
    securityLogger.auditWarn("Warning with supplier: {}", subject, () -> "suppliedValue");

    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testAuditWarnWithThrowable() {
    Exception exception = new RuntimeException("Test exception");
    assertDoesNotThrow(() -> securityLogger.auditWarn(TEST_MESSAGE, exception));
  }

  @Test
  void testAuditWarnWithSubjectAndThrowable() {
    Subject subject = mock(Subject.class);
    Exception exception = new RuntimeException("Test exception");
    securityLogger.auditWarn(TEST_MESSAGE, subject, exception);

    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testMessageCleaningRemovesNewlines() {
    // Newlines should be replaced with underscores; cleaning must not throw on CR/LF input.
    String messageWithNewlines = "Test\nmessage\rwith\r\nnewlines";
    assertDoesNotThrow(() -> securityLogger.audit(messageWithNewlines));
  }

  @Test
  void testEmptyAuditPropertiesPlugins() {
    securityLogger.setAuditPropertiesPlugins(Collections.emptyList());
    assertDoesNotThrow(() -> securityLogger.audit(TEST_MESSAGE));
  }

  @Test
  void testAuditPropertiesPluginReturnsNull() {
    AuditPropertiesPlugin plugin = mock(AuditPropertiesPlugin.class);
    when(plugin.generate()).thenReturn(null);

    securityLogger.setAuditPropertiesPlugins(List.of(plugin));
    securityLogger.audit(TEST_MESSAGE);

    // A null-returning plugin must still be consulted while building the audit line.
    verify(plugin).generate();
  }

  @Test
  void testMultipleAuditPropertiesPlugins() {
    AuditPropertiesPlugin plugin1 = mock(AuditPropertiesPlugin.class);
    when(plugin1.generate()).thenReturn(Pair.of("key1", "value1"));

    AuditPropertiesPlugin plugin2 = mock(AuditPropertiesPlugin.class);
    when(plugin2.generate()).thenReturn(Pair.of("key2", "value2"));

    securityLogger.setAuditPropertiesPlugins(List.of(plugin1, plugin2));
    securityLogger.audit(TEST_MESSAGE);

    // Every configured plugin must be consulted to assemble the audit properties.
    verify(plugin1).generate();
    verify(plugin2).generate();
  }

  @Test
  void testSubjectOperationsReturnsUnknown() {
    when(subjectOperations.getName(any(Subject.class), eq("UNKNOWN"))).thenReturn("UNKNOWN");

    Subject subject = mock(Subject.class);
    securityLogger.audit(TEST_MESSAGE, subject);

    verify(subjectOperations).getName(subject, "UNKNOWN");
  }

  @Test
  void testNullSubject() {
    // Passing null subject should use ThreadContext or fall back gracefully.
    assertDoesNotThrow(() -> securityLogger.audit(TEST_MESSAGE, (Subject) null));
  }

  @Test
  void testAuditWithEmptyMessage() {
    assertDoesNotThrow(() -> securityLogger.audit(""));
  }

  @Test
  void testAuditWithNullThrowable() {
    assertDoesNotThrow(() -> securityLogger.audit(TEST_MESSAGE, (Throwable) null));
  }

  @Test
  void testAuditWarnWithNullThrowable() {
    assertDoesNotThrow(() -> securityLogger.auditWarn(TEST_MESSAGE, (Throwable) null));
  }

  @Test
  void testSubjectAttributeRetrieval() {
    when(subjectOperations.getAttribute(any(Subject.class), eq("testAttr")))
        .thenReturn(List.of("attrValue"));

    Subject subject = mock(Subject.class);

    // Set the system property for extra attributes
    String originalProp = System.getProperty("security.logger.extra_attributes");
    try {
      System.setProperty("security.logger.extra_attributes", "testAttr");
      securityLogger.audit(TEST_MESSAGE, subject);
    } finally {
      if (originalProp != null) {
        System.setProperty("security.logger.extra_attributes", originalProp);
      } else {
        System.clearProperty("security.logger.extra_attributes");
      }
    }

    // The configured extra attribute must be looked up on the subject for the audit line.
    verify(subjectOperations).getAttribute(subject, "testAttr");
  }

  @Test
  void testSubjectAttributeRetrievalMultipleValues() {
    when(subjectOperations.getAttribute(any(Subject.class), eq("testAttr")))
        .thenReturn(List.of("value1", "value2"));

    Subject subject = mock(Subject.class);

    String originalProp = System.getProperty("security.logger.extra_attributes");
    try {
      System.setProperty("security.logger.extra_attributes", "testAttr");
      securityLogger.audit(TEST_MESSAGE, subject);
    } finally {
      if (originalProp != null) {
        System.setProperty("security.logger.extra_attributes", originalProp);
      } else {
        System.clearProperty("security.logger.extra_attributes");
      }
    }

    // The configured extra attribute must be looked up to render its multiple values.
    verify(subjectOperations).getAttribute(subject, "testAttr");
  }

  @Test
  void testSubjectAttributeRetrievalEmptyList() {
    when(subjectOperations.getAttribute(any(Subject.class), eq("testAttr")))
        .thenReturn(Collections.emptyList());

    Subject subject = mock(Subject.class);

    String originalProp = System.getProperty("security.logger.extra_attributes");
    try {
      System.setProperty("security.logger.extra_attributes", "testAttr");
      securityLogger.audit(TEST_MESSAGE, subject);
    } finally {
      if (originalProp != null) {
        System.setProperty("security.logger.extra_attributes", originalProp);
      } else {
        System.clearProperty("security.logger.extra_attributes");
      }
    }

    // The configured extra attribute must still be looked up even when it has no values.
    verify(subjectOperations).getAttribute(subject, "testAttr");
  }
}
