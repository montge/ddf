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
package ddf.catalog.util.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.Constants;
import ddf.catalog.operation.Request;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RequestsTest {

  @Test
  void testIsEnterpriseReturnsFalseForNullRequest() {
    assertThat(Requests.isEnterprise(null), is(false));
  }

  @Test
  void testIsEnterpriseReturnsFalseWhenNoProperties() {
    Request request = mock(Request.class);
    when(request.hasProperties()).thenReturn(false);

    assertThat(Requests.isEnterprise(request), is(false));
  }

  @Test
  void testIsEnterpriseReturnsFalseWhenRemoteDestinationKeyNull() {
    Request request = mock(Request.class);
    when(request.hasProperties()).thenReturn(true);
    when(request.getPropertyValue(Constants.REMOTE_DESTINATION_KEY)).thenReturn(null);

    assertThat(Requests.isEnterprise(request), is(false));
  }

  @Test
  void testIsEnterpriseReturnsFalseWhenRemoteDestinationFalse() {
    Request request = mock(Request.class);
    when(request.hasProperties()).thenReturn(true);
    when(request.getPropertyValue(Constants.REMOTE_DESTINATION_KEY)).thenReturn(false);

    assertThat(Requests.isEnterprise(request), is(false));
  }

  @Test
  void testIsEnterpriseReturnsTrueWhenRemoteDestinationTrue() {
    Request request = mock(Request.class);
    when(request.hasProperties()).thenReturn(true);
    when(request.getPropertyValue(Constants.REMOTE_DESTINATION_KEY)).thenReturn(true);

    assertThat(Requests.isEnterprise(request), is(true));
  }

  @Test
  void testIsLocalReturnsTrueForNullRequest() {
    assertThat(Requests.isLocal((Request) null), is(true));
  }

  @Test
  void testIsLocalReturnsTrueWhenNoProperties() {
    Request request = mock(Request.class);
    when(request.hasProperties()).thenReturn(false);

    assertThat(Requests.isLocal(request), is(true));
  }

  @Test
  void testIsLocalReturnsTrueWhenLocalDestinationKeyNull() {
    Request request = mock(Request.class);
    when(request.hasProperties()).thenReturn(true);
    Map<String, Serializable> props = new HashMap<>();
    when(request.getProperties()).thenReturn(props);

    assertThat(Requests.isLocal(request), is(true));
  }

  @Test
  void testIsLocalReturnsTrueWhenLocalDestinationTrue() {
    Request request = mock(Request.class);
    when(request.hasProperties()).thenReturn(true);
    Map<String, Serializable> props = new HashMap<>();
    props.put(Constants.LOCAL_DESTINATION_KEY, true);
    when(request.getProperties()).thenReturn(props);

    assertThat(Requests.isLocal(request), is(true));
  }

  @Test
  void testIsLocalReturnsFalseWhenLocalDestinationFalse() {
    Request request = mock(Request.class);
    when(request.hasProperties()).thenReturn(true);
    Map<String, Serializable> props = new HashMap<>();
    props.put(Constants.LOCAL_DESTINATION_KEY, false);
    when(request.getProperties()).thenReturn(props);

    assertThat(Requests.isLocal(request), is(false));
  }

  @Test
  void testIsLocalMapReturnsTrueForNullMap() {
    assertThat(Requests.isLocal((Map<String, Serializable>) null), is(true));
  }

  @Test
  void testIsLocalMapReturnsTrueWhenLocalDestinationKeyNull() {
    Map<String, Serializable> props = new HashMap<>();

    assertThat(Requests.isLocal(props), is(true));
  }

  @Test
  void testIsLocalMapReturnsTrueWhenLocalDestinationTrue() {
    Map<String, Serializable> props = new HashMap<>();
    props.put(Constants.LOCAL_DESTINATION_KEY, true);

    assertThat(Requests.isLocal(props), is(true));
  }

  @Test
  void testIsLocalMapReturnsFalseWhenLocalDestinationFalse() {
    Map<String, Serializable> props = new HashMap<>();
    props.put(Constants.LOCAL_DESTINATION_KEY, false);

    assertThat(Requests.isLocal(props), is(false));
  }
}
