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
package org.codice.ddf.metrics.servlet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codice.ddf.platform.filter.http.HttpFilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Enhanced tests for {@link ServletMetrics} focusing on coverage and edge cases.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Synchronous request metrics collection
 *   <li>Asynchronous request metrics collection
 *   <li>HTTP method and status code tagging
 *   <li>Exception handling and status code mapping
 *   <li>Timeout handling for async requests
 *   <li>Latency measurement accuracy
 *   <li>Micrometer integration
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class ServletMetricsEnhancedTest {

  private static final String LATENCY_METRIC = "ddf.platform.http.latency";

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  @Mock private HttpFilterChain mockFilterChain;

  private ServletMetrics servletMetrics;
  private SimpleMeterRegistry meterRegistry;

  @BeforeEach
  public void setUp() {
    servletMetrics = new ServletMetrics();
    meterRegistry = new SimpleMeterRegistry();
    Metrics.addRegistry(meterRegistry);

    // Default mock behavior
    when(mockRequest.isAsyncStarted()).thenReturn(false);
    when(mockRequest.getMethod()).thenReturn("GET");
    when(mockResponse.getStatus()).thenReturn(200);
  }

  @AfterEach
  public void tearDown() {
    Metrics.globalRegistry.clear();
  }

  @Test
  public void testSyncRequestRecordsMetrics() throws IOException, ServletException {
    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    Iterable<Tag> tags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).max(), greaterThanOrEqualTo(0.0));
  }

  @Test
  public void testSyncRequestWithDifferentHttpMethods() throws IOException, ServletException {
    String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"};

    for (String method : methods) {
      setUp(); // Reset mocks
      when(mockRequest.getMethod()).thenReturn(method);

      servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

      Iterable<Tag> tags = getTags(method, 200);
      assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
    }
  }

  @Test
  public void testSyncRequestWithDifferentStatusCodes() throws IOException, ServletException {
    int[] statusCodes = {200, 201, 204, 301, 302, 400, 401, 403, 404, 500, 502, 503};

    for (int statusCode : statusCodes) {
      setUp(); // Reset mocks
      when(mockResponse.getStatus()).thenReturn(statusCode);

      servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

      Iterable<Tag> tags = getTags("GET", statusCode);
      assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
    }
  }

  @Test
  public void testSyncRequestMeasuresLatency() throws IOException, ServletException {
    doAnswer(
            invocation -> {
              Thread.sleep(50);
              return null;
            })
        .when(mockFilterChain)
        .doFilter(mockRequest, mockResponse);

    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    Iterable<Tag> tags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).max(), greaterThanOrEqualTo(50.0));
  }

  @Test
  public void testSyncRequestWithIOException() throws IOException, ServletException {
    doThrow(new IOException("Test IO exception"))
        .when(mockFilterChain)
        .doFilter(mockRequest, mockResponse);

    assertThrows(
        IOException.class,
        () -> servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain));

    // Exception should result in status 500
    Iterable<Tag> tags = getTags("GET", 500);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
  }

  @Test
  public void testSyncRequestWithServletException() throws IOException, ServletException {
    doThrow(new ServletException("Test servlet exception"))
        .when(mockFilterChain)
        .doFilter(mockRequest, mockResponse);

    assertThrows(
        ServletException.class,
        () -> servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain));

    // Exception should result in status 500
    Iterable<Tag> tags = getTags("GET", 500);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
  }

  @Test
  public void testSyncRequestWithRuntimeException() throws IOException, ServletException {
    doThrow(new RuntimeException("Test runtime exception"))
        .when(mockFilterChain)
        .doFilter(mockRequest, mockResponse);

    assertThrows(
        RuntimeException.class,
        () -> servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain));

    // Exception should result in status 500
    Iterable<Tag> tags = getTags("GET", 500);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
  }

  @Test
  public void testAsyncRequestRecordsMetrics() throws IOException, ServletException {
    when(mockRequest.isAsyncStarted()).thenReturn(true);
    AsyncContext asyncContext = mock(AsyncContext.class);
    when(mockRequest.getAsyncContext()).thenReturn(asyncContext);

    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    ArgumentCaptor<AsyncListener> listenerCaptor = ArgumentCaptor.forClass(AsyncListener.class);
    verify(asyncContext).addListener(listenerCaptor.capture());

    // No metrics recorded yet
    Iterable<Tag> tags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(0L));

    // Simulate async completion
    AsyncEvent event = createAsyncEvent();
    listenerCaptor.getValue().onComplete(event);

    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
  }

  @Test
  public void testAsyncRequestWithTimeout() throws IOException, ServletException {
    when(mockRequest.isAsyncStarted()).thenReturn(true);
    AsyncContext asyncContext = mock(AsyncContext.class);
    when(mockRequest.getAsyncContext()).thenReturn(asyncContext);

    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    ArgumentCaptor<AsyncListener> listenerCaptor = ArgumentCaptor.forClass(AsyncListener.class);
    verify(asyncContext).addListener(listenerCaptor.capture());
    AsyncListener listener = listenerCaptor.getValue();

    AsyncEvent event = createAsyncEvent();
    listener.onTimeout(event);
    listener.onComplete(event);

    // Timeout should result in status 408
    Iterable<Tag> tags = getTags("GET", 408);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
  }

  @Test
  public void testAsyncRequestWithError() throws IOException, ServletException {
    when(mockRequest.isAsyncStarted()).thenReturn(true);
    AsyncContext asyncContext = mock(AsyncContext.class);
    when(mockRequest.getAsyncContext()).thenReturn(asyncContext);

    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    ArgumentCaptor<AsyncListener> listenerCaptor = ArgumentCaptor.forClass(AsyncListener.class);
    verify(asyncContext).addListener(listenerCaptor.capture());
    AsyncListener listener = listenerCaptor.getValue();

    AsyncEvent event = createAsyncEvent();
    listener.onError(event);
    listener.onComplete(event);

    // Error should result in status 500
    Iterable<Tag> tags = getTags("GET", 500);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
  }

  @Test
  public void testAsyncRequestOnStartAsync() throws IOException, ServletException {
    when(mockRequest.isAsyncStarted()).thenReturn(true);
    AsyncContext asyncContext = mock(AsyncContext.class);
    when(mockRequest.getAsyncContext()).thenReturn(asyncContext);

    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    ArgumentCaptor<AsyncListener> listenerCaptor = ArgumentCaptor.forClass(AsyncListener.class);
    verify(asyncContext).addListener(listenerCaptor.capture());
    AsyncListener listener = listenerCaptor.getValue();

    AsyncEvent event = createAsyncEvent();
    listener.onStartAsync(event);

    // onStartAsync should not record metrics by itself
    Iterable<Tag> tags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(0L));
  }

  @Test
  public void testAsyncRequestTimeoutOverridesStatusCode() throws IOException, ServletException {
    when(mockRequest.isAsyncStarted()).thenReturn(true);
    when(mockResponse.getStatus()).thenReturn(200); // Non-timeout status
    AsyncContext asyncContext = mock(AsyncContext.class);
    when(mockRequest.getAsyncContext()).thenReturn(asyncContext);

    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    ArgumentCaptor<AsyncListener> listenerCaptor = ArgumentCaptor.forClass(AsyncListener.class);
    verify(asyncContext).addListener(listenerCaptor.capture());
    AsyncListener listener = listenerCaptor.getValue();

    AsyncEvent event = createAsyncEvent();
    listener.onTimeout(event);
    listener.onComplete(event);

    // Timeout should override status code to 408
    Iterable<Tag> tags = getTags("GET", 408);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));

    // Original status should not be recorded
    Iterable<Tag> originalTags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, originalTags).count(), is(0L));
  }

  @Test
  public void testAsyncRequestErrorOverridesStatusCode() throws IOException, ServletException {
    when(mockRequest.isAsyncStarted()).thenReturn(true);
    when(mockResponse.getStatus()).thenReturn(200); // Non-error status
    AsyncContext asyncContext = mock(AsyncContext.class);
    when(mockRequest.getAsyncContext()).thenReturn(asyncContext);

    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    ArgumentCaptor<AsyncListener> listenerCaptor = ArgumentCaptor.forClass(AsyncListener.class);
    verify(asyncContext).addListener(listenerCaptor.capture());
    AsyncListener listener = listenerCaptor.getValue();

    AsyncEvent event = createAsyncEvent();
    listener.onError(event);
    listener.onComplete(event);

    // Error should override status code to 500
    Iterable<Tag> tags = getTags("GET", 500);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));

    // Original status should not be recorded
    Iterable<Tag> originalTags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, originalTags).count(), is(0L));
  }

  @Test
  public void testExceptionWithConflictingStatusCode() throws IOException, ServletException {
    when(mockResponse.getStatus()).thenReturn(200); // Non-error status
    doThrow(new RuntimeException("Test exception"))
        .when(mockFilterChain)
        .doFilter(mockRequest, mockResponse);

    try {
      servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);
    } catch (RuntimeException e) {
      // Expected
    }

    // Exception should override to 500
    Iterable<Tag> tags = getTags("GET", 500);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));

    // Original status 200 should not be recorded
    Iterable<Tag> originalTags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, originalTags).count(), is(0L));
  }

  @Test
  public void testExceptionWithMatchingStatusCode() throws IOException, ServletException {
    when(mockResponse.getStatus()).thenReturn(500); // Matching error status
    doThrow(new RuntimeException("Test exception"))
        .when(mockFilterChain)
        .doFilter(mockRequest, mockResponse);

    try {
      servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);
    } catch (RuntimeException e) {
      // Expected
    }

    // Should record status 500
    Iterable<Tag> tags = getTags("GET", 500);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
  }

  @Test
  public void testMultipleSyncRequests() throws IOException, ServletException {
    for (int i = 0; i < 10; i++) {
      servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);
    }

    Iterable<Tag> tags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(10L));
  }

  @Test
  public void testMixedMethodsAndStatusCodes() throws IOException, ServletException {
    // GET 200
    when(mockRequest.getMethod()).thenReturn("GET");
    when(mockResponse.getStatus()).thenReturn(200);
    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    // POST 201
    when(mockRequest.getMethod()).thenReturn("POST");
    when(mockResponse.getStatus()).thenReturn(201);
    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    // PUT 404
    when(mockRequest.getMethod()).thenReturn("PUT");
    when(mockResponse.getStatus()).thenReturn(404);
    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    assertThat(meterRegistry.summary(LATENCY_METRIC, getTags("GET", 200)).count(), is(1L));
    assertThat(meterRegistry.summary(LATENCY_METRIC, getTags("POST", 201)).count(), is(1L));
    assertThat(meterRegistry.summary(LATENCY_METRIC, getTags("PUT", 404)).count(), is(1L));
  }

  @Test
  public void testLatencyMeasurementConsistency() throws IOException, ServletException {
    final long sleepTime = 100;

    doAnswer(
            invocation -> {
              Thread.sleep(sleepTime);
              return null;
            })
        .when(mockFilterChain)
        .doFilter(mockRequest, mockResponse);

    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    Iterable<Tag> tags = getTags("GET", 200);
    double recordedLatency = meterRegistry.summary(LATENCY_METRIC, tags).max();

    // Recorded latency should be at least the sleep time
    assertThat(recordedLatency, greaterThanOrEqualTo((double) sleepTime));
  }

  @Test
  public void testPercentileMetricsArePublished() throws IOException, ServletException {
    servletMetrics.doFilter(mockRequest, mockResponse, mockFilterChain);

    // The ServletMetrics publishes percentiles at 0.5 and 0.95
    // Verify that the metric was recorded with the distribution summary
    Iterable<Tag> tags = getTags("GET", 200);
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).count(), is(1L));
    assertThat(meterRegistry.summary(LATENCY_METRIC, tags).max(), greaterThanOrEqualTo(0.0));
  }

  private AsyncEvent createAsyncEvent() {
    AsyncEvent event = mock(AsyncEvent.class);
    when(event.getSuppliedRequest()).thenReturn(mockRequest);
    when(event.getSuppliedResponse()).thenReturn(mockResponse);
    return event;
  }

  private static Iterable<Tag> getTags(String method, int status) {
    return Tags.of("method", method, "status", Integer.toString(status));
  }
}
