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
package org.codice.ddf.spatial.ogc.wfs.v110.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

class RequestClassesTest {

  @Test
  void testGetCapabilitiesRequestDefaults() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();

    assertThat(request, is(notNullValue()));
    assertThat(request.getRequest(), is("GetCapabilities"));
    assertThat(request.getVersion(), is(Wfs11Constants.VERSION_1_1_0));
    assertThat(request.getService(), is("WFS"));
  }

  @Test
  void testGetCapabilitiesRequestTypeNameIsNull() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();

    assertThat(request.getTypeName(), is(nullValue()));
  }

  @Test
  void testDescribeFeatureTypeRequestWithQName() {
    QName qname = new QName("http://example.com", "TestFeature", "test");
    DescribeFeatureTypeRequest request = new DescribeFeatureTypeRequest(qname);

    assertThat(request, is(notNullValue()));
    assertThat(request.getRequest(), is("DescribeFeatureType"));
    assertThat(request.getVersion(), is(Wfs11Constants.VERSION_1_1_0));
    assertThat(request.getService(), is("WFS"));
    assertThat(request.getTypeName(), is("test:TestFeature"));
  }

  @Test
  void testDescribeFeatureTypeRequestWithQNameNoPrefix() {
    QName qname = new QName("http://example.com", "TestFeature");
    DescribeFeatureTypeRequest request = new DescribeFeatureTypeRequest(qname);

    assertThat(request, is(notNullValue()));
    assertThat(request.getTypeName(), is("TestFeature"));
  }

  @Test
  void testDescribeFeatureTypeRequestWithNullQName() {
    DescribeFeatureTypeRequest request = new DescribeFeatureTypeRequest(null);

    assertThat(request, is(notNullValue()));
    assertThat(request.getRequest(), is("DescribeFeatureType"));
    assertThat(request.getVersion(), is(Wfs11Constants.VERSION_1_1_0));
    assertThat(request.getService(), is("WFS"));
    assertThat(request.getTypeName(), is(nullValue()));
  }

  @Test
  void testDescribeFeatureTypeRequestWithEmptyPrefix() {
    QName qname = new QName("http://example.com", "TestFeature", "");
    DescribeFeatureTypeRequest request = new DescribeFeatureTypeRequest(qname);

    assertThat(request.getTypeName(), is("TestFeature"));
  }

  @Test
  void testGetCapabilitiesRequestSetters() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();
    request.setTypeName("CustomType");

    assertThat(request.getTypeName(), is("CustomType"));
  }

  @Test
  void testDescribeFeatureTypeRequestSetters() {
    QName qname = new QName("http://example.com", "Original");
    DescribeFeatureTypeRequest request = new DescribeFeatureTypeRequest(qname);

    request.setRequest("CustomRequest");
    request.setVersion("2.0.0");
    request.setService("CustomService");
    request.setTypeName("CustomType");

    assertThat(request.getRequest(), is("CustomRequest"));
    assertThat(request.getVersion(), is("2.0.0"));
    assertThat(request.getService(), is("CustomService"));
    assertThat(request.getTypeName(), is("CustomType"));
  }
}
