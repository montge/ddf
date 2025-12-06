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
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.Metacard;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for RestFilterDelegate */
@RunWith(MockitoJUnitRunner.class)
public class RestFilterDelegateTest {

  private RestUrl restUrl;

  private RestFilterDelegate delegate;

  @Before
  public void setUp() throws MalformedURLException, URISyntaxException {
    restUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    delegate = new RestFilterDelegate(restUrl);
  }

  @Test
  public void testConstructor() {
    assertThat(delegate, notNullValue());
    assertThat(delegate.getRestUrl(), is(restUrl));
  }

  @Test
  public void testGetRestUrl() {
    RestUrl returnedUrl = delegate.getRestUrl();

    assertThat(returnedUrl, notNullValue());
    assertThat(returnedUrl, is(restUrl));
  }

  @Test
  public void testPropertyIsEqualToWithMetacardId() {
    String testId = "test-metacard-id-12345";

    RestUrl result = delegate.propertyIsEqualTo(Metacard.ID, testId, true);

    assertThat(result, notNullValue());
    assertThat(result, is(restUrl));
  }

  @Test
  public void testPropertyIsEqualToWithMetacardIdCaseInsensitive() {
    String testId = "test-metacard-id-12345";

    RestUrl result = delegate.propertyIsEqualTo(Metacard.ID, testId, false);

    assertThat(result, notNullValue());
    assertThat(result, is(restUrl));
  }

  @Test
  public void testPropertyIsEqualToWithNonIdProperty() {
    RestUrl result = delegate.propertyIsEqualTo("title", "test title", true);

    assertThat(result, nullValue());
  }

  @Test
  public void testPropertyIsEqualToWithDifferentProperty() {
    RestUrl result = delegate.propertyIsEqualTo("description", "test description", true);

    assertThat(result, nullValue());
  }

  @Test
  public void testPropertyIsEqualToWithEmptyId() {
    RestUrl result = delegate.propertyIsEqualTo(Metacard.ID, "", true);

    assertThat(result, notNullValue());
  }

  @Test
  public void testPropertyIsEqualToWithNullId() {
    RestUrl result = delegate.propertyIsEqualTo(Metacard.ID, null, true);

    assertThat(result, notNullValue());
  }

  @Test
  public void testDefaultOperation() {
    RestUrl result = delegate.defaultOperation("property", "literal", String.class, null);

    assertThat(result, nullValue());
  }

  @Test
  public void testAndWithSingleRestUrl() throws Exception {
    RestUrl firstUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    firstUrl.setId("test-id-1");

    List<RestUrl> operands = new ArrayList<>();
    operands.add(firstUrl);

    RestUrl result = delegate.and(operands);

    assertThat(result, notNullValue());
    assertThat(result, is(firstUrl));
  }

  @Test
  public void testAndWithMultipleRestUrls() throws Exception {
    RestUrl firstUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    firstUrl.setId("test-id-1");

    RestUrl secondUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    secondUrl.setId("test-id-2");

    List<RestUrl> operands = Arrays.asList(firstUrl, secondUrl);

    RestUrl result = delegate.and(operands);

    assertThat(result, notNullValue());
    assertThat(result, is(firstUrl)); // Should return first non-null
  }

  @Test
  public void testAndWithNullInList() throws Exception {
    RestUrl firstUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    firstUrl.setId("test-id-1");

    List<RestUrl> operands = Arrays.asList(null, firstUrl);

    RestUrl result = delegate.and(operands);

    assertThat(result, notNullValue());
    assertThat(result, is(firstUrl));
  }

  @Test
  public void testAndWithAllNulls() {
    List<RestUrl> operands = Arrays.asList(null, null, null);

    RestUrl result = delegate.and(operands);

    assertThat(result, nullValue());
  }

  @Test
  public void testAndWithEmptyList() {
    List<RestUrl> operands = new ArrayList<>();

    RestUrl result = delegate.and(operands);

    assertThat(result, nullValue());
  }

