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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OidcAuthenticationTokenTest {

  @Test
  public void testConstructor() {
    String credentials = "test-credentials";
    Object context = new Object();
    String ip = "127.0.0.1";

    OidcAuthenticationToken token = new OidcAuthenticationToken(credentials, context, ip);

    assertThat(token.getPrincipal(), is(nullValue()));
    assertThat(token.getCredentials(), is(credentials));
    assertThat(token.getContext(), is(context));
    assertThat(token.getIpAddress(), is(ip));
  }

  @Test
  public void testGetContext() {
    Object context = new Object();
    OidcAuthenticationToken token = new OidcAuthenticationToken("creds", context, "127.0.0.1");

    assertThat(token.getContext(), is(context));
    assertThat(token.getContext(), is(notNullValue()));
  }

  @Test
  public void testGetCredentialsAsString() {
    OidcAuthenticationToken token = new OidcAuthenticationToken("creds", new Object(), "127.0.0.1");

    String result = token.getCredentialsAsString();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testWithNullCredentials() {
    Object context = new Object();
    OidcAuthenticationToken token = new OidcAuthenticationToken(null, context, "127.0.0.1");

    assertThat(token.getCredentials(), is(nullValue()));
    assertThat(token.getContext(), is(context));
  }

  @Test
  public void testWithNullContext() {
    String credentials = "test-credentials";
    OidcAuthenticationToken token = new OidcAuthenticationToken(credentials, null, "127.0.0.1");

    assertThat(token.getCredentials(), is(credentials));
    assertThat(token.getContext(), is(nullValue()));
  }

  @Test
  public void testWithIpv4Address() {
    OidcAuthenticationToken token =
        new OidcAuthenticationToken("creds", new Object(), "192.168.1.1");

    assertThat(token.getIpAddress(), is("192.168.1.1"));
  }

  @Test
  public void testWithIpv6Address() {
    OidcAuthenticationToken token =
        new OidcAuthenticationToken("creds", new Object(), "2001:db8::1");

    // IPv6 addresses should be formatted with brackets
    String ip = token.getIpAddress();
    assertThat(ip, is(notNullValue()));
    assertThat(ip.startsWith("["), is(true));
    assertThat(ip.endsWith("]"), is(true));
  }

  @Test
  public void testWithIpv6LoopbackAddress() {
    OidcAuthenticationToken token = new OidcAuthenticationToken("creds", new Object(), "::1");

    assertThat(token.getIpAddress(), is("[::1]"));
  }

  @Test
  public void testWithNullIpAddress() {
    OidcAuthenticationToken token = new OidcAuthenticationToken("creds", new Object(), null);

    assertThat(token.getIpAddress(), is(nullValue()));
  }

  @Test
  public void testContextAsString() {
    String contextString = "context-string";
    OidcAuthenticationToken token =
        new OidcAuthenticationToken("creds", contextString, "127.0.0.1");

    assertThat(token.getContext(), is(contextString));
  }

  @Test
  public void testContextAsComplexObject() {
    ComplexContext context = new ComplexContext("user123", "session456");
    OidcAuthenticationToken token = new OidcAuthenticationToken("creds", context, "127.0.0.1");

    assertThat(token.getContext(), is(notNullValue()));
    ComplexContext retrievedContext = (ComplexContext) token.getContext();
    assertThat(retrievedContext.userId, is("user123"));
    assertThat(retrievedContext.sessionId, is("session456"));
  }

  @Test
  public void testCredentialsAsStringAlwaysReturnsNull() {
    String credentials = "any-credentials-value";
    OidcAuthenticationToken token =
        new OidcAuthenticationToken(credentials, new Object(), "127.0.0.1");

    // Should always return null regardless of actual credentials
    assertThat(token.getCredentialsAsString(), is(nullValue()));
  }

  @Test
  public void testPrincipalAlwaysNull() {
    OidcAuthenticationToken token = new OidcAuthenticationToken("creds", new Object(), "127.0.0.1");

    // Principal is always null for OIDC tokens (set in constructor)
    assertThat(token.getPrincipal(), is(nullValue()));
  }

  @Test
  public void testMultipleTokensWithDifferentContexts() {
    Object context1 = new Object();
    Object context2 = new Object();

    OidcAuthenticationToken token1 = new OidcAuthenticationToken("creds1", context1, "127.0.0.1");
    OidcAuthenticationToken token2 = new OidcAuthenticationToken("creds2", context2, "127.0.0.2");

    assertThat(token1.getContext(), is(context1));
    assertThat(token2.getContext(), is(context2));
    assertThat(token1.getContext().equals(token2.getContext()), is(false));
  }

  @Test
  public void testWithEmptyStringCredentials() {
    OidcAuthenticationToken token = new OidcAuthenticationToken("", new Object(), "127.0.0.1");

    assertThat(token.getCredentials(), is(""));
  }

  @Test
  public void testInheritedBehaviorFromBaseAuthenticationToken() {
    OidcAuthenticationToken token = new OidcAuthenticationToken("creds", new Object(), "127.0.0.1");

    // Test inherited methods
    token.setType(AuthenticationTokenType.OIDC);
    assertThat(token.getType(), is(AuthenticationTokenType.OIDC));

    token.setAllowGuest(true);
    assertThat(token.getAllowGuest(), is(true));

    String uri = "/services/oidc/callback";
    token.setRequestURI(uri);
    assertThat(token.getRequestURI(), is(uri));
  }

  private static class ComplexContext {
    String userId;
    String sessionId;

    ComplexContext(String userId, String sessionId) {
      this.userId = userId;
      this.sessionId = sessionId;
    }
  }
}
