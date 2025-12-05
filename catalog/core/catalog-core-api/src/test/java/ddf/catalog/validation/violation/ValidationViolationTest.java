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

/** Tests for {@link ValidationViolation} interface and its Severity enum. */
public class ValidationViolationTest {

  @Test
  public void testSeverityWarningExists() {
    assertThat(ValidationViolation.Severity.WARNING, is(notNullValue()));
  }

  @Test
  public void testSeverityErrorExists() {
    assertThat(ValidationViolation.Severity.ERROR, is(notNullValue()));
  }

  @Test
  public void testSeverityValuesCount() {
    assertThat(ValidationViolation.Severity.values().length, is(2));
  }

  @Test
  public void testSeverityValuesOrder() {
    assertThat(
        ValidationViolation.Severity.values(),
        arrayContaining(ValidationViolation.Severity.WARNING, ValidationViolation.Severity.ERROR));
  }

  @Test
  public void testSeverityWarningOrdinal() {
    assertThat(ValidationViolation.Severity.WARNING.ordinal(), is(0));
  }

  @Test
  public void testSeverityErrorOrdinal() {
    assertThat(ValidationViolation.Severity.ERROR.ordinal(), is(1));
  }

  @Test
  public void testSeverityWarningName() {
    assertThat(ValidationViolation.Severity.WARNING.name(), is("WARNING"));
  }

  @Test
  public void testSeverityErrorName() {
    assertThat(ValidationViolation.Severity.ERROR.name(), is("ERROR"));
  }

  @Test
  public void testSeverityValueOfWarning() {
    assertThat(
        ValidationViolation.Severity.valueOf("WARNING"), is(ValidationViolation.Severity.WARNING));
  }

  @Test
  public void testSeverityValueOfError() {
    assertThat(
        ValidationViolation.Severity.valueOf("ERROR"), is(ValidationViolation.Severity.ERROR));
  }
}
