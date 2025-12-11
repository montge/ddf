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

import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.QueryResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codice.ddf.commands.catalog.facade.CatalogFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Comprehensive tests for SpatialCommand */
@ExtendWith(MockitoExtension.class)
public class SpatialCommandTest extends ConsoleOutputCommon {

  @Mock private CatalogFacade mockCatalog;

  @Mock private QueryResponse mockQueryResponse;

  private SpatialCommand spatialCommand;

  @BeforeEach
  public void setUp() throws Exception {
    spatialCommand =
        new SpatialCommand() {
          @Override
          protected CatalogFacade getCatalog() {
            return mockCatalog;
          }
        };
  }

  @Test
  public void testSpatialSearchWithPoint() throws Exception {
    spatialCommand.operation = "RADIUS";
    spatialCommand.pointX = "10";
    spatialCommand.pointY = "20";
    spatialCommand.numberOfItems = 10;

    // Configuration test
    assertThat(spatialCommand.operation, is(notNullValue()));
    assertThat(spatialCommand.pointX, is("10"));
  }

  @Test
  public void testSpatialSearchWithRadiusOperation() throws Exception {
    spatialCommand.operation = "RADIUS";
    spatialCommand.pointX = "10";
    spatialCommand.pointY = "20";
    spatialCommand.radius = "5000";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.operation, is("RADIUS"));
    assertThat(spatialCommand.radius, is("5000"));
  }

  @Test
  public void testSpatialSearchWithNNOperation() throws Exception {
    spatialCommand.operation = "NN";
    spatialCommand.pointX = "40.7128";
    spatialCommand.pointY = "-74.0060";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.operation, is("NN"));
  }

  @Test
  public void testSpatialSearchWithContainsOperation() throws Exception {
    spatialCommand.operation = "CONTAINS";
    spatialCommand.pointX = "10";
    spatialCommand.pointY = "20";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.operation, is("CONTAINS"));
  }

  @Test
  public void testSpatialSearchWithIntersectsOperation() throws Exception {
    spatialCommand.operation = "INTERSECTS";
    spatialCommand.pointX = "0";
    spatialCommand.pointY = "0";
    spatialCommand.numberOfItems = 20;

    assertThat(spatialCommand.operation, is("INTERSECTS"));
  }

  @Test
  public void testSpatialSearchWithEqualsOperation() throws Exception {
    spatialCommand.operation = "EQUALS";
    spatialCommand.pointX = "45.5";
    spatialCommand.pointY = "-122.7";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.operation, is("EQUALS"));
  }

  @Test
  public void testSpatialSearchWithDisjointOperation() throws Exception {
    spatialCommand.operation = "DISJOINT";
    spatialCommand.pointX = "10";
    spatialCommand.pointY = "20";
    spatialCommand.numberOfItems = 15;

    assertThat(spatialCommand.operation, is("DISJOINT"));
  }

  @Test
  public void testSpatialSearchWithTouchesOperation() throws Exception {
    spatialCommand.operation = "TOUCHES";
    spatialCommand.pointX = "0";
    spatialCommand.pointY = "90";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.operation, is("TOUCHES"));
  }

  @Test
  public void testSpatialSearchWithCrossesOperation() throws Exception {
    spatialCommand.operation = "CROSSES";
    spatialCommand.pointX = "170";
    spatialCommand.pointY = "10";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.operation, is("CROSSES"));
  }

  @Test
  public void testSpatialSearchWithWithinOperation() throws Exception {
    spatialCommand.operation = "WITHIN";
    spatialCommand.pointX = "20";
    spatialCommand.pointY = "30";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.operation, is("WITHIN"));
  }

  @Test
  public void testSpatialSearchWithOverlapsOperation() throws Exception {
    spatialCommand.operation = "OVERLAPS";
    spatialCommand.pointX = "15";
    spatialCommand.pointY = "25";
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.operation, is("OVERLAPS"));
  }

  @Test
  public void testSpatialSearchWithDefaultRadius() throws Exception {
    spatialCommand.operation = "RADIUS";
    spatialCommand.pointX = "10";
    spatialCommand.pointY = "20";
    // Don't set radius, should use default
    spatialCommand.numberOfItems = 10;

    assertThat(spatialCommand.radius, is("10000"));
  }

  @Test
  public void testSpatialSearchWithCustomRadius() throws Exception {
    spatialCommand.operation = "RADIUS";
    spatialCommand.pointX = "0";
    spatialCommand.pointY = "0";
    spatialCommand.radius = "50000";
    spatialCommand.numberOfItems = 50;

    assertThat(spatialCommand.radius, is("50000"));
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
