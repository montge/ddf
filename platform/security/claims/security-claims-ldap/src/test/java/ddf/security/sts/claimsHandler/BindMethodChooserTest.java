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
package ddf.security.sts.claimsHandler;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.forgerock.opendj.ldap.requests.BindRequest;
import org.forgerock.opendj.ldap.requests.DigestMD5SASLBindRequest;
import org.forgerock.opendj.ldap.requests.GSSAPISASLBindRequest;
import org.forgerock.opendj.ldap.requests.PlainSASLBindRequest;
import org.forgerock.opendj.ldap.requests.SimpleBindRequest;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test harness for BindMethodChooser utility class.
 *
 * <p>Tests all LDAP bind methods: Simple, SASL, GSSAPI SASL, Digest MD5 SASL.
 *
 * <p>Coverage includes: - All supported bind method types - Null and empty parameter handling -
 * Realm and KDC address configuration - Default fallback behavior - Credential security
 */
public class BindMethodChooserTest {

  private static final String BIND_USER_DN = "cn=admin,dc=example,dc=com";
  private static final String BIND_PASSWORD = "testPassword123";
  private static final String REALM = "EXAMPLE.COM";
  private static final String KDC_ADDRESS = "kdc.example.com";

  /**
   * Tests Simple bind method selection.
   *
   * <p>Simple bind is the most basic LDAP authentication using DN and password.
   */
  @Test
  public void testSimpleBindMethod() {
    BindRequest request =
        BindMethodChooser.selectBindMethod("Simple", BIND_USER_DN, BIND_PASSWORD, null, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return SimpleBindRequest instance", request, instanceOf(SimpleBindRequest.class));
    SimpleBindRequest simpleRequest = (SimpleBindRequest) request;
    assertThat("Bind DN should match", simpleRequest.getName(), is(BIND_USER_DN));
  }

  /**
   * Tests SASL bind method selection (PLAIN SASL).
   *
   * <p>SASL provides additional security mechanisms beyond simple bind.
   */
  @Test
  public void testSaslBindMethod() {
    BindRequest request =
        BindMethodChooser.selectBindMethod("SASL", BIND_USER_DN, BIND_PASSWORD, null, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return PlainSASLBindRequest instance",
        request,
        instanceOf(PlainSASLBindRequest.class));
    PlainSASLBindRequest saslRequest = (PlainSASLBindRequest) request;
    assertThat(
        "Authentication ID should match", saslRequest.getAuthenticationID(), is(BIND_USER_DN));
  }

  /**
   * Tests GSSAPI SASL bind method with realm and KDC configuration.
   *
   * <p>GSSAPI SASL is used for Kerberos authentication.
   */
  @Test
  public void testGssapiSaslBindMethodWithRealmAndKdc() {
    BindRequest request =
        BindMethodChooser.selectBindMethod(
            "GSSAPI SASL", BIND_USER_DN, BIND_PASSWORD, REALM, KDC_ADDRESS);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return GSSAPISASLBindRequest instance",
        request,
        instanceOf(GSSAPISASLBindRequest.class));
    GSSAPISASLBindRequest gssapiRequest = (GSSAPISASLBindRequest) request;
    assertThat(
        "Authentication ID should match", gssapiRequest.getAuthenticationID(), is(BIND_USER_DN));
    assertThat("Realm should be configured", gssapiRequest.getRealm(), is(REALM));
    assertThat("KDC address should be configured", gssapiRequest.getKDCAddress(), is(KDC_ADDRESS));
  }

  /**
   * Tests GSSAPI SASL bind method without realm and KDC (null values).
   *
   * <p>Verifies that null realm and KDC values are handled gracefully.
   */
  @Test
  public void testGssapiSaslBindMethodWithoutRealmAndKdc() {
    BindRequest request =
        BindMethodChooser.selectBindMethod("GSSAPI SASL", BIND_USER_DN, BIND_PASSWORD, null, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return GSSAPISASLBindRequest instance",
        request,
        instanceOf(GSSAPISASLBindRequest.class));
    GSSAPISASLBindRequest gssapiRequest = (GSSAPISASLBindRequest) request;
    assertThat(
        "Authentication ID should match", gssapiRequest.getAuthenticationID(), is(BIND_USER_DN));
  }

