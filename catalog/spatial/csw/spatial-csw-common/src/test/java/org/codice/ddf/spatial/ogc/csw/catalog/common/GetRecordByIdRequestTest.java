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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class GetRecordByIdRequestTest {

  @Test
  void testDefaultConstructor() {
    GetRecordByIdRequest request = new GetRecordByIdRequest();

    assertThat(request, is(notNullValue()));
    assertThat(request.getRequest(), is(CswConstants.GET_RECORD_BY_ID));
  }

  @Test
  void testConstructorWithService() {
    GetRecordByIdRequest request = new GetRecordByIdRequest("CSW");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getRequest(), is(CswConstants.GET_RECORD_BY_ID));
  }

  @Test
  void testGetAndSetId() {
    GetRecordByIdRequest request = new GetRecordByIdRequest();

    assertThat(request.getId(), is(nullValue()));

    request.setId("record-123");

    assertThat(request.getId(), is("record-123"));
  }

  @Test
  void testGetAndSetElementSetName() {
    GetRecordByIdRequest request = new GetRecordByIdRequest();

    assertThat(request.getElementSetName(), is(nullValue()));

    request.setElementSetName("full");

    assertThat(request.getElementSetName(), is("full"));
  }

  @Test
  void testGetAndSetOutputFormat() {
    GetRecordByIdRequest request = new GetRecordByIdRequest();

    assertThat(request.getOutputFormat(), is(nullValue()));

    request.setOutputFormat("application/xml");

    assertThat(request.getOutputFormat(), is("application/xml"));
  }

  @Test
  void testGetAndSetOutputSchema() {
    GetRecordByIdRequest request = new GetRecordByIdRequest();

    assertThat(request.getOutputSchema(), is(nullValue()));

    request.setOutputSchema("http://www.opengis.net/cat/csw/2.0.2");

    assertThat(request.getOutputSchema(), is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  void testFullyPopulatedRequest() {
    GetRecordByIdRequest request = new GetRecordByIdRequest("CSW");
    request.setId("metacard-uuid");
    request.setElementSetName("brief");
    request.setOutputFormat("text/xml");
    request.setOutputSchema("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getId(), is("metacard-uuid"));
    assertThat(request.getElementSetName(), is("brief"));
    assertThat(request.getOutputFormat(), is("text/xml"));
    assertThat(request.getOutputSchema(), is("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"));
  }

  @Test
  void testSetIdToNull() {
    GetRecordByIdRequest request = new GetRecordByIdRequest();
    request.setId("initial-id");

    request.setId(null);

    assertThat(request.getId(), is(nullValue()));
  }

  @Test
  void testElementSetNameValues() {
    GetRecordByIdRequest request = new GetRecordByIdRequest();

    request.setElementSetName("summary");
    assertThat(request.getElementSetName(), is("summary"));

    request.setElementSetName("brief");
    assertThat(request.getElementSetName(), is("brief"));

    request.setElementSetName("full");
    assertThat(request.getElementSetName(), is("full"));
  }
}
