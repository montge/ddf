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
package ddf.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.jupiter.api.Test;

class NamespaceMapImplTest {

  @Test
  void testGetNamespaceURIReturnsCorrectValue() {
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("gml", "http://www.opengis.net/gml");
    namespaces.put("dc", "http://purl.org/dc/elements/1.1/");

    NamespaceMapImpl namespaceMap = new NamespaceMapImpl(namespaces);

    assertThat(namespaceMap.getNamespaceURI("gml"), is("http://www.opengis.net/gml"));
    assertThat(namespaceMap.getNamespaceURI("dc"), is("http://purl.org/dc/elements/1.1/"));
  }

  @Test
  void testGetNamespaceURIReturnsNullForUnknownPrefix() {
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("gml", "http://www.opengis.net/gml");

    NamespaceMapImpl namespaceMap = new NamespaceMapImpl(namespaces);

    assertThat(namespaceMap.getNamespaceURI("unknown"), is(nullValue()));
  }

  @Test
  void testGetPrefixReturnsCorrectValue() {
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("gml", "http://www.opengis.net/gml");
    namespaces.put("dc", "http://purl.org/dc/elements/1.1/");

    NamespaceMapImpl namespaceMap = new NamespaceMapImpl(namespaces);

    assertThat(namespaceMap.getPrefix("http://www.opengis.net/gml"), is("gml"));
    assertThat(namespaceMap.getPrefix("http://purl.org/dc/elements/1.1/"), is("dc"));
  }

  @Test
  void testGetPrefixReturnsNullForUnknownNamespace() {
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("gml", "http://www.opengis.net/gml");

    NamespaceMapImpl namespaceMap = new NamespaceMapImpl(namespaces);

    assertThat(namespaceMap.getPrefix("http://unknown.namespace.com"), is(nullValue()));
  }

  @Test
  void testGetPrefixesReturnsNull() {
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("gml", "http://www.opengis.net/gml");

    NamespaceMapImpl namespaceMap = new NamespaceMapImpl(namespaces);

    Iterator<?> prefixes = namespaceMap.getPrefixes("http://www.opengis.net/gml");

    assertThat(prefixes, is(nullValue()));
  }

  @Test
  void testEmptyNamespacesMap() {
    Map<String, String> namespaces = new HashMap<>();

    NamespaceMapImpl namespaceMap = new NamespaceMapImpl(namespaces);

    assertThat(namespaceMap.getNamespaceURI("any"), is(nullValue()));
    assertThat(namespaceMap.getPrefix("http://any.namespace.com"), is(nullValue()));
  }

  @Test
  void testMultipleNamespaces() {
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("gml", "http://www.opengis.net/gml");
    namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
    namespaces.put("csw", "http://www.opengis.net/cat/csw/2.0.2");

    NamespaceMapImpl namespaceMap = new NamespaceMapImpl(namespaces);

    assertThat(namespaceMap.getNamespaceURI("gml"), is("http://www.opengis.net/gml"));
    assertThat(namespaceMap.getNamespaceURI("dc"), is("http://purl.org/dc/elements/1.1/"));
    assertThat(namespaceMap.getNamespaceURI("csw"), is("http://www.opengis.net/cat/csw/2.0.2"));
    assertThat(namespaceMap.getPrefix("http://www.opengis.net/gml"), is("gml"));
    assertThat(namespaceMap.getPrefix("http://purl.org/dc/elements/1.1/"), is("dc"));
    assertThat(namespaceMap.getPrefix("http://www.opengis.net/cat/csw/2.0.2"), is("csw"));
  }
}
