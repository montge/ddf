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
package org.codice.ddf.security.handler.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.logout.OidcLogoutActionBuilder;

@RunWith(MockitoJUnitRunner.class)
public class OidcHandlerConfigurationTest {

  @Mock private OidcConfiguration oidcConfiguration;

  @Mock private OidcClient<OidcConfiguration> oidcClient;

  @Mock private OidcLogoutActionBuilder oidcLogoutActionBuilder;

  private OidcHandlerConfiguration handlerConfiguration;

  @Before
  public void setUp() {
    handlerConfiguration =
        new OidcHandlerConfiguration() {
          @Override
          public OidcConfiguration getOidcConfiguration() {
            return oidcConfiguration;
          }

          @Override
          public OidcClient<OidcConfiguration> getOidcClient(String callBackUri) {
            return oidcClient;
          }

          @Override
          public OidcLogoutActionBuilder getOidcLogoutActionBuilder() {
            return oidcLogoutActionBuilder;
          }

          @Override
          public void testConnection() {
            // No-op for test
          }

          @Override
          public int getConnectTimeout() {
            return 5000;
          }

          @Override
          public int getReadTimeout() {
            return 10000;
          }
        };
  }

  @Test
  public void testGetOidcConfiguration() {
    OidcConfiguration result = handlerConfiguration.getOidcConfiguration();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(oidcConfiguration));
  }

  @Test
  public void testGetOidcClient() {
    String callbackUri = "https://localhost:8993/callback";
    OidcClient<OidcConfiguration> result = handlerConfiguration.getOidcClient(callbackUri);
    assertThat(result, is(notNullValue()));
    assertThat(result, is(oidcClient));
  }

  @Test
  public void testGetOidcLogoutActionBuilder() {
    OidcLogoutActionBuilder result = handlerConfiguration.getOidcLogoutActionBuilder();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(oidcLogoutActionBuilder));
  }

  @Test
  public void testTestConnection() {
    // Should not throw exception
    handlerConfiguration.testConnection();
  }

  @Test
  public void testGetConnectTimeout() {
    int timeout = handlerConfiguration.getConnectTimeout();
    assertThat(timeout, is(5000));
  }

  @Test
  public void testGetReadTimeout() {
    int timeout = handlerConfiguration.getReadTimeout();
    assertThat(timeout, is(10000));
  }

  @Test
  public void testGetOidcClientWithNullCallbackUri() {
    OidcClient<OidcConfiguration> result = handlerConfiguration.getOidcClient(null);
    assertThat(result, is(notNullValue()));
    assertThat(result, is(oidcClient));
  }

  @Test
  public void testGetOidcClientWithEmptyCallbackUri() {
    OidcClient<OidcConfiguration> result = handlerConfiguration.getOidcClient("");
    assertThat(result, is(notNullValue()));
    assertThat(result, is(oidcClient));
  }

  @Test
  public void testGetOidcClientWithMultipleCalls() {
    String callbackUri1 = "https://localhost:8993/callback1";
    String callbackUri2 = "https://localhost:8993/callback2";

    OidcClient<OidcConfiguration> result1 = handlerConfiguration.getOidcClient(callbackUri1);
    OidcClient<OidcConfiguration> result2 = handlerConfiguration.getOidcClient(callbackUri2);

    assertThat(result1, is(notNullValue()));
    assertThat(result2, is(notNullValue()));
  }
}
