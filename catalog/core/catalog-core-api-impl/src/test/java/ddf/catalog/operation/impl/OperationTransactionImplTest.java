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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.OperationTransaction.OperationType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class OperationTransactionImplTest {

  @Test
  void testConstructorWithCreateType() {
    Metacard metacard = mock(Metacard.class);
    List<Metacard> metacards = Collections.singletonList(metacard);

    OperationTransactionImpl transaction =
        new OperationTransactionImpl(OperationType.CREATE, metacards);

    assertThat(transaction.getType(), is(OperationType.CREATE));
    assertThat(transaction.getPreviousStateMetacards(), contains(metacard));
  }

  @Test
  void testConstructorWithUpdateType() {
    Metacard metacard = mock(Metacard.class);
    List<Metacard> metacards = Collections.singletonList(metacard);

    OperationTransactionImpl transaction =
        new OperationTransactionImpl(OperationType.UPDATE, metacards);

    assertThat(transaction.getType(), is(OperationType.UPDATE));
    assertThat(transaction.getPreviousStateMetacards(), contains(metacard));
  }

  @Test
  void testConstructorWithDeleteType() {
    Metacard metacard = mock(Metacard.class);
    List<Metacard> metacards = Collections.singletonList(metacard);

    OperationTransactionImpl transaction =
        new OperationTransactionImpl(OperationType.DELETE, metacards);

    assertThat(transaction.getType(), is(OperationType.DELETE));
    assertThat(transaction.getPreviousStateMetacards(), contains(metacard));
  }

  @Test
  void testConstructorWithMultipleMetacards() {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    Metacard metacard3 = mock(Metacard.class);
    List<Metacard> metacards = Arrays.asList(metacard1, metacard2, metacard3);

    OperationTransactionImpl transaction =
        new OperationTransactionImpl(OperationType.UPDATE, metacards);

    assertThat(transaction.getPreviousStateMetacards(), hasSize(3));
    assertThat(transaction.getPreviousStateMetacards(), contains(metacard1, metacard2, metacard3));
  }

  @Test
  void testConstructorWithEmptyMetacards() {
    List<Metacard> emptyList = Collections.emptyList();

    OperationTransactionImpl transaction =
        new OperationTransactionImpl(OperationType.CREATE, emptyList);

    assertThat(transaction.getType(), is(OperationType.CREATE));
    assertThat(transaction.getPreviousStateMetacards(), is(empty()));
  }

  @Test
  void testPreviousStateMetacardsIsUnmodifiable() {
    Metacard metacard = mock(Metacard.class);
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(metacard);

    OperationTransactionImpl transaction =
        new OperationTransactionImpl(OperationType.UPDATE, metacards);

    List<Metacard> returnedList = transaction.getPreviousStateMetacards();

    assertThrows(UnsupportedOperationException.class, () -> returnedList.add(mock(Metacard.class)));
  }

  @Test
  void testOriginalListModificationDoesNotAffectTransaction() {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    List<Metacard> metacards = new ArrayList<>();
    metacards.add(metacard1);

    OperationTransactionImpl transaction =
        new OperationTransactionImpl(OperationType.CREATE, metacards);

    // Modify the original list after creation
    metacards.add(metacard2);

    // Transaction should only have the original metacard
    assertThat(transaction.getPreviousStateMetacards(), hasSize(1));
    assertThat(transaction.getPreviousStateMetacards(), contains(metacard1));
  }
}
