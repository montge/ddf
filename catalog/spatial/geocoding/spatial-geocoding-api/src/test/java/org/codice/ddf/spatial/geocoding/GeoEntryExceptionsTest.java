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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/** Unit tests for GeoEntry exception classes. */
public class GeoEntryExceptionsTest {

  @Test
  public void testGeoEntryIndexingException() {
    GeoEntryIndexingException exception = new GeoEntryIndexingException("Test message");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test message"));
  }

  @Test
  public void testGeoEntryIndexingExceptionWithCause() {
    Throwable cause = new RuntimeException("Cause exception");
    GeoEntryIndexingException exception = new GeoEntryIndexingException("Test message", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Test message"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testGeoEntryQueryException() {
    GeoEntryQueryException exception = new GeoEntryQueryException("Query failed");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Query failed"));
  }

  @Test
  public void testGeoEntryQueryExceptionWithCause() {
    Throwable cause = new IllegalArgumentException("Invalid query");
    GeoEntryQueryException exception = new GeoEntryQueryException("Query failed", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Query failed"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testGeoEntryExtractionException() {
    GeoEntryExtractionException exception = new GeoEntryExtractionException("Extraction failed");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Extraction failed"));
  }

  @Test
  public void testGeoEntryExtractionExceptionWithCause() {
    Throwable cause = new IllegalStateException("Invalid state");
    GeoEntryExtractionException exception =
        new GeoEntryExtractionException("Extraction failed", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Extraction failed"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testFeatureIndexingException() {
    FeatureIndexingException exception = new FeatureIndexingException("Feature index failed");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Feature index failed"));
  }

  @Test
  public void testFeatureIndexingExceptionWithCause() {
    Throwable cause = new RuntimeException("Feature error");
    FeatureIndexingException exception =
        new FeatureIndexingException("Feature index failed", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Feature index failed"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testFeatureQueryException() {
    FeatureQueryException exception = new FeatureQueryException("Feature query error");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Feature query error"));
  }

  @Test
  public void testFeatureQueryExceptionWithCause() {
    Throwable cause = new IllegalArgumentException("Bad feature query");
    FeatureQueryException exception = new FeatureQueryException("Feature query error", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Feature query error"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testFeatureExtractionException() {
    FeatureExtractionException exception =
        new FeatureExtractionException("Feature extraction error");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Feature extraction error"));
  }

  @Test
  public void testFeatureExtractionExceptionWithCause() {
    Throwable cause = new IllegalStateException("Invalid feature state");
    FeatureExtractionException exception =
        new FeatureExtractionException("Feature extraction error", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Feature extraction error"));
    assertThat(exception.getCause(), is(cause));
  }

  @Test
  public void testGeoNamesRemoteDownloadException() {
    GeoNamesRemoteDownloadException exception =
        new GeoNamesRemoteDownloadException("Download failed");
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Download failed"));
  }

  @Test
  public void testGeoNamesRemoteDownloadExceptionWithCause() {
    Throwable cause = new RuntimeException("Network error");
    GeoNamesRemoteDownloadException exception =
        new GeoNamesRemoteDownloadException("Download failed", cause);
    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is("Download failed"));
    assertThat(exception.getCause(), is(cause));
  }
}
