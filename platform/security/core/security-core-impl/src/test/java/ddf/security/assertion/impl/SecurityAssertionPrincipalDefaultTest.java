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
package ddf.security.assertion.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.assertion.SecurityAssertion;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SecurityAssertionPrincipalDefaultTest {

  private static final String TEST_PRINCIPAL_NAME = "testuser";

  @Mock private SecurityAssertion mockAssertion;

  @Mock private Principal mockPrincipal;

  private SecurityAssertionPrincipalDefault securityAssertionPrincipal;

  @BeforeEach
  public void setUp() {
    when(mockPrincipal.getName()).thenReturn(TEST_PRINCIPAL_NAME);
    when(mockAssertion.getPrincipal()).thenReturn(mockPrincipal);
    securityAssertionPrincipal = new SecurityAssertionPrincipalDefault(mockAssertion);
  }

  @Test
  public void testConstructor() {
    SecurityAssertionPrincipalDefault principal =
        new SecurityAssertionPrincipalDefault(mockAssertion);
    assertThat(principal, is(notNullValue()));
  }

  @Test
  public void testGetAssertion() {
    SecurityAssertion assertion = securityAssertionPrincipal.getAssertion();
    assertThat(assertion, is(notNullValue()));
    assertThat(assertion, is(mockAssertion));
  }

  @Test
  public void testGetName() {
    String name = securityAssertionPrincipal.getName();
    assertThat(name, is(notNullValue()));
    assertThat(name, is(TEST_PRINCIPAL_NAME));
  }

  @Test
  public void testGetNameWithDifferentPrincipal() {
    Principal anotherPrincipal = mock(Principal.class);
    when(anotherPrincipal.getName()).thenReturn("anotheruser");
    SecurityAssertion anotherAssertion = mock(SecurityAssertion.class);
    when(anotherAssertion.getPrincipal()).thenReturn(anotherPrincipal);

    SecurityAssertionPrincipalDefault principal =
        new SecurityAssertionPrincipalDefault(anotherAssertion);

    assertThat(principal.getName(), is("anotheruser"));
  }

  @Test
  public void testGetNameWithNullPrincipal() {
    SecurityAssertion assertion = mock(SecurityAssertion.class);
    when(assertion.getPrincipal()).thenReturn(null);

    SecurityAssertionPrincipalDefault principal = new SecurityAssertionPrincipalDefault(assertion);
    assertThrows(NullPointerException.class, () -> principal.getName());
  }
}
