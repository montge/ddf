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
package org.codice.ddf.opensearch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.types.Core;
import org.junit.jupiter.api.Test;

class OpenSearchConstantsTest {

  @Test
  void testSearchTermsConstant() {
    assertThat(OpenSearchConstants.SEARCH_TERMS, is("q"));
  }

  @Test
  void testSearchTermsDelimiter() {
    assertThat(OpenSearchConstants.SEARCH_TERMS_DELIMITER, is(" "));
  }

  @Test
  void testSupportedTemporalSearchTerm() {
    assertThat(OpenSearchConstants.SUPPORTED_TEMPORAL_SEARCH_TERM, is(Core.MODIFIED));
  }

  @Test
  void testDateStartConstant() {
    assertThat(OpenSearchConstants.DATE_START, is("dtstart"));
  }

  @Test
  void testDateEndConstant() {
    assertThat(OpenSearchConstants.DATE_END, is("dtend"));
  }

  @Test
  void testDateOffsetConstant() {
    assertThat(OpenSearchConstants.DATE_OFFSET, is("dtoffset"));
  }

  @Test
  void testSupportedSpatialSearchTerm() {
    assertThat(OpenSearchConstants.SUPPORTED_SPATIAL_SEARCH_TERM, is(Metacard.ANY_GEO));
  }

  @Test
  void testBboxConstant() {
    assertThat(OpenSearchConstants.BBOX, is("bbox"));
  }

  @Test
  void testBboxDelimiter() {
    assertThat(OpenSearchConstants.BBOX_DELIMITER, is(","));
  }

  @Test
  void testPolygonConstant() {
    assertThat(OpenSearchConstants.POLYGON, is("polygon"));
  }

  @Test
  void testPolygonLonLatDelimiter() {
    assertThat(OpenSearchConstants.POLYGON_LON_LAT_DELIMITER, is(","));
  }

  @Test
  void testGeometryConstant() {
    assertThat(OpenSearchConstants.GEOMETRY, is("geometry"));
  }

  @Test
  void testLatConstant() {
    assertThat(OpenSearchConstants.LAT, is("lat"));
  }

  @Test
  void testLonConstant() {
    assertThat(OpenSearchConstants.LON, is("lon"));
  }

  @Test
  void testRadiusConstant() {
    assertThat(OpenSearchConstants.RADIUS, is("radius"));
  }

  @Test
  void testMaxResultsConstant() {
    assertThat(OpenSearchConstants.MAX_RESULTS, is("mr"));
  }

  @Test
  void testSourcesConstant() {
    assertThat(OpenSearchConstants.SOURCES, is("src"));
  }

  @Test
  void testSourcesDelimiter() {
    assertThat(OpenSearchConstants.SOURCES_DELIMITER, is(","));
  }

  @Test
  void testLocalSourceConstant() {
    assertThat(OpenSearchConstants.LOCAL_SOURCE, is("local"));
  }

  @Test
  void testMaxTimeoutConstant() {
    assertThat(OpenSearchConstants.MAX_TIMEOUT, is("mt"));
  }

  @Test
  void testStartIndexConstant() {
    assertThat(OpenSearchConstants.START_INDEX, is("start"));
  }

  @Test
  void testCountConstant() {
    assertThat(OpenSearchConstants.COUNT, is("count"));
  }

  @Test
  void testTypeConstant() {
    assertThat(OpenSearchConstants.TYPE, is("type"));
  }

  @Test
  void testVersionsConstant() {
    assertThat(OpenSearchConstants.VERSIONS, is("version"));
  }

  @Test
  void testVersionsDelimiter() {
    assertThat(OpenSearchConstants.VERSIONS_DELIMITER, is(","));
  }

  @Test
  void testSelectorsConstant() {
    assertThat(OpenSearchConstants.SELECTORS, is("selector"));
  }

  @Test
  void testSelectorsDelimiter() {
    assertThat(OpenSearchConstants.SELECTORS_DELIMITER, is(","));
  }

  @Test
  void testSortConstant() {
    assertThat(OpenSearchConstants.SORT, is("sort"));
  }

