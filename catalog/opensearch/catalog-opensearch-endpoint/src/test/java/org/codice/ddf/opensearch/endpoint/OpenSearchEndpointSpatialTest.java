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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.operation.impl.QueryResponseImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.codice.ddf.configuration.SystemInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests spatial parameter handling in OpenSearchEndpoint */
@RunWith(MockitoJUnitRunner.class)
public class OpenSearchEndpointSpatialTest {

  private static final String TEST_SITE_NAME = "TestSite";

  @Mock private CatalogFramework mockFramework;

  @Mock private UriInfo mockUriInfo;

  @Mock private HttpServletRequest mockRequest;

  @Mock private MultivaluedMap<String, String> mockQueryParams;

  @Mock private BinaryContent mockBinaryContent;

  private FilterBuilder filterBuilder;

  private OpenSearchEndpoint endpoint;

  @Before
  public void setUp() throws Exception {
    filterBuilder = new GeotoolsFilterBuilder();
    endpoint = new OpenSearchEndpoint(mockFramework, filterBuilder);

    when(mockUriInfo.getRequestUri())
        .thenReturn(new URI("http://localhost:8181/services/catalog/query"));
    when(mockUriInfo.getQueryParameters()).thenReturn(mockQueryParams);
    when(mockQueryParams.get("subscription")).thenReturn(null);
    when(mockQueryParams.get("interval")).thenReturn(null);
    when(mockRequest.getParameterMap()).thenReturn(new java.util.HashMap<>());

    InputStream is = new ByteArrayInputStream("Test Response".getBytes("UTF-8"));
    when(mockBinaryContent.getInputStream()).thenReturn(is);
    when(mockBinaryContent.getMimeTypeValue()).thenReturn("application/atom+xml");

    when(mockFramework.query(any(QueryRequest.class)))
        .thenAnswer(invocation -> new QueryResponseImpl(invocation.getArgument(0)));

    when(mockFramework.transform(any(QueryResponse.class), anyString(), anyMap()))
        .thenReturn(mockBinaryContent);

    System.setProperty(SystemInfo.SITE_NAME, TEST_SITE_NAME);
  }

  @Test
  public void testBBoxSpatialFilter() throws Exception {
    String bbox = "-10.0,-20.0,10.0,20.0";

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            null,
            bbox,
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

    ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
    verify(mockFramework).query(captor.capture());

    assertThat(captor.getValue(), notNullValue());
  }

  @Test
  public void testPointRadiusSpatialFilter() throws Exception {
    String lat = "40.7128";
    String lon = "-74.0060";
    String radius = "10000";

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
            lat,
            lon,
            radius,
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
  public void testPointRadiusWithDefaultRadius() throws Exception {
    String lat = "40.7128";
    String lon = "-74.0060";

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
            lat,
            lon,
            null, // No radius, should use default
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
  public void testGeometrySpatialFilter() throws Exception {
    String geometry = "POINT(10 20)";

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            geometry,
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
  public void testPolygonSpatialFilter() throws Exception {
    String polygon = "10,20,30,40,50,60,10,20";

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
            polygon,
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
  public void testLatWithoutLon() throws Exception {
    // Lat without lon should be ignored
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
            "40.7128",
            null, // No lon
            "5000",
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
  public void testLonWithoutLat() throws Exception {
    // Lon without lat should be ignored
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
            null, // No lat
            "-74.0060",
            "5000",
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
  public void testMultipleSpatialParameters() throws Exception {
    // When multiple spatial parameters are provided, all should be processed
    String geometry = "POINT(10 20)";
    String bbox = "-10.0,-20.0,10.0,20.0";

    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            geometry,
            bbox,
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
  public void testEmptySpatialParameters() throws Exception {
    // Empty strings should be ignored
    Response response =
        endpoint.processQuery(
            "test",
            null,
            null,
            null,
            null,
            null,
            "",
            "",
            "",
            "",
            "",
            "",
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
