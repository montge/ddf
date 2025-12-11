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
package org.codice.ddf.cxf.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.codice.ddf.configuration.PropertyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClientBuilderTest {

  @Mock private ClientBuilder<TestService> clientBuilder;

  @Mock private SecureCxfClientFactory<TestService> clientFactory;

  @Mock private Interceptor<? extends Message> interceptor;

  @Mock private PropertyResolver propertyResolver;

  private List<Object> entityProviders;

  private Map<String, String> additionalOauthParams;

  @BeforeEach
  public void setUp() {
    entityProviders = Arrays.asList(new Object(), new Object());
    additionalOauthParams = new HashMap<>();
    additionalOauthParams.put("scope", "openid profile");
    additionalOauthParams.put("resource", "test-resource");
  }

  @Test
  public void testBuild() {
    when(clientBuilder.build()).thenReturn(clientFactory);

    SecureCxfClientFactory<TestService> result = clientBuilder.build();

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(clientFactory)));
    verify(clientBuilder).build();
  }

  @Test
  public void testEndpoint() {
    String endpointUrl = "https://localhost:8993/services/test";
    when(clientBuilder.endpoint(endpointUrl)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.endpoint(endpointUrl);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).endpoint(endpointUrl);
  }

  @Test
  public void testInterfaceClass() {
    when(clientBuilder.interfaceClass(TestService.class)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.interfaceClass(TestService.class);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).interfaceClass(TestService.class);
  }

  @Test
  public void testEntityProviders() {
    when(clientBuilder.entityProviders(entityProviders)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.entityProviders(entityProviders);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).entityProviders(entityProviders);
  }

  @Test
  public void testInterceptor() {
    when(clientBuilder.interceptor(interceptor)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.interceptor(interceptor);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).interceptor(interceptor);
  }

  @Test
  public void testDisableCnCheck() {
    when(clientBuilder.disableCnCheck(true)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.disableCnCheck(true);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).disableCnCheck(true);
  }

  @Test
  public void testDisableCnCheckFalse() {
    when(clientBuilder.disableCnCheck(false)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.disableCnCheck(false);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).disableCnCheck(false);
  }

  @Test
  public void testAllowRedirects() {
    when(clientBuilder.allowRedirects(true)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.allowRedirects(true);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).allowRedirects(true);
  }

  @Test
  public void testAllowRedirectsFalse() {
    when(clientBuilder.allowRedirects(false)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.allowRedirects(false);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).allowRedirects(false);
  }

  @Test
  public void testConnectionTimeout() {
    Integer timeout = 30000;
    when(clientBuilder.connectionTimeout(timeout)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.connectionTimeout(timeout);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).connectionTimeout(timeout);
  }

  @Test
  public void testReceiveTimeout() {
    Integer timeout = 60000;
    when(clientBuilder.receiveTimeout(timeout)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.receiveTimeout(timeout);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).receiveTimeout(timeout);
  }

  @Test
  public void testUsername() {
    String username = "testuser";
    when(clientBuilder.username(username)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.username(username);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).username(username);
  }

  @Test
  public void testPassword() {
    String password = "testpassword";
    when(clientBuilder.password(password)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.password(password);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).password(password);
  }

  @Test
  public void testClientKeyInfo() {
    String certAlias = "test-cert";
    Path keystorePath = Paths.get("/path/to/keystore.jks");
    when(clientBuilder.clientKeyInfo(certAlias, keystorePath)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.clientKeyInfo(certAlias, keystorePath);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).clientKeyInfo(certAlias, keystorePath);
  }

  @Test
  public void testSslProtocol() {
    String sslProtocol = "TLSv1.2";
    when(clientBuilder.sslProtocol(sslProtocol)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.sslProtocol(sslProtocol);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).sslProtocol(sslProtocol);
  }

  @Test
  public void testSourceId() {
    String sourceId = "test-source";
    when(clientBuilder.sourceId(sourceId)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.sourceId(sourceId);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).sourceId(sourceId);
  }

  @Test
  public void testDiscovery() {
    URI discoveryUrl = URI.create("https://oauth.example.com/.well-known/openid-configuration");
    when(clientBuilder.discovery(discoveryUrl)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.discovery(discoveryUrl);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).discovery(discoveryUrl);
  }

  @Test
  public void testClientId() {
    String clientId = "test-client-id";
    when(clientBuilder.clientId(clientId)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.clientId(clientId);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).clientId(clientId);
  }

  @Test
  public void testClientSecret() {
    String clientSecret = "test-client-secret";
    when(clientBuilder.clientSecret(clientSecret)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.clientSecret(clientSecret);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).clientSecret(clientSecret);
  }

  @Test
  public void testOauthFlow() {
    String oauthFlow = "client_credentials";
    when(clientBuilder.oauthFlow(oauthFlow)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.oauthFlow(oauthFlow);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).oauthFlow(oauthFlow);
  }

  @Test
  public void testAdditionalOauthParameters() {
    when(clientBuilder.additionalOauthParameters(additionalOauthParams)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result =
        clientBuilder.additionalOauthParameters(additionalOauthParams);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).additionalOauthParameters(additionalOauthParams);
  }

  @Test
  public void testPropertyResolver() {
    when(clientBuilder.propertyResolver(propertyResolver)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.propertyResolver(propertyResolver);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).propertyResolver(propertyResolver);
  }

  @Test
  public void testUseOAuth() {
    when(clientBuilder.useOAuth(true)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.useOAuth(true);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).useOAuth(true);
  }

  @Test
  public void testUseOAuthFalse() {
    when(clientBuilder.useOAuth(false)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.useOAuth(false);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).useOAuth(false);
  }

  @Test
  public void testUseSamlEcp() {
    when(clientBuilder.useSamlEcp(true)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.useSamlEcp(true);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).useSamlEcp(true);
  }

  @Test
  public void testUseSamlEcpFalse() {
    when(clientBuilder.useSamlEcp(false)).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.useSamlEcp(false);

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).useSamlEcp(false);
  }

  @Test
  public void testUseSubjectRetrievalInterceptor() {
    when(clientBuilder.useSubjectRetrievalInterceptor()).thenReturn(clientBuilder);

    ClientBuilder<TestService> result = clientBuilder.useSubjectRetrievalInterceptor();

    assertThat(result, is(notNullValue()));
    verify(clientBuilder).useSubjectRetrievalInterceptor();
  }

  @Test
  public void testFluentBuilderChaining() {
    when(clientBuilder.endpoint("https://localhost:8993/services/test")).thenReturn(clientBuilder);
    when(clientBuilder.interfaceClass(TestService.class)).thenReturn(clientBuilder);
    when(clientBuilder.connectionTimeout(30000)).thenReturn(clientBuilder);
    when(clientBuilder.receiveTimeout(60000)).thenReturn(clientBuilder);
    when(clientBuilder.allowRedirects(true)).thenReturn(clientBuilder);
    when(clientBuilder.build()).thenReturn(clientFactory);

    ClientBuilder<TestService> builder =
        clientBuilder
            .endpoint("https://localhost:8993/services/test")
            .interfaceClass(TestService.class)
            .connectionTimeout(30000)
            .receiveTimeout(60000)
            .allowRedirects(true);

    SecureCxfClientFactory<TestService> factory = builder.build();

    assertThat(factory, is(notNullValue()));
    verify(clientBuilder).endpoint("https://localhost:8993/services/test");
    verify(clientBuilder).interfaceClass(TestService.class);
    verify(clientBuilder).connectionTimeout(30000);
    verify(clientBuilder).receiveTimeout(60000);
    verify(clientBuilder).allowRedirects(true);
    verify(clientBuilder).build();
  }

  @Test
  public void testBuilderWithBasicAuthentication() {
    when(clientBuilder.endpoint("https://localhost:8993/services/test")).thenReturn(clientBuilder);
    when(clientBuilder.interfaceClass(TestService.class)).thenReturn(clientBuilder);
    when(clientBuilder.username("admin")).thenReturn(clientBuilder);
    when(clientBuilder.password("admin123")).thenReturn(clientBuilder);
    when(clientBuilder.build()).thenReturn(clientFactory);

    ClientBuilder<TestService> builder =
        clientBuilder
            .endpoint("https://localhost:8993/services/test")
            .interfaceClass(TestService.class)
            .username("admin")
            .password("admin123");

    SecureCxfClientFactory<TestService> factory = builder.build();

    assertThat(factory, is(notNullValue()));
    verify(clientBuilder).username("admin");
    verify(clientBuilder).password("admin123");
  }

  @Test
  public void testBuilderWithOAuthConfiguration() {
    URI discoveryUrl = URI.create("https://oauth.example.com/.well-known/openid-configuration");
    when(clientBuilder.endpoint("https://localhost:8993/services/test")).thenReturn(clientBuilder);
    when(clientBuilder.interfaceClass(TestService.class)).thenReturn(clientBuilder);
    when(clientBuilder.useOAuth(true)).thenReturn(clientBuilder);
    when(clientBuilder.sourceId("test-source")).thenReturn(clientBuilder);
    when(clientBuilder.discovery(discoveryUrl)).thenReturn(clientBuilder);
    when(clientBuilder.clientId("client-id")).thenReturn(clientBuilder);
    when(clientBuilder.clientSecret("client-secret")).thenReturn(clientBuilder);
    when(clientBuilder.oauthFlow("client_credentials")).thenReturn(clientBuilder);
    when(clientBuilder.build()).thenReturn(clientFactory);

    ClientBuilder<TestService> builder =
        clientBuilder
            .endpoint("https://localhost:8993/services/test")
            .interfaceClass(TestService.class)
            .useOAuth(true)
            .sourceId("test-source")
            .discovery(discoveryUrl)
            .clientId("client-id")
            .clientSecret("client-secret")
            .oauthFlow("client_credentials");

    SecureCxfClientFactory<TestService> factory = builder.build();

    assertThat(factory, is(notNullValue()));
    verify(clientBuilder).useOAuth(true);
    verify(clientBuilder).sourceId("test-source");
    verify(clientBuilder).discovery(discoveryUrl);
    verify(clientBuilder).clientId("client-id");
    verify(clientBuilder).clientSecret("client-secret");
    verify(clientBuilder).oauthFlow("client_credentials");
  }

  @Test
  public void testBuilderWithTlsConfiguration() {
    Path keystorePath = Paths.get("/etc/ddf/keystores/serverKeystore.jks");
    when(clientBuilder.endpoint("https://localhost:8993/services/test")).thenReturn(clientBuilder);
    when(clientBuilder.interfaceClass(TestService.class)).thenReturn(clientBuilder);
    when(clientBuilder.clientKeyInfo("server-cert", keystorePath)).thenReturn(clientBuilder);
    when(clientBuilder.sslProtocol("TLSv1.3")).thenReturn(clientBuilder);
    when(clientBuilder.disableCnCheck(false)).thenReturn(clientBuilder);
    when(clientBuilder.build()).thenReturn(clientFactory);

    ClientBuilder<TestService> builder =
        clientBuilder
            .endpoint("https://localhost:8993/services/test")
            .interfaceClass(TestService.class)
            .clientKeyInfo("server-cert", keystorePath)
            .sslProtocol("TLSv1.3")
            .disableCnCheck(false);

    SecureCxfClientFactory<TestService> factory = builder.build();

    assertThat(factory, is(notNullValue()));
    verify(clientBuilder).clientKeyInfo("server-cert", keystorePath);
    verify(clientBuilder).sslProtocol("TLSv1.3");
    verify(clientBuilder).disableCnCheck(false);
  }

  // Test interface to use as generic type parameter
  private interface TestService {
    String getData();
  }
}
