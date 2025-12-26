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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import net.opengis.cat.csw.v_2_0_2.DeleteType;
import net.opengis.cat.csw.v_2_0_2.QueryConstraintType;
import org.codice.ddf.spatial.ogc.csw.catalog.actions.DeleteAction;
import org.codice.ddf.spatial.ogc.csw.catalog.common.CswConstants;
import org.junit.jupiter.api.Test;

class DeleteActionImplTest {

  @Test
  void testImplementsDeleteAction() {
    DeleteType deleteType = mock(DeleteType.class);
    Map<String, String> prefixMappings = new HashMap<>();

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action, instanceOf(DeleteAction.class));
  }

  @Test
  void testConstructorWithTypeName() {
    DeleteType deleteType = mock(DeleteType.class);
    when(deleteType.getTypeName()).thenReturn("csw:Record");
    when(deleteType.getHandle()).thenReturn("deleteHandle");
    QueryConstraintType constraint = mock(QueryConstraintType.class);
    when(deleteType.getConstraint()).thenReturn(constraint);
    Map<String, String> prefixMappings = new HashMap<>();

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action.getTypeName(), is("csw:Record"));
    assertThat(action.getHandle(), is("deleteHandle"));
    assertThat(action.getConstraint(), is(sameInstance(constraint)));
  }

  @Test
  void testDefaultTypeNameWhenNull() {
    DeleteType deleteType = mock(DeleteType.class);
    when(deleteType.getTypeName()).thenReturn(null);
    when(deleteType.getHandle()).thenReturn("handle");
    Map<String, String> prefixMappings = new HashMap<>();

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action.getTypeName(), is(CswConstants.CSW_RECORD));
  }

  @Test
  void testDefaultTypeNameWhenEmpty() {
    DeleteType deleteType = mock(DeleteType.class);
    when(deleteType.getTypeName()).thenReturn("");
    when(deleteType.getHandle()).thenReturn("handle");
    Map<String, String> prefixMappings = new HashMap<>();

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action.getTypeName(), is(CswConstants.CSW_RECORD));
  }

  @Test
  void testDefaultHandleWhenNull() {
    DeleteType deleteType = mock(DeleteType.class);
    when(deleteType.getTypeName()).thenReturn("csw:Record");
    when(deleteType.getHandle()).thenReturn(null);
    Map<String, String> prefixMappings = new HashMap<>();

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action.getHandle(), is(""));
  }

  @Test
  void testDefaultHandleWhenEmpty() {
    DeleteType deleteType = mock(DeleteType.class);
    when(deleteType.getTypeName()).thenReturn("csw:Record");
    when(deleteType.getHandle()).thenReturn("");
    Map<String, String> prefixMappings = new HashMap<>();

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action.getHandle(), is(""));
  }

  @Test
  void testGetPrefixToUriMappings() {
    DeleteType deleteType = mock(DeleteType.class);
    Map<String, String> prefixMappings = new HashMap<>();
    prefixMappings.put("csw", "http://www.opengis.net/cat/csw/2.0.2");
    prefixMappings.put("dc", "http://purl.org/dc/elements/1.1/");

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action.getPrefixToUriMappings(), is(sameInstance(prefixMappings)));
    assertThat(action.getPrefixToUriMappings().size(), is(2));
    assertThat(
        action.getPrefixToUriMappings().get("csw"), is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  void testGetConstraint() {
    DeleteType deleteType = mock(DeleteType.class);
    QueryConstraintType constraint = mock(QueryConstraintType.class);
    when(deleteType.getConstraint()).thenReturn(constraint);
    Map<String, String> prefixMappings = new HashMap<>();

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action.getConstraint(), is(sameInstance(constraint)));
  }

  @Test
  void testWithNullConstraint() {
    DeleteType deleteType = mock(DeleteType.class);
    when(deleteType.getTypeName()).thenReturn("csw:Record");
    when(deleteType.getHandle()).thenReturn("handle");
    when(deleteType.getConstraint()).thenReturn(null);
    Map<String, String> prefixMappings = new HashMap<>();

    DeleteActionImpl action = new DeleteActionImpl(deleteType, prefixMappings);

    assertThat(action.getConstraint(), is((QueryConstraintType) null));
  }
}
