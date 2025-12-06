/*
 * Copyright (c) Codice Foundation
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.spatial.kml.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import java.io.InputStream;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

public class KmlDocumentToJtsGeometryConverterTest {
  private static Document testKmlDocument;

  @BeforeClass
  public static void setupClass() {
    InputStream stream =
        KmlDocumentToJtsGeometryConverterTest.class.getResourceAsStream("/kmlDocument.kml");

    Kml kml = Kml.unmarshal(stream);

    testKmlDocument = ((Document) kml.getFeature());
  }

  @Test
  public void testConvertKmlDocument() {
    Geometry jtsGeometry = KmlDocumentToJtsGeometryConverter.from(testKmlDocument);

    assertThat(jtsGeometry, notNullValue());
    assertKmlDocument(testKmlDocument, jtsGeometry);
  }

  @Test
  public void testConvertNullKmlDocumentReturnsNullJtsGeometry() {
    Geometry jtsGeometry = KmlDocumentToJtsGeometryConverter.from(null);

    assertThat(jtsGeometry, nullValue());
  }

  @Test
  public void testConvertEmptyKmlDocumentReturnsNull() {
    Document emptyDocument = new Document();

    Geometry jtsGeometry = KmlDocumentToJtsGeometryConverter.from(emptyDocument);

    assertThat(jtsGeometry, nullValue());
  }

  @Test
  public void testConvertKmlDocumentWithSinglePlacemark() {
    Document document = new Document();
    Placemark placemark = document.createAndAddPlacemark();
    Point point = placemark.createAndSetPoint();
    point.addToCoordinates(-122.0822035425683, 37.42228990140251);

    Geometry jtsGeometry = KmlDocumentToJtsGeometryConverter.from(document);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry, is(org.hamcrest.Matchers.instanceOf(GeometryCollection.class)));
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(1)));
  }

  @Test
  public void testConvertKmlDocumentWithMultiplePlacemarks() {
    Document document = new Document();

    Placemark placemark1 = document.createAndAddPlacemark();
    Point point1 = placemark1.createAndSetPoint();
    point1.addToCoordinates(-122.0822035425683, 37.42228990140251);

    Placemark placemark2 = document.createAndAddPlacemark();
    Point point2 = placemark2.createAndSetPoint();
    point2.addToCoordinates(-122.084075, 37.4220033612141);

    Geometry jtsGeometry = KmlDocumentToJtsGeometryConverter.from(document);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(2)));
  }

  @Test
  public void testConvertKmlDocumentWithMixedFeatures() {
    Document document = new Document();

    Placemark placemark = document.createAndAddPlacemark();
    Point point = placemark.createAndSetPoint();
    point.addToCoordinates(-122.0822035425683, 37.42228990140251);

    Folder folder = document.createAndAddFolder();
    Placemark nestedPlacemark = folder.createAndAddPlacemark();
    Point nestedPoint = nestedPlacemark.createAndSetPoint();
    nestedPoint.addToCoordinates(-122.084075, 37.4220033612141);

    Geometry jtsGeometry = KmlDocumentToJtsGeometryConverter.from(document);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(2)));
  }

  @Test
  public void testConvertKmlDocumentWithNullGeometries() {
    Document document = new Document();
    Placemark placemark1 = document.createAndAddPlacemark();
    Point point1 = placemark1.createAndSetPoint();
    point1.addToCoordinates(-122.0822035425683, 37.42228990140251);

    document.createAndAddPlacemark();

    Geometry jtsGeometry = KmlDocumentToJtsGeometryConverter.from(document);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(1)));
  }

  @Test
  public void testConvertKmlDocumentWithOnlyNullGeometries() {
    Document document = new Document();
    document.createAndAddPlacemark();
    document.createAndAddPlacemark();

    Geometry jtsGeometry = KmlDocumentToJtsGeometryConverter.from(document);

    assertThat(jtsGeometry, nullValue());
  }

  static void assertKmlDocument(Document kmlDocument, Geometry jtsGeometry) {
    assertThat(kmlDocument.getFeature().size(), is(equalTo(jtsGeometry.getNumGeometries())));

    for (int i = 0; i < kmlDocument.getFeature().size(); i++) {
      KmlPlacemarkToJtsGeometryConverterTest.assertPlacemark(
          (Placemark) kmlDocument.getFeature().get(i), jtsGeometry.getGeometryN(i));
    }
  }
}
