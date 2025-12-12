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
package org.codice.ddf.security.handler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.codice.ddf.security.handler.api.HandlerResult;
import org.codice.ddf.security.handler.api.HandlerResult.Status;
import org.junit.jupiter.api.Test;

public class HandlerResultTest {

  @Test
  public void testDefaultConstructorSetsNoActionStatus() {
    HandlerResultImpl result = new HandlerResultImpl();

    assertThat(result.getStatus(), is(Status.NO_ACTION));
    assertThat(result.getToken(), is(nullValue()));
    assertThat(result.getSource(), is(nullValue()));
  }

  @Test
  public void testParameterizedConstructorSetsStatusAndToken() {
    BaseAuthenticationToken token = new BaseAuthenticationToken("principal", "creds", "127.0.0.1");

    HandlerResultImpl result = new HandlerResultImpl(Status.COMPLETED, token);

    assertThat(result.getStatus(), is(Status.COMPLETED));
    assertThat(result.getToken(), is(token));
    assertThat(result.getSource(), is(nullValue()));
  }

  @Test
  public void testParameterizedConstructorWithNullToken() {
    HandlerResultImpl result = new HandlerResultImpl(Status.REDIRECTED, null);

    assertThat(result.getStatus(), is(Status.REDIRECTED));
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testSetAndGetStatusForAllStatusValues() {
    HandlerResultImpl result = new HandlerResultImpl();

    for (Status status : Status.values()) {
      result.setStatus(status);
      assertThat(result.getStatus(), is(status));
    }
  }

  @Test
  public void testSetAndGetToken() {
    HandlerResultImpl result = new HandlerResultImpl();
    BaseAuthenticationToken token = new BaseAuthenticationToken("user", "pass", "192.168.1.1");

    result.setToken(token);
    assertThat(result.getToken(), is(token));

    result.setToken(null);
    assertThat(result.getToken(), is(nullValue()));
  }

  @Test
  public void testSetAndGetSource() {
    HandlerResultImpl result = new HandlerResultImpl();

    result.setSource("SAMLHandler");
    assertThat(result.getSource(), is("SAMLHandler"));

    result.setSource("");
    assertThat(result.getSource(), is(""));

    result.setSource(null);
    assertThat(result.getSource(), is(nullValue()));
  }

  @Test
  public void testToStringWithNullToken() {
    HandlerResultImpl result = new HandlerResultImpl();
    result.setSource("TestSource");

    String toString = result.toString();

    assertThat(toString, containsString("Status: NO_ACTION"));
    assertThat(toString, containsString("Source: TestSource"));
    assertThat(toString, containsString("Token: null"));
  }

  @Test
  public void testToStringWithValidToken() {
    BaseAuthenticationToken token =
        new BaseAuthenticationToken("testUser", "testCreds", "10.0.0.1");
    HandlerResultImpl result = new HandlerResultImpl(Status.COMPLETED, token);
    result.setSource("BasicHandler");

    String toString = result.toString();

    assertThat(toString, containsString("Status: COMPLETED"));
    assertThat(toString, containsString("Source: BasicHandler"));
    assertThat(toString, containsString("Token:"));
  }

  @Test
  public void testToStringWithNullSource() {
    HandlerResultImpl result = new HandlerResultImpl();

    String toString = result.toString();

    assertThat(toString, containsString("Source: null"));
  }

  @Test
  public void testStatusEnumValues() {
    assertThat(Status.valueOf("NO_ACTION"), is(Status.NO_ACTION));
    assertThat(Status.valueOf("COMPLETED"), is(Status.COMPLETED));
    assertThat(Status.valueOf("REDIRECTED"), is(Status.REDIRECTED));
  }

  @Test
  public void testLegacyConstructorBehavior() {
    HandlerResult result = new HandlerResultImpl();
    assertEquals(HandlerResult.Status.NO_ACTION, result.getStatus());

    BaseAuthenticationToken token = new BaseAuthenticationToken("x", "y", "127.0.0.1");
    result = new HandlerResultImpl(HandlerResult.Status.COMPLETED, token);
    assertEquals(HandlerResult.Status.COMPLETED, result.getStatus());
    assertEquals(result.getToken(), token);
  }
}
