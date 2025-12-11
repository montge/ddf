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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link ValidationExceptionImpl} class.
 *
 * <p>Tests all constructors, error/warning handling, and exception messages.
 */
@ExtendWith(MockitoExtension.class)
public class ValidationExceptionImplTest {

  private static final String SUMMARY_MESSAGE = "Validation failed";
  private static final String ERROR_1 = "Missing required field: title";
  private static final String ERROR_2 = "Invalid date format";
  private static final String WARNING_1 = "Field 'description' is recommended";
  private static final String WARNING_2 = "Thumbnail size exceeds recommendation";

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests default constructor.
   *
   * <p>Verifies no-arg constructor.
   */
  @Test
  public void testDefaultConstructor() {
    ValidationExceptionImpl exception = new ValidationExceptionImpl();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getErrors(), is(nullValue()));
    assertThat(exception.getWarnings(), is(nullValue()));
  }

  /**
   * Tests constructor with summary message only.
   *
   * <p>Verifies message-only constructor.
   */
  @Test
  public void testConstructorWithSummaryMessage() {
    ValidationExceptionImpl exception = new ValidationExceptionImpl(SUMMARY_MESSAGE);

    assertThat(exception.getMessage(), is(equalTo(SUMMARY_MESSAGE)));
    assertThat(exception.getErrors(), is(nullValue()));
    assertThat(exception.getWarnings(), is(nullValue()));
  }

  /**
   * Tests constructor with summary, errors, and warnings.
   *
   * <p>Verifies full message constructor.
   */
  @Test
  public void testConstructorWithSummaryErrorsAndWarnings() {
    List<String> errors = Arrays.asList(ERROR_1, ERROR_2);
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);

    ValidationExceptionImpl exception =
        new ValidationExceptionImpl(SUMMARY_MESSAGE, errors, warnings);

    assertThat(exception.getMessage(), is(equalTo(SUMMARY_MESSAGE)));
    assertThat(exception.getErrors(), hasSize(2));
    assertThat(exception.getErrors(), containsInAnyOrder(ERROR_1, ERROR_2));
    assertThat(exception.getWarnings(), hasSize(2));
    assertThat(exception.getWarnings(), containsInAnyOrder(WARNING_1, WARNING_2));
  }

  /**
   * Tests constructor with cause.
   *
   * <p>Verifies cause-only constructor.
   */
  @Test
  public void testConstructorWithCause() {
    IOException cause = new IOException("Network error");
    ValidationExceptionImpl exception = new ValidationExceptionImpl(cause);

    assertThat(exception.getCause(), is(equalTo(cause)));
    assertThat(exception.getErrors(), is(nullValue()));
    assertThat(exception.getWarnings(), is(nullValue()));
  }

  /**
   * Tests constructor with cause, errors, and warnings.
   *
   * <p>Verifies cause with messages constructor.
   */
  @Test
  public void testConstructorWithCauseErrorsAndWarnings() {
    IOException cause = new IOException("Network error");
    List<String> errors = Arrays.asList(ERROR_1);
    List<String> warnings = Arrays.asList(WARNING_1);

    ValidationExceptionImpl exception = new ValidationExceptionImpl(cause, errors, warnings);

    assertThat(exception.getCause(), is(equalTo(cause)));
    assertThat(exception.getErrors(), hasSize(1));
    assertThat(exception.getWarnings(), hasSize(1));
  }

  /**
   * Tests constructor with summary message and cause.
   *
   * <p>Verifies message and cause constructor.
   */
  @Test
  public void testConstructorWithSummaryAndCause() {
    IOException cause = new IOException("Network error");
    ValidationExceptionImpl exception = new ValidationExceptionImpl(SUMMARY_MESSAGE, cause);

    assertThat(exception.getMessage(), is(equalTo(SUMMARY_MESSAGE)));
    assertThat(exception.getCause(), is(equalTo(cause)));
  }

  /**
   * Tests full constructor with all parameters.
   *
   * <p>Verifies complete constructor.
   */
  @Test
  public void testFullConstructor() {
    IOException cause = new IOException("Network error");
    List<String> errors = Arrays.asList(ERROR_1, ERROR_2);
    List<String> warnings = Arrays.asList(WARNING_1);

    ValidationExceptionImpl exception =
        new ValidationExceptionImpl(SUMMARY_MESSAGE, cause, errors, warnings);

    assertThat(exception.getMessage(), is(equalTo(SUMMARY_MESSAGE)));
    assertThat(exception.getCause(), is(equalTo(cause)));
    assertThat(exception.getErrors(), hasSize(2));
    assertThat(exception.getWarnings(), hasSize(1));
  }

  /**
   * Tests constructor with null errors list.
   *
   * <p>Verifies null errors are handled.
   */
  @Test
  public void testConstructorWithNullErrors() {
    List<String> warnings = Arrays.asList(WARNING_1);

    ValidationExceptionImpl exception =
        new ValidationExceptionImpl(SUMMARY_MESSAGE, null, warnings);

    assertThat(exception.getErrors(), is(nullValue()));
    assertThat(exception.getWarnings(), hasSize(1));
  }

  /**
   * Tests constructor with null warnings list.
   *
   * <p>Verifies null warnings are handled.
   */
  @Test
  public void testConstructorWithNullWarnings() {
    List<String> errors = Arrays.asList(ERROR_1);

    ValidationExceptionImpl exception = new ValidationExceptionImpl(SUMMARY_MESSAGE, errors, null);

    assertThat(exception.getErrors(), hasSize(1));
    assertThat(exception.getWarnings(), is(nullValue()));
  }

  /**
   * Tests constructor with empty errors list.
   *
   * <p>Verifies empty lists result in null.
   */
  @Test
  public void testConstructorWithEmptyErrors() {
    List<String> emptyErrors = new ArrayList<>();
    List<String> warnings = Arrays.asList(WARNING_1);

    ValidationExceptionImpl exception =
        new ValidationExceptionImpl(SUMMARY_MESSAGE, emptyErrors, warnings);

    assertThat(exception.getErrors(), is(nullValue()));
  }

  /**
   * Tests constructor with empty warnings list.
   *
   * <p>Verifies empty warnings result in null.
   */
  @Test
  public void testConstructorWithEmptyWarnings() {
    List<String> errors = Arrays.asList(ERROR_1);
    List<String> emptyWarnings = new ArrayList<>();

    ValidationExceptionImpl exception =
        new ValidationExceptionImpl(SUMMARY_MESSAGE, errors, emptyWarnings);

    assertThat(exception.getWarnings(), is(nullValue()));
  }

  // ====================
  // Setter Tests
  // ====================

  /**
   * Tests setErrors with valid list.
   *
   * <p>Verifies error setting.
   */
  @Test
  public void testSetErrors() {
    ValidationExceptionImpl exception = new ValidationExceptionImpl();
    List<String> errors = Arrays.asList(ERROR_1, ERROR_2);

    exception.setErrors(errors);

    assertThat(exception.getErrors(), hasSize(2));
    assertThat(exception.getErrors(), containsInAnyOrder(ERROR_1, ERROR_2));
  }

  /**
   * Tests setErrors with null list.
   *
   * <p>Verifies null errors are handled.
   */
  @Test
  public void testSetErrorsWithNull() {
    ValidationExceptionImpl exception = new ValidationExceptionImpl();

    exception.setErrors(null);

    assertThat(exception.getErrors(), is(nullValue()));
  }

  /**
   * Tests setErrors with empty list.
   *
   * <p>Verifies empty list results in null.
   */
  @Test
  public void testSetErrorsWithEmptyList() {
    ValidationExceptionImpl exception = new ValidationExceptionImpl();

    exception.setErrors(new ArrayList<>());

    assertThat(exception.getErrors(), is(nullValue()));
  }

  /**
   * Tests setWarnings with valid list.
   *
   * <p>Verifies warning setting.
   */
  @Test
  public void testSetWarnings() {
    ValidationExceptionImpl exception = new ValidationExceptionImpl();
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);

    exception.setWarnings(warnings);

    assertThat(exception.getWarnings(), hasSize(2));
    assertThat(exception.getWarnings(), containsInAnyOrder(WARNING_1, WARNING_2));
  }

  /**
   * Tests setWarnings with null list.
   *
   * <p>Verifies null warnings are handled.
   */
  @Test
  public void testSetWarningsWithNull() {
    ValidationExceptionImpl exception = new ValidationExceptionImpl();

    exception.setWarnings(null);

    assertThat(exception.getWarnings(), is(nullValue()));
  }

  /**
   * Tests setWarnings with empty list.
   *
   * <p>Verifies empty list results in null.
   */
  @Test
  public void testSetWarningsWithEmptyList() {
    ValidationExceptionImpl exception = new ValidationExceptionImpl();

    exception.setWarnings(new ArrayList<>());

    assertThat(exception.getWarnings(), is(nullValue()));
  }

  /**
   * Tests updating errors via setter.
   *
   * <p>Verifies errors can be updated.
   */
  @Test
  public void testUpdatingErrorsViaSetter() {
    List<String> initialErrors = Arrays.asList(ERROR_1);
    ValidationExceptionImpl exception =
        new ValidationExceptionImpl(SUMMARY_MESSAGE, initialErrors, null);

    List<String> newErrors = Arrays.asList(ERROR_1, ERROR_2);
    exception.setErrors(newErrors);

    assertThat(exception.getErrors(), hasSize(2));
    assertThat(exception.getErrors(), containsInAnyOrder(ERROR_1, ERROR_2));
  }

  // ====================
  // Getter Tests
  // ====================

  /**
   * Tests getErrors returns correct errors.
   *
   * <p>Verifies error retrieval.
   */
  @Test
  public void testGetErrors() {
    List<String> errors = Arrays.asList(ERROR_1, ERROR_2);
    ValidationExceptionImpl exception = new ValidationExceptionImpl(SUMMARY_MESSAGE, errors, null);

    List<String> retrievedErrors = exception.getErrors();

    assertThat(retrievedErrors, hasSize(2));
    assertThat(retrievedErrors, containsInAnyOrder(ERROR_1, ERROR_2));
  }

  /**
   * Tests getWarnings returns correct warnings.
   *
   * <p>Verifies warning retrieval.
   */
  @Test
  public void testGetWarnings() {
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);
    ValidationExceptionImpl exception =
        new ValidationExceptionImpl(SUMMARY_MESSAGE, null, warnings);

    List<String> retrievedWarnings = exception.getWarnings();

    assertThat(retrievedWarnings, hasSize(2));
    assertThat(retrievedWarnings, containsInAnyOrder(WARNING_1, WARNING_2));
  }

  /**
   * Tests getErrors returns defensive copy.
   *
   * <p>Verifies that returned list is a copy.
   */
  @Test
  public void testGetErrorsReturnsDefensiveCopy() {
    List<String> errors = new ArrayList<>(Arrays.asList(ERROR_1));
    ValidationExceptionImpl exception = new ValidationExceptionImpl(SUMMARY_MESSAGE, errors, null);

    List<String> retrievedErrors = exception.getErrors();
    errors.add("new error");

    assertThat(retrievedErrors, hasSize(1));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete validation exception scenario.
   *
   * <p>Simulates a real validation failure.
   */
  @Test
  public void testCompleteValidationException() {
    String summary = "Metacard validation failed";
    List<String> errors =
        Arrays.asList(
            "Required field 'title' is missing", "Date format invalid for field 'created'");
    List<String> warnings =
        Arrays.asList("Field 'description' should be provided", "Thumbnail is recommended");

    ValidationExceptionImpl exception = new ValidationExceptionImpl(summary, errors, warnings);

    assertThat(exception.getMessage(), is(equalTo(summary)));
    assertThat(exception.getErrors(), hasSize(2));
    assertThat(exception.getWarnings(), hasSize(2));
  }

  /**
   * Tests validation exception with only errors.
   *
   * <p>Simulates validation failure with no warnings.
   */
  @Test
  public void testValidationExceptionWithOnlyErrors() {
    List<String> errors = Arrays.asList(ERROR_1, ERROR_2);

    ValidationExceptionImpl exception =
        new ValidationExceptionImpl("Validation failed", errors, null);

    assertThat(exception.getErrors(), hasSize(2));
    assertThat(exception.getWarnings(), is(nullValue()));
  }

  /**
   * Tests validation exception with only warnings.
   *
   * <p>Simulates validation with only warnings (unusual case).
   */
  @Test
  public void testValidationExceptionWithOnlyWarnings() {
    List<String> warnings = Arrays.asList(WARNING_1);

    ValidationExceptionImpl exception =
        new ValidationExceptionImpl("Validation completed with warnings", null, warnings);

    assertThat(exception.getErrors(), is(nullValue()));
    assertThat(exception.getWarnings(), hasSize(1));
  }
}
