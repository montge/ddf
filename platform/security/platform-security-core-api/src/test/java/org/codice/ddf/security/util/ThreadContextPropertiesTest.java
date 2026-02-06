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
package org.codice.ddf.security.util;

import static org.codice.ddf.security.util.ThreadContextProperties.CLIENT_INFO_KEY;
import static org.codice.ddf.security.util.ThreadContextProperties.SERVLET_CONTEXT_PATH;
import static org.codice.ddf.security.util.ThreadContextProperties.SERVLET_REMOTE_ADDR;
import static org.codice.ddf.security.util.ThreadContextProperties.SERVLET_REMOTE_HOST;
import static org.codice.ddf.security.util.ThreadContextProperties.SERVLET_REMOTE_PORT;
import static org.codice.ddf.security.util.ThreadContextProperties.SERVLET_SCHEME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import com.google.common.net.HttpHeaders;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;
import org.apache.shiro.util.ThreadContext;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ThreadContextPropertiesTest {

  public static final String TRACE_CONTEXT_KEY = "trace-context";

  private static final String TRACE_ID = "trace-id";

  private static final String MOCK_REMOTE_ADDRESS = "127.0.0.1";

  private static final String MOCK_FORWARDED_REMOTE_ADDRESS = "192.168.1.1";

  private static final String MOCK_REMOTE_PORT = "2222";

  private static final String MOCK_FORWARDED_REMOTE_PORT = "3333";

  private static final String MOCK_REMOTE_HOST = "localhost";

  private static final String MOCK_FORWARDED_REMOTE_HOST = "clientHost";

  private static final String MOCK_SCHEME = "http";

  private static final String MOCK_CONTEXT_PATH = "/example/path";

  @Mock private ServletContext mockServletContext;

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletRequest jettyRequest;

  @BeforeEach
  public void setup() throws Exception {
    when(mockRequest.getRemoteAddr()).thenReturn(MOCK_REMOTE_ADDRESS);
    when(mockRequest.getRemotePort()).thenReturn(Integer.parseInt(MOCK_REMOTE_PORT));
    when(mockRequest.getRemoteHost()).thenReturn(MOCK_REMOTE_HOST);
    when(mockRequest.getScheme()).thenReturn(MOCK_SCHEME);
    when(mockRequest.getServletContext()).thenReturn(mockServletContext);
    when(mockServletContext.getContextPath()).thenReturn(MOCK_CONTEXT_PATH);

    when(jettyRequest.getHeader(HttpHeaders.X_FORWARDED_FOR.toString()))
        .thenReturn(MOCK_FORWARDED_REMOTE_ADDRESS);
    when(jettyRequest.getHeader(HttpHeaders.X_FORWARDED_PORT.toString()))
        .thenReturn(MOCK_FORWARDED_REMOTE_PORT);
    when(jettyRequest.getHeader(HttpHeaders.X_FORWARDED_HOST.toString()))
        .thenReturn(MOCK_FORWARDED_REMOTE_HOST);
    when(jettyRequest.getScheme()).thenReturn(MOCK_SCHEME);
    when(jettyRequest.getServletContext()).thenReturn(mockServletContext);
  }

  @AfterEach
  public void tearDown() throws Exception {
    ThreadContextProperties.removeClientInfo();
    ThreadContextProperties.removeTraceId();
  }

  @Test
  public void testAddTraceContext() throws Exception {
    String traceId = ThreadContextProperties.addTraceId();
    assertThatTraceContextMapIsAccurate(traceId);
    assertThat(traceId.equals(ThreadContextProperties.getTraceId()), is(true));
    ThreadContext.remove(TRACE_CONTEXT_KEY);
    ThreadContextProperties.removeTraceId();
    assertThat(ThreadContextProperties.getTraceId(), is(nullValue()));
  }

  @Test
  public void testGetTraceContextWhenMissing() throws Exception {
    String traceId = ThreadContextProperties.getTraceId();
    assertThat(traceId, is(nullValue()));
  }

  private void assertThatTraceContextMapIsAccurate(String expectedTraceId) throws Exception {
    Map<String, String> traceContext = (Map<String, String>) ThreadContext.get(TRACE_CONTEXT_KEY);
    assertThat(traceContext, notNullValue());
    String traceId = traceContext.get(TRACE_ID);
    assertThat(traceId, notNullValue());
    assertThat(expectedTraceId.equals(traceId), is(true));
  }

  @Test
  public void testAddClientInfoServletRequest() throws Exception {
    ThreadContextProperties.addClientInfo(mockRequest);
    assertThatClientInfoMapIsAccurate();
    ThreadContextProperties.removeClientInfo();
    assertThatClientInfoMapIsNull();
  }

  @Test
  public void testAddClientInfoServletRequestXForwardedHeaderSet() throws Exception {
    ThreadContextProperties.addClientInfo(jettyRequest);
    assertThatClientInfoMapIsAccurateForwardedHeaderSet();
    ThreadContextProperties.removeClientInfo();
    assertThatClientInfoMapIsNull();
  }

  @Test
  public void testAddClientInfoInetSocketAddressRequestURI() throws Exception {
    URI requestUri = new URI("http", "localhost", MOCK_CONTEXT_PATH, null);
    ThreadContextProperties.addClientInfo(
        MOCK_REMOTE_ADDRESS, MOCK_REMOTE_HOST, MOCK_REMOTE_PORT, requestUri);
    assertThatClientInfoMapIsAccurate();
    ThreadContextProperties.removeClientInfo();
    assertThatClientInfoMapIsNull();
  }

  @Test
  public void testAddClientInfoNullRequestURI() throws Exception {
    ThreadContextProperties.addClientInfo(
        MOCK_REMOTE_ADDRESS, MOCK_REMOTE_HOST, MOCK_REMOTE_PORT, null);
    Map<String, String> clientInfoMap = (Map<String, String>) ThreadContext.get(CLIENT_INFO_KEY);
    assertThat(clientInfoMap, CoreMatchers.notNullValue());
    assertThat(clientInfoMap.get(SERVLET_REMOTE_ADDR), is(MOCK_REMOTE_ADDRESS));
    assertThat(clientInfoMap.get(SERVLET_REMOTE_PORT), is(MOCK_REMOTE_PORT));
    assertThat(clientInfoMap.get(SERVLET_REMOTE_HOST), is(MOCK_REMOTE_HOST));
    assertThat(clientInfoMap.get(SERVLET_SCHEME), is(nullValue()));
    assertThat(clientInfoMap.get(SERVLET_CONTEXT_PATH), is(nullValue()));
    ThreadContextProperties.removeClientInfo();
    assertThatClientInfoMapIsNull();
  }

  @Test
  public void testGetClientIP() throws Exception {
    ThreadContextProperties.addClientInfo(mockRequest);
    assertThat(ThreadContextProperties.getRemoteAddress(), is(MOCK_REMOTE_ADDRESS));
  }

  @Test
  public void testGetClientPort() throws Exception {
    ThreadContextProperties.addClientInfo(mockRequest);
    assertThat(ThreadContextProperties.getRemotePort(), is(MOCK_REMOTE_PORT));
  }

  @Test
  public void testGetClientHost() throws Exception {
    ThreadContextProperties.addClientInfo(mockRequest);
    assertThat(ThreadContextProperties.getRemoteHost(), is(MOCK_REMOTE_HOST));
  }

  @Test
  public void testGetContextPath() throws Exception {
    ThreadContextProperties.addClientInfo(mockRequest);
    assertThat(ThreadContextProperties.getContextPath(), is(MOCK_CONTEXT_PATH));
  }

  @Test
  public void testGetScheme() throws Exception {
    ThreadContextProperties.addClientInfo(mockRequest);
    assertThat(ThreadContextProperties.getScheme(), is(MOCK_SCHEME));
  }

  @Test
  public void testGetClientIPIsNull() throws Exception {
    assertThat(ThreadContextProperties.getRemoteAddress(), is(nullValue()));
  }

  @Test
  public void testGetClientPortIsNull() throws Exception {
    assertThat(ThreadContextProperties.getRemotePort(), is(nullValue()));
  }

  @Test
  public void testGetClientHostIsNull() throws Exception {
    assertThat(ThreadContextProperties.getRemoteHost(), is(nullValue()));
  }

  @Test
  public void testGetContextPathIsNull() throws Exception {
    assertThat(ThreadContextProperties.getRemoteHost(), is(nullValue()));
  }

  @Test
  public void testGetSchemeIsNull() throws Exception {
    assertThat(ThreadContextProperties.getRemoteHost(), is(nullValue()));
  }

  private void assertThatClientInfoMapIsAccurate() throws Exception {
    Map<String, String> clientInfoMap = (Map<String, String>) ThreadContext.get(CLIENT_INFO_KEY);
    assertThat(clientInfoMap, CoreMatchers.notNullValue());
    assertThat(clientInfoMap.get(SERVLET_REMOTE_ADDR), is(MOCK_REMOTE_ADDRESS));
    assertThat(clientInfoMap.get(SERVLET_REMOTE_PORT), is(MOCK_REMOTE_PORT));
    assertThat(clientInfoMap.get(SERVLET_REMOTE_HOST), is(MOCK_REMOTE_HOST));
    assertThat(clientInfoMap.get(SERVLET_SCHEME), is(MOCK_SCHEME));
    assertThat(clientInfoMap.get(SERVLET_CONTEXT_PATH), is(MOCK_CONTEXT_PATH));
  }

  private void assertThatClientInfoMapIsAccurateForwardedHeaderSet() throws Exception {
    Map<String, String> clientInfoMap = (Map<String, String>) ThreadContext.get(CLIENT_INFO_KEY);
    assertThat(clientInfoMap, CoreMatchers.notNullValue());
    assertThat(clientInfoMap.get(SERVLET_REMOTE_ADDR), is(MOCK_FORWARDED_REMOTE_ADDRESS));
    assertThat(clientInfoMap.get(SERVLET_REMOTE_PORT), is(MOCK_FORWARDED_REMOTE_PORT));
    assertThat(clientInfoMap.get(SERVLET_REMOTE_HOST), is(MOCK_FORWARDED_REMOTE_HOST));
    assertThat(clientInfoMap.get(SERVLET_SCHEME), is(MOCK_SCHEME));
    assertThat(clientInfoMap.get(SERVLET_CONTEXT_PATH), is(MOCK_CONTEXT_PATH));
  }

  private void assertThatClientInfoMapIsNull() throws Exception {
    Map<String, String> clientInfoMap = (Map<String, String>) ThreadContext.get(CLIENT_INFO_KEY);
    assertThat(clientInfoMap, CoreMatchers.nullValue());
  }
}
