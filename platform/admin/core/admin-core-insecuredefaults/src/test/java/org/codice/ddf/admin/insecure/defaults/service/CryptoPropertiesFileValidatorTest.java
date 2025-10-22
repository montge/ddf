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

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/** Unit tests for {@link CryptoPropertiesFileValidator} via SignaturePropertiesFileValidator */
@RunWith(MockitoJUnitRunner.class)
public class CryptoPropertiesFileValidatorTest {

  private static final String DEFAULT_PASSWORD = "changeit";
  private static final String DEFAULT_ALIAS = "localhost";
  private static final String DEFAULT_PRIVATE_KEY_PASSWORD = "changeit";
  private static final String CUSTOM_PASSWORD = "customPass123";
  private static final String CUSTOM_ALIAS = "customAlias";
  private static final String CUSTOM_PRIVATE_KEY_PASSWORD = "customPrivateKey456";

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  private SignaturePropertiesFileValidator validator;
  private Path propertiesPath;

  @Before
  public void setUp() throws Exception {
    validator = new SignaturePropertiesFileValidator();
    propertiesPath = tempFolder.newFile("signature.properties").toPath();
  }

  @Test
  public void testValidateWithDefaultPassword() throws Exception {
    Properties props = new Properties();
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_PASSWORD_PROPERTY, DEFAULT_PASSWORD);
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_ALIAS_PROPERTY, CUSTOM_ALIAS);
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, CUSTOM_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("default keystore password")),
        is(true));
  }

  @Test
  public void testValidateWithDefaultAlias() throws Exception {
    Properties props = new Properties();
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_PASSWORD_PROPERTY, CUSTOM_PASSWORD);
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_ALIAS_PROPERTY, DEFAULT_ALIAS);
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, CUSTOM_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("default keystore alias")), is(true));
  }

  @Test
  public void testValidateWithDefaultPrivateKeyPassword() throws Exception {
    Properties props = new Properties();
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_PASSWORD_PROPERTY, CUSTOM_PASSWORD);
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_ALIAS_PROPERTY, CUSTOM_ALIAS);
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, DEFAULT_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("default keystore private password")),
        is(true));
  }

  @Test
  public void testValidateWithAllCustomValues() throws Exception {
    Properties props = new Properties();
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_PASSWORD_PROPERTY, CUSTOM_PASSWORD);
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_ALIAS_PROPERTY, CUSTOM_ALIAS);
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, CUSTOM_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    // Should have no alerts for default values
    assertThat(
        alerts.stream()
            .noneMatch(
                a -> a.getMessage().contains("default") && !a.getMessage().contains("No default")),
        is(true));
  }

  @Test
  public void testValidateWithMissingPassword() throws Exception {
    Properties props = new Properties();
    // Don't set password property
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_ALIAS_PROPERTY, CUSTOM_ALIAS);
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, CUSTOM_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("Could not find password")),
        is(true));
  }

  @Test
  public void testValidateWithMissingAlias() throws Exception {
    Properties props = new Properties();
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_PASSWORD_PROPERTY, CUSTOM_PASSWORD);
    // Don't set alias property
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, CUSTOM_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream().anyMatch(a -> a.getMessage().contains("Could not find keystore alias")),
        is(true));
  }

  @Test
  public void testValidateWithNoDefaultPasswordProvided() throws Exception {
    Properties props = new Properties();
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_PASSWORD_PROPERTY, CUSTOM_PASSWORD);
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_ALIAS_PROPERTY, CUSTOM_ALIAS);
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, CUSTOM_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    // Don't set default password
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream()
            .anyMatch(
                a -> a.getMessage().contains("No default password provided to the validator")),
        is(true));
  }

  @Test
  public void testValidateWithNoDefaultAliasProvided() throws Exception {
    Properties props = new Properties();
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_PASSWORD_PROPERTY, CUSTOM_PASSWORD);
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_ALIAS_PROPERTY, CUSTOM_ALIAS);
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, CUSTOM_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    // Don't set default alias
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    assertThat(
        alerts.stream()
            .anyMatch(
                a ->
                    a.getMessage().contains("No default keystore alias provided to the validator")),
        is(true));
  }

  @Test
  public void testValidateWithEmptyPropertiesFile() throws Exception {
    Properties props = new Properties();
    // Empty properties file

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    // Should return empty list since file has no properties
    assertThat(alerts, is(empty()));
  }

  @Test
  public void testValidateWithAllDefaultValues() throws Exception {
    Properties props = new Properties();
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_PASSWORD_PROPERTY, DEFAULT_PASSWORD);
    props.setProperty(CryptoPropertiesFileValidator.KEYSTORE_ALIAS_PROPERTY, DEFAULT_ALIAS);
    props.setProperty(
        CryptoPropertiesFileValidator.PRIVATE_KEY_PASSWORD_PROPERTY, DEFAULT_PRIVATE_KEY_PASSWORD);

    try (FileOutputStream fos = new FileOutputStream(propertiesPath.toFile())) {
      props.store(fos, null);
    }

    validator.setPath(propertiesPath);
    validator.setDefaultPassword(DEFAULT_PASSWORD);
    validator.setDefaultAlias(DEFAULT_ALIAS);
    validator.setDefaultPrivateKeyPassword(DEFAULT_PRIVATE_KEY_PASSWORD);

    List<Alert> alerts = validator.validate();

    assertThat(alerts, is(not(empty())));
    // Should have alerts for all three default values
    assertThat(
        alerts.stream()
                .filter(
                    a ->
                        a.getMessage().contains("default keystore password")
                            || a.getMessage().contains("default keystore alias")
                            || a.getMessage().contains("default keystore private password"))
                .count()
            >= 3,
        is(true));
  }
}
