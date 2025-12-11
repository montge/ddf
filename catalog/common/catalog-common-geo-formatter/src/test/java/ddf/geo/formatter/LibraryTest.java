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
package ddf.geo.formatter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

public class LibraryTest extends AbstractTestCompositeGeometry {

  @Test
  public void testNullPointArgument() {
    assertThrows(IllegalArgumentException.class, () -> new Point(null));
  }

  @Test
  public void testNullMultiPointArgument() {
    assertThrows(IllegalArgumentException.class, () -> new MultiPoint(null));
  }

  @Test
  public void testNullLineStringArgument() {
    assertThrows(IllegalArgumentException.class, () -> new LineString(null));
  }

  @Test
  public void testNotAGeometryCollection() throws ParseException {
    Geometry polygon = getSamplePolygon();
    assertThrows(IllegalArgumentException.class, () -> new GeometryCollection(polygon));
  }

  @Test
  public void testNotALineString() throws ParseException {
    Geometry polygon = getSamplePolygon();
    assertThrows(IllegalArgumentException.class, () -> new LineString(polygon));
  }

  @Test
  public void testNotAMultiLineString() throws ParseException {
    Geometry polygon = getSamplePolygon();
    assertThrows(IllegalArgumentException.class, () -> new MultiLineString(polygon));
  }

  @Test
  public void testNotAMultiPoint() throws ParseException {
    Geometry polygon = getSamplePolygon();
    assertThrows(IllegalArgumentException.class, () -> new MultiPoint(polygon));
  }

  @Test
  public void testNotAMultiPolygon() throws ParseException {
    Geometry polygon = getSamplePolygon();
    assertThrows(IllegalArgumentException.class, () -> new MultiPolygon(polygon));
  }

  @Test
  public void testNotAPoint() throws ParseException {
    Geometry polygon = getSamplePolygon();
    assertThrows(IllegalArgumentException.class, () -> new Point(polygon));
  }

  @Test
  public void testNotAPolygon() throws ParseException {
    Geometry point = reader.read(new StringReader("POINT (1 0)"));
    assertThrows(IllegalArgumentException.class, () -> new Polygon(point));
  }

  protected Geometry getSamplePolygon() throws ParseException {
    return reader.read(
        new StringReader(
            "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10),(20 30, 35 35, 30 20, 20 30))"));
  }
}
