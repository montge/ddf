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
package org.codice.ddf.admin.core.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

/** Tests for {@link ConfigurationDetails} interface constants and default methods. */
public class ConfigurationDetailsTest {

  private TestConfigurationDetails configDetails;

  @Before
  public void setUp() {
    configDetails = new TestConfigurationDetails();
  }

  @Test
  public void testIdConstant() {
    assertThat(ConfigurationDetails.ID, is("id"));
  }

  @Test
  public void testFpidConstant() {
    assertThat(ConfigurationDetails.FPID, is("fpid"));
  }

  @Test
  public void testNameConstant() {
    assertThat(ConfigurationDetails.NAME, is("name"));
  }

  @Test
  public void testBundleConstant() {
    assertThat(ConfigurationDetails.BUNDLE, is("bundle"));
  }

  @Test
  public void testBundleNameConstant() {
    assertThat(ConfigurationDetails.BUNDLE_NAME, is("bundle_name"));
  }

  @Test
  public void testBundleLocationConstant() {
    assertThat(ConfigurationDetails.BUNDLE_LOCATION, is("bundle_location"));
  }

  @Test
  public void testConfigurationPropertiesConstant() {
    assertThat(ConfigurationDetails.CONFIGURATION_PROPERTIES, is("properties"));
  }

  @Test
  public void testEnabledConstant() {
    assertThat(ConfigurationDetails.ENABLED, is("enabled"));
  }

  @Test
  public void testDisabledServiceIdentifierConstant() {
    assertThat(ConfigurationDetails.DISABLED_SERVICE_IDENTIFIER, is("_disabled"));
  }

  @Test
  public void testGetIdReturnsNull() {
    assertThat(configDetails.getId(), is(nullValue()));
  }

  @Test
  public void testSetAndGetId() {
    configDetails.setId("config-123");
    assertThat(configDetails.getId(), is("config-123"));
  }

  @Test
  public void testGetFactoryPidReturnsNull() {
    assertThat(configDetails.getFactoryPid(), is(nullValue()));
  }

  @Test
  public void testSetAndGetFactoryPid() {
    configDetails.setFactoryPid("org.example.Factory");
    assertThat(configDetails.getFactoryPid(), is("org.example.Factory"));
  }

  @Test
  public void testGetNameReturnsNull() {
    assertThat(configDetails.getName(), is(nullValue()));
  }

  @Test
  public void testSetAndGetName() {
    configDetails.setName("Test Configuration");
    assertThat(configDetails.getName(), is("Test Configuration"));
  }

  @Test
  public void testSetAndGetBundle() {
    configDetails.setBundle(42L);
    assertThat(configDetails.getBundle(), is(42L));
  }

  @Test
  public void testGetBundleNameReturnsNull() {
    assertThat(configDetails.getBundleName(), is(nullValue()));
  }

  @Test
  public void testSetAndGetBundleName() {
    configDetails.setBundleName("my-bundle");
    assertThat(configDetails.getBundleName(), is("my-bundle"));
  }

  @Test
  public void testGetBundleLocationReturnsNull() {
    assertThat(configDetails.getBundleLocation(), is(nullValue()));
  }

  @Test
  public void testSetAndGetBundleLocation() {
    configDetails.setBundleLocation("file:/bundles/my-bundle.jar");
    assertThat(configDetails.getBundleLocation(), is("file:/bundles/my-bundle.jar"));
  }

  @Test
  public void testGetConfigurationPropertiesReturnsNull() {
    assertThat(configDetails.getConfigurationProperties(), is(nullValue()));
  }

  @Test
  public void testSetAndGetConfigurationProperties() {
    ConfigurationProperties props = mock(ConfigurationProperties.class);
    configDetails.setConfigurationProperties(props);
    assertThat(configDetails.getConfigurationProperties(), is(props));
  }

  @Test
  public void testSetAndGetEnabled() {
    configDetails.setEnabled(true);
    assertThat(configDetails.isEnabled(), is(true));
  }

  @Test
  public void testSetEnabledFalse() {
    configDetails.setEnabled(false);
    assertThat(configDetails.isEnabled(), is(false));
  }

  @Test
  public void testIdIsStoredInMap() {
    configDetails.setId("stored-id");
    assertThat(configDetails.get(ConfigurationDetails.ID), is("stored-id"));
  }

  @Test
  public void testFactoryPidIsStoredInMap() {
    configDetails.setFactoryPid("factory.pid");
    assertThat(configDetails.get(ConfigurationDetails.FPID), is("factory.pid"));
  }

  /** Test implementation of ConfigurationDetails that extends HashMap. */
  private static class TestConfigurationDetails extends HashMap<String, Object>
      implements ConfigurationDetails {}
}
