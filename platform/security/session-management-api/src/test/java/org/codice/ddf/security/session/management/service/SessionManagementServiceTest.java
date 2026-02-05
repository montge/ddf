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
package org.codice.ddf.security.session.management.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests for {@link SessionManagementService} interface contract. Uses a test implementation to
 * verify the interface behavior and contract expectations.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SessionManagementServiceTest {

  private static final String SESSION_EXPIRY = "1800000"; // 30 minutes in ms
  private static final String INVALIDATE_URL = "https://localhost:8993/logout";

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpSession mockSession;

  private TestSessionManagementService sessionService;

  @BeforeEach
  public void setUp() {
    sessionService = new TestSessionManagementService();
    when(mockRequest.getSession(false)).thenReturn(mockSession);
  }

  @Test
  public void testGetExpiryReturnsSessionExpiry() {
    when(mockSession.getMaxInactiveInterval()).thenReturn(1800);
    String expiry = sessionService.getExpiry(mockRequest);
    assertThat(expiry, is(notNullValue()));
  }

  @Test
  public void testGetExpiryWithNoSession() {
    when(mockRequest.getSession(false)).thenReturn(null);
    String expiry = sessionService.getExpiry(mockRequest);
    assertThat(expiry, is(nullValue()));
  }

  @Test
  public void testGetExpiryReturnsValidTimeFormat() {
    when(mockSession.getMaxInactiveInterval()).thenReturn(1800);
    String expiry = sessionService.getExpiry(mockRequest);
    // Should be a numeric string representing milliseconds
    assertThat(expiry.matches("\\d+"), is(true));
  }

  @Test
  public void testGetInvalidateReturnsUri() {
    URI invalidateUri = sessionService.getInvalidate(mockRequest);
    assertThat(invalidateUri, is(notNullValue()));
  }

  @Test
  public void testGetInvalidateReturnsLogoutUrl() {
    URI invalidateUri = sessionService.getInvalidate(mockRequest);
    assertThat(invalidateUri.toString(), is(INVALIDATE_URL));
  }

  @Test
  public void testGetInvalidateWithNullRequest() {
    URI invalidateUri = sessionService.getInvalidate(null);
    // Implementation should handle null gracefully
    assertThat(invalidateUri, is(notNullValue()));
  }

  @Test
  public void testGetExpiryWithExpiredSession() {
    when(mockSession.getMaxInactiveInterval()).thenReturn(0);
    String expiry = sessionService.getExpiry(mockRequest);
    assertThat(expiry, is("0"));
  }

  @Test
  public void testGetExpiryWithNegativeInterval() {
    // Negative interval means session never expires
    when(mockSession.getMaxInactiveInterval()).thenReturn(-1);
    String expiry = sessionService.getExpiry(mockRequest);
    assertThat(expiry, is("-1000")); // Converted to milliseconds
  }

  @Test
  public void testSessionServiceWithDifferentContexts() {
    HttpServletRequest adminRequest = mock(HttpServletRequest.class);
    HttpSession adminSession = mock(HttpSession.class);
    when(adminRequest.getSession(false)).thenReturn(adminSession);
    when(adminSession.getMaxInactiveInterval()).thenReturn(3600);

    HttpServletRequest userRequest = mock(HttpServletRequest.class);
    HttpSession userSession = mock(HttpSession.class);
    when(userRequest.getSession(false)).thenReturn(userSession);
    when(userSession.getMaxInactiveInterval()).thenReturn(900);

    String adminExpiry = sessionService.getExpiry(adminRequest);
    String userExpiry = sessionService.getExpiry(userRequest);

    // Different sessions should have different expiry times
    assertThat(Long.parseLong(adminExpiry) > Long.parseLong(userExpiry), is(true));
  }

  /** Simple test implementation of SessionManagementService interface. */
  private static class TestSessionManagementService implements SessionManagementService {

    @Override
    public String getExpiry(HttpServletRequest request) {
      if (request == null) {
        return null;
      }
      HttpSession session = request.getSession(false);
      if (session == null) {
        return null;
      }
      int intervalSeconds = session.getMaxInactiveInterval();
      return String.valueOf(intervalSeconds * 1000L);
    }

    @Override
    public URI getInvalidate(HttpServletRequest request) {
      return URI.create(INVALIDATE_URL);
    }
  }
}
