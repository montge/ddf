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
package ddf.security.samlp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SignatureExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "Signature validation failed";
    SignatureException exception = new SignatureException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessage() {
    SignatureException exception = new SignatureException((String) null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new SignatureException("Test signature exception");
    } catch (SignatureException e) {
      assertThat(e.getMessage(), is("Test signature exception"));
    }
  }

  @Test
  public void testExceptionInheritance() {
    SignatureException exception = new SignatureException("Test");
    assertThat(exception instanceof Exception, is(true));
  }

  @Test
  public void testExceptionWithEmptyMessage() {
    SignatureException exception = new SignatureException("");
    assertThat(exception.getMessage(), is(""));
  }

  @Test
  public void testDefaultConstructor() {
    SignatureException exception = new SignatureException();

    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndThrowable() {
    String message = "Signature error occurred";
    Throwable cause = new RuntimeException("Root cause");
    SignatureException exception = new SignatureException(message, cause);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testConstructorWithThrowable() {
    Throwable cause = new RuntimeException("Root cause");
    SignatureException exception = new SignatureException(cause);

    assertThat(exception.getCause(), is(cause));
    assertThat(exception.getMessage(), is("java.lang.RuntimeException: Root cause"));
  }

  @Test
  public void testConstructorWithNullThrowable() {
    SignatureException exception = new SignatureException((Throwable) null);

    assertThat(exception.getCause(), is(nullValue()));
    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndNullThrowable() {
    String message = "Error message";
    SignatureException exception = new SignatureException(message, null);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }
}
