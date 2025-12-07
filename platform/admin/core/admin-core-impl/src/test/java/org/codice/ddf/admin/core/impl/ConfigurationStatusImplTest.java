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
package org.codice.ddf.admin.core.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class ConfigurationStatusImplTest {

  @Test
  public void testConstructorWithAllParameters() {
    String newFactoryPid = "new.factory.pid";
    String newPid = "new.pid";
    String originalFactoryPid = "original.factory.pid";
    String originalPid = "original.pid";

    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl(newFactoryPid, newPid, originalFactoryPid, originalPid);

    assertThat(status.getNewFactoryPid(), is(equalTo(newFactoryPid)));
    assertThat(status.getNewPid(), is(equalTo(newPid)));
    assertThat(status.getOriginalFactoryPid(), is(equalTo(originalFactoryPid)));
    assertThat(status.getOriginalPid(), is(equalTo(originalPid)));
  }

  @Test
  public void testConstructorWithNullValues() {
    ConfigurationStatusImpl status = new ConfigurationStatusImpl(null, null, null, null);

    assertThat(status.getNewFactoryPid(), is(nullValue()));
    assertThat(status.getNewPid(), is(nullValue()));
    assertThat(status.getOriginalFactoryPid(), is(nullValue()));
    assertThat(status.getOriginalPid(), is(nullValue()));
  }

  @Test
  public void testSettersAndGetters() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl("old.fpid", "old.pid", "old.orig.fpid", "old.orig.pid");

    String newFactoryPid = "updated.factory.pid";
    String newPid = "updated.pid";
    String originalFactoryPid = "updated.original.factory.pid";
    String originalPid = "updated.original.pid";

    status.setNewFactoryPid(newFactoryPid);
    status.setNewPid(newPid);
    status.setOriginalFactoryPid(originalFactoryPid);
    status.setOriginalPid(originalPid);

    assertThat(status.getNewFactoryPid(), is(equalTo(newFactoryPid)));
    assertThat(status.getNewPid(), is(equalTo(newPid)));
    assertThat(status.getOriginalFactoryPid(), is(equalTo(originalFactoryPid)));
    assertThat(status.getOriginalPid(), is(equalTo(originalPid)));
  }

  @Test
  public void testIsDisabledWhenDisabled() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl(
            "new.factory.pid", "new.pid", "original.factory.pid_disabled", "original.pid");

    assertThat(status.isDisabled(), is(true));
  }

  @Test
  public void testIsDisabledWhenEnabled() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl(
            "new.factory.pid", "new.pid", "original.factory.pid", "original.pid");

    assertThat(status.isDisabled(), is(false));
  }

  @Test
  public void testMapBehavior() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl("newFPid", "newPid", "origFPid", "origPid");

    assertThat(status.containsKey("newFactoryPid"), is(true));
    assertThat(status.containsKey("newPid"), is(true));
    assertThat(status.containsKey("originalFactoryPid"), is(true));
    assertThat(status.containsKey("originalPid"), is(true));
    assertThat(status.size(), is(4));
  }

  @Test
  public void testEmptyStrings() {
    ConfigurationStatusImpl status = new ConfigurationStatusImpl("", "", "", "");

    assertThat(status.getNewFactoryPid(), is(equalTo("")));
    assertThat(status.getNewPid(), is(equalTo("")));
    assertThat(status.getOriginalFactoryPid(), is(equalTo("")));
    assertThat(status.getOriginalPid(), is(equalTo("")));
  }

  @Test
  public void testIsDisabledWithEmptyOriginalFactoryPid() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl("newFPid", "newPid", "", "origPid");

    assertThat(status.isDisabled(), is(false));
  }

  @Test
  public void testIsDisabledWithDisabledSuffix() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl(
            "enabled.factory.pid", "enabled.pid", "some.service_disabled", "some.pid");

    assertThat(status.isDisabled(), is(true));
    assertThat(status.getOriginalFactoryPid().endsWith("_disabled"), is(true));
  }

  @Test
  public void testIsDisabledWithMultipleUnderscores() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl("new.fpid", "new.pid", "some_factory_pid_disabled", "orig.pid");

    assertThat(status.isDisabled(), is(true));
  }

  @Test
  public void testIsDisabledWithDisabledInMiddle() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl("new.fpid", "new.pid", "some_disabled_factory.pid", "orig.pid");

    assertThat(status.isDisabled(), is(false));
  }

  @Test
  public void testUpdateFromEnabledToDisabled() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl(
            "test.factory.pid_disabled", "test.pid", "test.factory.pid", "test.pid");

    assertThat(status.getNewFactoryPid(), is(equalTo("test.factory.pid_disabled")));
    assertThat(status.getOriginalFactoryPid(), is(equalTo("test.factory.pid")));
    assertThat(status.isDisabled(), is(false));
  }

  @Test
  public void testUpdateFromDisabledToEnabled() {
    ConfigurationStatusImpl status =
        new ConfigurationStatusImpl(
            "test.factory.pid", "test.pid", "test.factory.pid_disabled", "test.pid");

    assertThat(status.getNewFactoryPid(), is(equalTo("test.factory.pid")));
    assertThat(status.getOriginalFactoryPid(), is(equalTo("test.factory.pid_disabled")));
    assertThat(status.isDisabled(), is(true));
  }
}
