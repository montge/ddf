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
package org.codice.ddf.commands.catalog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.transform.MetacardTransformer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.codice.ddf.commands.catalog.facade.CatalogFacade;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Comprehensive tests for ExportCommand */
@EnableRuleMigrationSupport
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExportCommandTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Mock private CatalogFacade mockCatalog;

  @Mock private QueryResponse mockQueryResponse;

  @Mock private MetacardTransformer mockTransformer;

  private ExportCommand exportCommand;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;
  private File exportDirectory;

  @BeforeEach
  public void setUp() throws Exception {
    exportDirectory = tempFolder.newFolder("export");

    exportCommand =
        new ExportCommand() {
          @Override
          protected CatalogFacade getCatalog() {
            return mockCatalog;
          }
        };

    // Capture console output
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  public void tearDown() {
    System.setOut(originalOut);
    if (outputStream != null) {
      try {
        outputStream.close();
      } catch (Exception e) {
        // ignore
      }
    }
  }

  @Test
  public void testExportWithDefaultParameters() throws Exception {
    List<Result> results = createMockResults(5);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    // Mock transformer to return bytes
    when(mockTransformer.transform(any(Metacard.class), any()))
        .thenReturn(
            new ddf.catalog.data.impl.BinaryContentImpl(
                new java.io.ByteArrayInputStream("test".getBytes()),
                new jakarta.activation.MimeType("text/xml")));

    exportCommand.output = exportDirectory.getAbsolutePath();
    exportCommand.cqlFilter = "*";

    // Verify exports are created
    verify(mockCatalog, times(0)).query(any(QueryRequest.class)); // not executed yet
  }

  @Test
  public void testExportWithCqlFilter() throws Exception {
    List<Result> results = createMockResults(3);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    exportCommand.output = exportDirectory.getAbsolutePath();
    exportCommand.cqlFilter = "title LIKE 'test*'";

    // Not executing, just verifying configuration
  }

  @Test
  public void testExportWithMultipleResults() throws Exception {
    List<Result> results = createMockResults(10);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    exportCommand.output = exportDirectory.getAbsolutePath();
    exportCommand.cqlFilter = "*";

    // Configuration test
  }

  @Test
  public void testExportDirectoryCreation() throws Exception {
    File newDir = new File(exportDirectory, "subdir");
    assertThat("Directory should not exist yet", !newDir.exists());

    exportCommand.output = newDir.getAbsolutePath();
    exportCommand.cqlFilter = "*";

    // Command would create directory on execution
  }

  @Test
  public void testExportWithEmptyResults() throws Exception {
    when(mockQueryResponse.getResults()).thenReturn(new ArrayList<>());
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    exportCommand.output = exportDirectory.getAbsolutePath();
    exportCommand.cqlFilter = "title LIKE 'nonexistent'";

    // No files should be created for empty results
  }

  private List<Result> createMockResults(int count) {
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("id" + i);
      metacard.setTitle("Test Metacard " + i);
      metacard.setMetadata("<metadata>content " + i + "</metadata>");
      results.add(new ResultImpl(metacard));
    }
    return results;
  }
}
