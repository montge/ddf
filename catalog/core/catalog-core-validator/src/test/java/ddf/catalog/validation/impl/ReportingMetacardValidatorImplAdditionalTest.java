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
package ddf.catalog.validation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.validation.AttributeValidator;
import ddf.catalog.validation.AttributeValidatorRegistry;
import ddf.catalog.validation.ValidationException;
import ddf.catalog.validation.impl.report.AttributeValidationReportImpl;
import ddf.catalog.validation.impl.validator.SizeValidator;
import ddf.catalog.validation.impl.violation.ValidationViolationImpl;
import ddf.catalog.validation.report.MetacardValidationReport;
import ddf.catalog.validation.violation.ValidationViolation;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReportingMetacardValidatorImplAdditionalTest {

  @Mock private AttributeValidatorRegistry mockRegistry;

  @Mock private AttributeValidator mockValidator;

  private ReportingMetacardValidatorImpl validator;

  @BeforeEach
  public void setup() {
    validator = new ReportingMetacardValidatorImpl(mockRegistry);
  }

  @Test
  public void testValidateMetacardWithNull() {
    assertThrows(IllegalArgumentException.class, () -> validator.validateMetacard(null));
  }

  @Test
  public void testValidateWithNull() {
    assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
  }

  @Test
  public void testValidateMetacardWithNoAttributes() {
    MetacardImpl metacard = new MetacardImpl(new MetacardTypeImpl("test", Collections.emptySet()));

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithAttributeButNoValidator() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test Title");

    when(mockRegistry.getValidators(Metacard.TITLE)).thenReturn(Collections.emptySet());

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithNullAttributeValue() {
    MetacardImpl metacard = new MetacardImpl();
    // Title is in the metacard type but not set (null)

    when(mockRegistry.getValidators(Metacard.TITLE)).thenReturn(Collections.emptySet());

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    // Null attributes should be skipped
    assertThat(report.isPresent(), is(false));
    verify(mockRegistry, never()).getValidators(Metacard.TITLE);
  }

  @Test
  public void testValidateMetacardWithValidatorReturningNoViolations() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Valid Title");

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Collections.singleton(mockValidator));
    when(mockValidator.validate(any(Attribute.class))).thenReturn(Optional.empty());

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
    verify(mockValidator, times(1)).validate(any(Attribute.class));
  }

  @Test
  public void testValidateMetacardWithValidatorReturningViolations() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Invalid");

    AttributeValidationReportImpl attributeReport = new AttributeValidationReportImpl();
    attributeReport.addViolation(
        new ValidationViolationImpl(
            Collections.singleton(Metacard.TITLE),
            "Title is invalid",
            ValidationViolation.Severity.ERROR));

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Collections.singleton(mockValidator));
    when(mockValidator.validate(any(Attribute.class))).thenReturn(Optional.of(attributeReport));

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(1));
    verify(mockValidator, times(1)).validate(any(Attribute.class));
  }

  @Test
  public void testValidateMetacardWithMultipleValidatorsForSameAttribute() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    AttributeValidator validator1 = mock(AttributeValidator.class);
    AttributeValidator validator2 = mock(AttributeValidator.class);

    AttributeValidationReportImpl report1 = new AttributeValidationReportImpl();
    report1.addViolation(
        new ValidationViolationImpl(
            Collections.singleton(Metacard.TITLE),
            "Violation 1",
            ValidationViolation.Severity.ERROR));

    AttributeValidationReportImpl report2 = new AttributeValidationReportImpl();
    report2.addViolation(
        new ValidationViolationImpl(
            Collections.singleton(Metacard.TITLE),
            "Violation 2",
            ValidationViolation.Severity.WARNING));

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Sets.newHashSet(validator1, validator2));
    when(validator1.validate(any(Attribute.class))).thenReturn(Optional.of(report1));
    when(validator2.validate(any(Attribute.class))).thenReturn(Optional.of(report2));

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(2));
  }

  @Test
  public void testValidateThrowsExceptionWithErrors() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Invalid");

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Collections.singleton(new SizeValidator(10, 20)));

    try {
      validator.validate(metacard);
      assertThat("Expected ValidationException", false);
    } catch (ValidationException e) {
      assertThat(e.getErrors(), notNullValue());
      assertThat(e.getErrors(), hasSize(1));
      assertThat(e.getErrors().get(0), containsString("title"));
    }
  }

  @Test
  public void testValidateThrowsExceptionWithWarnings() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    AttributeValidationReportImpl attributeReport = new AttributeValidationReportImpl();
    attributeReport.addViolation(
        new ValidationViolationImpl(
            Collections.singleton(Metacard.TITLE),
            "Title warning",
            ValidationViolation.Severity.WARNING));

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Collections.singleton(mockValidator));
    when(mockValidator.validate(any(Attribute.class))).thenReturn(Optional.of(attributeReport));

    try {
      validator.validate(metacard);
      assertThat("Expected ValidationException", false);
    } catch (ValidationException e) {
      assertThat(e.getWarnings(), notNullValue());
      assertThat(e.getWarnings(), hasSize(1));
      assertThat(e.getWarnings().get(0), is("Title warning"));
    }
  }

  @Test
  public void testValidateThrowsExceptionWithMixedSeverities() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    AttributeValidator validator1 = mock(AttributeValidator.class);
    AttributeValidator validator2 = mock(AttributeValidator.class);

    AttributeValidationReportImpl report1 = new AttributeValidationReportImpl();
    report1.addViolation(
        new ValidationViolationImpl(
            Collections.singleton(Metacard.TITLE),
            "Error message",
            ValidationViolation.Severity.ERROR));

    AttributeValidationReportImpl report2 = new AttributeValidationReportImpl();
    report2.addViolation(
        new ValidationViolationImpl(
            Collections.singleton(Metacard.TITLE),
            "Warning message",
            ValidationViolation.Severity.WARNING));

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Sets.newHashSet(validator1, validator2));
    when(validator1.validate(any(Attribute.class))).thenReturn(Optional.of(report1));
    when(validator2.validate(any(Attribute.class))).thenReturn(Optional.of(report2));

    try {
      validator.validate(metacard);
      assertThat("Expected ValidationException", false);
    } catch (ValidationException e) {
      assertThat(e.getErrors(), hasSize(1));
      assertThat(e.getWarnings(), hasSize(1));
      assertThat(e.getErrors().get(0), is("Error message"));
      assertThat(e.getWarnings().get(0), is("Warning message"));
    }
  }

  @Test
  public void testValidateMetacardWithMultipleAttributes() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");
    metacard.setContentTypeName("application/xml");

    when(mockRegistry.getValidators(Metacard.TITLE)).thenReturn(Collections.emptySet());
    when(mockRegistry.getValidators(Metacard.CONTENT_TYPE)).thenReturn(Collections.emptySet());

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
    verify(mockRegistry, times(1)).getValidators(Metacard.TITLE);
    verify(mockRegistry, times(1)).getValidators(Metacard.CONTENT_TYPE);
  }

  @Test
  public void testValidateMetacardWithMultipleAttributesHavingViolations() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("X"); // Too short
    metacard.setContentTypeName("invalid");

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Collections.singleton(new SizeValidator(5, 20)));
    when(mockRegistry.getValidators(Metacard.CONTENT_TYPE))
        .thenReturn(Collections.singleton(new SizeValidator(20, 50)));

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(2));
  }

  @Test
  public void testValidateReportHasNoMetacardViolations() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Invalid");

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Collections.singleton(new SizeValidator(10, 20)));

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    // Should only have attribute violations, not metacard violations
    assertThat(report.get().getMetacardValidationViolations(), empty());
  }

  @Test
  public void testValidatorConstructorWithRegistry() {
    AttributeValidatorRegistry registry = new AttributeValidatorRegistryImpl();
    ReportingMetacardValidatorImpl validator = new ReportingMetacardValidatorImpl(registry);

    assertThat(validator, notNullValue());
  }

  @Test
  public void testValidateWithEmptyMetacard() throws ValidationException {
    MetacardImpl metacard = new MetacardImpl();

    when(mockRegistry.getValidators(any(String.class))).thenReturn(Collections.emptySet());

    // Should not throw exception for empty metacard
    validator.validate(metacard);
  }

  @Test
  public void testValidateMetacardWithAttributeHavingMultipleValues() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setAttribute(
        new AttributeImpl(Metacard.TITLE, java.util.Arrays.asList("Title1", "Title2")));

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Collections.singleton(mockValidator));
    when(mockValidator.validate(any(Attribute.class))).thenReturn(Optional.empty());

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
    verify(mockValidator, times(1)).validate(any(Attribute.class));
  }

  @Test
  public void testValidationExceptionContainsCorrectMessages() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("X");

    when(mockRegistry.getValidators(Metacard.TITLE))
        .thenReturn(Collections.singleton(new SizeValidator(5, 20)));

    try {
      validator.validate(metacard);
      assertThat("Expected ValidationException", false);
    } catch (ValidationException e) {
      assertThat(e.getErrors(), hasSize(1));
      String errorMessage = e.getErrors().get(0);
      assertThat(errorMessage, containsString("title"));
      assertThat(errorMessage.toLowerCase(), containsString("size"));
    }
  }
}
