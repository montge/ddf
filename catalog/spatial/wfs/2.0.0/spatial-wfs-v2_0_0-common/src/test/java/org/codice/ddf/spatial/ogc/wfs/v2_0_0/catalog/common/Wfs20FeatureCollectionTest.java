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
package org.codice.ddf.spatial.ogc.wfs.v2_0_0.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class Wfs20FeatureCollectionTest {

  @Test
  void testDefaultConstructor() {
    Wfs20FeatureCollection collection = new Wfs20FeatureCollection();

    assertThat(collection.getNumberReturned(), is(nullValue()));
    assertThat(collection.getNumberMatched(), is(nullValue()));
    assertThat(collection.getMembers(), is(notNullValue()));
    assertThat(collection.getMembers(), is(empty()));
  }

  @Test
  void testSetAndGetNumberReturned() {
    Wfs20FeatureCollection collection = new Wfs20FeatureCollection();

    collection.setNumberReturned(BigInteger.valueOf(42));

    assertThat(collection.getNumberReturned(), is(BigInteger.valueOf(42)));
  }

  @Test
  void testSetAndGetNumberMatched() {
    Wfs20FeatureCollection collection = new Wfs20FeatureCollection();

    collection.setNumberMatched("100");

    assertThat(collection.getNumberMatched(), is("100"));
  }

  @Test
  void testSetAndGetMembers() {
    Wfs20FeatureCollection collection = new Wfs20FeatureCollection();
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    List<Metacard> members = Arrays.asList(metacard1, metacard2);

    collection.setMembers(members);

    assertThat(collection.getMembers(), hasSize(2));
    assertThat(collection.getMembers().get(0), is(metacard1));
    assertThat(collection.getMembers().get(1), is(metacard2));
  }

  @Test
  void testNumberMatchedUnknown() {
    Wfs20FeatureCollection collection = new Wfs20FeatureCollection();

    collection.setNumberMatched("unknown");

    assertThat(collection.getNumberMatched(), is("unknown"));
  }
}
