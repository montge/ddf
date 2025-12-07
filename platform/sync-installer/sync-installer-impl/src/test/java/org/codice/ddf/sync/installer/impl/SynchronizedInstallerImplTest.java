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
package org.codice.ddf.sync.installer.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import org.apache.karaf.bundle.core.BundleInfo;
import org.apache.karaf.bundle.core.BundleService;
import org.apache.karaf.bundle.core.BundleState;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.codice.ddf.sync.installer.api.SynchronizedInstaller;
import org.codice.ddf.sync.installer.api.SynchronizedInstallerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.util.tracker.ServiceTracker;

/** Tests for {@link SynchronizedInstallerImpl} class. */
@RunWith(MockitoJUnitRunner.class)
public class SynchronizedInstallerImplTest {

  private static final String TEST_PID = "test.service.pid";
  private static final String TEST_FEATURE = "test-feature";
  private static final String TEST_BUNDLE = "test-bundle";

  @Mock private BundleContext bundleContext;

  @Mock private ConfigurationAdmin configAdmin;

  @Mock private FeaturesService featuresService;

  @Mock private BundleService bundleService;

  @Mock private Feature feature;

  @Mock private Bundle bundle;

  @Mock private BundleInfo bundleInfo;

  @Mock private Filter filter;

  private SynchronizedInstallerImpl installer;

  @Before
  public void setUp() throws Exception {
    installer =
        new SynchronizedInstallerImpl(bundleContext, configAdmin, featuresService, bundleService);
  }

  @Test
  public void testImplementsSynchronizedInstaller() {
    assertThat(installer, is(instanceOf(SynchronizedInstaller.class)));
  }

  // SynchronizedConfigurationListener tests

  @Test
  public void testConfigurationListenerIsNotUpdatedByDefault() {
    SynchronizedInstallerImpl.SynchronizedConfigurationListener listener =
        new SynchronizedInstallerImpl.SynchronizedConfigurationListener(TEST_PID);

    assertThat(listener.isUpdated(), is(false));
  }

  @Test
  public void testConfigurationListenerIsUpdatedOnMatchingEvent() {
    SynchronizedInstallerImpl.SynchronizedConfigurationListener listener =
        new SynchronizedInstallerImpl.SynchronizedConfigurationListener(TEST_PID);
    ConfigurationEvent event = mock(ConfigurationEvent.class);
    when(event.getPid()).thenReturn(TEST_PID);
    when(event.getType()).thenReturn(ConfigurationEvent.CM_UPDATED);

    listener.configurationEvent(event);

    assertThat(listener.isUpdated(), is(true));
  }

  @Test
  public void testConfigurationListenerIgnoresNonMatchingPid() {
    SynchronizedInstallerImpl.SynchronizedConfigurationListener listener =
        new SynchronizedInstallerImpl.SynchronizedConfigurationListener(TEST_PID);
    ConfigurationEvent event = mock(ConfigurationEvent.class);
    when(event.getPid()).thenReturn("other.pid");

    listener.configurationEvent(event);

    assertThat(listener.isUpdated(), is(false));
  }

  @Test
  public void testConfigurationListenerIgnoresNonUpdateEvent() {
    SynchronizedInstallerImpl.SynchronizedConfigurationListener listener =
        new SynchronizedInstallerImpl.SynchronizedConfigurationListener(TEST_PID);
    ConfigurationEvent event = mock(ConfigurationEvent.class);
    when(event.getPid()).thenReturn(TEST_PID);
    when(event.getType()).thenReturn(ConfigurationEvent.CM_DELETED);

    listener.configurationEvent(event);

    assertThat(listener.isUpdated(), is(false));
  }

  // getConfigListener tests

  @Test
  public void testGetConfigListenerReturnsListener() {
    SynchronizedInstallerImpl.SynchronizedConfigurationListener listener =
        installer.getConfigListener(TEST_PID);

    assertThat(listener, is(notNullValue()));
  }

  // wait method tests

  @Test
  public void testWaitReturnsImmediatelyWhenConditionMet() throws Exception {
    Callable<Boolean> alwaysTrue = () -> true;

    installer.wait(alwaysTrue, 1000, 100, "Should not fail");
    // No exception means success
  }

  @Test
  public void testWaitThrowsWhenConditionNotMetWithinTimeout() {
    Callable<Boolean> alwaysFalse = () -> false;

    assertThrows(
        SynchronizedInstallerException.class,
        () -> installer.wait(alwaysFalse, 100, 10, "Condition not met"));
  }

  @Test
  public void testWaitRethrowsInterruptedException() {
    Callable<Boolean> throwsInterrupt =
        () -> {
          throw new InterruptedException("test");
        };

    assertThrows(
        InterruptedException.class,
        () -> installer.wait(throwsInterrupt, 1000, 100, "Should throw"));
  }

  @Test
  public void testWaitWrapsGenericException() {
    Callable<Boolean> throwsGeneric =
        () -> {
          throw new RuntimeException("test");
        };

    assertThrows(
        SynchronizedInstallerException.class,
        () -> installer.wait(throwsGeneric, 1000, 100, "Should wrap exception"));
  }

