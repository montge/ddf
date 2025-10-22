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
package org.codice.ddf.catalog.actions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import ddf.action.Action;
import ddf.catalog.data.Metacard;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codice.ddf.configuration.SystemBaseUrl;
import org.codice.ddf.configuration.SystemInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Extended tests for AbstractMetacardActionProvider to increase code coverage to 80%+. These tests
 * focus on edge cases, error handling, and various metacard configurations.
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractMetacardActionProviderExtendedTest {

  private static final String ACTION_ID = "test-action-id";

  private static final String TITLE = "Test Action";

  private static final String DESCRIPTION = "Test action description";

  private static final String METACARD_ID = "test-metacard-123";

  private static final String SOURCE_ID = "test-source";

  private static final String TEST_URL = "https://example.com/action";

  @Mock private Metacard metacard;

  @Mock private Action mockAction;

  private String originalExternalHost;

  private String originalSiteName;

  @Before
  public void setup() {
    // Save original system properties
    originalExternalHost = System.getProperty(SystemBaseUrl.EXTERNAL_HOST);
    originalSiteName = System.getProperty(SystemInfo.SITE_NAME);

    // Setup default metacard
    when(metacard.getId()).thenReturn(METACARD_ID);
    when(metacard.getSourceId()).thenReturn(SOURCE_ID);
    when(metacard.getTags()).thenReturn(Collections.singleton(Metacard.DEFAULT_TAG));
  }

  @After
  public void tearDown() {
    // Restore original system properties
    if (originalExternalHost != null) {
      System.setProperty(SystemBaseUrl.EXTERNAL_HOST, originalExternalHost);
    } else {
      System.clearProperty(SystemBaseUrl.EXTERNAL_HOST);
    }

    if (originalSiteName != null) {
      System.setProperty(SystemInfo.SITE_NAME, originalSiteName);
    } else {
      System.clearProperty(SystemInfo.SITE_NAME);
    }
  }

  @Test
  public void testConstructorWithValidParameters() {
    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    assertThat(provider.getId(), is(ACTION_ID));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithEmptyActionProviderId() {
    new TestMetacardActionProvider("", TITLE, DESCRIPTION);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithWhitespaceActionProviderId() {
    new TestMetacardActionProvider("   ", TITLE, DESCRIPTION);
  }

  @Test
  public void testGetIdReturnsCorrectValue() {
    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    assertThat(provider.getId(), is(ACTION_ID));
  }

  @Test
  public void testToStringIncludesActionProviderDetails() {
    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    String toString = provider.toString();

    assertThat(toString, containsString("ActionProvider"));
    assertThat(toString, containsString(ACTION_ID));
  }

  @Test
  public void testCanHandleWithMetacardHavingNullTags() {
    when(metacard.getTags()).thenReturn(null);
    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    boolean canHandle = provider.canHandle(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleWithMetacardHavingEmptyTags() {
    when(metacard.getTags()).thenReturn(Collections.emptySet());
    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    boolean canHandle = provider.canHandle(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleWithMetacardHavingDefaultTag() {
    when(metacard.getTags()).thenReturn(Collections.singleton(Metacard.DEFAULT_TAG));
    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    boolean canHandle = provider.canHandle(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleWithMetacardHavingMultipleTags() {
    Set<String> tags = new HashSet<>();
    tags.add(Metacard.DEFAULT_TAG);
    tags.add("custom-tag");
    when(metacard.getTags()).thenReturn(tags);

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    boolean canHandle = provider.canHandle(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleWithMetacardHavingOnlyNonDefaultTags() {
    Set<String> tags = new HashSet<>();
    tags.add("custom-tag");
    tags.add("another-tag");
    when(metacard.getTags()).thenReturn(tags);

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    boolean canHandle = provider.canHandle(metacard);

    assertThat(canHandle, is(false));
  }

  @Test
  public void testGetActionWithEmptyStringId() {
    when(metacard.getId()).thenReturn("");
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "example.com");

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithBlankSourceId() throws Exception {
    when(metacard.getSourceId()).thenReturn("   ");
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "example.com");
    System.setProperty(SystemInfo.SITE_NAME, "test-site");

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionWithNullSourceIdUsesSiteName() throws Exception {
    when(metacard.getSourceId()).thenReturn(null);
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "example.com");
    System.setProperty(SystemInfo.SITE_NAME, "my-ddf-site");

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionWithEmptySourceIdUsesSiteName() throws Exception {
    when(metacard.getSourceId()).thenReturn("");
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "example.com");
    System.setProperty(SystemInfo.SITE_NAME, "my-ddf-site");

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(notNullValue()));
  }

  @Test
  public void testGetActionWithMalformedURLException() {
    when(metacard.getSourceId()).thenReturn("valid-source");
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "example.com");

    MalformedURLActionProvider provider =
        new MalformedURLActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithURISyntaxException() {
    when(metacard.getSourceId()).thenReturn("valid-source");
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "example.com");

    URISyntaxActionProvider provider = new URISyntaxActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithUnsupportedEncodingException() {
    when(metacard.getSourceId()).thenReturn("valid-source");
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "example.com");

    UnsupportedEncodingActionProvider provider =
        new UnsupportedEncodingActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testGetActionWithRuntimeException() {
    when(metacard.getSourceId()).thenReturn("valid-source");
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "example.com");

    RuntimeExceptionActionProvider provider =
        new RuntimeExceptionActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(nullValue()));
  }

  @Test
  public void testCanHandleMetacardReturnsTrue() {
    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    boolean canHandle = provider.canHandleMetacard(metacard);

    assertThat(canHandle, is(true));
  }

  @Test
  public void testCanHandleMetacardWithCustomImplementation() {
    CustomCanHandleActionProvider provider =
        new CustomCanHandleActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    boolean canHandle = provider.canHandleMetacard(metacard);

    assertThat(canHandle, is(false));
  }

  @Test
  public void testGetActionCreatesCorrectAction() throws Exception {
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "localhost");

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(notNullValue()));
    assertThat(action.getId(), is(ACTION_ID));
    assertThat(action.getTitle(), is(TITLE));
    assertThat(action.getDescription(), is(DESCRIPTION));
    assertThat(action.getUrl(), is(notNullValue()));
  }

  @Test
  public void testGetActionUrlContainsMetacardId() throws Exception {
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "localhost");

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(notNullValue()));
    assertThat(action.getUrl().toString(), containsString(METACARD_ID));
  }

  @Test
  public void testGetActionUrlContainsSourceId() throws Exception {
    System.setProperty(SystemBaseUrl.EXTERNAL_HOST, "localhost");

    TestMetacardActionProvider provider =
        new TestMetacardActionProvider(ACTION_ID, TITLE, DESCRIPTION);

    Action action = provider.getAction(metacard);

    assertThat(action, is(notNullValue()));
    assertThat(action.getUrl().toString(), containsString(SOURCE_ID));
  }

  /** Test implementation of AbstractMetacardActionProvider */
  private static class TestMetacardActionProvider extends AbstractMetacardActionProvider {

    TestMetacardActionProvider(String actionProviderId, String title, String description) {
      super(actionProviderId, title, description);
    }

    @Override
    protected Action createMetacardAction(
        String actionProviderId, String title, String description, URL url) {
      return new TestAction(actionProviderId, title, description, url);
    }

    @Override
    protected URL getMetacardActionUrl(String metacardSource, Metacard metacard)
        throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
      return new URL(TEST_URL + "?source=" + metacardSource + "&id=" + metacard.getId());
    }
  }

  /** Test implementation that throws MalformedURLException */
  private static class MalformedURLActionProvider extends AbstractMetacardActionProvider {

    MalformedURLActionProvider(String actionProviderId, String title, String description) {
      super(actionProviderId, title, description);
    }

    @Override
    protected Action createMetacardAction(
        String actionProviderId, String title, String description, URL url) {
      return new TestAction(actionProviderId, title, description, url);
    }

    @Override
    protected URL getMetacardActionUrl(String metacardSource, Metacard metacard)
        throws MalformedURLException {
      throw new MalformedURLException("Test exception");
    }
  }

  /** Test implementation that throws URISyntaxException */
  private static class URISyntaxActionProvider extends AbstractMetacardActionProvider {

    URISyntaxActionProvider(String actionProviderId, String title, String description) {
      super(actionProviderId, title, description);
    }

    @Override
    protected Action createMetacardAction(
        String actionProviderId, String title, String description, URL url) {
      return new TestAction(actionProviderId, title, description, url);
    }

    @Override
    protected URL getMetacardActionUrl(String metacardSource, Metacard metacard)
        throws URISyntaxException {
      throw new URISyntaxException("test", "Test exception");
    }
  }

  /** Test implementation that throws UnsupportedEncodingException */
  private static class UnsupportedEncodingActionProvider extends AbstractMetacardActionProvider {

    UnsupportedEncodingActionProvider(String actionProviderId, String title, String description) {
      super(actionProviderId, title, description);
    }

    @Override
    protected Action createMetacardAction(
        String actionProviderId, String title, String description, URL url) {
      return new TestAction(actionProviderId, title, description, url);
    }

    @Override
    protected URL getMetacardActionUrl(String metacardSource, Metacard metacard)
        throws UnsupportedEncodingException {
      throw new UnsupportedEncodingException("Test exception");
    }
  }

  /** Test implementation that throws RuntimeException */
  private static class RuntimeExceptionActionProvider extends AbstractMetacardActionProvider {

    RuntimeExceptionActionProvider(String actionProviderId, String title, String description) {
      super(actionProviderId, title, description);
    }

    @Override
    protected Action createMetacardAction(
        String actionProviderId, String title, String description, URL url) {
      return new TestAction(actionProviderId, title, description, url);
    }

    @Override
    protected URL getMetacardActionUrl(String metacardSource, Metacard metacard) {
      throw new RuntimeException("Test runtime exception");
    }
  }

  /** Test implementation with custom canHandleMetacard logic */
  private static class CustomCanHandleActionProvider extends AbstractMetacardActionProvider {

    CustomCanHandleActionProvider(String actionProviderId, String title, String description) {
      super(actionProviderId, title, description);
    }

    @Override
    protected boolean canHandleMetacard(Metacard metacard) {
      return false;
    }

    @Override
    protected Action createMetacardAction(
        String actionProviderId, String title, String description, URL url) {
      return new TestAction(actionProviderId, title, description, url);
    }

    @Override
    protected URL getMetacardActionUrl(String metacardSource, Metacard metacard)
        throws MalformedURLException {
      return new URL(TEST_URL);
    }
  }

  /** Simple test Action implementation */
  private static class TestAction implements Action {

    private final String id;

    private final String title;

    private final String description;

    private final URL url;

    TestAction(String id, String title, String description, URL url) {
      this.id = id;
      this.title = title;
      this.description = description;
      this.url = url;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public String getTitle() {
      return title;
    }

    @Override
    public String getDescription() {
      return description;
    }

    @Override
    public URL getUrl() {
      return url;
    }
  }
}
