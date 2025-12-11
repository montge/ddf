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
package org.codice.ddf.platform.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.codice.ddf.admin.application.plugin.AbstractApplicationPlugin;
import org.codice.ddf.admin.application.plugin.ApplicationPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link MetricsPlugin} class. */
public class MetricsPluginTest {

  private MetricsPlugin plugin;

  @BeforeEach
  public void setUp() {
    plugin = new MetricsPlugin();
  }

  @Test
  public void testExtendsAbstractApplicationPlugin() {
    assertThat(plugin instanceof AbstractApplicationPlugin, is(true));
  }

  @Test
  public void testImplementsApplicationPlugin() {
    assertThat(plugin instanceof ApplicationPlugin, is(true));
  }

  @Test
  public void testGetDisplayNameReturnsMetrics() {
    assertThat(plugin.getDisplayName(), is("Metrics"));
  }

  @Test
  public void testGetIframeLocationReturnsMetricsPath() {
    String location = plugin.getIframeLocation();
    assertThat(location, is("./metrics/index.html"));
  }

  @Test
  public void testGetAssociationsContainsPlatformApp() {
    assertThat(plugin.getAssocations(), contains("platform-app"));
  }

  @Test
  public void testGetIdIsNotNull() {
    assertThat(plugin.getID(), is(notNullValue()));
  }

  @Test
  public void testToJsonReturnsNonNullMap() {
    assertThat(plugin.toJSON(), is(notNullValue()));
  }

  @Test
  public void testToJsonContainsDisplayName() {
    assertThat(plugin.toJSON().get(ApplicationPlugin.DISPLAY_NAME_KEY), is("Metrics"));
  }

  @Test
  public void testToJsonContainsIframeLocation() {
    assertThat(
        plugin.toJSON().get(ApplicationPlugin.IFRAME_LOCATION_KEY), is("./metrics/index.html"));
  }

  @Test
  public void testMatchesAssociationNameForPlatformApp() {
    assertThat(plugin.matchesAssocationName("platform-app"), is(true));
  }

  @Test
  public void testMatchesAssociationNameReturnsFalseForOther() {
    assertThat(plugin.matchesAssocationName("other-app"), is(false));
  }
}
