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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ObjectFactoryTest {

  private ObjectFactory factory;

  @BeforeEach
  public void setUp() {
    factory = new ObjectFactory();
  }

  @Test
  public void testCreateAdaptedMetacard() {
    AdaptedMetacard metacard = factory.createAdaptedMetacard();

    assertThat(metacard, is(notNullValue()));
    assertThat(metacard, is(instanceOf(AdaptedMetacard.class)));
  }

  @Test
  public void testCreateAdaptedSourceResponse() {
    AdaptedSourceResponse response = factory.createAdaptedSourceResponse();

    assertThat(response, is(notNullValue()));
    assertThat(response, is(instanceOf(AdaptedSourceResponse.class)));
  }

  @Test
  public void testMultipleMetacardCreations() {
    AdaptedMetacard metacard1 = factory.createAdaptedMetacard();
    AdaptedMetacard metacard2 = factory.createAdaptedMetacard();

    assertThat(metacard1, is(notNullValue()));
    assertThat(metacard2, is(notNullValue()));
    // Each call should return a new instance
    assertThat(metacard1 == metacard2, is(false));
  }

  @Test
  public void testMultipleSourceResponseCreations() {
    AdaptedSourceResponse response1 = factory.createAdaptedSourceResponse();
    AdaptedSourceResponse response2 = factory.createAdaptedSourceResponse();

    assertThat(response1, is(notNullValue()));
    assertThat(response2, is(notNullValue()));
    // Each call should return a new instance
    assertThat(response1 == response2, is(false));
  }
}
