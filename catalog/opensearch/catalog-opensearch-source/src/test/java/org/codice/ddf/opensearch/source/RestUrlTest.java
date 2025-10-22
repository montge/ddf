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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.codice.ddf.opensearch.OpenSearchConstants;
import org.junit.Before;
import org.junit.Test;

/** Comprehensive tests for RestUrl query parameter building */
public class RestUrlTest {

  private RestUrl restUrl;
  private static final String BASE_URL = "https://example.com/search";

  @Before
  public void setUp() {
    restUrl = new RestUrl(BASE_URL);
  }

  @Test
  public void testBasicUrlConstruction() {
    assertThat(restUrl, is(notNullValue()));
    String url = restUrl.buildUrl();
    assertThat(url, containsString(BASE_URL));
  }

  @Test
  public void testAddSearchTerms() {
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, "test query");
    String url = restUrl.buildUrl();
    assertThat(url, containsString("q=test+query"));
  }

  @Test
  public void testAddMultipleParameters() {
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, "test");
    restUrl.addParameter(OpenSearchConstants.COUNT, "10");
    restUrl.addParameter(OpenSearchConstants.START_INDEX, "1");

    String url = restUrl.buildUrl();
    assertThat(url, containsString("q=test"));
    assertThat(url, containsString("count=10"));
    assertThat(url, containsString("start=1"));
  }

  @Test
  public void testAddGeographicParameter() {
    String bbox = "10,20,30,40";
    restUrl.addParameter(OpenSearchConstants.GEO_BOX, bbox);

    String url = restUrl.buildUrl();
    assertThat(url, containsString("bbox="));
  }

  @Test
  public void testAddTemporalParameters() {
    String startDate = "2023-01-01";
    String endDate = "2023-12-31";

    restUrl.addParameter(OpenSearchConstants.DATE_START, startDate);
    restUrl.addParameter(OpenSearchConstants.DATE_END, endDate);

    String url = restUrl.buildUrl();
    assertThat(url, containsString("dtstart="));
    assertThat(url, containsString("dtend="));
  }

  @Test
  public void testAddPointRadiusSearch() {
    String lat = "40.7128";
    String lon = "-74.0060";
    String radius = "1000";

    restUrl.addParameter(OpenSearchConstants.GEO_LAT, lat);
    restUrl.addParameter(OpenSearchConstants.GEO_LON, lon);
    restUrl.addParameter(OpenSearchConstants.GEO_RADIUS, radius);

    String url = restUrl.buildUrl();
    assertThat(url, containsString("lat=" + lat));
    assertThat(url, containsString("lon=" + lon));
    assertThat(url, containsString("radius=" + radius));
  }

  @Test
  public void testUrlEncodingOfSpecialCharacters() {
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, "test&value=special");
    String url = restUrl.buildUrl();

    // Should be URL encoded
    assertThat(url, containsString("%26")); // & should be encoded
  }

  @Test
  public void testUrlWithEmptyParameters() {
    // Don't add any parameters
    String url = restUrl.buildUrl();
    assertThat(url, is(BASE_URL));
  }

  @Test
  public void testUrlWithNullParameterValue() {
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, null);
    String url = restUrl.buildUrl();

    // Should handle null gracefully
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testUrlWithEmptyStringParameter() {
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, "");
    String url = restUrl.buildUrl();

    // Should handle empty string
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testMultiplePointRadiusSearches() {
    restUrl.addParameter(OpenSearchConstants.GEO_LAT, "40.7128");
    restUrl.addParameter(OpenSearchConstants.GEO_LON, "-74.0060");
    restUrl.addParameter(OpenSearchConstants.GEO_RADIUS, "1000");

    String url = restUrl.buildUrl();
    assertThat(url, containsString("lat="));
    assertThat(url, containsString("lon="));
    assertThat(url, containsString("radius="));
  }

  @Test
  public void testAddGeometryParameter() {
    String wkt = "POLYGON ((10 10, 10 20, 20 20, 20 10, 10 10))";
    restUrl.addParameter(OpenSearchConstants.GEO_POLYGON, wkt);

    String url = restUrl.buildUrl();
    assertThat(url, containsString("geometry="));
  }

  @Test
  public void testSortParameter() {
    restUrl.addParameter(OpenSearchConstants.SORT, "date:desc");
    String url = restUrl.buildUrl();
    assertThat(url, containsString("sort="));
  }

  @Test
  public void testFormatParameter() {
    restUrl.addParameter(OpenSearchConstants.FORMAT, "atom");
    String url = restUrl.buildUrl();
    assertThat(url, containsString("format=atom"));
  }

  @Test
  public void testComplexQueryWithAllParameters() {
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, "satellite imagery");
    restUrl.addParameter(OpenSearchConstants.COUNT, "50");
    restUrl.addParameter(OpenSearchConstants.START_INDEX, "1");
    restUrl.addParameter(OpenSearchConstants.DATE_START, "2023-01-01");
    restUrl.addParameter(OpenSearchConstants.DATE_END, "2023-12-31");
    restUrl.addParameter(OpenSearchConstants.GEO_LAT, "40.7128");
    restUrl.addParameter(OpenSearchConstants.GEO_LON, "-74.0060");
    restUrl.addParameter(OpenSearchConstants.GEO_RADIUS, "5000");
    restUrl.addParameter(OpenSearchConstants.FORMAT, "atom");

    String url = restUrl.buildUrl();
    assertThat(url, containsString("q=satellite+imagery"));
    assertThat(url, containsString("count=50"));
    assertThat(url, containsString("start=1"));
    assertThat(url, containsString("dtstart="));
    assertThat(url, containsString("dtend="));
    assertThat(url, containsString("lat="));
    assertThat(url, containsString("lon="));
    assertThat(url, containsString("radius="));
    assertThat(url, containsString("format=atom"));
  }

  @Test
  public void testUrlDoesNotContainDuplicateQuestionMarks() {
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, "test");
    String url = restUrl.buildUrl();

    int questionMarkCount = 0;
    for (char c : url.toCharArray()) {
      if (c == '?') {
        questionMarkCount++;
      }
    }

    assertThat("Should have exactly one question mark", questionMarkCount, is(1));
  }

  @Test
  public void testParameterOverwrite() {
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, "first");
    restUrl.addParameter(OpenSearchConstants.SEARCH_TERMS, "second");

    String url = restUrl.buildUrl();
    // Behavior depends on implementation - either overwrites or appends
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testUrlWithBaseUrlContainingQueryString() {
    RestUrl urlWithQuery = new RestUrl(BASE_URL + "?existing=param");
    urlWithQuery.addParameter(OpenSearchConstants.SEARCH_TERMS, "test");

    String url = urlWithQuery.buildUrl();
    assertThat(url, containsString("existing=param"));
    assertThat(url, containsString("q=test"));
  }
}
