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
package ddf.security.encryption.crypter;

import static ddf.security.encryption.crypter.Crypter.CHUNK_SIZE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import com.google.common.io.ByteStreams;
import ddf.security.SecurityConstants;
import ddf.security.encryption.crypter.Crypter.CrypterException;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;

@EnableRuleMigrationSupport
public class CrypterTest {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @BeforeEach
  public void setUp() throws Exception {
    String keysetHome = temporaryFolder.newFolder("keysets").getAbsolutePath();
    String associatedDataHome = temporaryFolder.newFolder("etc").getAbsolutePath();
    System.setProperty(SecurityConstants.KEYSET_DIR, keysetHome);
    System.setProperty(
        SecurityConstants.ASSOCIATED_DATA_PATH,
        associatedDataHome.concat("/associatedData.properties"));
  }

  @AfterEach
  public void cleanUp() throws Exception {
    System.clearProperty(SecurityConstants.KEYSET_DIR);
    System.clearProperty(SecurityConstants.ASSOCIATED_DATA_PATH);
  }

  @Test
  public void testBadSetup() throws Exception {
    assertThrows(
        CrypterException.class,
        () -> {
          try (OutputStream badKeysetOutputStream =
              new FileOutputStream(
                  System.getProperty(SecurityConstants.KEYSET_DIR) + "/default.json")) {
            badKeysetOutputStream.write("BadKeyset".getBytes());
          }
          new Crypter();
        });
  }

  @Test
  public void testEncryptDecrypt() throws Exception {
    final byte[] plainBytes = new byte[16];
    new SecureRandom().nextBytes(plainBytes);
    final Crypter crypter = new Crypter();

    final byte[] encryptedBytes = crypter.encrypt(plainBytes);

    final byte[] decryptedBytes = crypter.decrypt(encryptedBytes);

    assertArrayEquals(plainBytes, decryptedBytes);
  }

  @Test
  public void testEncryptDecryptString() throws Exception {
    final String plainPassword = "protect";
    final Crypter crypter = new Crypter();

    final String encryptedPassword = crypter.encrypt(plainPassword);

    final String decryptedPassword = crypter.decrypt(encryptedPassword);

    assertEquals(plainPassword, decryptedPassword);
  }

  @Test
  public void testEncryptDecryptStream() throws Exception {
    // make test data larger than chunk size
    final byte[] plainBytes = new byte[CHUNK_SIZE * 3];
    new SecureRandom().nextBytes(plainBytes);
    final InputStream plainInputStream = new ByteArrayInputStream(plainBytes);
    final Crypter crypter = new Crypter();

    final InputStream encryptedInputStream = crypter.encrypt(plainInputStream);

    final InputStream decryptedInputStream = crypter.decrypt(encryptedInputStream);

    final byte[] decryptedBytes = ByteStreams.toByteArray(decryptedInputStream);

    assertArrayEquals(plainBytes, decryptedBytes);
  }

  @Test
  public void testEncryptNull() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final String nullString = null;

