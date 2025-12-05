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
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.codice.ddf.security.handler.api.SessionHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link ListSessionsCommand} class. */
@RunWith(MockitoJUnitRunner.class)
public class ListSessionsCommandTest {

  @Mock private SessionHandler sessionHandler;

  private ListSessionsCommand command;

  private ByteArrayOutputStream outputStream;

  private PrintStream originalOut;

  @Before
  public void setUp() throws Exception {
    command = new ListSessionsCommand();
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
    Command annotation = ListSessionsCommand.class.getAnnotation(Command.class);
    assertThat(annotation, is(notNullValue()));
    assertThat(annotation.scope(), is("security"));
    assertThat(annotation.name(), is("listsessions"));
    assertThat(annotation.description(), is("Lists all active SAML sessions."));
  }

  @Test
  public void testExecuteCallsGetActiveSessions() throws Exception {
    when(sessionHandler.getActiveSessions()).thenReturn(Collections.emptyMap());

    command.execute();

    verify(sessionHandler).getActiveSessions();
  }

  @Test
  public void testExecuteWithEmptySessionsMap() throws Exception {
    when(sessionHandler.getActiveSessions()).thenReturn(Collections.emptyMap());

    command.execute();

    // Should just print empty lines
    String output = outputStream.toString();
    assertThat(output, is(notNullValue()));
  }

  @Test
  public void testExecutePrintsActiveSessions() throws Exception {
    Map<String, Set<String>> activeSessions = new HashMap<>();
    Set<String> sps = new HashSet<>();
    sps.add("sp1");
    sps.add("sp2");
    activeSessions.put("testuser", sps);

    when(sessionHandler.getActiveSessions()).thenReturn(activeSessions);

    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("testuser"));
    assertThat(output, containsString("active SPs"));
    assertThat(output, containsString("sp1"));
    assertThat(output, containsString("sp2"));
  }

  @Test
  public void testExecuteWithMultipleSessions() throws Exception {
    Map<String, Set<String>> activeSessions = new HashMap<>();
    Set<String> sps1 = new HashSet<>();
    sps1.add("serviceProvider1");
    activeSessions.put("user1", sps1);

    Set<String> sps2 = new HashSet<>();
    sps2.add("serviceProvider2");
    sps2.add("serviceProvider3");
    activeSessions.put("user2", sps2);

    when(sessionHandler.getActiveSessions()).thenReturn(activeSessions);

    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("user1"));
    assertThat(output, containsString("user2"));
    assertThat(output, containsString("serviceProvider1"));
    assertThat(output, containsString("serviceProvider2"));
  }

  @Test
  public void testExecuteReturnsNull() throws Exception {
    when(sessionHandler.getActiveSessions()).thenReturn(Collections.emptyMap());

    Object result = command.execute();

    assertThat(result, is(nullValue()));
  }

  private void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
