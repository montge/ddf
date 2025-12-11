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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import org.codice.ddf.security.Security;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Tests the {@link CommandJobFactory} class.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Job creation via factory pattern
 *   <li>Security injection into jobs
 *   <li>Multiple job instantiation
 *   <li>Null safety for job creation
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class CommandJobFactoryTest {

  @Mock private Security mockSecurity;

  @Mock private TriggerFiredBundle mockTriggerBundle;

  @Mock private Scheduler mockScheduler;

  private CommandJobFactory factory;

  @BeforeEach
  public void setUp() {
    factory = new CommandJobFactory(mockSecurity);
  }

  @Test
  public void testConstructor() {
    CommandJobFactory testFactory = new CommandJobFactory(mockSecurity);

    assertThat(testFactory, is(notNullValue()));
  }

  @Test
  public void testNewJobCreatesCommandJob() throws SchedulerException {
    Job job = factory.newJob(mockTriggerBundle, mockScheduler);

    assertThat(job, is(notNullValue()));
    assertThat(job, is(instanceOf(CommandJob.class)));
  }

  @Test
  public void testNewJobWithNullTriggerBundle() throws SchedulerException {
    Job job = factory.newJob(null, mockScheduler);

    assertThat(job, is(notNullValue()));
    assertThat(job, is(instanceOf(CommandJob.class)));
  }

  @Test
  public void testNewJobWithNullScheduler() throws SchedulerException {
    Job job = factory.newJob(mockTriggerBundle, null);

    assertThat(job, is(notNullValue()));
    assertThat(job, is(instanceOf(CommandJob.class)));
  }

  @Test
  public void testNewJobWithNullArguments() throws SchedulerException {
    Job job = factory.newJob(null, null);

    assertThat(job, is(notNullValue()));
    assertThat(job, is(instanceOf(CommandJob.class)));
  }

  @Test
  public void testMultipleJobCreation() throws SchedulerException {
    Job job1 = factory.newJob(mockTriggerBundle, mockScheduler);
    Job job2 = factory.newJob(mockTriggerBundle, mockScheduler);
    Job job3 = factory.newJob(mockTriggerBundle, mockScheduler);

    assertThat(job1, is(notNullValue()));
    assertThat(job2, is(notNullValue()));
    assertThat(job3, is(notNullValue()));

    // Each job should be a new instance
    assertThat(job1 == job2, is(false));
    assertThat(job2 == job3, is(false));
    assertThat(job1 == job3, is(false));
  }

  @Test
  public void testNewJobCreatesJobWithSecurity() throws SchedulerException {
    Security security = mock(Security.class);
    CommandJobFactory securityFactory = new CommandJobFactory(security);

    Job job = securityFactory.newJob(mockTriggerBundle, mockScheduler);

    assertThat(job, is(notNullValue()));
    assertThat(job, is(instanceOf(CommandJob.class)));
  }

  @Test
  public void testFactoryWithNullSecurity() throws SchedulerException {
    CommandJobFactory nullSecurityFactory = new CommandJobFactory(null);

    // Should still create a job even with null security
    // The CommandJob constructor will handle null security
    Job job = nullSecurityFactory.newJob(mockTriggerBundle, mockScheduler);

    assertThat(job, is(notNullValue()));
  }

  @Test
  public void testConcurrentJobCreation() throws Exception {
    // Test thread safety of factory
    final int threadCount = 10;
    final Job[] jobs = new Job[threadCount];
    final Exception[] exceptions = new Exception[threadCount];

    Thread[] threads = new Thread[threadCount];
    for (int i = 0; i < threadCount; i++) {
      final int index = i;
      threads[i] =
          new Thread(
              () -> {
                try {
                  jobs[index] = factory.newJob(mockTriggerBundle, mockScheduler);
                } catch (Exception e) {
                  exceptions[index] = e;
                }
              });
    }

    // Start all threads
    for (Thread thread : threads) {
      thread.start();
    }

    // Wait for all threads to complete
    for (Thread thread : threads) {
      thread.join();
    }

    // Verify all jobs were created without exceptions
    for (int i = 0; i < threadCount; i++) {
      assertThat(exceptions[i], is(nullValue()));
      assertThat(jobs[i], is(notNullValue()));
      assertThat(jobs[i], is(instanceOf(CommandJob.class)));
    }
  }
}
