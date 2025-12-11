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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TokenStorage} interface contract. Uses a test implementation to verify the
 * interface behavior and contract expectations.
 */
public class TokenStorageTest {

  private static final String USER_ID = "testUser";
  private static final String SOURCE_ID = "testSource";
  private static final String ACCESS_TOKEN_VALUE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...";
  private static final String REFRESH_TOKEN_VALUE = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...";
  private static final String DISCOVERY_URL_VALUE = "https://oauth.example.com/.well-known/openid";

  private TestTokenStorage tokenStorage;

  @BeforeEach
  public void setUp() {
    tokenStorage = new TestTokenStorage();
  }

  @Test
  public void testTokenStorageConstants() {
    assertThat(TokenStorage.ACCESS_TOKEN, is("access_token"));
    assertThat(TokenStorage.REFRESH_TOKEN, is("refresh_token"));
    assertThat(TokenStorage.DISCOVERY_URL, is("discovery_url"));
    assertThat(TokenStorage.EXPIRES_AT, is("expires_at"));
    assertThat(TokenStorage.CLIENT_ID, is("client_id"));
    assertThat(TokenStorage.SECRET, is("client_secret"));
    assertThat(TokenStorage.SOURCE_ID, is("source_id"));
    assertThat(TokenStorage.STATE, is("state"));
  }

  @Test
  public void testGetStateMapEmpty() {
    Map<String, Map<String, Object>> stateMap = tokenStorage.getStateMap();
    assertThat(stateMap, is(notNullValue()));
    assertThat(stateMap.entrySet(), is(empty()));
  }

  @Test
  public void testCreateNewToken() {
    int status =
        tokenStorage.create(
            USER_ID, SOURCE_ID, ACCESS_TOKEN_VALUE, REFRESH_TOKEN_VALUE, DISCOVERY_URL_VALUE);
    assertThat(status, is(201)); // HTTP 201 Created
  }

  @Test
  public void testCreateUpdateExistingToken() {
    tokenStorage.create(
        USER_ID, SOURCE_ID, ACCESS_TOKEN_VALUE, REFRESH_TOKEN_VALUE, DISCOVERY_URL_VALUE);
    int status =
        tokenStorage.create(
            USER_ID, SOURCE_ID, "newAccessToken", "newRefreshToken", DISCOVERY_URL_VALUE);
    assertThat(status, is(200)); // HTTP 200 OK for update
  }

  @Test
  public void testReadTokenInformation() {
    tokenStorage.create(
        USER_ID, SOURCE_ID, ACCESS_TOKEN_VALUE, REFRESH_TOKEN_VALUE, DISCOVERY_URL_VALUE);

    TokenInformation tokenInfo = tokenStorage.read(USER_ID);
    assertThat(tokenInfo, is(notNullValue()));
    assertThat(tokenInfo.getId(), is(USER_ID));
  }

  @Test
  public void testReadReturnsNullForNonExistentUser() {
    TokenInformation tokenInfo = tokenStorage.read("nonexistent");
    assertThat(tokenInfo, is(nullValue()));
  }

  @Test
  public void testReadTokenEntryForSource() {
    tokenStorage.create(
        USER_ID, SOURCE_ID, ACCESS_TOKEN_VALUE, REFRESH_TOKEN_VALUE, DISCOVERY_URL_VALUE);

    TokenInformation.TokenEntry entry = tokenStorage.read(USER_ID, SOURCE_ID);
    assertThat(entry, is(notNullValue()));
    assertThat(entry.getAccessToken(), is(ACCESS_TOKEN_VALUE));
    assertThat(entry.getRefreshToken(), is(REFRESH_TOKEN_VALUE));
    assertThat(entry.getDiscoveryUrl(), is(DISCOVERY_URL_VALUE));
  }

  @Test
  public void testReadTokenEntryReturnsNullForNonExistentSource() {
    tokenStorage.create(
        USER_ID, SOURCE_ID, ACCESS_TOKEN_VALUE, REFRESH_TOKEN_VALUE, DISCOVERY_URL_VALUE);

    TokenInformation.TokenEntry entry = tokenStorage.read(USER_ID, "nonexistentSource");
    assertThat(entry, is(nullValue()));
  }

  @Test
  public void testIsAvailableReturnsTrue() {
    tokenStorage.create(
        USER_ID, SOURCE_ID, ACCESS_TOKEN_VALUE, REFRESH_TOKEN_VALUE, DISCOVERY_URL_VALUE);
    assertThat(tokenStorage.isAvailable(USER_ID, SOURCE_ID), is(true));
  }

  @Test
  public void testIsAvailableReturnsFalseForNonExistent() {
    assertThat(tokenStorage.isAvailable(USER_ID, SOURCE_ID), is(false));
  }

  @Test
  public void testDeleteAllTokensForUser() {
    tokenStorage.create(
        USER_ID, SOURCE_ID, ACCESS_TOKEN_VALUE, REFRESH_TOKEN_VALUE, DISCOVERY_URL_VALUE);

    int status = tokenStorage.delete(USER_ID);
    assertThat(status, is(200)); // HTTP 200 OK
    assertThat(tokenStorage.read(USER_ID), is(nullValue()));
  }

