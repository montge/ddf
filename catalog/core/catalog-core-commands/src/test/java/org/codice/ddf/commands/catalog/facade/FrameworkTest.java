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
package org.codice.ddf.commands.catalog.facade;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.federation.FederationException;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.operation.CreateResponse;
import ddf.catalog.operation.DeleteRequest;
import ddf.catalog.operation.DeleteResponse;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.operation.UpdateRequest;
import ddf.catalog.operation.UpdateResponse;
import ddf.catalog.source.IngestException;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FrameworkTest {

  @Mock private CatalogFramework catalogFramework;

  private Framework framework;

  @BeforeEach
  void setUp() {
    framework = new Framework(catalogFramework);
  }

  @Test
  void testGetVersion() {
    when(catalogFramework.getVersion()).thenReturn("2.0.0");

    String version = framework.getVersion();

    assertThat(version, is("2.0.0"));
    verify(catalogFramework).getVersion();
  }

  @Test
  void testGetId() {
    when(catalogFramework.getId()).thenReturn("test-framework-id");

    String id = framework.getId();

    assertThat(id, is("test-framework-id"));
    verify(catalogFramework).getId();
  }

  @Test
  void testGetTitle() {
    when(catalogFramework.getTitle()).thenReturn("Test Framework");

    String title = framework.getTitle();

    assertThat(title, is("Test Framework"));
    verify(catalogFramework).getTitle();
  }

  @Test
  void testGetDescription() {
    when(catalogFramework.getDescription()).thenReturn("Framework description");

    String description = framework.getDescription();

    assertThat(description, is("Framework description"));
    verify(catalogFramework).getDescription();
  }

  @Test
  void testGetOrganization() {
    when(catalogFramework.getOrganization()).thenReturn("Test Organization");

    String organization = framework.getOrganization();

    assertThat(organization, is("Test Organization"));
    verify(catalogFramework).getOrganization();
  }

  @Test
  void testCreate() throws IngestException, SourceUnavailableException {
    CreateRequest createRequest = mock(CreateRequest.class);
    CreateResponse createResponse = mock(CreateResponse.class);
    when(catalogFramework.create(createRequest)).thenReturn(createResponse);

    CreateResponse result = framework.create(createRequest);

    assertThat(result, is(sameInstance(createResponse)));
    verify(catalogFramework).create(createRequest);
  }

  @Test
  void testUpdate() throws IngestException, SourceUnavailableException {
    UpdateRequest updateRequest = mock(UpdateRequest.class);
    UpdateResponse updateResponse = mock(UpdateResponse.class);
    when(catalogFramework.update(updateRequest)).thenReturn(updateResponse);

    UpdateResponse result = framework.update(updateRequest);

    assertThat(result, is(sameInstance(updateResponse)));
    verify(catalogFramework).update(updateRequest);
  }

  @Test
  void testDelete() throws IngestException, SourceUnavailableException {
    DeleteRequest deleteRequest = mock(DeleteRequest.class);
    DeleteResponse deleteResponse = mock(DeleteResponse.class);
    when(catalogFramework.delete(deleteRequest)).thenReturn(deleteResponse);

    DeleteResponse result = framework.delete(deleteRequest);

    assertThat(result, is(sameInstance(deleteResponse)));
    verify(catalogFramework).delete(deleteRequest);
  }

  @Test
  void testQuery()
      throws UnsupportedQueryException, SourceUnavailableException, FederationException {
    QueryRequest queryRequest = mock(QueryRequest.class);
    QueryResponse queryResponse = mock(QueryResponse.class);
    when(catalogFramework.query(queryRequest)).thenReturn(queryResponse);

    QueryResponse result = framework.query(queryRequest);

    assertThat(result, is(sameInstance(queryResponse)));
    verify(catalogFramework).query(queryRequest);
  }

  @Test
  void testGetSourceIds() {
    Set<String> sourceIds = new HashSet<>();
    sourceIds.add("source1");
    sourceIds.add("source2");
    when(catalogFramework.getSourceIds()).thenReturn(sourceIds);

    Set<String> result = framework.getSourceIds();

    assertThat(result, containsInAnyOrder("source1", "source2"));
    verify(catalogFramework).getSourceIds();
  }
}
