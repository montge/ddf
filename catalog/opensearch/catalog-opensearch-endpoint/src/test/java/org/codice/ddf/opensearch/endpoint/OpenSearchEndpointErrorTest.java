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
package org.codice.ddf.opensearch.endpoint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.federation.FederationException;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.codice.ddf.configuration.SystemInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Tests error handling in OpenSearchEndpoint */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OpenSearchEndpointErrorTest {

  private static final String TEST_SITE_NAME = "TestSite";

  @Mock private CatalogFramework mockFramework;

  @Mock private UriInfo mockUriInfo;

  @Mock private HttpServletRequest mockRequest;

  @Mock private MultivaluedMap<String, String> mockQueryParams;

  @Mock private BinaryContent mockBinaryContent;

  private FilterBuilder filterBuilder;

  private OpenSearchEndpoint endpoint;

  @BeforeEach
  public void setUp() throws Exception {
    filterBuilder = new GeotoolsFilterBuilder();
    endpoint = new OpenSearchEndpoint(mockFramework, filterBuilder);

    when(mockUriInfo.getRequestUri())
        .thenReturn(new URI("http://localhost:8181/services/catalog/query"));
    when(mockUriInfo.getQueryParameters()).thenReturn(mockQueryParams);
    when(mockQueryParams.get("subscription")).thenReturn(null);
    when(mockQueryParams.get("interval")).thenReturn(null);
    when(mockRequest.getParameterMap()).thenReturn(new java.util.HashMap<>());

    System.setProperty(SystemInfo.SITE_NAME, TEST_SITE_NAME);
  }

  @Test
  public void testQueryWithUnsupportedQueryException() throws Exception {
    when(mockFramework.query(any(QueryRequest.class)))
        .thenThrow(new UnsupportedQueryException("Query not supported"));

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
  }

  @Test
  public void testQueryWithFederationException() throws Exception {
    when(mockFramework.query(any(QueryRequest.class)))
        .thenThrow(new FederationException("Federation failed"));

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
  }

  @Test
  public void testQueryWithSourceUnavailableException() throws Exception {
    when(mockFramework.query(any(QueryRequest.class)))
        .thenThrow(new SourceUnavailableException("Source unavailable"));

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
  }

  @Test
  public void testQueryWithCatalogTransformerException() throws Exception {
    when(mockFramework.query(any(QueryRequest.class)))
        .thenReturn(new ddf.catalog.operation.impl.QueryResponseImpl(null));
    when(mockFramework.transform(any(QueryResponse.class), anyString(), anyMap()))
        .thenThrow(new CatalogTransformerException("Transform failed"));

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
  }

  @Test
  public void testQueryWithRuntimeException() throws Exception {
    when(mockFramework.query(any(QueryRequest.class)))
        .thenThrow(new RuntimeException("Unexpected error"));

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    assertThat(
        response.getEntity().toString(), containsString("RuntimeException on executing query"));
  }

  @Test
  public void testQueryWithNullPointerException() throws Exception {
    when(mockFramework.query(any(QueryRequest.class)))
        .thenThrow(new NullPointerException("Null value encountered"));

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
  }

  @Test
  public void testQueryWithInvalidStartIndex() throws Exception {
    InputStream is = new ByteArrayInputStream("Test Response".getBytes("UTF-8"));
    when(mockBinaryContent.getInputStream()).thenReturn(is);
    when(mockBinaryContent.getMimeTypeValue()).thenReturn("application/atom+xml");
    when(mockFramework.query(any(QueryRequest.class)))
        .thenReturn(new ddf.catalog.operation.impl.QueryResponseImpl(null));
    when(mockFramework.transform(any(QueryResponse.class), anyString(), anyMap()))
        .thenReturn(mockBinaryContent);

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            "invalid", // Invalid start index
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    // Should handle gracefully and use default
  }

  @Test
  public void testQueryWithInvalidCount() throws Exception {
    InputStream is = new ByteArrayInputStream("Test Response".getBytes("UTF-8"));
    when(mockBinaryContent.getInputStream()).thenReturn(is);
    when(mockBinaryContent.getMimeTypeValue()).thenReturn("application/atom+xml");
    when(mockFramework.query(any(QueryRequest.class)))
        .thenReturn(new ddf.catalog.operation.impl.QueryResponseImpl(null));
    when(mockFramework.transform(any(QueryResponse.class), anyString(), anyMap()))
        .thenReturn(mockBinaryContent);

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            "invalid", // Invalid count
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    // Should handle gracefully and use default
  }

  @Test
  public void testQueryWithInvalidTimeout() throws Exception {
    InputStream is = new ByteArrayInputStream("Test Response".getBytes("UTF-8"));
    when(mockBinaryContent.getInputStream()).thenReturn(is);
    when(mockBinaryContent.getMimeTypeValue()).thenReturn("application/atom+xml");
    when(mockFramework.query(any(QueryRequest.class)))
        .thenReturn(new ddf.catalog.operation.impl.QueryResponseImpl(null));
    when(mockFramework.transform(any(QueryResponse.class), anyString(), anyMap()))
        .thenReturn(mockBinaryContent);

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            "invalid", // Invalid timeout
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    // Should handle gracefully and use default
  }

  @Test
  public void testQueryWithEmptySearchTerms() throws Exception {
    InputStream is = new ByteArrayInputStream("Test Response".getBytes("UTF-8"));
    when(mockBinaryContent.getInputStream()).thenReturn(is);
    when(mockBinaryContent.getMimeTypeValue()).thenReturn("application/atom+xml");
    when(mockFramework.query(any(QueryRequest.class)))
        .thenReturn(new ddf.catalog.operation.impl.QueryResponseImpl(null));
    when(mockFramework.transform(any(QueryResponse.class), anyString(), anyMap()))
        .thenReturn(mockBinaryContent);

    Response response =
        endpoint.processQuery(
            "",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
  }

  @Test
  public void testQueryWithWhitespaceSearchTerms() throws Exception {
    InputStream is = new ByteArrayInputStream("Test Response".getBytes("UTF-8"));
    when(mockBinaryContent.getInputStream()).thenReturn(is);
    when(mockBinaryContent.getMimeTypeValue()).thenReturn("application/atom+xml");
    when(mockFramework.query(any(QueryRequest.class)))
        .thenReturn(new ddf.catalog.operation.impl.QueryResponseImpl(null));
    when(mockFramework.transform(any(QueryResponse.class), anyString(), anyMap()))
        .thenReturn(mockBinaryContent);

    Response response =
        endpoint.processQuery(
            "   ",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            mockUriInfo,
            null,
            null,
            mockRequest);

    assertThat(response, notNullValue());
    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
  }
}
