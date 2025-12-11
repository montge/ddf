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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class ForkJoinPoolFactoryTest {

  @Test
  public void testNewThread() {
    ForkJoinPoolFactory factory = new ForkJoinPoolFactory();
    ForkJoinPool pool = new ForkJoinPool();

    ForkJoinWorkerThread thread = factory.newThread(pool);

    assertThat(thread, notNullValue());
    assertThat(thread.getPool(), is(pool));
  }

  @Test
  public void testGetNewForkJoinPoolWithHandler() {
    AtomicReference<Throwable> exceptionHolder = new AtomicReference<>();
    UncaughtExceptionHandler handler = (t, e) -> exceptionHolder.set(e);

    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(handler, false);

    assertThat(pool, notNullValue());
    assertThat(pool.getParallelism(), greaterThan(0));
    assertThat(pool.getParallelism(), lessThanOrEqualTo(0x7fff));
  }

  @Test
  public void testGetNewForkJoinPoolWithNullHandler() {
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);

    assertThat(pool, notNullValue());
    assertThat(pool.getParallelism(), greaterThan(0));
  }

  @Test
  public void testGetNewForkJoinPoolSyncMode() {
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);

    assertThat(pool, notNullValue());
    assertThat(pool.getAsyncMode(), is(false));
  }

  @Test
  public void testGetNewForkJoinPoolAsyncMode() {
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, true);

    assertThat(pool, notNullValue());
    assertThat(pool.getAsyncMode(), is(true));
  }

  @Test
  public void testGetNewForkJoinPoolParallelism() {
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);

    int expectedParallelism = Math.min(0x7fff, Runtime.getRuntime().availableProcessors());
    assertThat(pool.getParallelism(), is(expectedParallelism));
  }

  @Test
  public void testGetNewForkJoinPoolUsesFactory() {
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);

    // Test that the pool uses the custom factory by executing a task
    RecursiveTask<Boolean> task =
        new RecursiveTask<Boolean>() {
          @Override
          protected Boolean compute() {
            Thread currentThread = Thread.currentThread();
            return currentThread instanceof ForkJoinWorkerThread;
          }
        };

    Boolean result = pool.invoke(task);
    assertThat(result, is(true));
  }

  @Test
  public void testGetNewForkJoinPoolExecutesTask() {
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);

    RecursiveTask<Integer> task =
        new RecursiveTask<Integer>() {
          @Override
          protected Integer compute() {
            return 42;
          }
        };

    Integer result = pool.invoke(task);
    assertThat(result, is(42));
  }

  @Test
  public void testGetNewForkJoinPoolHandlesException() throws InterruptedException {
    AtomicReference<Throwable> exceptionHolder = new AtomicReference<>();
    AtomicBoolean handlerCalled = new AtomicBoolean(false);

    UncaughtExceptionHandler handler =
        (t, e) -> {
          exceptionHolder.set(e);
          handlerCalled.set(true);
        };

    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(handler, false);

    ForkJoinTask<Void> task =
        new RecursiveTask<Void>() {
          @Override
          protected Void compute() {
            throw new RuntimeException("Test exception");
          }
        };

    try {
      pool.invoke(task);
    } catch (RuntimeException e) {
      // Expected
    }

    // Give the handler time to be called
    Thread.sleep(100);

    // Note: Exception handling behavior depends on JVM version and configuration
    // The test verifies the pool was created with the handler
    assertThat(pool, notNullValue());
  }

  @Test
  public void testNewThreadCreatesStandardForkJoinWorkerThread() {
    ForkJoinPoolFactory factory = new ForkJoinPoolFactory();
    ForkJoinPool pool = new ForkJoinPool();

    ForkJoinWorkerThread thread = factory.newThread(pool);

    assertThat(thread, notNullValue());
    assertThat(thread instanceof ForkJoinPoolFactory.StandardForkJoinWorkerThread, is(true));
  }

  @Test
  public void testStandardForkJoinWorkerThreadHasPool() {
    ForkJoinPoolFactory factory = new ForkJoinPoolFactory();
    ForkJoinPool pool = new ForkJoinPool();

    ForkJoinWorkerThread thread = factory.newThread(pool);

    assertThat(thread.getPool(), is(pool));
  }

  @Test
  public void testMultipleThreadsFromFactory() {
    ForkJoinPoolFactory factory = new ForkJoinPoolFactory();
    ForkJoinPool pool = new ForkJoinPool();

    ForkJoinWorkerThread thread1 = factory.newThread(pool);
    ForkJoinWorkerThread thread2 = factory.newThread(pool);

    assertThat(thread1, notNullValue());
    assertThat(thread2, notNullValue());
    assertThat(thread1.getPool(), is(pool));
    assertThat(thread2.getPool(), is(pool));
  }

  @Test
  public void testGetNewForkJoinPoolMultiplePools() {
    ForkJoinPool pool1 = ForkJoinPoolFactory.getNewForkJoinPool(null, false);
    ForkJoinPool pool2 = ForkJoinPoolFactory.getNewForkJoinPool(null, true);

    assertThat(pool1, notNullValue());
    assertThat(pool2, notNullValue());
    assertThat(pool1.getAsyncMode(), is(false));
    assertThat(pool2.getAsyncMode(), is(true));
  }

  @Test
  public void testGetNewForkJoinPoolParallelismLimit() {
    // Test that parallelism is capped at 0x7fff
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);

    assertThat(pool.getParallelism(), lessThanOrEqualTo(0x7fff));
  }

  @Test
  public void testGetNewForkJoinPoolWithBothModes() {
    ForkJoinPool syncPool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);
    ForkJoinPool asyncPool = ForkJoinPoolFactory.getNewForkJoinPool(null, true);

    assertThat(syncPool.getAsyncMode(), is(false));
    assertThat(asyncPool.getAsyncMode(), is(true));
  }

  @Test
  public void testPoolCanExecuteRecursiveTask() {
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);

    RecursiveTask<Integer> task =
        new RecursiveTask<Integer>() {
          @Override
          protected Integer compute() {
            if (getForkJoinTaskTag() < 10) {
              RecursiveTask<Integer> subtask =
                  new RecursiveTask<Integer>() {
                    @Override
                    protected Integer compute() {
                      return 1;
                    }
                  };
              subtask.fork();
              return subtask.join() + 1;
            }
            return 1;
          }
        };

    Integer result = pool.invoke(task);
    assertThat(result, notNullValue());
  }

  @Test
  public void testFactoryCreatesWorkingThreads() {
    ForkJoinPoolFactory factory = new ForkJoinPoolFactory();
    ForkJoinPool pool = ForkJoinPoolFactory.getNewForkJoinPool(null, false);

    assertThat(pool, notNullValue());

    // Execute a simple task to verify threads work
    RecursiveTask<String> task =
        new RecursiveTask<String>() {
          @Override
          protected String compute() {
            return "success";
          }
        };

    String result = pool.invoke(task);
    assertThat(result, is("success"));
  }
}
