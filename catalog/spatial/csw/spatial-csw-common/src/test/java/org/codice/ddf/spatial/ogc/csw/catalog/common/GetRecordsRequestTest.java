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

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class GetRecordsRequestTest {

  @Test
  void testDefaultConstructorSetsRequest() {
    GetRecordsRequest request = new GetRecordsRequest();

    assertThat(request.getRequest(), is(CswConstants.GET_RECORDS));
  }

  @Test
  void testDefaultConstructorLeavesFieldsNull() {
    GetRecordsRequest request = new GetRecordsRequest();

    assertThat(request.getVersion(), is(nullValue()));
    assertThat(request.getRequestId(), is(nullValue()));
    assertThat(request.getNamespace(), is(nullValue()));
    assertThat(request.getResultType(), is(nullValue()));
    assertThat(request.getOutputFormat(), is(nullValue()));
    assertThat(request.getOutputSchema(), is(nullValue()));
    assertThat(request.getStartPosition(), is(nullValue()));
    assertThat(request.getMaxRecords(), is(nullValue()));
    assertThat(request.getTypeNames(), is(nullValue()));
    assertThat(request.getElementName(), is(nullValue()));
    assertThat(request.getElementSetName(), is(nullValue()));
    assertThat(request.getConstraintLanguage(), is(nullValue()));
    assertThat(request.getConstraint(), is(nullValue()));
    assertThat(request.getSortBy(), is(nullValue()));
    assertThat(request.getDistributedSearch(), is(nullValue()));
    assertThat(request.getHopCount(), is(nullValue()));
    assertThat(request.getResponseHandler(), is(nullValue()));
  }

  @Test
  void testServiceAndVersionConstructor() {
    GetRecordsRequest request = new GetRecordsRequest("CSW", "2.0.2");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getVersion(), is("2.0.2"));
    assertThat(request.getRequest(), is(CswConstants.GET_RECORDS));
  }

  @Test
  void testSetAndGetVersion() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setVersion("2.0.2");

    assertThat(request.getVersion(), is("2.0.2"));
  }

  @Test
  void testSetAndGetRequestId() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setRequestId("request-123");

    assertThat(request.getRequestId(), is("request-123"));
  }

  @Test
  void testSetAndGetNamespace() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setNamespace("xmlns(csw=http://www.opengis.net/cat/csw/2.0.2)");

    assertThat(request.getNamespace(), is("xmlns(csw=http://www.opengis.net/cat/csw/2.0.2)"));
  }

  @Test
  void testSetAndGetResultType() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setResultType("results");

    assertThat(request.getResultType(), is("results"));
  }

  @Test
  void testSetAndGetOutputFormat() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setOutputFormat("application/xml");

    assertThat(request.getOutputFormat(), is("application/xml"));
  }

  @Test
  void testSetAndGetOutputSchema() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setOutputSchema("http://www.opengis.net/cat/csw/2.0.2");

    assertThat(request.getOutputSchema(), is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  void testSetAndGetStartPosition() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setStartPosition(BigInteger.valueOf(1));

    assertThat(request.getStartPosition(), is(BigInteger.valueOf(1)));
  }

  @Test
  void testSetAndGetMaxRecords() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setMaxRecords(BigInteger.valueOf(10));

    assertThat(request.getMaxRecords(), is(BigInteger.valueOf(10)));
  }

  @Test
  void testSetAndGetTypeNames() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setTypeNames("csw:Record");

    assertThat(request.getTypeNames(), is("csw:Record"));
  }

  @Test
  void testSetAndGetElementName() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setElementName("dc:title,dc:subject");

    assertThat(request.getElementName(), is("dc:title,dc:subject"));
  }

  @Test
  void testSetAndGetElementSetName() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setElementSetName("brief");

    assertThat(request.getElementSetName(), is("brief"));
  }

  @Test
  void testSetAndGetConstraintLanguage() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setConstraintLanguage("CQL_TEXT");

    assertThat(request.getConstraintLanguage(), is("CQL_TEXT"));
  }

  @Test
  void testSetAndGetConstraint() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setConstraint("title LIKE 'test%'");

    assertThat(request.getConstraint(), is("title LIKE 'test%'"));
  }

  @Test
  void testSetAndGetSortBy() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setSortBy("dc:title:A");

    assertThat(request.getSortBy(), is("dc:title:A"));
  }

  @Test
  void testSetAndGetDistributedSearch() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setDistributedSearch(Boolean.TRUE);

    assertThat(request.getDistributedSearch(), is(Boolean.TRUE));
  }

  @Test
  void testSetAndGetHopCount() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setHopCount(BigInteger.valueOf(2));

    assertThat(request.getHopCount(), is(BigInteger.valueOf(2)));
  }

  @Test
  void testSetAndGetResponseHandler() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setResponseHandler("http://example.com/handler");

    assertThat(request.getResponseHandler(), is("http://example.com/handler"));
  }

  @Test
  void testExtendsCswRequest() {
    GetRecordsRequest request = new GetRecordsRequest();

    assertThat(request, instanceOf(CswRequest.class));
  }

  @Test
  void testAllPropertiesCanBeSet() {
    GetRecordsRequest request = new GetRecordsRequest("CSW", "2.0.2");
    request.setRequestId("req-1");
    request.setNamespace("xmlns(csw=http://example.com)");
    request.setResultType("results");
    request.setOutputFormat("application/xml");
    request.setOutputSchema("http://www.opengis.net/cat/csw/2.0.2");
    request.setStartPosition(BigInteger.valueOf(1));
    request.setMaxRecords(BigInteger.valueOf(100));
    request.setTypeNames("csw:Record");
    request.setElementSetName("full");
    request.setConstraintLanguage("CQL_TEXT");
    request.setConstraint("title='test'");
    request.setSortBy("dc:title:A");
    request.setDistributedSearch(Boolean.TRUE);
    request.setHopCount(BigInteger.valueOf(3));
    request.setResponseHandler("http://handler.example.com");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getVersion(), is("2.0.2"));
    assertThat(request.getRequestId(), is("req-1"));
    assertThat(request.getResultType(), is("results"));
    assertThat(request.getOutputFormat(), is("application/xml"));
    assertThat(request.getStartPosition(), is(BigInteger.valueOf(1)));
    assertThat(request.getMaxRecords(), is(BigInteger.valueOf(100)));
    assertThat(request.getTypeNames(), is("csw:Record"));
    assertThat(request.getElementSetName(), is("full"));
    assertThat(request.getDistributedSearch(), is(Boolean.TRUE));
    assertThat(request.getHopCount(), is(BigInteger.valueOf(3)));
  }

  @Test
  void testDistributedSearchFalse() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setDistributedSearch(Boolean.FALSE);

    assertThat(request.getDistributedSearch(), is(Boolean.FALSE));
  }

  @Test
  void testLargeMaxRecords() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setMaxRecords(BigInteger.valueOf(1000000));

    assertThat(request.getMaxRecords(), is(BigInteger.valueOf(1000000)));
  }

  @Test
  void testZeroStartPosition() {
    GetRecordsRequest request = new GetRecordsRequest();
    request.setStartPosition(BigInteger.ZERO);

    assertThat(request.getStartPosition(), is(BigInteger.ZERO));
  }
}
