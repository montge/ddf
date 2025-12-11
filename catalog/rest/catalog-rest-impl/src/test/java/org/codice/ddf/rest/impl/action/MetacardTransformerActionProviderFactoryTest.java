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
package org.codice.ddf.rest.impl.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link MetacardTransformerActionProviderFactory} class. */
public class MetacardTransformerActionProviderFactoryTest {

  private MetacardTransformerActionProviderFactory factory;

  @BeforeEach
  public void setUp() {
    factory = new MetacardTransformerActionProviderFactory();
  }

  @Test
  public void testCreateActionProviderWithBasicParameters() {
    String id = "test-id";
    String transformer = "xml";
    List<String> attributeNames = Collections.emptyList();

    MetacardTransformerActionProvider provider =
        factory.createActionProvider(id, transformer, attributeNames);

    assertThat(provider, is(notNullValue()));
    assertThat(provider, instanceOf(MetacardTransformerActionProvider.class));
  }

  @Test
  public void testCreateActionProviderWithAttributes() {
    String id = "test-id-with-attrs";
    String transformer = "json";
    List<String> attributeNames = Arrays.asList("title", "description", "location");

    MetacardTransformerActionProvider provider =
        factory.createActionProvider(id, transformer, attributeNames);

    assertThat(provider, is(notNullValue()));
    assertThat(provider, instanceOf(MetacardTransformerActionProvider.class));
  }

  @Test
  public void testCreateActionProviderWithEmptyList() {
    String id = "empty-list-id";
    String transformer = "geojson";
    List<String> attributeNames = Collections.emptyList();

    MetacardTransformerActionProvider provider =
        factory.createActionProvider(id, transformer, attributeNames);

    assertThat(provider, is(notNullValue()));
  }

  @Test
  public void testCreateActionProviderWithNullAttributeList() {
    String id = "null-list-id";
    String transformer = "html";
    List<String> attributeNames = null;

    MetacardTransformerActionProvider provider =
        factory.createActionProvider(id, transformer, attributeNames);

    assertThat(provider, is(notNullValue()));
  }

  @Test
  public void testCreateActionProviderWithDifferentIds() {
    String id1 = "first-id";
    String id2 = "second-id";
    String transformer = "xml";
    List<String> attributeNames = Collections.emptyList();

    MetacardTransformerActionProvider provider1 =
        factory.createActionProvider(id1, transformer, attributeNames);
    MetacardTransformerActionProvider provider2 =
        factory.createActionProvider(id2, transformer, attributeNames);

    assertThat(provider1, is(notNullValue()));
    assertThat(provider2, is(notNullValue()));
  }

  @Test
  public void testCreateActionProviderWithDifferentTransformers() {
    String id = "test-id";
    String transformer1 = "xml";
    String transformer2 = "json";
    List<String> attributeNames = Collections.emptyList();

    MetacardTransformerActionProvider provider1 =
        factory.createActionProvider(id, transformer1, attributeNames);
    MetacardTransformerActionProvider provider2 =
        factory.createActionProvider(id, transformer2, attributeNames);

    assertThat(provider1, is(notNullValue()));
    assertThat(provider2, is(notNullValue()));
  }

  @Test
  public void testMultipleCallsCreateNewInstances() {
    String id = "test-id";
    String transformer = "xml";
    List<String> attributeNames = Collections.emptyList();

    MetacardTransformerActionProvider provider1 =
        factory.createActionProvider(id, transformer, attributeNames);
    MetacardTransformerActionProvider provider2 =
        factory.createActionProvider(id, transformer, attributeNames);

    assertThat(provider1, is(notNullValue()));
    assertThat(provider2, is(notNullValue()));
    // Each call should create a new instance
    assertThat(provider1 == provider2, is(false));
  }
}
