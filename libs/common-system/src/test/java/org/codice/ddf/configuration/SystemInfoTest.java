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
package org.codice.ddf.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SystemInfoTest {

  @After
  public void cleanUp() {
    // Clean up system properties after each test
    System.clearProperty(SystemInfo.SITE_NAME);
    System.clearProperty(SystemInfo.SITE_CONTACT);
    System.clearProperty(SystemInfo.VERSION);
    System.clearProperty(SystemInfo.ORGANIZATION);
  }

  @Test
  public void testGetSiteNameWhenSet() {
    String expectedSiteName = "Test Site";
    System.setProperty(SystemInfo.SITE_NAME, expectedSiteName);

    String siteName = SystemInfo.getSiteName();

    assertThat(siteName, is(expectedSiteName));
  }

  @Test
  public void testGetSiteNameWhenNotSet() {
    String siteName = SystemInfo.getSiteName();

    assertThat(siteName, notNullValue());
    assertThat(siteName, is(""));
  }

  @Test
  public void testGetSiteContactWhenSet() {
    String expectedContact = "admin@example.com";
    System.setProperty(SystemInfo.SITE_CONTACT, expectedContact);

    String siteContact = SystemInfo.getSiteContatct();

    assertThat(siteContact, is(expectedContact));
  }

  @Test
  public void testGetSiteContactWhenNotSet() {
    String siteContact = SystemInfo.getSiteContatct();

    assertThat(siteContact, notNullValue());
    assertThat(siteContact, is(""));
  }

  @Test
  public void testGetVersionWhenSet() {
    String expectedVersion = "2.26.0";
    System.setProperty(SystemInfo.VERSION, expectedVersion);

    String version = SystemInfo.getVersion();

    assertThat(version, is(expectedVersion));
  }

  @Test
  public void testGetVersionWhenNotSet() {
    String version = SystemInfo.getVersion();

    assertThat(version, notNullValue());
    assertThat(version, is(""));
  }

  @Test
  public void testGetOrganizationWhenSet() {
    String expectedOrganization = "Codice Foundation";
    System.setProperty(SystemInfo.ORGANIZATION, expectedOrganization);

    String organization = SystemInfo.getOrganization();

    assertThat(organization, is(expectedOrganization));
  }

  @Test
  public void testGetOrganizationWhenNotSet() {
    String organization = SystemInfo.getOrganization();

    assertThat(organization, notNullValue());
    assertThat(organization, is(""));
  }

  @Test
  public void testConstants() {
    assertThat(SystemInfo.SITE_NAME, is("org.codice.ddf.system.siteName"));
    assertThat(SystemInfo.SITE_CONTACT, is("org.codice.ddf.system.siteContact"));
    assertThat(SystemInfo.VERSION, is("org.codice.ddf.system.version"));
    assertThat(SystemInfo.ORGANIZATION, is("org.codice.ddf.system.organization"));
  }

  @Test
  public void testMultiplePropertiesSet() {
    System.setProperty(SystemInfo.SITE_NAME, "Test Site");
    System.setProperty(SystemInfo.SITE_CONTACT, "admin@test.com");
    System.setProperty(SystemInfo.VERSION, "1.0.0");
    System.setProperty(SystemInfo.ORGANIZATION, "Test Org");

    assertThat(SystemInfo.getSiteName(), is("Test Site"));
    assertThat(SystemInfo.getSiteContatct(), is("admin@test.com"));
    assertThat(SystemInfo.getVersion(), is("1.0.0"));
    assertThat(SystemInfo.getOrganization(), is("Test Org"));
  }

  @Test
  public void testSiteNameWithSpecialCharacters() {
    String specialName = "Test-Site_123 (Production)";
    System.setProperty(SystemInfo.SITE_NAME, specialName);

    assertThat(SystemInfo.getSiteName(), is(specialName));
  }

  @Test
  public void testSiteContactWithEmailFormat() {
    String email = "user@example.com";
    System.setProperty(SystemInfo.SITE_CONTACT, email);

    assertThat(SystemInfo.getSiteContatct(), is(email));
  }

  @Test
  public void testVersionWithSemanticVersioning() {
    String version = "2.26.0-SNAPSHOT";
    System.setProperty(SystemInfo.VERSION, version);

    assertThat(SystemInfo.getVersion(), is(version));
  }

  @Test
  public void testOrganizationWithSpaces() {
    String organization = "The Codice Foundation";
    System.setProperty(SystemInfo.ORGANIZATION, organization);

    assertThat(SystemInfo.getOrganization(), is(organization));
  }

  @Test
  public void testEmptyStringProperties() {
    System.setProperty(SystemInfo.SITE_NAME, "");
    System.setProperty(SystemInfo.SITE_CONTACT, "");
    System.setProperty(SystemInfo.VERSION, "");
    System.setProperty(SystemInfo.ORGANIZATION, "");

    assertThat(SystemInfo.getSiteName(), is(""));
    assertThat(SystemInfo.getSiteContatct(), is(""));
    assertThat(SystemInfo.getVersion(), is(""));
    assertThat(SystemInfo.getOrganization(), is(""));
  }

  @Test
  public void testSiteNameWithUnicodeCharacters() {
    String unicodeName = "测试站点";
    System.setProperty(SystemInfo.SITE_NAME, unicodeName);

    assertThat(SystemInfo.getSiteName(), is(unicodeName));
  }

  @Test
  public void testSystemPropertiesIndependence() {
    // Set one property
    System.setProperty(SystemInfo.SITE_NAME, "Site A");

    // Verify only that property is set
    assertThat(SystemInfo.getSiteName(), is("Site A"));
    assertThat(SystemInfo.getSiteContatct(), is(""));
    assertThat(SystemInfo.getVersion(), is(""));
    assertThat(SystemInfo.getOrganization(), is(""));
  }

  @Test
  public void testPropertyOverwrite() {
    System.setProperty(SystemInfo.SITE_NAME, "Initial Site");
    assertThat(SystemInfo.getSiteName(), is("Initial Site"));

    System.setProperty(SystemInfo.SITE_NAME, "Updated Site");
    assertThat(SystemInfo.getSiteName(), is("Updated Site"));
  }

  @Test
  public void testLongPropertyValues() {
    String longSiteName = "A".repeat(1000);
    System.setProperty(SystemInfo.SITE_NAME, longSiteName);

    assertThat(SystemInfo.getSiteName(), is(longSiteName));
  }
}
