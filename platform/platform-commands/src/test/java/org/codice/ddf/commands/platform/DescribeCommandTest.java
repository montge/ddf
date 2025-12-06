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

/** Tests for {@link DescribeCommand} class. */
public class DescribeCommandTest {

  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream outputStream;
  private DescribeCommand command;

  @Before
  public void setUp() {
    command = new DescribeCommand();
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
    Service annotation = DescribeCommand.class.getAnnotation(Service.class);
    assertThat(annotation, is(notNullValue()));
  }

  @Test
  public void testExecuteReturnsNull() throws Exception {
    Object result = command.execute();
    assertThat(result, is(nullValue()));
  }

  @Test
  public void testExecuteOutputsProtocolLabel() throws Exception {
    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Protocol="));
  }

  @Test
  public void testExecuteOutputsHostLabel() throws Exception {
    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Host="));
  }

  @Test
  public void testExecuteOutputsPortLabel() throws Exception {
    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Port="));
  }

  @Test
  public void testExecuteOutputsRootContextLabel() throws Exception {
    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Root Context="));
  }

  @Test
  public void testExecuteOutputsSiteNameLabel() throws Exception {
    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Site Name="));
  }

  @Test
  public void testExecuteOutputsOrganizationLabel() throws Exception {
    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Organization="));
  }

  @Test
  public void testExecuteOutputsVersionLabel() throws Exception {
    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Version="));
  }

  @Test
  public void testExecuteOutputsContactLabel() throws Exception {
    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Contact="));
  }
}
