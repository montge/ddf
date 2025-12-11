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
package org.codice.solr.factory.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringEndsWith.endsWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;

@EnableRuleMigrationSupport
public class ConfigsetsTest {

  private static final String TEST_COLLECTION_NAME = "test";

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  public File tempLocation;

  private Configsets configsets;

  @BeforeEach
  public void beforeTest() throws Exception {
    tempLocation = tempFolder.newFolder();
    configsets = new Configsets(tempLocation.toPath());
  }

  @Test
  public void writingDefaultToDisk() {
    File defaultConf = tempLocation.toPath().resolve(Paths.get("default", "conf")).toFile();
    assertThat(defaultConf.exists(), is(true));
    assertThat(defaultConf.listFiles().length, is(Configsets.SOLR_CONFIG_FILES.size()));
  }

  @Disabled
  @Test
  public void getMissingCollectionButGetDefaultInstead() {
    Path collectionLocation = configsets.get(TEST_COLLECTION_NAME);
    assertThat(
        collectionLocation.toString(),
        endsWith(File.separator + "default" + File.separator + "conf"));
  }

  @Disabled
  @Test
  public void getCollection() {
    tempLocation.toPath().resolve(Paths.get(TEST_COLLECTION_NAME, "conf")).toFile().mkdirs();
    Path collectionLocation = configsets.get(TEST_COLLECTION_NAME);
    assertThat(
        collectionLocation.toString(),
        endsWith(File.separator + TEST_COLLECTION_NAME + File.separator + "conf"));
  }

  @Test
  public void voidTestPartialConfigGetsRectified() throws IOException {
    Path rootPath = tempLocation.toPath();
    File confDir = rootPath.resolve(Paths.get(TEST_COLLECTION_NAME, "conf")).toFile();
    confDir.mkdirs();
    File schema = confDir.toPath().resolve(Paths.get("schema.xml")).toFile();
    IOUtils.write("schemastuff", new FileOutputStream(schema), Charset.defaultCharset());

    Path collectionLocation = configsets.get(TEST_COLLECTION_NAME);
    // Ensure that all required files are there even though only the schema was supplied
    for (String file : Configsets.SOLR_CONFIG_FILES) {
      File configFile = collectionLocation.resolve(Paths.get(file)).toFile();
      assertThat(configFile.exists(), is(true));
    }
  }
}
