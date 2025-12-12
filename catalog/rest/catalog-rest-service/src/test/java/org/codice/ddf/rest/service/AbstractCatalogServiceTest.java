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
package org.codice.ddf.rest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.content.StorageProvider;
import ddf.catalog.content.operation.ReadStorageResponse;
import ddf.catalog.data.AttributeRegistry;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.federation.FederationException;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.ProcessingDetails;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;
import ddf.mime.MimeTypeResolver;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import net.minidev.json.JSONObject;
import org.codice.ddf.attachment.AttachmentParser;
import org.codice.ddf.checksum.ChecksumProvider;
import org.codice.ddf.rest.api.CatalogServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link AbstractCatalogService} helper and utility methods. */
@ExtendWith(MockitoExtension.class)
public class AbstractCatalogServiceTest {

  @Mock private CatalogFramework catalogFramework;
  @Mock private AttachmentParser attachmentParser;
  @Mock private AttributeRegistry attributeRegistry;
  @Mock private MimeTypeResolver mimeTypeResolver;
  @Mock private HttpServletRequest httpServletRequest;
  @Mock private QueryResponse queryResponse;
  @Mock private BinaryContent binaryContent;

  private TestAbstractCatalogService catalogService;
  private FilterBuilder filterBuilder;

  @BeforeEach
  public void setUp() {
    filterBuilder = new GeotoolsFilterBuilder();
    catalogService =
        new TestAbstractCatalogService(catalogFramework, attachmentParser, attributeRegistry);
    catalogService.setFilterBuilder(filterBuilder);
    catalogService.setTikaMimeTypeResolver(mimeTypeResolver);
  }

  @Test
  public void testConstructor() {
    TestAbstractCatalogService service =
        new TestAbstractCatalogService(catalogFramework, attachmentParser, attributeRegistry);
    assertThat(service, is(notNullValue()));
  }

  @Test
  public void testGetFileExtensionForMimeType() {
    String mimeType = "application/json";
    String expectedExtension = "json";

    when(mimeTypeResolver.getFileExtensionForMimeType(mimeType)).thenReturn(expectedExtension);

    String result = catalogService.getFileExtensionForMimeType(mimeType);

    assertThat(result, is(expectedExtension));
    verify(mimeTypeResolver).getFileExtensionForMimeType(mimeType);
  }

  @Test
  public void testGetFileExtensionForMimeTypeWithXml() {
    String mimeType = "application/xml";
    String expectedExtension = "xml";

    when(mimeTypeResolver.getFileExtensionForMimeType(mimeType)).thenReturn(expectedExtension);

    String result = catalogService.getFileExtensionForMimeType(mimeType);

    assertThat(result, is(expectedExtension));
  }

  @Test
  public void testGetFileExtensionForMimeTypeWithPdf() {
    String mimeType = "application/pdf";
    String expectedExtension = "pdf";

    when(mimeTypeResolver.getFileExtensionForMimeType(mimeType)).thenReturn(expectedExtension);

    String result = catalogService.getFileExtensionForMimeType(mimeType);

    assertThat(result, is(expectedExtension));
  }

