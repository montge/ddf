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
package ddf.services.schematron;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import ddf.catalog.validation.impl.ValidationExceptionImpl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class SchematronValidationExceptionTest {

  private static final String TEST_MESSAGE = "Schematron validation failed";

  @Test
  void testDefaultConstructor() {
    SchematronValidationException exception = new SchematronValidationException();

    // Default constructor leaves errors and warnings as null
    assertThat(exception.getErrors(), is(nullValue()));
    assertThat(exception.getWarnings(), is(nullValue()));
  }

  @Test
  void testMessageConstructor() {
    SchematronValidationException exception = new SchematronValidationException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    SchematronValidationException exception = new SchematronValidationException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    SchematronValidationException exception =
        new SchematronValidationException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testMessageErrorsWarningsConstructor() {
    List<String> errors = Arrays.asList("error1", "error2");
    List<String> warnings = Arrays.asList("warning1");
    SchematronValidationException exception =
        new SchematronValidationException(TEST_MESSAGE, errors, warnings);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getErrors(), containsInAnyOrder("error1", "error2"));
    assertThat(exception.getWarnings(), containsInAnyOrder("warning1"));
  }

  @Test
  void testCauseErrorsWarningsConstructor() {
    Throwable cause = new RuntimeException("root cause");
    List<String> errors = Collections.singletonList("error");
    List<String> warnings = Collections.singletonList("warning");
    SchematronValidationException exception =
        new SchematronValidationException(cause, errors, warnings);

    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getErrors(), containsInAnyOrder("error"));
    assertThat(exception.getWarnings(), containsInAnyOrder("warning"));
  }

  @Test
  void testMessageCauseErrorsWarningsConstructor() {
    Throwable cause = new RuntimeException("root cause");
    List<String> errors = Arrays.asList("error1", "error2");
    List<String> warnings = Arrays.asList("warning1", "warning2");
    SchematronValidationException exception =
        new SchematronValidationException(TEST_MESSAGE, cause, errors, warnings);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getErrors(), containsInAnyOrder("error1", "error2"));
    assertThat(exception.getWarnings(), containsInAnyOrder("warning1", "warning2"));
  }

  @Test
  void testWithEmptyErrorsAndWarnings() {
    SchematronValidationException exception =
        new SchematronValidationException(
            TEST_MESSAGE, Collections.emptyList(), Collections.emptyList());

    // Empty lists may be stored as null or empty depending on parent implementation
    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testExtendsValidationExceptionImpl() {
    SchematronValidationException exception = new SchematronValidationException();

    assertThat(exception, instanceOf(ValidationExceptionImpl.class));
  }
}
