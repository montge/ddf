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
package org.codice.ddf.security.handler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class AuthenticationTokenTypeTest {

  @Test
  void testSamlValue() {
    assertThat(AuthenticationTokenType.SAML, is(notNullValue()));
    assertThat(AuthenticationTokenType.SAML.name(), is("SAML"));
  }

  @Test
  void testOidcValue() {
    assertThat(AuthenticationTokenType.OIDC, is(notNullValue()));
    assertThat(AuthenticationTokenType.OIDC.name(), is("OIDC"));
  }

  @Test
  void testPkiValue() {
    assertThat(AuthenticationTokenType.PKI, is(notNullValue()));
    assertThat(AuthenticationTokenType.PKI.name(), is("PKI"));
  }

  @Test
  void testUsernameValue() {
    assertThat(AuthenticationTokenType.USERNAME, is(notNullValue()));
    assertThat(AuthenticationTokenType.USERNAME.name(), is("USERNAME"));
  }

  @Test
  void testEnumValueCount() {
    assertThat(AuthenticationTokenType.values().length, is(4));
  }

  @Test
  void testEnumValuesContainsAll() {
    assertThat(
        AuthenticationTokenType.values(),
        arrayContainingInAnyOrder(
            AuthenticationTokenType.SAML,
            AuthenticationTokenType.OIDC,
            AuthenticationTokenType.PKI,
            AuthenticationTokenType.USERNAME));
  }

  @Test
  void testValueOfSaml() {
    assertThat(AuthenticationTokenType.valueOf("SAML"), is(AuthenticationTokenType.SAML));
  }

  @Test
  void testValueOfOidc() {
    assertThat(AuthenticationTokenType.valueOf("OIDC"), is(AuthenticationTokenType.OIDC));
  }

  @Test
  void testValueOfPki() {
    assertThat(AuthenticationTokenType.valueOf("PKI"), is(AuthenticationTokenType.PKI));
  }

  @Test
  void testValueOfUsername() {
    assertThat(AuthenticationTokenType.valueOf("USERNAME"), is(AuthenticationTokenType.USERNAME));
  }
}
