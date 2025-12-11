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
package org.codice.admin.ui.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

import org.codice.ddf.branding.BrandingRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link Configuration} class. */
@ExtendWith(MockitoExtension.class)
public class ConfigurationTest {

  @Mock private BrandingRegistry brandingRegistry;

  private Configuration configuration;

  @BeforeEach
  public void setUp() {
    configuration = Configuration.getInstance();
    configuration.setSystemUsageEnabled(false);
    configuration.setSystemUsageTitle(null);
    configuration.setSystemUsageMessage(null);
    configuration.setSystemUsageOncePerSession(false);
    configuration.setDisabledInstallerApps("");
    configuration.setBranding(null);
  }

  @Test
  public void testGetInstanceReturnsSingleton() {
    Configuration instance1 = Configuration.getInstance();
    Configuration instance2 = Configuration.getInstance();

    assertThat(instance1, is(sameInstance(instance2)));
  }

  @Test
  public void testGetInstanceReturnsNonNull() {
    assertThat(Configuration.getInstance(), is(notNullValue()));
  }

  @Test
  public void testSetAndGetDisabledInstallerApps() {
    configuration.setDisabledInstallerApps("app1,app2");

    assertThat(configuration.getDisabledInstallerApps(), is("app1,app2"));
  }

  @Test
  public void testSetAndGetSystemUsageEnabled() {
    configuration.setSystemUsageEnabled(true);

    assertThat(configuration.getSystemUsageEnabled(), is(true));
  }

  @Test
  public void testSetAndGetSystemUsageTitle() {
    configuration.setSystemUsageTitle("Test Title");

    assertThat(configuration.getSystemUsageTitle(), is("Test Title"));
  }

  @Test
  public void testSetAndGetSystemUsageMessage() {
    configuration.setSystemUsageMessage("Test Message");

    assertThat(configuration.getSystemUsageMessage(), is("Test Message"));
  }

  @Test
  public void testSetAndGetSystemUsageOncePerSession() {
    configuration.setSystemUsageOncePerSession(true);

    assertThat(configuration.getSystemUsageOncePerSession(), is(true));
  }

  @Test
  public void testGetProductNameReturnsEmptyWhenNoBranding() {
    configuration.setBranding(null);

    assertThat(configuration.getProductName(), is(""));
  }

  @Test
  public void testGetProductNameReturnsBrandingProductName() {
    when(brandingRegistry.getProductName()).thenReturn("Test Product");
    configuration.setBranding(brandingRegistry);

    assertThat(configuration.getProductName(), is("Test Product"));
  }

  @Test
  public void testGetBrandingReturnsNullWhenNotSet() {
    configuration.setBranding(null);

    assertThat(configuration.getBranding(), is(nullValue()));
  }

  @Test
  public void testGetBrandingReturnsBrandingWhenSet() {
    configuration.setBranding(brandingRegistry);

    assertThat(configuration.getBranding(), is(sameInstance(brandingRegistry)));
  }
}
