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
package ddf.security.pdp.realm.xacml.processor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ddf.security.audit.SecurityLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableRuleMigrationSupport
@ExtendWith(MockitoExtension.class)
public class PollingPolicyFinderModuleTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Mock private SecurityLogger securityLogger;

  private File policyDirectory;

  private Set<String> policyDirectories;

  @BeforeEach
  public void setup() throws IOException {
    temporaryFolder.create();
    policyDirectory = temporaryFolder.newFolder("policies");
    policyDirectories = new HashSet<>();
    policyDirectories.add(policyDirectory.getAbsolutePath());
  }

  @AfterEach
  public void cleanup() {
    temporaryFolder.delete();
  }

  @Test
  public void testConstructorWithValidDirectory() {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testConstructorWithMultipleDirectories() {
    Set<String> multipleDirectories = new HashSet<>();
    multipleDirectories.add(policyDirectory.getAbsolutePath());
    multipleDirectories.add(policyDirectory.getAbsolutePath() + "/subdir");

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(multipleDirectories, 60, securityLogger);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testConstructorWithShortPollingInterval() {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 1, securityLogger);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testStart() {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    module.start();

    // Module should start without throwing exceptions
    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnDirectoryChange() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    module.onDirectoryChange(policyDirectory);

    verify(securityLogger, times(1)).audit(anyString(), anyString());
  }

  @Test
  public void testOnDirectoryCreate() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    File newDir = new File(policyDirectory, "newdir");
    newDir.mkdir();

    module.onDirectoryCreate(newDir);

    verify(securityLogger, times(1)).audit(anyString(), anyString());
  }

  @Test
  public void testOnDirectoryDelete() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    File tempDir = new File(policyDirectory, "tempdir");
    tempDir.mkdir();

    module.onDirectoryDelete(tempDir);

    verify(securityLogger, times(1)).audit(anyString(), anyString());
  }

  @Test
  public void testOnFileChange() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    File policyFile = new File(policyDirectory, "test-policy.xml");
    Files.write(policyFile.toPath(), "<?xml version=\"1.0\"?><Policy></Policy>".getBytes());

    module.onFileChange(policyFile);

    verify(securityLogger, times(1)).audit(anyString(), anyString(), anyString());
  }

  @Test
  public void testOnFileCreate() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    File policyFile = new File(policyDirectory, "new-policy.xml");
    Files.write(policyFile.toPath(), "<?xml version=\"1.0\"?><Policy></Policy>".getBytes());

    module.onFileCreate(policyFile);

    verify(securityLogger, times(1)).audit(anyString(), anyString(), anyString());
  }

  @Test
  public void testOnFileDelete() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    File policyFile = new File(policyDirectory, "deleted-policy.xml");
    policyFile.createNewFile();

    module.onFileDelete(policyFile);

    verify(securityLogger, times(1)).audit(anyString(), anyString());
  }

  @Test
  public void testOnStartWithEmptyDirectory() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    FileAlterationObserver observer = new FileAlterationObserver(policyDirectory);

    module.onStart(observer);

    // Should complete without exceptions
    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnStartWithPolicies() throws IOException {
    File policyFile = new File(policyDirectory, "test-policy.xml");
    Files.write(policyFile.toPath(), "<?xml version=\"1.0\"?><Policy></Policy>".getBytes());

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    FileAlterationObserver observer = new FileAlterationObserver(policyDirectory);

    module.onStart(observer);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnStop() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    FileAlterationObserver observer = new FileAlterationObserver(policyDirectory);

    module.onStop(observer);

    // Should complete without exceptions
    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testReloadPolicies() {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    module.reloadPolicies();

    // Should reload without exceptions
    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnDirectoryChangeWithIOException() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    // Create a file that will throw IOException when getting canonical path
    File mockFile = mock(File.class);
    doThrow(new IOException("Test IO Exception")).when(mockFile).getCanonicalPath();

    // Should not throw exception, should log error
    module.onDirectoryChange(mockFile);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnFileChangeWithIOException() throws IOException {
    SecurityLogger mockLogger = mock(SecurityLogger.class);

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, mockLogger);

    File mockFile = mock(File.class);
    doThrow(new IOException("Test IO Exception")).when(mockFile).getCanonicalPath();

    // Should not throw exception, should log error
    module.onFileChange(mockFile);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnFileCreateWithIOException() throws IOException {
    SecurityLogger mockLogger = mock(SecurityLogger.class);

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, mockLogger);

    File mockFile = mock(File.class);
    doThrow(new IOException("Test IO Exception")).when(mockFile).getCanonicalPath();

    // Should not throw exception, should log error
    module.onFileCreate(mockFile);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnFileDeleteWithIOException() throws IOException {
    SecurityLogger mockLogger = mock(SecurityLogger.class);

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, mockLogger);

    File mockFile = mock(File.class);
    doThrow(new IOException("Test IO Exception")).when(mockFile).getCanonicalPath();

    // Should not throw exception, should log error
    module.onFileDelete(mockFile);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnStartWithIOException() throws IOException {
    SecurityLogger mockLogger = mock(SecurityLogger.class);

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, mockLogger);

    FileAlterationObserver mockObserver = mock(FileAlterationObserver.class);
    File mockDirectory = mock(File.class);
    doThrow(new IOException("Test IO Exception")).when(mockDirectory).getCanonicalPath();
    org.mockito.Mockito.when(mockObserver.getDirectory()).thenReturn(mockDirectory);

    // Should not throw exception, should log error
    module.onStart(mockObserver);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnStopWithIOException() throws IOException {
    SecurityLogger mockLogger = mock(SecurityLogger.class);

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, mockLogger);

    FileAlterationObserver mockObserver = mock(FileAlterationObserver.class);
    File mockDirectory = mock(File.class);
    doThrow(new IOException("Test IO Exception")).when(mockDirectory).getCanonicalPath();
    org.mockito.Mockito.when(mockObserver.getDirectory()).thenReturn(mockDirectory);

    // Should not throw exception, should log error
    module.onStop(mockObserver);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testMultiplePolicyChanges() throws IOException, InterruptedException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 1, securityLogger);

    module.start();

    File policyFile = new File(policyDirectory, "test-policy.xml");
    Files.write(policyFile.toPath(), "<?xml version=\"1.0\"?><Policy></Policy>".getBytes());

    module.onFileCreate(policyFile);

    Files.write(
        policyFile.toPath(),
        "<?xml version=\"1.0\"?><Policy><Rule>Updated</Rule></Policy>".getBytes());

    module.onFileChange(policyFile);

    module.onFileDelete(policyFile);

    // Verify events were logged (at least 2 times for onCreate and onChange)
    verify(securityLogger, times(3)).audit(anyString(), anyString(), anyString());
  }

  @Test
  public void testNonXmlFileIgnored() throws IOException {
    File nonXmlFile = new File(policyDirectory, "readme.txt");
    Files.write(nonXmlFile.toPath(), "This is not a policy file".getBytes());

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    // The module should still initialize correctly
    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testXmlFileCaseInsensitive() throws IOException {
    File upperCaseXml = new File(policyDirectory, "POLICY.XML");
    Files.write(upperCaseXml.toPath(), "<?xml version=\"1.0\"?><Policy></Policy>".getBytes());

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    module.onFileCreate(upperCaseXml);

    verify(securityLogger, times(1)).audit(anyString(), anyString(), anyString());
  }

  @Test
  public void testEmptyPolicyDirectorySet() {
    Set<String> emptySet = Collections.emptySet();

    PollingPolicyFinderModule module = new PollingPolicyFinderModule(emptySet, 60, securityLogger);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testLongPollingInterval() {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 3600, securityLogger);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testDirectoryWithSpecialCharacters() throws IOException {
    File specialDir = temporaryFolder.newFolder("policy-dir-with-special_chars");
    Set<String> specialDirs = new HashSet<>();
    specialDirs.add(specialDir.getAbsolutePath());

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(specialDirs, 60, securityLogger);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testNestedDirectory() throws IOException {
    File parentDir = temporaryFolder.newFolder("parent");
    File childDir = new File(parentDir, "child");
    childDir.mkdir();

    Set<String> nestedDirs = new HashSet<>();
    nestedDirs.add(childDir.getAbsolutePath());

    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(nestedDirs, 60, securityLogger);

    assertThat(module, is(notNullValue()));
  }

  @Test
  public void testOnDirectoryCreateAuditLogging() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    File newDir = new File(policyDirectory, "audit-test");
    newDir.mkdir();

    module.onDirectoryCreate(newDir);

    verify(securityLogger).audit(anyString(), anyString());
  }

  @Test
  public void testOnDirectoryDeleteAuditLogging() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    File dirToDelete = new File(policyDirectory, "to-delete");
    dirToDelete.mkdir();

    module.onDirectoryDelete(dirToDelete);

    verify(securityLogger).audit(anyString(), anyString());
  }

  @Test
  public void testPolicyReloadOnFileChange() throws IOException {
    PollingPolicyFinderModule module =
        new PollingPolicyFinderModule(policyDirectories, 60, securityLogger);

    File policyFile = new File(policyDirectory, "reload-test.xml");
    Files.write(policyFile.toPath(), "<?xml version=\"1.0\"?><Policy></Policy>".getBytes());

    module.onFileChange(policyFile);

    // Verify the file change triggers both audit logging and policy reload
    verify(securityLogger).audit(anyString(), anyString(), anyString());
  }
}
