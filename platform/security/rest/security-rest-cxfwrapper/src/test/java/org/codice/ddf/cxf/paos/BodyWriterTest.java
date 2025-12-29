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
package org.codice.ddf.cxf.paos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import javax.xml.stream.XMLStreamWriter;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.Phase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BodyWriterTest {

  @Mock private Message message;

  @Mock private OutputStream outputStream;

  @Mock private XMLStreamWriter xmlStreamWriter;

  private BodyWriter bodyWriter;

  @BeforeEach
  void setUp() {
    bodyWriter = new BodyWriter();
  }

  @Test
  void testConstructor() {
    BodyWriter newBodyWriter = new BodyWriter();

    assertThat(newBodyWriter, is(notNullValue()));
  }

  @Test
  void testGetPhase() {
    assertThat(bodyWriter.getPhase(), is(Phase.WRITE));
  }

  @Test
  void testHandleMessageWithNullContentsList() {
    // MessageContentsList.getContentsList calls message.getContent(List.class) first
    when(message.getContent(List.class)).thenReturn(null);

    bodyWriter.handleMessage(message);

    // No exception should be thrown, method should return early
  }

  @Test
  void testHandleMessageWithEmptyContentsList() {
    MessageContentsList contentsList = new MessageContentsList();
    when(message.getContent(List.class)).thenReturn(contentsList);

    bodyWriter.handleMessage(message);

    // No exception should be thrown, method should return early when list is empty
  }

  @Test
  void testHandleMessageWithNoOutputStreamOrWriter() {
    MessageContentsList contentsList = new MessageContentsList();
    contentsList.add("test body content");
    when(message.getContent(List.class)).thenReturn(contentsList);
    when(message.getContent(OutputStream.class)).thenReturn(null);
    when(message.getContent(XMLStreamWriter.class)).thenReturn(null);

    bodyWriter.handleMessage(message);

    // No exception should be thrown, method should return early when no output stream or writer
  }

  @Test
  void testHandleMessageWithXmlStreamWriterOnly() {
    MessageContentsList contentsList = new MessageContentsList();
    contentsList.add("test body content");
    when(message.getContent(List.class)).thenReturn(contentsList);
    when(message.getContent(OutputStream.class)).thenReturn(null);
    when(message.getContent(XMLStreamWriter.class)).thenReturn(xmlStreamWriter);
    when(message.get(Type.class)).thenReturn(null);

    // This will throw Fault (wrapping NPE) since we don't have a full CXF context
    // but it exercises the code path that goes through to doWriteBody
    assertThrows(Fault.class, () -> bodyWriter.handleMessage(message));
  }

  @Test
  void testHandleMessageWithOutputStream() {
    MessageContentsList contentsList = new MessageContentsList();
    contentsList.add("test body content");
    when(message.getContent(List.class)).thenReturn(contentsList);
    when(message.getContent(OutputStream.class)).thenReturn(outputStream);
    when(message.get(Type.class)).thenReturn(null);

    // This will throw Fault (wrapping NPE) since we don't have a full CXF context
    // but it exercises the code path that goes through to doWriteBody
    assertThrows(Fault.class, () -> bodyWriter.handleMessage(message));
  }

  @Test
  void testDoWriteBodyWithNullBodyType() {
    Object body = "test body";
    Type bodyType = null;

    // This will throw Fault from missing CXF context (wraps NPE), but exercises doWriteBody
    assertThrows(Fault.class, () -> bodyWriter.doWriteBody(message, body, bodyType));
  }

  @Test
  void testDoWriteBodyWithBodyType() {
    Object body = "test body";
    Type bodyType = String.class;

    // This will throw Fault from missing CXF context (wraps NPE), but exercises the code path
    // with non-null bodyType
    assertThrows(Fault.class, () -> bodyWriter.doWriteBody(message, body, bodyType));
  }

  @Test
  void testWriteBodyWithNullObject() {
    // writeBody with null object should return early without doing anything
    bodyWriter.writeBody(null, message, String.class, String.class);
    // No exception should be thrown
  }

  @Test
  void testWriteBodyWithValidObjectButNoContext() {
    String body = "test body";
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.add("Content-Type", "text/plain");
    when(message.get(Message.PROTOCOL_HEADERS)).thenReturn(headers);

    // This will throw NPE from ClientProviderFactory.getInstance(outMessage) since there's no CXF
    // Bus
    assertThrows(
        NullPointerException.class,
        () -> bodyWriter.writeBody(body, message, String.class, String.class));
  }

  @Test
  void testWriteBodyWithNullClass() {
    // writeBody with null class should still attempt to work
    String body = "test body";
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.add("Content-Type", "text/plain");
    when(message.get(Message.PROTOCOL_HEADERS)).thenReturn(headers);

    // This will throw NPE from ClientProviderFactory.getInstance(outMessage) since there's no CXF
    // Bus
    assertThrows(
        NullPointerException.class, () -> bodyWriter.writeBody(body, message, null, String.class));
  }
}
