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
package org.codice.ddf.admin.core.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import org.codice.ddf.admin.core.api.ConfigurationProperties;
import org.junit.jupiter.api.Test;

public class ConfigurationDetailsImplTest {

  @Test
  public void testDefaultConstructor() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    assertThat(details.isEmpty(), is(true));
    assertThat(details.size(), is(0));
  }

  @Test
  public void testSetAndGetId() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    String id = "test.config.id";
    details.setId(id);

    assertThat(details.getId(), is(equalTo(id)));
  }

  @Test
  public void testSetAndGetFactoryPid() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    String fpid = "test.factory.pid";
    details.setFactoryPid(fpid);

    assertThat(details.getFactoryPid(), is(equalTo(fpid)));
  }

  @Test
  public void testSetAndGetName() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    String name = "Test Configuration";
    details.setName(name);

    assertThat(details.getName(), is(equalTo(name)));
  }

  @Test
  public void testSetAndGetBundle() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    long bundleId = 123L;
    details.setBundle(bundleId);

    assertThat(details.getBundle(), is(equalTo(bundleId)));
  }

  @Test
  public void testSetAndGetBundleName() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    String bundleName = "Test Bundle";
    details.setBundleName(bundleName);

    assertThat(details.getBundleName(), is(equalTo(bundleName)));
  }

  @Test
  public void testSetAndGetBundleLocation() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    String bundleLocation = "file:/path/to/bundle.jar";
    details.setBundleLocation(bundleLocation);

    assertThat(details.getBundleLocation(), is(equalTo(bundleLocation)));
  }

  @Test
  public void testSetAndGetConfigurationProperties() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    ConfigurationProperties properties = mock(ConfigurationProperties.class);
    details.setConfigurationProperties(properties);

    assertThat(details.getConfigurationProperties(), is(equalTo(properties)));
  }

  @Test
  public void testSetAndGetEnabled() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    details.setEnabled(true);
    assertThat(details.isEnabled(), is(true));

    details.setEnabled(false);
    assertThat(details.isEnabled(), is(false));
  }

  @Test
  public void testNullValues() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    details.setId(null);
    details.setFactoryPid(null);
    details.setName(null);
    details.setBundleName(null);
    details.setBundleLocation(null);
    details.setConfigurationProperties(null);

    assertThat(details.getId(), is(nullValue()));
    assertThat(details.getFactoryPid(), is(nullValue()));
    assertThat(details.getName(), is(nullValue()));
    assertThat(details.getBundleName(), is(nullValue()));
    assertThat(details.getBundleLocation(), is(nullValue()));
    assertThat(details.getConfigurationProperties(), is(nullValue()));
  }

  @Test
  public void testMapBehavior() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    details.setId("test.id");
    details.setName("Test Name");
    details.setEnabled(true);

    assertThat(details.containsKey("id"), is(true));
    assertThat(details.containsKey("name"), is(true));
    assertThat(details.containsKey("enabled"), is(true));
    assertThat(details.containsKey("nonExistentKey"), is(false));
  }

  @Test
  public void testCompleteConfiguration() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    details.setId("complete.config.id");
    details.setFactoryPid("complete.factory.pid");
    details.setName("Complete Configuration");
    details.setBundle(42L);
    details.setBundleName("Complete Bundle");
    details.setBundleLocation("file:/complete/bundle.jar");
    details.setEnabled(true);

    ConfigurationProperties properties = new ConfigurationPropertiesImpl();
    properties.put("key1", "value1");
    details.setConfigurationProperties(properties);

    assertThat(details.getId(), is(equalTo("complete.config.id")));
    assertThat(details.getFactoryPid(), is(equalTo("complete.factory.pid")));
    assertThat(details.getName(), is(equalTo("Complete Configuration")));
    assertThat(details.getBundle(), is(equalTo(42L)));
    assertThat(details.getBundleName(), is(equalTo("Complete Bundle")));
    assertThat(details.getBundleLocation(), is(equalTo("file:/complete/bundle.jar")));
    assertThat(details.isEnabled(), is(true));
    assertThat(details.getConfigurationProperties(), is(equalTo(properties)));
  }

  @Test
  public void testBundleIdZero() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    details.setBundle(0L);
    assertThat(details.getBundle(), is(equalTo(0L)));
  }

  @Test
  public void testBundleIdNegative() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    details.setBundle(-1L);
    assertThat(details.getBundle(), is(equalTo(-1L)));
  }

  @Test
  public void testUpdateExistingValues() {
    ConfigurationDetailsImpl details = new ConfigurationDetailsImpl();

    details.setId("original.id");
    details.setName("Original Name");

    assertThat(details.getId(), is(equalTo("original.id")));
    assertThat(details.getName(), is(equalTo("Original Name")));

    details.setId("updated.id");
    details.setName("Updated Name");

    assertThat(details.getId(), is(equalTo("updated.id")));
    assertThat(details.getName(), is(equalTo("Updated Name")));
  }
}
