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
package org.codice.ddf.security.servlet.expiry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.codice.ddf.security.session.management.service.SessionManagementService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link SessionManagementEndpoint} class. */
@RunWith(MockitoJUnitRunner.class)
public class SessionManagementEndpointTest {

  @Mock private SessionManagementService sessionManagementService;

  @Mock private HttpServletRequest request;

  private SessionManagementEndpoint endpoint;

  @Before
  public void setUp() {
    endpoint = new SessionManagementEndpoint(sessionManagementService);
  }

  @Test
  public void testConstructor() {
    assertThat(endpoint, is(notNullValue()));
  }

  @Test
  public void testPathAnnotation() {
    Path annotation = SessionManagementEndpoint.class.getAnnotation(Path.class);
    assertThat(annotation, is(notNullValue()));
    assertThat(annotation.value(), is("/"));
  }

  @Test
  public void testGetExpiryReturnsOkResponse() {
    String expiryBody = "{\"expiry\": 3600}";
    when(sessionManagementService.getExpiry(request)).thenReturn(expiryBody);

    Response response = endpoint.getExpiry(request);

    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
  }

  @Test
  public void testGetExpiryReturnsBody() {
    String expiryBody = "{\"expiry\": 3600}";
    when(sessionManagementService.getExpiry(request)).thenReturn(expiryBody);

    Response response = endpoint.getExpiry(request);

    assertThat(response.getEntity(), is(expiryBody));
  }

  @Test
  public void testGetExpiryCallsSessionManagementService() {
    when(sessionManagementService.getExpiry(request)).thenReturn("{}");

    endpoint.getExpiry(request);

    verify(sessionManagementService).getExpiry(request);
  }

  @Test
  public void testGetInvalidateReturnsSeeOtherResponse() throws Exception {
    URI redirectUri = new URI("https://example.com/logout");
    when(sessionManagementService.getInvalidate(request)).thenReturn(redirectUri);

    Response response = endpoint.getInvalidate(request);

    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(Response.Status.SEE_OTHER.getStatusCode()));
  }

  @Test
  public void testGetInvalidateReturnsRedirectLocation() throws Exception {
    URI redirectUri = new URI("https://example.com/logout");
    when(sessionManagementService.getInvalidate(request)).thenReturn(redirectUri);

    Response response = endpoint.getInvalidate(request);

    assertThat(response.getLocation(), is(redirectUri));
  }

  @Test
  public void testGetInvalidateCallsSessionManagementService() throws Exception {
    URI redirectUri = new URI("https://example.com/logout");
    when(sessionManagementService.getInvalidate(request)).thenReturn(redirectUri);

    endpoint.getInvalidate(request);

    verify(sessionManagementService).getInvalidate(request);
  }

  @Test
  public void testGetExpiryWithEmptyBody() {
    when(sessionManagementService.getExpiry(request)).thenReturn("");

    Response response = endpoint.getExpiry(request);

    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    assertThat(response.getEntity(), is(""));
  }

  @Test
  public void testGetExpiryWithNullBody() {
    when(sessionManagementService.getExpiry(request)).thenReturn(null);

    Response response = endpoint.getExpiry(request);

    assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
  }
}
