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
package org.codice.ddf.platform.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;
import org.junit.Test;

public class DateUtilsTest {

  @Test
  public void testCopyWithNullDate() {
    Date result = DateUtils.copy(null);
    assertThat(result, nullValue());
  }

  @Test
  public void testCopyWithValidDate() {
    Date original = new Date(1234567890000L);
    Date copy = DateUtils.copy(original);

    assertThat(copy, notNullValue());
    assertThat(copy.getTime(), is(original.getTime()));
  }

  @Test
  public void testCopyCreatesNewInstance() {
    Date original = new Date(1234567890000L);
    Date copy = DateUtils.copy(original);

    assertThat(copy, not(original));
    assertThat(System.identityHashCode(copy), not(System.identityHashCode(original)));
  }

  @Test
  public void testCopyIsIndependent() {
    Date original = new Date(1234567890000L);
    Date copy = DateUtils.copy(original);

    // Modify the original
    original.setTime(9876543210000L);

    // Copy should remain unchanged
    assertThat(copy.getTime(), is(1234567890000L));
    assertThat(original.getTime(), is(9876543210000L));
  }

  @Test
  public void testCopyWithCurrentTime() {
    Date original = new Date();
    Date copy = DateUtils.copy(original);

    assertThat(copy, notNullValue());
    assertThat(copy.getTime(), is(original.getTime()));
  }

  @Test
  public void testCopyWithZeroTime() {
    Date original = new Date(0L);
    Date copy = DateUtils.copy(original);

    assertThat(copy, notNullValue());
    assertThat(copy.getTime(), is(0L));
  }

  @Test
  public void testCopyWithNegativeTime() {
    Date original = new Date(-1234567890000L);
    Date copy = DateUtils.copy(original);

    assertThat(copy, notNullValue());
    assertThat(copy.getTime(), is(-1234567890000L));
  }

  @Test
  public void testCopyWithMaxValue() {
    Date original = new Date(Long.MAX_VALUE);
    Date copy = DateUtils.copy(original);

    assertThat(copy, notNullValue());
    assertThat(copy.getTime(), is(Long.MAX_VALUE));
  }

  @Test
  public void testCopyWithMinValue() {
    Date original = new Date(Long.MIN_VALUE);
    Date copy = DateUtils.copy(original);

    assertThat(copy, notNullValue());
    assertThat(copy.getTime(), is(Long.MIN_VALUE));
  }

  @Test
  public void testMultipleCopiesAreIndependent() {
    Date original = new Date(1234567890000L);
    Date copy1 = DateUtils.copy(original);
    Date copy2 = DateUtils.copy(original);

    assertThat(copy1, not(copy2));
    assertThat(copy1.getTime(), is(copy2.getTime()));

    copy1.setTime(5000L);
    assertThat(copy2.getTime(), is(1234567890000L));
    assertThat(original.getTime(), is(1234567890000L));
  }
}
