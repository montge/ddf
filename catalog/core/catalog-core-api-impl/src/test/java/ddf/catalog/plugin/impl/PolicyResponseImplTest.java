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
package ddf.catalog.plugin.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PolicyResponseImplTest {

  @Test
  void testDefaultConstructor() {
    PolicyResponseImpl response = new PolicyResponseImpl();

    assertThat(response.operationPolicy(), is(notNullValue()));
    assertThat(response.operationPolicy().isEmpty(), is(true));
    assertThat(response.itemPolicy(), is(notNullValue()));
    assertThat(response.itemPolicy().isEmpty(), is(true));
  }

  @Test
  void testConstructorWithPolicies() {
    Map<String, Set<String>> operationPolicy = new HashMap<>();
    Set<String> opValues = new HashSet<>();
    opValues.add("read");
    operationPolicy.put("role", opValues);

    Map<String, Set<String>> itemPolicy = new HashMap<>();
    Set<String> itemValues = new HashSet<>();
    itemValues.add("admin");
    itemPolicy.put("access", itemValues);

    PolicyResponseImpl response = new PolicyResponseImpl(operationPolicy, itemPolicy);

    assertThat(response.operationPolicy(), hasEntry("role", opValues));
    assertThat(response.itemPolicy(), hasEntry("access", itemValues));
  }

  @Test
  void testConstructorWithNullOperationPolicy() {
    Map<String, Set<String>> itemPolicy = new HashMap<>();
    Set<String> itemValues = new HashSet<>();
    itemValues.add("value");
    itemPolicy.put("key", itemValues);

    PolicyResponseImpl response = new PolicyResponseImpl(null, itemPolicy);

    assertThat(response.operationPolicy(), is(notNullValue()));
    assertThat(response.operationPolicy().isEmpty(), is(true));
    assertThat(response.itemPolicy(), hasEntry("key", itemValues));
  }

  @Test
  void testConstructorWithNullItemPolicy() {
    Map<String, Set<String>> operationPolicy = new HashMap<>();
    Set<String> opValues = new HashSet<>();
    opValues.add("value");
    operationPolicy.put("key", opValues);

    PolicyResponseImpl response = new PolicyResponseImpl(operationPolicy, null);

    assertThat(response.operationPolicy(), hasEntry("key", opValues));
    assertThat(response.itemPolicy(), is(notNullValue()));
    assertThat(response.itemPolicy().isEmpty(), is(true));
  }

  @Test
  void testConstructorWithBothPoliciesNull() {
    PolicyResponseImpl response = new PolicyResponseImpl(null, null);

    assertThat(response.operationPolicy(), is(notNullValue()));
    assertThat(response.operationPolicy().isEmpty(), is(true));
    assertThat(response.itemPolicy(), is(notNullValue()));
    assertThat(response.itemPolicy().isEmpty(), is(true));
  }

  @Test
  void testOperationPolicyReturnsProvidedMap() {
    Map<String, Set<String>> operationPolicy = new HashMap<>();
    Set<String> values = new HashSet<>();
    values.add("execute");
    values.add("write");
    operationPolicy.put("permissions", values);

    PolicyResponseImpl response = new PolicyResponseImpl(operationPolicy, null);

    assertThat(response.operationPolicy().get("permissions").contains("execute"), is(true));
    assertThat(response.operationPolicy().get("permissions").contains("write"), is(true));
  }

  @Test
  void testItemPolicyReturnsProvidedMap() {
    Map<String, Set<String>> itemPolicy = new HashMap<>();
    Set<String> values = new HashSet<>();
    values.add("classified");
    itemPolicy.put("security", values);

    PolicyResponseImpl response = new PolicyResponseImpl(null, itemPolicy);

    assertThat(response.itemPolicy().get("security").contains("classified"), is(true));
  }
}
