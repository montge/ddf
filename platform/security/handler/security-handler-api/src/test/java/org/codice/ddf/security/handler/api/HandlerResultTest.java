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
package org.codice.ddf.security.handler.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link HandlerResult} interface contract. Uses a simple implementation to verify the
 * interface behavior.
 */
public class HandlerResultTest {

  private TestHandlerResult handlerResult;

  @Before
  public void setUp() {
    handlerResult = new TestHandlerResult();
  }

  @Test
  public void testInitialStatusIsNull() {
    assertThat(handlerResult.getStatus(), is(nullValue()));
  }

  @Test
  public void testSetAndGetStatus() {
    handlerResult.setStatus(HandlerResult.Status.COMPLETED);
    assertThat(handlerResult.getStatus(), is(HandlerResult.Status.COMPLETED));
  }

  @Test
  public void testSetStatusToNoAction() {
    handlerResult.setStatus(HandlerResult.Status.NO_ACTION);
    assertThat(handlerResult.getStatus(), is(HandlerResult.Status.NO_ACTION));
  }

  @Test
  public void testSetStatusToRedirected() {
    handlerResult.setStatus(HandlerResult.Status.REDIRECTED);
    assertThat(handlerResult.getStatus(), is(HandlerResult.Status.REDIRECTED));
  }

  @Test
  public void testInitialTokenIsNull() {
    assertThat(handlerResult.getToken(), is(nullValue()));
  }

  @Test
  public void testSetAndGetToken() {
    AuthenticationToken token = new UsernamePasswordToken("testUser", "testPassword");
    handlerResult.setToken(token);
    assertThat(handlerResult.getToken(), is(token));
  }

  @Test
  public void testSetTokenToNull() {
    AuthenticationToken token = new UsernamePasswordToken("testUser", "testPassword");
    handlerResult.setToken(token);
    handlerResult.setToken(null);
    assertThat(handlerResult.getToken(), is(nullValue()));
  }

  @Test
  public void testInitialSourceIsNull() {
    assertThat(handlerResult.getSource(), is(nullValue()));
  }

  @Test
  public void testSetAndGetSource() {
    String source = "BasicAuthHandler";
    handlerResult.setSource(source);
    assertThat(handlerResult.getSource(), is(source));
  }

  @Test
  public void testSetSourceToEmpty() {
    handlerResult.setSource("");
    assertThat(handlerResult.getSource(), is(""));
  }

  @Test
  public void testStatusEnumValues() {
    HandlerResult.Status[] statuses = HandlerResult.Status.values();
    assertThat(statuses.length, is(3));
    assertThat(HandlerResult.Status.valueOf("COMPLETED"), is(HandlerResult.Status.COMPLETED));
    assertThat(HandlerResult.Status.valueOf("NO_ACTION"), is(HandlerResult.Status.NO_ACTION));
    assertThat(HandlerResult.Status.valueOf("REDIRECTED"), is(HandlerResult.Status.REDIRECTED));
  }

  @Test
  public void testCompleteHandlerResultWorkflow() {
    // Simulate a complete authentication workflow
    handlerResult.setSource("PKIHandler");
    handlerResult.setStatus(HandlerResult.Status.COMPLETED);
    AuthenticationToken token = new UsernamePasswordToken("admin", "secret");
    handlerResult.setToken(token);

    assertThat(handlerResult.getSource(), is("PKIHandler"));
    assertThat(handlerResult.getStatus(), is(HandlerResult.Status.COMPLETED));
    assertThat(handlerResult.getToken(), is(token));
  }

  @Test
  public void testRedirectedWorkflow() {
    // Simulate a redirect workflow (e.g., SAML SSO)
    handlerResult.setSource("SAMLHandler");
    handlerResult.setStatus(HandlerResult.Status.REDIRECTED);
    // Token should remain null during redirect
    assertThat(handlerResult.getToken(), is(nullValue()));
    assertThat(handlerResult.getStatus(), is(HandlerResult.Status.REDIRECTED));
  }

  /** Simple test implementation of HandlerResult interface for testing purposes. */
  private static class TestHandlerResult implements HandlerResult {
    private Status status;
    private AuthenticationToken token;
    private String source;

    @Override
    public Status getStatus() {
      return status;
    }

    @Override
    public void setStatus(Status status) {
      this.status = status;
    }

    @Override
    public AuthenticationToken getToken() {
      return token;
    }

    @Override
    public void setToken(AuthenticationToken token) {
      this.token = token;
    }

    @Override
    public String getSource() {
      return source;
    }

    @Override
    public void setSource(String src) {
      this.source = src;
    }
  }
}