  @Test
  void testOrderAscendingConstant() {
    assertThat(OpenSearchConstants.ORDER_ASCENDING, is("asc"));
  }

  @Test
  void testOrderDescendingConstant() {
    assertThat(OpenSearchConstants.ORDER_DESCENDING, is("desc"));
  }

  @Test
  void testSortDelimiter() {
    assertThat(OpenSearchConstants.SORT_DELIMITER, is(":"));
  }

  @Test
  void testSortRelevanceConstant() {
    assertThat(OpenSearchConstants.SORT_RELEVANCE, is("relevance"));
  }

  @Test
  void testSortTemporalConstant() {
    assertThat(OpenSearchConstants.SORT_TEMPORAL, is("date"));
  }

  @Test
  void testFormatConstant() {
    assertThat(OpenSearchConstants.FORMAT, is("format"));
  }

  @Test
  void testAllConstantsAreNotNull() {
    assertThat(OpenSearchConstants.SEARCH_TERMS, is(notNullValue()));
    assertThat(OpenSearchConstants.SEARCH_TERMS_DELIMITER, is(notNullValue()));
    assertThat(OpenSearchConstants.SUPPORTED_TEMPORAL_SEARCH_TERM, is(notNullValue()));
    assertThat(OpenSearchConstants.DATE_START, is(notNullValue()));
    assertThat(OpenSearchConstants.DATE_END, is(notNullValue()));
    assertThat(OpenSearchConstants.DATE_OFFSET, is(notNullValue()));
    assertThat(OpenSearchConstants.SUPPORTED_SPATIAL_SEARCH_TERM, is(notNullValue()));
    assertThat(OpenSearchConstants.BBOX, is(notNullValue()));
    assertThat(OpenSearchConstants.BBOX_DELIMITER, is(notNullValue()));
    assertThat(OpenSearchConstants.POLYGON, is(notNullValue()));
    assertThat(OpenSearchConstants.POLYGON_LON_LAT_DELIMITER, is(notNullValue()));
    assertThat(OpenSearchConstants.GEOMETRY, is(notNullValue()));
    assertThat(OpenSearchConstants.LAT, is(notNullValue()));
    assertThat(OpenSearchConstants.LON, is(notNullValue()));
    assertThat(OpenSearchConstants.RADIUS, is(notNullValue()));
    assertThat(OpenSearchConstants.MAX_RESULTS, is(notNullValue()));
    assertThat(OpenSearchConstants.SOURCES, is(notNullValue()));
    assertThat(OpenSearchConstants.SOURCES_DELIMITER, is(notNullValue()));
    assertThat(OpenSearchConstants.LOCAL_SOURCE, is(notNullValue()));
    assertThat(OpenSearchConstants.MAX_TIMEOUT, is(notNullValue()));
    assertThat(OpenSearchConstants.START_INDEX, is(notNullValue()));
    assertThat(OpenSearchConstants.COUNT, is(notNullValue()));
    assertThat(OpenSearchConstants.TYPE, is(notNullValue()));
    assertThat(OpenSearchConstants.VERSIONS, is(notNullValue()));
    assertThat(OpenSearchConstants.VERSIONS_DELIMITER, is(notNullValue()));
    assertThat(OpenSearchConstants.SELECTORS, is(notNullValue()));
    assertThat(OpenSearchConstants.SELECTORS_DELIMITER, is(notNullValue()));
    assertThat(OpenSearchConstants.SORT, is(notNullValue()));
    assertThat(OpenSearchConstants.ORDER_ASCENDING, is(notNullValue()));
    assertThat(OpenSearchConstants.ORDER_DESCENDING, is(notNullValue()));
    assertThat(OpenSearchConstants.SORT_DELIMITER, is(notNullValue()));
    assertThat(OpenSearchConstants.SORT_RELEVANCE, is(notNullValue()));
    assertThat(OpenSearchConstants.SORT_TEMPORAL, is(notNullValue()));
    assertThat(OpenSearchConstants.FORMAT, is(notNullValue()));
  }
}
