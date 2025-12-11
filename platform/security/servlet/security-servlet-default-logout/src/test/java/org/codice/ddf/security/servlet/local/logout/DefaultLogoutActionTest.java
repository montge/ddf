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
package org.codice.ddf.security.servlet.local.logout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import ddf.action.Action;
import ddf.action.ActionProvider;
import ddf.security.SecurityConstants;
import ddf.security.Subject;
import ddf.security.SubjectOperations;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link DefaultLogoutAction} class. */
@ExtendWith(MockitoExtension.class)
public class DefaultLogoutActionTest {

  private static final String GUEST_TOKEN_TYPE = "guest";
  private static final String USERPASS_TOKEN_TYPE = "userpass";
  private static final String PKI_TOKEN_TYPE = "pki";
  private static final String SAML_TOKEN_TYPE = "saml";

  @Mock private SubjectOperations subjectOperations;

  @Mock private Subject subject;

  private DefaultLogoutAction logoutAction;

  @BeforeEach
  public void setUp() {
    logoutAction = new DefaultLogoutAction();
    logoutAction.setSubjectOperations(subjectOperations);
  }

  @Test
  public void testImplementsActionProvider() {
    assertThat(logoutAction instanceof ActionProvider, is(true));
  }

  @Test
  public void testGetId() {
    assertThat(logoutAction.getId(), is("security.logout.default"));
  }

  @Test
  public void testGetActionWithNullInput() {
    Action action = logoutAction.getAction(null);
    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNonMapInput() {
    Action action = logoutAction.getAction("not a map");
    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithEmptyMap() {
    Map<String, Object> subjectMap = new HashMap<>();
    Action action = logoutAction.getAction(subjectMap);
    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNonSubjectValue() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, "not a subject");
    Action action = logoutAction.getAction(subjectMap);
    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithGuestTokenType() {
    Map<String, Object> subjectMap = createSubjectMap();
    when(subjectOperations.getType(subject)).thenReturn(GUEST_TOKEN_TYPE);

    Action action = logoutAction.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getId(), is("security.logout.default"));
    assertThat(action.getTitle(), is("Local Logout"));
  }

  @Test
  public void testGetActionWithUserPassTokenType() {
    Map<String, Object> subjectMap = createSubjectMap();
    when(subjectOperations.getType(subject)).thenReturn(USERPASS_TOKEN_TYPE);

    Action action = logoutAction.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getId(), is("security.logout.default"));
  }

  @Test
  public void testGetActionWithPkiTokenType() {
    Map<String, Object> subjectMap = createSubjectMap();
    when(subjectOperations.getType(subject)).thenReturn(PKI_TOKEN_TYPE);

    Action action = logoutAction.getAction(subjectMap);

    assertThat(action, is(notNullValue()));
    assertThat(action.getId(), is("security.logout.default"));
  }

  @Test
  public void testGetActionWithSamlTokenTypeReturnsNull() {
    Map<String, Object> subjectMap = createSubjectMap();
    when(subjectOperations.getType(subject)).thenReturn(SAML_TOKEN_TYPE);

    Action action = logoutAction.getAction(subjectMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNullTokenTypeReturnsNull() {
    Map<String, Object> subjectMap = createSubjectMap();
    when(subjectOperations.getType(subject)).thenReturn(null);

    Action action = logoutAction.getAction(subjectMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithUnknownTokenTypeReturnsNull() {
    Map<String, Object> subjectMap = createSubjectMap();
    when(subjectOperations.getType(subject)).thenReturn("unknown");

    Action action = logoutAction.getAction(subjectMap);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testActionDescriptionContainsLogoutInfo() {
    Map<String, Object> subjectMap = createSubjectMap();
    when(subjectOperations.getType(subject)).thenReturn(GUEST_TOKEN_TYPE);

    Action action = logoutAction.getAction(subjectMap);

    assertThat(action.getDescription(), containsString("Logging out"));
    assertThat(action.getDescription(), containsString("local"));
  }

  @Test
  public void testActionUrlContainsLogoutPath() {
    Map<String, Object> subjectMap = createSubjectMap();
    when(subjectOperations.getType(subject)).thenReturn(GUEST_TOKEN_TYPE);

    Action action = logoutAction.getAction(subjectMap);

    assertThat(action.getUrl(), is(notNullValue()));
    assertThat(action.getUrl().getPath(), containsString("logout"));
  }

  private Map<String, Object> createSubjectMap() {
    Map<String, Object> subjectMap = new HashMap<>();
    subjectMap.put(SecurityConstants.SECURITY_SUBJECT, subject);
    return subjectMap;
  }
}
