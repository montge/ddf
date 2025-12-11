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
package ddf.catalog.data.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link ResultImpl} class.
 *
 * <p>Tests all constructors, getters, setters, and edge cases for the Result implementation.
 */
@ExtendWith(MockitoExtension.class)
public class ResultImplTest {

  @Mock private Metacard mockMetacard;

  private ResultImpl result;

  @BeforeEach
  public void setUp() {
    result = new ResultImpl();
  }

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests the default constructor creates a valid ResultImpl instance.
   *
   * <p>Verifies that a newly created Result has null values for all fields.
   */
  @Test
  public void testDefaultConstructor() {
    ResultImpl defaultResult = new ResultImpl();

    assertThat(defaultResult, is(notNullValue()));
    assertThat(defaultResult.getMetacard(), is(nullValue()));
    assertThat(defaultResult.getDistanceInMeters(), is(nullValue()));
    assertThat(defaultResult.getRelevanceScore(), is(nullValue()));
  }

  /**
   * Tests the constructor with a Metacard parameter.
   *
   * <p>Verifies that the Metacard is properly set while other fields remain null.
   */
  @Test
  public void testConstructorWithMetacard() {
    ResultImpl resultWithMetacard = new ResultImpl(mockMetacard);

    assertThat(resultWithMetacard, is(notNullValue()));
    assertThat(resultWithMetacard.getMetacard(), is(equalTo(mockMetacard)));
    assertThat(resultWithMetacard.getDistanceInMeters(), is(nullValue()));
    assertThat(resultWithMetacard.getRelevanceScore(), is(nullValue()));
  }

  /**
   * Tests constructor with null Metacard.
   *
   * <p>Verifies that the constructor accepts null without throwing exceptions.
   */
  @Test
  public void testConstructorWithNullMetacard() {
    ResultImpl resultWithNull = new ResultImpl(null);

    assertThat(resultWithNull, is(notNullValue()));
    assertThat(resultWithNull.getMetacard(), is(nullValue()));
  }

  // ====================
  // Metacard Tests
  // ====================

  /**
   * Tests setting and getting the Metacard.
   *
   * <p>Verifies that the Metacard can be set and retrieved correctly.
   */
  @Test
  public void testSetAndGetMetacard() {
    result.setMetacard(mockMetacard);

    assertThat(result.getMetacard(), is(equalTo(mockMetacard)));
  }

  /**
   * Tests setting Metacard to null.
   *
   * <p>Verifies that the Metacard can be set to null without issues.
   */
  @Test
  public void testSetMetacardToNull() {
    result.setMetacard(mockMetacard);
    result.setMetacard(null);

    assertThat(result.getMetacard(), is(nullValue()));
  }

  /**
   * Tests replacing an existing Metacard.
   *
   * <p>Verifies that setting a new Metacard replaces the previous one.
   */
  @Test
  public void testReplaceMetacard() {
    Metacard firstMetacard = mock(Metacard.class);
    Metacard secondMetacard = mock(Metacard.class);

    result.setMetacard(firstMetacard);
    assertThat(result.getMetacard(), is(equalTo(firstMetacard)));

    result.setMetacard(secondMetacard);
    assertThat(result.getMetacard(), is(equalTo(secondMetacard)));
  }

  // ====================
  // Distance Tests
  // ====================

  /**
   * Tests setting and getting distance in meters.
   *
   * <p>Verifies that a valid distance value can be set and retrieved.
   */
  @Test
  public void testSetAndGetDistanceInMeters() {
    Double distance = 1000.5;
    result.setDistanceInMeters(distance);

    assertThat(result.getDistanceInMeters(), is(equalTo(distance)));
  }

  /**
   * Tests setting distance to zero.
   *
   * <p>Verifies that zero distance is a valid value.
   */
  @Test
  public void testSetDistanceToZero() {
    result.setDistanceInMeters(0.0);

    assertThat(result.getDistanceInMeters(), is(equalTo(0.0)));
  }

  /**
   * Tests setting distance to null.
   *
   * <p>Verifies that distance can be set to null (unknown distance).
   */
  @Test
  public void testSetDistanceToNull() {
    result.setDistanceInMeters(100.0);
    result.setDistanceInMeters(null);

    assertThat(result.getDistanceInMeters(), is(nullValue()));
  }

