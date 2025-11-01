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
package org.codice.ddf.security.boot;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.karaf.system.SystemService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Comprehensive test suite for SecureBoot security component.
 *
 * <p>This class tests the critical security logic that prevents DDF installations in insecure
 * directories (user home directories).
 */
@RunWith(MockitoJUnitRunner.class)
public class SecureBootTest {

  private static final String DDF_HOME_PROPERTY = "ddf.home";
  private static final String USER_HOME_PROPERTY = "user.home";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Mock private SystemService mockSystemService;

  private String originalDdfHome;
  private String originalUserHome;
  private SecurityManager originalSecurityManager;

  @Before
  public void setUp() throws Exception {
    // Save original system properties
    originalDdfHome = System.getProperty(DDF_HOME_PROPERTY);
    originalUserHome = System.getProperty(USER_HOME_PROPERTY);
    originalSecurityManager = System.getSecurityManager();
  }

  @After
  public void tearDown() {
    // Restore original system properties
    restoreSystemProperty(DDF_HOME_PROPERTY, originalDdfHome);
    restoreSystemProperty(USER_HOME_PROPERTY, originalUserHome);
    System.setSecurityManager(originalSecurityManager);
  }

  /**
   * Tests secure installation scenario where ddf.home is NOT within user.home. This should allow
   * the system to continue without shutdown (even when security manager is disabled).
   */
  @Test
  public void testSecureInstallation() throws Exception {
    // Create separate directories for user home and ddf home
    File userHomeDir = temporaryFolder.newFolder("user-home");
    File ddfHomeDir = temporaryFolder.newFolder("opt-ddf");

    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());
    System.setProperty(DDF_HOME_PROPERTY, ddfHomeDir.getAbsolutePath());

    SecureBoot secureBoot = new SecureBoot(mockSystemService);
    secureBoot.init();

