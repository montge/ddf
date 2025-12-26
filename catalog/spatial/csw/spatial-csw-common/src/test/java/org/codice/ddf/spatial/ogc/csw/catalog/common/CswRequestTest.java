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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

class CswRequestTest {

  @Test
  void testDefaultConstructor() {
    CswRequest request = new CswRequest();

    assertThat(request.getRequest(), is(nullValue()));
    assertThat(request.getService(), is(nullValue()));
  }

  @Test
  void testRequestConstructor() {
    CswRequest request = new CswRequest("GetRecords");

    assertThat(request.getRequest(), is("GetRecords"));
    assertThat(request.getService(), is(nullValue()));
  }

  @Test
  void testServiceAndRequestConstructor() {
    CswRequest request = new CswRequest("CSW", "GetRecords");

    assertThat(request.getService(), is("CSW"));
    assertThat(request.getRequest(), is("GetRecords"));
  }

  @Test
  void testSetAndGetRequest() {
    CswRequest request = new CswRequest();
    request.setRequest("DescribeRecord");

    assertThat(request.getRequest(), is("DescribeRecord"));
  }

  @Test
  void testSetAndGetService() {
    CswRequest request = new CswRequest();
    request.setService("CSW");

    assertThat(request.getService(), is("CSW"));
  }

  @Test
  void testParseNamespacesWithValidInput() throws CswException {
    CswRequest request = new CswRequest();
    String namespaces = "xmlns(csw=http://www.opengis.net/cat/csw/2.0.2)";

    Map<String, String> result = request.parseNamespaces(namespaces);

    assertThat(result.get("csw"), is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  void testParseNamespacesWithMultipleNamespaces() throws CswException {
    CswRequest request = new CswRequest();
    String namespaces =
        "xmlns(csw=http://www.opengis.net/cat/csw),xmlns(gml=http://www.opengis.net/gml)";

    Map<String, String> result = request.parseNamespaces(namespaces);

    assertThat(result.size(), is(2));
    assertThat(result.get("csw"), is("http://www.opengis.net/cat/csw"));
    assertThat(result.get("gml"), is("http://www.opengis.net/gml"));
  }

  @Test
  void testParseNamespacesWithDefaultNamespace() throws CswException {
    CswRequest request = new CswRequest();
    String namespaces = "xmlns(http://default.namespace)";

    Map<String, String> result = request.parseNamespaces(namespaces);

    assertThat(result.get(""), is("http://default.namespace"));
  }

  @Test
  void testParseNamespacesWithNull() throws CswException {
    CswRequest request = new CswRequest();

    Map<String, String> result = request.parseNamespaces(null);

    assertThat(result.size(), is(0));
  }

  @Test
  void testParseNamespacesWithInvalidFormat() {
    CswRequest request = new CswRequest();
    String namespaces = "invalid-format";

    assertThrows(CswException.class, () -> request.parseNamespaces(namespaces));
  }

  // Inner test subclass to access protected method
  private static class TestCswRequest extends CswRequest {
    public List<QName> testTypeStringToQNames(String typeNames, Map<String, String> namespaces)
        throws CswException {
      return typeStringToQNames(typeNames, namespaces);
    }
  }

  @Test
  void testTypeStringToQNamesWithPrefix() throws CswException {
    TestCswRequest request = new TestCswRequest();
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("csw", "http://www.opengis.net/cat/csw");

    List<QName> result = request.testTypeStringToQNames("csw:Record", namespaces);

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getLocalPart(), is("Record"));
    assertThat(result.get(0).getPrefix(), is("csw"));
    assertThat(result.get(0).getNamespaceURI(), is("http://www.opengis.net/cat/csw"));
  }

  @Test
  void testTypeStringToQNamesWithoutPrefix() throws CswException {
    TestCswRequest request = new TestCswRequest();

    List<QName> result = request.testTypeStringToQNames("SimpleType", null);

    assertThat(result, hasSize(1));
    assertThat(result.get(0).getLocalPart(), is("SimpleType"));
  }

  @Test
  void testTypeStringToQNamesWithMultipleTypes() throws CswException {
    TestCswRequest request = new TestCswRequest();
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("csw", "http://www.opengis.net/cat/csw");

    List<QName> result = request.testTypeStringToQNames("csw:Record,SimpleType", namespaces);

    assertThat(result, hasSize(2));
  }

  @Test
  void testTypeStringToQNamesWithNull() throws CswException {
    TestCswRequest request = new TestCswRequest();

    List<QName> result = request.testTypeStringToQNames(null, null);

    assertThat(result, hasSize(0));
  }

  @Test
  void testTypeStringToQNamesWithUnknownPrefix() {
    TestCswRequest request = new TestCswRequest();
    Map<String, String> namespaces = new HashMap<>();

    assertThrows(
        CswException.class, () -> request.testTypeStringToQNames("unknown:Type", namespaces));
  }
}
