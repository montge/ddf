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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import ddf.security.audit.SecurityLogger;
import ddf.security.service.SecurityManager;
import org.codice.ddf.cxf.client.ClientBuilder;
import org.codice.ddf.cxf.oauth.OAuthSecurity;
import org.codice.ddf.security.jaxrs.SamlSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientBuilderFactoryImplTest {

  @Mock private OAuthSecurity oauthSecurity;

  @Mock private SamlSecurity samlSecurity;

  @Mock private SecurityLogger securityLogger;

  @Mock private SecurityManager securityManager;

  private ClientBuilderFactoryImpl factory;

  @BeforeEach
  void setUp() {
    factory = new ClientBuilderFactoryImpl();
    factory.setOauthSecurity(oauthSecurity);
    factory.setSamlSecurity(samlSecurity);
    factory.setSecurityLogger(securityLogger);
    factory.setSecurityManager(securityManager);
  }

  @Test
  void testGetClientBuilderReturnsNonNull() {
    ClientBuilder<Object> clientBuilder = factory.getClientBuilder();

    assertThat(clientBuilder, is(notNullValue()));
  }

  @Test
  void testGetClientBuilderReturnsClientBuilderImplInstance() {
    ClientBuilder<Object> clientBuilder = factory.getClientBuilder();

    assertThat(clientBuilder, is(instanceOf(ClientBuilderImpl.class)));
  }

  @Test
  void testGetClientBuilderReturnsNewInstanceEachTime() {
    ClientBuilder<Object> firstBuilder = factory.getClientBuilder();
    ClientBuilder<Object> secondBuilder = factory.getClientBuilder();

    assertThat(firstBuilder, is(notNullValue()));
    assertThat(secondBuilder, is(notNullValue()));
    assertThat(firstBuilder == secondBuilder, is(false));
  }

  @Test
  void testSetOauthSecurity() {
    ClientBuilderFactoryImpl newFactory = new ClientBuilderFactoryImpl();
    OAuthSecurity mockOauth = mock(OAuthSecurity.class);

    newFactory.setOauthSecurity(mockOauth);
    newFactory.setSamlSecurity(samlSecurity);
    newFactory.setSecurityLogger(securityLogger);
    newFactory.setSecurityManager(securityManager);

    ClientBuilder<Object> builder = newFactory.getClientBuilder();
    assertThat(builder, is(notNullValue()));
  }

  @Test
  void testSetSamlSecurity() {
    ClientBuilderFactoryImpl newFactory = new ClientBuilderFactoryImpl();
    SamlSecurity mockSaml = mock(SamlSecurity.class);

    newFactory.setOauthSecurity(oauthSecurity);
    newFactory.setSamlSecurity(mockSaml);
    newFactory.setSecurityLogger(securityLogger);
    newFactory.setSecurityManager(securityManager);

    ClientBuilder<Object> builder = newFactory.getClientBuilder();
    assertThat(builder, is(notNullValue()));
  }

  @Test
  void testSetSecurityLogger() {
    ClientBuilderFactoryImpl newFactory = new ClientBuilderFactoryImpl();
    SecurityLogger mockLogger = mock(SecurityLogger.class);

    newFactory.setOauthSecurity(oauthSecurity);
    newFactory.setSamlSecurity(samlSecurity);
    newFactory.setSecurityLogger(mockLogger);
    newFactory.setSecurityManager(securityManager);

    ClientBuilder<Object> builder = newFactory.getClientBuilder();
    assertThat(builder, is(notNullValue()));
  }

  @Test
  void testSetSecurityManager() {
    ClientBuilderFactoryImpl newFactory = new ClientBuilderFactoryImpl();
    SecurityManager mockManager = mock(SecurityManager.class);

    newFactory.setOauthSecurity(oauthSecurity);
    newFactory.setSamlSecurity(samlSecurity);
    newFactory.setSecurityLogger(securityLogger);
    newFactory.setSecurityManager(mockManager);

    ClientBuilder<Object> builder = newFactory.getClientBuilder();
    assertThat(builder, is(notNullValue()));
  }

  @Test
  void testFactoryWithNullDependencies() {
    ClientBuilderFactoryImpl newFactory = new ClientBuilderFactoryImpl();
    // Not setting any dependencies

    ClientBuilder<Object> builder = newFactory.getClientBuilder();
    assertThat(builder, is(notNullValue()));
  }

  @Test
  void testGetClientBuilderWithTypedInterface() {
    ClientBuilder<String> stringBuilder = factory.getClientBuilder();
    ClientBuilder<Integer> intBuilder = factory.getClientBuilder();

    assertThat(stringBuilder, is(notNullValue()));
    assertThat(intBuilder, is(notNullValue()));
  }
}
