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
package ddf.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.KeyStore;
import java.security.KeyStoreException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SecurityConstantsTest {

  private String originalKeystorePath;
  private String originalKeystorePassword;
  private String originalKeystoreType;
  private String originalTruststorePath;
  private String originalTruststorePassword;
  private String originalTruststoreType;
  private String originalDefaultSslProtocol;

  @BeforeEach
  public void setUp() {
    originalKeystorePath = System.getProperty(SecurityConstants.KEYSTORE_PATH);
    originalKeystorePassword = System.getProperty(SecurityConstants.KEYSTORE_PASSWORD);
    originalKeystoreType = System.getProperty(SecurityConstants.KEYSTORE_TYPE);
    originalTruststorePath = System.getProperty(SecurityConstants.TRUSTSTORE_PATH);
    originalTruststorePassword = System.getProperty(SecurityConstants.TRUSTSTORE_PASSWORD);
    originalTruststoreType = System.getProperty(SecurityConstants.TRUSTSTORE_TYPE);
    originalDefaultSslProtocol =
        System.getProperty(SecurityConstants.DEFAULT_SSL_PROTOCOL_PROPERTY);
  }

  @AfterEach
  public void tearDown() {
    restoreProperty(SecurityConstants.KEYSTORE_PATH, originalKeystorePath);
    restoreProperty(SecurityConstants.KEYSTORE_PASSWORD, originalKeystorePassword);
    restoreProperty(SecurityConstants.KEYSTORE_TYPE, originalKeystoreType);
    restoreProperty(SecurityConstants.TRUSTSTORE_PATH, originalTruststorePath);
    restoreProperty(SecurityConstants.TRUSTSTORE_PASSWORD, originalTruststorePassword);
    restoreProperty(SecurityConstants.TRUSTSTORE_TYPE, originalTruststoreType);
    restoreProperty(SecurityConstants.DEFAULT_SSL_PROTOCOL_PROPERTY, originalDefaultSslProtocol);
  }

  private void restoreProperty(String key, String value) {
    if (value == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, value);
    }
  }

  @Test
  public void testSecuritySubjectConstant() {
    assertThat(SecurityConstants.SECURITY_SUBJECT, is(notNullValue()));
    assertThat(SecurityConstants.SECURITY_SUBJECT, is("ddf.security.subject"));
  }

  @Test
  public void testSecurityTokenKeyConstant() {
    assertThat(SecurityConstants.SECURITY_TOKEN_KEY, is(notNullValue()));
    assertThat(SecurityConstants.SECURITY_TOKEN_KEY, is("security.assertion"));
  }

  @Test
  public void testAuthenticationTokenKeyConstant() {
    assertThat(SecurityConstants.AUTHENTICATION_TOKEN_KEY, is(notNullValue()));
    assertThat(SecurityConstants.AUTHENTICATION_TOKEN_KEY, is("ddf.security.token"));
  }

  // SAML_ASSERTION constant was removed in commit 146b6c35d0 (2019) and replaced with
  // AUTHENTICATION_TOKEN_KEY and SECURITY_TOKEN_KEY
  // @Test
  // public void testSamlAssertionConstant() {
  //   assertThat(SecurityConstants.SAML_ASSERTION, is(notNullValue()));
  //   assertThat(SecurityConstants.SAML_ASSERTION, is("ddf.security.assertion"));
  // }

  // GUEST_REALM_NAME constant does not exist in SecurityConstants
  // @Test
  // public void testGuestRealmNameConstant() {
  //   assertThat(SecurityConstants.GUEST_REALM_NAME, is(notNullValue()));
  //   assertThat(SecurityConstants.GUEST_REALM_NAME, is("guestRealmName"));
  // }

  @Test
  public void testAllConstantsAreNonNull() {
    assertThat(SecurityConstants.SECURITY_SUBJECT, is(notNullValue()));
    assertThat(SecurityConstants.SECURITY_TOKEN_KEY, is(notNullValue()));
    assertThat(SecurityConstants.AUTHENTICATION_TOKEN_KEY, is(notNullValue()));
  }

  @Test
  public void testSecurityConstantsCanBeUsedAsKeys() {
    String key1 = SecurityConstants.SECURITY_SUBJECT;
    String key2 = SecurityConstants.SECURITY_TOKEN_KEY;
    String key3 = SecurityConstants.AUTHENTICATION_TOKEN_KEY;

    assertThat(key1, is("ddf.security.subject"));
    assertThat(key2, is("security.assertion"));
    assertThat(key3, is("ddf.security.token"));
  }

  @Test
  public void testGetKeystorePath() {
    System.setProperty(SecurityConstants.KEYSTORE_PATH, "/path/to/keystore.jks");

    String result = SecurityConstants.getKeystorePath();

    assertThat(result, is("/path/to/keystore.jks"));
  }

  @Test
  public void testGetKeystorePathWhenNotSet() {
    System.clearProperty(SecurityConstants.KEYSTORE_PATH);

    String result = SecurityConstants.getKeystorePath();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetKeystorePassword() {
    System.setProperty(SecurityConstants.KEYSTORE_PASSWORD, "secret123");

    String result = SecurityConstants.getKeystorePassword();

    assertThat(result, is("secret123"));
  }

  @Test
  public void testGetKeystorePasswordWhenNotSet() {
    System.clearProperty(SecurityConstants.KEYSTORE_PASSWORD);

    String result = SecurityConstants.getKeystorePassword();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetTruststorePath() {
    System.setProperty(SecurityConstants.TRUSTSTORE_PATH, "/path/to/truststore.jks");

    String result = SecurityConstants.getTruststorePath();

    assertThat(result, is("/path/to/truststore.jks"));
  }

  @Test
  public void testGetTruststorePathWhenNotSet() {
    System.clearProperty(SecurityConstants.TRUSTSTORE_PATH);

    String result = SecurityConstants.getTruststorePath();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetTruststorePassword() {
    System.setProperty(SecurityConstants.TRUSTSTORE_PASSWORD, "trustpass");

    String result = SecurityConstants.getTruststorePassword();

    assertThat(result, is("trustpass"));
  }

  @Test
  public void testGetTruststorePasswordWhenNotSet() {
    System.clearProperty(SecurityConstants.TRUSTSTORE_PASSWORD);

    String result = SecurityConstants.getTruststorePassword();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetDefaultSslProtocol() {
    System.setProperty(SecurityConstants.DEFAULT_SSL_PROTOCOL_PROPERTY, "TLSv1.3");

    String result = SecurityConstants.getDefaultSslProtocol();

    assertThat(result, is("TLSv1.3"));
  }

  @Test
  public void testGetDefaultSslProtocolReturnsDefaultWhenNotSet() {
    System.clearProperty(SecurityConstants.DEFAULT_SSL_PROTOCOL_PROPERTY);

    String result = SecurityConstants.getDefaultSslProtocol();

    assertThat(result, is(SecurityConstants.DEFAULT_SSL_PROTOCOL));
    assertThat(result, is("TLSv1.2"));
  }

  @Test
  public void testNewKeystore() throws KeyStoreException {
    System.setProperty(SecurityConstants.KEYSTORE_TYPE, "JKS");

    KeyStore keyStore = SecurityConstants.newKeystore();

    assertThat(keyStore, is(notNullValue()));
    assertThat(keyStore.getType(), is("JKS"));
  }

  @Test
  public void testNewKeystoreWithPKCS12() throws KeyStoreException {
    System.setProperty(SecurityConstants.KEYSTORE_TYPE, "PKCS12");

    KeyStore keyStore = SecurityConstants.newKeystore();

    assertThat(keyStore, is(notNullValue()));
    assertThat(keyStore.getType(), is("PKCS12"));
  }

  @Test
  public void testNewTruststore() throws KeyStoreException {
    System.setProperty(SecurityConstants.TRUSTSTORE_TYPE, "JKS");

    KeyStore trustStore = SecurityConstants.newTruststore();

    assertThat(trustStore, is(notNullValue()));
    assertThat(trustStore.getType(), is("JKS"));
  }

  @Test
  public void testNewTruststoreWithPKCS12() throws KeyStoreException {
    System.setProperty(SecurityConstants.TRUSTSTORE_TYPE, "PKCS12");

    KeyStore trustStore = SecurityConstants.newTruststore();

    assertThat(trustStore, is(notNullValue()));
    assertThat(trustStore.getType(), is("PKCS12"));
  }

  @Test
  public void testNewKeystoreThrowsExceptionForInvalidType() {
    System.setProperty(SecurityConstants.KEYSTORE_TYPE, "INVALID_TYPE");

    assertThrows(KeyStoreException.class, SecurityConstants::newKeystore);
  }

  @Test
  public void testNewTruststoreThrowsExceptionForInvalidType() {
    System.setProperty(SecurityConstants.TRUSTSTORE_TYPE, "INVALID_TYPE");

    assertThrows(KeyStoreException.class, SecurityConstants::newTruststore);
  }

  @Test
  public void testAdditionalConstantsAreNonNull() {
    assertThat(SecurityConstants.SECURITY_LOGGER, is(notNullValue()));
    assertThat(SecurityConstants.SECURITY_JAVA_SUBJECT, is(notNullValue()));
    assertThat(SecurityConstants.SAML_COOKIE_NAME, is(notNullValue()));
    assertThat(SecurityConstants.SAML_HEADER_NAME, is(notNullValue()));
    assertThat(SecurityConstants.HTTPS_CIPHER_SUITES, is(notNullValue()));
    assertThat(SecurityConstants.KEYSTORE_PASSWORD, is(notNullValue()));
    assertThat(SecurityConstants.KEYSTORE_PATH, is(notNullValue()));
    assertThat(SecurityConstants.KEYSTORE_TYPE, is(notNullValue()));
    assertThat(SecurityConstants.TRUSTSTORE_PASSWORD, is(notNullValue()));
    assertThat(SecurityConstants.TRUSTSTORE_PATH, is(notNullValue()));
    assertThat(SecurityConstants.TRUSTSTORE_TYPE, is(notNullValue()));
    assertThat(SecurityConstants.KEYSET_DIR, is(notNullValue()));
    assertThat(SecurityConstants.ASSOCIATED_DATA_PATH, is(notNullValue()));
    assertThat(SecurityConstants.DEFAULT_SSL_PROTOCOL, is(notNullValue()));
    assertThat(SecurityConstants.DEFAULT_SSL_PROTOCOL_PROPERTY, is(notNullValue()));
  }
}
