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
package org.codice.ddf.spatial.ogc.wfs.v2_0_0.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import javax.xml.namespace.QName;
import org.codice.ddf.spatial.ogc.wfs.catalog.common.WfsConstants;
import org.junit.jupiter.api.Test;

class RequestClassesTest {

  @Test
  void testGetCapabilitiesRequestDefaultConstructor() {
    GetCapabilitiesRequest request = new GetCapabilitiesRequest();

    assertThat(request.getRequest(), is(Wfs20Constants.GET_CAPABILITIES));
    assertThat(request.getVersion(), is(Wfs20Constants.VERSION_2_0_0));
    assertThat(request.getService(), is(WfsConstants.WFS));
  }

  @Test
  void testDescribeFeatureTypeRequestDefaultConstructor() {
    DescribeFeatureTypeRequest request = new DescribeFeatureTypeRequest();

    assertThat(request.getRequest(), is(nullValue()));
    assertThat(request.getVersion(), is(nullValue()));
  }

  @Test
  void testDescribeFeatureTypeRequestWithQName() {
    QName qname = new QName("http://test.namespace", "TestFeature");
    DescribeFeatureTypeRequest request = new DescribeFeatureTypeRequest(qname);

    assertThat(request.getRequest(), is(Wfs20Constants.DESCRIBE_FEATURE_TYPE));
    assertThat(request.getVersion(), is(Wfs20Constants.VERSION_2_0_0));
    assertThat(request.getService(), is(WfsConstants.WFS));
    assertThat(request.getTypeName(), is("TestFeature"));
  }

  @Test
  void testGetPropertyValueRequestDefaultConstructor() {
    GetPropertyValueRequest request = new GetPropertyValueRequest();

    assertThat(request.getRequest(), is(Wfs20Constants.GET_CAPABILITIES));
    assertThat(request.getVersion(), is(Wfs20Constants.VERSION_2_0_0));
    assertThat(request.getService(), is(WfsConstants.WFS));
    assertThat(request.getAdhocQuery(), is(""));
    assertThat(request.getValueReference(), is(""));
    assertThat(request.getResolvePath(), is(""));
  }

  @Test
  void testGetPropertyValueRequestSettersAndGetters() {
    GetPropertyValueRequest request = new GetPropertyValueRequest();

    request.setAdhocQuery("SELECT * FROM test");
    request.setValueReference("someProperty");
    request.setResolvePath("/path/to/resolve");

    assertThat(request.getAdhocQuery(), is("SELECT * FROM test"));
    assertThat(request.getValueReference(), is("someProperty"));
    assertThat(request.getResolvePath(), is("/path/to/resolve"));
  }
}
