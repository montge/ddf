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
package org.codice.ddf.libs.klv.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

/** Tests for {@link Klv} class enums. */
public class KlvTest {

  @Test
  public void testLengthEncodingValues() {
    assertThat(Klv.LengthEncoding.values().length, is(4));
    assertThat(
        Klv.LengthEncoding.values(),
        arrayContainingInAnyOrder(
            Klv.LengthEncoding.ONE_BYTE,
            Klv.LengthEncoding.TWO_BYTES,
            Klv.LengthEncoding.FOUR_BYTES,
            Klv.LengthEncoding.BER));
  }

  @Test
  public void testLengthEncodingOneByteValue() {
    assertThat(Klv.LengthEncoding.ONE_BYTE.value(), is(1));
  }

  @Test
  public void testLengthEncodingTwoBytesValue() {
    assertThat(Klv.LengthEncoding.TWO_BYTES.value(), is(2));
  }

  @Test
  public void testLengthEncodingFourBytesValue() {
    assertThat(Klv.LengthEncoding.FOUR_BYTES.value(), is(4));
  }

  @Test
  public void testLengthEncodingBerValue() {
    assertThat(Klv.LengthEncoding.BER.value(), is(5));
  }

  @Test
  public void testKeyLengthValues() {
    assertThat(Klv.KeyLength.values().length, is(4));
    assertThat(
        Klv.KeyLength.values(),
        arrayContainingInAnyOrder(
            Klv.KeyLength.ONE_BYTE,
            Klv.KeyLength.TWO_BYTES,
            Klv.KeyLength.FOUR_BYTES,
            Klv.KeyLength.SIXTEEN_BYTES));
  }

  @Test
  public void testKeyLengthOneByteValue() {
    assertThat(Klv.KeyLength.ONE_BYTE.value(), is(1));
  }

  @Test
  public void testKeyLengthTwoBytesValue() {
    assertThat(Klv.KeyLength.TWO_BYTES.value(), is(2));
  }

  @Test
  public void testKeyLengthFourBytesValue() {
    assertThat(Klv.KeyLength.FOUR_BYTES.value(), is(4));
  }

  @Test
  public void testKeyLengthSixteenBytesValue() {
    assertThat(Klv.KeyLength.SIXTEEN_BYTES.value(), is(16));
  }

  @Test
  public void testLengthEncodingValueOf() {
    assertThat(Klv.LengthEncoding.valueOf("ONE_BYTE"), is(Klv.LengthEncoding.ONE_BYTE));
    assertThat(Klv.LengthEncoding.valueOf("TWO_BYTES"), is(Klv.LengthEncoding.TWO_BYTES));
    assertThat(Klv.LengthEncoding.valueOf("FOUR_BYTES"), is(Klv.LengthEncoding.FOUR_BYTES));
    assertThat(Klv.LengthEncoding.valueOf("BER"), is(Klv.LengthEncoding.BER));
  }

  @Test
  public void testKeyLengthValueOf() {
    assertThat(Klv.KeyLength.valueOf("ONE_BYTE"), is(Klv.KeyLength.ONE_BYTE));
    assertThat(Klv.KeyLength.valueOf("TWO_BYTES"), is(Klv.KeyLength.TWO_BYTES));
    assertThat(Klv.KeyLength.valueOf("FOUR_BYTES"), is(Klv.KeyLength.FOUR_BYTES));
    assertThat(Klv.KeyLength.valueOf("SIXTEEN_BYTES"), is(Klv.KeyLength.SIXTEEN_BYTES));
  }
}
