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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/** Tests for {@link Service} interface constants and default methods. */
public class ServiceTest {

  private TestService service;

  @Before
  public void setUp() {
    service = new TestService();
  }

  @Test
  public void testFactoryConstant() {
    assertThat(Service.FACTORY, is("factory"));
  }

  @Test
  public void testConfigurationsConstant() {
    assertThat(Service.CONFIGURATIONS, is("configurations"));
  }

  @Test
  public void testDisabledConfigurationsConstant() {
    assertThat(Service.DISABLED_CONFIGURATIONS, is("disabledConfigurations"));
  }

  @Test
  public void testDescriptionConstant() {
    assertThat(Service.DESCRIPTION, is("description"));
  }

  @Test
  public void testSetAndIsFactory() {
    service.setFactory(true);
    assertThat(service.isFactory(), is(true));
  }

  @Test
  public void testSetAndIsFactoryFalse() {
    service.setFactory(false);
    assertThat(service.isFactory(), is(false));
  }

  @Test
  public void testGetConfigurationsReturnsNull() {
    assertThat(service.getConfigurations(), is(nullValue()));
  }

  @Test
  public void testSetAndGetConfigurations() {
    List<ConfigurationDetails> configs = new ArrayList<>();
    service.setConfigurations(configs);
    assertThat(service.getConfigurations(), is(configs));
  }

  @Test
  public void testGetDescriptionReturnsNull() {
    assertThat(service.getDescription(), is(nullValue()));
  }

  @Test
  public void testSetAndGetDescription() {
    service.setDescription("A test service description");
    assertThat(service.getDescription(), is("A test service description"));
  }

  @Test
  public void testGetDisabledConfigurationsReturnsNull() {
    assertThat(service.getDisabledConfigurations(), is(nullValue()));
  }

  @Test
  public void testSetAndGetDisabledConfigurations() {
    List<ConfigurationDetails> disabledConfigs = new ArrayList<>();
    service.setDisabledConfigurations(disabledConfigs);
    assertThat(service.getDisabledConfigurations(), is(disabledConfigs));
  }

  @Test
  public void testFactoryIsStoredInMap() {
    service.setFactory(true);
    assertThat(service.get(Service.FACTORY), is(true));
  }

  @Test
  public void testDescriptionIsStoredInMap() {
    service.setDescription("My Description");
    assertThat(service.get(Service.DESCRIPTION), is("My Description"));
  }

  @Test
  public void testConfigurationsAreStoredInMap() {
    List<ConfigurationDetails> configs = new ArrayList<>();
    service.setConfigurations(configs);
    assertThat(service.get(Service.CONFIGURATIONS), is(configs));
  }

  @Test
  public void testDisabledConfigurationsAreStoredInMap() {
    List<ConfigurationDetails> disabledConfigs = new ArrayList<>();
    service.setDisabledConfigurations(disabledConfigs);
    assertThat(service.get(Service.DISABLED_CONFIGURATIONS), is(disabledConfigs));
  }

  // Test inherited Metatype functionality

  @Test
  public void testSetAndGetId() {
    service.setId("service-id");
    assertThat(service.getId(), is("service-id"));
  }

  @Test
  public void testSetAndGetName() {
    service.setName("Service Name");
    assertThat(service.getName(), is("Service Name"));
  }

  @Test
  public void testSetAndGetAttributeDefinitions() {
    List<MetatypeAttribute> attrs = new ArrayList<>();
    service.setAttributeDefinitions(attrs);
    assertThat(service.getAttributeDefinitions(), is(attrs));
  }

  /** Test implementation of Service that extends HashMap and implements Service. */
  private static class TestService extends HashMap<String, Object> implements Service {}
}
