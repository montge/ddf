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
package org.codice.ddf.catalog.plugin.metacard.backup.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.CreateResponse;
import ddf.catalog.operation.DeleteResponse;
import ddf.catalog.operation.Response;
import ddf.catalog.operation.Update;
import ddf.catalog.operation.UpdateResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseMetacardActionSplitterTest {

  private ResponseMetacardActionSplitter splitter;

  @BeforeEach
  void setUp() {
    splitter = new ResponseMetacardActionSplitter();
  }

  @Test
  void testSplitCreateResponse() {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    CreateResponse response = mock(CreateResponse.class);
    when(response.getCreatedMetacards()).thenReturn(Arrays.asList(metacard1, metacard2));

    List<Metacard> result = splitter.split(response);

    assertThat(result, hasSize(2));
    assertThat(result, containsInAnyOrder(metacard1, metacard2));
  }

  @Test
  void testSplitCreateResponseWithEmptyList() {
    CreateResponse response = mock(CreateResponse.class);
    when(response.getCreatedMetacards()).thenReturn(Collections.emptyList());

    List<Metacard> result = splitter.split(response);

    assertThat(result, is(empty()));
  }

  @Test
  void testSplitUpdateResponse() {
    Metacard newMetacard1 = mock(Metacard.class);
    Metacard newMetacard2 = mock(Metacard.class);
    Update update1 = mock(Update.class);
    Update update2 = mock(Update.class);
    when(update1.getNewMetacard()).thenReturn(newMetacard1);
    when(update2.getNewMetacard()).thenReturn(newMetacard2);

    UpdateResponse response = mock(UpdateResponse.class);
    when(response.getUpdatedMetacards()).thenReturn(Arrays.asList(update1, update2));

    List<Metacard> result = splitter.split(response);

    assertThat(result, hasSize(2));
    assertThat(result, containsInAnyOrder(newMetacard1, newMetacard2));
  }

  @Test
  void testSplitUpdateResponseWithEmptyList() {
    UpdateResponse response = mock(UpdateResponse.class);
    when(response.getUpdatedMetacards()).thenReturn(Collections.emptyList());

    List<Metacard> result = splitter.split(response);

    assertThat(result, is(empty()));
  }

  @Test
  void testSplitDeleteResponse() {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    Metacard metacard3 = mock(Metacard.class);
    DeleteResponse response = mock(DeleteResponse.class);
    when(response.getDeletedMetacards()).thenReturn(Arrays.asList(metacard1, metacard2, metacard3));

    List<Metacard> result = splitter.split(response);

    assertThat(result, hasSize(3));
    assertThat(result, containsInAnyOrder(metacard1, metacard2, metacard3));
  }

  @Test
  void testSplitDeleteResponseWithEmptyList() {
    DeleteResponse response = mock(DeleteResponse.class);
    when(response.getDeletedMetacards()).thenReturn(Collections.emptyList());

    List<Metacard> result = splitter.split(response);

    assertThat(result, is(empty()));
  }

  @Test
  void testSplitUnknownResponseType() {
    Response response = mock(Response.class);

    List<Metacard> result = splitter.split(response);

    assertThat(result, is(empty()));
  }

  @Test
  void testSplitSingleCreateResponse() {
    Metacard metacard = mock(Metacard.class);
    CreateResponse response = mock(CreateResponse.class);
    when(response.getCreatedMetacards()).thenReturn(Collections.singletonList(metacard));

    List<Metacard> result = splitter.split(response);

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(metacard));
  }

  @Test
  void testSplitSingleUpdateResponse() {
    Metacard newMetacard = mock(Metacard.class);
    Update update = mock(Update.class);
    when(update.getNewMetacard()).thenReturn(newMetacard);

    UpdateResponse response = mock(UpdateResponse.class);
    when(response.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));

    List<Metacard> result = splitter.split(response);

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(newMetacard));
  }
}
