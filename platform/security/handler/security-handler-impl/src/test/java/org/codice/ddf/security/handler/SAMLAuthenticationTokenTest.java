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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.assertion.SecurityAssertion;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.shiro.subject.PrincipalCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@ExtendWith(MockitoExtension.class)
public class SAMLAuthenticationTokenTest {

  @Mock private PrincipalCollection principalCollection;

  @Mock private SecurityAssertion samlAssertion;

  private Element samlElement;

  @BeforeEach
  public void setUp() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    samlElement = doc.createElementNS("urn:oasis:names:tc:SAML:2.0:assertion", "saml:Assertion");
    samlElement.setAttribute("ID", "test-assertion-id");
    doc.appendChild(samlElement);
  }

  @Test
  public void testConstructor() {
    String principal = "testuser";
    String ip = "127.0.0.1";

    SAMLAuthenticationToken token = new SAMLAuthenticationToken(principal, principalCollection, ip);

    assertThat(token.getPrincipal(), is(principal));
    assertThat(token.getCredentials(), is(principalCollection));
    assertThat(token.getIpAddress(), is(ip));
  }

  @Test
  public void testGetSAMLTokenAsElement() {
    when(samlAssertion.getTokenType()).thenReturn("SAML");
    when(samlAssertion.getToken()).thenReturn(samlElement);

    List<SecurityAssertion> assertions = new ArrayList<>();
    assertions.add(samlAssertion);

    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    Element result = token.getSAMLTokenAsElement();

    assertThat(result, is(notNullValue()));
    assertThat(result, is(samlElement));
    assertThat(result.getAttribute("ID"), is("test-assertion-id"));
  }

  @Test
  public void testGetSAMLTokenAsElementCaseInsensitive() {
    when(samlAssertion.getTokenType()).thenReturn("saml");
    when(samlAssertion.getToken()).thenReturn(samlElement);

    List<SecurityAssertion> assertions = new ArrayList<>();
    assertions.add(samlAssertion);

    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    Element result = token.getSAMLTokenAsElement();

    assertThat(result, is(notNullValue()));
    assertThat(result, is(samlElement));
  }

  @Test
  public void testGetSAMLTokenAsElementNotFound() {
    when(samlAssertion.getTokenType()).thenReturn("OTHER");
    // No need to stub getToken() since it won't be called for non-SAML token types

    List<SecurityAssertion> assertions = new ArrayList<>();
    assertions.add(samlAssertion);

    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    Element result = token.getSAMLTokenAsElement();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetSAMLTokenAsElementEmptyCollection() {
    List<SecurityAssertion> assertions = new ArrayList<>();
    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    Element result = token.getSAMLTokenAsElement();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetSAMLTokenAsElementNonElementToken() {
    when(samlAssertion.getTokenType()).thenReturn("SAML");
    when(samlAssertion.getToken()).thenReturn("not-an-element");

    List<SecurityAssertion> assertions = new ArrayList<>();
    assertions.add(samlAssertion);

    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    Element result = token.getSAMLTokenAsElement();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetCredentialsAsString() {
    when(samlAssertion.getTokenType()).thenReturn("SAML");
    when(samlAssertion.getToken()).thenReturn(samlElement);

    List<SecurityAssertion> assertions = new ArrayList<>();
    assertions.add(samlAssertion);

    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    String result = token.getCredentialsAsString();

    assertThat(result, is(notNullValue()));
    assertThat(result.contains("saml:Assertion"), is(true));
    assertThat(result.contains("test-assertion-id"), is(true));
  }

  @Test
  public void testGetCredentialsAsStringNoElement() {
    List<SecurityAssertion> assertions = new ArrayList<>();
    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    String result = token.getCredentialsAsString();

    assertThat(result, is(""));
  }

  @Test
  public void testGetSAMLTokenAsElementWithMultipleAssertions() {
    SecurityAssertion otherAssertion = mock(SecurityAssertion.class);
    when(otherAssertion.getTokenType()).thenReturn("OTHER");

    when(samlAssertion.getTokenType()).thenReturn("SAML");
    when(samlAssertion.getToken()).thenReturn(samlElement);

    List<SecurityAssertion> assertions = new ArrayList<>();
    assertions.add(otherAssertion);
    assertions.add(samlAssertion);

    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    Element result = token.getSAMLTokenAsElement();

    assertThat(result, is(notNullValue()));
    assertThat(result, is(samlElement));
  }

  @Test
  public void testWithNullPrincipal() {
    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken(null, principalCollection, "127.0.0.1");

    assertThat(token.getPrincipal(), is(nullValue()));
    assertThat(token.getCredentials(), is(principalCollection));
  }

  @Test
  public void testWithIpv6Address() {
    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "::1");

    assertThat(token.getIpAddress(), is("[::1]"));
  }

  @Test
  public void testGetCredentialsAsStringWithNamespace() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element assertion =
        doc.createElementNS("urn:oasis:names:tc:SAML:2.0:assertion", "saml2:Assertion");
    assertion.setAttribute("xmlns:saml2", "urn:oasis:names:tc:SAML:2.0:assertion");
    doc.appendChild(assertion);

    when(samlAssertion.getTokenType()).thenReturn("SAML");
    when(samlAssertion.getToken()).thenReturn(assertion);

    List<SecurityAssertion> assertions = new ArrayList<>();
    assertions.add(samlAssertion);

    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    SAMLAuthenticationToken token =
        new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

    String result = token.getCredentialsAsString();

    assertThat(result, is(notNullValue()));
    assertThat(result.contains("Assertion"), is(true));
  }

  @Test
  public void testTokenTypeVariations() {
    when(samlAssertion.getToken()).thenReturn(samlElement);

    List<SecurityAssertion> assertions = new ArrayList<>();
    assertions.add(samlAssertion);

    when(principalCollection.byType(SecurityAssertion.class)).thenReturn(assertions);

    String[] tokenTypes = {"SAML", "saml", "Saml", "SAML2", "saml2.0"};

    for (String tokenType : tokenTypes) {
      when(samlAssertion.getTokenType()).thenReturn(tokenType);

      SAMLAuthenticationToken token =
          new SAMLAuthenticationToken("testuser", principalCollection, "127.0.0.1");

      Element result = token.getSAMLTokenAsElement();

      if (tokenType.toLowerCase().contains("saml")) {
        assertThat("Token type " + tokenType + " should be found", result, is(notNullValue()));
      }
    }
  }
}
