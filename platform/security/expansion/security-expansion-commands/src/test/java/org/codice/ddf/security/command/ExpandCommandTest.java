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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.expansion.Expansion;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link ExpandCommand} class. */
@ExtendWith(MockitoExtension.class)
public class ExpandCommandTest {

  @Mock private Expansion expansion;

  private ExpandCommand command;

  private ByteArrayOutputStream outputStream;

  private PrintStream originalOut;

  @BeforeEach
  public void setUp() {
    command = new ExpandCommand();
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testImplementsAction() {
    assertThat(command instanceof Action, is(true));
  }

  @Test
  public void testCommandAnnotation() {
    Command annotation = ExpandCommand.class.getAnnotation(Command.class);
    assertThat(annotation, is(notNullValue()));
    assertThat(annotation.scope(), is("security"));
    assertThat(annotation.name(), is("expand"));
    assertThat(annotation.description(), is("Expands a given key and set of values."));
  }

  @Test
  public void testExecuteWithNullKeyReturnsNull() throws Exception {
    setField(command, "key", null);
    setField(command, "values", new HashSet<>(Collections.singletonList("value")));

    Object result = command.execute();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testExecuteWithNullValuesReturnsNull() throws Exception {
    setField(command, "key", "testKey");
    setField(command, "values", null);

    Object result = command.execute();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testExecuteWithNullExpansionListPrintsMessage() throws Exception {
    setField(command, "key", "testKey");
    setField(command, "values", new HashSet<>(Collections.singletonList("value")));
    setField(command, "expansionList", null);

    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("No expansion services currently available."));
  }

  @Test
  public void testExecuteWithEmptyExpansionListPrintsMessage() throws Exception {
    setField(command, "key", "testKey");
    setField(command, "values", new HashSet<>(Collections.singletonList("value")));
    setField(command, "expansionList", Collections.emptyList());

    command.execute();

    String output = outputStream.toString();
    assertThat(output, containsString("No expansion services currently available."));
  }

  @Test
  public void testExecuteWithExpansionServiceExpandsValues() throws Exception {
    String key = "role";
    Set<String> values = new HashSet<>(Collections.singletonList("admin"));
    Set<String> expandedValues = new HashSet<>(Arrays.asList("admin", "superuser", "moderator"));

    when(expansion.expand(eq(key), eq(values))).thenReturn(expandedValues);

    setField(command, "key", key);
    setField(command, "values", values);
    setField(command, "expansionList", Collections.singletonList(expansion));

    command.execute();

    verify(expansion).expand(key, values);
    String output = outputStream.toString();
    assertThat(output, containsString("admin"));
    assertThat(output, containsString("superuser"));
    assertThat(output, containsString("moderator"));
  }

  @Test
  public void testExecuteWithMultipleExpansionServices() throws Exception {
    String key = "role";
    Set<String> values = new HashSet<>(Collections.singletonList("admin"));
    Set<String> expandedValues1 = new HashSet<>(Arrays.asList("admin", "expanded1"));
    Set<String> expandedValues2 = new HashSet<>(Arrays.asList("admin", "expanded2"));

    Expansion expansion2 = org.mockito.Mockito.mock(Expansion.class);

    when(expansion.expand(eq(key), eq(values))).thenReturn(expandedValues1);
    when(expansion2.expand(eq(key), eq(values))).thenReturn(expandedValues2);

    List<Expansion> expansionList = Arrays.asList(expansion, expansion2);

    setField(command, "key", key);
    setField(command, "values", values);
    setField(command, "expansionList", expansionList);

    command.execute();

    verify(expansion).expand(key, values);
    verify(expansion2).expand(key, values);
    String output = outputStream.toString();
    assertThat(output, containsString("expanded1"));
    assertThat(output, containsString("expanded2"));
  }

  @Test
  public void testExecuteReturnsNull() throws Exception {
    String key = "role";
    Set<String> values = new HashSet<>(Collections.singletonList("admin"));

    when(expansion.expand(eq(key), eq(values))).thenReturn(values);

    setField(command, "key", key);
    setField(command, "values", values);
    setField(command, "expansionList", Collections.singletonList(expansion));

    Object result = command.execute();

    assertThat(result, is(nullValue()));
  }

  private void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
