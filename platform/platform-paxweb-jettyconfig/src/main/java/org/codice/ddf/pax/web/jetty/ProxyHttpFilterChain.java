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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.codice.ddf.platform.filter.http.HttpFilter;
import org.codice.ddf.platform.filter.http.HttpFilterChain;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of filter chain that allows the ability to dynamically add new {@link
 * HttpFilter}s to a chain. The {@link ProxyHttpFilterChain} may not be reused. That is, once the
 * {@link ProxyHttpFilterChain#doFilter} method is called, no more {@link HttpFilter}s may be added.
 *
 * <p>In Jetty 12, when the filter chain is exhausted, the next handler in the Jetty core handler
 * chain is invoked using the core Request/Response/Callback API.
 */
public class ProxyHttpFilterChain implements HttpFilterChain {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProxyHttpFilterChain.class);

  private final Iterator<HttpFilter> iterator;
  private final Handler handler;
  private final Request request;
  private final Response response;
  private final Callback callback;

  public ProxyHttpFilterChain(
      List<HttpFilter> filters,
      Handler handler,
      Request request,
      Response response,
      Callback callback) {
    this.iterator = filters.iterator();
    this.handler = handler;
    this.request = request;
    this.response = response;
    this.callback = callback;
  }

  @Override
  public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
      throws IOException, ServletException {
    if (iterator.hasNext()) {
      HttpFilter filter = iterator.next();
      LOGGER.debug(
          "Calling filter {}.doFilter({}, {}, {})",
          filter.getClass().getName(),
          servletRequest,
          servletResponse,
          this);
      filter.doFilter(servletRequest, servletResponse, this);
    } else {
      try {
        handler.handle(request, response, callback);
      } catch (Exception e) {
        if (e instanceof IOException) {
          throw (IOException) e;
        }
        throw new ServletException(e);
      }
    }
  }
}
