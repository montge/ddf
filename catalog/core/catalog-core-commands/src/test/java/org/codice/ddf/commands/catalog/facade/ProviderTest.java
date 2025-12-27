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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.federation.FederationException;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.operation.CreateResponse;
import ddf.catalog.operation.DeleteRequest;
import ddf.catalog.operation.DeleteResponse;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.operation.UpdateRequest;
import ddf.catalog.operation.UpdateResponse;
import ddf.catalog.source.CatalogProvider;
import ddf.catalog.source.IngestException;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProviderTest {

  @Mock private CatalogProvider catalogProvider;

  private Provider provider;

  @BeforeEach
  void setUp() {
    provider = new Provider(catalogProvider);
  }

  @Test
  void testGetVersion() {
    when(catalogProvider.getVersion()).thenReturn("1.0.0");

    String version = provider.getVersion();

    assertThat(version, is("1.0.0"));
    verify(catalogProvider).getVersion();
  }

  @Test
  void testGetId() {
    when(catalogProvider.getId()).thenReturn("test-provider-id");

    String id = provider.getId();

    assertThat(id, is("test-provider-id"));
    verify(catalogProvider).getId();
  }

  @Test
  void testGetTitle() {
    when(catalogProvider.getTitle()).thenReturn("Test Provider");

    String title = provider.getTitle();

    assertThat(title, is("Test Provider"));
    verify(catalogProvider).getTitle();
  }

  @Test
  void testGetDescription() {
    when(catalogProvider.getDescription()).thenReturn("Provider description");

    String description = provider.getDescription();

    assertThat(description, is("Provider description"));
    verify(catalogProvider).getDescription();
  }

  @Test
  void testGetOrganization() {
    when(catalogProvider.getOrganization()).thenReturn("Test Organization");

    String organization = provider.getOrganization();

    assertThat(organization, is("Test Organization"));
    verify(catalogProvider).getOrganization();
  }

  @Test
  void testCreate() throws IngestException, SourceUnavailableException {
    CreateRequest createRequest = mock(CreateRequest.class);
    CreateResponse createResponse = mock(CreateResponse.class);
    when(catalogProvider.create(createRequest)).thenReturn(createResponse);

    CreateResponse result = provider.create(createRequest);

    assertThat(result, is(sameInstance(createResponse)));
    verify(catalogProvider).create(createRequest);
  }

  @Test
  void testUpdate() throws IngestException, SourceUnavailableException {
    UpdateRequest updateRequest = mock(UpdateRequest.class);
    UpdateResponse updateResponse = mock(UpdateResponse.class);
    when(catalogProvider.update(updateRequest)).thenReturn(updateResponse);

    UpdateResponse result = provider.update(updateRequest);

    assertThat(result, is(sameInstance(updateResponse)));
    verify(catalogProvider).update(updateRequest);
  }

  @Test
  void testDelete() throws IngestException, SourceUnavailableException {
    DeleteRequest deleteRequest = mock(DeleteRequest.class);
    DeleteResponse deleteResponse = mock(DeleteResponse.class);
    when(catalogProvider.delete(deleteRequest)).thenReturn(deleteResponse);

    DeleteResponse result = provider.delete(deleteRequest);

    assertThat(result, is(sameInstance(deleteResponse)));
    verify(catalogProvider).delete(deleteRequest);
  }

  @Test
  void testQuery()
      throws UnsupportedQueryException, SourceUnavailableException, FederationException {
    QueryRequest queryRequest = mock(QueryRequest.class);
    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(catalogProvider.query(queryRequest)).thenReturn(sourceResponse);

    SourceResponse result = provider.query(queryRequest);

    assertThat(result, is(sameInstance(sourceResponse)));
    verify(catalogProvider).query(queryRequest);
  }

  @Test
  void testGetSourceIdsReturnsEmptySet() {
    assertThat(provider.getSourceIds(), is(empty()));
  }
}
