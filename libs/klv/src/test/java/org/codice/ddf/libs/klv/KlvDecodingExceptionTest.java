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
package org.codice.ddf.libs.klv;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

/** Tests for {@link KlvDecodingException} class. */
public class KlvDecodingExceptionTest {

  @Test
  public void testMessageConstructor() {
    String message = "Invalid KLV data";
    KlvDecodingException exception = new KlvDecodingException(message);
    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testMessageAndCauseConstructor() {
    String message = "Invalid KLV data";
    Throwable cause = new IllegalArgumentException("Bad value");
    KlvDecodingException exception = new KlvDecodingException(message, cause);
    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testExtendsException() {
    KlvDecodingException exception = new KlvDecodingException("test");
    assertThat(exception instanceof Exception, is(true));
  }

  @Test
  public void testCanBeThrown() {
    assertThrows(
        KlvDecodingException.class,
        () -> {
          throw new KlvDecodingException("Test exception");
        });
  }

  @Test
  public void testCanBeThrownWithCause() {
    assertThrows(
        KlvDecodingException.class,
        () -> {
          throw new KlvDecodingException("Test exception", new RuntimeException("cause"));
        });
  }
}
