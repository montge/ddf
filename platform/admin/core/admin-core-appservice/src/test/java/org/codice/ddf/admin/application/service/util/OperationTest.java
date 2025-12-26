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
package org.codice.ddf.admin.application.service.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class OperationTest {

  @Test
  void testInstallOperatingName() {
    assertThat(Operation.INSTALL.getOperatingName(), is("Installing"));
  }

  @Test
  void testUninstallOperatingName() {
    assertThat(Operation.UNINSTALL.getOperatingName(), is("Uninstalling"));
  }

  @Test
  void testStopOperatingName() {
    assertThat(Operation.STOP.getOperatingName(), is("Stopping"));
  }

  @Test
  void testStartOperatingName() {
    assertThat(Operation.START.getOperatingName(), is("Starting"));
  }

  @Test
  void testUpdateOperatingName() {
    assertThat(Operation.UPDATE.getOperatingName(), is("Updating"));
  }

  @Test
  void testEnumValueCount() {
    assertThat(Operation.values().length, is(5));
  }

  @Test
  void testEnumValuesContainsAll() {
    assertThat(
        Operation.values(),
        arrayContainingInAnyOrder(
            Operation.INSTALL,
            Operation.UNINSTALL,
            Operation.STOP,
            Operation.START,
            Operation.UPDATE));
  }

  @Test
  void testValueOfInstall() {
    assertThat(Operation.valueOf("INSTALL"), is(Operation.INSTALL));
  }

  @Test
  void testValueOfUninstall() {
    assertThat(Operation.valueOf("UNINSTALL"), is(Operation.UNINSTALL));
  }

  @Test
  void testValueOfStop() {
    assertThat(Operation.valueOf("STOP"), is(Operation.STOP));
  }

  @Test
  void testValueOfStart() {
    assertThat(Operation.valueOf("START"), is(Operation.START));
  }

  @Test
  void testValueOfUpdate() {
    assertThat(Operation.valueOf("UPDATE"), is(Operation.UPDATE));
  }
}
