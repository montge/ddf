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
package org.codice.ddf.catalog.download.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;
import org.codice.ddf.catalog.resource.cache.ResourceCacheServiceMBean;
import org.codice.ddf.configuration.SystemBaseUrl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ResourceDownloadActionProviderCanHandleTest {

  private static final String ACTION_PROVIDER_ID = "actionID";

  private static final String DEFAULT_METACARD_ID = "ce4de61db5da46bdbf6dad8fe6394663";

  private static final String LOCAL_SITE_NAME = "local-ddf";

  private static final String REMOTE_SITE_NAME = "remote-ddf";

  private static final String CONTENT_RESOURCE_URI = "content:f74e48380d9347b28a6b4fd88ffe024b";

  private static final String REMOTE_RESOURCE_URI =
      "https://remote-ddf:20002/services/catalog/sources/ddf.distribution/ce4de61db5da46bdbf6dad8fe6394663?transform=resource";

  @Mock private Metacard mockMetacard;

  @Mock private ResourceCacheServiceMBean mockResourceCacheServiceMBeanProxy;

  private ResourceDownloadActionProvider actionProvider;

  private AutoCloseable mocks;

  static Stream<Arguments> getTestData() {
    return Stream.of(
        Arguments.of(REMOTE_SITE_NAME, REMOTE_RESOURCE_URI, DEFAULT_METACARD_ID, true, false),
        Arguments.of(REMOTE_SITE_NAME, REMOTE_RESOURCE_URI, DEFAULT_METACARD_ID, false, true),
        Arguments.of(REMOTE_SITE_NAME, CONTENT_RESOURCE_URI, DEFAULT_METACARD_ID, true, false),
        Arguments.of(REMOTE_SITE_NAME, CONTENT_RESOURCE_URI, DEFAULT_METACARD_ID, false, true),
        Arguments.of(LOCAL_SITE_NAME, REMOTE_RESOURCE_URI, DEFAULT_METACARD_ID, true, false),
        Arguments.of(LOCAL_SITE_NAME, REMOTE_RESOURCE_URI, DEFAULT_METACARD_ID, false, true),
        Arguments.of(LOCAL_SITE_NAME, CONTENT_RESOURCE_URI, DEFAULT_METACARD_ID, true, false),
        Arguments.of(LOCAL_SITE_NAME, CONTENT_RESOURCE_URI, DEFAULT_METACARD_ID, false, false),
        Arguments.of(LOCAL_SITE_NAME, null, DEFAULT_METACARD_ID, false, false),
        Arguments.of(REMOTE_SITE_NAME, null, DEFAULT_METACARD_ID, false, false));
  }

  @BeforeEach
  public void setup() {
    mocks = MockitoAnnotations.openMocks(this);
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "localhost");
    actionProvider =
        new ResourceDownloadActionProvider(ACTION_PROVIDER_ID) {
          @Override
          ResourceCacheServiceMBean createResourceCacheMBeanProxy() {
            return mockResourceCacheServiceMBeanProxy;
          }

          @Override
          String getLocalSiteName() {
            return LOCAL_SITE_NAME;
          }
        };
  }

  @AfterEach
  public void tearDown() throws Exception {
    mocks.close();
  }

  @ParameterizedTest
  @MethodSource("getTestData")
  public void testCanHandle(
      String siteName,
      String resourceUri,
      String metacardId,
      boolean isMetacardResourceCached,
      boolean expectedCanHandle)
      throws Exception {
    setupMockBasicMetacard(siteName, resourceUri, metacardId);
    setupMockResourceCacheServiceMBeanProxy(isMetacardResourceCached);
    assertThat(actionProvider.canHandleMetacard(mockMetacard), is(expectedCanHandle));
  }

  private void setupMockResourceCacheServiceMBeanProxy(boolean isMetacardResourceCached) {
    when(mockResourceCacheServiceMBeanProxy.contains(mockMetacard))
        .thenReturn(isMetacardResourceCached);
  }

  private void setupMockBasicMetacard(String sourceId, String resourceUri, String metacardId)
      throws URISyntaxException {
    when(mockMetacard.getId()).thenReturn(metacardId);
    if (sourceId != null) {
      when(mockMetacard.getSourceId()).thenReturn(sourceId);
    }
    if (resourceUri != null) {
      when(mockMetacard.getResourceURI()).thenReturn(new URI(resourceUri));
    }
  }
}
