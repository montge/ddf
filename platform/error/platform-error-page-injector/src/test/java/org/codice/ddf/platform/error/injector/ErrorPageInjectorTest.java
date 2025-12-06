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
package org.codice.ddf.platform.error.injector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.framework.hooks.service.ListenerHook.ListenerInfo;

/** Tests for {@link ErrorPageInjector} class. */
@RunWith(MockitoJUnitRunner.class)
public class ErrorPageInjectorTest {

  @Mock private ServiceEvent serviceEvent;

  @Mock private ServiceReference<Object> serviceReference;

  @Mock private Bundle bundle;

  @Mock private BundleContext bundleContext;

  private ErrorPageInjector injector;

  @Before
  public void setUp() {
    injector = new ErrorPageInjector();
  }

  @Test
  public void testImplementsEventListenerHook() {
    assertThat(injector instanceof EventListenerHook, is(true));
  }

  @Test
  public void testEventIgnoresUnregisteredServiceEvents() {
    when(serviceEvent.getType()).thenReturn(ServiceEvent.UNREGISTERING);
    Map<BundleContext, Collection<ListenerInfo>> listeners = Collections.emptyMap();

    injector.event(serviceEvent, listeners);

    verify(serviceEvent, never()).getServiceReference();
  }

  @Test
  public void testEventIgnoresModifiedServiceEvents() {
    when(serviceEvent.getType()).thenReturn(ServiceEvent.MODIFIED);
    Map<BundleContext, Collection<ListenerInfo>> listeners = Collections.emptyMap();

    injector.event(serviceEvent, listeners);

    verify(serviceEvent, never()).getServiceReference();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testEventProcessesRegisteredNonServletContext() {
    when(serviceEvent.getType()).thenReturn(ServiceEvent.REGISTERED);
    when(serviceEvent.getServiceReference()).thenReturn((ServiceReference) serviceReference);
    when(serviceReference.getBundle()).thenReturn(bundle);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    when(bundleContext.getService(serviceReference)).thenReturn("not a servlet context");
    Map<BundleContext, Collection<ListenerInfo>> listeners = Collections.emptyMap();

    // Should not throw - handles non-ServletContext services
    injector.event(serviceEvent, listeners);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testEventProcessesRegisteredServletContext() {
    ServletContext servletContext = mock(ServletContext.class);
    when(serviceEvent.getType()).thenReturn(ServiceEvent.REGISTERED);
    when(serviceEvent.getServiceReference()).thenReturn((ServiceReference) serviceReference);
    when(serviceReference.getBundle()).thenReturn(bundle);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    when(bundleContext.getService(serviceReference)).thenReturn(servletContext);
    Map<BundleContext, Collection<ListenerInfo>> listeners = Collections.emptyMap();

    // Should not throw - handles ServletContext services
    injector.event(serviceEvent, listeners);
  }

  @Test
  public void testInitDoesNotThrowWhenBundleContextUnavailable() {
    // When FrameworkUtil.getBundle returns null, getContext returns empty Optional
    // This is the case outside of an OSGi container
    injector.init();
  }
}