  @Test
  public void testOrWithSingleRestUrl() throws Exception {
    RestUrl firstUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    firstUrl.setId("test-id-1");

    List<RestUrl> operands = new ArrayList<>();
    operands.add(firstUrl);

    RestUrl result = delegate.or(operands);

    assertThat(result, notNullValue());
    assertThat(result, is(firstUrl));
  }

  @Test
  public void testOrWithMultipleRestUrls() throws Exception {
    RestUrl firstUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    firstUrl.setId("test-id-1");

    RestUrl secondUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    secondUrl.setId("test-id-2");

    List<RestUrl> operands = Arrays.asList(firstUrl, secondUrl);

    RestUrl result = delegate.or(operands);

    assertThat(result, notNullValue());
    assertThat(result, is(firstUrl)); // Should return first non-null
  }

  @Test
  public void testOrWithNullInList() throws Exception {
    RestUrl firstUrl = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    firstUrl.setId("test-id-1");

    List<RestUrl> operands = Arrays.asList(null, null, firstUrl);

    RestUrl result = delegate.or(operands);

    assertThat(result, notNullValue());
    assertThat(result, is(firstUrl));
  }

  @Test
  public void testOrWithAllNulls() {
    List<RestUrl> operands = Arrays.asList(null, null, null);

    RestUrl result = delegate.or(operands);

    assertThat(result, nullValue());
  }

  @Test
  public void testOrWithEmptyList() {
    List<RestUrl> operands = new ArrayList<>();

    RestUrl result = delegate.or(operands);

    assertThat(result, nullValue());
  }

  @Test
  public void testAndAndOrMixedOperations() throws Exception {
    RestUrl url1 = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    url1.setId("id-1");

    RestUrl url2 = RestUrl.newInstance("http://localhost:8181/services/catalog/query");
    url2.setId("id-2");

    List<RestUrl> andOperands = Arrays.asList(url1, url2);
    RestUrl andResult = delegate.and(andOperands);

    List<RestUrl> orOperands = Arrays.asList(andResult, null);
    RestUrl orResult = delegate.or(orOperands);

    assertThat(orResult, notNullValue());
    assertThat(orResult, is(url1));
  }

  @Test
  public void testPropertyIsEqualToWithSpecialCharactersInId() {
    String specialId = "test-id-with-!@#$%^&*()";

    RestUrl result = delegate.propertyIsEqualTo(Metacard.ID, specialId, true);

    assertThat(result, notNullValue());
  }

  @Test
  public void testPropertyIsEqualToWithVeryLongId() {
    String longId = "a".repeat(1000);

    RestUrl result = delegate.propertyIsEqualTo(Metacard.ID, longId, true);

    assertThat(result, notNullValue());
  }

  @Test
  public void testAndWithSingleNonNullUrl() throws Exception {
    RestUrl url = RestUrl.newInstance("http://localhost:8181/services/catalog/query");

    List<RestUrl> operands = Arrays.asList(null, null, url, null);

    RestUrl result = delegate.and(operands);

    assertThat(result, is(url));
  }

  @Test
  public void testOrWithSingleNonNullUrl() throws Exception {
    RestUrl url = RestUrl.newInstance("http://localhost:8181/services/catalog/query");

    List<RestUrl> operands = Arrays.asList(null, null, url, null);

    RestUrl result = delegate.or(operands);

    assertThat(result, is(url));
  }

  @Test
  public void testDefaultOperationWithNullLiteral() {
    RestUrl result = delegate.defaultOperation("property", null, String.class, null);

    assertThat(result, nullValue());
  }

  @Test
  public void testDefaultOperationWithIntegerLiteral() {
    RestUrl result = delegate.defaultOperation("property", 123, Integer.class, null);

    assertThat(result, nullValue());
  }

  @Test
  public void testDefaultOperationWithBooleanLiteral() {
    RestUrl result = delegate.defaultOperation("property", true, Boolean.class, null);

    assertThat(result, nullValue());
  }
}
