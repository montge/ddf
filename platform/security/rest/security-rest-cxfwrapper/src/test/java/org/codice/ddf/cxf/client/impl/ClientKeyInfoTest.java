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
package org.codice.ddf.cxf.client.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class ClientKeyInfoTest {

  @Test
  void testConstructorWithValidValues() {
    Path keystorePath = Paths.get("/etc/keystore.jks");
    ClientKeyInfo keyInfo = new ClientKeyInfo("myAlias", keystorePath);

    assertThat(keyInfo.getAlias(), is("myAlias"));
    assertThat(keyInfo.getKeystorePath(), is(keystorePath));
  }

  @Test
  void testConstructorWithNullAlias() {
    Path keystorePath = Paths.get("/etc/keystore.jks");
    ClientKeyInfo keyInfo = new ClientKeyInfo(null, keystorePath);

    assertThat(keyInfo.getAlias(), is(nullValue()));
    assertThat(keyInfo.getKeystorePath(), is(keystorePath));
  }

  @Test
  void testConstructorWithNullKeystorePath() {
    ClientKeyInfo keyInfo = new ClientKeyInfo("alias", null);

    assertThat(keyInfo.getAlias(), is("alias"));
    assertThat(keyInfo.getKeystorePath(), is(nullValue()));
  }

  @Test
  void testConstructorWithBothNull() {
    ClientKeyInfo keyInfo = new ClientKeyInfo(null, null);

    assertThat(keyInfo.getAlias(), is(nullValue()));
    assertThat(keyInfo.getKeystorePath(), is(nullValue()));
  }

  @Test
  void testConstructorWithEmptyAlias() {
    Path keystorePath = Paths.get("/etc/keystore.jks");
    ClientKeyInfo keyInfo = new ClientKeyInfo("", keystorePath);

    assertThat(keyInfo.getAlias(), is(""));
    assertThat(keyInfo.getKeystorePath(), is(keystorePath));
  }

  @Test
  void testConstructorWithRelativePath() {
    Path keystorePath = Paths.get("config/keystore.jks");
    ClientKeyInfo keyInfo = new ClientKeyInfo("certAlias", keystorePath);

    assertThat(keyInfo.getAlias(), is("certAlias"));
    assertThat(keyInfo.getKeystorePath(), is(keystorePath));
  }

  @Test
  void testConstructorWithAbsolutePath() {
    Path keystorePath = Paths.get("/opt/ddf/etc/keystores/serverKeystore.jks");
    ClientKeyInfo keyInfo = new ClientKeyInfo("serverCert", keystorePath);

    assertThat(keyInfo.getAlias(), is("serverCert"));
    assertThat(keyInfo.getKeystorePath(), is(keystorePath));
  }

  @Test
  void testGetAliasReturnsCorrectValue() {
    ClientKeyInfo keyInfo = new ClientKeyInfo("testAlias", Paths.get("/test/path"));

    String alias = keyInfo.getAlias();

    assertThat(alias, is("testAlias"));
  }

  @Test
  void testGetKeystorePathReturnsCorrectValue() {
    Path expectedPath = Paths.get("/expected/keystore/path.jks");
    ClientKeyInfo keyInfo = new ClientKeyInfo("alias", expectedPath);

    Path actualPath = keyInfo.getKeystorePath();

    assertThat(actualPath, is(expectedPath));
  }

  @Test
  void testConstructorWithSpecialCharactersInAlias() {
    Path keystorePath = Paths.get("/etc/keystore.jks");
    ClientKeyInfo keyInfo = new ClientKeyInfo("alias-with_special.chars", keystorePath);

    assertThat(keyInfo.getAlias(), is("alias-with_special.chars"));
  }
}
