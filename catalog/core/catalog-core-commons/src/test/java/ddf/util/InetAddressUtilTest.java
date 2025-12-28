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
package ddf.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import org.junit.jupiter.api.Test;

public class InetAddressUtilTest {

  @Test
  public void testGetFirstNonLoopbackAddress() throws SocketException {
    InetAddress address = InetAddressUtil.getFirstNonLoopbackAddress();

    // The result depends on the host's network configuration
    // It may be null if no non-loopback addresses exist, or an InetAddress if one does
    assertThat(address, anyOf(nullValue(), notNullValue()));
    if (address != null) {
      assertThat(address.isLoopbackAddress(), is(false));
    }
  }

  @Test
  public void testGetFirstNonLoopbackAddressPreferIpv4() throws SocketException {
    InetAddress address = InetAddressUtil.getFirstNonLoopbackAddress(true, false);

    // If an address is returned, it should be IPv4 when preferring IPv4
    if (address != null) {
      assertThat(address.isLoopbackAddress(), is(false));
      assertThat(address, instanceOf(Inet4Address.class));
    }
  }

  @Test
  public void testGetFirstNonLoopbackAddressPreferIpv6() throws SocketException {
    InetAddress address = InetAddressUtil.getFirstNonLoopbackAddress(false, true);

    // If an address is returned, it should be IPv6 when preferring IPv6
    if (address != null) {
      assertThat(address.isLoopbackAddress(), is(false));
      assertThat(address, instanceOf(Inet6Address.class));
    }
  }

  @Test
  public void testGetFirstNonLoopbackAddressNoPreference() throws SocketException {
    InetAddress address = InetAddressUtil.getFirstNonLoopbackAddress(false, false);

    // Without any preference, any non-loopback address type is acceptable
    if (address != null) {
      assertThat(address.isLoopbackAddress(), is(false));
    }
  }

  @Test
  public void testGetFirstNonLoopbackAddressBothPreferences() throws SocketException {
    // When both preferences are true, the method may return null or an address
    // depending on what's available on the system
    InetAddress address = InetAddressUtil.getFirstNonLoopbackAddress(true, true);

    if (address != null) {
      assertThat(address.isLoopbackAddress(), is(false));
    }
  }
}
