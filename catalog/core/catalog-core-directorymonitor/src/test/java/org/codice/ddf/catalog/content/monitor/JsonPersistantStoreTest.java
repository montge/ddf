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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;

@EnableRuleMigrationSupport
public class JsonPersistantStoreTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private JsonPersistantStore store;
  private String originalDataPath;

  @BeforeEach
  public void setUp() throws IOException {
    originalDataPath = System.getProperty("ddf.home");
    System.setProperty("ddf.home", temporaryFolder.getRoot().getAbsolutePath());
    store = new JsonPersistantStore("testStore");
  }

  @AfterEach
  public void tearDown() {
    if (originalDataPath != null) {
      System.setProperty("ddf.home", originalDataPath);
    } else {
      System.clearProperty("ddf.home");
    }
  }

  @Test
  public void testStoreAndLoadAsyncFileEntry() throws IOException {
    File testFile = temporaryFolder.newFile("test.txt");
    AsyncFileEntry entry = new AsyncFileEntry(testFile);

    store.store("testKey", entry);

    AsyncFileEntry loaded = store.load("testKey", AsyncFileEntry.class);

    assertThat(loaded, notNullValue());
    assertThat(loaded.getName(), is(entry.getName()));
  }

  @Test
  public void testStoreAndLoadString() {
    String testValue = "test value";

    store.store("stringKey", testValue);

    String loaded = store.load("stringKey", String.class);

    assertThat(loaded, is(testValue));
  }

  @Test
  public void testLoadNonExistentKey() {
    AsyncFileEntry loaded = store.load("nonexistent", AsyncFileEntry.class);

    assertThat(loaded, nullValue());
  }

  @Test
  public void testStoreOverwritesExistingValue() throws IOException {
    File testFile1 = temporaryFolder.newFile("test1.txt");
    File testFile2 = temporaryFolder.newFile("test2.txt");

    AsyncFileEntry entry1 = new AsyncFileEntry(testFile1);
    AsyncFileEntry entry2 = new AsyncFileEntry(testFile2);

    store.store("key", entry1);
    store.store("key", entry2);

    AsyncFileEntry loaded = store.load("key", AsyncFileEntry.class);

    assertThat(loaded, notNullValue());
    assertThat(loaded.getName(), is(entry2.getName()));
  }

  @Test
  public void testStoreMultipleKeys() throws IOException {
    File testFile1 = temporaryFolder.newFile("test1.txt");
    File testFile2 = temporaryFolder.newFile("test2.txt");

    AsyncFileEntry entry1 = new AsyncFileEntry(testFile1);
    AsyncFileEntry entry2 = new AsyncFileEntry(testFile2);

    store.store("key1", entry1);
    store.store("key2", entry2);

    AsyncFileEntry loaded1 = store.load("key1", AsyncFileEntry.class);
    AsyncFileEntry loaded2 = store.load("key2", AsyncFileEntry.class);

    assertThat(loaded1.getName(), is(entry1.getName()));
    assertThat(loaded2.getName(), is(entry2.getName()));
  }

  @Test
  public void testDefaultConstructor() {
    JsonPersistantStore defaultStore = new JsonPersistantStore();
    assertThat(defaultStore, notNullValue());

    defaultStore.store("test", "value");
    String loaded = defaultStore.load("test", String.class);
    assertThat(loaded, is("value"));
  }

  @Test
  public void testStoreComplexObject() throws IOException {
    File rootDir = temporaryFolder.newFolder("root");
    File childFile = new File(rootDir, "child.txt");
    childFile.createNewFile();

    AsyncFileEntry parent = new AsyncFileEntry(rootDir);
    AsyncFileEntry child = new AsyncFileEntry(parent, childFile);
    parent.addChild(child);

    store.store("complex", parent);

    AsyncFileEntry loaded = store.load("complex", AsyncFileEntry.class);

    assertThat(loaded, notNullValue());
    assertThat(loaded.getName(), is(parent.getName()));
    assertThat(loaded.getChildren().size(), is(1));
  }

  @Test
  public void testStoreWithSpecialCharactersInKey() {
    String specialKey = "key/with:special*chars?";
    String value = "test value";

    store.store(specialKey, value);

    String loaded = store.load(specialKey, String.class);

    assertThat(loaded, is(value));
  }

  @Test
  public void testStoreNullValue() {
    store.store("nullKey", null);

    Object loaded = store.load("nullKey", Object.class);

    assertThat(loaded, nullValue());
  }

  @Test
  public void testLoadWithWrongClass() throws IOException {
    File testFile = temporaryFolder.newFile("test.txt");
    AsyncFileEntry entry = new AsyncFileEntry(testFile);

    store.store("key", entry);

    // Try to load an object that was stored as a complex AsyncFileEntry as a plain String.
    // The deserialization mismatch is swallowed (JsonSyntaxException) and load returns null
    // rather than throwing.
    String loaded = store.load("key", String.class);

    assertThat(loaded, nullValue());
  }

  @Test
  public void testStoreCreatesDirectory() {
    // Clear any existing data directory
    File dataDir = new File(temporaryFolder.getRoot(), "data");
    if (dataDir.exists()) {
      dataDir.delete();
    }

    JsonPersistantStore newStore = new JsonPersistantStore("newStore");
    newStore.store("key", "value");

    // Verify directory was created
    File storeDir = new File(dataDir, "newStore");
    assertThat(storeDir.exists(), is(true));
    assertThat(storeDir.isDirectory(), is(true));
  }

  @Test
  public void testMultipleStoresWithDifferentNames() {
    JsonPersistantStore store1 = new JsonPersistantStore("store1");
    JsonPersistantStore store2 = new JsonPersistantStore("store2");

    store1.store("key", "value1");
    store2.store("key", "value2");

    String loaded1 = store1.load("key", String.class);
    String loaded2 = store2.load("key", String.class);

    assertThat(loaded1, is("value1"));
    assertThat(loaded2, is("value2"));
  }
}
