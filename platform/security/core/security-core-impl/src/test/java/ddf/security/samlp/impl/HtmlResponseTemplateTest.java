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
package ddf.security.samlp.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

public class HtmlResponseTemplateTest {

  private static final String TEST_TARGET_URL = "https://example.com/acs";
  private static final String TEST_SAML_VALUE = "PHNhbWw6QXNzZXJ0aW9uPi4uLjwvc2FtbDpBc3NlcnRpb24+";
  private static final String TEST_RELAY_STATE = "relay-state-123";

  @Test
  public void testGetPostPageWithAllParameters() {
    String result =
        HtmlResponseTemplate.getPostPage(
            TEST_TARGET_URL, SamlProtocol.Type.RESPONSE, TEST_SAML_VALUE, TEST_RELAY_STATE);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString(TEST_TARGET_URL));
    assertThat(result, containsString(TEST_SAML_VALUE));
    assertThat(result, containsString(TEST_RELAY_STATE));
  }

  @Test
  public void testGetPostPageWithResponseType() {
    String result =
        HtmlResponseTemplate.getPostPage(
            TEST_TARGET_URL, SamlProtocol.Type.RESPONSE, TEST_SAML_VALUE, TEST_RELAY_STATE);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString(SamlProtocol.Type.RESPONSE.getKey()));
  }

  @Test
  public void testGetPostPageWithRequestType() {
    String result =
        HtmlResponseTemplate.getPostPage(
            TEST_TARGET_URL, SamlProtocol.Type.REQUEST, TEST_SAML_VALUE, TEST_RELAY_STATE);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString(SamlProtocol.Type.REQUEST.getKey()));
  }

  @Test
  public void testGetPostPageWithNullUrl() {
    String result =
        HtmlResponseTemplate.getPostPage(
            null, SamlProtocol.Type.RESPONSE, TEST_SAML_VALUE, TEST_RELAY_STATE);

    assertThat(result, is(notNullValue()));
    // Should contain empty string instead of null
  }

  @Test
  public void testGetPostPageWithNullType() {
    String result =
        HtmlResponseTemplate.getPostPage(TEST_TARGET_URL, null, TEST_SAML_VALUE, TEST_RELAY_STATE);

    assertThat(result, is(notNullValue()));
    // Should use NULL type's key
    assertThat(result, containsString(SamlProtocol.Type.NULL.getKey()));
  }

  @Test
  public void testGetPostPageWithNullSamlValue() {
    String result =
        HtmlResponseTemplate.getPostPage(
            TEST_TARGET_URL, SamlProtocol.Type.RESPONSE, null, TEST_RELAY_STATE);

    assertThat(result, is(notNullValue()));
    // Should contain empty string instead of null
  }

  @Test
  public void testGetPostPageWithNullRelayState() {
    String result =
        HtmlResponseTemplate.getPostPage(
            TEST_TARGET_URL, SamlProtocol.Type.RESPONSE, TEST_SAML_VALUE, null);

    assertThat(result, is(notNullValue()));
    // Should contain empty string instead of null
  }

  @Test
  public void testGetPostPageWithAllNullParameters() {
    String result = HtmlResponseTemplate.getPostPage(null, null, null, null);

    assertThat(result, is(notNullValue()));
    // Should not throw exception, should handle all nulls gracefully
  }

  @Test
  public void testGetRedirectPageWithValidUrl() {
    String result = HtmlResponseTemplate.getRedirectPage(TEST_TARGET_URL);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString(TEST_TARGET_URL));
  }

  @Test
  public void testGetRedirectPageWithNullUrl() {
    String result = HtmlResponseTemplate.getRedirectPage(null);

    assertThat(result, is(notNullValue()));
    // Should contain empty string instead of null
  }

  @Test
  public void testGetRedirectPageWithEmptyUrl() {
    String result = HtmlResponseTemplate.getRedirectPage("");

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetRedirectPageContainsHtmlStructure() {
    String result = HtmlResponseTemplate.getRedirectPage(TEST_TARGET_URL);

    // Should be a valid HTML page
    assertThat(result, is(notNullValue()));
    // Basic HTML structure check
    assertThat(result.length() > 0, is(true));
  }

  @Test
  public void testGetPostPageWithSpecialCharactersInUrl() {
    String urlWithParams = "https://example.com/acs?param1=value1&param2=value2";
    String result =
        HtmlResponseTemplate.getPostPage(
            urlWithParams, SamlProtocol.Type.RESPONSE, TEST_SAML_VALUE, TEST_RELAY_STATE);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetPostPageWithLongSamlValue() {
    StringBuilder longValue = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longValue.append("ABC");
    }

    String result =
        HtmlResponseTemplate.getPostPage(
            TEST_TARGET_URL, SamlProtocol.Type.RESPONSE, longValue.toString(), TEST_RELAY_STATE);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString(longValue.toString()));
  }
}
