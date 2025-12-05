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
package org.codice.ddf.security.logout.endpoint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.codice.ddf.security.logout.service.LogoutService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link LogoutEndpoint} class. */
@RunWith(MockitoJUnitRunner.class)
public class LogoutEndpointTest {

  @Mock private LogoutService logoutService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  private LogoutEndpoint endpoint;

  @Before
  public void setUp() {
    endpoint = new LogoutEndpoint(logoutService);
  }

  @Test
  public void testConstructor() {
    assertThat(endpoint, is(notNullValue()));
  }

  @Test
  public void testPathAnnotation() {
    Path annotation = LogoutEndpoint.class.getAnnotation(Path.class);
    assertThat(annotation, is(notNullValue()));
    assertThat(annotation.value(), is("/"));
  }

  @Test
  public void testGetActionProvidersReturnsOkResponse() throws Exception {
    String jsonResponse = "{\"actions\":[]}";
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);

    Response result = endpoint.getActionProviders(request, response);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(Response.Status.OK.getStatusCode()));
  }

  @Test
  public void testGetActionProvidersCallsLogoutService() throws Exception {
    String jsonResponse = "{\"actions\":[]}";
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);

    endpoint.getActionProviders(request, response);

    verify(logoutService).getActionProviders(request, response);
  }

  @Test
  public void testGetActionProvidersReturnsInputStream() throws Exception {
    String jsonResponse = "{\"actions\":[{\"id\":\"test\"}]}";
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);

    Response result = endpoint.getActionProviders(request, response);

    assertThat(result.getEntity() instanceof InputStream, is(true));
  }

  @Test
  public void testGetActionProvidersSetsCacheControlHeader() throws Exception {
    String jsonResponse = "{\"actions\":[]}";
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);

    Response result = endpoint.getActionProviders(request, response);

    assertThat(result.getHeaderString("Cache-Control"), is("no-cache, no-store"));
  }

  @Test
  public void testGetActionProvidersSetsPragmaHeader() throws Exception {
    String jsonResponse = "{\"actions\":[]}";
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);

    Response result = endpoint.getActionProviders(request, response);

    assertThat(result.getHeaderString("Pragma"), is("no-cache"));
  }

  @Test
  public void testGetActionProvidersWithEmptyJsonResponse() throws Exception {
    String jsonResponse = "{}";
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);

    Response result = endpoint.getActionProviders(request, response);

    assertThat(result.getStatus(), is(Response.Status.OK.getStatusCode()));
  }

  @Test
  public void testGetActionProvidersResponseContainsCorrectContent() throws Exception {
    String jsonResponse = "{\"test\":\"value\"}";
    when(logoutService.getActionProviders(request, response)).thenReturn(jsonResponse);

    Response result = endpoint.getActionProviders(request, response);

    ByteArrayInputStream entity = (ByteArrayInputStream) result.getEntity();
    byte[] bytes = new byte[entity.available()];
    entity.read(bytes);
    String content = new String(bytes);
    assertThat(content, is(jsonResponse));
  }
}
