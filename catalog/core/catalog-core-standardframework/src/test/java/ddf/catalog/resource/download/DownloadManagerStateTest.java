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

import ddf.catalog.resource.download.DownloadManagerState.DownloadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DownloadManagerStateTest {

  private DownloadManagerState state;

  @BeforeEach
  void setUp() {
    state = new DownloadManagerState();
  }

  @Test
  void testInitialDownloadStateIsNull() {
    assertThat(state.getDownloadState(), is(nullValue()));
  }

  @Test
  void testSetAndGetDownloadState() {
    state.setDownloadState(DownloadState.IN_PROGRESS);

    assertThat(state.getDownloadState(), is(DownloadState.IN_PROGRESS));
  }

  @Test
  void testInitialCacheEnabledIsFalse() {
    assertThat(state.isCacheEnabled(), is(false));
  }

  @Test
  void testSetCacheEnabledTrue() {
    state.setCacheEnabled(true);

    assertThat(state.isCacheEnabled(), is(true));
  }

  @Test
  void testSetCacheEnabledFalse() {
    state.setCacheEnabled(true);
    state.setCacheEnabled(false);

    assertThat(state.isCacheEnabled(), is(false));
  }

  @Test
  void testInitialContinueCachingIsFalse() {
    assertThat(state.isContinueCaching(), is(false));
  }

  @Test
  void testSetContinueCachingTrue() {
    state.setContinueCaching(true);

    assertThat(state.isContinueCaching(), is(true));
  }

  @Test
  void testSetContinueCachingFalse() {
    state.setContinueCaching(true);
    state.setContinueCaching(false);

    assertThat(state.isContinueCaching(), is(false));
  }

  @Test
  void testDownloadStateNotStarted() {
    state.setDownloadState(DownloadState.NOT_STARTED);

    assertThat(state.getDownloadState(), is(DownloadState.NOT_STARTED));
  }

  @Test
  void testDownloadStateCompleted() {
    state.setDownloadState(DownloadState.COMPLETED);

    assertThat(state.getDownloadState(), is(DownloadState.COMPLETED));
  }

  @Test
  void testDownloadStateCanceled() {
    state.setDownloadState(DownloadState.CANCELED);

    assertThat(state.getDownloadState(), is(DownloadState.CANCELED));
  }

  @Test
  void testDownloadStateFailed() {
    state.setDownloadState(DownloadState.FAILED);

    assertThat(state.getDownloadState(), is(DownloadState.FAILED));
  }

  @Test
  void testDownloadStateEnumValues() {
    DownloadState[] values = DownloadState.values();

    assertThat(values.length, is(5));
    assertThat(DownloadState.valueOf("NOT_STARTED"), is(DownloadState.NOT_STARTED));
    assertThat(DownloadState.valueOf("IN_PROGRESS"), is(DownloadState.IN_PROGRESS));
    assertThat(DownloadState.valueOf("COMPLETED"), is(DownloadState.COMPLETED));
    assertThat(DownloadState.valueOf("CANCELED"), is(DownloadState.CANCELED));
    assertThat(DownloadState.valueOf("FAILED"), is(DownloadState.FAILED));
  }
}