  /**
   * Tests setting negative distance.
   *
   * <p>Verifies that negative distances are accepted (may represent invalid/unknown values).
   */
  @Test
  public void testSetNegativeDistance() {
    result.setDistanceInMeters(-1.0);

    assertThat(result.getDistanceInMeters(), is(equalTo(-1.0)));
  }

  /**
   * Tests setting very large distance.
   *
   * <p>Verifies that very large distance values (e.g., circumference of Earth) are handled.
   */
  @Test
  public void testSetVeryLargeDistance() {
    Double earthCircumference = 40075000.0; // meters
    result.setDistanceInMeters(earthCircumference);

    assertThat(result.getDistanceInMeters(), is(equalTo(earthCircumference)));
  }

  /**
   * Tests setting very small distance.
   *
   * <p>Verifies that very small distance values (millimeters) are handled.
   */
  @Test
  public void testSetVerySmallDistance() {
    Double tinyDistance = 0.001; // 1 millimeter
    result.setDistanceInMeters(tinyDistance);

    assertThat(result.getDistanceInMeters(), is(equalTo(tinyDistance)));
  }

  /**
   * Tests distance with maximum Double value.
   *
   * <p>Verifies that extreme distance values are handled correctly.
   */
  @Test
  public void testSetMaximumDistance() {
    result.setDistanceInMeters(Double.MAX_VALUE);

    assertThat(result.getDistanceInMeters(), is(equalTo(Double.MAX_VALUE)));
  }

  /**
   * Tests distance with infinity.
   *
   * <p>Verifies that infinite distance values are handled.
   */
  @Test
  public void testSetInfiniteDistance() {
    result.setDistanceInMeters(Double.POSITIVE_INFINITY);

    assertThat(result.getDistanceInMeters(), is(equalTo(Double.POSITIVE_INFINITY)));
  }

  // ====================
  // Relevance Score Tests
  // ====================

  /**
   * Tests setting and getting relevance score.
   *
   * <p>Verifies that a valid relevance score can be set and retrieved.
   */
  @Test
  public void testSetAndGetRelevanceScore() {
    Double score = 0.85;
    result.setRelevanceScore(score);

    assertThat(result.getRelevanceScore(), is(equalTo(score)));
  }

  /**
   * Tests setting relevance score to zero.
   *
   * <p>Verifies that zero relevance score is valid (no relevance).
   */
  @Test
  public void testSetRelevanceScoreToZero() {
    result.setRelevanceScore(0.0);

    assertThat(result.getRelevanceScore(), is(equalTo(0.0)));
  }

  /**
   * Tests setting relevance score to one.
   *
   * <p>Verifies that maximum relevance score (1.0) is valid.
   */
  @Test
  public void testSetRelevanceScoreToOne() {
    result.setRelevanceScore(1.0);

    assertThat(result.getRelevanceScore(), is(equalTo(1.0)));
  }

  /**
   * Tests setting relevance score to null.
   *
   * <p>Verifies that relevance score can be set to null (unknown relevance).
   */
  @Test
  public void testSetRelevanceScoreToNull() {
    result.setRelevanceScore(0.5);
    result.setRelevanceScore(null);

    assertThat(result.getRelevanceScore(), is(nullValue()));
  }

  /**
   * Tests setting negative relevance score.
   *
   * <p>Verifies that negative relevance scores are accepted (though typically invalid).
   */
  @Test
  public void testSetNegativeRelevanceScore() {
    result.setRelevanceScore(-0.5);

    assertThat(result.getRelevanceScore(), is(equalTo(-0.5)));
  }

  /**
   * Tests setting relevance score greater than one.
   *
   * <p>Verifies that scores greater than 1.0 are accepted (though typically invalid).
   */
  @Test
  public void testSetRelevanceScoreGreaterThanOne() {
    result.setRelevanceScore(1.5);

    assertThat(result.getRelevanceScore(), is(equalTo(1.5)));
  }

  /**
   * Tests setting very precise relevance score.
   *
   * <p>Verifies that high-precision floating point scores are handled correctly.
   */
  @Test
  public void testSetPreciseRelevanceScore() {
    Double preciseScore = 0.123456789;
    result.setRelevanceScore(preciseScore);

    assertThat(result.getRelevanceScore(), is(equalTo(preciseScore)));
  }

  // ====================
  // Combined State Tests
  // ====================

