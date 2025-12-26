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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.ProcessingDetails;
import ddf.catalog.operation.Update;
import ddf.catalog.operation.UpdateRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UpdateResponseImplTest {

  @Test
  void testConstructorWithUpdateList() {
    UpdateRequest request = mock(UpdateRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key", "value");
    List<Update> updates = new ArrayList<>();
    updates.add(mock(Update.class));

    UpdateResponseImpl response = new UpdateResponseImpl(request, properties, updates);

    assertThat(response.getRequest(), is(sameInstance(request)));
    assertThat(response.getProperties(), hasEntry("key", "value"));
    assertThat(response.getUpdatedMetacards(), hasSize(1));
    assertThat(response.getProcessingErrors(), is(empty()));
  }

  @Test
  void testConstructorWithUpdateListAndErrors() {
    UpdateRequest request = mock(UpdateRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    List<Update> updates = new ArrayList<>();
    updates.add(mock(Update.class));
    Set<ProcessingDetails> errors = new HashSet<>();
    ProcessingDetails error = mock(ProcessingDetails.class);
    errors.add(error);

    UpdateResponseImpl response = new UpdateResponseImpl(request, properties, updates, errors);

    assertThat(response.getUpdatedMetacards(), hasSize(1));
    assertThat(response.getProcessingErrors(), hasSize(1));
    assertThat(response.getProcessingErrors().contains(error), is(true));
  }

  @Test
  void testConstructorWithOldAndNewMetacards() {
    UpdateRequest request = mock(UpdateRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    List<Metacard> newMetacards = new ArrayList<>();
    newMetacards.add(mock(Metacard.class));
    newMetacards.add(mock(Metacard.class));
    List<Metacard> oldMetacards = new ArrayList<>();
    oldMetacards.add(mock(Metacard.class));
    oldMetacards.add(mock(Metacard.class));

    UpdateResponseImpl response =
        new UpdateResponseImpl(request, properties, newMetacards, oldMetacards);

    assertThat(response.getUpdatedMetacards(), hasSize(2));
    assertThat(response.getProcessingErrors(), is(empty()));
  }

  @Test
  void testConstructorWithOldAndNewMetacardsAndErrors() {
    UpdateRequest request = mock(UpdateRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    List<Metacard> newMetacards = new ArrayList<>();
    newMetacards.add(mock(Metacard.class));
    List<Metacard> oldMetacards = new ArrayList<>();
    oldMetacards.add(mock(Metacard.class));
    Set<ProcessingDetails> errors = new HashSet<>();
    ProcessingDetails error = mock(ProcessingDetails.class);
    errors.add(error);

    UpdateResponseImpl response =
        new UpdateResponseImpl(request, properties, newMetacards, oldMetacards, errors);

    assertThat(response.getUpdatedMetacards(), hasSize(1));
    assertThat(response.getProcessingErrors(), hasSize(1));
  }

  @Test
  void testConstructorWithMismatchedListSizesThrowsException() {
    UpdateRequest request = mock(UpdateRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    List<Metacard> newMetacards = new ArrayList<>();
    newMetacards.add(mock(Metacard.class));
    newMetacards.add(mock(Metacard.class));
    List<Metacard> oldMetacards = new ArrayList<>();
    oldMetacards.add(mock(Metacard.class));

    assertThrows(
        IllegalArgumentException.class,
        () -> new UpdateResponseImpl(request, properties, newMetacards, oldMetacards));
  }

  @Test
  void testConstructorWithNullErrorsInUpdateList() {
    UpdateRequest request = mock(UpdateRequest.class);
    List<Update> updates = new ArrayList<>();

    UpdateResponseImpl response = new UpdateResponseImpl(request, null, updates, null);

    assertThat(response.getProcessingErrors(), is(empty()));
  }

  @Test
  void testConstructorWithNullErrorsInMetacardLists() {
    UpdateRequest request = mock(UpdateRequest.class);
    List<Metacard> newMetacards = new ArrayList<>();
    newMetacards.add(mock(Metacard.class));
    List<Metacard> oldMetacards = new ArrayList<>();
    oldMetacards.add(mock(Metacard.class));

    UpdateResponseImpl response =
        new UpdateResponseImpl(request, null, newMetacards, oldMetacards, null);

    assertThat(response.getProcessingErrors(), is(empty()));
  }

  @Test
  void testGetUpdatedMetacardsReturnsCorrectList() {
    UpdateRequest request = mock(UpdateRequest.class);
    Update update1 = mock(Update.class);
    Update update2 = mock(Update.class);
    List<Update> updates = new ArrayList<>();
    updates.add(update1);
    updates.add(update2);

    UpdateResponseImpl response = new UpdateResponseImpl(request, null, updates);

    assertThat(response.getUpdatedMetacards(), containsInAnyOrder(update1, update2));
  }

  @Test
  void testGetProcessingErrorsReturnsCorrectSet() {
    UpdateRequest request = mock(UpdateRequest.class);
    List<Update> updates = new ArrayList<>();
    ProcessingDetails error1 = mock(ProcessingDetails.class);
    ProcessingDetails error2 = mock(ProcessingDetails.class);
    Set<ProcessingDetails> errors = new HashSet<>();
    errors.add(error1);
    errors.add(error2);

    UpdateResponseImpl response = new UpdateResponseImpl(request, null, updates, errors);

    assertThat(response.getProcessingErrors(), containsInAnyOrder(error1, error2));
  }

  @Test
  void testEmptyUpdateList() {
    UpdateRequest request = mock(UpdateRequest.class);
    List<Update> updates = new ArrayList<>();

    UpdateResponseImpl response = new UpdateResponseImpl(request, null, updates);

    assertThat(response.getUpdatedMetacards(), is(empty()));
  }

  @Test
  void testEmptyMetacardLists() {
    UpdateRequest request = mock(UpdateRequest.class);
    List<Metacard> newMetacards = new ArrayList<>();
    List<Metacard> oldMetacards = new ArrayList<>();

    UpdateResponseImpl response = new UpdateResponseImpl(request, null, newMetacards, oldMetacards);

    assertThat(response.getUpdatedMetacards(), is(empty()));
  }

  @Test
  void testUpdateCreatedFromMetacardLists() {
    UpdateRequest request = mock(UpdateRequest.class);
    Metacard newMetacard = mock(Metacard.class);
    Metacard oldMetacard = mock(Metacard.class);
    List<Metacard> newMetacards = new ArrayList<>();
    newMetacards.add(newMetacard);
    List<Metacard> oldMetacards = new ArrayList<>();
    oldMetacards.add(oldMetacard);

    UpdateResponseImpl response = new UpdateResponseImpl(request, null, newMetacards, oldMetacards);

    assertThat(response.getUpdatedMetacards(), hasSize(1));
    Update update = response.getUpdatedMetacards().get(0);
    assertThat(update.getNewMetacard(), is(sameInstance(newMetacard)));
    assertThat(update.getOldMetacard(), is(sameInstance(oldMetacard)));
  }
}
