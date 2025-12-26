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
package org.codice.ddf.security.token.storage.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TokenInformationImplTest {

  @Test
  void testConstructorAndGetters() {
    String id = "user123";
    Map<String, TokenInformation.TokenEntry> tokenEntryMap = new HashMap<>();
    Set<String> discoveryUrls = new HashSet<>();
    discoveryUrls.add("https://idp.example.com/.well-known");
    String json = "{\"token\":\"value\"}";

    TokenInformationImpl tokenInfo =
        new TokenInformationImpl(id, tokenEntryMap, discoveryUrls, json);

    assertThat(tokenInfo.getId(), is(id));
    assertThat(tokenInfo.getTokenEntries(), is(tokenEntryMap));
    assertThat(
        tokenInfo.getDiscoveryUrls(), containsInAnyOrder("https://idp.example.com/.well-known"));
    assertThat(tokenInfo.getTokenJson(), is(json));
  }

  @Test
  void testGetIdReturnsCorrectValue() {
    TokenInformationImpl tokenInfo =
        new TokenInformationImpl("testUser", new HashMap<>(), new HashSet<>(), null);

    assertThat(tokenInfo.getId(), is("testUser"));
  }

  @Test
  void testGetTokenEntriesReturnsMap() {
    Map<String, TokenInformation.TokenEntry> tokenEntryMap = new HashMap<>();
    TokenInformationImpl.TokenEntryImpl entry =
        new TokenInformationImpl.TokenEntryImpl("access", "refresh", "https://discovery.url");
    tokenEntryMap.put("source1", entry);

    TokenInformationImpl tokenInfo =
        new TokenInformationImpl("user", tokenEntryMap, new HashSet<>(), null);

    assertThat(tokenInfo.getTokenEntries(), hasEntry("source1", entry));
  }

  @Test
  void testGetDiscoveryUrlsReturnsSet() {
    Set<String> discoveryUrls = new HashSet<>();
    discoveryUrls.add("https://url1.com");
    discoveryUrls.add("https://url2.com");

    TokenInformationImpl tokenInfo =
        new TokenInformationImpl("user", new HashMap<>(), discoveryUrls, null);

    assertThat(
        tokenInfo.getDiscoveryUrls(), containsInAnyOrder("https://url1.com", "https://url2.com"));
  }

  @Test
  void testGetTokenJsonReturnsJson() {
    String json = "{\"access_token\":\"abc123\"}";

    TokenInformationImpl tokenInfo =
        new TokenInformationImpl("user", new HashMap<>(), new HashSet<>(), json);

    assertThat(tokenInfo.getTokenJson(), is(json));
  }

  @Test
  void testNullValues() {
    TokenInformationImpl tokenInfo = new TokenInformationImpl(null, null, null, null);

    assertThat(tokenInfo.getId(), is(nullValue()));
    assertThat(tokenInfo.getTokenEntries(), is(nullValue()));
    assertThat(tokenInfo.getDiscoveryUrls(), is(nullValue()));
    assertThat(tokenInfo.getTokenJson(), is(nullValue()));
  }

  @Test
  void testTokenEntryImplConstructorAndGetters() {
    String accessToken = "access_token_value";
    String refreshToken = "refresh_token_value";
    String discoveryUrl = "https://idp.example.com/.well-known/openid-configuration";

    TokenInformationImpl.TokenEntryImpl entry =
        new TokenInformationImpl.TokenEntryImpl(accessToken, refreshToken, discoveryUrl);

    assertThat(entry.getAccessToken(), is(accessToken));
    assertThat(entry.getRefreshToken(), is(refreshToken));
    assertThat(entry.getDiscoveryUrl(), is(discoveryUrl));
  }

  @Test
  void testTokenEntryImplWithNullValues() {
    TokenInformationImpl.TokenEntryImpl entry =
        new TokenInformationImpl.TokenEntryImpl(null, null, null);

    assertThat(entry.getAccessToken(), is(nullValue()));
    assertThat(entry.getRefreshToken(), is(nullValue()));
    assertThat(entry.getDiscoveryUrl(), is(nullValue()));
  }

  @Test
  void testTokenEntryImplPartialValues() {
    TokenInformationImpl.TokenEntryImpl entry =
        new TokenInformationImpl.TokenEntryImpl("access_only", null, "https://discovery.url");

    assertThat(entry.getAccessToken(), is("access_only"));
    assertThat(entry.getRefreshToken(), is(nullValue()));
    assertThat(entry.getDiscoveryUrl(), is("https://discovery.url"));
  }
}