  /**
   * Tests Digest MD5 SASL bind method with realm.
   *
   * <p>Digest MD5 provides secure authentication using challenge-response mechanism.
   */
  @Test
  public void testDigestMd5SaslBindMethodWithRealm() {
    BindRequest request =
        BindMethodChooser.selectBindMethod(
            "Digest MD5 SASL", BIND_USER_DN, BIND_PASSWORD, REALM, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return DigestMD5SASLBindRequest instance",
        request,
        instanceOf(DigestMD5SASLBindRequest.class));
    DigestMD5SASLBindRequest digestRequest = (DigestMD5SASLBindRequest) request;
    assertThat(
        "Authentication ID should match", digestRequest.getAuthenticationID(), is(BIND_USER_DN));
    assertThat("Realm should be configured", digestRequest.getRealm(), is(REALM));
    assertThat(
        "Cipher should be HIGH",
        digestRequest.getCipher(),
        is(DigestMD5SASLBindRequest.CIPHER_HIGH));
    assertThat("Should have QOP configured", digestRequest.getQOPs().size(), is(3));
    assertThat(
        "Should contain AUTH_CONF QOP",
        digestRequest.getQOPs().contains(DigestMD5SASLBindRequest.QOP_AUTH_CONF),
        is(true));
    assertThat(
        "Should contain AUTH_INT QOP",
        digestRequest.getQOPs().contains(DigestMD5SASLBindRequest.QOP_AUTH_INT),
        is(true));
    assertThat(
        "Should contain AUTH QOP",
        digestRequest.getQOPs().contains(DigestMD5SASLBindRequest.QOP_AUTH),
        is(true));
  }

  /**
   * Tests Digest MD5 SASL bind method without realm (null realm).
   *
   * <p>Verifies that empty realm is handled correctly.
   */
  @Test
  public void testDigestMd5SaslBindMethodWithoutRealm() {
    BindRequest request =
        BindMethodChooser.selectBindMethod(
            "Digest MD5 SASL", BIND_USER_DN, BIND_PASSWORD, null, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return DigestMD5SASLBindRequest instance",
        request,
        instanceOf(DigestMD5SASLBindRequest.class));
    DigestMD5SASLBindRequest digestRequest = (DigestMD5SASLBindRequest) request;
    assertThat(
        "Authentication ID should match", digestRequest.getAuthenticationID(), is(BIND_USER_DN));
  }

  /**
   * Tests Digest MD5 SASL bind method with empty realm string.
   *
   * <p>Verifies that empty string realm is handled correctly (should not set realm).
   */
  @Test
  public void testDigestMd5SaslBindMethodWithEmptyRealm() {
    BindRequest request =
        BindMethodChooser.selectBindMethod(
            "Digest MD5 SASL", BIND_USER_DN, BIND_PASSWORD, "", null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return DigestMD5SASLBindRequest instance",
        request,
        instanceOf(DigestMD5SASLBindRequest.class));
  }

  /**
   * Tests default fallback behavior for unknown bind method.
   *
   * <p>Verifies that unknown bind methods default to Simple bind.
   */
  @Test
  public void testUnknownBindMethodDefaultsToSimple() {
    BindRequest request =
        BindMethodChooser.selectBindMethod(
            "UnknownMethod", BIND_USER_DN, BIND_PASSWORD, null, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return SimpleBindRequest instance as default",
        request,
        instanceOf(SimpleBindRequest.class));
    SimpleBindRequest simpleRequest = (SimpleBindRequest) request;
    assertThat("Bind DN should match", simpleRequest.getName(), is(BIND_USER_DN));
  }

