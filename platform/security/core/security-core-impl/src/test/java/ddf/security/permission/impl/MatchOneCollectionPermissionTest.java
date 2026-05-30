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
package ddf.security.permission.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.permission.CollectionPermission;
import ddf.security.permission.KeyValuePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchOneCollectionPermissionTest {

  private MatchOneCollectionPermission matchOnePermission;

  @BeforeEach
  public void setUp() {
    // Empty setup, will create instances in each test
  }

  @Test
  public void testImpliesWithEmptyPermissionList() {
    matchOnePermission = new MatchOneCollectionPermission(Collections.emptyList());

    Permission testPermission = mock(Permission.class);

    assertThat(matchOnePermission.implies(testPermission), is(false));
  }

  @Test
  public void testImpliesWithSingleKeyValuePermission() {
    KeyValuePermission kvp = new KeyValuePermissionImpl("testKey");
    kvp.addValue("value1");
    kvp.addValue("value2");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp));

    KeyValuePermission testPermission = new KeyValuePermissionImpl("testKey");
    testPermission.addValue("value1");

    // "Match one" semantics are exercised through the CollectionPermission code path, which is
    // how MatchOneCollectionPermission is invoked in production (see AuthzRealm). The bare
    // single-KeyValuePermission path is a "match all" catch-all and does not split values.
    CollectionPermission collectionToTest = mock(CollectionPermission.class);
    when(collectionToTest.getPermissionList()).thenReturn(Arrays.asList(testPermission));

    assertThat(matchOnePermission.implies(collectionToTest), is(true));
  }

  @Test
  public void testImpliesWithKeyValuePermissionNoMatch() {
    KeyValuePermission kvp = new KeyValuePermissionImpl("key1");
    kvp.addValue("value1");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp));

    KeyValuePermission testPermission = new KeyValuePermissionImpl("key1");
    testPermission.addValue("value2");

    assertThat(matchOnePermission.implies(testPermission), is(false));
  }

  @Test
  public void testImpliesWithMultipleKeyValuePermissions() {
    KeyValuePermission kvp1 = new KeyValuePermissionImpl("key1");
    kvp1.addValue("value1");
    kvp1.addValue("value2");

    KeyValuePermission kvp2 = new KeyValuePermissionImpl("key2");
    kvp2.addValue("value3");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp1, kvp2));

    KeyValuePermission testPermission1 = new KeyValuePermissionImpl("key1");
    testPermission1.addValue("value1");

    KeyValuePermission testPermission2 = new KeyValuePermissionImpl("key2");
    testPermission2.addValue("value3");

    CollectionPermission collectionToTest = mock(CollectionPermission.class);
    when(collectionToTest.getPermissionList())
        .thenReturn(Arrays.asList(testPermission1, testPermission2));

    assertThat(matchOnePermission.implies(collectionToTest), is(true));
  }

  @Test
  public void testImpliesWithCollectionPermissionPartialMatch() {
    KeyValuePermission kvp1 = new KeyValuePermissionImpl("key1");
    kvp1.addValue("value1");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp1));

    KeyValuePermission testPermission1 = new KeyValuePermissionImpl("key1");
    testPermission1.addValue("value1");

    KeyValuePermission testPermission2 = new KeyValuePermissionImpl("key2");
    testPermission2.addValue("value2");

    CollectionPermission collectionToTest = mock(CollectionPermission.class);
    when(collectionToTest.getPermissionList())
        .thenReturn(Arrays.asList(testPermission1, testPermission2));

    assertThat(matchOnePermission.implies(collectionToTest), is(false));
  }

  @Test
  public void testImpliesWithNonKeyValuePermission() {
    WildcardPermission wildcard1 = new WildcardPermission("test:permission:1");
    WildcardPermission wildcard2 = new WildcardPermission("test:permission:2");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(wildcard1, wildcard2));

    WildcardPermission testPermission = new WildcardPermission("test:permission:1");

    assertThat(matchOnePermission.implies(testPermission), is(true));
  }

  @Test
  public void testImpliesWithNonKeyValuePermissionNoMatch() {
    WildcardPermission wildcard1 = new WildcardPermission("test:permission:1");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(wildcard1));

    WildcardPermission testPermission = new WildcardPermission("test:permission:2");

    assertThat(matchOnePermission.implies(testPermission), is(false));
  }

  @Test
  public void testImpliesWithMixedPermissionTypes() {
    KeyValuePermission kvp = new KeyValuePermissionImpl("key1");
    kvp.addValue("value1");

    WildcardPermission wildcard = new WildcardPermission("test:permission");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp, wildcard));

    KeyValuePermission testPermission = new KeyValuePermissionImpl("key1");
    testPermission.addValue("value1");

    assertThat(matchOnePermission.implies(testPermission), is(true));
  }

  @Test
  public void testImpliesWithCollectionPermissionContainingNonKeyValuePermission() {
    WildcardPermission wildcard1 = new WildcardPermission("test:permission:1");
    WildcardPermission wildcard2 = new WildcardPermission("test:permission:2");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(wildcard1, wildcard2));

    WildcardPermission testPermission1 = new WildcardPermission("test:permission:1");
    WildcardPermission testPermission2 = new WildcardPermission("test:permission:2");

    CollectionPermission collectionToTest = mock(CollectionPermission.class);
    when(collectionToTest.getPermissionList())
        .thenReturn(Arrays.asList(testPermission1, testPermission2));

    assertThat(matchOnePermission.implies(collectionToTest), is(true));
  }

  @Test
  public void testImpliesWithKeyValuePermissionMultipleValues() {
    KeyValuePermission kvp = new KeyValuePermissionImpl("key1");
    kvp.addValue("value1");
    kvp.addValue("value2");
    kvp.addValue("value3");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp));

    KeyValuePermission testPermission = new KeyValuePermissionImpl("key1");
    testPermission.addValue("value2");

    // "Match one" semantics are exercised through the CollectionPermission code path, which is
    // how MatchOneCollectionPermission is invoked in production (see AuthzRealm). The bare
    // single-KeyValuePermission path is a "match all" catch-all and does not split values, so
    // matching one of several values must go through a CollectionPermission.
    CollectionPermission collectionToTest = mock(CollectionPermission.class);
    when(collectionToTest.getPermissionList()).thenReturn(Arrays.asList(testPermission));

    assertThat(matchOnePermission.implies(collectionToTest), is(true));
  }

  @Test
  public void testImpliesWithKeyValuePermissionAllValuesMatch() {
    KeyValuePermission kvp = new KeyValuePermissionImpl("key1");
    kvp.addValue("value1");
    kvp.addValue("value2");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp));

    KeyValuePermission testPermission = new KeyValuePermissionImpl("key1");
    testPermission.addValue("value1");
    testPermission.addValue("value2");

    assertThat(matchOnePermission.implies(testPermission), is(true));
  }

  @Test
  public void testConstructorWithNullAction() {
    KeyValuePermission kvp = new KeyValuePermissionImpl("key1");
    kvp.addValue("value1");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp));

    assertThat(matchOnePermission.getPermissionList().size(), is(1));
  }

  @Test
  public void testImpliesWithComplexCollectionPermission() {
    KeyValuePermission kvp1 = new KeyValuePermissionImpl("key1");
    kvp1.addValue("value1");
    kvp1.addValue("value2");

    KeyValuePermission kvp2 = new KeyValuePermissionImpl("key2");
    kvp2.addValue("value3");
    kvp2.addValue("value4");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp1, kvp2));

    KeyValuePermission testPerm1 = new KeyValuePermissionImpl("key1");
    testPerm1.addValue("value1");

    KeyValuePermission testPerm2 = new KeyValuePermissionImpl("key2");
    testPerm2.addValue("value4");

    CollectionPermission collectionToTest = mock(CollectionPermission.class);
    when(collectionToTest.getPermissionList()).thenReturn(Arrays.asList(testPerm1, testPerm2));

    assertThat(matchOnePermission.implies(collectionToTest), is(true));
  }

  @Test
  public void testImpliesWithCollectionPermissionNoMatch() {
    KeyValuePermission kvp1 = new KeyValuePermissionImpl("key1");
    kvp1.addValue("value1");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp1));

    KeyValuePermission testPerm = new KeyValuePermissionImpl("key1");
    testPerm.addValue("value2");

    CollectionPermission collectionToTest = mock(CollectionPermission.class);
    when(collectionToTest.getPermissionList()).thenReturn(Arrays.asList(testPerm));

    assertThat(matchOnePermission.implies(collectionToTest), is(false));
  }

  @Test
  public void testImpliesWithSinglePermissionInCollection() {
    KeyValuePermission kvp = new KeyValuePermissionImpl("key1");
    kvp.addValue("value1");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp));

    KeyValuePermission testPerm = new KeyValuePermissionImpl("key1");
    testPerm.addValue("value1");

    CollectionPermission collectionToTest = mock(CollectionPermission.class);
    when(collectionToTest.getPermissionList()).thenReturn(Arrays.asList(testPerm));

    assertThat(matchOnePermission.implies(collectionToTest), is(true));
  }

  @Test
  public void testImpliesWithDifferentKeys() {
    KeyValuePermission kvp = new KeyValuePermissionImpl("key1");
    kvp.addValue("value1");

    matchOnePermission = new MatchOneCollectionPermission(Arrays.asList(kvp));

    KeyValuePermission testPermission = new KeyValuePermissionImpl("key2");
    testPermission.addValue("value1");

    assertThat(matchOnePermission.implies(testPermission), is(false));
  }

  @Test
  public void testExtendsCollectionPermissionImpl() {
    matchOnePermission = new MatchOneCollectionPermission(Collections.emptyList());

    // MatchOneCollectionPermission extends CollectionPermissionImpl
    assertThat(
        matchOnePermission.getClass().getSuperclass(), is((Object) CollectionPermissionImpl.class));
  }

  @Test
  public void testGetPermissionListReturnsCorrectPermissions() {
    KeyValuePermission kvp1 = new KeyValuePermissionImpl("key1");
    kvp1.addValue("value1");

    KeyValuePermission kvp2 = new KeyValuePermissionImpl("key2");
    kvp2.addValue("value2");

    List<Permission> permissions = Arrays.asList(kvp1, kvp2);
    matchOnePermission = new MatchOneCollectionPermission(permissions);

    assertThat(matchOnePermission.getPermissionList().size(), is(2));
  }
}
