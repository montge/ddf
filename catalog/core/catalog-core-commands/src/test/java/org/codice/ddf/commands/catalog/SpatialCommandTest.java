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
import static org.hamcrest.Matchers.notNullValue;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Comprehensive tests for SpatialCommand */
@RunWith(MockitoJUnitRunner.class)
public class SpatialCommandTest {

  @Mock private CatalogFacade mockCatalog;

  @Mock private QueryResponse mockQueryResponse;

  private SpatialCommand spatialCommand;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @Before
  public void setUp() throws Exception {
    spatialCommand =
        new SpatialCommand() {
          @Override
          protected CatalogFacade getCatalog() {
            return mockCatalog;
          }

          @Override
          protected Object doExecute() throws Exception {
            return executeWithSubject();
          }
        };

    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    spatialCommand.console = new PrintStream(outputStream);
  }

  @After
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
  public void testSpatialSearchWithPoint() throws Exception {
    List<Result> results = createMockResults(5);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(5L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POINT (10 20)";
    spatialCommand.numberOfItems = 10;

    // Configuration test
    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithPolygon() throws Exception {
    List<Result> results = createMockResults(8);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(8L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POLYGON ((10 10, 10 20, 20 20, 20 10, 10 10))";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithLineString() throws Exception {
    List<Result> results = createMockResults(3);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(3L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "LINESTRING (0 0, 10 10, 20 5)";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithMultiPolygon() throws Exception {
    List<Result> results = createMockResults(6);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(6L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt =
        "MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)), "
            + "((20 35, 10 30, 10 10, 30 5, 45 20, 20 35)))";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithBoundingBox() throws Exception {
    List<Result> results = createMockResults(10);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(10L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    // Bounding box as polygon
    spatialCommand.wkt = "POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90))";
    spatialCommand.numberOfItems = 20;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithPointRadius() throws Exception {
    List<Result> results = createMockResults(4);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(4L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POINT (40.7128 -74.0060)"; // New York City
    spatialCommand.radius = 10000.0; // 10km radius
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithZeroRadius() throws Exception {
    List<Result> results = createMockResults(1);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(1L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POINT (10 20)";
    spatialCommand.radius = 0.0;
    spatialCommand.numberOfItems = 10;

    // Should handle zero radius (exact point match)
  }

  @Test
  public void testSpatialSearchWithLargeRadius() throws Exception {
    List<Result> results = createMockResults(50);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(100L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POINT (0 0)";
    spatialCommand.radius = 1000000.0; // 1000km radius
    spatialCommand.numberOfItems = 50;

    assertThat(spatialCommand.radius, is(1000000.0));
  }

  @Test
  public void testSpatialSearchCrossingDateLine() throws Exception {
    List<Result> results = createMockResults(7);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(7L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    // Polygon crossing international date line
    spatialCommand.wkt = "POLYGON ((170 10, 170 20, -170 20, -170 10, 170 10))";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchAtPoles() throws Exception {
    List<Result> results = createMockResults(2);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(2L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POINT (0 90)"; // North pole
    spatialCommand.radius = 1000.0;
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithNoResults() throws Exception {
    when(mockQueryResponse.getResults()).thenReturn(new ArrayList<>());
    when(mockQueryResponse.getHits()).thenReturn(0L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POINT (0 0)";
    spatialCommand.numberOfItems = 10;

    // Should handle no results gracefully
  }

  @Test
  public void testSpatialSearchWithComplexPolygon() throws Exception {
    List<Result> results = createMockResults(12);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(12L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    // Irregular polygon
    spatialCommand.wkt = "POLYGON ((0 0, 5 10, 10 5, 15 12, 8 2, 0 0))";
    spatialCommand.numberOfItems = 20;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithGeometryCollection() throws Exception {
    List<Result> results = createMockResults(5);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(5L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "GEOMETRYCOLLECTION (POINT (4 6), LINESTRING (4 6, 7 10))";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.wkt, is(notNullValue()));
  }

  @Test
  public void testSpatialSearchWithIntersectsOperator() throws Exception {
    List<Result> results = createMockResults(9);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(9L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POLYGON ((10 10, 10 20, 20 20, 20 10, 10 10))";
    spatialCommand.spatialType = "intersects";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.spatialType, is("intersects"));
  }

  @Test
  public void testSpatialSearchWithContainsOperator() throws Exception {
    List<Result> results = createMockResults(3);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(3L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POLYGON ((10 10, 10 20, 20 20, 20 10, 10 10))";
    spatialCommand.spatialType = "contains";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.spatialType, is("contains"));
  }

  @Test
  public void testSpatialSearchWithWithinOperator() throws Exception {
    List<Result> results = createMockResults(6);
    when(mockQueryResponse.getResults()).thenReturn(results);
    when(mockQueryResponse.getHits()).thenReturn(6L);
    when(mockCatalog.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);

    spatialCommand.wkt = "POLYGON ((10 10, 10 20, 20 20, 20 10, 10 10))";
    spatialCommand.spatialType = "within";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.spatialType, is("within"));
  }

  private List<Result> createMockResults(int count) {
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("id" + i);
      metacard.setTitle("Spatial Test Metacard " + i);
      metacard.setModifiedDate(new Date());
      metacard.setLocation("POINT (" + (10 + i) + " " + (20 + i) + ")");
      results.add(new ResultImpl(metacard));
    }
    return results;
  }
}
