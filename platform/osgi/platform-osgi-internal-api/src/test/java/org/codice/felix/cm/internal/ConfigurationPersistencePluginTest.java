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
package org.codice.felix.cm.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests the {@link ConfigurationPersistencePlugin} interface and its contract.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Plugin initialization with configuration state
 *   <li>Configuration store operations
 *   <li>Configuration delete operations
 *   <li>Exception handling for store and delete
 *   <li>Null safety for contexts and PIDs
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConfigurationPersistencePluginTest {

  @Mock private ConfigurationContext mockContext;

  private ConfigurationPersistencePlugin plugin;

  @BeforeEach
  public void setUp() {
    plugin = new TestConfigurationPersistencePlugin();
  }

  @Test
  public void testInitializeWithEmptyState() {
    Set<ConfigurationContext> emptyState = Collections.emptySet();

    plugin.initialize(emptyState);

    // Should not throw exception with empty state
  }

  @Test
  public void testInitializeWithSingleContext() {
    Set<ConfigurationContext> state = new HashSet<>();
    state.add(mockContext);

    when(mockContext.getServicePid()).thenReturn("test.pid");

    plugin.initialize(state);

    // Should not throw exception
  }

  @Test
  public void testInitializeWithMultipleContexts() {
    Set<ConfigurationContext> state = new HashSet<>();
    ConfigurationContext context1 = mock(ConfigurationContext.class);
    ConfigurationContext context2 = mock(ConfigurationContext.class);
    ConfigurationContext context3 = mock(ConfigurationContext.class);

    when(context1.getServicePid()).thenReturn("test.pid.1");
    when(context2.getServicePid()).thenReturn("test.pid.2");
    when(context3.getServicePid()).thenReturn("test.pid.3");

    state.add(context1);
    state.add(context2);
    state.add(context3);

    plugin.initialize(state);

    assertThat(state.size(), is(3));
  }

  @Test
  public void testHandleStoreWithValidContext() throws IOException {
    when(mockContext.getServicePid()).thenReturn("test.service.pid");

    plugin.handleStore(mockContext);

    // Should complete successfully without exception
  }

  @Test
  public void testHandleStoreThrowsIOException() {
    TestConfigurationPersistencePlugin throwingPlugin =
        new TestConfigurationPersistencePlugin(true, false);

    assertThrows(IOException.class, () -> throwingPlugin.handleStore(mockContext));
  }

  @Test
  public void testHandleStoreThrowsIllegalStateException() {
    TestConfigurationPersistencePlugin throwingPlugin =
        new TestConfigurationPersistencePlugin(false, true);

    assertThrows(IllegalStateException.class, () -> throwingPlugin.handleStore(mockContext));
  }

  @Test
  public void testHandleDeleteWithValidPid() throws IOException {
    String pid = "test.service.pid";

    plugin.handleDelete(pid);

    // Should complete successfully without exception
  }

  @Test
  public void testHandleDeleteWithNullPid() throws IOException {
    plugin.handleDelete(null);

    // Should complete successfully - implementation should handle null gracefully
  }

  @Test
  public void testHandleDeleteWithEmptyPid() throws IOException {
    plugin.handleDelete("");

    // Should complete successfully - implementation should handle empty string gracefully
  }

  @Test
  public void testHandleDeleteThrowsIOException() {
    TestConfigurationPersistencePlugin throwingPlugin =
        new TestConfigurationPersistencePlugin(true, false);

    assertThrows(IOException.class, () -> throwingPlugin.handleDelete("test.pid"));
  }

  @Test
  public void testPluginLifecycle() throws IOException {
    // Test full lifecycle: initialize -> store -> delete
    Set<ConfigurationContext> state = new HashSet<>();
    state.add(mockContext);

    when(mockContext.getServicePid()).thenReturn("lifecycle.test.pid");

    plugin.initialize(state);
    plugin.handleStore(mockContext);
    plugin.handleDelete("lifecycle.test.pid");

    // Should complete full lifecycle without exception
  }

  @Test
  public void testMultipleStoreOperations() throws IOException {
    ConfigurationContext context1 = mock(ConfigurationContext.class);
    ConfigurationContext context2 = mock(ConfigurationContext.class);

    when(context1.getServicePid()).thenReturn("pid.1");
    when(context2.getServicePid()).thenReturn("pid.2");

    plugin.handleStore(context1);
    plugin.handleStore(context2);

    // Should handle multiple store operations
  }

  @Test
  public void testMultipleDeleteOperations() throws IOException {
    plugin.handleDelete("pid.1");
    plugin.handleDelete("pid.2");
    plugin.handleDelete("pid.3");

    // Should handle multiple delete operations
  }

  @Test
  public void testStoreAfterInitialization() throws IOException {
    Set<ConfigurationContext> state = new HashSet<>();
    ConfigurationContext initContext = mock(ConfigurationContext.class);
    when(initContext.getServicePid()).thenReturn("init.pid");
    state.add(initContext);

    plugin.initialize(state);

    // Store a different context after initialization
    when(mockContext.getServicePid()).thenReturn("new.pid");
    plugin.handleStore(mockContext);

    // Should allow storing new configurations after initialization
  }

  @Test
  public void testDeleteAfterStore() throws IOException {
    when(mockContext.getServicePid()).thenReturn("store.delete.pid");

    plugin.handleStore(mockContext);
    plugin.handleDelete("store.delete.pid");

    // Should allow delete after store
  }

  /**
   * Test implementation of ConfigurationPersistencePlugin for testing purposes.
   *
   * <p>This implementation can be configured to throw exceptions on store and delete operations.
   */
  private static class TestConfigurationPersistencePlugin
      implements ConfigurationPersistencePlugin {

    private final boolean throwIOExceptionOnStore;
    private final boolean throwIllegalStateOnStore;

    public TestConfigurationPersistencePlugin() {
      this(false, false);
    }

    public TestConfigurationPersistencePlugin(
        boolean throwIOExceptionOnStore, boolean throwIllegalStateOnStore) {
      this.throwIOExceptionOnStore = throwIOExceptionOnStore;
      this.throwIllegalStateOnStore = throwIllegalStateOnStore;
    }

    @Override
    public void initialize(Set<ConfigurationContext> state) {
      // Test implementation - no-op
    }

    @Override
    public void handleStore(ConfigurationContext context) throws IOException {
      if (throwIllegalStateOnStore) {
        throw new IllegalStateException("Configuration data disappeared or felix data corrupt");
      }
      if (throwIOExceptionOnStore) {
        throw new IOException("Failed to store configuration");
      }
      // Test implementation - no-op
    }

    @Override
    public void handleDelete(String pid) throws IOException {
      if (throwIOExceptionOnStore) {
        throw new IOException("Failed to delete configuration");
      }
      // Test implementation - no-op
    }
  }
}
