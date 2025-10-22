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
package org.codice.ddf.platform.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class StandardThreadFactoryBuilderTest {

  @Test
  public void testNewThreadFactoryWithNameOnly() {
    String threadName = "test-thread";
    ThreadFactory factory = StandardThreadFactoryBuilder.newThreadFactory(threadName);

    assertThat(factory, notNullValue());

    Thread thread = factory.newThread(() -> {});
    assertThat(thread, notNullValue());
    assertThat(thread.getName(), startsWith(threadName));
    assertThat(thread.isDaemon(), is(true));
    assertThat(thread.getPriority(), is(Thread.NORM_PRIORITY));
  }

  @Test
  public void testNewThreadFactoryWithAllParameters() {
    String threadName = "custom-thread";
    boolean isDaemon = false;
    int priority = Thread.MAX_PRIORITY;

    ThreadFactory factory =
        StandardThreadFactoryBuilder.newThreadFactory(threadName, isDaemon, priority);

    assertThat(factory, notNullValue());

    Thread thread = factory.newThread(() -> {});
    assertThat(thread, notNullValue());
    assertThat(thread.getName(), startsWith(threadName));
    assertThat(thread.isDaemon(), is(false));
    assertThat(thread.getPriority(), is(Thread.MAX_PRIORITY));
  }

  @Test
  public void testNewThreadFactoryWithNullName() {
    ThreadFactory factory = StandardThreadFactoryBuilder.newThreadFactory(null);

    assertThat(factory, notNullValue());

    Thread thread = factory.newThread(() -> {});
    assertThat(thread, notNullValue());
    assertThat(thread.getName(), startsWith("thread"));
  }

  @Test
  public void testNewThreadFactoryWithEmptyName() {
    ThreadFactory factory = StandardThreadFactoryBuilder.newThreadFactory("");

    assertThat(factory, notNullValue());

    Thread thread = factory.newThread(() -> {});
    assertThat(thread, notNullValue());
    assertThat(thread.getName(), startsWith("thread"));
  }

  @Test
  public void testThreadFactoryIncrementsThreadNames() {
    String threadName = "numbered-thread";
    ThreadFactory factory = StandardThreadFactoryBuilder.newThreadFactory(threadName);

    Thread thread1 = factory.newThread(() -> {});
    Thread thread2 = factory.newThread(() -> {});
    Thread thread3 = factory.newThread(() -> {});

    assertThat(thread1.getName(), is(threadName + " 0"));
    assertThat(thread2.getName(), is(threadName + " 1"));
    assertThat(thread3.getName(), is(threadName + " 2"));
  }

  @Test
  public void testThreadFactoryWithMinPriority() {
    ThreadFactory factory =
        StandardThreadFactoryBuilder.newThreadFactory("min-priority", true, Thread.MIN_PRIORITY);

    Thread thread = factory.newThread(() -> {});
    assertThat(thread.getPriority(), is(Thread.MIN_PRIORITY));
  }

  @Test
  public void testThreadFactoryCreatesDaemonThreads() {
    ThreadFactory factory =
        StandardThreadFactoryBuilder.newThreadFactory("daemon-thread", true, Thread.NORM_PRIORITY);

    Thread thread = factory.newThread(() -> {});
    assertThat(thread.isDaemon(), is(true));
  }

  @Test
  public void testThreadFactoryCreatesNonDaemonThreads() {
    ThreadFactory factory =
        StandardThreadFactoryBuilder.newThreadFactory("non-daemon", false, Thread.NORM_PRIORITY);

    Thread thread = factory.newThread(() -> {});
    assertThat(thread.isDaemon(), is(false));
  }

  @Test
  public void testThreadFactoryWithRunnable() {
    ThreadFactory factory = StandardThreadFactoryBuilder.newThreadFactory("runnable-test");
    AtomicInteger counter = new AtomicInteger(0);

    Runnable runnable = () -> counter.incrementAndGet();
    Thread thread = factory.newThread(runnable);

    assertThat(thread, notNullValue());
    thread.start();

    try {
      thread.join(1000); // Wait up to 1 second
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertThat(counter.get(), is(1));
  }

  @Test
  public void testStandardThreadFactoryBuilderDefaultNewThread() {
    StandardThreadFactoryBuilder builder = new StandardThreadFactoryBuilder();
    Thread thread = builder.newThread(() -> {});

    assertThat(thread, notNullValue());
  }

  @Test
  public void testMultipleFactoriesIndependentCounters() {
    ThreadFactory factory1 = StandardThreadFactoryBuilder.newThreadFactory("factory1");
    ThreadFactory factory2 = StandardThreadFactoryBuilder.newThreadFactory("factory2");

    Thread thread1 = factory1.newThread(() -> {});
    Thread thread2 = factory2.newThread(() -> {});
    Thread thread3 = factory1.newThread(() -> {});
    Thread thread4 = factory2.newThread(() -> {});

    assertThat(thread1.getName(), is("factory1 0"));
    assertThat(thread2.getName(), is("factory2 0"));
    assertThat(thread3.getName(), is("factory1 1"));
    assertThat(thread4.getName(), is("factory2 1"));
  }

  @Test
  public void testThreadFactoryWithSpecialCharactersInName() {
    String threadName = "test-thread_with-special@chars#123";
    ThreadFactory factory = StandardThreadFactoryBuilder.newThreadFactory(threadName);

    Thread thread = factory.newThread(() -> {});
    assertThat(thread.getName(), containsString(threadName));
  }
}
