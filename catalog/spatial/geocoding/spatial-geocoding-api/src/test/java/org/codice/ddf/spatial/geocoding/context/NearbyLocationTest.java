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
package org.codice.ddf.spatial.geocoding.context;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/** Unit tests for NearbyLocation interface and its implementation. */
public class NearbyLocationTest {

  @Test
  public void testNearbyLocationImplementation() {
    NearbyLocation location = createTestNearbyLocation("Phoenix", "NE", 25.5);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is("Phoenix"));
    assertThat(location.getCardinalDirection(), is("NE"));
    assertThat(location.getDistance(), is(25.5));
  }

  @Test
  public void testNearbyLocationWithNullName() {
    NearbyLocation location = createTestNearbyLocation(null, "SW", 10.0);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is((String) null));
    assertThat(location.getCardinalDirection(), is("SW"));
    assertThat(location.getDistance(), is(10.0));
  }

  @Test
  public void testNearbyLocationWithNullCardinalDirection() {
    NearbyLocation location = createTestNearbyLocation("Denver", null, 15.3);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is("Denver"));
    assertThat(location.getCardinalDirection(), is((String) null));
    assertThat(location.getDistance(), is(15.3));
  }

  @Test
  public void testNearbyLocationWithZeroDistance() {
    NearbyLocation location = createTestNearbyLocation("Boston", "N", 0.0);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is("Boston"));
    assertThat(location.getCardinalDirection(), is("N"));
    assertThat(location.getDistance(), is(0.0));
  }

  @Test
  public void testNearbyLocationWithNegativeDistance() {
    NearbyLocation location = createTestNearbyLocation("Seattle", "W", -5.0);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is("Seattle"));
    assertThat(location.getCardinalDirection(), is("W"));
    assertThat(location.getDistance(), is(-5.0));
  }

  @Test
  public void testNearbyLocationWithLargeDistance() {
    NearbyLocation location = createTestNearbyLocation("Tokyo", "E", 10000.5);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is("Tokyo"));
    assertThat(location.getCardinalDirection(), is("E"));
    assertThat(location.getDistance(), is(10000.5));
  }

  @Test
  public void testNearbyLocationWithAllCardinalDirections() {
    String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    for (String direction : directions) {
      NearbyLocation location = createTestNearbyLocation("City", direction, 50.0);
      assertThat(location.getCardinalDirection(), is(direction));
    }
  }

  @Test
  public void testNearbyLocationWithSpecialCharactersInName() {
    String specialName = "São José dos Campos";
    NearbyLocation location = createTestNearbyLocation(specialName, "S", 12.34);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is(specialName));
    assertThat(location.getCardinalDirection(), is("S"));
    assertThat(location.getDistance(), is(12.34));
  }

  @Test
  public void testNearbyLocationWithEmptyName() {
    NearbyLocation location = createTestNearbyLocation("", "NW", 7.89);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is(""));
    assertThat(location.getCardinalDirection(), is("NW"));
    assertThat(location.getDistance(), is(7.89));
  }

  @Test
  public void testMultipleNearbyLocationsIndependence() {
    NearbyLocation location1 = createTestNearbyLocation("Chicago", "N", 100.0);
    NearbyLocation location2 = createTestNearbyLocation("Miami", "S", 200.0);

    assertThat(location1.getName(), is("Chicago"));
    assertThat(location1.getCardinalDirection(), is("N"));
    assertThat(location1.getDistance(), is(100.0));

    assertThat(location2.getName(), is("Miami"));
    assertThat(location2.getCardinalDirection(), is("S"));
    assertThat(location2.getDistance(), is(200.0));
  }

  @Test
  public void testNearbyLocationWithVerySmallDistance() {
    NearbyLocation location = createTestNearbyLocation("Los Angeles", "W", 0.001);

    assertThat(location, is(notNullValue()));
    assertThat(location.getName(), is("Los Angeles"));
    assertThat(location.getCardinalDirection(), is("W"));
    assertThat(location.getDistance(), is(0.001));
  }

  private NearbyLocation createTestNearbyLocation(
      final String name, final String cardinalDirection, final double distance) {
    return new NearbyLocation() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public String getCardinalDirection() {
        return cardinalDirection;
      }

      @Override
      public double getDistance() {
        return distance;
      }
    };
  }
}
