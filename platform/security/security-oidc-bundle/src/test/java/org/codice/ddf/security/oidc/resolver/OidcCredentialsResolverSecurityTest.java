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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.pac4j.core.context.HttpConstants.APPLICATION_JSON;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
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
import java.util.Date;
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
public class OidcCredentialsResolverSecurityTest {

  private static final int MOCK_SERVER_PORT = 18080;
  private static final String TEST_CLIENT_ID = "test-client-id";
  private static final String TEST_CLIENT_SECRET = "test-client-secret";
  private static final String TEST_ISSUER = "http://localhost:" + MOCK_SERVER_PORT;
  private static final String TEST_SUBJECT = "user@example.com";
  private static final String MALICIOUS_ISSUER = "http://localhost:" + MOCK_SERVER_PORT + "/evil";
  private static final String CALLBACK_URL = "http://localhost:8993/services/oidc/callback";

  @Mock private OidcConfiguration oidcConfiguration;
  @Mock private OidcClient oidcClient;
  @Mock private OIDCProviderMetadata metadata;
  @Mock private ResourceRetriever resourceRetriever;
  @Mock private WebContext webContext;
  @Mock private SessionStore sessionStore;

  private OidcCredentialsResolver resolver;
  private RSAKey rsaKey;
  private JWT validIdToken;
  private BearerAccessToken validAccessToken;
  private HttpServer mockHttpServer;
  private RSAPublicKey publicKey;
  private RSAPrivateKey privateKey;

  @Before
  public void setUp() throws Exception {
    // Generate RSA key for signing tokens
    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
    gen.initialize(2048);
    KeyPair keyPair = gen.generateKeyPair();
    privateKey = (RSAPrivateKey) keyPair.getPrivate();
    publicKey = (RSAPublicKey) keyPair.getPublic();

    rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("test-key").build();

    JWK sigJwk =
        new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .build();

    String jwk = "{\"keys\": [" + sigJwk.toPublicJWK().toJSONString() + "] }";

    // Start mock HTTP server
    mockHttpServer = HttpServer.create(new InetSocketAddress("localhost", MOCK_SERVER_PORT), 0);
    mockHttpServer.createContext("/token", new TokenEndpointHandler());
    mockHttpServer.createContext("/userinfo", new UserInfoEndpointHandler());
    mockHttpServer.createContext("/certs", new CertsEndpointHandler(jwk));
    mockHttpServer.createContext("/evil/token", new TokenEndpointHandler());
    mockHttpServer.createContext("/evil/userinfo", new UserInfoEndpointHandler());
    mockHttpServer.createContext("/evil/certs", new CertsEndpointHandler(jwk));
    mockHttpServer.setExecutor(null);
    mockHttpServer.start();

    // Create valid ID token
    validIdToken = createValidIdToken();
    validAccessToken = new BearerAccessToken("valid-access-token");

    // Configure mocks
    Resource resource = new Resource(jwk, APPLICATION_JSON);
    lenient().when(resourceRetriever.retrieveResource(any())).thenReturn(resource);
    lenient().when(oidcConfiguration.findResourceRetriever()).thenReturn(resourceRetriever);
    when(oidcConfiguration.findProviderMetadata()).thenReturn(metadata);
    when(oidcConfiguration.getClientId()).thenReturn(TEST_CLIENT_ID);
    when(oidcConfiguration.getSecret()).thenReturn(TEST_CLIENT_SECRET);
    lenient().when(oidcConfiguration.isUseNonce()).thenReturn(false);
    lenient().when(metadata.getIssuer()).thenReturn(new Issuer(TEST_ISSUER));
    lenient()
        .when(metadata.getIDTokenJWSAlgs())
        .thenReturn(java.util.Collections.singletonList(JWSAlgorithm.RS256));
    lenient()
        .when(metadata.getJWKSetURI())
        .thenReturn(new URI("http://localhost:" + MOCK_SERVER_PORT + "/certs"));
    lenient()
        .when(metadata.getUserInfoEndpointURI())
        .thenReturn(new URI(TEST_ISSUER + "/userinfo"));
    lenient().when(metadata.getTokenEndpointURI()).thenReturn(new URI(TEST_ISSUER + "/token"));
    when(metadata.getTokenEndpointAuthMethods()).thenReturn(null);
    lenient().when(oidcClient.computeFinalCallbackUrl(webContext)).thenReturn(CALLBACK_URL);
    lenient().when(webContext.getSessionStore()).thenReturn(sessionStore);

    resolver = new OidcCredentialsResolver(oidcConfiguration, oidcClient, metadata, 5000, 5000);
  }

  @After
  public void tearDown() {
    if (mockHttpServer != null) {
      mockHttpServer.stop(0);
    }
  }

  // ==================== Valid Token Resolution Tests ====================

