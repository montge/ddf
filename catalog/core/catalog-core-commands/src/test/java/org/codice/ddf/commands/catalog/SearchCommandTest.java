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
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.codice.ddf.commands.catalog.facade.CatalogFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Comprehensive tests for SearchCommand */
@ExtendWith(MockitoExtension.class)
public class SearchCommandTest extends ConsoleOutputCommon {

  @Mock private CatalogFacade mockCatalog;

  @Mock private QueryResponse mockQueryResponse;

  private SearchCommand searchCommand;

  @BeforeEach
  public void setUp() throws Exception {
    searchCommand =
        new SearchCommand() {
          @Override
          protected CatalogFacade getCatalog() {
            return mockCatalog;
          }
        };
    searchCommand.filterBuilder = new GeotoolsFilterBuilder();
  }

  @Test
  public void testSearchWithDefaultParameters() throws Exception {
    // Setup mock results
    List<Result> results = createMockResults(10);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(10L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    // Execute search with default wildcard
    searchCommand.searchPhraseArgument = "*";
    searchCommand.numberOfItems = 10;
    searchCommand.executeWithSubject();

    // Verify catalog was queried
    verify(mockCatalog, times(2)).query(any(QueryRequest.class)); // once for results, once for hits

    // Verify output contains result count
    String output = consoleOutput.getOutput();
    assertThat(output, containsString("10 result(s)"));
  }

  @Test
  public void testSearchWithSpecificSearchPhrase() throws Exception {
    List<Result> results = createMockResults(5);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(5L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "test query";
    searchCommand.numberOfItems = 100;
    searchCommand.executeWithSubject();

    verify(mockCatalog, times(2)).query(any(QueryRequest.class));

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("5 result(s)"));
  }

  @Test
  public void testSearchWithLimitedResults() throws Exception {
    List<Result> results = createMockResults(5);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(100L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "*";
    searchCommand.numberOfItems = 5;
    searchCommand.executeWithSubject();

    verify(mockCatalog, times(2)).query(any(QueryRequest.class));

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("5 result(s) out of"));
    assertThat(output, containsString("100"));
  }

  @Test
  public void testSearchWithNoResults() throws Exception {
    when(mockQueryResponse.getResults()).thenReturn(Collections.emptyList());
    when(mockQueryResponse.getHits()).thenReturn(0L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "nonexistent";
    searchCommand.numberOfItems = 10;
    searchCommand.executeWithSubject();

    verify(mockCatalog, times(2)).query(any(QueryRequest.class));

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("0 result(s)"));
  }

  @Test
  public void testSearchWithMetacardTitles() throws Exception {
    List<Result> results = new ArrayList<>();
    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setId("id1");
    metacard1.setTitle("Test Metacard 1");
    metacard1.setModifiedDate(new Date());
    results.add(new ResultImpl(metacard1));

    MetacardImpl metacard2 = new MetacardImpl();
    metacard2.setId("id2");
    metacard2.setTitle("Test Metacard 2");
    metacard2.setModifiedDate(new Date());
    results.add(new ResultImpl(metacard2));

    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(2L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "*";
    searchCommand.numberOfItems = 10;
    searchCommand.executeWithSubject();

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("Test Metacard 1"));
    assertThat(output, containsString("Test Metacard 2"));
  }

  @Test
  public void testSearchWithNullTitles() throws Exception {
    List<Result> results = new ArrayList<>();
    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setId("id1");
    metacard1.setTitle(null); // null title
    metacard1.setModifiedDate(new Date());
    results.add(new ResultImpl(metacard1));

    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(1L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "*";
    searchCommand.numberOfItems = 10;
    searchCommand.executeWithSubject();

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("N/A")); // Should display N/A for null titles
  }

  @Test
  public void testSearchWithCaseSensitive() throws Exception {
    List<Result> results = createMockResults(3);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(3L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "Test";
    searchCommand.caseSensitive = true;
    searchCommand.numberOfItems = 10;
    searchCommand.executeWithSubject();

    verify(mockCatalog, times(2)).query(any(QueryRequest.class));

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("3 result(s)"));
  }

  @Test
  public void testSearchWithUnlimitedResults() throws Exception {
    List<Result> results = createMockResults(15);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(15L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "*";
    searchCommand.numberOfItems = -1; // unlimited
    searchCommand.executeWithSubject();

    verify(mockCatalog, times(2)).query(any(QueryRequest.class));

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("15 result(s)"));
  }

  @Test
  public void testSearchWithModifiedDates() throws Exception {
    List<Result> results = new ArrayList<>();
    Date now = new Date();

    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setId("id1");
    metacard1.setTitle("Test");
    metacard1.setModifiedDate(now);
    results.add(new ResultImpl(metacard1));

    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(1L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "*";
    searchCommand.numberOfItems = 10;
    searchCommand.executeWithSubject();

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("id1"));
  }

  @Test
  public void testSearchWithNullModifiedDate() throws Exception {
    List<Result> results = new ArrayList<>();

    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setId("id1");
    metacard1.setTitle("Test");
    metacard1.setModifiedDate(null); // null modified date
    results.add(new ResultImpl(metacard1));

    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(1L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "*";
    searchCommand.numberOfItems = 10;
    searchCommand.executeWithSubject();

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("id1"));
    // Should not crash with null date
  }

  @Test
  public void testSearchPerformanceTiming() throws Exception {
    List<Result> results = createMockResults(100);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(100L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    searchCommand.searchPhraseArgument = "*";
    searchCommand.numberOfItems = 100;
    searchCommand.executeWithSubject();

    String output = consoleOutput.getOutput();
    assertThat(output, containsString("seconds")); // Should display timing
  }

  private List<Result> createMockResults(int count) {
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("id" + i);
      metacard.setTitle("Test Metacard " + i);
      metacard.setModifiedDate(new Date());
      metacard.setMetadata("<metadata>test content " + i + "</metadata>");
      results.add(new ResultImpl(metacard));
    }
    return results;
  }
}
