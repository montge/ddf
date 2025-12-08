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
package ddf.security.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.Subject;
import ddf.security.audit.SecurityLogger;
import ddf.security.service.SecurityServiceException;
import java.util.Arrays;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Test;

public class SecurityManagerImplTest {

  private static final String REALM_NAME = "MOCKREALM";

  /**
   * Test for failure when a non-token is sent in.
   *
   * @throws SecurityServiceException
   */
  @Test
  public void testBadToken() {
    SecurityManagerImpl manager = new SecurityManagerImpl(mock(SecurityLogger.class));
    assertThrows(SecurityServiceException.class, () -> manager.getSubject(REALM_NAME));
  }

  /**
   * Test to check for failure when no realms are added.
   *
   * @throws SecurityServiceException
   */
  @Test
  public void testAuthTokenNoRealm() {
    AuthenticationToken token = mock(AuthenticationToken.class);
    when(token.getCredentials()).thenReturn("testUser");
    AuthenticationInfo info = mock(AuthenticationInfo.class);
    Realm realm = mock(Realm.class);
    when(realm.getAuthenticationInfo(token)).thenReturn(info);
    SecurityManagerImpl manager = new SecurityManagerImpl(mock(SecurityLogger.class));
    AuthenticationException exception =
        assertThrows(AuthenticationException.class, () -> manager.getSubject(token));
    assertThat(
        exception.getMessage(), containsString("Authentication failed for token submission"));
  }

  /**
   * Creates mock objects and uses those to pass through the system when an authentication token is
   * used.
   *
   * @throws SecurityServiceException
   */
  @Test
  public void testAuthToken() throws SecurityServiceException {
    // mock setup
    SimplePrincipalCollection principals = new SimplePrincipalCollection();
    SecurityToken secToken = new SecurityToken();
    principals.add(secToken, REALM_NAME);

    AuthenticationToken authToken = mock(AuthenticationToken.class);
    when(authToken.getCredentials()).thenReturn("testUser");
    AuthenticationInfo info = mock(AuthenticationInfo.class);
    when(info.getPrincipals()).thenReturn(principals);

    // realm
    Realm realm = mock(Realm.class);
    when(realm.getAuthenticationInfo(authToken)).thenReturn(info);
    when(realm.supports(authToken)).thenReturn(Boolean.TRUE);
    when(realm.getName()).thenReturn(REALM_NAME);

    SecurityManagerImpl manager = new SecurityManagerImpl(mock(SecurityLogger.class));
    manager.setRealms(Arrays.asList(new Realm[] {realm}));
    Subject subject = manager.getSubject(authToken);
    assertNotNull(subject);
  }
}
