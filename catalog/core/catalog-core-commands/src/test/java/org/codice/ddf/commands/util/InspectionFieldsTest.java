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
package org.codice.ddf.commands.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class InspectionFieldsTest {

  @Test
  void testSiteEnumValue() {
    assertThat(InspectionFields.SITE, is(notNullValue()));
    assertThat(InspectionFields.SITE.name(), is("SITE"));
  }

  @Test
  void testGeosEnumValue() {
    assertThat(InspectionFields.GEOS, is(notNullValue()));
    assertThat(InspectionFields.GEOS.name(), is("GEOS"));
  }

  @Test
  void testEnumValueCount() {
    assertThat(InspectionFields.values().length, is(2));
  }

  @Test
  void testEnumValuesContainsBoth() {
    assertThat(
        InspectionFields.values(),
        arrayContainingInAnyOrder(InspectionFields.SITE, InspectionFields.GEOS));
  }

  @Test
  void testValueOfSite() {
    assertThat(InspectionFields.valueOf("SITE"), is(InspectionFields.SITE));
  }

  @Test
  void testValueOfGeos() {
    assertThat(InspectionFields.valueOf("GEOS"), is(InspectionFields.GEOS));
  }
}