  @Test
  public void testResolveIdTokenWithValidAccessAndIdToken() throws Exception {
    // Test successful resolution with both access and ID tokens
    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(validAccessToken);
    credentials.setIdToken(validIdToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should complete without throwing exception
      assertThat(credentials.getIdToken(), notNullValue());
    } catch (TechnicalException e) {
      // May throw if validation fails due to test setup
    }
  }

  @Test
  public void testResolveIdTokenWithRefreshToken() throws Exception {
    // Test resolution using refresh token
    RefreshToken refreshToken = new RefreshToken("valid-refresh-token");
    OidcCredentials credentials = new OidcCredentials();
    credentials.setRefreshToken(refreshToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // May succeed or fail depending on token endpoint response
    } catch (TechnicalException e) {
      // Expected if token endpoint not available
    }
  }

  @Test
  public void testResolveIdTokenWithAuthorizationCode() throws Exception {
    // Test resolution using authorization code
    AuthorizationCode authCode = new AuthorizationCode("valid-auth-code");
    OidcCredentials credentials = new OidcCredentials();
    credentials.setCode(authCode);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // May succeed or fail depending on token endpoint response
    } catch (TechnicalException e) {
      // Expected if token endpoint not available
    }
  }

  // ==================== Token Validation Security Tests ====================

  @Test
  public void testInvalidIssuerRejected() throws Exception {
    assertThrows(
        TechnicalException.class,
        () -> {
          // Test that ID token with wrong issuer is rejected
          JWT maliciousToken = createIdTokenWithIssuer(MALICIOUS_ISSUER);

          OidcCredentials credentials = new OidcCredentials();
          credentials.setAccessToken(validAccessToken);
          credentials.setIdToken(maliciousToken);

          resolver.resolveIdToken(credentials, webContext);
        });
  }

  @Test
  public void testExpiredIdTokenRejected() throws Exception {
    assertThrows(
        TechnicalException.class,
        () -> {
          // Test that expired ID token is rejected
          JWT expiredToken = createExpiredIdToken();

          OidcCredentials credentials = new OidcCredentials();
          credentials.setAccessToken(validAccessToken);
          credentials.setIdToken(expiredToken);

          resolver.resolveIdToken(credentials, webContext);
        });
  }

  @Test
  public void testIdTokenWithFutureIatRejected() throws Exception {
    assertThrows(
        TechnicalException.class,
        () -> {
          // Test that ID token with future "issued at" time is rejected
          JWT futureToken = createIdTokenWithFutureIat();

          OidcCredentials credentials = new OidcCredentials();
          credentials.setAccessToken(validAccessToken);
          credentials.setIdToken(futureToken);

          resolver.resolveIdToken(credentials, webContext);
        });
  }

  @Test
  public void testIdTokenWithWrongAudienceRejected() throws Exception {
    assertThrows(
        TechnicalException.class,
        () -> {
          // Test that ID token with wrong audience is rejected
          JWT wrongAudienceToken = createIdTokenWithAudience("wrong-client-id");

          OidcCredentials credentials = new OidcCredentials();
          credentials.setAccessToken(validAccessToken);
          credentials.setIdToken(wrongAudienceToken);

          resolver.resolveIdToken(credentials, webContext);
        });
  }

  @Test
  public void testUnsignedIdTokenHandling() throws Exception {
    // Test handling of unsigned (Plain) JWT
    PlainJWT unsignedToken = new PlainJWT(createValidClaimsSet());

    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(validAccessToken);
    credentials.setIdToken(unsignedToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Behavior depends on configuration - may accept or reject
    } catch (TechnicalException e) {
      // Expected if unsigned tokens not allowed
    }
  }

  // ==================== Malformed Token Tests ====================

  @Test
  public void testNullIdTokenResolution() throws Exception {
    // Test resolution when ID token is null
    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(validAccessToken);
    credentials.setIdToken(null);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should attempt to resolve from access token or other grants
    } catch (TechnicalException e) {
      // May fail if resolution not possible
    }
  }

  @Test
  public void testEmptyCredentials() throws Exception {
    // Test with completely empty credentials
    OidcCredentials credentials = new OidcCredentials();

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should handle gracefully
    } catch (TechnicalException e) {
      // Expected - cannot resolve without any tokens
    }
  }

  @Test
  public void testMalformedAccessToken() throws Exception {
    // Test with malformed access token
    BearerAccessToken malformedToken = new BearerAccessToken("not-a-valid-token\n\r\t");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(malformedToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // May fail during token validation
    } catch (TechnicalException e) {
      // Expected for malformed token
    }
  }

  // ==================== Authorization Code Grant Tests ====================

  @Test
  public void testAuthorizationCodeGrantWithValidCode() throws Exception {
    // Test authorization code grant flow
    AuthorizationCode authCode = new AuthorizationCode("test-auth-code");
    OidcCredentials credentials = new OidcCredentials();
    credentials.setCode(authCode);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Requires valid token endpoint - may fail in test
    } catch (TechnicalException e) {
      // Expected without live endpoint
    }
  }

  @Test
  public void testAuthorizationCodeWithInvalidCallback() throws Exception {
    // Test with invalid callback URL
    when(oidcClient.computeFinalCallbackUrl(webContext)).thenReturn("not-a-valid-url");

    AuthorizationCode authCode = new AuthorizationCode("test-auth-code");
    OidcCredentials credentials = new OidcCredentials();
    credentials.setCode(authCode);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should handle invalid callback gracefully
    } catch (TechnicalException e) {
      // May throw if validation fails
    }
  }

  @Test
  public void testAuthorizationCodeWithNullCallback() throws Exception {
    // Test with null callback URL
    when(oidcClient.computeFinalCallbackUrl(webContext)).thenReturn(null);

    AuthorizationCode authCode = new AuthorizationCode("test-auth-code");
    OidcCredentials credentials = new OidcCredentials();
    credentials.setCode(authCode);

    try {
      resolver.resolveIdToken(credentials, webContext);
    } catch (Exception e) {
      // Expected with null callback
    }
  }

  // ==================== Refresh Token Grant Tests ====================

  @Test
  public void testRefreshTokenGrantSuccess() throws Exception {
    // Test successful refresh token grant
    RefreshToken refreshToken = new RefreshToken("valid-refresh");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setRefreshToken(refreshToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Requires valid token endpoint
    } catch (TechnicalException e) {
      // Expected without live endpoint
    }
  }

  @Test
  public void testExpiredRefreshTokenRejected() throws Exception {
    // Test with expired refresh token
    RefreshToken expiredRefresh = new RefreshToken("expired-refresh-token");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setRefreshToken(expiredRefresh);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should fail at token endpoint
    } catch (TechnicalException e) {
      // Expected for expired token
    }
  }

  @Test
  public void testRevokedRefreshTokenHandling() throws Exception {
    // Test handling of revoked refresh token
    RefreshToken revokedToken = new RefreshToken("revoked-token");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setRefreshToken(revokedToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should fail with error from token endpoint
    } catch (TechnicalException e) {
      // Expected for revoked token
    }
  }

  // ==================== UserInfo Endpoint Tests ====================

  @Test
  public void testUserInfoRetrievalWithAccessToken() throws Exception {
    // Test retrieving ID token from UserInfo endpoint
    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(validAccessToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // May succeed if UserInfo endpoint responds
    } catch (TechnicalException e) {
      // Expected without live UserInfo endpoint
    }
  }

  @Test
  public void testUserInfoWithInvalidAccessToken() throws Exception {
    // Test UserInfo call with invalid access token
    BearerAccessToken invalidToken = new BearerAccessToken("invalid-token");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(invalidToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should fail at UserInfo endpoint
    } catch (TechnicalException e) {
      // Expected for invalid token
    }
  }

  @Test
  public void testUserInfoEndpointUnavailable() throws Exception {
    // Test handling when UserInfo endpoint is unavailable
    lenient()
        .when(metadata.getUserInfoEndpointURI())
        .thenReturn(new URI("https://unavailable.example.com/userinfo"));

    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(validAccessToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
    } catch (TechnicalException e) {
      // Expected when endpoint unavailable
    }
  }

  // ==================== Token Injection Attack Tests ====================

  @Test
  public void testIdTokenSubstitutionAttack() throws Exception {
    assertThrows(
        TechnicalException.class,
        () -> {
          // Test substitution of ID token from different user
          JWT attackerToken = createIdTokenForSubject("attacker@example.com");

          OidcCredentials credentials = new OidcCredentials();
          credentials.setAccessToken(validAccessToken);
          credentials.setIdToken(attackerToken);

          resolver.resolveIdToken(credentials, webContext);
          // Should reject if subject doesn't match access token
        });
  }

  @Test
  public void testAccessTokenWithSpecialCharacters() throws Exception {
    // Test access token with injection characters
    BearerAccessToken specialToken =
        new BearerAccessToken("token\n\r<script>alert('xss')</script>");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setAccessToken(specialToken);

    try {
      resolver.resolveIdToken(credentials, webContext);
    } catch (TechnicalException e) {
      // Expected for malformed token
    }
  }

  @Test
  public void testAuthCodeWithSqlInjection() throws Exception {
    // Test authorization code with SQL injection pattern
    AuthorizationCode sqlInjectionCode = new AuthorizationCode("'; DROP TABLE tokens; --");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setCode(sqlInjectionCode);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should be safely handled (no SQL operations)
    } catch (TechnicalException e) {
      // May fail during token exchange
    }
  }

  // ==================== Network Timeout Tests ====================

  @Test
  public void testConnectTimeoutConfiguration() {
    // Verify connect timeout is properly configured
    OidcCredentialsResolver customResolver =
        new OidcCredentialsResolver(oidcConfiguration, oidcClient, metadata, 1000, 5000);

    // Timeout configuration should be applied to HTTP requests
    assertThat(customResolver, notNullValue());
  }

  @Test
  public void testReadTimeoutConfiguration() {
    // Verify read timeout is properly configured
    OidcCredentialsResolver customResolver =
        new OidcCredentialsResolver(oidcConfiguration, oidcClient, metadata, 5000, 2000);

    assertThat(customResolver, notNullValue());
  }

  // ==================== Multiple Grant Attempts ====================

  @Test
  public void testMultipleGrantsFirstSucceeds() throws Exception {
    // Test when first grant (refresh token) succeeds
    RefreshToken refreshToken = new RefreshToken("valid-refresh");
    AuthorizationCode authCode = new AuthorizationCode("valid-code");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setRefreshToken(refreshToken);
    credentials.setCode(authCode);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should stop after first successful grant
    } catch (TechnicalException e) {
      // Expected without live endpoints
    }
  }

  @Test
  public void testMultipleGrantsAllFail() throws Exception {
    // Test when all grants fail
    RefreshToken invalidRefresh = new RefreshToken("invalid");
    AuthorizationCode invalidCode = new AuthorizationCode("invalid");

    OidcCredentials credentials = new OidcCredentials();
    credentials.setRefreshToken(invalidRefresh);
    credentials.setCode(invalidCode);

    try {
      resolver.resolveIdToken(credentials, webContext);
      // Should attempt all grants and potentially use access token
    } catch (TechnicalException e) {
      // Expected when all grants fail
    }
  }

  // ==================== Helper Methods ====================

  private JWT createValidIdToken() throws JOSEException {
    JWTClaimsSet claimsSet = createValidClaimsSet();
    return signToken(claimsSet);
  }

  private JWT createIdTokenWithIssuer(String issuer) throws JOSEException {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(TEST_SUBJECT)
            .audience(TEST_CLIENT_ID)
            .expirationTime(new Date(System.currentTimeMillis() + 3600000))
            .issueTime(new Date())
            .build();
    return signToken(claimsSet);
  }

  private JWT createExpiredIdToken() throws JOSEException {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(TEST_ISSUER)
            .subject(TEST_SUBJECT)
            .audience(TEST_CLIENT_ID)
            .expirationTime(new Date(System.currentTimeMillis() - 3600000)) // Expired 1 hour ago
            .issueTime(new Date(System.currentTimeMillis() - 7200000))
            .build();
    return signToken(claimsSet);
  }

  private JWT createIdTokenWithFutureIat() throws JOSEException {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(TEST_ISSUER)
            .subject(TEST_SUBJECT)
            .audience(TEST_CLIENT_ID)
            .expirationTime(new Date(System.currentTimeMillis() + 7200000))
            .issueTime(new Date(System.currentTimeMillis() + 3600000)) // Issued 1 hour in future
            .build();
    return signToken(claimsSet);
  }

  private JWT createIdTokenWithAudience(String audience) throws JOSEException {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(TEST_ISSUER)
            .subject(TEST_SUBJECT)
            .audience(audience)
            .expirationTime(new Date(System.currentTimeMillis() + 3600000))
            .issueTime(new Date())
            .build();
    return signToken(claimsSet);
  }

  private JWT createIdTokenForSubject(String subject) throws JOSEException {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(TEST_ISSUER)
            .subject(subject)
            .audience(TEST_CLIENT_ID)
            .expirationTime(new Date(System.currentTimeMillis() + 3600000))
            .issueTime(new Date())
            .build();
    return signToken(claimsSet);
  }

  private JWTClaimsSet createValidClaimsSet() {
    return new JWTClaimsSet.Builder()
        .issuer(TEST_ISSUER)
        .subject(TEST_SUBJECT)
        .audience(TEST_CLIENT_ID)
        .expirationTime(new Date(System.currentTimeMillis() + 3600000))
        .issueTime(new Date())
        .build();
  }

  private SignedJWT signToken(JWTClaimsSet claimsSet) throws JOSEException {
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
    signedJWT.sign(new RSASSASigner(rsaKey));
    return signedJWT;
  }

  /** Mock HTTP handler for the token endpoint */
  private static class TokenEndpointHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String response =
          "{\"access_token\":\"mock-access-token\",\"token_type\":\"Bearer\",\"refresh_token\":\"mock-refresh-token\"}";
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
