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
package org.codice.ddf.sync.installer.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;
import org.apache.karaf.shell.api.action.Action;
import org.codice.ddf.sync.installer.api.SynchronizedInstaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link WaitForReadyCommand} class. */
@ExtendWith(MockitoExtension.class)
public class WaitForReadyCommandTest {

  @Mock private SynchronizedInstaller syncInstaller;

  private WaitForReadyCommand command;

  @BeforeEach
  public void setUp() {
    command = new WaitForReadyCommand();
    command.syncInstaller = syncInstaller;
    // LogService is not mocked - tests that don't use increaseLogLevel don't need it
  }

  @Test
  public void testImplementsAction() {
    assertThat(command, is(instanceOf(Action.class)));
  }

  @Test
  public void testExecuteReturnsNull() throws Exception {
    Object result = command.execute();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testExecuteCallsWaitForBootFinishWithDefaultTimeout() throws Exception {
    command.execute();

    verify(syncInstaller).waitForBootFinish();
  }

  @Test
  public void testExecuteWithWaitTimeCallsWaitForBootFinishWithTimeout() throws Exception {
    command.waitTime = 30;

    command.execute();

    verify(syncInstaller).waitForBootFinish(eq(TimeUnit.SECONDS.toMillis(30)));
  }

  @Test
  public void testExecuteWithZeroWaitTimeUsesZeroTimeout() throws Exception {
    command.waitTime = 0;

    command.execute();

    verify(syncInstaller).waitForBootFinish(eq(0L));
  }

  @Test
  public void testExecuteWithNegativeWaitTimeUsesZeroTimeout() throws Exception {
    command.waitTime = -5;

    command.execute();

    verify(syncInstaller).waitForBootFinish(eq(0L));
  }

  @Test
  public void testExecuteThrowsWhenSyncInstallerThrows() throws Exception {
    doThrow(new InterruptedException("test")).when(syncInstaller).waitForBootFinish();

    assertThrows(InterruptedException.class, () -> command.execute());
  }

  @Test
  public void testExecuteThrowsWhenSyncInstallerWithTimeoutThrows() throws Exception {
    command.waitTime = 10;
    doThrow(new InterruptedException("test")).when(syncInstaller).waitForBootFinish(anyLong());

    assertThrows(InterruptedException.class, () -> command.execute());
  }

  @Test
  public void testDefaultIncreaseLogLevelIsFalse() {
    WaitForReadyCommand newCommand = new WaitForReadyCommand();

    assertThat(newCommand.increaseLogLevel, is(false));
  }

  @Test
  public void testDefaultWaitTimeIsNull() {
    WaitForReadyCommand newCommand = new WaitForReadyCommand();

    assertThat(newCommand.waitTime, is(nullValue()));
  }
}
