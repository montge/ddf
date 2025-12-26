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
package org.codice.proxy.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

class HttpProxyWrappedCleanRequestTest {

  @Test
  void testGetQueryStringWithTrailingAmpersand() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getQueryString()).thenReturn("param1=value1&param2=value2&");

    HttpProxyWrappedCleanRequest wrappedRequest = new HttpProxyWrappedCleanRequest(mockRequest);

    assertThat(wrappedRequest.getQueryString(), is("param1=value1&param2=value2"));
  }

  @Test
  void testGetQueryStringWithoutTrailingAmpersand() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getQueryString()).thenReturn("param1=value1&param2=value2");

    HttpProxyWrappedCleanRequest wrappedRequest = new HttpProxyWrappedCleanRequest(mockRequest);

    assertThat(wrappedRequest.getQueryString(), is("param1=value1&param2=value2"));
  }

  @Test
  void testGetQueryStringWithNull() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getQueryString()).thenReturn(null);

    HttpProxyWrappedCleanRequest wrappedRequest = new HttpProxyWrappedCleanRequest(mockRequest);

    assertThat(wrappedRequest.getQueryString(), is(nullValue()));
  }

  @Test
  void testGetQueryStringWithEmptyString() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getQueryString()).thenReturn("");

    HttpProxyWrappedCleanRequest wrappedRequest = new HttpProxyWrappedCleanRequest(mockRequest);

    assertThat(wrappedRequest.getQueryString(), is(""));
  }

  @Test
  void testGetQueryStringWithOnlyAmpersand() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getQueryString()).thenReturn("&");

    HttpProxyWrappedCleanRequest wrappedRequest = new HttpProxyWrappedCleanRequest(mockRequest);

    assertThat(wrappedRequest.getQueryString(), is(""));
  }

  @Test
  void testGetQueryStringWithMultipleTrailingAmpersands() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    // Only removes one trailing ampersand per spec
    when(mockRequest.getQueryString()).thenReturn("param=value&&");

    HttpProxyWrappedCleanRequest wrappedRequest = new HttpProxyWrappedCleanRequest(mockRequest);

    // Should only remove the last ampersand
    assertThat(wrappedRequest.getQueryString(), is("param=value&"));
  }

  @Test
  void testGetQueryStringWithSingleParam() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getQueryString()).thenReturn("param=value");

    HttpProxyWrappedCleanRequest wrappedRequest = new HttpProxyWrappedCleanRequest(mockRequest);

    assertThat(wrappedRequest.getQueryString(), is("param=value"));
  }

  @Test
  void testExtendsHttpServletRequestWrapper() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpProxyWrappedCleanRequest wrappedRequest = new HttpProxyWrappedCleanRequest(mockRequest);

    assertThat(
        javax.servlet.http.HttpServletRequestWrapper.class.isAssignableFrom(
            wrappedRequest.getClass()),
        is(true));
  }
}
