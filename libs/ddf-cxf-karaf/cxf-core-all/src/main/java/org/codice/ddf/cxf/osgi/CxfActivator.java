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
package org.codice.ddf.cxf.osgi;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi Bundle Activator for Apache CXF 4.x.
 *
 * <p>This activator initializes the CXF Bus in an OSGi environment and registers it as an OSGi
 * service. It follows the pattern established by camel-karaf for providing CXF 4.x support in
 * Apache Karaf.
 *
 * @see <a href="https://issues.apache.org/jira/browse/CXF-9086">CXF-9086: Bring back OSGi
 *     support</a>
 */
public class CxfActivator implements BundleActivator {

  private static final Logger LOGGER = LoggerFactory.getLogger(CxfActivator.class);

  private Bus bus;
  private ServiceRegistration<Bus> busRegistration;

  @Override
  public void start(BundleContext context) throws Exception {
    LOGGER.info("Starting DDF CXF 4.x OSGi bundle");

    // Set the bundle classloader as the context classloader for CXF initialization
    ClassLoader bundleClassLoader = this.getClass().getClassLoader();
    ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

    try {
      Thread.currentThread().setContextClassLoader(bundleClassLoader);

      // Create and configure the CXF Bus
      bus = BusFactory.newInstance().createBus();
      BusFactory.setDefaultBus(bus);

      // Register the Bus as an OSGi service
      busRegistration = context.registerService(Bus.class, bus, null);

      LOGGER.info("CXF Bus initialized and registered as OSGi service");
    } finally {
      Thread.currentThread().setContextClassLoader(originalClassLoader);
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    LOGGER.info("Stopping DDF CXF 4.x OSGi bundle");

    if (busRegistration != null) {
      busRegistration.unregister();
      busRegistration = null;
    }

    if (bus != null) {
      bus.shutdown(true);
      bus = null;
    }

    LOGGER.info("CXF Bus shutdown complete");
  }
}
