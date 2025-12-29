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
package org.codice.ddf.spatial.geocoder.endpoint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import org.codice.ddf.spatial.geocoding.GeoCoderService;
import org.codice.ddf.spatial.geocoding.GeoEntryQueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeoCoderEndpointTest {

  private static final String TEST_JSONP = "callback";
  private static final String TEST_QUERY = "New York";
  private static final String TEST_JSON_RESULT = "{\"lat\":40.7128,\"lon\":-74.0060}";
  private static final String TEST_WKT = "POINT(-74.0060 40.7128)";

  @Mock private GeoCoderService geoCoderService;

  private GeoCoderEndpoint endpoint;

  @BeforeEach
  void setUp() {
    endpoint = new GeoCoderEndpoint(geoCoderService);
  }

  @Test
  void testConstructor() {
    assertThat(endpoint, is(notNullValue()));
  }

  @Test
  void testGetLocationReturnsOkWithJsonp() {
    when(geoCoderService.getLocation(TEST_JSONP, TEST_QUERY)).thenReturn(TEST_JSON_RESULT);

    Response response = endpoint.getLocation(TEST_JSONP, TEST_QUERY);

    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    String entity = (String) response.getEntity();
    assertThat(entity, containsString(TEST_JSONP));
    assertThat(entity, containsString(TEST_JSON_RESULT));
  }

  @Test
  void testGetLocationReturnsBadRequestWhenNull() {
    when(geoCoderService.getLocation(TEST_JSONP, TEST_QUERY)).thenReturn(null);

    Response response = endpoint.getLocation(TEST_JSONP, TEST_QUERY);

    assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
  }

  @Test
  void testGetLocationEscapesHtmlInJsonp() {
    String maliciousJsonp = "<script>alert('xss')</script>";
    when(geoCoderService.getLocation(maliciousJsonp, TEST_QUERY)).thenReturn(TEST_JSON_RESULT);

    Response response = endpoint.getLocation(maliciousJsonp, TEST_QUERY);

    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    String entity = (String) response.getEntity();
    assertThat(entity.contains("<script>"), is(false));
    assertThat(entity, containsString("&lt;script&gt;"));
  }

  @Test
  void testGetNearbyCitiesReturnsOk() throws Exception {
    when(geoCoderService.getNearbyCities(TEST_WKT)).thenReturn(TEST_JSON_RESULT);

    Response response = endpoint.getNearbyCities(TEST_WKT);

    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    assertThat(response.getEntity(), is(TEST_JSON_RESULT));
  }

  @Test
  void testGetNearbyCitiesReturnsNoContentWhenNull() throws Exception {
    when(geoCoderService.getNearbyCities(TEST_WKT)).thenReturn(null);

    Response response = endpoint.getNearbyCities(TEST_WKT);

    assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
  }

  @Test
  void testGetNearbyCitiesReturnsBadRequestOnException() throws Exception {
    when(geoCoderService.getNearbyCities(anyString()))
        .thenThrow(new GeoEntryQueryException("Query failed"));

    Response response = endpoint.getNearbyCities(TEST_WKT);

    assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
  }

  @Test
  void testGetLocationWithEmptyQuery() {
    when(geoCoderService.getLocation(TEST_JSONP, "")).thenReturn(null);

    Response response = endpoint.getLocation(TEST_JSONP, "");

    assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
  }

  @Test
  void testGetLocationWithNullJsonp() {
    when(geoCoderService.getLocation(null, TEST_QUERY)).thenReturn(TEST_JSON_RESULT);

    Response response = endpoint.getLocation(null, TEST_QUERY);

    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
  }
}
