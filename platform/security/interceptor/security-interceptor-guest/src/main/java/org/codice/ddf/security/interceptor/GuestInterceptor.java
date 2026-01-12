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
package org.codice.ddf.security.interceptor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ddf.security.Subject;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.assertion.impl.SecurityAssertionPrincipalDefault;
import ddf.security.audit.SecurityLogger;
import ddf.security.service.SecurityManager;
import ddf.security.service.SecurityServiceException;
import java.security.Principal;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.DefaultSecurityContext;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;
import org.apache.shiro.subject.PrincipalCollection;
import org.codice.ddf.platform.filter.AuthenticationException;
import org.codice.ddf.security.handler.GuestAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Interceptor for guest access to SOAP endpoints. */
public class GuestInterceptor extends AbstractSoapInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(GuestInterceptor.class);

  // WSS4J interceptor class names for ordering (avoid importing cxf-rt-ws-security)
  private static final String WSS4J_IN_INTERCEPTOR =
      "org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor";
  private static final String POLICY_WSS4J_IN_INTERCEPTOR =
      "org.apache.cxf.ws.security.wss4j.PolicyBasedWSS4JInInterceptor";
  private static final String WSS4J_CHECK_STRING = WSS4J_IN_INTERCEPTOR + ".DONE";

  // CXF HTTP request key (replacing AbstractHTTPDestination.HTTP_REQUEST constant)
  private static final String HTTP_REQUEST = "HTTP.REQUEST";

  private SecurityManager securityManager;

  private SecurityLogger securityLogger;

  private Cache<String, Subject> guestSubjectCache =
      CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

  public GuestInterceptor(SecurityManager securityManager) {
    super(Phase.PRE_PROTOCOL);
    LOGGER.trace("Constructing Legacy Guest Interceptor.");
    this.securityManager = securityManager;
    // make sure this interceptor runs before the WSS4J one in the same Phase, otherwise it won't
    // work
    Set<String> before = getBefore();
    before.add(WSS4J_IN_INTERCEPTOR);
    before.add(POLICY_WSS4J_IN_INTERCEPTOR);
    LOGGER.trace("Exiting Legacy Guest Interceptor constructor.");
  }

  @Override
  public void handleMessage(SoapMessage message) throws Fault {

    if (message != null) {

      HttpServletRequest request = (HttpServletRequest) message.get(HTTP_REQUEST);
      LOGGER.debug("Getting new Guest user token");
      Principal principal = null;

      Subject subject = null;
      try {
        subject = getSubject(request.getRemoteAddr());
      } catch (AuthenticationException e) {
        throw new Fault(e);
      }
      if (subject != null) {
        PrincipalCollection principals = subject.getPrincipals();
        SecurityAssertion securityAssertion = principals.oneByType(SecurityAssertion.class);
        if (securityAssertion != null) {
          principal = new SecurityAssertionPrincipalDefault(securityAssertion);
        } else {
          LOGGER.debug("Subject did not contain a security assertion");
        }

        message.put(SecurityContext.class, new DefaultSecurityContext(principal, null));
        message.put(WSS4J_CHECK_STRING, Boolean.TRUE);
      }
    } else {
      LOGGER.debug("Incoming SOAP message is null - guest interceptor makes no sense.");
    }
  }

  private synchronized Subject getSubject(String ipAddress) throws AuthenticationException {
    Subject subject = guestSubjectCache.getIfPresent(ipAddress);
    if (subject == null) {
      if (securityManager == null) {
        throw new AuthenticationException(
            "Unable to create the guest subject, system is not ready.");
      }
      GuestAuthenticationToken token = new GuestAuthenticationToken(ipAddress, securityLogger);
      LOGGER.debug("Getting new Guest user token for {}", ipAddress);
      try {
        subject = securityManager.getSubject(token);
        // this should be a cache not a map so we can remove items, make this change
        guestSubjectCache.put(ipAddress, subject);
      } catch (SecurityServiceException sse) {
        LOGGER.info("Unable to request subject for guest user.", sse);
      }

    } else {
      LOGGER.debug("Using cached Guest user token for {}", ipAddress);
    }
    return subject;
  }

  public void setSecurityLogger(SecurityLogger securityLogger) {
    this.securityLogger = securityLogger;
  }
}
