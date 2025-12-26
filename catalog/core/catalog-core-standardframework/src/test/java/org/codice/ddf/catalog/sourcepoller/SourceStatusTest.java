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
package org.codice.ddf.catalog.sourcepoller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class SourceStatusTest {

  @Test
  void testAvailableEnumValue() {
    assertThat(SourceStatus.AVAILABLE, is(notNullValue()));
    assertThat(SourceStatus.AVAILABLE.name(), is("AVAILABLE"));
  }

  @Test
  void testUnavailableEnumValue() {
    assertThat(SourceStatus.UNAVAILABLE, is(notNullValue()));
    assertThat(SourceStatus.UNAVAILABLE.name(), is("UNAVAILABLE"));
  }

  @Test
  void testExceptionEnumValue() {
    assertThat(SourceStatus.EXCEPTION, is(notNullValue()));
    assertThat(SourceStatus.EXCEPTION.name(), is("EXCEPTION"));
  }

  @Test
  void testTimeoutEnumValue() {
    assertThat(SourceStatus.TIMEOUT, is(notNullValue()));
    assertThat(SourceStatus.TIMEOUT.name(), is("TIMEOUT"));
  }

  @Test
  void testEnumHasFourValues() {
    assertThat(SourceStatus.values().length, is(4));
  }

  @Test
  void testValueOfAvailable() {
    assertThat(SourceStatus.valueOf("AVAILABLE"), is(SourceStatus.AVAILABLE));
  }

  @Test
  void testValueOfUnavailable() {
    assertThat(SourceStatus.valueOf("UNAVAILABLE"), is(SourceStatus.UNAVAILABLE));
  }

  @Test
  void testValueOfException() {
    assertThat(SourceStatus.valueOf("EXCEPTION"), is(SourceStatus.EXCEPTION));
  }

  @Test
  void testValueOfTimeout() {
    assertThat(SourceStatus.valueOf("TIMEOUT"), is(SourceStatus.TIMEOUT));
  }
}
