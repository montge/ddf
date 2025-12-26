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
package org.codice.ddf.spatial.ogc.wfs.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

class WfsRequestTest {

  @Test
  void testDefaultConstructor() {
    WfsRequest request = new WfsRequest();

    assertThat(request.getRequest(), is(nullValue()));
    assertThat(request.getService(), is(nullValue()));
    assertThat(request.getTypeName(), is(nullValue()));
    assertThat(request.getVersion(), is(nullValue()));
  }

  @Test
  void testQNameConstructorWithPrefix() {
    QName qname = new QName("http://example.com", "FeatureType", "ns");
    WfsRequest request = new WfsRequest(qname);

    assertThat(request.getTypeName(), is("ns:FeatureType"));
  }

  @Test
  void testQNameConstructorWithoutPrefix() {
    QName qname = new QName("http://example.com", "FeatureType");
    WfsRequest request = new WfsRequest(qname);

    assertThat(request.getTypeName(), is("FeatureType"));
  }

  @Test
  void testQNameConstructorWithEmptyPrefix() {
    QName qname = new QName("http://example.com", "FeatureType", "");
    WfsRequest request = new WfsRequest(qname);

    assertThat(request.getTypeName(), is("FeatureType"));
  }

  @Test
  void testQNameConstructorWithNullQName() {
    WfsRequest request = new WfsRequest(null);

    assertThat(request.getTypeName(), is(nullValue()));
  }

  @Test
  void testSetAndGetRequest() {
    WfsRequest request = new WfsRequest();
    request.setRequest("GetFeature");

    assertThat(request.getRequest(), is("GetFeature"));
  }

  @Test
  void testSetAndGetService() {
    WfsRequest request = new WfsRequest();
    request.setService("WFS");

    assertThat(request.getService(), is("WFS"));
  }

  @Test
  void testSetAndGetTypeName() {
    WfsRequest request = new WfsRequest();
    request.setTypeName("ns:MyType");

    assertThat(request.getTypeName(), is("ns:MyType"));
  }

  @Test
  void testSetAndGetVersion() {
    WfsRequest request = new WfsRequest();
    request.setVersion("1.1.0");

    assertThat(request.getVersion(), is("1.1.0"));
  }

  @Test
  void testAllPropertiesCanBeSet() {
    WfsRequest request = new WfsRequest();
    request.setRequest("DescribeFeatureType");
    request.setService("WFS");
    request.setTypeName("prefix:TypeName");
    request.setVersion("2.0.0");

    assertThat(request.getRequest(), is("DescribeFeatureType"));
    assertThat(request.getService(), is("WFS"));
    assertThat(request.getTypeName(), is("prefix:TypeName"));
    assertThat(request.getVersion(), is("2.0.0"));
  }

  @Test
  void testQNameWithLocalPartOnly() {
    QName qname = new QName("LocalOnly");
    WfsRequest request = new WfsRequest(qname);

    assertThat(request.getTypeName(), is("LocalOnly"));
  }
}
