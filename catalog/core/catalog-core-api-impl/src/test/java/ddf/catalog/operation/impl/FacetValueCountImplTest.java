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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link FacetValueCountImpl} class.
 *
 * <p>Tests constructor and getter methods for facet value counts.
 */
@ExtendWith(MockitoExtension.class)
public class FacetValueCountImplTest {

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with valid value and count.
   *
   * <p>Verifies basic construction.
   */
  @Test
  public void testConstructorWithValueAndCount() {
    String value = "category1";
    long count = 42L;

    FacetValueCountImpl facetValueCount = new FacetValueCountImpl(value, count);

    assertThat(facetValueCount, is(notNullValue()));
    assertThat(facetValueCount.getValue(), is(equalTo(value)));
    assertThat(facetValueCount.getCount(), is(equalTo(count)));
  }

  /**
   * Tests constructor with null value.
   *
   * <p>Verifies null value is accepted.
   */
  @Test
  public void testConstructorWithNullValue() {
    FacetValueCountImpl facetValueCount = new FacetValueCountImpl(null, 10L);

    assertThat(facetValueCount.getValue(), is(nullValue()));
    assertThat(facetValueCount.getCount(), is(equalTo(10L)));
  }

  /**
   * Tests constructor with zero count.
   *
   * <p>Verifies zero count is accepted.
   */
  @Test
  public void testConstructorWithZeroCount() {
    FacetValueCountImpl facetValueCount = new FacetValueCountImpl("value", 0L);

    assertThat(facetValueCount.getValue(), is(equalTo("value")));
    assertThat(facetValueCount.getCount(), is(equalTo(0L)));
  }

  /**
   * Tests constructor with negative count.
   *
   * <p>Verifies negative count is accepted (though unusual).
   */
  @Test
  public void testConstructorWithNegativeCount() {
    FacetValueCountImpl facetValueCount = new FacetValueCountImpl("value", -5L);

    assertThat(facetValueCount.getCount(), is(equalTo(-5L)));
  }

  /**
   * Tests constructor with empty string value.
   *
   * <p>Verifies empty string is accepted.
   */
  @Test
  public void testConstructorWithEmptyStringValue() {
    FacetValueCountImpl facetValueCount = new FacetValueCountImpl("", 5L);

    assertThat(facetValueCount.getValue(), is(equalTo("")));
  }

  /**
   * Tests constructor with large count.
   *
   * <p>Verifies large count values are handled.
   */
  @Test
  public void testConstructorWithLargeCount() {
    long largeCount = Long.MAX_VALUE;
    FacetValueCountImpl facetValueCount = new FacetValueCountImpl("value", largeCount);

    assertThat(facetValueCount.getCount(), is(equalTo(largeCount)));
  }

  // ====================
  // Getter Tests
  // ====================

  /**
   * Tests getValue returns correct value.
   *
   * <p>Verifies value retrieval.
   */
  @Test
  public void testGetValue() {
    String value = "document-type";
    FacetValueCountImpl facetValueCount = new FacetValueCountImpl(value, 100L);

    assertThat(facetValueCount.getValue(), is(equalTo(value)));
  }

  /**
   * Tests getCount returns correct count.
   *
   * <p>Verifies count retrieval.
   */
  @Test
  public void testGetCount() {
    long count = 999L;
    FacetValueCountImpl facetValueCount = new FacetValueCountImpl("type", count);

    assertThat(facetValueCount.getCount(), is(equalTo(count)));
  }

  /**
   * Tests getValue with various string formats.
   *
   * <p>Verifies different value formats.
   */
  @Test
  public void testGetValueWithVariousFormats() {
    FacetValueCountImpl fvc1 = new FacetValueCountImpl("simple", 1L);
    assertThat(fvc1.getValue(), is(equalTo("simple")));

    FacetValueCountImpl fvc2 = new FacetValueCountImpl("with spaces", 2L);
    assertThat(fvc2.getValue(), is(equalTo("with spaces")));

    FacetValueCountImpl fvc3 = new FacetValueCountImpl("with-dashes-and_underscores", 3L);
    assertThat(fvc3.getValue(), is(equalTo("with-dashes-and_underscores")));

    FacetValueCountImpl fvc4 = new FacetValueCountImpl("123numeric", 4L);
    assertThat(fvc4.getValue(), is(equalTo("123numeric")));
  }

