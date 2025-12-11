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
package org.codice.ddf.catalog.security.logging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.content.data.ContentItem;
import ddf.catalog.content.operation.CreateStorageRequest;
import ddf.catalog.content.operation.CreateStorageResponse;
import ddf.catalog.content.operation.UpdateStorageRequest;
import ddf.catalog.content.operation.UpdateStorageResponse;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.operation.CreateResponse;
import ddf.catalog.operation.DeleteRequest;
import ddf.catalog.operation.DeleteResponse;
import ddf.catalog.operation.Query;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.operation.ResourceRequest;
import ddf.catalog.operation.ResourceResponse;
import ddf.catalog.operation.Update;
import ddf.catalog.operation.UpdateRequest;
import ddf.catalog.operation.UpdateResponse;
import ddf.catalog.resource.Resource;
import ddf.catalog.source.Source;
import ddf.security.audit.SecurityLogger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SecurityLoggingPluginTest {

  private SecurityLoggingPlugin plugin;
  private SecurityLogger securityLogger;

  @BeforeEach
  public void setUp() {
    plugin = new SecurityLoggingPlugin();
    securityLogger = mock(SecurityLogger.class);
    plugin.setSecurityLogger(securityLogger);
  }

  // ==================== CreateRequest Tests ====================

  @Test
  public void testProcessCreateRequestLocalOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    CreateRequest createRequest = mock(CreateRequest.class);
    when(createRequest.getMetacards()).thenReturn(Collections.singletonList(metacard1));
    when(createRequest.getStoreIds()).thenReturn(null);

    CreateRequest result = plugin.process(createRequest);

    assertThat(result, is(createRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessCreateRequestMultipleMetacards() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");
    when(metacard2.getId()).thenReturn("id-2");

    CreateRequest createRequest = mock(CreateRequest.class);
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(metacard1);
    metacards.add(metacard2);
    when(createRequest.getMetacards()).thenReturn(metacards);
    when(createRequest.getStoreIds()).thenReturn(null);

    CreateRequest result = plugin.process(createRequest);

    assertThat(result, is(createRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessCreateRequestFederatedOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    CreateRequest createRequest = mock(CreateRequest.class);
    when(createRequest.getMetacards()).thenReturn(Collections.singletonList(metacard1));
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source");
    when(createRequest.getStoreIds()).thenReturn(storeIds);

    CreateRequest result = plugin.process(createRequest);

    assertThat(result, is(createRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessCreateRequestEmptyMetacards() throws Exception {
    CreateRequest createRequest = mock(CreateRequest.class);
    when(createRequest.getMetacards()).thenReturn(new ArrayList<>());
    when(createRequest.getStoreIds()).thenReturn(null);

    CreateRequest result = plugin.process(createRequest);

    assertThat(result, is(createRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== UpdateRequest Tests ====================

  @Test
  public void testProcessUpdateRequestLocalOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    UpdateRequest updateRequest = mock(UpdateRequest.class);
    Map.Entry<Serializable, Metacard> entry = new TestMapEntry("key", metacard1);
    List<Map.Entry<Serializable, Metacard>> updates = Collections.singletonList(entry);
    when(updateRequest.getUpdates()).thenReturn(updates);
    when(updateRequest.getStoreIds()).thenReturn(null);

    UpdateRequest result = plugin.process(updateRequest);

    assertThat(result, is(updateRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessUpdateRequestMultipleUpdates() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");
    when(metacard2.getId()).thenReturn("id-2");

    UpdateRequest updateRequest = mock(UpdateRequest.class);
    List<Map.Entry<Serializable, Metacard>> updates = new ArrayList<>();
    updates.add(new TestMapEntry("key1", metacard1));
    updates.add(new TestMapEntry("key2", metacard2));
    when(updateRequest.getUpdates()).thenReturn(updates);
    when(updateRequest.getStoreIds()).thenReturn(null);

    UpdateRequest result = plugin.process(updateRequest);

    assertThat(result, is(updateRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessUpdateRequestFederatedOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    UpdateRequest updateRequest = mock(UpdateRequest.class);
    List<Map.Entry<Serializable, Metacard>> updates =
        Collections.singletonList(new TestMapEntry("key", metacard1));
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source");
    when(updateRequest.getUpdates()).thenReturn(updates);
    when(updateRequest.getStoreIds()).thenReturn(storeIds);

    UpdateRequest result = plugin.process(updateRequest);

    assertThat(result, is(updateRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== DeleteRequest Tests ====================

  @Test
  @SuppressWarnings("unchecked")
  public void testProcessDeleteRequestLocalOperation() throws Exception {
    DeleteRequest deleteRequest = mock(DeleteRequest.class);
    when(deleteRequest.getAttributeName()).thenReturn("id");
    List mockList = mock(List.class);
    when(mockList.stream()).thenReturn(java.util.stream.Stream.of("id-1", "id-2"));
    when(deleteRequest.getAttributeValues()).thenReturn(mockList);
    when(deleteRequest.getStoreIds()).thenReturn(null);

    DeleteRequest result = plugin.process(deleteRequest);

    assertThat(result, is(deleteRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testProcessDeleteRequestFederatedOperation() throws Exception {
    DeleteRequest deleteRequest = mock(DeleteRequest.class);
    when(deleteRequest.getAttributeName()).thenReturn("id");
    List mockList = mock(List.class);
    when(mockList.stream()).thenReturn(java.util.stream.Stream.of("id-1"));
    when(deleteRequest.getAttributeValues()).thenReturn(mockList);
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source");
    when(deleteRequest.getStoreIds()).thenReturn(storeIds);

    DeleteRequest result = plugin.process(deleteRequest);

    assertThat(result, is(deleteRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== QueryRequest Tests ====================

  @Test
  public void testProcessQueryRequestLocalOperation() throws Exception {
    Query query = mock(Query.class);
    when(query.toString()).thenReturn("title=test");

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getQuery()).thenReturn(query);
    when(queryRequest.getStoreIds()).thenReturn(null);

    QueryRequest result = plugin.process(queryRequest);

    assertThat(result, is(queryRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessQueryRequestComplexQuery() throws Exception {
    Query query = mock(Query.class);
    when(query.toString()).thenReturn("(title=test AND location=USA)");

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getQuery()).thenReturn(query);
    when(queryRequest.getStoreIds()).thenReturn(null);

    QueryRequest result = plugin.process(queryRequest);

    assertThat(result, is(queryRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessQueryRequestFederatedOperation() throws Exception {
    Query query = mock(Query.class);
    when(query.toString()).thenReturn("title=test");

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getQuery()).thenReturn(query);
    Set<String> storeIds = new HashSet<>();
    storeIds.add("source1");
    when(queryRequest.getStoreIds()).thenReturn(storeIds);

    QueryRequest result = plugin.process(queryRequest);

    assertThat(result, is(queryRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== FederatedQueryRequest Tests ====================

  @Test
  public void testProcessFederatedQueryRequestWithSource() throws Exception {
    Source source = mock(Source.class);
    when(source.getId()).thenReturn("source-id");

    Query query = mock(Query.class);
    when(query.toString()).thenReturn("title=federated");

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getQuery()).thenReturn(query);

    QueryRequest result = plugin.process(source, queryRequest);

    assertThat(result, is(queryRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== ResourceRequest Tests ====================

  @Test
  public void testProcessResourceRequestLocalOperation() throws Exception {
    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    when(resourceRequest.getAttributeName()).thenReturn("resource-uri");
    when(resourceRequest.getAttributeValue()).thenReturn("http://example.com/resource");
    when(resourceRequest.getStoreIds()).thenReturn(null);

    ResourceRequest result = plugin.process(resourceRequest);

    assertThat(result, is(resourceRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessResourceRequestFederatedOperation() throws Exception {
    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    when(resourceRequest.getAttributeName()).thenReturn("id");
    when(resourceRequest.getAttributeValue()).thenReturn("resource-123");
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source");
    when(resourceRequest.getStoreIds()).thenReturn(storeIds);

    ResourceRequest result = plugin.process(resourceRequest);

    assertThat(result, is(resourceRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== CreateResponse Tests ====================

  @Test
  public void testProcessCreateResponseLocalOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    CreateResponse createResponse = mock(CreateResponse.class);
    when(createResponse.getCreatedMetacards()).thenReturn(Collections.singletonList(metacard1));

    CreateRequest createRequest = mock(CreateRequest.class);
    when(createRequest.getStoreIds()).thenReturn(null);
    when(createResponse.getRequest()).thenReturn(createRequest);

    CreateResponse result = plugin.process(createResponse);

    assertThat(result, is(createResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessCreateResponseMultipleMetacards() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");
    when(metacard2.getId()).thenReturn("id-2");

    CreateResponse createResponse = mock(CreateResponse.class);
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(metacard1);
    metacards.add(metacard2);
    when(createResponse.getCreatedMetacards()).thenReturn(metacards);

    CreateRequest createRequest = mock(CreateRequest.class);
    when(createRequest.getStoreIds()).thenReturn(null);
    when(createResponse.getRequest()).thenReturn(createRequest);

    CreateResponse result = plugin.process(createResponse);

    assertThat(result, is(createResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessCreateResponseFederatedOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    CreateResponse createResponse = mock(CreateResponse.class);
    when(createResponse.getCreatedMetacards()).thenReturn(Collections.singletonList(metacard1));

    CreateRequest createRequest = mock(CreateRequest.class);
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source");
    when(createRequest.getStoreIds()).thenReturn(storeIds);
    when(createResponse.getRequest()).thenReturn(createRequest);

    CreateResponse result = plugin.process(createResponse);

    assertThat(result, is(createResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== UpdateResponse Tests ====================

  @Test
  public void testProcessUpdateResponseLocalOperation() throws Exception {
    Metacard updatedMetacard = mock(Metacard.class);
    when(updatedMetacard.getId()).thenReturn("id-1");

    Update update = mock(Update.class);
    when(update.getNewMetacard()).thenReturn(updatedMetacard);

    UpdateResponse updateResponse = mock(UpdateResponse.class);
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));

    UpdateRequest updateRequest = mock(UpdateRequest.class);
    when(updateRequest.getStoreIds()).thenReturn(null);
    when(updateResponse.getRequest()).thenReturn(updateRequest);

    UpdateResponse result = plugin.process(updateResponse);

    assertThat(result, is(updateResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessUpdateResponseFederatedOperation() throws Exception {
    Metacard updatedMetacard = mock(Metacard.class);
    when(updatedMetacard.getId()).thenReturn("id-1");

    Update update = mock(Update.class);
    when(update.getNewMetacard()).thenReturn(updatedMetacard);

    UpdateResponse updateResponse = mock(UpdateResponse.class);
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));

    UpdateRequest updateRequest = mock(UpdateRequest.class);
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source");
    when(updateRequest.getStoreIds()).thenReturn(storeIds);
    when(updateResponse.getRequest()).thenReturn(updateRequest);

    UpdateResponse result = plugin.process(updateResponse);

    assertThat(result, is(updateResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== DeleteResponse Tests ====================

  @Test
  public void testProcessDeleteResponseLocalOperation() throws Exception {
    Metacard deletedMetacard = mock(Metacard.class);
    when(deletedMetacard.getId()).thenReturn("id-1");

    DeleteResponse deleteResponse = mock(DeleteResponse.class);
    when(deleteResponse.getDeletedMetacards())
        .thenReturn(Collections.singletonList(deletedMetacard));

    DeleteRequest deleteRequest = mock(DeleteRequest.class);
    when(deleteRequest.getStoreIds()).thenReturn(null);
    when(deleteResponse.getRequest()).thenReturn(deleteRequest);

    DeleteResponse result = plugin.process(deleteResponse);

    assertThat(result, is(deleteResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessDeleteResponseMultipleMetacards() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    Metacard metacard3 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");
    when(metacard2.getId()).thenReturn("id-2");
    when(metacard3.getId()).thenReturn("id-3");

    DeleteResponse deleteResponse = mock(DeleteResponse.class);
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(metacard1);
    metacards.add(metacard2);
    metacards.add(metacard3);
    when(deleteResponse.getDeletedMetacards()).thenReturn(metacards);

    DeleteRequest deleteRequest = mock(DeleteRequest.class);
    when(deleteRequest.getStoreIds()).thenReturn(null);
    when(deleteResponse.getRequest()).thenReturn(deleteRequest);

    DeleteResponse result = plugin.process(deleteResponse);

    assertThat(result, is(deleteResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== QueryResponse Tests ====================

  @Test
  public void testProcessQueryResponseLocalOperation() throws Exception {
    Metacard resultMetacard = mock(Metacard.class);
    when(resultMetacard.getId()).thenReturn("id-1");

    Result result1 = mock(Result.class);
    when(result1.getMetacard()).thenReturn(resultMetacard);

    QueryResponse queryResponse = mock(QueryResponse.class);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result1));

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getStoreIds()).thenReturn(null);
    when(queryResponse.getRequest()).thenReturn(queryRequest);

    QueryResponse queryRespResult = plugin.process(queryResponse);

    assertThat(queryRespResult, is(queryResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessQueryResponseMultipleResults() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");
    when(metacard2.getId()).thenReturn("id-2");

    Result resultOne = mock(Result.class);
    Result resultTwo = mock(Result.class);
    when(resultOne.getMetacard()).thenReturn(metacard1);
    when(resultTwo.getMetacard()).thenReturn(metacard2);

    QueryResponse queryResponse = mock(QueryResponse.class);
    List<Result> results = new ArrayList<>();
    results.add(resultOne);
    results.add(resultTwo);
    when(queryResponse.getResults()).thenReturn(results);

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getStoreIds()).thenReturn(null);
    when(queryResponse.getRequest()).thenReturn(queryRequest);

    QueryResponse queryRespResult = plugin.process(queryResponse);

    assertThat(queryRespResult, is(queryResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessQueryResponseEmptyResults() throws Exception {
    QueryResponse queryResponse = mock(QueryResponse.class);
    when(queryResponse.getResults()).thenReturn(new ArrayList<>());

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getStoreIds()).thenReturn(null);
    when(queryResponse.getRequest()).thenReturn(queryRequest);

    QueryResponse queryRespResult = plugin.process(queryResponse);

    assertThat(queryRespResult, is(queryResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== ResourceResponse Tests ====================

  @Test
  public void testProcessResourceResponseLocalOperation() throws Exception {
    Resource resource = mock(Resource.class);
    when(resource.getName()).thenReturn("resource-file.pdf");

    ResourceResponse resourceResponse = mock(ResourceResponse.class);
    when(resourceResponse.getResource()).thenReturn(resource);

    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    when(resourceRequest.getStoreIds()).thenReturn(null);
    when(resourceResponse.getRequest()).thenReturn(resourceRequest);

    ResourceResponse resourceRespResult = plugin.process(resourceResponse);

    assertThat(resourceRespResult, is(resourceResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessResourceResponseFederatedOperation() throws Exception {
    Resource resource = mock(Resource.class);
    when(resource.getName()).thenReturn("image.png");

    ResourceResponse resourceResponse = mock(ResourceResponse.class);
    when(resourceResponse.getResource()).thenReturn(resource);

    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source");
    when(resourceRequest.getStoreIds()).thenReturn(storeIds);
    when(resourceResponse.getRequest()).thenReturn(resourceRequest);

    ResourceResponse resourceRespResult = plugin.process(resourceResponse);

    assertThat(resourceRespResult, is(resourceResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== CreateStorageRequest Tests ====================

  @Test
  public void testProcessCreateStorageRequestLocalOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    ContentItem contentItem1 = mock(ContentItem.class);
    when(contentItem1.getMetacard()).thenReturn(metacard1);

    CreateStorageRequest storageRequest = mock(CreateStorageRequest.class);
    when(storageRequest.getContentItems()).thenReturn(Collections.singletonList(contentItem1));
    when(storageRequest.getStoreIds()).thenReturn(null);

    CreateStorageRequest result = plugin.process(storageRequest);

    assertThat(result, is(storageRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessCreateStorageRequestMultipleContentItems() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");
    when(metacard2.getId()).thenReturn("id-2");

    ContentItem contentItem1 = mock(ContentItem.class);
    ContentItem contentItem2 = mock(ContentItem.class);
    when(contentItem1.getMetacard()).thenReturn(metacard1);
    when(contentItem2.getMetacard()).thenReturn(metacard2);

    CreateStorageRequest storageRequest = mock(CreateStorageRequest.class);
    List<ContentItem> contentItems = new ArrayList<>();
    contentItems.add(contentItem1);
    contentItems.add(contentItem2);
    when(storageRequest.getContentItems()).thenReturn(contentItems);
    when(storageRequest.getStoreIds()).thenReturn(null);

    CreateStorageRequest result = plugin.process(storageRequest);

    assertThat(result, is(storageRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== UpdateStorageRequest Tests ====================

  @Test
  public void testProcessUpdateStorageRequestLocalOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    ContentItem contentItem1 = mock(ContentItem.class);
    when(contentItem1.getMetacard()).thenReturn(metacard1);

    UpdateStorageRequest storageRequest = mock(UpdateStorageRequest.class);
    when(storageRequest.getContentItems()).thenReturn(Collections.singletonList(contentItem1));
    when(storageRequest.getStoreIds()).thenReturn(null);

    UpdateStorageRequest result = plugin.process(storageRequest);

    assertThat(result, is(storageRequest));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== CreateStorageResponse Tests ====================

  @Test
  public void testProcessCreateStorageResponseLocalOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    ContentItem contentItem1 = mock(ContentItem.class);
    when(contentItem1.getMetacard()).thenReturn(metacard1);

    CreateStorageResponse storageResponse = mock(CreateStorageResponse.class);
    when(storageResponse.getCreatedContentItems())
        .thenReturn(Collections.singletonList(contentItem1));

    CreateStorageRequest storageRequest = mock(CreateStorageRequest.class);
    when(storageRequest.getStoreIds()).thenReturn(null);
    when(storageResponse.getRequest()).thenReturn(storageRequest);

    CreateStorageResponse result = plugin.process(storageResponse);

    assertThat(result, is(storageResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== UpdateStorageResponse Tests ====================

  @Test
  public void testProcessUpdateStorageResponseLocalOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    ContentItem contentItem1 = mock(ContentItem.class);
    when(contentItem1.getMetacard()).thenReturn(metacard1);

    UpdateStorageResponse storageResponse = mock(UpdateStorageResponse.class);
    when(storageResponse.getUpdatedContentItems())
        .thenReturn(Collections.singletonList(contentItem1));

    UpdateStorageRequest storageRequest = mock(UpdateStorageRequest.class);
    when(storageRequest.getStoreIds()).thenReturn(null);
    when(storageResponse.getRequest()).thenReturn(storageRequest);

    UpdateStorageResponse result = plugin.process(storageResponse);

    assertThat(result, is(storageResponse));
    verify(securityLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== Return Value Tests ====================

  @Test
  public void testProcessCreateRequestReturnsInputObject() throws Exception {
    CreateRequest createRequest = mock(CreateRequest.class);
    when(createRequest.getMetacards()).thenReturn(new ArrayList<>());
    when(createRequest.getStoreIds()).thenReturn(null);

    CreateRequest result = plugin.process(createRequest);

    assertThat(result, notNullValue());
    assertThat(result, is(createRequest));
  }

  @Test
  public void testProcessUpdateRequestReturnsInputObject() throws Exception {
    UpdateRequest updateRequest = mock(UpdateRequest.class);
    when(updateRequest.getUpdates()).thenReturn(new ArrayList<>());
    when(updateRequest.getStoreIds()).thenReturn(null);

    UpdateRequest result = plugin.process(updateRequest);

    assertThat(result, notNullValue());
    assertThat(result, is(updateRequest));
  }

  @Test
  public void testProcessDeleteRequestReturnsInputObject() throws Exception {
    DeleteRequest deleteRequest = mock(DeleteRequest.class);
    when(deleteRequest.getAttributeName()).thenReturn("id");
    when(deleteRequest.getAttributeValues()).thenReturn(new ArrayList<>());
    when(deleteRequest.getStoreIds()).thenReturn(null);

    DeleteRequest result = plugin.process(deleteRequest);

    assertThat(result, notNullValue());
    assertThat(result, is(deleteRequest));
  }

  @Test
  public void testProcessQueryRequestReturnsInputObject() throws Exception {
    Query query = mock(Query.class);
    when(query.toString()).thenReturn("*");

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getQuery()).thenReturn(query);
    when(queryRequest.getStoreIds()).thenReturn(null);

    QueryRequest result = plugin.process(queryRequest);

    assertThat(result, notNullValue());
    assertThat(result, is(queryRequest));
  }

  @Test
  public void testProcessResourceRequestReturnsInputObject() throws Exception {
    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    when(resourceRequest.getAttributeName()).thenReturn("id");
    when(resourceRequest.getAttributeValue()).thenReturn("test");
    when(resourceRequest.getStoreIds()).thenReturn(null);

    ResourceRequest result = plugin.process(resourceRequest);

    assertThat(result, notNullValue());
    assertThat(result, is(resourceRequest));
  }

  @Test
  public void testProcessCreateResponseReturnsInputObject() throws Exception {
    CreateResponse createResponse = mock(CreateResponse.class);
    when(createResponse.getCreatedMetacards()).thenReturn(new ArrayList<>());

    CreateRequest createRequest = mock(CreateRequest.class);
    when(createRequest.getStoreIds()).thenReturn(null);
    when(createResponse.getRequest()).thenReturn(createRequest);

    CreateResponse result = plugin.process(createResponse);

    assertThat(result, notNullValue());
    assertThat(result, is(createResponse));
  }

  @Test
  public void testProcessUpdateResponseReturnsInputObject() throws Exception {
    UpdateResponse updateResponse = mock(UpdateResponse.class);
    when(updateResponse.getUpdatedMetacards()).thenReturn(new ArrayList<>());

    UpdateRequest updateRequest = mock(UpdateRequest.class);
    when(updateRequest.getStoreIds()).thenReturn(null);
    when(updateResponse.getRequest()).thenReturn(updateRequest);

    UpdateResponse result = plugin.process(updateResponse);

    assertThat(result, notNullValue());
    assertThat(result, is(updateResponse));
  }

  @Test
  public void testProcessDeleteResponseReturnsInputObject() throws Exception {
    DeleteResponse deleteResponse = mock(DeleteResponse.class);
    when(deleteResponse.getDeletedMetacards()).thenReturn(new ArrayList<>());

    DeleteRequest deleteRequest = mock(DeleteRequest.class);
    when(deleteRequest.getStoreIds()).thenReturn(null);
    when(deleteResponse.getRequest()).thenReturn(deleteRequest);

    DeleteResponse result = plugin.process(deleteResponse);

    assertThat(result, notNullValue());
    assertThat(result, is(deleteResponse));
  }

  @Test
  public void testProcessQueryResponseReturnsInputObject() throws Exception {
    QueryResponse queryResponse = mock(QueryResponse.class);
    when(queryResponse.getResults()).thenReturn(new ArrayList<>());

    QueryRequest queryRequest = mock(QueryRequest.class);
    when(queryRequest.getStoreIds()).thenReturn(null);
    when(queryResponse.getRequest()).thenReturn(queryRequest);

    QueryResponse result = plugin.process(queryResponse);

    assertThat(result, notNullValue());
    assertThat(result, is(queryResponse));
  }

  @Test
  public void testProcessResourceResponseReturnsInputObject() throws Exception {
    Resource resource = mock(Resource.class);
    when(resource.getName()).thenReturn("test.txt");

    ResourceResponse resourceResponse = mock(ResourceResponse.class);
    when(resourceResponse.getResource()).thenReturn(resource);

    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    when(resourceRequest.getStoreIds()).thenReturn(null);
    when(resourceResponse.getRequest()).thenReturn(resourceRequest);

    ResourceResponse result = plugin.process(resourceResponse);

    assertThat(result, notNullValue());
    assertThat(result, is(resourceResponse));
  }

  @Test
  public void testSetSecurityLogger() throws Exception {
    SecurityLogger newLogger = mock(SecurityLogger.class);
    plugin.setSecurityLogger(newLogger);

    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("id-1");

    CreateRequest createRequest = mock(CreateRequest.class);
    when(createRequest.getMetacards()).thenReturn(Collections.singletonList(metacard1));
    when(createRequest.getStoreIds()).thenReturn(null);

    plugin.process(createRequest);

    verify(newLogger).audit(anyString(), any(Object.class), any(Object.class));
  }

  // ==================== Remote Operation Tests (Branch Coverage) ====================

  @Test
  public void testProcessCreateRequestRemoteOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("remote-id-1");

    CreateRequest createRequest = mock(CreateRequest.class);
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source-1");
    Map<String, Serializable> properties =
        Collections.singletonMap("local-destination", Boolean.FALSE);

    when(createRequest.getMetacards()).thenReturn(Collections.singletonList(metacard1));
    when(createRequest.getStoreIds()).thenReturn(storeIds);
    when(createRequest.hasProperties()).thenReturn(true);
    when(createRequest.getProperties()).thenReturn(properties);

    CreateRequest result = plugin.process(createRequest);

    assertThat(result, is(createRequest));
    verify(securityLogger)
        .audit(anyString(), any(Object.class), any(Object.class), any(Object.class));
  }

  @Test
  public void testProcessCreateResponseRemoteOperation() throws Exception {
    Metacard metacard1 = mock(Metacard.class);
    when(metacard1.getId()).thenReturn("remote-id-1");

    CreateRequest createRequest = mock(CreateRequest.class);
    Set<String> storeIds = new HashSet<>();
    storeIds.add("remote-source-1");
    Map<String, Serializable> properties =
        Collections.singletonMap("local-destination", Boolean.FALSE);

    when(createRequest.getStoreIds()).thenReturn(storeIds);
    when(createRequest.hasProperties()).thenReturn(true);
    when(createRequest.getProperties()).thenReturn(properties);

    CreateResponse createResponse = mock(CreateResponse.class);
    when(createResponse.getCreatedMetacards()).thenReturn(Collections.singletonList(metacard1));
    when(createResponse.getRequest()).thenReturn(createRequest);

    CreateResponse result = plugin.process(createResponse);

    assertThat(result, is(createResponse));
    verify(securityLogger)
        .audit(anyString(), any(Object.class), any(Object.class), any(Object.class));
  }

  // ==================== Helper Class ====================

  private static class TestMapEntry implements Map.Entry<Serializable, Metacard> {
    private final Serializable key;
    private final Metacard mapValue;

    TestMapEntry(String key, Metacard mapValue) {
      this.key = key;
      this.mapValue = mapValue;
    }

    @Override
    public Serializable getKey() {
      return key;
    }

    @Override
    public Metacard getValue() {
      return mapValue;
    }

    @Override
    public Metacard setValue(Metacard setValue) {
      return mapValue;
    }
  }
}
