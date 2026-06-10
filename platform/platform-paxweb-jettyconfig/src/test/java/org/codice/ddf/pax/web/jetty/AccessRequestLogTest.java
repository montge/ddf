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
package org.codice.ddf.pax.web.jetty;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

import java.lang.reflect.Field;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccessRequestLogTest {

  @Mock private Request mockRequest;

  @Mock private Response mockResponse;

  private String originalPropertyValue;

  private static final String ACCESS_LOG_PROPERTY = "org.codice.ddf.http.access.log.enabled";

  @BeforeEach
  public void setup() {
    // Save original property value
    originalPropertyValue = System.getProperty(ACCESS_LOG_PROPERTY);
  }

  @AfterEach
  public void cleanup() {
    // Restore original property value
    if (originalPropertyValue != null) {
      System.setProperty(ACCESS_LOG_PROPERTY, originalPropertyValue);
    } else {
      System.clearProperty(ACCESS_LOG_PROPERTY);
    }
  }

  @Test
  public void testLogWhenAccessLogEnabled() {
    System.setProperty(ACCESS_LOG_PROPERTY, "true");

    AccessRequestLog requestLog = spy(new AccessRequestLog());

    // When access logging is enabled, log() delegates to the (unstarted) parent
    // RequestLogImpl, which is a no-op over zero appenders and must not throw.
    assertDoesNotThrow(() -> requestLog.log(mockRequest, mockResponse));
  }

  @Test
  public void testLogWhenAccessLogDisabled() {
    System.setProperty(ACCESS_LOG_PROPERTY, "false");

    AccessRequestLog requestLog = spy(new AccessRequestLog());

    // When disabled, log() short-circuits before touching the parent and must not throw.
    assertDoesNotThrow(() -> requestLog.log(mockRequest, mockResponse));
  }

  @Test
  public void testLogWhenPropertyNotSet() {
    System.clearProperty(ACCESS_LOG_PROPERTY);

    AccessRequestLog requestLog = spy(new AccessRequestLog());

    // When the property is not set, Boolean.valueOf(null) is false, so logging is disabled.
    assertFalse(isAccessLogEnabled(requestLog));
    assertDoesNotThrow(() -> requestLog.log(mockRequest, mockResponse));
  }

  @Test
  public void testLogWhenPropertySetToTrue() {
    System.setProperty(ACCESS_LOG_PROPERTY, "true");

    AccessRequestLog requestLog = new AccessRequestLog();

    // Property "true" enables logging; the call must still complete without throwing.
    assertTrue(isAccessLogEnabled(requestLog));
    assertDoesNotThrow(() -> requestLog.log(mockRequest, mockResponse));
  }

  @Test
  public void testLogWhenPropertySetToFalse() {
    System.setProperty(ACCESS_LOG_PROPERTY, "false");

    AccessRequestLog requestLog = new AccessRequestLog();

    // Property "false" disables logging; the call must complete without throwing.
    assertFalse(isAccessLogEnabled(requestLog));
    assertDoesNotThrow(() -> requestLog.log(mockRequest, mockResponse));
  }

  @Test
  public void testLogWhenPropertySetToInvalidValue() {
    System.setProperty(ACCESS_LOG_PROPERTY, "invalid");

    AccessRequestLog requestLog = new AccessRequestLog();

    // Boolean.valueOf("invalid") returns false, so logging is disabled.
    assertFalse(isAccessLogEnabled(requestLog));
    assertDoesNotThrow(() -> requestLog.log(mockRequest, mockResponse));
  }

  @Test
  public void testConstructorReadsSystemProperty() {
    System.setProperty(ACCESS_LOG_PROPERTY, "true");

    AccessRequestLog requestLog = new AccessRequestLog();

    // The constructor must read the system property into the enabled flag.
    assertTrue(isAccessLogEnabled(requestLog));
  }

  @Test
  public void testMultipleLogsWhenEnabled() {
    System.setProperty(ACCESS_LOG_PROPERTY, "true");

    AccessRequestLog requestLog = new AccessRequestLog();

    // Repeated log calls while enabled must all complete without throwing.
    assertDoesNotThrow(
        () -> {
          requestLog.log(mockRequest, mockResponse);
          requestLog.log(mockRequest, mockResponse);
          requestLog.log(mockRequest, mockResponse);
        });
  }

  @Test
  public void testMultipleLogsWhenDisabled() {
    System.setProperty(ACCESS_LOG_PROPERTY, "false");

    AccessRequestLog requestLog = new AccessRequestLog();

    // Repeated log calls while disabled are all skipped and must not throw.
    assertFalse(isAccessLogEnabled(requestLog));
    assertDoesNotThrow(
        () -> {
          requestLog.log(mockRequest, mockResponse);
          requestLog.log(mockRequest, mockResponse);
          requestLog.log(mockRequest, mockResponse);
        });
  }

  @Test
  public void testLogWithNullRequest() {
    System.setProperty(ACCESS_LOG_PROPERTY, "true");

    AccessRequestLog requestLog = new AccessRequestLog();

    // With logging enabled, a null request is forwarded to the parent RequestLogImpl,
    // which dereferences it while building the access event and throws NPE.
    assertThrows(NullPointerException.class, () -> requestLog.log(null, mockResponse));
  }

  @Test
  public void testLogWithNullResponse() {
    System.setProperty(ACCESS_LOG_PROPERTY, "true");

    AccessRequestLog requestLog = new AccessRequestLog();

    // With logging enabled, a null response is forwarded to the parent RequestLogImpl.
    // The (unstarted) parent builds the access event without dereferencing the response and
    // appends over zero appenders, so the call completes without throwing.
    assertDoesNotThrow(() -> requestLog.log(mockRequest, null));
  }

  @Test
  public void testLogWithBothNull() {
    System.setProperty(ACCESS_LOG_PROPERTY, "true");

    AccessRequestLog requestLog = new AccessRequestLog();

    // With logging enabled, null request/response are forwarded to the parent
    // RequestLogImpl, which dereferences them while building the access event.
    assertThrows(NullPointerException.class, () -> requestLog.log(null, null));
  }

  private static boolean isAccessLogEnabled(AccessRequestLog requestLog) {
    try {
      final Field field = AccessRequestLog.class.getDeclaredField("isAccessLogEnabled");
      field.setAccessible(true);
      return field.getBoolean(requestLog);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError("Unable to read isAccessLogEnabled field", e);
    }
  }
}
