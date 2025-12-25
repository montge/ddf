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
package org.codice.ddf.ui.searchui.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RedirectServletTest {

  private static final String RELATIVE_URI = "/intrigue/";
  private static final String ABSOLUTE_URI = "https://example.com/search";

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Test
  void testConstructorWithRelativeUri() {
    RedirectServlet servlet = new RedirectServlet(RELATIVE_URI);
    assertThat(servlet, is(notNullValue()));
  }

  @Test
  void testConstructorWithAbsoluteUri() {
    RedirectServlet servlet = new RedirectServlet(ABSOLUTE_URI);
    assertThat(servlet, is(notNullValue()));
  }

  @Test
  void testConstructorWithNullThrowsException() {
    assertThrows(NullPointerException.class, () -> new RedirectServlet(null));
  }

  @Test
  void testConstructorWithBlankUriThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new RedirectServlet("   "));
  }

  @Test
  void testConstructorWithEmptyUriThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new RedirectServlet(""));
  }

  @Test
  void testServiceRedirectsToDefaultUri() throws Exception {
    RedirectServlet servlet = new RedirectServlet(RELATIVE_URI);

    servlet.service(request, response);

    verify(response).sendRedirect(RELATIVE_URI);
  }

  @Test
  void testServiceRedirectsToAbsoluteUri() throws Exception {
    RedirectServlet servlet = new RedirectServlet(ABSOLUTE_URI);

    servlet.service(request, response);

    verify(response).sendRedirect(ABSOLUTE_URI);
  }

  @Test
  void testSetDefaultUriWithRelativeUri() throws Exception {
    RedirectServlet servlet = new RedirectServlet(ABSOLUTE_URI);
    servlet.setDefaultUri(RELATIVE_URI);

    servlet.service(request, response);

    verify(response).sendRedirect(RELATIVE_URI);
  }

  @Test
  void testSetDefaultUriWithAbsoluteUri() throws Exception {
    RedirectServlet servlet = new RedirectServlet(RELATIVE_URI);
    servlet.setDefaultUri(ABSOLUTE_URI);

    servlet.service(request, response);

    verify(response).sendRedirect(ABSOLUTE_URI);
  }

  @Test
  void testSetDefaultUriWithNullThrowsException() {
    RedirectServlet servlet = new RedirectServlet(RELATIVE_URI);
    assertThrows(NullPointerException.class, () -> servlet.setDefaultUri(null));
  }

  @Test
  void testSetDefaultUriWithBlankThrowsException() {
    RedirectServlet servlet = new RedirectServlet(RELATIVE_URI);
    assertThrows(IllegalArgumentException.class, () -> servlet.setDefaultUri("   "));
  }

  @Test
  void testConstructorWithInvalidUriThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new RedirectServlet("://invalid"));
  }
}
