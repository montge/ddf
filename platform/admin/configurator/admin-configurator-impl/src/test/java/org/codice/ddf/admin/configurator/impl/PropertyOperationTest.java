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
package org.codice.ddf.admin.configurator.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.codice.ddf.admin.configurator.ConfiguratorException;
import org.codice.ddf.admin.configurator.Operation;
import org.codice.ddf.admin.configurator.Result;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.junit.jupiter.MockitoExtension;

/** Comprehensive unit tests for {@link PropertyOperation} */
@ExtendWith(MockitoExtension.class)
@EnableRuleMigrationSupport
public class PropertyOperationTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  private PropertyOperation.Actions actions;
  private Path testPropertiesPath;

  @BeforeEach
  public void setUp() throws Exception {
    // Set ddf.home to temp folder for tests that require it
    System.setProperty("ddf.home", tempFolder.getRoot().getAbsolutePath());
    // Create allowed subdirectory (system is an allowed edit location)
    tempFolder.newFolder("system");
    actions = new PropertyOperation.Actions();
    testPropertiesPath =
        Paths.get(tempFolder.getRoot().getAbsolutePath(), "system", "test.properties");
  }

  @Test
  public void testCreateOperationCommit() throws Exception {
    Map<String, String> configs = new HashMap<>();
    configs.put("key1", "value1");
    configs.put("key2", "value2");

    Operation<Void> createOp = actions.create(testPropertiesPath, configs);
    Result<Void> result = createOp.commit();

    assertThat(result, is(notNullValue()));

    // Verify file was created with properties
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(testPropertiesPath.toFile())) {
      props.load(fis);
    }

    assertThat(props.getProperty("key1"), is(equalTo("value1")));
    assertThat(props.getProperty("key2"), is(equalTo("value2")));
  }

  @Test
  public void testCreateOperationRollback() throws Exception {
    Map<String, String> configs = new HashMap<>();
    configs.put("key1", "value1");

    Operation<Void> createOp = actions.create(testPropertiesPath, configs);
    createOp.commit();

    assertThat(testPropertiesPath.toFile().exists(), is(true));

    Result<Void> rollbackResult = createOp.rollback();
    assertThat(rollbackResult, is(notNullValue()));
  }

  @Test
  public void testUpdateOperationCommit() throws Exception {
    // Create initial properties file
    Properties initialProps = new Properties();
    initialProps.setProperty("existing1", "value1");
    initialProps.setProperty("existing2", "value2");
    try (FileOutputStream fos = new FileOutputStream(testPropertiesPath.toFile())) {
      initialProps.store(fos, null);
    }

    Map<String, String> configs = new HashMap<>();
    configs.put("existing2", "updatedValue2");
    configs.put("newKey", "newValue");

    Operation<Void> updateOp = actions.update(testPropertiesPath, configs, true);
    Result<Void> result = updateOp.commit();

    assertThat(result, is(notNullValue()));

    // Verify updated properties
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(testPropertiesPath.toFile())) {
      props.load(fis);
    }

    assertThat(props.getProperty("existing1"), is(equalTo("value1")));
    assertThat(props.getProperty("existing2"), is(equalTo("updatedValue2")));
    assertThat(props.getProperty("newKey"), is(equalTo("newValue")));
  }

  @Test
  public void testUpdateOperationWithoutKeepingExisting() throws Exception {
    // Create initial properties file
    Properties initialProps = new Properties();
    initialProps.setProperty("existing1", "value1");
    initialProps.setProperty("existing2", "value2");
    try (FileOutputStream fos = new FileOutputStream(testPropertiesPath.toFile())) {
      initialProps.store(fos, null);
    }

    Map<String, String> configs = new HashMap<>();
    configs.put("newKey", "newValue");

    Operation<Void> updateOp = actions.update(testPropertiesPath, configs, false);
    Result<Void> result = updateOp.commit();

    assertThat(result, is(notNullValue()));

    // Verify only new properties exist
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(testPropertiesPath.toFile())) {
      props.load(fis);
    }

    assertThat(props.containsKey("existing1"), is(false));
    assertThat(props.containsKey("existing2"), is(false));
    assertThat(props.getProperty("newKey"), is(equalTo("newValue")));
  }

  @Test
  public void testUpdateOperationRollback() throws Exception {
    // Create initial properties file
    Properties initialProps = new Properties();
    initialProps.setProperty("original", "originalValue");
    try (FileOutputStream fos = new FileOutputStream(testPropertiesPath.toFile())) {
      initialProps.store(fos, null);
    }

    Map<String, String> configs = new HashMap<>();
    configs.put("original", "modifiedValue");

    Operation<Void> updateOp = actions.update(testPropertiesPath, configs, true);
    updateOp.commit();

    Result<Void> rollbackResult = updateOp.rollback();
    assertThat(rollbackResult, is(notNullValue()));

    // Verify rollback restored original value
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(testPropertiesPath.toFile())) {
      props.load(fis);
    }

    assertThat(props.getProperty("original"), is(equalTo("originalValue")));
  }

  @Test
  public void testDeleteOperationCommit() throws Exception {
    // Create initial properties file
    Properties initialProps = new Properties();
    initialProps.setProperty("key", "value");
    try (FileOutputStream fos = new FileOutputStream(testPropertiesPath.toFile())) {
      initialProps.store(fos, null);
    }

    assertThat(testPropertiesPath.toFile().exists(), is(true));

    Operation<Void> deleteOp = actions.delete(testPropertiesPath);
    Result<Void> result = deleteOp.commit();

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetProperties() throws Exception {
    // Create properties file
    Properties initialProps = new Properties();
    initialProps.setProperty("key1", "value1");
    initialProps.setProperty("key2", "value2");
    try (FileOutputStream fos = new FileOutputStream(testPropertiesPath.toFile())) {
      initialProps.store(fos, null);
    }

    Map<String, String> props = actions.getProperties(testPropertiesPath);

    assertThat(props, hasEntry("key1", "value1"));
    assertThat(props, hasEntry("key2", "value2"));
  }

  @Test
  public void testCreateWithNullPath() {
    Map<String, String> configs = new HashMap<>();
    configs.put("key", "value");
    // Implementation throws IllegalArgumentException for null path validation
    assertThrows(IllegalArgumentException.class, () -> actions.create(null, configs));
  }

  @Test
  public void testCreateWithEmptyConfigs() {
    // Implementation throws IllegalArgumentException for empty config validation
    assertThrows(
        IllegalArgumentException.class, () -> actions.create(testPropertiesPath, new HashMap<>()));
  }

  @Test
  public void testCreateWithNullConfigs() {
    // Implementation throws IllegalArgumentException for null config validation
    assertThrows(IllegalArgumentException.class, () -> actions.create(testPropertiesPath, null));
  }

  @Test
  public void testUpdateWithNullPath() {
    Map<String, String> configs = new HashMap<>();
    configs.put("key", "value");
    // Implementation throws IllegalArgumentException for null path validation
    assertThrows(IllegalArgumentException.class, () -> actions.update(null, configs, true));
  }

  @Test
  public void testUpdateWithNullConfigs() {
    // Implementation throws IllegalArgumentException for null config validation
    assertThrows(
        IllegalArgumentException.class, () -> actions.update(testPropertiesPath, null, true));
  }

  @Test
  public void testDeleteWithNullPath() {
    // Implementation throws IllegalArgumentException for null path validation
    assertThrows(IllegalArgumentException.class, () -> actions.delete(null));
  }

  @Test
  public void testGetPropertiesWithNullPath() {
    // Implementation throws IllegalArgumentException for null path validation
    assertThrows(IllegalArgumentException.class, () -> actions.getProperties(null));
  }

  @Test
  public void testUpdateWithNonExistentFile() {
    // Path must be in an allowed subdirectory (system/) to avoid illegal path rejection
    Path nonExistentPath =
        Paths.get(tempFolder.getRoot().getAbsolutePath(), "system", "nonexistent.properties");

    Map<String, String> configs = new HashMap<>();
    configs.put("key", "value");

    // Update with non-existent file throws ConfiguratorException
    assertThrows(ConfiguratorException.class, () -> actions.update(nonExistentPath, configs, true));
  }

  @Test
  public void testCreateWithSpecialCharactersInValues() throws Exception {
    Map<String, String> configs = new HashMap<>();
    configs.put("key1", "value with spaces");
    configs.put("key2", "value=with=equals");
    configs.put("key3", "value:with:colons");

    Operation<Void> createOp = actions.create(testPropertiesPath, configs);
    createOp.commit();

    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(testPropertiesPath.toFile())) {
      props.load(fis);
    }

    assertThat(props.getProperty("key1"), is(equalTo("value with spaces")));
    assertThat(props.getProperty("key2"), is(equalTo("value=with=equals")));
    assertThat(props.getProperty("key3"), is(equalTo("value:with:colons")));
  }

  @Test
  public void testUpdateWithEmptyValue() throws Exception {
    Map<String, String> configs = new HashMap<>();
    configs.put("emptyKey", "");

    Operation<Void> createOp = actions.create(testPropertiesPath, configs);
    createOp.commit();

    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(testPropertiesPath.toFile())) {
      props.load(fis);
    }

    assertThat(props.containsKey("emptyKey"), is(true));
    assertThat(props.getProperty("emptyKey"), is(equalTo("")));
  }
}
