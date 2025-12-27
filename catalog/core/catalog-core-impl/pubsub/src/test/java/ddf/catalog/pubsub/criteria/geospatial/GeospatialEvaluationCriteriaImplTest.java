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
package ddf.catalog.pubsub.criteria.geospatial;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;

class GeospatialEvaluationCriteriaImplTest {

  private static final String POINT_WKT = "POINT (0 0)";
  private static final String POLYGON_WKT = "POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0))";
  private static final double DELTA = 0.0001;

  private GeometryFactory geometryFactory;
  private Geometry criteriaGeometry;
  private Geometry inputGeometry;

  @BeforeEach
  void setUp() {
    geometryFactory = new GeometryFactory();
    criteriaGeometry = geometryFactory.createPoint(new Coordinate(0, 0));
    inputGeometry = geometryFactory.createPoint(new Coordinate(1, 1));
  }

  @Test
  void testConstructorWithGeometries() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "INTERSECTS", inputGeometry, 100.0);

    assertThat(criteria, is(notNullValue()));
  }

  @Test
  void testImplementsInterface() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "INTERSECTS", inputGeometry, 100.0);

    assertThat(criteria, instanceOf(GeospatialEvaluationCriteria.class));
  }

  @Test
  void testGetCriteria() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "INTERSECTS", inputGeometry, 100.0);

    assertThat(criteria.getCriteria(), is(sameInstance(criteriaGeometry)));
  }

  @Test
  void testGetInput() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "INTERSECTS", inputGeometry, 100.0);

    assertThat(criteria.getInput(), is(sameInstance(inputGeometry)));
  }

  @Test
  void testGetOperation() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "WITHIN", inputGeometry, 50.0);

    assertThat(criteria.getOperation(), is("WITHIN"));
  }

  @Test
  void testGetDistance() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "INTERSECTS", inputGeometry, 123.45);

    assertThat(criteria.getDistance(), is(closeTo(123.45, DELTA)));
  }

  @Test
  void testConstructorWithWktStringAndDistance() throws ParseException {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "CONTAINS", POINT_WKT, 200.0);

    assertThat(criteria, is(notNullValue()));
    assertThat(criteria.getInput(), is(notNullValue()));
    assertThat(criteria.getDistance(), is(closeTo(200.0, DELTA)));
  }

  @Test
  void testConstructorWithWktStringNoDistance() throws ParseException {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "DISJOINT", POLYGON_WKT);

    assertThat(criteria, is(notNullValue()));
    assertThat(criteria.getInput(), is(notNullValue()));
    assertThat(criteria.getOperation(), is("DISJOINT"));
  }

  @Test
  void testWktStringParsedCorrectly() throws ParseException {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "INTERSECTS", POINT_WKT, 0.0);

    Geometry parsedInput = criteria.getInput();
    assertThat(parsedInput.getGeometryType(), is("Point"));
  }

  @Test
  void testPolygonWktParsedCorrectly() throws ParseException {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "WITHIN", POLYGON_WKT);

    Geometry parsedInput = criteria.getInput();
    assertThat(parsedInput.getGeometryType(), is("Polygon"));
  }

  @Test
  void testInvalidWktThrowsParseException() {
    assertThrows(
        ParseException.class,
        () ->
            new GeospatialEvaluationCriteriaImpl(
                criteriaGeometry, "INTERSECTS", "INVALID WKT", 0.0));
  }

  @Test
  void testZeroDistance() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "EQUALS", inputGeometry, 0.0);

    assertThat(criteria.getDistance(), is(closeTo(0.0, DELTA)));
  }

  @Test
  void testNegativeDistance() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "INTERSECTS", inputGeometry, -50.0);

    assertThat(criteria.getDistance(), is(closeTo(-50.0, DELTA)));
  }

  @Test
  void testNullOperation() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, null, inputGeometry, 0.0);

    assertThat(criteria.getOperation(), is((String) null));
  }

  @Test
  void testDifferentOperations() {
    String[] operations = {"INTERSECTS", "WITHIN", "CONTAINS", "DISJOINT", "OVERLAPS"};

    for (String operation : operations) {
      GeospatialEvaluationCriteriaImpl criteria =
          new GeospatialEvaluationCriteriaImpl(criteriaGeometry, operation, inputGeometry, 0.0);
      assertThat(criteria.getOperation(), is(operation));
    }
  }

  @Test
  void testLargeDistanceValue() {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(
            criteriaGeometry, "INTERSECTS", inputGeometry, Double.MAX_VALUE);

    assertThat(criteria.getDistance(), is(Double.MAX_VALUE));
  }

  @Test
  void testDefaultDistanceWithThreeArgConstructor() throws ParseException {
    GeospatialEvaluationCriteriaImpl criteria =
        new GeospatialEvaluationCriteriaImpl(criteriaGeometry, "INTERSECTS", POINT_WKT);

    assertThat(criteria.getDistance(), is(closeTo(0.0, DELTA)));
  }
}
