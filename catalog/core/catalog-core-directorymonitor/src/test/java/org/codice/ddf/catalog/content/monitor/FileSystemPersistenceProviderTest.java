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
package org.codice.ddf.catalog.content.monitor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;

@EnableRuleMigrationSupport
public class FileSystemPersistenceProviderTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private FileSystemPersistenceProvider persistenceProvider;

  private String testMapName = "test-map";

  @BeforeEach
  public void setUp() throws IOException {
    persistenceProvider =
        new TestFileSystemPersistenceProvider(
            testMapName, temporaryFolder.getRoot().getAbsolutePath());
  }

  @Test
  public void testDefaultConstructor() {
    FileSystemPersistenceProvider provider = new FileSystemPersistenceProvider();
    assertThat(provider, is(notNullValue()));
  }

  @Test
  public void testStoreAndLoadString() {
    String key = "test-key";
    String value = "test-value";

    persistenceProvider.store(key, value);

    Object loadedValue = persistenceProvider.loadFromPersistence(key);
    assertThat(loadedValue, is(notNullValue()));
    assertThat(loadedValue, is(equalTo(value)));
  }

  @Test
  public void testStoreAndLoadSerializable() {
    String key = "serializable-key";
    TestSerializable value = new TestSerializable("test-data", 42);

    persistenceProvider.store(key, value);

    Object loadedValue = persistenceProvider.loadFromPersistence(key);
    assertThat(loadedValue, is(notNullValue()));
    assertThat(loadedValue, is(equalTo(value)));
  }

  @Test
  public void testStoreOverwritesExistingValue() {
    String key = "overwrite-key";
    String originalValue = "original";
    String newValue = "new";

    persistenceProvider.store(key, originalValue);
    persistenceProvider.store(key, newValue);

    Object loadedValue = persistenceProvider.loadFromPersistence(key);
    assertThat(loadedValue, is(equalTo(newValue)));
  }

  @Test
  public void testStoreAll() {
    Map<String, Object> keyValueMap = new HashMap<>();
    keyValueMap.put("key1", "value1");
    keyValueMap.put("key2", "value2");
    keyValueMap.put("key3", 123);

    persistenceProvider.storeAll(keyValueMap);

    assertThat(persistenceProvider.loadFromPersistence("key1"), is(equalTo("value1")));
    assertThat(persistenceProvider.loadFromPersistence("key2"), is(equalTo("value2")));
    assertThat(persistenceProvider.loadFromPersistence("key3"), is(equalTo(123)));
  }

  @Test
  public void testStoreAllWithEmptyMap() {
    persistenceProvider.storeAll(Collections.emptyMap());
    Set<String> keys = persistenceProvider.loadAllKeys();
    assertThat(keys, is(empty()));
  }

  @Test
  public void testDelete() {
    String key = "delete-key";
    String value = "delete-value";

    persistenceProvider.store(key, value);
    assertThat(persistenceProvider.loadFromPersistence(key), is(notNullValue()));

    persistenceProvider.delete(key);
    assertThat(persistenceProvider.loadFromPersistence(key), is(nullValue()));
  }

  @Test
  public void testDeleteNonExistentKey() {
    // Deleting a key that was never stored should be a safe no-op (no exception)
    assertDoesNotThrow(() -> persistenceProvider.delete("non-existent-key"));
    assertThat(persistenceProvider.loadFromPersistence("non-existent-key"), is(nullValue()));
  }

  @Test
  public void testDeleteAll() {
    persistenceProvider.store("key1", "value1");
    persistenceProvider.store("key2", "value2");
    persistenceProvider.store("key3", "value3");

    persistenceProvider.deleteAll(Arrays.asList("key1", "key3"));

    assertThat(persistenceProvider.loadFromPersistence("key1"), is(nullValue()));
    assertThat(persistenceProvider.loadFromPersistence("key2"), is(notNullValue()));
    assertThat(persistenceProvider.loadFromPersistence("key3"), is(nullValue()));
  }

  @Test
  public void testDeleteAllWithEmptyCollection() {
    persistenceProvider.store("key1", "value1");
    persistenceProvider.deleteAll(Collections.emptyList());
    assertThat(persistenceProvider.loadFromPersistence("key1"), is(notNullValue()));
  }

  @Test
  public void testLoad() {
    // load() method always returns null by design (not implemented)
    String key = "load-key";
    persistenceProvider.store(key, "value");

    Object result = persistenceProvider.load(key);
    assertThat(result, is(nullValue()));
  }

  @Test
  public void testLoadFromPersistenceWithNonExistentKey() {
    Object result = persistenceProvider.loadFromPersistence("non-existent");
    assertThat(result, is(nullValue()));
  }

  @Test
  public void testLoadAll() {
    persistenceProvider.store("key1", "value1");
    persistenceProvider.store("key2", "value2");
    persistenceProvider.store("key3", "value3");

    Map<String, Object> loadedValues =
        persistenceProvider.loadAll(Arrays.asList("key1", "key2", "non-existent"));

    assertThat(loadedValues.size(), is(2));
    assertThat(loadedValues.get("key1"), is(equalTo("value1")));
    assertThat(loadedValues.get("key2"), is(equalTo("value2")));
    assertThat(loadedValues.containsKey("non-existent"), is(false));
  }

  @Test
  public void testLoadAllWithEmptyCollection() {
    Map<String, Object> result = persistenceProvider.loadAll(Collections.emptyList());
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  public void testLoadAllKeys() {
    persistenceProvider.store("key1", "value1");
    persistenceProvider.store("key2", "value2");
    persistenceProvider.store("key3", "value3");

    Set<String> keys = persistenceProvider.loadAllKeys();

    assertThat(keys.size(), is(3));
    assertThat(keys, hasItems("key1", "key2", "key3"));
  }

  @Test
  public void testLoadAllKeysWhenEmpty() {
    Set<String> keys = persistenceProvider.loadAllKeys();
    assertThat(keys, is(notNullValue()));
    assertThat(keys.isEmpty(), is(true));
  }

  @Test
  public void testLoadAllKeysWithNonExistentDirectory() throws IOException {
    // Use a path that doesn't exist
    String nonExistentPath = temporaryFolder.getRoot().getAbsolutePath() + "/non-existent-dir";
    FileSystemPersistenceProvider provider =
        new TestFileSystemPersistenceProviderNoCreate("non-existent", nonExistentPath);

    Set<String> keys = provider.loadAllKeys();
    assertThat(keys, is(notNullValue()));
    assertThat(keys.isEmpty(), is(true));
  }

  @Test
  public void testGetPersistencePath() {
    assertThat(persistenceProvider.getPersistencePath(), is(notNullValue()));
  }

  @Test
  public void testGetMapStorePath() {
    String path = persistenceProvider.getMapStorePath();
    assertThat(path, is(notNullValue()));
    assertThat(path.contains(testMapName), is(true));
  }

  @Test
  public void testToString() {
    String result = persistenceProvider.toString();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(equalTo(persistenceProvider.getMapStorePath())));
  }

  @Test
  public void testStoreWithSpecialCharactersInKey() {
    String key = "key-with-special_chars.test";
    String value = "special-value";

    persistenceProvider.store(key, value);
    Object loadedValue = persistenceProvider.loadFromPersistence(key);

    assertThat(loadedValue, is(equalTo(value)));
  }

  @Test
  public void testMultipleStoresAndDeletes() {
    // Store multiple values
    for (int i = 0; i < 10; i++) {
      persistenceProvider.store("key" + i, "value" + i);
    }

    // Verify all were stored
    Set<String> keys = persistenceProvider.loadAllKeys();
    assertThat(keys.size(), is(10));

    // Delete half of them
    for (int i = 0; i < 5; i++) {
      persistenceProvider.delete("key" + i);
    }

    // Verify correct keys remain
    keys = persistenceProvider.loadAllKeys();
    assertThat(keys.size(), is(5));
    for (int i = 5; i < 10; i++) {
      assertThat(keys.contains("key" + i), is(true));
    }
  }

  @Test
  public void testStoreNullValue() {
    String key = "null-key";
    persistenceProvider.store(key, null);
    Object loadedValue = persistenceProvider.loadFromPersistence(key);
    assertThat(loadedValue, is(nullValue()));
  }

  @Test
  public void testConcurrentOperations() {
    // Test that basic concurrent-like operations don't corrupt data
    persistenceProvider.store("concurrent1", "value1");
    persistenceProvider.store("concurrent2", "value2");

    Object value1 = persistenceProvider.loadFromPersistence("concurrent1");
    Object value2 = persistenceProvider.loadFromPersistence("concurrent2");

    assertThat(value1, is(equalTo("value1")));
    assertThat(value2, is(equalTo("value2")));

    persistenceProvider.delete("concurrent1");
    assertThat(persistenceProvider.loadFromPersistence("concurrent1"), is(nullValue()));
    assertThat(persistenceProvider.loadFromPersistence("concurrent2"), is(equalTo("value2")));
  }

  /**
   * Test implementation that uses a temporary folder instead of the DDF_HOME data directory for
   * testing purposes
   */
  private static class TestFileSystemPersistenceProvider extends FileSystemPersistenceProvider {
    private String persistencePath;

    public TestFileSystemPersistenceProvider(String mapName, String persistencePath) {
      // Use the no-arg constructor to avoid calling getPersistencePath() before field is set
      super();
      this.persistencePath = persistencePath;
      // Manually set the mapName after construction
      setMapName(mapName);
      // Create the directory if needed
      File dir = new File(getPersistencePath());
      if (!dir.exists()) {
        dir.mkdir();
      }
    }

    @Override
    String getPersistencePath() {
      return persistencePath;
    }

    private void setMapName(String name) {
      try {
        java.lang.reflect.Field field =
            FileSystemPersistenceProvider.class.getDeclaredField("mapName");
        field.setAccessible(true);
        field.set(this, name);
      } catch (Exception e) {
        throw new RuntimeException("Failed to set mapName", e);
      }
    }
  }

  /**
   * Test implementation that does not create the directory - for testing non-existent directory
   * scenarios
   */
  private static class TestFileSystemPersistenceProviderNoCreate
      extends FileSystemPersistenceProvider {
    private String persistencePath;

    public TestFileSystemPersistenceProviderNoCreate(String mapName, String persistencePath) {
      super();
      this.persistencePath = persistencePath;
      try {
        java.lang.reflect.Field field =
            FileSystemPersistenceProvider.class.getDeclaredField("mapName");
        field.setAccessible(true);
        field.set(this, mapName);
      } catch (Exception e) {
        throw new RuntimeException("Failed to set mapName", e);
      }
    }

    @Override
    String getPersistencePath() {
      return persistencePath;
    }
  }

  /** Test serializable class for testing object persistence */
  private static class TestSerializable implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String stringValue;
    private final int intValue;

    public TestSerializable(String stringValue, int intValue) {
      this.stringValue = stringValue;
      this.intValue = intValue;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof TestSerializable)) {
        return false;
      }
      TestSerializable that = (TestSerializable) obj;
      return intValue == that.intValue
          && (stringValue != null
              ? stringValue.equals(that.stringValue)
              : that.stringValue == null);
    }

    @Override
    public int hashCode() {
      int result = stringValue != null ? stringValue.hashCode() : 0;
      result = 31 * result + intValue;
      return result;
    }
  }
}
