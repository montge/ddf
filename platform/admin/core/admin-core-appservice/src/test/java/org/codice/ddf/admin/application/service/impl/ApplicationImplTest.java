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
package org.codice.ddf.admin.application.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.karaf.features.BundleInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.Bundle;

/** Unit tests for {@link ApplicationImpl} */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationImplTest {

  private ApplicationImpl application;

  @Before
  public void setUp() {
    application = new ApplicationImpl();
  }

  @Test
  public void testDefaultConstructor() {
    ApplicationImpl app = new ApplicationImpl();

    assertThat(app.getName(), is(equalTo(null)));
    assertThat(app.getDescription(), is(equalTo(null)));
    assertThat(app.getBundles(), is(notNullValue()));
    assertThat(app.getBundles(), is(empty()));
  }

  @Test
  public void testBundleToBundleInfo() {
    Bundle mockBundle = mock(Bundle.class);
    when(mockBundle.getLocation()).thenReturn("file:/test/bundle.jar");

    BundleInfo bundleInfo = application.bundleToBundleInfo(mockBundle);

    assertThat(bundleInfo, is(notNullValue()));
    assertThat(bundleInfo.getLocation(), is(equalTo("file:/test/bundle.jar")));
    assertThat(bundleInfo.getOriginalLocation(), is(equalTo("file:/test/bundle.jar")));
    assertThat(bundleInfo.getStartLevel(), is(equalTo(-1)));
    assertThat(bundleInfo.isStart(), is(false));
    assertThat(bundleInfo.isDependency(), is(false));
    assertThat(bundleInfo.isBlacklisted(), is(false));
    assertThat(bundleInfo.isOverriden(), is(equalTo(BundleInfo.BundleOverrideMode.NONE)));
  }

  @Test
  public void testLoadBundlesWithActiveBundles() {
    Map<String, Bundle> bundlesByLocation = new HashMap<>();

    Bundle activeBundle1 = mock(Bundle.class);
    when(activeBundle1.getLocation()).thenReturn("file:/test/bundle1.jar");
    when(activeBundle1.getState()).thenReturn(Bundle.ACTIVE);

    Bundle activeBundle2 = mock(Bundle.class);
    when(activeBundle2.getLocation()).thenReturn("file:/test/bundle2.jar");
    when(activeBundle2.getState()).thenReturn(Bundle.ACTIVE);

    bundlesByLocation.put("file:/test/bundle1.jar", activeBundle1);
    bundlesByLocation.put("file:/test/bundle2.jar", activeBundle2);

    // Use reflection to set bundleLocations
    application.bundleLocations.add("file:/test/bundle1.jar");
    application.bundleLocations.add("file:/test/bundle2.jar");

    application.loadBundles(bundlesByLocation);

    Set<BundleInfo> bundles = application.getBundles();
    assertThat(bundles, hasSize(2));
  }

  @Test
  public void testLoadBundlesWithInactiveBundles() {
    Map<String, Bundle> bundlesByLocation = new HashMap<>();

    Bundle inactiveBundle = mock(Bundle.class);
    when(inactiveBundle.getLocation()).thenReturn("file:/test/bundle.jar");
    when(inactiveBundle.getState()).thenReturn(Bundle.RESOLVED);

    bundlesByLocation.put("file:/test/bundle.jar", inactiveBundle);

    application.bundleLocations.add("file:/test/bundle.jar");

    application.loadBundles(bundlesByLocation);

    Set<BundleInfo> bundles = application.getBundles();
    assertThat(bundles, is(empty()));
  }

  @Test
  public void testLoadBundlesWithMissingBundles() {
    Map<String, Bundle> bundlesByLocation = new HashMap<>();

    // Don't add the bundle to the map
    application.bundleLocations.add("file:/test/missing-bundle.jar");

    application.loadBundles(bundlesByLocation);

    Set<BundleInfo> bundles = application.getBundles();
    assertThat(bundles, is(empty()));
  }

  @Test
  public void testLoadBundlesWithMixedStates() {
    Map<String, Bundle> bundlesByLocation = new HashMap<>();

    Bundle activeBundle = mock(Bundle.class);
    when(activeBundle.getLocation()).thenReturn("file:/test/active.jar");
    when(activeBundle.getState()).thenReturn(Bundle.ACTIVE);

    Bundle installedBundle = mock(Bundle.class);
    when(installedBundle.getLocation()).thenReturn("file:/test/installed.jar");
    when(installedBundle.getState()).thenReturn(Bundle.INSTALLED);

    Bundle startingBundle = mock(Bundle.class);
    when(startingBundle.getLocation()).thenReturn("file:/test/starting.jar");
    when(startingBundle.getState()).thenReturn(Bundle.STARTING);

    bundlesByLocation.put("file:/test/active.jar", activeBundle);
    bundlesByLocation.put("file:/test/installed.jar", installedBundle);
    bundlesByLocation.put("file:/test/starting.jar", startingBundle);

    application.bundleLocations.add("file:/test/active.jar");
    application.bundleLocations.add("file:/test/installed.jar");
    application.bundleLocations.add("file:/test/starting.jar");

    application.loadBundles(bundlesByLocation);

    Set<BundleInfo> bundles = application.getBundles();
    // Only ACTIVE bundles should be loaded
    assertThat(bundles, hasSize(1));
  }

  @Test
  public void testLoadBundlesWithEmptyMap() {
    Map<String, Bundle> bundlesByLocation = new HashMap<>();

    application.bundleLocations.add("file:/test/bundle.jar");

    application.loadBundles(bundlesByLocation);

    Set<BundleInfo> bundles = application.getBundles();
    assertThat(bundles, is(empty()));
  }

  @Test
  public void testLoadBundlesWithEmptyBundleLocations() {
    Map<String, Bundle> bundlesByLocation = new HashMap<>();

    Bundle activeBundle = mock(Bundle.class);
    when(activeBundle.getLocation()).thenReturn("file:/test/bundle.jar");
    when(activeBundle.getState()).thenReturn(Bundle.ACTIVE);

    bundlesByLocation.put("file:/test/bundle.jar", activeBundle);

    // Don't add any bundle locations
    application.loadBundles(bundlesByLocation);

    Set<BundleInfo> bundles = application.getBundles();
    assertThat(bundles, is(empty()));
  }

  @Test
  public void testGetNameAndDescription() {
    // Access via reflection to set name and description since there are no setters in public API
    application.name = "Test Application";
    application.description = "Test Description";

    assertThat(application.getName(), is(equalTo("Test Application")));
    assertThat(application.getDescription(), is(equalTo("Test Description")));
  }

  @Test
  public void testMultipleLoadBundlesCalls() {
    Map<String, Bundle> bundlesByLocation1 = new HashMap<>();
    Map<String, Bundle> bundlesByLocation2 = new HashMap<>();

    Bundle activeBundle1 = mock(Bundle.class);
    when(activeBundle1.getLocation()).thenReturn("file:/test/bundle1.jar");
    when(activeBundle1.getState()).thenReturn(Bundle.ACTIVE);

    Bundle activeBundle2 = mock(Bundle.class);
    when(activeBundle2.getLocation()).thenReturn("file:/test/bundle2.jar");
    when(activeBundle2.getState()).thenReturn(Bundle.ACTIVE);

    bundlesByLocation1.put("file:/test/bundle1.jar", activeBundle1);
    bundlesByLocation2.put("file:/test/bundle2.jar", activeBundle2);

    application.bundleLocations.add("file:/test/bundle1.jar");

    application.loadBundles(bundlesByLocation1);
    assertThat(application.getBundles(), hasSize(1));

    // Add more bundle locations and load again
    application.bundleLocations.add("file:/test/bundle2.jar");
    application.loadBundles(bundlesByLocation2);

    // Bundles should accumulate (not replace)
    assertThat(application.getBundles(), hasSize(2));
  }
}
