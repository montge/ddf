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
package org.codice.ddf.platform.email.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import ddf.security.encryption.EncryptionService;
import java.util.Properties;
import java.util.concurrent.Future;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive unit tests for {@link SmtpClientImpl} class.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Session creation with and without authentication
 *   <li>Property configuration (host, port, username, password)
 *   <li>Email sending operations
 *   <li>Error handling and edge cases
 *   <li>Encryption service integration
 *   <li>Security logging integration
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class SmtpClientImplTest {

  private static final String TEST_HOST = "smtp.example.com";
  private static final Integer TEST_PORT = 587;
  private static final String TEST_USERNAME = "testuser";
  private static final String TEST_PASSWORD = "testpassword";
  private static final String ENCRYPTED_PASSWORD = "encrypted_password";

  @Mock private EncryptionService mockEncryptionService;

  @Mock private SecurityLogger mockSecurityLogger;

  private SmtpClientImpl smtpClient;

  @BeforeEach
  public void setUp() {
    when(mockEncryptionService.decryptValue(anyString())).then(returnsFirstArg());
    smtpClient = new SmtpClientImpl(mockEncryptionService);
    smtpClient.setSecurityLogger(mockSecurityLogger);
  }

  @Test
  public void testConstructorWithEncryptionService() {
    EncryptionService encryptionService = mock(EncryptionService.class);
    SmtpClientImpl client = new SmtpClientImpl(encryptionService);

    assertThat(client, is(notNullValue()));
  }

  @Test
  public void testSetHostName() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);

    Session session = smtpClient.createSession();

    assertThat(session, is(notNullValue()));
    Properties props = session.getProperties();
    assertThat(props.getProperty("mail.smtp.host"), is(TEST_HOST));
  }

  @Test
  public void testSetHostNameWithEmptyString() {
    assertThrows(IllegalArgumentException.class, () -> smtpClient.setHostName(""));
  }

  @Test
  public void testSetHostNameWithNull() {
    assertThrows(IllegalArgumentException.class, () -> smtpClient.setHostName(null));
  }

  @Test
  public void testSetPortNumber() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);

    Session session = smtpClient.createSession();

    Properties props = session.getProperties();
    assertThat(props.getProperty("mail.smtp.port"), is(TEST_PORT.toString()));
  }

  @Test
  public void testSetPortNumberWithNull() {
    assertThrows(IllegalArgumentException.class, () -> smtpClient.setPortNumber(null));
  }

  @Test
  public void testSetPortNumberWithZero() {
    assertThrows(IllegalArgumentException.class, () -> smtpClient.setPortNumber(0));
  }

  @Test
  public void testSetPortNumberWithNegative() {
    assertThrows(IllegalArgumentException.class, () -> smtpClient.setPortNumber(-1));
  }

  @Test
  public void testSetPortNumberWithValidValues() {
    smtpClient.setPortNumber(25);
    smtpClient.setPortNumber(465);
    smtpClient.setPortNumber(587);
    smtpClient.setPortNumber(2525);
  }

  @Test
  public void testSetUserName() {
    smtpClient.setUserName(TEST_USERNAME);

    // Should not throw exception
  }

  @Test
  public void testSetUserNameWithNull() {
    smtpClient.setUserName(null);

    // Should not throw exception
  }

  @Test
  public void testSetUserNameWithEmptyString() {
    smtpClient.setUserName("");

    // Should not throw exception
  }

  @Test
  public void testSetPassword() {
    when(mockEncryptionService.decryptValue(ENCRYPTED_PASSWORD)).thenReturn(TEST_PASSWORD);

    smtpClient.setPassword(ENCRYPTED_PASSWORD);

    verify(mockEncryptionService).decryptValue(ENCRYPTED_PASSWORD);
  }

  @Test
  public void testSetPasswordWithNull() {
    smtpClient.setPassword(null);

    verify(mockEncryptionService).decryptValue(null);
  }

  @Test
  public void testSetSecurityLogger() {
    SecurityLogger logger = mock(SecurityLogger.class);
    smtpClient.setSecurityLogger(logger);

    // Should not throw exception
  }

  @Test
  public void testCreateSessionWithNullHostname() {
    smtpClient.setPortNumber(TEST_PORT);
    assertThrows(IllegalArgumentException.class, () -> smtpClient.createSession());
  }

  @Test
  public void testCreateSessionWithoutAuthentication() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);

    Session session = smtpClient.createSession();

    assertThat(session, is(notNullValue()));
    Properties props = session.getProperties();
    assertThat(props.getProperty("mail.smtp.host"), is(TEST_HOST));
    assertThat(props.getProperty("mail.smtp.port"), is(TEST_PORT.toString()));
    assertThat(props.getProperty("mail.smtp.auth"), is("false"));
  }

  @Test
  public void testCreateSessionWithAuthentication() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);
    smtpClient.setUserName(TEST_USERNAME);
    smtpClient.setPassword(TEST_PASSWORD);

    Session session = smtpClient.createSession();

    assertThat(session, is(notNullValue()));
    Properties props = session.getProperties();
    assertThat(props.getProperty("mail.smtp.host"), is(TEST_HOST));
    assertThat(props.getProperty("mail.smtp.port"), is(TEST_PORT.toString()));
    assertThat(props.getProperty("mail.smtp.auth"), is("true"));
    assertThat(props.getProperty("mail.smtp.starttls.enable"), is("true"));
  }

  @Test
  public void testCreateSessionWithBlankUsername() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);
    smtpClient.setUserName("   ");
    smtpClient.setPassword(TEST_PASSWORD);

    Session session = smtpClient.createSession();

    Properties props = session.getProperties();
    // Blank username should result in no authentication
    assertThat(props.getProperty("mail.smtp.auth"), is("false"));
  }

  @Test
  public void testCreateAuthenticator() {
    smtpClient.setUserName(TEST_USERNAME);
    smtpClient.setPassword(TEST_PASSWORD);

    Authenticator authenticator = smtpClient.createAuthenticator();

    assertThat(authenticator, is(notNullValue()));
  }

  @Test
  public void testAuthenticatorReturnsCorrectCredentials() throws Exception {
    when(mockEncryptionService.decryptValue(ENCRYPTED_PASSWORD)).thenReturn(TEST_PASSWORD);

    smtpClient.setUserName(TEST_USERNAME);
    smtpClient.setPassword(ENCRYPTED_PASSWORD);

    Authenticator authenticator = smtpClient.createAuthenticator();
    PasswordAuthentication passwordAuth =
        (PasswordAuthentication)
            authenticator
                .getClass()
                .getDeclaredMethod("getPasswordAuthentication")
                .invoke(authenticator);

    assertThat(passwordAuth.getUserName(), is(TEST_USERNAME));
    assertThat(passwordAuth.getPassword(), is(TEST_PASSWORD));
  }

  @Test
  public void testSendWithNullMessage() {
    assertThrows(IllegalArgumentException.class, () -> smtpClient.send(null));
  }

  @Test
  public void testSendWithValidMessage() throws MessagingException {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);

    Session session = smtpClient.createSession();
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("from@example.com"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("to@example.com"));
    message.setSubject("Test Subject");
    message.setText("Test Body");

    Future<Void> future = smtpClient.send(message);

    assertThat(future, is(notNullValue()));
  }

  @Test
  public void testSendLogsAuditMessage() throws MessagingException {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);

    Session session = smtpClient.createSession();
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("from@example.com"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("to@example.com"));
    message.setSubject("Test Subject");
    message.setText("Test Body");

    smtpClient.send(message);

    verify(mockSecurityLogger).audit(anyString(), (Object) any(), (Object) any());
  }

  @Test
  public void testSendHandlesMessagingExceptionDuringAudit() throws MessagingException {
    Message mockMessage = mock(Message.class);
    when(mockMessage.getAllRecipients()).thenThrow(new MessagingException("Test exception"));

    Future<Void> future = smtpClient.send(mockMessage);

    assertThat(future, is(notNullValue()));
    verify(mockSecurityLogger).auditWarn(anyString(), any(MessagingException.class));
  }

  @Test
  public void testMultipleSessionCreations() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);

    Session session1 = smtpClient.createSession();
    Session session2 = smtpClient.createSession();

    assertThat(session1, is(notNullValue()));
    assertThat(session2, is(notNullValue()));
  }

  @Test
  public void testSessionPropertiesAreImmutable() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);

    Session session = smtpClient.createSession();
    String originalHost = session.getProperties().getProperty("mail.smtp.host");

    // Modify properties after session creation
    smtpClient.setHostName("different.host.com");

    // Original session should still have original host
    assertThat(session.getProperties().getProperty("mail.smtp.host"), is(originalHost));
  }

  @Test
  public void testPasswordEncryptionServiceIntegration() {
    String encryptedValue = "encrypted123";
    String decryptedValue = "decrypted456";
    when(mockEncryptionService.decryptValue(encryptedValue)).thenReturn(decryptedValue);

    smtpClient.setPassword(encryptedValue);

    verify(mockEncryptionService).decryptValue(encryptedValue);
  }

  @Test
  public void testCreateSessionPreservesThreadContextClassLoader() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);

    ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

    smtpClient.createSession();

    ClassLoader afterCallClassLoader = Thread.currentThread().getContextClassLoader();
    assertThat(afterCallClassLoader, is(equalTo(originalClassLoader)));
  }

  @Test
  public void testCreateSessionWithAuthenticationPreservesThreadContextClassLoader() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);
    smtpClient.setUserName(TEST_USERNAME);
    smtpClient.setPassword(TEST_PASSWORD);

    ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

    smtpClient.createSession();

    ClassLoader afterCallClassLoader = Thread.currentThread().getContextClassLoader();
    assertThat(afterCallClassLoader, is(equalTo(originalClassLoader)));
  }

  @Test
  public void testSessionPropertiesWithStandardSmtpPort() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(25);

    Session session = smtpClient.createSession();

    assertThat(session.getProperties().getProperty("mail.smtp.port"), is("25"));
  }

  @Test
  public void testSessionPropertiesWithSecureSmtpPort() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(465);

    Session session = smtpClient.createSession();

    assertThat(session.getProperties().getProperty("mail.smtp.port"), is("465"));
  }

  @Test
  public void testSessionPropertiesWithSubmissionPort() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(587);

    Session session = smtpClient.createSession();

    assertThat(session.getProperties().getProperty("mail.smtp.port"), is("587"));
  }

  @Test
  public void testSessionPropertiesWithAlternativePort() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(2525);

    Session session = smtpClient.createSession();

    assertThat(session.getProperties().getProperty("mail.smtp.port"), is("2525"));
  }

  @Test
  public void testSetUserNameDoesNotRequirePassword() {
    smtpClient.setHostName(TEST_HOST);
    smtpClient.setPortNumber(TEST_PORT);
    smtpClient.setUserName(TEST_USERNAME);
    // Not setting password

    Session session = smtpClient.createSession();

    // Should still enable auth if username is present
    assertThat(session.getProperties().getProperty("mail.smtp.auth"), is("true"));
  }

  @Test
  public void testCreateSessionWithNullEncryptionService() {
    SmtpClientImpl clientWithNullEncryption = new SmtpClientImpl(null);
    clientWithNullEncryption.setHostName(TEST_HOST);
    clientWithNullEncryption.setPortNumber(TEST_PORT);
    clientWithNullEncryption.setSecurityLogger(mockSecurityLogger);

    Session session = clientWithNullEncryption.createSession();

    assertThat(session, is(notNullValue()));
  }
}
