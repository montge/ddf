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
import static ddf.measure.Distance.LinearUnit.FOOT_U_S;
import static ddf.measure.Distance.LinearUnit.KILOMETER;
import static ddf.measure.Distance.LinearUnit.METER;
import static ddf.measure.Distance.LinearUnit.MILE;
import static ddf.measure.Distance.LinearUnit.NAUTICAL_MILE;
import static ddf.measure.Distance.LinearUnit.YARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests the different string values supported by the {@link LinearUnit#fromString(String)} method.
 */
public class LinearUnitFromStringParametrizedTest {

  static Stream<Arguments> data() {
    return Stream.of(
        Arguments.of("FOOT_U_S", FOOT_U_S),
        Arguments.of("foot_u_s", FOOT_U_S),
        Arguments.of("foot", FOOT_U_S),
        Arguments.of("FOOT", FOOT_U_S),
        Arguments.of("meter", METER),
        Arguments.of("METER", METER),
        Arguments.of("kilometer", KILOMETER),
        Arguments.of("KILOMETER", KILOMETER),
        Arguments.of("nautical_mile", NAUTICAL_MILE),
        Arguments.of("NAUTICAL_MILE", NAUTICAL_MILE),
        Arguments.of("nauticalMile", NAUTICAL_MILE),
        Arguments.of("mile", MILE),
        Arguments.of("MILE", MILE),
        Arguments.of("yard", YARD),
        Arguments.of("YARD", YARD));
  }

  @ParameterizedTest(name = "fromString({0})")
  @MethodSource("data")
  public void testLinearUnit(String enumValueString, LinearUnit expectedEnumValue) {
    assertThat(LinearUnit.fromString(enumValueString), equalTo(expectedEnumValue));
  }
}
