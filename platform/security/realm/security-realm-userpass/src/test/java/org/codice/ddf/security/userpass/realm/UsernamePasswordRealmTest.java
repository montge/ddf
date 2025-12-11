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
package org.codice.ddf.security.userpass.realm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.assertion.Attribute;
import ddf.security.assertion.AttributeStatement;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.claims.ClaimsCollection;
import ddf.security.claims.ClaimsHandler;
import ddf.security.claims.impl.ClaimImpl;
import ddf.security.claims.impl.ClaimsCollectionImpl;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import org.apache.karaf.jaas.boot.principal.RolePrincipal;
import org.apache.karaf.jaas.boot.principal.UserPrincipal;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.codice.ddf.security.handler.AuthenticationTokenFactory;
import org.codice.ddf.security.handler.AuthenticationTokenType;
import org.codice.ddf.security.handler.BaseAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UsernamePasswordRealmTest {
  UsernamePasswordRealm upRealm =
      new UsernamePasswordRealm() {
        protected Subject login(String username, String password, String realmName)
            throws LoginException {
          if (realmName.equals("realm")) {
            HashSet<Principal> principals = new HashSet<>();
            UserPrincipal principal = mock(UserPrincipal.class);
            RolePrincipal rolePrincipal = mock(RolePrincipal.class);
            when(rolePrincipal.getName()).thenReturn("manager");
            when(principal.getName()).thenReturn("admin");
            principals.add(principal);
            principals.add(rolePrincipal);
            return new Subject(true, principals, new HashSet<>(), new HashSet<>());
          }
          throw new LoginException();
        }
      };

  @BeforeEach
  public void setup() {
    List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    claimsHandlers.add(mock(ClaimsHandler.class));
    claimsHandlers.add(mock(ClaimsHandler.class));
    ClaimsCollection claims1 = new ClaimsCollectionImpl();
    ClaimImpl email1 = new ClaimImpl("email");
    email1.addValue("test@example.com");
    claims1.add(email1);
    ClaimsCollection claims2 = new ClaimsCollectionImpl();
    ClaimImpl email2 = new ClaimImpl("email");
    email2.addValue("tester@example.com");
    claims2.add(email2);
    when(claimsHandlers.get(0).retrieveClaims(any())).thenReturn(claims1);
    when(claimsHandlers.get(1).retrieveClaims(any())).thenReturn(claims2);
    upRealm.setClaimsHandlers(claimsHandlers);

    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("realm");
    upRealm.realmList.add(jaasRealm);
  }

  @Test
  public void testSupportsGood() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn("");
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.USERNAME);
    boolean supports = upRealm.supports(authenticationToken);
    assertTrue(supports);
  }

  @Test
  public void testSupportsBad() {
    UsernamePasswordRealm testRealm = new UsernamePasswordRealm();

    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    boolean supports = testRealm.supports(authenticationToken);
    assertFalse(supports);

    authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);
    supports = testRealm.supports(authenticationToken);
    assertFalse(supports);

    authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.USERNAME);
    supports = testRealm.supports(authenticationToken);
    assertFalse(supports);

    authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn(new Object());
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.USERNAME);
    supports = testRealm.supports(authenticationToken);
    assertFalse(supports);

    authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn("");
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.USERNAME);
    supports = testRealm.supports(authenticationToken);
    assertTrue(supports); // Empty string is still a String type, so it's supported
  }

  @Test
  public void testDoGetAuthenticationInfo() {
    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = upRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertNotNull(assertion);
    assertThat(assertion.getPrincipal().getName(), is("admin"));

    AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
    assertNotNull(attributeStatement);
    assertThat(attributeStatement.getAttributes().size(), greaterThan(0));
    Attribute attribute = attributeStatement.getAttributes().get(0);
    assertThat(attribute.getName(), is("email"));
    assertThat(attribute.getValues().size(), is(2));
    assertThat(attribute.getValues(), contains("tester@example.com", "test@example.com"));
  }

  @Test
  public void testDoGetAuthenticationInfoFailedLogin() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          UsernamePasswordRealm failingRealm =
              new UsernamePasswordRealm() {
                protected Subject login(String username, String password, String realmName)
                    throws LoginException {
                  throw new LoginException("Invalid credentials");
                }
              };

          List<ClaimsHandler> claimsHandlers = new ArrayList<>();
          failingRealm.setClaimsHandlers(claimsHandlers);

          JaasRealm jaasRealm = mock(JaasRealm.class);
          when(jaasRealm.getName()).thenReturn("realm");
          failingRealm.realmList.add(jaasRealm);

          AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
          AuthenticationToken authenticationToken =
              authenticationTokenFactory.fromUsernamePassword("user", "wrongpass", "0.0.0.0");

          failingRealm.doGetAuthenticationInfo(authenticationToken);
        });
  }

  @Test
  public void testDoGetAuthenticationInfoRealmFallback() {
    UsernamePasswordRealm multiRealmRealm =
        new UsernamePasswordRealm() {
          protected Subject login(String username, String password, String realmName)
              throws LoginException {
            if (realmName.equals("realm2")) {
              HashSet<Principal> principals = new HashSet<>();
              UserPrincipal principal = mock(UserPrincipal.class);
              when(principal.getName()).thenReturn("testuser");
              principals.add(principal);
              return new Subject(true, principals, new HashSet<>(), new HashSet<>());
            }
            throw new LoginException("Failed");
          }
        };

    List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    multiRealmRealm.setClaimsHandlers(claimsHandlers);

    JaasRealm jaasRealm1 = mock(JaasRealm.class);
    when(jaasRealm1.getName()).thenReturn("realm1");
    multiRealmRealm.realmList.add(jaasRealm1);

    JaasRealm jaasRealm2 = mock(JaasRealm.class);
    when(jaasRealm2.getName()).thenReturn("realm2");
    multiRealmRealm.realmList.add(jaasRealm2);

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("testuser", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo =
        multiRealmRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertNotNull(assertion);
    assertThat(assertion.getPrincipal().getName(), is("testuser"));
  }

  @Test
  public void testSupportsNonBaseToken() {
    AuthenticationToken authenticationToken =
        new AuthenticationToken() {
          @Override
          public Object getPrincipal() {
            return "principal";
          }

          @Override
          public Object getCredentials() {
            return "credentials";
          }
        };
    boolean supports = upRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testSupportsNullToken() {
    boolean supports = upRealm.supports(null);
    assertFalse(supports);
  }

  @Test
  public void testSupportsWrongTokenType() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn("credentials");
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.PKI);

    boolean supports = upRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testSupportsNonStringCredentials() {
    BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
    when(authenticationToken.getCredentials()).thenReturn(new Object());
    when(authenticationToken.getType()).thenReturn(AuthenticationTokenType.USERNAME);

    boolean supports = upRealm.supports(authenticationToken);
    assertFalse(supports);
  }

  @Test
  public void testSecurityAssertionProperties() {
    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = upRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertThat(assertion.getIssuer(), is(equalTo("DDF")));
    assertThat(assertion.getTokenType(), is(equalTo("userpass")));
    assertThat(assertion.getWeight(), is(equalTo(SecurityAssertion.LOCAL_AUTH_WEIGHT)));
    assertNotNull(assertion.getNotBefore());
    assertNotNull(assertion.getNotOnOrAfter());
  }

  @Test
  public void testClaimsHandlersEmpty() {
    UsernamePasswordRealm realmWithoutClaims =
        new UsernamePasswordRealm() {
          protected Subject login(String username, String password, String realmName)
              throws LoginException {
            if (realmName.equals("realm")) {
              HashSet<Principal> principals = new HashSet<>();
              UserPrincipal principal = mock(UserPrincipal.class);
              when(principal.getName()).thenReturn("admin");
              principals.add(principal);
              return new Subject(true, principals, new HashSet<>(), new HashSet<>());
            }
            throw new LoginException();
          }
        };

    realmWithoutClaims.setClaimsHandlers(Collections.emptyList());

    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("realm");
    realmWithoutClaims.realmList.add(jaasRealm);

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo =
        realmWithoutClaims.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertNotNull(assertion);
    assertThat(assertion.getAttributeStatements().get(0).getAttributes(), hasSize(0));
  }

  @Test
  public void testMultipleRolePrincipals() {
    UsernamePasswordRealm multiRoleRealm =
        new UsernamePasswordRealm() {
          protected Subject login(String username, String password, String realmName)
              throws LoginException {
            if (realmName.equals("realm")) {
              HashSet<Principal> principals = new HashSet<>();
              UserPrincipal userPrincipal = mock(UserPrincipal.class);
              when(userPrincipal.getName()).thenReturn("admin");
              principals.add(userPrincipal);

              RolePrincipal role1 = mock(RolePrincipal.class);
              when(role1.getName()).thenReturn("admin");
              principals.add(role1);

              RolePrincipal role2 = mock(RolePrincipal.class);
              when(role2.getName()).thenReturn("manager");
              principals.add(role2);

              RolePrincipal role3 = mock(RolePrincipal.class);
              when(role3.getName()).thenReturn("viewer");
              principals.add(role3);

              return new Subject(true, principals, new HashSet<>(), new HashSet<>());
            }
            throw new LoginException();
          }
        };

    List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    multiRoleRealm.setClaimsHandlers(claimsHandlers);

    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("realm");
    multiRoleRealm.realmList.add(jaasRealm);

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo =
        multiRoleRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertNotNull(assertion);
    assertThat(assertion.getPrincipal().getName(), is("admin"));
  }

  @Test
  public void testDoGetAuthenticationInfoNoUserPrincipal() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          UsernamePasswordRealm noUserRealm =
              new UsernamePasswordRealm() {
                protected Subject login(String username, String password, String realmName)
                    throws LoginException {
                  if (realmName.equals("realm")) {
                    HashSet<Principal> principals = new HashSet<>();
                    RolePrincipal rolePrincipal = mock(RolePrincipal.class);
                    when(rolePrincipal.getName()).thenReturn("admin");
                    principals.add(rolePrincipal);
                    return new Subject(true, principals, new HashSet<>(), new HashSet<>());
                  }
                  throw new LoginException();
                }
              };

          List<ClaimsHandler> claimsHandlers = new ArrayList<>();
          noUserRealm.setClaimsHandlers(claimsHandlers);

          JaasRealm jaasRealm = mock(JaasRealm.class);
          when(jaasRealm.getName()).thenReturn("realm");
          noUserRealm.realmList.add(jaasRealm);

          AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
          AuthenticationToken authenticationToken =
              authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

          noUserRealm.doGetAuthenticationInfo(authenticationToken);
        });
  }

  @Test
  public void testClaimsMerging() {
    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = upRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
    Attribute emailAttribute = attributeStatement.getAttributes().get(0);

    assertThat(emailAttribute.getName(), is("email"));
    assertThat(emailAttribute.getValues(), hasSize(2));
    assertThat(emailAttribute.getValues(), contains("tester@example.com", "test@example.com"));
  }

  @Test
  public void testDoGetAuthenticationInfoInvalidCredentialsFormat() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
          when(authenticationToken.getCredentials()).thenReturn("invalid");

          upRealm.doGetAuthenticationInfo(authenticationToken);
        });
  }

  @Test
  public void testDoGetAuthenticationInfoEmptyCredentials() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
          when(authenticationToken.getCredentials()).thenReturn("");

          upRealm.doGetAuthenticationInfo(authenticationToken);
        });
  }

  @Test
  public void testDoGetAuthenticationInfoMissingColon() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
          when(authenticationToken.getCredentials()).thenReturn("YWRtaW4=");

          upRealm.doGetAuthenticationInfo(authenticationToken);
        });
  }

  @Test
  public void testDoGetAuthenticationInfoMultipleColons() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          BaseAuthenticationToken authenticationToken = mock(BaseAuthenticationToken.class);
          when(authenticationToken.getCredentials()).thenReturn("user:pass:extra");

          upRealm.doGetAuthenticationInfo(authenticationToken);
        });
  }

  @Test
  public void testDoGetAuthenticationInfoAllRealmsFailLogin() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          UsernamePasswordRealm failingRealm =
              new UsernamePasswordRealm() {
                protected Subject login(String username, String password, String realmName)
                    throws LoginException {
                  throw new LoginException("All realms fail");
                }
              };

          List<ClaimsHandler> claimsHandlers = new ArrayList<>();
          failingRealm.setClaimsHandlers(claimsHandlers);

          JaasRealm jaasRealm1 = mock(JaasRealm.class);
          when(jaasRealm1.getName()).thenReturn("realm1");
          failingRealm.realmList.add(jaasRealm1);

          JaasRealm jaasRealm2 = mock(JaasRealm.class);
          when(jaasRealm2.getName()).thenReturn("realm2");
          failingRealm.realmList.add(jaasRealm2);

          JaasRealm jaasRealm3 = mock(JaasRealm.class);
          when(jaasRealm3.getName()).thenReturn("realm3");
          failingRealm.realmList.add(jaasRealm3);

          AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
          AuthenticationToken authenticationToken =
              authenticationTokenFactory.fromUsernamePassword("user", "pass", "0.0.0.0");

          failingRealm.doGetAuthenticationInfo(authenticationToken);
        });
  }

  @Test
  public void testDoGetAuthenticationInfoNoRealms() {
    UsernamePasswordRealm noRealmsRealm = new UsernamePasswordRealm();
    noRealmsRealm.setClaimsHandlers(Collections.emptyList());

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    try {
      noRealmsRealm.doGetAuthenticationInfo(authenticationToken);
    } catch (AuthenticationException e) {
      assertNotNull(e);
      assertTrue(e.getMessage().contains("Login failed"));
    }
  }

  @Test
  public void testAddRealm() {
    UsernamePasswordRealm testRealm = new UsernamePasswordRealm();
    int initialSize = testRealm.realmList.size();

    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("newRealm");
    testRealm.realmList.add(jaasRealm);

    assertThat(testRealm.realmList.size(), is(equalTo(initialSize + 1)));
  }

  @Test
  public void testRemoveRealm() {
    UsernamePasswordRealm testRealm = new UsernamePasswordRealm();
    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("realmToRemove");
    testRealm.realmList.add(jaasRealm);

    int sizeAfterAdd = testRealm.realmList.size();
    testRealm.realmList.remove(jaasRealm);

    assertThat(testRealm.realmList.size(), is(equalTo(sizeAfterAdd - 1)));
  }

  @Test
  public void testSetClaimsHandlers() {
    UsernamePasswordRealm testRealm = new UsernamePasswordRealm();
    List<ClaimsHandler> newHandlers = new ArrayList<>();
    ClaimsHandler handler = mock(ClaimsHandler.class);
    newHandlers.add(handler);

    testRealm.setClaimsHandlers(newHandlers);

    assertNotNull(testRealm);
  }

  @Test
  public void testUserPrincipalExtraction() {
    UsernamePasswordRealm testRealm =
        new UsernamePasswordRealm() {
          protected Subject login(String username, String password, String realmName)
              throws LoginException {
            if (realmName.equals("realm")) {
              HashSet<Principal> principals = new HashSet<>();
              UserPrincipal userPrincipal = mock(UserPrincipal.class);
              when(userPrincipal.getName()).thenReturn("testuser");
              principals.add(userPrincipal);

              RolePrincipal role1 = mock(RolePrincipal.class);
              when(role1.getName()).thenReturn("admin");
              principals.add(role1);

              RolePrincipal role2 = mock(RolePrincipal.class);
              when(role2.getName()).thenReturn("manager");
              principals.add(role2);

              return new Subject(true, principals, new HashSet<>(), new HashSet<>());
            }
            throw new LoginException();
          }
        };

    List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    testRealm.setClaimsHandlers(claimsHandlers);

    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("realm");
    testRealm.realmList.add(jaasRealm);

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("testuser", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = testRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertNotNull(assertion);
    assertThat(assertion.getPrincipal().getName(), is("testuser"));
  }

  @Test
  public void testLoginWithFirstRealmSuccess() {
    UsernamePasswordRealm testRealm =
        new UsernamePasswordRealm() {
          protected Subject login(String username, String password, String realmName)
              throws LoginException {
            if (realmName.equals("realm1")) {
              HashSet<Principal> principals = new HashSet<>();
              UserPrincipal userPrincipal = mock(UserPrincipal.class);
              when(userPrincipal.getName()).thenReturn("firstuser");
              principals.add(userPrincipal);
              return new Subject(true, principals, new HashSet<>(), new HashSet<>());
            }
            throw new LoginException("Should not reach here");
          }
        };

    List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    testRealm.setClaimsHandlers(claimsHandlers);

    JaasRealm jaasRealm1 = mock(JaasRealm.class);
    when(jaasRealm1.getName()).thenReturn("realm1");
    testRealm.realmList.add(jaasRealm1);

    JaasRealm jaasRealm2 = mock(JaasRealm.class);
    when(jaasRealm2.getName()).thenReturn("realm2");
    testRealm.realmList.add(jaasRealm2);

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("firstuser", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = testRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);
    assertThat(assertion.getPrincipal().getName(), is("firstuser"));
  }

  @Test
  public void testClaimsHandlerReturnsNullCollection() {
    UsernamePasswordRealm testRealm =
        new UsernamePasswordRealm() {
          protected Subject login(String username, String password, String realmName)
              throws LoginException {
            if (realmName.equals("realm")) {
              HashSet<Principal> principals = new HashSet<>();
              UserPrincipal userPrincipal = mock(UserPrincipal.class);
              when(userPrincipal.getName()).thenReturn("admin");
              principals.add(userPrincipal);
              return new Subject(true, principals, new HashSet<>(), new HashSet<>());
            }
            throw new LoginException();
          }
        };

    List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    ClaimsHandler nullHandler = mock(ClaimsHandler.class);
    when(nullHandler.retrieveClaims(any())).thenReturn(null);
    claimsHandlers.add(nullHandler);
    testRealm.setClaimsHandlers(claimsHandlers);

    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("realm");
    testRealm.realmList.add(jaasRealm);

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    try {
      testRealm.doGetAuthenticationInfo(authenticationToken);
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  public void testMultipleClaimsHandlersWithDifferentClaims() {
    UsernamePasswordRealm testRealm =
        new UsernamePasswordRealm() {
          protected Subject login(String username, String password, String realmName)
              throws LoginException {
            if (realmName.equals("realm")) {
              HashSet<Principal> principals = new HashSet<>();
              UserPrincipal userPrincipal = mock(UserPrincipal.class);
              when(userPrincipal.getName()).thenReturn("admin");
              principals.add(userPrincipal);
              return new Subject(true, principals, new HashSet<>(), new HashSet<>());
            }
            throw new LoginException();
          }
        };

    List<ClaimsHandler> claimsHandlers = new ArrayList<>();

    ClaimsHandler handler1 = mock(ClaimsHandler.class);
    ClaimsCollection claims1 = new ClaimsCollectionImpl();
    ClaimImpl claim1 = new ClaimImpl("department");
    claim1.addValue("engineering");
    claims1.add(claim1);
    when(handler1.retrieveClaims(any())).thenReturn(claims1);

    ClaimsHandler handler2 = mock(ClaimsHandler.class);
    ClaimsCollection claims2 = new ClaimsCollectionImpl();
    ClaimImpl claim2 = new ClaimImpl("location");
    claim2.addValue("headquarters");
    claims2.add(claim2);
    when(handler2.retrieveClaims(any())).thenReturn(claims2);

    claimsHandlers.add(handler1);
    claimsHandlers.add(handler2);
    testRealm.setClaimsHandlers(claimsHandlers);

    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("realm");
    testRealm.realmList.add(jaasRealm);

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = testRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
    assertThat(attributeStatement.getAttributes(), hasSize(2));
  }

  @Test
  public void testCredentialsPreservedInAuthenticationInfo() {
    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    String originalCredentials = (String) authenticationToken.getCredentials();

    AuthenticationInfo authenticationInfo = upRealm.doGetAuthenticationInfo(authenticationToken);

    assertThat(authenticationInfo.getCredentials(), is(equalTo(originalCredentials)));
  }

  @Test
  public void testSubjectWithOnlyUserPrincipalNoRoles() {
    UsernamePasswordRealm testRealm =
        new UsernamePasswordRealm() {
          protected Subject login(String username, String password, String realmName)
              throws LoginException {
            if (realmName.equals("realm")) {
              HashSet<Principal> principals = new HashSet<>();
              UserPrincipal userPrincipal = mock(UserPrincipal.class);
              when(userPrincipal.getName()).thenReturn("basicuser");
              principals.add(userPrincipal);
              return new Subject(true, principals, new HashSet<>(), new HashSet<>());
            }
            throw new LoginException();
          }
        };

    List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    testRealm.setClaimsHandlers(claimsHandlers);

    JaasRealm jaasRealm = mock(JaasRealm.class);
    when(jaasRealm.getName()).thenReturn("realm");
    testRealm.realmList.add(jaasRealm);

    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("basicuser", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = testRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertNotNull(assertion);
    assertThat(assertion.getPrincipal().getName(), is("basicuser"));
  }

  @Test
  public void testAssertionWeight() {
    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = upRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertThat(assertion.getWeight(), is(equalTo(SecurityAssertion.LOCAL_AUTH_WEIGHT)));
  }

  @Test
  public void testAssertionValidityPeriod() {
    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = upRealm.doGetAuthenticationInfo(authenticationToken);
    SecurityAssertion assertion =
        authenticationInfo.getPrincipals().oneByType(SecurityAssertion.class);

    assertNotNull(assertion.getNotBefore());
    assertNotNull(assertion.getNotOnOrAfter());
    assertTrue(
        "NotOnOrAfter should be after NotBefore",
        assertion.getNotOnOrAfter().after(assertion.getNotBefore()));
  }

  @Test
  public void testPrincipalCollectionRealmName() {
    AuthenticationTokenFactory authenticationTokenFactory = new AuthenticationTokenFactory();
    AuthenticationToken authenticationToken =
        authenticationTokenFactory.fromUsernamePassword("admin", "pass", "0.0.0.0");

    AuthenticationInfo authenticationInfo = upRealm.doGetAuthenticationInfo(authenticationToken);

    assertNotNull(authenticationInfo.getPrincipals());
    assertNotNull(authenticationInfo.getPrincipals().getRealmNames());
    assertThat(authenticationInfo.getPrincipals().getRealmNames().size(), is(greaterThan(0)));
  }
}
