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
package org.codice.ddf.admin.insecure.defaults.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.osgi.service.event.EventAdmin;

@EnableRuleMigrationSupport
public class UsersPropertiesCollectorTest {

  private UsersPropertiesCollector collector;
  private EventAdmin eventAdmin;
  private DefaultUsersDeletionScheduler scheduler;

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @BeforeEach
  public void setUp() throws Exception {
    eventAdmin = mock(EventAdmin.class);
    scheduler = mock(DefaultUsersDeletionScheduler.class);
    collector = new UsersPropertiesCollector(eventAdmin, scheduler);

    File testFile = temporaryFolder.newFile("users.properties");
    DefaultUsersDeletionScheduler.setUsersPropertiesFilePath(Paths.get(testFile.getPath()));
  }

  @Test
  public void usersPropertiesNotice() throws Exception {
    collector.setUsersPropertiesDeletion(true);
    when(scheduler.defaultUsersExist()).thenReturn(true);
    when(scheduler.scheduleDeletion()).thenReturn(true);

    collector.run();

    verify(scheduler, times(1)).installationDate();
    verify(eventAdmin, times(1)).postEvent(any());
  }

  @Test
  public void usersPropertiesNoticeWithTimestamp() throws Exception {
    collector.setUsersPropertiesDeletion(true);
    when(scheduler.scheduleDeletion()).thenReturn(true);
    when(scheduler.defaultUsersExist()).thenReturn(true);
    when(scheduler.installationDate()).thenReturn(Instant.now());

    collector.run();

    verify(eventAdmin, times(1)).postEvent(any());
  }

  @Test
  public void deleteScheduledDeletionsTest() throws Exception {
    collector.setUsersPropertiesDeletion(false);
    collector.run();
    verify(scheduler, times(1)).deleteScheduledDeletions();
  }

  @AfterEach
  public void tearDown() throws Exception {
    temporaryFolder.delete();
  }
}
