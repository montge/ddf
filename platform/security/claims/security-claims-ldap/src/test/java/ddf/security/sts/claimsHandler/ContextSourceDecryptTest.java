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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.encryption.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test harness for ContextSourceDecrypt LDAP password decryption utility.
 *
 * <p>Tests the secure password handling for LDAP context sources, including encryption service
 * integration and null-safety.
 *
 * <p>Coverage includes: - Password decryption with EncryptionService - Null EncryptionService
 * handling - Empty and null password handling - Multiple password set operations -
 * EncryptionService lifecycle
 */
@ExtendWith(MockitoExtension.class)
public class ContextSourceDecryptTest {

  private static final String ENCRYPTED_PASSWORD = "ENC(abc123xyz)";
  private static final String DECRYPTED_PASSWORD = "plainTextPassword";
  private static final String PLAIN_PASSWORD = "simplePassword";

  @Mock private EncryptionService mockEncryptionService;

  private ContextSourceDecrypt contextSourceDecrypt;

  @BeforeEach
  public void setUp() {
    contextSourceDecrypt = new ContextSourceDecrypt();
  }

  /**
   * Tests password decryption when EncryptionService is properly configured.
   *
   * <p>Verifies that encrypted passwords are decrypted before being set.
   */
  @Test
  public void testSetPasswordWithEncryptionService() {
    when(mockEncryptionService.decryptValue(ENCRYPTED_PASSWORD)).thenReturn(DECRYPTED_PASSWORD);

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword(ENCRYPTED_PASSWORD);

    verify(mockEncryptionService, times(1)).decryptValue(ENCRYPTED_PASSWORD);
    assertThat(
        "Password should be decrypted", contextSourceDecrypt.getPassword(), is(DECRYPTED_PASSWORD));
  }

  /**
   * Tests password handling when EncryptionService is null.
   *
   * <p>Verifies that password is stored as-is when no encryption service is available.
   */
  @Test
  public void testSetPasswordWithoutEncryptionService() {
    // Do not set encryption service (remains null)
    contextSourceDecrypt.setPassword(ENCRYPTED_PASSWORD);

    assertThat(
        "Password should be stored as-is when EncryptionService is null",
        contextSourceDecrypt.getPassword(),
        is(ENCRYPTED_PASSWORD));
  }

  /**
   * Tests setting null password with EncryptionService configured.
   *
   * <p>Verifies graceful handling of null password value.
   */
  @Test
  public void testSetNullPasswordWithEncryptionService() {
    when(mockEncryptionService.decryptValue(null)).thenReturn(null);

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword(null);

    verify(mockEncryptionService, times(1)).decryptValue(null);
    assertThat("Null password should remain null", contextSourceDecrypt.getPassword(), nullValue());
  }

  /**
   * Tests setting null password without EncryptionService.
   *
   * <p>Verifies that null password is stored when no encryption service is available.
   */
  @Test
  public void testSetNullPasswordWithoutEncryptionService() {
    contextSourceDecrypt.setPassword(null);

    assertThat(
        "Null password should be stored as-is", contextSourceDecrypt.getPassword(), nullValue());
  }

  /**
   * Tests setting empty password with EncryptionService.
   *
   * <p>Verifies that empty string passwords are handled correctly.
   */
  @Test
  public void testSetEmptyPasswordWithEncryptionService() {
    when(mockEncryptionService.decryptValue("")).thenReturn("");

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword("");

    verify(mockEncryptionService, times(1)).decryptValue("");
    assertThat("Empty password should be decrypted", contextSourceDecrypt.getPassword(), is(""));
  }

  /**
   * Tests setting empty password without EncryptionService.
   *
   * <p>Verifies that empty password is stored when no encryption service is available.
   */
  @Test
  public void testSetEmptyPasswordWithoutEncryptionService() {
    contextSourceDecrypt.setPassword("");

    assertThat("Empty password should be stored as-is", contextSourceDecrypt.getPassword(), is(""));
  }

