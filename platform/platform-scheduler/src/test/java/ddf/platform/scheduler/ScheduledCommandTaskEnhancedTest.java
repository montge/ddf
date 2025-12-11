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
package ddf.platform.scheduler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.codice.ddf.security.Security;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

/**
 * Enhanced tests for {@link ScheduledCommandTask} class.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Task creation and scheduling with Quartz
 *   <li>CRON expression-based scheduling
 *   <li>Simple interval-based scheduling
 *   <li>Task update and rescheduling
 *   <li>Task deletion
 *   <li>Configuration property handling
 *   <li>Error handling during scheduling operations
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class ScheduledCommandTaskEnhancedTest {

  @Mock private Scheduler mockScheduler;

  @Mock private CommandJobFactory mockJobFactory;

  private ScheduledCommandTask scheduledTask;

  @BeforeEach
  public void setUp() throws SchedulerException {
    scheduledTask = new ScheduledCommandTask(mockScheduler, mockJobFactory);
    verify(mockScheduler).setJobFactory(mockJobFactory);
  }

  @Test
  public void testConstructorSetsJobFactory() throws SchedulerException {
    Scheduler scheduler = mock(Scheduler.class);
    CommandJobFactory factory = new CommandJobFactory(mock(Security.class));

    new ScheduledCommandTask(scheduler, factory);

    verify(scheduler).setJobFactory(factory);
  }

  @Test
  public void testSetAndGetCommand() {
    String command = "catalog:search";

    scheduledTask.setCommand(command);

    assertThat(scheduledTask.getCommand(), is(command));
  }

  @Test
  public void testSetCommandWithNull() {
    scheduledTask.setCommand(null);

    // Should not throw exception
  }

  @Test
  public void testSetAndGetIntervalString() {
    String interval = "0 0/5 * * * ?";

    scheduledTask.setIntervalString(interval);

    assertThat(scheduledTask.getIntervalString(), is(interval));
  }

  @Test
  public void testSetIntervalStringWithEmpty() {
    String originalInterval = scheduledTask.getIntervalString();
    scheduledTask.setIntervalString("");

    // Empty string should be ignored, keeping default
    assertThat(scheduledTask.getIntervalString(), is(originalInterval));
  }

  @Test
  public void testSetIntervalStringWithNull() {
    String originalInterval = scheduledTask.getIntervalString();
    scheduledTask.setIntervalString(null);

    // Null should be ignored, keeping default
    assertThat(scheduledTask.getIntervalString(), is(originalInterval));
  }

  @Test
  public void testSetAndGetIntervalType() {
    scheduledTask.setIntervalType(ScheduledCommandTask.SECOND_INTERVAL);

    assertThat(scheduledTask.getIntervalType(), is(ScheduledCommandTask.SECOND_INTERVAL));
  }

  @Test
  public void testSetIntervalTypeWithEmpty() {
    String originalType = scheduledTask.getIntervalType();
    scheduledTask.setIntervalType("");

    // Empty string should be ignored
    assertThat(scheduledTask.getIntervalType(), is(originalType));
  }

  @Test
  public void testSetIntervalTypeWithNull() {
    String originalType = scheduledTask.getIntervalType();
    scheduledTask.setIntervalType(null);

    // Null should be ignored
    assertThat(scheduledTask.getIntervalType(), is(originalType));
  }

  @Test
  public void testDefaultIntervalType() {
    assertThat(scheduledTask.getIntervalType(), is(ScheduledCommandTask.CRON_STRING));
  }

  @Test
  public void testDefaultIntervalString() {
    assertThat(scheduledTask.getIntervalString(), is(ScheduledCommandTask.ONE_DAY));
  }

  @Test
  public void testNewTaskWithCronSchedule() throws SchedulerException {
    scheduledTask.setCommand("test:command");
    scheduledTask.setIntervalType(ScheduledCommandTask.CRON_STRING);
    scheduledTask.setIntervalString("0 0 * * * ?");

    when(mockScheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
        .thenReturn(new Date());

    scheduledTask.newTask();

    ArgumentCaptor<JobDetail> jobCaptor = ArgumentCaptor.forClass(JobDetail.class);
    ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);
    verify(mockScheduler).scheduleJob(jobCaptor.capture(), triggerCaptor.capture());

    JobDetail capturedJob = jobCaptor.getValue();
    assertThat(capturedJob, is(notNullValue()));
    assertThat(capturedJob.getJobDataMap().getString(CommandJob.COMMAND_KEY), is("test:command"));
  }

  @Test
  public void testNewTaskWithSecondInterval() throws SchedulerException {
    scheduledTask.setCommand("test:command");
    scheduledTask.setIntervalType(ScheduledCommandTask.SECOND_INTERVAL);
    scheduledTask.setIntervalString("60");

    when(mockScheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
        .thenReturn(new Date());

    scheduledTask.newTask();

    verify(mockScheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
  }

  @Test
  public void testNewTaskWithInvalidSecondInterval() throws SchedulerException {
    scheduledTask.setCommand("test:command");
    scheduledTask.setIntervalType(ScheduledCommandTask.SECOND_INTERVAL);
    scheduledTask.setIntervalString("invalid");

    scheduledTask.newTask();

    // Should not schedule a job with invalid interval
    verify(mockScheduler, times(0)).scheduleJob(any(JobDetail.class), any(Trigger.class));
  }

  @Test
  public void testNewTaskWithUnknownIntervalType() throws SchedulerException {
    scheduledTask.setCommand("test:command");
    scheduledTask.setIntervalType("UNKNOWN_TYPE");

    scheduledTask.newTask();

    // Should not schedule a job with unknown interval type
    verify(mockScheduler, times(0)).scheduleJob(any(JobDetail.class), any(Trigger.class));
  }

  @Test
  public void testNewTaskHandlesSchedulerException() throws SchedulerException {
    scheduledTask.setCommand("test:command");

    when(mockScheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
        .thenThrow(new SchedulerException("Test exception"));

    scheduledTask.newTask();

    // Should handle exception gracefully without propagating
  }

  @Test
  public void testDeleteTask() throws SchedulerException {
    // First create a task
    scheduledTask.setCommand("test:command");
    when(mockScheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
        .thenReturn(new Date());
    scheduledTask.newTask();

    // Then delete it
    when(mockScheduler.deleteJob(any(JobKey.class))).thenReturn(true);

    scheduledTask.deleteTask(0);

    verify(mockScheduler).deleteJob(any(JobKey.class));
  }

  @Test
  public void testDeleteTaskHandlesSchedulerException() throws SchedulerException {
    scheduledTask.setCommand("test:command");
    when(mockScheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
        .thenReturn(new Date());
    scheduledTask.newTask();

    when(mockScheduler.deleteJob(any(JobKey.class)))
        .thenThrow(new SchedulerException("Test exception"));

    scheduledTask.deleteTask(0);

    // Should handle exception gracefully
  }

  @Test
  public void testUpdateTaskWithEmptyProperties() {
    Map<String, Object> emptyProps = new HashMap<>();

    scheduledTask.updateTask(emptyProps);

    // Should handle empty properties without error
  }

  @Test
  public void testUpdateTaskWithNullProperties() {
    scheduledTask.updateTask(null);

    // Should handle null properties without error
  }

  @Test
  public void testUpdateTaskWithCommand() throws SchedulerException {
    scheduledTask.setCommand("original:command");
    when(mockScheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
        .thenReturn(new Date());
    scheduledTask.newTask();

    Map<String, Object> properties = new HashMap<>();
    properties.put(CommandJob.COMMAND_KEY, "updated:command");

    doNothing().when(mockScheduler).addJob(any(JobDetail.class), eq(true));
    when(mockScheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class)))
        .thenReturn(new Date());

    scheduledTask.updateTask(properties);

    assertThat(scheduledTask.getCommand(), is("updated:command"));
    verify(mockScheduler).addJob(any(JobDetail.class), eq(true));
    verify(mockScheduler).rescheduleJob(any(TriggerKey.class), any(Trigger.class));
  }

  @Test
  public void testUpdateTaskWithIntervalString() throws SchedulerException {
    scheduledTask.newTask();

    Map<String, Object> properties = new HashMap<>();
    properties.put(ScheduledCommandTask.INTERVAL_STRING, "0 0 12 * * ?");

    doNothing().when(mockScheduler).addJob(any(JobDetail.class), eq(true));
    when(mockScheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class)))
        .thenReturn(new Date());

    scheduledTask.updateTask(properties);

    assertThat(scheduledTask.getIntervalString(), is("0 0 12 * * ?"));
  }

  @Test
  public void testUpdateTaskWithIntervalType() throws SchedulerException {
    scheduledTask.newTask();

    Map<String, Object> properties = new HashMap<>();
    properties.put(ScheduledCommandTask.INTERVAL_TYPE, ScheduledCommandTask.SECOND_INTERVAL);
    properties.put(ScheduledCommandTask.INTERVAL_STRING, "120");

    doNothing().when(mockScheduler).addJob(any(JobDetail.class), eq(true));
    when(mockScheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class)))
        .thenReturn(new Date());

    scheduledTask.updateTask(properties);

    assertThat(scheduledTask.getIntervalType(), is(ScheduledCommandTask.SECOND_INTERVAL));
  }

  @Test
  public void testUpdateTaskWithMultipleProperties() throws SchedulerException {
    scheduledTask.newTask();

    Map<String, Object> properties = new HashMap<>();
    properties.put(CommandJob.COMMAND_KEY, "new:command");
    properties.put(ScheduledCommandTask.INTERVAL_STRING, "0 0/15 * * * ?");
    properties.put(ScheduledCommandTask.INTERVAL_TYPE, ScheduledCommandTask.CRON_STRING);

    doNothing().when(mockScheduler).addJob(any(JobDetail.class), eq(true));
    when(mockScheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class)))
        .thenReturn(new Date());

    scheduledTask.updateTask(properties);

    assertThat(scheduledTask.getCommand(), is("new:command"));
    assertThat(scheduledTask.getIntervalString(), is("0 0/15 * * * ?"));
    assertThat(scheduledTask.getIntervalType(), is(ScheduledCommandTask.CRON_STRING));
  }

  @Test
  public void testUpdateTaskHandlesSchedulerException() throws SchedulerException {
    scheduledTask.newTask();

    Map<String, Object> properties = new HashMap<>();
    properties.put(CommandJob.COMMAND_KEY, "test:command");

    doThrow(new SchedulerException("Test exception"))
        .when(mockScheduler)
        .addJob(any(JobDetail.class), eq(true));

    scheduledTask.updateTask(properties);

    // Should handle exception gracefully
  }

  @Test
  public void testUpdateTaskWithInvalidIntervalType() throws SchedulerException {
    scheduledTask.newTask();

    Map<String, Object> properties = new HashMap<>();
    properties.put(ScheduledCommandTask.INTERVAL_TYPE, "INVALID_TYPE");

    scheduledTask.updateTask(properties);

    // Should not reschedule with invalid interval type
    verify(mockScheduler, times(0)).rescheduleJob(any(TriggerKey.class), any(Trigger.class));
  }

  @Test
  public void testUpdateTaskWithInvalidSecondInterval() throws SchedulerException {
    scheduledTask.newTask();

    Map<String, Object> properties = new HashMap<>();
    properties.put(ScheduledCommandTask.INTERVAL_TYPE, ScheduledCommandTask.SECOND_INTERVAL);
    properties.put(ScheduledCommandTask.INTERVAL_STRING, "not_a_number");

    scheduledTask.updateTask(properties);

    // Should not reschedule with invalid second interval
    verify(mockScheduler, times(0)).rescheduleJob(any(TriggerKey.class), any(Trigger.class));
  }

  @Test
  public void testScheduledCommandTaskConstants() {
    assertThat(ScheduledCommandTask.SECOND_INTERVAL, is("secondInterval"));
    assertThat(ScheduledCommandTask.CRON_STRING, is("cronString"));
    assertThat(ScheduledCommandTask.INTERVAL_STRING, is("intervalString"));
    assertThat(ScheduledCommandTask.INTERVAL_TYPE, is("intervalType"));
    assertThat(ScheduledCommandTask.ONE_DAY, is("0 0 0 1/1 * ? *"));
  }
}
