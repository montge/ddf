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
import static org.hamcrest.Matchers.is;

import org.codice.ddf.admin.configurator.ConfiguratorException;
import org.codice.ddf.admin.configurator.Result;
import org.codice.ddf.admin.configurator.Status;
import org.junit.jupiter.api.Test;

class ResultImplTest {

  @Test
  void testPassReturnsCommitPassedStatus() {
    Result<Void> result = ResultImpl.pass();

    assertThat(result.getStatus(), is(Status.COMMIT_PASSED));
    assertThat(result.isOperationSucceeded(), is(true));
    assertThat(result.getError().isPresent(), is(false));
    assertThat(result.getOperationData().isPresent(), is(false));
  }

  @Test
  void testPassWithDataReturnsCommitPassedWithData() {
    String testData = "test-data";

    Result<String> result = ResultImpl.passWithData(testData);

    assertThat(result.getStatus(), is(Status.COMMIT_PASSED));
    assertThat(result.isOperationSucceeded(), is(true));
    assertThat(result.getError().isPresent(), is(false));
    assertThat(result.getOperationData().isPresent(), is(true));
    assertThat(result.getOperationData().get(), is(testData));
  }

  @Test
  void testFailReturnsCommitFailedStatus() {
    ConfiguratorException error = new ConfiguratorException("Test error");

    Result<Void> result = ResultImpl.fail(error);

    assertThat(result.getStatus(), is(Status.COMMIT_FAILED));
    assertThat(result.isOperationSucceeded(), is(false));
    assertThat(result.getError().isPresent(), is(true));
    assertThat(result.getError().get().getMessage(), is("Test error"));
    assertThat(result.getOperationData().isPresent(), is(false));
  }

  @Test
  void testRollbackReturnsRollbackPassedStatus() {
    Result<Void> result = ResultImpl.rollback();

    assertThat(result.getStatus(), is(Status.ROLLBACK_PASSED));
    assertThat(result.isOperationSucceeded(), is(false));
    assertThat(result.getError().isPresent(), is(false));
    assertThat(result.getOperationData().isPresent(), is(false));
  }

  @Test
  void testRollbackWithDataReturnsRollbackPassedWithData() {
    Integer testData = 42;

    Result<Integer> result = ResultImpl.rollbackWithData(testData);

    assertThat(result.getStatus(), is(Status.ROLLBACK_PASSED));
    assertThat(result.isOperationSucceeded(), is(false));
    assertThat(result.getError().isPresent(), is(false));
    assertThat(result.getOperationData().isPresent(), is(true));
    assertThat(result.getOperationData().get(), is(42));
  }

  @Test
  void testRollbackFailReturnsRollbackFailedStatus() {
    ConfiguratorException error = new ConfiguratorException("Rollback error");

    Result<Void> result = ResultImpl.rollbackFail(error);

    assertThat(result.getStatus(), is(Status.ROLLBACK_FAILED));
    assertThat(result.isOperationSucceeded(), is(false));
    assertThat(result.getError().isPresent(), is(true));
    assertThat(result.getError().get().getMessage(), is("Rollback error"));
    assertThat(result.getOperationData().isPresent(), is(false));
  }

  @Test
  void testRollbackFailWithDataReturnsRollbackFailedWithData() {
    ConfiguratorException error = new ConfiguratorException("Rollback fail with data");
    String testData = "rollback-data";

    Result<String> result = ResultImpl.rollbackFailWithData(error, testData);

    assertThat(result.getStatus(), is(Status.ROLLBACK_FAILED));
    assertThat(result.isOperationSucceeded(), is(false));
    assertThat(result.getError().isPresent(), is(true));
    assertThat(result.getError().get().getMessage(), is("Rollback fail with data"));
    assertThat(result.getOperationData().isPresent(), is(true));
    assertThat(result.getOperationData().get(), is(testData));
  }

  @Test
  void testSkipReturnsSkippedStatus() {
    Result<Void> result = ResultImpl.skip();

    assertThat(result.getStatus(), is(Status.SKIPPED));
    assertThat(result.isOperationSucceeded(), is(false));
    assertThat(result.getError().isPresent(), is(false));
    assertThat(result.getOperationData().isPresent(), is(false));
  }

  @Test
  void testPassWithNullData() {
    Result<String> result = ResultImpl.passWithData(null);

    assertThat(result.getStatus(), is(Status.COMMIT_PASSED));
    assertThat(result.isOperationSucceeded(), is(true));
    assertThat(result.getOperationData().isPresent(), is(false));
  }

  @Test
  void testIsOperationSucceededOnlyTrueForCommitPassed() {
    assertThat(ResultImpl.pass().isOperationSucceeded(), is(true));
    assertThat(ResultImpl.passWithData("data").isOperationSucceeded(), is(true));

    assertThat(ResultImpl.fail(new ConfiguratorException("e")).isOperationSucceeded(), is(false));
    assertThat(ResultImpl.rollback().isOperationSucceeded(), is(false));
    assertThat(ResultImpl.rollbackWithData("data").isOperationSucceeded(), is(false));
    assertThat(
        ResultImpl.rollbackFail(new ConfiguratorException("e")).isOperationSucceeded(), is(false));
    assertThat(ResultImpl.skip().isOperationSucceeded(), is(false));
  }
}
