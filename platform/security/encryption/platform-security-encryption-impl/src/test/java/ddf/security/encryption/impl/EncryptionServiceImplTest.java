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

import static ddf.security.encryption.impl.EncryptionServiceImpl.CRYPTER_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import ddf.security.SecurityConstants;
import ddf.security.encryption.crypter.Crypter.CrypterException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionServiceImplTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionServiceImplTest.class);

  private static final TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    TEMPORARY_FOLDER.create();
    String keysetHome = TEMPORARY_FOLDER.newFolder("keysets").getAbsolutePath();
    String associatedDataHome = TEMPORARY_FOLDER.newFolder("etc").getAbsolutePath();
    System.setProperty(SecurityConstants.KEYSET_DIR, keysetHome);
    System.setProperty(
        SecurityConstants.ASSOCIATED_DATA_PATH,
        associatedDataHome.concat("/associatedData.properties"));
  }

  @After
  public void cleanUp() throws Exception {
    TEMPORARY_FOLDER.delete();
    System.clearProperty(SecurityConstants.KEYSET_DIR);
    System.clearProperty(SecurityConstants.ASSOCIATED_DATA_PATH);
  }

  @Test
  public void testBadSetup() throws Exception {
    try (OutputStream badKeysetOutputStream =
        new FileOutputStream(
            System.getProperty(SecurityConstants.KEYSET_DIR) + "/" + CRYPTER_NAME + ".json")) {
      badKeysetOutputStream.write("BadKeyset".getBytes());
    }
    assertThrows(CrypterException.class, () -> new EncryptionServiceImpl());
  }

  @Test
  public void testEncryptDecrypt() throws Exception {
    final String unencryptedPassword = "protect";

    LOGGER.debug("Unencrypted Password: {}", unencryptedPassword);

    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();

    final String encryptedPassword = encryptionService.encrypt(unencryptedPassword);
    LOGGER.debug("Encrypted Password: {}", encryptedPassword);

    final String decryptedPassword = encryptionService.decrypt(encryptedPassword);
    LOGGER.debug("Decrypted Password: {}", decryptedPassword);

    assertEquals(unencryptedPassword, decryptedPassword);
  }

  @Test
  public void testEncryptionServiceInteroperability() throws Exception {
    final EncryptionServiceImpl encryptionService1 = new EncryptionServiceImpl();
    final EncryptionServiceImpl encryptionService2 = new EncryptionServiceImpl();

    final String unencryptedPassword = "protect";

    String encryptedPassword1 = encryptionService1.encrypt(unencryptedPassword);
    String encryptedPassword2 = encryptionService2.encrypt(unencryptedPassword);

    String plainPassword1 = encryptionService1.decrypt(encryptedPassword2);
    String plainPassword2 = encryptionService2.decrypt(encryptedPassword1);

    assertEquals(unencryptedPassword, plainPassword1);
    assertEquals(unencryptedPassword, plainPassword2);
  }

  @Test
  public void testWrappingAndEncrypting() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String wrappedEncryptedValue = encryptionService.encryptValue("test");
    assertThat(wrappedEncryptedValue, startsWith("ENC"));
    assertThat(wrappedEncryptedValue, endsWith(")"));
  }

  @Test
  public void testEncryptValueWithEmptyAndNull() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    String encryptedNull = encryptionService.encryptValue(null);
    assertThat(encryptedNull, is(nullValue()));
    String encryptedEmpty = encryptionService.encryptValue("");
    assertThat(encryptedEmpty, equalTo(""));
  }

  @Test
  public void testUnwrapDecrypt() throws Exception {
    final String expectedDecryptedValue = "test";
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();

    final String wrappedEncryptedValue =
        "ENC(".concat(encryptionService.encrypt(expectedDecryptedValue)).concat(")");

    LOGGER.debug("Original wrapped encrypted value is: {}", wrappedEncryptedValue);

    final String decryptedValue = encryptionService.decryptValue(wrappedEncryptedValue);
    LOGGER.debug("Unwrapped decrypted value is: {}", decryptedValue);

    assertEquals(expectedDecryptedValue, decryptedValue);
  }

  @Test
  public void testUnwrapDecryptNull() throws Exception {
    final String wrappedEncryptedValue = null;

    LOGGER.debug("Original wrapped encrypted value is: null");

    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();

    final String decryptedValue = encryptionService.decryptValue(wrappedEncryptedValue);

    assertNull(decryptedValue);
  }

  @Test
  public void testUnwrapDecryptEmpty() throws Exception {
    final String wrappedEncryptedValue = "";

    LOGGER.debug("Original wrapped encrypted value is: <blank>");

    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();

    final String decryptedValue = encryptionService.decryptValue(wrappedEncryptedValue);

    assertEquals(decryptedValue, "");
  }

  @Test
  public void testUnwrapDecryptPlainText() throws Exception {
    final String wrappedEncryptedValue = "plaintext";

    LOGGER.debug("Original value is: {}", wrappedEncryptedValue);

    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();

    final String decryptedValue = encryptionService.decryptValue(wrappedEncryptedValue);
    LOGGER.debug("Unwrapped decrypted value is: {}", decryptedValue);

    assertEquals(wrappedEncryptedValue, decryptedValue);
  }

  @Test
  public void testUnwrapWithMalformedEncFormat() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();

    // Missing closing parenthesis
    String malformed1 = "ENC(missingClosing";
    assertEquals(malformed1, encryptionService.unwrapEncryptedValue(malformed1));

    // Missing opening
    String malformed2 = "missingOpening)";
    assertEquals(malformed2, encryptionService.unwrapEncryptedValue(malformed2));

    // Wrong order
    String malformed3 = "ENC)wrongOrder(";
    assertEquals(malformed3, encryptionService.unwrapEncryptedValue(malformed3));

    // Only ENC
    String malformed4 = "ENC";
    assertEquals(malformed4, encryptionService.unwrapEncryptedValue(malformed4));

    // Only parentheses
    String malformed5 = "()";
    assertEquals(malformed5, encryptionService.unwrapEncryptedValue(malformed5));
  }

  @Test
  public void testEncryptDecryptWithWhitespace() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String whitespacePassword = "   spaces   ";

    String encrypted = encryptionService.encrypt(whitespacePassword);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(whitespacePassword, decrypted);
  }

  @Test
  public void testEncryptDecryptWithTabsAndNewlines() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String complexWhitespace = "line1\t\tline2\nline3\r\nline4";

    String encrypted = encryptionService.encrypt(complexWhitespace);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(complexWhitespace, decrypted);
  }

  @Test
  public void testEncryptValueDecryptValueWithLongPassword() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final StringBuilder longPassword = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longPassword.append("LongPasswordPart").append(i);
    }

    String wrappedEncrypted = encryptionService.encryptValue(longPassword.toString());
    String decrypted = encryptionService.decryptValue(wrappedEncrypted);

    assertEquals(longPassword.toString(), decrypted);
  }

  @Test
  public void testMultipleConsecutiveEncryptions() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String password = "testPassword";

    // Encrypt multiple times - each should produce different ciphertext
    String encrypted1 = encryptionService.encrypt(password);
    String encrypted2 = encryptionService.encrypt(password);
    String encrypted3 = encryptionService.encrypt(password);

    // All should decrypt correctly
    assertEquals(password, encryptionService.decrypt(encrypted1));
    assertEquals(password, encryptionService.decrypt(encrypted2));
    assertEquals(password, encryptionService.decrypt(encrypted3));

    // Ciphertexts should be different (due to IV)
    assertThat(encrypted1, is(not(equalTo(encrypted2))));
    assertThat(encrypted2, is(not(equalTo(encrypted3))));
    assertThat(encrypted1, is(not(equalTo(encrypted3))));
  }

  @Test
  public void testUnwrapEmptyEncValue() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String emptyWrapped = "ENC()";

    String unwrapped = encryptionService.unwrapEncryptedValue(emptyWrapped);

    assertEquals("", unwrapped);
  }

  @Test
  public void testDecryptValueWithEmptyWrappedContent() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String emptyWrapped = "ENC()";

    String decrypted = encryptionService.decryptValue(emptyWrapped);

    // Empty wrapped content should return empty string
    assertEquals("", decrypted);
  }

  @Test
  public void testEncryptDecryptNumericString() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String numericPassword = "1234567890";

    String encrypted = encryptionService.encrypt(numericPassword);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(numericPassword, decrypted);
  }

  @Test
  public void testEncryptDecryptBase64LikeString() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String base64Like = "VGhpcyBpcyBhIGJhc2U2NCBzdHJpbmc=";

    String encrypted = encryptionService.encrypt(base64Like);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(base64Like, decrypted);
  }

  @Test
  public void testUnwrapNestedEncFormat() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    // Test nested ENC format (edge case)
    final String nested = "ENC(ENC(innerValue))";

    String unwrapped = encryptionService.unwrapEncryptedValue(nested);

    // Should only unwrap outer layer
    assertEquals("ENC(innerValue)", unwrapped);
  }

  @Test
  public void testEncryptDecryptSingleCharacter() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String singleChar = "x";

    String encrypted = encryptionService.encrypt(singleChar);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(singleChar, decrypted);
  }

  @Test
  public void testEncryptDecryptAllPrintableAscii() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final StringBuilder allPrintable = new StringBuilder();
    for (int i = 32; i < 127; i++) {
      allPrintable.append((char) i);
    }

    String encrypted = encryptionService.encrypt(allPrintable.toString());
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(allPrintable.toString(), decrypted);
  }

  @Test
  public void testDecryptValueWithSpacesAroundWrapper() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String password = "test";
    final String encrypted = encryptionService.encrypt(password);

    // These should NOT be recognized as wrapped (space before/after)
    String withLeadingSpace = " ENC(" + encrypted + ")";
    String withTrailingSpace = "ENC(" + encrypted + ") ";
    String withBothSpaces = " ENC(" + encrypted + ") ";

    assertEquals(withLeadingSpace, encryptionService.decryptValue(withLeadingSpace));
    assertEquals(withTrailingSpace, encryptionService.decryptValue(withTrailingSpace));
    assertEquals(withBothSpaces, encryptionService.decryptValue(withBothSpaces));
  }

  @Test
  public void testEncryptValueWrappingFormat() {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String password = "myPassword123";

    String wrapped = encryptionService.encryptValue(password);

    // Verify format
    assertThat(wrapped, startsWith("ENC("));
    assertThat(wrapped, endsWith(")"));
    assertThat(wrapped.length(), is(greaterThan(8))); // At least "ENC(x)"

    // Verify it can be decrypted
    String decrypted = encryptionService.decryptValue(wrapped);
    assertEquals(password, decrypted);
  }

  @Test
  public void testConcurrentEncryptionDecryption() throws Exception {
    final EncryptionServiceImpl encryptionService = new EncryptionServiceImpl();
    final String password = "concurrentTest";
    final int threadCount = 20;

    Thread[] threads = new Thread[threadCount];
    String[] results = new String[threadCount];

    for (int i = 0; i < threadCount; i++) {
      final int index = i;
      threads[i] =
          new Thread(
              () -> {
                String encrypted = encryptionService.encrypt(password);
                results[index] = encryptionService.decrypt(encrypted);
              });
      threads[i].start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    // All threads should have successfully encrypted and decrypted
    for (int i = 0; i < threadCount; i++) {
      assertEquals("Thread " + i + " failed", password, results[i]);
    }
  }
}
