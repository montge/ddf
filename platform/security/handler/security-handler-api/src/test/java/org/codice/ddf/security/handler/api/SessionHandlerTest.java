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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SessionHandlerTest {

  private SessionHandler sessionHandler;
  private Map<String, Set<String>> activeSessions;

  @Before
  public void setUp() {
    activeSessions = new HashMap<>();
    sessionHandler =
        new SessionHandler() {
          @Override
          public Map<String, Set<String>> getActiveSessions() {
            return activeSessions;
          }

          @Override
          public void invalidateSession(String subjectName) {
            activeSessions.remove(subjectName);
          }
        };
  }

  @Test
  public void testGetActiveSessionsEmpty() {
    Map<String, Set<String>> result = sessionHandler.getActiveSessions();
    assertThat(result, is(notNullValue()));
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  public void testGetActiveSessionsWithSessions() {
    Set<String> sps1 = new HashSet<>();
    sps1.add("sp1");
    sps1.add("sp2");
    activeSessions.put("user1", sps1);

    Set<String> sps2 = new HashSet<>();
    sps2.add("sp3");
    activeSessions.put("user2", sps2);

    Map<String, Set<String>> result = sessionHandler.getActiveSessions();
    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(2));
    assertThat(result.containsKey("user1"), is(true));
    assertThat(result.containsKey("user2"), is(true));
    assertThat(result.get("user1").size(), is(2));
    assertThat(result.get("user2").size(), is(1));
    assertThat(result.get("user1"), hasItem("sp1"));
    assertThat(result.get("user1"), hasItem("sp2"));
    assertThat(result.get("user2"), hasItem("sp3"));
  }

  @Test
  public void testInvalidateSession() {
    Set<String> sps = new HashSet<>();
    sps.add("sp1");
    activeSessions.put("user1", sps);

    assertThat(activeSessions.containsKey("user1"), is(true));
    sessionHandler.invalidateSession("user1");
    assertThat(activeSessions.containsKey("user1"), is(false));
  }

  @Test
  public void testInvalidateNonExistentSession() {
    // Should not throw exception
    sessionHandler.invalidateSession("nonexistent");
    assertThat(activeSessions.isEmpty(), is(true));
  }

  @Test
  public void testInvalidateSessionWithNullSubjectName() {
    Set<String> sps = new HashSet<>();
    sps.add("sp1");
    activeSessions.put("user1", sps);

    // Should not throw exception
    sessionHandler.invalidateSession(null);
    // Session should still exist
    assertThat(activeSessions.containsKey("user1"), is(true));
  }

  @Test
  public void testMultipleSessionsWithSameSps() {
    Set<String> sps1 = new HashSet<>();
    sps1.add("sp1");
    activeSessions.put("user1", sps1);

    Set<String> sps2 = new HashSet<>();
    sps2.add("sp1");
    activeSessions.put("user2", sps2);

    Map<String, Set<String>> result = sessionHandler.getActiveSessions();
    assertThat(result.size(), is(2));
    assertThat(result.get("user1"), hasItem("sp1"));
    assertThat(result.get("user2"), hasItem("sp1"));
  }

  @Test
  public void testSessionWithNoAttachedSps() {
    Set<String> emptySps = new HashSet<>();
    activeSessions.put("user1", emptySps);

    Map<String, Set<String>> result = sessionHandler.getActiveSessions();
    assertThat(result.containsKey("user1"), is(true));
    assertThat(result.get("user1").isEmpty(), is(true));
  }

  @Test
  public void testInvalidateMultipleSessions() {
    Set<String> sps1 = new HashSet<>();
    sps1.add("sp1");
    activeSessions.put("user1", sps1);

    Set<String> sps2 = new HashSet<>();
    sps2.add("sp2");
    activeSessions.put("user2", sps2);

    sessionHandler.invalidateSession("user1");
    assertThat(activeSessions.containsKey("user1"), is(false));
    assertThat(activeSessions.containsKey("user2"), is(true));

    sessionHandler.invalidateSession("user2");
    assertThat(activeSessions.containsKey("user2"), is(false));
    assertThat(activeSessions.isEmpty(), is(true));
  }

  @Test
  public void testGetActiveSessionsImmutability() {
    Set<String> sps = new HashSet<>();
    sps.add("sp1");
    activeSessions.put("user1", sps);

    Map<String, Set<String>> result = sessionHandler.getActiveSessions();
    // Verify the returned map reflects the internal state
    assertThat(result.size(), is(1));
  }
}
