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
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.cache.impl.ResourceCacheImpl;
import ddf.catalog.event.retrievestatus.DownloadsStatusEventListener;
import ddf.catalog.event.retrievestatus.DownloadsStatusEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReliableResourceDownloaderConfigTest {

  private ReliableResourceDownloaderConfig config;

  @BeforeEach
  void setUp() {
    config = new ReliableResourceDownloaderConfig();
  }

  // Constants tests

  @Test
  void testKbConstant() {
    assertThat(ReliableResourceDownloaderConfig.KB, is(1024));
  }

  @Test
  void testMbConstant() {
    assertThat(ReliableResourceDownloaderConfig.MB, is(1024 * 1024));
  }

  // Default values tests

  @Test
  void testDefaultMaxRetryAttempts() {
    assertThat(config.getMaxRetryAttempts(), is(3));
  }

  @Test
  void testDefaultDelayBetweenAttemptsMS() {
    assertThat(config.getDelayBetweenAttemptsMS(), is(10000));
  }

  @Test
  void testDefaultMonitorPeriodMS() {
    assertThat(config.getMonitorPeriodMS(), is(5000L));
  }

  @Test
  void testDefaultMonitorInitialDelayMS() {
    assertThat(config.getMonitorInitialDelayMS(), is(1000));
  }

  @Test
  void testDefaultCacheEnabled() {
    assertThat(config.isCacheEnabled(), is(false));
  }

  @Test
  void testDefaultCacheWhenCanceled() {
    assertThat(config.isCacheWhenCanceled(), is(false));
  }

  @Test
  void testDefaultChunkSize() {
    assertThat(config.getChunkSize(), is(ReliableResourceDownloaderConfig.MB));
  }

  @Test
  void testDefaultResourceCacheIsNull() {
    assertThat(config.getResourceCache(), is(nullValue()));
  }

  @Test
  void testDefaultEventPublisherIsNull() {
    assertThat(config.getEventPublisher(), is(nullValue()));
  }

  @Test
  void testDefaultEventListenerIsNull() {
    assertThat(config.getEventListener(), is(nullValue()));
  }

  // Setter tests

  @Test
  void testSetMaxRetryAttempts() {
    config.setMaxRetryAttempts(5);

    assertThat(config.getMaxRetryAttempts(), is(5));
  }

  @Test
  void testSetDelayBetweenAttemptsMS() {
    config.setDelayBetweenAttemptsMS(20000);

    assertThat(config.getDelayBetweenAttemptsMS(), is(20000));
  }

  @Test
  void testSetMonitorPeriodMS() {
    config.setMonitorPeriodMS(10000L);

    assertThat(config.getMonitorPeriodMS(), is(10000L));
  }

  @Test
  void testSetMonitorInitialDelayMS() {
    config.setMonitorInitialDelayMS(2000);

    assertThat(config.getMonitorInitialDelayMS(), is(2000));
  }

  @Test
  void testSetCacheEnabled() {
    config.setCacheEnabled(true);

    assertThat(config.isCacheEnabled(), is(true));
  }

  @Test
  void testSetCacheWhenCanceled() {
    config.setCacheWhenCanceled(true);

    assertThat(config.isCacheWhenCanceled(), is(true));
  }

  @Test
  void testSetChunkSize() {
    int customChunkSize = 2 * ReliableResourceDownloaderConfig.MB;
    config.setChunkSize(customChunkSize);

    assertThat(config.getChunkSize(), is(customChunkSize));
  }

  @Test
  void testSetResourceCache() {
    ResourceCacheImpl resourceCache = mock(ResourceCacheImpl.class);

    config.setResourceCache(resourceCache);

    assertThat(config.getResourceCache(), is(sameInstance(resourceCache)));
  }

  @Test
  void testSetEventPublisher() {
    DownloadsStatusEventPublisher eventPublisher = mock(DownloadsStatusEventPublisher.class);

    config.setEventPublisher(eventPublisher);

    assertThat(config.getEventPublisher(), is(sameInstance(eventPublisher)));
  }

  @Test
  void testSetEventListener() {
    DownloadsStatusEventListener eventListener = mock(DownloadsStatusEventListener.class);

    config.setEventListener(eventListener);

    assertThat(config.getEventListener(), is(sameInstance(eventListener)));
  }

  // Edge case tests

  @Test
  void testSetMaxRetryAttemptsToZero() {
    config.setMaxRetryAttempts(0);

    assertThat(config.getMaxRetryAttempts(), is(0));
  }

  @Test
  void testSetDelayBetweenAttemptsMSToZero() {
    config.setDelayBetweenAttemptsMS(0);

    assertThat(config.getDelayBetweenAttemptsMS(), is(0));
  }

  @Test
  void testSetMonitorPeriodMSToZero() {
    config.setMonitorPeriodMS(0L);

    assertThat(config.getMonitorPeriodMS(), is(0L));
  }

  @Test
  void testSetResourceCacheToNull() {
    ResourceCacheImpl resourceCache = mock(ResourceCacheImpl.class);
    config.setResourceCache(resourceCache);

    config.setResourceCache(null);

    assertThat(config.getResourceCache(), is(nullValue()));
  }
}
