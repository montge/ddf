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
package ddf.security.realm.sts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.assertion.saml.impl.SecurityAssertionSaml;
import java.util.Arrays;
import java.util.Collections;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.codice.ddf.platform.filter.AuthenticationFailureException;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.codice.ddf.security.handler.SAMLAuthenticationToken;
import org.codice.ddf.security.saml.assertion.validator.SamlAssertionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Element;

/** Test SAML authentication and validation for SamlRealm. */
public class SamlRealmAuthenticationTest {

  private SamlRealm realm;

  private AutoCloseable mocks;

  @Mock private SamlAssertionValidator samlAssertionValidator;
  @Mock private SAMLAuthenticationToken samlToken;
  @Mock private Element securityTokenElement;
  @Mock private SecurityAssertionSaml securityAssertionSaml;

  @BeforeEach
  public void setup() {
    mocks = MockitoAnnotations.openMocks(this);

    realm = new SamlRealm();
    realm.setSamlAssertionValidator(samlAssertionValidator);
    realm.setUsernameAttributeList(Arrays.asList("uid", "username"));
  }

  @AfterEach
  public void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  public void testSupportsSamlToken() {
    when(samlToken.getCredentials()).thenReturn("credentials");

    boolean supported = realm.supports(samlToken);

    assertThat(supported, is(true));
  }

  @Test
  public void testSupportsNullToken() {
    boolean supported = realm.supports(null);

    assertThat(supported, is(false));
  }

  @Test
  public void testSupportsNonSamlToken() {
    AuthenticationToken otherToken = mock(BaseAuthenticationToken.class);
    when(otherToken.getCredentials()).thenReturn("credentials");

    boolean supported = realm.supports(otherToken);

    assertThat(supported, is(false));
  }

  @Test
  public void testSupportsTokenWithNullCredentials() {
    when(samlToken.getCredentials()).thenReturn(null);

    boolean supported = realm.supports(samlToken);

    assertThat(supported, is(false));
  }

  @Test
  public void testSuccessfulAuthentication() throws AuthenticationFailureException {
    PrincipalCollection principalCollection = new SimplePrincipalCollection();
    when(samlToken.getCredentials()).thenReturn(principalCollection);

    AuthenticationInfo info = realm.doGetAuthenticationInfo(samlToken);

    assertThat(info, is(notNullValue()));
    assertThat(info.getCredentials(), is(principalCollection));
    verify(samlAssertionValidator, times(1)).validate(samlToken);
  }

  @Test
  public void testAuthenticationWithNullCredentials() {
    when(samlToken.getCredentials()).thenReturn(null);

    assertThrows(AuthenticationException.class, () -> realm.doGetAuthenticationInfo(samlToken));
  }

  @Test
  public void testAuthenticationValidationFailure() throws AuthenticationFailureException {
    when(samlToken.getCredentials()).thenReturn("credentials");
    doThrow(new AuthenticationFailureException("Validation failed"))
        .when(samlAssertionValidator)
        .validate(samlToken);

    assertThrows(AuthenticationException.class, () -> realm.doGetAuthenticationInfo(samlToken));
  }

  @Test
  public void testAuthenticationWithPrincipalCollection() throws AuthenticationFailureException {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    principalCollection.add(securityAssertionSaml, "realm");

    when(securityAssertionSaml.getToken()).thenReturn(securityTokenElement);
    when(samlToken.getCredentials()).thenReturn(principalCollection);

    AuthenticationInfo info = realm.doGetAuthenticationInfo(samlToken);

    assertThat(info, is(notNullValue()));
    assertThat(info.getCredentials(), is(principalCollection));
  }

  @Test
  public void testAuthenticationWithEmptyPrincipalCollection()
      throws AuthenticationFailureException {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    when(samlToken.getCredentials()).thenReturn(principalCollection);

    AuthenticationInfo info = realm.doGetAuthenticationInfo(samlToken);

    assertThat(info, is(notNullValue()));
    assertThat(info.getCredentials(), is(principalCollection));
  }

  @Test
  public void testAuthenticationWithNonElementToken() throws AuthenticationFailureException {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    SecurityAssertionSaml assertionWithNonElement = mock(SecurityAssertionSaml.class);
    when(assertionWithNonElement.getToken()).thenReturn("not an element");
    principalCollection.add(assertionWithNonElement, "realm");

    when(samlToken.getCredentials()).thenReturn(principalCollection);

    AuthenticationInfo info = realm.doGetAuthenticationInfo(samlToken);

    assertThat(info, is(notNullValue()));
  }

  @Test
  public void testSetUsernameAttributeList() {
    realm.setUsernameAttributeList(Arrays.asList("cn", "mail", "uid"));
    // Verify setter completes without exception
    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testSetSamlAssertionValidator() {
    SamlAssertionValidator validator = mock(SamlAssertionValidator.class);
    realm.setSamlAssertionValidator(validator);
    // Verify setter completes without exception
    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testCredentialsMatcherIsSet() {
    assertThat(realm.getCredentialsMatcher(), is(notNullValue()));
  }

  @Test
  public void testSupportsWithDifferentCredentialTypes() {
    SAMLAuthenticationToken tokenWithString = mock(SAMLAuthenticationToken.class);
    when(tokenWithString.getCredentials()).thenReturn("string credentials");
    assertThat(realm.supports(tokenWithString), is(true));

    SAMLAuthenticationToken tokenWithPrincipalCollection = mock(SAMLAuthenticationToken.class);
    PrincipalCollection pc = new SimplePrincipalCollection();
    when(tokenWithPrincipalCollection.getCredentials()).thenReturn(pc);
    assertThat(realm.supports(tokenWithPrincipalCollection), is(true));
  }

  @Test
  public void testNonSamlTokenAuthentication() {
    AuthenticationToken nonSamlToken = mock(BaseAuthenticationToken.class);
    when(nonSamlToken.getCredentials()).thenReturn("credentials");

    assertThrows(AuthenticationException.class, () -> realm.doGetAuthenticationInfo(nonSamlToken));
  }

  @Test
  public void testMultipleSecurityAssertions() throws AuthenticationFailureException {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();

    SecurityAssertionSaml assertion1 = mock(SecurityAssertionSaml.class);
    SecurityAssertionSaml assertion2 = mock(SecurityAssertionSaml.class);
    Element element1 = mock(Element.class);
    Element element2 = mock(Element.class);

    when(assertion1.getToken()).thenReturn(element1);
    when(assertion2.getToken()).thenReturn(element2);

    principalCollection.add(assertion1, "realm");
    principalCollection.add(assertion2, "realm");

    when(samlToken.getCredentials()).thenReturn(principalCollection);

    AuthenticationInfo info = realm.doGetAuthenticationInfo(samlToken);

    assertThat(info, is(notNullValue()));
  }

  @Test
  public void testEmptyUsernameAttributeList() throws AuthenticationFailureException {
    realm.setUsernameAttributeList(Collections.emptyList());

    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    when(samlToken.getCredentials()).thenReturn(principalCollection);

    AuthenticationInfo info = realm.doGetAuthenticationInfo(samlToken);

    assertThat(info, is(notNullValue()));
  }

  @Test
  public void testNullUsernameAttributeList() throws AuthenticationFailureException {
    realm.setUsernameAttributeList(null);

    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    when(samlToken.getCredentials()).thenReturn(principalCollection);

    AuthenticationInfo info = realm.doGetAuthenticationInfo(samlToken);

    assertThat(info, is(notNullValue()));
  }
}
