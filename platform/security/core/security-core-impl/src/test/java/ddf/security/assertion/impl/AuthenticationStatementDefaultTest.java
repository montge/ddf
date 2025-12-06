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
package ddf.security.assertion.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class AuthenticationStatementDefaultTest {

  private static final String TEST_SESSION_INDEX = "session123";
  private static final String TEST_AUTHN_CONTEXT_CLASS_REF =
      "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";

  private AuthenticationStatementDefault authenticationStatement;

  @Before
  public void setUp() {
    authenticationStatement = new AuthenticationStatementDefault();
  }

  @Test
  public void testConstructor() {
    AuthenticationStatementDefault statement = new AuthenticationStatementDefault();
    assertThat(statement, is(notNullValue()));
  }

  @Test
  public void testGetSetAuthnInstant() {
    assertThat(authenticationStatement.getAuthnInstant(), is(nullValue()));

    DateTime now = DateTime.now();
    authenticationStatement.setAuthnInstant(now);

    assertThat(authenticationStatement.getAuthnInstant(), is(notNullValue()));
    assertThat(authenticationStatement.getAuthnInstant(), is(now));
  }

  @Test
  public void testGetSetSessionIndex() {
    assertThat(authenticationStatement.getSessionIndex(), is(nullValue()));

    authenticationStatement.setSessionIndex(TEST_SESSION_INDEX);

    assertThat(authenticationStatement.getSessionIndex(), is(notNullValue()));
    assertThat(authenticationStatement.getSessionIndex(), is(TEST_SESSION_INDEX));
  }

  @Test
  public void testGetSetSessionNotOnOrAfter() {
    assertThat(authenticationStatement.getSessionNotOnOrAfter(), is(nullValue()));

    DateTime expirationTime = DateTime.now().plusHours(1);
    authenticationStatement.setSessionNotOnOrAfter(expirationTime);

    assertThat(authenticationStatement.getSessionNotOnOrAfter(), is(notNullValue()));
    assertThat(authenticationStatement.getSessionNotOnOrAfter(), is(expirationTime));
  }

  @Test
  public void testGetSetAuthnContextClassRef() {
    assertThat(authenticationStatement.getAuthnContextClassRef(), is(nullValue()));

    authenticationStatement.setAuthnContextClassRef(TEST_AUTHN_CONTEXT_CLASS_REF);

    assertThat(authenticationStatement.getAuthnContextClassRef(), is(notNullValue()));
    assertThat(authenticationStatement.getAuthnContextClassRef(), is(TEST_AUTHN_CONTEXT_CLASS_REF));
  }

  @Test
  public void testSetNullValues() {
    DateTime now = DateTime.now();
    authenticationStatement.setAuthnInstant(now);
    authenticationStatement.setSessionIndex(TEST_SESSION_INDEX);
    authenticationStatement.setSessionNotOnOrAfter(now);
    authenticationStatement.setAuthnContextClassRef(TEST_AUTHN_CONTEXT_CLASS_REF);

    authenticationStatement.setAuthnInstant(null);
    authenticationStatement.setSessionIndex(null);
    authenticationStatement.setSessionNotOnOrAfter(null);
    authenticationStatement.setAuthnContextClassRef(null);

    assertThat(authenticationStatement.getAuthnInstant(), is(nullValue()));
    assertThat(authenticationStatement.getSessionIndex(), is(nullValue()));
    assertThat(authenticationStatement.getSessionNotOnOrAfter(), is(nullValue()));
    assertThat(authenticationStatement.getAuthnContextClassRef(), is(nullValue()));
  }

  @Test
  public void testAllFieldsSet() {
    DateTime authnInstant = DateTime.now();
    DateTime sessionExpiry = authnInstant.plusHours(2);

    authenticationStatement.setAuthnInstant(authnInstant);
    authenticationStatement.setSessionIndex(TEST_SESSION_INDEX);
    authenticationStatement.setSessionNotOnOrAfter(sessionExpiry);
    authenticationStatement.setAuthnContextClassRef(TEST_AUTHN_CONTEXT_CLASS_REF);

    assertThat(authenticationStatement.getAuthnInstant(), is(authnInstant));
    assertThat(authenticationStatement.getSessionIndex(), is(TEST_SESSION_INDEX));
    assertThat(authenticationStatement.getSessionNotOnOrAfter(), is(sessionExpiry));
    assertThat(authenticationStatement.getAuthnContextClassRef(), is(TEST_AUTHN_CONTEXT_CLASS_REF));
  }
}
