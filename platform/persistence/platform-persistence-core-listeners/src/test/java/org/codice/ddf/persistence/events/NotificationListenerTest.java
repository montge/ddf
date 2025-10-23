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
package org.codice.ddf.persistence.events;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.codice.ddf.notifications.Notification;
import org.codice.ddf.persistence.PersistenceException;
import org.codice.ddf.persistence.PersistentItem;
import org.codice.ddf.persistence.PersistentStore;
import org.codice.ddf.persistence.PersistentStore.PersistenceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.service.event.Event;

/**
 * Comprehensive test harness for NotificationListener OSGi event handler.
 *
 * <p>Tests the persistence of notification events to the PersistentStore.
 *
 * <p>Coverage includes: - Notification event persistence - User ID validation (required field) -
 * All notification properties (id, userId, timestamp, application, title, message) - Error handling
 * during persistence - Null and empty property handling - IllegalArgumentException for missing
 * userId - Edge cases and boundary conditions
 */
@RunWith(MockitoJUnitRunner.class)
public class NotificationListenerTest {

  @Mock private PersistentStore mockPersistentStore;

  private NotificationListener notificationListener;

  @Before
  public void setUp() {
    notificationListener = new NotificationListener(mockPersistentStore);
  }

  /**
   * Tests successful notification event persistence with all properties.
   *
   * <p>Verifies that complete notification events are stored correctly.
   */
  @Test
  public void testHandleEventWithCompleteNotification() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat("Stored item should not be null", storedItem, notNullValue());
    assertThat(
        "ID should match",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_ID + "_txt"),
        is("notification123"));
    assertThat(
        "User ID should match",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_USER_ID + "_txt"),
        is("admin"));
    assertThat(
        "Timestamp should match",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_TIMESTAMP + "_txt"),
        is("2025-01-01T12:00:00Z"));
    assertThat(
        "Application should match",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_APPLICATION + "_txt"),
        is("catalog-app"));
    assertThat(
        "Title should match",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_TITLE + "_txt"),
        is("Download Complete"));
    assertThat(
        "Message should match",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_MESSAGE + "_txt"),
        is("Your download has completed successfully"));
  }

  /**
   * Tests notification event with missing user ID throws IllegalArgumentException.
   *
   * <p>Verifies that userId is a required field and its absence causes an exception.
   */
  @Test
  public void testHandleEventWithMissingUserIdThrowsException() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.remove(Notification.NOTIFICATION_KEY_USER_ID);
    Event event = new Event("ddf/notification", properties);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> notificationListener.handleEvent(event));

    assertThat(
        "Exception message should mention userId",
        exception.getMessage().contains(Notification.NOTIFICATION_KEY_USER_ID),
        is(true));
    assertThat(
        "Exception message should mention blank",
        exception.getMessage().contains("blank"),
        is(true));

    verify(mockPersistentStore, never())
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests notification event with null user ID throws IllegalArgumentException.
   *
   * <p>Verifies that null userId is treated as invalid.
   */
  @Test
  public void testHandleEventWithNullUserIdThrowsException() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_USER_ID, null);
    Event event = new Event("ddf/notification", properties);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> notificationListener.handleEvent(event));

    assertThat("Exception should be thrown for null userId", exception, notNullValue());

    verify(mockPersistentStore, never())
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests notification event with empty string user ID throws IllegalArgumentException.
   *
   * <p>Verifies that empty userId string is treated as invalid.
   */
  @Test
  public void testHandleEventWithEmptyUserIdThrowsException() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_USER_ID, "");
    Event event = new Event("ddf/notification", properties);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> notificationListener.handleEvent(event));

    assertThat("Exception should be thrown for empty userId", exception, notNullValue());

    verify(mockPersistentStore, never())
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests notification event with whitespace-only user ID throws IllegalArgumentException.
   *
   * <p>Verifies that whitespace-only userId is treated as invalid.
   */
  @Test
  public void testHandleEventWithWhitespaceUserIdThrowsException() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_USER_ID, "   ");
    Event event = new Event("ddf/notification", properties);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> notificationListener.handleEvent(event));

    assertThat("Exception should be thrown for whitespace userId", exception, notNullValue());

    verify(mockPersistentStore, never())
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests notification event with null application.
   *
   * <p>Verifies that null application is accepted (optional field).
   */
  @Test
  public void testHandleEventWithNullApplication() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_APPLICATION, null);
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());
  }

  /**
   * Tests notification event with null title.
   *
   * <p>Verifies that null title is accepted (optional field).
   */
  @Test
  public void testHandleEventWithNullTitle() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_TITLE, null);
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());
  }

  /**
   * Tests notification event with null message.
   *
   * <p>Verifies that null message is accepted (optional field).
   */
  @Test
  public void testHandleEventWithNullMessage() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_MESSAGE, null);
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());
  }

  /**
   * Tests notification event with null timestamp.
   *
   * <p>Verifies that null timestamp is accepted.
   */
  @Test
  public void testHandleEventWithNullTimestamp() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_TIMESTAMP, null);
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());
  }

  /**
   * Tests notification event with empty strings for optional fields.
   *
   * <p>Verifies that empty strings are stored correctly.
   */
  @Test
  public void testHandleEventWithEmptyStrings() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_APPLICATION, "");
    properties.put(Notification.NOTIFICATION_KEY_TITLE, "");
    properties.put(Notification.NOTIFICATION_KEY_MESSAGE, "");
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Empty application should be stored",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_APPLICATION + "_txt"),
        is(""));
    assertThat(
        "Empty title should be stored",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_TITLE + "_txt"),
        is(""));
    assertThat(
        "Empty message should be stored",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_MESSAGE + "_txt"),
        is(""));
  }

  /**
   * Tests persistence exception handling.
   *
   * <p>Verifies that PersistenceException is caught and suppressed (empty catch block in source).
   */
  @Test
  public void testHandleEventWithPersistenceException() throws PersistenceException {
    doThrow(new PersistenceException("Test persistence failure"))
        .when(mockPersistentStore)
        .add(any(String.class), any(PersistentItem.class));

    Map<String, Object> properties = createCompleteNotificationEvent();
    Event event = new Event("ddf/notification", properties);

    // Should not throw exception (exception is caught in empty catch block)
    notificationListener.handleEvent(event);

    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests notification with special characters in title and message.
   *
   * <p>Verifies that special characters are preserved.
   */
  @Test
  public void testHandleEventWithSpecialCharacters() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_TITLE, "Alert: <Important> & \"Critical\"");
    properties.put(
        Notification.NOTIFICATION_KEY_MESSAGE, "Status: 100% complete | Error #123 @ 12:00");
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Special characters in title should be preserved",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_TITLE + "_txt"),
        is("Alert: <Important> & \"Critical\""));
    assertThat(
        "Special characters in message should be preserved",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_MESSAGE + "_txt"),
        is("Status: 100% complete | Error #123 @ 12:00"));
  }

  /**
   * Tests notification with very long strings.
   *
   * <p>Verifies that long strings are handled correctly.
   */
  @Test
  public void testHandleEventWithVeryLongStrings() throws PersistenceException {
    String longString = new String(new char[10000]).replace('\0', 'x');
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_MESSAGE, longString);
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Long string should be stored",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_MESSAGE + "_txt"),
        is(longString));
  }

  /**
   * Tests notification with Unicode characters.
   *
   * <p>Verifies that international characters are handled correctly.
   */
  @Test
  public void testHandleEventWithUnicodeCharacters() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_TITLE, "通知 - Уведомление - إشعار");
    properties.put(Notification.NOTIFICATION_KEY_MESSAGE, "メッセージ - Сообщение - رسالة");
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Unicode characters should be preserved",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_TITLE + "_txt"),
        is("通知 - Уведомление - إشعار"));
  }

  /**
   * Tests multiple consecutive notification events.
   *
   * <p>Verifies that listener can handle multiple events in sequence.
   */
  @Test
  public void testHandleMultipleConsecutiveEvents() throws PersistenceException {
    Map<String, Object> properties1 = createCompleteNotificationEvent();
    properties1.put(Notification.NOTIFICATION_KEY_ID, "notification1");
    properties1.put(Notification.NOTIFICATION_KEY_USER_ID, "user1");
    Event event1 = new Event("ddf/notification", properties1);

    Map<String, Object> properties2 = createCompleteNotificationEvent();
    properties2.put(Notification.NOTIFICATION_KEY_ID, "notification2");
    properties2.put(Notification.NOTIFICATION_KEY_USER_ID, "user2");
    Event event2 = new Event("ddf/notification", properties2);

    Map<String, Object> properties3 = createCompleteNotificationEvent();
    properties3.put(Notification.NOTIFICATION_KEY_ID, "notification3");
    properties3.put(Notification.NOTIFICATION_KEY_USER_ID, "user3");
    Event event3 = new Event("ddf/notification", properties3);

    notificationListener.handleEvent(event1);
    notificationListener.handleEvent(event2);
    notificationListener.handleEvent(event3);

    verify(mockPersistentStore, times(3))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests notification with different application names.
   *
   * <p>Verifies that notifications from various applications are stored.
   */
  @Test
  public void testHandleEventWithDifferentApplications() throws PersistenceException {
    String[] applications = {
      "catalog-app", "search-app", "security-app", "admin-app", "spatial-app"
    };

    for (String app : applications) {
      Map<String, Object> properties = createCompleteNotificationEvent();
      properties.put(Notification.NOTIFICATION_KEY_APPLICATION, app);
      Event event = new Event("ddf/notification", properties);

      notificationListener.handleEvent(event);
    }

    verify(mockPersistentStore, times(5))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests notification with minimal required properties (only userId).
   *
   * <p>Verifies that notification with only userId can be stored.
   */
  @Test
  public void testHandleEventWithMinimalProperties() throws PersistenceException {
    Map<String, Object> properties = new HashMap<>();
    properties.put(Notification.NOTIFICATION_KEY_ID, "minimal123");
    properties.put(Notification.NOTIFICATION_KEY_USER_ID, "admin");
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Minimal notification should be stored",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_USER_ID + "_txt"),
        is("admin"));
  }

  /**
   * Tests notification with various user ID formats.
   *
   * <p>Verifies that different userId formats are accepted.
   */
  @Test
  public void testHandleEventWithVariousUserIdFormats() throws PersistenceException {
    String[] userIds = {"admin", "user@example.com", "domain\\user", "user123", "user-name_123"};

    for (String userId : userIds) {
      Map<String, Object> properties = createCompleteNotificationEvent();
      properties.put(Notification.NOTIFICATION_KEY_USER_ID, userId);
      Event event = new Event("ddf/notification", properties);

      notificationListener.handleEvent(event);
    }

    verify(mockPersistentStore, times(5))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests notification with newlines in message.
   *
   * <p>Verifies that multi-line messages are preserved.
   */
  @Test
  public void testHandleEventWithMultiLineMessage() throws PersistenceException {
    Map<String, Object> properties = createCompleteNotificationEvent();
    properties.put(Notification.NOTIFICATION_KEY_MESSAGE, "Line 1\nLine 2\nLine 3\nLine 4");
    Event event = new Event("ddf/notification", properties);

    notificationListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.NOTIFICATION_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Multi-line message should be preserved",
        storedItem.getTextProperty(Notification.NOTIFICATION_KEY_MESSAGE + "_txt"),
        is("Line 1\nLine 2\nLine 3\nLine 4"));
  }

  /**
   * Helper method to create a complete notification event with all properties.
   *
   * @return map of notification properties
   */
  private Map<String, Object> createCompleteNotificationEvent() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(Notification.NOTIFICATION_KEY_ID, "notification123");
    properties.put(Notification.NOTIFICATION_KEY_USER_ID, "admin");
    properties.put(Notification.NOTIFICATION_KEY_TIMESTAMP, "2025-01-01T12:00:00Z");
    properties.put(Notification.NOTIFICATION_KEY_APPLICATION, "catalog-app");
    properties.put(Notification.NOTIFICATION_KEY_TITLE, "Download Complete");
    properties.put(
        Notification.NOTIFICATION_KEY_MESSAGE, "Your download has completed successfully");
    return properties;
  }
}
