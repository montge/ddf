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
package org.codice.ddf.validator.metacard.wkt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Core;
import ddf.catalog.data.types.Validation;
import ddf.catalog.validation.ValidationException;
import ddf.catalog.validation.report.MetacardValidationReport;
import ddf.catalog.validation.violation.ValidationViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.codice.ddf.validator.wkt.WktValidator;
import org.codice.ddf.validator.wkt.WktValidatorImpl;
import org.junit.jupiter.api.Test;

public class MetacardWktValidatorTest {

  public static final String TEST_ATTRIBUTE = "test-attribute";

  private MetacardWktValidator metacardWktValidator =
      new MetacardWktValidator(new WktValidatorImpl(), TEST_ATTRIBUTE);

  @Test
  public void testMetacardWithValidWkt() throws Exception {
    Metacard metacard = getMetacard();
    String wktLoc = "POINT(50 50)";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wktLoc));
    metacardWktValidator.validate(metacard);
  }

  @Test
  public void testMetacardWithInvalidLocation() {
    Metacard metacard = getMetacard();
    String wktLoc = "POINT(250  250)";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wktLoc));
    assertThrows(ValidationException.class, () -> metacardWktValidator.validate(metacard));
  }

  @Test
  public void testNoLocation() throws Exception {
    Metacard metacard = getMetacard();
    metacardWktValidator.validate(metacard);
  }

  @Test
  public void testLocationValidationErrorNoLocation() throws Exception {
    String validationMessage = TEST_ATTRIBUTE + " is invalid: POINT(2000 2000)";
    Metacard metacard = getMetacard();
    metacard.setAttribute(new AttributeImpl(Validation.VALIDATION_ERRORS, validationMessage));
    Optional<MetacardValidationReport> validationReportOptional =
        metacardWktValidator.validateMetacard(metacard);
    assertThat(validationReportOptional.isPresent(), is(true));

    Set<ValidationViolation> validationViolationSet =
        validationReportOptional.get().getMetacardValidationViolations();
    assertThat(validationViolationSet, hasSize(1));
    assertThat(validationViolationSet.iterator().next().getMessage(), equalTo(validationMessage));
  }

  @Test
  public void testNoLocationNoErrors() throws Exception {
    Metacard metacard = getMetacard();
    Optional<MetacardValidationReport> validationReportOptional =
        metacardWktValidator.validateMetacard(metacard);
    assertThat(validationReportOptional.isPresent(), is(false));
  }

  @Test
  public void testNoLocationNonLocationError() throws Exception {
    Metacard metacard = getMetacard();
    metacard.setAttribute(
        new AttributeImpl(Validation.VALIDATION_ERRORS, "another attribute error"));
    Optional<MetacardValidationReport> validationReportOptional =
        metacardWktValidator.validateMetacard(metacard);
    assertThat(validationReportOptional.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithValidWkt() throws Exception {
    Metacard metacard = getMetacard();
    String wktLoc = "POLYGON((10 10, 20 10, 20 20, 10 20, 10 10))";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wktLoc));

    Optional<MetacardValidationReport> report = metacardWktValidator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithInvalidWkt() throws Exception {
    Metacard metacard = getMetacard();
    String invalidWkt = "POINT(250 250)";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, invalidWkt));

    Optional<MetacardValidationReport> report = metacardWktValidator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    Set<ValidationViolation> violations = report.get().getMetacardValidationViolations();
    assertThat(violations, hasSize(1));
    ValidationViolation violation = violations.iterator().next();
    assertThat(violation.getMessage(), containsString(TEST_ATTRIBUTE + " is invalid:"));
    assertThat(violation.getAttributes(), hasSize(1));
    assertThat(violation.getAttributes().iterator().next(), is(TEST_ATTRIBUTE));
    assertThat(violation.getSeverity(), is(ValidationViolation.Severity.ERROR));
  }

  @Test
  public void testValidateMetacardWithNullAttribute() throws Exception {
    Metacard metacard = getMetacard();
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, (String) null));

    Optional<MetacardValidationReport> report = metacardWktValidator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithEmptyStringAttribute() throws Exception {
    Metacard metacard = getMetacard();
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, ""));

    Optional<MetacardValidationReport> report = metacardWktValidator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithMultipleValidationErrors() throws Exception {
    Metacard metacard = getMetacard();
    List<String> errors = new ArrayList<>();
    errors.add(TEST_ATTRIBUTE + " is invalid: POINT(1000 1000)");
    errors.add("another-attribute is invalid: some value");
    metacard.setAttribute(
        new AttributeImpl(
            Validation.VALIDATION_ERRORS, (List<? extends java.io.Serializable>) errors));

    Optional<MetacardValidationReport> report = metacardWktValidator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    Set<ValidationViolation> violations = report.get().getMetacardValidationViolations();
    assertThat(violations, hasSize(1));
    ValidationViolation violation = violations.iterator().next();
    assertThat(violation.getMessage(), equalTo(TEST_ATTRIBUTE + " is invalid: POINT(1000 1000)"));
  }

  @Test
  public void testValidateWithMockedWktValidator() throws Exception {
    WktValidator mockValidator = mock(WktValidator.class);
    when(mockValidator.isValid(anyString())).thenReturn(true);

    MetacardWktValidator validator = new MetacardWktValidator(mockValidator, TEST_ATTRIBUTE);
    Metacard metacard = getMetacard();
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, "POINT(10 20)"));

    validator.validate(metacard);
    verify(mockValidator).isValid("POINT(10 20)");
  }

  @Test
  public void testValidateWithMockedWktValidatorInvalid() throws Exception {
    WktValidator mockValidator = mock(WktValidator.class);
    when(mockValidator.isValid(anyString())).thenReturn(false);

    MetacardWktValidator validator = new MetacardWktValidator(mockValidator, TEST_ATTRIBUTE);
    Metacard metacard = getMetacard();
    String wktValue = "INVALID WKT";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wktValue));

    try {
      validator.validate(metacard);
    } catch (ValidationException e) {
      assertThat(e.getErrors(), hasSize(1));
      assertThat(e.getErrors().get(0), containsString(TEST_ATTRIBUTE + " is invalid:"));
    }

    verify(mockValidator).isValid(wktValue);
  }

  @Test
  public void testValidateNoAttributeCallsNoValidator() throws Exception {
    WktValidator mockValidator = mock(WktValidator.class);
    MetacardWktValidator validator = new MetacardWktValidator(mockValidator, TEST_ATTRIBUTE);
    Metacard metacard = getMetacard();

    validator.validate(metacard);

    verify(mockValidator, never()).isValid(anyString());
  }

  @Test
  public void testValidateMetacardWithComplexPolygon() throws Exception {
    Metacard metacard = getMetacard();
    String wkt = "POLYGON((30 10, 40 40, 20 40, 10 20, 30 10))";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wkt));

    Optional<MetacardValidationReport> report = metacardWktValidator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithLineString() throws Exception {
    Metacard metacard = getMetacard();
    String wkt = "LINESTRING(30 10, 10 30, 40 40)";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wkt));

    Optional<MetacardValidationReport> report = metacardWktValidator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithMultiPoint() throws Exception {
    Metacard metacard = getMetacard();
    String wkt = "MULTIPOINT((10 40), (40 30), (20 20), (30 10))";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wkt));

    Optional<MetacardValidationReport> report = metacardWktValidator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithMalformedWkt() {
    Metacard metacard = getMetacard();
    String wkt = "POINT(30)";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wkt));

    assertThrows(ValidationException.class, () -> metacardWktValidator.validate(metacard));
  }

  @Test
  public void testValidateExceptionContainsErrorMessage() throws Exception {
    Metacard metacard = getMetacard();
    String wkt = "POINT(300 300)";
    metacard.setAttribute(new AttributeImpl(TEST_ATTRIBUTE, wkt));

    try {
      metacardWktValidator.validate(metacard);
    } catch (ValidationException e) {
      assertThat(e, is(notNullValue()));
      assertThat(e.getErrors(), hasSize(1));
      assertThat(e.getErrors().get(0), containsString(TEST_ATTRIBUTE));
    }
  }

  private Metacard getMetacard() {
    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Core.ID, UUID.randomUUID().toString()));
    return metacard;
  }
}
