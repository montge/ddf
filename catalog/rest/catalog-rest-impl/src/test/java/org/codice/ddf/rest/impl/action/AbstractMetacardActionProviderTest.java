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
package org.codice.ddf.rest.impl.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import ddf.action.Action;
import ddf.catalog.data.Metacard;
import org.codice.ddf.configuration.SystemBaseUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests for {@link AbstractMetacardActionProvider} class (deprecated).
 *
 * <p>Note: This class is deprecated but still needs test coverage.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AbstractMetacardActionProviderTest {

  private static final String TEST_ID = "test-action-id";
  private static final String METACARD_ID = "metacard123";
  private static final String SOURCE_ID = "source456";
  private static final String SITE_NAME = "testSite";
  private static final String HOST = "localhost";
  private static final String UNKNOWN_TARGET = "0.0.0.0";

  @Mock private Metacard metacard;

  private TestAbstractMetacardActionProvider actionProvider;

  @BeforeEach
  public void setUp() {
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, HOST);
    System.setProperty("org.codice.ddf.system.siteName", SITE_NAME);
    actionProvider = new TestAbstractMetacardActionProvider(TEST_ID);
  }

  @Test
  public void testGetIdReturnsActionProviderId() {
    assertThat(actionProvider.getId(), is(TEST_ID));
  }

  @Test
  public void testCanHandleMetacard() {
    assertThat(actionProvider.canHandle(metacard), is(true));
  }

  @Test
  public void testCannotHandleNonMetacardInput() {
    assertThat(actionProvider.canHandle("not a metacard"), is(false));
  }

  @Test
  public void testCannotHandleNullInput() {
    assertThat(actionProvider.canHandle(null), is(false));
  }

  @Test
  public void testGetActionWithValidMetacard() {
    when(metacard.getId()).thenReturn(METACARD_ID);
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionWithBlankMetacardId() {
    when(metacard.getId()).thenReturn("");
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNullMetacardId() {
    when(metacard.getId()).thenReturn(null);
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithUnsetHost() {
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, UNKNOWN_TARGET);
    actionProvider = new TestAbstractMetacardActionProvider(TEST_ID);

    when(metacard.getId()).thenReturn(METACARD_ID);
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithNullHost() {
    System.clearProperty(SystemBaseUrl.EXTERNAL_HOST);
    actionProvider = new TestAbstractMetacardActionProvider(TEST_ID);

    when(metacard.getId()).thenReturn(METACARD_ID);
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testIsHostUnsetWithUnknownTarget() {
    assertThat(actionProvider.isHostUnset(UNKNOWN_TARGET), is(true));
  }

  @Test
  public void testIsHostUnsetWithNullHost() {
    assertThat(actionProvider.isHostUnset(null), is(true));
  }

  @Test
  public void testIsHostUnsetWithValidHost() {
    assertThat(actionProvider.isHostUnset("localhost"), is(false));
  }

  @Test
  public void testIsHostUnsetWithWhitespaceOnlyHost() {
    assertThat(actionProvider.isHostUnset("   "), is(false));
  }

  @Test
  public void testGetSourceWithValidSourceId() {
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);

    String source = actionProvider.getSource(metacard);

    assertThat(source, is(SOURCE_ID));
  }

  @Test
  public void testGetSourceWithNullSourceId() {
    when(metacard.getSourceId()).thenReturn(null);

    String source = actionProvider.getSource(metacard);

    assertThat(source, is(SITE_NAME));
  }

  @Test
  public void testGetSourceWithBlankSourceId() {
    when(metacard.getSourceId()).thenReturn("");

    String source = actionProvider.getSource(metacard);

    assertThat(source, is(SITE_NAME));
  }

  @Test
  public void testGetSourceWithWhitespaceSourceId() {
    when(metacard.getSourceId()).thenReturn("   ");

    String source = actionProvider.getSource(metacard);

    assertThat(source, is(SITE_NAME));
  }

  @Test
  public void testGetActionWithNonMetacardInput() {
    Action action = actionProvider.getAction("not a metacard");

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testActionProviderWithSpecialCharactersInMetacardId() {
    String specialId = "id-with-special-chars-&?=";
    when(metacard.getId()).thenReturn(specialId);
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testActionProviderWithUnicodeInMetacardId() {
    String unicodeId = "id-with-unicode-\u00E9\u00F1";
    when(metacard.getId()).thenReturn(unicodeId);
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);

    Action action = actionProvider.getAction(metacard);

    assertThat(action, is(notNullValue()));
  }

  /** Concrete implementation of AbstractMetacardActionProvider for testing purposes. */
  private static class TestAbstractMetacardActionProvider extends AbstractMetacardActionProvider {

    public TestAbstractMetacardActionProvider(String actionProviderId) {
      this.actionProviderId = actionProviderId;
    }

    @Override
    protected Action getAction(String metacardId, String metacardSource) {
      // Return a mock action for testing
      return new Action() {
        @Override
        public String getId() {
          return actionProviderId;
        }

        @Override
        public String getTitle() {
          return "Test Action";
        }

        @Override
        public String getDescription() {
          return "Test Description";
        }

        @Override
        public java.net.URL getUrl() {
          try {
            return new java.net.URL("http://localhost:8181/test");
          } catch (java.net.MalformedURLException e) {
            return null;
          }
        }
      };
    }
  }
}
