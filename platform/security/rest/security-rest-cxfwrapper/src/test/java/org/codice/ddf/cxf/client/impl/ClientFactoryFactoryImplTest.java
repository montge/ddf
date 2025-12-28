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
package org.codice.ddf.cxf.client.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.codice.ddf.configuration.PropertyResolver;
import org.codice.ddf.cxf.client.ClientBuilder;
import org.codice.ddf.cxf.client.ClientBuilderFactory;
import org.codice.ddf.cxf.client.SecureCxfClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("deprecation")
class ClientFactoryFactoryImplTest {

  @Mock private ClientBuilderFactory clientBuilderFactory;

  @Mock private ClientBuilder<TestService> clientBuilder;

  @Mock private SecureCxfClientFactory<TestService> secureCxfClientFactory;

  @Mock private Interceptor<? extends Message> interceptor;

  @Mock private PropertyResolver propertyResolver;

  private ClientFactoryFactoryImpl factory;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    factory = new ClientFactoryFactoryImpl();
    factory.setClientBuilderFactory(clientBuilderFactory);

    // Set up the mock chain for fluent builder
    when(clientBuilderFactory.<TestService>getClientBuilder()).thenReturn(clientBuilder);
    when(clientBuilder.endpoint(any())).thenReturn(clientBuilder);
    when(clientBuilder.interfaceClass(any())).thenReturn(clientBuilder);
    when(clientBuilder.entityProviders(any())).thenReturn(clientBuilder);
    when(clientBuilder.interceptor(any())).thenReturn(clientBuilder);
    when(clientBuilder.disableCnCheck(any(Boolean.class))).thenReturn(clientBuilder);
    when(clientBuilder.allowRedirects(any(Boolean.class))).thenReturn(clientBuilder);
    when(clientBuilder.connectionTimeout(any())).thenReturn(clientBuilder);
    when(clientBuilder.receiveTimeout(any())).thenReturn(clientBuilder);
    when(clientBuilder.username(any())).thenReturn(clientBuilder);
    when(clientBuilder.password(any())).thenReturn(clientBuilder);
    when(clientBuilder.clientKeyInfo(any(), any())).thenReturn(clientBuilder);
    when(clientBuilder.sslProtocol(any())).thenReturn(clientBuilder);
    when(clientBuilder.sourceId(any())).thenReturn(clientBuilder);
    when(clientBuilder.discovery(any())).thenReturn(clientBuilder);
    when(clientBuilder.clientId(any())).thenReturn(clientBuilder);
    when(clientBuilder.clientSecret(any())).thenReturn(clientBuilder);
    when(clientBuilder.oauthFlow(any())).thenReturn(clientBuilder);
    when(clientBuilder.additionalOauthParameters(any())).thenReturn(clientBuilder);
    when(clientBuilder.propertyResolver(any())).thenReturn(clientBuilder);
    when(clientBuilder.useOAuth(any(Boolean.class))).thenReturn(clientBuilder);
    when(clientBuilder.useSamlEcp(any(Boolean.class))).thenReturn(clientBuilder);
    when(clientBuilder.build()).thenReturn(secureCxfClientFactory);
  }

  @Test
  void testGetSecureCxfClientFactoryWithUsernamePassword() {
    List<?> providers = Collections.emptyList();

    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service",
            TestService.class,
            providers,
            interceptor,
            true,
            true,
            30000,
            60000,
            "user",
            "pass");

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).endpoint("https://example.com/service");
    verify(clientBuilder).interfaceClass(TestService.class);
    verify(clientBuilder).username("user");
    verify(clientBuilder).password("pass");
    verify(clientBuilder).useSamlEcp(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryWithCertificate() {
    List<?> providers = Collections.emptyList();

    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service",
            TestService.class,
            providers,
            interceptor,
            true,
            true,
            30000,
            60000,
            "certAlias",
            "/path/to/keystore.jks",
            "TLSv1.2");

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).sslProtocol("TLSv1.2");
    verify(clientBuilder).useSamlEcp(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryWithOAuthAndCertificate() {
    List<?> providers = Collections.emptyList();

    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service",
            TestService.class,
            providers,
            interceptor,
            true,
            true,
            30000,
            60000,
            "certAlias",
            "/path/to/keystore.jks",
            "TLSv1.2",
            "source1",
            "https://example.com/discovery",
            "clientId",
            "clientSecret",
            "client_credentials");

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).sourceId("source1");
    verify(clientBuilder).clientId("clientId");
    verify(clientBuilder).clientSecret("clientSecret");
    verify(clientBuilder).oauthFlow("client_credentials");
    verify(clientBuilder).useOAuth(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryWithTimeouts() {
    List<?> providers = Collections.emptyList();

    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service",
            TestService.class,
            providers,
            interceptor,
            false,
            false,
            15000,
            45000);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).connectionTimeout(15000);
    verify(clientBuilder).receiveTimeout(45000);
    verify(clientBuilder).disableCnCheck(false);
    verify(clientBuilder).allowRedirects(false);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryWithOAuthNoClientKey() {
    List<?> providers = Collections.emptyList();

    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service",
            TestService.class,
            providers,
            interceptor,
            true,
            true,
            30000,
            60000,
            "source1",
            "https://example.com/discovery",
            "clientId",
            "clientSecret",
            "code");

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).useOAuth(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryWithAdditionalOAuthParams() {
    List<?> providers = Collections.emptyList();
    Map<String, String> additionalParams = new HashMap<>();
    additionalParams.put("scope", "openid");
    additionalParams.put("audience", "api");

    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service",
            TestService.class,
            providers,
            interceptor,
            true,
            true,
            30000,
            60000,
            "source1",
            "https://example.com/discovery",
            "clientId",
            "clientSecret",
            "user",
            "pass",
            additionalParams);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).additionalOauthParameters(additionalParams);
    verify(clientBuilder).username("user");
    verify(clientBuilder).password("pass");
    verify(clientBuilder).useOAuth(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryWithPropertyResolver() {
    List<?> providers = Collections.emptyList();

    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service",
            TestService.class,
            providers,
            interceptor,
            true,
            false,
            propertyResolver);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).propertyResolver(propertyResolver);
    verify(clientBuilder).useSamlEcp(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryMinimalParams() {
    List<?> providers = Collections.emptyList();

    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service", TestService.class, providers, interceptor, false, true);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).disableCnCheck(false);
    verify(clientBuilder).allowRedirects(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryWithUsernamePasswordOnly() {
    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service", TestService.class, "admin", "secret");

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).username("admin");
    verify(clientBuilder).password("secret");
    verify(clientBuilder).useSamlEcp(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryWithOAuthOnly() {
    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory(
            "https://example.com/service",
            TestService.class,
            "source1",
            "https://example.com/discovery",
            "clientId",
            "clientSecret",
            "client_credentials");

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).sourceId("source1");
    verify(clientBuilder).oauthFlow("client_credentials");
    verify(clientBuilder).useOAuth(true);
    verify(clientBuilder).build();
  }

  @Test
  void testGetSecureCxfClientFactoryEndpointAndInterface() {
    SecureCxfClientFactory<TestService> result =
        factory.getSecureCxfClientFactory("https://example.com/service", TestService.class);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).endpoint("https://example.com/service");
    verify(clientBuilder).interfaceClass(TestService.class);
    verify(clientBuilder).useSamlEcp(true);
    verify(clientBuilder).build();
  }

  @Test
  void testSetClientBuilderFactory() {
    ClientFactoryFactoryImpl newFactory = new ClientFactoryFactoryImpl();
    ClientBuilderFactory mockFactory = mock(ClientBuilderFactory.class);

    newFactory.setClientBuilderFactory(mockFactory);

    // Verify by calling a method that uses the factory
    ClientBuilder<Object> mockBuilder = mock(ClientBuilder.class);
    when(mockFactory.getClientBuilder()).thenReturn(mockBuilder);
    when(mockBuilder.endpoint(any())).thenReturn(mockBuilder);
    when(mockBuilder.interfaceClass(any())).thenReturn(mockBuilder);
    when(mockBuilder.useSamlEcp(any(Boolean.class))).thenReturn(mockBuilder);
    when(mockBuilder.build()).thenReturn(mock(SecureCxfClientFactory.class));

    newFactory.getSecureCxfClientFactory("https://test.com", Object.class);

    verify(mockFactory).getClientBuilder();
  }

  @Test
  void testGetSecureCxfClientFactoryWithInvalidDiscoveryUrl() {
    List<?> providers = Collections.emptyList();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            factory.getSecureCxfClientFactory(
                "https://example.com/service",
                TestService.class,
                providers,
                interceptor,
                true,
                true,
                30000,
                60000,
                "source1",
                "not a valid uri %%",
                "clientId",
                "clientSecret",
                "code"));
  }

  private interface TestService {
    String doSomething();
  }
}