  // stopBundles tests

  @Test
  public void testStopBundlesStopsMatchingBundle() throws Exception {
    when(bundle.getSymbolicName()).thenReturn(TEST_BUNDLE);
    when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});

    installer.stopBundles(TEST_BUNDLE);

    verify(bundle).stop();
  }

  @Test
  public void testStopBundlesSkipsNonMatchingBundle() throws Exception {
    when(bundle.getSymbolicName()).thenReturn("other-bundle");
    when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});

    installer.stopBundles(TEST_BUNDLE);

    verify(bundle, never()).stop();
  }

  @Test
  public void testStopBundlesStopsMultipleBundles() throws Exception {
    Bundle bundle2 = mock(Bundle.class);
    when(bundle.getSymbolicName()).thenReturn(TEST_BUNDLE);
    when(bundle2.getSymbolicName()).thenReturn("bundle2");
    when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle, bundle2});

    installer.stopBundles(TEST_BUNDLE, "bundle2");

    verify(bundle).stop();
    verify(bundle2).stop();
  }

  // startBundles tests

  @Test
  public void testStartBundlesStartsMatchingBundle() throws Exception {
    when(bundle.getSymbolicName()).thenReturn(TEST_BUNDLE);
    when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});

    installer.startBundles(TEST_BUNDLE);

    verify(bundle).start();
  }

  @Test
  public void testStartBundlesSkipsNonMatchingBundle() throws Exception {
    when(bundle.getSymbolicName()).thenReturn("other-bundle");
    when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});

    installer.startBundles(TEST_BUNDLE);

    verify(bundle, never()).start();
  }

  // installFeatures tests

  @Test
  public void testInstallFeaturesSkipsWhenFeatureAlreadyInstalled() throws Exception {
    when(featuresService.getFeature(TEST_FEATURE)).thenReturn(feature);
    when(featuresService.isInstalled(feature)).thenReturn(true);

    installer.installFeatures(TEST_FEATURE);

    verify(featuresService, never()).installFeatures(anySet(), any());
  }

  @Test
  public void testInstallFeaturesSkipsWhenFeatureNotFound() throws Exception {
    when(featuresService.getFeature(TEST_FEATURE)).thenReturn(null);

    installer.installFeatures(TEST_FEATURE);

    verify(featuresService, never()).installFeatures(anySet(), any());
  }

  // uninstallFeatures tests

  @Test
  public void testUninstallFeaturesSkipsWhenFeatureNotInstalled() throws Exception {
    when(featuresService.getFeature(TEST_FEATURE)).thenReturn(feature);
    when(featuresService.isInstalled(feature)).thenReturn(false);

    installer.uninstallFeatures(TEST_FEATURE);

    verify(featuresService, never()).uninstallFeatures(anySet(), any());
  }

  // waitForBundles tests - testing through bundle states

  @Test
  public void testWaitForBundlesSucceedsWhenAllBundlesActive() throws Exception {
    when(bundle.getSymbolicName()).thenReturn(TEST_BUNDLE);
    when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});
    when(bundleService.getInfo(bundle)).thenReturn(bundleInfo);
    when(bundleInfo.getState()).thenReturn(BundleState.Active);
    when(bundleInfo.isFragment()).thenReturn(false);

    // Should complete without exception
    installer.waitForBundles(100, TEST_BUNDLE);
  }

  @Test
  public void testWaitForBundlesSucceedsWhenFragmentResolved() throws Exception {
    when(bundle.getSymbolicName()).thenReturn(TEST_BUNDLE);
    when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});
    when(bundleService.getInfo(bundle)).thenReturn(bundleInfo);
    when(bundleInfo.getState()).thenReturn(BundleState.Resolved);
    when(bundleInfo.isFragment()).thenReturn(true);

    // Should complete without exception
    installer.waitForBundles(100, TEST_BUNDLE);
  }

  @Test
  public void testWaitForBundlesThrowsWhenBundleFailed() {
    java.util.Dictionary<String, String> headers = new java.util.Hashtable<>();
    headers.put(org.osgi.framework.Constants.BUNDLE_NAME, TEST_BUNDLE);
    when(bundle.getSymbolicName()).thenReturn(TEST_BUNDLE);
    when(bundle.getHeaders()).thenReturn(headers);
    when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});
    when(bundleService.getInfo(bundle)).thenReturn(bundleInfo);
    when(bundleInfo.getState()).thenReturn(BundleState.Failure);
    when(bundleService.getDiag(bundle)).thenReturn("Test diagnostic");

    assertThrows(
        SynchronizedInstallerException.class, () -> installer.waitForBundles(100, TEST_BUNDLE));
  }

  // getServiceTracker tests

  @Test
  public void testGetServiceTrackerReturnsTracker() throws Exception {
    ServiceTracker tracker = installer.getServiceTracker(bundleContext, filter);

    assertThat(tracker, is(notNullValue()));
  }
}
