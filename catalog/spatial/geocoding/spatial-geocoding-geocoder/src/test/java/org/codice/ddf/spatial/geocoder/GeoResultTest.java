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
package org.codice.ddf.spatial.geocoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GeoResultTest {

  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  private GeoResult geoResult;

  @BeforeEach
  public void setUp() {
    geoResult = new GeoResult();
  }

  @Test
  public void testSetAndGetPoint() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(-71.0595, 42.3577));

    geoResult.setPoint(point);

    assertThat(geoResult.getPoint(), is(notNullValue()));
    assertThat(geoResult.getPoint(), is(equalTo(point)));
    assertThat(geoResult.getPoint().getX(), is(equalTo(-71.0595)));
    assertThat(geoResult.getPoint().getY(), is(equalTo(42.3577)));
  }

  @Test
  public void testSetAndGetPointWithNull() {
    geoResult.setPoint(null);

    assertThat(geoResult.getPoint(), is(nullValue()));
  }

  @Test
  public void testSetAndGetBbox() {
    List<Point> bbox = new ArrayList<>();
    Point northWest = GEOMETRY_FACTORY.createPoint(new Coordinate(-71.1, 42.4));
    Point southEast = GEOMETRY_FACTORY.createPoint(new Coordinate(-71.0, 42.3));
    bbox.add(northWest);
    bbox.add(southEast);

    geoResult.setBbox(bbox);

    assertThat(geoResult.getBbox(), is(notNullValue()));
    assertThat(geoResult.getBbox().size(), is(equalTo(2)));
    assertThat(geoResult.getBbox(), is(equalTo(bbox)));
    assertThat(geoResult.getBbox().get(0).getX(), is(equalTo(-71.1)));
    assertThat(geoResult.getBbox().get(0).getY(), is(equalTo(42.4)));
    assertThat(geoResult.getBbox().get(1).getX(), is(equalTo(-71.0)));
    assertThat(geoResult.getBbox().get(1).getY(), is(equalTo(42.3)));
  }

  @Test
  public void testSetAndGetBboxWithEmptyList() {
    List<Point> emptyBbox = new ArrayList<>();

    geoResult.setBbox(emptyBbox);

    assertThat(geoResult.getBbox(), is(notNullValue()));
    assertThat(geoResult.getBbox().size(), is(equalTo(0)));
  }

  @Test
  public void testSetAndGetBboxWithNull() {
    geoResult.setBbox(null);

    assertThat(geoResult.getBbox(), is(nullValue()));
  }

  @Test
  public void testSetAndGetFullName() {
    String fullName = "Boston, MA, USA";

    geoResult.setFullName(fullName);

    assertThat(geoResult.getFullName(), is(notNullValue()));
    assertThat(geoResult.getFullName(), is(equalTo(fullName)));
  }

  @Test
  public void testSetAndGetFullNameWithEmptyString() {
    geoResult.setFullName("");

    assertThat(geoResult.getFullName(), is(notNullValue()));
    assertThat(geoResult.getFullName(), is(equalTo("")));
  }

  @Test
  public void testSetAndGetFullNameWithNull() {
    geoResult.setFullName(null);

    assertThat(geoResult.getFullName(), is(nullValue()));
  }

  @Test
  public void testGeoResultInitialState() {
    GeoResult newGeoResult = new GeoResult();

    assertThat(newGeoResult.getPoint(), is(nullValue()));
    assertThat(newGeoResult.getBbox(), is(nullValue()));
    assertThat(newGeoResult.getFullName(), is(nullValue()));
  }

  @Test
  public void testSetAllProperties() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(-122.4194, 37.7749));

    List<Point> bbox = new ArrayList<>();
    Point northWest = GEOMETRY_FACTORY.createPoint(new Coordinate(-122.5, 37.8));
    Point southEast = GEOMETRY_FACTORY.createPoint(new Coordinate(-122.35, 37.7));
    bbox.add(northWest);
    bbox.add(southEast);

    String fullName = "San Francisco, CA, USA";

    geoResult.setPoint(point);
    geoResult.setBbox(bbox);
    geoResult.setFullName(fullName);

    assertThat(geoResult.getPoint(), is(equalTo(point)));
    assertThat(geoResult.getBbox(), is(equalTo(bbox)));
    assertThat(geoResult.getFullName(), is(equalTo(fullName)));
  }

  @Test
  public void testBboxWithMultiplePoints() {
    List<Point> bbox = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(i * 10.0, i * 5.0));
      bbox.add(point);
    }

    geoResult.setBbox(bbox);

    assertThat(geoResult.getBbox().size(), is(equalTo(5)));
    for (int i = 0; i < 5; i++) {
      assertThat(geoResult.getBbox().get(i).getX(), is(equalTo(i * 10.0)));
      assertThat(geoResult.getBbox().get(i).getY(), is(equalTo(i * 5.0)));
    }
  }

  @Test
  public void testPointWithExtremeCoordinates() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(-180.0, -90.0));

    geoResult.setPoint(point);

    assertThat(geoResult.getPoint().getX(), is(equalTo(-180.0)));
    assertThat(geoResult.getPoint().getY(), is(equalTo(-90.0)));
  }

  @Test
  public void testPointWithMaximumCoordinates() {
    Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(180.0, 90.0));

    geoResult.setPoint(point);

    assertThat(geoResult.getPoint().getX(), is(equalTo(180.0)));
    assertThat(geoResult.getPoint().getY(), is(equalTo(90.0)));
  }

  @Test
  public void testFullNameWithSpecialCharacters() {
    String fullName = "São Paulo, Brasil - Açores (Îles)";

    geoResult.setFullName(fullName);

    assertThat(geoResult.getFullName(), is(equalTo(fullName)));
  }

  @Test
  public void testFullNameWithLongString() {
    StringBuilder longName = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longName.append("Very Long Location Name ");
    }

    geoResult.setFullName(longName.toString());

    assertThat(geoResult.getFullName(), is(equalTo(longName.toString())));
  }
}
