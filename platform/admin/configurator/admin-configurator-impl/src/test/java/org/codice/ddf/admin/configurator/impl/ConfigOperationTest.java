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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.codice.ddf.admin.configurator.Operation;
import org.codice.ddf.admin.configurator.Result;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link ConfigOperation} */
@ExtendWith(MockitoExtension.class)
@EnableRuleMigrationSupport
public class ConfigOperationTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  private ConfigOperation.Actions actions;
  private Path testFilePath;

  @BeforeEach
  public void setUp() throws Exception {
    // Set ddf.home to temp folder for tests that require it
    System.setProperty("ddf.home", tempFolder.getRoot().getAbsolutePath());
    // Create allowed subdirectory (etc is an allowed edit location)
    tempFolder.newFolder("etc");
    actions = new ConfigOperation.Actions();
    testFilePath = Paths.get(tempFolder.getRoot().getAbsolutePath(), "etc", "test-config.cfg");
    // Create the test file
    testFilePath.toFile().createNewFile();
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
    // Path must be in an allowed subdirectory (etc/)
    Path nonExistentPath =
        Paths.get(tempFolder.getRoot().getAbsolutePath(), "etc", "nonexistent.cfg");

    Operation<Void> deleteOp = actions.delete(nonExistentPath);
    Result<Void> result = deleteOp.commit();

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testDeleteWithNullPath() {
    // Implementation throws IllegalArgumentException for null path validation
    assertThrows(IllegalArgumentException.class, () -> actions.delete(null));
  }

  @Test
  public void testDeleteWithInvalidPath() {
    // Implementation throws IllegalArgumentException for invalid path validation
    assertThrows(IllegalArgumentException.class, () -> actions.delete(Paths.get("")));
  }

  @Test
  public void testCreateNotSupported() {
    assertThrows(UnsupportedOperationException.class, () -> actions.create(testFilePath, null));
  }

  @Test
  public void testUpdateNotSupported() {
    assertThrows(
        UnsupportedOperationException.class, () -> actions.update(testFilePath, null, false));
  }

  @Test
  public void testGetPropertiesNotSupported() {
    assertThrows(UnsupportedOperationException.class, () -> actions.getProperties(testFilePath));
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
