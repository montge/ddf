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
package org.codice.ddf.spatial.ogc.csw.catalog.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/** Unit tests for {@link CswAxisOrder}. */
public class CswAxisOrderTest {

  @Test
  public void testLatLonAxisOrder() {
    CswAxisOrder axisOrder = CswAxisOrder.LAT_LON;
    assertThat(axisOrder, is(notNullValue()));
    assertThat(axisOrder.name(), is("LAT_LON"));
  }

  @Test
  public void testLonLatAxisOrder() {
    CswAxisOrder axisOrder = CswAxisOrder.LON_LAT;
    assertThat(axisOrder, is(notNullValue()));
    assertThat(axisOrder.name(), is("LON_LAT"));
  }

  @Test
  public void testEnumValues() {
    CswAxisOrder[] values = CswAxisOrder.values();
    assertThat(values, is(notNullValue()));
    assertThat(values.length, is(2));
    assertThat(values[0], is(CswAxisOrder.LAT_LON));
    assertThat(values[1], is(CswAxisOrder.LON_LAT));
  }

  @Test
  public void testValueOf() {
    CswAxisOrder latLon = CswAxisOrder.valueOf("LAT_LON");
    assertThat(latLon, is(CswAxisOrder.LAT_LON));

    CswAxisOrder lonLat = CswAxisOrder.valueOf("LON_LAT");
    assertThat(lonLat, is(CswAxisOrder.LON_LAT));
  }

  @Test
  public void testEnumEquality() {
    assertThat(CswAxisOrder.LAT_LON.equals(CswAxisOrder.LAT_LON), is(true));
    assertThat(CswAxisOrder.LON_LAT.equals(CswAxisOrder.LON_LAT), is(true));
    assertThat(CswAxisOrder.LAT_LON.equals(CswAxisOrder.LON_LAT), is(false));
  }

  @Test
  public void testEnumComparison() {
    assertThat(CswAxisOrder.LAT_LON == CswAxisOrder.LAT_LON, is(true));
    assertThat(CswAxisOrder.LON_LAT == CswAxisOrder.LON_LAT, is(true));
    assertThat(CswAxisOrder.LAT_LON == CswAxisOrder.LON_LAT, is(false));
  }
}
