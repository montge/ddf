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
package org.codice.ddf.admin.configurator.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.List;
import java.util.UUID;
import org.codice.ddf.admin.configurator.ConfiguratorException;
import org.codice.ddf.admin.configurator.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OperationReportImplTest {

  private OperationReportImpl report;

  @BeforeEach
  void setUp() {
    report = new OperationReportImpl();
  }

  @Test
  void testEmptyReportHasTransactionSucceeded() {
    assertThat(report.hasTransactionSucceeded(), is(true));
  }

  @Test
  void testHasTransactionSucceededWithOnlyPassedResults() {
    UUID key1 = UUID.randomUUID();
    UUID key2 = UUID.randomUUID();

    report.putResult(key1, ResultImpl.pass());
    report.putResult(key2, ResultImpl.passWithData("data"));

    assertThat(report.hasTransactionSucceeded(), is(true));
  }

  @Test
  void testHasTransactionSucceededReturnsFalseWithFailedResult() {
    UUID key1 = UUID.randomUUID();
    UUID key2 = UUID.randomUUID();

    report.putResult(key1, ResultImpl.pass());
    report.putResult(key2, ResultImpl.fail(new ConfiguratorException("error")));

    assertThat(report.hasTransactionSucceeded(), is(false));
  }

  @Test
  void testHasTransactionSucceededReturnsFalseWithRollbackResult() {
    UUID key = UUID.randomUUID();

    report.putResult(key, ResultImpl.rollback());

    assertThat(report.hasTransactionSucceeded(), is(false));
  }

  @Test
  void testHasTransactionSucceededReturnsFalseWithSkippedResult() {
    UUID key = UUID.randomUUID();

    report.putResult(key, ResultImpl.skip());

    assertThat(report.hasTransactionSucceeded(), is(false));
  }

  @Test
  void testGetResultReturnsStoredResult() {
    UUID key = UUID.randomUUID();
    Result<Void> result = ResultImpl.pass();

    report.putResult(key, result);

    assertThat(report.getResult(key), is(sameInstance(result)));
  }

  @Test
  void testGetResultReturnsNullForUnknownKey() {
    UUID unknownKey = UUID.randomUUID();

    assertThat(report.getResult(unknownKey), is(nullValue()));
  }

  @Test
  void testGetFailedResultsReturnsEmptyListWhenNoFailures() {
    UUID key = UUID.randomUUID();
    report.putResult(key, ResultImpl.pass());

    List<Result> failedResults = report.getFailedResults();

    assertThat(failedResults, is(notNullValue()));
    assertThat(failedResults, hasSize(0));
  }

  @Test
  void testGetFailedResultsReturnsFailedResults() {
    UUID passKey = UUID.randomUUID();
    UUID failKey = UUID.randomUUID();

    report.putResult(passKey, ResultImpl.pass());
    report.putResult(failKey, ResultImpl.fail(new ConfiguratorException("error")));

    List<Result> failedResults = report.getFailedResults();

    assertThat(failedResults, hasSize(1));
  }

  @Test
  void testGetFailedResultsIncludesRollbackAndSkippedResults() {
    UUID passKey = UUID.randomUUID();
    UUID failKey = UUID.randomUUID();
    UUID rollbackKey = UUID.randomUUID();
    UUID skipKey = UUID.randomUUID();

    report.putResult(passKey, ResultImpl.pass());
    report.putResult(failKey, ResultImpl.fail(new ConfiguratorException("error")));
    report.putResult(rollbackKey, ResultImpl.rollback());
    report.putResult(skipKey, ResultImpl.skip());

    List<Result> failedResults = report.getFailedResults();

    assertThat(failedResults, hasSize(3));
  }

  @Test
  void testContainsFailedResultsReturnsFalseWhenEmpty() {
    assertThat(report.containsFailedResults(), is(false));
  }

  @Test
  void testContainsFailedResultsReturnsFalseWhenAllPassed() {
    report.putResult(UUID.randomUUID(), ResultImpl.pass());
    report.putResult(UUID.randomUUID(), ResultImpl.passWithData("data"));

    assertThat(report.containsFailedResults(), is(false));
  }

  @Test
  void testContainsFailedResultsReturnsTrueWhenFailed() {
    report.putResult(UUID.randomUUID(), ResultImpl.pass());
    report.putResult(UUID.randomUUID(), ResultImpl.fail(new ConfiguratorException("error")));

    assertThat(report.containsFailedResults(), is(true));
  }

  @Test
  void testContainsFailedResultsReturnsTrueForRollback() {
    report.putResult(UUID.randomUUID(), ResultImpl.rollback());

    assertThat(report.containsFailedResults(), is(true));
  }

  @Test
  void testContainsFailedResultsReturnsTrueForSkipped() {
    report.putResult(UUID.randomUUID(), ResultImpl.skip());

    assertThat(report.containsFailedResults(), is(true));
  }

  @Test
  void testPutResultOverwritesPreviousResult() {
    UUID key = UUID.randomUUID();
    Result<Void> firstResult = ResultImpl.pass();
    Result<Void> secondResult = ResultImpl.fail(new ConfiguratorException("error"));

    report.putResult(key, firstResult);
    report.putResult(key, secondResult);

    assertThat(report.getResult(key), is(sameInstance(secondResult)));
  }

  @Test
  void testGetFailedResultsReturnsUnmodifiableList() {
    report.putResult(UUID.randomUUID(), ResultImpl.fail(new ConfiguratorException("error")));

    List<Result> failedResults = report.getFailedResults();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class, () -> failedResults.add(ResultImpl.pass()));
  }

  @Test
  void testMultipleResultsPreserveOrder() {
    UUID key1 = UUID.randomUUID();
    UUID key2 = UUID.randomUUID();
    UUID key3 = UUID.randomUUID();

    Result<Void> result1 = ResultImpl.fail(new ConfiguratorException("error1"));
    Result<Void> result2 = ResultImpl.rollback();
    Result<Void> result3 = ResultImpl.skip();

    report.putResult(key1, result1);
    report.putResult(key2, result2);
    report.putResult(key3, result3);

    List<Result> failedResults = report.getFailedResults();

    assertThat(failedResults, hasSize(3));
  }
}
