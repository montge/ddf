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
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/** Unit tests for {@link WcsConstants}. */
public class WcsConstantsTest {

  @Test
  public void testWcsConstant() {
    assertThat(WcsConstants.WCS, is("WCS"));
  }

  @Test
  public void testVersionConstant() {
    assertThat(WcsConstants.VERSION_1_0_0, is("1.0.0"));
  }

  @Test
  public void testGetCapabilitiesConstant() {
    assertThat(WcsConstants.GET_CAPABILITIES, is("GetCapabilities"));
  }

  @Test
  public void testDescribeCoverageConstant() {
    assertThat(WcsConstants.DESCRIBE_COVERAGE, is("DescribeCoverage"));
  }

  @Test
  public void testGetCoverageConstant() {
    assertThat(WcsConstants.GET_COVERAGE, is("GetCoverage"));
  }

  @Test
  public void testSrsNameConstant() {
    assertThat(WcsConstants.SRS_NAME, is("EPSG:4326"));
  }

  @Test
  public void testAllConstantsAreNotNull() {
    assertThat(WcsConstants.WCS != null, is(true));
    assertThat(WcsConstants.VERSION_1_0_0 != null, is(true));
    assertThat(WcsConstants.GET_CAPABILITIES != null, is(true));
    assertThat(WcsConstants.DESCRIBE_COVERAGE != null, is(true));
    assertThat(WcsConstants.GET_COVERAGE != null, is(true));
    assertThat(WcsConstants.SRS_NAME != null, is(true));
  }

  @Test
  public void testConstantsAreNotEmpty() {
    assertThat(WcsConstants.WCS.isEmpty(), is(false));
    assertThat(WcsConstants.VERSION_1_0_0.isEmpty(), is(false));
    assertThat(WcsConstants.GET_CAPABILITIES.isEmpty(), is(false));
    assertThat(WcsConstants.DESCRIBE_COVERAGE.isEmpty(), is(false));
    assertThat(WcsConstants.GET_COVERAGE.isEmpty(), is(false));
    assertThat(WcsConstants.SRS_NAME.isEmpty(), is(false));
  }
}
