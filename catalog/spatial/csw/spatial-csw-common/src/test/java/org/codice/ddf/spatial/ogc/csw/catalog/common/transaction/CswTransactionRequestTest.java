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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import org.codice.ddf.spatial.ogc.csw.catalog.actions.DeleteAction;
import org.codice.ddf.spatial.ogc.csw.catalog.actions.InsertAction;
import org.codice.ddf.spatial.ogc.csw.catalog.actions.UpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CswTransactionRequestTest {

  private CswTransactionRequest request;

  @BeforeEach
  void setUp() {
    request = new CswTransactionRequest();
  }

  @Test
  void testDefaultConstructorInitializesEmptyLists() {
    assertThat(request, is(notNullValue()));
    assertThat(request.getInsertActions(), is(notNullValue()));
    assertThat(request.getDeleteActions(), is(notNullValue()));
    assertThat(request.getUpdateActions(), is(notNullValue()));
    assertThat(request.getInsertActions(), is(empty()));
    assertThat(request.getDeleteActions(), is(empty()));
    assertThat(request.getUpdateActions(), is(empty()));
  }

  @Test
  void testDefaultVerboseIsFalse() {
    assertThat(request.isVerbose(), is(false));
  }

  @Test
  void testDefaultVersionIsNull() {
    assertThat(request.getVersion(), is(nullValue()));
  }

  @Test
  void testDefaultServiceIsNull() {
    assertThat(request.getService(), is(nullValue()));
  }

  @Test
  void testSetAndGetVersion() {
    request.setVersion("2.0.2");

    assertThat(request.getVersion(), is("2.0.2"));
  }

  @Test
  void testSetAndGetService() {
    request.setService("CSW");

    assertThat(request.getService(), is("CSW"));
  }

  @Test
  void testSetAndGetVerbose() {
    request.setVerbose(true);

    assertThat(request.isVerbose(), is(true));
  }

  @Test
  void testAddInsertAction() {
    InsertAction insertAction = mock(InsertAction.class);

    request.getInsertActions().add(insertAction);

    assertThat(request.getInsertActions(), hasSize(1));
    assertThat(request.getInsertActions().get(0), is(insertAction));
  }

  @Test
  void testAddDeleteAction() {
    DeleteAction deleteAction = mock(DeleteAction.class);

    request.getDeleteActions().add(deleteAction);

    assertThat(request.getDeleteActions(), hasSize(1));
    assertThat(request.getDeleteActions().get(0), is(deleteAction));
  }

  @Test
  void testAddUpdateAction() {
    UpdateAction updateAction = mock(UpdateAction.class);

    request.getUpdateActions().add(updateAction);

    assertThat(request.getUpdateActions(), hasSize(1));
    assertThat(request.getUpdateActions().get(0), is(updateAction));
  }

  @Test
  void testAddMultipleInsertActions() {
    InsertAction action1 = mock(InsertAction.class);
    InsertAction action2 = mock(InsertAction.class);
    InsertAction action3 = mock(InsertAction.class);

    request.getInsertActions().add(action1);
    request.getInsertActions().add(action2);
    request.getInsertActions().add(action3);

    assertThat(request.getInsertActions(), hasSize(3));
  }

  @Test
  void testMixedActions() {
    InsertAction insertAction = mock(InsertAction.class);
    DeleteAction deleteAction = mock(DeleteAction.class);
    UpdateAction updateAction = mock(UpdateAction.class);

    request.getInsertActions().add(insertAction);
    request.getDeleteActions().add(deleteAction);
    request.getUpdateActions().add(updateAction);

    assertThat(request.getInsertActions(), hasSize(1));
    assertThat(request.getDeleteActions(), hasSize(1));
    assertThat(request.getUpdateActions(), hasSize(1));
  }

  @Test
  void testFullyPopulatedRequest() {
    request.setVersion("2.0.2");
    request.setService("CSW");
    request.setVerbose(true);
    request.getInsertActions().add(mock(InsertAction.class));
    request.getDeleteActions().add(mock(DeleteAction.class));
    request.getUpdateActions().add(mock(UpdateAction.class));

    assertThat(request.getVersion(), is("2.0.2"));
    assertThat(request.getService(), is("CSW"));
    assertThat(request.isVerbose(), is(true));
    assertThat(request.getInsertActions(), hasSize(1));
    assertThat(request.getDeleteActions(), hasSize(1));
    assertThat(request.getUpdateActions(), hasSize(1));
  }

  @Test
  void testSetVersionToNull() {
    request.setVersion("2.0.2");

    request.setVersion(null);

    assertThat(request.getVersion(), is(nullValue()));
  }

  @Test
  void testSetServiceToNull() {
    request.setService("CSW");

    request.setService(null);

    assertThat(request.getService(), is(nullValue()));
  }

  @Test
  void testToggleVerbose() {
    request.setVerbose(true);
    assertThat(request.isVerbose(), is(true));

    request.setVerbose(false);
    assertThat(request.isVerbose(), is(false));
  }
}
