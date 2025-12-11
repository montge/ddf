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
package org.codice.ddf.spatial.kml.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class KmlMarshallerTest {

  private KmlMarshaller kmlMarshaller;

  @BeforeEach
  public void setup() {
    setupXpath();
    kmlMarshaller = new KmlMarshaller();
  }

  void setupXpath() {
    NamespaceContext ctx =
        new SimpleNamespaceContext(singletonMap("m", "http://www.opengis.net/kml/2.2"));
    XMLUnit.setXpathNamespaceContext(ctx);
  }

  @Test
  public void marshall() throws SAXException, IOException, XpathException {

    Placemark placemark = new Placemark();
    placemark.setName("a");

    Kml kml = new Kml();
    kml.setFeature(placemark);
    final String kmlString = kmlMarshaller.marshal(kml);

    assertXpathExists("/m:kml", kmlString);
    assertXpathEvaluatesTo("a", "//m:Placemark/m:name", kmlString);
  }

  @Test
  public void marshallNull() {
    Assert.assertThrows(IllegalArgumentException.class, () -> kmlMarshaller.marshal(null));
  }

  @Test
  public void unmarshall() {
    final InputStream resourceAsStream = this.getClass().getResourceAsStream("/kmlPoint.kml");
    final Kml kml = kmlMarshaller.unmarshal(resourceAsStream).get();
    final Feature feature = kml.getFeature();

    assertThat(feature.getName(), is("Simple placemark"));
  }

  @Test
  public void unmarshallNullStream() {
    Assert.assertThrows(NoSuchElementException.class, () -> kmlMarshaller.unmarshal(null).get());
  }

  @Test
  public void unmarshallEmptyStream() {
    final ByteArrayInputStream inputStream = new ByteArrayInputStream("".getBytes(UTF_8));
    Assert.assertThrows(
        NoSuchElementException.class, () -> kmlMarshaller.unmarshal(inputStream).get());
  }
}
