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
package org.codice.ddf.pax.web.jetty;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DoPrivilegedFilterTest {

  private final DoPrivilegedFilter underTest = new DoPrivilegedFilter();

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  @Mock private ProxyHttpFilterChain mockFilterChain;

  @Test
  public void testDefaultDoFilter() throws Exception {
    underTest.doFilter(mockRequest, mockResponse, mockFilterChain);
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testIoException() {
    doThrow(IOException.class).when(mockFilterChain).doFilter(mockRequest, mockResponse);
    assertThrows(
        IOException.class, () -> underTest.doFilter(mockRequest, mockResponse, mockFilterChain));
  }

  @Test
  public void testServletException() {
    doThrow(ServletException.class).when(mockFilterChain).doFilter(mockRequest, mockResponse);
    assertThrows(
        ServletException.class,
        () -> underTest.doFilter(mockRequest, mockResponse, mockFilterChain));
  }
}
