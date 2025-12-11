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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ddf.security.assertion.saml.impl.SecurityAssertionSaml;
import java.util.Arrays;
import java.util.List;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.codice.ddf.platform.filter.AuthenticationFailureException;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.codice.ddf.security.handler.SAMLAuthenticationToken;
import org.codice.ddf.security.saml.assertion.validator.SamlAssertionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.w3c.dom.Element;

public class SamlRealmEnhancedTest {

  @Mock private SamlAssertionValidator samlAssertionValidator;

  @Mock private SAMLAuthenticationToken samlAuthenticationToken;

  @Mock private BaseAuthenticationToken baseAuthenticationToken;

  @Mock private Element securityTokenElement;

  @Mock private SecurityAssertionSaml securityAssertionSaml;

  @Mock private PrincipalCollection principalCollection;

  private SamlRealm samlRealm;

  @BeforeEach
  public void setup() {
    openMocks(this);
    samlRealm = new SamlRealm();
    samlRealm.setSamlAssertionValidator(samlAssertionValidator);
    samlRealm.setUsernameAttributeList(Arrays.asList("uid", "username", "email"));
  }

  @Test
  public void testSupportsWithValidSAMLToken() {
    when(samlAuthenticationToken.getCredentials()).thenReturn("credentials");

    boolean supports = samlRealm.supports(samlAuthenticationToken);

    assertThat(supports, is(true));
  }

  @Test
  public void testSupportsWithNullCredentials() {
    when(samlAuthenticationToken.getCredentials()).thenReturn(null);

    boolean supports = samlRealm.supports(samlAuthenticationToken);

    assertThat(supports, is(false));
  }

  @Test
  public void testSupportsWithNullToken() {
    boolean supports = samlRealm.supports(null);

    assertThat(supports, is(false));
  }

  @Test
  public void testSupportsWithNonSAMLToken() {
    AuthenticationToken otherToken = mock(AuthenticationToken.class);
    when(otherToken.getCredentials()).thenReturn("credentials");

    boolean supports = samlRealm.supports(otherToken);

    assertThat(supports, is(false));
  }

