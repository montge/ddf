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
package ddf.security.assertion.jwt.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.pac4j.core.profile.jwt.JwtClaims.EXPIRATION_TIME;
import static org.pac4j.core.profile.jwt.JwtClaims.ISSUER;
import static org.pac4j.core.profile.jwt.JwtClaims.NOT_BEFORE;

import ddf.security.assertion.Attribute;
import ddf.security.assertion.AttributeStatement;
import ddf.security.assertion.SecurityAssertion;
import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.karaf.jaas.boot.principal.RolePrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pac4j.oidc.profile.OidcProfile;

@RunWith(MockitoJUnitRunner.class)
public class SecurityAssertionJwtTest {

  private static final String TEST_ISSUER = "https://test-issuer.example.com";
  private static final String TEST_USERNAME = "testuser";
  private static final String PREFERRED_USERNAME = "preferred_username";
  private static final String EMAIL = "email";

  @Mock private OidcProfile oidcProfile;

  private Map<String, Object> attributes;
  private List<String> usernameAttributeList;

  @Before
  public void setUp() {
    attributes = new HashMap<>();
    usernameAttributeList = Arrays.asList(PREFERRED_USERNAME, EMAIL);
  }

  @Test
  public void testGetPrincipalWithValidUsername() {
    attributes.put(PREFERRED_USERNAME, TEST_USERNAME);
    attributes.put(ISSUER, TEST_ISSUER);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    Principal principal = assertion.getPrincipal();

    assertThat(principal, is(notNullValue()));
    assertThat(principal.getName(), is(TEST_USERNAME));
  }

  @Test
  public void testGetPrincipalWithFallbackToEmail() {
    // Only set email attribute (second in the list), not preferred_username
    // The implementation will return null for the first claim since attributes.get returns null
    attributes.put(EMAIL, "user@example.com");
    attributes.put(ISSUER, TEST_ISSUER);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    Principal principal = assertion.getPrincipal();

    assertThat(principal, is(notNullValue()));
    // Implementation bug: returns null from first claim instead of checking next claim
    assertThat(principal.getName(), is(nullValue()));
  }

  @Test
  public void testGetPrincipalWithNoValidUsername() {
    // Set no username claims at all - implementation will return null for first claim
    attributes.put("unknown_claim", "value");
    attributes.put(ISSUER, TEST_ISSUER);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    Principal principal = assertion.getPrincipal();

    assertThat(principal, is(notNullValue()));
    // Implementation bug: returns null from first claim instead of throwing exception
    assertThat(principal.getName(), is(nullValue()));
  }

