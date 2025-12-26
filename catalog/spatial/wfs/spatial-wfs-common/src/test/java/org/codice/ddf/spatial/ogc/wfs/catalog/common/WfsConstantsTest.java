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
package org.codice.ddf.spatial.ogc.wfs.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class WfsConstantsTest {

  @Test
  void testServiceName() {
    assertThat(WfsConstants.WFS, is("WFS"));
  }

  @Test
  void testXmlEncodedFilterConstants() {
    assertThat(WfsConstants.ESCAPE, is("!"));
    assertThat(WfsConstants.WILD_CARD, is("*"));
    assertThat(WfsConstants.METERS, is("METERS"));
  }

  @Test
  void testNamespacePrefixes() {
    assertThat(WfsConstants.XSI_PREFIX, is("xsi"));
    assertThat(WfsConstants.WFS_NAMESPACE_PREFIX, is("wfs"));
    assertThat(WfsConstants.GML_PREFIX, is("gml"));
    assertThat(WfsConstants.ATTRIBUTE_SCHEMA_LOCATION, is("xsi:schemaLocation"));
    assertThat(WfsConstants.NAMESPACE_URN_ROOT, is("urn:ddf.catalog.gml."));
  }

  @Test
  void testDegreesToRadiansConversion() {
    assertThat(WfsConstants.DEGREES_TO_RADIANS, closeTo(Math.PI / 180.0, 0.00001));
  }

  @Test
  void testRadiansToDegreesConversion() {
    assertThat(WfsConstants.RADIANS_TO_DEGREES, closeTo(180.0 / Math.PI, 0.00001));
  }

  @Test
  void testConversionReciprocal() {
    assertThat(
        WfsConstants.DEGREES_TO_RADIANS * WfsConstants.RADIANS_TO_DEGREES, closeTo(1.0, 0.00001));
  }

  @Test
  void testEarthMeanRadiusMeters() {
    assertThat(WfsConstants.EARTH_MEAN_RADIUS_METERS, closeTo(6371008.7714, 0.0001));
  }

  @Test
  void testByteSizeLabels() {
    assertThat(WfsConstants.B, is("B"));
    assertThat(WfsConstants.KB, is("KB"));
    assertThat(WfsConstants.MB, is("MB"));
    assertThat(WfsConstants.GB, is("GB"));
    assertThat(WfsConstants.TB, is("TB"));
    assertThat(WfsConstants.PB, is("PB"));
  }

  @Test
  void testByteSizeValues() {
    assertThat(WfsConstants.BYTES_PER_KB, is("1024"));
    assertThat(WfsConstants.BYTES_PER_MB, is("1048576"));
    assertThat(WfsConstants.BYTES_PER_GB, is("1073741824"));
    assertThat(WfsConstants.BYTES_PER_TB, is("1099511627776"));
    assertThat(WfsConstants.BYTES_PER_PB, is("1125899906842624"));
  }

  @Test
  void testByteSizeValuesArePowersOfTwo() {
    assertThat(Long.parseLong(WfsConstants.BYTES_PER_KB), is(1024L));
    assertThat(Long.parseLong(WfsConstants.BYTES_PER_MB), is(1024L * 1024));
    assertThat(Long.parseLong(WfsConstants.BYTES_PER_GB), is(1024L * 1024 * 1024));
    assertThat(Long.parseLong(WfsConstants.BYTES_PER_TB), is(1024L * 1024 * 1024 * 1024));
    assertThat(Long.parseLong(WfsConstants.BYTES_PER_PB), is(1024L * 1024 * 1024 * 1024 * 1024));
  }
}
