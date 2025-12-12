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
package org.codice.ddf.admin.configurator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class StatusTest {

  @Test
  void testAllStatusValuesExist() {
    assertThat(Status.COMMIT_PASSED, is(notNullValue()));
    assertThat(Status.COMMIT_FAILED, is(notNullValue()));
    assertThat(Status.SKIPPED, is(notNullValue()));
    assertThat(Status.ROLLBACK_PASSED, is(notNullValue()));
    assertThat(Status.ROLLBACK_FAILED, is(notNullValue()));
  }

  @Test
  void testStatusEnumHasFiveValues() {
    assertThat(Status.values().length, is(5));
  }

  @Test
  void testStatusValuesArray() {
    assertThat(
        Status.values(),
        arrayContainingInAnyOrder(
            Status.COMMIT_PASSED,
            Status.COMMIT_FAILED,
            Status.SKIPPED,
            Status.ROLLBACK_PASSED,
            Status.ROLLBACK_FAILED));
  }

  @Test
  void testValueOfCommitPassed() {
    assertThat(Status.valueOf("COMMIT_PASSED"), is(Status.COMMIT_PASSED));
  }

  @Test
  void testValueOfCommitFailed() {
    assertThat(Status.valueOf("COMMIT_FAILED"), is(Status.COMMIT_FAILED));
  }

  @Test
  void testValueOfSkipped() {
    assertThat(Status.valueOf("SKIPPED"), is(Status.SKIPPED));
  }

  @Test
  void testValueOfRollbackPassed() {
    assertThat(Status.valueOf("ROLLBACK_PASSED"), is(Status.ROLLBACK_PASSED));
  }

  @Test
  void testValueOfRollbackFailed() {
    assertThat(Status.valueOf("ROLLBACK_FAILED"), is(Status.ROLLBACK_FAILED));
  }
}
