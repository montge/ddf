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
package ddf.security.assertion.saml.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.security.assertion.AuthenticationStatement;
import ddf.security.assertion.impl.AuthenticationStatementDefault;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class AuthenticationStatementSamlTest {

  @Test
  public void testCanInstantiate() {
    AuthenticationStatementSaml statement = new AuthenticationStatementSaml();

    assertThat(statement, is(notNullValue()));
  }

  @Test
  public void testExtendsAuthenticationStatementDefault() {
    AuthenticationStatementSaml statement = new AuthenticationStatementSaml();

    assertThat(statement, is(instanceOf(AuthenticationStatementDefault.class)));
  }

  @Test
  public void testImplementsAuthenticationStatementInterface() {
    AuthenticationStatementSaml statement = new AuthenticationStatementSaml();

    assertThat(statement, is(instanceOf(AuthenticationStatement.class)));
  }

  @Test
  public void testInheritedAuthnInstantProperty() {
    AuthenticationStatementSaml statement = new AuthenticationStatementSaml();
    DateTime instant = new DateTime();

    statement.setAuthnInstant(instant);

    assertThat(statement.getAuthnInstant(), is(instant));
  }

  @Test
  public void testInheritedSessionIndexProperty() {
    AuthenticationStatementSaml statement = new AuthenticationStatementSaml();

    statement.setSessionIndex("session123");

    assertThat(statement.getSessionIndex(), is("session123"));
  }

  @Test
  public void testInheritedSessionNotOnOrAfterProperty() {
    AuthenticationStatementSaml statement = new AuthenticationStatementSaml();
    DateTime notOnOrAfter = new DateTime().plusHours(1);

    statement.setSessionNotOnOrAfter(notOnOrAfter);

    assertThat(statement.getSessionNotOnOrAfter(), is(notOnOrAfter));
  }

  @Test
  public void testInheritedAuthnContextClassRefProperty() {
    AuthenticationStatementSaml statement = new AuthenticationStatementSaml();

    statement.setAuthnContextClassRef(
        "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

    assertThat(
        statement.getAuthnContextClassRef(),
        is("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"));
  }

  @Test
  public void testDefaultStateIsNull() {
    AuthenticationStatementSaml statement = new AuthenticationStatementSaml();

    assertThat(statement.getAuthnInstant(), is(nullValue()));
    assertThat(statement.getSessionIndex(), is(nullValue()));
    assertThat(statement.getSessionNotOnOrAfter(), is(nullValue()));
    assertThat(statement.getAuthnContextClassRef(), is(nullValue()));
  }
}
