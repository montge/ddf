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
package org.codice.ddf.pax.web.jetty;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Objects;
import org.codice.ddf.platform.filter.http.HttpFilter;
import org.codice.ddf.platform.util.SortedServiceList;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code DelegatingHttpFilterHandler} provides a way to create global filters which will apply
 * to all requests. It finds any registered {@link HttpFilter} services and passes incoming requests
 * to them in order of service ranking.
 *
 * <p>In Jetty 12, this extends {@link Handler.Wrapper} and works with the core
 * Request/Response/Callback API. It attempts to extract servlet request/response from the EE10
 * layer for passing to HttpFilter instances. If no servlet wrapping is available, the request is
 * passed through to the next handler without filtering.
 */
public class DelegatingHttpFilterHandler extends Handler.Wrapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingHttpFilterHandler.class);

  private static final String FILTER = "(objectclass=" + HttpFilter.class.getName() + ")";

  private final HttpFilterServiceListener listener = new HttpFilterServiceListener();

  private final SortedServiceList<HttpFilter> httpFilters;

  private final BundleContext context;

  private static BundleContext getContext() {
    Bundle bundle = FrameworkUtil.getBundle(DelegatingHttpFilterHandler.class);
    Objects.requireNonNull(bundle, "Bundle cannot be null");
    return bundle.getBundleContext();
  }

  public DelegatingHttpFilterHandler() throws InvalidSyntaxException {
    this(getContext());
  }

  public DelegatingHttpFilterHandler(BundleContext context) throws InvalidSyntaxException {
    Objects.requireNonNull(context, "Bundle context cannot be null");
    this.context = context;
    this.context.addServiceListener(listener, FILTER);
    this.httpFilters =
        new SortedServiceList<HttpFilter>() {
          @Override
          protected BundleContext getContext() {
            return context;
          }
        };

    /*
     * The service listener won't pick up services that are already registered. Must manually
     * add them to the service list.
     */
    Collection<ServiceReference<HttpFilter>> serviceReferences =
        this.context.getServiceReferences(HttpFilter.class, FILTER);
    for (ServiceReference<HttpFilter> reference : serviceReferences) {
      this.listener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, reference));
    }
  }

  @Override
  public boolean handle(Request request, Response response, Callback callback) throws Exception {
    LOGGER.trace("Delegating to {} HttpFilters.", httpFilters.size());

    // In Jetty 12 EE10, get the servlet request/response from core request attributes
    HttpServletRequest servletRequest =
        (HttpServletRequest) request.getAttribute(HttpServletRequest.class.getName());
    HttpServletResponse servletResponse =
        (HttpServletResponse) request.getAttribute(HttpServletResponse.class.getName());

    if (servletRequest != null && servletResponse != null) {
      ProxyHttpFilterChain filterChain =
          new ProxyHttpFilterChain(httpFilters, getHandler(), request, response, callback);
      filterChain.doFilter(servletRequest, servletResponse);
      return true;
    } else {
      // No servlet layer available yet, delegate directly to next handler
      return super.handle(request, response, callback);
    }
  }

  private class HttpFilterServiceListener implements ServiceListener {

    @Override
    public void serviceChanged(ServiceEvent event) {
      ServiceReference<?> reference = event.getServiceReference();
      switch (event.getType()) {
        case ServiceEvent.REGISTERED:
          httpFilters.bindPlugin(reference);
          break;
        case ServiceEvent.UNREGISTERING:
          httpFilters.unbindPlugin(reference);
          break;
        default:
          /* only care when services are added or removed */
          break;
      }
    }
  }
}
