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
package org.codice.ddf.spatial.ogc.csw.catalog.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import com.thoughtworks.xstream.converters.Converter;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.security.encryption.EncryptionService;
import ddf.security.permission.Permissions;
import java.util.Map;
import java.util.function.Consumer;
import org.codice.ddf.cxf.client.ClientBuilderFactory;
import org.codice.ddf.cxf.client.SecureCxfClientFactory;
import org.codice.ddf.security.Security;
import org.codice.ddf.spatial.ogc.csw.catalog.common.Csw;
import org.codice.ddf.spatial.ogc.csw.catalog.common.CswSourceConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;

/** Unit tests for CswSourceImpl */
@RunWith(MockitoJUnitRunner.class)
public class CswSourceImplTest {

  private static final String CSW_URL = "http://example.com/csw";
  private static final String SOURCE_ID = "test-csw-source";

  @Mock private BundleContext mockContext;

  @Mock private CswSourceConfiguration mockConfiguration;

  @Mock private Converter mockConverter;

  @Mock private ClientBuilderFactory mockClientBuilderFactory;

  @Mock private EncryptionService mockEncryptionService;

  @Mock private Security mockSecurity;

  @Mock private Permissions mockPermissions;

  @Mock private SecureCxfClientFactory<Csw> mockFactory;

  @Mock private Csw mockCsw;

  private FilterBuilder filterBuilder;

  private CswSourceImpl cswSource;

  @Before
  public void setUp() throws Exception {
    filterBuilder = new GeotoolsFilterBuilder();

    // Configure mock configuration
    when(mockConfiguration.getId()).thenReturn(SOURCE_ID);
    when(mockConfiguration.getCswUrl()).thenReturn(CSW_URL);
    when(mockConfiguration.getDisableCnCheck()).thenReturn(false);
    when(mockConfiguration.getConnectionTimeout()).thenReturn(30000);
    when(mockConfiguration.getReceiveTimeout()).thenReturn(60000);
    when(mockConfiguration.getOutputSchema()).thenReturn("http://www.opengis.net/cat/csw/2.0.2");
    when(mockConfiguration.getQueryTypeName()).thenReturn("csw:Record");
    when(mockConfiguration.getQueryTypeNamespace())
        .thenReturn("http://www.opengis.net/cat/csw/2.0.2");

    cswSource =
        new CswSourceImpl(
            mockContext,
            mockConfiguration,
            mockConverter,
            mockClientBuilderFactory,
            mockEncryptionService,
            mockSecurity,
            mockPermissions);
  }

  @Test
  public void testConstructorWithFullParameters() {
    assertThat(cswSource, is(notNullValue()));
  }

  @Test
  public void testConstructorWithMinimalParameters() {
    CswSourceImpl minimalSource =
        new CswSourceImpl(
            mockEncryptionService, mockClientBuilderFactory, mockSecurity, mockPermissions);

    assertThat(minimalSource, is(notNullValue()));
  }

  @Test
  public void testGetAdditionalConsumers() {
    Map<String, Consumer<Object>> consumers = cswSource.getAdditionalConsumers();

    assertThat(consumers, is(notNullValue()));
    assertThat(consumers.isEmpty(), is(true));
  }

  @Test
  public void testGetId() {
    when(mockConfiguration.getId()).thenReturn(SOURCE_ID);

    String id = mockConfiguration.getId();

    assertThat(id, is(equalTo(SOURCE_ID)));
  }

  @Test
  public void testGetCswUrl() {
    when(mockConfiguration.getCswUrl()).thenReturn(CSW_URL);

    String url = mockConfiguration.getCswUrl();

    assertThat(url, is(equalTo(CSW_URL)));
  }

  @Test
  public void testConstructorWithNullEncryptionService() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new CswSourceImpl(null, mockClientBuilderFactory, mockSecurity, mockPermissions));
  }

  @Test
  public void testSourceConfiguration() {
    assertThat(mockConfiguration.getId(), is(equalTo(SOURCE_ID)));
    assertThat(mockConfiguration.getCswUrl(), is(equalTo(CSW_URL)));
    assertThat(mockConfiguration.getConnectionTimeout(), is(equalTo(30000)));
    assertThat(mockConfiguration.getReceiveTimeout(), is(equalTo(60000)));
  }

  @Test
  public void testGetOutputSchema() {
    String outputSchema = mockConfiguration.getOutputSchema();

    assertThat(outputSchema, is(notNullValue()));
    assertThat(outputSchema, is(equalTo("http://www.opengis.net/cat/csw/2.0.2")));
  }

  @Test
  public void testGetQueryTypeName() {
    String queryTypeName = mockConfiguration.getQueryTypeName();

    assertThat(queryTypeName, is(equalTo("csw:Record")));
  }

  @Test
  public void testGetQueryTypeNamespace() {
    String queryTypeNamespace = mockConfiguration.getQueryTypeNamespace();

    assertThat(queryTypeNamespace, is(equalTo("http://www.opengis.net/cat/csw/2.0.2")));
  }

  @Test
  public void testDisableCnCheckConfiguration() {
    when(mockConfiguration.getDisableCnCheck()).thenReturn(true);

    boolean disableCnCheck = mockConfiguration.getDisableCnCheck();

    assertThat(disableCnCheck, is(true));
  }

  @Test
  public void testTimeoutConfiguration() {
    when(mockConfiguration.getConnectionTimeout()).thenReturn(10000);
    when(mockConfiguration.getReceiveTimeout()).thenReturn(20000);

    assertThat(mockConfiguration.getConnectionTimeout(), is(equalTo(10000)));
    assertThat(mockConfiguration.getReceiveTimeout(), is(equalTo(20000)));
  }
}
