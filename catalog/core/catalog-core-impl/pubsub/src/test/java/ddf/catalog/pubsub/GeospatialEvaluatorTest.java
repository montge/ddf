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
package ddf.catalog.pubsub;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import ddf.catalog.pubsub.criteria.geospatial.GeospatialEvaluator;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeospatialEvaluatorTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialEvaluatorTest.class);

  @Test
  public void testBuildGeometry() throws Exception {
    // The metadata schema declares <gml:pos> points in LAT,LON order. buildGeometry must parse
    // the GML and swap the coordinates so the resulting WKT geometry uses LON,LAT order.
    String gmlText =
        "<gml:Polygon xmlns:gml=\"http://www.opengis.net/gml\" gml:id=\"BGE-1\">\n"
            + "    <gml:exterior>\n"
            + "        <gml:LinearRing>\n"
            + "            <gml:pos>34.0 44.0</gml:pos>\n"
            + "            <gml:pos>33.0 44.0</gml:pos>\n"
            + "            <gml:pos>33.0 45.0</gml:pos>\n"
            + "            <gml:pos>34.0 45.0</gml:pos>\n"
            + "            <gml:pos>34.0 44.0</gml:pos>\n"
            + "        </gml:LinearRing>\n"
            + "    </gml:exterior>\n"
            + "</gml:Polygon>";

    Geometry geometry = GeospatialEvaluator.buildGeometry(gmlText);
    LOGGER.debug("geometry.toText() = {}", geometry.toText());

    assertThat(geometry, is(notNullValue()));
    assertThat(geometry, is(instanceOf(Polygon.class)));

    // The first GML pos (LAT=34.0, LON=44.0) must be swapped to a LON,LAT coordinate (x=44.0,
    // y=34.0) in the returned geometry.
    Coordinate firstCoord = geometry.getCoordinates()[0];
    assertThat(firstCoord.x, is(44.0));
    assertThat(firstCoord.y, is(34.0));
  }
}
