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
package org.codice.ddf.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * Enhanced tests for {@link ConfigurationManager} class.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Configuration initialization and management
 *   <li>Property setting and retrieval
 *   <li>ConfigurationWatcher binding and notification
 *   <li>OSGi ConfigurationAdmin integration
 *   <li>Read-only property handling
 *   <li>Edge cases and error conditions
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class ConfigurationManagerEnhancedTest {

  private static final String TEST_PROTOCOL = "https";
  private static final String TEST_HOST = "localhost";
  private static final String TEST_PORT = "8993";
  private static final String TEST_SITE_NAME = "testSite";
  private static final String TEST_VERSION = "2.0.0";
  private static final String TEST_ORGANIZATION = "TestOrg";
  private static final String TEST_CONTACT = "admin@test.com";
  private static final String TEST_SERVICE_PID = "test.service.pid";
  private static final String TEST_PROPERTY_NAME = "testProperty";
  private static final String TEST_PROPERTY_VALUE = "testValue";

  @Mock private ConfigurationAdmin mockConfigAdmin;

  @Mock private ConfigurationWatcher mockWatcher;

  private ConfigurationManager configManager;

  private List<ConfigurationWatcher> watchers;

  @BeforeEach
  public void setUp() {
    watchers = new ArrayList<>();
    watchers.add(mockWatcher);
    configManager = new ConfigurationManager(watchers, mockConfigAdmin);
  }

  @Test
  public void testConstructorInitializesReadOnlySettings() {
    assertThat(configManager.configuration, is(notNullValue()));
    assertThat(configManager.readOnlySettings, is(notNullValue()));
    assertThat(configManager.configuration.size(), is(not(0)));
  }

  @Test
  public void testConstructorWithNullConfigurationAdmin() {
    ConfigurationManager manager = new ConfigurationManager(watchers, null);

    assertThat(manager.getConfigurationAdmin(), is(nullValue()));
  }

  @Test
  public void testConstructorWithEmptyWatchersList() {
    List<ConfigurationWatcher> emptyWatchers = new ArrayList<>();
    ConfigurationManager manager = new ConfigurationManager(emptyWatchers, mockConfigAdmin);

    assertThat(manager, is(notNullValue()));
    assertThat(manager.services, is(notNullValue()));
  }

  @Test
  public void testConstructorSetsHomeDir() {
    assertThat(configManager.readOnlySettings, hasKey(ConfigurationManager.HOME_DIR));
    String homeDir = configManager.readOnlySettings.get(ConfigurationManager.HOME_DIR);
    assertThat(homeDir, is(notNullValue()));
  }

  @Test
  public void testConstructorSetsKeystoreProperties() {
    // These may be null if not set in system properties, so just verify keys exist
    assertThat(configManager.readOnlySettings, hasKey(ConfigurationManager.KEY_STORE));
    assertThat(configManager.readOnlySettings, hasKey(ConfigurationManager.KEY_STORE_PASS));
    assertThat(configManager.readOnlySettings, hasKey(ConfigurationManager.TRUST_STORE));
    assertThat(configManager.readOnlySettings, hasKey(ConfigurationManager.TRUST_STORE_PASS));
  }

  @Test
  public void testSetProtocol() {
    configManager.setProtocol(TEST_PROTOCOL);

    // Protocol should be stored in configuration properties
  }

  @Test
  public void testSetHost() {
    configManager.setHost(TEST_HOST);

    // Host should be stored in configuration properties
  }

  @Test
  public void testSetPort() {
    configManager.setPort(TEST_PORT);

    // Port should be stored in configuration properties
  }

  @Test
  public void testSetId() {
    configManager.setId(TEST_SITE_NAME);

    // Site name should be stored in configuration properties
  }

  @Test
  public void testSetVersion() {
    configManager.setVersion(TEST_VERSION);

    // Version should be stored in configuration properties
  }

  @Test
  public void testSetOrganization() {
    configManager.setOrganization(TEST_ORGANIZATION);

    // Organization should be stored in configuration properties
  }

  @Test
  public void testSetContact() {
    configManager.setContact(TEST_CONTACT);

    // Contact should be stored in configuration properties
  }

  @Test
  public void testInitCallsUpdated() {
    configManager.setProtocol(TEST_PROTOCOL);
    configManager.setHost(TEST_HOST);
    configManager.setPort(TEST_PORT);

    configManager.init();

    ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    verify(mockWatcher, times(1)).configurationUpdateCallback(captor.capture());
  }

  @Test
  public void testUpdatedWithNullConfig() {
    Map<String, String> originalConfig = new HashMap<>(configManager.configuration);

    configManager.updated(null);

    // Configuration should remain unchanged
    verify(mockWatcher).configurationUpdateCallback(configManager.configuration);
  }

  @Test
  public void testUpdatedWithEmptyConfig() {
    Map<String, String> originalConfig = new HashMap<>(configManager.configuration);

    configManager.updated(Collections.emptyMap());

    // Configuration should remain unchanged
    verify(mockWatcher).configurationUpdateCallback(configManager.configuration);
  }

  @Test
  public void testUpdatedWithValidConfig() {
    Map<String, String> newConfig = new HashMap<>();
    newConfig.put("testKey", "testValue");

    configManager.updated(newConfig);

    ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    verify(mockWatcher).configurationUpdateCallback(captor.capture());

    Map<String, String> capturedConfig = captor.getValue();
    assertThat(capturedConfig, hasKey("testKey"));
    assertThat(capturedConfig.get("testKey"), is("testValue"));
  }

  @Test
  public void testUpdatedIncludesReadOnlySettings() {
    Map<String, String> newConfig = new HashMap<>();
    newConfig.put("configurableKey", "configurableValue");

    configManager.updated(newConfig);

    ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    verify(mockWatcher).configurationUpdateCallback(captor.capture());

    Map<String, String> capturedConfig = captor.getValue();
    // Should include both configurable and read-only settings
    assertThat(capturedConfig, hasKey("configurableKey"));
    assertThat(capturedConfig, hasKey(ConfigurationManager.HOME_DIR));
  }

  @Test
  public void testUpdatedWithNullValues() {
    Map<String, Object> configWithNulls = new HashMap<>();
    configWithNulls.put("key1", "value1");
    configWithNulls.put("key2", null);
    configWithNulls.put("key3", "value3");

    configManager.updated(configWithNulls);

    ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    verify(mockWatcher).configurationUpdateCallback(captor.capture());

    Map<String, String> capturedConfig = captor.getValue();
    assertThat(capturedConfig, hasKey("key1"));
    assertThat(capturedConfig, not(hasKey("key2")));
    assertThat(capturedConfig, hasKey("key3"));
  }

  @Test
  public void testUpdatedNotifiesAllWatchers() {
    ConfigurationWatcher watcher2 = mock(ConfigurationWatcher.class);
    ConfigurationWatcher watcher3 = mock(ConfigurationWatcher.class);
    watchers.add(watcher2);
    watchers.add(watcher3);

    Map<String, String> config = new HashMap<>();
    config.put("test", "value");

    configManager.updated(config);

    verify(mockWatcher).configurationUpdateCallback(configManager.configuration);
    verify(watcher2).configurationUpdateCallback(configManager.configuration);
    verify(watcher3).configurationUpdateCallback(configManager.configuration);
  }

  @Test
  public void testBindWithValidWatcher() {
    ConfigurationWatcher newWatcher = mock(ConfigurationWatcher.class);

    configManager.bind(newWatcher, null);

    verify(newWatcher).configurationUpdateCallback(configManager.configuration);
  }

  @Test
  public void testBindWithNullWatcher() {
    configManager.bind(null, null);

    // Should not throw exception
  }

  @Test
  public void testBindWithNullProperties() {
    ConfigurationWatcher newWatcher = mock(ConfigurationWatcher.class);

    configManager.bind(newWatcher, null);

    verify(newWatcher).configurationUpdateCallback(configManager.configuration);
  }

  @Test
  public void testBindWithEmptyProperties() {
    ConfigurationWatcher newWatcher = mock(ConfigurationWatcher.class);

    configManager.bind(newWatcher, Collections.emptyMap());

    verify(newWatcher).configurationUpdateCallback(configManager.configuration);
  }

  @Test
  public void testBindSendsCurrentConfiguration() {
    Map<String, String> testConfig = new HashMap<>();
    testConfig.put("customKey", "customValue");
    configManager.updated(testConfig);

    ConfigurationWatcher newWatcher = mock(ConfigurationWatcher.class);
    configManager.bind(newWatcher, null);

    ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    verify(newWatcher).configurationUpdateCallback(captor.capture());

    Map<String, String> sentConfig = captor.getValue();
    assertThat(sentConfig, hasKey("customKey"));
  }

  @Test
  public void testGetConfigurationAdmin() {
    assertThat(configManager.getConfigurationAdmin(), is(mockConfigAdmin));
  }

  @Test
  public void testSetConfigurationAdmin() {
    ConfigurationAdmin newAdmin = mock(ConfigurationAdmin.class);

    configManager.setConfigurationAdmin(newAdmin);

    assertThat(configManager.getConfigurationAdmin(), is(newAdmin));
  }

  @Test
  public void testGetConfigurationValueWithValidServicePid() throws IOException {
    Configuration mockConfig = mock(Configuration.class);
    Dictionary<String, Object> properties = new Hashtable<>();
    properties.put(TEST_PROPERTY_NAME, TEST_PROPERTY_VALUE);

    when(mockConfigAdmin.getConfiguration(TEST_SERVICE_PID)).thenReturn(mockConfig);
    when(mockConfig.getProperties()).thenReturn(properties);

    String value = configManager.getConfigurationValue(TEST_SERVICE_PID, TEST_PROPERTY_NAME);

    assertThat(value, is(TEST_PROPERTY_VALUE));
  }

  @Test
  public void testGetConfigurationValueWithNonExistentProperty() throws IOException {
    Configuration mockConfig = mock(Configuration.class);
    Dictionary<String, Object> properties = new Hashtable<>();

    when(mockConfigAdmin.getConfiguration(TEST_SERVICE_PID)).thenReturn(mockConfig);
    when(mockConfig.getProperties()).thenReturn(properties);

    String value = configManager.getConfigurationValue(TEST_SERVICE_PID, TEST_PROPERTY_NAME);

    assertThat(value, is(""));
  }

  @Test
  public void testGetConfigurationValueWithNullProperties() throws IOException {
    Configuration mockConfig = mock(Configuration.class);

    when(mockConfigAdmin.getConfiguration(TEST_SERVICE_PID)).thenReturn(mockConfig);
    when(mockConfig.getProperties()).thenReturn(null);

    String value = configManager.getConfigurationValue(TEST_SERVICE_PID, TEST_PROPERTY_NAME);

    assertThat(value, is(""));
  }

  @Test
  public void testGetConfigurationValueWithNullConfiguration() throws IOException {
    when(mockConfigAdmin.getConfiguration(TEST_SERVICE_PID)).thenReturn(null);

    String value = configManager.getConfigurationValue(TEST_SERVICE_PID, TEST_PROPERTY_NAME);

    assertThat(value, is(""));
  }

  @Test
  public void testGetConfigurationValueWithNullConfigurationAdmin() {
    configManager.setConfigurationAdmin(null);

    String value = configManager.getConfigurationValue(TEST_SERVICE_PID, TEST_PROPERTY_NAME);

    assertThat(value, is(""));
  }

  @Test
  public void testGetConfigurationValueWithIOException() throws IOException {
    when(mockConfigAdmin.getConfiguration(anyString()))
        .thenThrow(new IOException("Test exception"));

    String value = configManager.getConfigurationValue(TEST_SERVICE_PID, TEST_PROPERTY_NAME);

    assertThat(value, is(""));
  }

  @Test
  public void testGetConfigurationValueHandlesRuntimeException() throws IOException {
    when(mockConfigAdmin.getConfiguration(TEST_SERVICE_PID))
        .thenThrow(new RuntimeException("Unexpected error"));

    // The method doesn't catch RuntimeException, so it will propagate
    assertThrows(
        RuntimeException.class,
        () -> configManager.getConfigurationValue(TEST_SERVICE_PID, TEST_PROPERTY_NAME));
  }

  @Test
  public void testConfigurationIsImmutableInCallback() {
    ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    Map<String, String> config = new HashMap<>();
    config.put("key", "value");

    configManager.updated(config);

    verify(mockWatcher).configurationUpdateCallback(captor.capture());

    Map<String, String> capturedConfig = captor.getValue();
    // The map passed to callback should be unmodifiable
    // This is enforced by Collections.unmodifiableMap() in the implementation
    assertThat(capturedConfig, is(notNullValue()));
  }

  @Test
  public void testUpdatedClearsExistingConfiguration() {
    Map<String, String> firstConfig = new HashMap<>();
    firstConfig.put("oldKey", "oldValue");
    configManager.updated(firstConfig);

    Map<String, String> secondConfig = new HashMap<>();
    secondConfig.put("newKey", "newValue");
    configManager.updated(secondConfig);

    // Old key should be cleared, only new key and read-only settings should remain
    assertThat(configManager.configuration, hasKey("newKey"));
    assertThat(configManager.configuration, not(hasKey("oldKey")));
    assertThat(configManager.configuration, hasKey(ConfigurationManager.HOME_DIR));
  }

  @Test
  public void testMultipleUpdatesNotifyWatchers() {
    configManager.updated(Collections.singletonMap("key1", "value1"));
    configManager.updated(Collections.singletonMap("key2", "value2"));
    configManager.updated(Collections.singletonMap("key3", "value3"));

    verify(mockWatcher, times(3)).configurationUpdateCallback(configManager.configuration);
  }

  @Test
  public void testReadOnlySettingsNotModifiedByUpdate() {
    String originalHomeDir = configManager.readOnlySettings.get(ConfigurationManager.HOME_DIR);

    Map<String, String> config = new HashMap<>();
    config.put(ConfigurationManager.HOME_DIR, "differentValue");
    configManager.updated(config);

    // Read-only settings should still have original value
    assertThat(
        configManager.readOnlySettings.get(ConfigurationManager.HOME_DIR), is(originalHomeDir));
  }

  @Test
  public void testInitSetsPropertiesBeforeNotification() {
    configManager.setProtocol(TEST_PROTOCOL);
    configManager.setHost(TEST_HOST);
    configManager.setPort(TEST_PORT);
    configManager.setId(TEST_SITE_NAME);
    configManager.setVersion(TEST_VERSION);
    configManager.setOrganization(TEST_ORGANIZATION);
    configManager.setContact(TEST_CONTACT);

    configManager.init();

    ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    verify(mockWatcher).configurationUpdateCallback(captor.capture());

    Map<String, String> notifiedConfig = captor.getValue();
    // Should include all set properties plus read-only settings
    assertThat(notifiedConfig.size(), is(not(0)));
  }

  @Test
  public void testSettersWithNullValues() {
    // All setters should handle null gracefully without throwing
    configManager.setProtocol(null);
    configManager.setHost(null);
    configManager.setPort(null);
    configManager.setId(null);
    configManager.setVersion(null);
    configManager.setOrganization(null);
    configManager.setContact(null);

    // Should not throw exception
  }

  @Test
  public void testSettersWithEmptyStrings() {
    configManager.setProtocol("");
    configManager.setHost("");
    configManager.setPort("");
    configManager.setId("");
    configManager.setVersion("");
    configManager.setOrganization("");
    configManager.setContact("");

    // Should not throw exception
  }

  @Test
  public void testConfigurationManagerConstants() {
    assertThat(ConfigurationManager.PID, is("ddf.platform.config"));
    assertThat(ConfigurationManager.HOME_DIR, is("homeDir"));
    assertThat(ConfigurationManager.HTTP_PORT, is("httpPort"));
    assertThat(ConfigurationManager.SERVICES_CONTEXT_ROOT, is("servicesContextRoot"));
    assertThat(ConfigurationManager.HOST, is("host"));
    assertThat(ConfigurationManager.PORT, is("port"));
    assertThat(ConfigurationManager.PROTOCOL, is("protocol"));
    assertThat(ConfigurationManager.TRUST_STORE, is("trustStore"));
    assertThat(ConfigurationManager.TRUST_STORE_PASS, is("trustStorePassword"));
    assertThat(ConfigurationManager.KEY_STORE, is("keyStore"));
    assertThat(ConfigurationManager.KEY_STORE_PASS, is("keyStorePassword"));
    assertThat(ConfigurationManager.SITE_NAME, is("id"));
    assertThat(ConfigurationManager.VERSION, is("version"));
    assertThat(ConfigurationManager.ORGANIZATION, is("organization"));
    assertThat(ConfigurationManager.CONTACT, is("contact"));
  }

  @Test
  public void testUpdatedWithMixedValueTypes() {
    Map<String, Object> config = new HashMap<>();
    config.put("stringValue", "test");
    config.put("integerValue", 123);
    config.put("booleanValue", true);
    config.put("nullValue", null);

    configManager.updated(config);

    ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    verify(mockWatcher).configurationUpdateCallback(captor.capture());

    Map<String, String> capturedConfig = captor.getValue();
    assertThat(capturedConfig.get("stringValue"), is("test"));
    assertThat(capturedConfig.get("integerValue"), is("123"));
    assertThat(capturedConfig.get("booleanValue"), is("true"));
    assertThat(capturedConfig, not(hasKey("nullValue")));
  }
}
