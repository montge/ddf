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
package org.codice.ddf.security.command.sessionmanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.codice.ddf.security.handler.api.SessionHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link EndSessionCommand} class. */
@RunWith(MockitoJUnitRunner.class)
public class EndSessionCommandTest {

  @Mock private SessionHandler sessionHandler;

  private EndSessionCommand command;

  private ByteArrayOutputStream outputStream;

  private PrintStream originalOut;

  @Before
  public void setUp() throws Exception {
    command = new EndSessionCommand();
    setField(command, "sessionHandler", sessionHandler);

    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testImplementsAction() {
    assertThat(command instanceof Action, is(true));
  }

  @Test
  public void testCommandAnnotation() {
    Command annotation = EndSessionCommand.class.getAnnotation(Command.class);
    assertThat(annotation, is(notNullValue()));
    assertThat(annotation.scope(), is("security"));
    assertThat(annotation.name(), is("endsession"));
    assertThat(annotation.description(), is("Deletes an active SAML session."));
  }

  @Test
  public void testExecuteCallsInvalidateSession() throws Exception {
    String subjectName = "testuser";
    setField(command, "subjectName", subjectName);

    command.execute();

    verify(sessionHandler).invalidateSession(subjectName);
  }

  @Test
  public void testExecutePrintsConfirmationMessage() throws Exception {
    String subjectName = "testuser";
    setField(command, "subjectName", subjectName);

    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("Asynchronous backchannel logout request sent"));
    assertThat(output, containsString(subjectName));
  }

  @Test
  public void testExecuteReturnsNull() throws Exception {
    String subjectName = "testuser";
    setField(command, "subjectName", subjectName);

    Object result = command.execute();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testExecuteWithDifferentSubjectName() throws Exception {
    String subjectName = "admin@example.com";
    setField(command, "subjectName", subjectName);

    command.execute();

    verify(sessionHandler).invalidateSession(subjectName);
    String output = outputStream.toString();
    assertThat(output, containsString(subjectName));
  }

  private void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
