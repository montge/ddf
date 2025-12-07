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
package org.codice.ddf.rest.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

/** Tests for {@link CatalogServiceException} class. */
public class CatalogServiceExceptionTest {

  @Test
  public void testMessageConstructor() {
    String message = "Test error message";
    CatalogServiceException exception = new CatalogServiceException(message);
    assertThat(exception.getMessage(), is(message));
  }

  @Test
  public void testMessageConstructorWithNullMessage() {
    CatalogServiceException exception = new CatalogServiceException(null);
    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  public void testExtendsException() {
    CatalogServiceException exception = new CatalogServiceException("test");
    assertThat(exception instanceof Exception, is(true));
  }

  @Test
  public void testCanBeThrown() {
    assertThrows(
        CatalogServiceException.class,
        () -> {
          throw new CatalogServiceException("Test exception");
        });
  }

  @Test
  public void testCauseIsNull() {
    CatalogServiceException exception = new CatalogServiceException("test");
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testExceptionIsNotNull() {
    CatalogServiceException exception = new CatalogServiceException("test");
    assertThat(exception, is(notNullValue()));
  }

  @Test
  public void testEmptyMessage() {
    CatalogServiceException exception = new CatalogServiceException("");
    assertThat(exception.getMessage(), is(""));
  }
}
