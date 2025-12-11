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
package ddf.catalog.operation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

/** Tests for {@link OperationTransaction} interface and its OperationType enum. */
public class OperationTransactionTest {

  @Test
  public void testOperationTypeCreateExists() {
    assertThat(OperationTransaction.OperationType.CREATE, is(notNullValue()));
  }

  @Test
  public void testOperationTypeUpdateExists() {
    assertThat(OperationTransaction.OperationType.UPDATE, is(notNullValue()));
  }

  @Test
  public void testOperationTypeDeleteExists() {
    assertThat(OperationTransaction.OperationType.DELETE, is(notNullValue()));
  }

  @Test
  public void testOperationTypeValuesCount() {
    assertThat(OperationTransaction.OperationType.values().length, is(3));
  }

  @Test
  public void testOperationTypeValuesOrder() {
    assertThat(
        OperationTransaction.OperationType.values(),
        arrayContaining(
            OperationTransaction.OperationType.CREATE,
            OperationTransaction.OperationType.UPDATE,
            OperationTransaction.OperationType.DELETE));
  }

  @Test
  public void testOperationTypeCreateOrdinal() {
    assertThat(OperationTransaction.OperationType.CREATE.ordinal(), is(0));
  }

  @Test
  public void testOperationTypeUpdateOrdinal() {
    assertThat(OperationTransaction.OperationType.UPDATE.ordinal(), is(1));
  }

  @Test
  public void testOperationTypeDeleteOrdinal() {
    assertThat(OperationTransaction.OperationType.DELETE.ordinal(), is(2));
  }

  @Test
  public void testOperationTypeCreateName() {
    assertThat(OperationTransaction.OperationType.CREATE.name(), is("CREATE"));
  }

  @Test
  public void testOperationTypeUpdateName() {
    assertThat(OperationTransaction.OperationType.UPDATE.name(), is("UPDATE"));
  }

  @Test
  public void testOperationTypeDeleteName() {
    assertThat(OperationTransaction.OperationType.DELETE.name(), is("DELETE"));
  }

  @Test
  public void testOperationTypeValueOfCreate() {
    assertThat(
        OperationTransaction.OperationType.valueOf("CREATE"),
        is(OperationTransaction.OperationType.CREATE));
  }

  @Test
  public void testOperationTypeValueOfUpdate() {
    assertThat(
        OperationTransaction.OperationType.valueOf("UPDATE"),
        is(OperationTransaction.OperationType.UPDATE));
  }

  @Test
  public void testOperationTypeValueOfDelete() {
    assertThat(
        OperationTransaction.OperationType.valueOf("DELETE"),
        is(OperationTransaction.OperationType.DELETE));
  }
}
