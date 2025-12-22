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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.thoughtworks.xstream.converters.Converter;
import ddf.security.encryption.EncryptionService;
import ddf.security.permission.Permissions;
import java.util.Map;
import java.util.function.Consumer;
import org.codice.ddf.cxf.client.ClientBuilderFactory;
import org.codice.ddf.security.Security;
import org.codice.ddf.spatial.ogc.csw.catalog.common.CswSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;

/** Unit tests for TransactionalCswStoreImpl */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionalCswStoreImplTest {

  private static final String CSW_URL = "http://example.com/csw";
  private static final String SOURCE_ID = "test-transactional-csw-store";

  @Mock private BundleContext mockContext;

  @Mock private CswSourceConfiguration mockConfiguration;

  @Mock private Converter mockConverter;

  @Mock private ClientBuilderFactory mockClientBuilderFactory;

  @Mock private EncryptionService mockEncryptionService;

  @Mock private Security mockSecurity;

  @Mock private Permissions mockPermissions;

  private TransactionalCswStoreImpl transactionalCswStore;

  @BeforeEach
  public void setUp() throws Exception {
    // Configure mock configuration
    when(mockConfiguration.getId()).thenReturn(SOURCE_ID);
    when(mockConfiguration.getCswUrl()).thenReturn(CSW_URL);
    when(mockConfiguration.getDisableCnCheck()).thenReturn(false);
    when(mockConfiguration.getConnectionTimeout()).thenReturn(30000);
    when(mockConfiguration.getReceiveTimeout()).thenReturn(60000);
    when(mockConfiguration.getOutputSchema()).thenReturn("http://www.opengis.net/cat/csw/2.0.2");

    transactionalCswStore =
        new TransactionalCswStoreImpl(
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
    assertThat(transactionalCswStore, is(notNullValue()));
  }

  @Test
  public void testConstructorWithMinimalParameters() {
    TransactionalCswStoreImpl minimalStore =
        new TransactionalCswStoreImpl(
            mockEncryptionService, mockClientBuilderFactory, mockSecurity, mockPermissions);

    assertThat(minimalStore, is(notNullValue()));
  }

  @Test
  public void testGetAdditionalConsumers() {
    Map<String, Consumer<Object>> consumers = transactionalCswStore.getAdditionalConsumers();

    assertThat(consumers, is(notNullValue()));
    assertThat(consumers.isEmpty(), is(true));
  }

  @Test
  public void testStoreConfiguration() {
    assertThat(mockConfiguration.getId(), is(equalTo(SOURCE_ID)));
    assertThat(mockConfiguration.getCswUrl(), is(equalTo(CSW_URL)));
  }

  @Test
  public void testOutputSchema() {
    String outputSchema = mockConfiguration.getOutputSchema();

    assertThat(outputSchema, is(notNullValue()));
    assertThat(outputSchema, is(equalTo("http://www.opengis.net/cat/csw/2.0.2")));
  }

  @Test
  public void testTimeouts() {
    assertThat(mockConfiguration.getConnectionTimeout(), is(equalTo(30000)));
    assertThat(mockConfiguration.getReceiveTimeout(), is(equalTo(60000)));
  }

  @Test
  public void testDisableCnCheckDefault() {
    boolean disableCnCheck = mockConfiguration.getDisableCnCheck();

    assertThat(disableCnCheck, is(false));
  }

  @Test
  public void testDisableCnCheckEnabled() {
    when(mockConfiguration.getDisableCnCheck()).thenReturn(true);

    boolean disableCnCheck = mockConfiguration.getDisableCnCheck();

    assertThat(disableCnCheck, is(true));
  }

  @Test
  public void testConstructorWithNullEncryptionService() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new TransactionalCswStoreImpl(
                null, mockClientBuilderFactory, mockSecurity, mockPermissions));
  }

  @Test
  public void testStoreIdConfiguration() {
    when(mockConfiguration.getId()).thenReturn("custom-store-id");

    String storeId = mockConfiguration.getId();

    assertThat(storeId, is(equalTo("custom-store-id")));
  }

  @Test
  public void testCswUrlConfiguration() {
    String customUrl = "https://secure.example.com/csw";
    when(mockConfiguration.getCswUrl()).thenReturn(customUrl);

    String cswUrl = mockConfiguration.getCswUrl();

    assertThat(cswUrl, is(equalTo(customUrl)));
  }
}
