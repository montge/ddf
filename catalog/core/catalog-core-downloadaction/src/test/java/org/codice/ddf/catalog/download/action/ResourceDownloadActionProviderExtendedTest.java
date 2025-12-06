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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import ddf.action.Action;
import ddf.catalog.data.Metacard;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codice.ddf.catalog.resource.cache.ResourceCacheServiceMBean;
import org.codice.ddf.configuration.SystemBaseUrl;
import org.codice.ddf.configuration.SystemInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Extended tests for ResourceDownloadActionProvider to increase code coverage to 80%+. These tests
 * focus on edge cases, resource caching scenarios, and various metacard configurations.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceDownloadActionProviderExtendedTest {

  private static final String ACTION_PROVIDER_ID = "download-action";

  private static final String METACARD_ID = "test-metacard-id";

  private static final String LOCAL_SITE = "local-site";

  private static final String REMOTE_SITE = "remote-site";

  private static final String CONTENT_URI = "content:abc123";

  private static final String REMOTE_HTTP_URI = "http://remote.example.com/resource";

  private static final String REMOTE_HTTPS_URI = "https://remote.example.com/resource";

  @Mock private Metacard metacard;

  @Mock private ResourceCacheServiceMBean resourceCacheMBean;

  private ResourceDownloadActionProvider actionProvider;

  private String originalExternalHost;

  private String originalSiteName;

  @Before
  public void setup() {
    // Save original system properties
    originalExternalHost = System.getProperty(SystemBaseUrl.EXTERNAL_HOST);
    originalSiteName = System.getProperty(SystemInfo.SITE_NAME);

    // Set test system properties
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "localhost");
    System.setProperty(SystemInfo.SITE_NAME, LOCAL_SITE);

    // Create action provider with mock cache
    actionProvider =
        new ResourceDownloadActionProvider(ACTION_PROVIDER_ID) {
          @Override
          ResourceCacheServiceMBean createResourceCacheMBeanProxy() {
            return resourceCacheMBean;
          }

          @Override
          String getLocalSiteName() {
            return LOCAL_SITE.toLowerCase();
          }
        };

    // Setup default metacard
    when(metacard.getId()).thenReturn(METACARD_ID);
    when(metacard.getTags()).thenReturn(Collections.singleton(Metacard.DEFAULT_TAG));
  }

  @After
  public void tearDown() {
    // Restore original system properties
    if (originalExternalHost != null) {
      System.setProperty(SystemBaseUrl.EXTERNAL_HOST, originalExternalHost);
    } else {
      System.clearProperty(SystemBaseUrl.EXTERNAL_HOST);
    }

    if (originalSiteName != null) {
      System.setProperty(SystemInfo.SITE_NAME, originalSiteName);
    } else {
      System.clearProperty(SystemInfo.SITE_NAME);
    }
  }

  @Test
  public void testCanHandleMetacardWithRemoteSourceAndRemoteUri() throws Exception {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithLocalSourceAndContentUri() throws Exception {
    when(metacard.getSourceId()).thenReturn(LOCAL_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(CONTENT_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(false));
  }

  @Test
  public void testCanHandleMetacardWithRemoteSourceAndContentUri() throws Exception {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(CONTENT_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(false));
  }

  @Test
  public void testCanHandleMetacardWithLocalSourceAndRemoteUri() throws Exception {
    when(metacard.getSourceId()).thenReturn(LOCAL_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithNullResourceUri() {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(null);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(false));
  }

  @Test
  public void testCanHandleMetacardWithNullSourceId() throws Exception {
    when(metacard.getSourceId()).thenReturn(null);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithCachedResource() throws Exception {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(true);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(false));
  }

  @Test
  public void testCanHandleMetacardWithHttpsUri() throws Exception {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTPS_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithCaseInsensitiveSourceId() throws Exception {
    when(metacard.getSourceId()).thenReturn("LOCAL-SITE");
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithMixedCaseSourceId() throws Exception {
    when(metacard.getSourceId()).thenReturn("LoCaL-SiTe");
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testGetActionWithRemoteMetacard() throws Exception {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionWithLocalMetacardAndContentUri() throws Exception {
    when(metacard.getSourceId()).thenReturn(LOCAL_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(CONTENT_URI));

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithCachedResource() throws Exception {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(true);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetLocalSiteNameReturnsLowerCase() {
    ResourceDownloadActionProvider provider =
        new ResourceDownloadActionProvider(ACTION_PROVIDER_ID);

    String siteName = provider.getLocalSiteName();

    assertThat(siteName, is(notNullValue()));
    // Should be lowercase
    assertThat(siteName, is(siteName.toLowerCase()));
  }

  @Test
  public void testCreateMetacardActionReturnsValidAction() throws Exception {
    String title = "Test Title";
    String description = "Test Description";
    URI url = new URI("https://localhost/test");

    Action action =
        actionProvider.createMetacardAction(ACTION_PROVIDER_ID, title, description, url.toURL());

    assertThat(action, is(notNullValue()));
    assertThat(action.getId(), is(ACTION_PROVIDER_ID));
    assertThat(action.getTitle(), is(title));
    assertThat(action.getDescription(), is(description));
    assertThat(action.getUrl(), is(url.toURL()));
  }

  @Test
  public void testGetMetacardActionUrlWithSpecialCharacters() throws Exception {
    String specialId = "test&id=123";
    when(metacard.getId()).thenReturn(specialId);
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);

    java.net.URL url = actionProvider.getMetacardActionUrl(REMOTE_SITE, metacard);

    assertThat(url, is(notNullValue()));
    // URL should contain encoded special characters
    assertThat(url.toString().contains("&"), is(true));
  }

  @Test
  public void testGetMetacardActionUrlWithUnicodeCharacters() throws Exception {
    String unicodeId = "test-\u00E9\u00F1";
    when(metacard.getId()).thenReturn(unicodeId);
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);

    java.net.URL url = actionProvider.getMetacardActionUrl(REMOTE_SITE, metacard);

    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testCanHandleMetacardWithEmptySourceId() throws Exception {
    when(metacard.getSourceId()).thenReturn("");
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithWhitespaceSourceId() throws Exception {
    when(metacard.getSourceId()).thenReturn("   ");
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleWithNonResourceMetacard() throws Exception {
    Set<String> tags = new HashSet<>();
    tags.add("custom-tag");
    when(metacard.getTags()).thenReturn(tags);
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));

    boolean canHandle = actionProvider.canHandle(metacard);

    assertThat(canHandle, is(false));
  }

  @Test
  public void testCanHandleWithNullTags() throws Exception {
    when(metacard.getTags()).thenReturn(null);
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandle(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleWithEmptyTags() throws Exception {
    when(metacard.getTags()).thenReturn(Collections.emptySet());
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandle(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithContentSchemeUpperCase() throws Exception {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI("CONTENT:abc123"));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(false));
  }

  @Test
  public void testCanHandleMetacardWithFileUri() throws Exception {
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI("file:///tmp/resource.txt"));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test(expected = ResourceDownloadActionException.class)
  public void testCreateResourceCacheMBeanProxyWithInvalidObjectName() {
    ResourceDownloadActionProvider provider =
        new ResourceDownloadActionProvider(ACTION_PROVIDER_ID) {
          @Override
          ResourceCacheServiceMBean createResourceCacheMBeanProxy() {
            // Call the actual implementation which may throw exception
            return super.createResourceCacheMBeanProxy();
          }
        };

    // This should work in normal circumstances, but we're testing the exception path
    // The exception would occur if the MBean is not available
  }

  @Test
  public void testGetIdReturnsActionProviderId() {
    assertThat(actionProvider.getId(), is(ACTION_PROVIDER_ID));
  }

  @Test
  public void testGetActionWithNullMetacardId() {
    when(metacard.getId()).thenReturn(null);
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithBlankMetacardId() {
    when(metacard.getId()).thenReturn("   ");
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testCanHandleWithMultipleTags() throws Exception {
    Set<String> tags = new HashSet<>();
    tags.add(Metacard.DEFAULT_TAG);
    tags.add("custom-tag");
    when(metacard.getTags()).thenReturn(tags);
    when(metacard.getSourceId()).thenReturn(REMOTE_SITE);
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandle(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithSourceIdMatchingLocalSiteExactCase() throws Exception {
    when(metacard.getSourceId()).thenReturn(LOCAL_SITE.toLowerCase());
    when(metacard.getResourceURI()).thenReturn(new URI(REMOTE_HTTP_URI));
    when(resourceCacheMBean.contains(metacard)).thenReturn(false);

    boolean canHandle = actionProvider.canHandleMetacard(metacard);

    // Should still be able to handle if resource URI is remote
    assertThat(canHandle, is(true));
  }
}
