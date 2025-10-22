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
package ddf.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecurityConstantsTest {

  @Test
  public void testSecuritySubjectConstant() {
    assertThat(SecurityConstants.SECURITY_SUBJECT, is(notNullValue()));
    assertThat(SecurityConstants.SECURITY_SUBJECT, is("ddf.security.subject"));
  }

  @Test
  public void testSecurityTokenKeyConstant() {
    assertThat(SecurityConstants.SECURITY_TOKEN_KEY, is(notNullValue()));
    assertThat(SecurityConstants.SECURITY_TOKEN_KEY, is("ddf.security.securityToken"));
  }

  @Test
  public void testSamlAssertionConstant() {
    assertThat(SecurityConstants.SAML_ASSERTION, is(notNullValue()));
    assertThat(SecurityConstants.SAML_ASSERTION, is("ddf.security.assertion"));
  }

  @Test
  public void testGuestRealmNameConstant() {
    assertThat(SecurityConstants.GUEST_REALM_NAME, is(notNullValue()));
    assertThat(SecurityConstants.GUEST_REALM_NAME, is("guestRealmName"));
  }

  @Test
  public void testAllConstantsAreNonNull() {
    assertThat(SecurityConstants.SECURITY_SUBJECT, is(notNullValue()));
    assertThat(SecurityConstants.SECURITY_TOKEN_KEY, is(notNullValue()));
    assertThat(SecurityConstants.SAML_ASSERTION, is(notNullValue()));
    assertThat(SecurityConstants.GUEST_REALM_NAME, is(notNullValue()));
  }

  @Test
  public void testSecurityConstantsCanBeUsedAsKeys() {
    String key1 = SecurityConstants.SECURITY_SUBJECT;
    String key2 = SecurityConstants.SECURITY_TOKEN_KEY;
    String key3 = SecurityConstants.SAML_ASSERTION;
    String key4 = SecurityConstants.GUEST_REALM_NAME;

    assertThat(key1, is("ddf.security.subject"));
    assertThat(key2, is("ddf.security.securityToken"));
    assertThat(key3, is("ddf.security.assertion"));
    assertThat(key4, is("guestRealmName"));
  }
}
