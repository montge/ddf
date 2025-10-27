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

import java.net.URISyntaxException;
import org.junit.Test;

/** Comprehensive tests for RestUrl query parameter building */
public class RestUrlTest {

  private static final String BASE_URL_TEMPLATE =
      "https://example.com:8993/services/catalog/sources/test-source/metacard123";
  private static final String QUERY_URL_TEMPLATE =
      "https://example.com:8993/services/catalog/query?q={searchTerms}&src={fs:routeTo?}&count={count?}";

  @Test
  public void testBasicUrlConstruction() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    assertThat(restUrl, is(notNullValue()));
  }

  @Test
  public void testBuildUrlWithId() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("metacard123");
    String url = restUrl.buildUrl();
    assertThat(url, containsString("metacard123"));
  }

  @Test
  public void testBuildUrlWithoutId() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    String url = restUrl.buildUrl();
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testBuildUrlWithRetrieveResource() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("metacard456");
    restUrl.setRetrieveResource(true);
    String url = restUrl.buildUrl();
    assertThat(url, containsString("transform=resource"));
  }

  @Test
  public void testBuildUrlWithoutRetrieveResource() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("metacard789");
    restUrl.setRetrieveResource(false);
    String url = restUrl.buildUrl();
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testGetId() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    String testId = "test-id-123";
    restUrl.setId(testId);
    assertThat(restUrl.getId(), is(testId));
  }

  @Test
  public void testIsRetrieveResource() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setRetrieveResource(true);
    assertThat(restUrl.isRetrieveResource(), is(true));
  }

  @Test
  public void testToString() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("metacard999");
    String toString = restUrl.toString();
    String buildUrl = restUrl.buildUrl();
    assertThat(toString, is(buildUrl));
  }

  @Test
  public void testUrlTemplateWithParameters() throws Exception {
    String urlWithParams =
        "http://localhost:8181/services/catalog/query?q={searchTerms}&src={fs:routeTo?}&count={count?}";
    RestUrl restUrl = RestUrl.newInstance(urlWithParams);
    assertThat(restUrl, is(notNullValue()));
    String url = restUrl.buildUrl();
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testUrlTemplateExtraction() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    String url = restUrl.buildUrl();
    assertThat(url, containsString("https://example.com:8993/services/catalog/"));
  }

  @Test
  public void testComplexUrlTemplate() throws Exception {
    String complexTemplate =
        "https://remote.server.com:443/services/rest/catalog/sources/remote/metacard{id}?transform={transform?}";
    RestUrl restUrl = RestUrl.newInstance(complexTemplate);
    restUrl.setId("abc123");
    String url = restUrl.buildUrl();
    assertThat(url, containsString("abc123"));
  }

  @Test
  public void testMultipleCallsToSetId() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("first-id");
    restUrl.setId("second-id");
    assertThat(restUrl.getId(), is("second-id"));
  }

  @Test
  public void testResourceTransformParameter() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("doc123");
    restUrl.setRetrieveResource(true);
    String url = restUrl.buildUrl();
    assertThat(url, containsString("?transform=resource"));
  }

  @Test
  public void testUrlWithIdAndResource() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("document-with-resource");
    restUrl.setRetrieveResource(true);
    String url = restUrl.buildUrl();
    assertThat(url, containsString("document-with-resource"));
    assertThat(url, containsString("transform=resource"));
  }

  @Test
  public void testUrlWithSpecialCharactersInId() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    String specialId = "id-with-dashes_and_underscores.123";
    restUrl.setId(specialId);
    String url = restUrl.buildUrl();
    assertThat(url, containsString(specialId));
  }

  @Test
  public void testUrlWithHttpProtocol() throws Exception {
    String httpUrl =
        "http://example.com:8080/services/catalog/query?q={searchTerms}&count={count?}";
    RestUrl restUrl = RestUrl.newInstance(httpUrl);
    String url = restUrl.buildUrl();
    assertThat(url, containsString("http://example.com:8080"));
  }

  @Test
  public void testUrlWithHttpsProtocol() throws Exception {
    String httpsUrl =
        "https://secure.example.com:8993/services/catalog/query?q={searchTerms}&count={count?}";
    RestUrl restUrl = RestUrl.newInstance(httpsUrl);
    String url = restUrl.buildUrl();
    assertThat(url, containsString("https://secure.example.com:8993"));
  }

  @Test
  public void testUrlWithDifferentPort() throws Exception {
    String urlWithPort =
        "https://example.com:9443/services/catalog/query?q={searchTerms}&count={count?}";
    RestUrl restUrl = RestUrl.newInstance(urlWithPort);
    String url = restUrl.buildUrl();
    assertThat(url, containsString(":9443"));
  }

  @Test
  public void testUrlWithLongPath() throws Exception {
    String longPathUrl =
        "https://example.com:8993/services/catalog/sources/remote-source/federated/query?q={searchTerms}";
    RestUrl restUrl = RestUrl.newInstance(longPathUrl);
    String url = restUrl.buildUrl();
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testUrlWithNullId() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId(null);
    String url = restUrl.buildUrl();
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testUrlWithEmptyId() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("");
    String url = restUrl.buildUrl();
    assertThat(url, is(notNullValue()));
  }

  @Test
  public void testRetrieveResourceFalseByDefault() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    assertThat(restUrl.isRetrieveResource(), is(false));
  }

  @Test(expected = URISyntaxException.class)
  public void testInvalidUrlTemplate() throws Exception {
    String invalidUrl = "not a valid url template";
    RestUrl.newInstance(invalidUrl);
  }

  @Test
  public void testUrlTemplateWithoutParameters() throws Exception {
    String simpleUrl = "https://example.com:8993/services/catalog/query";
    RestUrl restUrl = RestUrl.newInstance(simpleUrl);
    String url = restUrl.buildUrl();
    assertThat(url, containsString("https://example.com:8993"));
  }

  @Test
  public void testMultipleResourceRetrievalToggle() throws Exception {
    RestUrl restUrl = RestUrl.newInstance(QUERY_URL_TEMPLATE);
    restUrl.setId("toggle-test");
    restUrl.setRetrieveResource(true);
    restUrl.setRetrieveResource(false);
    String url = restUrl.buildUrl();
    assertThat(url, is(notNullValue()));
  }
}
