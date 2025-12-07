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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.management.ManagementFactory;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.After;
import org.junit.Test;

public class BasicMBeanTest {

  private static final String TEST_OBJECT_NAME = "org.codice.ddf:type=TestMBean";

  private TestMBeanImpl testMBean;

  @After
  public void tearDown() throws Exception {
    if (testMBean != null) {
      try {
        testMBean.destroy();
      } catch (Exception e) {
        // Ignore cleanup errors
      }
    }
  }

  @Test
  public void testInitRegistersBean() throws Exception {
    testMBean = new TestMBeanImpl(TEST_OBJECT_NAME);
    testMBean.init();

    MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = new ObjectName(TEST_OBJECT_NAME);

    assertThat(mbeanServer.isRegistered(objectName), is(true));
  }

  @Test
  public void testDestroyUnregistersBean() throws Exception {
    testMBean = new TestMBeanImpl(TEST_OBJECT_NAME);
    testMBean.init();

    MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = new ObjectName(TEST_OBJECT_NAME);

    assertThat(mbeanServer.isRegistered(objectName), is(true));

    testMBean.destroy();

    assertThat(mbeanServer.isRegistered(objectName), is(false));
  }

  @Test
  public void testInitWithExistingBeanReplaces() throws Exception {
    testMBean = new TestMBeanImpl(TEST_OBJECT_NAME);
    testMBean.init();

    MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = new ObjectName(TEST_OBJECT_NAME);

    assertThat(mbeanServer.isRegistered(objectName), is(true));

    TestMBeanImpl secondMBean = new TestMBeanImpl(TEST_OBJECT_NAME);
    secondMBean.init();

    assertThat(mbeanServer.isRegistered(objectName), is(true));

    secondMBean.destroy();
    assertThat(mbeanServer.isRegistered(objectName), is(false));
  }

  @Test
  public void testDestroyWithoutInit() throws Exception {
    testMBean = new TestMBeanImpl(TEST_OBJECT_NAME);

    try {
      testMBean.destroy();
    } catch (RuntimeException e) {
      assertThat(e, is(notNullValue()));
    }
  }

  @Test(expected = RuntimeException.class)
  public void testDestroyThrowsRuntimeException() throws Exception {
    testMBean = new TestMBeanImpl(TEST_OBJECT_NAME);
    testMBean.init();
    testMBean.destroy();

    // Second destroy should fail with InstanceNotFoundException wrapped in RuntimeException
    testMBean.destroy();
  }

  @Test
  public void testMultipleMBeansWithDifferentNames() throws Exception {
    testMBean = new TestMBeanImpl(TEST_OBJECT_NAME);
    testMBean.init();

    String secondObjectName = "org.codice.ddf:type=SecondTestMBean";
    TestMBeanImpl secondMBean = new TestMBeanImpl(secondObjectName);
    secondMBean.init();

    MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName1 = new ObjectName(TEST_OBJECT_NAME);
    ObjectName objectName2 = new ObjectName(secondObjectName);

    assertThat(mbeanServer.isRegistered(objectName1), is(true));
    assertThat(mbeanServer.isRegistered(objectName2), is(true));

    testMBean.destroy();
    assertThat(mbeanServer.isRegistered(objectName1), is(false));
    assertThat(mbeanServer.isRegistered(objectName2), is(true));

    secondMBean.destroy();
    assertThat(mbeanServer.isRegistered(objectName2), is(false));
  }

  @Test
  public void testMBeanInvocation() throws Exception {
    testMBean = new TestMBeanImpl(TEST_OBJECT_NAME);
    testMBean.init();

    MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = new ObjectName(TEST_OBJECT_NAME);

    String result = (String) mbeanServer.invoke(objectName, "testMethod", null, null);
    assertThat(result, is("test result"));
  }

  @Test(expected = InstanceNotFoundException.class)
  public void testMBeanInvocationAfterDestroy() throws Exception {
    testMBean = new TestMBeanImpl(TEST_OBJECT_NAME);
    testMBean.init();

    MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = new ObjectName(TEST_OBJECT_NAME);

    testMBean.destroy();

    // This should throw InstanceNotFoundException
    mbeanServer.invoke(objectName, "testMethod", null, null);
  }

  // Test MBean interface
  public interface TestMBean {
    String testMethod();
  }

  // Test MBean implementation
  private static class TestMBeanImpl extends BasicMBean implements TestMBean {

    public TestMBeanImpl(String objectNameString) throws Exception {
      super(TestMBean.class, objectNameString);
    }

    @Override
    public String testMethod() {
      return "test result";
    }
  }
}
