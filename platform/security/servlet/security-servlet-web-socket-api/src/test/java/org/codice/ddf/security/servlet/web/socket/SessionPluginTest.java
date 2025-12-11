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
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SessionPlugin} interface contract. Uses a test implementation to verify the
 * interface behavior and contract expectations.
 */
public class SessionPluginTest {

  private TestSessionPlugin sessionPlugin;
  private Session mockSession;

  @BeforeEach
  public void setUp() {
    sessionPlugin = new TestSessionPlugin();
    mockSession = mock(Session.class);
  }

  @Test
  public void testHandleIsCalled() {
    sessionPlugin.handle(mockSession);
    assertThat(sessionPlugin.isHandleCalled(), is(true));
  }

  @Test
  public void testHandleReceivesCorrectSession() {
    sessionPlugin.handle(mockSession);
    assertThat(sessionPlugin.getLastSession(), is(mockSession));
  }

  @Test
  public void testHandleMultipleTimes() {
    Session session1 = mock(Session.class);
    Session session2 = mock(Session.class);
    Session session3 = mock(Session.class);

    sessionPlugin.handle(session1);
    sessionPlugin.handle(session2);
    sessionPlugin.handle(session3);

    assertThat(sessionPlugin.getHandleCount(), is(3));
    assertThat(sessionPlugin.getLastSession(), is(session3));
  }

  @Test
  public void testHandleWithNullSession() {
    sessionPlugin.handle(null);
    assertThat(sessionPlugin.isHandleCalled(), is(true));
    assertThat(sessionPlugin.getLastSession(), is((Session) null));
  }

  @Test
  public void testMultiplePluginsCanHandleSameSession() {
    TestSessionPlugin plugin1 = new TestSessionPlugin();
    TestSessionPlugin plugin2 = new TestSessionPlugin();

    plugin1.handle(mockSession);
    plugin2.handle(mockSession);

    assertThat(plugin1.isHandleCalled(), is(true));
    assertThat(plugin2.isHandleCalled(), is(true));
    assertThat(plugin1.getLastSession(), is(mockSession));
    assertThat(plugin2.getLastSession(), is(mockSession));
  }

  /** Simple test implementation of SessionPlugin interface. */
  private static class TestSessionPlugin implements SessionPlugin {
    private boolean handleCalled = false;
    private Session lastSession;
    private int handleCount = 0;

    @Override
    public void handle(Session session) {
      handleCalled = true;
      lastSession = session;
      handleCount++;
    }

    public boolean isHandleCalled() {
      return handleCalled;
    }

    public Session getLastSession() {
      return lastSession;
    }

    public int getHandleCount() {
      return handleCount;
    }
  }
}
