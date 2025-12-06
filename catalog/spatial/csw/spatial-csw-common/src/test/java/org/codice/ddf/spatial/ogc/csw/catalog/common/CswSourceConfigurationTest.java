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
package org.codice.ddf.spatial.ogc.csw.catalog.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import ddf.security.encryption.EncryptionService;
import ddf.security.permission.Permissions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link CswSourceConfiguration}. */
public class CswSourceConfigurationTest {

  @Mock private EncryptionService encryptionService;

  @Mock private Permissions permissions;

  private CswSourceConfiguration configuration;

  private AutoCloseable mocks;

  @Before
  public void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
    configuration = new CswSourceConfiguration(encryptionService, permissions);
  }

  @After
  public void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  public void testConstructor() {
    assertThat(configuration, is(notNullValue()));
  }

  @Test
  public void testSetAndGetCswUrl() {
    configuration.setCswUrl("http://example.com/csw");
    assertThat(configuration.getCswUrl(), is("http://example.com/csw"));
  }

  @Test
  public void testSetAndGetId() {
    configuration.setId("test-csw-source");
    assertThat(configuration.getId(), is("test-csw-source"));
  }

  @Test
  public void testSetAndGetUsername() {
    configuration.setUsername("testuser");
    assertThat(configuration.getUsername(), is("testuser"));
  }

  @Test
  public void testSetAndGetPassword() {
    configuration.setPassword("testpassword");
    assertThat(configuration.getPassword(), is("testpassword"));
  }

  @Test
  public void testSetAndGetDisableCnCheck() {
    configuration.setDisableCnCheck(true);
    assertThat(configuration.getDisableCnCheck(), is(true));

    configuration.setDisableCnCheck(false);
    assertThat(configuration.getDisableCnCheck(), is(false));
  }

  @Test
  public void testSetAndGetCswAxisOrder() {
    configuration.setCswAxisOrder(CswAxisOrder.LON_LAT);
    assertThat(configuration.getCswAxisOrder(), is(CswAxisOrder.LON_LAT));

    configuration.setCswAxisOrder(CswAxisOrder.LAT_LON);
    assertThat(configuration.getCswAxisOrder(), is(CswAxisOrder.LAT_LON));
  }

  @Test
  public void testSetAndGetUsePosList() {
    configuration.setUsePosList(true);
    assertThat(configuration.isSetUsePosList(), is(true));

    configuration.setUsePosList(false);
    assertThat(configuration.isSetUsePosList(), is(false));
  }

  @Test
  public void testSetAndGetMetacardCswMappings() {
    java.util.Map<String, String> mappings = new java.util.HashMap<>();
    mappings.put("title", "dc:title");
    mappings.put("description", "dc:description");
    configuration.setMetacardCswMappings(mappings);
    assertThat(configuration.getMetacardCswMappings(), is(mappings));
  }

  @Test
  public void testSetAndGetPollIntervalMinutes() {
    configuration.setPollIntervalMinutes(60);
    assertThat(configuration.getPollIntervalMinutes(), is(60));
  }

  @Test
  public void testSetAndGetConnectionTimeout() {
    configuration.setConnectionTimeout(30000);
    assertThat(configuration.getConnectionTimeout(), is(30000));
  }

  @Test
  public void testSetAndGetReceiveTimeout() {
    configuration.setReceiveTimeout(60000);
    assertThat(configuration.getReceiveTimeout(), is(60000));
  }

  @Test
  public void testSetAndGetOutputSchema() {
    configuration.setOutputSchema("http://www.opengis.net/cat/csw/2.0.2");
    assertThat(configuration.getOutputSchema(), is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  public void testSetAndGetQueryTypeName() {
    configuration.setQueryTypeName("csw:Record");
    assertThat(configuration.getQueryTypeName(), is("csw:Record"));
  }

  @Test
  public void testSetAndGetQueryTypeNamespace() {
    configuration.setQueryTypeNamespace("http://www.opengis.net/cat/csw/2.0.2");
    assertThat(configuration.getQueryTypeNamespace(), is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  public void testSetAndGetIsCqlForced() {
    configuration.setIsCqlForced(true);
    assertThat(configuration.isCqlForced(), is(true));

    configuration.setIsCqlForced(false);
    assertThat(configuration.isCqlForced(), is(false));
  }

  @Test
  public void testDefaultValues() {
    CswSourceConfiguration newConfig = new CswSourceConfiguration(encryptionService, permissions);

    // Test that getters return reasonable defaults or null for unset values
    assertThat(newConfig.getCswUrl(), is(nullValue()));
    assertThat(newConfig.getId(), is(nullValue()));
    assertThat(newConfig.getUsername(), is(nullValue()));
  }

  @Test
  public void testSetNullValues() {
    configuration.setCswUrl(null);
    assertThat(configuration.getCswUrl(), is(nullValue()));

    configuration.setUsername(null);
    assertThat(configuration.getUsername(), is(nullValue()));
  }

  @Test
  public void testSetEmptyStrings() {
    configuration.setCswUrl("");
    assertThat(configuration.getCswUrl(), is(""));

    configuration.setUsername("");
    assertThat(configuration.getUsername(), is(""));

    configuration.setOutputSchema("");
    assertThat(configuration.getOutputSchema(), is(""));
  }
}
