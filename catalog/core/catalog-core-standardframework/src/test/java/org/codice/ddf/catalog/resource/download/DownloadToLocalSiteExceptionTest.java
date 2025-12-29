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
package org.codice.ddf.catalog.resource.download;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

class DownloadToLocalSiteExceptionTest {

  private static final String TEST_MESSAGE = "Download failed";

  @Test
  void testStatusOnlyConstructor() {
    DownloadToLocalSiteException exception = new DownloadToLocalSiteException(Status.NOT_FOUND);

    assertThat(exception.getStatus(), is(Status.NOT_FOUND));
    assertThat(exception.getMessage(), is(nullValue()));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testStatusAndMessageConstructor() {
    DownloadToLocalSiteException exception =
        new DownloadToLocalSiteException(Status.INTERNAL_SERVER_ERROR, TEST_MESSAGE);

    assertThat(exception.getStatus(), is(Status.INTERNAL_SERVER_ERROR));
    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testStatusMessageAndThrowableConstructor() {
    Throwable cause = new RuntimeException("root cause");
    DownloadToLocalSiteException exception =
        new DownloadToLocalSiteException(Status.BAD_REQUEST, TEST_MESSAGE, cause);

    assertThat(exception.getStatus(), is(Status.BAD_REQUEST));
    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testStatusAndThrowableConstructor() {
    Throwable cause = new RuntimeException("root cause");
    DownloadToLocalSiteException exception =
        new DownloadToLocalSiteException(Status.SERVICE_UNAVAILABLE, cause);

    assertThat(exception.getStatus(), is(Status.SERVICE_UNAVAILABLE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testIsRuntimeException() {
    DownloadToLocalSiteException exception = new DownloadToLocalSiteException(Status.OK);

    assertThat(exception, instanceOf(RuntimeException.class));
  }

  @Test
  void testWithDifferentStatusCodes() {
    assertThat(new DownloadToLocalSiteException(Status.OK).getStatus(), is(Status.OK));
    assertThat(
        new DownloadToLocalSiteException(Status.FORBIDDEN).getStatus(), is(Status.FORBIDDEN));
    assertThat(
        new DownloadToLocalSiteException(Status.UNAUTHORIZED).getStatus(), is(Status.UNAUTHORIZED));
  }

  @Test
  void testWithNullMessage() {
    DownloadToLocalSiteException exception =
        new DownloadToLocalSiteException(Status.NOT_FOUND, (String) null);

    assertThat(exception.getStatus(), is(Status.NOT_FOUND));
    assertThat(exception.getMessage(), is(nullValue()));
  }
}
