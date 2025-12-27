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
import static org.hamcrest.Matchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidatePrinterTest {

  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;
  private ValidatePrinter printer;

  @BeforeEach
  void setUp() {
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
    printer = new ValidatePrinter();
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void testPrintReportWithNoEntries() {
    ValidateReport report = new ValidateReport("test-id");

    printer.print(report);

    String output = outputStream.toString();
    assertThat(output, containsString("test-id"));
  }

  @Test
  void testPrintReportWithErrors() {
    ValidateReport report = new ValidateReport("metacard-123");
    ValidateReportEntry entry =
        new ValidateReportEntry(
            "TestValidator", Arrays.asList("Error 1", "Error 2"), Collections.emptyList());
    report.addEntry(entry);

    printer.print(report);

    String output = outputStream.toString();
    assertThat(output, containsString("metacard-123"));
    assertThat(output, containsString("TestValidator"));
    assertThat(output, containsString("Error 1"));
    assertThat(output, containsString("Error 2"));
  }

  @Test
  void testPrintReportWithWarnings() {
    ValidateReport report = new ValidateReport("metacard-456");
    ValidateReportEntry entry =
        new ValidateReportEntry(
            "WarningValidator", Collections.emptyList(), Arrays.asList("Warning 1"));
    report.addEntry(entry);

    printer.print(report);

    String output = outputStream.toString();
    assertThat(output, containsString("metacard-456"));
    assertThat(output, containsString("WarningValidator"));
    assertThat(output, containsString("Warning 1"));
  }

  @Test
  void testPrintReportWithBothErrorsAndWarnings() {
    ValidateReport report = new ValidateReport("metacard-789");
    ValidateReportEntry entry =
        new ValidateReportEntry("MixedValidator", Arrays.asList("Error"), Arrays.asList("Warning"));
    report.addEntry(entry);

    printer.print(report);

    String output = outputStream.toString();
    assertThat(output, containsString("Error"));
    assertThat(output, containsString("Warning"));
  }

  @Test
  void testPrintReportSkipsEmptyEntries() {
    ValidateReport report = new ValidateReport("metacard-empty");
    ValidateReportEntry entry =
        new ValidateReportEntry("EmptyValidator", Collections.emptyList(), Collections.emptyList());
    report.addEntry(entry);

    printer.print(report);

    String output = outputStream.toString();
    assertThat(output, containsString("metacard-empty"));
  }

  @Test
  void testPrintError() {
    printer.printError("This is an error message");

    String output = outputStream.toString();
    assertThat(output, containsString("This is an error message"));
  }

  @Test
  void testPrintWarning() {
    printer.printWarning("This is a warning message");

    String output = outputStream.toString();
    assertThat(output, containsString("This is a warning message"));
  }

  @Test
  void testPrintSummaryWithNoErrors() {
    printer.printSummary(0, 10);

    String output = outputStream.toString();
    assertThat(output, containsString("SUMMARY"));
    assertThat(output, containsString("0/10"));
    assertThat(output, containsString("metacards contain errors or warnings"));
  }

  @Test
  void testPrintSummaryWithErrors() {
    printer.printSummary(5, 20);

    String output = outputStream.toString();
    assertThat(output, containsString("5/20"));
  }

  @Test
  void testPrintSummaryWithAllErrors() {
    printer.printSummary(100, 100);

    String output = outputStream.toString();
    assertThat(output, containsString("100/100"));
  }

  @Test
  void testPrintMultipleEntries() {
    ValidateReport report = new ValidateReport("multi-entry-metacard");
    report.addEntry(
        new ValidateReportEntry("Validator1", Arrays.asList("Error1"), Collections.emptyList()));
    report.addEntry(
        new ValidateReportEntry("Validator2", Collections.emptyList(), Arrays.asList("Warning1")));

    printer.print(report);

    String output = outputStream.toString();
    assertThat(output, containsString("Validator1"));
    assertThat(output, containsString("Validator2"));
    assertThat(output, containsString("Error1"));
    assertThat(output, containsString("Warning1"));
  }
}
