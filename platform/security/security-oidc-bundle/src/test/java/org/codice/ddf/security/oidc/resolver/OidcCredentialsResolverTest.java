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
package org.codice.ddf.security.oidc.resolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.pac4j.core.context.HttpConstants.APPLICATION_JSON;

import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;

@RunWith(MockitoJUnitRunner.class)
public class OidcCredentialsResolverTest {

  private static final String NONCE_SESSION_ATTRIBUTE = "session-attribute";
  private static final String CLIENT_ID = "ddf-client";
  private static final String CLIENT_SECRET = "secret";
  private static final int MOCK_SERVER_PORT = 18080;
  private static final String ISSUER_URI =
      "http://localhost:" + MOCK_SERVER_PORT + "/auth/realms/master";
  private static final String CALLBACK_URL = "http://localhost:8993/callback";

  @Mock private ResourceRetriever resourceRetriever;
  @Mock private OidcConfiguration configuration;
  @Mock private OIDCProviderMetadata oidcProviderMetadata;
  @Mock private OidcClient<OidcConfiguration> oidcClient;
  @Mock private WebContext webContext;
  @Mock private SessionStore sessionStore;

  private Algorithm validAlgorithm;
  private OidcCredentialsResolver resolver;
  private HttpServer mockHttpServer;
  private String mockAccessTokenString;

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

    validAlgorithm = Algorithm.RSA256(publicKey, privateKey);
    mockAccessTokenString = createAccessToken();

    // Start mock HTTP server
    mockHttpServer = HttpServer.create(new InetSocketAddress("localhost", MOCK_SERVER_PORT), 0);
    mockHttpServer.createContext(
        "/auth/realms/master/protocol/openid-connect/token", new TokenEndpointHandler(this));
    mockHttpServer.createContext(
        "/auth/realms/master/protocol/openid-connect/userinfo", new UserInfoEndpointHandler());
    mockHttpServer.createContext(
        "/auth/realms/master/protocol/openid-connect/certs", new CertsEndpointHandler(jwk));
    mockHttpServer.setExecutor(null); // creates a default executor
    mockHttpServer.start();

    when(oidcProviderMetadata.getIDTokenJWSAlgs())
        .thenReturn(java.util.Collections.singletonList(JWSAlgorithm.RS256));
    when(oidcProviderMetadata.getIssuer()).thenReturn(new Issuer(ISSUER_URI));
    when(oidcProviderMetadata.getJWKSetURI())
        .thenReturn(
            new URI(
                "http://localhost:"
                    + MOCK_SERVER_PORT
                    + "/auth/realms/master/protocol/openid-connect/certs"));
    when(oidcProviderMetadata.getTokenEndpointURI())
        .thenReturn(
            new URI(
                "http://localhost:"
                    + MOCK_SERVER_PORT
                    + "/auth/realms/master/protocol/openid-connect/token"));
    lenient()
        .when(oidcProviderMetadata.getUserInfoEndpointURI())
        .thenReturn(
            new URI(
                "http://localhost:"
                    + MOCK_SERVER_PORT
                    + "/auth/realms/master/protocol/openid-connect/userinfo"));

    Resource resource = new Resource(jwk, APPLICATION_JSON);
    when(resourceRetriever.retrieveResource(any())).thenReturn(resource);

    when(configuration.getClientId()).thenReturn(CLIENT_ID);
    when(configuration.getSecret()).thenReturn(CLIENT_SECRET);
    when(configuration.isUseNonce()).thenReturn(true);
    when(configuration.getResponseType())
        .thenReturn("code"); // Use authorization code flow, not implicit
    when(configuration.findProviderMetadata()).thenReturn(oidcProviderMetadata);
    when(configuration.findResourceRetriever()).thenReturn(resourceRetriever);

    when(oidcClient.getNonceSessionAttributeName()).thenReturn(NONCE_SESSION_ATTRIBUTE);
    when(oidcClient.computeFinalCallbackUrl(any())).thenReturn(CALLBACK_URL);

