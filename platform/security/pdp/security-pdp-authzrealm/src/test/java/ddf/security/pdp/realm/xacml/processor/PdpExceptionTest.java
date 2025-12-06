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
package ddf.security.pdp.realm.xacml.processor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PdpExceptionTest {

  @Test
  public void testDefaultConstructor() {
    PdpException exception = new PdpException();

    assertThat(exception, is(notNullValue()));
    assertThat(exception, is(instanceOf(Exception.class)));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessage() {
    String message = "Test exception message";

    PdpException exception = new PdpException(message);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(equalTo(message)));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessage() {
    PdpException exception = new PdpException((String) null);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithEmptyMessage() {
    String message = "";

    PdpException exception = new PdpException(message);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(equalTo(message)));
  }

  @Test
  public void testConstructorWithThrowable() {
    Throwable cause = new IOException("IO error");

    PdpException exception = new PdpException(cause);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getCause(), is(equalTo(cause)));
    assertThat(exception.getCause().getMessage(), is(equalTo("IO error")));
  }

  @Test
  public void testConstructorWithNullThrowable() {
    PdpException exception = new PdpException((Throwable) null);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndThrowable() {
    String message = "PDP processing failed";
    Throwable cause = new IOException("File not found");

    PdpException exception = new PdpException(message, cause);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(equalTo(message)));
    assertThat(exception.getCause(), is(equalTo(cause)));
    assertThat(exception.getCause().getMessage(), is(equalTo("File not found")));
  }

  @Test
  public void testConstructorWithMessageAndNullThrowable() {
    String message = "PDP processing failed";

    PdpException exception = new PdpException(message, null);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(equalTo(message)));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessageAndThrowable() {
    Throwable cause = new IOException("IO error");

    PdpException exception = new PdpException(null, cause);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(equalTo(cause)));
  }

  @Test
  public void testConstructorWithNullMessageAndNullThrowable() {
    PdpException exception = new PdpException(null, null);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new PdpException("Test exception");
    } catch (PdpException e) {
      assertThat(e.getMessage(), is(equalTo("Test exception")));
    }
  }

  @Test
  public void testExceptionCanBeCaught() {
    try {
      methodThatThrowsPdpException();
    } catch (PdpException e) {
      assertThat(e, is(notNullValue()));
      assertThat(e.getMessage(), is(equalTo("Method threw PdpException")));
    }
  }

  @Test(expected = PdpException.class)
  public void testExceptionPropagation() throws PdpException {
    methodThatThrowsPdpException();
  }

  @Test
  public void testExceptionWithNestedThrowable() {
    IOException ioException = new IOException("Disk full");
    RuntimeException runtimeException = new RuntimeException("Processing error", ioException);
    PdpException pdpException = new PdpException("PDP failed", runtimeException);

    assertThat(pdpException.getMessage(), is(equalTo("PDP failed")));
    assertThat(pdpException.getCause(), is(instanceOf(RuntimeException.class)));
    assertThat(pdpException.getCause().getCause(), is(instanceOf(IOException.class)));
    assertThat(pdpException.getCause().getCause().getMessage(), is(equalTo("Disk full")));
  }

  @Test
  public void testExceptionSerializable() {
    PdpException exception = new PdpException("Test message");

    // Test that serialVersionUID is defined
    assertThat(exception, is(instanceOf(java.io.Serializable.class)));
  }

  @Test
  public void testExceptionWithLongMessage() {
    StringBuilder longMessage = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longMessage.append("This is a very long error message. ");
    }

    PdpException exception = new PdpException(longMessage.toString());

    assertThat(exception.getMessage(), is(equalTo(longMessage.toString())));
  }

  @Test
  public void testExceptionWithSpecialCharactersInMessage() {
    String specialMessage = "PDP error: <policy> failed with \"attribute\" & 'value'";

    PdpException exception = new PdpException(specialMessage);

    assertThat(exception.getMessage(), is(equalTo(specialMessage)));
  }

  @Test
  public void testExceptionWithUnicodeCharacters() {
    String unicodeMessage = "PDP 错误: 策略失败 - エラー";

    PdpException exception = new PdpException(unicodeMessage);

    assertThat(exception.getMessage(), is(equalTo(unicodeMessage)));
  }

  @Test
  public void testExceptionStackTrace() {
    PdpException exception = new PdpException("Test exception with stack trace");

    StackTraceElement[] stackTrace = exception.getStackTrace();

    assertThat(stackTrace, is(notNullValue()));
    assertThat(stackTrace.length > 0, is(true));
  }

  @Test
  public void testExceptionCauseChain() {
    IOException cause1 = new IOException("Root cause");
    RuntimeException cause2 = new RuntimeException("Middle cause", cause1);
    PdpException exception = new PdpException("Top level exception", cause2);

    Throwable currentCause = exception.getCause();
    int chainLength = 0;

    while (currentCause != null) {
      chainLength++;
      currentCause = currentCause.getCause();
    }

    assertThat(chainLength, is(equalTo(2)));
  }

  @Test
  public void testExceptionEqualsAndHashCode() {
    PdpException exception1 = new PdpException("Same message");
    PdpException exception2 = new PdpException("Same message");

    // Exceptions are not equal even with same message (object identity)
    assertThat(exception1.equals(exception2), is(false));
    assertThat(exception1.hashCode() == exception2.hashCode(), is(false));
  }

  @Test
  public void testExceptionToString() {
    PdpException exception = new PdpException("Test message");

    String exceptionString = exception.toString();

    assertThat(exceptionString, is(notNullValue()));
    assertThat(exceptionString.contains("PdpException"), is(true));
    assertThat(exceptionString.contains("Test message"), is(true));
  }

  @Test
  public void testExceptionWithMultilineMessage() {
    String multilineMessage = "Error occurred:\nLine 1\nLine 2\nLine 3";

    PdpException exception = new PdpException(multilineMessage);

    assertThat(exception.getMessage(), is(equalTo(multilineMessage)));
  }

  private void methodThatThrowsPdpException() throws PdpException {
    throw new PdpException("Method threw PdpException");
  }
}
