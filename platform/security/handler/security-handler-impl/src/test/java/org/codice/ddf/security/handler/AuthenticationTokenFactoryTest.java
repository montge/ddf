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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.security.auth.x500.X500Principal;
import org.apache.shiro.authc.AuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationTokenFactoryTest {

  private AuthenticationTokenFactory factory;

  @Before
  public void setUp() {
    factory = new AuthenticationTokenFactory();
  }

  @Test
  public void testFromUsernamePassword() {
    String username = "admin";
    String password = "password";
    String ip = "127.0.0.1";

    AuthenticationToken token = factory.fromUsernamePassword(username, password, ip);

    assertThat(token, is(notNullValue()));
    assertThat(token, instanceOf(BaseAuthenticationToken.class));

    BaseAuthenticationToken baseToken = (BaseAuthenticationToken) token;
    assertThat(baseToken.getPrincipal(), is(username));
    assertThat(baseToken.getType(), is(AuthenticationTokenType.USERNAME));
    assertThat(baseToken.getIpAddress(), is(ip));

    // Verify credentials are base64 encoded
    String credentials = (String) baseToken.getCredentials();
    assertThat(credentials, is(notNullValue()));
    assertThat(
        credentials.contains(Base64.getEncoder().encodeToString(username.getBytes())), is(true));
    assertThat(
        credentials.contains(Base64.getEncoder().encodeToString(password.getBytes())), is(true));
  }

  @Test(expected = NullPointerException.class)
  public void testFromUsernamePasswordWithNullUsername() {
    String password = "password";
    String ip = "127.0.0.1";

    // Should throw NullPointerException
    factory.fromUsernamePassword(null, password, ip);
  }

  @Test(expected = NullPointerException.class)
  public void testFromUsernamePasswordWithNullPassword() {
    String username = "admin";
    String ip = "127.0.0.1";

    // Should throw NullPointerException
    factory.fromUsernamePassword(username, null, ip);
  }

  @Test
  public void testFromUsernamePasswordWithEmptyCredentials() {
    String username = "";
    String password = "";
    String ip = "127.0.0.1";

    AuthenticationToken token = factory.fromUsernamePassword(username, password, ip);

    assertThat(token, is(notNullValue()));
    assertThat(token.getPrincipal(), is(username));
  }

  @Test
  public void testFromUsernamePasswordWithIpv6Address() {
    String username = "admin";
    String password = "password";
    String ip = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

    AuthenticationToken token = factory.fromUsernamePassword(username, password, ip);

    assertThat(token, is(notNullValue()));
    BaseAuthenticationToken baseToken = (BaseAuthenticationToken) token;
    // IPv6 addresses should be formatted with brackets
    assertThat(baseToken.getIpAddress().startsWith("["), is(true));
    assertThat(baseToken.getIpAddress().endsWith("]"), is(true));
  }

  @Test
  public void testFromCertificates() {
    X509Certificate[] certs = createTestCertificates();
    String ip = "127.0.0.1";

    AuthenticationToken token = factory.fromCertificates(certs, ip);

    assertThat(token, is(notNullValue()));
    assertThat(token, instanceOf(BaseAuthenticationToken.class));

    BaseAuthenticationToken baseToken = (BaseAuthenticationToken) token;
    assertThat(baseToken.getPrincipal(), is(certs[0].getSubjectX500Principal()));
    assertThat(baseToken.getType(), is(AuthenticationTokenType.PKI));
    assertThat(baseToken.getIpAddress(), is(ip));
    // Certs are stored as credentials in the token
    assertThat(baseToken.getCredentials(), is(certs));
  }

  @Test
  public void testFromCertificatesWithNullCerts() {
    String ip = "127.0.0.1";

    AuthenticationToken token = factory.fromCertificates(null, ip);

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testFromCertificatesWithEmptyCerts() {
    X509Certificate[] certs = new X509Certificate[0];
    String ip = "127.0.0.1";

    AuthenticationToken token = factory.fromCertificates(certs, ip);

    assertThat(token, is(nullValue()));
  }

  @Test
  public void testFromCertificatesWithMultipleCerts() {
    X509Certificate[] certs = createMultipleTestCertificates();
    String ip = "127.0.0.1";

    AuthenticationToken token = factory.fromCertificates(certs, ip);

    assertThat(token, is(notNullValue()));
    BaseAuthenticationToken baseToken = (BaseAuthenticationToken) token;
    // Should use the first certificate's subject
    assertThat(baseToken.getPrincipal(), is(certs[0].getSubjectX500Principal()));
    X509Certificate[] retrievedCerts = (X509Certificate[]) baseToken.getCredentials();
    assertThat(retrievedCerts.length, is(certs.length));
  }

  @Test
  public void testFromCertificatesWithIpv6Address() {
    X509Certificate[] certs = createTestCertificates();
    String ip = "::1";

    AuthenticationToken token = factory.fromCertificates(certs, ip);

    assertThat(token, is(notNullValue()));
    BaseAuthenticationToken baseToken = (BaseAuthenticationToken) token;
    // IPv6 loopback should be formatted with brackets
    assertThat(baseToken.getIpAddress(), is("[::1]"));
  }

  @Test
  public void testFromUsernamePasswordWithSpecialCharacters() {
    String username = "user@domain.com";
    String password = "p@ssw0rd!#$%";
    String ip = "192.168.1.1";

    AuthenticationToken token = factory.fromUsernamePassword(username, password, ip);

    assertThat(token, is(notNullValue()));
    BaseAuthenticationToken baseToken = (BaseAuthenticationToken) token;
    assertThat(baseToken.getPrincipal(), is(username));
    assertThat(baseToken.getIpAddress(), is(ip));
  }

  @Test
  public void testFromUsernamePasswordWithNullIpAddress() {
    String username = "admin";
    String password = "password";

    AuthenticationToken token = factory.fromUsernamePassword(username, password, null);

    assertThat(token, is(notNullValue()));
    BaseAuthenticationToken baseToken = (BaseAuthenticationToken) token;
    assertThat(baseToken.getPrincipal(), is(username));
    assertThat(baseToken.getIpAddress(), is(nullValue()));
  }

  @Test
  public void testFromCertificatesWithNullIpAddress() {
    X509Certificate[] certs = createTestCertificates();

    AuthenticationToken token = factory.fromCertificates(certs, null);

    assertThat(token, is(notNullValue()));
    BaseAuthenticationToken baseToken = (BaseAuthenticationToken) token;
    assertThat(baseToken.getIpAddress(), is(nullValue()));
  }

  private X509Certificate[] createTestCertificates() {
    X509Certificate cert = mock(X509Certificate.class);
    X500Principal principal = new X500Principal("CN=Test User, OU=Security, O=DDF, C=US");
    when(cert.getSubjectX500Principal()).thenReturn(principal);
    return new X509Certificate[] {cert};
  }

  private X509Certificate[] createMultipleTestCertificates() {
    X509Certificate cert1 = mock(X509Certificate.class);
    X509Certificate cert2 = mock(X509Certificate.class);
    X500Principal principal = new X500Principal("CN=Test User, OU=Security, O=DDF, C=US");
    when(cert1.getSubjectX500Principal()).thenReturn(principal);
    // cert2 doesn't need stubbing since only cert1's principal is accessed
    return new X509Certificate[] {cert1, cert2};
  }
}
