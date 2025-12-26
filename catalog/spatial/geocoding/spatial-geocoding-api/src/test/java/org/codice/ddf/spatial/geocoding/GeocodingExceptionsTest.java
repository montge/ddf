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
package org.codice.ddf.spatial.geocoding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class GeocodingExceptionsTest {

  private static final String TEST_MESSAGE = "Test exception message";

  // GeoEntryExtractionException tests

  @Test
  void testGeoEntryExtractionExceptionMessageConstructor() {
    GeoEntryExtractionException exception = new GeoEntryExtractionException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testGeoEntryExtractionExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    GeoEntryExtractionException exception = new GeoEntryExtractionException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testGeoEntryExtractionExceptionIsCheckedException() {
    GeoEntryExtractionException exception = new GeoEntryExtractionException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  // GeoEntryQueryException tests

  @Test
  void testGeoEntryQueryExceptionMessageConstructor() {
    GeoEntryQueryException exception = new GeoEntryQueryException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testGeoEntryQueryExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    GeoEntryQueryException exception = new GeoEntryQueryException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testGeoEntryQueryExceptionIsCheckedException() {
    GeoEntryQueryException exception = new GeoEntryQueryException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  // GeoEntryIndexingException tests

  @Test
  void testGeoEntryIndexingExceptionMessageConstructor() {
    GeoEntryIndexingException exception = new GeoEntryIndexingException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testGeoEntryIndexingExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    GeoEntryIndexingException exception = new GeoEntryIndexingException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testGeoEntryIndexingExceptionIsCheckedException() {
    GeoEntryIndexingException exception = new GeoEntryIndexingException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  // GeoNamesRemoteDownloadException tests

  @Test
  void testGeoNamesRemoteDownloadExceptionMessageConstructor() {
    GeoNamesRemoteDownloadException exception = new GeoNamesRemoteDownloadException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testGeoNamesRemoteDownloadExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    GeoNamesRemoteDownloadException exception =
        new GeoNamesRemoteDownloadException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testGeoNamesRemoteDownloadExceptionIsCheckedException() {
    GeoNamesRemoteDownloadException exception = new GeoNamesRemoteDownloadException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  // FeatureQueryException tests

  @Test
  void testFeatureQueryExceptionMessageConstructor() {
    FeatureQueryException exception = new FeatureQueryException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testFeatureQueryExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    FeatureQueryException exception = new FeatureQueryException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testFeatureQueryExceptionIsCheckedException() {
    FeatureQueryException exception = new FeatureQueryException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  // FeatureIndexingException tests

  @Test
  void testFeatureIndexingExceptionMessageConstructor() {
    FeatureIndexingException exception = new FeatureIndexingException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testFeatureIndexingExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    FeatureIndexingException exception = new FeatureIndexingException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testFeatureIndexingExceptionIsCheckedException() {
    FeatureIndexingException exception = new FeatureIndexingException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  // FeatureExtractionException tests

  @Test
  void testFeatureExtractionExceptionMessageConstructor() {
    FeatureExtractionException exception = new FeatureExtractionException(TEST_MESSAGE);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
  }

  @Test
  void testFeatureExtractionExceptionMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    FeatureExtractionException exception = new FeatureExtractionException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testFeatureExtractionExceptionIsCheckedException() {
    FeatureExtractionException exception = new FeatureExtractionException(TEST_MESSAGE);

    assertThat(exception, instanceOf(Exception.class));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
