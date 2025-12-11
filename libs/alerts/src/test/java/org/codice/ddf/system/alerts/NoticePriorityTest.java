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
package org.codice.ddf.system.alerts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

/** Tests for {@link NoticePriority} enum. */
public class NoticePriorityTest {

  @Test
  public void testValuesContainsFourPriorities() {
    assertThat(NoticePriority.values().length, is(4));
  }

  @Test
  public void testValuesContainsAllPriorities() {
    assertThat(
        NoticePriority.values(),
        arrayContainingInAnyOrder(
            NoticePriority.CRITICAL,
            NoticePriority.IMPORTANT,
            NoticePriority.NORMAL,
            NoticePriority.LOW));
  }

  @Test
  public void testCriticalValue() {
    assertThat(NoticePriority.CRITICAL.value(), is(4));
  }

  @Test
  public void testImportantValue() {
    assertThat(NoticePriority.IMPORTANT.value(), is(3));
  }

  @Test
  public void testNormalValue() {
    assertThat(NoticePriority.NORMAL.value(), is(2));
  }

  @Test
  public void testLowValue() {
    assertThat(NoticePriority.LOW.value(), is(1));
  }

  @Test
  public void testValueOfCritical() {
    assertThat(NoticePriority.valueOf(4), is(NoticePriority.CRITICAL));
  }

  @Test
  public void testValueOfImportant() {
    assertThat(NoticePriority.valueOf(3), is(NoticePriority.IMPORTANT));
  }

  @Test
  public void testValueOfNormal() {
    assertThat(NoticePriority.valueOf(2), is(NoticePriority.NORMAL));
  }

  @Test
  public void testValueOfLow() {
    assertThat(NoticePriority.valueOf(1), is(NoticePriority.LOW));
  }

  @Test
  public void testValueOfInvalidReturnsNull() {
    assertThat(NoticePriority.valueOf(0), is(nullValue()));
  }

  @Test
  public void testValueOfNegativeReturnsNull() {
    assertThat(NoticePriority.valueOf(-1), is(nullValue()));
  }

  @Test
  public void testValueOfTooHighReturnsNull() {
    assertThat(NoticePriority.valueOf(5), is(nullValue()));
  }

  @Test
  public void testValueOfByName() {
    assertThat(NoticePriority.valueOf("CRITICAL"), is(NoticePriority.CRITICAL));
    assertThat(NoticePriority.valueOf("IMPORTANT"), is(NoticePriority.IMPORTANT));
    assertThat(NoticePriority.valueOf("NORMAL"), is(NoticePriority.NORMAL));
    assertThat(NoticePriority.valueOf("LOW"), is(NoticePriority.LOW));
  }

  @Test
  public void testPriorityOrdering() {
    assertThat(NoticePriority.CRITICAL.value() > NoticePriority.IMPORTANT.value(), is(true));
    assertThat(NoticePriority.IMPORTANT.value() > NoticePriority.NORMAL.value(), is(true));
    assertThat(NoticePriority.NORMAL.value() > NoticePriority.LOW.value(), is(true));
  }
}
