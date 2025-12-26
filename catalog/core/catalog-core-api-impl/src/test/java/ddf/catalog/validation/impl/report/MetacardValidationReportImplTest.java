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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.validation.violation.ValidationViolation;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetacardValidationReportImplTest {

  private MetacardValidationReportImpl report;

  @BeforeEach
  void setUp() {
    report = new MetacardValidationReportImpl();
  }

  @Test
  void testInitialAttributeViolationsEmpty() {
    assertThat(report.getAttributeValidationViolations(), is(empty()));
  }

  @Test
  void testInitialMetacardViolationsEmpty() {
    assertThat(report.getMetacardValidationViolations(), is(empty()));
  }

  @Test
  void testAddAttributeViolation() {
    ValidationViolation violation = mock(ValidationViolation.class);

    report.addAttributeViolation(violation);

    assertThat(report.getAttributeValidationViolations(), hasSize(1));
    assertThat(report.getAttributeValidationViolations().contains(violation), is(true));
  }

  @Test
  void testAddMetacardViolation() {
    ValidationViolation violation = mock(ValidationViolation.class);

    report.addMetacardViolation(violation);

    assertThat(report.getMetacardValidationViolations(), hasSize(1));
    assertThat(report.getMetacardValidationViolations().contains(violation), is(true));
  }

  @Test
  void testAddMultipleAttributeViolations() {
    ValidationViolation violation1 = mock(ValidationViolation.class);
    ValidationViolation violation2 = mock(ValidationViolation.class);

    report.addAttributeViolation(violation1);
    report.addAttributeViolation(violation2);

    assertThat(report.getAttributeValidationViolations(), hasSize(2));
  }

  @Test
  void testAddMultipleMetacardViolations() {
    ValidationViolation violation1 = mock(ValidationViolation.class);
    ValidationViolation violation2 = mock(ValidationViolation.class);

    report.addMetacardViolation(violation1);
    report.addMetacardViolation(violation2);

    assertThat(report.getMetacardValidationViolations(), hasSize(2));
  }

  @Test
  void testAddNullAttributeViolationThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> report.addAttributeViolation(null));
  }

  @Test
  void testAddNullMetacardViolationThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> report.addMetacardViolation(null));
  }

  @Test
  void testGetAttributeViolationsReturnsUnmodifiableSet() {
    ValidationViolation violation = mock(ValidationViolation.class);
    report.addAttributeViolation(violation);

    Set<ValidationViolation> violations = report.getAttributeValidationViolations();

    assertThrows(
        UnsupportedOperationException.class, () -> violations.add(mock(ValidationViolation.class)));
  }

  @Test
  void testGetMetacardViolationsReturnsUnmodifiableSet() {
    ValidationViolation violation = mock(ValidationViolation.class);
    report.addMetacardViolation(violation);

    Set<ValidationViolation> violations = report.getMetacardValidationViolations();

    assertThrows(
        UnsupportedOperationException.class, () -> violations.add(mock(ValidationViolation.class)));
  }

  @Test
  void testAttributeAndMetacardViolationsAreIndependent() {
    ValidationViolation attrViolation = mock(ValidationViolation.class);
    ValidationViolation metacardViolation = mock(ValidationViolation.class);

    report.addAttributeViolation(attrViolation);
    report.addMetacardViolation(metacardViolation);

    assertThat(report.getAttributeValidationViolations(), hasSize(1));
    assertThat(report.getMetacardValidationViolations(), hasSize(1));
    assertThat(report.getAttributeValidationViolations().contains(attrViolation), is(true));
    assertThat(report.getMetacardValidationViolations().contains(metacardViolation), is(true));
  }
}
