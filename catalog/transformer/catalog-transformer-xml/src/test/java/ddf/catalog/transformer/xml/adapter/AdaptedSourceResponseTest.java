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
package ddf.catalog.transformer.xml.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.operation.impl.QueryRequestImpl;
import ddf.catalog.operation.impl.SourceResponseImpl;
import ddf.catalog.transformer.xml.binding.MetacardElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AdaptedSourceResponseTest {

  @Test
  public void testConstructorWithNullResponse() {
    AdaptedSourceResponse response = new AdaptedSourceResponse(null);

    assertThat(response, is(notNullValue()));
    assertThat(response.getResults(), is(notNullValue()));
    assertThat(response.getResults(), is(empty()));
  }

  @Test
  public void testConstructorWithValidResponse() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setTitle("Test Title");

    Result result = new ResultImpl(metacard);
    List<Result> results = new ArrayList<>();
    results.add(result);

    QueryRequest request = new QueryRequestImpl(null);
    SourceResponse sourceResponse = new SourceResponseImpl(request, results);

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    assertThat(adaptedResponse, is(notNullValue()));
    assertThat(adaptedResponse.getResults().size(), is(1));
    // Hits is 1 because SourceResponseImpl counts results
    assertThat(adaptedResponse.getHits(), is(1L));
  }

  @Test
  public void testGetMetacardWithSingleResult() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("metacard-123");
    metacard.setTitle("Test Metacard");
    metacard.setSourceId("source-1");

    Result result = new ResultImpl(metacard);
    List<Result> results = Collections.singletonList(result);

    SourceResponse sourceResponse = new SourceResponseImpl(new QueryRequestImpl(null), results);

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    List<MetacardElement> metacardElements = adaptedResponse.getMetacard();

    assertThat(metacardElements, is(notNullValue()));
    assertThat(metacardElements.size(), is(1));

    MetacardElement element = metacardElements.get(0);
    assertThat(element.getId(), is("metacard-123"));
    assertThat(element.getSource(), is("source-1"));
  }

  @Test
  public void testGetMetacardWithMultipleResults() {
    List<Result> results = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("id-" + i);
      metacard.setTitle("Title " + i);
      results.add(new ResultImpl(metacard));
    }

    SourceResponse sourceResponse = new SourceResponseImpl(new QueryRequestImpl(null), results);

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    List<MetacardElement> metacardElements = adaptedResponse.getMetacard();

    assertThat(metacardElements.size(), is(5));
    for (int i = 0; i < 5; i++) {
      assertThat(metacardElements.get(i).getId(), is("id-" + i));
    }
  }

  @Test
  public void testGetMetacardWithNullMetacard() {
    Result result = mock(Result.class);
    when(result.getMetacard()).thenReturn(null);

    List<Result> results = Collections.singletonList(result);
    SourceResponse sourceResponse = new SourceResponseImpl(new QueryRequestImpl(null), results);

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    List<MetacardElement> metacardElements = adaptedResponse.getMetacard();

    // Null metacards should be skipped
    assertThat(metacardElements, is(empty()));
  }

  @Test
  public void testGetMetacardWithEmptyResults() {
    SourceResponse sourceResponse =
        new SourceResponseImpl(new QueryRequestImpl(null), new ArrayList<>());

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    List<MetacardElement> metacardElements = adaptedResponse.getMetacard();

    assertThat(metacardElements, is(empty()));
  }

  @Test
  public void testDefaultConstructor() {
    AdaptedSourceResponse response = new AdaptedSourceResponse();

    assertThat(response, is(notNullValue()));
    assertThat(response.getResults(), is(notNullValue()));
  }

  @Test
  public void testGetRequest() {
    QueryRequest request = new QueryRequestImpl(null);
    SourceResponse sourceResponse = new SourceResponseImpl(request, new ArrayList<>());

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    assertThat(adaptedResponse.getRequest(), is(notNullValue()));
    assertThat(adaptedResponse.getRequest(), is(request));
  }

  @Test
  public void testHasProperties() {
    SourceResponse sourceResponse =
        new SourceResponseImpl(new QueryRequestImpl(null), new ArrayList<>());

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    assertThat(adaptedResponse.hasProperties(), is(false));
  }

  @Test
  public void testGetHits() {
    SourceResponse sourceResponse =
        new SourceResponseImpl(new QueryRequestImpl(null), new ArrayList<>());

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    assertThat(adaptedResponse.getHits(), is(0L));
  }

  @Test
  public void testGetMetacardWithComplexMetacardType() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("complex-id");
    metacard.setTitle("Complex Title");
    metacard.setSourceId("complex-source");
    metacard.setAttribute("description", "Test description");
    metacard.setAttribute("count", 42);

    Result result = new ResultImpl(metacard);
    List<Result> results = Collections.singletonList(result);

    SourceResponse sourceResponse = new SourceResponseImpl(new QueryRequestImpl(null), results);

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    List<MetacardElement> metacardElements = adaptedResponse.getMetacard();

    assertThat(metacardElements.size(), is(1));
    MetacardElement element = metacardElements.get(0);
    assertThat(element.getId(), is("complex-id"));
    assertThat(element.getAttributes(), is(notNullValue()));
  }

  @Test
  public void testGetMetacardWithNullMetacardType() {
    Metacard metacard = mock(Metacard.class);
    when(metacard.getId()).thenReturn("test-id");
    when(metacard.getSourceId()).thenReturn("test-source");
    when(metacard.getMetacardType()).thenReturn(null);

    Result result = new ResultImpl(metacard);
    List<Result> results = Collections.singletonList(result);

    SourceResponse sourceResponse = new SourceResponseImpl(new QueryRequestImpl(null), results);

    AdaptedSourceResponse adaptedResponse = new AdaptedSourceResponse(sourceResponse);

    List<MetacardElement> metacardElements = adaptedResponse.getMetacard();

    // Should still create element even with null metacard type
    assertThat(metacardElements.size(), is(1));
  }
}
