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
package ddf.camel.component.catalog.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.BinaryContent;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BinaryContentStringTypeConverterTest {

  private BinaryContentStringTypeConverter converter;
  private Exchange mockExchange;
  private Message mockMessage;

  @BeforeEach
  void setUp() {
    converter = new BinaryContentStringTypeConverter();
    mockExchange = mock(Exchange.class);
    mockMessage = mock(Message.class);
    when(mockExchange.getMessage()).thenReturn(mockMessage);
  }

  @Test
  void testConvertToWithTextPlainContentType() {
    String testContent = "test content";
    InputStream inputStream =
        new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class))
        .thenReturn(MediaType.TEXT_PLAIN);
    when(mockMessage.getBody(InputStream.class)).thenReturn(inputStream);

    BinaryContent result = converter.convertTo(BinaryContent.class, mockExchange, testContent);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeType().getBaseType(), is("text/plain"));
  }

  @Test
  void testConvertToWithXmlContentType() {
    String testContent = "<xml>test</xml>";
    InputStream inputStream =
        new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class))
        .thenReturn(MediaType.APPLICATION_XML);
    when(mockMessage.getBody(InputStream.class)).thenReturn(inputStream);

    BinaryContent result = converter.convertTo(BinaryContent.class, mockExchange, testContent);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeType().getBaseType(), is("application/xml"));
  }

  @Test
  void testConvertToWithJsonContentType() {
    String testContent = "{\"key\":\"value\"}";
    InputStream inputStream =
        new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class))
        .thenReturn(MediaType.APPLICATION_JSON);
    when(mockMessage.getBody(InputStream.class)).thenReturn(inputStream);

    BinaryContent result = converter.convertTo(BinaryContent.class, mockExchange, testContent);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeType().getBaseType(), is("application/json"));
  }

  @Test
  void testConvertToWithNullContentTypeDefaultsToTextPlain() {
    String testContent = "test content";
    InputStream inputStream =
        new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class)).thenReturn(null);
    when(mockMessage.getBody(InputStream.class)).thenReturn(inputStream);

    BinaryContent result = converter.convertTo(BinaryContent.class, mockExchange, testContent);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeType().getBaseType(), is("text/plain"));
  }

  @Test
  void testConvertToWithInvalidMimeType() {
    String testContent = "test content";
    InputStream inputStream =
        new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class))
        .thenReturn("invalid-mime-type");
    when(mockMessage.getBody(InputStream.class)).thenReturn(inputStream);

    BinaryContent result = converter.convertTo(BinaryContent.class, mockExchange, testContent);

    assertThat(result, is(notNullValue()));
  }

  @Test
  void testConvertToWithNullInputStream() {
    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class))
        .thenReturn(MediaType.TEXT_PLAIN);
    when(mockMessage.getBody(InputStream.class)).thenReturn(null);

    BinaryContent result = converter.convertTo(BinaryContent.class, mockExchange, "test");

    assertThat(result, is(notNullValue()));
  }

  @Test
  void testConvertToWithOctetStreamContentType() {
    String testContent = "binary data";
    InputStream inputStream =
        new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class))
        .thenReturn(MediaType.APPLICATION_OCTET_STREAM);
    when(mockMessage.getBody(InputStream.class)).thenReturn(inputStream);

    BinaryContent result = converter.convertTo(BinaryContent.class, mockExchange, testContent);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeType().getBaseType(), is("application/octet-stream"));
  }

  @Test
  void testConvertToWithWrongTargetTypeReturnsNull() {
    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class))
        .thenReturn(MediaType.TEXT_PLAIN);
    when(mockMessage.getBody(InputStream.class)).thenReturn(null);

    String result = converter.convertTo(String.class, mockExchange, "test");

    assertThat(result, is(nullValue()));
  }

  @Test
  void testConvertToWithContentTypeContainingCharset() {
    String testContent = "test content";
    InputStream inputStream =
        new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

    when(mockMessage.getHeader(HttpHeaders.CONTENT_TYPE, String.class))
        .thenReturn("text/plain; charset=UTF-8");
    when(mockMessage.getBody(InputStream.class)).thenReturn(inputStream);

    BinaryContent result = converter.convertTo(BinaryContent.class, mockExchange, testContent);

    assertThat(result, is(notNullValue()));
    assertThat(result.getMimeType().getBaseType(), is("text/plain"));
  }
}
