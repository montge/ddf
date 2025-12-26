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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class CswGetCapabilitiesRequestTest {

  @Test
  void testDefaultConstructorSetsRequest() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();

    assertThat(request.getRequest(), is(CswConstants.GET_CAPABILITIES));
  }

  @Test
  void testDefaultConstructorLeavesOtherFieldsNull() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();

    assertThat(request.getService(), is(nullValue()));
    assertThat(request.getAcceptVersions(), is(nullValue()));
    assertThat(request.getSections(), is(nullValue()));
    assertThat(request.getUpdateSequence(), is(nullValue()));
    assertThat(request.getAcceptFormats(), is(nullValue()));
  }

  @Test
  void testServiceConstructor() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest("CSW");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getRequest(), is(CswConstants.GET_CAPABILITIES));
  }

  @Test
  void testSetAndGetAcceptVersions() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();
    request.setAcceptVersions("2.0.2,2.0.1");

    assertThat(request.getAcceptVersions(), is("2.0.2,2.0.1"));
  }

  @Test
  void testSetAndGetSections() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();
    request.setSections("ServiceIdentification,ServiceProvider");

    assertThat(request.getSections(), is("ServiceIdentification,ServiceProvider"));
  }

  @Test
  void testSetAndGetUpdateSequence() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();
    request.setUpdateSequence("12345");

    assertThat(request.getUpdateSequence(), is("12345"));
  }

  @Test
  void testSetAndGetAcceptFormats() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();
    request.setAcceptFormats("application/xml,text/xml");

    assertThat(request.getAcceptFormats(), is("application/xml,text/xml"));
  }

  @Test
  void testExtendsCswRequest() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();

    assertThat(request, instanceOf(CswRequest.class));
  }

  @Test
  void testAllPropertiesCanBeSet() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest("CSW");
    request.setAcceptVersions("2.0.2");
    request.setSections("All");
    request.setUpdateSequence("12345");
    request.setAcceptFormats("application/xml");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getRequest(), is(CswConstants.GET_CAPABILITIES));
    assertThat(request.getAcceptVersions(), is("2.0.2"));
    assertThat(request.getSections(), is("All"));
    assertThat(request.getUpdateSequence(), is("12345"));
    assertThat(request.getAcceptFormats(), is("application/xml"));
  }
}
