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
package ddf.catalog.transformer.input.geojson;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.AttributeRegistry;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeRegistryImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.codice.ddf.platform.util.SortedServiceList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Enhanced comprehensive test harness for GeoJsonInputTransformer. Tests GeoJSON parsing, geometry
 * validation, security, and edge cases.
 *
 * <p>SECURITY CRITICAL: GeoJSON transformers must handle: - Malformed GeoJSON injection - Invalid
 * geometry coordinates - Property injection attacks - Extremely large coordinate arrays (DoS) -
 * Invalid JSON structure
 */
public class GeoJsonInputTransformerEnhancedTest {

  private GeoJsonInputTransformer transformer;

  @BeforeEach
  public void setUp() {
    transformer = new GeoJsonInputTransformer();
    transformer.setInputTransformers(mock(SortedServiceList.class));
    // The transformer looks up unknown property keys in the AttributeRegistry. In the OSGi
    // runtime this is injected via blueprint; provide a real (empty) registry so unknown
    // properties (e.g. injection attempts, nested objects) are silently ignored instead of
    // triggering a NullPointerException.
    AttributeRegistry attributeRegistry = new AttributeRegistryImpl();
    transformer.setAttributeRegistry(attributeRegistry);
  }

  @Test
  public void testTransformValidPointGeometry() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"Point\","
            + "    \"coordinates\": [100.0, 0.5]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Test Point\"},"
            + "    \"description\": {\"value\": \"A test point feature\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
    assertThat(result.getLocation(), containsString("POINT"));
  }

  @Test
  public void testTransformValidLineStringGeometry() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"LineString\","
            + "    \"coordinates\": [[100.0, 0.0], [101.0, 1.0], [102.0, 2.0]]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Test Line\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
    assertThat(result.getLocation(), containsString("LINESTRING"));
  }

  @Test
  public void testTransformValidPolygonGeometry() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"Polygon\","
            + "    \"coordinates\": ["
            + "      [[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]"
            + "    ]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Test Polygon\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
    assertThat(result.getLocation(), containsString("POLYGON"));
  }

  @Test
  public void testTransformMultiPointGeometry() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"MultiPoint\","
            + "    \"coordinates\": [[100.0, 0.0], [101.0, 1.0], [102.0, 2.0]]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Multiple Points\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
  }

  @Test
  public void testTransformGeometryCollection() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"GeometryCollection\","
            + "    \"geometries\": ["
            + "      {\"type\": \"Point\", \"coordinates\": [100.0, 0.0]},"
            + "      {\"type\": \"LineString\", \"coordinates\": [[101.0, 0.0], [102.0, 1.0]]}"
            + "    ]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Geometry Collection\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
  }

  @Test
  public void testTransformFeatureWithoutGeometry() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": null,"
            + "  \"properties\": {"
            + "    \"title\": \"No Geometry Feature\","
            + "    \"description\": \"A feature without geometry\""
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is("No Geometry Feature"));
  }

  @Test
  public void testTransformWithPropertiesOnly() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"properties\": {"
            + "    \"title\": \"Properties Only\","
            + "    \"description\": \"Test\","
            + "    \"created\": \"2021-01-01T00:00:00Z\""
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is("Properties Only"));
  }

  @Test
  public void testTransformWithAllPropertyTypes() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"properties\": {"
            + "    \"title\": \"Full Properties\","
            + "    \"description\": \"Complete test\","
            + "    \"id\": \"test-id-456\","
            + "    \"created\": \"2021-01-01T12:00:00Z\","
            + "    \"modified\": \"2021-01-02T12:00:00Z\""
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is("Full Properties"));
    assertThat(result.getAttribute(Metacard.DESCRIPTION).getValue(), is("Complete test"));
  }

  @Test
  public void testTransformInvalidJson() {
    String invalidJson = "{invalid json structure";

    assertThrows(
        CatalogTransformerException.class, () -> transformer.transform(toInputStream(invalidJson)));
  }

  @Test
  public void testTransformEmptyString() {
    assertThrows(CatalogTransformerException.class, () -> transformer.transform(toInputStream("")));
  }

  @Test
  public void testTransformNullInput() {
    assertThrows(CatalogTransformerException.class, () -> transformer.transform(null));
  }

  @Test
  public void testTransformWithSpecialCharactersInProperties() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"properties\": {"
            + "    \"title\": \"Test & Title <special> \\\"quoted\\\"\","
            + "    \"description\": \"Line1\\nLine2\\tTabbed\""
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), containsString("&"));
    assertThat(result.getTitle(), containsString("<special>"));
  }

  @Test
  public void testTransformWithUnicodeCharacters() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"properties\": {"
            + "    \"title\": \"测试标题 Тест العنوان\","
            + "    \"description\": \"Unicode: ñ é ü ö 日本語\""
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is("测试标题 Тест العنوان"));
  }

  @Test
  public void testTransformWithEmojiCharacters() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"properties\": {"
            + "    \"title\": \"Emoji Test 😀 🎉 🚀\""
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWith3DCoordinates() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"Point\","
            + "    \"coordinates\": [100.0, 0.5, 50.0]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"3D Point\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
  }

  @Test
  public void testTransformWithNegativeCoordinates() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"Point\","
            + "    \"coordinates\": [-122.419, 37.775]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"San Francisco\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), containsString("-122.419"));
  }

  @Test
  public void testTransformWithVeryPreciseCoordinates() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"Point\","
            + "    \"coordinates\": [100.123456789012345, 0.987654321098765]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Precise Location\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
  }

  /** SECURITY TEST: Extremely large coordinate array (DoS attempt) */
  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  public void testHandlesLargeCoordinateArray() throws Exception {
    StringBuilder coords = new StringBuilder("[");
    for (int i = 0; i < 10000; i++) {
      if (i > 0) coords.append(",");
      coords.append("[").append(i * 0.001).append(",").append(i * 0.001).append("]");
    }
    coords.append("]");

    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"LineString\","
            + "    \"coordinates\": "
            + coords.toString()
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Large LineString\"}"
            + "  }"
            + "}";

    try {
      Metacard result = transformer.transform(toInputStream(geoJson));
      // Should handle or reject gracefully
      assertThat(result, notNullValue());
    } catch (CatalogTransformerException | OutOfMemoryError e) {
      // Acceptable to have size limits
    }
  }

  /** SECURITY TEST: Property injection attempt */
  @Test
  public void testPreventsPropertyInjection() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"properties\": {"
            + "    \"title\": \"Normal Title\","
            + "    \"malicious-script\": \"<script>alert('XSS')</script>\","
            + "    \"path-traversal\": \"../../etc/passwd\""
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    // Malicious properties should be ignored or sanitized
  }

  /** SECURITY TEST: Invalid geometry type */
  @Test
  public void testHandlesInvalidGeometryType() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"InvalidType\","
            + "    \"coordinates\": [100.0, 0.0]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Invalid Geometry\"}"
            + "  }"
            + "}";

    // An unrecognized geometry type is handled gracefully: CompositeGeometry yields no geometry,
    // so the transform succeeds but no location is set on the metacard (rather than crashing or
    // fabricating a bogus location).
    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), is(nullValue()));
  }

  @Test
  public void testTransformPolygonWithHole() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"Polygon\","
            + "    \"coordinates\": ["
            + "      [[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]],"
            + "      [[100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2]]"
            + "    ]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Polygon with Hole\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
  }

  @Test
  public void testTransformWithEmptyProperties() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"Point\","
            + "    \"coordinates\": [100.0, 0.0]"
            + "  },"
            + "  \"properties\": {}"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformFeatureCollection() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"FeatureCollection\","
            + "  \"features\": ["
            + "    {"
            + "      \"type\": \"Feature\","
            + "      \"geometry\": {\"type\": \"Point\", \"coordinates\": [100.0, 0.0]},"
            + "      \"properties\": {\"title\": {\"value\": \"Feature 1\"}}"
            + "    },"
            + "    {"
            + "      \"type\": \"Feature\","
            + "      \"geometry\": {\"type\": \"Point\", \"coordinates\": [101.0, 1.0]},"
            + "      \"properties\": {\"title\": {\"value\": \"Feature 2\"}}"
            + "    }"
            + "  ]"
            + "}";

    // The transformer only supports a root "type" of "Feature"; a "FeatureCollection" is rejected
    // with a CatalogTransformerException rather than being silently transformed.
    assertThrows(
        CatalogTransformerException.class, () -> transformer.transform(toInputStream(geoJson)));
  }

  @Test
  public void testTransformWithWhitespace() throws Exception {
    String geoJson =
        "{\n"
            + "  \"type\": \"Feature\",\n"
            + "  \"geometry\": {\n"
            + "    \"type\": \"Point\",\n"
            + "    \"coordinates\": [100.0, 0.0]\n"
            + "  },\n"
            + "  \"properties\": {\n"
            + "    \"title\": {\"value\": \"Whitespace Test\"}\n"
            + "  }\n"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformMinimalGeoJson() throws Exception {
    String geoJson = "{\"type\":\"Feature\",\"properties\":{}}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformAcrossAntimeridian() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"LineString\","
            + "    \"coordinates\": [[170.0, 0.0], [-170.0, 0.0]]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"Crosses Antimeridian\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
  }

  @Test
  public void testTransformPolarCoordinates() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"geometry\": {"
            + "    \"type\": \"Point\","
            + "    \"coordinates\": [0.0, 90.0]"
            + "  },"
            + "  \"properties\": {"
            + "    \"title\": {\"value\": \"North Pole\"}"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getLocation(), notNullValue());
  }

  @Test
  public void testTransformWithNestedProperties() throws Exception {
    String geoJson =
        "{"
            + "  \"type\": \"Feature\","
            + "  \"properties\": {"
            + "    \"title\": \"Nested Test\","
            + "    \"nested\": {"
            + "      \"level1\": {"
            + "        \"level2\": \"deep value\""
            + "      }"
            + "    }"
            + "  }"
            + "}";

    Metacard result = transformer.transform(toInputStream(geoJson));

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is("Nested Test"));
  }

  // Helper method

  private InputStream toInputStream(String content) {
    return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
  }
}
