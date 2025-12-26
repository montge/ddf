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
package org.codice.ddf.security.policy.context.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.permission.KeyValuePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.codice.ddf.security.policy.context.attributes.ContextAttributeMapping;
import org.junit.jupiter.api.Test;

class PolicyTest {

  @Test
  void testConstructorAndGetters() {
    Collection<String> authMethods = Arrays.asList("SAML", "BASIC");
    Collection<ContextAttributeMapping> mappings = new ArrayList<>();

    Policy policy = new Policy("/context", authMethods, mappings);

    assertThat(policy.getContextPath(), is("/context"));
    assertThat(policy.getAuthenticationMethods(), containsInAnyOrder("SAML", "BASIC"));
  }

  @Test
  void testGetContextPath() {
    Policy policy = new Policy("/api/v1", Collections.emptyList(), Collections.emptyList());

    assertThat(policy.getContextPath(), is("/api/v1"));
  }

  @Test
  void testGetAuthenticationMethods() {
    Collection<String> authMethods = Arrays.asList("PKI", "GUEST");

    Policy policy = new Policy("/test", authMethods, Collections.emptyList());

    assertThat(policy.getAuthenticationMethods(), containsInAnyOrder("PKI", "GUEST"));
  }

  @Test
  void testGetAllowedAttributePermissions() {
    ContextAttributeMapping mapping1 = mock(ContextAttributeMapping.class);
    ContextAttributeMapping mapping2 = mock(ContextAttributeMapping.class);
    KeyValuePermission perm1 = mock(KeyValuePermission.class);
    KeyValuePermission perm2 = mock(KeyValuePermission.class);
    when(mapping1.getAttributePermission()).thenReturn(perm1);
    when(mapping2.getAttributePermission()).thenReturn(perm2);
    Collection<ContextAttributeMapping> mappings = Arrays.asList(mapping1, mapping2);

    Policy policy = new Policy("/secure", Collections.emptyList(), mappings);

    assertThat(policy.getAllowedAttributePermissions(), is(notNullValue()));
    assertThat(policy.getAllowedAttributePermissions().getPermissionList(), hasSize(2));
  }

  @Test
  void testGetAllowedAttributeNames() {
    ContextAttributeMapping mapping1 = mock(ContextAttributeMapping.class);
    ContextAttributeMapping mapping2 = mock(ContextAttributeMapping.class);
    when(mapping1.getAttributeName()).thenReturn("role");
    when(mapping2.getAttributeName()).thenReturn("email");
    Collection<ContextAttributeMapping> mappings = Arrays.asList(mapping1, mapping2);

    Policy policy = new Policy("/test", Collections.emptyList(), mappings);

    assertThat(policy.getAllowedAttributeNames(), containsInAnyOrder("role", "email"));
  }

  @Test
  void testGetAllowedAttributeNamesWithEmptyMappings() {
    Policy policy = new Policy("/test", Collections.emptyList(), Collections.emptyList());

    assertThat(policy.getAllowedAttributeNames(), is(empty()));
  }

  @Test
  void testGetAllowedAttributeNamesWithNullMappings() {
    Policy policy = new Policy("/test", Collections.emptyList(), null);

    assertThat(policy.getAllowedAttributeNames(), is(empty()));
  }

  @Test
  void testGetAllowedAttributes() {
    ContextAttributeMapping mapping = mock(ContextAttributeMapping.class);
    Collection<ContextAttributeMapping> mappings = Collections.singletonList(mapping);

    Policy policy = new Policy("/test", Collections.emptyList(), mappings);

    assertThat(policy.getAllowedAttributes(), hasSize(1));
  }

  @Test
  void testGetAllowedAttributesWithNullMappings() {
    Policy policy = new Policy("/test", Collections.emptyList(), null);

    assertThat(policy.getAllowedAttributes(), is(empty()));
  }

  @Test
  void testGetAllowedAttributesReturnsCopy() {
    ContextAttributeMapping mapping = mock(ContextAttributeMapping.class);
    Collection<ContextAttributeMapping> mappings = new ArrayList<>();
    mappings.add(mapping);

    Policy policy = new Policy("/test", Collections.emptyList(), mappings);

    Collection<ContextAttributeMapping> result = policy.getAllowedAttributes();
    result.clear();

    // Original should not be affected
    assertThat(policy.getAllowedAttributes(), hasSize(1));
  }

  @Test
  void testToString() {
    Collection<String> authMethods = Arrays.asList("SAML");
    ContextAttributeMapping mapping = mock(ContextAttributeMapping.class);
    when(mapping.toString()).thenReturn("mapping1");
    Collection<ContextAttributeMapping> mappings = Collections.singletonList(mapping);

    Policy policy = new Policy("/context/path", authMethods, mappings);

    String result = policy.toString();
    assertThat(result, containsString("Context Path: /context/path"));
    assertThat(result, containsString("SAML"));
    assertThat(result, containsString("mapping1"));
  }
}
