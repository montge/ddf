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
package org.codice.ddf.security.oidc.realm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.apache.shiro.authc.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.profile.OidcProfile;

@RunWith(MockitoJUnitRunner.class)
public class CustomOidcProfileCreatorTest {

  private CustomOidcProfileCreator profileCreator;
  private Algorithm validAlgorithm;

  @Mock private OidcConfiguration oidcConfiguration;
  @Mock private OidcClient oidcClient;
  @Mock private WebContext webContext;

  @Before
  public void setup() throws Exception {
    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
    gen.initialize(2048);
    KeyPair keyPair = gen.generateKeyPair();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    validAlgorithm = Algorithm.RSA256(publicKey, privateKey);

    when(oidcConfiguration.getTokenExpirationAdvance()).thenReturn(0);

    profileCreator = new CustomOidcProfileCreator(oidcConfiguration, oidcClient);
  }

  @Test
  public void testCreateWithAccessTokenAndIdToken() throws Exception {
    String idToken = createIdToken();
    JWT jwt = SignedJWT.parse(idToken);
    AccessToken accessToken = new BearerAccessToken("access-token-value");

    OidcCredentials credentials = mock(OidcCredentials.class);
    when(credentials.getIdToken()).thenReturn(jwt);
    when(credentials.getAccessToken()).thenReturn(accessToken);

    Optional<UserProfile> profile = profileCreator.create(credentials, webContext);

    assertTrue(profile.isPresent());
    OidcProfile oidcProfile = (OidcProfile) profile.get();
    assertNotNull(oidcProfile);
    assertNotNull(oidcProfile.getAccessToken());
    assertThat(oidcProfile.getAccessToken().getValue(), is(equalTo("access-token-value")));
  }

  @Test
  public void testCreateWithRefreshToken() throws Exception {
    String idToken = createIdToken();
    JWT jwt = SignedJWT.parse(idToken);
    AccessToken accessToken = new BearerAccessToken("access-token-value");
    RefreshToken refreshToken = new RefreshToken("refresh-token-value");

    OidcCredentials credentials = mock(OidcCredentials.class);
    when(credentials.getIdToken()).thenReturn(jwt);
    when(credentials.getAccessToken()).thenReturn(accessToken);
    when(credentials.getRefreshToken()).thenReturn(refreshToken);

    Optional<UserProfile> profile = profileCreator.create(credentials, webContext);

    assertTrue(profile.isPresent());
    OidcProfile oidcProfile = (OidcProfile) profile.get();
    assertNotNull(oidcProfile.getRefreshToken());
    assertThat(oidcProfile.getRefreshToken().getValue(), is(equalTo("refresh-token-value")));
  }

  @Test
  public void testCreateWithoutAccessToken() throws Exception {
    String idToken = createIdToken();
    JWT jwt = SignedJWT.parse(idToken);

    OidcCredentials credentials = mock(OidcCredentials.class);
    when(credentials.getIdToken()).thenReturn(jwt);
    when(credentials.getAccessToken()).thenReturn(null);

    Optional<UserProfile> profile = profileCreator.create(credentials, webContext);

    assertTrue(profile.isPresent());
    OidcProfile oidcProfile = (OidcProfile) profile.get();
    assertNotNull(oidcProfile);
  }

  @Test
  public void testCreateWithoutRefreshToken() throws Exception {
    String idToken = createIdToken();
    JWT jwt = SignedJWT.parse(idToken);
    AccessToken accessToken = new BearerAccessToken("access-token-value");

    OidcCredentials credentials = mock(OidcCredentials.class);
    when(credentials.getIdToken()).thenReturn(jwt);
    when(credentials.getAccessToken()).thenReturn(accessToken);
    when(credentials.getRefreshToken()).thenReturn(null);

    Optional<UserProfile> profile = profileCreator.create(credentials, webContext);

    assertTrue(profile.isPresent());
    OidcProfile oidcProfile = (OidcProfile) profile.get();
    assertNotNull(oidcProfile);
  }

