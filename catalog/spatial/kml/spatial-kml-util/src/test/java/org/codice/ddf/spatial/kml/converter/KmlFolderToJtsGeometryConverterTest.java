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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

public class KmlFolderToJtsGeometryConverterTest {

  private static Folder testKmlFolder;

  @BeforeAll
  public static void setupClass() {
    InputStream stream =
        KmlFolderToJtsGeometryConverterTest.class.getResourceAsStream("/kmlFolder.kml");

    Kml kml = Kml.unmarshal(stream);

    testKmlFolder = ((Folder) kml.getFeature());
  }

  @Test
  public void testConvertKmlFolder() {
    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(testKmlFolder);

    assertThat(jtsGeometry, notNullValue());
    assertKmlFolder(testKmlFolder, jtsGeometry);
  }

  @Test
  public void testConvertNullKmlFolderReturnsNullJtsGeometry() {
    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(null);

    assertThat(jtsGeometry, nullValue());
  }

  @Test
  public void testConvertEmptyKmlFolderReturnsNull() {
    Folder emptyFolder = new Folder();

    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(emptyFolder);

    assertThat(jtsGeometry, nullValue());
  }

  @Test
  public void testConvertKmlFolderWithSinglePlacemark() {
    Folder folder = new Folder();
    Placemark placemark = folder.createAndAddPlacemark();
    Point point = placemark.createAndSetPoint();
    point.addToCoordinates(-122.0822035425683, 37.42228990140251);

    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(folder);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry, is(org.hamcrest.Matchers.instanceOf(GeometryCollection.class)));
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(1)));
  }

  @Test
  public void testConvertKmlFolderWithMultiplePlacemarks() {
    Folder folder = new Folder();

    Placemark placemark1 = folder.createAndAddPlacemark();
    Point point1 = placemark1.createAndSetPoint();
    point1.addToCoordinates(-122.0822035425683, 37.42228990140251);

    Placemark placemark2 = folder.createAndAddPlacemark();
    Point point2 = placemark2.createAndSetPoint();
    point2.addToCoordinates(-122.084075, 37.4220033612141);

    Placemark placemark3 = folder.createAndAddPlacemark();
    Point point3 = placemark3.createAndSetPoint();
    point3.addToCoordinates(-122.085, 37.423);

    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(folder);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(3)));
  }

  @Test
  public void testConvertKmlFolderWithNestedFolders() {
    Folder parentFolder = new Folder();

    Placemark placemark1 = parentFolder.createAndAddPlacemark();
    Point point1 = placemark1.createAndSetPoint();
    point1.addToCoordinates(-122.0822035425683, 37.42228990140251);

    Folder nestedFolder = parentFolder.createAndAddFolder();
    Placemark placemark2 = nestedFolder.createAndAddPlacemark();
    Point point2 = placemark2.createAndSetPoint();
    point2.addToCoordinates(-122.084075, 37.4220033612141);

    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(parentFolder);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(2)));
  }

  @Test
  public void testConvertKmlFolderWithMixedFeatures() {
    Folder folder = new Folder();

    Placemark placemark = folder.createAndAddPlacemark();
    Point point = placemark.createAndSetPoint();
    point.addToCoordinates(-122.0822035425683, 37.42228990140251);

    Document document = folder.createAndAddDocument();
    Placemark docPlacemark = document.createAndAddPlacemark();
    Point docPoint = docPlacemark.createAndSetPoint();
    docPoint.addToCoordinates(-122.084075, 37.4220033612141);

    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(folder);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(2)));
  }

  @Test
  public void testConvertKmlFolderWithNullGeometries() {
    Folder folder = new Folder();
    Placemark placemark1 = folder.createAndAddPlacemark();
    Point point1 = placemark1.createAndSetPoint();
    point1.addToCoordinates(-122.0822035425683, 37.42228990140251);

    folder.createAndAddPlacemark();

    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(folder);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(1)));
  }

  @Test
  public void testConvertKmlFolderWithOnlyNullGeometries() {
    Folder folder = new Folder();
    folder.createAndAddPlacemark();
    folder.createAndAddPlacemark();
    folder.createAndAddPlacemark();

    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(folder);

    assertThat(jtsGeometry, nullValue());
  }

  @Test
  public void testConvertKmlFolderWithDeepNesting() {
    Folder level1 = new Folder();
    Folder level2 = level1.createAndAddFolder();
    Folder level3 = level2.createAndAddFolder();

    Placemark placemark = level3.createAndAddPlacemark();
    Point point = placemark.createAndSetPoint();
    point.addToCoordinates(-122.0822035425683, 37.42228990140251);

    Geometry jtsGeometry = KmlFolderToJtsGeometryConverter.from(level1);

    assertThat(jtsGeometry, notNullValue());
    assertThat(jtsGeometry.getNumGeometries(), is(equalTo(1)));
  }

  static void assertKmlFolder(Folder kmlFolder, Geometry jtsGeometry) {
    assertThat(kmlFolder.getFeature().size(), is(equalTo(jtsGeometry.getNumGeometries())));

    for (int i = 0; i < kmlFolder.getFeature().size(); i++) {
      KmlPlacemarkToJtsGeometryConverterTest.assertPlacemark(
          (Placemark) kmlFolder.getFeature().get(i), jtsGeometry.getGeometryN(i));
    }
  }
}