  @Test
  public void testDoGetAuthenticationInfoWithValidSAMLToken() throws Exception {
    SimplePrincipalCollection credentials = new SimplePrincipalCollection();
    credentials.add(securityAssertionSaml, "default");
    when(securityAssertionSaml.getToken()).thenReturn(securityTokenElement);

    when(samlAuthenticationToken.getCredentials()).thenReturn(credentials);

    AuthenticationInfo authInfo = samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getCredentials(), is(notNullValue()));
    assertThat(authInfo.getPrincipals(), is(notNullValue()));
    verify(samlAssertionValidator).validate(samlAuthenticationToken);
  }

  @Test
  public void testDoGetAuthenticationInfoWithValidationFailure() throws Exception {
    assertThrows(
        AuthenticationException.class,
        () -> {
          when(samlAuthenticationToken.getCredentials()).thenReturn("credentials");
          doThrow(new AuthenticationFailureException("Validation failed"))
              .when(samlAssertionValidator)
              .validate(any(SAMLAuthenticationToken.class));

          samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);
        });
  }

  @Test
  public void testDoGetAuthenticationInfoWithValidationFailureMessage() throws Exception {
    when(samlAuthenticationToken.getCredentials()).thenReturn("credentials");
    doThrow(new AuthenticationFailureException("Validation failed"))
        .when(samlAssertionValidator)
        .validate(any(SAMLAuthenticationToken.class));

    try {
      samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);
      fail("Expected AuthenticationException");
    } catch (AuthenticationException e) {
      assertThat(e.getMessage(), containsString("Unable to validate request's authentication"));
    }
  }

  @Test
  public void testDoGetAuthenticationInfoWithNullCredentials() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          when(samlAuthenticationToken.getCredentials()).thenReturn(null);

          samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);
        });
  }

  @Test
  public void testDoGetAuthenticationInfoWithNullCredentialsMessage() {
    when(samlAuthenticationToken.getCredentials()).thenReturn(null);

    try {
      samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);
      fail("Expected AuthenticationException");
    } catch (AuthenticationException e) {
      assertThat(e.getMessage(), containsString("A NULL credential was provided"));
    }
  }

  @Test
  public void testDoGetAuthenticationInfoWithPrincipalCollection() throws Exception {
    SimplePrincipalCollection credentials = new SimplePrincipalCollection();
    credentials.add(securityAssertionSaml, "default");
    when(securityAssertionSaml.getToken()).thenReturn(securityTokenElement);

    when(samlAuthenticationToken.getCredentials()).thenReturn(credentials);

    AuthenticationInfo authInfo = samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getCredentials(), is(credentials));
  }

  @Test
  public void testDoGetAuthenticationInfoWithNonElementToken() throws Exception {
    SimplePrincipalCollection credentials = new SimplePrincipalCollection();
    credentials.add(securityAssertionSaml, "default");
    when(securityAssertionSaml.getToken()).thenReturn("not an element");

    when(samlAuthenticationToken.getCredentials()).thenReturn(credentials);

    AuthenticationInfo authInfo = samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);

    assertThat(authInfo, is(notNullValue()));
  }

  @Test
  public void testDoGetAuthenticationInfoWithEmptyPrincipalCollection() throws Exception {
    SimplePrincipalCollection credentials = new SimplePrincipalCollection();
    when(samlAuthenticationToken.getCredentials()).thenReturn(credentials);

    AuthenticationInfo authInfo = samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getCredentials(), is(credentials));
  }

  @Test
  public void testGettersAndSetters() {
    List<String> usernameAttributes = Arrays.asList("uid", "cn", "email");
    samlRealm.setUsernameAttributeList(usernameAttributes);

    assertThat(samlRealm.getUsernameAttributeList(), equalTo(usernameAttributes));
    assertThat(samlRealm.getSamlAssertionValidator(), equalTo(samlAssertionValidator));
  }

  @Test
  public void testSTSCredentialsMatcherWithSAMLToken() {
    SamlRealm.STSCredentialsMatcher matcher = new SamlRealm.STSCredentialsMatcher();

    Object credentials1 = "sameCreds";
    Object credentials2 = "sameCreds";

    when(samlAuthenticationToken.getCredentials()).thenReturn(credentials1);

    org.apache.shiro.authc.AuthenticationInfo info =
        mock(org.apache.shiro.authc.AuthenticationInfo.class);
    when(info.getCredentials()).thenReturn(credentials2);

    boolean matches = matcher.doCredentialsMatch(samlAuthenticationToken, info);

    assertThat(matches, is(true));
  }

  @Test
  public void testSTSCredentialsMatcherWithDifferentCredentials() {
    SamlRealm.STSCredentialsMatcher matcher = new SamlRealm.STSCredentialsMatcher();

    Object credentials1 = "creds1";
    Object credentials2 = "creds2";

    when(samlAuthenticationToken.getCredentials()).thenReturn(credentials1);

    org.apache.shiro.authc.AuthenticationInfo info =
        mock(org.apache.shiro.authc.AuthenticationInfo.class);
    when(info.getCredentials()).thenReturn(credentials2);

    boolean matches = matcher.doCredentialsMatch(samlAuthenticationToken, info);

    assertThat(matches, is(false));
  }

  @Test
  public void testSTSCredentialsMatcherWithBaseAuthenticationToken() {
    SamlRealm.STSCredentialsMatcher matcher = new SamlRealm.STSCredentialsMatcher();

    String xmlCreds = "<xml>credentials</xml>";

    when(baseAuthenticationToken.getCredentialsAsString()).thenReturn(xmlCreds);

    org.apache.shiro.authc.AuthenticationInfo info =
        mock(org.apache.shiro.authc.AuthenticationInfo.class);
    when(info.getCredentials()).thenReturn(xmlCreds);

    boolean matches = matcher.doCredentialsMatch(baseAuthenticationToken, info);

    assertThat(matches, is(true));
  }

  @Test
  public void testSTSCredentialsMatcherWithBaseAuthenticationTokenNullCreds() {
    SamlRealm.STSCredentialsMatcher matcher = new SamlRealm.STSCredentialsMatcher();

    when(baseAuthenticationToken.getCredentialsAsString()).thenReturn(null);

    org.apache.shiro.authc.AuthenticationInfo info =
        mock(org.apache.shiro.authc.AuthenticationInfo.class);
    when(info.getCredentials()).thenReturn("creds");

    boolean matches = matcher.doCredentialsMatch(baseAuthenticationToken, info);

    assertThat(matches, is(false));
  }

  @Test
  public void testSTSCredentialsMatcherWithNullInfoCredentials() {
    SamlRealm.STSCredentialsMatcher matcher = new SamlRealm.STSCredentialsMatcher();

    when(baseAuthenticationToken.getCredentialsAsString()).thenReturn("creds");

    org.apache.shiro.authc.AuthenticationInfo info =
        mock(org.apache.shiro.authc.AuthenticationInfo.class);
    when(info.getCredentials()).thenReturn(null);

    boolean matches = matcher.doCredentialsMatch(baseAuthenticationToken, info);

    assertThat(matches, is(false));
  }

  @Test
  public void testSTSCredentialsMatcherWithGenericToken() {
    SamlRealm.STSCredentialsMatcher matcher = new SamlRealm.STSCredentialsMatcher();

    AuthenticationToken genericToken = mock(AuthenticationToken.class);
    when(genericToken.getCredentials()).thenReturn("creds");

    org.apache.shiro.authc.AuthenticationInfo info =
        mock(org.apache.shiro.authc.AuthenticationInfo.class);
    when(info.getCredentials()).thenReturn("creds");

    boolean matches = matcher.doCredentialsMatch(genericToken, info);

    assertThat(matches, is(true));
  }

  @Test
  public void testSTSCredentialsMatcherWithBothNull() {
    SamlRealm.STSCredentialsMatcher matcher = new SamlRealm.STSCredentialsMatcher();

    AuthenticationToken genericToken = mock(AuthenticationToken.class);
    when(genericToken.getCredentials()).thenReturn(null);

    org.apache.shiro.authc.AuthenticationInfo info =
        mock(org.apache.shiro.authc.AuthenticationInfo.class);
    when(info.getCredentials()).thenReturn(null);

    boolean matches = matcher.doCredentialsMatch(genericToken, info);

    assertThat(matches, is(false));
  }

  @Test
  public void testDoGetAuthenticationInfoCreatesCorrectPrincipals() throws Exception {
    SimplePrincipalCollection credentials = new SimplePrincipalCollection();
    credentials.add(securityAssertionSaml, "default");
    when(securityAssertionSaml.getToken()).thenReturn(securityTokenElement);

    when(samlAuthenticationToken.getCredentials()).thenReturn(credentials);

    AuthenticationInfo authInfo = samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);

    assertThat(authInfo.getPrincipals(), is(notNullValue()));
    // Verify that principals were returned (credentials are used as principals)
    assertThat(authInfo.getCredentials(), is(equalTo(credentials)));
  }

  @Test
  public void testRealmInitialization() {
    SamlRealm newRealm = new SamlRealm();

    assertThat(newRealm.getCredentialsMatcher(), is(notNullValue()));
    assertThat(
        newRealm.getCredentialsMatcher().getClass().getSimpleName(),
        equalTo("STSCredentialsMatcher"));
  }

  @Test
  public void testSupportsMultipleCallsSameToken() {
    when(samlAuthenticationToken.getCredentials()).thenReturn("credentials");

    boolean supports1 = samlRealm.supports(samlAuthenticationToken);
    boolean supports2 = samlRealm.supports(samlAuthenticationToken);

    assertThat(supports1, is(true));
    assertThat(supports2, is(true));
  }

  @Test
  public void testDoGetAuthenticationInfoWithUsernameAttributeList() throws Exception {
    List<String> attributes = Arrays.asList("uid", "username", "email");
    samlRealm.setUsernameAttributeList(attributes);

    SimplePrincipalCollection credentials = new SimplePrincipalCollection();
    credentials.add(securityAssertionSaml, "default");
    when(securityAssertionSaml.getToken()).thenReturn(securityTokenElement);

    when(samlAuthenticationToken.getCredentials()).thenReturn(credentials);

    AuthenticationInfo authInfo = samlRealm.doGetAuthenticationInfo(samlAuthenticationToken);

    assertThat(authInfo, is(notNullValue()));
    assertThat(samlRealm.getUsernameAttributeList(), equalTo(attributes));
  }
}
