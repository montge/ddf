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

import java.security.SecureRandom;

public class RandomNumberGenerator {

  // Deliberately NOT Dual_EC_DRBG: the previous implementation expanded entropy through
  // BouncyCastle's DualECSP800DRBG, an algorithm withdrawn from NIST SP 800-90A (2014) over
  // suspected backdoor constants. The platform CSPRNG is the appropriate seed source.
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private RandomNumberGenerator() {}

  public static SecureRandom create() {
    return new SecureRandom(createSeed());
  }

  public static byte[] createSeed() {
    byte[] seed = new byte[256];
    SECURE_RANDOM.nextBytes(seed);
    return seed;
  }
}
