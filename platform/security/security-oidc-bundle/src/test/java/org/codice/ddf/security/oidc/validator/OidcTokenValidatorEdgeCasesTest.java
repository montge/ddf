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
package org.codice.ddf.security.oidc.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pac4j.core.context.HttpConstants.APPLICATION_JSON;

import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OidcTokenValidatorEdgeCasesTest {

  private static final String NONCE_SESSION_ATTRIBUTE = "session-attribute";

  @Mock private ResourceRetriever resourceRetriever;
  @Mock private OidcConfiguration configuration;
  @Mock private OIDCProviderMetadata oidcProviderMetadata;
  @Mock private OidcClient<OidcConfiguration> oidcClient;

  private Algorithm validAlgorithm;

  @Before
  public void setup() throws Exception {
    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
    gen.initialize(2048);
    KeyPair keyPair = gen.generateKeyPair();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

    JWK sigJwk =
        new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .build();

    String jwk = "{\"keys\": [" + sigJwk.toPublicJWK().toJSONString() + "] }";

    when(oidcProviderMetadata.getIDTokenJWSAlgs())
        .thenReturn(Collections.singletonList(JWSAlgorithm.RS256));
    when(oidcProviderMetadata.getIssuer())
        .thenReturn(new Issuer("http://localhost:8080/auth/realms/master"));
    when(oidcProviderMetadata.getJWKSetURI())
        .thenReturn(
            new URI("http://localhost:8080/auth/realms/master/protocol/openid-connect/certs"));
    when(oidcProviderMetadata.getUserInfoJWSAlgs())
        .thenReturn(Collections.singletonList(JWSAlgorithm.RS256));

    Resource resource = new Resource(jwk, APPLICATION_JSON);
    when(resourceRetriever.retrieveResource(any())).thenReturn(resource);

    when(configuration.getClientId()).thenReturn("ddf-client");
    when(configuration.getSecret()).thenReturn("secret");
    when(configuration.isUseNonce()).thenReturn(true);
    when(configuration.findProviderMetadata()).thenReturn(oidcProviderMetadata);
    when(configuration.findResourceRetriever()).thenReturn(resourceRetriever);

    validAlgorithm = Algorithm.RSA256(publicKey, privateKey);

    when(oidcClient.getNonceSessionAttributeName()).thenReturn(NONCE_SESSION_ATTRIBUTE);
  }

  @Test
  public void testValidateIdTokensWithNullConfiguration() throws Exception {
    String stringJwt = createIdToken("myNonce");
    JWT jwt = SignedJWT.parse(stringJwt);
    WebContext context = getWebContext();

    Object result = OidcTokenValidator.validateIdTokens(jwt, context, null, oidcClient);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testValidateIdTokensWithNonceDisabled() throws Exception {
    when(configuration.isUseNonce()).thenReturn(false);
    String stringJwt = createIdToken(null);
    JWT jwt = SignedJWT.parse(stringJwt);
    WebContext context = getWebContext();

    OidcTokenValidator.validateIdTokens(jwt, context, configuration, oidcClient);
  }

  @Test
  public void testValidateIdTokensWithNonceNotInSession() throws Exception {
    WebContext context = mock(WebContext.class);
    SessionStore sessionStore = mock(SessionStore.class);
    when(sessionStore.get(context, NONCE_SESSION_ATTRIBUTE)).thenReturn(Optional.empty());
    when(context.getSessionStore()).thenReturn(sessionStore);

    String stringJwt = createIdToken(null);
    JWT jwt = SignedJWT.parse(stringJwt);

    OidcTokenValidator.validateIdTokens(jwt, context, configuration, oidcClient);
  }

  @Test
  public void testValidateUserInfoIdTokenWithNullMetadata() throws Exception {
    String stringJwt = createIdToken("myNonce");
    JWT jwt = SignedJWT.parse(stringJwt);

    OidcTokenValidator.validateUserInfoIdToken(jwt, resourceRetriever, null);
  }

  @Test
  public void testValidateUserInfoIdTokenWithNullResourceRetriever() throws Exception {
    String stringJwt = createIdToken("myNonce");
    JWT jwt = SignedJWT.parse(stringJwt);

    // Mock JWKSetURI to return null to prevent network connection attempts
    when(oidcProviderMetadata.getJWKSetURI()).thenReturn(null);

    OidcTokenValidator.validateUserInfoIdToken(jwt, null, oidcProviderMetadata);
  }

  @Test
  public void testValidateUserInfoIdTokenWithPlainJwt() throws Exception {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .jwtID(UUID.randomUUID().toString())
            .issuer("http://localhost:8080/auth/realms/master")
            .subject("subject")
            .build();

    JWT jwt = new PlainJWT(claimsSet);

    OidcTokenValidator.validateUserInfoIdToken(jwt, resourceRetriever, oidcProviderMetadata);
  }

  @Test
  public void testValidateUserInfoIdTokenWithEmptyAlgorithmList() throws Exception {
    when(oidcProviderMetadata.getUserInfoJWSAlgs()).thenReturn(Collections.emptyList());

    String stringJwt = createIdToken("myNonce");
    JWT jwt = SignedJWT.parse(stringJwt);

    OidcTokenValidator.validateUserInfoIdToken(jwt, resourceRetriever, oidcProviderMetadata);
  }

  @Test(expected = OidcValidationException.class)
  public void testValidateUserInfoIdTokenWithMismatchedAlgorithm() throws Exception {
    when(oidcProviderMetadata.getUserInfoJWSAlgs())
        .thenReturn(Collections.singletonList(JWSAlgorithm.HS256));

    String stringJwt = createIdToken("myNonce");
    JWT jwt = SignedJWT.parse(stringJwt);

    OidcTokenValidator.validateUserInfoIdToken(jwt, resourceRetriever, oidcProviderMetadata);
  }

  @Test
  public void testValidateAccessTokenWithNullAccessToken() throws Exception {
    OidcTokenValidator.validateAccessToken(
        null, null, resourceRetriever, oidcProviderMetadata, configuration);
  }

  @Test
  public void testValidateAccessTokenWithNullMetadata() throws Exception {
    String accessTokenString = createAccessToken();
    AccessToken accessToken = new BearerAccessToken(accessTokenString);

    OidcTokenValidator.validateAccessToken(
        accessToken, null, resourceRetriever, null, configuration);
  }

  @Test
  public void testValidateAccessTokenWithNullResourceRetriever() throws Exception {
    String accessTokenString = createAccessToken();
    AccessToken accessToken = new BearerAccessToken(accessTokenString);

    // Mock JWKSetURI to return null to prevent network connection attempts
    when(oidcProviderMetadata.getJWKSetURI()).thenReturn(null);

    OidcTokenValidator.validateAccessToken(
        accessToken, null, null, oidcProviderMetadata, configuration);
  }

  @Test
  public void testValidateAccessTokenWithoutIdToken() throws Exception {
    String accessTokenString = createAccessToken();
    AccessToken accessToken = new BearerAccessToken(accessTokenString);

    OidcTokenValidator.validateAccessToken(
        accessToken, null, resourceRetriever, oidcProviderMetadata, configuration);
  }

  @Test
  public void testValidateAccessTokenWithNullConfiguration() throws Exception {
    String accessTokenString = createAccessToken();
    AccessToken accessToken = new BearerAccessToken(accessTokenString);

    String idTokenString = createIdToken("myNonce");
    JWT idToken = SignedJWT.parse(idTokenString);

    OidcTokenValidator.validateAccessToken(
        accessToken, idToken, resourceRetriever, oidcProviderMetadata, null);
  }

  @Test
  public void testValidateAccessTokenWithNonImplicitFlow() throws Exception {
    when(configuration.getResponseType()).thenReturn("code");

    String accessTokenString = createAccessToken();
    AccessToken accessToken = new BearerAccessToken(accessTokenString);

    String idTokenString = createIdToken("myNonce");
    JWT idToken = SignedJWT.parse(idTokenString);

    OidcTokenValidator.validateAccessToken(
        accessToken, idToken, resourceRetriever, oidcProviderMetadata, configuration);
  }

  @Test(expected = OidcValidationException.class)
  public void testValidateAccessTokenWithImplicitFlowNoAtHash() throws Exception {
    // Mock response type before creating tokens
    when(configuration.getResponseType()).thenReturn("id_token token");

    String accessTokenString = createAccessToken();
    AccessToken accessToken = new BearerAccessToken(accessTokenString);

    // Create ID token WITHOUT at_hash claim - this should trigger validation error
    // for implicit flow
    String idTokenString = createIdToken("myNonce");
    JWT idToken = SignedJWT.parse(idTokenString);

    OidcTokenValidator.validateAccessToken(
        accessToken, idToken, resourceRetriever, oidcProviderMetadata, configuration);
  }

  @Test(expected = OidcValidationException.class)
  public void testValidateUserInfoIdTokenWithNoMatchingKeys() throws Exception {
    when(resourceRetriever.retrieveResource(any()))
        .thenReturn(new Resource("{\"keys\": []}", APPLICATION_JSON));

    String stringJwt = createIdToken("myNonce");
    JWT jwt = SignedJWT.parse(stringJwt);

    OidcTokenValidator.validateUserInfoIdToken(jwt, resourceRetriever, oidcProviderMetadata);
  }

  private String createIdToken(String nonce) {
    com.auth0.jwt.JWTCreator.Builder builder =
        com.auth0
            .jwt
            .JWT
            .create()
            .withJWTId(UUID.randomUUID().toString())
            .withExpiresAt(new Date(Instant.now().plus(Duration.ofDays(3)).toEpochMilli()))
            .withNotBefore(new Date(0))
            .withIssuedAt(new Date())
            .withIssuer("http://localhost:8080/auth/realms/master")
            .withAudience("ddf-client")
            .withSubject("subject")
            .withClaim("preferred_username", "admin");

    if (nonce != null) {
      builder.withClaim("nonce", nonce);
    }

    return builder.sign(validAlgorithm);
  }

  private String createAccessToken() {
    return com.auth0
        .jwt
        .JWT
        .create()
        .withJWTId(UUID.randomUUID().toString())
        .withExpiresAt(new Date(Instant.now().plus(Duration.ofDays(3)).toEpochMilli()))
        .withNotBefore(new Date(0))
        .withIssuedAt(new Date())
        .withIssuer("http://localhost:8080/auth/realms/master")
        .withArrayClaim("aud", new String[] {"master-realm", "account"})
        .withSubject("subject")
        .withClaim("typ", "Bearer")
        .withClaim("preferred_username", "admin")
        .sign(validAlgorithm);
  }

  private WebContext getWebContext() {
    WebContext context = mock(WebContext.class);
    SessionStore sessionStore = mock(SessionStore.class);
    when(sessionStore.get(context, NONCE_SESSION_ATTRIBUTE)).thenReturn(Optional.of("myNonce"));
    when(context.getSessionStore()).thenReturn(sessionStore);
    return context;
  }
}
