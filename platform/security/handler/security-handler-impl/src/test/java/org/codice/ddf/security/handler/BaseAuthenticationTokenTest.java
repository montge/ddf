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

import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BaseAuthenticationTokenTest {

  @Test
  public void testConstructor() {
    String principal = "testuser";
    String credentials = "testcredentials";
    String ip = "127.0.0.1";

    BaseAuthenticationToken token = new BaseAuthenticationToken(principal, credentials, ip);

    assertThat(token.getPrincipal(), is(principal));
    assertThat(token.getCredentials(), is(credentials));
    assertThat(token.getIpAddress(), is(ip));
  }

  @Test
  public void testGetAndSetType() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    assertThat(token.getType(), is(nullValue()));

    token.setType(AuthenticationTokenType.USERNAME);
    assertThat(token.getType(), is(AuthenticationTokenType.USERNAME));

    token.setType(AuthenticationTokenType.SAML);
    assertThat(token.getType(), is(AuthenticationTokenType.SAML));

    token.setType(AuthenticationTokenType.OIDC);
    assertThat(token.getType(), is(AuthenticationTokenType.OIDC));

    token.setType(AuthenticationTokenType.PKI);
    assertThat(token.getType(), is(AuthenticationTokenType.PKI));
  }

  @Test
  public void testGetAndSetAllowGuest() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    assertThat(token.getAllowGuest(), is(false));

    token.setAllowGuest(true);
    assertThat(token.getAllowGuest(), is(true));

    token.setAllowGuest(false);
    assertThat(token.getAllowGuest(), is(false));
  }

  @Test
  public void testGetAndSetX509Certs() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    assertThat(token.getX509Certs(), is(nullValue()));

    X509Certificate[] certs = new X509Certificate[] {mock(X509Certificate.class)};
    token.setX509Certs(certs);

    assertThat(token.getX509Certs(), is(notNullValue()));
    assertThat(token.getX509Certs().length, is(1));
    assertThat(token.getX509Certs(), is(certs));
  }

  @Test
  public void testGetAndSetRequestURI() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    assertThat(token.getRequestURI(), is(nullValue()));

    String uri = "/services/catalog/query";
    token.setRequestURI(uri);

    assertThat(token.getRequestURI(), is(uri));
  }

  @Test
  public void testGetCredentialsAsString() {
    String credentials = "testcredentials";
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", credentials, "127.0.0.1");

    assertThat(token.getCredentialsAsString(), is(credentials));
  }

  @Test
  public void testFormatIpv4Address() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "192.168.1.1");

    assertThat(token.getIpAddress(), is("192.168.1.1"));
  }

  @Test
  public void testFormatIpv6Address() {
    BaseAuthenticationToken token =
        new BaseAuthenticationToken("user", "pass", "2001:db8:85a3::8a2e:370:7334");

    // IPv6 addresses should be wrapped in brackets
    String ip = token.getIpAddress();
    assertThat(ip, is(notNullValue()));
    assertThat(ip.startsWith("["), is(true));
    assertThat(ip.endsWith("]"), is(true));
  }

  @Test
  public void testFormatIpv6LoopbackAddress() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "::1");

    String ip = token.getIpAddress();
    assertThat(ip, is("[::1]"));
  }

  @Test
  public void testFormatIpv6AddressAlreadyBracketed() {
    BaseAuthenticationToken token =
        new BaseAuthenticationToken("user", "pass", "[2001:db8:85a3::8a2e:370:7334]");

    // Should not double-bracket
    String ip = token.getIpAddress();
    assertThat(ip, is("[2001:db8:85a3::8a2e:370:7334]"));
  }

  @Test
  public void testFormatInvalidIpAddress() {
    // Invalid IP address should still be set
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "not-an-ip");

    assertThat(token.getIpAddress(), is("not-an-ip"));
  }

  @Test
  public void testNullIpAddress() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", null);

    assertThat(token.getIpAddress(), is(nullValue()));
  }

  @Test
  public void testSetCredentials() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    assertThat(token.getCredentials(), is("pass"));

    token.setCredentials("newpass");
    assertThat(token.getCredentials(), is("newpass"));
  }

  @Test
  public void testWithNullPrincipal() {
    BaseAuthenticationToken token = new BaseAuthenticationToken(null, "pass", "127.0.0.1");

    assertThat(token.getPrincipal(), is(nullValue()));
    assertThat(token.getCredentials(), is("pass"));
  }

  @Test
  public void testWithNullCredentials() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", null, "127.0.0.1");

    assertThat(token.getPrincipal(), is("user"));
    assertThat(token.getCredentials(), is(nullValue()));
  }

  @Test
  public void testMultipleCertificates() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    X509Certificate[] certs =
        new X509Certificate[] {
          mock(X509Certificate.class), mock(X509Certificate.class), mock(X509Certificate.class)
        };

    token.setX509Certs(certs);
    assertThat(token.getX509Certs().length, is(3));
  }

  @Test
  public void testRequestURIWithQueryParameters() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    String uri = "/services/catalog/query?q=test&max=100";
    token.setRequestURI(uri);

    assertThat(token.getRequestURI(), is(uri));
  }

  @Test
  public void testAllTokenTypes() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    for (AuthenticationTokenType type : AuthenticationTokenType.values()) {
      token.setType(type);
      assertThat(token.getType(), is(type));
    }
  }

  @Test
  public void testComplexPrincipal() {
    Object complexPrincipal =
        new Object() {
          @Override
          public String toString() {
            return "ComplexPrincipal";
          }
        };

    BaseAuthenticationToken token =
        new BaseAuthenticationToken(complexPrincipal, "pass", "127.0.0.1");

    assertThat(token.getPrincipal(), is(complexPrincipal));
  }

  @Test
  public void testIpv4Localhost() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "127.0.0.1");

    assertThat(token.getIpAddress(), is("127.0.0.1"));
  }

  @Test
  public void testIpv6CompressedAddress() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "2001:db8::1");

    String ip = token.getIpAddress();
    assertThat(ip, is(notNullValue()));
    assertThat(ip.startsWith("["), is(true));
    assertThat(ip.endsWith("]"), is(true));
  }
}
