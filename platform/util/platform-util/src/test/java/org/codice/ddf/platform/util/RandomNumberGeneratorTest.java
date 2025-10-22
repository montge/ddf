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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class RandomNumberGeneratorTest {

  @Test
  public void testCreate() {
    SecureRandom random = RandomNumberGenerator.create();
    assertThat(random, notNullValue());
  }

  @Test
  public void testCreateSeed() {
    byte[] seed = RandomNumberGenerator.createSeed();
    assertThat(seed, notNullValue());
    assertThat(seed.length, is(256));
  }

  @Test
  public void testCreateSeedNotAllZeros() {
    byte[] seed = RandomNumberGenerator.createSeed();

    boolean hasNonZeroByte = false;
    for (byte b : seed) {
      if (b != 0) {
        hasNonZeroByte = true;
        break;
      }
    }

    assertThat("Seed should contain at least one non-zero byte", hasNonZeroByte, is(true));
  }

  @Test
  public void testCreateSeedUniqueness() {
    byte[] seed1 = RandomNumberGenerator.createSeed();
    byte[] seed2 = RandomNumberGenerator.createSeed();

    assertThat("Seeds should be different", Arrays.equals(seed1, seed2), is(false));
  }

  @Test
  public void testCreateMultipleInstances() {
    SecureRandom random1 = RandomNumberGenerator.create();
    SecureRandom random2 = RandomNumberGenerator.create();

    assertThat(random1, notNullValue());
    assertThat(random2, notNullValue());
    assertThat(random1, not(random2));
  }

  @Test
  public void testRandomGeneratesNumbers() {
    SecureRandom random = RandomNumberGenerator.create();

    int value1 = random.nextInt();
    int value2 = random.nextInt();

    // With proper randomness, these should be different (extremely high probability)
    assertThat("Random should generate different values", value1, not(value2));
  }

  @Test
  public void testRandomGeneratesBytes() {
    SecureRandom random = RandomNumberGenerator.create();
    byte[] bytes = new byte[256];

    random.nextBytes(bytes);

    boolean hasNonZeroByte = false;
    for (byte b : bytes) {
      if (b != 0) {
        hasNonZeroByte = true;
        break;
      }
    }

    assertThat("Random bytes should contain at least one non-zero byte", hasNonZeroByte, is(true));
  }

  @Test
  public void testRandomDistribution() {
    SecureRandom random = RandomNumberGenerator.create();
    Set<Integer> values = new HashSet<>();

    // Generate 100 random integers - should have good variety
    for (int i = 0; i < 100; i++) {
      values.add(random.nextInt(1000));
    }

    // Should have generated at least 80 unique values out of 100
    assertThat("Random should produce varied values", values.size(), greaterThan(80));
  }

  @Test
  public void testCreateSeedConsistentLength() {
    for (int i = 0; i < 10; i++) {
      byte[] seed = RandomNumberGenerator.createSeed();
      assertThat("Seed length should always be 256", seed.length, is(256));
    }
  }

  @Test
  public void testMultipleSeedsAreDifferent() {
    byte[] seed1 = RandomNumberGenerator.createSeed();
    byte[] seed2 = RandomNumberGenerator.createSeed();
    byte[] seed3 = RandomNumberGenerator.createSeed();

    assertThat(Arrays.equals(seed1, seed2), is(false));
    assertThat(Arrays.equals(seed2, seed3), is(false));
    assertThat(Arrays.equals(seed1, seed3), is(false));
  }

  @Test
  public void testRandomGeneratesPositiveIntegers() {
    SecureRandom random = RandomNumberGenerator.create();

    int positive = random.nextInt(Integer.MAX_VALUE);
    assertThat(positive, greaterThan(-1));
  }

  @Test
  public void testRandomGeneratesLongs() {
    SecureRandom random = RandomNumberGenerator.create();

    long value1 = random.nextLong();
    long value2 = random.nextLong();

    assertThat("Random should generate different long values", value1, not(value2));
  }
}