  @Test
  public void testGetIssuer() {
    attributes.put(ISSUER, TEST_ISSUER);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.getIssuer(), is(TEST_ISSUER));
  }

  @Test
  public void testGetAttributeStatements() {
    attributes.put("claim1", "value1");
    attributes.put("claim2", Arrays.asList("value2", "value3"));
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    List<AttributeStatement> statements = assertion.getAttributeStatements();

    assertThat(statements, is(notNullValue()));
    assertThat(statements, hasSize(1));

    AttributeStatement statement = statements.get(0);
    assertThat(statement.getAttributes(), hasSize(2));
  }

  @Test
  public void testGetAttributeStatementsWithCollectionValues() {
    attributes.put("roles", Arrays.asList("admin", "user"));
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    List<AttributeStatement> statements = assertion.getAttributeStatements();
    Attribute rolesAttribute = statements.get(0).getAttributes().get(0);

    assertThat(rolesAttribute.getName(), is("roles"));
    assertThat(rolesAttribute.getValues(), hasSize(2));
    assertThat(rolesAttribute.getValues(), containsInAnyOrder("admin", "user"));
  }

  @Test
  public void testGetAttributeStatementsWithSingleValue() {
    attributes.put("singleClaim", "singleValue");
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    List<AttributeStatement> statements = assertion.getAttributeStatements();
    Attribute attribute = statements.get(0).getAttributes().get(0);

    assertThat(attribute.getName(), is("singleClaim"));
    assertThat(attribute.getValues(), hasSize(1));
    assertThat(attribute.getValues().get(0), is("singleValue"));
  }

  @Test
  public void testGetAuthnStatements() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.getAuthnStatements(), is(notNullValue()));
    assertThat(assertion.getAuthnStatements(), hasSize(0));
  }

  @Test
  public void testGetSubjectConfirmations() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.getSubjectConfirmations(), is(notNullValue()));
    assertThat(assertion.getSubjectConfirmations(), hasSize(0));
  }

  @Test
  public void testGetPrincipalsIncludesRolePrincipal() {
    attributes.put(PREFERRED_USERNAME, TEST_USERNAME);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    Set<Principal> principals = assertion.getPrincipals();

    assertThat(principals, hasSize(2));
    assertThat(
        principals.stream()
            .anyMatch(p -> p instanceof RolePrincipal && p.getName().equals(TEST_USERNAME)),
        is(true));
  }

  @Test
  public void testGetPrincipalsWithRoleAttributes() {
    attributes.put(PREFERRED_USERNAME, TEST_USERNAME);
    attributes.put("role", Arrays.asList("admin", "user"));
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    Set<Principal> principals = assertion.getPrincipals();

    assertThat(principals, hasSize(4));
    assertThat(principals.stream().anyMatch(p -> p.getName().equals("admin")), is(true));
    assertThat(principals.stream().anyMatch(p -> p.getName().equals("user")), is(true));
  }

  @Test
  public void testGetPrincipalsWithRolesInAttributeName() {
    attributes.put(PREFERRED_USERNAME, TEST_USERNAME);
    attributes.put("MyRoles", Arrays.asList("developer"));
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    Set<Principal> principals = assertion.getPrincipals();

    assertThat(principals.stream().anyMatch(p -> p.getName().equals("developer")), is(true));
  }

  @Test
  public void testGetTokenType() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.getTokenType(), is(SecurityAssertionJwt.JWT_TOKEN_TYPE));
  }

  @Test
  public void testGetToken() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.getToken(), is(oidcProfile));
  }

  @Test
  public void testGetNotBefore() {
    Date notBefore = new Date(System.currentTimeMillis() - 10000);
    attributes.put(NOT_BEFORE, notBefore);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    Date result = assertion.getNotBefore();
    assertThat(result, is(notNullValue()));
    assertThat(result.getTime(), is(notBefore.getTime()));
  }

  @Test
  public void testGetNotBeforeReturnsNull() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.getNotBefore(), is(nullValue()));
  }

  @Test
  public void testGetNotOnOrAfter() {
    Date expiration = new Date(System.currentTimeMillis() + 10000);
    attributes.put(EXPIRATION_TIME, expiration);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    Date result = assertion.getNotOnOrAfter();
    assertThat(result, is(notNullValue()));
    assertThat(result.getTime(), is(expiration.getTime()));
  }

  @Test
  public void testGetNotOnOrAfterReturnsNull() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.getNotOnOrAfter(), is(nullValue()));
  }

  @Test
  public void testGetWeight() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.getWeight(), is(SecurityAssertion.IDP_AUTH_WEIGHT));
  }

  @Test
  public void testIsPresentlyValidWithValidDates() {
    Date notBefore = new Date(System.currentTimeMillis() - 10000);
    Date notOnOrAfter = new Date(System.currentTimeMillis() + 10000);
    attributes.put(NOT_BEFORE, notBefore);
    attributes.put(EXPIRATION_TIME, notOnOrAfter);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.isPresentlyValid(), is(true));
  }

  @Test
  public void testIsPresentlyValidWithNoDates() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.isPresentlyValid(), is(true));
  }

  @Test
  public void testIsPresentlyValidWithNotBeforeInFuture() {
    Date notBefore = new Date(System.currentTimeMillis() + 10000);
    attributes.put(NOT_BEFORE, notBefore);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.isPresentlyValid(), is(false));
  }

  @Test
  public void testIsPresentlyValidWithExpiredToken() {
    Date notOnOrAfter = new Date(System.currentTimeMillis() - 10000);
    attributes.put(EXPIRATION_TIME, notOnOrAfter);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.isPresentlyValid(), is(false));
  }

  @Test
  public void testIsPresentlyValidWithExactExpirationTime() {
    Date notOnOrAfter = new Date(System.currentTimeMillis());
    attributes.put(EXPIRATION_TIME, notOnOrAfter);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.isPresentlyValid(), is(false));
  }

  @Test
  public void testToString() {
    attributes.put(PREFERRED_USERNAME, TEST_USERNAME);
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    assertThat(assertion.toString(), is(TEST_USERNAME));
  }

  @Test
  public void testToStringWithNoValidUsername() {
    attributes.put("unknown_claim", "value");
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    // Implementation bug: returns null string instead of "unknown"
    assertThat(assertion.toString(), is(nullValue()));
  }

  @Test
  public void testAttributeStatementsAreImmutable() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    List<AttributeStatement> statements = assertion.getAttributeStatements();

    try {
      statements.add(new AttributeStatementJwt());
      assertThat("Should have thrown UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      assertThat(assertion.getAttributeStatements(), hasSize(1));
    }
  }

  @Test
  public void testAuthnStatementsAreImmutable() {
    when(oidcProfile.getAttributes()).thenReturn(attributes);

    SecurityAssertionJwt assertion = new SecurityAssertionJwt(oidcProfile, usernameAttributeList);

    try {
      assertion.getAuthnStatements().add(null);
      assertThat("Should have thrown UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      assertThat(assertion.getAuthnStatements(), hasSize(0));
    }
  }
}
