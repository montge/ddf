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
package org.codice.ddf.catalog.admin.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.operation.SourceInfoResponse;
import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.CatalogProvider;
import ddf.catalog.source.FederatedSource;
import ddf.catalog.source.SourceDescriptor;
import ddf.catalog.source.SourceUnavailableException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SourceConfigurationAdminPluginTest {

  private static final String TEST_CONFIG_PID = "test.config.pid";
  private static final String TEST_SOURCE_ID = "testSourceId";

  @Mock private CatalogFramework catalogFramework;

  @Mock private BundleContext bundleContext;

  @Mock private SourceInfoResponse sourceInfoResponse;

  private SourceConfigurationAdminPlugin plugin;

  @BeforeEach
  void setUp() throws Exception {
    plugin = new SourceConfigurationAdminPlugin();
    plugin.setCatalogFramework(catalogFramework);

    when(bundleContext.getServiceReferences(FederatedSource.class, null))
        .thenReturn(Collections.emptyList());
    when(bundleContext.getServiceReferences(CatalogProvider.class, null))
        .thenReturn(Collections.emptyList());
  }

  @Test
  void testConstructor() {
    SourceConfigurationAdminPlugin newPlugin = new SourceConfigurationAdminPlugin();
    assertThat(newPlugin, is(notNullValue()));
  }

  @Test
  void testInitAndDestroy() {
    plugin.init();
    plugin.destroy();
  }

  @Test
  void testGetSetCatalogFramework() {
    CatalogFramework newFramework = mock(CatalogFramework.class);
    plugin.setCatalogFramework(newFramework);
    assertThat(plugin.getCatalogFramework(), is(sameInstance(newFramework)));
  }

  @Test
  void testGetConfigurationDataWithNullCatalogFramework() throws Exception {
    plugin.setCatalogFramework(null);

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result, is(notNullValue()));
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void testGetConfigurationDataWithNoServices() throws Exception {
    when(catalogFramework.getSourceInfo(any())).thenReturn(sourceInfoResponse);
    when(sourceInfoResponse.getSourceInfo()).thenReturn(Collections.emptySet());

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result, is(notNullValue()));
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void testGetConfigurationDataWithMatchingConfiguredService() throws Exception {
    ConfiguredFederatedSource source = mock(ConfiguredFederatedSource.class);
    when(source.getConfigurationPid()).thenReturn(TEST_CONFIG_PID);
    when(source.getId()).thenReturn(TEST_SOURCE_ID);

    ServiceReference<FederatedSource> ref = mockServiceReference();
    when(bundleContext.getServiceReferences(FederatedSource.class, null))
        .thenReturn(Collections.singletonList(ref));
    when(bundleContext.getService(ref)).thenReturn(source);

    SourceDescriptor descriptor = mock(SourceDescriptor.class);
    when(descriptor.getSourceId()).thenReturn(TEST_SOURCE_ID);
    when(descriptor.isAvailable()).thenReturn(true);

    Set<SourceDescriptor> descriptors = new HashSet<>();
    descriptors.add(descriptor);

    when(catalogFramework.getSourceInfo(any())).thenReturn(sourceInfoResponse);
    when(sourceInfoResponse.getSourceInfo()).thenReturn(descriptors);

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result, hasEntry("available", true));
    assertThat(result, hasEntry("sourceId", TEST_SOURCE_ID));
  }

  @Test
  void testGetConfigurationDataWithNoMatchingDescriptor() throws Exception {
    ConfiguredFederatedSource source = mock(ConfiguredFederatedSource.class);
    when(source.getConfigurationPid()).thenReturn(TEST_CONFIG_PID);
    when(source.getId()).thenReturn(TEST_SOURCE_ID);

    ServiceReference<FederatedSource> ref = mockServiceReference();
    when(bundleContext.getServiceReferences(FederatedSource.class, null))
        .thenReturn(Collections.singletonList(ref));
    when(bundleContext.getService(ref)).thenReturn(source);

    SourceDescriptor descriptor = mock(SourceDescriptor.class);
    when(descriptor.getSourceId()).thenReturn("differentSourceId");

    Set<SourceDescriptor> descriptors = new HashSet<>();
    descriptors.add(descriptor);

    when(catalogFramework.getSourceInfo(any())).thenReturn(sourceInfoResponse);
    when(sourceInfoResponse.getSourceInfo()).thenReturn(descriptors);

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void testGetConfigurationDataWithNoSourcesButMatchingPid() throws Exception {
    ConfiguredFederatedSource source = mock(ConfiguredFederatedSource.class);
    when(source.getConfigurationPid()).thenReturn(TEST_CONFIG_PID);
    when(source.isAvailable()).thenReturn(true);

    ServiceReference<FederatedSource> ref = mockServiceReference();
    when(bundleContext.getServiceReferences(FederatedSource.class, null))
        .thenReturn(Collections.singletonList(ref));
    when(bundleContext.getService(ref)).thenReturn(source);

    when(catalogFramework.getSourceInfo(any())).thenReturn(sourceInfoResponse);
    when(sourceInfoResponse.getSourceInfo()).thenReturn(Collections.emptySet());

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result, hasEntry("available", true));
  }

  @Test
  void testGetConfigurationDataWithSourceUnavailableException() throws Exception {
    when(catalogFramework.getSourceInfo(any())).thenThrow(new SourceUnavailableException("error"));

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result, is(notNullValue()));
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void testGetConfigurationDataWithCatalogProvider() throws Exception {
    ConfiguredCatalogProvider provider = mock(ConfiguredCatalogProvider.class);
    when(provider.getConfigurationPid()).thenReturn(TEST_CONFIG_PID);
    when(provider.getId()).thenReturn(TEST_SOURCE_ID);

    ServiceReference<CatalogProvider> ref = mockServiceReference();
    when(bundleContext.getServiceReferences(CatalogProvider.class, null))
        .thenReturn(Collections.singletonList(ref));
    when(bundleContext.getService(ref)).thenReturn(provider);

    SourceDescriptor descriptor = mock(SourceDescriptor.class);
    when(descriptor.getSourceId()).thenReturn(TEST_SOURCE_ID);
    when(descriptor.isAvailable()).thenReturn(true);

    Set<SourceDescriptor> descriptors = new HashSet<>();
    descriptors.add(descriptor);

    when(catalogFramework.getSourceInfo(any())).thenReturn(sourceInfoResponse);
    when(sourceInfoResponse.getSourceInfo()).thenReturn(descriptors);

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result, hasEntry("available", true));
    assertThat(result, hasEntry("sourceId", TEST_SOURCE_ID));
  }

  @Test
  void testGetConfigurationDataWithNonConfiguredService() throws Exception {
    FederatedSource source = mock(FederatedSource.class);

    ServiceReference<FederatedSource> ref = mockServiceReference();
    when(bundleContext.getServiceReferences(FederatedSource.class, null))
        .thenReturn(Collections.singletonList(ref));
    when(bundleContext.getService(ref)).thenReturn(source);

    when(catalogFramework.getSourceInfo(any())).thenReturn(sourceInfoResponse);
    when(sourceInfoResponse.getSourceInfo()).thenReturn(Collections.emptySet());

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void testGetConfigurationDataWithEmptyConfigurationPid() throws Exception {
    ConfiguredFederatedSource source = mock(ConfiguredFederatedSource.class);
    when(source.getConfigurationPid()).thenReturn("");
    when(source.getId()).thenReturn(TEST_SOURCE_ID);

    ServiceReference<FederatedSource> ref = mockServiceReference();
    when(bundleContext.getServiceReferences(FederatedSource.class, null))
        .thenReturn(Collections.singletonList(ref));
    when(bundleContext.getService(ref)).thenReturn(source);

    when(catalogFramework.getSourceInfo(any())).thenReturn(sourceInfoResponse);
    when(sourceInfoResponse.getSourceInfo()).thenReturn(Collections.emptySet());

    Map<String, Object> result =
        plugin.getConfigurationData(TEST_CONFIG_PID, Collections.emptyMap(), bundleContext);

    assertThat(result.isEmpty(), is(true));
  }

  @SuppressWarnings("unchecked")
  private <T> ServiceReference<T> mockServiceReference() {
    return mock(ServiceReference.class);
  }

  private interface ConfiguredFederatedSource extends FederatedSource, ConfiguredService {}

  private interface ConfiguredCatalogProvider extends CatalogProvider, ConfiguredService {}
}
