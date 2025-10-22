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
package org.codice.ddf.security.oidc.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OidcValidationExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "Test validation error";
    OidcValidationException exception = new OidcValidationException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithMessageAndCause() {
    String message = "Test validation error";
    Throwable cause = new RuntimeException("Root cause");
    OidcValidationException exception = new OidcValidationException(message, cause);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(notNullValue()));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testConstructorWithNullMessage() {
    OidcValidationException exception = new OidcValidationException(null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullCause() {
    String message = "Test validation error";
    OidcValidationException exception = new OidcValidationException(message, null);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new OidcValidationException("Test exception");
    } catch (OidcValidationException e) {
      assertThat(e.getMessage(), is("Test exception"));
    }
  }

  @Test
  public void testExceptionWithCauseCanBeThrown() {
    RuntimeException rootCause = new RuntimeException("Root cause");
    try {
      throw new OidcValidationException("Test exception", rootCause);
    } catch (OidcValidationException e) {
      assertThat(e.getMessage(), is("Test exception"));
      assertThat(e.getCause(), is(rootCause));
    }
  }
}
