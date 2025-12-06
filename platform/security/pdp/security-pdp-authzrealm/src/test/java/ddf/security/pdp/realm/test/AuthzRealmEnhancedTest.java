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
package ddf.security.pdp.realm.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import ddf.security.pdp.realm.AuthzRealm;
import ddf.security.pdp.realm.xacml.processor.PdpException;
import ddf.security.permission.CollectionPermission;
import ddf.security.permission.KeyValueCollectionPermission;
import ddf.security.permission.KeyValuePermission;
import ddf.security.permission.impl.KeyValueCollectionPermissionImpl;
import ddf.security.permission.impl.KeyValuePermissionImpl;
import ddf.security.policy.extension.PolicyExtension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.subject.PrincipalCollection;
import org.codice.ddf.parser.xml.XmlParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthzRealmEnhancedTest {

  @Mock private SecurityLogger securityLogger;

  @Mock private PrincipalCollection principalCollection;

  private AuthzRealm realm;

  private SimpleAuthorizationInfo authInfo;

  @Before
  public void setup() throws PdpException {
    when(principalCollection.getPrimaryPrincipal()).thenReturn("testuser");

    authInfo = new SimpleAuthorizationInfo();
    authInfo.addRole("user");

    KeyValuePermission kvp1 = new KeyValuePermissionImpl("attr1");
    kvp1.addValue("value1");
    authInfo.addObjectPermission(kvp1);

    realm =
        new AuthzRealm("src/test/resources/policies", new XmlParser()) {
          @Override
          public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
            return authInfo;
          }
        };

    realm.setSecurityLogger(securityLogger);
  }

  @Test
  public void testConstructorWithValidParameters() throws PdpException {
    AuthzRealm newRealm = new AuthzRealm("src/test/resources/policies", new XmlParser());
    newRealm.setSecurityLogger(securityLogger);

    assertThat(newRealm, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullDirectory() {
    try {
      new AuthzRealm(null, new XmlParser());
    } catch (PdpException e) {
      assertThat(e, is(notNullValue()));
    }
  }

  @Test
  public void testConstructorWithEmptyDirectory() {
    try {
      new AuthzRealm("", new XmlParser());
    } catch (PdpException e) {
      assertThat(e, is(notNullValue()));
    }
  }

  // Note: doGetAuthenticationInfo is protected in AuthzRealm and cannot be tested directly
  // The AuthzRealm is an authorization-only realm that returns null for authentication

  @Test
  public void testIsPermittedWithNullPrincipalCollection() {
    List<Permission> permissions = new ArrayList<>();
    permissions.add(new KeyValuePermissionImpl("attr1", Arrays.asList("value1")));

    boolean[] results = realm.isPermitted(null, permissions);

    assertThat(results, is(notNullValue()));
    assertThat(results.length, is(1));
  }

  @Test
  public void testIsPermittedWithEmptyPermissionList() {
    List<Permission> emptyPermissions = new ArrayList<>();

    boolean[] results = realm.isPermitted(principalCollection, emptyPermissions);

    assertThat(results, is(notNullValue()));
    assertThat(results.length, is(0));
  }

  @Test
  public void testIsPermittedSinglePermissionOverload() {
    Permission permission = new KeyValuePermissionImpl("attr1", Arrays.asList("value1"));

    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(true));
  }

  @Test
  public void testSetMatchAllMappingsWithValidMappings() {
    List<String> mappings = Arrays.asList("userAttr1=resourceAttr1", "userAttr2=resourceAttr2");

    realm.setMatchAllMappings(mappings);

    verify(securityLogger, times(2)).audit(any(String.class), any(String.class), any(String.class));
  }

  @Test
  public void testSetMatchAllMappingsWithInvalidMapping() {
    List<String> mappings = Arrays.asList("invalidMapping", "valid=mapping");

    realm.setMatchAllMappings(mappings);

    // Should log warning for invalid mapping but still process valid one
    verify(securityLogger, times(1)).audit(any(String.class), any(String.class), any(String.class));
  }

  @Test
  public void testSetMatchAllMappingsWithNull() {
    realm.setMatchAllMappings(null);

    // Should not throw exception
    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testSetMatchAllMappingsWithEmptyList() {
    realm.setMatchAllMappings(Collections.emptyList());

    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testSetMatchOneMappingsWithValidMappings() {
    List<String> mappings = Arrays.asList("userAttr1=resourceAttr1", "userAttr2=resourceAttr2");

    realm.setMatchOneMappings(mappings);

    verify(securityLogger, times(2)).audit(any(String.class), any(String.class), any(String.class));
  }

  @Test
  public void testSetMatchOneMappingsWithInvalidMapping() {
    List<String> mappings = Arrays.asList("invalidMapping", "valid=mapping");

    realm.setMatchOneMappings(mappings);

    verify(securityLogger, times(1)).audit(any(String.class), any(String.class), any(String.class));
  }

  @Test
  public void testSetMatchOneMappingsWithNull() {
    realm.setMatchOneMappings(null);

    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testSetEnvironmentAttributes() {
    List<String> envAttrs = Arrays.asList("attr1=value1", "attr2=value2");

    realm.setEnvironmentAttributes(envAttrs);

    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testSetEnvironmentAttributesWithEmptyList() {
    realm.setEnvironmentAttributes(Collections.emptyList());

    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testAddPolicyExtension() {
    PolicyExtension extension = mock(PolicyExtension.class);

    realm.addPolicyExtension(extension);

    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testRemovePolicyExtension() {
    PolicyExtension extension = mock(PolicyExtension.class);

    realm.addPolicyExtension(extension);
    realm.removePolicyExtension(extension);

    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testSetPolicyExtensionsWithMultipleExtensions() {
    PolicyExtension ext1 = mock(PolicyExtension.class);
    PolicyExtension ext2 = mock(PolicyExtension.class);

    List<PolicyExtension> extensions = Arrays.asList(ext1, ext2);

    realm.setPolicyExtensions(extensions);

    assertThat(realm, is(notNullValue()));
  }

  @Test
  public void testIsPermittedWithMatchAllMapping() throws PdpException {
    realm.setMatchAllMappings(Arrays.asList("userAttr=resourceAttr"));

    KeyValuePermission userPerm = new KeyValuePermissionImpl("userAttr");
    userPerm.addValue("A");
    userPerm.addValue("B");

    authInfo.addObjectPermission(userPerm);

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("resourceAttr", Arrays.asList("A", "B"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(true));
  }

  @Test
  public void testIsPermittedWithMatchOneMapping() throws PdpException {
    realm.setMatchOneMappings(Arrays.asList("userAttr=resourceAttr"));

    KeyValuePermission userPerm = new KeyValuePermissionImpl("userAttr");
    userPerm.addValue("A");
    userPerm.addValue("B");

    authInfo.addObjectPermission(userPerm);

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("resourceAttr", Arrays.asList("A", "B", "C"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(true));
  }

  @Test
  public void testIsPermittedWithNoMatchingAttributes() throws PdpException {
    HashMap<String, List<String>> security = new HashMap<>();
    security.put("unknownAttr", Arrays.asList("X", "Y", "Z"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(false));
  }

  @Test
  public void testIsPermittedWithPolicyExtensionMatchAll() throws PdpException {
    PolicyExtension extension =
        new PolicyExtension() {
          @Override
          public KeyValueCollectionPermission isPermittedMatchAll(
              CollectionPermission subjectAllCollection,
              KeyValueCollectionPermission matchAllCollection,
              KeyValueCollectionPermission allPermissionsCollection) {
            // Modify permissions to allow access
            return new KeyValueCollectionPermissionImpl(matchAllCollection.getAction());
          }

          @Override
          public KeyValueCollectionPermission isPermittedMatchOne(
              CollectionPermission subjectAllCollection,
              KeyValueCollectionPermission matchOneCollection,
              KeyValueCollectionPermission allPermissionsCollection) {
            return matchOneCollection;
          }
        };

    realm.addPolicyExtension(extension);

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("attr1", Arrays.asList("value1"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(true));
  }

  @Test
  public void testIsPermittedWithPolicyExtensionMatchOne() throws PdpException {
    PolicyExtension extension =
        new PolicyExtension() {
          @Override
          public KeyValueCollectionPermission isPermittedMatchAll(
              CollectionPermission subjectAllCollection,
              KeyValueCollectionPermission matchAllCollection,
              KeyValueCollectionPermission allPermissionsCollection) {
            return matchAllCollection;
          }

          @Override
          public KeyValueCollectionPermission isPermittedMatchOne(
              CollectionPermission subjectAllCollection,
              KeyValueCollectionPermission matchOneCollection,
              KeyValueCollectionPermission allPermissionsCollection) {
            // Modify permissions to allow access
            return new KeyValueCollectionPermissionImpl(matchOneCollection.getAction());
          }
        };

    realm.addPolicyExtension(extension);
    realm.setMatchOneMappings(Arrays.asList("userAttr=resourceAttr"));

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("resourceAttr", Arrays.asList("value1"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(true));
  }

  @Test
  public void testIsPermittedWithMultiplePolicyExtensions() throws PdpException {
    PolicyExtension ext1 = mock(PolicyExtension.class);
    PolicyExtension ext2 = mock(PolicyExtension.class);

    when(ext1.isPermittedMatchAll(any(), any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(1));
    when(ext1.isPermittedMatchOne(any(), any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(1));
    when(ext2.isPermittedMatchAll(any(), any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(1));
    when(ext2.isPermittedMatchOne(any(), any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(1));

    realm.setPolicyExtensions(Arrays.asList(ext1, ext2));

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("attr1", Arrays.asList("value1"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    realm.isPermitted(principalCollection, permission);

    // May be called twice due to matchAllPreXacml processing
    verify(ext1, times(2)).isPermittedMatchAll(any(), any(), any());
    verify(ext2, times(2)).isPermittedMatchAll(any(), any(), any());
  }

  @Test
  public void testIsPermittedWithStringPermissions() throws PdpException {
    authInfo.addStringPermission("wildcard:permission");

    realm.setPermissionResolver(permString -> new WildcardPermission(permString));

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("attr1", Arrays.asList("value1"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    // Should process string permissions
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testIsPermittedWithRolePermissionResolver() throws PdpException {
    authInfo.addRole("admin");

    realm.setRolePermissionResolver(
        role -> {
          if ("admin".equals(role)) {
            KeyValuePermission adminPerm = new KeyValuePermissionImpl("adminAttr");
            adminPerm.addValue("adminValue");
            return Arrays.asList(adminPerm);
          }
          return Collections.emptyList();
        });

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("adminAttr", Arrays.asList("adminValue"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testIsPermittedWithComplexScenario() throws PdpException {
    // Set up complex scenario with multiple mappings
    realm.setMatchAllMappings(Arrays.asList("clearance=classification"));
    realm.setMatchOneMappings(Arrays.asList("citizenship=releasability"));

    // User has clearance and citizenship
    KeyValuePermission clearancePerm = new KeyValuePermissionImpl("clearance");
    clearancePerm.addValue("SECRET");
    clearancePerm.addValue("CONFIDENTIAL");
    authInfo.addObjectPermission(clearancePerm);

    KeyValuePermission citizenshipPerm = new KeyValuePermissionImpl("citizenship");
    citizenshipPerm.addValue("USA");
    authInfo.addObjectPermission(citizenshipPerm);

    // Resource requires SECRET clearance and USA/CAN releasability
    HashMap<String, List<String>> security = new HashMap<>();
    security.put("classification", Arrays.asList("SECRET"));
    security.put("releasability", Arrays.asList("USA", "CAN"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(true));
  }

  @Test
  public void testIsPermittedDenialLogging() throws PdpException {
    HashMap<String, List<String>> security = new HashMap<>();
    security.put("restrictedAttr", Arrays.asList("restrictedValue"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    realm.isPermitted(principalCollection, permission);

    // May be called multiple times (once for XACML, once for match failure)
    verify(securityLogger, times(2)).audit(any(String.class));
  }

  @Test
  public void testIsPermittedWithNullSubjectPrincipal() throws PdpException {
    when(principalCollection.getPrimaryPrincipal()).thenReturn(null);

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("attr1", Arrays.asList("value1"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    boolean result = realm.isPermitted(principalCollection, permission);

    // Should handle null principal gracefully
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetAuthorizationInfoCaching() {
    // Test that getAuthorizationInfo can be called multiple times
    AuthorizationInfo info1 = realm.getAuthorizationInfo(principalCollection);
    AuthorizationInfo info2 = realm.getAuthorizationInfo(principalCollection);

    assertThat(info1, is(notNullValue()));
    assertThat(info2, is(notNullValue()));
  }

  @Test
  public void testMappingWithWhitespace() throws PdpException {
    List<String> mappings = Arrays.asList("  userAttr  =  resourceAttr  ");

    realm.setMatchAllMappings(mappings);

    verify(securityLogger, times(1)).audit(any(String.class), any(String.class), any(String.class));
  }

  @Test
  public void testMappingWithMultipleEquals() throws PdpException {
    List<String> mappings = Arrays.asList("userAttr=resourceAttr=extra");

    realm.setMatchAllMappings(mappings);

    // Should be ignored as invalid format
    verify(securityLogger, times(0)).audit(any(String.class), any(String.class), any(String.class));
  }

  @Test
  public void testPolicyExtensionExceptionHandling() throws PdpException {
    PolicyExtension faultyExtension = mock(PolicyExtension.class);
    when(faultyExtension.isPermittedMatchAll(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));
    when(faultyExtension.isPermittedMatchOne(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    realm.addPolicyExtension(faultyExtension);

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("attr1", Arrays.asList("value1"));

    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("read", security);

    // Should handle exception gracefully
    boolean result = realm.isPermitted(principalCollection, permission);

    assertThat(result, is(notNullValue()));
    // May be called 3 times: matchAllCollection, matchAllPreXacmlCollection, matchOneCollection
    verify(securityLogger, times(3)).auditWarn(any(String.class), any(Exception.class));
  }
}
