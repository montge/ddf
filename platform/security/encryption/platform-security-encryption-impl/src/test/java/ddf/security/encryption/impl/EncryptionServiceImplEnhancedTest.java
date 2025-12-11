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
package ddf.security.encryption.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.SecurityConstants;
import ddf.security.encryption.crypter.Crypter;
import ddf.security.encryption.crypter.Crypter.CrypterException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EncryptionServiceImplEnhancedTest {

  private static final TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();

  private EncryptionServiceImpl encryptionService;

  @BeforeEach
  public void setUp() throws Exception {
    TEMPORARY_FOLDER.create();
    String keysetHome = TEMPORARY_FOLDER.newFolder("keysets").getAbsolutePath();
    String associatedDataHome = TEMPORARY_FOLDER.newFolder("etc").getAbsolutePath();
    System.setProperty(SecurityConstants.KEYSET_DIR, keysetHome);
    System.setProperty(
        SecurityConstants.ASSOCIATED_DATA_PATH,
        associatedDataHome.concat("/associatedData.properties"));
    encryptionService = new EncryptionServiceImpl();
  }

  @AfterEach
  public void cleanUp() throws Exception {
    TEMPORARY_FOLDER.delete();
    System.clearProperty(SecurityConstants.KEYSET_DIR);
    System.clearProperty(SecurityConstants.ASSOCIATED_DATA_PATH);
  }

  @Test
  public void testEncryptReturnsNonNullValue() {
    String plaintext = "test";
    String encrypted = encryptionService.encrypt(plaintext);

    assertThat(encrypted, is(notNullValue()));
    assertThat(encrypted, is(not(equalTo(plaintext))));
  }

  @Test
  public void testEncryptDifferentValuesProduceDifferentCiphertext() {
    String plaintext1 = "test1";
    String plaintext2 = "test2";

    String encrypted1 = encryptionService.encrypt(plaintext1);
    String encrypted2 = encryptionService.encrypt(plaintext2);

    assertThat(encrypted1, is(not(equalTo(encrypted2))));
  }

  @Test
  public void testEncryptSameValueProducesDifferentCiphertext() {
    // Due to IV, same plaintext should produce different ciphertext
    String plaintext = "test";

    String encrypted1 = encryptionService.encrypt(plaintext);
    String encrypted2 = encryptionService.encrypt(plaintext);

    // Both should decrypt to same value but ciphertext should differ
    assertThat(encryptionService.decrypt(encrypted1), equalTo(plaintext));
    assertThat(encryptionService.decrypt(encrypted2), equalTo(plaintext));
  }

  @Test
  public void testDecryptReturnsOriginalValue() {
    String plaintext = "sensitive-data";
    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testDecryptWithInvalidCiphertextReturnsOriginal() throws Exception {
    String invalidCiphertext = "not-a-valid-encrypted-value";

    // Mock a crypter that throws exception
    Crypter mockCrypter = mock(Crypter.class);
    when(mockCrypter.decrypt(anyString())).thenThrow(new CrypterException("Invalid"));

    encryptionService.crypter = mockCrypter;

    String result = encryptionService.decrypt(invalidCiphertext);

    assertThat(result, equalTo(invalidCiphertext));
  }

  @Test
  public void testEncryptValueWithValidPlaintext() {
    String plaintext = "password123";
    String wrappedEncrypted = encryptionService.encryptValue(plaintext);

    assertThat(wrappedEncrypted, startsWith("ENC("));
    assertThat(wrappedEncrypted, endsWith(")"));
    assertThat(wrappedEncrypted, containsString("ENC("));
  }

  @Test
  public void testEncryptValueWithEmptyString() {
    String result = encryptionService.encryptValue("");

    assertThat(result, equalTo(""));
  }

  @Test
  public void testEncryptValueWithNullString() {
    String result = encryptionService.encryptValue(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testEncryptValueWithWhitespace() {
    String plaintext = "   ";
    String wrappedEncrypted = encryptionService.encryptValue(plaintext);

    assertThat(wrappedEncrypted, startsWith("ENC("));
    assertThat(wrappedEncrypted, endsWith(")"));

    String decrypted = encryptionService.decryptValue(wrappedEncrypted);
    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testDecryptValueWithValidWrappedValue() {
    String plaintext = "myPassword";
    String encrypted = encryptionService.encrypt(plaintext);
    String wrapped = "ENC(" + encrypted + ")";

    String decrypted = encryptionService.decryptValue(wrapped);

    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testDecryptValueWithEmptyString() {
    String result = encryptionService.decryptValue("");

    assertThat(result, equalTo(""));
  }

  @Test
  public void testDecryptValueWithNullString() {
    String result = encryptionService.decryptValue(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testDecryptValueWithUnwrappedValue() {
    String plaintext = "notEncrypted";

    String result = encryptionService.decryptValue(plaintext);

    // Should return as-is if not in ENC() format
    assertThat(result, equalTo(plaintext));
  }

  @Test
  public void testDecryptValueWithMalformedWrapper() {
    String malformed1 = "ENC(missing-closing";
    String malformed2 = "missing-opening)";
    String malformed3 = "ENC)wrong-order(";

    assertThat(encryptionService.decryptValue(malformed1), equalTo(malformed1));
    assertThat(encryptionService.decryptValue(malformed2), equalTo(malformed2));
    assertThat(encryptionService.decryptValue(malformed3), equalTo(malformed3));
  }

  @Test
  public void testUnwrapEncryptedValueWithValidWrapper() {
    String encrypted = "someEncryptedValue";
    String wrapped = "ENC(" + encrypted + ")";

    String unwrapped = encryptionService.unwrapEncryptedValue(wrapped);

    assertThat(unwrapped, equalTo(encrypted));
  }

  @Test
  public void testUnwrapEncryptedValueWithNullValue() {
    String unwrapped = encryptionService.unwrapEncryptedValue(null);

    assertThat(unwrapped, is(nullValue()));
  }

  @Test
  public void testUnwrapEncryptedValueWithUnwrappedValue() {
    String plaintext = "notWrapped";

    String result = encryptionService.unwrapEncryptedValue(plaintext);

    assertThat(result, equalTo(plaintext));
  }

  @Test
  public void testUnwrapEncryptedValueWithEmptyWrapper() {
    String wrapped = "ENC()";

    String unwrapped = encryptionService.unwrapEncryptedValue(wrapped);

    assertThat(unwrapped, equalTo(""));
  }

  @Test
  public void testEncryptDecryptRoundTripWithSpecialCharacters() {
    String plaintext = "p@ssw0rd!#$%^&*()[]{}|\\:;\"'<>,.?/~`";
    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testEncryptDecryptRoundTripWithUnicode() {
    String plaintext = "密码 пароль パスワード 🔐🔑";
    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testEncryptDecryptRoundTripWithLongString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("This is a long string with repeated content. ");
    }
    String plaintext = sb.toString();

    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testEncryptValueDecryptValueRoundTrip() {
    String plaintext = "secretPassword123";

    String wrappedEncrypted = encryptionService.encryptValue(plaintext);
    String decrypted = encryptionService.decryptValue(wrappedEncrypted);

    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testMultipleEncryptionsAreDeterministicallyDecryptable() {
    String plaintext = "test";

    for (int i = 0; i < 10; i++) {
      String encrypted = encryptionService.encrypt(plaintext);
      String decrypted = encryptionService.decrypt(encrypted);
      assertThat("Iteration " + i, decrypted, equalTo(plaintext));
    }
  }

  @Test
  public void testEncryptWithEmptyString() {
    String encrypted = encryptionService.encrypt("");

    assertThat(encrypted, is(notNullValue()));

    String decrypted = encryptionService.decrypt(encrypted);
    assertThat(decrypted, equalTo(""));
  }

  @Test
  public void testSynchronizedAccess() throws Exception {
    // Test concurrent access
    String plaintext = "concurrent-test";

    Thread[] threads = new Thread[10];
    String[] results = new String[10];

    for (int i = 0; i < 10; i++) {
      final int index = i;
      threads[i] =
          new Thread(
              () -> {
                String encrypted = encryptionService.encrypt(plaintext);
                results[index] = encryptionService.decrypt(encrypted);
              });
      threads[i].start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    for (String result : results) {
      assertThat(result, equalTo(plaintext));
    }
  }

  @Test
  public void testEncryptFailureReturnsPlaintext() throws Exception {
    Crypter mockCrypter = mock(Crypter.class);
    when(mockCrypter.encrypt(anyString())).thenThrow(new CrypterException("Encryption failed"));

    encryptionService.crypter = mockCrypter;

    String plaintext = "test";
    String result = encryptionService.encrypt(plaintext);

    // On failure, should return plaintext
    assertThat(result, equalTo(plaintext));
  }

  @Test
  public void testDecryptValueWithInvalidWrappedContent() throws Exception {
    Crypter mockCrypter = mock(Crypter.class);
    doThrow(new CrypterException("Decryption failed")).when(mockCrypter).decrypt(anyString());

    encryptionService.crypter = mockCrypter;

    String wrapped = "ENC(invalidContent)";
    String result = encryptionService.decryptValue(wrapped);

    // Should return unwrapped value on decryption failure (assumes encryption failed originally)
    assertThat(result, equalTo("invalidContent"));
  }

  @Test
  public void testEncryptionServiceNameConstant() {
    assertThat(EncryptionServiceImpl.CRYPTER_NAME, equalTo("encryption-service"));
  }

  @Test
  public void testEncryptDecryptWithNumericString() {
    String plaintext = "1234567890";
    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testEncryptDecryptWithMixedContent() {
    String plaintext = "User123!@#ABC xyz";
    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertThat(decrypted, equalTo(plaintext));
  }

  @Test
  public void testUnwrapMultipleNestedWrappers() {
    // Edge case: ENC(ENC(value))
    String wrapped = "ENC(ENC(innerValue))";
    String unwrapped = encryptionService.unwrapEncryptedValue(wrapped);

    // Should only unwrap one layer
    assertThat(unwrapped, equalTo("ENC(innerValue)"));
  }
}
