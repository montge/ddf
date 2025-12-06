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
package ddf.catalog.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/** Tests for {@link ValidationException} abstract class. */
public class ValidationExceptionTest {

  private static final String TEST_MESSAGE = "Validation failed";
  private static final String ERROR_1 = "Error: Invalid field value";
  private static final String ERROR_2 = "Error: Missing required field";
  private static final String WARNING_1 = "Warning: Deprecated field usage";
  private static final String WARNING_2 = "Warning: Unusual value detected";

  private Exception testCause;

  @Before
  public void setUp() {
    testCause = new RuntimeException("Root cause");
  }

  @Test
  public void testNoArgConstructor() {
    TestValidationException exception = new TestValidationException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testMessageConstructor() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(equalTo(TEST_MESSAGE)));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testCauseConstructor() {
    TestValidationException exception = new TestValidationException(testCause);

    assertThat(exception.getCause(), is(equalTo(testCause)));
    assertThat(exception.getMessage(), is(equalTo(testCause.toString())));
  }

  @Test
  public void testMessageAndCauseConstructor() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE, testCause);

    assertThat(exception.getMessage(), is(equalTo(TEST_MESSAGE)));
    assertThat(exception.getCause(), is(equalTo(testCause)));
  }

  @Test
  public void testGetErrorsWithNoErrors() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setErrors(null);

    assertThat(exception.getErrors(), is(nullValue()));
  }

  @Test
  public void testGetErrorsWithEmptyList() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setErrors(Collections.emptyList());

    assertThat(exception.getErrors(), is(notNullValue()));
    assertThat(exception.getErrors().isEmpty(), is(true));
  }

  @Test
  public void testGetErrorsWithSingleError() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    List<String> errors = Arrays.asList(ERROR_1);
    exception.setErrors(errors);

    assertThat(exception.getErrors(), is(equalTo(errors)));
    assertThat(exception.getErrors().size(), is(1));
    assertThat(exception.getErrors().get(0), is(equalTo(ERROR_1)));
  }

  @Test
  public void testGetErrorsWithMultipleErrors() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    List<String> errors = Arrays.asList(ERROR_1, ERROR_2);
    exception.setErrors(errors);

    assertThat(exception.getErrors(), is(equalTo(errors)));
    assertThat(exception.getErrors().size(), is(2));
  }

  @Test
  public void testGetWarningsWithNoWarnings() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setWarnings(null);

    assertThat(exception.getWarnings(), is(nullValue()));
  }

  @Test
  public void testGetWarningsWithEmptyList() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setWarnings(Collections.emptyList());

    assertThat(exception.getWarnings(), is(notNullValue()));
    assertThat(exception.getWarnings().isEmpty(), is(true));
  }

  @Test
  public void testGetWarningsWithSingleWarning() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    List<String> warnings = Arrays.asList(WARNING_1);
    exception.setWarnings(warnings);

    assertThat(exception.getWarnings(), is(equalTo(warnings)));
    assertThat(exception.getWarnings().size(), is(1));
    assertThat(exception.getWarnings().get(0), is(equalTo(WARNING_1)));
  }

  @Test
  public void testGetWarningsWithMultipleWarnings() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);
    exception.setWarnings(warnings);

    assertThat(exception.getWarnings(), is(equalTo(warnings)));
    assertThat(exception.getWarnings().size(), is(2));
  }

  @Test
  public void testToStringWithNoErrorsOrWarnings() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);

    String result = exception.toString();

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString(TEST_MESSAGE));
  }

  @Test
  public void testToStringWithErrorsOnly() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setErrors(Arrays.asList(ERROR_1, ERROR_2));

    String result = exception.toString();

    assertThat(result, containsString(TEST_MESSAGE));
    assertThat(result, containsString(":ERRORS"));
    assertThat(result, containsString(ERROR_1));
    assertThat(result, containsString(ERROR_2));
  }

  @Test
  public void testToStringWithWarningsOnly() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setWarnings(Arrays.asList(WARNING_1, WARNING_2));

    String result = exception.toString();

    assertThat(result, containsString(TEST_MESSAGE));
    assertThat(result, containsString(":WARNINGS"));
    assertThat(result, containsString(WARNING_1));
    assertThat(result, containsString(WARNING_2));
  }

  @Test
  public void testToStringWithErrorsAndWarnings() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setErrors(Arrays.asList(ERROR_1));
    exception.setWarnings(Arrays.asList(WARNING_1));

    String result = exception.toString();

    assertThat(result, containsString(TEST_MESSAGE));
    assertThat(result, containsString(":ERRORS"));
    assertThat(result, containsString(ERROR_1));
    assertThat(result, containsString(":WARNINGS"));
    assertThat(result, containsString(WARNING_1));
  }

  @Test
  public void testToStringWithEmptyErrorsList() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setErrors(Collections.emptyList());

    String result = exception.toString();

    assertThat(result, containsString(TEST_MESSAGE));
  }

  @Test
  public void testToStringWithEmptyWarningsList() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);
    exception.setWarnings(Collections.emptyList());

    String result = exception.toString();

    assertThat(result, containsString(TEST_MESSAGE));
  }

  @Test
  public void testSerialization() throws Exception {
    TestValidationException original = new TestValidationException(TEST_MESSAGE, testCause);
    original.setErrors(Arrays.asList(ERROR_1, ERROR_2));
    original.setWarnings(Arrays.asList(WARNING_1, WARNING_2));

    // Serialize
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(original);
    oos.close();

    // Deserialize
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);
    TestValidationException deserialized = (TestValidationException) ois.readObject();
    ois.close();

    // Verify
    assertThat(deserialized.getMessage(), is(equalTo(original.getMessage())));
    assertThat(deserialized.getErrors(), is(equalTo(original.getErrors())));
    assertThat(deserialized.getWarnings(), is(equalTo(original.getWarnings())));
  }

  @Test
  public void testExceptionIsSerializable() {
    TestValidationException exception = new TestValidationException(TEST_MESSAGE);

    assertThat(exception instanceof java.io.Serializable, is(true));
  }

  /**
   * Concrete test implementation of the abstract ValidationException class for testing purposes.
   */
  private static class TestValidationException extends ValidationException {

    private static final long serialVersionUID = 1L;

    private List<String> errors;
    private List<String> warnings;

    public TestValidationException() {
      super();
    }

    public TestValidationException(String summaryMessage) {
      super(summaryMessage);
    }

    public TestValidationException(Throwable cause) {
      super(cause);
    }

    public TestValidationException(String summaryMessage, Throwable cause) {
      super(summaryMessage, cause);
    }

    @Override
    public List<String> getErrors() {
      return errors;
    }

    public void setErrors(List<String> errors) {
      this.errors = errors;
    }

    @Override
    public List<String> getWarnings() {
      return warnings;
    }

    public void setWarnings(List<String> warnings) {
      this.warnings = warnings;
    }
  }
}
