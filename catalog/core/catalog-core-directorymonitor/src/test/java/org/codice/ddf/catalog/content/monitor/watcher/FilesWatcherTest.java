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
package org.codice.ddf.catalog.content.monitor.watcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;

@EnableRuleMigrationSupport
public class FilesWatcherTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private FilesWatcher watcher;
  private static final long TEST_POLL_TIME = 1; // 1 second for faster tests

  @BeforeEach
  public void setUp() {
    watcher = new FilesWatcher(TEST_POLL_TIME);
  }

  @AfterEach
  public void tearDown() {
    if (watcher != null) {
      watcher.destroy();
    }
  }

  @Test
  public void testWatchFileWithCallback() throws Exception {
    File testFile = temporaryFolder.newFile("test.txt");
    CountDownLatch latch = new CountDownLatch(1);
    AtomicInteger callbackCount = new AtomicInteger(0);

    FileWatcher fileWatcher =
        new FileWatcher(
            testFile,
            file -> {
              callbackCount.incrementAndGet();
              latch.countDown();
            });

    watcher.watch(fileWatcher);

    // Wait for callback to be invoked
    latch.await(5, TimeUnit.SECONDS);

    assertThat(callbackCount.get(), is(1));
  }

  @Test
  public void testMultipleWatchers() throws Exception {
    File file1 = temporaryFolder.newFile("file1.txt");
    File file2 = temporaryFolder.newFile("file2.txt");

    CountDownLatch latch = new CountDownLatch(2);
    AtomicInteger count1 = new AtomicInteger(0);
    AtomicInteger count2 = new AtomicInteger(0);

    FileWatcher watcher1 =
        new FileWatcher(
            file1,
            file -> {
              count1.incrementAndGet();
              latch.countDown();
            });

    FileWatcher watcher2 =
        new FileWatcher(
            file2,
            file -> {
              count2.incrementAndGet();
              latch.countDown();
            });

    watcher.watch(watcher1);
    watcher.watch(watcher2);

    latch.await(5, TimeUnit.SECONDS);

    assertThat(count1.get(), is(1));
    assertThat(count2.get(), is(1));
  }

  @Test
  public void testWatcherRemovedAfterCallback() throws Exception {
    File testFile = temporaryFolder.newFile("test.txt");
    CountDownLatch latch = new CountDownLatch(1);
    AtomicInteger callbackCount = new AtomicInteger(0);

    FileWatcher fileWatcher =
        new FileWatcher(
            testFile,
            file -> {
              callbackCount.incrementAndGet();
              latch.countDown();
            });

    watcher.watch(fileWatcher);
    latch.await(5, TimeUnit.SECONDS);

    // Wait for another poll cycle
    Thread.sleep(TEST_POLL_TIME * 1000 + 500);

    // Callback should only be called once since watcher is removed
    assertThat(callbackCount.get(), is(1));
  }

  @Test
  public void testDefaultPollTime() {
    FilesWatcher defaultWatcher = new FilesWatcher();
    assertThat(defaultWatcher, is(org.hamcrest.Matchers.notNullValue()));
    defaultWatcher.destroy();
  }

  @Test
  public void testDestroyWithNoWatchers() {
    FilesWatcher emptyWatcher = new FilesWatcher(TEST_POLL_TIME);
    // Destroying a watcher that is tracking no files must complete without throwing
    assertDoesNotThrow(emptyWatcher::destroy);
  }

  @Test
  public void testDestroyWithActiveWatchers() throws Exception {
    File testFile = temporaryFolder.newFile("test.txt");
    FileWatcher fileWatcher = new FileWatcher(testFile, file -> {});

    watcher.watch(fileWatcher);

    // Destroying a watcher that still has an active file watch must complete without throwing
    assertDoesNotThrow(watcher::destroy);
  }

  @Test
  public void testSystemPropertyPollTime() {
    String originalProperty = System.getProperty("org.codice.ddf.cdm.fileCheckPeriod");
    try {
      System.setProperty("org.codice.ddf.cdm.fileCheckPeriod", "10");
      FilesWatcher customWatcher = new FilesWatcher();
      assertThat(customWatcher, is(org.hamcrest.Matchers.notNullValue()));
      customWatcher.destroy();
    } finally {
      if (originalProperty != null) {
        System.setProperty("org.codice.ddf.cdm.fileCheckPeriod", originalProperty);
      } else {
        System.clearProperty("org.codice.ddf.cdm.fileCheckPeriod");
      }
    }
  }

  @Test
  public void testInvalidSystemPropertyPollTime() {
    String originalProperty = System.getProperty("org.codice.ddf.cdm.fileCheckPeriod");
    try {
      System.setProperty("org.codice.ddf.cdm.fileCheckPeriod", "invalid");
      FilesWatcher customWatcher = new FilesWatcher();
      // Should fallback to default poll time
      assertThat(customWatcher, is(org.hamcrest.Matchers.notNullValue()));
      customWatcher.destroy();
    } finally {
      if (originalProperty != null) {
        System.setProperty("org.codice.ddf.cdm.fileCheckPeriod", originalProperty);
      } else {
        System.clearProperty("org.codice.ddf.cdm.fileCheckPeriod");
      }
    }
  }

  @Test
  public void testWatchSameFileTwice() throws Exception {
    File testFile = temporaryFolder.newFile("test.txt");
    AtomicInteger count = new AtomicInteger(0);
    CountDownLatch latch = new CountDownLatch(1);

    FileWatcher watcher1 =
        new FileWatcher(
            testFile,
            file -> {
              count.incrementAndGet();
              latch.countDown();
            });
    FileWatcher watcher2 =
        new FileWatcher(
            testFile,
            file -> {
              count.incrementAndGet();
              latch.countDown();
            });

    watcher.watch(watcher1);
    watcher.watch(watcher2);

    // Wait for callback (file needs 2 checks to stabilize)
    latch.await(5, TimeUnit.SECONDS);

    // Only the second watcher should remain (same file replaces first)
    assertThat(count.get(), is(1));
  }
}
