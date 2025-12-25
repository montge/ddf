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
package org.codice.ddf.catalog.admin.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;
import org.codice.ddf.admin.application.plugin.ApplicationPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MaplayersPluginTest {

  private MaplayersPlugin plugin;

  @BeforeEach
  void setUp() {
    plugin = new MaplayersPlugin();
  }

  @Test
  void testDisplayName() {
    assertThat(plugin.getDisplayName(), is("Map Layers"));
  }

  @Test
  void testIframeLocation() {
    assertThat(plugin.getIframeLocation(), is("./map-layers/index.html"));
  }

  @Test
  void testAssociations() {
    assertThat(plugin.getAssocations(), contains("search-ui-app"));
  }

  @Test
  void testMatchesSearchUiApp() {
    assertThat(plugin.matchesAssocationName("search-ui-app"), is(true));
  }

  @Test
  void testDoesNotMatchOtherApp() {
    assertThat(plugin.matchesAssocationName("catalog-app"), is(false));
  }

  @Test
  void testIdIsGenerated() {
    assertThat(plugin.getID(), is(notNullValue()));
  }

  @Test
  void testToJsonContainsDisplayName() {
    Map<String, Object> json = plugin.toJSON();

    assertThat(json, hasEntry(ApplicationPlugin.DISPLAY_NAME_KEY, "Map Layers"));
  }

  @Test
  void testToJsonContainsIframeLocation() {
    Map<String, Object> json = plugin.toJSON();

    assertThat(json, hasEntry(ApplicationPlugin.IFRAME_LOCATION_KEY, "./map-layers/index.html"));
  }

  @Test
  void testToJsonContainsId() {
    Map<String, Object> json = plugin.toJSON();

    assertThat(json.get(ApplicationPlugin.ID_KEY), is(notNullValue()));
  }

  @Test
  void testPluginExtendsAbstractApplicationPlugin() {
    assertThat(
        plugin instanceof org.codice.ddf.admin.application.plugin.AbstractApplicationPlugin,
        is(true));
  }
}
