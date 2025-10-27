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
package org.codice.ddf.spatial.ogc.csw.catalog.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/** Unit tests for {@link CswException}. */
public class CswExceptionTest {

  @Test
  public void testCswExceptionWithMessage() {
    CswException exception = new CswException("Test CSW exception");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test CSW exception"));
  }

  @Test
  public void testCswExceptionWithMessageAndCause() {
    Throwable cause = new RuntimeException("Cause exception");
    CswException exception = new CswException("Test CSW exception", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test CSW exception"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testCswExceptionWithNullMessage() {
    CswException exception = new CswException((String) null);
    assertThat(exception, is(notNullValue()));
  }

  @Test
  public void testCswExceptionWithEmptyMessage() {
    CswException exception = new CswException("");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(""));
  }

  @Test
  public void testCswExceptionWithNullCause() {
    CswException exception = new CswException("Test message", null);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test message"));
    // When null is passed as cause, getCause() returns null
  }

  @Test
  public void testCswExceptionChaining() {
    Throwable rootCause = new IllegalArgumentException("Root cause");
    Throwable intermediateCause = new IllegalStateException("Intermediate cause", rootCause);
    CswException exception = new CswException("Top level exception", intermediateCause);

    assertThat(exception.getCause(), is(intermediateCause));
    assertThat(exception.getCause().getCause(), is(rootCause));
  }
}
