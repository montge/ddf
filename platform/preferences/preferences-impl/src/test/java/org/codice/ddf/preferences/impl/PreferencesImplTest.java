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
package org.codice.ddf.preferences.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.InjectableAttribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.federation.FederationException;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.operation.CreateResponse;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.operation.UpdateRequest;
import ddf.catalog.operation.UpdateResponse;
import ddf.catalog.source.IngestException;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codice.ddf.platform.util.uuidgenerator.UuidGenerator;
import org.codice.ddf.preferences.DefaultPreferencesSupplier;
import org.codice.ddf.preferences.PostRetrievalPlugin;
import org.codice.ddf.preferences.PreferencesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PreferencesImplTest {

  private static final String TEST_USER_ID = "testUser";
  private static final String TEST_METACARD_ID = "test-metacard-id";

  @Mock private CatalogFramework catalogFramework;

  @Mock private UuidGenerator uuidGenerator;

  @Mock private QueryResponse queryResponse;

  @Mock private CreateResponse createResponse;

  @Mock private UpdateResponse updateResponse;

  private FilterBuilder filterBuilder;

  private List<PostRetrievalPlugin> postRetrievalPlugins;

  private List<DefaultPreferencesSupplier> defaultPreferencesSuppliers;

  private List<InjectableAttribute> injectableAttributes;

  private PreferencesImpl preferences;

  @BeforeEach
  void setUp() throws Exception {
    filterBuilder = new GeotoolsFilterBuilder();
    postRetrievalPlugins = new ArrayList<>();
    defaultPreferencesSuppliers = new ArrayList<>();
    injectableAttributes = new ArrayList<>();

    when(uuidGenerator.generateKnownId(any(), any())).thenReturn(TEST_METACARD_ID);

    preferences =
        new PreferencesImpl(
            catalogFramework,
            filterBuilder,
            uuidGenerator,
            postRetrievalPlugins,
            defaultPreferencesSuppliers,
            injectableAttributes);
  }

  @Test
  void testAddCreatesNewMetacardWhenNoneExists() throws Exception {
    when(queryResponse.getResults()).thenReturn(Collections.emptyList());
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    when(catalogFramework.create(any(CreateRequest.class))).thenReturn(createResponse);

    Map<String, Object> prefs = new HashMap<>();
    prefs.put("theme", "dark");

    preferences.add(prefs, TEST_USER_ID);

    verify(catalogFramework).create(any(CreateRequest.class));
    verify(catalogFramework, never()).update(any(UpdateRequest.class));
  }

  @Test
  void testAddUpdatesExistingMetacard() throws Exception {
    Metacard existingMetacard = new MetacardImpl(new PreferencesMetacardType());
    existingMetacard.setAttribute(
        new AttributeImpl(PreferencesMetacardType.USER_ATTRIBUTE, TEST_USER_ID));

    Result result = mock(Result.class);
    when(result.getMetacard()).thenReturn(existingMetacard);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result));
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    when(catalogFramework.update(any(UpdateRequest.class))).thenReturn(updateResponse);

    Map<String, Object> prefs = new HashMap<>();
    prefs.put("theme", "light");

    preferences.add(prefs, TEST_USER_ID);

    verify(catalogFramework).update(any(UpdateRequest.class));
    verify(catalogFramework, never()).create(any(CreateRequest.class));
  }

  @Test
  void testAddThrowsWhenMultipleMetacardsFound() throws Exception {
    Result result1 = mock(Result.class);
    Result result2 = mock(Result.class);
    when(queryResponse.getResults()).thenReturn(List.of(result1, result2));
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    Map<String, Object> prefs = new HashMap<>();

    assertThrows(PreferencesException.class, () -> preferences.add(prefs, TEST_USER_ID));
  }

  @Test
  void testAddThrowsOnQueryException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(new UnsupportedQueryException("query failed"));

    Map<String, Object> prefs = new HashMap<>();

    assertThrows(PreferencesException.class, () -> preferences.add(prefs, TEST_USER_ID));
  }

  @Test
  void testAddThrowsOnSourceUnavailable() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(new SourceUnavailableException("source down"));

    Map<String, Object> prefs = new HashMap<>();

    assertThrows(PreferencesException.class, () -> preferences.add(prefs, TEST_USER_ID));
  }

  @Test
  void testAddThrowsOnFederationException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(new FederationException("federation failed"));

    Map<String, Object> prefs = new HashMap<>();

    assertThrows(PreferencesException.class, () -> preferences.add(prefs, TEST_USER_ID));
  }

  @Test
  void testAddThrowsOnIngestException() throws Exception {
    when(queryResponse.getResults()).thenReturn(Collections.emptyList());
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    when(catalogFramework.create(any(CreateRequest.class)))
        .thenThrow(new IngestException("ingest failed"));

    Map<String, Object> prefs = new HashMap<>();

    assertThrows(PreferencesException.class, () -> preferences.add(prefs, TEST_USER_ID));
  }

  @Test
  void testGetReturnsDefaultsWhenNoPreferencesExist() throws Exception {
    when(queryResponse.getResults()).thenReturn(Collections.emptyList());
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    Map<String, Object> defaultPrefs = new HashMap<>();
    defaultPrefs.put("defaultKey", "defaultValue");
    DefaultPreferencesSupplier supplier = () -> defaultPrefs;
    defaultPreferencesSuppliers.add(supplier);

    preferences =
        new PreferencesImpl(
            catalogFramework,
            filterBuilder,
            uuidGenerator,
            postRetrievalPlugins,
            defaultPreferencesSuppliers,
            injectableAttributes);

    Map<String, Object> result = preferences.get(TEST_USER_ID);

    assertThat(result, is(notNullValue()));
    assertThat(result, hasEntry("defaultKey", "defaultValue"));
    assertThat(result, hasEntry(PreferencesMetacardType.USER_ATTRIBUTE, TEST_USER_ID));
  }

  @Test
  void testGetReturnsExistingPreferences() throws Exception {
    Metacard existingMetacard = new MetacardImpl(new PreferencesMetacardType());
    existingMetacard.setAttribute(
        new AttributeImpl(PreferencesMetacardType.USER_ATTRIBUTE, TEST_USER_ID));

    Result result = mock(Result.class);
    when(result.getMetacard()).thenReturn(existingMetacard);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result));
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    Map<String, Object> prefs = preferences.get(TEST_USER_ID);

    assertThat(prefs, is(notNullValue()));
    assertThat(prefs, hasEntry(PreferencesMetacardType.USER_ATTRIBUTE, TEST_USER_ID));
  }

  @Test
  void testGetAppliesPostRetrievalPlugins() throws Exception {
    when(queryResponse.getResults()).thenReturn(Collections.emptyList());
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);

    PostRetrievalPlugin plugin =
        prefs -> {
          prefs.put("pluginAdded", true);
        };
    postRetrievalPlugins.add(plugin);

    preferences =
        new PreferencesImpl(
            catalogFramework,
            filterBuilder,
            uuidGenerator,
            postRetrievalPlugins,
            defaultPreferencesSuppliers,
            injectableAttributes);

    Map<String, Object> result = preferences.get(TEST_USER_ID);

    assertThat(result, hasEntry("pluginAdded", true));
  }

  @Test
  void testGetThrowsOnQueryException() throws Exception {
    when(catalogFramework.query(any(QueryRequest.class)))
        .thenThrow(new UnsupportedQueryException("query failed"));

    assertThrows(PreferencesException.class, () -> preferences.get(TEST_USER_ID));
  }

  @Test
  void testAddIgnoresUserAndIdAttributes() throws Exception {
    when(queryResponse.getResults()).thenReturn(Collections.emptyList());
    when(catalogFramework.query(any(QueryRequest.class))).thenReturn(queryResponse);
    when(catalogFramework.create(any(CreateRequest.class))).thenReturn(createResponse);

    Map<String, Object> prefs = new HashMap<>();
    prefs.put(PreferencesMetacardType.USER_ATTRIBUTE, "shouldBeIgnored");
    prefs.put(Metacard.ID, "shouldAlsoBeIgnored");
    prefs.put("validKey", "validValue");

    preferences.add(prefs, TEST_USER_ID);

    verify(catalogFramework).create(any(CreateRequest.class));
  }
}
