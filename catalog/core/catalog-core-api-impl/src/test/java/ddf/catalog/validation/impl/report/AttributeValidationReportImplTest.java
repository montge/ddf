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
package ddf.catalog.validation.impl.report;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.validation.violation.ValidationViolation;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttributeValidationReportImplTest {

  private AttributeValidationReportImpl report;

  @BeforeEach
  void setUp() {
    report = new AttributeValidationReportImpl();
  }

  @Test
  void testInitialViolationsEmpty() {
    assertThat(report.getAttributeValidationViolations(), is(empty()));
  }

  @Test
  void testInitialSuggestedValuesEmpty() {
    assertThat(report.getSuggestedValues(), is(empty()));
  }

  @Test
  void testAddViolation() {
    ValidationViolation violation = mock(ValidationViolation.class);

    report.addViolation(violation);

    assertThat(report.getAttributeValidationViolations(), hasSize(1));
    assertThat(report.getAttributeValidationViolations().contains(violation), is(true));
  }

  @Test
  void testAddViolations() {
    ValidationViolation violation1 = mock(ValidationViolation.class);
    ValidationViolation violation2 = mock(ValidationViolation.class);
    Set<ValidationViolation> violations = new HashSet<>();
    violations.add(violation1);
    violations.add(violation2);

    report.addViolations(violations);

    assertThat(report.getAttributeValidationViolations(), hasSize(2));
  }

  @Test
  void testAddNullViolationThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> report.addViolation(null));
  }

  @Test
  void testAddNullViolationsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> report.addViolations(null));
  }

  @Test
  void testAddEmptyViolationsThrowsException() {
    Set<ValidationViolation> emptyViolations = new HashSet<>();

    assertThrows(IllegalArgumentException.class, () -> report.addViolations(emptyViolations));
  }

  @Test
  void testAddSuggestedValue() {
    report.addSuggestedValue("suggestion1");

    assertThat(report.getSuggestedValues(), hasSize(1));
    assertThat(report.getSuggestedValues().contains("suggestion1"), is(true));
  }

  @Test
  void testAddSuggestedValues() {
    Set<String> values = new HashSet<>();
    values.add("value1");
    values.add("value2");

    report.addSuggestedValues(values);

    assertThat(report.getSuggestedValues(), hasSize(2));
    assertThat(report.getSuggestedValues(), containsInAnyOrder("value1", "value2"));
  }

  @Test
  void testAddNullSuggestedValueThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> report.addSuggestedValue(null));
  }

  @Test
  void testAddNullSuggestedValuesThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> report.addSuggestedValues(null));
  }

  @Test
  void testAddEmptySuggestedValuesThrowsException() {
    Set<String> emptyValues = new HashSet<>();

    assertThrows(IllegalArgumentException.class, () -> report.addSuggestedValues(emptyValues));
  }

  @Test
  void testGetViolationsReturnsUnmodifiableSet() {
    ValidationViolation violation = mock(ValidationViolation.class);
    report.addViolation(violation);

    Set<ValidationViolation> violations = report.getAttributeValidationViolations();

    assertThrows(
        UnsupportedOperationException.class, () -> violations.add(mock(ValidationViolation.class)));
  }

  @Test
  void testGetSuggestedValuesReturnsUnmodifiableSet() {
    report.addSuggestedValue("value");

    Set<String> values = report.getSuggestedValues();

    assertThrows(UnsupportedOperationException.class, () -> values.add("newValue"));
  }

  @Test
  void testMultipleAddOperations() {
    ValidationViolation violation1 = mock(ValidationViolation.class);
    ValidationViolation violation2 = mock(ValidationViolation.class);
    report.addViolation(violation1);
    report.addViolation(violation2);
    report.addSuggestedValue("value1");
    report.addSuggestedValue("value2");

    assertThat(report.getAttributeValidationViolations(), hasSize(2));
    assertThat(report.getSuggestedValues(), hasSize(2));
  }
}
