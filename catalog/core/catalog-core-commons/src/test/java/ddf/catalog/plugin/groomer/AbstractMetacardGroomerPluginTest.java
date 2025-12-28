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
package ddf.catalog.plugin.groomer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.operation.DeleteRequest;
import ddf.catalog.operation.UpdateRequest;
import ddf.catalog.plugin.PluginExecutionException;
import ddf.catalog.plugin.StopProcessingException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class AbstractMetacardGroomerPluginTest {

  private TestableMetacardGroomerPlugin plugin;

  @Before
  public void setUp() {
    plugin = spy(new TestableMetacardGroomerPlugin());
  }

  @Test
  public void testProcessCreateRequestWithNullInput() throws Exception {
    CreateRequest result = plugin.process((CreateRequest) null);
    assertThat(result, is((CreateRequest) null));
    verify(plugin, never()).applyCreatedOperationRules(any(), any(), any());
  }

  @Test
  public void testProcessCreateRequestWithNullMetacards() throws Exception {
    CreateRequest request = mock(CreateRequest.class);
    when(request.getMetacards()).thenReturn(null);

    CreateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, never()).applyCreatedOperationRules(any(), any(), any());
  }

  @Test
  public void testProcessCreateRequestWithEmptyMetacards() throws Exception {
    CreateRequest request = mock(CreateRequest.class);
    when(request.getMetacards()).thenReturn(Collections.emptyList());

    CreateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, never()).applyCreatedOperationRules(any(), any(), any());
  }

  @Test
  public void testProcessCreateRequestWithNonLocalRequest() throws Exception {
    CreateRequest request = mock(CreateRequest.class);
    Metacard metacard = mock(Metacard.class);
    when(request.getMetacards()).thenReturn(Collections.singletonList(metacard));

    // Make it a non-local request by setting local-destination to false
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("local-destination", false);
    when(request.hasProperties()).thenReturn(true);
    when(request.getProperties()).thenReturn(properties);

    CreateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, never()).applyCreatedOperationRules(any(), any(), any());
  }

  @Test
  public void testProcessCreateRequestWithLocalRequest() throws Exception {
    CreateRequest request = mock(CreateRequest.class);
    Metacard metacard = mock(Metacard.class);
    when(request.getMetacards()).thenReturn(Collections.singletonList(metacard));
    when(request.hasProperties()).thenReturn(false);

    CreateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, times(1)).applyCreatedOperationRules(any(), any(), any());
  }

  @Test
  public void testProcessCreateRequestWithMultipleMetacards() throws Exception {
    CreateRequest request = mock(CreateRequest.class);
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(mock(Metacard.class));
    metacards.add(mock(Metacard.class));
    metacards.add(mock(Metacard.class));
    when(request.getMetacards()).thenReturn(metacards);
    when(request.hasProperties()).thenReturn(false);

    CreateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, times(3)).applyCreatedOperationRules(any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestWithNullInput() throws Exception {
    UpdateRequest result = plugin.process((UpdateRequest) null);
    assertThat(result, is((UpdateRequest) null));
    verify(plugin, never()).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestWithNullUpdates() throws Exception {
    UpdateRequest request = mock(UpdateRequest.class);
    when(request.getUpdates()).thenReturn(null);

    UpdateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, never()).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestWithEmptyUpdates() throws Exception {
    UpdateRequest request = mock(UpdateRequest.class);
    when(request.getUpdates()).thenReturn(Collections.emptyList());

    UpdateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, never()).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestWithNonLocalRequest() throws Exception {
    UpdateRequest request = mock(UpdateRequest.class);
    Metacard metacard = mock(Metacard.class);
    List<Map.Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new AbstractMap.SimpleEntry<>("id1", metacard));
    when(request.getUpdates()).thenReturn(updates);

    // Make it a non-local request by setting local-destination to false
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("local-destination", false);
    when(request.hasProperties()).thenReturn(true);
    when(request.getProperties()).thenReturn(properties);

    UpdateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, never()).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestWithLocalRequest() throws Exception {
    UpdateRequest request = mock(UpdateRequest.class);
    Metacard metacard = mock(Metacard.class);
    List<Map.Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new AbstractMap.SimpleEntry<>("id1", metacard));
    when(request.getUpdates()).thenReturn(updates);
    when(request.hasProperties()).thenReturn(false);

    UpdateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, times(1)).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestSkipsNullEntry() throws Exception {
    UpdateRequest request = mock(UpdateRequest.class);
    Metacard metacard = mock(Metacard.class);
    List<Map.Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(null);
    updates.add(new AbstractMap.SimpleEntry<>("id1", metacard));
    when(request.getUpdates()).thenReturn(updates);
    when(request.hasProperties()).thenReturn(false);

    UpdateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, times(1)).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestSkipsNullKey() throws Exception {
    UpdateRequest request = mock(UpdateRequest.class);
    Metacard metacard = mock(Metacard.class);
    List<Map.Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new AbstractMap.SimpleEntry<>(null, metacard));
    updates.add(new AbstractMap.SimpleEntry<>("id1", metacard));
    when(request.getUpdates()).thenReturn(updates);
    when(request.hasProperties()).thenReturn(false);

    UpdateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, times(1)).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestSkipsNullValue() throws Exception {
    UpdateRequest request = mock(UpdateRequest.class);
    Metacard metacard = mock(Metacard.class);
    List<Map.Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new AbstractMap.SimpleEntry<>("id0", null));
    updates.add(new AbstractMap.SimpleEntry<>("id1", metacard));
    when(request.getUpdates()).thenReturn(updates);
    when(request.hasProperties()).thenReturn(false);

    UpdateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, times(1)).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessUpdateRequestWithMultipleUpdates() throws Exception {
    UpdateRequest request = mock(UpdateRequest.class);
    List<Map.Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new AbstractMap.SimpleEntry<>("id1", mock(Metacard.class)));
    updates.add(new AbstractMap.SimpleEntry<>("id2", mock(Metacard.class)));
    updates.add(new AbstractMap.SimpleEntry<>("id3", mock(Metacard.class)));
    when(request.getUpdates()).thenReturn(updates);
    when(request.hasProperties()).thenReturn(false);

    UpdateRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
    verify(plugin, times(3)).applyUpdateOperationRules(any(), any(), any(), any());
  }

  @Test
  public void testProcessDeleteRequest() throws Exception {
    DeleteRequest request = mock(DeleteRequest.class);
    DeleteRequest result = plugin.process(request);
    assertThat(result, is(sameInstance(request)));
  }

  @Test
  public void testProcessDeleteRequestWithNullInput() throws Exception {
    DeleteRequest result = plugin.process((DeleteRequest) null);
    assertThat(result, is((DeleteRequest) null));
  }

  /**
   * Testable implementation of AbstractMetacardGroomerPlugin that provides empty implementations of
   * abstract methods.
   */
  private static class TestableMetacardGroomerPlugin extends AbstractMetacardGroomerPlugin {

    @Override
    protected void applyCreatedOperationRules(
        CreateRequest createRequest, Metacard aMetacard, Date timestamp)
        throws PluginExecutionException, StopProcessingException {
      // Empty implementation for testing
    }

    @Override
    protected void applyUpdateOperationRules(
        UpdateRequest updateRequest,
        Map.Entry<Serializable, Metacard> anUpdate,
        Metacard aMetacard,
        Date timestamp)
        throws PluginExecutionException, StopProcessingException {
      // Empty implementation for testing
    }
  }
}
