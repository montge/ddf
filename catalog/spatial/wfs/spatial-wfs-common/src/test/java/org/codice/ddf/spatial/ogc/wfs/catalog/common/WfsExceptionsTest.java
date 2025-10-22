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
package org.codice.ddf.spatial.ogc.wfs.catalog.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/** Unit tests for WFS exception classes. */
public class WfsExceptionsTest {

  @Test
  public void testWfsException() {
    WfsException exception = new WfsException("Test WFS exception");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test WFS exception"));
  }

  @Test
  public void testWfsExceptionWithCause() {
    Throwable cause = new RuntimeException("Cause exception");
    WfsException exception = new WfsException("Test WFS exception", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test WFS exception"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testWfsParseException() {
    WfsParseException exception = new WfsParseException("Parse error");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Parse error"));
  }

  @Test
  public void testWfsParseExceptionWithCause() {
    Throwable cause = new IllegalArgumentException("Invalid XML");
    WfsParseException exception = new WfsParseException("Parse error", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Parse error"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testWfsNullResponseException() {
    WfsNullResponseException exception = new WfsNullResponseException("Null response");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Null response"));
  }

  @Test
  public void testWfsNullResponseExceptionWithCause() {
    Throwable cause = new NullPointerException("Response is null");
    WfsNullResponseException exception = new WfsNullResponseException("Null response", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Null response"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testWfsInvalidEntityException() {
    WfsInvalidEntityException exception = new WfsInvalidEntityException("Invalid entity");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Invalid entity"));
  }

  @Test
  public void testWfsInvalidEntityExceptionWithCause() {
    Throwable cause = new IllegalStateException("Entity validation failed");
    WfsInvalidEntityException exception = new WfsInvalidEntityException("Invalid entity", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Invalid entity"));
    assertThat(exception.getCause(), is(cause));
  }
}