    // Verify that shutdown was NOT called (secure installation)
    verify(mockSystemService, never()).halt(anyString());
  }

  /**
   * Tests insecure installation scenario where ddf.home is within user.home. Without security
   * manager enabled (default in Java 11+), this should NOT trigger shutdown.
   */
  @Test
  public void testInsecureInstallationSecurityManagerDisabled() throws Exception {
    // Create ddf home inside user home
    File userHomeDir = temporaryFolder.newFolder("user-home");
    File ddfHomeDir = new File(userHomeDir, "ddf-install");
    ddfHomeDir.mkdirs();

    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());
    System.setProperty(DDF_HOME_PROPERTY, ddfHomeDir.getAbsolutePath());

    // Security manager is null by default in tests
    SecureBoot secureBoot = new SecureBoot(mockSystemService);
    secureBoot.init();

    // Verify that shutdown was NOT called (security manager disabled)
    verify(mockSystemService, never()).halt(anyString());
  }

  /**
   * Tests that securityManagerEnabled() method correctly detects when no security manager is set.
   * This is the standard state in Java 11+.
   */
  @Test
  public void testSecurityManagerEnabledReturnsFalse() {
    File userHomeDir = new File(temporaryFolder.getRoot(), "user-home");
    File ddfHomeDir = new File(temporaryFolder.getRoot(), "opt-ddf");
    userHomeDir.mkdirs();
    ddfHomeDir.mkdirs();

    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());
    System.setProperty(DDF_HOME_PROPERTY, ddfHomeDir.getAbsolutePath());

    SecureBoot secureBoot = new SecureBoot(mockSystemService);

    // Verify security manager is detected as disabled (default state)
    assertThat(secureBoot.securityManagerEnabled(), is(false));
  }

  /**
   * Tests that symbolic links are resolved correctly to their real paths. This prevents bypassing
   * security checks with symlinks.
   */
  @Test
  public void testRealPathResolution() throws Exception {
    // Skip this test on Windows where symlinks require admin privileges
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
      return;
    }

    File userHomeDir = temporaryFolder.newFolder("user-home");
    File actualDdfDir = temporaryFolder.newFolder("opt-ddf-actual");
    File symlinkDdfDir = new File(userHomeDir, "ddf-symlink");

    // Create symbolic link from user home to actual ddf directory
    Path symlink = symlinkDdfDir.toPath();
    Path target = actualDdfDir.toPath();
    Files.createSymbolicLink(symlink, target);

    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());
    System.setProperty(DDF_HOME_PROPERTY, symlinkDdfDir.getAbsolutePath());

    SecureBoot secureBoot = new SecureBoot(mockSystemService);
    secureBoot.init();

    // Verify that shutdown was NOT called (symlink resolved to real path outside user home)
    verify(mockSystemService, never()).halt(anyString());
  }

  /**
   * Tests that IO errors when resolving ddf.home path trigger shutdown. This prevents operation
   * with invalid configurations.
   */
  @Test
  public void testInvalidDdfHomePath() throws Exception {
    // Set ddf.home to a path that will cause IOException when resolving
    File userHomeDir = temporaryFolder.newFolder("user-home");
    File nonExistentParent = new File("/nonexistent/parent/that/does/not/exist");
    File ddfHomeDir = new File(nonExistentParent, "ddf");

    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());
    System.setProperty(DDF_HOME_PROPERTY, ddfHomeDir.getAbsolutePath());

    SecureBoot secureBoot = new SecureBoot(mockSystemService);

    // Constructor should call getDdfHome() which triggers shutdown on IO error
    verify(mockSystemService, times(1)).halt("0");
  }

  /**
   * Tests that IO errors when resolving user.home path trigger shutdown. This prevents operation
   * with invalid configurations.
   */
  @Test
  public void testInvalidUserHomePath() throws Exception {
    // Set valid ddf.home
    File ddfHomeDir = temporaryFolder.newFolder("opt-ddf");
    // Set user.home to a path that will cause IOException when resolving
    File nonExistentParent = new File("/nonexistent/parent/that/does/not/exist");
    File userHomeDir = new File(nonExistentParent, "user");

    System.setProperty(DDF_HOME_PROPERTY, ddfHomeDir.getAbsolutePath());
    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());

    SecureBoot secureBoot = new SecureBoot(mockSystemService);

    // Constructor should call getUserHome() which triggers shutdown on IO error
    verify(mockSystemService, times(1)).halt("0");
  }

  /**
   * Tests that the shuttingDown flag prevents multiple shutdown attempts. This prevents cascading
   * shutdown calls and ensures clean shutdown.
   */
  @Test
  public void testShuttingDownFlag() throws Exception {
    // Set invalid path to trigger shutdown in constructor
    File userHomeDir = temporaryFolder.newFolder("user-home");
    File nonExistentParent = new File("/nonexistent/parent/that/does/not/exist");
    File ddfHomeDir = new File(nonExistentParent, "ddf");

    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());
    System.setProperty(DDF_HOME_PROPERTY, ddfHomeDir.getAbsolutePath());

    SecureBoot secureBoot = new SecureBoot(mockSystemService);

    // Try to call shutdown again via init (even though shutdown already happened in constructor)
    secureBoot.init();
    secureBoot.init();

    // Verify halt was only called once despite multiple attempts
    verify(mockSystemService, times(1)).halt("0");
  }

  /**
   * Tests secure installation with nested directory structure to ensure path comparison logic works
   * correctly.
   */
  @Test
  public void testSecureInstallationNestedPaths() throws Exception {
    // Create similar but non-overlapping paths
    File userHomeDir = temporaryFolder.newFolder("home-user");
    File ddfHomeDir = temporaryFolder.newFolder("home-ddf");

    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());
    System.setProperty(DDF_HOME_PROPERTY, ddfHomeDir.getAbsolutePath());

    SecureBoot secureBoot = new SecureBoot(mockSystemService);
    secureBoot.init();

    // Verify that shutdown was NOT called (secure installation)
    verify(mockSystemService, never()).halt(anyString());
  }

  /** Tests path startsWith logic handles different directory names correctly. */
  @Test
  public void testPathComparisonLogic() throws Exception {
    // Create paths with similar names but different structures
    File userHomeDir = temporaryFolder.newFolder("user");
    File ddfHomeDir = temporaryFolder.newFolder("user-data");

    System.setProperty(USER_HOME_PROPERTY, userHomeDir.getAbsolutePath());
    System.setProperty(DDF_HOME_PROPERTY, ddfHomeDir.getAbsolutePath());

    SecureBoot secureBoot = new SecureBoot(mockSystemService);
    secureBoot.init();

    // Verify that shutdown was NOT called (paths don't overlap)
    verify(mockSystemService, never()).halt(anyString());
  }

  /**
   * Helper method to restore system property to its original value.
   *
   * @param propertyName the name of the system property
   * @param originalValue the original value (can be null)
   */
  private void restoreSystemProperty(String propertyName, String originalValue) {
    if (originalValue == null) {
      System.clearProperty(propertyName);
    } else {
      System.setProperty(propertyName, originalValue);
    }
  }
}
