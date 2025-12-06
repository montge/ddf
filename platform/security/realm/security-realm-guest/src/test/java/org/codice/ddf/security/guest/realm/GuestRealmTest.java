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
package org.codice.ddf.security.guest.realm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import ddf.security.assertion.Attribute;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.audit.SecurityLogger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.codice.ddf.security.handler.GuestAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GuestRealmTest {

  private GuestRealm guestRealm;

  @Mock private SecurityLogger securityLogger;

  @Before
  public void setup() {
    guestRealm = new GuestRealm();
    guestRealm.setSecurityLogger(securityLogger);
    guestRealm.setAttributes(
        Arrays.asList("claim1=value1", "claim2=value2|value3", "bad", ":=invalid"));
  }

  @Test
  public void testSupportsNull() {
    boolean supports = guestRealm.supports(null);

    assertFalse(supports);
  }

  @Test
  public void testSupportsBaseGuestAllowed() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    boolean supports = guestRealm.supports(baseAuthenticationToken);

    assertTrue(supports);
  }

  @Test
  public void testSupportsNotBase() {
    AuthenticationToken authenticationToken =
        new AuthenticationToken() {
          @Override
          public Object getPrincipal() {
            return "principal";
          }

          @Override
          public Object getCredentials() {
            return "credentials";
          }
        };
    boolean supports = guestRealm.supports(authenticationToken);

    assertFalse(supports);
  }

  @Test
  public void testSupportsBaseGuestNotAllowed() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(false);

    boolean supports = guestRealm.supports(baseAuthenticationToken);

    assertFalse(supports);
  }

  @Test
  public void testDoGetAuthenticationInfo() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    assertEquals(baseAuthenticationToken.getCredentials(), authenticationInfo.getCredentials());

    PrincipalCollection principals = authenticationInfo.getPrincipals();

    assertEquals(2, principals.asList().size());

    Iterator iterator = principals.iterator();

    assertEquals("Guest@0.0.0.0", iterator.next());

    Object next = iterator.next();

    assertTrue(next instanceof SecurityAssertion);

    SecurityAssertion securityAssertion = (SecurityAssertion) next;

    assertEquals(2, securityAssertion.getAttributeStatements().get(0).getAttributes().size());

    boolean claim1 = false;
    boolean claim2 = false;
    boolean claim3 = false;
    boolean claim4 = false;
    for (Attribute attribute : securityAssertion.getAttributeStatements().get(0).getAttributes()) {
      if (attribute.getName().equals("claim1")) {
        claim1 = true;
        assertEquals("value1", attribute.getValues().get(0));
      }
      if (attribute.getName().equals("claim2")) {
        claim2 = true;
        assertTrue(attribute.getValues().stream().anyMatch(v -> v.equals("value2")));
        assertTrue(attribute.getValues().stream().anyMatch(v -> v.equals("value3")));
      }
      if (attribute.getName().equals(":")) {
        claim3 = true;
      }
      if (attribute.getName().equals("bad")) {
        claim4 = true;
      }
    }
    assertTrue(claim1);
    assertTrue(claim2);
    assertFalse(claim3);
    assertFalse(claim4);

    AuthenticationInfo newAuthenticationInfo =
        guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    assertNotSame(authenticationInfo, newAuthenticationInfo);
  }

  @Test
  public void testSupportsGuestAuthenticationToken() {
    GuestAuthenticationToken guestToken = new GuestAuthenticationToken("127.0.0.1", securityLogger);

    boolean supports = guestRealm.supports(guestToken);

    assertTrue(supports);
  }

  @Test
  public void testSupportsNullCredentials() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", null, "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    boolean supports = guestRealm.supports(baseAuthenticationToken);

    assertFalse(supports);
  }

  @Test
  public void testSetAttributesNull() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);

    newRealm.setAttributes(null);

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    assertNotNull(authenticationInfo);
    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(0));
  }

  @Test
  public void testSetAttributesEmpty() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(Collections.emptyList());

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    assertNotNull(authenticationInfo);
    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(0));
  }

  @Test
  public void testDoGetAuthenticationInfoAuditsIpAddress() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "192.168.1.100");
    baseAuthenticationToken.setAllowGuest(true);

    guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    verify(securityLogger).audit("Guest assertion generated for IP address: 192.168.1.100");
  }

  @Test
  public void testDoGetAuthenticationInfoMultipleCalls() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "10.0.0.1");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo first = guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);
    AuthenticationInfo second = guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    assertNotSame("Each call should produce a new authentication info", first, second);
    assertNotSame(
        "Each call should produce different security assertions",
        first.getPrincipals().asList().get(1),
        second.getPrincipals().asList().get(1));
  }

  @Test
  public void testPrincipalCollectionStructure() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "172.16.0.1");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    PrincipalCollection principals = authenticationInfo.getPrincipals();
    assertThat(principals, is(notNullValue()));
    assertThat(principals.asList().size(), is(equalTo(2)));

    String principalName = (String) principals.asList().get(0);
    assertThat(principalName, is(equalTo("Guest@172.16.0.1")));

    Object assertion = principals.asList().get(1);
    assertTrue(
        "Second principal should be SecurityAssertion", assertion instanceof SecurityAssertion);
  }

  @Test
  public void testAttributesWithSingleValue() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(Arrays.asList("http://example.com/role=guest"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    Attribute roleAttribute =
        securityAssertion.getAttributeStatements().get(0).getAttributes().get(0);
    assertThat(roleAttribute.getName(), is(equalTo("http://example.com/role")));
    assertThat(roleAttribute.getValues(), hasSize(1));
    assertThat(roleAttribute.getValues().get(0), is(equalTo("guest")));
  }

  @Test
  public void testAttributesWithMultipleValues() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(Arrays.asList("http://example.com/roles=guest|user|viewer"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    Attribute rolesAttribute =
        securityAssertion.getAttributeStatements().get(0).getAttributes().get(0);
    assertThat(rolesAttribute.getName(), is(equalTo("http://example.com/roles")));
    assertThat(rolesAttribute.getValues(), hasSize(3));
    assertThat(rolesAttribute.getValues(), containsInAnyOrder("guest", "user", "viewer"));
  }

  @Test
  public void testInvalidAttributeMappingIgnored() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(
        Arrays.asList(
            "http://example.com/valid=value",
            "invalidmapping",
            "also=invalid=format",
            "http://example.com/another=value2"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(2));
  }

  @Test
  public void testSecurityAssertionProperties() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "10.20.30.40");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);

    assertThat(securityAssertion.getIssuer(), is(equalTo("local")));
    assertThat(securityAssertion.getTokenType(), is(equalTo("guest")));
    assertNotNull(securityAssertion.getNotBefore());
    assertNotNull(securityAssertion.getNotOnOrAfter());
    assertTrue(
        "NotOnOrAfter should be after NotBefore",
        securityAssertion.getNotOnOrAfter().after(securityAssertion.getNotBefore()));
  }

  @Test
  public void testSetAttributesWithInvalidURISyntax() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(Arrays.asList("not a valid uri=value", "http://valid.com/claim=value"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(1));
  }

  @Test
  public void testAttributesWithEmptyValue() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(Arrays.asList("http://example.com/claim=value"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(1));
    Attribute claimAttribute =
        securityAssertion.getAttributeStatements().get(0).getAttributes().get(0);
    assertThat(claimAttribute.getValues(), hasSize(1));
    assertThat(claimAttribute.getValues().get(0), is(equalTo("value")));
  }

  @Test
  public void testAttributesWithMultiplePipeDelimitedValues() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(Arrays.asList("http://example.com/roles=role1|role2|role3|role4|role5"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    Attribute rolesAttribute =
        securityAssertion.getAttributeStatements().get(0).getAttributes().get(0);
    assertThat(rolesAttribute.getValues(), hasSize(5));
    assertThat(
        rolesAttribute.getValues(),
        containsInAnyOrder("role1", "role2", "role3", "role4", "role5"));
  }

  @Test
  public void testAttributesWithSpecialCharacters() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(
        Arrays.asList(
            "http://example.com/name=Test User@Domain.com",
            "http://example.com/group=Dev&QA|Support"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(2));
  }

  @Test
  public void testDifferentIpAddresses() {
    String[] ipAddresses = {
      "127.0.0.1", "192.168.0.1", "10.0.0.1", "172.16.0.1", "255.255.255.255", "0.0.0.0"
    };

    for (String ip : ipAddresses) {
      BaseAuthenticationToken baseAuthenticationToken =
          new MockBaseAuthenticationToken("principal", "credentials", ip);
      baseAuthenticationToken.setAllowGuest(true);

      AuthenticationInfo authenticationInfo =
          guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

      String principalName = (String) authenticationInfo.getPrincipals().asList().get(0);
      assertThat(principalName, is(equalTo("Guest@" + ip)));
    }
  }

  @Test
  public void testRealmNameInPrincipalCollection() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    PrincipalCollection principals = authenticationInfo.getPrincipals();
    assertNotNull(principals.getRealmNames());
    assertThat(principals.getRealmNames().size(), is(equalTo(1)));
  }

  @Test
  public void testAttributesWithOnlyEquals() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(Arrays.asList("=", "==", "==="));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(0));
  }

  @Test
  public void testAttributesWithWhitespace() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(
        Arrays.asList(
            "http://example.com/claim1=  value with spaces  ",
            "http://example.com/claim2=\ttab\tvalue"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(2));
  }

  @Test
  public void testGuestAuthenticationTokenWithDifferentIp() {
    GuestAuthenticationToken guestToken =
        new GuestAuthenticationToken("203.0.113.42", securityLogger);

    boolean supports = guestRealm.supports(guestToken);
    assertTrue(supports);

    AuthenticationInfo authenticationInfo = guestRealm.doGetAuthenticationInfo(guestToken);
    assertNotNull(authenticationInfo);

    String principalName = (String) authenticationInfo.getPrincipals().asList().get(0);
    assertThat(principalName, is(equalTo("Guest@203.0.113.42")));
  }

  @Test
  public void testTokenWithEmptyIpAddress() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    String principalName = (String) authenticationInfo.getPrincipals().asList().get(0);
    assertThat(principalName, is(equalTo("Guest@")));
  }

  @Test
  public void testTokenWithNullIpAddress() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", null);
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    String principalName = (String) authenticationInfo.getPrincipals().asList().get(0);
    assertThat(principalName, is(equalTo("Guest@")));
  }

  @Test
  public void testAttributesWithNoEquals() {
    GuestRealm newRealm = new GuestRealm();
    newRealm.setSecurityLogger(securityLogger);
    newRealm.setAttributes(Arrays.asList("noequals", "alsonoequals"));

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo authenticationInfo =
        newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    SecurityAssertion securityAssertion =
        (SecurityAssertion) authenticationInfo.getPrincipals().asList().get(1);
    assertThat(securityAssertion.getAttributeStatements().get(0).getAttributes(), hasSize(0));
  }

  @Test
  public void testSetSecurityLogger() {
    GuestRealm newRealm = new GuestRealm();
    SecurityLogger newLogger = mock(SecurityLogger.class);
    newRealm.setSecurityLogger(newLogger);
    newRealm.setAttributes(Collections.emptyList());

    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "192.168.1.1");
    baseAuthenticationToken.setAllowGuest(true);

    newRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    verify(newLogger).audit("Guest assertion generated for IP address: 192.168.1.1");
  }

  @Test
  public void testSupportsWithAllowGuestFalseButGuestToken() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(false);

    boolean supports = guestRealm.supports(baseAuthenticationToken);
    assertFalse(supports);

    GuestAuthenticationToken guestToken = new GuestAuthenticationToken("0.0.0.0", securityLogger);
    boolean guestSupports = guestRealm.supports(guestToken);
    assertTrue(guestSupports);
  }

  @Test
  public void testConsecutiveCallsGenerateUniqueAssertions() {
    BaseAuthenticationToken baseAuthenticationToken =
        new MockBaseAuthenticationToken("principal", "credentials", "0.0.0.0");
    baseAuthenticationToken.setAllowGuest(true);

    AuthenticationInfo first = guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);
    AuthenticationInfo second = guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);
    AuthenticationInfo third = guestRealm.doGetAuthenticationInfo(baseAuthenticationToken);

    assertNotSame(first, second);
    assertNotSame(second, third);
    assertNotSame(first, third);

    SecurityAssertion firstAssertion = (SecurityAssertion) first.getPrincipals().asList().get(1);
    SecurityAssertion secondAssertion = (SecurityAssertion) second.getPrincipals().asList().get(1);

    assertNotSame(firstAssertion, secondAssertion);
  }

  class MockBaseAuthenticationToken extends BaseAuthenticationToken {
    public MockBaseAuthenticationToken(Object principal, Object credentials, String ip) {
      super(principal, credentials, ip);
    }
  }
}
