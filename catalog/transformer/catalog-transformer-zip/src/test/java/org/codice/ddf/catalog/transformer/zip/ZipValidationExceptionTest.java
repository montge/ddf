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
package org.codice.ddf.catalog.transformer.zip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class ZipValidationExceptionTest {

  private static final String TEST_MESSAGE = "Zip validation failed";

  @Test
  void testMessageConstructor() {
    ZipValidationException exception = new ZipValidationException(TEST_MESSAGE);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testExceptionIsCheckedException() {
    ZipValidationException exception = new ZipValidationException(TEST_MESSAGE);

    assertThat(Exception.class.isAssignableFrom(exception.getClass()), is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  @Test
  void testNullMessage() {
    ZipValidationException exception = new ZipValidationException(null);

    assertThat(exception.getMessage(), is(nullValue()));
  }
}