  /**
   * Tests getCount with various count values.
   *
   * <p>Verifies different count ranges.
   */
  @Test
  public void testGetCountWithVariousValues() {
    FacetValueCountImpl fvc1 = new FacetValueCountImpl("val1", 1L);
    assertThat(fvc1.getCount(), is(equalTo(1L)));

    FacetValueCountImpl fvc2 = new FacetValueCountImpl("val2", 1000L);
    assertThat(fvc2.getCount(), is(equalTo(1000L)));

    FacetValueCountImpl fvc3 = new FacetValueCountImpl("val3", 1000000L);
    assertThat(fvc3.getCount(), is(equalTo(1000000L)));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests facet value count for category facet.
   *
   * <p>Simulates a real category facet result.
   */
  @Test
  public void testCategoryFacetValueCount() {
    FacetValueCountImpl imageFacet = new FacetValueCountImpl("image", 250L);
    FacetValueCountImpl videoFacet = new FacetValueCountImpl("video", 100L);
    FacetValueCountImpl documentFacet = new FacetValueCountImpl("document", 450L);

    assertThat(imageFacet.getValue(), is(equalTo("image")));
    assertThat(imageFacet.getCount(), is(equalTo(250L)));

    assertThat(videoFacet.getValue(), is(equalTo("video")));
    assertThat(videoFacet.getCount(), is(equalTo(100L)));

    assertThat(documentFacet.getValue(), is(equalTo("document")));
    assertThat(documentFacet.getCount(), is(equalTo(450L)));
  }

  /**
   * Tests facet value count for source facet.
   *
   * <p>Simulates source distribution facet result.
   */
  @Test
  public void testSourceFacetValueCount() {
    FacetValueCountImpl localSource = new FacetValueCountImpl("local-catalog", 5000L);
    FacetValueCountImpl remoteSource1 = new FacetValueCountImpl("remote-source-1", 2000L);
    FacetValueCountImpl remoteSource2 = new FacetValueCountImpl("remote-source-2", 1500L);

    assertThat(localSource.getCount(), is(equalTo(5000L)));
    assertThat(remoteSource1.getCount(), is(equalTo(2000L)));
    assertThat(remoteSource2.getCount(), is(equalTo(1500L)));
  }

  /**
   * Tests facet value count with special characters.
   *
   * <p>Verifies handling of special characters in values.
   */
  @Test
  public void testFacetValueCountWithSpecialCharacters() {
    FacetValueCountImpl fvc1 = new FacetValueCountImpl("value/with/slashes", 10L);
    assertThat(fvc1.getValue(), is(equalTo("value/with/slashes")));

    FacetValueCountImpl fvc2 = new FacetValueCountImpl("value.with.dots", 20L);
    assertThat(fvc2.getValue(), is(equalTo("value.with.dots")));

    FacetValueCountImpl fvc3 = new FacetValueCountImpl("value:with:colons", 30L);
    assertThat(fvc3.getValue(), is(equalTo("value:with:colons")));
  }

  /**
   * Tests creating multiple facet value counts.
   *
   * <p>Simulates a complete facet result set.
   */
  @Test
  public void testMultipleFacetValueCounts() {
    FacetValueCountImpl fvc1 = new FacetValueCountImpl("value1", 100L);
    FacetValueCountImpl fvc2 = new FacetValueCountImpl("value2", 200L);
    FacetValueCountImpl fvc3 = new FacetValueCountImpl("value3", 150L);

    assertThat(fvc1.getValue(), is(equalTo("value1")));
    assertThat(fvc1.getCount(), is(equalTo(100L)));

    assertThat(fvc2.getValue(), is(equalTo("value2")));
    assertThat(fvc2.getCount(), is(equalTo(200L)));

    assertThat(fvc3.getValue(), is(equalTo("value3")));
    assertThat(fvc3.getCount(), is(equalTo(150L)));
  }
}
