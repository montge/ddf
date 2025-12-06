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
package ddf.camel.component.catalog.content;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileSystemDataAccessObjectTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private FileSystemDataAccessObject fileSystemDataAccessObject;

  private File testDir;

  private static final String TEST_SUFFIX = ".ser";
  private static final String TEST_SUFFIX_REGEX = "\\.ser";

  @Before
  public void setUp() throws Exception {
    fileSystemDataAccessObject = new FileSystemDataAccessObject();
    testDir = temporaryFolder.newFolder("testStore");
  }

  @Test
  public void testFileSystemPersistenceProviderHelper() throws Exception {
    String testString = "test string";

    // store the test string object
    fileSystemDataAccessObject.store(testDir.getPath(), ".test", "/example", testString);

    // assert that a file was made to store the string object
    assertThat(Files.exists(Paths.get(testDir.getPath() + "/example.test")), is(true));

    // load the string object that was stored
    assertThat(
        fileSystemDataAccessObject
            .loadFromPersistence(testDir.getPath(), ".test", "/example")
            .equals(testString),
        is(true));

    // make a filename filter to find the .test file
    FilenameFilter filenameFilter = fileSystemDataAccessObject.getFilenameFilter(".test");

    // load stored keys
    Set<String> keys =
        fileSystemDataAccessObject.loadAllKeys(testDir.getPath(), ".test", filenameFilter);

    // assert that the file that was stored was in the key set
    assertThat(keys.contains("example"), is(true));

    // clear out the stored keys
    fileSystemDataAccessObject.clear(testDir.getPath(), filenameFilter);

    // assert that the key that was stored was cleared out
    assertThat(
        fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), ".test", "/example"),
        is(nullValue()));
  }

  @Test
  public void testStoreAndLoadString() {
    String key = "string-key";
    String value = "test-value";

    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, key, value);

    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), TEST_SUFFIX, key);
    assertThat(loaded, is(notNullValue()));
    assertThat(loaded, is(equalTo(value)));
  }

  @Test
  public void testStoreAndLoadSerializable() {
    String key = "serializable-key";
    TestSerializable value = new TestSerializable("data", 100);

    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, key, value);

    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), TEST_SUFFIX, key);
    assertThat(loaded, is(notNullValue()));
    assertThat(loaded, is(equalTo(value)));
  }

  @Test
  public void testStoreCreatesDirectory() {
    File newDir = new File(testDir, "newSubDir");
    assertThat(newDir.exists(), is(false));

    String storePath = newDir.getAbsolutePath() + File.separator;
    fileSystemDataAccessObject.store(storePath, TEST_SUFFIX, "key", "value");

    assertThat(newDir.exists(), is(true));
    assertThat(newDir.isDirectory(), is(true));
  }

  @Test
  public void testStoreOverwritesExistingValue() {
    String key = "overwrite-key";
    String original = "original-value";
    String updated = "updated-value";

    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, key, original);
    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, key, updated);

    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), TEST_SUFFIX, key);
    assertThat(loaded, is(equalTo(updated)));
  }

  @Test
  public void testLoadFromPersistenceWithNonExistentKey() {
    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(
            testDir.getPath(), TEST_SUFFIX, "non-existent");
    assertThat(loaded, is(nullValue()));
  }

  @Test
  public void testLoadFromPersistenceWithNonExistentFile() {
    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(
            testDir.getPath() + "/non-existent-dir", TEST_SUFFIX, "key");
    assertThat(loaded, is(nullValue()));
  }

  @Test
  public void testLoadAllKeys() {
    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, "key1", "value1");
    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, "key2", "value2");
    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, "key3", "value3");

    FilenameFilter filter = fileSystemDataAccessObject.getFilenameFilter(TEST_SUFFIX);
    Set<String> keys =
        fileSystemDataAccessObject.loadAllKeys(testDir.getPath(), TEST_SUFFIX_REGEX, filter);

    assertThat(keys, is(notNullValue()));
    assertThat(keys, hasSize(3));
    assertThat(keys.contains("key1"), is(true));
    assertThat(keys.contains("key2"), is(true));
    assertThat(keys.contains("key3"), is(true));
  }

  @Test
  public void testLoadAllKeysWithEmptyDirectory() {
    FilenameFilter filter = fileSystemDataAccessObject.getFilenameFilter(TEST_SUFFIX);
    Set<String> keys =
        fileSystemDataAccessObject.loadAllKeys(testDir.getPath(), TEST_SUFFIX_REGEX, filter);

    assertThat(keys, is(notNullValue()));
    assertThat(keys, is(empty()));
  }

  @Test
  public void testLoadAllKeysWithNonExistentDirectory() {
    String nonExistentPath = testDir.getPath() + "/non-existent-dir";
    FilenameFilter filter = fileSystemDataAccessObject.getFilenameFilter(TEST_SUFFIX);
    Set<String> keys =
        fileSystemDataAccessObject.loadAllKeys(nonExistentPath, TEST_SUFFIX_REGEX, filter);

    assertThat(keys, is(notNullValue()));
    assertThat(keys, is(empty()));
  }

  @Test
  public void testClear() {
    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, "key1", "value1");
    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, "key2", "value2");
    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, "key3", "value3");

    FilenameFilter filter = fileSystemDataAccessObject.getFilenameFilter(TEST_SUFFIX);
    fileSystemDataAccessObject.clear(testDir.getPath(), filter);

    Set<String> keys =
        fileSystemDataAccessObject.loadAllKeys(testDir.getPath(), TEST_SUFFIX_REGEX, filter);
    assertThat(keys, is(empty()));
  }

  @Test
  public void testClearWithNonExistentDirectory() {
    String nonExistentPath = testDir.getPath() + "/non-existent-dir";
    FilenameFilter filter = fileSystemDataAccessObject.getFilenameFilter(TEST_SUFFIX);

    // Should not throw exception
    fileSystemDataAccessObject.clear(nonExistentPath, filter);
  }

  @Test
  public void testGetFilenameFilter() {
    fileSystemDataAccessObject.store(testDir.getPath(), ".txt", "doc1", "text content");
    fileSystemDataAccessObject.store(testDir.getPath(), ".xml", "doc2", "xml content");
    fileSystemDataAccessObject.store(testDir.getPath(), ".txt", "doc3", "more text");

    FilenameFilter txtFilter = fileSystemDataAccessObject.getFilenameFilter(".txt");
    Set<String> txtKeys =
        fileSystemDataAccessObject.loadAllKeys(testDir.getPath(), "\\.txt", txtFilter);

    assertThat(txtKeys, hasSize(2));
    assertThat(txtKeys.contains("doc1"), is(true));
    assertThat(txtKeys.contains("doc3"), is(true));
  }

  @Test
  public void testGetFilenameFilterIsCaseInsensitive() {
    File testFile1 = new File(testDir, "test.TXT");
    File testFile2 = new File(testDir, "test2.txt");
    File testFile3 = new File(testDir, "test3.Txt");

    try {
      testFile1.createNewFile();
      testFile2.createNewFile();
      testFile3.createNewFile();

      FilenameFilter filter = fileSystemDataAccessObject.getFilenameFilter(".txt");

      assertThat(filter.accept(testDir, "test.TXT"), is(true));
      assertThat(filter.accept(testDir, "test2.txt"), is(true));
      assertThat(filter.accept(testDir, "test3.Txt"), is(true));
      assertThat(filter.accept(testDir, "test.xml"), is(false));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testStoreWithSpecialCharactersInKey() {
    String key = "key-with-special_chars.123";
    String value = "special-value";

    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, key, value);
    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), TEST_SUFFIX, key);

    assertThat(loaded, is(equalTo(value)));
  }

  @Test
  public void testStoreNullValue() {
    String key = "null-key";

    fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, key, null);
    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), TEST_SUFFIX, key);

    assertThat(loaded, is(nullValue()));
  }

  @Test
  public void testMultipleStoreAndLoad() {
    for (int i = 0; i < 10; i++) {
      fileSystemDataAccessObject.store(testDir.getPath(), TEST_SUFFIX, "key" + i, "value" + i);
    }

    for (int i = 0; i < 10; i++) {
      Object loaded =
          fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), TEST_SUFFIX, "key" + i);
      assertThat(loaded, is(equalTo("value" + i)));
    }
  }

  @Test
  public void testClearOnlyRemovesMatchingFiles() {
    fileSystemDataAccessObject.store(testDir.getPath(), ".ser", "key1", "value1");
    fileSystemDataAccessObject.store(testDir.getPath(), ".dat", "key2", "value2");

    FilenameFilter serFilter = fileSystemDataAccessObject.getFilenameFilter(".ser");
    fileSystemDataAccessObject.clear(testDir.getPath(), serFilter);

    assertThat(
        fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), ".ser", "key1"),
        is(nullValue()));
    assertThat(
        fileSystemDataAccessObject.loadFromPersistence(testDir.getPath(), ".dat", "key2"),
        is(notNullValue()));
  }

  @Test
  public void testStoreWithTrailingSlash() {
    String pathWithSlash = testDir.getPath() + File.separator;
    fileSystemDataAccessObject.store(pathWithSlash, TEST_SUFFIX, "key", "value");

    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(pathWithSlash, TEST_SUFFIX, "key");
    assertThat(loaded, is(equalTo("value")));
  }

  @Test
  public void testStoreWithoutTrailingSlash() {
    String pathWithoutSlash = testDir.getPath();
    fileSystemDataAccessObject.store(pathWithoutSlash, TEST_SUFFIX, "key", "value");

    Object loaded =
        fileSystemDataAccessObject.loadFromPersistence(pathWithoutSlash, TEST_SUFFIX, "key");
    assertThat(loaded, is(equalTo("value")));
  }

  /** Test serializable class for testing object persistence */
  private static class TestSerializable implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String data;
    private final int number;

    public TestSerializable(String data, int number) {
      this.data = data;
      this.number = number;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      TestSerializable that = (TestSerializable) obj;
      return number == that.number && (data != null ? data.equals(that.data) : that.data == null);
    }

    @Override
    public int hashCode() {
      int result = data != null ? data.hashCode() : 0;
      result = 31 * result + number;
      return result;
    }
  }
}
