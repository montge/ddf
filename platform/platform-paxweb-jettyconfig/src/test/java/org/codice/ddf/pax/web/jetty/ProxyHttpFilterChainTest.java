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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.codice.ddf.platform.filter.http.HttpFilter;
import org.codice.ddf.platform.filter.http.HttpFilterChain;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProxyHttpFilterChainTest {

  @Mock private HttpServletRequest mockServletRequest;

  @Mock private HttpServletResponse mockServletResponse;

  @Mock private Handler.Wrapper mockWrapper;

  @Mock private Handler mockHandler;

  @Mock private Request mockRequest;

  @Mock private Response mockResponse;

  @Mock private Callback mockCallback;

  @Mock private HttpFilter mockFilter1;

  @Mock private HttpFilter mockFilter2;

  @Mock private HttpFilter mockFilter3;

  @BeforeEach
  public void setup() {
    // Setup default behavior if needed
  }

  @Test
  public void testDoFilterWithNoFilters() throws Exception {
    when(mockWrapper.getHandler()).thenReturn(mockHandler);
    List<HttpFilter> filters = Collections.emptyList();

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockHandler).handle(mockRequest, mockResponse, mockCallback);
  }

  @Test
  public void testDoFilterWithSingleFilter() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockFilter1)
        .doFilter(eq(mockServletRequest), eq(mockServletResponse), any(HttpFilterChain.class));
  }

  @Test
  public void testDoFilterWithMultipleFilters() throws IOException, ServletException {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2, mockFilter3);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockFilter1)
        .doFilter(eq(mockServletRequest), eq(mockServletResponse), any(HttpFilterChain.class));
    verify(mockFilter2, never()).doFilter(any(), any(), any());
    verify(mockFilter3, never()).doFilter(any(), any(), any());
  }

  @Test
  public void testFilterChainExecutesInOrder() throws Exception {
    when(mockWrapper.getHandler()).thenReturn(mockHandler);
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2, mockFilter3);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    mockFilterBehavior(mockFilter1);
    mockFilterBehavior(mockFilter2);
    mockFilterBehavior(mockFilter3);

    chain.doFilter(mockServletRequest, mockServletResponse);

    InOrder inOrder = inOrder(mockFilter1, mockFilter2, mockFilter3, mockHandler);
    inOrder.verify(mockFilter1).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    inOrder.verify(mockFilter2).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    inOrder.verify(mockFilter3).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    inOrder.verify(mockHandler).handle(mockRequest, mockResponse, mockCallback);
  }

  @Test
  public void testHandlerCalledAfterAllFilters() throws Exception {
    when(mockWrapper.getHandler()).thenReturn(mockHandler);
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    mockFilterBehavior(mockFilter1);
    mockFilterBehavior(mockFilter2);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockFilter1).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    verify(mockFilter2).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    verify(mockHandler).handle(mockRequest, mockResponse, mockCallback);
  }

  @Test
  public void testFilterStopsChain() throws IOException, ServletException {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2, mockFilter3);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    mockFilterBehavior(mockFilter1);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockFilter1).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    verify(mockFilter2).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    verify(mockFilter3, never()).doFilter(any(), any(), any());
  }

  @Test
  public void testEmptyListGoesDirectlyToHandler() throws Exception {
    when(mockWrapper.getHandler()).thenReturn(mockHandler);
    List<HttpFilter> filters = Collections.emptyList();

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockHandler, times(1)).handle(mockRequest, mockResponse, mockCallback);
  }

  @Test
  public void testFilterThrowsIOException() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    org.mockito.Mockito.doThrow(new IOException("Test exception"))
        .when(mockFilter1)
        .doFilter(any(), any(), any());

    assertThrows(IOException.class, () -> chain.doFilter(mockServletRequest, mockServletResponse));
  }

  @Test
  public void testFilterThrowsServletException() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    org.mockito.Mockito.doThrow(new ServletException("Test exception"))
        .when(mockFilter1)
        .doFilter(any(), any(), any());

    assertThrows(
        ServletException.class, () -> chain.doFilter(mockServletRequest, mockServletResponse));
  }

  @Test
  public void testMultipleFiltersWithOneThrowingException() throws Exception {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2, mockFilter3);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    mockFilterBehavior(mockFilter1);
    org.mockito.Mockito.doThrow(new ServletException("Test exception"))
        .when(mockFilter2)
        .doFilter(any(), any(), any());

    try {
      chain.doFilter(mockServletRequest, mockServletResponse);
    } catch (ServletException e) {
      // Expected
    }

    verify(mockFilter1).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    verify(mockFilter2).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    verify(mockFilter3, never()).doFilter(any(), any(), any());
  }

  @Test
  public void testCallbackSucceedsWhenNoWrappedHandler() throws Exception {
    when(mockWrapper.getHandler()).thenReturn(null);
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    mockFilterBehavior(mockFilter1);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockCallback).succeeded();
  }

  @Test
  public void testChainIsNotReusable() throws Exception {
    when(mockWrapper.getHandler()).thenReturn(mockHandler);
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockWrapper, mockRequest, mockResponse, mockCallback);

    mockFilterBehavior(mockFilter1);
    mockFilterBehavior(mockFilter2);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockFilter1, times(1)).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    verify(mockFilter2, times(1)).doFilter(eq(mockServletRequest), eq(mockServletResponse), any());
    verify(mockHandler, times(1)).handle(mockRequest, mockResponse, mockCallback);

    chain.doFilter(mockServletRequest, mockServletResponse);

    verify(mockFilter1, times(1)).doFilter(any(), any(), any());
    verify(mockFilter2, times(1)).doFilter(any(), any(), any());
    verify(mockHandler, times(2)).handle(mockRequest, mockResponse, mockCallback);
  }

  private void mockFilterBehavior(HttpFilter filter) throws IOException, ServletException {
    org.mockito.Mockito.doAnswer(
            invocation -> {
              HttpFilterChain chain = invocation.getArgument(2);
              chain.doFilter(mockServletRequest, mockServletResponse);
              return null;
            })
        .when(filter)
        .doFilter(any(), any(), any());
  }
}
