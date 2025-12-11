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
package ddf.security.principal.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GuestPrincipalTest {

  @Test
  public void testConstructorWithValidName() {
    GuestPrincipal principal = new GuestPrincipal("testuser");

    assertThat(principal.getName(), is("Guest@testuser"));
  }

  @Test
  public void testConstructorWithNullName() {
    GuestPrincipal principal = new GuestPrincipal(null);

    assertThat(principal.getName(), is("Guest@"));
  }

  @Test
  public void testConstructorWithEmptyName() {
    GuestPrincipal principal = new GuestPrincipal("");

    assertThat(principal.getName(), is("Guest@"));
  }

  @Test
  public void testConstructorWithNameAlreadyHavingPrefix() {
    GuestPrincipal principal = new GuestPrincipal("Guest@existinguser");

    assertThat(principal.getName(), is("Guest@existinguser"));
  }

  @Test
  public void testConstructorWithNameStartingWithGuestButNoDelimiter() {
    GuestPrincipal principal = new GuestPrincipal("GuestUser");

    assertThat(principal.getName(), is("Guest@GuestUser"));
  }

  @Test
  public void testGetName() {
    GuestPrincipal principal = new GuestPrincipal("user123");

    assertThat(principal.getName(), is(notNullValue()));
    assertThat(principal.getName(), startsWith("Guest@"));
  }

  @Test
  public void testToString() {
    GuestPrincipal principal = new GuestPrincipal("testuser");

    assertThat(principal.toString(), is("Guest@testuser"));
  }

  @Test
  public void testToStringWithNullName() {
    GuestPrincipal principal = new GuestPrincipal(null);

    assertThat(principal.toString(), is("Guest@"));
  }

  @Test
  public void testGuestNamePrefix() {
    assertThat(GuestPrincipal.GUEST_NAME_PREFIX, is("Guest"));
  }

  @Test
  public void testNameDelimiter() {
    assertThat(GuestPrincipal.NAME_DELIMITER, is("@"));
  }

  @Test
  public void testConstructorWithSpecialCharacters() {
    GuestPrincipal principal = new GuestPrincipal("user@domain.com");

    assertThat(principal.getName(), is("Guest@user@domain.com"));
  }

  @Test
  public void testConstructorWithSpaces() {
    GuestPrincipal principal = new GuestPrincipal("user name");

    assertThat(principal.getName(), is("Guest@user name"));
  }

  @Test
  public void testConstructorWithNumericName() {
    GuestPrincipal principal = new GuestPrincipal("12345");

    assertThat(principal.getName(), is("Guest@12345"));
  }

  @Test
  public void testSerialization() throws Exception {
    GuestPrincipal principal = new GuestPrincipal("testuser");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(principal);
    oos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);
    GuestPrincipal deserializedPrincipal = (GuestPrincipal) ois.readObject();
    ois.close();

    assertThat(deserializedPrincipal.getName(), is(principal.getName()));
    assertThat(deserializedPrincipal.getName(), is("Guest@testuser"));
  }

  @Test
  public void testSerializationWithNullName() throws Exception {
    GuestPrincipal principal = new GuestPrincipal(null);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(principal);
    oos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);
    GuestPrincipal deserializedPrincipal = (GuestPrincipal) ois.readObject();
    ois.close();

    assertThat(deserializedPrincipal.getName(), is("Guest@"));
  }

  @Test
  public void testConstructorWithLongName() {
    String longName = "verylongusernamethatshouldstillworkproperly12345";
    GuestPrincipal principal = new GuestPrincipal(longName);

    assertThat(principal.getName(), is("Guest@" + longName));
  }

  @Test
  public void testConstructorWithUnicodeCharacters() {
    GuestPrincipal principal = new GuestPrincipal("用户名");

    assertThat(principal.getName(), is("Guest@用户名"));
  }

  @Test
  public void testConstructorPreservesExactPrefixedName() {
    String prefixedName = "Guest@originaluser";
    GuestPrincipal principal = new GuestPrincipal(prefixedName);

    assertThat(principal.getName(), is(prefixedName));
  }

  @Test
  public void testMultipleDelimitersInName() {
    GuestPrincipal principal = new GuestPrincipal("user@domain@subdomain");

    assertThat(principal.getName(), is("Guest@user@domain@subdomain"));
  }

  @Test
  public void testConstructorWithWhitespaceOnlyName() {
    GuestPrincipal principal = new GuestPrincipal("   ");

    assertThat(principal.getName(), is("Guest@   "));
  }

  @Test
  public void testConstructorWithTabsAndNewlines() {
    GuestPrincipal principal = new GuestPrincipal("user\t\nname");

    assertThat(principal.getName(), is("Guest@user\t\nname"));
  }

  @Test
  public void testNameWithOnlyPrefix() {
    GuestPrincipal principal = new GuestPrincipal("Guest");

    assertThat(principal.getName(), is("Guest@Guest"));
  }

  @Test
  public void testNameWithPrefixAndDelimiterOnly() {
    GuestPrincipal principal = new GuestPrincipal("Guest@");

    assertThat(principal.getName(), is("Guest@"));
  }

  @Test
  public void testEqualityOfPrincipalsWithSameName() {
    GuestPrincipal principal1 = new GuestPrincipal("testuser");
    GuestPrincipal principal2 = new GuestPrincipal("testuser");

    assertThat(principal1.getName(), is(equalTo(principal2.getName())));
  }

  @Test
  public void testEqualityOfPrincipalsWithDifferentNames() {
    GuestPrincipal principal1 = new GuestPrincipal("user1");
    GuestPrincipal principal2 = new GuestPrincipal("user2");

    assertThat(principal1.getName().equals(principal2.getName()), is(false));
  }

  @Test
  public void testConstructorWithCaseSensitiveName() {
    GuestPrincipal principal1 = new GuestPrincipal("TestUser");
    GuestPrincipal principal2 = new GuestPrincipal("testuser");

    assertThat(principal1.getName(), is("Guest@TestUser"));
    assertThat(principal2.getName(), is("Guest@testuser"));
    assertThat(principal1.getName().equals(principal2.getName()), is(false));
  }
}
