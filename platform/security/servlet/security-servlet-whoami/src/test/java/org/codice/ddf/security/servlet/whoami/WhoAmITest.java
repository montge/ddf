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
package org.codice.ddf.security.servlet.whoami;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.SubjectOperations;
import ddf.security.assertion.Attribute;
import ddf.security.assertion.AttributeStatement;
import ddf.security.assertion.AuthenticationStatement;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.principal.impl.GuestPrincipal;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link WhoAmI} class. */
@RunWith(MockitoJUnitRunner.class)
public class WhoAmITest {

  private static final String TEST_NAME = "testuser";
  private static final String TEST_DISPLAY_NAME = "Test User";
  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_ISSUER = "https://idp.example.com";
  private static final String TEST_AUTHN_CONTEXT =
      "urn:oasis:names:tc:SAML:2.0:ac:classes:Password";

  @Mock private Subject subject;

  @Mock private SubjectOperations subjectOperations;

  @Mock private PrincipalCollection principals;

  @Mock private SecurityAssertion securityAssertion;

  @Mock private SecurityManager securityManager;

  @Before
  public void setUp() {
    ThreadContext.bind(securityManager);
  }

  @After
  public void tearDown() {
    ThreadContext.unbindSecurityManager();
    ThreadContext.unbindSubject();
  }

