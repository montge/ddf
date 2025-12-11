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
package org.codice.admin.modules.installer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.net.URI;
import org.codice.ddf.ui.admin.api.module.AdminModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link InstallerModule} class. */
public class InstallerModuleTest {

  private InstallerModule module;

  @BeforeEach
  public void setUp() {
    module = new InstallerModule();
  }

  @Test
  public void testImplementsAdminModule() {
    assertThat(module instanceof AdminModule, is(true));
  }

  @Test
  public void testGetNameReturnsSetup() {
    assertThat(module.getName(), is("Setup"));
  }

  @Test
  public void testGetIdReturnsInstallation() {
    assertThat(module.getId(), is("installation"));
  }

  @Test
  public void testGetJSLocationReturnsValidUri() {
    URI jsLocation = module.getJSLocation();
    assertThat(jsLocation, is(notNullValue()));
    assertThat(jsLocation.toString(), is("js/modules/Installer.module.js"));
  }

  @Test
  public void testGetCSSLocationReturnsNull() {
    assertThat(module.getCSSLocation(), is(nullValue()));
  }

  @Test
  public void testGetIframeLocationReturnsNull() {
    assertThat(module.getIframeLocation(), is(nullValue()));
  }
}
