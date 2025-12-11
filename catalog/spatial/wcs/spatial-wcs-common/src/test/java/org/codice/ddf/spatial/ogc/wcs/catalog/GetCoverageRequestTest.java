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
package org.codice.ddf.spatial.ogc.wcs.catalog;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link GetCoverageRequest}. */
public class GetCoverageRequestTest {

  @Test
  public void testGetCoverageRequestWithIdAndFormat() {
    GetCoverageRequest request = new GetCoverageRequest("testId", "image/tiff");
    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is("testId"));
    assertThat(request.getCoverageFormat(), is("image/tiff"));
  }

  @Test
  public void testGetCoverageRequestWithNullId() {
    GetCoverageRequest request = new GetCoverageRequest(null, "image/png");
    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(nullValue()));
    assertThat(request.getCoverageFormat(), is("image/png"));
  }

  @Test
  public void testGetCoverageRequestWithNullFormat() {
    GetCoverageRequest request = new GetCoverageRequest("coverage123", null);
    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is("coverage123"));
    assertThat(request.getCoverageFormat(), is(nullValue()));
  }

  @Test
  public void testGetCoverageRequestWithNullIdAndFormat() {
    GetCoverageRequest request = new GetCoverageRequest(null, null);
    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(nullValue()));
    assertThat(request.getCoverageFormat(), is(nullValue()));
  }

  @Test
  public void testGetCoverageRequestWithEmptyId() {
    GetCoverageRequest request = new GetCoverageRequest("", "image/jpeg");
    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(""));
    assertThat(request.getCoverageFormat(), is("image/jpeg"));
  }

  @Test
  public void testGetCoverageRequestWithEmptyFormat() {
    GetCoverageRequest request = new GetCoverageRequest("coverage456", "");
    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is("coverage456"));
    assertThat(request.getCoverageFormat(), is(""));
  }

  @Test
  public void testGetCoverageRequestWithVariousFormats() {
    GetCoverageRequest tiffRequest = new GetCoverageRequest("cov1", "image/tiff");
    assertThat(tiffRequest.getCoverageFormat(), is("image/tiff"));

    GetCoverageRequest jpegRequest = new GetCoverageRequest("cov2", "image/jpeg");
    assertThat(jpegRequest.getCoverageFormat(), is("image/jpeg"));

    GetCoverageRequest pngRequest = new GetCoverageRequest("cov3", "image/png");
    assertThat(pngRequest.getCoverageFormat(), is("image/png"));

    GetCoverageRequest geotiffRequest = new GetCoverageRequest("cov4", "image/geotiff");
    assertThat(geotiffRequest.getCoverageFormat(), is("image/geotiff"));
  }

  @Test
  public void testGetCoverageRequestGetId() {
    GetCoverageRequest request = new GetCoverageRequest("uniqueId123", "image/tiff");
    String id = request.getId();
    assertThat(id, is("uniqueId123"));
  }

  @Test
  public void testGetCoverageRequestGetCoverageFormat() {
    GetCoverageRequest request = new GetCoverageRequest("coverage789", "application/netcdf");
    String format = request.getCoverageFormat();
    assertThat(format, is("application/netcdf"));
  }

  @Test
  public void testMultipleInstancesAreIndependent() {
    GetCoverageRequest request1 = new GetCoverageRequest("id1", "format1");
    GetCoverageRequest request2 = new GetCoverageRequest("id2", "format2");

    assertThat(request1.getId(), is("id1"));
    assertThat(request1.getCoverageFormat(), is("format1"));
    assertThat(request2.getId(), is("id2"));
    assertThat(request2.getCoverageFormat(), is("format2"));

    // Verify they are different instances
    assertThat(request1 == request2, is(false));
  }

  @Test
  public void testGetCoverageRequestWithLongId() {
    String longId = "very-long-coverage-id-with-many-characters-to-test-string-handling-1234567890";
    GetCoverageRequest request = new GetCoverageRequest(longId, "image/tiff");
    assertThat(request.getId(), is(longId));
  }

  @Test
  public void testGetCoverageRequestWithSpecialCharactersInId() {
    String specialId = "coverage-id_with.special@chars#123";
    GetCoverageRequest request = new GetCoverageRequest(specialId, "image/png");
    assertThat(request.getId(), is(specialId));
  }
}
