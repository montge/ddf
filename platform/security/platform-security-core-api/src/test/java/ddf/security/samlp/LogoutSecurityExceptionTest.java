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
public class LogoutSecurityExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "Logout security error";
    LogoutSecurityException exception = new LogoutSecurityException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessage() {
    LogoutSecurityException exception = new LogoutSecurityException((String) null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new LogoutSecurityException("Test logout security exception");
    } catch (LogoutSecurityException e) {
      assertThat(e.getMessage(), is("Test logout security exception"));
    }
  }

  @Test
  public void testExceptionInheritance() {
    LogoutSecurityException exception = new LogoutSecurityException("Test");
    assertThat(exception instanceof Exception, is(true));
  }

  @Test
  public void testExceptionWithEmptyMessage() {
    LogoutSecurityException exception = new LogoutSecurityException("");
    assertThat(exception.getMessage(), is(""));
  }
}
