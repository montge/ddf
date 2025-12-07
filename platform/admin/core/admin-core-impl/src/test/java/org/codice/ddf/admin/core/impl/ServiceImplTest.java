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

import java.util.Arrays;
import java.util.List;
import org.codice.ddf.admin.core.api.ConfigurationDetails;
import org.codice.ddf.admin.core.api.MetatypeAttribute;
import org.junit.Test;

public class ServiceImplTest {

  @Test
  public void testDefaultConstructor() {
    ServiceImpl service = new ServiceImpl();

    assertThat(service.isEmpty(), is(true));
    assertThat(service.size(), is(0));
  }

  @Test
  public void testSetAndGetFactory() {
    ServiceImpl service = new ServiceImpl();

    service.setFactory(true);
    assertThat(service.isFactory(), is(true));

    service.setFactory(false);
    assertThat(service.isFactory(), is(false));
  }

  @Test
  public void testSetAndGetDescription() {
    ServiceImpl service = new ServiceImpl();

    String description = "Test service description";
    service.setDescription(description);

    assertThat(service.getDescription(), is(equalTo(description)));
  }

  @Test
  public void testSetAndGetConfigurations() {
    ServiceImpl service = new ServiceImpl();

    ConfigurationDetails config1 = mock(ConfigurationDetails.class);
    ConfigurationDetails config2 = mock(ConfigurationDetails.class);
    List<ConfigurationDetails> configurations = Arrays.asList(config1, config2);

    service.setConfigurations(configurations);

    assertThat(service.getConfigurations(), is(equalTo(configurations)));
    assertThat(service.getConfigurations().size(), is(2));
  }

  @Test
  public void testSetAndGetDisabledConfigurations() {
    ServiceImpl service = new ServiceImpl();

    ConfigurationDetails disabledConfig1 = mock(ConfigurationDetails.class);
    ConfigurationDetails disabledConfig2 = mock(ConfigurationDetails.class);
    List<ConfigurationDetails> disabledConfigurations =
        Arrays.asList(disabledConfig1, disabledConfig2);

    service.setDisabledConfigurations(disabledConfigurations);

    assertThat(service.getDisabledConfigurations(), is(equalTo(disabledConfigurations)));
    assertThat(service.getDisabledConfigurations().size(), is(2));
  }

  @Test
  public void testMetatypeProperties() {
    ServiceImpl service = new ServiceImpl();

    String id = "service.id";
    String name = "Service Name";
    MetatypeAttribute attr = mock(MetatypeAttribute.class);
    List<MetatypeAttribute> attributes = Arrays.asList(attr);

    service.setId(id);
    service.setName(name);
    service.setAttributeDefinitions(attributes);

    assertThat(service.getId(), is(equalTo(id)));
    assertThat(service.getName(), is(equalTo(name)));
    assertThat(service.getAttributeDefinitions(), is(equalTo(attributes)));
  }

  @Test
  public void testEmptyConfigurations() {
    ServiceImpl service = new ServiceImpl();

    List<ConfigurationDetails> emptyConfigs = Arrays.asList();
    service.setConfigurations(emptyConfigs);

    assertThat(service.getConfigurations(), is(equalTo(emptyConfigs)));
    assertThat(service.getConfigurations().size(), is(0));
  }

  @Test
  public void testEmptyDisabledConfigurations() {
    ServiceImpl service = new ServiceImpl();

    List<ConfigurationDetails> emptyDisabledConfigs = Arrays.asList();
    service.setDisabledConfigurations(emptyDisabledConfigs);

    assertThat(service.getDisabledConfigurations(), is(equalTo(emptyDisabledConfigs)));
    assertThat(service.getDisabledConfigurations().size(), is(0));
  }

  @Test
  public void testNullValues() {
    ServiceImpl service = new ServiceImpl();

    service.setDescription(null);
    service.setConfigurations(null);
    service.setDisabledConfigurations(null);

    assertThat(service.getDescription(), is(nullValue()));
    assertThat(service.getConfigurations(), is(nullValue()));
    assertThat(service.getDisabledConfigurations(), is(nullValue()));
  }

  @Test
  public void testMapBehavior() {
    ServiceImpl service = new ServiceImpl();

    service.setFactory(true);
    service.setDescription("Test Description");
    service.setId("test.id");

    assertThat(service.containsKey("factory"), is(true));
    assertThat(service.containsKey("description"), is(true));
    assertThat(service.containsKey("id"), is(true));
  }

  @Test
  public void testCompleteServiceConfiguration() {
    ServiceImpl service = new ServiceImpl();

    service.setId("test.service.id");
    service.setName("Test Service");
    service.setDescription("A test service for unit testing");
    service.setFactory(true);

    MetatypeAttribute attr1 = mock(MetatypeAttribute.class);
    MetatypeAttribute attr2 = mock(MetatypeAttribute.class);
    service.setAttributeDefinitions(Arrays.asList(attr1, attr2));

    ConfigurationDetails config1 = mock(ConfigurationDetails.class);
    ConfigurationDetails config2 = mock(ConfigurationDetails.class);
    service.setConfigurations(Arrays.asList(config1, config2));

    ConfigurationDetails disabledConfig = mock(ConfigurationDetails.class);
    service.setDisabledConfigurations(Arrays.asList(disabledConfig));

    assertThat(service.getId(), is(equalTo("test.service.id")));
    assertThat(service.getName(), is(equalTo("Test Service")));
    assertThat(service.getDescription(), is(equalTo("A test service for unit testing")));
    assertThat(service.isFactory(), is(true));
    assertThat(service.getAttributeDefinitions().size(), is(2));
    assertThat(service.getConfigurations().size(), is(2));
    assertThat(service.getDisabledConfigurations().size(), is(1));
  }
}
