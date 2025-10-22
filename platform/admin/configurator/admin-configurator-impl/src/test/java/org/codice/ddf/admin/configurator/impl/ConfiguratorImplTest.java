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
package org.codice.ddf.admin.configurator.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.audit.SecurityLogger;
import java.util.UUID;
import org.codice.ddf.admin.configurator.ConfiguratorException;
import org.codice.ddf.admin.configurator.Operation;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.configurator.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Comprehensive unit tests for {@link ConfiguratorImpl} */
@RunWith(MockitoJUnitRunner.class)
public class ConfiguratorImplTest {

  @Mock private SecurityLogger mockSecurityLogger;

  @Mock private Operation<Void> mockOperation1;

  @Mock private Operation<Void> mockOperation2;

  @Mock private Operation<Void> mockOperation3;

  private ConfiguratorImpl configurator;

  @Before
  public void setUp() {
    configurator = new ConfiguratorImpl(mockSecurityLogger);
  }

  @Test
  public void testConstructor() {
    ConfiguratorImpl config = new ConfiguratorImpl(mockSecurityLogger);
    assertThat(config, is(notNullValue()));
  }

  @Test
  public void testAddOperation() {
    UUID id = configurator.add(mockOperation1);

    assertThat(id, is(notNullValue()));
  }

  @Test
  public void testAddMultipleOperations() {
    UUID id1 = configurator.add(mockOperation1);
    UUID id2 = configurator.add(mockOperation2);

    assertThat(id1, is(notNullValue()));
    assertThat(id2, is(notNullValue()));
    assertThat(id1.equals(id2), is(false));
  }

  @Test
  public void testCommitWithSingleOperationSuccess() throws Exception {
    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());

    configurator.add(mockOperation1);
    OperationReport report = configurator.commit("Test audit message");

