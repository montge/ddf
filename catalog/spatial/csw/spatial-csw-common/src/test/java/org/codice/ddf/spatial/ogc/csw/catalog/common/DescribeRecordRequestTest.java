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

class DescribeRecordRequestTest {

  @Test
  void testDefaultConstructorSetsRequest() {
    DescribeRecordRequest request = new DescribeRecordRequest();

    assertThat(request.getRequest(), is(CswConstants.DESCRIBE_RECORD));
  }

  @Test
  void testDefaultConstructorLeavesOtherFieldsNull() {
    DescribeRecordRequest request = new DescribeRecordRequest();

    assertThat(request.getVersion(), is(nullValue()));
    assertThat(request.getNamespace(), is(nullValue()));
    assertThat(request.getTypeName(), is(nullValue()));
    assertThat(request.getOutputFormat(), is(nullValue()));
    assertThat(request.getSchemaLanguage(), is(nullValue()));
  }

  @Test
  void testServiceVersionConstructor() {
    DescribeRecordRequest request = new DescribeRecordRequest("CSW", "2.0.2");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getVersion(), is("2.0.2"));
    assertThat(request.getRequest(), is(CswConstants.DESCRIBE_RECORD));
  }

  @Test
  void testSetAndGetVersion() {
    DescribeRecordRequest request = new DescribeRecordRequest();
    request.setVersion("2.0.2");

    assertThat(request.getVersion(), is("2.0.2"));
  }

  @Test
  void testSetAndGetNamespace() {
    DescribeRecordRequest request = new DescribeRecordRequest();
    request.setNamespace("http://www.opengis.net/cat/csw/2.0.2");

    assertThat(request.getNamespace(), is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  void testSetAndGetTypeName() {
    DescribeRecordRequest request = new DescribeRecordRequest();
    request.setTypeName("csw:Record");

    assertThat(request.getTypeName(), is("csw:Record"));
  }

  @Test
  void testSetAndGetOutputFormat() {
    DescribeRecordRequest request = new DescribeRecordRequest();
    request.setOutputFormat("application/xml");

    assertThat(request.getOutputFormat(), is("application/xml"));
  }

  @Test
  void testSetAndGetSchemaLanguage() {
    DescribeRecordRequest request = new DescribeRecordRequest();
    request.setSchemaLanguage("http://www.w3.org/2001/XMLSchema");

    assertThat(request.getSchemaLanguage(), is("http://www.w3.org/2001/XMLSchema"));
  }

  @Test
  void testExtendsCswRequest() {
    DescribeRecordRequest request = new DescribeRecordRequest();

    assertThat(request, instanceOf(CswRequest.class));
  }

  @Test
  void testAllPropertiesCanBeSet() {
    DescribeRecordRequest request = new DescribeRecordRequest("CSW", "2.0.2");
    request.setNamespace("http://namespace.example");
    request.setTypeName("csw:Record");
    request.setOutputFormat("application/xml");
    request.setSchemaLanguage("http://www.w3.org/2001/XMLSchema");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getVersion(), is("2.0.2"));
    assertThat(request.getRequest(), is(CswConstants.DESCRIBE_RECORD));
    assertThat(request.getNamespace(), is("http://namespace.example"));
    assertThat(request.getTypeName(), is("csw:Record"));
    assertThat(request.getOutputFormat(), is("application/xml"));
    assertThat(request.getSchemaLanguage(), is("http://www.w3.org/2001/XMLSchema"));
  }
}
