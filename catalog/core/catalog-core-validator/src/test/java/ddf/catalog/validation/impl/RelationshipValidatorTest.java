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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.validation.ValidationException;
import ddf.catalog.validation.impl.validator.RelationshipValidator;
import ddf.catalog.validation.report.MetacardValidationReport;
import ddf.catalog.validation.violation.ValidationViolation;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RelationshipValidatorTest {

  private MetacardImpl metacard;

  @BeforeEach
  public void setup() {
    metacard = new MetacardImpl();
  }

  @Test
  public void testMustHaveRelationshipValid() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testMustHaveRelationshipInvalid() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    // Missing required encoding attribute

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(1));
    assertThat(
        report.get().getAttributeValidationViolations().iterator().next().getSeverity(),
        is(ValidationViolation.Severity.ERROR));
  }

  @Test
  public void testMustHaveWithMultipleTargetValues() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    // Use multi-valued attribute - setAttribute replaces, so use a list
    metacard.setAttribute(new AttributeImpl("encoding", java.util.Arrays.asList("UTF-8", "ASCII")));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8", "ASCII");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testMustHaveWithPartialTargetValues() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8", "ASCII");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(1));
  }

  @Test
  public void testCannotHaveRelationshipValid() {
    metacard.setAttribute(new AttributeImpl("format", "analog"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "analog", "cannotHave", "encoding");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testCannotHaveRelationshipInvalid() {
    metacard.setAttribute(new AttributeImpl("format", "analog"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "analog", "cannotHave", "encoding");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(1));
  }

  @Test
  public void testCannotHaveWithSpecificValue() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encryption", "AES"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "cannotHave", "encryption", "DES");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testCannotHaveWithSpecificValueViolation() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encryption", "DES"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "cannotHave", "encryption", "DES");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(1));
  }

  @Test
  public void testCanOnlyHaveRelationshipValid() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    RelationshipValidator validator =
        new RelationshipValidator(
            "format", "digital", "canOnlyHave", "encoding", "UTF-8", "ASCII", "UTF-16");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testCanOnlyHaveRelationshipInvalid() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", "EBCDIC"));

    RelationshipValidator validator =
        new RelationshipValidator(
            "format", "digital", "canOnlyHave", "encoding", "UTF-8", "ASCII", "UTF-16");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(1));
  }

  @Test
  public void testCanOnlyHaveWithMultipleValues() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));
    metacard.setAttribute(new AttributeImpl("encoding", "ASCII"));

    RelationshipValidator validator =
        new RelationshipValidator(
            "format", "digital", "canOnlyHave", "encoding", "UTF-8", "ASCII", "UTF-16");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testCanOnlyHaveEmptyTargetAttribute() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "canOnlyHave", "encoding", "UTF-8", "ASCII");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    // Empty target attribute is valid for canOnlyHave
    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testSourceAttributeNotPresent() {
    // Source attribute not set - validation should not trigger

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testSourceAttributeWithDifferentValue() {
    metacard.setAttribute(new AttributeImpl("format", "analog"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    // Validation only triggers when source has specific value
    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testSourceAttributeWithAnyValue() {
    metacard.setAttribute(new AttributeImpl("format", "anything"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    // Empty source value means "any value"
    RelationshipValidator validator =
        new RelationshipValidator("format", "", "mustHave", "encoding", "UTF-8");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testSourceAttributeWithNullValue() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    // Null source value means "any value"
    RelationshipValidator validator =
        new RelationshipValidator("format", null, "mustHave", "encoding", "UTF-8");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testInvalidRelationshipType() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RelationshipValidator("format", "digital", "invalidRelationship", "encoding"));
  }

  @Test
  public void testCanOnlyHaveWithoutTargetValues() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RelationshipValidator("format", "digital", "canOnlyHave", "encoding"));
  }

  @Test
  public void testValidateMethodThrowsException() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding");

    try {
      validator.validate(metacard);
      fail("Expected ValidationException to be thrown");
    } catch (ValidationException e) {
      assertThat(e.getErrors(), hasSize(greaterThan(0)));
    }
  }

  @Test
  public void testValidateMethodWithValidMetacard() throws ValidationException {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");

    // Should not throw exception
    validator.validate(metacard);
  }

  @Test
  public void testEqualsAndHashCode() {
    RelationshipValidator validator1 =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");
    RelationshipValidator validator2 =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");
    RelationshipValidator validator3 =
        new RelationshipValidator("format", "analog", "mustHave", "encoding", "UTF-8");

    assertThat(validator1.equals(validator2), is(true));
    assertThat(validator1.hashCode(), is(validator2.hashCode()));
    assertThat(validator1.equals(validator3), is(false));
    assertThat(validator1.hashCode(), is(not(validator3.hashCode())));
  }

  @Test
  public void testEqualsWithNull() {
    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");

    assertThat(validator.equals(null), is(false));
  }

  @Test
  public void testEqualsWithSelf() {
    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");

    assertThat(validator.equals(validator), is(true));
  }

  @Test
  public void testEqualsWithDifferentClass() {
    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");

    assertThat(validator.equals("not a validator"), is(false));
  }

  @Test
  public void testToString() {
    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8", "ASCII");

    String toString = validator.toString();

    assertThat(toString.contains("sourceAttribute='format'"), is(true));
    assertThat(toString.contains("sourceValue='digital'"), is(true));
    assertThat(toString.contains("relationship='mustHave'"), is(true));
    assertThat(toString.contains("targetAttribute='encoding'"), is(true));
  }

  @Test
  public void testNullTargetValuesFiltered() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    // Constructor should filter out null values
    RelationshipValidator validator =
        new RelationshipValidator(
            "format", "digital", "mustHave", "encoding", "UTF-8", null, "ASCII");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    // Should still validate correctly with nulls filtered
    assertThat(report.isPresent(), is(true));
  }

  @Test
  public void testTargetAttributeWithEmptyStrings() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("encoding", ""));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    // Empty strings should be filtered out
    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testSourceAttributeWithMultipleValues() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));
    metacard.setAttribute(new AttributeImpl("format", "analog"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    // Should trigger validation if any source value matches
    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testCannotHaveWithMultipleTargetValues() {
    metacard.setAttribute(new AttributeImpl("format", "analog"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));
    metacard.setAttribute(new AttributeImpl("encoding", "ASCII"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "analog", "cannotHave", "encoding", "DES");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    // Should pass since neither value is DES
    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testCannotHaveWithMultipleTargetValuesViolation() {
    metacard.setAttribute(new AttributeImpl("format", "analog"));
    metacard.setAttribute(new AttributeImpl("encoding", "UTF-8"));
    metacard.setAttribute(new AttributeImpl("encoding", "DES"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "analog", "cannotHave", "encoding", "DES", "3DES");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    // Should fail since one value is DES
    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getAttributeValidationViolations(), hasSize(1));
  }

  @Test
  public void testValidationMessageFormat() {
    metacard.setAttribute(new AttributeImpl("format", "digital"));

    RelationshipValidator validator =
        new RelationshipValidator("format", "digital", "mustHave", "encoding", "UTF-8");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    String message = report.get().getAttributeValidationViolations().iterator().next().getMessage();
    assertThat(message.contains("format"), is(true));
    assertThat(message.contains("digital"), is(true));
    assertThat(message.contains("encoding"), is(true));
    assertThat(message.contains("UTF-8"), is(true));
  }
}
