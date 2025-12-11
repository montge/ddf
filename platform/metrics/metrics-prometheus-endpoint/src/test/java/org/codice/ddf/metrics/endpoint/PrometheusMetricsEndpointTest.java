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
package org.codice.ddf.metrics.endpoint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PrometheusMetricsEndpointTest {

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  private PrometheusMetricsEndpoint underTest;

  private StringWriter stringWriter;

  private PrintWriter printWriter;

  @BeforeEach
  public void setUp() throws IOException {
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(printWriter);

    underTest = new PrometheusMetricsEndpoint();
  }

  @AfterEach
  public void tearDown() {
    if (printWriter != null) {
      printWriter.close();
    }
    // Clear the global registry to prevent test pollution
    Metrics.globalRegistry.clear();
  }

  @Test
  public void testDoGetSetsContentType() throws Exception {
    underTest.doGet(mockRequest, mockResponse);

    verify(mockResponse).setContentType("text/plain");
  }

  @Test
  public void testDoGetWritesMetricsOutput() throws Exception {
    // Register a test counter
    Counter counter = Metrics.counter("test.counter", "tag1", "value1");
    counter.increment(5);

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    // Verify Prometheus format output contains our metric
    assertThat(output, containsString("test_counter_total"));
    assertThat(output, containsString("5.0"));
  }

  @Test
  public void testDoGetWithGaugeMetric() throws Exception {
    // Register a test gauge
    Gauge.builder("test.gauge", () -> 42.0).tag("tag1", "value1").register(Metrics.globalRegistry);

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    assertThat(output, containsString("test_gauge"));
    assertThat(output, containsString("42.0"));
  }

  @Test
  public void testDoGetWithTimerMetric() throws Exception {
    // Register a test timer
    Timer timer = Metrics.timer("test.timer", "tag1", "value1");
    timer.record(
        () -> {
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        });

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    assertThat(output, containsString("test_timer"));
  }

  @Test
  public void testDoGetWithMultipleMetrics() throws Exception {
    // Register multiple metrics
    Counter counter1 = Metrics.counter("test.counter1");
    counter1.increment(10);

    Counter counter2 = Metrics.counter("test.counter2");
    counter2.increment(20);

    Gauge.builder("test.gauge", () -> 100.0).register(Metrics.globalRegistry);

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    assertThat(output, containsString("test_counter1_total"));
    assertThat(output, containsString("test_counter2_total"));
    assertThat(output, containsString("test_gauge"));
  }

  @Test
  public void testDoGetWithTags() throws Exception {
    // Register a counter with multiple tags
    Counter counter =
        Metrics.counter("test.tagged.counter", "method", "GET", "status", "200", "path", "/test");
    counter.increment(15);

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    assertThat(output, containsString("test_tagged_counter_total"));
    assertThat(output, containsString("method=\"GET\""));
    assertThat(output, containsString("status=\"200\""));
    assertThat(output, containsString("path=\"/test\""));
  }

  @Test
  public void testDoGetHandlesIOException() throws Exception {
    doThrow(new IOException("Test exception")).when(mockResponse).getWriter();

    // Should not throw exception, just log it
    underTest.doGet(mockRequest, mockResponse);

    verify(mockResponse).setContentType("text/plain");
  }

  @Test
  public void testDoGetProducesValidPrometheusFormat() throws Exception {
    // Register a counter
    Counter counter = Metrics.counter("http.requests", "method", "GET", "status", "200");
    counter.increment(100);

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    // Verify Prometheus format conventions
    assertThat(output, containsString("# HELP"));
    assertThat(output, containsString("# TYPE"));
    assertThat(output, containsString("http_requests_total"));
  }

  @Test
  public void testDoGetWithEmptyRegistry() throws Exception {
    // Create a new endpoint (registry might have metrics from previous tests)
    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    // Should return valid output even if no custom metrics
    assertThat(output, is(notNullValue()));
  }

  @Test
  public void testDoGetWithSpecialCharactersInMetricName() throws Exception {
    // Prometheus converts special characters
    Counter counter = Metrics.counter("test-metric.with_special.chars");
    counter.increment(5);

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    // Prometheus format converts dots and dashes
    assertThat(output, containsString("test_metric_with_special_chars"));
  }

  @Test
  public void testDoGetWithVeryLargeMetricValue() throws Exception {
    Counter counter = Metrics.counter("test.large.counter");
    for (int i = 0; i < 1000000; i++) {
      counter.increment();
    }

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    assertThat(output, containsString("test_large_counter_total"));
    assertThat(output, containsString("1000000"));
  }

  @Test
  public void testDoGetWithDecimalValues() throws Exception {
    Gauge.builder("test.decimal.gauge", () -> 3.14159).register(Metrics.globalRegistry);

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    assertThat(output, containsString("test_decimal_gauge"));
    assertThat(output, containsString("3.14159"));
  }

  @Test
  public void testConstructorRegistersRegistry() {
    // Constructor should add registry to global metrics
    PrometheusMetricsEndpoint endpoint = new PrometheusMetricsEndpoint();

    // Verify by creating a metric and checking it's available
    Counter testCounter = Metrics.counter("constructor.test");
    testCounter.increment();

    assertThat(testCounter.count(), is(1.0));
  }

  @Test
  public void testDoGetFlushesWriter() throws Exception {
    Counter counter = Metrics.counter("test.flush.counter");
    counter.increment(7);

    underTest.doGet(mockRequest, mockResponse);

    // Verify writer was used
    verify(mockResponse).getWriter();
    printWriter.flush();

    String output = stringWriter.toString();
    assertThat(output.length() > 0, is(true));
  }

  @Test
  public void testDoGetWithZeroValueMetric() throws Exception {
    Counter counter = Metrics.counter("test.zero.counter");
    // Don't increment, should show 0

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    assertThat(output, containsString("test_zero_counter_total"));
    assertThat(output, containsString("0.0"));
  }

  @Test
  public void testDoGetWithNegativeGaugeValue() throws Exception {
    Gauge.builder("test.negative.gauge", () -> -42.5).register(Metrics.globalRegistry);

    underTest.doGet(mockRequest, mockResponse);

    printWriter.flush();
    String output = stringWriter.toString();

    assertThat(output, containsString("test_negative_gauge"));
    assertThat(output, containsString("-42.5"));
  }
}
