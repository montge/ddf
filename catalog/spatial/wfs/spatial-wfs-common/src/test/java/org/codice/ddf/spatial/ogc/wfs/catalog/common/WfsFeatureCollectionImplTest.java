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
package org.codice.ddf.spatial.ogc.wfs.catalog.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link WfsFeatureCollectionImpl}. */
public class WfsFeatureCollectionImplTest {

  @Test
  public void testConstructorWithHitsOnly() {
    WfsFeatureCollectionImpl collection = new WfsFeatureCollectionImpl(100L);

    assertThat(collection, is(notNullValue()));
    assertThat(collection.getNumberOfFeatures(), is(100L));
    assertThat(collection.getFeatureMembers(), is(notNullValue()));
    assertThat(collection.getFeatureMembers().isEmpty(), is(true));
  }

  @Test
  public void testConstructorWithFeaturesAndCount() {
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(mock(Metacard.class));
    metacards.add(mock(Metacard.class));
    metacards.add(mock(Metacard.class));

    WfsFeatureCollectionImpl collection = new WfsFeatureCollectionImpl(3L, metacards);

    assertThat(collection, is(notNullValue()));
    assertThat(collection.getNumberOfFeatures(), is(3L));
    assertThat(collection.getFeatureMembers(), is(notNullValue()));
    assertThat(collection.getFeatureMembers().size(), is(3));
  }

  @Test
  public void testConstructorWithEmptyFeatureList() {
    List<Metacard> metacards = Collections.emptyList();
    WfsFeatureCollectionImpl collection = new WfsFeatureCollectionImpl(0L, metacards);

    assertThat(collection, is(notNullValue()));
    assertThat(collection.getNumberOfFeatures(), is(0L));
    assertThat(collection.getFeatureMembers().isEmpty(), is(true));
  }

  @Test
  public void testConstructorWithMismatchedCountAndFeatures() {
    // Total count may be higher than returned features (pagination)
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(mock(Metacard.class));
    metacards.add(mock(Metacard.class));

    WfsFeatureCollectionImpl collection = new WfsFeatureCollectionImpl(1000L, metacards);

    assertThat(collection.getNumberOfFeatures(), is(1000L));
    assertThat(collection.getFeatureMembers().size(), is(2));
  }

  @Test
  public void testConstructorWithZeroFeatures() {
    WfsFeatureCollectionImpl collection = new WfsFeatureCollectionImpl(0L);

    assertThat(collection.getNumberOfFeatures(), is(0L));
    assertThat(collection.getFeatureMembers().isEmpty(), is(true));
  }

  @Test
  public void testConstructorWithLargeNumberOfFeatures() {
    List<Metacard> metacards = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      metacards.add(mock(Metacard.class));
    }

    WfsFeatureCollectionImpl collection = new WfsFeatureCollectionImpl(1000L, metacards);

    assertThat(collection.getNumberOfFeatures(), is(1000L));
    assertThat(collection.getFeatureMembers().size(), is(1000));
  }

  @Test
  public void testGetFeatureMembersReturnsProvidedList() {
    List<Metacard> originalMetacards = new ArrayList<>();
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    originalMetacards.add(metacard1);
    originalMetacards.add(metacard2);

    WfsFeatureCollectionImpl collection = new WfsFeatureCollectionImpl(2L, originalMetacards);

    List<Metacard> retrievedMetacards = collection.getFeatureMembers();
    assertThat(retrievedMetacards, is(originalMetacards));
    assertThat(retrievedMetacards.size(), is(2));
  }
}
