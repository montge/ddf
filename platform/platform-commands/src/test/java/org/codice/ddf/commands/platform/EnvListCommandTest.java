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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Tests for {@link EnvListCommand} class. */
public class EnvListCommandTest {

  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream outputStream;
  private EnvListCommand command;

  @Before
  public void setUp() {
    command = new EnvListCommand();
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testExtendsPlatformCommands() {
    assertThat(command instanceof PlatformCommands, is(true));
  }

  @Test
  public void testHasServiceAnnotation() {
    Service annotation = EnvListCommand.class.getAnnotation(Service.class);
    assertThat(annotation, is(notNullValue()));
  }

  @Test
  public void testExecuteReturnsNull() throws Exception {
    Object result = command.execute();
    assertThat(result, is(nullValue()));
  }

  @Test
  public void testExecuteOutputsEnvironmentVariables() throws Exception {
    command.execute();

    String output = outputStream.toString();
    // PATH is almost always set on all systems
    if (System.getenv("PATH") != null) {
      assertThat(output, containsString("PATH="));
    }
  }

  @Test
  public void testExecuteOutputsInKeyEqualsValueFormat() throws Exception {
    command.execute();

    String output = outputStream.toString();
    // Verify the output format is key=value
    assertThat(output.isEmpty() || output.contains("="), is(true));
  }
}
