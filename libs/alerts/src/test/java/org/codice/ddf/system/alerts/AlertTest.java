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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests for {@link Alert} class. */
public class AlertTest {

  @Test
  public void testAlertDismissTopicConstant() {
    assertThat(Alert.ALERT_DISMISS_TOPIC, is("ddf/alert/dismiss"));
  }

  @Test
  public void testAlertEventTypeConstant() {
    assertThat(Alert.ALERT_EVENT_TYPE, is("system-alert"));
  }

  @Test
  public void testAlertStatusConstants() {
    assertThat(Alert.ALERT_DISMISSED_STATUS, is("dismissed"));
    assertThat(Alert.ALERT_ACTIVE_STATUS, is("active"));
  }

  @Test
  public void testAlertKeyConstants() {
    assertThat(Alert.ALERT_LAST_UPDATED, is("last-updated"));
    assertThat(Alert.ALERT_COUNT, is("count"));
    assertThat(Alert.ALERT_STATUS, is("status"));
    assertThat(Alert.ALERT_DISMISSED_TIME, is("dismissed-time"));
    assertThat(Alert.ALERT_DISMISSED_BY, is("dismissed-by"));
  }

  @Test
  public void testDefaultConstructorSetsAlertEventType() {
    Alert alert = new Alert();
    Map<String, Object> props = alert.getProperties();
    assertThat(props.get(SystemNotice.EVENT_TYPE_KEY), is(Alert.ALERT_EVENT_TYPE));
  }

  @Test
  public void testDefaultConstructorSetsCountToOne() {
    Alert alert = new Alert();
    assertThat(alert.getCount(), is(1L));
  }

  @Test
  public void testDefaultConstructorSetsActiveStatus() {
    Alert alert = new Alert();
    assertThat(alert.getStatus(), is(Alert.ALERT_ACTIVE_STATUS));
  }

  @Test
  public void testDefaultConstructorSetsLastUpdated() {
    Date before = new Date();
    Alert alert = new Alert();
    Date after = new Date();

    Date lastUpdated = alert.getLastUpdated();
    assertThat(lastUpdated, is(notNullValue()));
    assertThat(lastUpdated.getTime(), greaterThanOrEqualTo(before.getTime()));
    assertThat(lastUpdated.getTime(), lessThanOrEqualTo(after.getTime()));
  }

  @Test
  public void testDefaultConstructorInheritsSytemNoticeProperties() {
    Alert alert = new Alert();
    assertThat(alert.getId(), is(notNullValue()));
    assertThat(alert.getHostName(), is(notNullValue()));
    assertThat(alert.getHostAddress(), is(notNullValue()));
    assertThat(alert.getTime(), is(notNullValue()));
  }

  @Test
  public void testParameterizedConstructorSetsSource() {
    Set<String> details = new HashSet<>();
    Alert alert = new Alert("test.source", NoticePriority.CRITICAL, "Test Alert", details);
    assertThat(alert.getSource(), is("test.source"));
  }

  @Test
  public void testParameterizedConstructorSetsPriority() {
    Set<String> details = new HashSet<>();
    Alert alert = new Alert("test.source", NoticePriority.CRITICAL, "Test Alert", details);
    assertThat(alert.getPriority(), is(NoticePriority.CRITICAL));
  }

  @Test
  public void testParameterizedConstructorSetsTitle() {
    Set<String> details = new HashSet<>();
    Alert alert = new Alert("test.source", NoticePriority.NORMAL, "Test Alert", details);
    assertThat(alert.getTitle(), is("Test Alert"));
  }

  @Test
  public void testParameterizedConstructorSetsAlertSpecificDefaults() {
    Set<String> details = new HashSet<>();
    Alert alert = new Alert("test.source", NoticePriority.NORMAL, "Test Alert", details);

    assertThat(alert.getCount(), is(1L));
    assertThat(alert.getStatus(), is(Alert.ALERT_ACTIVE_STATUS));
    assertThat(alert.getLastUpdated(), is(notNullValue()));
  }

