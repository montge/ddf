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
package ddf.security.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecurityServiceExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "Security service error";
    SecurityServiceException exception = new SecurityServiceException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructorWithNullMessage() {
    SecurityServiceException exception = new SecurityServiceException(null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new SecurityServiceException("Test security service exception");
    } catch (SecurityServiceException e) {
      assertThat(e.getMessage(), is("Test security service exception"));
    }
  }

  @Test
  public void testExceptionInheritance() {
    SecurityServiceException exception = new SecurityServiceException("Test");
    assertThat(exception instanceof Exception, is(true));
  }

  @Test
  public void testExceptionWithEmptyMessage() {
    SecurityServiceException exception = new SecurityServiceException("");
    assertThat(exception.getMessage(), is(""));
  }
}
