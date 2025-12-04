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
package org.codice.ddf.admin.core.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

/** Tests for {@link ConfigurationStatus} interface constants and default methods. */
public class ConfigurationStatusTest {

  private TestConfigurationStatus status;

  @Before
  public void setUp() {
    status = new TestConfigurationStatus();
  }

  @Test
  public void testDisabledExtensionConstant() {
    assertThat(ConfigurationStatus.DISABLED_EXTENSION, is("_disabled"));
  }

  @Test
  public void testNewFactoryPidConstant() {
    assertThat(ConfigurationStatus.NEW_FACTORY_PID, is("newFactoryPid"));
  }

  @Test
  public void testNewPidConstant() {
    assertThat(ConfigurationStatus.NEW_PID, is("newPid"));
  }

  @Test
  public void testOriginalPidConstant() {
    assertThat(ConfigurationStatus.ORIGINAL_PID, is("originalPid"));
  }

  @Test
  public void testOriginalFactoryPidConstant() {
    assertThat(ConfigurationStatus.ORIGINAL_FACTORY_PID, is("originalFactoryPid"));
  }

  @Test
  public void testGetNewFactoryPidReturnsNull() {
    assertThat(status.getNewFactoryPid(), is(nullValue()));
  }

  @Test
  public void testSetAndGetNewFactoryPid() {
    status.setNewFactoryPid("org.example.Factory.new");
    assertThat(status.getNewFactoryPid(), is("org.example.Factory.new"));
  }

  @Test
  public void testGetNewPidReturnsNull() {
    assertThat(status.getNewPid(), is(nullValue()));
  }

  @Test
  public void testSetAndGetNewPid() {
    status.setNewPid("new-pid-123");
    assertThat(status.getNewPid(), is("new-pid-123"));
  }

  @Test
  public void testGetOriginalPidReturnsNull() {
    assertThat(status.getOriginalPid(), is(nullValue()));
  }

  @Test
  public void testSetAndGetOriginalPid() {
    status.setOriginalPid("original-pid-456");
    assertThat(status.getOriginalPid(), is("original-pid-456"));
  }

  @Test
  public void testGetOriginalFactoryPidReturnsNull() {
    assertThat(status.getOriginalFactoryPid(), is(nullValue()));
  }

  @Test
  public void testSetAndGetOriginalFactoryPid() {
    status.setOriginalFactoryPid("org.example.Factory.original");
    assertThat(status.getOriginalFactoryPid(), is("org.example.Factory.original"));
  }

  @Test
  public void testIsDisabledReturnsTrueForDisabledConfig() {
    status.setOriginalFactoryPid("org.example.Factory_disabled");
    assertThat(status.isDisabled(), is(true));
  }

  @Test
  public void testIsDisabledReturnsFalseForEnabledConfig() {
    status.setOriginalFactoryPid("org.example.Factory");
    assertThat(status.isDisabled(), is(false));
  }

  @Test
  public void testNewFactoryPidIsStoredInMap() {
    status.setNewFactoryPid("stored.factory.pid");
    assertThat(status.get(ConfigurationStatus.NEW_FACTORY_PID), is("stored.factory.pid"));
  }

  @Test
  public void testNewPidIsStoredInMap() {
    status.setNewPid("stored-pid");
    assertThat(status.get(ConfigurationStatus.NEW_PID), is("stored-pid"));
  }

  @Test
  public void testOriginalPidIsStoredInMap() {
    status.setOriginalPid("original-stored-pid");
    assertThat(status.get(ConfigurationStatus.ORIGINAL_PID), is("original-stored-pid"));
  }

  @Test
  public void testOriginalFactoryPidIsStoredInMap() {
    status.setOriginalFactoryPid("original.factory.pid");
    assertThat(status.get(ConfigurationStatus.ORIGINAL_FACTORY_PID), is("original.factory.pid"));
  }

  /** Test implementation of ConfigurationStatus that extends HashMap. */
  private static class TestConfigurationStatus extends HashMap<String, String>
      implements ConfigurationStatus {}
}
