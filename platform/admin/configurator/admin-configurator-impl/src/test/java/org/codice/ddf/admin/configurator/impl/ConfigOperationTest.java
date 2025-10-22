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
package org.codice.ddf.admin.configurator.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.codice.ddf.admin.configurator.ConfiguratorException;
import org.codice.ddf.admin.configurator.Operation;
import org.codice.ddf.admin.configurator.Result;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/** Unit tests for {@link ConfigOperation} */
@RunWith(MockitoJUnitRunner.class)
public class ConfigOperationTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  private ConfigOperation.Actions actions;
  private Path testFilePath;

  @Before
  public void setUp() throws Exception {
    actions = new ConfigOperation.Actions();
    testFilePath = tempFolder.newFile("test-config.cfg").toPath();
  }

  @Test
  public void testDeleteOperationCommit() throws Exception {
    // Create a file to delete
    File testFile = testFilePath.toFile();
    assertThat(testFile.exists(), is(true));

    Operation<Void> deleteOp = actions.delete(testFilePath);
    assertThat(deleteOp, is(notNullValue()));

    Result<Void> result = deleteOp.commit();
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testDeleteOperationRollback() throws Exception {
    Operation<Void> deleteOp = actions.delete(testFilePath);

    Result<Void> rollbackResult = deleteOp.rollback();
    assertThat(rollbackResult, is(notNullValue()));
  }

  @Test
  public void testDeleteNonExistentFile() throws Exception {
    Path nonExistentPath = Paths.get(tempFolder.getRoot().getAbsolutePath(), "nonexistent.cfg");

    Operation<Void> deleteOp = actions.delete(nonExistentPath);
    Result<Void> result = deleteOp.commit();

    assertThat(result, is(notNullValue()));
  }

  @Test(expected = ConfiguratorException.class)
  public void testDeleteWithNullPath() throws Exception {
    actions.delete(null);
  }

  @Test(expected = ConfiguratorException.class)
  public void testDeleteWithInvalidPath() throws Exception {
    actions.delete(Paths.get(""));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testCreateNotSupported() throws Exception {
    actions.create(testFilePath, null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testUpdateNotSupported() throws Exception {
    actions.update(testFilePath, null, false);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetPropertiesNotSupported() throws Exception {
    actions.getProperties(testFilePath);
  }

  @Test
  public void testDeleteOperationWithReadOnlyFile() throws Exception {
    File testFile = testFilePath.toFile();
    testFile.setReadOnly();

    Operation<Void> deleteOp = actions.delete(testFilePath);
    Result<Void> result = deleteOp.commit();

    assertThat(result, is(notNullValue()));
  }
}
