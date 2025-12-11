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
package org.codice.ddf.security.certificate.admin.module;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.codice.ddf.admin.application.plugin.AbstractApplicationPlugin;
import org.codice.ddf.admin.application.plugin.ApplicationPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link CertificateAdminModule} class. */
public class CertificateAdminModuleTest {

  private CertificateAdminModule module;

  @BeforeEach
  public void setUp() {
    module = new CertificateAdminModule();
  }

  @Test
  public void testExtendsAbstractApplicationPlugin() {
    assertThat(module instanceof AbstractApplicationPlugin, is(true));
  }

  @Test
  public void testImplementsApplicationPlugin() {
    assertThat(module instanceof ApplicationPlugin, is(true));
  }

  @Test
  public void testGetDisplayNameReturnsCertificates() {
    assertThat(module.getDisplayName(), is("Certificates"));
  }

  @Test
  public void testGetIframeLocationReturnsCertificatePath() {
    assertThat(module.getIframeLocation(), is("./certificate/index.html"));
  }

  @Test
  public void testGetAssociationsContainsSecurityServicesApp() {
    assertThat(module.getAssocations(), contains("security-services-app"));
  }

  @Test
  public void testGetIdIsNotNull() {
    assertThat(module.getID(), is(notNullValue()));
  }

  @Test
  public void testToJsonReturnsNonNullMap() {
    assertThat(module.toJSON(), is(notNullValue()));
  }

  @Test
  public void testToJsonContainsDisplayName() {
    assertThat(module.toJSON().get(ApplicationPlugin.DISPLAY_NAME_KEY), is("Certificates"));
  }

  @Test
  public void testToJsonContainsIframeLocation() {
    assertThat(
        module.toJSON().get(ApplicationPlugin.IFRAME_LOCATION_KEY), is("./certificate/index.html"));
  }

  @Test
  public void testMatchesAssociationNameForSecurityServicesApp() {
    assertThat(module.matchesAssocationName("security-services-app"), is(true));
  }

  @Test
  public void testMatchesAssociationNameReturnsFalseForOther() {
    assertThat(module.matchesAssocationName("other-app"), is(false));
  }
}
