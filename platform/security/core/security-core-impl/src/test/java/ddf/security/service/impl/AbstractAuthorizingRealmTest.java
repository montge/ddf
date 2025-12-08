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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.assertion.Attribute;
import ddf.security.assertion.AttributeStatement;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.assertion.impl.AttributeDefault;
import ddf.security.expansion.Expansion;
import ddf.security.permission.KeyValuePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.ServiceReference;

@RunWith(MockitoJUnitRunner.class)
public class AbstractAuthorizingRealmTest {

  private static final String SAML_ROLE =
      "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role";

  private TestAuthorizingRealm realm;

  @Mock private SecurityAssertion securityAssertion;

  @Mock private AttributeStatement attributeStatement;

  @Mock private ServiceReference<Expansion> expansionServiceRef;

  @Mock private Expansion expansionService;

  @Before
  public void setUp() {
    realm = new TestAuthorizingRealm();
  }

  @Test
  public void testDoGetAuthorizationInfoWithValidAssertion() {
    PrincipalCollection principalCollection = createPrincipalCollectionWithAssertion();

    List<Attribute> attributes = new ArrayList<>();
    Attribute attribute1 = new AttributeDefault();
    attribute1.setName("testAttribute");
    attribute1.setValues(Arrays.asList("value1", "value2"));
    attributes.add(attribute1);

    when(securityAssertion.getAttributeStatements()).thenReturn(Arrays.asList(attributeStatement));
    when(attributeStatement.getAttributes()).thenReturn(attributes);

    AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principalCollection);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getObjectPermissions(), hasSize(1));
    assertThat(authInfo.getRoles(), hasSize(0));
  }

  @Test
  public void testDoGetAuthorizationInfoWithRoles() {
    PrincipalCollection principalCollection = createPrincipalCollectionWithAssertion();

    List<Attribute> attributes = new ArrayList<>();
    Attribute roleAttribute = new AttributeDefault();
    roleAttribute.setName(SAML_ROLE);
    roleAttribute.setValues(Arrays.asList("admin", "user"));
    attributes.add(roleAttribute);

    when(securityAssertion.getAttributeStatements()).thenReturn(Arrays.asList(attributeStatement));
    when(attributeStatement.getAttributes()).thenReturn(attributes);

    AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principalCollection);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getRoles(), hasSize(2));
    assertThat(authInfo.getRoles(), containsInAnyOrder("admin", "user"));
  }

  @Test
  public void testDoGetAuthorizationInfoWithNoAssertion() {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    principalCollection.add("testUser", "testRealm");

    assertThrows(
        AuthorizationException.class, () -> realm.doGetAuthorizationInfo(principalCollection));
  }

  @Test
  public void testDoGetAuthorizationInfoWithMultipleAttributes() {
    PrincipalCollection principalCollection = createPrincipalCollectionWithAssertion();

    List<Attribute> attributes = new ArrayList<>();

    Attribute attribute1 = new AttributeDefault();
    attribute1.setName("attribute1");
    attribute1.setValues(Arrays.asList("value1", "value2"));
    attributes.add(attribute1);

    Attribute attribute2 = new AttributeDefault();
    attribute2.setName("attribute2");
    attribute2.setValues(Arrays.asList("value3"));
    attributes.add(attribute2);

    when(securityAssertion.getAttributeStatements()).thenReturn(Arrays.asList(attributeStatement));
    when(attributeStatement.getAttributes()).thenReturn(attributes);

    AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principalCollection);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getObjectPermissions(), hasSize(2));
  }

  @Test
  public void testDoGetAuthorizationInfoWithMultipleAttributeStatements() {
    PrincipalCollection principalCollection = createPrincipalCollectionWithAssertion();

    AttributeStatement statement1 = mock(AttributeStatement.class);
    AttributeStatement statement2 = mock(AttributeStatement.class);

    List<Attribute> attributes1 = new ArrayList<>();
    Attribute attribute1 = new AttributeDefault();
    attribute1.setName("attr1");
    attribute1.setValues(Arrays.asList("val1"));
    attributes1.add(attribute1);

    List<Attribute> attributes2 = new ArrayList<>();
    Attribute attribute2 = new AttributeDefault();
    attribute2.setName("attr2");
    attribute2.setValues(Arrays.asList("val2"));
    attributes2.add(attribute2);

    when(statement1.getAttributes()).thenReturn(attributes1);
    when(statement2.getAttributes()).thenReturn(attributes2);

    when(securityAssertion.getAttributeStatements())
        .thenReturn(Arrays.asList(statement1, statement2));

    AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principalCollection);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getObjectPermissions(), hasSize(2));
  }

  @Test
  public void testDoGetAuthorizationInfoWithExpansionService() {
    PrincipalCollection principalCollection = createPrincipalCollectionWithAssertion();

    List<Attribute> attributes = new ArrayList<>();
    Attribute attribute = new AttributeDefault();
    attribute.setName("testAttribute");
    attribute.setValues(Arrays.asList("value1"));
    attributes.add(attribute);

    when(securityAssertion.getAttributeStatements()).thenReturn(Arrays.asList(attributeStatement));
    when(attributeStatement.getAttributes()).thenReturn(attributes);

    Set<String> expandedValues = new HashSet<>(Arrays.asList("value1", "expandedValue"));
    when(expansionService.expand(anyString(), anySet())).thenReturn(expandedValues);

    realm.addUserExpansion(expansionServiceRef, expansionService);

    AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principalCollection);

    assertThat(authInfo, is(notNullValue()));
    verify(expansionService).expand(anyString(), anySet());
  }

  @Test
  public void testAddUserExpansion() {
    realm.addUserExpansion(expansionServiceRef, expansionService);

    assertThat(realm.userExpansionServices.size(), is(1));
    assertThat(realm.userExpansionServices.get(expansionServiceRef), is(expansionService));
  }

  @Test
  public void testAddUserExpansionWithNullServiceReference() {
    realm.addUserExpansion(null, expansionService);

    assertThat(realm.userExpansionServices.size(), is(0));
  }

  @Test
  public void testRemoveUserExpansion() {
    realm.addUserExpansion(expansionServiceRef, expansionService);
    assertThat(realm.userExpansionServices.size(), is(1));

    realm.removeUserExpansion(expansionServiceRef);

    assertThat(realm.userExpansionServices.size(), is(0));
  }

  @Test
  public void testRemoveUserExpansionWithNullServiceReference() {
    realm.addUserExpansion(expansionServiceRef, expansionService);
    assertThat(realm.userExpansionServices.size(), is(1));

    realm.removeUserExpansion(null);

    assertThat(realm.userExpansionServices.size(), is(1));
  }

  @Test
  public void testAddMetacardExpansion() {
    realm.addMetacardExpansion(expansionServiceRef, expansionService);

    assertThat(realm.metacardExpansionServices.size(), is(1));
    assertThat(realm.metacardExpansionServices.get(expansionServiceRef), is(expansionService));
  }

  @Test
  public void testAddMetacardExpansionWithNullServiceReference() {
    realm.addMetacardExpansion(null, expansionService);

    assertThat(realm.metacardExpansionServices.size(), is(0));
  }

  @Test
  public void testRemoveMetacardExpansion() {
    realm.addMetacardExpansion(expansionServiceRef, expansionService);
    assertThat(realm.metacardExpansionServices.size(), is(1));

    realm.removeMetacardExpansion(expansionServiceRef);

    assertThat(realm.metacardExpansionServices.size(), is(0));
  }

  @Test
  public void testRemoveMetacardExpansionWithNullServiceReference() {
    realm.addMetacardExpansion(expansionServiceRef, expansionService);
    assertThat(realm.metacardExpansionServices.size(), is(1));

    realm.removeMetacardExpansion(null);

    assertThat(realm.metacardExpansionServices.size(), is(1));
  }

  @Test
  public void testExpandPermissionsWithKeyValuePermission() {
    KeyValuePermission permission = mock(KeyValuePermission.class);
    when(permission.getKey()).thenReturn("testKey");
    when(permission.getValues()).thenReturn(new HashSet<>(Arrays.asList("value1")));

    Set<String> expandedValues = new HashSet<>(Arrays.asList("value1", "expandedValue"));
    when(expansionService.expand(anyString(), anySet())).thenReturn(expandedValues);

    realm.addMetacardExpansion(expansionServiceRef, expansionService);

    List<Permission> permissions = Arrays.asList(permission);
    List<Permission> expandedPermissions = realm.expandPermissions(permissions);

    assertThat(expandedPermissions, is(notNullValue()));
    assertThat(expandedPermissions.size(), is(1));
    verify(expansionService).expand(anyString(), anySet());
  }

  @Test
  public void testExpandPermissionsWithNoExpansionService() {
    KeyValuePermission permission = mock(KeyValuePermission.class);

    List<Permission> permissions = Arrays.asList(permission);
    List<Permission> expandedPermissions = realm.expandPermissions(permissions);

    assertThat(expandedPermissions, is(permissions));
  }

  @Test
  public void testConstructorDisablesAuthorizationCaching() {
    assertThat(realm.isAuthorizationCachingEnabled(), is(false));
  }

  @Test
  public void testDoGetAuthorizationInfoMergesAttributesWithSameName() {
    PrincipalCollection principalCollection = createPrincipalCollectionWithAssertion();

    List<Attribute> attributes = new ArrayList<>();

    Attribute attribute1 = new AttributeDefault();
    attribute1.setName("testAttribute");
    attribute1.setValues(Arrays.asList("value1"));
    attributes.add(attribute1);

    Attribute attribute2 = new AttributeDefault();
    attribute2.setName("testAttribute");
    attribute2.setValues(Arrays.asList("value2"));
    attributes.add(attribute2);

    when(securityAssertion.getAttributeStatements()).thenReturn(Arrays.asList(attributeStatement));
    when(attributeStatement.getAttributes()).thenReturn(attributes);

    AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principalCollection);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getObjectPermissions(), hasSize(1));

    KeyValuePermission permission =
        (KeyValuePermission) authInfo.getObjectPermissions().iterator().next();
    assertThat(permission.getValues(), hasSize(2));
    assertThat(permission.getValues(), containsInAnyOrder("value1", "value2"));
  }

  @Test
  public void testDoGetAuthorizationInfoWithEmptyAttributes() {
    PrincipalCollection principalCollection = createPrincipalCollectionWithAssertion();

    when(securityAssertion.getAttributeStatements()).thenReturn(Arrays.asList(attributeStatement));
    when(attributeStatement.getAttributes()).thenReturn(new ArrayList<>());

    AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principalCollection);

    assertThat(authInfo, is(notNullValue()));
    assertThat(authInfo.getObjectPermissions(), hasSize(0));
    assertThat(authInfo.getRoles(), hasSize(0));
  }

  private PrincipalCollection createPrincipalCollectionWithAssertion() {
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
    principalCollection.add("testUser", "testRealm");
    principalCollection.add(securityAssertion, "testRealm");
    return principalCollection;
  }

  /**
   * Concrete implementation of AbstractAuthorizingRealm for testing purposes. The abstract class
   * requires implementation of doGetAuthenticationInfo.
   */
  private static class TestAuthorizingRealm extends AbstractAuthorizingRealm {
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
        throws AuthenticationException {
      return null;
    }
  }
}
