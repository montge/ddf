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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link ContextPolicyManager} interface contract. Uses a test implementation to verify
 * the interface behavior and contract expectations.
 */
public class ContextPolicyManagerTest {

  private TestContextPolicyManager policyManager;

  @Before
  public void setUp() {
    policyManager = new TestContextPolicyManager();
  }

  @Test
  public void testGetContextPolicyReturnsNullForUnknownPath() {
    ContextPolicy policy = policyManager.getContextPolicy("/unknown");
    assertThat(policy, is(nullValue()));
  }

  @Test
  public void testGetContextPolicyReturnsConfiguredPolicy() {
    ContextPolicy mockPolicy = mock(ContextPolicy.class);
    when(mockPolicy.getContextPath()).thenReturn("/services");
    policyManager.setContextPolicy("/services", mockPolicy);

    ContextPolicy result = policyManager.getContextPolicy("/services");
    assertThat(result, is(notNullValue()));
    assertThat(result.getContextPath(), is("/services"));
  }

  @Test
  public void testGetAllContextPoliciesEmpty() {
    Collection<ContextPolicy> policies = policyManager.getAllContextPolicies();
    assertThat(policies, is(empty()));
  }

  @Test
  public void testGetAllContextPoliciesWithMultiplePolicies() {
    ContextPolicy policy1 = mock(ContextPolicy.class);
    ContextPolicy policy2 = mock(ContextPolicy.class);
    policyManager.setContextPolicy("/services", policy1);
    policyManager.setContextPolicy("/admin", policy2);

    Collection<ContextPolicy> policies = policyManager.getAllContextPolicies();
    assertThat(policies, hasSize(2));
  }

  @Test
  public void testSetContextPolicyOverwritesExisting() {
    ContextPolicy originalPolicy = mock(ContextPolicy.class);
    when(originalPolicy.getContextPath()).thenReturn("/services");

    ContextPolicy newPolicy = mock(ContextPolicy.class);
    when(newPolicy.getContextPath()).thenReturn("/services");

    policyManager.setContextPolicy("/services", originalPolicy);
    policyManager.setContextPolicy("/services", newPolicy);

    Collection<ContextPolicy> policies = policyManager.getAllContextPolicies();
    assertThat(policies, hasSize(1));
  }

  @Test
  public void testIsWhiteListedReturnsFalseByDefault() {
    assertThat(policyManager.isWhiteListed("/services"), is(false));
  }

  @Test
  public void testIsWhiteListedReturnsTrueForWhitelistedPath() {
    policyManager.addToWhitelist("/public");
    assertThat(policyManager.isWhiteListed("/public"), is(true));
  }

  @Test
  public void testIsWhiteListedReturnsFalseForNonWhitelistedPath() {
    policyManager.addToWhitelist("/public");
    assertThat(policyManager.isWhiteListed("/private"), is(false));
  }

  @Test
  public void testGetGuestAccessDefaultsToFalse() {
    assertThat(policyManager.getGuestAccess(), is(false));
  }

  @Test
  public void testGetGuestAccessReturnsConfiguredValue() {
    policyManager.setGuestAccess(true);
    assertThat(policyManager.getGuestAccess(), is(true));
  }

  @Test
  public void testGetSessionAccessDefaultsToFalse() {
    assertThat(policyManager.getSessionAccess(), is(false));
  }

  @Test
  public void testGetSessionAccessReturnsConfiguredValue() {
    policyManager.setSessionAccess(true);
    assertThat(policyManager.getSessionAccess(), is(true));
  }

  @Test
  public void testGetContextPolicyWithNullPath() {
    ContextPolicy policy = policyManager.getContextPolicy(null);
    assertThat(policy, is(nullValue()));
  }

  @Test
  public void testGetContextPolicyWithEmptyPath() {
    ContextPolicy policy = policyManager.getContextPolicy("");
    assertThat(policy, is(nullValue()));
  }

  @Test
  public void testGetAllContextPoliciesReturnsUnmodifiableCollection() {
    ContextPolicy policy = mock(ContextPolicy.class);
    policyManager.setContextPolicy("/services", policy);

    Collection<ContextPolicy> policies = policyManager.getAllContextPolicies();
    try {
      policies.add(mock(ContextPolicy.class));
    } catch (UnsupportedOperationException e) {
      // Expected for unmodifiable collection
    }
    // Original should be unchanged
    assertThat(policyManager.getAllContextPolicies(), hasSize(1));
  }

  /** Simple test implementation of ContextPolicyManager interface. */
  private static class TestContextPolicyManager implements ContextPolicyManager {
    private final Map<String, ContextPolicy> policies = new HashMap<>();
    private final java.util.Set<String> whitelist = new java.util.HashSet<>();
    private boolean guestAccess = false;
    private boolean sessionAccess = false;

    public void addToWhitelist(String path) {
      whitelist.add(path);
    }

    public void setGuestAccess(boolean guestAccess) {
      this.guestAccess = guestAccess;
    }

    public void setSessionAccess(boolean sessionAccess) {
      this.sessionAccess = sessionAccess;
    }

    @Override
    public ContextPolicy getContextPolicy(String path) {
      if (path == null || path.isEmpty()) {
        return null;
      }
      return policies.get(path);
    }

    @Override
    public Collection<ContextPolicy> getAllContextPolicies() {
      return Collections.unmodifiableCollection(policies.values());
    }

    @Override
    public void setContextPolicy(String path, ContextPolicy contextPolicy) {
      policies.put(path, contextPolicy);
    }

    @Override
    public boolean isWhiteListed(String path) {
      return whitelist.contains(path);
    }

    @Override
    public boolean getGuestAccess() {
      return guestAccess;
    }

    @Override
    public boolean getSessionAccess() {
      return sessionAccess;
    }
  }
}
