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
package ddf.catalog.pubsub.predicate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Core;
import ddf.catalog.pubsub.EventProcessorImpl.DateType;
import ddf.catalog.pubsub.internal.PubSubConstants;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.event.Event;

public class TemporalPredicateTest {

  private Date testStartDate;
  private Date testEndDate;
  private Date dateInRange;
  private Date dateBeforeRange;
  private Date dateAfterRange;

  @BeforeEach
  public void setUp() {
    Calendar cal = Calendar.getInstance();
    cal.set(2023, Calendar.JANUARY, 1, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    testStartDate = cal.getTime();

    cal.set(2023, Calendar.DECEMBER, 31, 23, 59, 59);
    cal.set(Calendar.MILLISECOND, 999);
    testEndDate = cal.getTime();

    cal.set(2023, Calendar.JUNE, 15, 12, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    dateInRange = cal.getTime();

    cal.set(2022, Calendar.DECEMBER, 31, 23, 59, 59);
    cal.set(Calendar.MILLISECOND, 999);
    dateBeforeRange = cal.getTime();

    cal.set(2024, Calendar.JANUARY, 1, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    dateAfterRange = cal.getTime();
  }

  @Test
  public void testTemporalPredicateConstructorWithDates() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.EFFECTIVE);

    assertThat(predicate.getStart(), is(notNullValue()));
    assertThat(predicate.getEnd(), is(notNullValue()));
    assertThat(predicate.getType(), is(equalTo(DateType.EFFECTIVE)));
  }

  @Test
  public void testTemporalPredicateConstructorWithOffset() {
    long offset = 86400000L; // 1 day in milliseconds
    TemporalPredicate predicate = new TemporalPredicate(offset, DateType.MODIFIED);

    assertThat(predicate.getType(), is(equalTo(DateType.MODIFIED)));
  }

  @Test
  public void testTemporalPredicateConstructorWithNullDates() {
    TemporalPredicate predicate = new TemporalPredicate(null, null, DateType.CREATED);

    assertThat(predicate.getStart(), is(nullValue()));
    assertThat(predicate.getEnd(), is(nullValue()));
    assertThat(predicate.getType(), is(equalTo(DateType.CREATED)));
  }

  @Test
  public void testMatchesEffectiveDateInRange() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.EFFECTIVE);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setEffectiveDate(dateInRange);

    Event event = createEvent(metacard);
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesEffectiveDateBeforeRange() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.EFFECTIVE);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setEffectiveDate(dateBeforeRange);

    Event event = createEvent(metacard);
    assertFalse(predicate.matches(event));
  }

  @Test
  public void testMatchesEffectiveDateAfterRange() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.EFFECTIVE);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setEffectiveDate(dateAfterRange);

    Event event = createEvent(metacard);
    assertFalse(predicate.matches(event));
  }

  @Test
  public void testMatchesModifiedDateInRange() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.MODIFIED);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setModifiedDate(dateInRange);

    Event event = createEvent(metacard);
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesCreatedDateInRange() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.CREATED);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setCreatedDate(dateInRange);

    Event event = createEvent(metacard);
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesExpirationDateInRange() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.EXPIRATION);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setExpirationDate(dateInRange);

    Event event = createEvent(metacard);
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesMetacardCreatedDateInRange() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.METACARD_CREATED);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setAttribute(Core.METACARD_CREATED, dateInRange);

    Event event = createEvent(metacard);
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesMetacardModifiedDateInRange() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.METACARD_MODIFIED);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setAttribute(Core.METACARD_MODIFIED, dateInRange);

    Event event = createEvent(metacard);
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesWithDeleteOperation() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.EFFECTIVE);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setEffectiveDate(dateInRange);

    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.DELETE);
    properties.put(PubSubConstants.HEADER_ENTRY_KEY, metacard);

    Map<String, Object> contextualMap = new HashMap<>();
    contextualMap.put("METADATA", PubSubConstants.METADATA_DELETED);
    properties.put(PubSubConstants.HEADER_CONTEXTUAL_KEY, contextualMap);

    Event event = new Event("topic", properties);
    // Should return true when DELETE operation with METADATA_DELETED
    assertTrue(predicate.matches(event));
  }

  @Test
  public void testMatchesWithOffsetPredicate() throws InterruptedException {
    long offset = 1000L; // 1 second in milliseconds
    TemporalPredicate predicate = new TemporalPredicate(offset, DateType.EFFECTIVE);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setEffectiveDate(new Date()); // Current time

    Event event = createEvent(metacard);
    assertTrue(predicate.matches(event));

    // Test with date outside offset
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -2); // 2 hours ago
    metacard.setEffectiveDate(cal.getTime());

    event = createEvent(metacard);
    assertFalse(predicate.matches(event));
  }

  @Test
  public void testIsTemporalWithValidDates() {
    assertTrue(TemporalPredicate.isTemporal("2023-01-01", "2023-12-31"));
  }

  @Test
  public void testIsTemporalWithEmptyDates() {
    assertFalse(TemporalPredicate.isTemporal("", "2023-12-31"));
    assertFalse(TemporalPredicate.isTemporal("2023-01-01", ""));
    assertFalse(TemporalPredicate.isTemporal("", ""));
  }

  @Test
  public void testGettersReturnDefensiveCopies() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.EFFECTIVE);

    Date retrievedStart = predicate.getStart();
    Date retrievedEnd = predicate.getEnd();

    // Modify the retrieved dates
    retrievedStart.setTime(0);
    retrievedEnd.setTime(0);

    // Original values in predicate should be unchanged
    assertThat(predicate.getStart().getTime(), is(equalTo(testStartDate.getTime())));
    assertThat(predicate.getEnd().getTime(), is(equalTo(testEndDate.getTime())));
  }

  @Test
  public void testToStringIncludesRelevantInformation() {
    TemporalPredicate predicate =
        new TemporalPredicate(testStartDate, testEndDate, DateType.EFFECTIVE);

    String result = predicate.toString();

    assertThat(result, is(notNullValue()));
    assertTrue(result.contains("start"));
    assertTrue(result.contains("end"));
    assertTrue(result.contains("offset"));
    assertTrue(result.contains("type"));
  }

  @Test
  public void testToStringWithOffset() {
    long offset = 86400000L;
    TemporalPredicate predicate = new TemporalPredicate(offset, DateType.MODIFIED);

    String result = predicate.toString();

    assertThat(result, is(notNullValue()));
    assertTrue(result.contains("offset"));
    assertTrue(result.contains(String.valueOf(offset)));
  }

  private Event createEvent(MetacardImpl metacard) {
    HashMap<String, Object> properties = new HashMap<>();
    properties.put(PubSubConstants.HEADER_OPERATION_KEY, PubSubConstants.CREATE);
    properties.put(PubSubConstants.HEADER_ENTRY_KEY, metacard);

    Map<String, Object> contextualMap = new HashMap<>();
    contextualMap.put("METADATA", "<xml></xml>");
    properties.put(PubSubConstants.HEADER_CONTEXTUAL_KEY, contextualMap);

    return new Event("topic", properties);
  }
}
