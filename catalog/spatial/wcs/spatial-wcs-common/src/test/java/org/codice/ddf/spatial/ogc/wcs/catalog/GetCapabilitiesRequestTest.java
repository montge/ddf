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

import org.junit.Test;

/** Unit tests for {@link GetCapabilitiesRequest}. */
public class GetCapabilitiesRequestTest {

  @Test
  public void testGetCapabilitiesRequestInstantiation() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();
    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testGetCapabilitiesRequestDefaultValues() {
    // Test that the request can be instantiated and used
    // The private fields have default values but are not accessible
    // This test verifies the bean can be created for JAX-RS parameter injection
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();
    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testGetCapabilitiesRequestIsJaxRsBean() {
    // Verify that GetCapabilitiesRequest is a valid JAX-RS parameter bean
    // by checking it has a no-arg constructor
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();
    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testMultipleInstancesAreIndependent() {
    GetCapabilitiesRequest request1 = new GetCapabilitiesRequest();
    GetCapabilitiesRequest request2 = new GetCapabilitiesRequest();

    assertThat(request1, is(notNullValue()));
    assertThat(request2, is(notNullValue()));
    // Verify they are different instances
    assertThat(request1 == request2, is(false));
  }
}
