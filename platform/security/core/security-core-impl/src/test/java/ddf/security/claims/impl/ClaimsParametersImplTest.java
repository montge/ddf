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
package ddf.security.claims.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClaimsParametersImplTest {

  private Principal principal;
  private Set<Principal> roles;
  private Map<String, Object> additionalProperties;

  @BeforeEach
  public void setUp() {
    principal = () -> "testUser";
    roles = new HashSet<>();
    roles.add(() -> "admin");
    roles.add(() -> "user");

    additionalProperties = new HashMap<>();
    additionalProperties.put("property1", "value1");
    additionalProperties.put("property2", 42);
  }

  @Test
  public void testGetPrincipal() {
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, additionalProperties);

    assertThat(claimsParameters.getPrincipal(), is(principal));
    assertThat(claimsParameters.getPrincipal().getName(), is("testUser"));
  }

  @Test
  public void testGetRoles() {
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, additionalProperties);

    Set<Principal> result = claimsParameters.getRoles();

    assertThat(result, is(notNullValue()));
    assertThat(result, hasSize(2));
  }

  @Test
  public void testGetRolesReturnsUnmodifiableSet() {
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, additionalProperties);

    Set<Principal> result = claimsParameters.getRoles();

    try {
      result.add(() -> "newRole");
      assertThat("Should have thrown UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      assertThat(claimsParameters.getRoles(), hasSize(2));
    }
  }

  @Test
  public void testGetAdditionalProperties() {
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, additionalProperties);

    Map<String, Object> result = claimsParameters.getAdditionalProperties();

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(2));
    assertThat(result, hasEntry("property1", "value1"));
    assertThat(result, hasEntry("property2", 42));
  }

  @Test
  public void testGetAdditionalPropertiesReturnsUnmodifiableMap() {
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, additionalProperties);

    Map<String, Object> result = claimsParameters.getAdditionalProperties();

    try {
      result.put("newProperty", "newValue");
      assertThat("Should have thrown UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      assertThat(claimsParameters.getAdditionalProperties().size(), is(2));
    }
  }

  @Test
  public void testConstructorWithEmptyRoles() {
    Set<Principal> emptyRoles = new HashSet<>();
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, emptyRoles, additionalProperties);

    assertThat(claimsParameters.getRoles(), is(notNullValue()));
    assertThat(claimsParameters.getRoles(), hasSize(0));
  }

  @Test
  public void testConstructorWithEmptyAdditionalProperties() {
    Map<String, Object> emptyProperties = new HashMap<>();
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, emptyProperties);

    assertThat(claimsParameters.getAdditionalProperties(), is(notNullValue()));
    assertThat(claimsParameters.getAdditionalProperties().size(), is(0));
  }

  @Test
  public void testConstructorMakesDefensiveCopyOfRoles() {
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, additionalProperties);

    roles.add(() -> "newRole");

    assertThat(claimsParameters.getRoles(), hasSize(2));
  }

  @Test
  public void testConstructorMakesDefensiveCopyOfAdditionalProperties() {
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, additionalProperties);

    additionalProperties.put("newProperty", "newValue");

    assertThat(claimsParameters.getAdditionalProperties().size(), is(2));
  }

  @Test
  public void testWithNullPrincipal() {
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(null, roles, additionalProperties);

    assertThat(claimsParameters.getPrincipal(), is((Principal) null));
  }

  @Test
  public void testWithSingleRole() {
    Set<Principal> singleRole = new HashSet<>();
    singleRole.add(() -> "admin");

    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, singleRole, additionalProperties);

    assertThat(claimsParameters.getRoles(), hasSize(1));
  }

  @Test
  public void testWithMultipleRoles() {
    Set<Principal> multipleRoles = new HashSet<>();
    multipleRoles.add(() -> "admin");
    multipleRoles.add(() -> "user");
    multipleRoles.add(() -> "developer");

    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, multipleRoles, additionalProperties);

    assertThat(claimsParameters.getRoles(), hasSize(3));
  }

  @Test
  public void testWithSingleAdditionalProperty() {
    Map<String, Object> singleProperty = new HashMap<>();
    singleProperty.put("key", "value");

    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, singleProperty);

    assertThat(claimsParameters.getAdditionalProperties().size(), is(1));
    assertThat(claimsParameters.getAdditionalProperties(), hasEntry("key", "value"));
  }

  @Test
  public void testWithMultipleAdditionalProperties() {
    Map<String, Object> multipleProperties = new HashMap<>();
    multipleProperties.put("property1", "value1");
    multipleProperties.put("property2", 123);
    multipleProperties.put("property3", true);

    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, multipleProperties);

    assertThat(claimsParameters.getAdditionalProperties().size(), is(3));
    assertThat(claimsParameters.getAdditionalProperties(), hasEntry("property1", "value1"));
    assertThat(claimsParameters.getAdditionalProperties(), hasEntry("property2", 123));
    assertThat(claimsParameters.getAdditionalProperties(), hasEntry("property3", true));
  }

  @Test
  public void testAdditionalPropertiesWithNullValues() {
    Map<String, Object> propertiesWithNull = new HashMap<>();
    propertiesWithNull.put("nullProperty", null);
    propertiesWithNull.put("validProperty", "value");

    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, propertiesWithNull);

    assertThat(claimsParameters.getAdditionalProperties().size(), is(2));
    assertThat(claimsParameters.getAdditionalProperties().containsKey("nullProperty"), is(true));
    assertThat(claimsParameters.getAdditionalProperties().get("nullProperty"), is((Object) null));
  }

  @Test
  public void testAdditionalPropertiesWithComplexObjects() {
    Map<String, Object> complexProperties = new HashMap<>();
    complexProperties.put("string", "value");
    complexProperties.put("integer", 42);
    complexProperties.put("boolean", true);
    complexProperties.put("object", new Object());

    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, roles, complexProperties);

    assertThat(claimsParameters.getAdditionalProperties().size(), is(4));
    assertThat(claimsParameters.getAdditionalProperties(), hasEntry("string", "value"));
    assertThat(claimsParameters.getAdditionalProperties(), hasEntry("integer", 42));
    assertThat(claimsParameters.getAdditionalProperties(), hasEntry("boolean", true));
    assertThat(claimsParameters.getAdditionalProperties().containsKey("object"), is(true));
  }

  @Test
  public void testRolesContainCorrectPrincipals() {
    Principal admin = () -> "admin";
    Principal user = () -> "user";

    Set<Principal> testRoles = new HashSet<>();
    testRoles.add(admin);
    testRoles.add(user);

    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(principal, testRoles, additionalProperties);

    Set<Principal> result = claimsParameters.getRoles();
    assertThat(result.contains(admin), is(true));
    assertThat(result.contains(user), is(true));
  }

  @Test
  public void testGetPrincipalWithComplexName() {
    Principal complexPrincipal = () -> "user@domain.com";
    ClaimsParametersImpl claimsParameters =
        new ClaimsParametersImpl(complexPrincipal, roles, additionalProperties);

    assertThat(claimsParameters.getPrincipal().getName(), is("user@domain.com"));
  }
}