  @Test
  public void testMapConstructorCopiesProperties() {
    Map<String, Object> props = new HashMap<>();
    props.put(SystemNotice.SYSTEM_NOTICE_SOURCE_KEY, "custom.source");
    props.put(Alert.ALERT_COUNT, 5L);
    props.put(Alert.ALERT_STATUS, Alert.ALERT_DISMISSED_STATUS);

    Alert alert = new Alert(props);
    assertThat(alert.getSource(), is("custom.source"));
    assertThat(alert.getCount(), is(5L));
    assertThat(alert.getStatus(), is(Alert.ALERT_DISMISSED_STATUS));
  }

  @Test
  public void testGetDismissedTimeWhenNotDismissed() {
    Alert alert = new Alert();
    assertThat(alert.getDismissedTime(), is(nullValue()));
  }

  @Test
  public void testGetDismissedByWhenNotDismissed() {
    Alert alert = new Alert();
    assertThat(alert.getDismissedBy(), is(nullValue()));
  }

  @Test
  public void testGetDismissedAlertSetsStatus() {
    Alert alert = new Alert("source", NoticePriority.NORMAL, "Title", new HashSet<>());

    Alert dismissed = alert.getDismissedAlert("admin");

    assertThat(dismissed.getStatus(), is(Alert.ALERT_DISMISSED_STATUS));
  }

  @Test
  public void testGetDismissedAlertSetsDismissedBy() {
    Alert alert = new Alert("source", NoticePriority.NORMAL, "Title", new HashSet<>());

    Alert dismissed = alert.getDismissedAlert("admin");

    assertThat(dismissed.getDismissedBy(), is("admin"));
  }

  @Test
  public void testGetDismissedAlertSetsDismissedTime() {
    Alert alert = new Alert("source", NoticePriority.NORMAL, "Title", new HashSet<>());
    Date before = new Date();

    Alert dismissed = alert.getDismissedAlert("admin");

    Date after = new Date();
    assertThat(dismissed.getDismissedTime(), is(notNullValue()));
    assertThat(dismissed.getDismissedTime().getTime(), greaterThanOrEqualTo(before.getTime()));
    assertThat(dismissed.getDismissedTime().getTime(), lessThanOrEqualTo(after.getTime()));
  }

  @Test
  public void testGetDismissedAlertPreservesOriginalProperties() {
    Set<String> details = new HashSet<>();
    details.add("detail1");
    Alert alert = new Alert("test.source", NoticePriority.CRITICAL, "Test Title", details);

    Alert dismissed = alert.getDismissedAlert("admin");

    assertThat(dismissed.getSource(), is("test.source"));
    assertThat(dismissed.getPriority(), is(NoticePriority.CRITICAL));
    assertThat(dismissed.getTitle(), is("Test Title"));
  }

  @Test
  public void testOriginalAlertUnchangedAfterDismiss() {
    Alert alert = new Alert("source", NoticePriority.NORMAL, "Title", new HashSet<>());
    String originalStatus = alert.getStatus();

    alert.getDismissedAlert("admin");

    assertThat(alert.getStatus(), is(originalStatus));
    assertThat(alert.getDismissedBy(), is(nullValue()));
    assertThat(alert.getDismissedTime(), is(nullValue()));
  }

  @Test
  public void testAlertExtendsSystemNotice() {
    Alert alert = new Alert();
    assertThat(alert instanceof SystemNotice, is(true));
  }

  @Test
  public void testAlertOverridesEventType() {
    Alert alert = new Alert();
    SystemNotice notice = new SystemNotice();

    assertThat(alert.getProperties().get(SystemNotice.EVENT_TYPE_KEY), is(Alert.ALERT_EVENT_TYPE));
    assertThat(
        notice.getProperties().get(SystemNotice.EVENT_TYPE_KEY),
        is(SystemNotice.SYSTEM_NOTICE_EVENT_TYPE));
  }
}
