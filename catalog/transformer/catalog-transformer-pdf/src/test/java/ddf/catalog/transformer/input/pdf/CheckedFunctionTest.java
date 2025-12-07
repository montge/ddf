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
package ddf.catalog.transformer.input.pdf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;

public class CheckedFunctionTest {

  @Test
  public void testCheckedFunctionSuccess() throws IOException {
    CheckedFunction<String, Integer> lengthFunction = String::length;

    Integer result = lengthFunction.apply("test");

    assertThat(result, is(4));
  }

  @Test
  public void testCheckedFunctionWithInputStreamSuccess() throws IOException {
    CheckedFunction<InputStream, Integer> readFunction = InputStream::read;

    ByteArrayInputStream input = new ByteArrayInputStream(new byte[] {65, 66, 67});
    Integer result = readFunction.apply(input);

    assertThat(result, is(65)); // ASCII 'A'
  }

  @Test(expected = IOException.class)
  public void testCheckedFunctionThrowsIOException() throws IOException {
    CheckedFunction<String, String> throwingFunction =
        s -> {
          throw new IOException("Test exception");
        };

    throwingFunction.apply("test");
  }

  @Test
  public void testCheckedFunctionWithNullInput() throws IOException {
    CheckedFunction<String, String> nullSafeFunction =
        s -> {
          if (s == null) {
            return "null";
          }
          return s.toUpperCase();
        };

    String result = nullSafeFunction.apply(null);

    assertThat(result, is("null"));
  }

  @Test
  public void testCheckedFunctionComposition() throws IOException {
    CheckedFunction<String, String> upperCaseFunction = String::toUpperCase;
    CheckedFunction<String, Integer> lengthFunction = String::length;

    String input = "test";
    String upperResult = upperCaseFunction.apply(input);
    Integer lengthResult = lengthFunction.apply(upperResult);

    assertThat(upperResult, is("TEST"));
    assertThat(lengthResult, is(4));
  }

  @Test
  public void testCheckedFunctionWithComplexType() throws IOException {
    CheckedFunction<InputStream, byte[]> readAllBytesFunction = InputStream::readAllBytes;

    ByteArrayInputStream input = new ByteArrayInputStream(new byte[] {1, 2, 3, 4, 5});
    byte[] result = readAllBytesFunction.apply(input);

    assertThat(result.length, is(5));
    assertThat(result[0], is((byte) 1));
    assertThat(result[4], is((byte) 5));
  }
}
