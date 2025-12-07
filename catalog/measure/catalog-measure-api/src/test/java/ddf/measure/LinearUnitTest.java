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
package ddf.measure;

import static ddf.measure.Distance.LinearUnit;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class LinearUnitTest {

  // Note: commons-lang3 Validate.notNull() throws NullPointerException, not
  // IllegalArgumentException
  @Test
  public void testFromStringWithNull() {
    assertThrows(NullPointerException.class, () -> LinearUnit.fromString(null));
  }

  @Test
  public void testFromStringWithEmptyString() {
    assertThrows(IllegalArgumentException.class, () -> LinearUnit.fromString(""));
  }

  @Test
  public void testFromStringWithInvalidValue() {
    assertThrows(IllegalArgumentException.class, () -> LinearUnit.fromString("abc"));
  }
}
