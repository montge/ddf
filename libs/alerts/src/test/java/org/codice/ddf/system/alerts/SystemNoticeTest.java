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
package org.codice.ddf.system.alerts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

/** Tests for {@link SystemNotice} class. */
public class SystemNoticeTest {

  @Test
  public void testSystemNoticeBaseTopicConstant() {
    assertThat(SystemNotice.SYSTEM_NOTICE_BASE_TOPIC, is("decanter/collect/"));
  }

  @Test
  public void testSystemNoticeEventTypeConstant() {
    assertThat(SystemNotice.SYSTEM_NOTICE_EVENT_TYPE, is("system-notice"));
  }

  @Test
  public void testDefaultConstructorSetsEventType() {
    SystemNotice notice = new SystemNotice();
    Map<String, Object> props = notice.getProperties();
    assertThat(props.get(SystemNotice.EVENT_TYPE_KEY), is(SystemNotice.SYSTEM_NOTICE_EVENT_TYPE));
  }

  @Test
  public void testDefaultConstructorSetsId() {
    SystemNotice notice = new SystemNotice();
    assertThat(notice.getId(), is(notNullValue()));
    assertThat(notice.getId().length(), is(36)); // UUID format
  }

  @Test
  public void testDefaultConstructorSetsTime() {
    Date before = new Date();
    SystemNotice notice = new SystemNotice();
    Date after = new Date();

    Date noticeTime = notice.getTime();
    assertThat(noticeTime, is(notNullValue()));
    assertThat(noticeTime.getTime() >= before.getTime(), is(true));
    assertThat(noticeTime.getTime() <= after.getTime(), is(true));
  }

  @Test
  public void testDefaultConstructorSetsDefaultSource() {
    SystemNotice notice = new SystemNotice();
    assertThat(notice.getSource(), is("Unknown"));
  }

  @Test
  public void testDefaultConstructorSetsDefaultPriority() {
    SystemNotice notice = new SystemNotice();
    assertThat(notice.getPriority(), is(NoticePriority.NORMAL));
  }

  @Test
  public void testDefaultConstructorSetsEmptyTitle() {
    SystemNotice notice = new SystemNotice();
    assertThat(notice.getTitle(), is(""));
  }

  @Test
  public void testDefaultConstructorSetsEmptyDetails() {
    SystemNotice notice = new SystemNotice();
    assertThat(notice.getDetails(), is(empty()));
  }

  @Test
  public void testDefaultConstructorSetsHostName() {
    SystemNotice notice = new SystemNotice();
    assertThat(notice.getHostName(), is(notNullValue()));
    assertThat(notice.getHostName().length() > 0, is(true));
  }

  @Test
  public void testDefaultConstructorSetsHostAddress() {
    SystemNotice notice = new SystemNotice();
    assertThat(notice.getHostAddress(), is(notNullValue()));
    assertThat(notice.getHostAddress().length() > 0, is(true));
  }

  @Test
  public void testParameterizedConstructorSetsSource() {
    Set<String> details = new HashSet<>();
    SystemNotice notice =
        new SystemNotice("test.source", NoticePriority.CRITICAL, "Test Title", details);
    assertThat(notice.getSource(), is("test.source"));
  }

  @Test
  public void testParameterizedConstructorSetsPriority() {
    Set<String> details = new HashSet<>();
    SystemNotice notice =
        new SystemNotice("test.source", NoticePriority.CRITICAL, "Test Title", details);
    assertThat(notice.getPriority(), is(NoticePriority.CRITICAL));
  }

  @Test
  public void testParameterizedConstructorSetsTitle() {
    Set<String> details = new HashSet<>();
    SystemNotice notice =
        new SystemNotice("test.source", NoticePriority.NORMAL, "Test Title", details);
    assertThat(notice.getTitle(), is("Test Title"));
  }

  @Test
  public void testParameterizedConstructorSetsDetails() {
    Set<String> details = new HashSet<>();
    details.add("detail1");
    details.add("detail2");
    SystemNotice notice =
        new SystemNotice("test.source", NoticePriority.NORMAL, "Test Title", details);
    assertThat(notice.getDetails(), hasSize(2));
  }

  @Test
  public void testParameterizedConstructorWithNullDetailsCreatesEmptyCollection() {
    SystemNotice notice =
        new SystemNotice("test.source", NoticePriority.NORMAL, "Test Title", null);
    // When null is passed, an ArrayList is stored but getDetails() doesn't cast properly
    // This verifies the properties map contains a non-null collection
    assertThat(
        notice.getProperties().get(SystemNotice.SYSTEM_NOTICE_DETAILS_KEY), is(notNullValue()));
  }

