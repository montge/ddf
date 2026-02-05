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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.codice.ddf.platform.filter.http.HttpFilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResponseFilterTest {

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  @Mock private HttpFilterChain mockFilterChain;

  private ResponseFilter filter;

  @BeforeEach
  public void setup() {
    filter = new ResponseFilter();
  }

  @Test
  public void testDoFilterAddsDefaultHeaders() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    verify(mockResponse)
        .setHeader(ResponseFilter.X_XSS_PROTECTION, ResponseFilter.DEFAULT_XSS_PROTECTION_VALUE);
    verify(mockResponse)
        .setHeader(ResponseFilter.X_FRAME_OPTIONS, ResponseFilter.DEFAULT_X_FRAME_OPTIONS_VALUE);
    verify(mockResponse)
        .setHeader(
            ResponseFilter.X_CONTENT_SECURITY_POLICY,
            ResponseFilter.DEFAULT_CONTENT_SECURITY_POLICY);
    verify(mockResponse)
        .setHeader(ResponseFilter.CACHE_CONTROL, ResponseFilter.DEFAULT_CACHE_CONTROL_VALUE);
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterDisablesCachingForHtmlFiles() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/page.html");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    // Should set default headers first
    verify(mockResponse)
        .setHeader(ResponseFilter.CACHE_CONTROL, ResponseFilter.DEFAULT_CACHE_CONTROL_VALUE);

    // Then override cache control for HTML
    verify(mockResponse)
        .setHeader(ResponseFilter.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
    verify(mockResponse).setHeader("Pragma", "no-cache");
    verify(mockResponse).setHeader("Expires", "0");
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterDisablesCachingForDirectories() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/search/catalog/");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    // Should override cache control for directory paths
    verify(mockResponse)
        .setHeader(ResponseFilter.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
    verify(mockResponse).setHeader("Pragma", "no-cache");
    verify(mockResponse).setHeader("Expires", "0");
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterKeepsCachingForStaticResources() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/static/app.js");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    // Should keep default cache control for non-HTML files
    verify(mockResponse)
        .setHeader(ResponseFilter.CACHE_CONTROL, ResponseFilter.DEFAULT_CACHE_CONTROL_VALUE);
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithCustomHeaders() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    filter.setHeaders(
        Arrays.asList(
            "Custom-Header=custom-value",
            "Another-Header=another-value",
            ResponseFilter.X_XSS_PROTECTION + "=custom-xss-value"));

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    verify(mockResponse).setHeader("Custom-Header", "custom-value");
    verify(mockResponse).setHeader("Another-Header", "another-value");
    verify(mockResponse).setHeader(ResponseFilter.X_XSS_PROTECTION, "custom-xss-value");
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithMalformedHeader() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    filter.setHeaders(
        Arrays.asList(
            "Valid-Header=valid-value",
            "MalformedHeader", // No equals sign
            "Another-Valid=value"));

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    verify(mockResponse).setHeader("Valid-Header", "valid-value");
    verify(mockResponse).setHeader("Another-Valid", "value");
    // Malformed header should be skipped (logged but not set)
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithEmptyHeaderList() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    filter.setHeaders(Collections.emptyList());

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    // No headers should be set
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithHeaderContainingEquals() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    // Header value contains an equals sign
    filter.setHeaders(Collections.singletonList("Header=value=with=equals"));

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    // Should split only on first equals
    verify(mockResponse).setHeader("Header", "value=with=equals");
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDisableCachingForRootPath() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    verify(mockResponse)
        .setHeader(ResponseFilter.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
    verify(mockResponse).setHeader("Pragma", "no-cache");
    verify(mockResponse).setHeader("Expires", "0");
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithMultipleHtmlExtensions() throws IOException, ServletException {
    String[] htmlPaths = {"/page.html", "/index.html", "/test/nested/page.html"};

    for (String path : htmlPaths) {
      when(mockRequest.getRequestURI()).thenReturn(path);

      filter.doFilter(mockRequest, mockResponse, mockFilterChain);
    }

    // Verify cache control header was set for each HTML path
    verify(mockResponse, times(htmlPaths.length))
        .setHeader(ResponseFilter.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
    verify(mockFilterChain, times(htmlPaths.length)).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithStaticAssets() throws IOException, ServletException {
    String[] staticPaths = {
      "/static/app.js", "/css/style.css", "/images/logo.png", "/fonts/font.woff"
    };

    for (String path : staticPaths) {
      when(mockRequest.getRequestURI()).thenReturn(path);

      filter.doFilter(mockRequest, mockResponse, mockFilterChain);
    }

    // Each should get default cache control (called once per iteration)
    verify(mockResponse, times(staticPaths.length))
        .setHeader(ResponseFilter.CACHE_CONTROL, ResponseFilter.DEFAULT_CACHE_CONTROL_VALUE);
    verify(mockFilterChain, times(staticPaths.length)).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterCallsFilterChain() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testSetHeadersReplacesExistingHeaders() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    // Set initial headers
    filter.setHeaders(Collections.singletonList("Header1=value1"));
    filter.doFilter(mockRequest, mockResponse, mockFilterChain);
    verify(mockResponse).setHeader("Header1", "value1");

    // Replace with new headers
    filter.setHeaders(Collections.singletonList("Header2=value2"));
    filter.doFilter(mockRequest, mockResponse, mockFilterChain);
    verify(mockResponse).setHeader("Header2", "value2");
  }

  @Test
  public void testDoFilterWithNullHeaderValue() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    filter.setHeaders(Arrays.asList("Valid-Header=value", "Header-With-Empty=", "Valid2=val2"));

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    verify(mockResponse).setHeader("Valid-Header", "value");
    verify(mockResponse).setHeader("Header-With-Empty", "");
    verify(mockResponse).setHeader("Valid2", "val2");
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterPreservesFilterChainExecution() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/test/path");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    // Filter chain should always be called
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithAdminPath() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/admin/");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    // Admin paths ending with / should disable caching
    verify(mockResponse)
        .setHeader(ResponseFilter.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
    verify(mockResponse).setHeader("Pragma", "no-cache");
    verify(mockResponse).setHeader("Expires", "0");
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }

  @Test
  public void testDoFilterWithSearchPath() throws IOException, ServletException {
    when(mockRequest.getRequestURI()).thenReturn("/search/catalog/");

    filter.doFilter(mockRequest, mockResponse, mockFilterChain);

    // Search paths ending with / should disable caching
    verify(mockResponse)
        .setHeader(ResponseFilter.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
    verify(mockResponse).setHeader("Pragma", "no-cache");
    verify(mockResponse).setHeader("Expires", "0");
    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
  }
}
