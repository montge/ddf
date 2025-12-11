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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import ddf.security.assertion.AttributeStatement;
import ddf.security.assertion.AuthenticationStatement;
import ddf.security.assertion.SecurityAssertion;
import java.security.Principal;
import java.util.Date;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SecurityAssertionDefaultTest {

  private SecurityAssertionDefault assertion;
  private Principal testPrincipal;

  @BeforeEach
  public void setUp() {
    assertion = new SecurityAssertionDefault();
    testPrincipal = () -> "testUser";
  }

  @Test
  public void testGetPrincipal() {
    assertion.userPrincipal = testPrincipal;

    assertThat(assertion.getPrincipal(), is(testPrincipal));
    assertThat(assertion.getPrincipal().getName(), is("testUser"));
  }

  @Test
  public void testGetIssuer() {
    assertion.issuer = "https://test-issuer.example.com";

    assertThat(assertion.getIssuer(), is("https://test-issuer.example.com"));
  }

  @Test
  public void testGetAttributeStatements() {
    AttributeStatement statement1 = mock(AttributeStatement.class);
    AttributeStatement statement2 = mock(AttributeStatement.class);

    assertion.attributeStatements.add(statement1);
    assertion.attributeStatements.add(statement2);

    assertThat(assertion.getAttributeStatements(), hasSize(2));
    assertThat(assertion.getAttributeStatements(), containsInAnyOrder(statement1, statement2));
  }

  @Test
  public void testGetAuthnStatements() {
    AuthenticationStatement statement1 = mock(AuthenticationStatement.class);
    AuthenticationStatement statement2 = mock(AuthenticationStatement.class);

    assertion.authenticationStatements.add(statement1);
    assertion.authenticationStatements.add(statement2);

    assertThat(assertion.getAuthnStatements(), hasSize(2));
    assertThat(assertion.getAuthnStatements(), containsInAnyOrder(statement1, statement2));
  }

  @Test
  public void testGetSubjectConfirmations() {
    assertion.subjectConfirmations.add("urn:oasis:names:tc:SAML:2.0:cm:bearer");
    assertion.subjectConfirmations.add("urn:oasis:names:tc:SAML:2.0:cm:holder-of-key");

    assertThat(assertion.getSubjectConfirmations(), hasSize(2));
    assertThat(
        assertion.getSubjectConfirmations(),
        containsInAnyOrder(
            "urn:oasis:names:tc:SAML:2.0:cm:bearer",
            "urn:oasis:names:tc:SAML:2.0:cm:holder-of-key"));
  }

  @Test
  public void testGetPrincipals() {
    assertion.userPrincipal = testPrincipal;

    Principal principal1 = () -> "principal1";
    Principal principal2 = () -> "principal2";

    assertion.principals.add(principal1);
    assertion.principals.add(principal2);

    Set<Principal> result = assertion.getPrincipals();

    assertThat(result, hasSize(3));
    assertThat(result, containsInAnyOrder(testPrincipal, principal1, principal2));
  }

  @Test
  public void testGetPrincipalsWithOnlyUserPrincipal() {
    assertion.userPrincipal = testPrincipal;

    Set<Principal> result = assertion.getPrincipals();

    assertThat(result, hasSize(1));
    assertThat(result.contains(testPrincipal), is(true));
  }

  @Test
  public void testGetPrincipalsReturnsNewSet() {
    assertion.userPrincipal = testPrincipal;
    Principal principal1 = () -> "principal1";
    assertion.principals.add(principal1);

    Set<Principal> result1 = assertion.getPrincipals();
    Set<Principal> result2 = assertion.getPrincipals();

    assertThat(result1 == result2, is(false));
    assertThat(result1, hasSize(2));
    assertThat(result2, hasSize(2));
  }

  @Test
  public void testGetTokenType() {
    assertion.tokenType = "SAML";

    assertThat(assertion.getTokenType(), is("SAML"));
  }

  @Test
  public void testGetToken() {
    Object token = new Object();
    assertion.token = token;

    assertThat(assertion.getToken(), is(token));
  }

  @Test
  public void testGetNotBefore() {
    Date notBefore = new Date();
    assertion.notBefore = notBefore;

    assertThat(assertion.getNotBefore(), is(notBefore));
  }

  @Test
  public void testGetNotOnOrAfter() {
    Date notOnOrAfter = new Date();
    assertion.notOnOrAfter = notOnOrAfter;

    assertThat(assertion.getNotOnOrAfter(), is(notOnOrAfter));
  }

  @Test
  public void testGetWeightDefault() {
    assertThat(assertion.getWeight(), is(SecurityAssertion.NO_AUTH_WEIGHT));
  }

  @Test
  public void testGetWeightCustom() {
    assertion.weight = SecurityAssertion.IDP_AUTH_WEIGHT;

    assertThat(assertion.getWeight(), is(SecurityAssertion.IDP_AUTH_WEIGHT));
  }

  @Test
  public void testIsPresentlyValidWithNoDates() {
    assertThat(assertion.isPresentlyValid(), is(true));
  }

  @Test
  public void testIsPresentlyValidWithValidDates() {
    assertion.notBefore = new Date(System.currentTimeMillis() - 10000);
    assertion.notOnOrAfter = new Date(System.currentTimeMillis() + 10000);

    assertThat(assertion.isPresentlyValid(), is(true));
  }

  @Test
  public void testIsPresentlyValidWithOnlyNotBefore() {
    assertion.notBefore = new Date(System.currentTimeMillis() - 10000);

    assertThat(assertion.isPresentlyValid(), is(true));
  }

  @Test
  public void testIsPresentlyValidWithOnlyNotOnOrAfter() {
    assertion.notOnOrAfter = new Date(System.currentTimeMillis() + 10000);

    assertThat(assertion.isPresentlyValid(), is(true));
  }

  @Test
  public void testIsPresentlyValidWithNotBeforeInFuture() {
    assertion.notBefore = new Date(System.currentTimeMillis() + 10000);

    assertThat(assertion.isPresentlyValid(), is(false));
  }

  @Test
  public void testIsPresentlyValidWithExpiredToken() {
    assertion.notOnOrAfter = new Date(System.currentTimeMillis() - 10000);

    assertThat(assertion.isPresentlyValid(), is(false));
  }

  @Test
  public void testIsPresentlyValidWithExactExpirationTime() {
    assertion.notOnOrAfter = new Date(System.currentTimeMillis());

    assertThat(assertion.isPresentlyValid(), is(false));
  }

  @Test
  public void testIsPresentlyValidWithNotBeforeAndExpired() {
    assertion.notBefore = new Date(System.currentTimeMillis() - 20000);
    assertion.notOnOrAfter = new Date(System.currentTimeMillis() - 10000);

    assertThat(assertion.isPresentlyValid(), is(false));
  }

  @Test
  public void testIsPresentlyValidWithNotBeforeInFutureAndValidExpiration() {
    assertion.notBefore = new Date(System.currentTimeMillis() + 10000);
    assertion.notOnOrAfter = new Date(System.currentTimeMillis() + 20000);

    assertThat(assertion.isPresentlyValid(), is(false));
  }

  @Test
  public void testToString() {
    assertion.userPrincipal = testPrincipal;

    assertThat(assertion.toString(), is("testUser"));
  }

  @Test
  public void testDefaultConstructor() {
    SecurityAssertionDefault newAssertion = new SecurityAssertionDefault();

    assertThat(newAssertion.getPrincipal(), is(nullValue()));
    assertThat(newAssertion.getIssuer(), is(nullValue()));
    assertThat(newAssertion.getAttributeStatements(), is(notNullValue()));
    assertThat(newAssertion.getAttributeStatements(), hasSize(0));
    assertThat(newAssertion.getAuthnStatements(), is(notNullValue()));
    assertThat(newAssertion.getAuthnStatements(), hasSize(0));
    assertThat(newAssertion.getSubjectConfirmations(), is(notNullValue()));
    assertThat(newAssertion.getSubjectConfirmations(), hasSize(0));
    assertThat(newAssertion.getTokenType(), is(nullValue()));
    assertThat(newAssertion.getToken(), is(nullValue()));
    assertThat(newAssertion.getNotBefore(), is(nullValue()));
    assertThat(newAssertion.getNotOnOrAfter(), is(nullValue()));
    assertThat(newAssertion.getWeight(), is(SecurityAssertion.NO_AUTH_WEIGHT));
  }

  @Test
  public void testGetPrincipalsModificationDoesNotAffectOriginal() {
    assertion.userPrincipal = testPrincipal;
    Principal principal1 = () -> "principal1";
    assertion.principals.add(principal1);

    Set<Principal> result = assertion.getPrincipals();
    result.add(() -> "newPrincipal");

    assertThat(assertion.principals, hasSize(1));
    assertThat(assertion.principals.contains(principal1), is(true));
  }

  @Test
  public void testIsPresentlyValidAtBoundaryConditions() {
    long currentTime = System.currentTimeMillis();

    assertion.notBefore = new Date(currentTime - 1);
    assertion.notOnOrAfter = new Date(currentTime + 1);

    assertThat(assertion.isPresentlyValid(), is(true));
  }

  @Test
  public void testEmptyAttributeStatements() {
    assertThat(assertion.getAttributeStatements(), is(notNullValue()));
    assertThat(assertion.getAttributeStatements(), hasSize(0));
  }

  @Test
  public void testEmptyAuthenticationStatements() {
    assertThat(assertion.getAuthnStatements(), is(notNullValue()));
    assertThat(assertion.getAuthnStatements(), hasSize(0));
  }

  @Test
  public void testEmptySubjectConfirmations() {
    assertThat(assertion.getSubjectConfirmations(), is(notNullValue()));
    assertThat(assertion.getSubjectConfirmations(), hasSize(0));
  }

  @Test
  public void testMultipleSubjectConfirmations() {
    assertion.subjectConfirmations.add("confirmation1");
    assertion.subjectConfirmations.add("confirmation2");
    assertion.subjectConfirmations.add("confirmation3");

    assertThat(assertion.getSubjectConfirmations(), hasSize(3));
  }
}
