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
package org.codice.ddf.opensearch.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

/** Comprehensive tests for BoundingBox spatial search representation */
public class BoundingBoxTest {

  @Test
  public void testBoundingBoxCreation() {
    double west = -180.0;
    double south = -90.0;
    double east = 180.0;
    double north = 90.0;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox, is(notNullValue()));
    assertThat(bbox.getWest(), is(west));
    assertThat(bbox.getSouth(), is(south));
    assertThat(bbox.getEast(), is(east));
    assertThat(bbox.getNorth(), is(north));
  }

  @Test
  public void testBoundingBoxWithSmallArea() {
    double west = 10.0;
    double south = 20.0;
    double east = 10.5;
    double north = 20.5;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getWest(), is(west));
    assertThat(bbox.getSouth(), is(south));
    assertThat(bbox.getEast(), is(east));
    assertThat(bbox.getNorth(), is(north));
  }

  @Test
  public void testBoundingBoxAtEquator() {
    double west = -10.0;
    double south = -5.0;
    double east = 10.0;
    double north = 5.0;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getSouth() < 0, is(true));
    assertThat(bbox.getNorth() > 0, is(true));
  }

  @Test
  public void testBoundingBoxCrossingDateLine() {
    // Bounding box crossing the international date line
    double west = 170.0;
    double south = 10.0;
    double east = -170.0;
    double north = 20.0;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getWest(), is(170.0));
    assertThat(bbox.getEast(), is(-170.0));
  }

  @Test
  public void testBoundingBoxAtPrimeMeridian() {
    double west = -5.0;
    double south = 45.0;
    double east = 5.0;
    double north = 55.0;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getWest() < 0, is(true));
    assertThat(bbox.getEast() > 0, is(true));
  }

  @Test
  public void testBoundingBoxAtNorthPole() {
    double west = -180.0;
    double south = 85.0;
    double east = 180.0;
    double north = 90.0;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getNorth(), is(90.0));
  }

  @Test
  public void testBoundingBoxAtSouthPole() {
    double west = -180.0;
    double south = -90.0;
    double east = 180.0;
    double north = -85.0;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getSouth(), is(-90.0));
  }

  @Test
  public void testBoundingBoxWithZeroWidth() {
    // A vertical line
    double west = 10.0;
    double south = 20.0;
    double east = 10.0;
    double north = 30.0;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getWest(), is(bbox.getEast()));
  }

  @Test
  public void testBoundingBoxWithZeroHeight() {
    // A horizontal line
    double west = 10.0;
    double south = 20.0;
    double east = 30.0;
    double north = 20.0;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getSouth(), is(bbox.getNorth()));
  }

  @Test
  public void testBoundingBoxAsPoint() {
    // All coordinates the same - represents a point
    double coord = 15.0;
    BoundingBox bbox = new BoundingBox(coord, coord, coord, coord);

    assertThat(bbox.getWest(), is(coord));
    assertThat(bbox.getSouth(), is(coord));
    assertThat(bbox.getEast(), is(coord));
    assertThat(bbox.getNorth(), is(coord));
  }

  @Test
  public void testBoundingBoxCoversWholeWorld() {
    BoundingBox bbox = new BoundingBox(-180.0, -90.0, 180.0, 90.0);

    assertThat(bbox.getWest(), is(-180.0));
    assertThat(bbox.getSouth(), is(-90.0));
    assertThat(bbox.getEast(), is(180.0));
    assertThat(bbox.getNorth(), is(90.0));
  }

  @Test
  public void testBoundingBoxInNorthernHemisphere() {
    BoundingBox bbox = new BoundingBox(-10.0, 40.0, 10.0, 60.0);

    assertThat(bbox.getSouth() > 0, is(true));
    assertThat(bbox.getNorth() > 0, is(true));
  }

  @Test
  public void testBoundingBoxInSouthernHemisphere() {
    BoundingBox bbox = new BoundingBox(-10.0, -60.0, 10.0, -40.0);

    assertThat(bbox.getSouth() < 0, is(true));
    assertThat(bbox.getNorth() < 0, is(true));
  }

  @Test
  public void testBoundingBoxInEasternHemisphere() {
    BoundingBox bbox = new BoundingBox(10.0, -10.0, 80.0, 10.0);

    assertThat(bbox.getWest() > 0, is(true));
    assertThat(bbox.getEast() > 0, is(true));
  }

  @Test
  public void testBoundingBoxInWesternHemisphere() {
    BoundingBox bbox = new BoundingBox(-80.0, -10.0, -10.0, 10.0);

    assertThat(bbox.getWest() < 0, is(true));
    assertThat(bbox.getEast() < 0, is(true));
  }

  @Test
  public void testBoundingBoxEquality() {
    BoundingBox bbox1 = new BoundingBox(10.0, 20.0, 30.0, 40.0);
    BoundingBox bbox2 = new BoundingBox(10.0, 20.0, 30.0, 40.0);

    assertThat(bbox1.getWest(), is(bbox2.getWest()));
    assertThat(bbox1.getSouth(), is(bbox2.getSouth()));
    assertThat(bbox1.getEast(), is(bbox2.getEast()));
    assertThat(bbox1.getNorth(), is(bbox2.getNorth()));
  }

  @Test
  public void testBoundingBoxWithHighPrecision() {
    double west = -74.00597;
    double south = 40.71278;
    double east = -73.98597;
    double north = 40.73278;

    BoundingBox bbox = new BoundingBox(west, south, east, north);

    assertThat(bbox.getWest(), is(west));
    assertThat(bbox.getSouth(), is(south));
    assertThat(bbox.getEast(), is(east));
    assertThat(bbox.getNorth(), is(north));
  }

  @Test
  public void testBoundingBoxToString() {
    BoundingBox bbox = new BoundingBox(10.0, 20.0, 30.0, 40.0);
    String str = bbox.toString();

    assertThat(str, is(notNullValue()));
    // ToString should contain bounding box coordinates
  }

  @Test
  public void testBoundingBoxWidth() {
    BoundingBox bbox = new BoundingBox(10.0, 20.0, 30.0, 40.0);

    double width = bbox.getEast() - bbox.getWest();
    assertThat(width, is(20.0));
  }

  @Test
  public void testBoundingBoxHeight() {
    BoundingBox bbox = new BoundingBox(10.0, 20.0, 30.0, 40.0);

    double height = bbox.getNorth() - bbox.getSouth();
    assertThat(height, is(20.0));
  }

  @Test
  public void testBoundingBoxForNewYorkCity() {
    // Approximate bounding box for NYC
    BoundingBox bbox = new BoundingBox(-74.05, 40.70, -73.90, 40.85);

    assertThat(bbox.getWest() < bbox.getEast(), is(true));
    assertThat(bbox.getSouth() < bbox.getNorth(), is(true));
  }

  @Test
  public void testBoundingBoxForLondon() {
    // Approximate bounding box for London
    BoundingBox bbox = new BoundingBox(-0.5, 51.3, 0.3, 51.7);

    assertThat(bbox.getWest() < 0, is(true));
    assertThat(bbox.getEast() > 0, is(true));
  }
}
