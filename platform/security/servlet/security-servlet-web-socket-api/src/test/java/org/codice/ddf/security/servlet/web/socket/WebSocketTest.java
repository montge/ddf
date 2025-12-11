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

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link WebSocket} interface contract. Uses a test implementation to verify the
 * interface behavior and contract expectations.
 */
public class WebSocketTest {

  private static final String TEST_MESSAGE = "Hello WebSocket";
  private static final int CLOSE_STATUS_NORMAL = 1000;
  private static final String CLOSE_REASON = "Normal closure";

  private TestWebSocket webSocket;
  private Session mockSession;

  @BeforeEach
  public void setUp() {
    webSocket = new TestWebSocket();
    mockSession = mock(Session.class);
  }

  @Test
  public void testOnOpenIsCalled() {
    webSocket.onOpen(mockSession);
    assertThat(webSocket.isOpenCalled(), is(true));
    assertThat(webSocket.getLastSession(), is(mockSession));
  }

  @Test
  public void testOnCloseIsCalled() {
    webSocket.onClose(mockSession, CLOSE_STATUS_NORMAL, CLOSE_REASON);
    assertThat(webSocket.isCloseCalled(), is(true));
    assertThat(webSocket.getLastStatusCode(), is(CLOSE_STATUS_NORMAL));
    assertThat(webSocket.getLastCloseReason(), is(CLOSE_REASON));
  }

  @Test
  public void testOnErrorIsCalled() {
    Throwable testError = new RuntimeException("Test error");
    webSocket.onError(mockSession, testError);
    assertThat(webSocket.isErrorCalled(), is(true));
    assertThat(webSocket.getLastError(), is(testError));
  }

  @Test
  public void testOnMessageIsCalled() throws IOException {
    webSocket.onMessage(mockSession, TEST_MESSAGE);
    assertThat(webSocket.isMessageCalled(), is(true));
    assertThat(webSocket.getLastMessage(), is(TEST_MESSAGE));
  }

  @Test
  public void testOnMessageWithEmptyMessage() throws IOException {
    webSocket.onMessage(mockSession, "");
    assertThat(webSocket.isMessageCalled(), is(true));
    assertThat(webSocket.getLastMessage(), is(""));
  }

  @Test
  public void testOnCloseWithDifferentStatusCodes() {
    int abnormalClose = 1006;
    webSocket.onClose(mockSession, abnormalClose, "Abnormal closure");
    assertThat(webSocket.getLastStatusCode(), is(abnormalClose));
  }

  @Test
  public void testMultipleMessagesCalled() throws IOException {
    webSocket.onMessage(mockSession, "Message 1");
    webSocket.onMessage(mockSession, "Message 2");
    webSocket.onMessage(mockSession, "Message 3");
    assertThat(webSocket.getMessageCount(), is(3));
    assertThat(webSocket.getLastMessage(), is("Message 3"));
  }

  @Test
  public void testOnCloseWithNullReason() {
    webSocket.onClose(mockSession, CLOSE_STATUS_NORMAL, null);
    assertThat(webSocket.isCloseCalled(), is(true));
    assertThat(webSocket.getLastCloseReason(), is((String) null));
  }

  /** Simple test implementation of WebSocket interface. */
  private static class TestWebSocket implements WebSocket {
    private boolean openCalled = false;
    private boolean closeCalled = false;
    private boolean errorCalled = false;
    private boolean messageCalled = false;
    private Session lastSession;
    private int lastStatusCode;
    private String lastCloseReason;
    private Throwable lastError;
    private String lastMessage;
    private int messageCount = 0;

    @Override
    public void onOpen(Session session) {
      openCalled = true;
      lastSession = session;
    }

    @Override
    public void onClose(Session session, int statusCode, String reason) {
      closeCalled = true;
      lastSession = session;
      lastStatusCode = statusCode;
      lastCloseReason = reason;
    }

    @Override
    public void onError(Session session, Throwable ex) {
      errorCalled = true;
      lastSession = session;
      lastError = ex;
    }

    @Override
    public void onMessage(Session session, String message) throws IOException {
      messageCalled = true;
      lastSession = session;
      lastMessage = message;
      messageCount++;
    }

    public boolean isOpenCalled() {
      return openCalled;
    }

    public boolean isCloseCalled() {
      return closeCalled;
    }

    public boolean isErrorCalled() {
      return errorCalled;
    }

    public boolean isMessageCalled() {
      return messageCalled;
    }

    public Session getLastSession() {
      return lastSession;
    }

    public int getLastStatusCode() {
      return lastStatusCode;
    }

    public String getLastCloseReason() {
      return lastCloseReason;
    }

    public Throwable getLastError() {
      return lastError;
    }

    public String getLastMessage() {
      return lastMessage;
    }

    public int getMessageCount() {
      return messageCount;
    }
  }
}