  /**
   * Tests setting all fields together.
   *
   * <p>Verifies that all Result fields can be set and retrieved independently.
   */
  @Test
  public void testSetAllFields() {
    Double distance = 500.0;
    Double score = 0.9;

    result.setMetacard(mockMetacard);
    result.setDistanceInMeters(distance);
    result.setRelevanceScore(score);

    assertThat(result.getMetacard(), is(equalTo(mockMetacard)));
    assertThat(result.getDistanceInMeters(), is(equalTo(distance)));
    assertThat(result.getRelevanceScore(), is(equalTo(score)));
  }

  /**
   * Tests modifying all fields multiple times.
   *
   * <p>Verifies that fields can be changed multiple times without side effects.
   */
  @Test
  public void testMultipleModifications() {
    result.setMetacard(mockMetacard);
    result.setDistanceInMeters(100.0);
    result.setRelevanceScore(0.5);

    result.setDistanceInMeters(200.0);
    result.setRelevanceScore(0.8);

    assertThat(result.getDistanceInMeters(), is(equalTo(200.0)));
    assertThat(result.getRelevanceScore(), is(equalTo(0.8)));
    assertThat(result.getMetacard(), is(equalTo(mockMetacard)));
  }

  // ====================
  // ToString Test
  // ====================

  /**
   * Tests toString method returns non-null value.
   *
   * <p>Verifies that the toString method produces a valid string representation.
   */
  @Test
  public void testToString() {
    result.setMetacard(mockMetacard);
    result.setDistanceInMeters(100.0);
    result.setRelevanceScore(0.75);

    String stringValue = result.toString();

    assertThat(stringValue, is(notNullValue()));
  }

  /**
   * Tests toString with null fields.
   *
   * <p>Verifies that toString handles null fields gracefully.
   */
  @Test
  public void testToStringWithNullFields() {
    String stringValue = result.toString();

    assertThat(stringValue, is(notNullValue()));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests creating a complete Result for a distance-based query.
   *
   * <p>Simulates a real-world scenario where a Result contains distance information.
   */
  @Test
  public void testDistanceQueryResult() {
    when(mockMetacard.getId()).thenReturn("test-id-1");

    ResultImpl distanceResult = new ResultImpl(mockMetacard);
    distanceResult.setDistanceInMeters(1250.5);
    distanceResult.setRelevanceScore(null); // Distance queries may not have relevance

    assertThat(distanceResult.getMetacard().getId(), is(equalTo("test-id-1")));
    assertThat(distanceResult.getDistanceInMeters(), is(equalTo(1250.5)));
    assertThat(distanceResult.getRelevanceScore(), is(nullValue()));
  }

  /**
   * Tests creating a complete Result for a text-based relevance query.
   *
   * <p>Simulates a real-world scenario where a Result contains relevance score.
   */
  @Test
  public void testRelevanceQueryResult() {
    when(mockMetacard.getId()).thenReturn("test-id-2");

    ResultImpl relevanceResult = new ResultImpl(mockMetacard);
    relevanceResult.setRelevanceScore(0.92);
    relevanceResult.setDistanceInMeters(null); // Text queries may not have distance

    assertThat(relevanceResult.getMetacard().getId(), is(equalTo("test-id-2")));
    assertThat(relevanceResult.getRelevanceScore(), is(equalTo(0.92)));
    assertThat(relevanceResult.getDistanceInMeters(), is(nullValue()));
  }

  /**
   * Tests creating a Result with both distance and relevance.
   *
   * <p>Simulates a hybrid query that combines spatial and textual ranking.
   */
  @Test
  public void testHybridQueryResult() {
    when(mockMetacard.getId()).thenReturn("test-id-3");

    ResultImpl hybridResult = new ResultImpl(mockMetacard);
    hybridResult.setDistanceInMeters(3500.0);
    hybridResult.setRelevanceScore(0.78);

    assertThat(hybridResult.getMetacard().getId(), is(equalTo("test-id-3")));
    assertThat(hybridResult.getDistanceInMeters(), is(equalTo(3500.0)));
    assertThat(hybridResult.getRelevanceScore(), is(equalTo(0.78)));
  }

  /**
   * Tests Result interface compliance.
   *
   * <p>Verifies that ResultImpl properly implements the Result interface.
   */
  @Test
  public void testResultInterfaceCompliance() {
    Result resultInterface = new ResultImpl(mockMetacard);

    assertThat(resultInterface, is(notNullValue()));
    assertThat(resultInterface.getMetacard(), is(equalTo(mockMetacard)));
  }
}
