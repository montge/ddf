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

import java.security.cert.X509Certificate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SessionTokenTest {

  @Mock private X509Certificate certificate;

  @Test
  public void testConstructor() {
    String principal = "testuser";
    String credentials = "session-credentials";
    String ip = "127.0.0.1";

    SessionToken token = new SessionToken(principal, credentials, ip);

    assertThat(token.getPrincipal(), is(principal));
    assertThat(token.getCredentials(), is(credentials));
    assertThat(token.getIpAddress(), is(ip));
  }

  @Test
  public void testWithNullPrincipal() {
    SessionToken token = new SessionToken(null, "credentials", "127.0.0.1");

    assertThat(token.getPrincipal(), is(nullValue()));
    assertThat(token.getCredentials(), is("credentials"));
  }

  @Test
  public void testWithNullCredentials() {
    SessionToken token = new SessionToken("principal", null, "127.0.0.1");

    assertThat(token.getPrincipal(), is("principal"));
    assertThat(token.getCredentials(), is(nullValue()));
  }

  @Test
  public void testWithNullIpAddress() {
    SessionToken token = new SessionToken("principal", "credentials", null);

    assertThat(token.getPrincipal(), is("principal"));
    assertThat(token.getCredentials(), is("credentials"));
    assertThat(token.getIpAddress(), is(nullValue()));
  }

  @Test
  public void testWithIpv4Address() {
    SessionToken token = new SessionToken("principal", "credentials", "192.168.1.1");

    assertThat(token.getIpAddress(), is("192.168.1.1"));
  }

  @Test
  public void testWithIpv6Address() {
    SessionToken token = new SessionToken("principal", "credentials", "2001:db8::1");

    // IPv6 addresses should be formatted with brackets
    String ip = token.getIpAddress();
    assertThat(ip, is(notNullValue()));
    assertThat(ip.startsWith("["), is(true));
    assertThat(ip.endsWith("]"), is(true));
  }

  @Test
  public void testWithIpv6LoopbackAddress() {
    SessionToken token = new SessionToken("principal", "credentials", "::1");

    assertThat(token.getIpAddress(), is("[::1]"));
  }

  @Test
  public void testGetCredentialsAsString() {
    String credentials = "session-token-credentials";
    SessionToken token = new SessionToken("principal", credentials, "127.0.0.1");

    assertThat(token.getCredentialsAsString(), is(credentials));
  }

  @Test
  public void testInheritedMethodsFromBaseAuthenticationToken() {
    SessionToken token = new SessionToken("principal", "credentials", "127.0.0.1");

    // Test setting type
    token.setType(AuthenticationTokenType.USERNAME);
    assertThat(token.getType(), is(AuthenticationTokenType.USERNAME));

    // Test allow guest
    token.setAllowGuest(true);
    assertThat(token.getAllowGuest(), is(true));

    // Test request URI
    String uri = "/services/session";
    token.setRequestURI(uri);
    assertThat(token.getRequestURI(), is(uri));
  }

  @Test
  public void testWithX509Certificates() {
    SessionToken token = new SessionToken("principal", "credentials", "127.0.0.1");

    X509Certificate[] certs = new X509Certificate[] {certificate};
    token.setX509Certs(certs);

    assertThat(token.getX509Certs(), is(notNullValue()));
    assertThat(token.getX509Certs().length, is(1));
    assertThat(token.getX509Certs()[0], is(certificate));
  }

  @Test
  public void testWithEmptyStringPrincipal() {
    SessionToken token = new SessionToken("", "credentials", "127.0.0.1");

    assertThat(token.getPrincipal(), is(""));
  }

  @Test
  public void testWithEmptyStringCredentials() {
    SessionToken token = new SessionToken("principal", "", "127.0.0.1");

    assertThat(token.getCredentials(), is(""));
    assertThat(token.getCredentialsAsString(), is(""));
  }

  @Test
  public void testWithComplexPrincipal() {
    Object complexPrincipal =
        new Object() {
          @Override
          public String toString() {
            return "ComplexSessionPrincipal";
          }
        };

    SessionToken token = new SessionToken(complexPrincipal, "credentials", "127.0.0.1");

    assertThat(token.getPrincipal(), is(complexPrincipal));
  }

  @Test
  public void testWithComplexCredentials() {
    Object complexCredentials =
        new Object() {
          @Override
          public String toString() {
            return "ComplexSessionCredentials";
          }
        };

    SessionToken token = new SessionToken("principal", complexCredentials, "127.0.0.1");

    assertThat(token.getCredentials(), is(complexCredentials));
    assertThat(token.getCredentialsAsString(), is("ComplexSessionCredentials"));
  }

  @Test
  public void testMultipleSessionTokensIndependence() {
    SessionToken token1 = new SessionToken("user1", "creds1", "127.0.0.1");
    SessionToken token2 = new SessionToken("user2", "creds2", "127.0.0.2");

    assertThat(token1.getPrincipal(), is("user1"));
    assertThat(token2.getPrincipal(), is("user2"));
    assertThat(token1.getCredentials(), is("creds1"));
    assertThat(token2.getCredentials(), is("creds2"));
    assertThat(token1.getIpAddress(), is("127.0.0.1"));
    assertThat(token2.getIpAddress(), is("127.0.0.2"));
  }

  @Test
  public void testSetCredentials() {
    SessionToken token = new SessionToken("principal", "original-credentials", "127.0.0.1");

    assertThat(token.getCredentials(), is("original-credentials"));

    token.setCredentials("new-credentials");
    assertThat(token.getCredentials(), is("new-credentials"));
    assertThat(token.getCredentialsAsString(), is("new-credentials"));
  }

  @Test
  public void testAllTokenTypes() {
    SessionToken token = new SessionToken("principal", "credentials", "127.0.0.1");

    for (AuthenticationTokenType type : AuthenticationTokenType.values()) {
      token.setType(type);
      assertThat(token.getType(), is(type));
    }
  }

  @Test
  public void testWithRequestURIContainingQueryParameters() {
    SessionToken token = new SessionToken("principal", "credentials", "127.0.0.1");

    String uri = "/services/session?id=123&token=abc";
    token.setRequestURI(uri);

    assertThat(token.getRequestURI(), is(uri));
  }

  @Test
  public void testIpv6AddressAlreadyBracketed() {
    SessionToken token = new SessionToken("principal", "credentials", "[2001:db8::1]");

    // Should not double-bracket
    assertThat(token.getIpAddress(), is("[2001:db8::1]"));
  }

  @Test
  public void testInvalidIpAddress() {
    SessionToken token = new SessionToken("principal", "credentials", "not-an-ip");

    // Invalid IP should still be set
    assertThat(token.getIpAddress(), is("not-an-ip"));
  }
}
