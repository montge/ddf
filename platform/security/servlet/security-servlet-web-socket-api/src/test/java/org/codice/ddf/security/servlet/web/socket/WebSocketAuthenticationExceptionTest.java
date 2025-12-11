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
package org.codice.ddf.security.servlet.web.socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

/** Tests for {@link WebSocketAuthenticationException}. */
public class WebSocketAuthenticationExceptionTest {

  private static final String ERROR_MESSAGE = "Authentication failed";
  private static final String WS_MESSAGE = "Invalid credentials";

  @Test
  public void testExceptionWithMessageOnly() {
    WebSocketAuthenticationException exception =
        new WebSocketAuthenticationException(ERROR_MESSAGE);
    assertThat(exception.getMessage(), is(ERROR_MESSAGE));
    assertThat(exception.getWsMessage(), is(nullValue()));
  }

  @Test
  public void testExceptionWithMessageAndWsMessage() {
    WebSocketAuthenticationException exception =
        new WebSocketAuthenticationException(ERROR_MESSAGE, WS_MESSAGE);
    assertThat(exception.getMessage(), is(ERROR_MESSAGE));
    assertThat(exception.getWsMessage(), is(WS_MESSAGE));
  }

  @Test
  public void testExceptionIsRuntimeException() {
    WebSocketAuthenticationException exception =
        new WebSocketAuthenticationException(ERROR_MESSAGE);
    assertThat(exception, is(instanceOf(RuntimeException.class)));
  }

  @Test
  public void testExceptionWithNullWsMessage() {
    WebSocketAuthenticationException exception =
        new WebSocketAuthenticationException(ERROR_MESSAGE, null);
    assertThat(exception.getMessage(), is(ERROR_MESSAGE));
    assertThat(exception.getWsMessage(), is(nullValue()));
  }

  @Test
  public void testExceptionCanBeCaught() {
    try {
      throw new WebSocketAuthenticationException(ERROR_MESSAGE, WS_MESSAGE);
    } catch (WebSocketAuthenticationException e) {
      assertThat(e.getMessage(), containsString("Authentication"));
      assertThat(e.getWsMessage(), is(WS_MESSAGE));
    }
  }

  @Test
  public void testExceptionWithEmptyMessage() {
    WebSocketAuthenticationException exception = new WebSocketAuthenticationException("");
    assertThat(exception.getMessage(), is(""));
    assertThat(exception.getWsMessage(), is(nullValue()));
  }

  @Test
  public void testExceptionWithEmptyWsMessage() {
    WebSocketAuthenticationException exception =
        new WebSocketAuthenticationException(ERROR_MESSAGE, "");
    assertThat(exception.getMessage(), is(ERROR_MESSAGE));
    assertThat(exception.getWsMessage(), is(""));
  }
}
