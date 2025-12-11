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
package org.codice.ddf.spatial.ogc.wfs.catalog.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

/** Tests for {@link WfsUriResolver} class. */
public class WfsUriResolverTest {

  private static final String GML_NAMESPACE = "http://www.opengis.net/gml";
  private static final String WFS_NAMESPACE = "http://www.opengis.net/wfs";
  private static final String OTHER_NAMESPACE = "http://example.com/other";

  private WfsUriResolver resolver;

  @BeforeEach
  public void setUp() {
    resolver = new WfsUriResolver();
  }

  @Test
  public void testResolveEntityReturnsNullWhenGmlNamespaceNotSet() {
    resolver.setWfsNamespace(WFS_NAMESPACE);

    InputSource result = resolver.resolveEntity(OTHER_NAMESPACE, "schema.xsd", "http://base");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testResolveEntityReturnsNullWhenWfsNamespaceNotSet() {
    resolver.setGmlNamespace(GML_NAMESPACE);

    InputSource result = resolver.resolveEntity(OTHER_NAMESPACE, "schema.xsd", "http://base");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testResolveEntityReturnsNullForGmlNamespace() {
    resolver.setGmlNamespace(GML_NAMESPACE);
    resolver.setWfsNamespace(WFS_NAMESPACE);

    InputSource result = resolver.resolveEntity(GML_NAMESPACE, "gml.xsd", "http://base");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testResolveEntityReturnsNullForWfsNamespace() {
    resolver.setGmlNamespace(GML_NAMESPACE);
    resolver.setWfsNamespace(WFS_NAMESPACE);

    InputSource result = resolver.resolveEntity(WFS_NAMESPACE, "wfs.xsd", "http://base");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testResolveEntityReturnsNullWhenBothNamespacesEmpty() {
    resolver.setGmlNamespace("");
    resolver.setWfsNamespace("");

    InputSource result = resolver.resolveEntity(OTHER_NAMESPACE, "other.xsd", "http://base");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testSetGmlNamespace() {
    resolver.setGmlNamespace(GML_NAMESPACE);
    resolver.setWfsNamespace(WFS_NAMESPACE);

    InputSource result = resolver.resolveEntity(GML_NAMESPACE, "gml.xsd", "http://base");

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testSetWfsNamespace() {
    resolver.setGmlNamespace(GML_NAMESPACE);
    resolver.setWfsNamespace(WFS_NAMESPACE);

    InputSource result = resolver.resolveEntity(WFS_NAMESPACE, "wfs.xsd", "http://base");

    assertThat(result, is(nullValue()));
  }
}
