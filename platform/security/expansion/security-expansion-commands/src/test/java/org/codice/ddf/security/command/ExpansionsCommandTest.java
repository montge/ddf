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
package org.codice.ddf.security.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.expansion.Expansion;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link ExpansionsCommand} class. */
@RunWith(MockitoJUnitRunner.class)
public class ExpansionsCommandTest {

  @Mock private Expansion expansion;

  private ExpansionsCommand command;

  private ByteArrayOutputStream outputStream;

  private PrintStream originalOut;

  @Before
  public void setUp() {
    command = new ExpansionsCommand();
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
    Command annotation = ExpansionsCommand.class.getAnnotation(Command.class);
    assertThat(annotation, is(notNullValue()));
    assertThat(annotation.scope(), is("security"));
    assertThat(annotation.name(), is("expansions"));
    assertThat(annotation.description(), is("Dumps the current expansion tables."));
  }

  @Test
  public void testExecuteWithNullExpansionListPrintsMessage() throws Exception {
    setField(command, "expansionList", null);

    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("No expansion services currently available."));
  }

  @Test
  public void testExecuteWithEmptyExpansionListPrintsMessage() throws Exception {
    setField(command, "expansionList", Collections.emptyList());

    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("No expansion services currently available."));
  }

  @Test
  public void testExecuteWithExpansionServicePrintsExpansionMap() throws Exception {
    Map<String, List<String[]>> expansionMap = new HashMap<>();
    List<String[]> mappings = new ArrayList<>();
    mappings.add(new String[] {"admin", "superuser"});
    expansionMap.put("role", mappings);

    when(expansion.getExpansionMap()).thenReturn(expansionMap);

    setField(command, "expansionList", Collections.singletonList(expansion));

    command.execute();

    verify(expansion).getExpansionMap();
    String output = outputStream.toString();
    assertThat(output, containsString("role"));
    assertThat(output, containsString("admin"));
    assertThat(output, containsString("superuser"));
  }

  @Test
  public void testExecuteWithMultipleExpansionEntries() throws Exception {
    Map<String, List<String[]>> expansionMap = new HashMap<>();

    List<String[]> roleMappings = new ArrayList<>();
    roleMappings.add(new String[] {"admin", "superuser"});
    roleMappings.add(new String[] {"user", "guest"});
    expansionMap.put("role", roleMappings);

    List<String[]> groupMappings = new ArrayList<>();
    groupMappings.add(new String[] {"engineering", "developers"});
    expansionMap.put("group", groupMappings);

    when(expansion.getExpansionMap()).thenReturn(expansionMap);

    setField(command, "expansionList", Collections.singletonList(expansion));

    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("role"));
    assertThat(output, containsString("admin"));
    assertThat(output, containsString("superuser"));
    assertThat(output, containsString("user"));
    assertThat(output, containsString("guest"));
    assertThat(output, containsString("group"));
    assertThat(output, containsString("engineering"));
    assertThat(output, containsString("developers"));
  }

  @Test
  public void testExecuteWithNullExpansionMapFromService() throws Exception {
    when(expansion.getExpansionMap()).thenReturn(null);

    setField(command, "expansionList", Collections.singletonList(expansion));

    command.execute();

    verify(expansion).getExpansionMap();
    // Should not print anything when map is null (just color escape codes)
  }

  @Test
  public void testExecuteWithEmptyExpansionMapFromService() throws Exception {
    when(expansion.getExpansionMap()).thenReturn(Collections.emptyMap());

    setField(command, "expansionList", Collections.singletonList(expansion));

    command.execute();

    verify(expansion).getExpansionMap();
    // Should not print anything when map is empty (just color escape codes)
  }

  @Test
  public void testExecuteWithMultipleExpansionServices() throws Exception {
    Map<String, List<String[]>> expansionMap1 = new HashMap<>();
    List<String[]> mappings1 = new ArrayList<>();
    mappings1.add(new String[] {"value1a", "value1b"});
    expansionMap1.put("key1", mappings1);

    Map<String, List<String[]>> expansionMap2 = new HashMap<>();
    List<String[]> mappings2 = new ArrayList<>();
    mappings2.add(new String[] {"value2a", "value2b"});
    expansionMap2.put("key2", mappings2);

    Expansion expansion2 = org.mockito.Mockito.mock(Expansion.class);

    when(expansion.getExpansionMap()).thenReturn(expansionMap1);
    when(expansion2.getExpansionMap()).thenReturn(expansionMap2);

    List<Expansion> expansionList = Arrays.asList(expansion, expansion2);

    setField(command, "expansionList", expansionList);

    command.execute();

    verify(expansion).getExpansionMap();
    verify(expansion2).getExpansionMap();
    String output = outputStream.toString();
    assertThat(output, containsString("key1"));
    assertThat(output, containsString("value1a"));
    assertThat(output, containsString("key2"));
    assertThat(output, containsString("value2a"));
  }

  @Test
  public void testExecuteReturnsNull() throws Exception {
    setField(command, "expansionList", null);

    Object result = command.execute();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testOutputFormat() throws Exception {
    Map<String, List<String[]>> expansionMap = new HashMap<>();
    List<String[]> mappings = new ArrayList<>();
    mappings.add(new String[] {"admin", "superuser"});
    expansionMap.put("role", mappings);

    when(expansion.getExpansionMap()).thenReturn(expansionMap);

    setField(command, "expansionList", Collections.singletonList(expansion));

    command.execute();

    String output = outputStream.toString();
    // Output format should be: key : mapping[0] : mapping[1]
    assertThat(output, containsString("role : admin : superuser"));
  }

  private void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
