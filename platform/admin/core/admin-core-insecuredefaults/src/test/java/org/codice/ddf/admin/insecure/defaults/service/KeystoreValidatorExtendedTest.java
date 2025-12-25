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
package org.codice.ddf.admin.insecure.defaults.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import ddf.security.SecurityConstants;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

/** Extended unit tests for {@EnableRuleMigrationSupport
 * @link KeystoreValidator} to increase coverage */
@ExtendWith(MockitoExtension.class)
public class KeystoreValidatorExtendedTest {

  private static final String DEFAULT_KEYSTORE_PASSWORD = "changeit";
  private static final String DEFAULT_KEY_PASSWORD = "changeit";
  private static final String CUSTOM_KEYSTORE_PASSWORD = "customPassword123";
  private static final String CUSTOM_KEY_PASSWORD = "customKeyPass456";

  @TempDir Path tempFolder;

  private KeystoreValidator validator;
  private Path keystorePath;
  private Path blacklistKeystorePath;

  @BeforeEach
  public void setUp() throws Exception {
    System.setProperty(SecurityConstants.KEYSTORE_TYPE, "JKS");

    validator = new KeystoreValidator();

    keystorePath = Files.createFile(tempFolder.resolve("test-keystore.jks"));
    blacklistKeystorePath = Files.createFile(tempFolder.resolve("test-blacklist.jks"));
  }

