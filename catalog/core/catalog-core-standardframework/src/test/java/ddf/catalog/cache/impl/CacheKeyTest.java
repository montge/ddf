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
package ddf.catalog.cache.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.content.data.ContentItem;
import ddf.catalog.data.Metacard;
import ddf.catalog.operation.ResourceRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CacheKeyTest {

  private static final String SOURCE_ID = "source1";
  private static final String METACARD_ID = "metacard123";

  @Test
  void testGenerateKeyWithMetacardOnly() {
    Metacard metacard = createMockMetacard(SOURCE_ID, METACARD_ID);

    CacheKey cacheKey = new CacheKey(metacard);
    String key = cacheKey.generateKey();

    assertThat(key, is(SOURCE_ID + "-" + METACARD_ID));
  }

  @Test
  void testGenerateKeyWithMetacardAndResourceRequest() {
    Metacard metacard = createMockMetacard(SOURCE_ID, METACARD_ID);
    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    when(resourceRequest.getPropertyNames()).thenReturn(Collections.emptySet());

    CacheKey cacheKey = new CacheKey(metacard, resourceRequest);
    String key = cacheKey.generateKey();

    assertThat(key, is(SOURCE_ID + "-" + METACARD_ID));
  }

  @Test
  void testGenerateKeyWithOptionArgument() {
    Metacard metacard = createMockMetacard(SOURCE_ID, METACARD_ID);
    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    Set<String> propertyNames = new HashSet<>();
    propertyNames.add(ResourceRequest.OPTION_ARGUMENT);
    when(resourceRequest.getPropertyNames()).thenReturn(propertyNames);
    when(resourceRequest.getPropertyValue(ResourceRequest.OPTION_ARGUMENT)).thenReturn("PDF");

    CacheKey cacheKey = new CacheKey(metacard, resourceRequest);
    String key = cacheKey.generateKey();

    assertThat(key, containsString(SOURCE_ID + "-" + METACARD_ID));
    assertThat(key, containsString(ResourceRequest.OPTION_ARGUMENT));
    assertThat(key, containsString("PDF"));
  }

  @Test
  void testGenerateKeyWithQualifierKeyword() {
    Metacard metacard = createMockMetacard(SOURCE_ID, METACARD_ID);
    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    Set<String> propertyNames = new HashSet<>();
    propertyNames.add(ContentItem.QUALIFIER_KEYWORD);
    when(resourceRequest.getPropertyNames()).thenReturn(propertyNames);
    when(resourceRequest.getPropertyValue(ContentItem.QUALIFIER_KEYWORD)).thenReturn("thumbnail");

    CacheKey cacheKey = new CacheKey(metacard, resourceRequest);
    String key = cacheKey.generateKey();

    assertThat(key, containsString(SOURCE_ID + "-" + METACARD_ID));
    assertThat(key, containsString(ContentItem.QUALIFIER_KEYWORD));
    assertThat(key, containsString("thumbnail"));
  }

  @Test
  void testGenerateKeyWithNonMatchingProperties() {
    Metacard metacard = createMockMetacard(SOURCE_ID, METACARD_ID);
    ResourceRequest resourceRequest = mock(ResourceRequest.class);
    Set<String> propertyNames = new HashSet<>();
    propertyNames.add("someOtherProperty");
    when(resourceRequest.getPropertyNames()).thenReturn(propertyNames);

    CacheKey cacheKey = new CacheKey(metacard, resourceRequest);
    String key = cacheKey.generateKey();

    assertThat(key, is(SOURCE_ID + "-" + METACARD_ID));
  }

  @Test
  void testNullMetacardThrowsException() {
    assertThrows(NullPointerException.class, () -> new CacheKey(null));
  }

  @Test
  void testNullResourceRequestThrowsException() {
    Metacard metacard = createMockMetacard(SOURCE_ID, METACARD_ID);

    assertThrows(NullPointerException.class, () -> new CacheKey(metacard, null));
  }

  @Test
  void testDifferentMetacardsProduceDifferentKeys() {
    Metacard metacard1 = createMockMetacard(SOURCE_ID, "id1");
    Metacard metacard2 = createMockMetacard(SOURCE_ID, "id2");

    CacheKey cacheKey1 = new CacheKey(metacard1);
    CacheKey cacheKey2 = new CacheKey(metacard2);

    assertThat(cacheKey1.generateKey(), is(not(cacheKey2.generateKey())));
  }

  @Test
  void testDifferentSourcesProduceDifferentKeys() {
    Metacard metacard1 = createMockMetacard("source1", METACARD_ID);
    Metacard metacard2 = createMockMetacard("source2", METACARD_ID);

    CacheKey cacheKey1 = new CacheKey(metacard1);
    CacheKey cacheKey2 = new CacheKey(metacard2);

    assertThat(cacheKey1.generateKey(), is(not(cacheKey2.generateKey())));
  }

  @Test
  void testSameMetacardProducesSameKey() {
    Metacard metacard = createMockMetacard(SOURCE_ID, METACARD_ID);

    CacheKey cacheKey1 = new CacheKey(metacard);
    CacheKey cacheKey2 = new CacheKey(metacard);

    assertThat(cacheKey1.generateKey(), is(cacheKey2.generateKey()));
  }

  private Metacard createMockMetacard(String sourceId, String id) {
    Metacard metacard = mock(Metacard.class);
    when(metacard.getSourceId()).thenReturn(sourceId);
    when(metacard.getId()).thenReturn(id);
    return metacard;
  }
}
