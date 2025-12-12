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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ddf.security.audit.SecurityLogger;
import ddf.security.principal.impl.GuestPrincipal;
import org.junit.jupiter.api.Test;

public class GuestAuthenticationTokenTest {

  @Test
  public void testConstructorWithIpAddress() {
    SecurityLogger securityLogger = mock(SecurityLogger.class);

    GuestAuthenticationToken token = new GuestAuthenticationToken("127.0.0.1", securityLogger);

    assertThat(token.getPrincipal(), is(instanceOf(GuestPrincipal.class)));
    assertThat(token.getCredentials(), is(GuestAuthenticationToken.GUEST_CREDENTIALS));
    verify(securityLogger).audit(anyString());
  }

  @Test
  public void testConstructorWithNameAndIp() {
    SecurityLogger securityLogger = mock(SecurityLogger.class);
    // GuestPrincipal uses "@" as delimiter and prepends "Guest@"
    String name = "Guest@192.168.1.100";

    GuestAuthenticationToken token = new GuestAuthenticationToken(name, securityLogger);

    assertThat(token.getPrincipal(), is(instanceOf(GuestPrincipal.class)));
    assertThat(token.getIpAddress(), is("192.168.1.100"));
    verify(securityLogger).audit(anyString());
  }

  @Test
  public void testConstructorWithEmptyName() {
    SecurityLogger securityLogger = mock(SecurityLogger.class);

    GuestAuthenticationToken token = new GuestAuthenticationToken("", securityLogger);

    assertThat(token.getPrincipal(), is(instanceOf(GuestPrincipal.class)));
    verify(securityLogger, never()).audit(anyString());
  }

  @Test
  public void testConstructorWithNullName() {
    SecurityLogger securityLogger = mock(SecurityLogger.class);

    GuestAuthenticationToken token = new GuestAuthenticationToken(null, securityLogger);

    assertThat(token.getPrincipal(), is(instanceOf(GuestPrincipal.class)));
    verify(securityLogger, never()).audit(anyString());
  }

  @Test
  public void testParseAddressFromNameWithDelimiter() {
    // GuestPrincipal uses "@" as delimiter
    String result = GuestAuthenticationToken.parseAddressFromName("Guest@10.0.0.1");

    assertThat(result, is("10.0.0.1"));
  }

  @Test
  public void testParseAddressFromNameWithoutDelimiter() {
    String result = GuestAuthenticationToken.parseAddressFromName("justAnIp");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testParseAddressFromNameWithNull() {
    String result = GuestAuthenticationToken.parseAddressFromName(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testParseAddressFromNameWithEmpty() {
    String result = GuestAuthenticationToken.parseAddressFromName("");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testParseAddressFromNameWithIPv6() {
    // GuestPrincipal uses "@" as delimiter
    String result = GuestAuthenticationToken.parseAddressFromName("Guest@::1");

    assertThat(result, is("[::1]"));
  }

  @Test
  public void testParseAddressFromNameWithFullIPv6() {
    String result =
        GuestAuthenticationToken.parseAddressFromName(
            "Guest@2001:0db8:85a3:0000:0000:8a2e:0370:7334");

    assertThat(result, is("[2001:0db8:85a3:0000:0000:8a2e:0370:7334]"));
  }

  @Test
  public void testParseAddressFromNameWithAlreadyBracketedIPv6() {
    String result = GuestAuthenticationToken.parseAddressFromName("Guest@[::1]");

    assertThat(result, is("[::1]"));
  }

  @Test
  public void testGetIpAddressWithGuestPrincipal() {
    SecurityLogger securityLogger = mock(SecurityLogger.class);
    // When you pass "172.16.0.1", GuestPrincipal creates "Guest@172.16.0.1"
    GuestAuthenticationToken token = new GuestAuthenticationToken("172.16.0.1", securityLogger);

    assertThat(token.getIpAddress(), is("172.16.0.1"));
  }

  @Test
  public void testGetCredentialsAsString() {
    SecurityLogger securityLogger = mock(SecurityLogger.class);
    GuestAuthenticationToken token = new GuestAuthenticationToken("127.0.0.1", securityLogger);

    assertThat(token.getCredentialsAsString(), is("Guest"));
  }

  @Test
  public void testToString() {
    SecurityLogger securityLogger = mock(SecurityLogger.class);
    GuestAuthenticationToken token = new GuestAuthenticationToken("192.168.0.1", securityLogger);

    String result = token.toString();

    assertThat(result, containsString("Guest IP:"));
    assertThat(result, containsString("192.168.0.1"));
  }

  @Test
  public void testToStringWithIpOnlyInput() {
    SecurityLogger securityLogger = mock(SecurityLogger.class);
    // When input doesn't have delimiter, GuestPrincipal prepends "Guest@"
    // so getIpAddress still works
    GuestAuthenticationToken token = new GuestAuthenticationToken("10.0.0.1", securityLogger);

    String result = token.toString();

    assertThat(result, containsString("Guest IP:"));
    assertThat(result, containsString("10.0.0.1"));
  }

  @Test
  public void testGuestCredentialsConstant() {
    assertThat(GuestAuthenticationToken.GUEST_CREDENTIALS, is("Guest"));
  }

  @Test
  public void testLegacyConstructorBehavior() {
    GuestAuthenticationToken token =
        new GuestAuthenticationToken("127.0.0.1", mock(SecurityLogger.class));
    assertTrue(token.getPrincipal() instanceof GuestPrincipal);
    assertEquals(GuestAuthenticationToken.GUEST_CREDENTIALS, token.getCredentials());
    assertEquals(token.getIpAddress(), "127.0.0.1");
  }
}
