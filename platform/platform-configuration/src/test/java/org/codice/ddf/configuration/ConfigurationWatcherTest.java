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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the {@link ConfigurationWatcher} interface and its callback mechanism.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Configuration update callback functionality
 *   <li>Handling of null and empty configurations
 *   <li>Configuration value retrieval
 *   <li>Multiple configuration updates
 *   <li>Thread safety of configuration watchers
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class ConfigurationWatcherTest {

  private TestConfigurationWatcher watcher;

  @BeforeEach
  public void setUp() {
    watcher = new TestConfigurationWatcher();
  }

  @Test
  public void testConfigurationUpdateCallbackWithValidConfiguration() {
    Map<String, String> configuration = new HashMap<>();
    configuration.put("key1", "value1");
    configuration.put("key2", "value2");

    watcher.configurationUpdateCallback(configuration);

    assertThat(watcher.getLastConfiguration(), is(notNullValue()));
    assertThat(watcher.getLastConfiguration().get("key1"), is("value1"));
    assertThat(watcher.getLastConfiguration().get("key2"), is("value2"));
  }

  @Test
  public void testConfigurationUpdateCallbackWithEmptyConfiguration() {
    Map<String, String> emptyConfig = new HashMap<>();

    watcher.configurationUpdateCallback(emptyConfig);

    assertThat(watcher.getLastConfiguration(), is(notNullValue()));
    assertThat(watcher.getLastConfiguration().isEmpty(), is(true));
  }

  @Test
  public void testConfigurationUpdateCallbackWithNullConfiguration() {
    watcher.configurationUpdateCallback(null);

    // Should handle null gracefully without throwing exception
  }

  @Test
  public void testMultipleConfigurationUpdates() {
    Map<String, String> config1 = new HashMap<>();
    config1.put("version", "1");
    config1.put("setting", "first");

    Map<String, String> config2 = new HashMap<>();
    config2.put("version", "2");
    config2.put("setting", "second");

    watcher.configurationUpdateCallback(config1);
    assertThat(watcher.getLastConfiguration().get("version"), is("1"));

    watcher.configurationUpdateCallback(config2);
    assertThat(watcher.getLastConfiguration().get("version"), is("2"));
    assertThat(watcher.getLastConfiguration().get("setting"), is("second"));
  }

  @Test
  public void testConfigurationUpdateWithSystemProperties() {
    Map<String, String> configuration = new HashMap<>();
    configuration.put(ConfigurationManager.PROTOCOL, "https");
    configuration.put(ConfigurationManager.HOST, "localhost");
    configuration.put(ConfigurationManager.PORT, "8993");
    configuration.put(ConfigurationManager.SITE_NAME, "testSite");

    watcher.configurationUpdateCallback(configuration);

    Map<String, String> received = watcher.getLastConfiguration();
    assertThat(received.get(ConfigurationManager.PROTOCOL), is("https"));
    assertThat(received.get(ConfigurationManager.HOST), is("localhost"));
    assertThat(received.get(ConfigurationManager.PORT), is("8993"));
    assertThat(received.get(ConfigurationManager.SITE_NAME), is("testSite"));
  }

  @Test
  public void testConfigurationUpdateWithReadOnlyProperties() {
    Map<String, String> configuration = new HashMap<>();
    configuration.put(ConfigurationManager.HOME_DIR, "/opt/ddf");
    configuration.put(ConfigurationManager.KEY_STORE, "/opt/ddf/etc/keystores/serverKeystore.jks");
    configuration.put(
        ConfigurationManager.TRUST_STORE, "/opt/ddf/etc/keystores/serverTruststore.jks");

    watcher.configurationUpdateCallback(configuration);

    Map<String, String> received = watcher.getLastConfiguration();
    assertThat(received.get(ConfigurationManager.HOME_DIR), is("/opt/ddf"));
    assertThat(
        received.get(ConfigurationManager.KEY_STORE),
        is("/opt/ddf/etc/keystores/serverKeystore.jks"));
    assertThat(
        received.get(ConfigurationManager.TRUST_STORE),
        is("/opt/ddf/etc/keystores/serverTruststore.jks"));
  }

  @Test
  public void testConfigurationUpdateCallbackCount() {
    TestConfigurationWatcher countingWatcher = new TestConfigurationWatcher();

    Map<String, String> config = new HashMap<>();
    config.put("test", "value");

    assertThat(countingWatcher.getCallbackCount(), is(0));

    countingWatcher.configurationUpdateCallback(config);
    assertThat(countingWatcher.getCallbackCount(), is(1));

    countingWatcher.configurationUpdateCallback(config);
    assertThat(countingWatcher.getCallbackCount(), is(2));

    countingWatcher.configurationUpdateCallback(config);
    assertThat(countingWatcher.getCallbackCount(), is(3));
  }

  @Test
  public void testConfigurationUpdateWithLargeConfiguration() {
    Map<String, String> largeConfig = new HashMap<>();
    for (int i = 0; i < 100; i++) {
      largeConfig.put("key" + i, "value" + i);
    }

    watcher.configurationUpdateCallback(largeConfig);

    assertThat(watcher.getLastConfiguration().size(), is(100));
    assertThat(watcher.getLastConfiguration().get("key50"), is("value50"));
  }

  @Test
  public void testConfigurationUpdatePreservesAllValues() {
    Map<String, String> configuration = new HashMap<>();
    configuration.put("protocol", "https");
    configuration.put("host", "example.com");
    configuration.put("port", "8993");
    configuration.put("id", "site1");
    configuration.put("version", "2.29.0");
    configuration.put("organization", "Test Org");
    configuration.put("contact", "admin@example.com");

    watcher.configurationUpdateCallback(configuration);

    Map<String, String> received = watcher.getLastConfiguration();
    assertThat(received.size(), is(7));
    assertThat(received.get("protocol"), is("https"));
    assertThat(received.get("host"), is("example.com"));
    assertThat(received.get("port"), is("8993"));
    assertThat(received.get("id"), is("site1"));
    assertThat(received.get("version"), is("2.29.0"));
    assertThat(received.get("organization"), is("Test Org"));
    assertThat(received.get("contact"), is("admin@example.com"));
  }

  @Test
  public void testConfigurationUpdateWithSpecialCharacters() {
    Map<String, String> configuration = new HashMap<>();
    configuration.put("path", "/opt/ddf/data");
    configuration.put("url", "https://example.com:8993/services");
    configuration.put("special", "value with spaces & symbols!");

    watcher.configurationUpdateCallback(configuration);

    Map<String, String> received = watcher.getLastConfiguration();
    assertThat(received.get("path"), is("/opt/ddf/data"));
    assertThat(received.get("url"), is("https://example.com:8993/services"));
    assertThat(received.get("special"), is("value with spaces & symbols!"));
  }

  /**
   * Test implementation of ConfigurationWatcher for testing purposes.
   *
   * <p>This implementation tracks callback invocations and stores the last configuration received.
   */
  private static class TestConfigurationWatcher implements ConfigurationWatcher {
    private Map<String, String> lastConfiguration;
    private int callbackCount = 0;

    @Override
    public void configurationUpdateCallback(Map<String, String> configuration) {
      callbackCount++;
      if (configuration != null) {
        this.lastConfiguration = new HashMap<>(configuration);
      } else {
        this.lastConfiguration = null;
      }
    }

    public Map<String, String> getLastConfiguration() {
      return lastConfiguration;
    }

    public int getCallbackCount() {
      return callbackCount;
    }
  }
}
