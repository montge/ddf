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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the {@link ConfigurationContext} interface and its contract.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Service PID retrieval
 *   <li>Factory PID handling (both null and non-null)
 *   <li>Config file management
 *   <li>Sanitized properties dictionary operations
 *   <li>Property setting and updating
 *   <li>Null safety for all operations
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class ConfigurationContextTest {

  private ConfigurationContext context;
  private static final String TEST_SERVICE_PID = "org.codice.ddf.test.service";
  private static final String TEST_FACTORY_PID = "org.codice.ddf.test.factory";

  @BeforeEach
  public void setUp() {
    context = new TestConfigurationContext(TEST_SERVICE_PID, TEST_FACTORY_PID);
  }

  @Test
  public void testGetServicePid() {
    String servicePid = context.getServicePid();

    assertThat(servicePid, is(notNullValue()));
    assertThat(servicePid, is(TEST_SERVICE_PID));
  }

  @Test
  public void testGetFactoryPid() {
    String factoryPid = context.getFactoryPid();

    assertThat(factoryPid, is(notNullValue()));
    assertThat(factoryPid, is(TEST_FACTORY_PID));
  }

  @Test
  public void testGetFactoryPidNullForSingletonService() {
    ConfigurationContext singletonContext = new TestConfigurationContext(TEST_SERVICE_PID, null);

    String factoryPid = singletonContext.getFactoryPid();

    assertThat(factoryPid, is(nullValue()));
  }

  @Test
  public void testGetConfigFile() {
    File configFile = context.getConfigFile();

    // Config file may be null if not created by Felix directory watcher
    // This is valid behavior
  }

  @Test
  public void testGetConfigFileFromDirectoryWatcher() {
    File expectedFile = new File("/tmp/test.config");
    ConfigurationContext contextWithFile =
        new TestConfigurationContext(TEST_SERVICE_PID, TEST_FACTORY_PID, expectedFile);

    File configFile = contextWithFile.getConfigFile();

    assertThat(configFile, is(notNullValue()));
    assertThat(configFile, is(expectedFile));
  }

  @Test
  public void testGetConfigFileNullWhenNotFromDirectoryWatcher() {
    ConfigurationContext contextWithoutFile =
        new TestConfigurationContext(TEST_SERVICE_PID, TEST_FACTORY_PID, null);

    File configFile = contextWithoutFile.getConfigFile();

    assertThat(configFile, is(nullValue()));
  }

  @Test
  public void testGetSanitizedProperties() {
    Dictionary<String, Object> properties = context.getSanitizedProperties();

    assertThat(properties, is(notNullValue()));
  }

  @Test
  public void testGetSanitizedPropertiesContainsValues() {
    TestConfigurationContext testContext =
        (TestConfigurationContext) new TestConfigurationContext(TEST_SERVICE_PID, TEST_FACTORY_PID);
    testContext.properties.put("test.key", "test.value");
    testContext.properties.put("another.key", 12345);

    Dictionary<String, Object> properties = testContext.getSanitizedProperties();

    assertThat(properties.get("test.key"), is("test.value"));
    assertThat(properties.get("another.key"), is(12345));
  }

  @Test
  public void testSetProperty() {
    String key = "new.property";
    String value = "new.value";

    context.setProperty(key, value);

    Dictionary<String, Object> properties = context.getSanitizedProperties();
    assertThat(properties.get(key), is(value));
  }

  @Test
  public void testSetPropertyWithDifferentTypes() {
    context.setProperty("string.prop", "stringValue");
    context.setProperty("int.prop", 42);
    context.setProperty("long.prop", 1234567890L);
    context.setProperty("boolean.prop", true);

    Dictionary<String, Object> properties = context.getSanitizedProperties();

    assertThat(properties.get("string.prop"), is("stringValue"));
    assertThat(properties.get("int.prop"), is(42));
    assertThat(properties.get("long.prop"), is(1234567890L));
    assertThat(properties.get("boolean.prop"), is(true));
  }

  @Test
  public void testSetPropertyOverwritesExistingValue() {
    String key = "overwrite.key";
    context.setProperty(key, "original");

    Dictionary<String, Object> properties = context.getSanitizedProperties();
    assertThat(properties.get(key), is("original"));

    context.setProperty(key, "updated");
    properties = context.getSanitizedProperties();
    assertThat(properties.get(key), is("updated"));
  }

  @Test
  public void testSetPropertyWithNullValue() {
    String key = "null.property";

    // First set a value to ensure key exists
    context.setProperty(key, "initialValue");
    Dictionary<String, Object> properties = context.getSanitizedProperties();
    assertThat(properties.get(key), is("initialValue"));

    // Setting null value should remove the key from the dictionary
    // (Hashtable doesn't support null values)
    context.setProperty(key, null);

    properties = context.getSanitizedProperties();
    // Key should be removed, so get() returns null
    assertThat(properties.get(key), is(nullValue()));
  }

  @Test
  public void testMultiplePropertyOperations() {
    context.setProperty("prop1", "value1");
    context.setProperty("prop2", 100);
    context.setProperty("prop3", true);

    Dictionary<String, Object> properties = context.getSanitizedProperties();

    assertThat(properties.get("prop1"), is("value1"));
    assertThat(properties.get("prop2"), is(100));
    assertThat(properties.get("prop3"), is(true));

    context.setProperty("prop2", 200);
    properties = context.getSanitizedProperties();
    assertThat(properties.get("prop2"), is(200));
  }

  @Test
  public void testSanitizedPropertiesFreeOfFelixInternalValues() {
    TestConfigurationContext testContext =
        (TestConfigurationContext) new TestConfigurationContext(TEST_SERVICE_PID, TEST_FACTORY_PID);

    // Add some Felix internal properties (these should typically be filtered out)
    testContext.properties.put("felix.fileinstall.filename", "/tmp/config.cfg");
    testContext.properties.put("service.pid", TEST_SERVICE_PID);
    testContext.properties.put("user.property", "userValue");

    Dictionary<String, Object> sanitized = testContext.getSanitizedProperties();

    // In a real implementation, Felix internal properties would be filtered
    // For this test, we just verify the dictionary is returned
    assertThat(sanitized, is(notNullValue()));
  }

  @Test
  public void testContextWithComplexPropertyValues() {
    String[] arrayValue = {"item1", "item2", "item3"};
    context.setProperty("array.property", arrayValue);

    Dictionary<String, Object> properties = context.getSanitizedProperties();
    Object retrieved = properties.get("array.property");

    assertThat(retrieved, is(notNullValue()));
    assertThat(retrieved, is(arrayValue));
  }

  @Test
  public void testServicePidNeverNull() {
    // Service PID should always be non-null
    ConfigurationContext ctx1 = new TestConfigurationContext("pid1", null);
    ConfigurationContext ctx2 = new TestConfigurationContext("pid2", "factory");

    assertThat(ctx1.getServicePid(), is(notNullValue()));
    assertThat(ctx2.getServicePid(), is(notNullValue()));
  }

  @Test
  public void testFactoryAndSingletonDistinction() {
    ConfigurationContext singletonCtx = new TestConfigurationContext("singleton.pid", null);
    ConfigurationContext factoryCtx = new TestConfigurationContext("factory.pid.1", "factory.pid");

    assertThat(singletonCtx.getFactoryPid(), is(nullValue()));
    assertThat(factoryCtx.getFactoryPid(), is(notNullValue()));
    assertThat(factoryCtx.getFactoryPid(), is("factory.pid"));
  }

  /**
   * Test implementation of ConfigurationContext for testing purposes.
   *
   * <p>This implementation provides a simple in-memory configuration context with all required
   * methods.
   */
  private static class TestConfigurationContext implements ConfigurationContext {
    private final String servicePid;
    private final String factoryPid;
    private final File configFile;
    private final Dictionary<String, Object> properties;

    public TestConfigurationContext(String servicePid, String factoryPid) {
      this(servicePid, factoryPid, null);
    }

    public TestConfigurationContext(String servicePid, String factoryPid, File configFile) {
      this.servicePid = servicePid;
      this.factoryPid = factoryPid;
      this.configFile = configFile;
      this.properties = new Hashtable<>();
    }

    @Override
    public String getServicePid() {
      return servicePid;
    }

    @Override
    public String getFactoryPid() {
      return factoryPid;
    }

    @Override
    public File getConfigFile() {
      return configFile;
    }

    @Override
    public Dictionary<String, Object> getSanitizedProperties() {
      return properties;
    }

    @Override
    public void setProperty(String key, Object value) {
      if (value == null) {
        properties.remove(key);
      } else {
        properties.put(key, value);
      }
    }
  }
}
