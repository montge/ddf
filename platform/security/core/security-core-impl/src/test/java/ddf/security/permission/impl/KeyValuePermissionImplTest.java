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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.permission.KeyValueCollectionPermission;
import ddf.security.permission.KeyValuePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KeyValuePermissionImplTest {

  @Test
  public void testConstructorWithKey() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey");

    assertThat(permission.getKey(), is("testKey"));
    assertThat(permission.getValues(), is(notNullValue()));
    assertThat(permission.getValues().isEmpty(), is(true));
  }

  @Test
  public void testConstructorWithKeyAndList() {
    List<String> values = Arrays.asList("value1", "value2", "value3");
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey", values);

    assertThat(permission.getKey(), is("testKey"));
    assertThat(permission.getValues(), hasSize(3));
  }

  @Test
  public void testConstructorWithKeyAndSet() {
    Set<String> values = new HashSet<>(Arrays.asList("value1", "value2"));
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey", values);

    assertThat(permission.getKey(), is("testKey"));
    assertThat(permission.getValues(), hasSize(2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullKey() {
    new KeyValuePermissionImpl(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullKeyAndList() {
    List<String> values = Arrays.asList("value1");
    new KeyValuePermissionImpl(null, values);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullKeyAndSet() {
    Set<String> values = new HashSet<>(Arrays.asList("value1"));
    new KeyValuePermissionImpl(null, values);
  }

  @Test
  public void testConstructorWithNullValues() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey", (Set<String>) null);

    assertThat(permission.getKey(), is("testKey"));
    assertThat(permission.getValues(), is(notNullValue()));
    assertThat(permission.getValues().isEmpty(), is(true));
  }

  @Test
  public void testConstructorWithNullListValues() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey", (List<String>) null);

    assertThat(permission.getKey(), is("testKey"));
    assertThat(permission.getValues(), is(notNullValue()));
    assertThat(permission.getValues().isEmpty(), is(true));
  }

  @Test
  public void testAddValue() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey");
    permission.addValue("value1");
    permission.addValue("value2");

    assertThat(permission.getValues(), hasSize(2));
  }

  @Test
  public void testGetValuesReturnsUnmodifiableSet() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey");
    permission.addValue("value1");

    Set<String> values = permission.getValues();

    try {
      values.add("value2");
      assertThat("Should have thrown UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      assertThat(permission.getValues(), hasSize(1));
    }
  }

  @Test
  public void testImpliesWithSameKeyAndValues() {
    KeyValuePermissionImpl permission1 = new KeyValuePermissionImpl("testKey");
    permission1.addValue("value1");
    permission1.addValue("value2");

    KeyValuePermissionImpl permission2 = new KeyValuePermissionImpl("testKey");
    permission2.addValue("value1");

    assertThat(permission1.implies(permission2), is(true));
  }

  @Test
  public void testImpliesWithDifferentKeys() {
    KeyValuePermissionImpl permission1 = new KeyValuePermissionImpl("key1");
    permission1.addValue("value1");

    KeyValuePermissionImpl permission2 = new KeyValuePermissionImpl("key2");
    permission2.addValue("value1");

    assertThat(permission1.implies(permission2), is(false));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testImpliesWithKeyValueCollectionPermission() {
    KeyValuePermissionImpl permission1 = new KeyValuePermissionImpl("testKey");
    permission1.addValue("value1");
    permission1.addValue("value2");

    KeyValuePermissionImpl nestedPermission = new KeyValuePermissionImpl("testKey");
    nestedPermission.addValue("value1");

    KeyValueCollectionPermission collectionPermission = mock(KeyValueCollectionPermission.class);
    List<KeyValuePermission> permissionList = Collections.singletonList(nestedPermission);
    when(collectionPermission.getKeyValuePermissionList()).thenReturn((List) permissionList);

    assertThat(permission1.implies(collectionPermission), is(true));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testImpliesWithKeyValueCollectionPermissionNoMatch() {
    KeyValuePermissionImpl permission1 = new KeyValuePermissionImpl("key1");
    permission1.addValue("value1");

    KeyValuePermissionImpl nestedPermission = new KeyValuePermissionImpl("key2");
    nestedPermission.addValue("value1");

    KeyValueCollectionPermission collectionPermission = mock(KeyValueCollectionPermission.class);
    List<KeyValuePermission> permissionList = Collections.singletonList(nestedPermission);
    when(collectionPermission.getKeyValuePermissionList()).thenReturn((List) permissionList);

    assertThat(permission1.implies(collectionPermission), is(false));
  }

  @Test
  public void testImpliesWithMatchOneCollectionPermission() {
    KeyValuePermissionImpl permission1 = new KeyValuePermissionImpl("testKey");
    permission1.addValue("value1");

    MatchOneCollectionPermission matchOnePermission = mock(MatchOneCollectionPermission.class);
    when(matchOnePermission.implies(permission1)).thenReturn(true);

    assertThat(permission1.implies(matchOnePermission), is(true));
  }

  @Test
  public void testImpliesWithWildcardPermission() {
    KeyValuePermissionImpl permission1 = new KeyValuePermissionImpl("testKey");
    permission1.addValue("value1");

    WildcardPermission wildcardPermission = new WildcardPermission("testKey:value1");

    assertThat(permission1.implies(wildcardPermission), is(true));
  }

  @Test
  public void testImpliesWithUnsupportedPermission() {
    KeyValuePermissionImpl permission1 = new KeyValuePermissionImpl("testKey");
    permission1.addValue("value1");

    Permission unsupportedPermission = mock(Permission.class);

    assertThat(permission1.implies(unsupportedPermission), is(false));
  }

  @Test
  public void testToString() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey");
    permission.addValue("value1");
    permission.addValue("value2");

    String result = permission.toString();

    assertThat(result, containsString("testKey"));
    assertThat(result, containsString(":"));
  }

  @Test
  public void testToStringWithNoValues() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey");

    String result = permission.toString();

    assertThat(result, containsString("testKey"));
  }

  @Test
  public void testImpliesWithSamePermission() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey");
    permission.addValue("value1");

    assertThat(permission.implies(permission), is(true));
  }

  @Test
  public void testImpliesWithMoreRestrictivePermission() {
    KeyValuePermissionImpl broadPermission = new KeyValuePermissionImpl("testKey");
    broadPermission.addValue("value1");
    broadPermission.addValue("value2");
    broadPermission.addValue("value3");

    KeyValuePermissionImpl restrictivePermission = new KeyValuePermissionImpl("testKey");
    restrictivePermission.addValue("value1");
    restrictivePermission.addValue("value2");

    assertThat(broadPermission.implies(restrictivePermission), is(true));
  }

  @Test
  public void testImpliesWithLessRestrictivePermission() {
    KeyValuePermissionImpl restrictivePermission = new KeyValuePermissionImpl("testKey");
    restrictivePermission.addValue("value1");

    KeyValuePermissionImpl broadPermission = new KeyValuePermissionImpl("testKey");
    broadPermission.addValue("value1");
    broadPermission.addValue("value2");
    broadPermission.addValue("value3");

    assertThat(restrictivePermission.implies(broadPermission), is(false));
  }

  @Test
  public void testImpliesWithEmptyValues() {
    KeyValuePermissionImpl permission1 = new KeyValuePermissionImpl("testKey");
    KeyValuePermissionImpl permission2 = new KeyValuePermissionImpl("testKey");

    assertThat(permission1.implies(permission2), is(false));
  }

  @Test
  public void testAddNullValue() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey");
    permission.addValue(null);

    assertThat(permission.getValues().contains(null), is(true));
  }

  @Test
  public void testAddDuplicateValues() {
    KeyValuePermissionImpl permission = new KeyValuePermissionImpl("testKey");
    permission.addValue("value1");
    permission.addValue("value1");

    assertThat(permission.getValues(), hasSize(1));
  }
}
