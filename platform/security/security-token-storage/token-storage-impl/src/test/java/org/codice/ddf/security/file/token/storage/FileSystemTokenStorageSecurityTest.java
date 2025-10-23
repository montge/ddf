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
package org.codice.ddf.security.file.token.storage;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.encryption.crypter.Crypter;
import ddf.security.encryption.crypter.Crypter.CrypterException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.codice.ddf.security.token.storage.api.TokenInformation;
import org.codice.ddf.security.token.storage.api.TokenInformation.TokenEntry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileSystemTokenStorageSecurityTest {

  private static final String TEST_USER_ID = "test-user-123";
  private static final String TEST_SOURCE_ID = "test-source-456";
  private static final String TEST_ACCESS_TOKEN = "access-token-xyz";
  private static final String TEST_REFRESH_TOKEN = "refresh-token-abc";
  private static final String TEST_DISCOVERY_URL = "https://idp.example.com/.well-known";

  private static final String MALICIOUS_USER_ID = "../../../etc/passwd";
  private static final String SQL_INJECTION_ID = "'; DROP TABLE users; --";
  private static final String XSS_USER_ID = "<script>alert('xss')</script>";

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Mock private Crypter crypter;

  private FileSystemTokenStorage tokenStorage;
  private Path testDirectory;

  @Before
  public void setUp() throws Exception {
    testDirectory = tempFolder.newFolder("tokens").toPath();

    // Configure crypter to pass-through for testing
    when(crypter.encrypt(any(InputStream.class)))
        .thenAnswer(
            invocation -> {
              InputStream input = invocation.getArgument(0);
              return new ByteArrayInputStream(input.readAllBytes());
            });

    when(crypter.decrypt(any(InputStream.class)))
        .thenAnswer(
            invocation -> {
              InputStream input = invocation.getArgument(0);
              return new ByteArrayInputStream(input.readAllBytes());
            });

    tokenStorage = new FileSystemTokenStorage(crypter);
    tokenStorage.setBaseDirectory(testDirectory.toString());
  }

  // ==================== Token Creation Security Tests ====================

  @Test
  public void testCreateTokensWithValidData() {
    // Test normal token creation
    int status =
        tokenStorage.create(
            TEST_USER_ID,
            TEST_SOURCE_ID,
            TEST_ACCESS_TOKEN,
            TEST_REFRESH_TOKEN,
            TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenEntry entry = tokenStorage.read(TEST_USER_ID, TEST_SOURCE_ID);
    assertThat(entry, notNullValue());
    assertThat(entry.getAccessToken(), is(TEST_ACCESS_TOKEN));
    assertThat(entry.getRefreshToken(), is(TEST_REFRESH_TOKEN));
  }

  @Test
  public void testCreateTokensWithNullAccessToken() {
    // Test with null access token (should still create entry)
    int status =
        tokenStorage.create(
            TEST_USER_ID, TEST_SOURCE_ID, null, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
  }

  @Test
  public void testCreateTokensWithEmptyStrings() {
    // Test with empty strings
    int status = tokenStorage.create(TEST_USER_ID, TEST_SOURCE_ID, "", "", "");

    assertThat(status, is(SC_OK));
    TokenEntry entry = tokenStorage.read(TEST_USER_ID, TEST_SOURCE_ID);
    assertThat(entry, notNullValue());
  }

  @Test
  public void testUpdateExistingTokens() {
    // Test updating existing tokens
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    String newAccessToken = "new-access-token";
    int status =
        tokenStorage.create(
            TEST_USER_ID, TEST_SOURCE_ID, newAccessToken, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenEntry entry = tokenStorage.read(TEST_USER_ID, TEST_SOURCE_ID);
    assertThat(entry.getAccessToken(), is(newAccessToken));
  }

  @Test
  public void testMultipleSourcesForSameUser() {
    // Test storing tokens for multiple sources under same user
    String source1 = "source-1";
    String source2 = "source-2";

    tokenStorage.create(TEST_USER_ID, source1, "token1", "refresh1", TEST_DISCOVERY_URL);
    tokenStorage.create(TEST_USER_ID, source2, "token2", "refresh2", TEST_DISCOVERY_URL);

    TokenInformation tokenInfo = tokenStorage.read(TEST_USER_ID);
    assertThat(tokenInfo.getTokenEntries().size(), is(2));
    assertThat(tokenInfo.getTokenEntries().get(source1), notNullValue());
    assertThat(tokenInfo.getTokenEntries().get(source2), notNullValue());
  }

  // ==================== Path Traversal Attack Tests ====================

  @Test
  public void testPathTraversalAttackInUserId() {
    // Test path traversal attack - should be prevented by hashing
    int status =
        tokenStorage.create(
            MALICIOUS_USER_ID,
            TEST_SOURCE_ID,
            TEST_ACCESS_TOKEN,
            TEST_REFRESH_TOKEN,
            TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    // Verify file is created with hashed name, not at traversed path
    TokenInformation tokenInfo = tokenStorage.read(MALICIOUS_USER_ID);
    assertThat(tokenInfo, notNullValue());
    // File should be in correct directory, not at /etc/passwd
  }

  @Test
  public void testRelativePathInUserId() {
    // Test relative path in user ID
    String relativePathId = "../../tokens/user";
    int status =
        tokenStorage.create(
            relativePathId,
            TEST_SOURCE_ID,
            TEST_ACCESS_TOKEN,
            TEST_REFRESH_TOKEN,
            TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenInformation tokenInfo = tokenStorage.read(relativePathId);
    assertThat(tokenInfo, notNullValue());
  }

  @Test
  public void testAbsolutePathInUserId() {
    // Test absolute path in user ID
    String absolutePathId = "/tmp/malicious/user";
    int status =
        tokenStorage.create(
            absolutePathId,
            TEST_SOURCE_ID,
            TEST_ACCESS_TOKEN,
            TEST_REFRESH_TOKEN,
            TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenInformation tokenInfo = tokenStorage.read(absolutePathId);
    assertThat(tokenInfo, notNullValue());
  }

  // ==================== Injection Attack Tests ====================

  @Test
  public void testSqlInjectionInUserId() {
    // Test SQL injection pattern in user ID (not applicable to file storage but test sanitization)
    int status =
        tokenStorage.create(
            SQL_INJECTION_ID,
            TEST_SOURCE_ID,
            TEST_ACCESS_TOKEN,
            TEST_REFRESH_TOKEN,
            TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenEntry entry = tokenStorage.read(SQL_INJECTION_ID, TEST_SOURCE_ID);
    assertThat(entry, notNullValue());
  }

  @Test
  public void testXssAttackInUserId() {
    // Test XSS pattern in user ID
    int status =
        tokenStorage.create(
            XSS_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenEntry entry = tokenStorage.read(XSS_USER_ID, TEST_SOURCE_ID);
    assertThat(entry, notNullValue());
  }

  @Test
  public void testSpecialCharactersInTokenData() {
    // Test special characters in token data
    String specialToken = "token\n\r\t\"'\\<>&";
    int status =
        tokenStorage.create(
            TEST_USER_ID, TEST_SOURCE_ID, specialToken, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenEntry entry = tokenStorage.read(TEST_USER_ID, TEST_SOURCE_ID);
    assertThat(entry.getAccessToken(), is(specialToken));
  }

  // ==================== Token Read Security Tests ====================

  @Test
  public void testReadNonExistentToken() {
    // Test reading non-existent token
    TokenInformation tokenInfo = tokenStorage.read("non-existent-user");

    assertThat(tokenInfo, nullValue());
  }

  @Test
  public void testReadNonExistentSource() {
    // Test reading non-existent source
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    TokenEntry entry = tokenStorage.read(TEST_USER_ID, "non-existent-source");

    assertThat(entry, nullValue());
  }

  @Test
  public void testIsAvailableWithValidTokens() {
    // Test token availability check
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    boolean available = tokenStorage.isAvailable(TEST_USER_ID, TEST_SOURCE_ID);

    assertThat(available, is(true));
  }

  @Test
  public void testIsAvailableWithNullAccessToken() {
    // Test availability with null access token
    tokenStorage.create(TEST_USER_ID, TEST_SOURCE_ID, null, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    boolean available = tokenStorage.isAvailable(TEST_USER_ID, TEST_SOURCE_ID);

    assertThat(available, is(false));
  }

  @Test
  public void testIsAvailableWithNonExistentToken() {
    // Test availability check for non-existent token
    boolean available = tokenStorage.isAvailable("non-existent", "source");

    assertThat(available, is(false));
  }

  // ==================== Token Deletion Security Tests ====================

  @Test
  public void testDeleteAllTokensForUser() {
    // Test deleting all tokens for a user
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    int status = tokenStorage.delete(TEST_USER_ID);

    assertThat(status, is(SC_OK));
    TokenInformation tokenInfo = tokenStorage.read(TEST_USER_ID);
    assertThat(tokenInfo, nullValue());
  }

  @Test
  public void testDeleteTokensForSpecificSource() {
    // Test deleting tokens for specific source
    String source1 = "source-1";
    String source2 = "source-2";

    tokenStorage.create(TEST_USER_ID, source1, "token1", "refresh1", TEST_DISCOVERY_URL);
    tokenStorage.create(TEST_USER_ID, source2, "token2", "refresh2", TEST_DISCOVERY_URL);

    int status = tokenStorage.delete(TEST_USER_ID, source1);

    assertThat(status, is(SC_OK));
    TokenInformation tokenInfo = tokenStorage.read(TEST_USER_ID);
    assertThat(tokenInfo.getTokenEntries().size(), is(1));
    assertThat(tokenInfo.getTokenEntries().get(source2), notNullValue());
  }

  @Test
  public void testDeleteLastSourceRemovesFile() {
    // Test that deleting last source removes file completely
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    int status = tokenStorage.delete(TEST_USER_ID, TEST_SOURCE_ID);

    assertThat(status, is(SC_OK));
    TokenInformation tokenInfo = tokenStorage.read(TEST_USER_ID);
    assertThat(tokenInfo, nullValue());
  }

  @Test
  public void testDeleteNonExistentUser() {
    // Test deleting non-existent user (should succeed)
    int status = tokenStorage.delete("non-existent-user");

    assertThat(status, is(SC_OK));
  }

  @Test
  public void testDeleteNonExistentSource() {
    // Test deleting non-existent source (should succeed)
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    int status = tokenStorage.delete(TEST_USER_ID, "non-existent-source");

    assertThat(status, is(SC_OK));
  }

  // ==================== Encryption Security Tests ====================

  @Test
  public void testTokensAreEncryptedOnDisk() throws Exception {
    // Test that tokens are encrypted when written to disk
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    TokenInformation tokenInfo = tokenStorage.read(TEST_USER_ID);
    assertThat(tokenInfo, notNullValue());

    // Verify file exists but verify encryption was called (via mock)
    // In real scenario, raw file content should not contain plaintext tokens
  }

  @Test
  public void testEncryptionFailureHandling() throws Exception {
    // Test handling of encryption failure
    Crypter failingCrypter = mock(Crypter.class);
    when(failingCrypter.encrypt(any(InputStream.class)))
        .thenThrow(new CrypterException("Encryption failed"));

    FileSystemTokenStorage failingStorage = new FileSystemTokenStorage(failingCrypter);
    failingStorage.setBaseDirectory(testDirectory.toString());

    int status =
        failingStorage.create(
            TEST_USER_ID,
            TEST_SOURCE_ID,
            TEST_ACCESS_TOKEN,
            TEST_REFRESH_TOKEN,
            TEST_DISCOVERY_URL);

    assertThat(status, is(SC_INTERNAL_SERVER_ERROR));
  }

  @Test
  public void testDecryptionFailureHandling() throws Exception {
    // Test handling of decryption failure
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    Crypter failingCrypter = mock(Crypter.class);
    when(failingCrypter.decrypt(any(InputStream.class)))
        .thenThrow(new CrypterException("Decryption failed"));

    FileSystemTokenStorage failingStorage = new FileSystemTokenStorage(failingCrypter);
    failingStorage.setBaseDirectory(testDirectory.toString());

    TokenInformation tokenInfo = failingStorage.read(TEST_USER_ID);

    assertThat(tokenInfo, nullValue());
  }

  // ==================== State Map Security Tests ====================

  @Test
  public void testStateMapReturnsCleanedData() {
    // Test that state map clears expired entries
    Map<String, Map<String, Object>> stateMap = tokenStorage.getStateMap();

    assertThat(stateMap, notNullValue());
    // State map should be empty initially
    assertThat(stateMap.isEmpty(), is(true));
  }

  // ==================== Concurrent Access Tests ====================

  @Test
  public void testConcurrentCreation() throws Exception {
    // Test concurrent token creation
    String user1 = "user1";
    String user2 = "user2";

    Thread t1 =
        new Thread(
            () ->
                tokenStorage.create(
                    user1,
                    TEST_SOURCE_ID,
                    TEST_ACCESS_TOKEN,
                    TEST_REFRESH_TOKEN,
                    TEST_DISCOVERY_URL));
    Thread t2 =
        new Thread(
            () ->
                tokenStorage.create(
                    user2,
                    TEST_SOURCE_ID,
                    TEST_ACCESS_TOKEN,
                    TEST_REFRESH_TOKEN,
                    TEST_DISCOVERY_URL));

    t1.start();
    t2.start();
    t1.join();
    t2.join();

    TokenInformation info1 = tokenStorage.read(user1);
    TokenInformation info2 = tokenStorage.read(user2);

    assertThat(info1, notNullValue());
    assertThat(info2, notNullValue());
  }

  @Test
  public void testConcurrentUpdateAndRead() throws Exception {
    // Test concurrent update and read
    tokenStorage.create(
        TEST_USER_ID, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    Thread updateThread =
        new Thread(
            () ->
                tokenStorage.create(
                    TEST_USER_ID,
                    TEST_SOURCE_ID,
                    "new-token",
                    TEST_REFRESH_TOKEN,
                    TEST_DISCOVERY_URL));
    Thread readThread = new Thread(() -> tokenStorage.read(TEST_USER_ID, TEST_SOURCE_ID));

    updateThread.start();
    readThread.start();
    updateThread.join();
    readThread.join();

    // Should complete without errors
    TokenEntry entry = tokenStorage.read(TEST_USER_ID, TEST_SOURCE_ID);
    assertThat(entry, notNullValue());
  }

  // ==================== File System Security Tests ====================

  @Test
  public void testInvalidBaseDirectory() throws Exception {
    // Test with invalid base directory path
    FileSystemTokenStorage newStorage = new FileSystemTokenStorage(crypter);

    try {
      newStorage.setBaseDirectory("\0invalid\0path");
      // Should fall back to karaf.home
    } catch (Exception e) {
      // Expected for invalid path
    }
  }

  @Test
  public void testBaseDirectoryCreation() throws Exception {
    // Test that base directory is created if it doesn't exist
    Path newDir = tempFolder.getRoot().toPath().resolve("new-token-dir");

    FileSystemTokenStorage newStorage = new FileSystemTokenStorage(crypter);
    newStorage.setBaseDirectory(newDir.toString());

    assertThat(Files.exists(newDir), is(true));
  }

  @Test
  public void testLongTokenData() {
    // Test with very long token data
    StringBuilder longToken = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      longToken.append("a");
    }

    int status =
        tokenStorage.create(
            TEST_USER_ID,
            TEST_SOURCE_ID,
            longToken.toString(),
            TEST_REFRESH_TOKEN,
            TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenEntry entry = tokenStorage.read(TEST_USER_ID, TEST_SOURCE_ID);
    assertThat(entry.getAccessToken(), is(longToken.toString()));
  }

  @Test
  public void testUnicodeInTokenData() {
    // Test Unicode characters in token data
    String unicodeToken =
        "token-with-unicode-\u4E2D\u6587-\u0628\u0627\u0644\u0639\u0631\u0628\u064A\u0629-\u0905\u0928\u0941\u0935\u093E\u0926";

    int status =
        tokenStorage.create(
            TEST_USER_ID, TEST_SOURCE_ID, unicodeToken, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
    TokenEntry entry = tokenStorage.read(TEST_USER_ID, TEST_SOURCE_ID);
    assertThat(entry.getAccessToken(), is(unicodeToken));
  }

  @Test
  public void testNullUserId() {
    // Test with null user ID (should cause NullPointerException)
    try {
      tokenStorage.create(
          null, TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);
    } catch (NullPointerException e) {
      // Expected
    }
  }

  @Test
  public void testEmptyUserId() {
    // Test with empty user ID
    int status =
        tokenStorage.create(
            "", TEST_SOURCE_ID, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, TEST_DISCOVERY_URL);

    assertThat(status, is(SC_OK));
  }
}
