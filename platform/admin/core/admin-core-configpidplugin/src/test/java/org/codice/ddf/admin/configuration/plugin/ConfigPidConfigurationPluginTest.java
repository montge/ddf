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
package org.codice.ddf.admin.configuration.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import java.util.Dictionary;
import java.util.Hashtable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.ServiceReference;

class ConfigPidConfigurationPluginTest {

  private ConfigPidConfigurationPlugin plugin;
  private ServiceReference<?> serviceReference;

  @BeforeEach
  void setUp() {
    plugin = new ConfigPidConfigurationPlugin();
    serviceReference = mock(ServiceReference.class);
  }

  @Test
  void testModifyConfigurationAddsConfigurationPid() {
    Dictionary<String, Object> properties = new Hashtable<>();
    properties.put("service.pid", "my.service.pid");

    plugin.modifyConfiguration(serviceReference, properties);

    assertThat(properties.get("configurationPid"), is("my.service.pid"));
  }

  @Test
  void testModifyConfigurationWithNullProperties() {
    // Should not throw NPE
    plugin.modifyConfiguration(serviceReference, null);
  }

  @Test
  void testModifyConfigurationWithNullServicePid() {
    Dictionary<String, Object> properties = new Hashtable<>();
    // service.pid not set

    plugin.modifyConfiguration(serviceReference, properties);

    // configurationPid should not be added
    assertThat(properties.get("configurationPid"), is(nullValue()));
  }

  @Test
  void testModifyConfigurationWithExistingConfigurationPid() {
    Dictionary<String, Object> properties = new Hashtable<>();
    properties.put("service.pid", "new.service.pid");
    properties.put("configurationPid", "old.config.pid");

    plugin.modifyConfiguration(serviceReference, properties);

    // configurationPid should be overwritten with service.pid value
    assertThat(properties.get("configurationPid"), is("new.service.pid"));
  }

  @Test
  void testModifyConfigurationPreservesOtherProperties() {
    Dictionary<String, Object> properties = new Hashtable<>();
    properties.put("service.pid", "my.service.pid");
    properties.put("other.property", "some.value");
    properties.put("another.property", 123);

    plugin.modifyConfiguration(serviceReference, properties);

    assertThat(properties.get("configurationPid"), is("my.service.pid"));
    assertThat(properties.get("other.property"), is("some.value"));
    assertThat(properties.get("another.property"), is(123));
  }

  @Test
  void testModifyConfigurationWithEmptyProperties() {
    Dictionary<String, Object> properties = new Hashtable<>();

    plugin.modifyConfiguration(serviceReference, properties);

    // Should not add configurationPid when service.pid is missing
    assertThat(properties.isEmpty(), is(true));
  }
}
