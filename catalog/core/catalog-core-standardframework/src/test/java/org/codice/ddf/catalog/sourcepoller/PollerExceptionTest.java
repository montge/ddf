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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PollerExceptionTest {

  @Test
  void testConstructorWithSingleCause() {
    Throwable cause = new RuntimeException("source1 failed");
    Map<String, Throwable> causes = Collections.singletonMap("source1", cause);

    PollerException exception = new PollerException(causes);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), containsString("source1"));
    assertThat(exception.getCauses().get("source1"), is(sameInstance(cause)));
  }

  @Test
  void testConstructorWithMultipleCauses() {
    Map<String, Throwable> causes = new HashMap<>();
    causes.put("source1", new RuntimeException("source1 failed"));
    causes.put("source2", new RuntimeException("source2 failed"));

    PollerException exception = new PollerException(causes);

    assertThat(exception.getMessage(), containsString("source1"));
    assertThat(exception.getMessage(), containsString("source2"));
    assertThat(exception.getCauses().size(), is(2));
  }

  @Test
  void testGetCausesReturnsSameMap() {
    Throwable cause = new RuntimeException("failed");
    Map<String, Throwable> causes = Collections.singletonMap("key", cause);

    PollerException exception = new PollerException(causes);

    assertThat(exception.getCauses(), is(causes));
  }

  @Test
  void testConstructorWithNullMapThrowsException() {
    assertThrows(NullPointerException.class, () -> new PollerException(null));
  }

  @Test
  void testConstructorWithEmptyMapThrowsException() {
    Map<String, Throwable> emptyCauses = Collections.emptyMap();

    assertThrows(IllegalArgumentException.class, () -> new PollerException(emptyCauses));
  }

  @Test
  void testExceptionIsCheckedException() {
    Map<String, Throwable> causes = Collections.singletonMap("key", new RuntimeException());
    PollerException exception = new PollerException(causes);

    assertThat(Exception.class.isAssignableFrom(exception.getClass()), is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