  @Test
  public void testDeleteTokensForSpecificSource() {
    tokenStorage.create(
        USER_ID, SOURCE_ID, ACCESS_TOKEN_VALUE, REFRESH_TOKEN_VALUE, DISCOVERY_URL_VALUE);
    tokenStorage.create(USER_ID, "anotherSource", "token2", "refresh2", DISCOVERY_URL_VALUE);

    int status = tokenStorage.delete(USER_ID, SOURCE_ID);
    assertThat(status, is(200));
    assertThat(tokenStorage.isAvailable(USER_ID, SOURCE_ID), is(false));
    assertThat(tokenStorage.isAvailable(USER_ID, "anotherSource"), is(true));
  }

  @Test
  public void testDeleteReturns404ForNonExistent() {
    int status = tokenStorage.delete("nonexistent");
    assertThat(status, is(404)); // HTTP 404 Not Found
  }

  @Test
  public void testGetStateMapWithEntries() {
    tokenStorage.addStateEntry("state123", USER_ID, SOURCE_ID, DISCOVERY_URL_VALUE);
    Map<String, Map<String, Object>> stateMap = tokenStorage.getStateMap();

    assertThat(stateMap, hasKey("state123"));
    assertThat(stateMap.get("state123").get(TokenStorage.SOURCE_ID), is(SOURCE_ID));
  }

  /** Simple test implementation of TokenStorage interface. */
  private static class TestTokenStorage implements TokenStorage {
    private final Map<String, TestTokenInformation> tokens = new HashMap<>();
    private final Map<String, Map<String, Object>> stateMap = new HashMap<>();

    public void addStateEntry(String state, String id, String sourceId, String discoveryUrl) {
      Map<String, Object> entry = new HashMap<>();
      entry.put(SOURCE_ID, sourceId);
      entry.put(DISCOVERY_URL, discoveryUrl);
      stateMap.put(state, entry);
    }

    @Override
    public Map<String, Map<String, Object>> getStateMap() {
      return stateMap;
    }

    @Override
    public int create(
        String id, String sourceId, String accessToken, String refreshToken, String discoveryUrl) {
      boolean isNew = !tokens.containsKey(id);
      TestTokenInformation tokenInfo = tokens.computeIfAbsent(id, TestTokenInformation::new);
      tokenInfo.addEntry(sourceId, accessToken, refreshToken, discoveryUrl);
      return isNew ? 201 : 200;
    }

    @Override
    public TokenInformation read(String id) {
      return tokens.get(id);
    }

    @Override
    public TokenInformation.TokenEntry read(String id, String sourceId) {
      TestTokenInformation tokenInfo = tokens.get(id);
      if (tokenInfo == null) {
        return null;
      }
      return tokenInfo.getTokenEntries().get(sourceId);
    }

    @Override
    public boolean isAvailable(String id, String sourceId) {
      TestTokenInformation tokenInfo = tokens.get(id);
      return tokenInfo != null && tokenInfo.getTokenEntries().containsKey(sourceId);
    }

    @Override
    public int delete(String id) {
      if (tokens.remove(id) != null) {
        return 200;
      }
      return 404;
    }

    @Override
    public int delete(String id, String sourceId) {
      TestTokenInformation tokenInfo = tokens.get(id);
      if (tokenInfo != null) {
        tokenInfo.getTokenEntries().remove(sourceId);
        return 200;
      }
      return 404;
    }
  }

  /** Simple test implementation of TokenInformation. */
  private static class TestTokenInformation implements TokenInformation {
    private final String id;
    private final Map<String, TokenEntry> entries = new HashMap<>();

    public TestTokenInformation(String id) {
      this.id = id;
    }

    public void addEntry(
        String sourceId, String accessToken, String refreshToken, String discoveryUrl) {
      entries.put(sourceId, new TestTokenEntry(accessToken, refreshToken, discoveryUrl));
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public Map<String, TokenEntry> getTokenEntries() {
      return entries;
    }

    @Override
    public Set<String> getDiscoveryUrls() {
      Set<String> urls = new HashSet<>();
      for (TokenEntry entry : entries.values()) {
        urls.add(entry.getDiscoveryUrl());
      }
      return urls;
    }

    @Override
    public String getTokenJson() {
      return "{}";
    }
  }

  /** Simple test implementation of TokenEntry. */
  private static class TestTokenEntry implements TokenInformation.TokenEntry {
    private final String accessToken;
    private final String refreshToken;
    private final String discoveryUrl;

    public TestTokenEntry(String accessToken, String refreshToken, String discoveryUrl) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.discoveryUrl = discoveryUrl;
    }

    @Override
    public String getAccessToken() {
      return accessToken;
    }

    @Override
    public String getRefreshToken() {
      return refreshToken;
    }

    @Override
    public String getDiscoveryUrl() {
      return discoveryUrl;
    }
  }
}
