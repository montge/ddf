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
package org.codice.ddf.spatial.ogc.wfs.catalog.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link MarkableStreamInterceptor} class. */
@ExtendWith(MockitoExtension.class)
public class MarkableStreamInterceptorTest {

  @Mock private Message message;

  private MarkableStreamInterceptor interceptor;

  @BeforeEach
  public void setUp() {
    interceptor = new MarkableStreamInterceptor();
  }

  @Test
  public void testExtendsAbstractPhaseInterceptor() {
    assertThat(interceptor instanceof AbstractPhaseInterceptor, is(true));
  }

  @Test
  public void testPhaseIsPreStream() {
    assertThat(interceptor.getPhase(), is(Phase.PRE_STREAM));
  }

  @Test
  public void testHandleMessageConvertsToBufferedStream() throws Exception {
    String content = "test content";
    InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    when(message.getContent(InputStream.class)).thenReturn(inputStream);

    interceptor.handleMessage(message);

    ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);
    verify(message).setContent(eq(InputStream.class), captor.capture());
    assertThat(captor.getValue(), instanceOf(ByteArrayInputStream.class));
  }

  @Test
  public void testHandleMessagePreservesContent() throws Exception {
    String content = "test content";
    InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    when(message.getContent(InputStream.class)).thenReturn(inputStream);

    interceptor.handleMessage(message);

    ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);
    verify(message).setContent(eq(InputStream.class), captor.capture());

    byte[] buffer = new byte[content.length()];
    captor.getValue().read(buffer);
    assertThat(new String(buffer, StandardCharsets.UTF_8), is(content));
  }

  @Test
  public void testHandleMessageWithNullInputStream() {
    when(message.getContent(InputStream.class)).thenReturn(null);

    // Should not throw - handles NullPointerException internally
    interceptor.handleMessage(message);
  }

  @Test
  public void testHandleMessageWithIOException() throws Exception {
    InputStream mockStream = org.mockito.Mockito.mock(InputStream.class);
    when(message.getContent(InputStream.class)).thenReturn(mockStream);
    doThrow(new IOException("test error")).when(mockStream).read(any(byte[].class));

    // Should not throw - handles IOException internally
    interceptor.handleMessage(message);
  }
}
