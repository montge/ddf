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
package org.codice.ddf.libs.geo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import java.lang.reflect.Field;
import org.junit.Test;

/** Tests for {@link GeoFormatException} class. */
public class GeoFormatExceptionTest {

  @Test
  public void testDefaultConstructor() {
    GeoFormatException exception = new GeoFormatException();
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testMessageConstructor() {
    String message = "Invalid coordinate format";
    GeoFormatException exception = new GeoFormatException(message);
    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  public void testMessageAndCauseConstructor() {
    String message = "Invalid coordinate format";
    Throwable cause = new IllegalArgumentException("Bad value");
    GeoFormatException exception = new GeoFormatException(message, cause);
    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testCauseConstructor() {
    Throwable cause = new IllegalArgumentException("Bad value");
    GeoFormatException exception = new GeoFormatException(cause);
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testExtendsException() {
    GeoFormatException exception = new GeoFormatException();
    assertThat(exception instanceof Exception, is(true));
  }

  @Test
  public void testSerialVersionUID() throws NoSuchFieldException {
    Field field = GeoFormatException.class.getDeclaredField("serialVersionUID");
    assertThat(field, is(notNullValue()));
  }

  @Test
  public void testCanBeThrown() {
    assertThrows(
        GeoFormatException.class,
        () -> {
          throw new GeoFormatException("Test exception");
        });
  }
}
