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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codice.ddf.commands.catalog.facade.CatalogFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Comprehensive tests for RangeCommand */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RangeCommandTest {

  @Mock private CatalogFacade mockCatalog;

  @Mock private QueryResponse mockQueryResponse;

  private RangeCommand rangeCommand;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @BeforeEach
  public void setUp() throws Exception {
    rangeCommand =
        new RangeCommand() {
          @Override
          protected CatalogFacade getCatalog() {
            return mockCatalog;
          }
        };

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
  public void testRangeSearchWithNumericAttribute() throws Exception {
    List<Result> results = createMockResults(5);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(5L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "price";
    rangeCommand.rangeBeginning = "10";
    rangeCommand.rangeEnd = "100";

    assertThat(rangeCommand.attributeName, is("price"));
    assertThat(rangeCommand.rangeBeginning, is("10"));
    assertThat(rangeCommand.rangeEnd, is("100"));
  }

  @Test
  public void testRangeSearchWithDateAttribute() throws Exception {
    List<Result> results = createMockResults(3);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(3L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "created";
    rangeCommand.rangeBeginning = "2023-01-01";
    rangeCommand.rangeEnd = "2023-12-31";

    assertThat(rangeCommand.attributeName, is("created"));
    assertThat(rangeCommand.rangeBeginning, is("2023-01-01"));
    assertThat(rangeCommand.rangeEnd, is("2023-12-31"));
  }

  @Test
  public void testRangeSearchWithIntegerBounds() throws Exception {
    List<Result> results = createMockResults(10);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(10L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "quantity";
    rangeCommand.rangeBeginning = "0";
    rangeCommand.rangeEnd = "1000";

    assertThat(rangeCommand.attributeName, is("quantity"));
    assertThat(rangeCommand.rangeBeginning, is("0"));
    assertThat(rangeCommand.rangeEnd, is("1000"));
  }

  @Test
  public void testRangeSearchWithDecimalBounds() throws Exception {
    List<Result> results = createMockResults(7);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(7L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "temperature";
    rangeCommand.rangeBeginning = "0.5";
    rangeCommand.rangeEnd = "100.5";

    assertThat(rangeCommand.attributeName, is("temperature"));
    assertThat(rangeCommand.rangeBeginning, is("0.5"));
    assertThat(rangeCommand.rangeEnd, is("100.5"));
  }

  @Test
  public void testRangeSearchWithNegativeBounds() throws Exception {
    List<Result> results = createMockResults(4);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(4L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "elevation";
    rangeCommand.rangeBeginning = "-100";
    rangeCommand.rangeEnd = "500";

    assertThat(rangeCommand.attributeName, is("elevation"));
    assertThat(rangeCommand.rangeBeginning, is("-100"));
    assertThat(rangeCommand.rangeEnd, is("500"));
  }

  @Test
  public void testRangeSearchWithEqualBounds() throws Exception {
    List<Result> results = createMockResults(2);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(2L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "status";
    rangeCommand.rangeBeginning = "100";
    rangeCommand.rangeEnd = "100";

    assertThat(rangeCommand.attributeName, is("status"));
    assertThat(rangeCommand.rangeBeginning, is(rangeCommand.rangeEnd));
    assertThat(rangeCommand.rangeBeginning, is("100"));
  }

  @Test
  public void testRangeSearchWithLargeNumbers() throws Exception {
    List<Result> results = createMockResults(1);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(1L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "fileSize";
    rangeCommand.rangeBeginning = "1000000";
    rangeCommand.rangeEnd = "10000000";

    assertThat(rangeCommand.attributeName, is("fileSize"));
    assertThat(rangeCommand.rangeBeginning, is("1000000"));
    assertThat(rangeCommand.rangeEnd, is("10000000"));
  }

  @Test
  public void testRangeSearchWithNoResults() throws Exception {
    when(mockQueryResponse.getResults()).thenReturn(new ArrayList<>());
    when(mockQueryResponse.getHits()).thenReturn(0L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "score";
    rangeCommand.rangeBeginning = "1000";
    rangeCommand.rangeEnd = "2000";

    assertThat(rangeCommand.attributeName, is("score"));
    assertThat(rangeCommand.rangeBeginning, is("1000"));
    assertThat(rangeCommand.rangeEnd, is("2000"));
    assertThat(mockQueryResponse.getResults().isEmpty(), is(true));
    assertThat(mockQueryResponse.getHits(), is(0L));
  }

  @Test
  public void testRangeSearchWithTimestamps() throws Exception {
    List<Result> results = createMockResults(6);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(6L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    String beginning = String.valueOf(System.currentTimeMillis() - 86400000);
    String end = String.valueOf(System.currentTimeMillis());
    rangeCommand.attributeName = "modified";
    rangeCommand.rangeBeginning = beginning;
    rangeCommand.rangeEnd = end;

    assertThat(rangeCommand.attributeName, is("modified"));
    assertThat(rangeCommand.rangeBeginning, is(beginning));
    assertThat(rangeCommand.rangeEnd, is(end));
  }

  @Test
  public void testRangeSearchWithCustomAttribute() throws Exception {
    List<Result> results = createMockResults(3);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(3L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    rangeCommand.attributeName = "custom.rating";
    rangeCommand.rangeBeginning = "1";
    rangeCommand.rangeEnd = "5";

    assertThat(rangeCommand.attributeName, is("custom.rating"));
    assertThat(rangeCommand.rangeBeginning, is("1"));
    assertThat(rangeCommand.rangeEnd, is("5"));
  }

  private List<Result> createMockResults(int count) {
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("id" + i);
      metacard.setTitle("Range Test Metacard " + i);
      metacard.setModifiedDate(new Date());
      results.add(new ResultImpl(metacard));
    }
    return results;
  }
}
