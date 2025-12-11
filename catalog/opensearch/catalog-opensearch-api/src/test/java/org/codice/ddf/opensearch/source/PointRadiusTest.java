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

/** Comprehensive tests for PointRadius spatial search representation */
public class PointRadiusTest {

  @Test
  public void testPointRadiusCreation() {
    double lon = -74.0060;
    double lat = 40.7128;
    double radius = 1000.0;

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius, is(notNullValue()));
    assertThat(pointRadius.getLon(), is(lon));
    assertThat(pointRadius.getLat(), is(lat));
    assertThat(pointRadius.getRadius(), is(radius));
  }

  @Test
  public void testPointRadiusWithZeroRadius() {
    double lon = 0.0;
    double lat = 0.0;
    double radius = 0.0;

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getLon(), is(lon));
    assertThat(pointRadius.getLat(), is(lat));
    assertThat(pointRadius.getRadius(), is(radius));
  }

  @Test
  public void testPointRadiusWithNegativeCoordinates() {
    double lon = -120.5;
    double lat = -35.2;
    double radius = 500.0;

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getLon(), is(lon));
    assertThat(pointRadius.getLat(), is(lat));
    assertThat(pointRadius.getRadius(), is(radius));
  }

  @Test
  public void testPointRadiusAtEquator() {
    double lon = 0.0;
    double lat = 0.0;
    double radius = 1000.0;

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getLon(), is(0.0));
    assertThat(pointRadius.getLat(), is(0.0));
    assertThat(pointRadius.getRadius(), is(radius));
  }

  @Test
  public void testPointRadiusAtNorthPole() {
    double lon = 0.0;
    double lat = 90.0;
    double radius = 100.0;

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getLat(), is(90.0));
  }

  @Test
  public void testPointRadiusAtSouthPole() {
    double lon = 0.0;
    double lat = -90.0;
    double radius = 100.0;

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getLat(), is(-90.0));
  }

  @Test
  public void testPointRadiusAtDateLine() {
    double lon = 180.0;
    double lat = 0.0;
    double radius = 1000.0;

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getLon(), is(180.0));
  }

  @Test
  public void testPointRadiusWithLargeRadius() {
    double lon = 10.0;
    double lat = 20.0;
    double radius = 1000000.0; // 1000 km

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getRadius(), is(radius));
  }

  @Test
  public void testPointRadiusWithSmallRadius() {
    double lon = 10.0;
    double lat = 20.0;
    double radius = 0.001; // 1 millimeter

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getRadius(), is(radius));
  }

  @Test
  public void testPointRadiusEquality() {
    PointRadius pr1 = new PointRadius(10.0, 20.0, 100.0);
    PointRadius pr2 = new PointRadius(10.0, 20.0, 100.0);

    assertThat(pr1.getLon(), is(pr2.getLon()));
    assertThat(pr1.getLat(), is(pr2.getLat()));
    assertThat(pr1.getRadius(), is(pr2.getRadius()));
  }

  @Test
  public void testPointRadiusInequality() {
    PointRadius pr1 = new PointRadius(10.0, 20.0, 100.0);
    PointRadius pr2 = new PointRadius(10.0, 20.0, 200.0);

    assertThat(pr1.getRadius() == pr2.getRadius(), is(false));
  }

  @Test
  public void testPointRadiusWithHighPrecisionCoordinates() {
    double lon = -74.00597;
    double lat = 40.71278;
    double radius = 1234.5678;

    PointRadius pointRadius = new PointRadius(lon, lat, radius);

    assertThat(pointRadius.getLon(), is(lon));
    assertThat(pointRadius.getLat(), is(lat));
    assertThat(pointRadius.getRadius(), is(radius));
  }

  @Test
  public void testPointRadiusToString() {
    PointRadius pointRadius = new PointRadius(10.0, 20.0, 100.0);
    String str = pointRadius.toString();

    assertThat(str, is(notNullValue()));
    // ToString should contain coordinate and radius information
  }

  @Test
  public void testMultiplePointRadiusCreations() {
    PointRadius pr1 = new PointRadius(10.0, 20.0, 100.0);
    PointRadius pr2 = new PointRadius(30.0, 40.0, 200.0);
    PointRadius pr3 = new PointRadius(50.0, 60.0, 300.0);

    assertThat(pr1.getLon(), is(10.0));
    assertThat(pr2.getLon(), is(30.0));
    assertThat(pr3.getLon(), is(50.0));
  }

  @Test
  public void testPointRadiusAcrossDateLine() {
    // Point near date line
    PointRadius pr1 = new PointRadius(179.9, 0.0, 1000.0);
    PointRadius pr2 = new PointRadius(-179.9, 0.0, 1000.0);

    // These should be valid representations
    assertThat(pr1.getLon(), is(179.9));
    assertThat(pr2.getLon(), is(-179.9));
  }

  @Test
  public void testPointRadiusInMeters() {
    // Assuming radius is in meters
    double radiusInMeters = 5000.0;
    PointRadius pointRadius = new PointRadius(0.0, 0.0, radiusInMeters);

    assertThat(pointRadius.getRadius(), is(radiusInMeters));
  }
}
