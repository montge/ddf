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
package ddf.security.permission;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.authz.Permission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KeyValuePermissionTest {

  @Test
  public void testGetKey() {
    KeyValuePermission permission = mock(KeyValuePermission.class);
    when(permission.getKey()).thenReturn("testKey");

    assertThat(permission.getKey(), is("testKey"));
  }

  @Test
  public void testGetValues() {
    KeyValuePermission permission = mock(KeyValuePermission.class);
    Set<String> values = new HashSet<>();
    values.add("value1");
    values.add("value2");
    when(permission.getValues()).thenReturn(values);

    assertThat(permission.getValues(), is(notNullValue()));
    assertThat(permission.getValues(), hasSize(2));
  }

  @Test
  public void testAddValue() {
    KeyValuePermission permission = mock(KeyValuePermission.class);
    Set<String> values = new HashSet<>();
    when(permission.getValues()).thenReturn(values);

    permission.addValue("newValue");
  }

  @Test
  public void testGetValuesReturnsEmptySet() {
    KeyValuePermission permission = mock(KeyValuePermission.class);
    when(permission.getValues()).thenReturn(Collections.emptySet());

    assertThat(permission.getValues(), is(notNullValue()));
    assertThat(permission.getValues().isEmpty(), is(true));
  }

  @Test
  public void testImpliesMethod() {
    KeyValuePermission permission1 = mock(KeyValuePermission.class);
    Permission permission2 = mock(Permission.class);

    when(permission1.implies(permission2)).thenReturn(true);

    assertThat(permission1.implies(permission2), is(true));
  }

  @Test
  public void testImpliesMethodReturnsFalse() {
    KeyValuePermission permission1 = mock(KeyValuePermission.class);
    Permission permission2 = mock(Permission.class);

    when(permission1.implies(permission2)).thenReturn(false);

    assertThat(permission1.implies(permission2), is(false));
  }

  @Test
  public void testAddMultipleValues() {
    KeyValuePermission permission = mock(KeyValuePermission.class);

    permission.addValue("value1");
    permission.addValue("value2");
    permission.addValue("value3");
  }

  @Test
  public void testKeyValuePermissionWithNullValues() {
    KeyValuePermission permission = mock(KeyValuePermission.class);
    when(permission.getValues()).thenReturn(null);

    Set<String> values = permission.getValues();
    assertThat(values, is((Set<String>) null));
  }
}
