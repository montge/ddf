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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class ValidateReportTest {

  @Test
  void testConstructor() {
    ValidateReport report = new ValidateReport("metacard123");

    assertThat(report.getId(), is("metacard123"));
    assertThat(report.getEntries(), is(empty()));
  }

  @Test
  void testAddEntry() {
    ValidateReport report = new ValidateReport("metacard123");
    ValidateReportEntry entry =
        new ValidateReportEntry("TestValidator", Collections.emptyList(), Collections.emptyList());

    report.addEntry(entry);

    assertThat(report.getEntries(), hasSize(1));
    assertThat(report.getEntries().get(0), is(sameInstance(entry)));
  }

  @Test
  void testAddMultipleEntries() {
    ValidateReport report = new ValidateReport("metacard123");
    ValidateReportEntry entry1 =
        new ValidateReportEntry("Validator1", Arrays.asList("error1"), Collections.emptyList());
    ValidateReportEntry entry2 =
        new ValidateReportEntry("Validator2", Collections.emptyList(), Arrays.asList("warning1"));
    ValidateReportEntry entry3 =
        new ValidateReportEntry("Validator3", Arrays.asList("error2"), Arrays.asList("warning2"));

    report.addEntry(entry1);
    report.addEntry(entry2);
    report.addEntry(entry3);

    assertThat(report.getEntries(), hasSize(3));
  }

  @Test
  void testGetEntriesPreservesOrder() {
    ValidateReport report = new ValidateReport("metacard123");
    ValidateReportEntry entry1 =
        new ValidateReportEntry("First", Collections.emptyList(), Collections.emptyList());
    ValidateReportEntry entry2 =
        new ValidateReportEntry("Second", Collections.emptyList(), Collections.emptyList());

    report.addEntry(entry1);
    report.addEntry(entry2);

    assertThat(report.getEntries().get(0).getValidatorName(), is("First"));
    assertThat(report.getEntries().get(1).getValidatorName(), is("Second"));
  }

  @Test
  void testWithNullId() {
    ValidateReport report = new ValidateReport(null);

    assertThat(report.getId(), is((String) null));
  }

  @Test
  void testWithEmptyId() {
    ValidateReport report = new ValidateReport("");

    assertThat(report.getId(), is(""));
  }
}