  @Test
  public void testCreateExtractsIdTokenClaims() throws Exception {
    String idToken = createIdTokenWithClaims();
    JWT jwt = SignedJWT.parse(idToken);

    OidcCredentials credentials = mock(OidcCredentials.class);
    when(credentials.getIdToken()).thenReturn(jwt);

    Optional<UserProfile> profile = profileCreator.create(credentials, webContext);

    assertTrue(profile.isPresent());
    OidcProfile oidcProfile = (OidcProfile) profile.get();
    assertNotNull(oidcProfile.getId());
    assertThat(oidcProfile.getAttribute("email"), is(notNullValue()));
    assertThat(oidcProfile.getAttribute("name"), is(notNullValue()));
  }

  @Test
  public void testCreateSetsIdTokenString() throws Exception {
    String idToken = createIdToken();
    JWT jwt = SignedJWT.parse(idToken);

    OidcCredentials credentials = mock(OidcCredentials.class);
    when(credentials.getIdToken()).thenReturn(jwt);

    Optional<UserProfile> profile = profileCreator.create(credentials, webContext);

    assertTrue(profile.isPresent());
    OidcProfile oidcProfile = (OidcProfile) profile.get();
    assertNotNull(oidcProfile.getIdTokenString());
  }

  @Test(expected = AuthenticationException.class)
  public void testCreateWithInvalidJWT() throws Exception {
    JWT jwt = mock(JWT.class);
    when(jwt.getJWTClaimsSet()).thenThrow(new java.text.ParseException("Invalid JWT", 0));
    when(jwt.getParsedString()).thenReturn("invalid-jwt-string");

    OidcCredentials credentials = mock(OidcCredentials.class);
    when(credentials.getIdToken()).thenReturn(jwt);

    profileCreator.create(credentials, webContext);
  }

  @Test
  public void testCreateWithMultipleClaims() throws Exception {
    String idToken =
        com.auth0
            .jwt
            .JWT
            .create()
            .withJWTId(UUID.randomUUID().toString())
            .withExpiresAt(new Date(Instant.now().plus(Duration.ofDays(1)).toEpochMilli()))
            .withIssuedAt(new Date())
            .withIssuer("http://localhost:8080/auth/realms/master")
            .withAudience("ddf-client")
            .withSubject("user123")
            .withClaim("email", "user@example.com")
            .withClaim("name", "Test User")
            .withClaim("given_name", "Test")
            .withClaim("family_name", "User")
            .withClaim("preferred_username", "testuser")
            .withClaim("custom_claim", "custom_value")
            .sign(validAlgorithm);

    JWT jwt = SignedJWT.parse(idToken);

    OidcCredentials credentials = mock(OidcCredentials.class);
    when(credentials.getIdToken()).thenReturn(jwt);

    Optional<UserProfile> profile = profileCreator.create(credentials, webContext);

    assertTrue(profile.isPresent());
    OidcProfile oidcProfile = (OidcProfile) profile.get();
    assertThat(oidcProfile.getAttribute("email"), is(equalTo("user@example.com")));
    assertThat(oidcProfile.getAttribute("name"), is(equalTo("Test User")));
    assertThat(oidcProfile.getAttribute("given_name"), is(equalTo("Test")));
    assertThat(oidcProfile.getAttribute("family_name"), is(equalTo("User")));
    assertThat(oidcProfile.getAttribute("preferred_username"), is(equalTo("testuser")));
    assertThat(oidcProfile.getAttribute("custom_claim"), is(equalTo("custom_value")));
  }

  private String createIdToken() {
    return com.auth0
        .jwt
        .JWT
        .create()
        .withJWTId(UUID.randomUUID().toString())
        .withExpiresAt(new Date(Instant.now().plus(Duration.ofDays(1)).toEpochMilli()))
        .withIssuedAt(new Date())
        .withIssuer("http://localhost:8080/auth/realms/master")
        .withAudience("ddf-client")
        .withSubject("subject123")
        .sign(validAlgorithm);
  }

  private String createIdTokenWithClaims() {
    return com.auth0
        .jwt
        .JWT
        .create()
        .withJWTId(UUID.randomUUID().toString())
        .withExpiresAt(new Date(Instant.now().plus(Duration.ofDays(1)).toEpochMilli()))
        .withIssuedAt(new Date())
        .withIssuer("http://localhost:8080/auth/realms/master")
        .withAudience("ddf-client")
        .withSubject("subject123")
        .withClaim("email", "test@example.com")
        .withClaim("name", "Test User")
        .withClaim("preferred_username", "testuser")
        .sign(validAlgorithm);
  }
}
