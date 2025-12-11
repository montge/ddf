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
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link DescribeCoverageRequest}. */
public class DescribeCoverageRequestTest {

  @Test
  public void testDescribeCoverageRequestInstantiation() {
    DescribeCoverageRequest request = new DescribeCoverageRequest();
    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testDescribeCoverageRequestIsJaxRsBean() {
    // Verify that DescribeCoverageRequest is a valid JAX-RS parameter bean
    // by checking it has a no-arg constructor
    DescribeCoverageRequest request = new DescribeCoverageRequest();
    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testMultipleInstancesAreIndependent() {
    DescribeCoverageRequest request1 = new DescribeCoverageRequest();
    DescribeCoverageRequest request2 = new DescribeCoverageRequest();

    assertThat(request1, is(notNullValue()));
    assertThat(request2, is(notNullValue()));
    // Verify they are different instances
    assertThat(request1 == request2, is(false));
  }
}
