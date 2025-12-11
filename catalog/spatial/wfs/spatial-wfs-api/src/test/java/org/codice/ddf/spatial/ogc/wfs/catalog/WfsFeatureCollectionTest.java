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
package org.codice.ddf.spatial.ogc.wfs.catalog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WfsFeatureCollectionTest {

  private List<Metacard> testMetacards;

  @BeforeEach
  public void setUp() {
    testMetacards = new ArrayList<>();
    testMetacards.add(new MetacardImpl());
    testMetacards.add(new MetacardImpl());
    testMetacards.add(new MetacardImpl());
  }

  @Test
  public void testGetNumberOfFeatures() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(5, testMetacards);
    assertThat(collection.getNumberOfFeatures(), is(5L));
  }

  @Test
  public void testGetNumberOfFeaturesWhenNotSet() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(-1, testMetacards);
    assertThat(collection.getNumberOfFeatures(), is(3L));
  }

  @Test
  public void testGetFeatureMembers() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(3, testMetacards);
    List<Metacard> members = collection.getFeatureMembers();

    assertThat(members, notNullValue());
    assertThat(members.size(), is(3));
  }

  @Test
  public void testGetFeatureMembersEmpty() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(0, Collections.emptyList());
    List<Metacard> members = collection.getFeatureMembers();

    assertThat(members, notNullValue());
    assertThat(members.size(), is(0));
  }

  @Test
  public void testGetNumberOfFeaturesMatchesListSize() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(-1, testMetacards);
    assertThat(collection.getNumberOfFeatures(), is((long) testMetacards.size()));
  }

  @Test
  public void testGetNumberOfFeaturesWithHitsResultType() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(100, testMetacards);
    assertThat(collection.getNumberOfFeatures(), is(100L));
  }

  @Test
  public void testGetFeatureMembersNotNull() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(0, testMetacards);
    assertThat(collection.getFeatureMembers(), notNullValue());
  }

  @Test
  public void testGetNumberOfFeaturesZero() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(0, Collections.emptyList());
    assertThat(collection.getNumberOfFeatures(), is(0L));
  }

  @Test
  public void testGetNumberOfFeaturesLargeValue() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(1000000, testMetacards);
    assertThat(collection.getNumberOfFeatures(), is(1000000L));
  }

  @Test
  public void testMultipleFeatureMembers() {
    List<Metacard> largeList = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      largeList.add(new MetacardImpl());
    }
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(-1, largeList);

    assertThat(collection.getFeatureMembers().size(), is(100));
    assertThat(collection.getNumberOfFeatures(), is(100L));
  }

  @Test
  public void testGetFeatureMembersReturnsSameList() {
    TestWfsFeatureCollection collection = new TestWfsFeatureCollection(3, testMetacards);
    List<Metacard> members1 = collection.getFeatureMembers();
    List<Metacard> members2 = collection.getFeatureMembers();

    assertThat(members1, is(members2));
  }

  // Test implementation of WfsFeatureCollection
  private static class TestWfsFeatureCollection implements WfsFeatureCollection {
    private final long numberOfFeatures;
    private final List<Metacard> featureMembers;

    public TestWfsFeatureCollection(long numberOfFeatures, List<Metacard> featureMembers) {
      this.numberOfFeatures = numberOfFeatures;
      this.featureMembers = featureMembers;
    }

    @Override
    public long getNumberOfFeatures() {
      if (numberOfFeatures >= 0) {
        return numberOfFeatures;
      }
      return featureMembers.size();
    }

    @Override
    public List<Metacard> getFeatureMembers() {
      return featureMembers;
    }
  }
}
