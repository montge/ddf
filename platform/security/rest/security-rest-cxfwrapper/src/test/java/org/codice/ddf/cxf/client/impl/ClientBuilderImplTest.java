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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import ddf.security.audit.SecurityLogger;
import ddf.security.service.SecurityManager;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.codice.ddf.configuration.PropertyResolver;
import org.codice.ddf.cxf.client.ClientBuilder;
import org.codice.ddf.cxf.oauth.OAuthSecurity;
import org.codice.ddf.security.jaxrs.SamlSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientBuilderImplTest {

  @Mock private OAuthSecurity oauthSecurity;

  @Mock private SamlSecurity samlSecurity;

  @Mock private SecurityLogger securityLogger;

  @Mock private SecurityManager securityManager;

  @Mock private Interceptor<? extends Message> interceptor;

  @Mock private PropertyResolver propertyResolver;

  private ClientBuilderImpl<TestInterface> builder;

  @BeforeEach
  void setUp() {
    builder = new ClientBuilderImpl<>(oauthSecurity, samlSecurity, securityLogger, securityManager);
  }

  @Test
  void testEndpoint() {
    String endpointUrl = "https://example.com/service";

    ClientBuilder<TestInterface> result = builder.endpoint(endpointUrl);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.endpointUrl, is(endpointUrl));
  }

  @Test
  void testEndpointWithNullValue() {
    ClientBuilder<TestInterface> result = builder.endpoint(null);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.endpointUrl, is(nullValue()));
  }

  @Test
  void testInterfaceClass() {
    ClientBuilder<TestInterface> result = builder.interfaceClass(TestInterface.class);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.interfaceClass, equalTo(TestInterface.class));
  }

  @Test
  void testEntityProviders() {
    List<Object> providers = Collections.singletonList(new Object());

    ClientBuilder<TestInterface> result = builder.entityProviders(providers);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.entityProviders, is(providers));
  }

  @Test
  void testInterceptor() {
    ClientBuilder<TestInterface> result = builder.interceptor(interceptor);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.interceptor, is(interceptor));
  }

  @Test
  void testDisableCnCheck() {
    ClientBuilder<TestInterface> resultTrue = builder.disableCnCheck(true);
    assertThat(resultTrue, is(sameInstance(builder)));
    assertThat(builder.disableCnCheck, is(true));

    ClientBuilder<TestInterface> resultFalse = builder.disableCnCheck(false);
    assertThat(resultFalse, is(sameInstance(builder)));
    assertThat(builder.disableCnCheck, is(false));
  }

  @Test
  void testAllowRedirects() {
    ClientBuilder<TestInterface> resultTrue = builder.allowRedirects(true);
    assertThat(resultTrue, is(sameInstance(builder)));
    assertThat(builder.allowRedirects, is(true));

    ClientBuilder<TestInterface> resultFalse = builder.allowRedirects(false);
    assertThat(resultFalse, is(sameInstance(builder)));
    assertThat(builder.allowRedirects, is(false));
  }

  @Test
  void testConnectionTimeout() {
    Integer timeout = 30000;

    ClientBuilder<TestInterface> result = builder.connectionTimeout(timeout);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.connectionTimeout, is(timeout));
  }

  @Test
  void testConnectionTimeoutWithNull() {
    ClientBuilder<TestInterface> result = builder.connectionTimeout(null);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.connectionTimeout, is(nullValue()));
  }

  @Test
  void testReceiveTimeout() {
    Integer timeout = 60000;

    ClientBuilder<TestInterface> result = builder.receiveTimeout(timeout);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.receiveTimeout, is(timeout));
  }

  @Test
  void testUsername() {
    String username = "testUser";

    ClientBuilder<TestInterface> result = builder.username(username);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.username, is(username));
  }

  @Test
  void testPassword() {
    String password = "testPassword";

    ClientBuilder<TestInterface> result = builder.password(password);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.password, is(password));
  }

  @Test
  void testClientKeyInfo() {
    String certAlias = "myCert";
    Path keystorePath = Paths.get("/etc/keystore.jks");

    ClientBuilder<TestInterface> result = builder.clientKeyInfo(certAlias, keystorePath);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.clientKeyInfo, is(notNullValue()));
    assertThat(builder.clientKeyInfo.getAlias(), is(certAlias));
    assertThat(builder.clientKeyInfo.getKeystorePath(), is(keystorePath));
  }

  @Test
  void testSslProtocol() {
    String protocol = "TLSv1.3";

    ClientBuilder<TestInterface> result = builder.sslProtocol(protocol);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.sslProtocol, is(protocol));
  }

  @Test
  void testSslProtocolWithBlankValue() {
    ClientBuilder<TestInterface> result = builder.sslProtocol("");

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.sslProtocol, is(notNullValue()));
  }

  @Test
  void testSslProtocolWithNull() {
    ClientBuilder<TestInterface> result = builder.sslProtocol(null);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.sslProtocol, is(notNullValue()));
  }

  @Test
  void testSourceId() {
    String sourceId = "remote-source-1";

    ClientBuilder<TestInterface> result = builder.sourceId(sourceId);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.sourceId, is(sourceId));
  }

  @Test
  void testDiscovery() throws Exception {
    URI discoveryUrl = new URI("https://example.com/.well-known/openid-configuration");

    ClientBuilder<TestInterface> result = builder.discovery(discoveryUrl);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.discoveryUrl, is(discoveryUrl));
  }

  @Test
  void testClientId() {
    String clientId = "my-client-id";

    ClientBuilder<TestInterface> result = builder.clientId(clientId);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.clientId, is(clientId));
  }

  @Test
  void testClientSecret() {
    String clientSecret = "my-secret";

    ClientBuilder<TestInterface> result = builder.clientSecret(clientSecret);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.clientSecret, is(clientSecret));
  }

  @Test
  void testOauthFlow() {
    String oauthFlow = "client_credentials";

    ClientBuilder<TestInterface> result = builder.oauthFlow(oauthFlow);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.oauthFlow, is(oauthFlow));
  }

  @Test
  void testAdditionalOauthParameters() {
    Map<String, String> params = new HashMap<>();
    params.put("scope", "openid profile");
    params.put("audience", "my-api");

    ClientBuilder<TestInterface> result = builder.additionalOauthParameters(params);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.additionalOauthParameters, is(params));
  }

  @Test
  void testPropertyResolver() {
    ClientBuilder<TestInterface> result = builder.propertyResolver(propertyResolver);

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.propertyResolver, is(propertyResolver));
  }

  @Test
  void testUseOAuth() {
    ClientBuilder<TestInterface> resultTrue = builder.useOAuth(true);
    assertThat(resultTrue, is(sameInstance(builder)));
    assertThat(builder.useOAuth, is(true));

    ClientBuilder<TestInterface> resultFalse = builder.useOAuth(false);
    assertThat(resultFalse, is(sameInstance(builder)));
    assertThat(builder.useOAuth, is(false));
  }

  @Test
  void testUseSamlEcp() {
    ClientBuilder<TestInterface> resultTrue = builder.useSamlEcp(true);
    assertThat(resultTrue, is(sameInstance(builder)));
    assertThat(builder.useSamlEcp, is(true));

    ClientBuilder<TestInterface> resultFalse = builder.useSamlEcp(false);
    assertThat(resultFalse, is(sameInstance(builder)));
    assertThat(builder.useSamlEcp, is(false));
  }

  @Test
  void testUseSubjectRetrievalInterceptor() {
    ClientBuilder<TestInterface> result = builder.useSubjectRetrievalInterceptor();

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.useSubjectRetrievalInterceptor, is(true));
  }

  @Test
  void testFluentBuilderChaining() throws Exception {
    URI discoveryUri = new URI("https://example.com/discovery");

    ClientBuilder<TestInterface> result =
        builder
            .endpoint("https://example.com/api")
            .interfaceClass(TestInterface.class)
            .disableCnCheck(true)
            .allowRedirects(true)
            .connectionTimeout(30000)
            .receiveTimeout(60000)
            .username("user")
            .password("pass")
            .sourceId("source1")
            .discovery(discoveryUri)
            .clientId("client1")
            .clientSecret("secret1")
            .oauthFlow("code")
            .useOAuth(true)
            .useSamlEcp(false)
            .useSubjectRetrievalInterceptor();

    assertThat(result, is(sameInstance(builder)));
    assertThat(builder.endpointUrl, is("https://example.com/api"));
    assertThat(builder.interfaceClass, equalTo(TestInterface.class));
    assertThat(builder.disableCnCheck, is(true));
    assertThat(builder.allowRedirects, is(true));
    assertThat(builder.connectionTimeout, is(30000));
    assertThat(builder.receiveTimeout, is(60000));
    assertThat(builder.username, is("user"));
    assertThat(builder.password, is("pass"));
    assertThat(builder.sourceId, is("source1"));
    assertThat(builder.discoveryUrl, is(discoveryUri));
    assertThat(builder.clientId, is("client1"));
    assertThat(builder.clientSecret, is("secret1"));
    assertThat(builder.oauthFlow, is("code"));
    assertThat(builder.useOAuth, is(true));
    assertThat(builder.useSamlEcp, is(false));
    assertThat(builder.useSubjectRetrievalInterceptor, is(true));
  }

  @Test
  void testConstructorInitializesDependencies() {
    ClientBuilderImpl<Object> newBuilder =
        new ClientBuilderImpl<>(oauthSecurity, samlSecurity, securityLogger, securityManager);

    assertThat(newBuilder, is(notNullValue()));
  }

  @Test
  void testConstructorWithNullDependencies() {
    ClientBuilderImpl<Object> newBuilder = new ClientBuilderImpl<>(null, null, null, null);

    assertThat(newBuilder, is(notNullValue()));
  }

  private interface TestInterface {
    String doSomething();
  }
}
