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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.opengis.cat.csw.v_2_0_2.QueryConstraintType;
import org.junit.jupiter.api.Test;

class UpdateActionImplTest {

  private static final String TYPE_NAME = "csw:Record";
  private static final String HANDLE = "updateHandle";

  @Test
  void testMetacardConstructor() {
    Metacard metacard = mock(Metacard.class);

    UpdateActionImpl action = new UpdateActionImpl(metacard, TYPE_NAME, HANDLE);

    assertThat(action.getMetacard(), is(sameInstance(metacard)));
    assertThat(action.getTypeName(), is(TYPE_NAME));
    assertThat(action.getHandle(), is(HANDLE));
  }

  @Test
  void testMetacardConstructorLeavesOtherFieldsNull() {
    Metacard metacard = mock(Metacard.class);

    UpdateActionImpl action = new UpdateActionImpl(metacard, TYPE_NAME, HANDLE);

    assertThat(action.getConstraint(), is(nullValue()));
    assertThat(action.getRecordProperties(), is(nullValue()));
    assertThat(action.getPrefixToUriMappings(), is(nullValue()));
  }

  @Test
  void testRecordPropertiesConstructor() {
    Map<String, Serializable> recordProperties = new HashMap<>();
    recordProperties.put("title", "Updated Title");
    QueryConstraintType constraint = mock(QueryConstraintType.class);
    Map<String, String> prefixMappings = new HashMap<>();
    prefixMappings.put("csw", "http://www.opengis.net/cat/csw/2.0.2");

    UpdateActionImpl action =
        new UpdateActionImpl(recordProperties, TYPE_NAME, HANDLE, constraint, prefixMappings);

    assertThat(action.getRecordProperties(), is(sameInstance(recordProperties)));
    assertThat(action.getTypeName(), is(TYPE_NAME));
    assertThat(action.getHandle(), is(HANDLE));
    assertThat(action.getConstraint(), is(sameInstance(constraint)));
    assertThat(action.getPrefixToUriMappings(), is(sameInstance(prefixMappings)));
  }

  @Test
  void testRecordPropertiesConstructorLeavesMetacardNull() {
    Map<String, Serializable> recordProperties = new HashMap<>();
    QueryConstraintType constraint = mock(QueryConstraintType.class);
    Map<String, String> prefixMappings = new HashMap<>();

    UpdateActionImpl action =
        new UpdateActionImpl(recordProperties, TYPE_NAME, HANDLE, constraint, prefixMappings);

    assertThat(action.getMetacard(), is(nullValue()));
  }

  @Test
  void testGetTypeName() {
    Metacard metacard = mock(Metacard.class);

    UpdateActionImpl action = new UpdateActionImpl(metacard, "custom:Type", HANDLE);

    assertThat(action.getTypeName(), is("custom:Type"));
  }

  @Test
  void testGetHandle() {
    Metacard metacard = mock(Metacard.class);

    UpdateActionImpl action = new UpdateActionImpl(metacard, TYPE_NAME, "myHandle");

    assertThat(action.getHandle(), is("myHandle"));
  }

  @Test
  void testWithNullMetacard() {
    UpdateActionImpl action = new UpdateActionImpl(null, TYPE_NAME, HANDLE);

    assertThat(action.getMetacard(), is(nullValue()));
    assertThat(action.getTypeName(), is(TYPE_NAME));
    assertThat(action.getHandle(), is(HANDLE));
  }

  @Test
  void testWithNullTypeName() {
    Metacard metacard = mock(Metacard.class);

    UpdateActionImpl action = new UpdateActionImpl(metacard, null, HANDLE);

    assertThat(action.getTypeName(), is(nullValue()));
  }

  @Test
  void testWithNullHandle() {
    Metacard metacard = mock(Metacard.class);

    UpdateActionImpl action = new UpdateActionImpl(metacard, TYPE_NAME, null);

    assertThat(action.getHandle(), is(nullValue()));
  }

  @Test
  void testRecordPropertiesContainsMultipleEntries() {
    Map<String, Serializable> recordProperties = new HashMap<>();
    recordProperties.put("title", "Title");
    recordProperties.put("description", "Description");
    recordProperties.put("modified", "2024-01-01");
    QueryConstraintType constraint = mock(QueryConstraintType.class);
    Map<String, String> prefixMappings = new HashMap<>();

    UpdateActionImpl action =
        new UpdateActionImpl(recordProperties, TYPE_NAME, HANDLE, constraint, prefixMappings);

    assertThat(action.getRecordProperties().size(), is(3));
    assertThat(action.getRecordProperties().get("title"), is("Title"));
    assertThat(action.getRecordProperties().get("description"), is("Description"));
  }
}