  /**
   * Tests null bind method throws NullPointerException.
   *
   * <p>Switch statements in Java cannot handle null values, so NPE is expected.
   */
  @Test
  public void testNullBindMethodThrowsNpe() {
    org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class,
        () -> BindMethodChooser.selectBindMethod(null, BIND_USER_DN, BIND_PASSWORD, null, null));
  }

  /**
   * Tests empty string bind method defaults to Simple bind.
   *
   * <p>Verifies that empty bind method string is handled safely.
   */
  @Test
  public void testEmptyBindMethodDefaultsToSimple() {
    BindRequest request =
        BindMethodChooser.selectBindMethod("", BIND_USER_DN, BIND_PASSWORD, null, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return SimpleBindRequest instance as default",
        request,
        instanceOf(SimpleBindRequest.class));
  }

  /**
   * Tests bind method with empty credentials.
   *
   * <p>Verifies that empty password is accepted (anonymous bind scenario).
   */
  @Test
  public void testSimpleBindMethodWithEmptyCredentials() {
    BindRequest request =
        BindMethodChooser.selectBindMethod("Simple", BIND_USER_DN, "", null, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return SimpleBindRequest instance", request, instanceOf(SimpleBindRequest.class));
  }

  /**
   * Tests case sensitivity of bind method selection.
   *
   * <p>Verifies that bind method string is case-sensitive (should default to Simple if wrong case).
   */
  @Test
  public void testBindMethodCaseSensitivity() {
    BindRequest request =
        BindMethodChooser.selectBindMethod("simple", BIND_USER_DN, BIND_PASSWORD, null, null);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return SimpleBindRequest due to case mismatch",
        request,
        instanceOf(SimpleBindRequest.class));
  }

  /**
   * Tests multiple consecutive calls with different bind methods.
   *
   * <p>Verifies that the utility method is stateless and can handle multiple calls.
   */
  @Test
  public void testMultipleConsecutiveCalls() {
    BindRequest request1 =
        BindMethodChooser.selectBindMethod("Simple", BIND_USER_DN, BIND_PASSWORD, null, null);
    BindRequest request2 =
        BindMethodChooser.selectBindMethod("SASL", BIND_USER_DN, BIND_PASSWORD, null, null);
    BindRequest request3 =
        BindMethodChooser.selectBindMethod(
            "GSSAPI SASL", BIND_USER_DN, BIND_PASSWORD, REALM, KDC_ADDRESS);

    assertThat("First request should be Simple", request1, instanceOf(SimpleBindRequest.class));
    assertThat("Second request should be SASL", request2, instanceOf(PlainSASLBindRequest.class));
    assertThat("Third request should be GSSAPI", request3, instanceOf(GSSAPISASLBindRequest.class));
  }

  /**
   * Tests GSSAPI SASL with empty realm (should still work).
   *
   * <p>Verifies that empty string realm is handled for GSSAPI.
   */
  @Test
  public void testGssapiSaslBindMethodWithEmptyRealm() {
    BindRequest request =
        BindMethodChooser.selectBindMethod(
            "GSSAPI SASL", BIND_USER_DN, BIND_PASSWORD, "", KDC_ADDRESS);

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return GSSAPISASLBindRequest instance",
        request,
        instanceOf(GSSAPISASLBindRequest.class));
    GSSAPISASLBindRequest gssapiRequest = (GSSAPISASLBindRequest) request;
    assertThat("Realm should be empty string", gssapiRequest.getRealm(), is(""));
  }

  /**
   * Tests GSSAPI SASL with empty KDC address (should still work).
   *
   * <p>Verifies that empty string KDC is handled for GSSAPI.
   */
  @Test
  public void testGssapiSaslBindMethodWithEmptyKdcAddress() {
    BindRequest request =
        BindMethodChooser.selectBindMethod("GSSAPI SASL", BIND_USER_DN, BIND_PASSWORD, REALM, "");

    assertThat("BindRequest should not be null", request, notNullValue());
    assertThat(
        "Should return GSSAPISASLBindRequest instance",
        request,
        instanceOf(GSSAPISASLBindRequest.class));
    GSSAPISASLBindRequest gssapiRequest = (GSSAPISASLBindRequest) request;
    assertThat("KDC address should be empty string", gssapiRequest.getKDCAddress(), is(""));
  }
}
