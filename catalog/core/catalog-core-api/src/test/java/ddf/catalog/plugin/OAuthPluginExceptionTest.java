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
package ddf.catalog.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/** Tests for {@link OAuthPluginException} class and its ErrorType enum. */
public class OAuthPluginExceptionTest {

  private static final String TEST_SOURCE_ID = "test-source";
  private static final String TEST_URL = "https://example.com/oauth";
  private static final String TEST_BASE_URL = "https://example.com";

  @Test
  public void testErrorTypeNoAuthExists() {
    assertThat(OAuthPluginException.ErrorType.NO_AUTH, is(notNullValue()));
  }

  @Test
  public void testErrorTypeAuthSourceExists() {
    assertThat(OAuthPluginException.ErrorType.AUTH_SOURCE, is(notNullValue()));
  }

  @Test
  public void testErrorTypeValuesCount() {
    assertThat(OAuthPluginException.ErrorType.values().length, is(2));
  }

  @Test
  public void testErrorTypeValuesOrder() {
    assertThat(
        OAuthPluginException.ErrorType.values(),
        arrayContaining(
            OAuthPluginException.ErrorType.NO_AUTH, OAuthPluginException.ErrorType.AUTH_SOURCE));
  }

  @Test
  public void testErrorTypeNoAuthStatusCode() {
    assertThat(OAuthPluginException.ErrorType.NO_AUTH.getStatusCode(), is(401));
  }

  @Test
  public void testErrorTypeAuthSourceStatusCode() {
    assertThat(OAuthPluginException.ErrorType.AUTH_SOURCE.getStatusCode(), is(412));
  }

  @Test
  public void testErrorTypeNoAuthOrdinal() {
    assertThat(OAuthPluginException.ErrorType.NO_AUTH.ordinal(), is(0));
  }

  @Test
  public void testErrorTypeAuthSourceOrdinal() {
    assertThat(OAuthPluginException.ErrorType.AUTH_SOURCE.ordinal(), is(1));
  }

  @Test
  public void testErrorTypeNoAuthName() {
    assertThat(OAuthPluginException.ErrorType.NO_AUTH.name(), is("NO_AUTH"));
  }

  @Test
  public void testErrorTypeAuthSourceName() {
    assertThat(OAuthPluginException.ErrorType.AUTH_SOURCE.name(), is("AUTH_SOURCE"));
  }

  @Test
  public void testExceptionConstructorAndGetters() {
    Map<String, String> params = new HashMap<>();
    params.put("key1", "value1");

    OAuthPluginException exception =
        new OAuthPluginException(
            TEST_SOURCE_ID,
            TEST_URL,
            TEST_BASE_URL,
            params,
            OAuthPluginException.ErrorType.NO_AUTH);

    assertThat(exception.getSourceId(), is(TEST_SOURCE_ID));
    assertThat(exception.getUrl(), is(TEST_URL));
    assertThat(exception.getBaseUrl(), is(TEST_BASE_URL));
    assertThat(exception.getParameters(), is(params));
    assertThat(exception.getErrorType(), is(OAuthPluginException.ErrorType.NO_AUTH));
  }

  @Test
  public void testExceptionWithAuthSourceErrorType() {
    Map<String, String> params = new HashMap<>();

    OAuthPluginException exception =
        new OAuthPluginException(
            TEST_SOURCE_ID,
            TEST_URL,
            TEST_BASE_URL,
            params,
            OAuthPluginException.ErrorType.AUTH_SOURCE);

    assertThat(exception.getErrorType(), is(OAuthPluginException.ErrorType.AUTH_SOURCE));
  }

  @Test
  public void testExceptionWithEmptyParameters() {
    Map<String, String> params = new HashMap<>();

    OAuthPluginException exception =
        new OAuthPluginException(
            TEST_SOURCE_ID,
            TEST_URL,
            TEST_BASE_URL,
            params,
            OAuthPluginException.ErrorType.NO_AUTH);

    assertThat(exception.getParameters().isEmpty(), is(true));
  }

  @Test
  public void testExceptionWithMultipleParameters() {
    Map<String, String> params = new HashMap<>();
    params.put("client_id", "my-client");
    params.put("redirect_uri", "https://callback.example.com");
    params.put("scope", "openid profile");

    OAuthPluginException exception =
        new OAuthPluginException(
            TEST_SOURCE_ID,
            TEST_URL,
            TEST_BASE_URL,
            params,
            OAuthPluginException.ErrorType.NO_AUTH);

    assertThat(exception.getParameters().size(), is(3));
    assertThat(exception.getParameters().get("client_id"), is("my-client"));
  }

  @Test
  public void testExceptionIsRuntimeException() {
    OAuthPluginException exception =
        new OAuthPluginException(
            TEST_SOURCE_ID,
            TEST_URL,
            TEST_BASE_URL,
            new HashMap<>(),
            OAuthPluginException.ErrorType.NO_AUTH);

    assertThat(exception instanceof RuntimeException, is(true));
  }
}
