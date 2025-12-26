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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class WfsInvalidEntityExceptionTest {

  @Test
  void testClassConstructor() {
    WfsInvalidEntityException exception = new WfsInvalidEntityException(String.class);

    assertThat(exception.getMessage(), containsString("entity type not understood"));
    assertThat(exception.getMessage(), containsString("java.lang.String"));
    assertThat(exception.getClazz(), is(sameInstance(String.class)));
  }

  @Test
  void testMessageConstructor() {
    WfsInvalidEntityException exception = new WfsInvalidEntityException("Invalid entity");

    assertThat(exception.getMessage(), is("Invalid entity"));
    assertThat(exception.getClazz(), is(nullValue()));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    WfsInvalidEntityException exception = new WfsInvalidEntityException("Invalid entity", cause);

    assertThat(exception.getMessage(), is("Invalid entity"));
    assertThat(exception.getCause(), is(sameInstance(cause)));
    assertThat(exception.getClazz(), is(nullValue()));
  }

  @Test
  void testGetClazzWithCustomClass() {
    WfsInvalidEntityException exception = new WfsInvalidEntityException(Integer.class);

    assertThat(exception.getClazz(), is(sameInstance(Integer.class)));
    assertThat(exception.getMessage(), containsString("java.lang.Integer"));
  }

  @Test
  void testExtendsWfsException() {
    WfsInvalidEntityException exception = new WfsInvalidEntityException(String.class);

    assertThat(exception, instanceOf(WfsException.class));
  }
}
