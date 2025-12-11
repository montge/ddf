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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Test;

public class ExceptionsTest {

  @Test
  public void testGetFullMessageWithSingleException() {
    Exception ex = new Exception("Test exception message");
    String result = Exceptions.getFullMessage(ex);

    assertThat(result, notNullValue());
    assertThat(result, is("Test exception message"));
  }

  @Test
  public void testGetFullMessageWithNestedException() {
    Exception rootCause = new Exception("Root cause message");
    Exception middleException = new Exception("Middle exception message", rootCause);
    Exception topException = new Exception("Top exception message", middleException);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, notNullValue());
    assertThat(result, containsString("Root cause message"));
    assertThat(result, containsString("Middle exception message"));
    assertThat(result, containsString("Top exception message"));
  }

  @Test
  public void testGetFullMessageOrderIsCorrect() {
    Exception rootCause = new Exception("First");
    Exception middleException = new Exception("Second", rootCause);
    Exception topException = new Exception("Third", middleException);

    String result = Exceptions.getFullMessage(topException);

    // Root cause should be first
    assertThat(result, startsWith("First\n"));
    assertThat(result, endsWith("Third"));
  }

  @Test
  public void testGetFullMessageWithTwoCauses() {
    Exception rootCause = new Exception("Root");
    Exception topException = new Exception("Top", rootCause);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, containsString("Root"));
    assertThat(result, containsString("Top"));
    assertThat(result, startsWith("Root\n"));
  }

  @Test
  public void testGetFullMessageWithNoCause() {
    Exception ex = new Exception("Single message");
    String result = Exceptions.getFullMessage(ex);

    assertThat(result, is("Single message"));
  }

  @Test
  public void testGetFullMessageWithMultipleLevels() {
    Exception level1 = new Exception("Level 1");
    Exception level2 = new Exception("Level 2", level1);
    Exception level3 = new Exception("Level 3", level2);
    Exception level4 = new Exception("Level 4", level3);

    String result = Exceptions.getFullMessage(level4);

    assertThat(result, containsString("Level 1"));
    assertThat(result, containsString("Level 2"));
    assertThat(result, containsString("Level 3"));
    assertThat(result, containsString("Level 4"));
  }

  @Test
  public void testGetFullMessageWithRuntimeException() {
    RuntimeException rootCause = new RuntimeException("Runtime root cause");
    Exception topException = new Exception("Top exception", rootCause);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, containsString("Runtime root cause"));
    assertThat(result, containsString("Top exception"));
  }

  @Test
  public void testGetFullMessageWithIOException() {
    java.io.IOException rootCause = new java.io.IOException("IO error");
    Exception topException = new Exception("Wrapper exception", rootCause);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, containsString("IO error"));
    assertThat(result, containsString("Wrapper exception"));
  }

  @Test
  public void testGetFullMessageWithEmptyMessage() {
    Exception rootCause = new Exception("");
    Exception topException = new Exception("Top", rootCause);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, notNullValue());
    // Should still construct a message even with empty strings
  }

  @Test
  public void testGetFullMessageWithNullMessage() {
    Exception rootCause = new Exception((String) null);
    Exception topException = new Exception("Top message", rootCause);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, notNullValue());
    assertThat(result, containsString("Top message"));
  }

  @Test
  public void testGetFullMessageFormat() {
    Exception rootCause = new Exception("Root");
    Exception topException = new Exception("Top", rootCause);

    String result = Exceptions.getFullMessage(topException);

    // Should contain newline separator
    assertThat(result, containsString("\n"));
  }

  @Test
  public void testGetFullMessageWithSpecialCharacters() {
    Exception rootCause = new Exception("Error: 123 @ special#chars");
    Exception topException = new Exception("Top: error!", rootCause);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, containsString("Error: 123 @ special#chars"));
    assertThat(result, containsString("Top: error!"));
  }

  @Test
  public void testGetFullMessageWithLongMessages() {
    String longMessage =
        "This is a very long error message that contains a lot of text "
            + "and should be properly handled by the getFullMessage method without any issues";
    Exception rootCause = new Exception(longMessage);
    Exception topException = new Exception("Short top message", rootCause);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, containsString(longMessage));
    assertThat(result, containsString("Short top message"));
  }

  @Test
  public void testGetFullMessagePreservesOrder() {
    Exception e1 = new Exception("Message 1");
    Exception e2 = new Exception("Message 2", e1);
    Exception e3 = new Exception("Message 3", e2);

    String result = Exceptions.getFullMessage(e3);

    int index1 = result.indexOf("Message 1");
    int index2 = result.indexOf("Message 2");
    int index3 = result.indexOf("Message 3");

    assertThat("Message 1 should come first", index1 < index2, is(true));
    assertThat("Message 2 should come before Message 3", index2 < index3, is(true));
  }

  @Test
  public void testGetFullMessageWithDifferentExceptionTypes() {
    IllegalArgumentException rootCause = new IllegalArgumentException("Illegal argument");
    IllegalStateException middleException = new IllegalStateException("Illegal state", rootCause);
    RuntimeException topException = new RuntimeException("Runtime error", middleException);

    String result = Exceptions.getFullMessage(topException);

    assertThat(result, containsString("Illegal argument"));
    assertThat(result, containsString("Illegal state"));
    assertThat(result, containsString("Runtime error"));
  }
}
