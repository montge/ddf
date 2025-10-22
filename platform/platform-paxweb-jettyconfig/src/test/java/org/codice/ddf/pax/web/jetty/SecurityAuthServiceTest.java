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
package org.codice.ddf.pax.web.jetty;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.eclipse.jetty.security.Authenticator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecurityAuthServiceTest {

  private SecurityAuthService securityAuthService;

  @Before
  public void setup() {
    securityAuthService = new SecurityAuthService();
  }

  @Test
  public void testGetAuthenticatorServiceWithDdfAuthMethod() {
    Authenticator authenticator =
        securityAuthService.getAuthenticatorService(
            JettyAuthenticator.DDF_AUTH_METHOD, Authenticator.class);

    assertThat(authenticator, notNullValue());
    assertThat(authenticator, instanceOf(JettyAuthenticator.class));
  }

  @Test
  public void testGetAuthenticatorServiceWithInvalidMethod() {
    Authenticator authenticator =
        securityAuthService.getAuthenticatorService("INVALID_METHOD", Authenticator.class);

    assertThat(authenticator, nullValue());
  }

  @Test
  public void testGetAuthenticatorServiceWithNullMethod() {
    Authenticator authenticator =
        securityAuthService.getAuthenticatorService(null, Authenticator.class);

    assertThat(authenticator, nullValue());
  }

  @Test
  public void testGetAuthenticatorServiceWithWrongInterface() {
    String result =
        securityAuthService.getAuthenticatorService(
            JettyAuthenticator.DDF_AUTH_METHOD, String.class);

    assertThat(result, nullValue());
  }

  @Test
  public void testGetAuthenticatorServiceWithDifferentAuthMethod() {
    Authenticator authenticator =
        securityAuthService.getAuthenticatorService("BASIC", Authenticator.class);

    assertThat(authenticator, nullValue());
  }

  @Test
  public void testGetAuthenticatorServiceReturnsSameType() {
    Object result =
        securityAuthService.getAuthenticatorService(
            JettyAuthenticator.DDF_AUTH_METHOD, Authenticator.class);

    assertThat(result, instanceOf(Authenticator.class));
  }

  @Test
  public void testGetAuthenticatorServiceReturnsNewInstance() {
    Authenticator authenticator1 =
        securityAuthService.getAuthenticatorService(
            JettyAuthenticator.DDF_AUTH_METHOD, Authenticator.class);
    Authenticator authenticator2 =
        securityAuthService.getAuthenticatorService(
            JettyAuthenticator.DDF_AUTH_METHOD, Authenticator.class);

    // Each call should return a new instance
    assertThat(authenticator1, notNullValue());
    assertThat(authenticator2, notNullValue());
    // They should not be the same instance
    assertThat(authenticator1 == authenticator2, is(false));
  }

  @Test
  public void testGetAuthenticatorServiceWithEmptyMethod() {
    Authenticator authenticator =
        securityAuthService.getAuthenticatorService("", Authenticator.class);

    assertThat(authenticator, nullValue());
  }

  @Test
  public void testGetAuthenticatorServiceCaseSensitivity() {
    // Auth method is case-sensitive
    Authenticator authenticator =
        securityAuthService.getAuthenticatorService(
            JettyAuthenticator.DDF_AUTH_METHOD.toLowerCase(), Authenticator.class);

    assertThat(authenticator, nullValue());
  }

  @Test
  public void testGetAuthenticatorServiceWithNullInterface() {
    Object result =
        securityAuthService.getAuthenticatorService(JettyAuthenticator.DDF_AUTH_METHOD, null);

    // Should handle null interface gracefully
    assertThat(result, nullValue());
  }
}
