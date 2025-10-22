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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.codice.ddf.activities.ActivityEvent;
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
 * Comprehensive test harness for ActivityListener OSGi event handler.
 *
 * <p>Tests the persistence of activity events (downloads, operations, progress tracking) to the
 * PersistentStore.
 *
 * <p>Coverage includes:
 * - Activity event persistence
 * - RUNNING status filtering (should not persist)
 * - All activity properties (id, session, status, title, message, timestamp, operations, progress,
 *   user, category, bytes, downloadId)
 * - Error handling during persistence
 * - Null and missing property handling
 * - Edge cases and boundary conditions
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityListenerTest {

  @Mock private PersistentStore mockPersistentStore;

  private ActivityListener activityListener;

  @Before
  public void setUp() {
    activityListener = new ActivityListener(mockPersistentStore);
  }

  /**
   * Tests successful activity event persistence with all properties.
   *
   * <p>Verifies that completed activity events are stored correctly.
   */
  @Test
  public void testHandleEventWithCompleteActivity() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat("Stored item should not be null", storedItem, notNullValue());
    assertThat(
        "ID should match",
        storedItem.getTextProperty(ActivityEvent.ID_KEY + "_txt"),
        is("activity123"));
    assertThat(
        "Session ID should match",
        storedItem.getTextProperty(ActivityEvent.SESSION_ID_KEY + "_txt"),
        is("session456"));
    assertThat(
        "Status should match",
        storedItem.getTextProperty(ActivityEvent.STATUS_KEY + "_txt"),
        is("COMPLETED"));
    assertThat(
        "Title should match",
        storedItem.getTextProperty(ActivityEvent.TITLE_KEY + "_txt"),
        is("Test Download"));
    assertThat(
        "Message should match",
        storedItem.getTextProperty(ActivityEvent.MESSAGE_KEY + "_txt"),
        is("Download completed successfully"));
    assertThat(
        "Timestamp should match",
        storedItem.getTextProperty(ActivityEvent.TIMESTAMP_KEY + "_txt"),
        is("2025-01-01T12:00:00Z"));
    assertThat(
        "Progress should match",
        storedItem.getTextProperty(ActivityEvent.PROGRESS_KEY + "_txt"),
        is("100"));
    assertThat(
        "User should match",
        storedItem.getTextProperty(ActivityEvent.USER_ID_KEY + "_txt"),
        is("admin"));
    assertThat(
        "Category should match",
        storedItem.getTextProperty(ActivityEvent.CATEGORY_KEY + "_txt"),
        is("Product Retrieval"));
    assertThat(
        "Download ID should match",
        storedItem.getTextProperty(ActivityEvent.DOWNLOAD_ID_KEY + "_txt"),
        is("download789"));
  }

  /**
   * Tests that RUNNING status activities are not persisted.
   *
   * <p>Verifies that in-progress activities are filtered out to avoid cluttering storage.
   */
  @Test
  public void testHandleEventWithRunningStatusDoesNotPersist() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("RUNNING");
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    verify(mockPersistentStore, never())
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests activity event with FAILED status.
   *
   * <p>Verifies that failed activities are persisted (not just completed ones).
   */
  @Test
  public void testHandleEventWithFailedStatus() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("FAILED");
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Failed status should be stored",
        storedItem.getTextProperty(ActivityEvent.STATUS_KEY + "_txt"),
        is("FAILED"));
  }

  /**
   * Tests activity event with CANCELLED status.
   *
   * <p>Verifies that cancelled activities are persisted.
   */
  @Test
  public void testHandleEventWithCancelledStatus() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("CANCELLED");
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Cancelled status should be stored",
        storedItem.getTextProperty(ActivityEvent.STATUS_KEY + "_txt"),
        is("CANCELLED"));
  }

  /**
   * Tests activity event with operations map.
   *
   * <p>Verifies that operation key-value pairs are stored with proper prefixes.
   */
  @Test
  public void testHandleEventWithOperations() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    // Operations should be stored as "operations_key" format
    assertThat(
        "Operation cancel should be stored",
        storedItem.getTextProperty(ActivityEvent.OPERATIONS_KEY + "_cancel" + "_txt"),
        is("/cancel/download"));
    assertThat(
        "Operation retry should be stored",
        storedItem.getTextProperty(ActivityEvent.OPERATIONS_KEY + "_retry" + "_txt"),
        is("/retry/download"));
  }

  /**
   * Tests activity event with empty operations map.
   *
   * <p>Verifies that empty operations don't cause errors.
   */
  @Test
  public void testHandleEventWithEmptyOperations() throws PersistenceException {
    Map<String, Object> properties = createActivityEventWithEmptyOperations();
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests activity event with zero progress.
   *
   * <p>Verifies that zero progress value is handled correctly.
   */
  @Test
  public void testHandleEventWithZeroProgress() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.PROGRESS_KEY, 0);
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Zero progress should be stored",
        storedItem.getTextProperty(ActivityEvent.PROGRESS_KEY + "_txt"),
        is("0"));
  }

  /**
   * Tests activity event with maximum progress.
   *
   * <p>Verifies that 100% progress value is handled correctly.
   */
  @Test
  public void testHandleEventWithMaximumProgress() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.PROGRESS_KEY, 100);
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Maximum progress should be stored",
        storedItem.getTextProperty(ActivityEvent.PROGRESS_KEY + "_txt"),
        is("100"));
  }

  /**
   * Tests activity event with zero bytes.
   *
   * <p>Verifies that zero byte count is handled correctly.
   */
  @Test
  public void testHandleEventWithZeroBytes() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.BYTES_READ_KEY, 0L);
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat("Zero bytes should be stored", storedItem.getLongProperty(ActivityEvent.BYTES_READ_KEY + "_lng"), is(0L));
  }

  /**
   * Tests activity event with large byte count.
   *
   * <p>Verifies that large file downloads are handled correctly.
   */
  @Test
  public void testHandleEventWithLargeByteCount() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.BYTES_READ_KEY, 10_000_000_000L); // 10GB
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Large byte count should be stored",
        storedItem.getLongProperty(ActivityEvent.BYTES_READ_KEY + "_lng"),
        is(10_000_000_000L));
  }

  /**
   * Tests persistence exception handling.
   *
   * <p>Verifies that PersistenceException is caught and logged (does not propagate).
   */
  @Test
  public void testHandleEventWithPersistenceException() throws PersistenceException {
    when(mockPersistentStore.add(any(String.class), any(PersistentItem.class)))
        .thenThrow(new PersistenceException("Test persistence failure"));

    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    Event event = new Event("ddf/download/activity", properties);

    // Should not throw exception (should be caught and logged)
    activityListener.handleEvent(event);

    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests activity event with null user.
   *
   * <p>Verifies that null user is handled (may represent system activity).
   */
  @Test
  public void testHandleEventWithNullUser() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.USER_ID_KEY, null);
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());
  }

  /**
   * Tests activity event with empty strings.
   *
   * <p>Verifies that empty string properties are handled correctly.
   */
  @Test
  public void testHandleEventWithEmptyStrings() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.TITLE_KEY, "");
    properties.put(ActivityEvent.MESSAGE_KEY, "");
    properties.put(ActivityEvent.CATEGORY_KEY, "");
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Empty title should be stored",
        storedItem.getTextProperty(ActivityEvent.TITLE_KEY + "_txt"),
        is(""));
  }

  /**
   * Tests activity event with special characters in strings.
   *
   * <p>Verifies that special characters are preserved.
   */
  @Test
  public void testHandleEventWithSpecialCharacters() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.TITLE_KEY, "Download: file<name>.xml & data");
    properties.put(ActivityEvent.MESSAGE_KEY, "Progress: 100% \"complete\"");
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Special characters should be preserved in title",
        storedItem.getTextProperty(ActivityEvent.TITLE_KEY + "_txt"),
        is("Download: file<name>.xml & data"));
  }

  /**
   * Tests activity event with multiple operations.
   *
   * <p>Verifies that multiple operation entries are all stored.
   */
  @Test
  public void testHandleEventWithMultipleOperations() throws PersistenceException {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    Map<String, String> operations = new HashMap<>();
    operations.put("cancel", "/cancel");
    operations.put("retry", "/retry");
    operations.put("details", "/details");
    operations.put("download", "/download");
    properties.put(ActivityEvent.OPERATIONS_KEY, operations);
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "All operations should be stored",
        storedItem.getTextProperty(ActivityEvent.OPERATIONS_KEY + "_cancel" + "_txt"),
        is("/cancel"));
    assertThat(
        "All operations should be stored",
        storedItem.getTextProperty(ActivityEvent.OPERATIONS_KEY + "_retry" + "_txt"),
        is("/retry"));
    assertThat(
        "All operations should be stored",
        storedItem.getTextProperty(ActivityEvent.OPERATIONS_KEY + "_details" + "_txt"),
        is("/details"));
    assertThat(
        "All operations should be stored",
        storedItem.getTextProperty(ActivityEvent.OPERATIONS_KEY + "_download" + "_txt"),
        is("/download"));
  }

  /**
   * Tests multiple consecutive activity events.
   *
   * <p>Verifies that listener can handle multiple events in sequence.
   */
  @Test
  public void testHandleMultipleConsecutiveEvents() throws PersistenceException {
    Map<String, Object> properties1 = createCompleteActivityEvent("COMPLETED");
    properties1.put(ActivityEvent.ID_KEY, "activity1");
    Event event1 = new Event("ddf/download/activity", properties1);

    Map<String, Object> properties2 = createCompleteActivityEvent("FAILED");
    properties2.put(ActivityEvent.ID_KEY, "activity2");
    Event event2 = new Event("ddf/download/activity", properties2);

    Map<String, Object> properties3 = createCompleteActivityEvent("CANCELLED");
    properties3.put(ActivityEvent.ID_KEY, "activity3");
    Event event3 = new Event("ddf/download/activity", properties3);

    activityListener.handleEvent(event1);
    activityListener.handleEvent(event2);
    activityListener.handleEvent(event3);

    verify(mockPersistentStore, times(3))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), any(PersistentItem.class));
  }

  /**
   * Tests activity event with very long strings.
   *
   * <p>Verifies that long property values are handled correctly.
   */
  @Test
  public void testHandleEventWithVeryLongStrings() throws PersistenceException {
    String longString = new String(new char[10000]).replace('\0', 'x');
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.MESSAGE_KEY, longString);
    Event event = new Event("ddf/download/activity", properties);

    activityListener.handleEvent(event);

    ArgumentCaptor<PersistentItem> captor = ArgumentCaptor.forClass(PersistentItem.class);
    verify(mockPersistentStore, times(1))
        .add(eq(PersistenceType.ACTIVITY_TYPE.toString()), captor.capture());

    PersistentItem storedItem = captor.getValue();
    assertThat(
        "Long string should be stored",
        storedItem.getTextProperty(ActivityEvent.MESSAGE_KEY + "_txt"),
        is(longString));
  }

  /**
   * Helper method to create a complete activity event with all properties.
   *
   * @param status the activity status
   * @return map of activity properties
   */
  private Map<String, Object> createCompleteActivityEvent(String status) {
    Map<String, Object> properties = new HashMap<>();
    properties.put(ActivityEvent.ID_KEY, "activity123");
    properties.put(ActivityEvent.SESSION_ID_KEY, "session456");
    properties.put(ActivityEvent.STATUS_KEY, status);
    properties.put(ActivityEvent.TITLE_KEY, "Test Download");
    properties.put(ActivityEvent.MESSAGE_KEY, "Download completed successfully");
    properties.put(ActivityEvent.TIMESTAMP_KEY, "2025-01-01T12:00:00Z");

    Map<String, String> operations = new HashMap<>();
    operations.put("cancel", "/cancel/download");
    operations.put("retry", "/retry/download");
    properties.put(ActivityEvent.OPERATIONS_KEY, operations);

    properties.put(ActivityEvent.PROGRESS_KEY, 100);
    properties.put(ActivityEvent.USER_ID_KEY, "admin");
    properties.put(ActivityEvent.CATEGORY_KEY, "Product Retrieval");
    properties.put(ActivityEvent.BYTES_READ_KEY, 1024000L);
    properties.put(ActivityEvent.DOWNLOAD_ID_KEY, "download789");

    return properties;
  }

  /**
   * Helper method to create activity event with empty operations.
   *
   * @return map of activity properties
   */
  private Map<String, Object> createActivityEventWithEmptyOperations() {
    Map<String, Object> properties = createCompleteActivityEvent("COMPLETED");
    properties.put(ActivityEvent.OPERATIONS_KEY, new HashMap<String, String>());
    return properties;
  }
}
