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
package ddf.catalog.resource.download;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class ReliableResourceStatusTest {

  @Test
  void testConstructor() {
    ReliableResourceStatus status =
        new ReliableResourceStatus(
            ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_COMPLETE, 1024L);

    assertThat(
        status.getDownloadStatus(),
        is(ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_COMPLETE));
    assertThat(status.getBytesRead(), is(1024L));
  }

  @Test
  void testGetAndSetBytesRead() {
    ReliableResourceStatus status =
        new ReliableResourceStatus(
            ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_COMPLETE, 0L);

    status.setBytesRead(2048L);

    assertThat(status.getBytesRead(), is(2048L));
  }

  @Test
  void testGetAndSetDownloadStatus() {
    ReliableResourceStatus status =
        new ReliableResourceStatus(
            ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_COMPLETE, 0L);

    status.setDownloadStatus(ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_INTERRUPTED);

    assertThat(
        status.getDownloadStatus(),
        is(ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_INTERRUPTED));
  }

  @Test
  void testSetMessage() {
    ReliableResourceStatus status =
        new ReliableResourceStatus(
            ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_COMPLETE, 0L);

    status.setMessage("Test message");

    assertThat(status.toString(), containsString("Test message"));
  }

  @Test
  void testToString() {
    ReliableResourceStatus status =
        new ReliableResourceStatus(
            ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_COMPLETE, 1000L);
    status.setMessage("Download successful");

    String result = status.toString();

    assertThat(result, containsString("bytesRead = 1000"));
    assertThat(result, containsString("RESOURCE_DOWNLOAD_COMPLETE"));
    assertThat(result, containsString("Download successful"));
  }

  @Test
  void testDownloadStatusEnumValues() {
    assertThat(ReliableResourceStatus.DownloadStatus.values().length, is(6));
  }

  @Test
  void testDownloadStatusResourceDownloadComplete() {
    assertThat(
        ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_COMPLETE, is(notNullValue()));
  }

  @Test
  void testDownloadStatusResourceDownloadInterrupted() {
    assertThat(
        ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_INTERRUPTED, is(notNullValue()));
  }

  @Test
  void testDownloadStatusResourceDownloadCanceled() {
    assertThat(
        ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_CANCELED, is(notNullValue()));
  }

  @Test
  void testDownloadStatusClientOutputStreamException() {
    assertThat(
        ReliableResourceStatus.DownloadStatus.CLIENT_OUTPUT_STREAM_EXCEPTION, is(notNullValue()));
  }

  @Test
  void testDownloadStatusCachedFileOutputStreamException() {
    assertThat(
        ReliableResourceStatus.DownloadStatus.CACHED_FILE_OUTPUT_STREAM_EXCEPTION,
        is(notNullValue()));
  }

  @Test
  void testDownloadStatusProductInputStreamException() {
    assertThat(
        ReliableResourceStatus.DownloadStatus.PRODUCT_INPUT_STREAM_EXCEPTION, is(notNullValue()));
  }

  @Test
  void testWithNegativeBytesRead() {
    ReliableResourceStatus status =
        new ReliableResourceStatus(
            ReliableResourceStatus.DownloadStatus.RESOURCE_DOWNLOAD_COMPLETE, -1L);

    assertThat(status.getBytesRead(), is(-1L));
  }
}
