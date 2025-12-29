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
package ddf.catalog.services.xsltlistener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.BinaryContent;
import jakarta.activation.MimeType;
import jakarta.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class XsltTransformedContentTest {

  @Test
  void testConstructorCreatesValidInstance() throws MimeTypeParseException {
    byte[] content = "test content".getBytes(StandardCharsets.UTF_8);
    MimeType mimeType = new MimeType("text/xml");

    XsltTransformedContent transformedContent = new XsltTransformedContent(content, mimeType);

    assertThat(transformedContent, is(notNullValue()));
  }

  @Test
  void testImplementsBinaryContent() throws MimeTypeParseException {
    byte[] content = "test".getBytes(StandardCharsets.UTF_8);
    MimeType mimeType = new MimeType("text/xml");

    XsltTransformedContent transformedContent = new XsltTransformedContent(content, mimeType);

    assertThat(transformedContent, instanceOf(BinaryContent.class));
  }

  @Test
  void testGetMimeType() throws MimeTypeParseException {
    byte[] content = "test".getBytes(StandardCharsets.UTF_8);
    MimeType mimeType = new MimeType("application/xml");

    XsltTransformedContent transformedContent = new XsltTransformedContent(content, mimeType);

    assertThat(transformedContent.getMimeType(), is(notNullValue()));
    assertThat(transformedContent.getMimeTypeValue(), is("application/xml"));
  }

  @Test
  void testGetInputStream() throws MimeTypeParseException, IOException {
    byte[] content = "test content".getBytes(StandardCharsets.UTF_8);
    MimeType mimeType = new MimeType("text/plain");

    XsltTransformedContent transformedContent = new XsltTransformedContent(content, mimeType);

    InputStream inputStream = transformedContent.getInputStream();
    assertThat(inputStream, is(notNullValue()));
  }

  @Test
  void testContentIsReadable() throws MimeTypeParseException, IOException {
    String originalContent = "Hello, World!";
    byte[] content = originalContent.getBytes(StandardCharsets.UTF_8);
    MimeType mimeType = new MimeType("text/plain");

    XsltTransformedContent transformedContent = new XsltTransformedContent(content, mimeType);

    InputStream inputStream = transformedContent.getInputStream();
    byte[] readContent = inputStream.readAllBytes();
    String result = new String(readContent, StandardCharsets.UTF_8);

    assertThat(result, is(originalContent));
  }

  @Test
  void testWithEmptyContent() throws MimeTypeParseException {
    byte[] content = new byte[0];
    MimeType mimeType = new MimeType("text/xml");

    XsltTransformedContent transformedContent = new XsltTransformedContent(content, mimeType);

    assertThat(transformedContent, is(notNullValue()));
  }

  @Test
  void testWithHtmlMimeType() throws MimeTypeParseException {
    byte[] content = "<html><body>Test</body></html>".getBytes(StandardCharsets.UTF_8);
    MimeType mimeType = new MimeType("text/html");

    XsltTransformedContent transformedContent = new XsltTransformedContent(content, mimeType);

    assertThat(transformedContent.getMimeTypeValue(), is("text/html"));
  }

  @Test
  void testWithJsonMimeType() throws MimeTypeParseException {
    byte[] content = "{\"key\": \"value\"}".getBytes(StandardCharsets.UTF_8);
    MimeType mimeType = new MimeType("application/json");

    XsltTransformedContent transformedContent = new XsltTransformedContent(content, mimeType);

    assertThat(transformedContent.getMimeTypeValue(), is("application/json"));
  }
}