    assertThat(report.hasTransactionSucceeded(), is(true));
    verify(mockOperation1, times(1)).commit();
    verify(mockOperation1, never()).rollback();
    verify(mockSecurityLogger, times(1)).audit("Test audit message");
  }

  @Test
  public void testCommitWithMultipleOperationsSuccess() throws Exception {
    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation2.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation3.commit()).thenReturn(ResultImpl.pass());

    configurator.add(mockOperation1);
    configurator.add(mockOperation2);
    configurator.add(mockOperation3);

    OperationReport report = configurator.commit("Test audit message", "param1", "param2");

    assertThat(report.hasTransactionSucceeded(), is(true));
    verify(mockOperation1, times(1)).commit();
    verify(mockOperation2, times(1)).commit();
    verify(mockOperation3, times(1)).commit();
    verify(mockSecurityLogger, times(1)).audit("Test audit message", "param1", "param2");
  }

  @Test
  public void testCommitWithFirstOperationFailure() throws Exception {
    ConfiguratorException exception = new ConfiguratorException("Operation 1 failed");
    when(mockOperation1.commit()).thenThrow(exception);
    when(mockOperation1.rollback()).thenReturn(ResultImpl.rollback());

    configurator.add(mockOperation1);
    configurator.add(mockOperation2);

    OperationReport report = configurator.commit("Test audit message");

    assertThat(report.hasTransactionSucceeded(), is(false));
    verify(mockOperation1, times(1)).commit();
    verify(mockOperation1, times(1)).rollback();
    verify(mockOperation2, never()).commit();
    verify(mockSecurityLogger, never()).audit(any());
  }

  @Test
  public void testCommitWithMiddleOperationFailure() throws Exception {
    ConfiguratorException exception = new ConfiguratorException("Operation 2 failed");
    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation2.commit()).thenThrow(exception);
    when(mockOperation1.rollback()).thenReturn(ResultImpl.rollback());

    configurator.add(mockOperation1);
    configurator.add(mockOperation2);
    configurator.add(mockOperation3);

    OperationReport report = configurator.commit("Test audit message");

    assertThat(report.hasTransactionSucceeded(), is(false));
    verify(mockOperation1, times(1)).commit();
    verify(mockOperation2, times(1)).commit();
    verify(mockOperation3, never()).commit();
    verify(mockOperation1, times(1)).rollback();
    verify(mockOperation2, never()).rollback();
    verify(mockSecurityLogger, never()).audit(any());
  }

  @Test
  public void testCommitWithLastOperationFailure() throws Exception {
    ConfiguratorException exception = new ConfiguratorException("Operation 3 failed");
    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation2.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation3.commit()).thenThrow(exception);
    when(mockOperation1.rollback()).thenReturn(ResultImpl.rollback());
    when(mockOperation2.rollback()).thenReturn(ResultImpl.rollback());

    configurator.add(mockOperation1);
    configurator.add(mockOperation2);
    configurator.add(mockOperation3);

    OperationReport report = configurator.commit("Test audit message");

    assertThat(report.hasTransactionSucceeded(), is(false));
    verify(mockOperation1, times(1)).commit();
    verify(mockOperation2, times(1)).commit();
    verify(mockOperation3, times(1)).commit();
    verify(mockOperation1, times(1)).rollback();
    verify(mockOperation2, times(1)).rollback();
    verify(mockSecurityLogger, never()).audit(any());
  }

  @Test
  public void testCommitWithFailureAndRollbackFailure() throws Exception {
    ConfiguratorException commitException = new ConfiguratorException("Commit failed");
    ConfiguratorException rollbackException = new ConfiguratorException("Rollback failed");

    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation2.commit()).thenThrow(commitException);
    when(mockOperation1.rollback()).thenThrow(rollbackException);

    configurator.add(mockOperation1);
    configurator.add(mockOperation2);

    OperationReport report = configurator.commit("Test audit message");

    assertThat(report.hasTransactionSucceeded(), is(false));
    verify(mockOperation1, times(1)).commit();
    verify(mockOperation2, times(1)).commit();
    verify(mockOperation1, times(1)).rollback();
    verify(mockSecurityLogger, never()).audit(any());
  }

  @Test
  public void testCommitWithNoOperations() {
    OperationReport report = configurator.commit("Test audit message");

    assertThat(report.hasTransactionSucceeded(), is(true));
    verify(mockSecurityLogger, times(1)).audit("Test audit message");
  }

  @Test
  public void testCommitRollbackOrder() throws Exception {
    ConfiguratorException exception = new ConfiguratorException("Operation 3 failed");

    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation2.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation3.commit()).thenThrow(exception);
    when(mockOperation2.rollback()).thenReturn(ResultImpl.rollback());
    when(mockOperation1.rollback()).thenReturn(ResultImpl.rollback());

    configurator.add(mockOperation1);
    configurator.add(mockOperation2);
    configurator.add(mockOperation3);

    configurator.commit("Test audit message");

    // Verify rollback happens in reverse order
    verify(mockOperation2, times(1)).rollback();
    verify(mockOperation1, times(1)).rollback();
    verify(mockOperation3, never()).rollback();
  }

  @Test
  public void testCommitWithOperationReturningData() throws Exception {
    Result<String> resultWithData = ResultImpl.passWithData("test-data");
    Operation<String> operationWithData = mock(Operation.class);
    when(operationWithData.commit()).thenReturn(resultWithData);

    configurator.add(operationWithData);
    OperationReport report = configurator.commit("Test audit message");

    assertThat(report.hasTransactionSucceeded(), is(true));
    verify(mockSecurityLogger, times(1)).audit("Test audit message");
  }

  @Test
  public void testCommitWithPartialRollbackFailure() throws Exception {
    ConfiguratorException commitException = new ConfiguratorException("Commit failed");
    ConfiguratorException rollbackException = new ConfiguratorException("Rollback 2 failed");

    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation2.commit()).thenReturn(ResultImpl.pass());
    when(mockOperation3.commit()).thenThrow(commitException);
    when(mockOperation2.rollback()).thenThrow(rollbackException);
    when(mockOperation1.rollback()).thenReturn(ResultImpl.rollback());

    configurator.add(mockOperation1);
    configurator.add(mockOperation2);
    configurator.add(mockOperation3);

    OperationReport report = configurator.commit("Test audit message");

    assertThat(report.hasTransactionSucceeded(), is(false));
    verify(mockOperation1, times(1)).rollback();
    verify(mockOperation2, times(1)).rollback();
    verify(mockSecurityLogger, never()).audit(any());
  }

  @Test
  public void testMultipleCommitCalls() throws Exception {
    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());

    configurator.add(mockOperation1);
    OperationReport report1 = configurator.commit("First commit");

    assertThat(report1.hasTransactionSucceeded(), is(true));
    verify(mockOperation1, times(1)).commit();

    // Adding another operation and committing again should process all operations
    when(mockOperation2.commit()).thenReturn(ResultImpl.pass());
    configurator.add(mockOperation2);
    OperationReport report2 = configurator.commit("Second commit");

    assertThat(report2.hasTransactionSucceeded(), is(true));
    // Operation1 should be committed again
    verify(mockOperation1, times(2)).commit();
    verify(mockOperation2, times(1)).commit();
  }

  @Test
  public void testCommitWithAuditMessageOnly() throws Exception {
    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());

    configurator.add(mockOperation1);
    OperationReport report = configurator.commit("Simple audit message");

    assertThat(report.hasTransactionSucceeded(), is(true));
    verify(mockSecurityLogger, times(1)).audit("Simple audit message");
  }

  @Test
  public void testCommitWithEmptyAuditMessage() throws Exception {
    when(mockOperation1.commit()).thenReturn(ResultImpl.pass());

    configurator.add(mockOperation1);
    OperationReport report = configurator.commit("");

    assertThat(report.hasTransactionSucceeded(), is(true));
    verify(mockSecurityLogger, times(1)).audit("");
  }
}
