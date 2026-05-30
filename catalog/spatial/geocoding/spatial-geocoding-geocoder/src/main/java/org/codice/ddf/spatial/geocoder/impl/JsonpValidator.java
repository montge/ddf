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

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonpValidator {

  private static final Pattern JSONP_VALID_PATTERN;

  // Linear-time pattern (negated character classes, no nested unbounded quantifiers) to avoid
  // catastrophic backtracking (ReDoS). See MAX_JSONP_LENGTH guard below, which is required to
  // bound stack depth on long valid input.
  static {
    JSONP_VALID_PATTERN =
        Pattern.compile("^[a-zA-Z_$][0-9a-zA-Z_$]*(?:\\[(?:\"[^\"]+\"|'[^']+'|\\d+)\\])*$");
  }

  // Upstream JSONP callback names are short identifiers; cap length to bound regex stack depth
  // and reject obviously hostile input early (unauthenticated DoS protection).
  private static final int MAX_JSONP_LENGTH = 128;

  private static final Set<String> RESERVED_WORDS =
      ImmutableSet.of(
          "abstract",
          "boolean",
          "break",
          "byte",
          "case",
          "catch",
          "char",
          "class",
          "const",
          "continue",
          "debugger",
          "default",
          "delete",
          "do",
          "double",
          "else",
          "enum",
          "export",
          "extends",
          "false",
          "final",
          "finally",
          "float",
          "for",
          "function",
          "goto",
          "if",
          "implements",
          "import",
          "in",
          "instanceof",
          "int",
          "interface",
          "long",
          "native",
          "new",
          "null",
          "package",
          "private",
          "protected",
          "public",
          "return",
          "short",
          "static",
          "super",
          "switch",
          "synchronized",
          "this",
          "throw",
          "throws",
          "transient",
          "true",
          "try",
          "typeof",
          "var",
          "void",
          "volatile",
          "while",
          "with");

  private JsonpValidator() {}

  public static boolean isValidJsonp(String jsonp) {
    if (jsonp == null || jsonp.length() > MAX_JSONP_LENGTH) {
      return false;
    }
    String[] jsonpPortions = jsonp.split("\\.");
    if (jsonpPortions.length == 0) {
      return false;
    }
    for (String portion : jsonpPortions) {
      Matcher matcher = JSONP_VALID_PATTERN.matcher(portion);
      if (!matcher.matches() || RESERVED_WORDS.contains(portion)) {
        return false;
      }
    }
    return true;
  }
}
