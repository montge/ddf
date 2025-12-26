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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import org.junit.jupiter.api.Test;

class UpdateImplTest {

  @Test
  void testConstructorAndGetters() {
    Metacard newMetacard = mock(Metacard.class);
    Metacard oldMetacard = mock(Metacard.class);

    UpdateImpl update = new UpdateImpl(newMetacard, oldMetacard);

    assertThat(update.getNewMetacard(), is(sameInstance(newMetacard)));
    assertThat(update.getOldMetacard(), is(sameInstance(oldMetacard)));
  }

  @Test
  void testGetNewMetacard() {
    Metacard newMetacard = mock(Metacard.class);
    Metacard oldMetacard = mock(Metacard.class);

    UpdateImpl update = new UpdateImpl(newMetacard, oldMetacard);

    assertThat(update.getNewMetacard(), is(sameInstance(newMetacard)));
  }

  @Test
  void testGetOldMetacard() {
    Metacard newMetacard = mock(Metacard.class);
    Metacard oldMetacard = mock(Metacard.class);

    UpdateImpl update = new UpdateImpl(newMetacard, oldMetacard);

    assertThat(update.getOldMetacard(), is(sameInstance(oldMetacard)));
  }

  @Test
  void testWithNullNewMetacard() {
    Metacard oldMetacard = mock(Metacard.class);

    UpdateImpl update = new UpdateImpl(null, oldMetacard);

    assertThat(update.getNewMetacard(), is(nullValue()));
    assertThat(update.getOldMetacard(), is(sameInstance(oldMetacard)));
  }

  @Test
  void testWithNullOldMetacard() {
    Metacard newMetacard = mock(Metacard.class);

    UpdateImpl update = new UpdateImpl(newMetacard, null);

    assertThat(update.getNewMetacard(), is(sameInstance(newMetacard)));
    assertThat(update.getOldMetacard(), is(nullValue()));
  }

  @Test
  void testWithBothNullMetacards() {
    UpdateImpl update = new UpdateImpl(null, null);

    assertThat(update.getNewMetacard(), is(nullValue()));
    assertThat(update.getOldMetacard(), is(nullValue()));
  }
}
