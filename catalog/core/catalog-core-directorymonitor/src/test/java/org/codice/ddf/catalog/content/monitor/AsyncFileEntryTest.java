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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;

@EnableRuleMigrationSupport
public class AsyncFileEntryTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private File testFile;
  private File testDir;

  @BeforeEach
  public void setUp() throws IOException {
    testFile = temporaryFolder.newFile("test.txt");
    testDir = temporaryFolder.newFolder("testDir");
    Files.write(testFile.toPath(), "Test content".getBytes());
  }

  @Test
  public void testCreateFileEntry() {
    AsyncFileEntry entry = new AsyncFileEntry(testFile);

    assertThat(entry.getName(), is(testFile.getName()));
    assertThat(entry.getFile(), is(testFile));
    assertThat(entry.isDirectory(), is(false));
    assertThat(entry.getParent().isPresent(), is(false));
  }

  @Test
  public void testCreateDirectoryEntry() {
    AsyncFileEntry entry = new AsyncFileEntry(testDir);

    assertThat(entry.getName(), is(testDir.getName()));
    assertThat(entry.getFile(), is(testDir));
    assertThat(entry.isDirectory(), is(true));
  }

  @Test
  public void testCreateEntryWithParent() {
    AsyncFileEntry parentEntry = new AsyncFileEntry(testDir);
    AsyncFileEntry childEntry = new AsyncFileEntry(parentEntry, testFile);

    assertThat(childEntry.getParent().isPresent(), is(true));
    assertThat(childEntry.getParent().get(), is(parentEntry));
  }

  @Test
  public void testHasChangedAfterFileModification() throws Exception {
    AsyncFileEntry entry = new AsyncFileEntry(testFile);
    assertFalse(entry.hasChanged());

    Thread.sleep(10);
    Files.write(testFile.toPath(), "Modified content".getBytes());

    assertTrue(entry.hasChanged());
  }

  @Test
  public void testHasChangedAfterFileDeletion() throws Exception {
    AsyncFileEntry entry = new AsyncFileEntry(testFile);
    assertFalse(entry.hasChanged());

    testFile.delete();

    assertTrue(entry.hasChanged());
  }

  @Test
  public void testCommitUpdatesSnapshot() throws Exception {
    AsyncFileEntry entry = new AsyncFileEntry(testFile);
    assertFalse(entry.hasChanged());

    Thread.sleep(10);
    Files.write(testFile.toPath(), "Modified content".getBytes());
    assertTrue(entry.hasChanged());

    entry.commit();
    assertFalse(entry.hasChanged());
  }

  @Test
  public void testAddChild() throws IOException {
    AsyncFileEntry parent = new AsyncFileEntry(testDir);
    File childFile = new File(testDir, "child.txt");
    childFile.createNewFile();
    AsyncFileEntry child = new AsyncFileEntry(parent, childFile);

    parent.addChild(child);

    assertThat(parent.getChildren(), hasSize(1));
    assertThat(parent.getChildren().get(0), is(child));
    assertTrue(parent.hasChildren());
  }

  @Test
  public void testRemoveChild() throws IOException {
    AsyncFileEntry parent = new AsyncFileEntry(testDir);
    File childFile = new File(testDir, "child.txt");
    childFile.createNewFile();
    AsyncFileEntry child = new AsyncFileEntry(parent, childFile);

    parent.addChild(child);
    assertThat(parent.getChildren(), hasSize(1));

    parent.removeChild(child);
    assertThat(parent.getChildren(), hasSize(0));
    assertFalse(parent.hasChildren());
  }

  @Test
  public void testGetChildren() throws IOException {
    AsyncFileEntry parent = new AsyncFileEntry(testDir);
    File child1 = new File(testDir, "child1.txt");
    File child2 = new File(testDir, "child2.txt");
    child1.createNewFile();
    child2.createNewFile();

    AsyncFileEntry entry1 = new AsyncFileEntry(parent, child1);
    AsyncFileEntry entry2 = new AsyncFileEntry(parent, child2);

    parent.addChild(entry1);
    parent.addChild(entry2);

    assertThat(parent.getChildren(), hasSize(2));
  }

  @Test
  public void testCompareTo() throws IOException {
    File file1 = new File(testDir, "a.txt");
    File file2 = new File(testDir, "b.txt");
    file1.createNewFile();
    file2.createNewFile();

    AsyncFileEntry entry1 = new AsyncFileEntry(file1);
    AsyncFileEntry entry2 = new AsyncFileEntry(file2);
    AsyncFileEntry entry1Copy = new AsyncFileEntry(file1);

    assertThat(entry1.compareTo(entry2), lessThan(0));
    assertThat(entry2.compareTo(entry1), greaterThan(0));
    assertThat(entry1.compareTo(entry1Copy), is(0));
  }

  @Test
  public void testCompareToFile() throws IOException {
    File file1 = new File(testDir, "a.txt");
    File file2 = new File(testDir, "b.txt");
    file1.createNewFile();
    file2.createNewFile();

    AsyncFileEntry entry1 = new AsyncFileEntry(file1);

    assertThat(entry1.compareToFile(file2), lessThan(0));
    assertThat(entry1.compareToFile(file1), is(0));
  }

  @Test
  public void testEquals() throws IOException {
    AsyncFileEntry entry1 = new AsyncFileEntry(testFile);
    AsyncFileEntry entry2 = new AsyncFileEntry(testFile);
    File differentFile = temporaryFolder.newFile("different.txt");
    AsyncFileEntry entry3 = new AsyncFileEntry(differentFile);

    assertThat(entry1.equals(entry2), is(true));
    assertThat(entry1.equals(entry3), is(false));
    assertThat(entry1.equals(testFile), is(false));
    assertThat(entry1.equals(null), is(false));
  }

  @Test
  public void testHashCode() {
    AsyncFileEntry entry1 = new AsyncFileEntry(testFile);
    AsyncFileEntry entry2 = new AsyncFileEntry(testFile);

    assertThat(entry1.hashCode(), is(entry2.hashCode()));
  }

  @Test
  public void testCheckNetwork() throws IOException {
    AsyncFileEntry rootEntry = new AsyncFileEntry(testDir);
    File subDir = new File(testDir, "subdir");
    subDir.mkdir();
    AsyncFileEntry childEntry = new AsyncFileEntry(rootEntry, subDir);

    assertTrue(rootEntry.checkNetwork());
    assertTrue(childEntry.checkNetwork());
  }

  @Test
  public void testCheckNetworkWhenParentDeleted() throws IOException {
    AsyncFileEntry rootEntry = new AsyncFileEntry(testDir);
    File subDir = new File(testDir, "subdir");
    subDir.mkdir();
    AsyncFileEntry childEntry = new AsyncFileEntry(rootEntry, subDir);
    rootEntry.addChild(childEntry);

    assertTrue(childEntry.checkNetwork());

    testDir.delete();

    assertFalse(childEntry.checkNetwork());
  }

  @Test
  public void testDestroy() throws IOException {
    AsyncFileEntry parent = new AsyncFileEntry(testDir);
    File childFile = new File(testDir, "child.txt");
    childFile.createNewFile();
    AsyncFileEntry child = new AsyncFileEntry(parent, childFile);

    parent.addChild(child);
    assertThat(parent.getChildren(), hasSize(1));

    parent.destroy();
    assertThat(parent.getChildren(), hasSize(0));
  }

  @Test
  public void testInitialize() throws IOException {
    AsyncFileEntry parent = new AsyncFileEntry(testDir);
    File childFile = new File(testDir, "child.txt");
    childFile.createNewFile();
    AsyncFileEntry child = new AsyncFileEntry(parent, childFile);
    parent.addChild(child);

    // Simulate deserialization by clearing parent
    AsyncFileEntry grandchild = new AsyncFileEntry(null, new File(childFile, "grandchild.txt"));
    child.addChild(grandchild);

    parent.initialize();

    assertThat(child.getParent().isPresent(), is(true));
  }

  @Test
  public void testInitializeWithNullFile() {
    AsyncFileEntry entry = new AsyncFileEntry(testFile);
    AsyncFileEntry childWithNullFile = new AsyncFileEntry(entry, null);
    entry.addChild(childWithNullFile);

    assertThrows(IllegalStateException.class, () -> entry.initialize());
  }

  @Test
  public void testHasChangedForNonExistentFile() {
    File nonExistent = new File(testDir, "nonexistent.txt");
    AsyncFileEntry entry = new AsyncFileEntry(nonExistent);

    assertFalse(entry.hasChanged());
  }

  @Test
  public void testHasChangedForNewlyCreatedFile() throws IOException {
    File newFile = new File(testDir, "newfile.txt");
    AsyncFileEntry entry = new AsyncFileEntry(newFile);

    assertFalse(entry.hasChanged());

    newFile.createNewFile();
    Files.write(newFile.toPath(), "content".getBytes());

    assertTrue(entry.hasChanged());
  }

  @Test
  public void testDirectoryToFileTransition() throws IOException {
    File path = new File(testDir, "path");
    path.mkdir();

    AsyncFileEntry entry = new AsyncFileEntry(path);
    assertTrue(entry.isDirectory());
    assertFalse(entry.hasChanged());

    path.delete();
    path.createNewFile();

    assertTrue(entry.hasChanged());
  }

  @Test
  public void testFileToDirectoryTransition() throws IOException {
    File path = new File(testDir, "path");
    path.createNewFile();

    AsyncFileEntry entry = new AsyncFileEntry(path);
    assertFalse(entry.isDirectory());
    assertFalse(entry.hasChanged());

    path.delete();
    path.mkdir();

    assertTrue(entry.hasChanged());
  }
}
