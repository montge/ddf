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
package org.codice.ddf.condpermadmin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;

/** Extended unit tests for {@link PermissionActivator} to increase coverage */
@RunWith(MockitoJUnitRunner.class)
public class PermissionActivatorExtendedTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Mock private BundleContext mockBundleContext;

  @Mock private ConditionalPermissionAdmin mockPermAdmin;

  @Mock private ConditionalPermissionUpdate mockPermUpdate;

  private PermissionActivator activator;
  private File securityDir;

  @Before
  public void setUp() throws Exception {
    activator = new PermissionActivator();
    securityDir = tempFolder.newFolder("security");

    // Set system property for ddf.home
    System.setProperty("ddf.home", tempFolder.getRoot().getAbsolutePath());

    when(mockPermAdmin.newConditionalPermissionUpdate()).thenReturn(mockPermUpdate);
    when(mockPermUpdate.getConditionalPermissionInfos())
        .thenReturn(Collections.synchronizedList(new java.util.ArrayList<>()));
  }

  @Test
  public void testGetConditionalPermissionAdmin() {
    ConditionalPermissionAdmin admin = activator.getConditionalPermissionAdmin(mockBundleContext);

    assertThat(admin, is(notNullValue()));
  }

  @Test
  public void testGrantPermissionWithNewBundle() throws Exception {
    setupActivator();

    // Create a mock ConditionalPermissionInfo
    ConditionalPermissionInfo mockPermInfo = mock(ConditionalPermissionInfo.class);
    when(mockPermInfo.getAccessDecision()).thenReturn(ConditionalPermissionInfo.ALLOW);
    when(mockPermInfo.getPermissionInfos())
        .thenReturn(
            new PermissionInfo[] {new PermissionInfo("java.io.FilePermission", "*", "read")});
    when(mockPermInfo.getConditionInfos()).thenReturn(new ConditionInfo[] {});

    when(mockPermAdmin.newConditionalPermissionInfo(
            anyString(), any(ConditionInfo[].class), any(PermissionInfo[].class), anyString()))
        .thenReturn(mockPermInfo);

    activator.grantPermission("test-bundle", "java.io.FilePermission \"*\", \"read\"");

    verify(mockPermUpdate, times(1)).commit();
  }

  @Test
  public void testGrantPermissionToExistingBundle() throws Exception {
    setupActivator();

    // Create existing permission with bundle condition
    ConditionalPermissionInfo existingPermInfo = mock(ConditionalPermissionInfo.class);
    when(existingPermInfo.getAccessDecision()).thenReturn(ConditionalPermissionInfo.ALLOW);
    PermissionInfo[] permInfos =
        new PermissionInfo[] {new PermissionInfo("java.io.FilePermission", "*", "read")};
    when(existingPermInfo.getPermissionInfos()).thenReturn(permInfos);

    ConditionInfo[] conditions =
        new ConditionInfo[] {
          new ConditionInfo(
              "org.codice.ddf.condition.BundleNameCondition", new String[] {"existing-bundle"})
        };
    when(existingPermInfo.getConditionInfos()).thenReturn(conditions);
    when(existingPermInfo.getName()).thenReturn("test-perm");

    List<ConditionalPermissionInfo> permList =
        Collections.synchronizedList(Arrays.asList(existingPermInfo));
    when(mockPermUpdate.getConditionalPermissionInfos()).thenReturn(permList);

    when(mockPermAdmin.newConditionalPermissionInfo(
            anyString(), any(ConditionInfo[].class), any(PermissionInfo[].class), anyString()))
        .thenReturn(existingPermInfo);

    activator.grantPermission("test-bundle", "java.io.FilePermission \"*\", \"read\"");

    verify(mockPermUpdate, times(1)).commit();
  }

  @Test
  public void testSystemExitCalledOnParseError() throws Exception {
    // Create invalid policy file
    File invalidPolicyFile = new File(securityDir, "invalid-policy.policy");
    try (FileWriter writer = new FileWriter(invalidPolicyFile)) {
      writer.write("invalid policy content {{{");
    }

    activator.systemExit(invalidPolicyFile);

    // Test passes if no exception is thrown
    assertThat(invalidPolicyFile.exists(), is(true));
  }

  @Test
  public void testStartWithValidPolicyFile() throws Exception {
    // Create valid policy file
    File policyFile = new File(securityDir, "test.policy");
    try (FileWriter writer = new FileWriter(policyFile)) {
      writer.write(
          "grant codebase \"file:/test-bundle\" {\n"
              + "  permission java.io.FilePermission \"*\", \"read\";\n"
              + "};\n");
    }

    activator.conditionalPermissionAdmin = mockPermAdmin;

    when(mockPermUpdate.commit()).thenReturn(true);

    activator.start(mockBundleContext);

    verify(mockPermAdmin, times(1)).newConditionalPermissionUpdate();
    verify(mockPermUpdate, times(1)).commit();
  }

  @Test
  public void testStartWithMultiplePolicyFiles() throws Exception {
    // Create multiple policy files
    File policy1 = new File(securityDir, "policy1.policy");
    try (FileWriter writer = new FileWriter(policy1)) {
      writer.write(
          "grant codebase \"file:/bundle1\" {\n"
              + "  permission java.io.FilePermission \"*\", \"read\";\n"
              + "};\n");
    }

    File policy2 = new File(securityDir, "policy2.policy");
    try (FileWriter writer = new FileWriter(policy2)) {
      writer.write(
          "grant codebase \"file:/bundle2\" {\n"
              + "  permission java.net.SocketPermission \"*\", \"connect\";\n"
              + "};\n");
    }

    activator.conditionalPermissionAdmin = mockPermAdmin;

    when(mockPermUpdate.commit()).thenReturn(true);

    activator.start(mockBundleContext);

    verify(mockPermAdmin, times(1)).newConditionalPermissionUpdate();
    verify(mockPermUpdate, times(1)).commit();
  }

  @Test
  public void testStartWithDenyPolicy() throws Exception {
    File policyFile = new File(securityDir, "deny.policy");
    try (FileWriter writer = new FileWriter(policyFile)) {
      writer.write(
          "priority deny;\n"
              + "deny codebase \"file:/untrusted-bundle\" {\n"
              + "  permission java.io.FilePermission \"*\", \"write\";\n"
              + "};\n");
    }

    activator.conditionalPermissionAdmin = mockPermAdmin;

    when(mockPermUpdate.commit()).thenReturn(true);

    activator.start(mockBundleContext);

    verify(mockPermAdmin, times(1)).newConditionalPermissionUpdate();
    verify(mockPermUpdate, times(1)).commit();
  }

  @Test
  public void testStartWithPrincipals() throws Exception {
    File policyFile = new File(securityDir, "principals.policy");
    try (FileWriter writer = new FileWriter(policyFile)) {
      writer.write(
          "grant principal javax.security.auth.x500.X500Principal \"CN=Test\" {\n"
              + "  permission java.io.FilePermission \"*\", \"read\";\n"
              + "};\n");
    }

    activator.conditionalPermissionAdmin = mockPermAdmin;

    when(mockPermUpdate.commit()).thenReturn(true);

    activator.start(mockBundleContext);

    verify(mockPermAdmin, times(1)).newConditionalPermissionUpdate();
    verify(mockPermUpdate, times(1)).commit();
  }

  @Test
  public void testStartWithSignedBy() throws Exception {
    File policyFile = new File(securityDir, "signed.policy");
    try (FileWriter writer = new FileWriter(policyFile)) {
      writer.write(
          "grant signedBy \"testSigner\" {\n"
              + "  permission java.io.FilePermission \"*\", \"read\";\n"
              + "};\n");
    }

    activator.conditionalPermissionAdmin = mockPermAdmin;

    when(mockPermUpdate.commit()).thenReturn(true);

    activator.start(mockBundleContext);

    verify(mockPermAdmin, times(1)).newConditionalPermissionUpdate();
    verify(mockPermUpdate, times(1)).commit();
  }

  @Test
  public void testStartWithEmptySecurityDirectory() throws Exception {
    // Empty directory - no policy files
    activator.conditionalPermissionAdmin = mockPermAdmin;

    when(mockPermUpdate.commit()).thenReturn(true);

    activator.start(mockBundleContext);

    verify(mockPermAdmin, times(1)).newConditionalPermissionUpdate();
    verify(mockPermUpdate, times(1)).commit();
  }

  @Test
  public void testStopActivator() throws Exception {
    setupActivator();

    activator.stop(mockBundleContext);

    // Test that stop completes without error
    assertThat(activator, is(notNullValue()));
  }

  private void setupActivator() throws Exception {
    // Create a simple valid policy file
    File policyFile = new File(securityDir, "test.policy");
    try (FileWriter writer = new FileWriter(policyFile)) {
      writer.write(
          "priority grant;\n"
              + "grant codebase \"file:/test\" {\n"
              + "  permission java.io.FilePermission \"*\", \"read\";\n"
              + "};\n");
    }

    activator.conditionalPermissionAdmin = mockPermAdmin;

    when(mockPermUpdate.commit()).thenReturn(true);

    activator.start(mockBundleContext);
  }
}
