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
package org.codice.ddf.security.idp.client;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.action.Action;
import ddf.security.SecurityConstants;
import ddf.security.SubjectOperations;
import ddf.security.encryption.EncryptionService;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IdpLogoutActionProviderEnhancedTest {

  @Mock private EncryptionService encryptionService;

  @Mock private SubjectOperations subjectOperations;

  @Mock private Subject subject;

  private IdpLogoutActionProvider idpLogoutActionProvider;

  @BeforeEach
  public void setup() {
    System.setProperty("org.codice.ddf.system.rootContext", "/services");

    idpLogoutActionProvider = new IdpLogoutActionProvider();
    idpLogoutActionProvider.setEncryptionService(encryptionService);
    idpLogoutActionProvider.setSubjectOperations(subjectOperations);

    when(encryptionService.encrypt(anyString()))
        .thenAnswer(
            invocation -> {
              String arg = invocation.getArgument(0);
              return "encrypted_" + arg;
            });

    when(subjectOperations.getName(any(Subject.class), anyString(), anyBoolean()))
        .thenReturn("testUser");

    when(subjectOperations.getType(any(Subject.class)))
        .thenReturn("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0");
  }

  @Test
  public void testGetId() {
    String id = idpLogoutActionProvider.getId();

    assertThat(id, equalTo("security.logout.idp"));
  }

  @Test
  public void testGetActionWithValidSubjectMap() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getId(), equalTo("security.logout.idp"));
    assertThat(action.getTitle(), equalTo("Identity Provider Logout"));
    assertThat(action.getDescription(), is(notNullValue()));
    assertThat(action.getUrl(), is(notNullValue()));
  }

  @Test
  public void testGetActionUrlContainsEncryptedNameIdTime() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getUrl().toString(), containsString("EncryptedNameIdTime="));
    assertThat(action.getUrl().toString(), containsString("/saml/logout/request"));
  }

  @Test
  public void testGetActionWithNonMapParameter() {
    String notAMap = "not a map";

    Action action = idpLogoutActionProvider.getAction(notAMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNullParameter() {
    Action action = idpLogoutActionProvider.getAction(null);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithEmptyMap() {
    Map<String, Object> emptyMap = new HashMap<>();

    Action action = idpLogoutActionProvider.getAction(emptyMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNonSubjectValue() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, "not a subject");

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNullSubject() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, null);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNonSamlAuthType() {
    when(subjectOperations.getType(any(Subject.class))).thenReturn("http://some.other.type");

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNullAuthType() {
    when(subjectOperations.getType(any(Subject.class))).thenReturn(null);

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithSamlV1AuthType() {
    when(subjectOperations.getType(any(Subject.class)))
        .thenReturn("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV1.1");

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    // Should still work as it starts with the SAML prefix
    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionWithDifferentUsername() {
    when(subjectOperations.getName(any(Subject.class), anyString(), anyBoolean()))
        .thenReturn("differentUser");

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getUrl(), is(notNullValue()));
  }

  @Test
  public void testGetActionWithEncodingException() throws Exception {
    // Create a mock that throws UnsupportedEncodingException
    when(encryptionService.encrypt(anyString())).thenThrow(new RuntimeException("Encoding failed"));

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    // Should still return an action with URL, even if there's an error
    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testSetEncryptionService() {
    EncryptionService newService = mock(EncryptionService.class);
    idpLogoutActionProvider.setEncryptionService(newService);

    // Verify it's set by using it
    when(newService.encrypt(anyString())).thenReturn("new_encrypted");
    when(subjectOperations.getName(any(Subject.class), anyString(), anyBoolean()))
        .thenReturn("user");

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);
    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testSetSubjectOperations() {
    SubjectOperations newOps = mock(SubjectOperations.class);
    when(newOps.getName(any(Subject.class), anyString(), anyBoolean())).thenReturn("newUser");
    when(newOps.getType(any(Subject.class)))
        .thenReturn("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0");

    idpLogoutActionProvider.setSubjectOperations(newOps);

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);
    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionDescription() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getDescription(), containsString("Logging out of the Identity Provider"));
  }

  @Test
  public void testGetActionTitle() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getTitle(), equalTo("Identity Provider Logout"));
  }

  @Test
  public void testGetActionUrlIsHttps() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getUrl().getProtocol(), equalTo("https"));
  }

  @Test
  public void testGetActionWithMapContainingExtraKeys() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);
    subjectMap.put("extraKey1", "extraValue1");
    subjectMap.put("extraKey2", "extraValue2");

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionMultipleCalls() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action1 = idpLogoutActionProvider.getAction(subjectMap);
    Action action2 = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action1, is(notNullValue()));
    assertThat(action2, is(notNullValue()));
    assertThat(action1.getId(), equalTo(action2.getId()));
    assertThat(action1.getTitle(), equalTo(action2.getTitle()));
  }

  @Test
  public void testGetActionWithEmptyUsername() {
    when(subjectOperations.getName(any(Subject.class), anyString(), anyBoolean())).thenReturn("");

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionWithSpecialCharactersInUsername() {
    when(subjectOperations.getName(any(Subject.class), anyString(), anyBoolean()))
        .thenReturn("user@example.com!#$%");

    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);

    Action action = idpLogoutActionProvider.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getUrl(), is(notNullValue()));
  }
}
