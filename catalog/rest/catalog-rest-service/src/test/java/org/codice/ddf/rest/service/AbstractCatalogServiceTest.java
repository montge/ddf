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
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.AttributeRegistry;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.QueryResponse;
import ddf.mime.MimeTypeResolver;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import net.minidev.json.JSONObject;
import org.codice.ddf.attachment.AttachmentParser;
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
  public void testGetHeadersWithNullId() throws CatalogServiceException {
    MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    URI uri = URI.create("http://localhost:8181/services/catalog");

    BinaryContent result = catalogService.getHeaders(null, null, uri, queryParams);

    assertThat(result, is(nullValue()));
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
      super(framework, attachmentParser, attributeRegistry);
    }

    @Override
    public BinaryContent getSourcesInfo() {
      // Return null for testing purposes
      return null;
    }
  }
}