  @Test
  public void testValidateWithNoKeystorePath() {
    validator.setKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("No keystore path provided")),
        is(true));
  }

  @Test
  public void testValidateWithBlankKeystorePath() {
    validator.setKeystorePath(Paths.get(""));
    validator.setKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("No keystore path provided")),
        is(true));
  }

  @Test
  public void testValidateWithNoBlacklistKeystorePath() {
    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream()
            .anyMatch(a -> a.getMessage().contains("No Blacklist keystore path provided")),
        is(true));
  }

  @Test
  public void testValidateWithBlankBlacklistKeystorePath() {
    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(Paths.get(""));
    validator.setBlacklistKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream()
            .anyMatch(a -> a.getMessage().contains("No Blacklist keystore path provided")),
        is(true));
  }

  @Test
  public void testValidateWithNoKeystorePassword() {
    validator.setKeystorePath(keystorePath);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("No keystore password provided")),
        is(true));
  }

  @Test
  public void testValidateWithNoBlacklistKeystorePassword() {
    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream()
            .anyMatch(
                a ->
                    a.getMessage().contains("Password for Blacklist keystore")
                        && a.getMessage().contains("was not provided")),
        is(true));
  }

  @Test
  public void testValidateWithDefaultKeystorePassword() throws Exception {
    createKeystore(keystorePath, DEFAULT_KEYSTORE_PASSWORD, "testAlias", DEFAULT_KEY_PASSWORD);
    createKeystore(
        blacklistKeystorePath, DEFAULT_KEYSTORE_PASSWORD, "blacklistAlias", DEFAULT_KEY_PASSWORD);

    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setDefaultKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setDefaultKeyPassword(DEFAULT_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream()
            .anyMatch(
                a ->
                    a.getMessage().contains("keystore password")
                        && a.getMessage().contains("default password")),
        is(true));
  }

  @Test
  public void testValidateWithDefaultKeyPassword() throws Exception {
    createKeystore(keystorePath, CUSTOM_KEYSTORE_PASSWORD, "testAlias", DEFAULT_KEY_PASSWORD);
    createKeystore(
        blacklistKeystorePath, CUSTOM_KEYSTORE_PASSWORD, "blacklistAlias", CUSTOM_KEY_PASSWORD);

    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    validator.setDefaultKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    validator.setDefaultKeyPassword(DEFAULT_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream()
            .anyMatch(
                a ->
                    a.getMessage().contains("key for alias")
                        && a.getMessage().contains("default password")),
        is(true));
  }

  @Test
  public void testValidateWithNonExistentKeystore() {
    Path nonExistentPath = tempFolder.resolve("nonexistent.jks");

    validator.setKeystorePath(nonExistentPath);
    validator.setKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("Cannot read keystore")), is(true));
  }

  @Test
  public void testValidateWithNonExistentBlacklistKeystore() throws Exception {
    createKeystore(keystorePath, CUSTOM_KEYSTORE_PASSWORD, "testAlias", CUSTOM_KEY_PASSWORD);
    Path nonExistentPath = tempFolder.resolve("nonexistent-blacklist.jks");

    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(nonExistentPath);
    validator.setBlacklistKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("Cannot read Blacklist keystore")),
        is(true));
  }

  @Test
  public void testValidateWithNoDefaultKeyPassword() throws Exception {
    createKeystore(keystorePath, CUSTOM_KEYSTORE_PASSWORD, "testAlias", CUSTOM_KEY_PASSWORD);
    createKeystore(
        blacklistKeystorePath, CUSTOM_KEYSTORE_PASSWORD, "blacklistAlias", CUSTOM_KEY_PASSWORD);

    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    validator.setDefaultKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    // Don't set default key password

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("No key password provided")),
        is(true));
  }

  @Test
  public void testValidateWithBlacklistedCertificate() throws Exception {
    // Create a certificate
    KeyPair keyPair = generateKeyPair();
    X509Certificate cert = generateCertificate("CN=Test", keyPair, 365);

    // Create keystore with the certificate
    KeyStore keystore = KeyStore.getInstance("JKS");
    keystore.load(null, null);
    keystore.setCertificateEntry("testCert", cert);
    try (FileOutputStream fos = new FileOutputStream(keystorePath.toFile())) {
      keystore.store(fos, CUSTOM_KEYSTORE_PASSWORD.toCharArray());
    }

    // Create blacklist keystore with the same certificate
    KeyStore blacklistKeystore = KeyStore.getInstance("JKS");
    blacklistKeystore.load(null, null);
    blacklistKeystore.setCertificateEntry("blacklistedCert", cert);
    try (FileOutputStream fos = new FileOutputStream(blacklistKeystorePath.toFile())) {
      blacklistKeystore.store(fos, CUSTOM_KEYSTORE_PASSWORD.toCharArray());
    }

    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    validator.setDefaultKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    validator.setDefaultKeyPassword(DEFAULT_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("blacklisted certificate")),
        is(true));
  }

  @Test
  public void testValidateWithCustomPasswordsAndNoBlacklisted() throws Exception {
    createKeystore(keystorePath, CUSTOM_KEYSTORE_PASSWORD, "testAlias", CUSTOM_KEY_PASSWORD);
    createKeystore(
        blacklistKeystorePath, CUSTOM_KEYSTORE_PASSWORD, "differentAlias", CUSTOM_KEY_PASSWORD);

    validator.setKeystorePath(keystorePath);
    validator.setKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    validator.setDefaultKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
    validator.setBlacklistKeystorePath(blacklistKeystorePath);
    validator.setBlacklistKeystorePassword(CUSTOM_KEYSTORE_PASSWORD);
    validator.setDefaultKeyPassword(DEFAULT_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    // Should be empty or only contain warnings about non-default passwords being good
    assertThat(
        alerts.stream()
            .noneMatch(
                a ->
                    a.getMessage().contains("default password")
                        || a.getMessage().contains("blacklisted")),
        is(true));
  }

  private void createKeystore(Path path, String password, String alias, String keyPassword)
      throws Exception {
    KeyStore keystore = KeyStore.getInstance("JKS");
    keystore.load(null, null);

    KeyPair keyPair = generateKeyPair();
    X509Certificate cert = generateCertificate("CN=" + alias, keyPair, 365);

    keystore.setKeyEntry(
        alias, keyPair.getPrivate(), keyPassword.toCharArray(), new Certificate[] {cert});

    try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
      keystore.store(fos, password.toCharArray());
    }
  }

  private KeyPair generateKeyPair() throws Exception {
    return TestCertificateGenerator.generateKeyPair();
  }

  private X509Certificate generateCertificate(String dn, KeyPair keyPair, int validityDays)
      throws Exception {
    return TestCertificateGenerator.generateCertificate(dn, keyPair, validityDays);
  }
}
