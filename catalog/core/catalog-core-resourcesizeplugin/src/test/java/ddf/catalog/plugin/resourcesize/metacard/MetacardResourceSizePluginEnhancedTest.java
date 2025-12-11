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
package ddf.catalog.plugin.resourcesize.metacard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.cache.ResourceCacheInterface;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.QueryResponse;
import ddf.catalog.resource.data.ReliableResource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MetacardResourceSizePluginEnhancedTest {

  @Mock private ResourceCacheInterface cache;

  @Mock private QueryResponse queryResponse;

  @Mock private ReliableResource cachedResource;

  private MetacardResourceSizePlugin plugin;

  @BeforeEach
  public void setUp() {
    plugin = new MetacardResourceSizePlugin(cache);
  }

  @Test
  public void testProcessWithEmptyResults() throws Exception {
    when(queryResponse.getResults()).thenReturn(Collections.emptyList());

    QueryResponse result = plugin.process(queryResponse);

    assertThat(result, is(queryResponse));
    verify(cache, never()).getValid(anyString(), any());
  }

  @Test
  public void testProcessWithMultipleResults() throws Exception {
    when(cachedResource.getSize()).thenReturn(1000L);
    when(cachedResource.hasProduct()).thenReturn(true);
    when(cache.getValid(anyString(), any())).thenReturn(cachedResource);

    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setId("id1");
    metacard1.setResourceSize("N/A");

    MetacardImpl metacard2 = new MetacardImpl();
    metacard2.setId("id2");
    metacard2.setResourceSize("N/A");

    List<Result> results = new ArrayList<>();
    results.add(new ResultImpl(metacard1));
    results.add(new ResultImpl(metacard2));

    when(queryResponse.getResults()).thenReturn(results);

    QueryResponse result = plugin.process(queryResponse);

    assertThat(result.getResults().size(), is(2));
    verify(cache, times(2)).getValid(anyString(), any());

    Attribute attr1 = result.getResults().get(0).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);
    Attribute attr2 = result.getResults().get(1).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);

    assertThat((String) attr1.getValue(), is("1000"));
    assertThat((String) attr2.getValue(), is("1000"));
  }

  @Test
  public void testProcessWithNegativeResourceSize() throws Exception {
    when(cachedResource.getSize()).thenReturn(-1L);
    when(cachedResource.hasProduct()).thenReturn(true);
    when(cache.getValid(anyString(), any())).thenReturn(cachedResource);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("id1");
    metacard.setResourceSize("N/A");

    Result result = new ResultImpl(metacard);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result));

    QueryResponse response = plugin.process(queryResponse);

    Attribute resourceSizeAttr =
        response.getResults().get(0).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);
    assertThat((String) resourceSizeAttr.getValue(), equalTo("N/A"));
  }

  @Test
  public void testProcessWithIllegalArgumentException() throws Exception {
    when(cache.getValid(anyString(), any())).thenThrow(new IllegalArgumentException("Invalid key"));

    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("id1");
    metacard.setResourceSize("N/A");

    Result result = new ResultImpl(metacard);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result));

    QueryResponse response = plugin.process(queryResponse);

    // Should not throw exception, just log and continue
    assertThat(response, notNullValue());
  }

  @Test
  public void testProcessWithMixedResults() throws Exception {
    // First metacard has cached resource
    ReliableResource resource1 = mock(ReliableResource.class);
    when(resource1.getSize()).thenReturn(500L);
    when(resource1.hasProduct()).thenReturn(true);

    // Second metacard has no cached resource
    when(cache.getValid(anyString(), any())).thenReturn(resource1, null);

    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setId("id1");
    metacard1.setResourceSize("N/A");

    MetacardImpl metacard2 = new MetacardImpl();
    metacard2.setId("id2");
    metacard2.setResourceSize("N/A");

    List<Result> results = new ArrayList<>();
    results.add(new ResultImpl(metacard1));
    results.add(new ResultImpl(metacard2));

    when(queryResponse.getResults()).thenReturn(results);

    QueryResponse response = plugin.process(queryResponse);

    Attribute attr1 =
        response.getResults().get(0).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);
    Attribute attr2 =
        response.getResults().get(1).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);

    assertThat((String) attr1.getValue(), is("500"));
    assertThat((String) attr2.getValue(), equalTo("N/A"));
  }

  @Test
  public void testProcessWithLargeResourceSize() throws Exception {
    when(cachedResource.getSize()).thenReturn(Long.MAX_VALUE);
    when(cachedResource.hasProduct()).thenReturn(true);
    when(cache.getValid(anyString(), any())).thenReturn(cachedResource);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("id1");
    metacard.setResourceSize("N/A");

    Result result = new ResultImpl(metacard);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result));

    QueryResponse response = plugin.process(queryResponse);

    Attribute resourceSizeAttr =
        response.getResults().get(0).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);
    assertThat((String) resourceSizeAttr.getValue(), is(String.valueOf(Long.MAX_VALUE)));
  }

  @Test
  public void testProcessWithExistingResourceSize() throws Exception {
    when(cachedResource.getSize()).thenReturn(1000L);
    when(cachedResource.hasProduct()).thenReturn(true);
    when(cache.getValid(anyString(), any())).thenReturn(cachedResource);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("id1");
    metacard.setResourceSize("500"); // Existing size

    Result result = new ResultImpl(metacard);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result));

    QueryResponse response = plugin.process(queryResponse);

    Attribute resourceSizeAttr =
        response.getResults().get(0).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);
    // Should be updated to cached value
    assertThat((String) resourceSizeAttr.getValue(), is("1000"));
  }

  @Test
  public void testProcessWithNoSourceId() throws Exception {
    when(cachedResource.getSize()).thenReturn(1000L);
    when(cachedResource.hasProduct()).thenReturn(true);
    when(cache.getValid(anyString(), any())).thenReturn(cachedResource);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("id1");
    // No source ID set

    Result result = new ResultImpl(metacard);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result));

    QueryResponse response = plugin.process(queryResponse);

    Attribute resourceSizeAttr =
        response.getResults().get(0).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);
    assertThat((String) resourceSizeAttr.getValue(), is("1000"));
  }

  @Test
  public void testProcessPreservesOtherAttributes() throws Exception {
    when(cachedResource.getSize()).thenReturn(1000L);
    when(cachedResource.hasProduct()).thenReturn(true);
    when(cache.getValid(anyString(), any())).thenReturn(cachedResource);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("id1");
    metacard.setTitle("Test Title");
    metacard.setResourceSize("N/A");

    Result result = new ResultImpl(metacard);
    when(queryResponse.getResults()).thenReturn(Collections.singletonList(result));

    QueryResponse response = plugin.process(queryResponse);

    Metacard resultMetacard = response.getResults().get(0).getMetacard();
    assertThat(resultMetacard.getTitle(), is("Test Title"));
    assertThat(resultMetacard.getId(), is("id1"));
  }

  @Test
  public void testProcessWithResultContainingNullMetacardInList() throws Exception {
    MetacardImpl validMetacard = new MetacardImpl();
    validMetacard.setId("valid");
    validMetacard.setResourceSize("N/A");

    Result validResult = new ResultImpl(validMetacard);
    Result nullResult = mock(Result.class);
    when(nullResult.getMetacard()).thenReturn(null);

    List<Result> results = new ArrayList<>();
    results.add(validResult);
    results.add(nullResult);

    when(queryResponse.getResults()).thenReturn(results);

    when(cachedResource.getSize()).thenReturn(1000L);
    when(cachedResource.hasProduct()).thenReturn(true);
    when(cache.getValid(anyString(), any())).thenReturn(cachedResource);

    QueryResponse response = plugin.process(queryResponse);

    assertThat(response, notNullValue());
  }

  @Test
  public void testProcessWithCacheReturningDifferentResourceSizes() throws Exception {
    ReliableResource resource1 = mock(ReliableResource.class);
    when(resource1.getSize()).thenReturn(100L);
    when(resource1.hasProduct()).thenReturn(true);

    ReliableResource resource2 = mock(ReliableResource.class);
    when(resource2.getSize()).thenReturn(200L);
    when(resource2.hasProduct()).thenReturn(true);

    when(cache.getValid(anyString(), any())).thenReturn(resource1, resource2);

    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setId("id1");

    MetacardImpl metacard2 = new MetacardImpl();
    metacard2.setId("id2");

    List<Result> results = new ArrayList<>();
    results.add(new ResultImpl(metacard1));
    results.add(new ResultImpl(metacard2));

    when(queryResponse.getResults()).thenReturn(results);

    QueryResponse response = plugin.process(queryResponse);

    Attribute attr1 =
        response.getResults().get(0).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);
    Attribute attr2 =
        response.getResults().get(1).getMetacard().getAttribute(Metacard.RESOURCE_SIZE);

    assertThat((String) attr1.getValue(), is("100"));
    assertThat((String) attr2.getValue(), is("200"));
  }
}
