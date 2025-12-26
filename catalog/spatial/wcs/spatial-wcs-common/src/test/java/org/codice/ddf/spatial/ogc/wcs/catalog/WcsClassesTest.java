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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class WcsClassesTest {

  // WcsConstants tests

  @Test
  void testWcsServiceName() {
    assertThat(WcsConstants.WCS, is("WCS"));
  }

  @Test
  void testWcsVersion() {
    assertThat(WcsConstants.VERSION_1_0_0, is("1.0.0"));
  }

  @Test
  void testGetCapabilitiesRequestType() {
    assertThat(WcsConstants.GET_CAPABILITIES, is("GetCapabilities"));
  }

  @Test
  void testDescribeCoverageRequestType() {
    assertThat(WcsConstants.DESCRIBE_COVERAGE, is("DescribeCoverage"));
  }

  @Test
  void testGetCoverageRequestType() {
    assertThat(WcsConstants.GET_COVERAGE, is("GetCoverage"));
  }

  @Test
  void testSrsName() {
    assertThat(WcsConstants.SRS_NAME, is("EPSG:4326"));
  }

  // GetCapabilitiesRequest tests

  @Test
  void testGetCapabilitiesRequestInstantiation() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();

    assertThat(request, is(notNullValue()));
  }

  // DescribeCoverageRequest tests

  @Test
  void testDescribeCoverageRequestInstantiation() {
    DescribeCoverageRequest request = new DescribeCoverageRequest();

    assertThat(request, is(notNullValue()));
  }

  // GetCoverageRequest tests

  @Test
  void testGetCoverageRequestConstructor() {
    GetCoverageRequest request = new GetCoverageRequest("coverage-123", "image/png");

    assertThat(request.getId(), is("coverage-123"));
    assertThat(request.getCoverageFormat(), is("image/png"));
  }

  @Test
  void testGetCoverageRequestWithDifferentValues() {
    GetCoverageRequest request = new GetCoverageRequest("test-id", "image/jpeg");

    assertThat(request.getId(), is("test-id"));
    assertThat(request.getCoverageFormat(), is("image/jpeg"));
  }

  @Test
  void testGetCoverageRequestWithNullValues() {
    GetCoverageRequest request = new GetCoverageRequest(null, null);

    assertThat(request.getId(), is(nullValue()));
    assertThat(request.getCoverageFormat(), is(nullValue()));
  }

  // GetCoverageResponse tests

  @Test
  void testGetCoverageResponseDefaultValues() {
    GetCoverageResponse response = new GetCoverageResponse();

    assertThat(response.getInputStream(), is(nullValue()));
    assertThat(response.getContentDisposition(), is(nullValue()));
  }

  @Test
  void testGetCoverageResponseSetInputStream() {
    GetCoverageResponse response = new GetCoverageResponse();
    InputStream inputStream = new ByteArrayInputStream("test data".getBytes());

    response.setInputStream(inputStream);

    assertThat(response.getInputStream(), is(sameInstance(inputStream)));
  }

  @Test
  void testGetCoverageResponseSetContentDisposition() {
    GetCoverageResponse response = new GetCoverageResponse();
    String contentDisposition = "attachment; filename=\"coverage.tif\"";

    response.setContentDisposition(contentDisposition);

    assertThat(response.getContentDisposition(), is(contentDisposition));
  }

  @Test
  void testGetCoverageResponseAllProperties() {
    GetCoverageResponse response = new GetCoverageResponse();
    InputStream inputStream = new ByteArrayInputStream("coverage data".getBytes());
    String contentDisposition = "attachment; filename=\"result.png\"";

    response.setInputStream(inputStream);
    response.setContentDisposition(contentDisposition);

    assertThat(response.getInputStream(), is(sameInstance(inputStream)));
    assertThat(response.getContentDisposition(), is(contentDisposition));
  }
}
