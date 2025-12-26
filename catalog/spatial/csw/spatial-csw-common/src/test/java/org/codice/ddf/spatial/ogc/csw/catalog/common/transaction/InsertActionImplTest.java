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
package org.codice.ddf.spatial.ogc.csw.catalog.common.transaction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.codice.ddf.spatial.ogc.csw.catalog.actions.InsertAction;
import org.junit.jupiter.api.Test;

class InsertActionImplTest {

  @Test
  void testImplementsInsertAction() {
    InsertActionImpl action =
        new InsertActionImpl("csw:Record", "handle1", Collections.emptyList());

    assertThat(action, instanceOf(InsertAction.class));
  }

  @Test
  void testConstructorAndGetters() {
    List<Metacard> records = Arrays.asList(mock(Metacard.class), mock(Metacard.class));

    InsertActionImpl action = new InsertActionImpl("csw:Record", "insertHandle", records);

    assertThat(action.getTypeName(), is("csw:Record"));
    assertThat(action.getHandle(), is("insertHandle"));
    assertThat(action.getRecords(), is(sameInstance(records)));
  }

  @Test
  void testGetRecordsReturnsList() {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    List<Metacard> records = Arrays.asList(metacard1, metacard2);

    InsertActionImpl action = new InsertActionImpl("csw:Record", "handle", records);

    assertThat(action.getRecords(), hasSize(2));
    assertThat(action.getRecords().get(0), is(sameInstance(metacard1)));
    assertThat(action.getRecords().get(1), is(sameInstance(metacard2)));
  }

  @Test
  void testWithEmptyRecordsList() {
    InsertActionImpl action = new InsertActionImpl("csw:Record", "handle", Collections.emptyList());

    assertThat(action.getRecords(), is(empty()));
  }

  @Test
  void testWithNullRecordsList() {
    InsertActionImpl action = new InsertActionImpl("csw:Record", "handle", null);

    assertThat(action.getRecords(), is(nullValue()));
  }

  @Test
  void testWithNullTypeName() {
    InsertActionImpl action = new InsertActionImpl(null, "handle", Collections.emptyList());

    assertThat(action.getTypeName(), is(nullValue()));
  }

  @Test
  void testWithNullHandle() {
    InsertActionImpl action = new InsertActionImpl("csw:Record", null, Collections.emptyList());

    assertThat(action.getHandle(), is(nullValue()));
  }

  @Test
  void testWithCustomTypeName() {
    InsertActionImpl action =
        new InsertActionImpl("gmd:MD_Metadata", "isoInsert", Collections.emptyList());

    assertThat(action.getTypeName(), is("gmd:MD_Metadata"));
  }
}
