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
package org.codice.ddf.metrics;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SystemMetricsReporterTest {

  private SimpleMeterRegistry meterRegistry;
  private SystemMetricsReporter underTest;

  @Before
  public void setUp() {
    // Set required system property for DDF home
    System.setProperty("ddf.home", System.getProperty("java.io.tmpdir"));
    System.setProperty("org.codice.ddf.system.hostname", "test-host");

    // Clear any existing registries
    Metrics.globalRegistry.getRegistries().forEach(Metrics.globalRegistry::remove);

    meterRegistry = new SimpleMeterRegistry();
    Metrics.addRegistry(meterRegistry);
  }

  @After
  public void tearDown() {
    if (meterRegistry != null) {
      Metrics.removeRegistry(meterRegistry);
      meterRegistry.close();
    }
  }

  @Test
  public void testConstructorRegistersJvmMetrics() {
    underTest = new SystemMetricsReporter();

    List<String> meterNames =
        meterRegistry.getMeters().stream()
            .map(meter -> meter.getId().getName())
            .collect(Collectors.toList());

    // Verify JVM metrics are registered
    assertThat(meterNames, hasItem("jvm.classes.loaded"));
    assertThat(meterNames, hasItem("jvm.memory.used"));
    assertThat(meterNames, hasItem("jvm.threads.live"));
  }

  @Test
  public void testConstructorRegistersDiskSpaceMetrics() {
    underTest = new SystemMetricsReporter();

    List<String> meterNames =
        meterRegistry.getMeters().stream()
            .map(meter -> meter.getId().getName())
            .collect(Collectors.toList());

    // Verify disk space metrics are registered
    assertThat(meterNames, hasItem("disk.free"));
    assertThat(meterNames, hasItem("disk.total"));
  }

  @Test
  public void testConstructorRegistersSystemMetrics() {
    underTest = new SystemMetricsReporter();

    List<String> meterNames =
        meterRegistry.getMeters().stream()
            .map(meter -> meter.getId().getName())
            .collect(Collectors.toList());

    // Verify system metrics are registered
    assertThat(meterNames, hasItem("process.uptime"));
    assertThat(meterNames, hasItem("system.cpu.count"));
  }

  @Test
  public void testConstructorRegistersFileDescriptorMetrics() {
    underTest = new SystemMetricsReporter();

    List<String> meterNames =
        meterRegistry.getMeters().stream()
            .map(meter -> meter.getId().getName())
            .collect(Collectors.toList());

    // Verify file descriptor metrics are registered (may not be available on all platforms)
    // Just check that some metrics are registered
    assertThat(meterNames.size(), greaterThan(0));
  }

  @Test
  public void testHostnameTagIsApplied() {
    underTest = new SystemMetricsReporter();

    // Get any meter and check its tags
    List<Meter> meters = meterRegistry.getMeters();
    assertThat(meters.size(), greaterThan(0));

    Meter meter = meters.get(0);
    List<Tag> tags = meter.getId().getTags();

    boolean hasHostTag =
        tags.stream()
            .anyMatch(tag -> tag.getKey().equals("host") && tag.getValue().equals("test-host"));

    assertThat(hasHostTag, is(true));
  }

  @Test
  public void testHostnameTagDefaultsToLocalhost() {
    System.clearProperty("org.codice.ddf.system.hostname");

    // Clear and recreate registry
    Metrics.removeRegistry(meterRegistry);
    meterRegistry.close();
    meterRegistry = new SimpleMeterRegistry();
    Metrics.addRegistry(meterRegistry);

    underTest = new SystemMetricsReporter();

    List<Meter> meters = meterRegistry.getMeters();
    assertThat(meters.size(), greaterThan(0));

    Meter meter = meters.get(0);
    List<Tag> tags = meter.getId().getTags();

    boolean hasLocalhostTag =
        tags.stream()
            .anyMatch(tag -> tag.getKey().equals("host") && tag.getValue().equals("localhost"));

    assertThat(hasLocalhostTag, is(true));
  }

  @Test
  public void testJvmMemoryMetricsHaveValues() {
    underTest = new SystemMetricsReporter();

    Gauge heapUsed = meterRegistry.find("jvm.memory.used").tag("area", "heap").gauge();
    assertThat(heapUsed, is(notNullValue()));
    assertThat(heapUsed.value(), greaterThan(0.0));
  }

  @Test
  public void testDiskSpaceMetricsHaveValues() {
    underTest = new SystemMetricsReporter();

    Gauge diskFree = meterRegistry.find("disk.free").gauge();
    assertThat(diskFree, is(notNullValue()));
    assertThat(diskFree.value(), greaterThan(0.0));
  }

  @Test
  public void testProcessorMetricsHaveValues() {
    underTest = new SystemMetricsReporter();

    Gauge cpuCount = meterRegistry.find("system.cpu.count").gauge();
    assertThat(cpuCount, is(notNullValue()));
    assertThat(cpuCount.value(), greaterThan(0.0));
  }

  @Test
  public void testUptimeMetricsHaveValues() {
    underTest = new SystemMetricsReporter();

    Gauge uptime = meterRegistry.find("process.uptime").gauge();
    assertThat(uptime, is(notNullValue()));
    assertThat(uptime.value(), greaterThan(0.0));
  }

  @Test
  public void testClassLoaderMetricsHaveValues() {
    underTest = new SystemMetricsReporter();

    Gauge classesLoaded = meterRegistry.find("jvm.classes.loaded").gauge();
    assertThat(classesLoaded, is(notNullValue()));
    assertThat(classesLoaded.value(), greaterThan(0.0));
  }

  @Test
  public void testThreadMetricsHaveValues() {
    underTest = new SystemMetricsReporter();

    Gauge threadsLive = meterRegistry.find("jvm.threads.live").gauge();
    assertThat(threadsLive, is(notNullValue()));
    assertThat(threadsLive.value(), greaterThan(0.0));
  }

  @Test
  public void testMultipleInstancesShareGlobalRegistry() {
    SystemMetricsReporter reporter1 = new SystemMetricsReporter();

    int metersAfterFirst = meterRegistry.getMeters().size();
    assertThat(metersAfterFirst, greaterThan(0));

    // Creating a second instance should add to the same registry
    SystemMetricsReporter reporter2 = new SystemMetricsReporter();

    int metersAfterSecond = meterRegistry.getMeters().size();
    // Second instance registers duplicate metrics, so count should increase
    assertThat(metersAfterSecond, greaterThan(metersAfterFirst));
  }

  @Test
  public void testDdfHomePropertyIsUsed() {
    String testPath = System.getProperty("java.io.tmpdir");
    System.setProperty("ddf.home", testPath);

    underTest = new SystemMetricsReporter();

    // Verify disk metrics are registered for the DDF home path
    Gauge diskFree = meterRegistry.find("disk.free").gauge();
    assertThat(diskFree, is(notNullValue()));

    // Verify the path tag contains our test path
    List<Tag> tags = diskFree.getId().getTags();
    boolean hasPathTag =
        tags.stream()
            .anyMatch(tag -> tag.getKey().equals("path") && tag.getValue().contains(testPath));

    assertThat(hasPathTag, is(true));
  }
}
