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
package org.codice.ddf.platform.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;

@EnableRuleMigrationSupport
public class PathBuilderTest {
  @Rule public TemporaryFolder tempDir = new TemporaryFolder();

  private ArrayList<String> pathTokens;

  @BeforeEach
  public void setup() {
    Path pathToTemp = Paths.get(tempDir.getRoot().getAbsolutePath());
    pathTokens = new ArrayList<>();
    pathToTemp.iterator().forEachRemaining(p -> pathTokens.add(p.toString()));
  }

  @Test
  public void pathWithNoSubs() {
    assertThat(
        new PathBuilder(pathTokens.get(0)).build(), is(equalTo(Paths.get(pathTokens.get(0)))));
  }

  @Test
  public void pathWithOneSubElement() {
    assertThat(
        new PathBuilder(pathTokens.get(0), pathTokens.get(1)).build(),
        is(equalTo(Paths.get(pathTokens.get(0), pathTokens.get(1)))));
  }

  @Test
  public void pathWithAllSubElement() {
    List<String> subElemsList = pathTokens.subList(1, pathTokens.size());
    String[] subElemsArr = subElemsList.toArray(new String[pathTokens.size() - 1]);

    assertThat(
        new PathBuilder(pathTokens.get(0), subElemsArr).build(),
        is(equalTo(Paths.get(pathTokens.get(0), subElemsArr))));
  }

  @Test
  public void pathWithNullRoot() {
    assertThrows(IllegalArgumentException.class, () -> new PathBuilder(null).build());
  }

  @Test
  public void pathWithNullRootAndSubElements() {
    String[] subElems =
        pathTokens.subList(1, pathTokens.size()).toArray(new String[pathTokens.size() - 1]);

    assertThrows(IllegalArgumentException.class, () -> new PathBuilder(null, subElems).build());
  }
}
