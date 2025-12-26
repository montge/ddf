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
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.DeleteRequest;
import ddf.catalog.operation.ProcessingDetails;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeleteResponseImplTest {

  @Test
  void testConstructorWithoutErrors() {
    DeleteRequest request = mock(DeleteRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key", "value");
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(mock(Metacard.class));

    DeleteResponseImpl response = new DeleteResponseImpl(request, properties, metacards);

    assertThat(response.getRequest(), is(sameInstance(request)));
    assertThat(response.getProperties(), hasEntry("key", "value"));
    assertThat(response.getDeletedMetacards(), hasSize(1));
    assertThat(response.getProcessingErrors(), is(empty()));
  }

  @Test
  void testConstructorWithErrors() {
    DeleteRequest request = mock(DeleteRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(mock(Metacard.class));
    Set<ProcessingDetails> errors = new HashSet<>();
    ProcessingDetails error = mock(ProcessingDetails.class);
    errors.add(error);

    DeleteResponseImpl response = new DeleteResponseImpl(request, properties, metacards, errors);

    assertThat(response.getDeletedMetacards(), hasSize(1));
    assertThat(response.getProcessingErrors(), hasSize(1));
    assertThat(response.getProcessingErrors().contains(error), is(true));
  }

  @Test
  void testConstructorWithNullErrors() {
    DeleteRequest request = mock(DeleteRequest.class);
    Map<String, Serializable> properties = new HashMap<>();
    List<Metacard> metacards = new ArrayList<>();

    DeleteResponseImpl response = new DeleteResponseImpl(request, properties, metacards, null);

    assertThat(response.getProcessingErrors(), is(empty()));
  }

  @Test
  void testGetDeletedMetacardsReturnsCorrectList() {
    DeleteRequest request = mock(DeleteRequest.class);
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(metacard1);
    metacards.add(metacard2);

    DeleteResponseImpl response = new DeleteResponseImpl(request, null, metacards);

    assertThat(response.getDeletedMetacards(), containsInAnyOrder(metacard1, metacard2));
  }

  @Test
  void testGetProcessingErrorsReturnsCorrectSet() {
    DeleteRequest request = mock(DeleteRequest.class);
    List<Metacard> metacards = new ArrayList<>();
    ProcessingDetails error1 = mock(ProcessingDetails.class);
    ProcessingDetails error2 = mock(ProcessingDetails.class);
    Set<ProcessingDetails> errors = new HashSet<>();
    errors.add(error1);
    errors.add(error2);

    DeleteResponseImpl response = new DeleteResponseImpl(request, null, metacards, errors);

    assertThat(response.getProcessingErrors(), containsInAnyOrder(error1, error2));
  }

  @Test
  void testEmptyMetacardList() {
    DeleteRequest request = mock(DeleteRequest.class);
    List<Metacard> metacards = new ArrayList<>();

    DeleteResponseImpl response = new DeleteResponseImpl(request, null, metacards);

    assertThat(response.getDeletedMetacards(), is(empty()));
  }

  @Test
  void testNullProperties() {
    DeleteRequest request = mock(DeleteRequest.class);
    List<Metacard> metacards = new ArrayList<>();

    DeleteResponseImpl response = new DeleteResponseImpl(request, null, metacards);

    assertThat(response.getRequest(), is(sameInstance(request)));
  }
}