  /**
   * Tests multiple password changes with EncryptionService.
   *
   * <p>Verifies that password can be changed multiple times and each change triggers decryption.
   */
  @Test
  public void testMultiplePasswordChangesWithEncryptionService() {
    when(mockEncryptionService.decryptValue(ENCRYPTED_PASSWORD)).thenReturn(DECRYPTED_PASSWORD);
    when(mockEncryptionService.decryptValue(PLAIN_PASSWORD)).thenReturn(PLAIN_PASSWORD);

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);

    // First password change
    contextSourceDecrypt.setPassword(ENCRYPTED_PASSWORD);
    assertThat(
        "First password should be decrypted",
        contextSourceDecrypt.getPassword(),
        is(DECRYPTED_PASSWORD));

    // Second password change
    contextSourceDecrypt.setPassword(PLAIN_PASSWORD);
    assertThat(
        "Second password should be decrypted",
        contextSourceDecrypt.getPassword(),
        is(PLAIN_PASSWORD));

    verify(mockEncryptionService, times(1)).decryptValue(ENCRYPTED_PASSWORD);
    verify(mockEncryptionService, times(1)).decryptValue(PLAIN_PASSWORD);
  }

  /**
   * Tests setting EncryptionService after password has been set.
   *
   * <p>Verifies that setting EncryptionService later does not re-process existing password.
   */
  @Test
  public void testSetEncryptionServiceAfterPassword() {
    // Set password first (without encryption service)
    contextSourceDecrypt.setPassword(ENCRYPTED_PASSWORD);
    assertThat(
        "Password should be stored as-is initially",
        contextSourceDecrypt.getPassword(),
        is(ENCRYPTED_PASSWORD));

    // Now set encryption service
    contextSourceDecrypt.setEncryptionService(mockEncryptionService);

    // Verify that the already-set password is NOT re-processed
    verify(mockEncryptionService, never()).decryptValue(anyString());
    assertThat(
        "Password should remain unchanged",
        contextSourceDecrypt.getPassword(),
        is(ENCRYPTED_PASSWORD));
  }

  /**
   * Tests EncryptionService returning null for decryption.
   *
   * <p>Verifies handling when decryption service returns null (failed decryption).
   */
  @Test
  public void testEncryptionServiceReturnsNull() {
    when(mockEncryptionService.decryptValue(ENCRYPTED_PASSWORD)).thenReturn(null);

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword(ENCRYPTED_PASSWORD);

    verify(mockEncryptionService, times(1)).decryptValue(ENCRYPTED_PASSWORD);
    assertThat(
        "Password should be null if decryption returns null",
        contextSourceDecrypt.getPassword(),
        nullValue());
  }

  /**
   * Tests EncryptionService returning empty string for decryption.
   *
   * <p>Verifies handling when decryption service returns empty string.
   */
  @Test
  public void testEncryptionServiceReturnsEmptyString() {
    when(mockEncryptionService.decryptValue(ENCRYPTED_PASSWORD)).thenReturn("");

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword(ENCRYPTED_PASSWORD);

    verify(mockEncryptionService, times(1)).decryptValue(ENCRYPTED_PASSWORD);
    assertThat(
        "Password should be empty string if decryption returns empty",
        contextSourceDecrypt.getPassword(),
        is(""));
  }

  /**
   * Tests plain text password handling with EncryptionService.
   *
   * <p>Verifies that even plain text passwords are passed through decryption service (service
   * should recognize and return as-is).
   */
  @Test
  public void testPlainTextPasswordWithEncryptionService() {
    when(mockEncryptionService.decryptValue(PLAIN_PASSWORD)).thenReturn(PLAIN_PASSWORD);

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword(PLAIN_PASSWORD);

    verify(mockEncryptionService, times(1)).decryptValue(PLAIN_PASSWORD);
    assertThat(
        "Plain password should be processed by encryption service",
        contextSourceDecrypt.getPassword(),
        is(PLAIN_PASSWORD));
  }

  /**
   * Tests replacing EncryptionService with different instance.
   *
   * <p>Verifies that EncryptionService can be replaced and subsequent operations use new service.
   */
  @Test
  public void testReplacingEncryptionService() {
    EncryptionService firstService = mock(EncryptionService.class);
    EncryptionService secondService = mock(EncryptionService.class);

    when(firstService.decryptValue(ENCRYPTED_PASSWORD)).thenReturn("firstDecrypted");
    when(secondService.decryptValue(PLAIN_PASSWORD)).thenReturn("secondDecrypted");

    // Set first encryption service
    contextSourceDecrypt.setEncryptionService(firstService);
    contextSourceDecrypt.setPassword(ENCRYPTED_PASSWORD);
    assertThat(
        "Should use first service", contextSourceDecrypt.getPassword(), is("firstDecrypted"));

    // Replace with second encryption service
    contextSourceDecrypt.setEncryptionService(secondService);
    contextSourceDecrypt.setPassword(PLAIN_PASSWORD);
    assertThat(
        "Should use second service", contextSourceDecrypt.getPassword(), is("secondDecrypted"));

    verify(firstService, times(1)).decryptValue(ENCRYPTED_PASSWORD);
    verify(secondService, times(1)).decryptValue(PLAIN_PASSWORD);
  }

  /**
   * Tests setting null EncryptionService.
   *
   * <p>Verifies that EncryptionService can be explicitly set to null.
   */
  @Test
  public void testSetNullEncryptionService() {
    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setEncryptionService(null);
    contextSourceDecrypt.setPassword(ENCRYPTED_PASSWORD);

    verify(mockEncryptionService, never()).decryptValue(anyString());
    assertThat(
        "Password should be stored as-is when service is null",
        contextSourceDecrypt.getPassword(),
        is(ENCRYPTED_PASSWORD));
  }

  /**
   * Tests password with special characters.
   *
   * <p>Verifies that passwords containing special characters are handled correctly.
   */
  @Test
  public void testPasswordWithSpecialCharacters() {
    String specialPassword = "p@ssw0rd!#$%^&*()_+-={}[]|:;<>?,./";
    String decryptedSpecial = "decrypted!@#$%";

    when(mockEncryptionService.decryptValue(specialPassword)).thenReturn(decryptedSpecial);

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword(specialPassword);

    verify(mockEncryptionService, times(1)).decryptValue(specialPassword);
    assertThat(
        "Special characters should be handled",
        contextSourceDecrypt.getPassword(),
        is(decryptedSpecial));
  }

  /**
   * Tests very long password string.
   *
   * <p>Verifies that long passwords are handled without issues.
   */
  @Test
  public void testVeryLongPassword() {
    String longPassword = new String(new char[1000]).replace('\0', 'x');
    String decryptedLong = "decryptedLong";

    when(mockEncryptionService.decryptValue(longPassword)).thenReturn(decryptedLong);

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword(longPassword);

    verify(mockEncryptionService, times(1)).decryptValue(longPassword);
    assertThat(
        "Long password should be handled", contextSourceDecrypt.getPassword(), is(decryptedLong));
  }

  /**
   * Tests password with Unicode characters.
   *
   * <p>Verifies that passwords with international characters are handled correctly.
   */
  @Test
  public void testPasswordWithUnicodeCharacters() {
    String unicodePassword = "пароль密码пароль";
    String decryptedUnicode = "decryptedUnicode";

    when(mockEncryptionService.decryptValue(unicodePassword)).thenReturn(decryptedUnicode);

    contextSourceDecrypt.setEncryptionService(mockEncryptionService);
    contextSourceDecrypt.setPassword(unicodePassword);

    verify(mockEncryptionService, times(1)).decryptValue(unicodePassword);
    assertThat(
        "Unicode password should be handled",
        contextSourceDecrypt.getPassword(),
        is(decryptedUnicode));
  }
}
