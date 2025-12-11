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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClaimImplTest {

  @Test
  public void testConstructorWithName() {
    ClaimImpl claim = new ClaimImpl("emailaddress");

    assertThat(claim.getName(), is("emailaddress"));
    assertThat(claim.getValues(), is(notNullValue()));
    assertThat(claim.getValues().isEmpty(), is(true));
  }

  @Test
  public void testConstructorWithNullName() {
    ClaimImpl claim = new ClaimImpl(null);

    assertThat(claim.getName(), is(nullValue()));
    assertThat(claim.getValues(), is(notNullValue()));
  }

  @Test
  public void testAddValue() {
    ClaimImpl claim = new ClaimImpl("emailaddress");
    claim.addValue("user@example.com");

    assertThat(claim.getValues(), hasSize(1));
    assertThat(claim.getValues().get(0), is("user@example.com"));
  }

  @Test
  public void testAddMultipleValues() {
    ClaimImpl claim = new ClaimImpl("roles");
    claim.addValue("admin");
    claim.addValue("user");
    claim.addValue("moderator");

    assertThat(claim.getValues(), hasSize(3));
  }

  @Test
  public void testAddNullValue() {
    ClaimImpl claim = new ClaimImpl("name");
    claim.addValue(null);

    assertThat(claim.getValues(), hasSize(1));
    assertThat(claim.getValues().get(0), is(nullValue()));
  }

  @Test
  public void testGetValuesReturnsUnmodifiableList() {
    ClaimImpl claim = new ClaimImpl("name");
    claim.addValue("John Doe");

    try {
      claim.getValues().add("Jane Doe");
      assertThat("Should have thrown UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      assertThat(claim.getValues(), hasSize(1));
    }
  }

  @Test
  public void testAddEmptyStringValue() {
    ClaimImpl claim = new ClaimImpl("name");
    claim.addValue("");

    assertThat(claim.getValues(), hasSize(1));
    assertThat(claim.getValues().get(0), is(""));
  }

  @Test
  public void testAddDuplicateValues() {
    ClaimImpl claim = new ClaimImpl("roles");
    claim.addValue("admin");
    claim.addValue("admin");
    claim.addValue("admin");

    assertThat(claim.getValues(), hasSize(3));
  }

  @Test
  public void testGetName() {
    ClaimImpl claim = new ClaimImpl("testClaim");

    assertThat(claim.getName(), is("testClaim"));
  }

  @Test
  public void testGetValuesEmptyList() {
    ClaimImpl claim = new ClaimImpl("emptyClaim");

    assertThat(claim.getValues(), is(notNullValue()));
    assertThat(claim.getValues().isEmpty(), is(true));
  }

  @Test
  public void testClaimWithLongName() {
    String longName = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";
    ClaimImpl claim = new ClaimImpl(longName);
    claim.addValue("user@example.com");

    assertThat(claim.getName(), is(longName));
    assertThat(claim.getValues(), hasSize(1));
  }

  @Test
  public void testClaimWithSpecialCharactersInValue() {
    ClaimImpl claim = new ClaimImpl("special");
    claim.addValue("value!@#$%^&*()");
    claim.addValue("value<>?{}[]");

    assertThat(claim.getValues(), hasSize(2));
  }

  @Test
  public void testClaimWithWhitespaceValues() {
    ClaimImpl claim = new ClaimImpl("whitespace");
    claim.addValue("   ");
    claim.addValue("\t");
    claim.addValue("\n");

    assertThat(claim.getValues(), hasSize(3));
  }
}
