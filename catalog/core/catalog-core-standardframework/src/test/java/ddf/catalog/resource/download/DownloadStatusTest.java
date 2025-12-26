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
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class DownloadStatusTest {

  @Test
  void testDownloadIdKeyConstant() {
    assertThat(DownloadStatus.DOWNLOAD_ID_KEY, is("downloadId"));
  }

  @Test
  void testFileNameKeyConstant() {
    assertThat(DownloadStatus.FILE_NAME_KEY, is("fileName"));
  }

  @Test
  void testBytesDownloadedKeyConstant() {
    assertThat(DownloadStatus.BYTES_DOWNLOADED_KEY, is("bytesDownloaded"));
  }

  @Test
  void testPercentKeyConstant() {
    assertThat(DownloadStatus.PERCENT_KEY, is("percent"));
  }

  @Test
  void testUserKeyConstant() {
    assertThat(DownloadStatus.USER_KEY, is("user"));
  }

  @Test
  void testStatusKeyConstant() {
    assertThat(DownloadStatus.STATUS_KEY, is("status"));
  }
}
