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
package org.codice.ddf.spatial.geocoder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class JsonpValidatorTest {

  static Stream<Arguments> testValues() {
    return Stream.of(
        Arguments.of("", false),
        Arguments.of(".", false),
        Arguments.of("...", false),
        Arguments.of("foo..bar", false),
        Arguments.of("foo", true),
        Arguments.of("foo.bar", true),
        Arguments.of("123", false),
        Arguments.of("foo123", true),
        Arguments.of("foo.bar.123", false),
        Arguments.of("test()", false),
        Arguments.of("a-b", false),
        Arguments.of("$", true),
        Arguments.of("$123", true),
        Arguments.of("foo$bar", true),
        Arguments.of("_foo", true),
        Arguments.of("foo_bar", true),
        Arguments.of("$.callback", true),
        Arguments.of("_.callback", true),
        Arguments.of(" foobar", false),
        Arguments.of("function", false),
        Arguments.of("(function xss(x){evil()})", false),
        Arguments.of("foo.function.bar", false),
        Arguments.of("$.ajaxHandler", true),
        Arguments.of("$.123", false),
        Arguments.of("array_of_functions[42]", true),
        Arguments.of("array_of_functions[42][54]", true),
        Arguments.of("array_of_functions[]", false),
        Arguments.of("array_of_functions[\"key\"]", true),
        Arguments.of("$.ajaxHandler[42][54].foo", true),
        Arguments.of(":badFunction", false));
  }

  @ParameterizedTest
  @MethodSource("testValues")
  public void testIsValidJSONP(String inputJsonp, Boolean expectedResult) {
    assertEquals(expectedResult, JsonpValidator.isValidJsonp(inputJsonp));
  }
}
