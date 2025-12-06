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
package ddf.catalog.source.solr;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationStoreTest {

  private ConfigurationStore configStore;

  @Before
  public void setup() {
    // Get the singleton instance
    configStore = ConfigurationStore.getInstance();
  }

  @Test
  public void testGetInstance() {
    // when getting the instance
    ConfigurationStore instance1 = ConfigurationStore.getInstance();
    ConfigurationStore instance2 = ConfigurationStore.getInstance();

    // then both instances should be the same
    assertThat(instance1, is(notNullValue()));
    assertThat(instance2, is(notNullValue()));
    assertThat(instance1, is(sameInstance(instance2)));
  }

  @Test
  public void testSetAndGetDataDirectoryPath() {
    // given a data directory path
    String testPath = "/test/data/directory";

    // when setting the data directory path
    configStore.setDataDirectoryPath(testPath);

    // then the path should be retrievable
    assertThat(configStore.getDataDirectoryPath(), is(testPath));
  }

  @Test
  public void testSetAndGetForceAutoCommit() {
    // given a force auto commit value
    boolean forceAutoCommit = true;

    // when setting force auto commit
    configStore.setForceAutoCommit(forceAutoCommit);

    // then the value should be retrievable
    assertThat(configStore.isForceAutoCommit(), is(forceAutoCommit));
  }

  @Test
  public void testSetAndGetInMemory() {
    // given an in-memory value
    boolean inMemory = true;

    // when setting in-memory mode
    configStore.setInMemory(inMemory);

    // then the value should be retrievable
    assertThat(configStore.isInMemory(), is(inMemory));
  }

  @Test
  public void testSetAndGetNearestNeighborDistanceLimit() {
    // given a distance limit
    Double distanceLimit = 100.5;

    // when setting the nearest neighbor distance limit
    configStore.setNearestNeighborDistanceLimit(distanceLimit);

    // then the value should be retrievable
    assertThat(configStore.getNearestNeighborDistanceLimit(), is(distanceLimit));
  }

  @Test
  public void testSetNearestNeighborDistanceLimitWithNegativeValue() {
    // given a negative distance limit
    Double negativeDistanceLimit = -50.0;

    // when setting the nearest neighbor distance limit with negative value
    configStore.setNearestNeighborDistanceLimit(negativeDistanceLimit);

    // then the absolute value should be stored
    assertThat(configStore.getNearestNeighborDistanceLimit(), is(50.0));
  }

  @Test
  public void testAddConfigurationListener() {
    // given a configuration listener
    ConfigurationListener listener = mock(ConfigurationListener.class);

    // when adding the listener
    configStore.addConfigurationListener(listener);

    // and updating configuration
    configStore.setDataDirectoryPath("/new/path");

    // then the listener should be notified
    verify(listener, times(1)).configurationUpdated();
  }

  @Test
  public void testRemoveConfigurationListener() {
    // given a configuration listener that has been added
    ConfigurationListener listener = mock(ConfigurationListener.class);
    configStore.addConfigurationListener(listener);

    // when removing the listener
    configStore.removeConfigurationListener(listener);

    // and updating configuration
    configStore.setDataDirectoryPath("/another/path");

    // then the listener should not be notified
    verifyNoInteractions(listener);
  }

  @Test
  public void testMultipleListenersNotified() {
    // given multiple configuration listeners
    ConfigurationListener listener1 = mock(ConfigurationListener.class);
    ConfigurationListener listener2 = mock(ConfigurationListener.class);
    ConfigurationListener listener3 = mock(ConfigurationListener.class);

    // when adding all listeners
    configStore.addConfigurationListener(listener1);
    configStore.addConfigurationListener(listener2);
    configStore.addConfigurationListener(listener3);

    // and updating configuration
    configStore.setNearestNeighborDistanceLimit(200.0);

    // then all listeners should be notified
    verify(listener1, times(1)).configurationUpdated();
    verify(listener2, times(1)).configurationUpdated();
    verify(listener3, times(1)).configurationUpdated();
  }

  @Test
  public void testListenersNotifiedOnDataDirectoryPathChange() {
    // given a configuration listener
    ConfigurationListener listener = mock(ConfigurationListener.class);
    configStore.addConfigurationListener(listener);

    // when changing data directory path
    configStore.setDataDirectoryPath("/test/path");

    // then listener should be notified
    verify(listener, times(1)).configurationUpdated();
  }

  @Test
  public void testListenersNotifiedOnInMemoryChange() {
    // given a configuration listener
    ConfigurationListener listener = mock(ConfigurationListener.class);
    configStore.addConfigurationListener(listener);

    // when changing in-memory mode
    configStore.setInMemory(true);

    // then listener should be notified
    verify(listener, times(1)).configurationUpdated();
  }

  @Test
  public void testListenersNotifiedOnNearestNeighborDistanceLimitChange() {
    // given a configuration listener
    ConfigurationListener listener = mock(ConfigurationListener.class);
    configStore.addConfigurationListener(listener);

    // when changing nearest neighbor distance limit
    configStore.setNearestNeighborDistanceLimit(150.0);

    // then listener should be notified
    verify(listener, times(1)).configurationUpdated();
  }

  @Test
  public void testForceAutoCommitDoesNotNotifyListeners() {
    // given a configuration listener
    ConfigurationListener listener = mock(ConfigurationListener.class);
    configStore.addConfigurationListener(listener);

    // when changing force auto commit (which doesn't notify listeners)
    configStore.setForceAutoCommit(true);

    // then listener should not be notified
    verifyNoInteractions(listener);
  }

  @Test
  public void testMultipleConfigurationUpdatesNotifyListenersMultipleTimes() {
    // given a configuration listener
    ConfigurationListener listener = mock(ConfigurationListener.class);
    configStore.addConfigurationListener(listener);

    // when making multiple configuration changes
    configStore.setDataDirectoryPath("/path1");
    configStore.setInMemory(true);
    configStore.setNearestNeighborDistanceLimit(100.0);

    // then listener should be notified for each change
    verify(listener, times(3)).configurationUpdated();
  }
}
