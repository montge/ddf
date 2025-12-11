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
package org.codice.ddf.catalog.admin.downloadmanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.event.retrievestatus.DownloadStatusInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link DownloadManagerService} class. */
@ExtendWith(MockitoExtension.class)
public class DownloadManagerServiceTest {

  private static final String DOWNLOAD_ID_1 = "download-123";
  private static final String DOWNLOAD_ID_2 = "download-456";
  private static final String USER_ID = "user1";

  @Mock private DownloadStatusInfo downloadStatusInfo;

  private DownloadManagerService service;

  @BeforeEach
  public void setUp() {
    service = new DownloadManagerService(downloadStatusInfo);
  }

  @Test
  public void testConstructor() {
    assertThat(service, is(notNullValue()));
  }

  @Test
  public void testImplementsDownloadManagerServiceMBean() {
    assertThat(service instanceof DownloadManagerServiceMBean, is(true));
  }

  @Test
  public void testGetAllDownloadsStatusWithEmptyList() {
    when(downloadStatusInfo.getAllDownloads()).thenReturn(Collections.emptyList());

    List<Map<String, String>> result = service.getAllDownloadsStatus();

    assertThat(result, is(notNullValue()));
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  public void testGetAllDownloadsStatusWithSingleDownload() {
    Map<String, String> status = createDownloadStatus(DOWNLOAD_ID_1, "IN_PROGRESS");
    when(downloadStatusInfo.getAllDownloads()).thenReturn(Collections.singletonList(DOWNLOAD_ID_1));
    when(downloadStatusInfo.getDownloadStatus(DOWNLOAD_ID_1)).thenReturn(status);

    List<Map<String, String>> result = service.getAllDownloadsStatus();

    assertThat(result, hasSize(1));
    assertThat(result.get(0).get("downloadId"), is(DOWNLOAD_ID_1));
    assertThat(result.get(0).get("status"), is("IN_PROGRESS"));
  }

  @Test
  public void testGetAllDownloadsStatusWithMultipleDownloads() {
    Map<String, String> status1 = createDownloadStatus(DOWNLOAD_ID_1, "COMPLETED");
    Map<String, String> status2 = createDownloadStatus(DOWNLOAD_ID_2, "IN_PROGRESS");
    when(downloadStatusInfo.getAllDownloads())
        .thenReturn(Arrays.asList(DOWNLOAD_ID_1, DOWNLOAD_ID_2));
    when(downloadStatusInfo.getDownloadStatus(DOWNLOAD_ID_1)).thenReturn(status1);
    when(downloadStatusInfo.getDownloadStatus(DOWNLOAD_ID_2)).thenReturn(status2);

    List<Map<String, String>> result = service.getAllDownloadsStatus();

    assertThat(result, hasSize(2));
  }

  @Test
  public void testGetDownloadStatus() {
    Map<String, String> status = createDownloadStatus(DOWNLOAD_ID_1, "COMPLETED");
    when(downloadStatusInfo.getDownloadStatus(DOWNLOAD_ID_1)).thenReturn(status);

    Map<String, String> result = service.getDownloadStatus(DOWNLOAD_ID_1);

    assertThat(result, is(notNullValue()));
    assertThat(result.get("downloadId"), is(DOWNLOAD_ID_1));
    assertThat(result.get("status"), is("COMPLETED"));
    verify(downloadStatusInfo).getDownloadStatus(DOWNLOAD_ID_1);
  }

  @Test
  public void testGetAllDownloads() {
    List<String> downloads = Arrays.asList(DOWNLOAD_ID_1, DOWNLOAD_ID_2);
    when(downloadStatusInfo.getAllDownloads()).thenReturn(downloads);

    List<String> result = service.getAllDownloads();

    assertThat(result, contains(DOWNLOAD_ID_1, DOWNLOAD_ID_2));
    verify(downloadStatusInfo).getAllDownloads();
  }

  @Test
  public void testGetAllDownloadsForUser() {
    List<String> downloads = Collections.singletonList(DOWNLOAD_ID_1);
    when(downloadStatusInfo.getAllDownloads(USER_ID)).thenReturn(downloads);

    List<String> result = service.getAllDownloads(USER_ID);

    assertThat(result, contains(DOWNLOAD_ID_1));
    verify(downloadStatusInfo).getAllDownloads(USER_ID);
  }

  @Test
  public void testRemoveDownloadInfo() {
    service.removeDownloadInfo(DOWNLOAD_ID_1);

    verify(downloadStatusInfo).removeDownloadInfo(DOWNLOAD_ID_1);
  }

  @Test
  public void testCancelDownload() {
    service.cancelDownload(USER_ID, DOWNLOAD_ID_1);

    verify(downloadStatusInfo).cancelDownload(USER_ID, DOWNLOAD_ID_1);
  }

  @Test
  public void testInitAndDestroy() {
    // Test that init and destroy don't throw exceptions
    service.init();
    service.destroy();
  }

  private Map<String, String> createDownloadStatus(String downloadId, String status) {
    Map<String, String> statusMap = new HashMap<>();
    statusMap.put("downloadId", downloadId);
    statusMap.put("status", status);
    return statusMap;
  }
}
