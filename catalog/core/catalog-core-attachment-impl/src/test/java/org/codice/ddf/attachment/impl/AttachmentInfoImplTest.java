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
package org.codice.ddf.attachment.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class AttachmentInfoImplTest {

  @Test
  void testConstructorAndGetters() {
    InputStream stream = new ByteArrayInputStream("test content".getBytes());
    String filename = "test.txt";
    String contentType = "text/plain";

    AttachmentInfoImpl attachmentInfo = new AttachmentInfoImpl(stream, filename, contentType);

    assertThat(attachmentInfo.getStream(), is(sameInstance(stream)));
    assertThat(attachmentInfo.getFilename(), is("test.txt"));
    assertThat(attachmentInfo.getContentType(), is("text/plain"));
  }

  @Test
  void testGetStream() {
    InputStream stream = new ByteArrayInputStream("data".getBytes());

    AttachmentInfoImpl attachmentInfo =
        new AttachmentInfoImpl(stream, "file.bin", "application/octet-stream");

    assertThat(attachmentInfo.getStream(), is(sameInstance(stream)));
  }

  @Test
  void testGetFilename() {
    AttachmentInfoImpl attachmentInfo =
        new AttachmentInfoImpl(
            new ByteArrayInputStream(new byte[0]), "document.pdf", "application/pdf");

    assertThat(attachmentInfo.getFilename(), is("document.pdf"));
  }

  @Test
  void testGetContentType() {
    AttachmentInfoImpl attachmentInfo =
        new AttachmentInfoImpl(new ByteArrayInputStream(new byte[0]), "image.png", "image/png");

    assertThat(attachmentInfo.getContentType(), is("image/png"));
  }

  @Test
  void testWithNullStream() {
    AttachmentInfoImpl attachmentInfo = new AttachmentInfoImpl(null, "file.txt", "text/plain");

    assertThat(attachmentInfo.getStream(), is(nullValue()));
    assertThat(attachmentInfo.getFilename(), is("file.txt"));
    assertThat(attachmentInfo.getContentType(), is("text/plain"));
  }

  @Test
  void testWithNullFilename() {
    InputStream stream = new ByteArrayInputStream("content".getBytes());

    AttachmentInfoImpl attachmentInfo = new AttachmentInfoImpl(stream, null, "text/plain");

    assertThat(attachmentInfo.getStream(), is(sameInstance(stream)));
    assertThat(attachmentInfo.getFilename(), is(nullValue()));
    assertThat(attachmentInfo.getContentType(), is("text/plain"));
  }

  @Test
  void testWithNullContentType() {
    InputStream stream = new ByteArrayInputStream("content".getBytes());

    AttachmentInfoImpl attachmentInfo = new AttachmentInfoImpl(stream, "file.txt", null);

    assertThat(attachmentInfo.getStream(), is(sameInstance(stream)));
    assertThat(attachmentInfo.getFilename(), is("file.txt"));
    assertThat(attachmentInfo.getContentType(), is(nullValue()));
  }

  @Test
  void testWithAllNullValues() {
    AttachmentInfoImpl attachmentInfo = new AttachmentInfoImpl(null, null, null);

    assertThat(attachmentInfo.getStream(), is(nullValue()));
    assertThat(attachmentInfo.getFilename(), is(nullValue()));
    assertThat(attachmentInfo.getContentType(), is(nullValue()));
  }

  @Test
  void testWithEmptyStrings() {
    InputStream stream = new ByteArrayInputStream(new byte[0]);

    AttachmentInfoImpl attachmentInfo = new AttachmentInfoImpl(stream, "", "");

    assertThat(attachmentInfo.getFilename(), is(""));
    assertThat(attachmentInfo.getContentType(), is(""));
  }
}
