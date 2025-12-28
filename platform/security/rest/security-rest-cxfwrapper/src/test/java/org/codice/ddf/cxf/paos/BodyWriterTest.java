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
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.List;
import javax.xml.stream.XMLStreamWriter;
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
}