  @Test
  public void testConstructorWithNullSubjectThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new WhoAmI(null, subjectOperations);
        });
  }

  @Test
  public void testConstructorWithNullPrincipalsThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          when(subject.getPrincipals()).thenReturn(null);
          new WhoAmI(subject, subjectOperations);
        });
  }

  @Test
  public void testConstructorWithEmptyAssertionsThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          when(subject.getPrincipals()).thenReturn(principals);
          when(principals.byType(SecurityAssertion.class)).thenReturn(Collections.emptySet());
          new WhoAmI(subject, subjectOperations);
        });
  }

  @Test
  public void testConstructorWithValidSubjectCreatesWhoAmI() {
    setupValidSubject();

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI, is(notNullValue()));
    assertThat(whoAmI.whoAmISubjects, hasSize(1));
  }

  @Test
  public void testWhoAmISubjectGetName() {
    setupValidSubject();
    when(subjectOperations.getName(subject)).thenReturn(TEST_NAME);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getName(), is(TEST_NAME));
  }

  @Test
  public void testWhoAmISubjectGetDisplayName() {
    setupValidSubject();
    when(subjectOperations.getName(subject, null, true)).thenReturn(TEST_DISPLAY_NAME);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getDisplayName(), is(TEST_DISPLAY_NAME));
  }

  @Test
  public void testWhoAmISubjectGetEmail() {
    setupValidSubject();
    when(subjectOperations.getEmailAddress(subject)).thenReturn(TEST_EMAIL);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getEmail(), is(TEST_EMAIL));
  }

  @Test
  public void testWhoAmISubjectGetIssuer() {
    setupValidSubject();
    when(securityAssertion.getIssuer()).thenReturn(TEST_ISSUER);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getIssuer(), is(TEST_ISSUER));
  }

  @Test
  public void testWhoAmISubjectIsGuestWithGuestPrincipal() {
    setupValidSubject();
    GuestPrincipal guestPrincipal = new GuestPrincipal("127.0.0.1");
    when(securityAssertion.getPrincipal()).thenReturn(guestPrincipal);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).isGuest(), is(true));
  }

  @Test
  public void testWhoAmISubjectIsGuestWithNonGuestPrincipal() {
    setupValidSubject();
    Principal regularPrincipal = mock(Principal.class);
    when(securityAssertion.getPrincipal()).thenReturn(regularPrincipal);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).isGuest(), is(false));
  }

  @Test
  public void testWhoAmISubjectGetNotOnOrAfter() {
    setupValidSubject();
    Date notOnOrAfter = new Date(System.currentTimeMillis() + 3600000);
    when(securityAssertion.getNotOnOrAfter()).thenReturn(notOnOrAfter);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getNotOnOrAfter(), is(notOnOrAfter));
  }

  @Test
  public void testWhoAmISubjectGetNotBefore() {
    setupValidSubject();
    Date notBefore = new Date(System.currentTimeMillis() - 3600000);
    when(securityAssertion.getNotBefore()).thenReturn(notBefore);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getNotBefore(), is(notBefore));
  }

  @Test
  public void testWhoAmISubjectGetExpiresInWithFutureExpiration() {
    setupValidSubject();
    Date notOnOrAfter = new Date(System.currentTimeMillis() + 3600000);
    when(securityAssertion.getNotOnOrAfter()).thenReturn(notOnOrAfter);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    String expiresIn = whoAmI.whoAmISubjects.get(0).getExpiresIn();
    assertThat(expiresIn, is(notNullValue()));
  }

  @Test
  public void testWhoAmISubjectGetExpiresInWithNullExpiration() {
    setupValidSubject();
    when(securityAssertion.getNotOnOrAfter()).thenReturn(null);

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getExpiresIn(), is(nullValue()));
  }

  @Test
  public void testWhoAmISubjectGetAuthnContextClasses() {
    setupValidSubject();
    AuthenticationStatement authnStatement = mock(AuthenticationStatement.class);
    when(authnStatement.getAuthnContextClassRef()).thenReturn(TEST_AUTHN_CONTEXT);
    when(securityAssertion.getAuthnStatements())
        .thenReturn(Collections.singletonList(authnStatement));

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    List<String> authnContextClasses = whoAmI.whoAmISubjects.get(0).getAuthnContextClasses();
    assertThat(authnContextClasses, hasSize(1));
    assertThat(authnContextClasses.get(0), is(TEST_AUTHN_CONTEXT));
  }

  @Test
  public void testWhoAmISubjectGetAuthnContextClassesWithEmptyStatements() {
    setupValidSubject();
    when(securityAssertion.getAuthnStatements()).thenReturn(Collections.emptyList());

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getAuthnContextClasses(), is(empty()));
  }

  @Test
  public void testWhoAmISubjectGetAuthnContextClassesFiltersNullStatements() {
    setupValidSubject();
    AuthenticationStatement authnStatement = mock(AuthenticationStatement.class);
    when(authnStatement.getAuthnContextClassRef()).thenReturn(TEST_AUTHN_CONTEXT);
    when(securityAssertion.getAuthnStatements()).thenReturn(Arrays.asList(null, authnStatement));

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    List<String> authnContextClasses = whoAmI.whoAmISubjects.get(0).getAuthnContextClasses();
    assertThat(authnContextClasses, hasSize(1));
    assertThat(authnContextClasses.get(0), is(TEST_AUTHN_CONTEXT));
  }

  @Test
  public void testWhoAmISubjectGetClaims() {
    setupValidSubject();
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn("role");
    when(attribute.getValues()).thenReturn(Arrays.asList("admin", "user"));

    AttributeStatement attributeStatement = mock(AttributeStatement.class);
    when(attributeStatement.getAttributes()).thenReturn(Collections.singletonList(attribute));

    when(securityAssertion.getAttributeStatements())
        .thenReturn(Collections.singletonList(attributeStatement));

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    Map<String, SortedSet<String>> claims = whoAmI.whoAmISubjects.get(0).getClaims();
    assertThat(claims, hasEntry(is("role"), containsInAnyOrder("admin", "user")));
  }

  @Test
  public void testWhoAmISubjectGetClaimsWithEmptyAttributeStatements() {
    setupValidSubject();
    when(securityAssertion.getAttributeStatements()).thenReturn(Collections.emptyList());

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects.get(0).getClaims().isEmpty(), is(true));
  }

  @Test
  public void testWhoAmISubjectGetClaimsFiltersNullValues() {
    setupValidSubject();
    Attribute attributeWithNull = mock(Attribute.class);
    // getName() is not stubbed because the filter checks values first and excludes this attribute
    when(attributeWithNull.getValues()).thenReturn(Arrays.asList("value", null));

    Attribute attributeWithoutNull = mock(Attribute.class);
    when(attributeWithoutNull.getName()).thenReturn("validAttr");
    when(attributeWithoutNull.getValues()).thenReturn(Arrays.asList("value1", "value2"));

    AttributeStatement attributeStatement = mock(AttributeStatement.class);
    when(attributeStatement.getAttributes())
        .thenReturn(Arrays.asList(attributeWithNull, attributeWithoutNull));

    when(securityAssertion.getAttributeStatements())
        .thenReturn(Collections.singletonList(attributeStatement));

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    Map<String, SortedSet<String>> claims = whoAmI.whoAmISubjects.get(0).getClaims();
    assertThat(claims.size(), is(1));
    assertThat(claims.containsKey("validAttr"), is(true));
  }

  @Test
  public void testWhoAmISubjectGetClaimsMergesDuplicateAttributeNames() {
    setupValidSubject();
    Attribute attribute1 = mock(Attribute.class);
    when(attribute1.getName()).thenReturn("role");
    when(attribute1.getValues()).thenReturn(Collections.singletonList("admin"));

    Attribute attribute2 = mock(Attribute.class);
    when(attribute2.getName()).thenReturn("role");
    when(attribute2.getValues()).thenReturn(Collections.singletonList("user"));

    AttributeStatement attributeStatement = mock(AttributeStatement.class);
    when(attributeStatement.getAttributes()).thenReturn(Arrays.asList(attribute1, attribute2));

    when(securityAssertion.getAttributeStatements())
        .thenReturn(Collections.singletonList(attributeStatement));

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    Map<String, SortedSet<String>> claims = whoAmI.whoAmISubjects.get(0).getClaims();
    assertThat(claims.get("role"), containsInAnyOrder("admin", "user"));
  }

  @Test
  public void testMultipleSecurityAssertions() {
    when(subject.getPrincipals()).thenReturn(principals);

    SecurityAssertion assertion1 = mock(SecurityAssertion.class);
    SecurityAssertion assertion2 = mock(SecurityAssertion.class);

    when(principals.byType(SecurityAssertion.class))
        .thenReturn(new java.util.HashSet<>(Arrays.asList(assertion1, assertion2)));

    when(assertion1.getAttributeStatements()).thenReturn(Collections.emptyList());
    when(assertion1.getAuthnStatements()).thenReturn(Collections.emptyList());
    when(assertion2.getAttributeStatements()).thenReturn(Collections.emptyList());
    when(assertion2.getAuthnStatements()).thenReturn(Collections.emptyList());

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    assertThat(whoAmI.whoAmISubjects, hasSize(2));
  }

  @Test
  public void testWhoAmISubjectClaimsAreUnmodifiable() {
    setupValidSubject();
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn("role");
    when(attribute.getValues()).thenReturn(Collections.singletonList("admin"));

    AttributeStatement attributeStatement = mock(AttributeStatement.class);
    when(attributeStatement.getAttributes()).thenReturn(Collections.singletonList(attribute));

    when(securityAssertion.getAttributeStatements())
        .thenReturn(Collections.singletonList(attributeStatement));

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    Map<String, SortedSet<String>> claims = whoAmI.whoAmISubjects.get(0).getClaims();

    try {
      claims.put("newKey", new TreeSet<>(Collections.singletonList("value")));
      assertThat("Expected UnsupportedOperationException", false, is(true));
    } catch (UnsupportedOperationException e) {
      // Expected
    }
  }

  @Test
  public void testWhoAmISubjectAuthnContextClassesAreUnmodifiable() {
    setupValidSubject();
    AuthenticationStatement authnStatement = mock(AuthenticationStatement.class);
    when(authnStatement.getAuthnContextClassRef()).thenReturn(TEST_AUTHN_CONTEXT);
    when(securityAssertion.getAuthnStatements())
        .thenReturn(Collections.singletonList(authnStatement));

    WhoAmI whoAmI = new WhoAmI(subject, subjectOperations);

    List<String> authnContextClasses = whoAmI.whoAmISubjects.get(0).getAuthnContextClasses();

    try {
      authnContextClasses.add("newContext");
      assertThat("Expected UnsupportedOperationException", false, is(true));
    } catch (UnsupportedOperationException e) {
      // Expected
    }
  }

  private void setupValidSubject() {
    when(subject.getPrincipals()).thenReturn(principals);
    when(principals.byType(SecurityAssertion.class))
        .thenReturn(Collections.singleton(securityAssertion));
    when(securityAssertion.getAttributeStatements()).thenReturn(Collections.emptyList());
    when(securityAssertion.getAuthnStatements()).thenReturn(Collections.emptyList());
  }
}
