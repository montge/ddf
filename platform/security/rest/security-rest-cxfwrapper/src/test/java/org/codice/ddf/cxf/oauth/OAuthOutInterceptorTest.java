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
package org.codice.ddf.cxf.oauth;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OAuthOutInterceptorTest {

  private OAuthOutInterceptor interceptor;
  private Message mockMessage;
  private Map<String, List<String>> headers;

  @BeforeEach
  void setUp() {
    interceptor = new OAuthOutInterceptor(Phase.PRE_STREAM);
    mockMessage = mock(Message.class);
    headers = new HashMap<>();
    when(mockMessage.get(Message.PROTOCOL_HEADERS)).thenReturn(headers);
  }

  @Test
  void testHandleMessageMovesOAuthHeaderToAuthorization() {
    List<String> oauthToken = new ArrayList<>(Arrays.asList("Bearer test-token-123"));
    headers.put(OAuthSecurity.OAUTH, oauthToken);

    interceptor.handleMessage(mockMessage);

    assertThat(headers.get(AUTHORIZATION), is(oauthToken));
    assertThat(headers.containsKey(OAuthSecurity.OAUTH), is(false));
  }

  @Test
  void testHandleMessageDoesNothingWhenNoOAuthHeader() {
    headers.put(AUTHORIZATION, Arrays.asList("Basic existing-auth"));

    interceptor.handleMessage(mockMessage);

    assertThat(headers.get(AUTHORIZATION), hasItem("Basic existing-auth"));
    assertThat(headers.containsKey(OAuthSecurity.OAUTH), is(false));
  }

  @Test
  void testHandleMessageDoesNothingWhenOAuthHeaderIsNull() {
    headers.put(OAuthSecurity.OAUTH, null);

    interceptor.handleMessage(mockMessage);

    assertThat(headers.get(AUTHORIZATION), is(nullValue()));
  }

  @Test
  void testHandleMessageDoesNothingWhenOAuthHeaderIsEmpty() {
    headers.put(OAuthSecurity.OAUTH, new ArrayList<>());

    interceptor.handleMessage(mockMessage);

    assertThat(headers.get(AUTHORIZATION), is(nullValue()));
  }

  @Test
  void testHandleMessageRemovesOAuthHeader() {
    headers.put(OAuthSecurity.OAUTH, Arrays.asList("Bearer token"));

    interceptor.handleMessage(mockMessage);

    assertThat(headers.containsKey(OAuthSecurity.OAUTH), is(false));
  }

  @Test
  void testHandleMessagePreservesOtherHeaders() {
    headers.put(OAuthSecurity.OAUTH, Arrays.asList("Bearer token"));
    headers.put("Content-Type", Arrays.asList("application/json"));
    headers.put("Accept", Arrays.asList("application/xml"));

    interceptor.handleMessage(mockMessage);

    assertThat(headers.get("Content-Type"), hasItem("application/json"));
    assertThat(headers.get("Accept"), hasItem("application/xml"));
  }

  @Test
  void testHandleMessageOverwritesExistingAuthorizationHeader() {
    headers.put(OAuthSecurity.OAUTH, Arrays.asList("Bearer new-token"));
    headers.put(AUTHORIZATION, Arrays.asList("Bearer old-token"));

    interceptor.handleMessage(mockMessage);

    assertThat(headers.get(AUTHORIZATION), hasItem("Bearer new-token"));
    assertThat(headers.get(AUTHORIZATION), not(hasItem("Bearer old-token")));
  }

  @Test
  void testHandleMessageWithMultipleTokenValues() {
    List<String> multipleTokens = Arrays.asList("Bearer token1", "Bearer token2");
    headers.put(OAuthSecurity.OAUTH, multipleTokens);

    interceptor.handleMessage(mockMessage);

    assertThat(headers.get(AUTHORIZATION), is(multipleTokens));
    assertThat(headers.get(AUTHORIZATION).size(), is(2));
  }

  @Test
  void testConstructorSetsPhase() {
    OAuthOutInterceptor interceptorWithPhase = new OAuthOutInterceptor(Phase.SEND);

    assertThat(interceptorWithPhase.getPhase(), is(Phase.SEND));
  }

  @Test
  void testHandleMessageWithEmptyHeaders() {
    interceptor.handleMessage(mockMessage);

    assertThat(headers.isEmpty(), is(true));
  }
}
