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
package org.codice.ddf.commands.platform;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.karaf.shell.api.action.Action;
import org.junit.Test;

/** Tests for {@link PlatformCommands} class. */
public class PlatformCommandsTest {

  private static final String TEST_MESSAGE = "Test message";

  @Test
  public void testNamespaceConstant() {
    assertThat(PlatformCommands.NAMESPACE, is("platform"));
  }

  @Test
  public void testImplementsAction() {
    TestPlatformCommand command = new TestPlatformCommand();
    assertThat(command instanceof Action, is(true));
  }

  @Test
  public void testOutputErrorMessageContainsMessage() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream testConsole = new PrintStream(outputStream);

    TestPlatformCommand command = new TestPlatformCommand(testConsole);
    command.callOutputErrorMessage(TEST_MESSAGE);

    String output = outputStream.toString();
    assertThat(output, containsString(TEST_MESSAGE));
  }

  @Test
  public void testOutputWarningMessageContainsMessage() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream testConsole = new PrintStream(outputStream);

    TestPlatformCommand command = new TestPlatformCommand(testConsole);
    command.callOutputWarningMessage(TEST_MESSAGE);

    String output = outputStream.toString();
    assertThat(output, containsString(TEST_MESSAGE));
  }

  @Test
  public void testOutputInfoMessageContainsMessage() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream testConsole = new PrintStream(outputStream);

    TestPlatformCommand command = new TestPlatformCommand(testConsole);
    command.callOutputInfoMessage(TEST_MESSAGE);

    String output = outputStream.toString();
    assertThat(output, containsString(TEST_MESSAGE));
  }

  @Test
  public void testOutputSuccessMessageContainsMessage() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream testConsole = new PrintStream(outputStream);

    TestPlatformCommand command = new TestPlatformCommand(testConsole);
    command.callOutputSuccessMessage(TEST_MESSAGE);

    String output = outputStream.toString();
    assertThat(output, containsString(TEST_MESSAGE));
  }

  @Test
  public void testGetConsoleReturnsSystemOut() {
    TestPlatformCommand command = new TestPlatformCommand();
    PrintStream console = command.callGetConsole();
    assertThat(console, is(System.out));
  }

  /** Test subclass of PlatformCommands for testing protected methods. */
  private static class TestPlatformCommand extends PlatformCommands {

    private final PrintStream console;

    TestPlatformCommand() {
      this.console = System.out;
    }

    TestPlatformCommand(PrintStream console) {
      this.console = console;
    }

    @Override
    public Object execute() throws Exception {
      return null;
    }

    @Override
    PrintStream getConsole() {
      return console;
    }

    void callOutputErrorMessage(String message) {
      outputErrorMessage(message);
    }

    void callOutputWarningMessage(String message) {
      outputWarningMessage(message);
    }

    void callOutputInfoMessage(String message) {
      outputInfoMessage(message);
    }

    void callOutputSuccessMessage(String message) {
      outputSuccessMessage(message);
    }

    PrintStream callGetConsole() {
      return super.getConsole();
    }
  }
}
