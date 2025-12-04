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
package ddf.security.encryption;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link EncryptionService} interface contract. Uses a test implementation to verify the
 * interface behavior and contract expectations.
 */
public class EncryptionServiceTest {

  private static final String PLAINTEXT = "mySecretPassword";
  private static final String ENCRYPTED_VALUE = "base64EncodedEncryptedValue";
  private static final String WRAPPED_ENCRYPTED = "ENC(" + ENCRYPTED_VALUE + ")";

  private TestEncryptionService encryptionService;

  @Before
  public void setUp() {
    encryptionService = new TestEncryptionService();
  }

  @Test
  public void testEncryptValueReturnsWrappedFormat() {
    String result = encryptionService.encryptValue(PLAINTEXT);
    assertThat(result, startsWith("ENC("));
    assertThat(result.endsWith(")"), is(true));
  }

  @Test
  public void testEncryptValueWithNullReturnsNull() {
    String result = encryptionService.encryptValue(null);
    assertThat(result, is(nullValue()));
  }

  @Test
  public void testEncryptValueWithEmptyString() {
    String result = encryptionService.encryptValue("");
    assertThat(result, is("ENC()"));
  }

  @Test
  public void testDecryptValueUnwrapsAndDecrypts() {
    String result = encryptionService.decryptValue(WRAPPED_ENCRYPTED);
    assertThat(result, is(not(WRAPPED_ENCRYPTED)));
    assertThat(result, not(containsString("ENC(")));
  }

  @Test
  public void testDecryptValueWithUnwrappedInputReturnsAsIs() {
    String unwrappedValue = "notEncrypted";
    String result = encryptionService.decryptValue(unwrappedValue);
    // Per interface contract, unwrapped inputs are returned as no-op
    assertThat(result, is(unwrappedValue));
  }

  @Test
  public void testUnwrapEncryptedValueRemovesEncWrapper() {
    String result = encryptionService.unwrapEncryptedValue(WRAPPED_ENCRYPTED);
    assertThat(result, is(ENCRYPTED_VALUE));
  }

  @Test
  public void testUnwrapEncryptedValueWithUnwrappedInput() {
    String unwrappedValue = "notWrapped";
    String result = encryptionService.unwrapEncryptedValue(unwrappedValue);
    // Per interface contract, unwrapped inputs are returned as no-op
    assertThat(result, is(unwrappedValue));
  }

  @Test
  public void testEncryptDecryptRoundTrip() {
    String encrypted = encryptionService.encryptValue(PLAINTEXT);
    String decrypted = encryptionService.decryptValue(encrypted);
    assertThat(decrypted, is(PLAINTEXT));
  }

  @Test
  public void testUnwrapWithNestedParentheses() {
    String nestedValue = "ENC(value(with)parens)";
    String result = encryptionService.unwrapEncryptedValue(nestedValue);
    // Implementation should handle nested parentheses correctly
    assertThat(result, is("value(with)parens"));
  }

  @Test
  public void testEncryptPreservesSpecialCharacters() {
    String specialChars = "p@$$w0rd!#$%^&*()";
    String encrypted = encryptionService.encryptValue(specialChars);
    String decrypted = encryptionService.decryptValue(encrypted);
    assertThat(decrypted, is(specialChars));
  }

  @Test
  public void testEncryptWithWhitespace() {
    String whitespaceValue = "password with spaces";
    String encrypted = encryptionService.encryptValue(whitespaceValue);
    assertThat(encrypted, startsWith("ENC("));
    String decrypted = encryptionService.decryptValue(encrypted);
    assertThat(decrypted, is(whitespaceValue));
  }

  @Test
  public void testPasswordEncryptorInterface() {
    // EncryptionService extends PasswordEncryptor from WSS4J
    org.apache.wss4j.common.crypto.PasswordEncryptor encryptor = encryptionService;
    String encrypted = encryptor.encrypt(PLAINTEXT);
    String decrypted = encryptor.decrypt(encrypted);
    assertThat(decrypted, is(PLAINTEXT));
  }

  /** Simple test implementation of EncryptionService interface. */
  private static class TestEncryptionService implements EncryptionService {

    @Override
    public String decryptValue(String wrappedEncryptedValue) {
      if (wrappedEncryptedValue == null) {
        return null;
      }
      if (!wrappedEncryptedValue.startsWith("ENC(") || !wrappedEncryptedValue.endsWith(")")) {
        return wrappedEncryptedValue; // Return as-is per interface contract
      }
      String unwrapped = unwrapEncryptedValue(wrappedEncryptedValue);
      return decrypt(unwrapped);
    }

    @Override
    public String encryptValue(String unwrappedPlaintext) {
      if (unwrappedPlaintext == null) {
        return null;
      }
      String encrypted = encrypt(unwrappedPlaintext);
      return "ENC(" + encrypted + ")";
    }

    @Override
    public String unwrapEncryptedValue(String wrappedEncryptedValue) {
      if (wrappedEncryptedValue == null) {
        return null;
      }
      if (!wrappedEncryptedValue.startsWith("ENC(") || !wrappedEncryptedValue.endsWith(")")) {
        return wrappedEncryptedValue; // Return as-is per interface contract
      }
      return wrappedEncryptedValue.substring(4, wrappedEncryptedValue.length() - 1);
    }

    @Override
    public String encrypt(String password) {
      if (password == null) {
        return null;
      }
      // Simple test encryption: just reverse the string (NOT secure, just for testing)
      return new StringBuilder(password).reverse().toString();
    }

    @Override
    public String decrypt(String encryptedPassword) {
      if (encryptedPassword == null) {
        return null;
      }
      // Simple test decryption: reverse the string back
      return new StringBuilder(encryptedPassword).reverse().toString();
    }
  }
}
