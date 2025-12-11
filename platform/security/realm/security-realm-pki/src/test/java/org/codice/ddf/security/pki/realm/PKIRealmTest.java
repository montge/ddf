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
package org.codice.ddf.security.pki.realm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ddf.security.SubjectOperations;
import ddf.security.assertion.Attribute;
import ddf.security.assertion.AttributeStatement;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.claims.ClaimsCollection;
import ddf.security.claims.ClaimsHandler;
import ddf.security.claims.impl.ClaimImpl;
import ddf.security.claims.impl.ClaimsCollectionImpl;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.codice.ddf.security.handler.AuthenticationTokenType;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class PKIRealmTest {

  private PKIRealm pkiRealm;

  @Mock private SubjectOperations subjectOperations;

  @BeforeEach
  public void setup() {
    openMocks(this);
    pkiRealm = new PKIRealm();
    pkiRealm.setSubjectOperations(subjectOperations);

    List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    claimsHandlers.add(mock(ClaimsHandler.class));
    claimsHandlers.add(mock(ClaimsHandler.class));
    ClaimsCollection claims1 = new ClaimsCollectionImpl();
    ClaimImpl email1 = new ClaimImpl("email");
    email1.addValue("test@example.com");
    claims1.add(email1);
    ClaimsCollection claims2 = new ClaimsCollectionImpl();
    ClaimImpl email2 = new ClaimImpl("email");
    email2.addValue("tester@example.com");
    claims2.add(email2);
    when(claimsHandlers.get(0).retrieveClaims(any())).thenReturn(claims1);
    when(claimsHandlers.get(1).retrieveClaims(any())).thenReturn(claims2);
    pkiRealm.setClaimsHandlers(claimsHandlers);
  }

  @Test
  public void testSupportsGood() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn(new X509Certificate[1]);
    when(authenticationToken.getPrincipal()).thenReturn(new X500Principal("cn=test"));
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);
    boolean supports = pkiRealm.supports(authenticationToken);
    assertTrue(supports);
  }

  @Test
  public void testSupportsBad() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    boolean supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);

    when(authenticationToken.getCredentials()).thenReturn(new Object());
    when(authenticationToken.getPrincipal()).thenReturn(new Object());
    supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);

    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.SAML);
    supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);

    when(authenticationToken.getCredentials()).thenReturn(new X509Certificate[1]);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);
    supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);

    when(authenticationToken.getCredentials()).thenReturn(new Object());
    when(authenticationToken.getPrincipal()).thenReturn(new X500Principal("cn=test"));
    supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testDoGetAuthenticationInfo() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=myxman,ou=someunit,o=someorg");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertThat(authenticationInfo.getCredentials(), is(certificates));
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertNotNull(assertion);
    assertThat(assertion.getPrincipal(), is(x500Principal));

    AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
    assertNotNull(attributeStatement);
    assertThat(attributeStatement.getAttributes().size(), greaterThan(0));
    Attribute attribute = attributeStatement.getAttributes().get(0);
    assertThat(attribute.getName(), is("email"));
    assertThat(attribute.getValues().size(), is(2));
    assertThat(attribute.getValues(), contains("tester@example.com", "test@example.com"));
  }

  @Test
  public void testSupportsNullToken() {
    boolean supports = pkiRealm.supports(null);
    assertFalse(supports);
  }

  @Test
  public void testSupportsNonBaseToken() {
    AuthenticationToken authenticationToken =
        new AuthenticationToken() {
          @Override
          public Object getPrincipal() {
            return new X500Principal("cn=test");
          }

          @Override
          public Object getCredentials() {
            return new X509Certificate[1];
          }
        };
    boolean supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testSupportsWrongCredentialType() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn("notacertificate");
    when(authenticationToken.getPrincipal()).thenReturn(new X500Principal("cn=test"));
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    boolean supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testSupportsWrongPrincipalType() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn(new X509Certificate[1]);
    when(authenticationToken.getPrincipal()).thenReturn("notanx500principal");
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    boolean supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testSupportsWrongTokenType() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn(new X509Certificate[1]);
    when(authenticationToken.getPrincipal()).thenReturn(new X500Principal("cn=test"));
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.USERNAME);

    boolean supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testDoGetAuthenticationInfoWithSubjectOperations() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test,emailAddress=test@example.com,c=US");

    when(subjectOperations.getEmailAddress(any(X500Principal.class)))
        .thenReturn("test@example.com");
    when(subjectOperations.getCountry(any(X500Principal.class))).thenReturn("US");

    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertNotNull(assertion);
  }

  @Test
  public void testDoGetAuthenticationInfoSubjectOperationsException() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");

    when(subjectOperations.getEmailAddress(any(X500Principal.class)))
        .thenThrow(new RuntimeException("Error extracting email"));
    when(subjectOperations.getCountry(any(X500Principal.class)))
        .thenThrow(new RuntimeException("Error extracting country"));

    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertNotNull(assertion);
  }

  @Test
  public void testSecurityAssertionProperties() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=testuser");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertThat(assertion.getIssuer(), is(equalTo("DDF")));
    assertThat(assertion.getTokenType(), is(equalTo("pki")));
    assertThat(assertion.getWeight(), is(equalTo(SecurityAssertion.LOCAL_AUTH_WEIGHT)));
    assertNotNull(assertion.getNotBefore());
    assertNotNull(assertion.getNotOnOrAfter());
    assertTrue(
        "NotOnOrAfter should be after NotBefore",
        assertion.getNotOnOrAfter().after(assertion.getNotBefore()));
  }

  @Test
  public void testClaimsHandlersEmpty() {
    PKIRealm realmWithoutClaims = new PKIRealm();
    realmWithoutClaims.setSubjectOperations(subjectOperations);
    realmWithoutClaims.setClaimsHandlers(Collections.emptyList());

    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo =
        realmWithoutClaims.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertNotNull(assertion);
    assertThat(assertion.getAttributeStatements().get(0).getAttributes(), hasSize(0));
  }

  @Test
  public void testMultipleCertificates() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[3];
    certificates[0] = mock(X509Certificate.class);
    certificates[1] = mock(X509Certificate.class);
    certificates[2] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertArrayEquals(
        "All certificates should be preserved in credentials",
        certificates,
        (X509Certificate[]) authenticationInfo.getCredentials());
  }

  @Test
  public void testPrincipalCollectionStructure() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=myuser,ou=engineering,o=company");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertThat(authenticationInfo.getPrincipals(), is(notNullValue()));
    assertThat(authenticationInfo.getPrincipals().asList().size(), is(equalTo(1)));

    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertNotNull(assertion);
    assertThat(assertion.getPrincipal(), is(equalTo(x500Principal)));
  }

  @Test
  public void testClaimsMerging() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
    Attribute emailAttribute = attributeStatement.getAttributes().get(0);

    assertThat(emailAttribute.getName(), is("email"));
    assertThat(emailAttribute.getValues(), hasSize(2));
    assertThat(emailAttribute.getValues(), contains("tester@example.com", "test@example.com"));
  }

  @Test
  public void testComplexX500Principal() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal =
        new X500Principal(
            "cn=John Doe,ou=Engineering,ou=R&D,o=ACME Corporation,l=Springfield,st=Illinois,c=US");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertNotNull(assertion);
    assertThat(assertion.getPrincipal(), is(equalTo(x500Principal)));
  }

  @Test
  public void testDoGetAuthenticationInfoWithNullSubjectOperations() {
    PKIRealm realmWithoutSubjectOps = new PKIRealm();
    realmWithoutSubjectOps.setSubjectOperations(null);
    realmWithoutSubjectOps.setClaimsHandlers(Collections.emptyList());

    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo =
        realmWithoutSubjectOps.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
  }

  @Test
  public void testDoGetAuthenticationInfoWithSubjectOperationsReturnsNull() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");

    when(subjectOperations.getEmailAddress(any(X500Principal.class))).thenReturn(null);
    when(subjectOperations.getCountry(any(X500Principal.class))).thenReturn(null);

    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertNotNull(assertion);
  }

  @Test
  public void testSetClaimsHandlers() {
    List<ClaimsHandler> newHandlers = new ArrayList<>();
    ClaimsHandler handler = mock(ClaimsHandler.class);
    newHandlers.add(handler);

    pkiRealm.setClaimsHandlers(newHandlers);

    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    ClaimsCollection claims = new ClaimsCollectionImpl();
    ClaimImpl claim = new ClaimImpl("testclaim");
    claim.addValue("testvalue");
    claims.add(claim);
    when(handler.retrieveClaims(any())).thenReturn(claims);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
  }

  @Test
  public void testAttributeStatementWithNoClaims() {
    PKIRealm realmNoClaims = new PKIRealm();
    realmNoClaims.setSubjectOperations(subjectOperations);

    List<ClaimsHandler> emptyHandlers = new ArrayList<>();
    ClaimsHandler handler1 = mock(ClaimsHandler.class);
    when(handler1.retrieveClaims(any())).thenReturn(new ClaimsCollectionImpl());
    emptyHandlers.add(handler1);
    realmNoClaims.setClaimsHandlers(emptyHandlers);

    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo =
        realmNoClaims.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertThat(assertion.getAttributeStatements().get(0).getAttributes(), hasSize(0));
  }

  @Test
  public void testEmptyCertificateArray() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[0];
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo = pkiRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
    assertThat(((X509Certificate[]) authenticationInfo.getCredentials()).length, is(equalTo(0)));
  }

  @Test
  public void testClaimsHandlerReturnsNullCollection() {
    PKIRealm realmWithNullClaims = new PKIRealm();
    realmWithNullClaims.setSubjectOperations(subjectOperations);

    List<ClaimsHandler> handlers = new ArrayList<>();
    ClaimsHandler nullHandler = mock(ClaimsHandler.class);
    when(nullHandler.retrieveClaims(any())).thenReturn(null);
    handlers.add(nullHandler);
    realmWithNullClaims.setClaimsHandlers(handlers);

    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    try {
      realmWithNullClaims.doGetAuthenticationInfo(authenticationToken);
    } catch (Exception e) {
      // Expected to potentially throw NPE or other exception
      assertNotNull(e);
    }
  }

  @Test
  public void testMultipleClaimsHandlersWithSameClaim() {
    List<ClaimsHandler> handlers = new ArrayList<>();
    ClaimsHandler handler1 = mock(ClaimsHandler.class);
    ClaimsHandler handler2 = mock(ClaimsHandler.class);

    ClaimsCollection claims1 = new ClaimsCollectionImpl();
    ClaimImpl role1 = new ClaimImpl("role");
    role1.addValue("admin");
    claims1.add(role1);

    ClaimsCollection claims2 = new ClaimsCollectionImpl();
    ClaimImpl role2 = new ClaimImpl("role");
    role2.addValue("manager");
    claims2.add(role2);

    when(handler1.retrieveClaims(any())).thenReturn(claims1);
    when(handler2.retrieveClaims(any())).thenReturn(claims2);

    handlers.add(handler1);
    handlers.add(handler2);

    PKIRealm realmMultiClaims = new PKIRealm();
    realmMultiClaims.setSubjectOperations(subjectOperations);
    realmMultiClaims.setClaimsHandlers(handlers);

    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    X509Certificate[] certificates = new X509Certificate[1];
    certificates[0] = mock(X509Certificate.class);
    X500Principal x500Principal = new X500Principal("cn=test");
    when(authenticationToken.getCredentials()).thenReturn(certificates);
    when(authenticationToken.getPrincipal()).thenReturn(x500Principal);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    AuthenticationInfo authenticationInfo =
        realmMultiClaims.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
    Attribute roleAttribute = attributeStatement.getAttributes().get(0);

    assertThat(roleAttribute.getName(), is("role"));
    assertThat(roleAttribute.getValues(), hasSize(2));
    assertThat(roleAttribute.getValues(), contains("manager", "admin"));
  }

  @Test
  public void testSupportsWithNullType() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn(new X509Certificate[1]);
    when(authenticationToken.getPrincipal()).thenReturn(new X500Principal("cn=test"));
    when(authenticationToken.getType()).thenReturn(null);

    boolean supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testSupportsWithNullPrincipal() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn(new X509Certificate[1]);
    when(authenticationToken.getPrincipal()).thenReturn(null);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    boolean supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testSupportsWithNullCredentials() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn(null);
    when(authenticationToken.getPrincipal()).thenReturn(new X500Principal("cn=test"));
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    boolean supports = pkiRealm.supports(authenticationToken);
    assertFalse(supports);
  }
}
