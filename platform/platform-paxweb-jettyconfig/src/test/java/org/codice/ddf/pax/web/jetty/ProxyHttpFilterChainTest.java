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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codice.ddf.platform.filter.http.HttpFilter;
import org.codice.ddf.platform.filter.http.HttpFilterChain;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProxyHttpFilterChainTest {

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  @Mock private Handler mockHandler;

  @Mock private Request mockBaseRequest;

  @Mock private HttpFilter mockFilter1;

  @Mock private HttpFilter mockFilter2;

  @Mock private HttpFilter mockFilter3;

  private static final String TARGET = "/test/target";

  @BeforeEach
  public void setup() {
    // Setup default behavior if needed
  }

  @Test
  public void testDoFilterWithNoFilters() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.emptyList();

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    chain.doFilter(mockRequest, mockResponse);

    // With no filters, should call handler directly
    verify(mockHandler).handle(TARGET, mockBaseRequest, mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithSingleFilter() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    chain.doFilter(mockRequest, mockResponse);

    // Should call the filter
    verify(mockFilter1).doFilter(eq(mockRequest), eq(mockResponse), any(HttpFilterChain.class));
    // Handler should not be called yet (filter hasn't called chain.doFilter)
    verify(mockHandler, never()).handle(TARGET, mockBaseRequest, mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithMultipleFilters() throws IOException, ServletException {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2, mockFilter3);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    chain.doFilter(mockRequest, mockResponse);

    // Should call first filter
    verify(mockFilter1).doFilter(eq(mockRequest), eq(mockResponse), any(HttpFilterChain.class));
    // Other filters not called yet
    verify(mockFilter2, never()).doFilter(any(), any(), any());
    verify(mockFilter3, never()).doFilter(any(), any(), any());
  }

  @Test
  public void testFilterChainExecutesInOrder() throws Exception {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2, mockFilter3);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    // Mock filters to call chain.doFilter
    mockFilterBehavior(mockFilter1);
    mockFilterBehavior(mockFilter2);
    mockFilterBehavior(mockFilter3);

    chain.doFilter(mockRequest, mockResponse);

    InOrder inOrder = inOrder(mockFilter1, mockFilter2, mockFilter3, mockHandler);
    inOrder.verify(mockFilter1).doFilter(eq(mockRequest), eq(mockResponse), any());
    inOrder.verify(mockFilter2).doFilter(eq(mockRequest), eq(mockResponse), any());
    inOrder.verify(mockFilter3).doFilter(eq(mockRequest), eq(mockResponse), any());
    inOrder.verify(mockHandler).handle(TARGET, mockBaseRequest, mockRequest, mockResponse);
  }

  @Test
  public void testHandlerCalledAfterAllFilters() throws Exception {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    // Mock filters to call chain.doFilter
    mockFilterBehavior(mockFilter1);
    mockFilterBehavior(mockFilter2);

    chain.doFilter(mockRequest, mockResponse);

    verify(mockFilter1).doFilter(eq(mockRequest), eq(mockResponse), any());
    verify(mockFilter2).doFilter(eq(mockRequest), eq(mockResponse), any());
    verify(mockHandler).handle(TARGET, mockBaseRequest, mockRequest, mockResponse);
  }

  @Test
  public void testFilterStopsChain() throws IOException, ServletException {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2, mockFilter3);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    // First filter calls chain.doFilter
    mockFilterBehavior(mockFilter1);
    // Second filter does NOT call chain.doFilter (stops the chain)
    // mockFilter2 default behavior is to do nothing

    chain.doFilter(mockRequest, mockResponse);

    verify(mockFilter1).doFilter(eq(mockRequest), eq(mockResponse), any());
    verify(mockFilter2).doFilter(eq(mockRequest), eq(mockResponse), any());
    // Third filter should not be called
    verify(mockFilter3, never()).doFilter(any(), any(), any());
    // Handler should not be called
    verify(mockHandler, never()).handle(TARGET, mockBaseRequest, mockRequest, mockResponse);
  }

  @Test
  public void testEmptyListGoesDirectlyToHandler() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.emptyList();

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    chain.doFilter(mockRequest, mockResponse);

    verify(mockHandler, times(1)).handle(TARGET, mockBaseRequest, mockRequest, mockResponse);
  }

  @Test
  public void testChainPassesCorrectParameters() throws Exception {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    String customTarget = "/custom/target";
    Request customBaseRequest = mock(Request.class);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, customTarget, customBaseRequest);

    mockFilterBehavior(mockFilter1);

    chain.doFilter(mockRequest, mockResponse);

    verify(mockHandler).handle(customTarget, customBaseRequest, mockRequest, mockResponse);
  }

  @Test
  public void testFilterThrowsIOException() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    // Mock filter to throw IOException
    org.mockito.Mockito.doThrow(new IOException("Test exception"))
        .when(mockFilter1)
        .doFilter(any(), any(), any());

    assertThrows(IOException.class, () -> chain.doFilter(mockRequest, mockResponse));
  }

  @Test
  public void testFilterThrowsServletException() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    // Mock filter to throw ServletException
    org.mockito.Mockito.doThrow(new ServletException("Test exception"))
        .when(mockFilter1)
        .doFilter(any(), any(), any());

    assertThrows(ServletException.class, () -> chain.doFilter(mockRequest, mockResponse));
  }

  @Test
  public void testMultipleFiltersWithOneThrowingException() throws Exception {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2, mockFilter3);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    mockFilterBehavior(mockFilter1);
    // Second filter throws exception
    org.mockito.Mockito.doThrow(new ServletException("Test exception"))
        .when(mockFilter2)
        .doFilter(any(), any(), any());

    try {
      chain.doFilter(mockRequest, mockResponse);
    } catch (ServletException e) {
      // Expected
    }

    verify(mockFilter1).doFilter(eq(mockRequest), eq(mockResponse), any());
    verify(mockFilter2).doFilter(eq(mockRequest), eq(mockResponse), any());
    verify(mockFilter3, never()).doFilter(any(), any(), any());
    verify(mockHandler, never()).handle(any(), any(), any(), any());
  }

  @Test
  public void testChainWithNullTarget() throws Exception {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, null, mockBaseRequest);

    mockFilterBehavior(mockFilter1);

    chain.doFilter(mockRequest, mockResponse);

    verify(mockHandler).handle(null, mockBaseRequest, mockRequest, mockResponse);
  }

  @Test
  public void testChainWithNullHandler() throws IOException, ServletException {
    List<HttpFilter> filters = Collections.singletonList(mockFilter1);

    ProxyHttpFilterChain chain = new ProxyHttpFilterChain(filters, null, TARGET, mockBaseRequest);

    mockFilterBehavior(mockFilter1);

    // The single filter re-enters the chain, exhausting the iterator, so the chain falls
    // through to handler.handle(...) on the null handler, which throws NPE.
    assertThrows(NullPointerException.class, () -> chain.doFilter(mockRequest, mockResponse));
  }

  @Test
  public void testChainIsNotReusable() throws Exception {
    List<HttpFilter> filters = Arrays.asList(mockFilter1, mockFilter2);

    ProxyHttpFilterChain chain =
        new ProxyHttpFilterChain(filters, mockHandler, TARGET, mockBaseRequest);

    mockFilterBehavior(mockFilter1);
    mockFilterBehavior(mockFilter2);

    // First call should work normally
    chain.doFilter(mockRequest, mockResponse);

    verify(mockFilter1, times(1)).doFilter(eq(mockRequest), eq(mockResponse), any());
    verify(mockFilter2, times(1)).doFilter(eq(mockRequest), eq(mockResponse), any());
    verify(mockHandler, times(1)).handle(TARGET, mockBaseRequest, mockRequest, mockResponse);

    // Second call should go directly to handler (iterator exhausted)
    chain.doFilter(mockRequest, mockResponse);

    // Filters should not be called again
    verify(mockFilter1, times(1)).doFilter(any(), any(), any());
    verify(mockFilter2, times(1)).doFilter(any(), any(), any());
    // Handler should be called again
    verify(mockHandler, times(2)).handle(TARGET, mockBaseRequest, mockRequest, mockResponse);
  }

  /** Helper method to mock filter behavior so it calls chain.doFilter */
  private void mockFilterBehavior(HttpFilter filter) throws IOException, ServletException {
    org.mockito.Mockito.doAnswer(
            invocation -> {
              HttpFilterChain chain = invocation.getArgument(2);
              chain.doFilter(mockRequest, mockResponse);
              return null;
            })
        .when(filter)
        .doFilter(any(), any(), any());
  }
}