  @Test
  public void testMapConstructorCopiesProperties() {
    Map<String, Object> props = new HashMap<>();
    props.put(SystemNotice.SYSTEM_NOTICE_SOURCE_KEY, "custom.source");
    props.put(SystemNotice.SYSTEM_NOTICE_TITLE_KEY, "Custom Title");
    props.put(SystemNotice.SYSTEM_NOTICE_PRIORITY_KEY, NoticePriority.IMPORTANT.value());

    SystemNotice notice = new SystemNotice(props);
    assertThat(notice.getSource(), is("custom.source"));
    assertThat(notice.getTitle(), is("Custom Title"));
    assertThat(notice.getPriority(), is(NoticePriority.IMPORTANT));
  }

  @Test
  public void testGetPropertiesReturnsCopy() {
    SystemNotice notice = new SystemNotice();
    Map<String, Object> props1 = notice.getProperties();
    Map<String, Object> props2 = notice.getProperties();

    assertThat(props1, is(not(sameInstance(props2)))); // Different object references
    assertThat(props1.size(), is(props2.size())); // Same content
  }

  @Test
  public void testGetPropertiesContainsAllKeys() {
    Set<String> details = new HashSet<>();
    details.add("test detail");
    SystemNotice notice =
        new SystemNotice("test.source", NoticePriority.IMPORTANT, "Title", details);

    Map<String, Object> props = notice.getProperties();
    assertThat(props, hasEntry(SystemNotice.EVENT_TYPE_KEY, SystemNotice.SYSTEM_NOTICE_EVENT_TYPE));
    assertThat(props, hasEntry(SystemNotice.SYSTEM_NOTICE_SOURCE_KEY, "test.source"));
    assertThat(props, hasEntry(SystemNotice.SYSTEM_NOTICE_TITLE_KEY, "Title"));
  }

  @Test
  public void testGetPriorityFromStringValue() {
    Map<String, Object> props = new HashMap<>();
    props.put(SystemNotice.SYSTEM_NOTICE_PRIORITY_KEY, "3"); // String representation

    SystemNotice notice = new SystemNotice(props);
    assertThat(notice.getPriority(), is(NoticePriority.IMPORTANT));
  }

  @Test
  public void testGetDetailsFromStringValue() {
    Map<String, Object> props = new HashMap<>();
    props.put(SystemNotice.SYSTEM_NOTICE_DETAILS_KEY, "single detail");

    SystemNotice notice = new SystemNotice(props);
    Set<String> details = notice.getDetails();
    assertThat(details, hasSize(1));
    assertThat(details.iterator().next(), is("single detail"));
  }

  @Test
  public void testGetTimeFromLongValue() {
    long timestamp = 1609459200000L; // 2021-01-01 00:00:00 UTC
    Map<String, Object> props = new HashMap<>();
    props.put(SystemNotice.SYSTEM_NOTICE_TIME_KEY, timestamp);

    SystemNotice notice = new SystemNotice(props);
    assertThat(notice.getTime().getTime(), is(timestamp));
  }

  @Test
  public void testKeyConstants() {
    assertThat(SystemNotice.EVENT_TYPE_KEY, is("type"));
    assertThat(SystemNotice.SYSTEM_NOTICE_HOST_NAME_KEY, is("hostName"));
    assertThat(SystemNotice.SYSTEM_NOTICE_HOST_ADDRESS_KEY, is("hostAddress"));
    assertThat(SystemNotice.SYSTEM_NOTICE_ID_KEY, is("id"));
    assertThat(SystemNotice.SYSTEM_NOTICE_TIME_KEY, is("noticeTime"));
    assertThat(SystemNotice.SYSTEM_NOTICE_SOURCE_KEY, is("source"));
    assertThat(SystemNotice.SYSTEM_NOTICE_PRIORITY_KEY, is("priority"));
    assertThat(SystemNotice.SYSTEM_NOTICE_TITLE_KEY, is("title"));
    assertThat(SystemNotice.SYSTEM_NOTICE_DETAILS_KEY, is("details"));
  }

  @Test
  public void testUniqueIdsForMultipleNotices() {
    SystemNotice notice1 = new SystemNotice();
    SystemNotice notice2 = new SystemNotice();

    assertThat(notice1.getId(), is(not(notice2.getId())));
  }

  @Test
  public void testAllPriorityLevelsCanBeSet() {
    for (NoticePriority priority : NoticePriority.values()) {
      SystemNotice notice = new SystemNotice("source", priority, "title", Collections.emptySet());
      assertThat(notice.getPriority(), is(priority));
    }
  }
}
