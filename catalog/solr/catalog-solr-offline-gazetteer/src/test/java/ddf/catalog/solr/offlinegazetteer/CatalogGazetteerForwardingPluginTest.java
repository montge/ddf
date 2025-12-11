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
package ddf.catalog.solr.offlinegazetteer;

import static ddf.catalog.solr.offlinegazetteer.GazetteerConstants.COLLECTION_NAME;
import static ddf.catalog.solr.offlinegazetteer.GazetteerConstants.GAZETTEER_METACARD_TAG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
import ddf.catalog.plugin.PreQueryPlugin;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.codice.solr.factory.SolrClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link CatalogGazetteerForwardingPlugin} class. */
@ExtendWith(MockitoExtension.class)
public class CatalogGazetteerForwardingPluginTest {

  @Mock private SolrClientFactory solrClientFactory;

  @Mock private SolrClient solrClient;

  @Mock private CreateResponse createResponse;

  @Mock private UpdateResponse updateResponse;

  @Mock private DeleteResponse deleteResponse;

  @Mock private Update update;

  private CatalogGazetteerForwardingPlugin plugin;

  @BeforeEach
  public void setUp() {
    when(solrClientFactory.newClient(COLLECTION_NAME)).thenReturn(solrClient);
    plugin = new CatalogGazetteerForwardingPlugin(solrClientFactory);
  }

  @Test
  public void testImplementsPostIngestPlugin() {
    assertThat(plugin instanceof PostIngestPlugin, is(true));
  }

  @Test
  public void testImplementsPreQueryPlugin() {
    assertThat(plugin instanceof PreQueryPlugin, is(true));
  }

  // CreateResponse tests

  @Test
  public void testProcessCreateReturnsInput() throws Exception {
    when(createResponse.getCreatedMetacards()).thenReturn(Collections.emptyList());

    CreateResponse result = plugin.process(createResponse);

    assertThat(result, is(sameInstance(createResponse)));
  }

  @Test
  public void testProcessCreateDoesNotAddWhenNoGazetteerMetacards() throws Exception {
    Metacard regularMetacard = createRegularMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(regularMetacard));

    plugin.process(createResponse);

    verify(solrClient, never()).add(eq(COLLECTION_NAME), anyList());
  }

  @Test
  public void testProcessCreateAddsGazetteerMetacards() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));

    plugin.process(createResponse);

    verify(solrClient).add(eq(COLLECTION_NAME), anyList());
  }

  @Test
  public void testProcessCreateThrowsOnSolrException() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));
    doThrow(new SolrServerException("test")).when(solrClient).add(eq(COLLECTION_NAME), anyList());

    assertThrows(PluginExecutionException.class, () -> plugin.process(createResponse));
  }

  @Test
  public void testProcessCreateThrowsOnIOException() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));
    doThrow(new IOException("test")).when(solrClient).add(eq(COLLECTION_NAME), anyList());

    assertThrows(PluginExecutionException.class, () -> plugin.process(createResponse));
  }

  // UpdateResponse tests

  @Test
  public void testProcessUpdateReturnsInput() throws Exception {
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.emptyList());

    UpdateResponse result = plugin.process(updateResponse);

    assertThat(result, is(sameInstance(updateResponse)));
  }

  @Test
  public void testProcessUpdateDoesNotAddWhenNoGazetteerMetacards() throws Exception {
    Metacard regularMetacard = createRegularMetacard();
    when(update.getNewMetacard()).thenReturn(regularMetacard);
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));

    plugin.process(updateResponse);

    verify(solrClient, never()).add(eq(COLLECTION_NAME), anyList());
  }

  @Test
  public void testProcessUpdateAddsGazetteerMetacards() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(update.getNewMetacard()).thenReturn(gazetteerMetacard);
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));

    plugin.process(updateResponse);

    verify(solrClient).add(eq(COLLECTION_NAME), anyList());
  }

  @Test
  public void testProcessUpdateThrowsOnSolrException() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(update.getNewMetacard()).thenReturn(gazetteerMetacard);
    when(updateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));
    doThrow(new SolrServerException("test")).when(solrClient).add(eq(COLLECTION_NAME), anyList());

    assertThrows(PluginExecutionException.class, () -> plugin.process(updateResponse));
  }

  // DeleteResponse tests

  @Test
  public void testProcessDeleteReturnsInput() throws Exception {
    when(deleteResponse.getDeletedMetacards()).thenReturn(Collections.emptyList());

    DeleteResponse result = plugin.process(deleteResponse);

    assertThat(result, is(sameInstance(deleteResponse)));
  }

  @Test
  public void testProcessDeleteDoesNotDeleteWhenNoGazetteerMetacards() throws Exception {
    Metacard regularMetacard = createRegularMetacard();
    when(deleteResponse.getDeletedMetacards())
        .thenReturn(Collections.singletonList(regularMetacard));

    plugin.process(deleteResponse);

    verify(solrClient, never()).deleteById(anyList());
  }

  @Test
  public void testProcessDeleteRemovesGazetteerMetacards() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(deleteResponse.getDeletedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));

    plugin.process(deleteResponse);

    verify(solrClient).deleteById(anyList());
  }

  @Test
  public void testProcessDeleteThrowsOnSolrException() throws Exception {
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(deleteResponse.getDeletedMetacards())
        .thenReturn(Collections.singletonList(gazetteerMetacard));
    doThrow(new SolrServerException("test")).when(solrClient).deleteById(anyList());

    assertThrows(PluginExecutionException.class, () -> plugin.process(deleteResponse));
  }

  // Mixed metacard tests

  @Test
  public void testProcessCreateFiltersMixedMetacards() throws Exception {
    Metacard regularMetacard = createRegularMetacard();
    Metacard gazetteerMetacard = createGazetteerMetacard();
    when(createResponse.getCreatedMetacards())
        .thenReturn(Arrays.asList(regularMetacard, gazetteerMetacard));

    plugin.process(createResponse);

    verify(solrClient).add(eq(COLLECTION_NAME), anyList());
  }

  private Metacard createGazetteerMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    Set<String> tags = new HashSet<>();
    tags.add(GAZETTEER_METACARD_TAG);
    metacard.setTags(tags);
    return metacard;
  }

  private Metacard createRegularMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("regular-id");
    Set<String> tags = new HashSet<>();
    tags.add("resource");
    metacard.setTags(tags);
    return metacard;
  }
}
