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
package org.codice.ddf.security.policy.context;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import ddf.security.permission.CollectionPermission;
import ddf.security.permission.KeyValuePermission;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codice.ddf.security.policy.context.attributes.ContextAttributeMapping;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link ContextPolicy} interface contract. Uses a test implementation to verify the
 * interface behavior and contract expectations.
 */
public class ContextPolicyTest {

  private static final String TEST_CONTEXT_PATH = "/services";
  private static final String NO_AUTH_POLICY_VALUE =
      "org.codice.ddf.security.policy.no_authentication";

  private TestContextPolicy contextPolicy;

  @Before
  public void setUp() {
    contextPolicy = new TestContextPolicy(TEST_CONTEXT_PATH);
  }

  @Test
  public void testNoAuthPolicyConstant() {
    assertThat(ContextPolicy.NO_AUTH_POLICY, is(NO_AUTH_POLICY_VALUE));
  }

  @Test
  public void testGetContextPath() {
    assertThat(contextPolicy.getContextPath(), is(TEST_CONTEXT_PATH));
  }

  @Test
  public void testGetContextPathWithRoot() {
    TestContextPolicy rootPolicy = new TestContextPolicy("/");
    assertThat(rootPolicy.getContextPath(), is("/"));
  }

  @Test
  public void testGetContextPathWithNestedPath() {
    TestContextPolicy nestedPolicy = new TestContextPolicy("/services/catalog/query");
    assertThat(nestedPolicy.getContextPath(), is("/services/catalog/query"));
  }

  @Test
  public void testGetAuthenticationMethodsEmpty() {
    TestContextPolicy emptyPolicy = new TestContextPolicy("/empty");
    emptyPolicy.setAuthenticationMethods(Collections.emptyList());
    assertThat(emptyPolicy.getAuthenticationMethods(), is(empty()));
  }

  @Test
  public void testGetAuthenticationMethodsSingle() {
    contextPolicy.setAuthenticationMethods(Collections.singletonList("SAML"));
    Collection<String> methods = contextPolicy.getAuthenticationMethods();
    assertThat(methods, hasSize(1));
    assertThat(methods, containsInAnyOrder("SAML"));
  }

  @Test
  public void testGetAuthenticationMethodsMultiple() {
    contextPolicy.setAuthenticationMethods(Arrays.asList("SAML", "BASIC", "PKI"));
    Collection<String> methods = contextPolicy.getAuthenticationMethods();
    assertThat(methods, hasSize(3));
    assertThat(methods, containsInAnyOrder("SAML", "BASIC", "PKI"));
  }

  @Test
  public void testGetAllowedAttributePermissions() {
    CollectionPermission permissions = contextPolicy.getAllowedAttributePermissions();
    assertThat(permissions, is(notNullValue()));
  }

  @Test
  public void testGetAllowedAttributeNamesEmpty() {
    assertThat(contextPolicy.getAllowedAttributeNames(), is(empty()));
  }

  @Test
  public void testGetAllowedAttributeNamesWithAttributes() {
    contextPolicy.addAttribute("role", "admin");
    contextPolicy.addAttribute("clearance", "TS");
    Collection<String> names = contextPolicy.getAllowedAttributeNames();
    assertThat(names, hasSize(2));
    assertThat(names, containsInAnyOrder("role", "clearance"));
  }

  @Test
  public void testGetAllowedAttributesEmpty() {
    assertThat(contextPolicy.getAllowedAttributes(), is(empty()));
  }

  @Test
  public void testGetAllowedAttributesWithMappings() {
    contextPolicy.addAttribute("role", "admin");
    Collection<ContextAttributeMapping> attrs = contextPolicy.getAllowedAttributes();
    assertThat(attrs, hasSize(1));
  }

  /** Simple test implementation of ContextPolicy interface. */
  private static class TestContextPolicy implements ContextPolicy {
    private final String contextPath;
    private Collection<String> authMethods = Collections.emptyList();
    private Set<TestContextAttributeMapping> attributes = new HashSet<>();

    public TestContextPolicy(String contextPath) {
      this.contextPath = contextPath;
    }

    public void setAuthenticationMethods(Collection<String> methods) {
      this.authMethods = methods;
    }

    public void addAttribute(String name, String value) {
      attributes.add(new TestContextAttributeMapping(name, value, contextPath));
    }

    @Override
    public String getContextPath() {
      return contextPath;
    }

    @Override
    public Collection<String> getAuthenticationMethods() {
      return authMethods;
    }

    @Override
    public CollectionPermission getAllowedAttributePermissions() {
      return mock(CollectionPermission.class);
    }

    @Override
    public Collection<String> getAllowedAttributeNames() {
      Set<String> names = new HashSet<>();
      for (TestContextAttributeMapping attr : attributes) {
        names.add(attr.getAttributeName());
      }
      return names;
    }

    @Override
    public Collection<ContextAttributeMapping> getAllowedAttributes() {
      return new HashSet<>(attributes);
    }
  }

  /** Simple test implementation of ContextAttributeMapping. */
  private static class TestContextAttributeMapping implements ContextAttributeMapping {
    private final String name;
    private final String value;
    private final String context;

    public TestContextAttributeMapping(String name, String value, String context) {
      this.name = name;
      this.value = value;
      this.context = context;
    }

    @Override
    public String getAttributeName() {
      return name;
    }

    @Override
    public String getAttributeValue() {
      return value;
    }

    @Override
    public KeyValuePermission getAttributePermission() {
      return mock(KeyValuePermission.class);
    }

    @Override
    public String getContext() {
      return context;
    }
  }
}
