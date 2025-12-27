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
package ddf.catalog.pubsub.criteria.temporal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemporalEvaluationCriteriaImplTest {

  private Date startDate;
  private Date endDate;
  private Date inputDate;

  @BeforeEach
  void setUp() {
    long now = System.currentTimeMillis();
    startDate = new Date(now - 10000);
    endDate = new Date(now + 10000);
    inputDate = new Date(now);
  }

  @Test
  void testConstructor() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    assertThat(criteria, is(notNullValue()));
  }

  @Test
  void testImplementsInterface() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    assertThat(criteria, instanceOf(TemporalEvaluationCriteria.class));
  }

  @Test
  void testGetStart() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    assertThat(criteria.getStart(), is(notNullValue()));
    assertThat(criteria.getStart().getTime(), is(startDate.getTime()));
  }

  @Test
  void testGetEnd() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    assertThat(criteria.getEnd(), is(notNullValue()));
    assertThat(criteria.getEnd().getTime(), is(endDate.getTime()));
  }

  @Test
  void testGetInput() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    assertThat(criteria.getInput(), is(notNullValue()));
    assertThat(criteria.getInput().getTime(), is(inputDate.getTime()));
  }

  @Test
  void testNullStartThrowsException() {
    assertThrows(
        NullPointerException.class,
        () -> new TemporalEvaluationCriteriaImpl(endDate, null, inputDate));
  }

  @Test
  void testNullInputThrowsException() {
    assertThrows(
        NullPointerException.class,
        () -> new TemporalEvaluationCriteriaImpl(endDate, startDate, null));
  }

  @Test
  void testNullEndIsAllowed() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(null, startDate, inputDate);

    assertThat(criteria.getEnd(), is(nullValue()));
    assertThat(criteria.getStart(), is(notNullValue()));
    assertThat(criteria.getInput(), is(notNullValue()));
  }

  @Test
  void testDefensiveCopyOnConstruction() {
    Date originalStart = new Date(startDate.getTime());
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    startDate.setTime(0);

    assertThat(criteria.getStart().getTime(), is(originalStart.getTime()));
  }

  @Test
  void testDefensiveCopyOnGetStart() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    Date returnedStart = criteria.getStart();
    assertThat(returnedStart, is(not(sameInstance(criteria.getStart()))));
  }

  @Test
  void testDefensiveCopyOnGetEnd() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    Date returnedEnd = criteria.getEnd();
    assertThat(returnedEnd, is(not(sameInstance(criteria.getEnd()))));
  }

  @Test
  void testDefensiveCopyOnGetInput() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    Date returnedInput = criteria.getInput();
    assertThat(returnedInput, is(not(sameInstance(criteria.getInput()))));
  }

  @Test
  void testStartBeforeEnd() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    assertThat(criteria.getStart().before(criteria.getEnd()), is(true));
  }

  @Test
  void testInputBetweenStartAndEnd() {
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);

    assertThat(criteria.getInput().after(criteria.getStart()), is(true));
    assertThat(criteria.getInput().before(criteria.getEnd()), is(true));
  }

  @Test
  void testWithSameDates() {
    Date sameDate = new Date();
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(sameDate, sameDate, sameDate);

    assertThat(criteria.getStart().getTime(), is(criteria.getEnd().getTime()));
    assertThat(criteria.getStart().getTime(), is(criteria.getInput().getTime()));
  }

  @Test
  void testWithVeryOldDate() {
    Date veryOldDate = new Date(0);
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(endDate, veryOldDate, inputDate);

    assertThat(criteria.getStart().getTime(), is(0L));
  }

  @Test
  void testWithFutureDate() {
    Date futureDate = new Date(Long.MAX_VALUE - 1000);
    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(futureDate, startDate, inputDate);

    assertThat(criteria.getEnd().getTime(), is(futureDate.getTime()));
  }

  @Test
  void testMultipleCriteriaAreIndependent() {
    TemporalEvaluationCriteriaImpl criteria1 =
        new TemporalEvaluationCriteriaImpl(endDate, startDate, inputDate);
    TemporalEvaluationCriteriaImpl criteria2 =
        new TemporalEvaluationCriteriaImpl(startDate, endDate, inputDate);

    assertThat(criteria1.getStart().getTime(), is(not(criteria2.getStart().getTime())));
    assertThat(criteria1.getEnd().getTime(), is(not(criteria2.getEnd().getTime())));
  }
}
