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
package org.codice.ddf.catalog.plugin.gazetteer;

import static org.codice.ddf.spatial.geocoding.GeoCodingConstants.GAZETTEER_METACARD_TAG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.operation.CreateResponse;
import ddf.catalog.operation.DeleteResponse;
import ddf.catalog.operation.Update;
import ddf.catalog.operation.UpdateResponse;
import ddf.catalog.plugin.PluginExecutionException;
import ddf.catalog.plugin.PostIngestPlugin;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link BuildSuggesterIndexPlugin} class. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class BuildSuggesterIndexPluginTest {

  @Mock private ScheduledThreadPoolExecutor executor;

  @Mock private BuildSuggesterIndex buildSuggesterIndex;

  @Mock private CreateResponse createResponse;

  @Mock private UpdateResponse updateResponse;

  @Mock private DeleteResponse deleteResponse;

  @Mock private Update update;

  @Mock private ScheduledFuture<?> scheduledFuture;

  private BuildSuggesterIndexPlugin plugin;

  @Before
  public void setUp() {
    plugin = new BuildSuggesterIndexPlugin(executor, buildSuggesterIndex);
    doReturn(scheduledFuture)
        .when(executor)
        .schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  @Test
  public void testImplementsPostIngestPlugin() {
    assertThat(plugin instanceof PostIngestPlugin, is(true));
  }

  // CreateResponse tests

  @Test
  public void testProcessCreateReturnsInput() throws Exception {
    when(createResponse.getCreatedMetacards()).thenReturn(Collections.emptyList());

    CreateResponse result = plugin.process(createResponse);

    assertThat(result, is(sameInstance(createResponse)));
  }

  @Test
  public void testProcessCreateSchedulesBuildWhenGazetteerMetacardPresent() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));

    plugin.process(createResponse);

    verify(executor).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  @Test
  public void testProcessCreateDoesNotScheduleWhenNoGazetteerMetacard() throws Exception {
    Metacard regularMetacard = createRegularMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(regularMetacard));

    plugin.process(createResponse);

    verify(executor, never()).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  @Test
  public void testProcessCreateSchedulesWhenMixedMetacards() throws Exception {
    Metacard regularMetacard = createRegularMetacard();
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Arrays.asList(regularMetacard, gazetteerMetacard));

    plugin.process(createResponse);

    verify(executor).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  // UpdateResponse tests

  @Test
  public void testProcessUpdateReturnsInput() throws Exception {
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.emptyList());

    UpdateResponse result = plugin.process(updateResponse);

    assertThat(result, is(sameInstance(updateResponse)));
  }

  @Test
  public void testProcessUpdateSchedulesBuildWhenNewMetacardIsGazetteer() throws Exception {
    Metacard oldMetacard = createRegularMetacard();
    Metacard newMetacard = createGazetteerMetacard();
    when(update.getOldMetacard()).thenReturn(oldMetacard);
    when(update.getNewMetacard()).thenReturn(newMetacard);
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));

    plugin.process(updateResponse);

    verify(executor).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  @Test
  public void testProcessUpdateSchedulesBuildWhenOldMetacardIsGazetteer() throws Exception {
    Metacard oldMetacard = createGazetteerMetacard();
    Metacard newMetacard = createRegularMetacard();
    when(update.getOldMetacard()).thenReturn(oldMetacard);
    when(update.getNewMetacard()).thenReturn(newMetacard);
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));

    plugin.process(updateResponse);

    verify(executor).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  @Test
  public void testProcessUpdateDoesNotScheduleWhenNoGazetteerMetacard() throws Exception {
    Metacard oldMetacard = createRegularMetacard();
    Metacard newMetacard = createRegularMetacard();
    when(update.getOldMetacard()).thenReturn(oldMetacard);
    when(update.getNewMetacard()).thenReturn(newMetacard);
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));

    plugin.process(updateResponse);

    verify(executor, never()).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  // DeleteResponse tests

  @Test
  public void testProcessDeleteReturnsInput() throws Exception {
    when(deleteResponse.getDeletedMetacards()).thenReturn(Collections.emptyList());

    DeleteResponse result = plugin.process(deleteResponse);

    assertThat(result, is(sameInstance(deleteResponse)));
  }

  @Test
  public void testProcessDeleteSchedulesBuildWhenGazetteerMetacardPresent() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(deleteResponse.getDeletedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));

    plugin.process(deleteResponse);

    verify(executor).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  @Test
  public void testProcessDeleteDoesNotScheduleWhenNoGazetteerMetacard() throws Exception {
    Metacard regularMetacard = createRegularMetacard();
    when(deleteResponse.getDeletedMetacards())
        .thenReturn(Collections.singletonList(regularMetacard));

    plugin.process(deleteResponse);

    verify(executor, never()).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
  }

  // Scheduling tests

  @Test
  public void testSchedulesCancelsPreviousFuture() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));

    // First call
    plugin.process(createResponse);
    // Second call should cancel the first
    plugin.process(createResponse);

    verify(scheduledFuture).cancel(false);
  }

  @Test(expected = PluginExecutionException.class)
  public void testThrowsPluginExecutionExceptionWhenRejected() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));
    when(executor.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
        .thenThrow(new RejectedExecutionException("test"));

    plugin.process(createResponse);
  }

  @Test
  public void testSchedulesWithOneMinuteDelay() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));

    plugin.process(createResponse);

    verify(executor)
        .schedule(
            any(Runnable.class),
            org.mockito.Mockito.eq(1L),
            org.mockito.Mockito.eq(TimeUnit.MINUTES));
  }

  private Metacard createGazetteerMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    Set<String> tags = new HashSet<>();
    tags.add(GAZETTEER_METACARD_TAG);
    metacard.setTags(tags);
    return metacard;
  }

  private Metacard createRegularMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    Set<String> tags = new HashSet<>();
    tags.add("resource");
    metacard.setTags(tags);
    return metacard;
  }
}
