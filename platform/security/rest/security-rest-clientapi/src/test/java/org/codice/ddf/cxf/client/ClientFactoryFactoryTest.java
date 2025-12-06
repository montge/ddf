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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.codice.ddf.configuration.PropertyResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientFactoryFactoryTest {

  @Mock private ClientFactoryFactory clientFactoryFactory;

  @Mock private SecureCxfClientFactory<TestService> secureCxfClientFactory;

  @Mock private Interceptor<? extends Message> interceptor;

  @Mock private PropertyResolver propertyResolver;

  private List<?> providers;

  private Map<String, String> additionalParameters;

  private static final String ENDPOINT_URL = "https://localhost:8993/services/test";
  private static final String USERNAME = "testuser";
  private static final String PASSWORD = "testpass";
  private static final String CERT_ALIAS = "test-cert";
  private static final String KEYSTORE_PATH = "/path/to/keystore.jks";
  private static final String SSL_PROTOCOL = "TLSv1.2";
  private static final String SOURCE_ID = "test-source";
  private static final String DISCOVERY_URL = "https://oauth.example.com/.well-known";
  private static final String CLIENT_ID = "client-id";
  private static final String CLIENT_SECRET = "client-secret";
  private static final String OAUTH_FLOW = "client_credentials";
  private static final Integer CONNECTION_TIMEOUT = 30000;
  private static final Integer RECEIVE_TIMEOUT = 60000;

  @Before
  public void setUp() {
    providers = Arrays.asList(new Object(), new Object());
    additionalParameters = new HashMap<>();
    additionalParameters.put("scope", "openid profile");
  }

  @Test
  public void testGetSecureCxfClientFactoryWithUsernamePassword() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            eq(USERNAME),
            eq(PASSWORD)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL,
            TestService.class,
            providers,
            interceptor,
            false,
            true,
            CONNECTION_TIMEOUT,
            RECEIVE_TIMEOUT,
            USERNAME,
            PASSWORD);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactoryWithTls() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            eq(CERT_ALIAS),
            eq(KEYSTORE_PATH),
            eq(SSL_PROTOCOL)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL,
            TestService.class,
            providers,
            interceptor,
            false,
            true,
            CONNECTION_TIMEOUT,
            RECEIVE_TIMEOUT,
            CERT_ALIAS,
            KEYSTORE_PATH,
            SSL_PROTOCOL);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactoryWithOAuth() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            eq(CERT_ALIAS),
            eq(KEYSTORE_PATH),
            eq(SSL_PROTOCOL),
            eq(SOURCE_ID),
            eq(DISCOVERY_URL),
            eq(CLIENT_ID),
            eq(CLIENT_SECRET),
            eq(OAUTH_FLOW)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL,
            TestService.class,
            providers,
            interceptor,
            false,
            true,
            CONNECTION_TIMEOUT,
            RECEIVE_TIMEOUT,
            CERT_ALIAS,
            KEYSTORE_PATH,
            SSL_PROTOCOL,
            SOURCE_ID,
            DISCOVERY_URL,
            CLIENT_ID,
            CLIENT_SECRET,
            OAUTH_FLOW);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactoryBasic() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean(),
            anyInt(),
            anyInt()))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL,
            TestService.class,
            providers,
            interceptor,
            false,
            true,
            CONNECTION_TIMEOUT,
            RECEIVE_TIMEOUT);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactoryWithOAuthNoTls() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            eq(SOURCE_ID),
            eq(DISCOVERY_URL),
            eq(CLIENT_ID),
            eq(CLIENT_SECRET),
            eq(OAUTH_FLOW)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL,
            TestService.class,
            providers,
            interceptor,
            false,
            true,
            CONNECTION_TIMEOUT,
            RECEIVE_TIMEOUT,
            SOURCE_ID,
            DISCOVERY_URL,
            CLIENT_ID,
            CLIENT_SECRET,
            OAUTH_FLOW);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactoryWithAdditionalOAuthParameters() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            eq(SOURCE_ID),
            eq(DISCOVERY_URL),
            eq(CLIENT_ID),
            eq(CLIENT_SECRET),
            eq(USERNAME),
            eq(PASSWORD),
            anyMap()))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL,
            TestService.class,
            providers,
            interceptor,
            false,
            true,
            CONNECTION_TIMEOUT,
            RECEIVE_TIMEOUT,
            SOURCE_ID,
            DISCOVERY_URL,
            CLIENT_ID,
            CLIENT_SECRET,
            USERNAME,
            PASSWORD,
            additionalParameters);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactoryWithPropertyResolver() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean(),
            any(PropertyResolver.class)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, interceptor, false, true, propertyResolver);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactoryMinimal() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean()))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, interceptor, false, true);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactorySimpleAuth() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL), eq(TestService.class), eq(USERNAME), eq(PASSWORD)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, USERNAME, PASSWORD);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactorySimpleOAuth() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            eq(SOURCE_ID),
            eq(DISCOVERY_URL),
            eq(CLIENT_ID),
            eq(CLIENT_SECRET),
            eq(OAUTH_FLOW)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL,
            TestService.class,
            SOURCE_ID,
            DISCOVERY_URL,
            CLIENT_ID,
            CLIENT_SECRET,
            OAUTH_FLOW);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactorySimplest() {
    when(clientFactoryFactory.getSecureCxfClientFactory(eq(ENDPOINT_URL), eq(TestService.class)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(ENDPOINT_URL, TestService.class);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(secureCxfClientFactory)));
  }

  @Test
  public void testGetSecureCxfClientFactoryWithNullProviders() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            eq(null),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean()))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, null, interceptor, false, true);

    assertThat(result, is(notNullValue()));
    verify(clientFactoryFactory)
        .getSecureCxfClientFactory(ENDPOINT_URL, TestService.class, null, interceptor, false, true);
  }

  @Test
  public void testGetSecureCxfClientFactoryWithNullInterceptor() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            eq(null),
            anyBoolean(),
            anyBoolean()))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, null, false, true);

    assertThat(result, is(notNullValue()));
    verify(clientFactoryFactory)
        .getSecureCxfClientFactory(ENDPOINT_URL, TestService.class, providers, null, false, true);
  }

  @Test
  public void testGetSecureCxfClientFactoryWithDisableCnCheck() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            eq(true),
            anyBoolean()))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, interceptor, true, true);

    assertThat(result, is(notNullValue()));
    verify(clientFactoryFactory)
        .getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, interceptor, true, true);
  }

  @Test
  public void testGetSecureCxfClientFactoryWithoutRedirects() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            eq(false)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, interceptor, false, false);

    assertThat(result, is(notNullValue()));
    verify(clientFactoryFactory)
        .getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, interceptor, false, false);
  }

  @Test
  public void testGetSecureCxfClientFactoryWithNullTimeouts() {
    when(clientFactoryFactory.getSecureCxfClientFactory(
            eq(ENDPOINT_URL),
            eq(TestService.class),
            anyList(),
            any(Interceptor.class),
            anyBoolean(),
            anyBoolean(),
            eq(null),
            eq(null)))
        .thenReturn(secureCxfClientFactory);

    SecureCxfClientFactory<TestService> result =
        clientFactoryFactory.getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, interceptor, false, true, null, null);

    assertThat(result, is(notNullValue()));
    verify(clientFactoryFactory)
        .getSecureCxfClientFactory(
            ENDPOINT_URL, TestService.class, providers, interceptor, false, true, null, null);
  }

  @Test
  public void testClientFactoryFactoryInterface() {
    // Verify the interface contract exists and can be mocked
    assertThat(clientFactoryFactory, is(notNullValue()));
    assertThat(ClientFactoryFactory.class.isInterface(), is(true));
    assertThat(ClientFactoryFactory.class.isAnnotationPresent(Deprecated.class), is(true));
  }

  @Test
  public void testClientFactoryFactoryHasExpectedMethods() throws NoSuchMethodException {
    // Verify the interface has some expected overloaded methods
    assertThat(
        ClientFactoryFactory.class.getMethod(
            "getSecureCxfClientFactory", String.class, Class.class),
        is(notNullValue()));

    assertThat(
        ClientFactoryFactory.class.getMethod(
            "getSecureCxfClientFactory", String.class, Class.class, String.class, String.class),
        is(notNullValue()));
  }

  // Test interface to use as generic type parameter
  private interface TestService {
    String getData();
  }
}