          crypter.encrypt(nullString);
        });
  }

  @Test
  public void testEncryptNullStream() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final InputStream nullInputStream = null;

          crypter.encrypt(nullInputStream);
        });
  }

  @Test
  public void testEncryptEmpty() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final String emptyString = "";

          crypter.encrypt(emptyString);
        });
  }

  @Test
  public void testEncryptEmptyStream() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final InputStream emptyInputStream = new ByteArrayInputStream("".getBytes());

          crypter.encrypt(emptyInputStream);
        });
  }

  @Test
  public void testEncryptBlank() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();

          crypter.encrypt(" ");
        });
  }

  @Test
  public void testDecryptNull() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final String nullString = null;

          crypter.decrypt(nullString);
        });
  }

  @Test
  public void testDecryptNullStream() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final InputStream nullInputStream = null;

          crypter.decrypt(nullInputStream);
        });
  }

  @Test
  public void testDecryptEmpty() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();

          crypter.decrypt("");
        });
  }

  @Test
  public void testDecryptEmptyStream() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final InputStream emptyInputStream = new ByteArrayInputStream("".getBytes());

          crypter.decrypt(emptyInputStream);
        });
  }

  @Test
  public void testReusabilityAndInteroperabilitySameKeyset() throws Exception {
    final Crypter crypter1 = new Crypter();
    final Crypter crypter2 = new Crypter();

    assertEquals(crypter1.keysetHandle.getKeysetInfo(), crypter2.keysetHandle.getKeysetInfo());

    final String unencryptedPassword = "protect";

    String encryptedPassword1 = crypter1.encrypt(unencryptedPassword);
    String encryptedPassword2 = crypter2.encrypt(unencryptedPassword);

    String plainPassword1 = crypter1.decrypt(encryptedPassword2);
    String plainPassword2 = crypter2.decrypt(encryptedPassword1);

    assertEquals(unencryptedPassword, plainPassword1);
    assertEquals(unencryptedPassword, plainPassword2);
  }

  @Test
  public void testReusabilityAndInteroperabilityDifferentKeysets() throws Exception {
    final Crypter crypter1 = new Crypter("crypter1");
    final Crypter crypter2 = new Crypter("crypter2");
    final String plaintext = "protect";

    final String encrypted1 = crypter1.encrypt(plaintext);
    final String encrypted2 = crypter2.encrypt(plaintext);

    try {
      crypter1.decrypt(encrypted2);
      fail("Keyset1 should not be able to decrypt a value encrypted by keyset2.");
    } catch (CrypterException expected) {
    }

    try {
      crypter2.decrypt(encrypted1);
      fail("Keyset2 should not be able to decrypt a value encrypted by keyset1.");
    } catch (CrypterException expected) {
    }
  }

  @Test
  public void testEncryptNullBytes() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final byte[] nullBytes = null;

          crypter.encrypt(nullBytes);
        });
  }

  @Test
  public void testEncryptEmptyBytes() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final byte[] emptyBytes = new byte[0];

          crypter.encrypt(emptyBytes);
        });
  }

  @Test
  public void testDecryptNullBytes() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final byte[] nullBytes = null;

          crypter.decrypt(nullBytes);
        });
  }

  @Test
  public void testDecryptEmptyBytes() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();
          final byte[] emptyBytes = new byte[0];

          crypter.decrypt(emptyBytes);
        });
  }

  @Test
  public void testDecryptBlank() {
    assertThrows(
        CrypterException.class,
        () -> {
          final Crypter crypter = new Crypter();

          crypter.decrypt(" ");
        });
  }

  @Test
  public void testDecryptInvalidBase64() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          final Crypter crypter = new Crypter();

          // Invalid base64 string should cause decryption failure (IllegalArgumentException from
          // Base64
          // decoder)
          crypter.decrypt("this-is-not-valid-base64!@#$%");
        });
  }

  @Test
  public void testEncryptDecryptLargeByteArray() throws Exception {
    // Test with large byte array (1MB)
    final byte[] plainBytes = new byte[1024 * 1024];
    new SecureRandom().nextBytes(plainBytes);
    final Crypter crypter = new Crypter();

    final byte[] encryptedBytes = crypter.encrypt(plainBytes);
    final byte[] decryptedBytes = crypter.decrypt(encryptedBytes);

    assertArrayEquals(plainBytes, decryptedBytes);
  }

  @Test
  public void testEncryptDecryptStreamLargerThanMultipleChunks() throws Exception {
    // Test with data larger than 10 chunks
    final byte[] plainBytes = new byte[CHUNK_SIZE * 10 + 123];
    new SecureRandom().nextBytes(plainBytes);
    final InputStream plainInputStream = new ByteArrayInputStream(plainBytes);
    final Crypter crypter = new Crypter();

    final InputStream encryptedInputStream = crypter.encrypt(plainInputStream);
    final InputStream decryptedInputStream = crypter.decrypt(encryptedInputStream);

    final byte[] decryptedBytes = ByteStreams.toByteArray(decryptedInputStream);

    assertArrayEquals(plainBytes, decryptedBytes);
  }

  @Test
  public void testEncryptDecryptStreamSmallerThanChunk() throws Exception {
    // Test with data smaller than chunk size
    final byte[] plainBytes = new byte[CHUNK_SIZE / 2];
    new SecureRandom().nextBytes(plainBytes);
    final InputStream plainInputStream = new ByteArrayInputStream(plainBytes);
    final Crypter crypter = new Crypter();

    final InputStream encryptedInputStream = crypter.encrypt(plainInputStream);
    final InputStream decryptedInputStream = crypter.decrypt(encryptedInputStream);

    final byte[] decryptedBytes = ByteStreams.toByteArray(decryptedInputStream);

    assertArrayEquals(plainBytes, decryptedBytes);
  }

  @Test
  public void testEncryptDecryptStreamExactlyChunkSize() throws Exception {
    // Test with data exactly equal to chunk size
    final byte[] plainBytes = new byte[CHUNK_SIZE];
    new SecureRandom().nextBytes(plainBytes);
    final InputStream plainInputStream = new ByteArrayInputStream(plainBytes);
    final Crypter crypter = new Crypter();

    final InputStream encryptedInputStream = crypter.encrypt(plainInputStream);
    final InputStream decryptedInputStream = crypter.decrypt(encryptedInputStream);

    final byte[] decryptedBytes = ByteStreams.toByteArray(decryptedInputStream);

    assertArrayEquals(plainBytes, decryptedBytes);
  }

  @Test
  public void testEncryptDecryptStreamOneByte() throws Exception {
    // Test with minimal data
    final byte[] plainBytes = new byte[1];
    plainBytes[0] = 42;
    final InputStream plainInputStream = new ByteArrayInputStream(plainBytes);
    final Crypter crypter = new Crypter();

    final InputStream encryptedInputStream = crypter.encrypt(plainInputStream);
    final InputStream decryptedInputStream = crypter.decrypt(encryptedInputStream);

    final byte[] decryptedBytes = ByteStreams.toByteArray(decryptedInputStream);

    assertArrayEquals(plainBytes, decryptedBytes);
  }

  @Test
  public void testEncryptDecryptStringWithSpecialCharacters() throws Exception {
    final String plainPassword = "p@$$w0rd!#$%^&*()[]{}|\\:;\"'<>,.?/~`";
    final Crypter crypter = new Crypter();

    final String encryptedPassword = crypter.encrypt(plainPassword);
    final String decryptedPassword = crypter.decrypt(encryptedPassword);

    assertEquals(plainPassword, decryptedPassword);
  }

  @Test
  public void testEncryptDecryptStringWithUnicode() throws Exception {
    final String plainPassword = "密码 пароль パスワード 🔐🔑";
    final Crypter crypter = new Crypter();

    final String encryptedPassword = crypter.encrypt(plainPassword);
    final String decryptedPassword = crypter.decrypt(encryptedPassword);

    assertEquals(plainPassword, decryptedPassword);
  }

  @Test
  public void testEncryptDecryptStringWithNewlines() throws Exception {
    final String plainPassword = "line1\nline2\r\nline3\rline4";
    final Crypter crypter = new Crypter();

    final String encryptedPassword = crypter.encrypt(plainPassword);
    final String decryptedPassword = crypter.decrypt(encryptedPassword);

    assertEquals(plainPassword, decryptedPassword);
  }

  @Test
  public void testEncryptDecryptLongString() throws Exception {
    // Test with very long string
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      sb.append("This is a long password with repeated content. ");
    }
    final String plainPassword = sb.toString();
    final Crypter crypter = new Crypter();

    final String encryptedPassword = crypter.encrypt(plainPassword);
    final String decryptedPassword = crypter.decrypt(encryptedPassword);

    assertEquals(plainPassword, decryptedPassword);
  }

  @Test
  public void testEncryptProducesDifferentOutputForSameInput() throws Exception {
    // Due to IV, encrypting same plaintext should produce different ciphertext
    final String plainPassword = "protect";
    final Crypter crypter = new Crypter();

    final String encrypted1 = crypter.encrypt(plainPassword);
    final String encrypted2 = crypter.encrypt(plainPassword);

    // Ciphertext should be different
    if (encrypted1.equals(encrypted2)) {
      fail("Encryption should produce different ciphertext for same plaintext due to IV");
    }

    // But both should decrypt to same plaintext
    assertEquals(plainPassword, crypter.decrypt(encrypted1));
    assertEquals(plainPassword, crypter.decrypt(encrypted2));
  }

  @Test
  public void testCrypterWithCustomKeysetFileName() throws Exception {
    final String customKeysetName = "custom-keyset";
    final Crypter crypter = new Crypter(customKeysetName);
    final String plaintext = "test-custom-keyset";

    final String encrypted = crypter.encrypt(plaintext);
    final String decrypted = crypter.decrypt(encrypted);

    assertEquals(plaintext, decrypted);
  }

  @Test
  public void testMultipleCryptersWithSameCustomKeysetName() throws Exception {
    final String customKeysetName = "shared-keyset";
    final Crypter crypter1 = new Crypter(customKeysetName);
    final Crypter crypter2 = new Crypter(customKeysetName);
    final String plaintext = "shared-test";

    final String encrypted = crypter1.encrypt(plaintext);
    final String decrypted = crypter2.decrypt(encrypted);

    assertEquals(plaintext, decrypted);
  }

  @Test
  public void testByteArrayEncryptionProducesDifferentOutput() throws Exception {
    final byte[] plainBytes = new byte[16];
    new SecureRandom().nextBytes(plainBytes);
    final Crypter crypter = new Crypter();

    final byte[] encrypted1 = crypter.encrypt(plainBytes);
    final byte[] encrypted2 = crypter.encrypt(plainBytes);

    // Ciphertext should be different due to IV
    if (new String(encrypted1).equals(new String(encrypted2))) {
      fail("Byte encryption should produce different ciphertext for same plaintext due to IV");
    }

    // But both should decrypt to same plaintext
    assertArrayEquals(plainBytes, crypter.decrypt(encrypted1));
    assertArrayEquals(plainBytes, crypter.decrypt(encrypted2));
  }
}
