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
package ddf.catalog.validation.violation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

/** Tests for {@link QueryValidationViolation} interface and its Severity enum. */
public class QueryValidationViolationTest {

  @Test
  public void testSeverityWarningExists() {
    assertThat(QueryValidationViolation.Severity.WARNING, is(notNullValue()));
  }

  @Test
  public void testSeverityErrorExists() {
    assertThat(QueryValidationViolation.Severity.ERROR, is(notNullValue()));
  }

  @Test
  public void testSeverityValuesCount() {
    assertThat(QueryValidationViolation.Severity.values().length, is(2));
  }

  @Test
  public void testSeverityValuesOrder() {
    assertThat(
        QueryValidationViolation.Severity.values(),
        arrayContaining(
            QueryValidationViolation.Severity.WARNING, QueryValidationViolation.Severity.ERROR));
  }

  @Test
  public void testSeverityWarningOrdinal() {
    assertThat(QueryValidationViolation.Severity.WARNING.ordinal(), is(0));
  }

  @Test
  public void testSeverityErrorOrdinal() {
    assertThat(QueryValidationViolation.Severity.ERROR.ordinal(), is(1));
  }

  @Test
  public void testSeverityWarningName() {
    assertThat(QueryValidationViolation.Severity.WARNING.name(), is("WARNING"));
  }

  @Test
  public void testSeverityErrorName() {
    assertThat(QueryValidationViolation.Severity.ERROR.name(), is("ERROR"));
  }

  @Test
  public void testSeverityValueOfWarning() {
    assertThat(
        QueryValidationViolation.Severity.valueOf("WARNING"),
        is(QueryValidationViolation.Severity.WARNING));
  }

  @Test
  public void testSeverityValueOfError() {
    assertThat(
        QueryValidationViolation.Severity.valueOf("ERROR"),
        is(QueryValidationViolation.Severity.ERROR));
  }
}
