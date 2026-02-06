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
package org.codice.ddf.security.handler.oauth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Adapts jakarta.servlet types to javax.servlet types for pac4j 5.x compatibility. pac4j 5.x uses
 * javax.servlet (via pac4j-javaee) but DDF's handler API now uses jakarta.servlet. This adapter
 * bridges the gap using dynamic proxies.
 *
 * <p>This class will be removed when pac4j is upgraded to 6.x (which uses jakarta.servlet natively
 * via pac4j-jakartaee).
 */
final class Pac4jServletAdapter {

  private Pac4jServletAdapter() {}

  static javax.servlet.http.HttpServletRequest adaptRequest(
      jakarta.servlet.http.HttpServletRequest jakartaRequest) {
    return (javax.servlet.http.HttpServletRequest)
        Proxy.newProxyInstance(
            javax.servlet.http.HttpServletRequest.class.getClassLoader(),
            new Class<?>[] {javax.servlet.http.HttpServletRequest.class},
            (proxy, method, args) -> {
              Object result = invokeDelegate(jakartaRequest, method, args);
              if (result instanceof jakarta.servlet.http.HttpSession) {
                return adaptSession((jakarta.servlet.http.HttpSession) result);
              }
              return result;
            });
  }

  static javax.servlet.http.HttpServletResponse adaptResponse(
      jakarta.servlet.http.HttpServletResponse jakartaResponse) {
    return (javax.servlet.http.HttpServletResponse)
        Proxy.newProxyInstance(
            javax.servlet.http.HttpServletResponse.class.getClassLoader(),
            new Class<?>[] {javax.servlet.http.HttpServletResponse.class},
            (proxy, method, args) -> invokeDelegate(jakartaResponse, method, args));
  }

  private static javax.servlet.http.HttpSession adaptSession(
      jakarta.servlet.http.HttpSession jakartaSession) {
    return (javax.servlet.http.HttpSession)
        Proxy.newProxyInstance(
            javax.servlet.http.HttpSession.class.getClassLoader(),
            new Class<?>[] {javax.servlet.http.HttpSession.class},
            (proxy, method, args) -> invokeDelegate(jakartaSession, method, args));
  }

  private static Object invokeDelegate(Object delegate, Method method, Object[] args)
      throws Throwable {
    try {
      Method delegateMethod =
          delegate.getClass().getMethod(method.getName(), method.getParameterTypes());
      return delegateMethod.invoke(delegate, args);
    } catch (NoSuchMethodException e) {
      // For methods with servlet-typed parameters (rare in pac4j usage),
      // fall back to matching by name and parameter count
      for (Method m : delegate.getClass().getMethods()) {
        if (m.getName().equals(method.getName())
            && m.getParameterCount() == method.getParameterCount()) {
          return m.invoke(delegate, args);
        }
      }
      throw e;
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }
}
