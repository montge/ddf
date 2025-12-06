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
package org.codice.ddf.validator.metacard.framecenter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Media;
import ddf.catalog.validation.MetacardValidator;
import ddf.catalog.validation.ReportingMetacardValidator;
import ddf.catalog.validation.ValidationException;
import ddf.catalog.validation.report.MetacardValidationReport;
import java.util.Optional;
import org.codice.ddf.validator.wkt.WktValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link MetacardFrameCenterValidator} class. */
@RunWith(MockitoJUnitRunner.class)
public class MetacardFrameCenterValidatorTest {

  private static final String VALID_WKT = "POINT(0 0)";
  private static final String INVALID_WKT = "INVALID";

  @Mock private WktValidator wktValidator;

  private MetacardFrameCenterValidator validator;

  @Before
  public void setUp() {
    validator = new MetacardFrameCenterValidator(wktValidator);
  }

  @Test
  public void testImplementsMetacardValidator() {
    assertThat(validator instanceof MetacardValidator, is(true));
  }

  @Test
  public void testImplementsReportingMetacardValidator() {
    assertThat(validator instanceof ReportingMetacardValidator, is(true));
  }

  @Test
  public void testValidateMetacardWithValidFrameCenter() {
    when(wktValidator.isValid(VALID_WKT)).thenReturn(true);
    Metacard metacard = createMetacardWithFrameCenter(VALID_WKT);

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithInvalidFrameCenter() {
    when(wktValidator.isValid(INVALID_WKT)).thenReturn(false);
    Metacard metacard = createMetacardWithFrameCenter(INVALID_WKT);

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getMetacardValidationViolations().isEmpty(), is(false));
  }

  @Test
  public void testValidateMetacardWithNullFrameCenter() {
    Metacard metacard = new MetacardImpl();

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateMetacardWithEmptyFrameCenter() {
    Metacard metacard = createMetacardWithFrameCenter("");

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  @Test
  public void testValidateWithValidFrameCenterDoesNotThrow() throws Exception {
    when(wktValidator.isValid(VALID_WKT)).thenReturn(true);
    Metacard metacard = createMetacardWithFrameCenter(VALID_WKT);

    validator.validate(metacard);
  }

  @Test(expected = ValidationException.class)
  public void testValidateWithInvalidFrameCenterThrowsException() throws Exception {
    when(wktValidator.isValid(anyString())).thenReturn(false);
    Metacard metacard = createMetacardWithFrameCenter(INVALID_WKT);

    validator.validate(metacard);
  }

  @Test
  public void testValidatesFrameCenterAttribute() {
    when(wktValidator.isValid(VALID_WKT)).thenReturn(true);
    MetacardImpl metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Media.FRAME_CENTER, VALID_WKT));

    Optional<MetacardValidationReport> report = validator.validateMetacard(metacard);

    assertThat(report.isPresent(), is(false));
  }

  private Metacard createMetacardWithFrameCenter(String frameCenter) {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Media.FRAME_CENTER, frameCenter));
    return metacard;
  }
}