    when(webContext.getSessionStore()).thenReturn(sessionStore);
    when(sessionStore.get(webContext, NONCE_SESSION_ATTRIBUTE)).thenReturn(Optional.of("myNonce"));

    resolver =
        new OidcCredentialsResolver(configuration, oidcClient, oidcProviderMetadata, 5000, 5000);
  }

  @After
  public void tearDown() {
    if (mockHttpServer != null) {
      mockHttpServer.stop(0);
    }
  }

  @Test
  public void testResolveIdTokenWithValidIdToken() throws Exception {
    String idTokenString = createIdToken("myNonce");
    JWT idToken = SignedJWT.parse(idTokenString);

    OidcCredentials credentials = new OidcCredentials();
    credentials.setIdToken(idToken);

    resolver.resolveIdToken(credentials, webContext);

    assertThat(credentials.getIdToken(), is(notNullValue()));
  }

  @Test
  public void testResolveIdTokenWithValidAccessToken() throws Exception {
    String accessTokenString = createAccessToken();
    String idTokenString = createIdToken("myNonce");

    AccessToken accessToken = new BearerAccessToken(accessTokenString);
    JWT idToken = SignedJWT.parse(idTokenString);

    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(accessToken);
    credentials.setIdToken(idToken);

    resolver.resolveIdToken(credentials, webContext);

    assertThat(credentials.getIdToken(), is(notNullValue()));
  }

  @Test
  public void testResolveIdTokenWithInvalidAccessToken() throws Exception {
    assertThrows(
        TechnicalException.class,
        () -> {
          String invalidAccessTokenString = "invalid.access.token";
          AccessToken accessToken = new BearerAccessToken(invalidAccessTokenString);

          OidcCredentials credentials = new OidcCredentials();
          credentials.setAccessToken(accessToken);

          resolver.resolveIdToken(credentials, webContext);
        });
  }

  @Test
  public void testResolveIdTokenWithRefreshToken() throws Exception {
    RefreshToken refreshToken = new RefreshToken();

    OidcCredentials credentials = new OidcCredentials();
    credentials.setRefreshToken(refreshToken);

    resolver.resolveIdToken(credentials, webContext);

    assertThat(credentials.getRefreshToken(), is(notNullValue()));
  }

  @Test
  public void testResolveIdTokenWithAuthorizationCode() throws Exception {
    AuthorizationCode authCode = new AuthorizationCode();

    OidcCredentials credentials = new OidcCredentials();
    credentials.setCode(authCode);

    resolver.resolveIdToken(credentials, webContext);

    assertThat(credentials.getCode(), is(notNullValue()));
  }

  @Test
  public void testGetOidcTokensWithValidGrant() throws Exception {
    AuthorizationGrant grant = mock(AuthorizationGrant.class, withSettings().lenient());
    lenient()
        .when(grant.getType())
        .thenReturn(com.nimbusds.oauth2.sdk.GrantType.AUTHORIZATION_CODE);

    ClientAuthentication clientAuth =
        new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET));

    // This will make an HTTP call to mock server which returns a minimal valid response
    OIDCTokens tokens =
        OidcCredentialsResolver.getOidcTokens(grant, oidcProviderMetadata, clientAuth, 5000, 5000);

    // Mock server returns access token but no ID token
    assertThat(tokens, is(notNullValue()));
    assertThat(tokens.getAccessToken(), is(notNullValue()));
  }

  @Test
  public void testGetOidcTokensWithCustomTimeouts() throws Exception {
    AuthorizationGrant grant = mock(AuthorizationGrant.class, withSettings().lenient());
    lenient()
        .when(grant.getType())
        .thenReturn(com.nimbusds.oauth2.sdk.GrantType.AUTHORIZATION_CODE);

    ClientAuthentication clientAuth =
        new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET));

    // This will make an HTTP call to mock server with custom timeouts
    OIDCTokens tokens =
        OidcCredentialsResolver.getOidcTokens(
            grant, oidcProviderMetadata, clientAuth, 10000, 10000);

    // Mock server returns access token but no ID token
    assertThat(tokens, is(notNullValue()));
    assertThat(tokens.getAccessToken(), is(notNullValue()));
  }

  @Test
  public void testGetOidcTokensDeprecatedMethod() throws Exception {
    AuthorizationGrant grant = mock(AuthorizationGrant.class, withSettings().lenient());
    lenient()
        .when(grant.getType())
        .thenReturn(com.nimbusds.oauth2.sdk.GrantType.AUTHORIZATION_CODE);

    ClientAuthentication clientAuth =
        new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET));

    // This will make an HTTP call to mock server using deprecated method
    OIDCTokens tokens =
        OidcCredentialsResolver.getOidcTokens(grant, oidcProviderMetadata, clientAuth);

    // Mock server returns access token but no ID token
    assertThat(tokens, is(notNullValue()));
    assertThat(tokens.getAccessToken(), is(notNullValue()));
  }

  @Test
  public void testResolveIdTokenWithNullAccessToken() throws Exception {
    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(null);

    resolver.resolveIdToken(credentials, webContext);

    assertThat(credentials.getAccessToken(), is(nullValue()));
  }

  @Test
  public void testResolveIdTokenWithNullIdToken() throws Exception {
    OidcCredentials credentials = new OidcCredentials();
    credentials.setIdToken(null);

    resolver.resolveIdToken(credentials, webContext);

    assertThat(credentials.getIdToken(), is(nullValue()));
  }

  private String createIdToken(String nonce) {
    return com.auth0
        .jwt
        .JWT
        .create()
        .withJWTId(UUID.randomUUID().toString())
        .withExpiresAt(new Date(Instant.now().plus(Duration.ofDays(3)).toEpochMilli()))
        .withNotBefore(new Date(0))
        .withIssuedAt(new Date())
        .withIssuer(ISSUER_URI)
        .withAudience(CLIENT_ID)
        .withSubject("test-subject")
        .withClaim("nonce", nonce)
        .withClaim("preferred_username", "testuser")
        .sign(validAlgorithm);
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
        .withIssuer(ISSUER_URI)
        .withAudience(CLIENT_ID)
        .withSubject("test-subject")
        .withClaim("typ", "Bearer")
        .withClaim("preferred_username", "testuser")
        .sign(validAlgorithm);
  }

  /** Mock HTTP handler for the token endpoint that returns a minimal valid response */
  private class TokenEndpointHandler implements HttpHandler {
    private final OidcCredentialsResolverTest testInstance;

    TokenEndpointHandler(OidcCredentialsResolverTest testInstance) {
      this.testInstance = testInstance;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
      // Return a valid JWT access token response with refresh token
      // This allows tests to proceed without throwing TechnicalException
      String response =
          "{\"access_token\":\""
              + testInstance.mockAccessTokenString
              + "\",\"token_type\":\"Bearer\",\"refresh_token\":\"mock_refresh_token\"}";
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, response.getBytes().length);
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }

  /** Mock HTTP handler for the userinfo endpoint */
  private static class UserInfoEndpointHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      // Return a simple userinfo response
      String response = "{\"sub\":\"test-subject\",\"preferred_username\":\"testuser\"}";
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, response.getBytes().length);
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }

  /** Mock HTTP handler for the certs (JWK Set) endpoint */
  private static class CertsEndpointHandler implements HttpHandler {
    private final String jwkSet;

    CertsEndpointHandler(String jwkSet) {
      this.jwkSet = jwkSet;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, jwkSet.getBytes().length);
      OutputStream os = exchange.getResponseBody();
      os.write(jwkSet.getBytes());
      os.close();
    }
  }
}
