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
package org.codice.ddf.commands.catalog.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidateReportEntryTest {

  @Test
  void testConstructorAndGetters() {
    List<String> errors = Arrays.asList("error1", "error2");
    List<String> warnings = Arrays.asList("warning1");

    ValidateReportEntry entry = new ValidateReportEntry("TestValidator", errors, warnings);

    assertThat(entry.getValidatorName(), is("TestValidator"));
    assertThat(entry.getErrors(), hasSize(2));
    assertThat(entry.getErrors(), containsInAnyOrder("error1", "error2"));
    assertThat(entry.getWarnings(), hasSize(1));
    assertThat(entry.getWarnings(), containsInAnyOrder("warning1"));
  }

  @Test
  void testWithEmptyErrors() {
    List<String> warnings = Arrays.asList("warning1");

    ValidateReportEntry entry =
        new ValidateReportEntry("TestValidator", Collections.emptyList(), warnings);

    assertThat(entry.getErrors(), is(empty()));
    assertThat(entry.getWarnings(), hasSize(1));
  }

  @Test
  void testWithEmptyWarnings() {
    List<String> errors = Arrays.asList("error1");

    ValidateReportEntry entry =
        new ValidateReportEntry("TestValidator", errors, Collections.emptyList());

    assertThat(entry.getErrors(), hasSize(1));
    assertThat(entry.getWarnings(), is(empty()));
  }

  @Test
  void testWithNullErrors() {
    List<String> warnings = Arrays.asList("warning1");

    ValidateReportEntry entry = new ValidateReportEntry("TestValidator", null, warnings);

    assertThat(entry.getErrors(), is(nullValue()));
  }

  @Test
  void testWithNullWarnings() {
    List<String> errors = Arrays.asList("error1");

    ValidateReportEntry entry = new ValidateReportEntry("TestValidator", errors, null);

    assertThat(entry.getWarnings(), is(nullValue()));
  }

  @Test
  void testWithBothEmpty() {
    ValidateReportEntry entry =
        new ValidateReportEntry("TestValidator", Collections.emptyList(), Collections.emptyList());

    assertThat(entry.getValidatorName(), is("TestValidator"));
    assertThat(entry.getErrors(), is(empty()));
    assertThat(entry.getWarnings(), is(empty()));
  }

  @Test
  void testWithMultipleErrors() {
    List<String> errors = Arrays.asList("error1", "error2", "error3", "error4");
    List<String> warnings = Collections.emptyList();

    ValidateReportEntry entry = new ValidateReportEntry("SchemaValidator", errors, warnings);

    assertThat(entry.getValidatorName(), is("SchemaValidator"));
    assertThat(entry.getErrors(), hasSize(4));
  }
}