  @Test
  public void testGetFileExtensionForUnknownMimeType() {
    String mimeType = "unknown/type";
    when(mimeTypeResolver.getFileExtensionForMimeType(mimeType)).thenReturn(null);

    String result = catalogService.getFileExtensionForMimeType(mimeType);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testSourceActionToJSON() {
    ddf.action.Action action = mock(ddf.action.Action.class);
    when(action.getTitle()).thenReturn("Test Title");
    when(action.getId()).thenReturn("test-id");
    when(action.getDescription()).thenReturn("Test Description");
    when(action.getUrl()).thenReturn(createTestUrl());

    JSONObject result = catalogService.sourceActionToJSON(action);

    assertThat(result.get("title"), is(equalTo("Test Title")));
    assertThat(result.get("id"), is(equalTo("test-id")));
    assertThat(result.get("description"), is(equalTo("Test Description")));
    assertThat(result.get("url"), is(notNullValue()));
  }

  @Test
  public void testDeleteDocumentWithNullId() {
    assertThrows(CatalogServiceException.class, () -> catalogService.deleteDocument(null));
  }

  @Test
  public void testGetHeadersWithNullIdThrowsException() {
    MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    URI uri = URI.create("http://localhost:8181/services/catalog");

    assertThrows(
        CatalogServiceException.class,
        () -> catalogService.getHeaders(null, null, uri, queryParams));
  }

  @Test
  public void testGetHeadersWithValidIdButNoResults() throws Exception {
    String sourceId = "source1";
    String id = "metacard123";
    MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    URI uri = URI.create("http://localhost:8181/services/catalog");

    when(catalogFramework.query(any(), any())).thenReturn(queryResponse);
    when(queryResponse.getResults()).thenReturn(new ArrayList<>());

    BinaryContent result = catalogService.getHeaders(sourceId, id, uri, queryParams);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetHeadersWithValidResults() throws Exception {
    String sourceId = "source1";
    String id = "metacard123";
    MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    URI uri = URI.create("http://localhost:8181/services/catalog");

    Metacard metacard = new MetacardImpl();
    Result result = mock(Result.class);
    when(result.getMetacard()).thenReturn(metacard);
    List<Result> results = new ArrayList<>();
    results.add(result);

    when(catalogFramework.query(any(), any())).thenReturn(queryResponse);
    when(queryResponse.getResults()).thenReturn(results);
    when(catalogFramework.transform(any(Metacard.class), anyString(), any()))
        .thenReturn(binaryContent);

    BinaryContent response = catalogService.getHeaders(sourceId, id, uri, queryParams);

    assertThat(response, is(notNullValue()));
    assertThat(response, is(binaryContent));
  }

  @Test
  public void testSetFilterBuilder() {
    FilterBuilder newFilterBuilder = new GeotoolsFilterBuilder();
    catalogService.setFilterBuilder(newFilterBuilder);
    assertThat(catalogService.getFilterBuilder(), is(newFilterBuilder));
  }

  @Test
  public void testGetFilterBuilder() {
    FilterBuilder result = catalogService.getFilterBuilder();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(filterBuilder));
  }

  @Test
  public void testSetTikaMimeTypeResolver() {
    MimeTypeResolver newResolver = mock(MimeTypeResolver.class);
    catalogService.setTikaMimeTypeResolver(newResolver);
    // Test by calling getFileExtensionForMimeType which uses the resolver
    String mimeType = "text/plain";
    when(newResolver.getFileExtensionForMimeType(mimeType)).thenReturn("txt");
    String result = catalogService.getFileExtensionForMimeType(mimeType);
    assertThat(result, is("txt"));
  }

  @Test
  public void testSetMimeTypeToTransformerMapper() {
    ddf.mime.MimeTypeToTransformerMapper mapper = mock(ddf.mime.MimeTypeToTransformerMapper.class);
    catalogService.setMimeTypeToTransformerMapper(mapper);
    // Just verify the method doesn't throw an exception
    assertThat(catalogService, is(notNullValue()));
  }

  @Test
  public void testContainsErrorsWithNullProcessingErrors() {
    ReadStorageResponse response = mock(ReadStorageResponse.class);
    when(response.getProcessingErrors()).thenReturn(null);

    boolean result = catalogService.containsErrors(response);

    assertThat(result, is(false));
  }

  @Test
  public void testContainsErrorsWithEmptyProcessingErrors() {
    ReadStorageResponse response = mock(ReadStorageResponse.class);
    when(response.getProcessingErrors()).thenReturn(new HashSet<>());

    boolean result = catalogService.containsErrors(response);

    assertThat(result, is(false));
  }

  @Test
  public void testContainsErrorsWithProcessingErrorWithException() {
    ReadStorageResponse response = mock(ReadStorageResponse.class);
    ProcessingDetails detail = mock(ProcessingDetails.class);
    when(detail.hasException()).thenReturn(true);
    Set<ProcessingDetails> errors = new HashSet<>();
    errors.add(detail);
    when(response.getProcessingErrors()).thenReturn(errors);

    boolean result = catalogService.containsErrors(response);

    assertThat(result, is(true));
  }

  @Test
  public void testContainsErrorsWithProcessingErrorWithoutException() {
    ReadStorageResponse response = mock(ReadStorageResponse.class);
    ProcessingDetails detail = mock(ProcessingDetails.class);
    when(detail.hasException()).thenReturn(false);
    Set<ProcessingDetails> errors = new HashSet<>();
    errors.add(detail);
    when(response.getProcessingErrors()).thenReturn(errors);

    boolean result = catalogService.containsErrors(response);

    assertThat(result, is(false));
  }

  @Test
  public void testDoesLocalResourceExistWithQueryException() throws Exception {
    TestAbstractCatalogServiceWithStorage service =
        new TestAbstractCatalogServiceWithStorage(
            catalogFramework, attachmentParser, attributeRegistry);
    service.setFilterBuilder(filterBuilder);

    when(catalogFramework.query(any()))
        .thenThrow(new UnsupportedQueryException("Query not supported"));

    assertThrows(
        CatalogServiceException.class, () -> service.doesLocalResourceExist("id123", "source1"));
  }

  @Test
  public void testDoesLocalResourceExistWithSourceUnavailable() throws Exception {
    TestAbstractCatalogServiceWithStorage service =
        new TestAbstractCatalogServiceWithStorage(
            catalogFramework, attachmentParser, attributeRegistry);
    service.setFilterBuilder(filterBuilder);

    when(catalogFramework.query(any()))
        .thenThrow(new SourceUnavailableException("Source unavailable"));

    assertThrows(
        CatalogServiceException.class, () -> service.doesLocalResourceExist("id123", "source1"));
  }

  @Test
  public void testDoesLocalResourceExistWithFederationException() throws Exception {
    TestAbstractCatalogServiceWithStorage service =
        new TestAbstractCatalogServiceWithStorage(
            catalogFramework, attachmentParser, attributeRegistry);
    service.setFilterBuilder(filterBuilder);

    when(catalogFramework.query(any())).thenThrow(new FederationException("Federation failed"));

    assertThrows(
        CatalogServiceException.class, () -> service.doesLocalResourceExist("id123", "source1"));
  }

  @Test
  public void testDoesLocalResourceExistWithNoResults() throws Exception {
    TestAbstractCatalogServiceWithStorage service =
        new TestAbstractCatalogServiceWithStorage(
            catalogFramework, attachmentParser, attributeRegistry);
    service.setFilterBuilder(filterBuilder);

    QueryResponse localQueryResponse = mock(QueryResponse.class);
    when(catalogFramework.query(any())).thenReturn(localQueryResponse);
    when(localQueryResponse.getResults()).thenReturn(new ArrayList<>());

    String result = service.doesLocalResourceExist("id123", "source1");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testDoesLocalResourceExistWithMultipleResults() throws Exception {
    TestAbstractCatalogServiceWithStorage service =
        new TestAbstractCatalogServiceWithStorage(
            catalogFramework, attachmentParser, attributeRegistry);
    service.setFilterBuilder(filterBuilder);

    Result result1 = mock(Result.class);
    Result result2 = mock(Result.class);
    List<Result> results = new ArrayList<>();
    results.add(result1);
    results.add(result2);

    QueryResponse localQueryResponse = mock(QueryResponse.class);
    when(catalogFramework.query(any())).thenReturn(localQueryResponse);
    when(localQueryResponse.getResults()).thenReturn(results);

    String result = service.doesLocalResourceExist("id123", "source1");

    // Should return null when there isn't exactly 1 result
    assertThat(result, is(nullValue()));
  }

  @Test
  public void testDoesLocalResourceExistWithNoStorageProviders() throws Exception {
    // Service without storage providers
    TestAbstractCatalogServiceWithStorage service =
        new TestAbstractCatalogServiceWithStorage(
            catalogFramework,
            attachmentParser,
            attributeRegistry,
            Collections.emptyList(),
            Collections.emptyList());
    service.setFilterBuilder(filterBuilder);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("id123");
    metacard.setResourceURI(URI.create("content:id123"));

    Result result = mock(Result.class);
    when(result.getMetacard()).thenReturn(metacard);
    List<Result> results = new ArrayList<>();
    results.add(result);

    QueryResponse localQueryResponse = mock(QueryResponse.class);
    when(catalogFramework.query(any())).thenReturn(localQueryResponse);
    when(localQueryResponse.getResults()).thenReturn(results);

    String checksum = service.doesLocalResourceExist("id123", "source1");

    // Should return null when there isn't exactly one storage provider
    assertThat(checksum, is(nullValue()));
  }

  @Test
  public void testSourceActionToJSONWithNullDescription() {
    ddf.action.Action action = mock(ddf.action.Action.class);
    when(action.getTitle()).thenReturn("Test Title");
    when(action.getId()).thenReturn("test-id");
    when(action.getDescription()).thenReturn(null);
    when(action.getUrl()).thenReturn(createTestUrl());

    JSONObject result = catalogService.sourceActionToJSON(action);

    assertThat(result.get("title"), is(equalTo("Test Title")));
    assertThat(result.get("id"), is(equalTo("test-id")));
    assertThat(result.get("description"), is(nullValue()));
    assertThat(result.get("url"), is(notNullValue()));
  }

  @Test
  public void testGetHeadersWithEmptyIdReturnsNullWhenNoResults() throws Exception {
    MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    URI uri = URI.create("http://localhost:8181/services/catalog");

    QueryResponse localQueryResponse = mock(QueryResponse.class);
    when(catalogFramework.query(any(), any())).thenReturn(localQueryResponse);
    when(localQueryResponse.getResults()).thenReturn(new ArrayList<>());

    BinaryContent result = catalogService.getHeaders("source1", "", uri, queryParams);

    // Empty ID should still attempt query and return null if no results
    assertThat(result, is(nullValue()));
  }

  @Test
  public void testSetUuidGenerator() {
    org.codice.ddf.platform.util.uuidgenerator.UuidGenerator generator =
        mock(org.codice.ddf.platform.util.uuidgenerator.UuidGenerator.class);
    catalogService.setUuidGenerator(generator);
    // Verify the method doesn't throw an exception
    assertThat(catalogService, is(notNullValue()));
  }

  /** Helper method to create a test URL. */
  private java.net.URL createTestUrl() {
    try {
      return new java.net.URL("http://localhost:8181/test");
    } catch (java.net.MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Concrete test implementation of AbstractCatalogService for testing purposes. Only implements
   * the abstract method.
   */
  private static class TestAbstractCatalogService extends AbstractCatalogService {

    public TestAbstractCatalogService(
        CatalogFramework framework,
        AttachmentParser attachmentParser,
        AttributeRegistry attributeRegistry) {
      super(
          framework,
          attachmentParser,
          attributeRegistry,
          Collections.emptyList(),
          Collections.emptyList());
    }

    @Override
    public BinaryContent getSourcesInfo() {
      // Return null for testing purposes
      return null;
    }
  }

  /**
   * Concrete test implementation of AbstractCatalogService with storage providers for testing
   * doesLocalResourceExist.
   */
  private static class TestAbstractCatalogServiceWithStorage extends AbstractCatalogService {

    public TestAbstractCatalogServiceWithStorage(
        CatalogFramework framework,
        AttachmentParser attachmentParser,
        AttributeRegistry attributeRegistry) {
      super(
          framework,
          attachmentParser,
          attributeRegistry,
          Collections.emptyList(),
          Collections.emptyList());
    }

    public TestAbstractCatalogServiceWithStorage(
        CatalogFramework framework,
        AttachmentParser attachmentParser,
        AttributeRegistry attributeRegistry,
        List<StorageProvider> storageProviders,
        List<ChecksumProvider> checksumProviders) {
      super(framework, attachmentParser, attributeRegistry, storageProviders, checksumProviders);
    }

    @Override
    public BinaryContent getSourcesInfo() {
      // Return null for testing purposes
      return null;
    }
  }
}
