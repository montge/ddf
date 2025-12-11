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
package ddf.action.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.action.Action;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link ActionImpl} class. */
public class ActionImplTest {

  private static final String PROVIDER_ID = "test-provider";
  private static final String TITLE = "Test Action";
  private static final String DESCRIPTION = "A test action description";
  private static final String URL_STRING = "http://example.com/action";

  private URL testUrl;
  private ActionImpl action;

  @BeforeEach
  public void setUp() throws MalformedURLException {
    testUrl = new URL(URL_STRING);
    action = new ActionImpl(PROVIDER_ID, TITLE, DESCRIPTION, testUrl);
  }

  @Test
  public void testImplementsActionInterface() {
    assertThat(action instanceof Action, is(true));
  }

  @Test
  public void testConstructorSetsAllFields() {
    assertThat(action.getId(), is(PROVIDER_ID));
    assertThat(action.getTitle(), is(TITLE));
    assertThat(action.getDescription(), is(DESCRIPTION));
    assertThat(action.getUrl(), is(testUrl));
  }

  @Test
  public void testGetId() {
    assertThat(action.getId(), is(PROVIDER_ID));
  }

  @Test
  public void testSetId() {
    String newId = "new-provider-id";
    action.setId(newId);
    assertThat(action.getId(), is(newId));
  }

  @Test
  public void testGetTitle() {
    assertThat(action.getTitle(), is(TITLE));
  }

  @Test
  public void testSetTitle() {
    String newTitle = "New Title";
    action.setTitle(newTitle);
    assertThat(action.getTitle(), is(newTitle));
  }

  @Test
  public void testGetDescription() {
    assertThat(action.getDescription(), is(DESCRIPTION));
  }

  @Test
  public void testSetDescription() {
    String newDescription = "New description";
    action.setDescription(newDescription);
    assertThat(action.getDescription(), is(newDescription));
  }

  @Test
  public void testGetUrl() {
    assertThat(action.getUrl(), is(testUrl));
  }

  @Test
  public void testSetUrl() throws MalformedURLException {
    URL newUrl = new URL("https://new.example.com/action");
    action.setUrl(newUrl);
    assertThat(action.getUrl(), is(newUrl));
  }

  @Test
  public void testConstructorWithNullValues() {
    ActionImpl nullAction = new ActionImpl(null, null, null, null);

    assertThat(nullAction, is(notNullValue()));
    assertThat(nullAction.getId(), is(nullValue()));
    assertThat(nullAction.getTitle(), is(nullValue()));
    assertThat(nullAction.getDescription(), is(nullValue()));
    assertThat(nullAction.getUrl(), is(nullValue()));
  }

  @Test
  public void testSetNullValues() {
    action.setId(null);
    action.setTitle(null);
    action.setDescription(null);
    action.setUrl(null);

    assertThat(action.getId(), is(nullValue()));
    assertThat(action.getTitle(), is(nullValue()));
    assertThat(action.getDescription(), is(nullValue()));
    assertThat(action.getUrl(), is(nullValue()));
  }

  @Test
  public void testUrlWithDifferentProtocols() throws MalformedURLException {
    URL httpsUrl = new URL("https://secure.example.com/action");
    action.setUrl(httpsUrl);
    assertThat(action.getUrl().getProtocol(), is("https"));

    URL fileUrl = new URL("file:///path/to/file");
    action.setUrl(fileUrl);
    assertThat(action.getUrl().getProtocol(), is("file"));
  }

  @Test
  public void testEmptyStringValues() {
    ActionImpl emptyAction = new ActionImpl("", "", "", testUrl);

    assertThat(emptyAction.getId(), is(""));
    assertThat(emptyAction.getTitle(), is(""));
    assertThat(emptyAction.getDescription(), is(""));
  }
}
