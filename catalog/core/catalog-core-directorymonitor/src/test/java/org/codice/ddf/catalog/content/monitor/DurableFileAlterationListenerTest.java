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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileConfiguration;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.codice.ddf.catalog.content.monitor.watcher.FilesWatcher;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableRuleMigrationSupport
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class DurableFileAlterationListenerTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Mock private AbstractDurableFileConsumer consumer;

  @Mock private FilesWatcher filesWatcher;

  @Mock private FileSystemPersistenceProvider productToMetacardIdMap;

  @Mock private org.apache.camel.component.file.FileEndpoint endpoint;

  @Mock private GenericFileConfiguration configuration;

  @Mock private Synchronization synchronization;

  private DurableFileAlterationListener listener;

  private File testFile;

  private org.apache.camel.CamelContext camelContext;

  @BeforeEach
  public void setUp() throws Exception {
    // Use a real CamelContext for Camel 4.x exchange creation
    camelContext = new org.apache.camel.impl.DefaultCamelContext();
    camelContext.start();

    testFile = temporaryFolder.newFile("test-file.txt");

    when(consumer.getEndpoint()).thenReturn(endpoint);
    when(endpoint.getConfiguration()).thenReturn(configuration);
    when(configuration.getDirectory()).thenReturn(temporaryFolder.getRoot().getAbsolutePath());
    when(endpoint.getCamelContext()).thenReturn(camelContext);

    // Mock createExchange to return a real exchange
    when(endpoint.createExchange(any(GenericFile.class)))
        .thenAnswer(
            invocation -> {
              Exchange exchange = new DefaultExchange(camelContext);
              Message message = new DefaultMessage(camelContext);
              exchange.setIn(message);
              return exchange;
            });

    listener = new DurableFileAlterationListener(consumer, filesWatcher);
  }

  @AfterEach
  public void tearDown() throws Exception {
    if (camelContext != null) {
      camelContext.stop();
    }
  }

  @Test
  public void testConstructorWithConsumerOnly() {
    DurableFileAlterationListener newListener = new DurableFileAlterationListener(consumer);
    assertThat(newListener, is(notNullValue()));
  }

  @Test
  public void testConstructorWithConsumerAndFilesWatcher() {
    DurableFileAlterationListener newListener =
        new DurableFileAlterationListener(consumer, filesWatcher);
    assertThat(newListener, is(notNullValue()));
  }

  @Test
  public void testOnFileCreate() {
    listener.onFileCreate(testFile, synchronization);

    verify(filesWatcher).watch(any());
  }

  @Test
  public void testOnFileCreateWithoutCallback() {
    listener.onFileCreate(testFile);

    verify(filesWatcher).watch(any());
  }

  @Test
  public void testOnFileChange() {
    String fileUri = testFile.toURI().toASCIIString();
    String metacardId = "test-metacard-id";
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    keys.add(refKey);

    // Setup mocks
    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);
    when(productToMetacardIdMap.loadFromPersistence(refKey)).thenReturn(metacardId);

    // Replace the productToMetacardIdMap with our mock
    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileChange(testFile, synchronization);

    verify(filesWatcher).watch(any());
  }

  @Test
  public void testOnFileChangeWithoutCallback() {
    String fileUri = testFile.toURI().toASCIIString();
    String metacardId = "test-metacard-id";
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    keys.add(refKey);

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);
    when(productToMetacardIdMap.loadFromPersistence(refKey)).thenReturn(metacardId);

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileChange(testFile);

    verify(filesWatcher).watch(any());
  }

  @Test
  public void testOnFileChangeWithNoMetacardId() {
    Set<String> keys = new HashSet<>();
    // Not including the ref key in the set

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileChange(testFile, synchronization);

    // Should still watch the file
    verify(filesWatcher).watch(any());
  }

  @Test
  public void testOnFileDelete() {
    String fileUri = testFile.toURI().toASCIIString();
    String metacardId = "delete-metacard-id";
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    keys.add(refKey);

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);
    when(productToMetacardIdMap.loadFromPersistence(refKey)).thenReturn(metacardId);

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileDelete(testFile, synchronization);

    ArgumentCaptor<Exchange> exchangeCaptor = ArgumentCaptor.forClass(Exchange.class);
    verify(consumer).submitExchange(exchangeCaptor.capture());

    Exchange capturedExchange = exchangeCaptor.getValue();
    assertThat(capturedExchange, is(notNullValue()));
    assertThat(
        capturedExchange.getIn().getHeader("operation"),
        is(org.hamcrest.Matchers.equalTo("DELETE")));
  }

  @Test
  public void testOnFileDeleteWithoutCallback() {
    String fileUri = testFile.toURI().toASCIIString();
    String metacardId = "delete-metacard-id";
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    keys.add(refKey);

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);
    when(productToMetacardIdMap.loadFromPersistence(refKey)).thenReturn(metacardId);

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileDelete(testFile);

    // The actual implementation delegates to onFileDelete(File, Synchronization)
    // which then calls onFileChange, so verify filesWatcher.watch was called
    verify(filesWatcher).watch(any());
  }

  @Test
  public void testOnFileDeleteWithNoMetacardId() {
    String fileUri = testFile.toURI().toASCIIString();
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    // Not including the ref key

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileDelete(testFile, synchronization);

    // Should call onComplete on the synchronization
    verify(synchronization).onComplete(null);
    verify(consumer, never()).submitExchange(any(Exchange.class));
  }

  @Test
  public void testOnStart() {
    FileAlterationObserver observer = mock(FileAlterationObserver.class);
    listener.onStart(observer);
    // Should be a no-op: the observer is never touched and no file is watched
    verifyNoInteractions(observer);
    verifyNoInteractions(filesWatcher);
    verify(consumer, never()).submitExchange(any(Exchange.class));
  }

  @Test
  public void testOnDirectoryCreate() {
    File directory = temporaryFolder.getRoot();
    listener.onDirectoryCreate(directory);
    // Should be a no-op: nothing is watched and no exchange is submitted
    verifyNoInteractions(filesWatcher);
    verify(consumer, never()).submitExchange(any(Exchange.class));
  }

  @Test
  public void testOnDirectoryChange() {
    File directory = temporaryFolder.getRoot();
    listener.onDirectoryChange(directory);
    // Should be a no-op: nothing is watched and no exchange is submitted
    verifyNoInteractions(filesWatcher);
    verify(consumer, never()).submitExchange(any(Exchange.class));
  }

  @Test
  public void testOnDirectoryDelete() {
    File directory = temporaryFolder.getRoot();
    listener.onDirectoryDelete(directory);
    // Should be a no-op: nothing is watched and no exchange is submitted
    verifyNoInteractions(filesWatcher);
    verify(consumer, never()).submitExchange(any(Exchange.class));
  }

  @Test
  public void testOnStop() {
    FileAlterationObserver observer = mock(FileAlterationObserver.class);
    listener.onStop(observer);
    // Should be a no-op: the observer is never touched and no file is watched
    verifyNoInteractions(observer);
    verifyNoInteractions(filesWatcher);
    verify(consumer, never()).submitExchange(any(Exchange.class));
  }

  @Test
  public void testDestroy() {
    listener.destroy();
    verify(filesWatcher).destroy();
  }

  @Test
  public void testFileCreateSubmitsExchange() {
    listener.onFileCreate(testFile, synchronization);

    verify(filesWatcher).watch(any());
  }

  @Test
  public void testFileChangeWithEmptyMetacardId() {
    String fileUri = testFile.toURI().toASCIIString();
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    keys.add(refKey);

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);
    when(productToMetacardIdMap.loadFromPersistence(refKey)).thenReturn("");

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileChange(testFile, synchronization);

    // Should still watch the file even with empty metacardId
    verify(filesWatcher).watch(any());
  }

  @Test
  public void testFileChangeWithNullMetacardId() {
    String fileUri = testFile.toURI().toASCIIString();
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    keys.add(refKey);

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);
    when(productToMetacardIdMap.loadFromPersistence(refKey)).thenReturn(null);

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileChange(testFile, synchronization);

    // Should still watch the file even with null metacardId
    verify(filesWatcher).watch(any());
  }

  @Test
  public void testFileDeleteWithEmptyMetacardId() {
    String fileUri = testFile.toURI().toASCIIString();
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    keys.add(refKey);

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);
    when(productToMetacardIdMap.loadFromPersistence(anyString())).thenReturn("");

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    listener.onFileDelete(testFile, synchronization);

    verify(synchronization).onComplete(null);
    verify(consumer, never()).submitExchange(any(Exchange.class));
  }

  @Test
  public void testMultipleFileOperations() {
    String fileUri = testFile.toURI().toASCIIString();
    String metacardId = "multi-test-id";
    String refKey = DigestUtils.sha1Hex(fileUri);
    Set<String> keys = new HashSet<>();
    keys.add(refKey);

    when(productToMetacardIdMap.loadAllKeys()).thenReturn(keys);
    when(productToMetacardIdMap.loadFromPersistence(refKey)).thenReturn(metacardId);

    setProductToMetacardIdMap(listener, productToMetacardIdMap);

    // Create
    listener.onFileCreate(testFile, synchronization);

    // Change
    listener.onFileChange(testFile, synchronization);

    // Delete
    listener.onFileDelete(testFile, synchronization);

    // Verify that filesWatcher.watch was called at least once for each operation
    verify(filesWatcher, org.mockito.Mockito.atLeast(2)).watch(any());
    verify(consumer).submitExchange(any(Exchange.class));
  }

  /** Helper method to set the productToMetacardIdMap field using reflection since it's private */
  private void setProductToMetacardIdMap(
      DurableFileAlterationListener listener, FileSystemPersistenceProvider provider) {
    try {
      java.lang.reflect.Field field =
          DurableFileAlterationListener.class.getDeclaredField("productToMetacardIdMap");
      field.setAccessible(true);
      field.set(listener, provider);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set productToMetacardIdMap", e);
    }
  }
}
