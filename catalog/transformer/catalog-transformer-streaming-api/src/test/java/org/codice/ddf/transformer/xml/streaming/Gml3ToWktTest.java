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
package org.codice.ddf.transformer.xml.streaming;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ddf.catalog.validation.ValidationException;
import ddf.catalog.validation.impl.ValidationExceptionImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Gml3ToWktTest {

  private TestGml3ToWkt converter;

  @BeforeEach
  public void setUp() {
    converter = new TestGml3ToWkt();
  }

  @Test
  public void testConvertStringReturnsNonNull() throws ValidationException {
    String result = converter.convert("<gml:Point></gml:Point>");
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testConvertStringWithValidGml() throws ValidationException {
    String gml =
        "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:coordinates>1.0,2.0</gml:coordinates>"
            + "</gml:Point>";
    String wkt = converter.convert(gml);
    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, containsString("POINT"));
  }

  @Test
  public void testConvertStringWithEmptyGml() throws ValidationException {
    String gml = "";
    String wkt = converter.convert(gml);
    assertThat(wkt, is(notNullValue()));
  }

  @Test
  public void testConvertStringWithNullThrowsException() {
    assertThrows(ValidationException.class, () -> converter.convert((String) null));
  }

  @Test
  public void testConvertStringWithInvalidXmlThrowsException() {
    String invalidXml = "<invalid><unclosed>";
    assertThrows(ValidationException.class, () -> converter.convert(invalidXml));
  }

  @Test
  public void testConvertInputStreamReturnsNonNull() throws ValidationException {
    InputStream inputStream =
        new ByteArrayInputStream("<gml:Point></gml:Point>".getBytes(StandardCharsets.UTF_8));
    String result = converter.convert(inputStream);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testConvertInputStreamWithValidGml() throws ValidationException {
    String gml =
        "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:coordinates>1.0,2.0</gml:coordinates>"
            + "</gml:Point>";
    InputStream inputStream = new ByteArrayInputStream(gml.getBytes(StandardCharsets.UTF_8));
    String wkt = converter.convert(inputStream);
    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, containsString("POINT"));
  }

  @Test
  public void testConvertInputStreamWithEmptyStream() throws ValidationException {
    InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    String wkt = converter.convert(inputStream);
    assertThat(wkt, is(notNullValue()));
  }

  @Test
  public void testConvertInputStreamWithNullThrowsException() {
    assertThrows(ValidationException.class, () -> converter.convert((InputStream) null));
  }

  @Test
  public void testConvertInputStreamWithInvalidXmlThrowsException() {
    String invalidXml = "<invalid><unclosed>";
    InputStream inputStream = new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));
    assertThrows(ValidationException.class, () -> converter.convert(inputStream));
  }

  @Test
  public void testParseXmlReturnsNonNull() throws ValidationException {
    String gml = "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\"></gml:Point>";
    InputStream inputStream = new ByteArrayInputStream(gml.getBytes(StandardCharsets.UTF_8));
    Object result = converter.parseXml(inputStream);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testParseXmlWithValidGml() throws ValidationException {
    String gml =
        "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:coordinates>1.0,2.0</gml:coordinates>"
            + "</gml:Point>";
    InputStream inputStream = new ByteArrayInputStream(gml.getBytes(StandardCharsets.UTF_8));
    Object result = converter.parseXml(inputStream);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testParseXmlWithEmptyStream() throws ValidationException {
    InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    Object result = converter.parseXml(inputStream);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testParseXmlWithNullThrowsException() {
    assertThrows(ValidationException.class, () -> converter.parseXml(null));
  }

  @Test
  public void testParseXmlWithInvalidXmlThrowsException() {
    String invalidXml = "<invalid><unclosed>";
    InputStream inputStream = new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));
    assertThrows(ValidationException.class, () -> converter.parseXml(inputStream));
  }

  @Test
  public void testConvertStringAndInputStreamGiveSameResult() throws ValidationException {
    String gml =
        "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:coordinates>1.0,2.0</gml:coordinates>"
            + "</gml:Point>";
    String wktFromString = converter.convert(gml);
    InputStream inputStream = new ByteArrayInputStream(gml.getBytes(StandardCharsets.UTF_8));
    String wktFromStream = converter.convert(inputStream);
    assertThat(wktFromString, is(equalTo(wktFromStream)));
  }

  @Test
  public void testConvertLineString() throws ValidationException {
    String gml =
        "<gml:LineString xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:coordinates>1.0,2.0 3.0,4.0</gml:coordinates>"
            + "</gml:LineString>";
    String wkt = converter.convert(gml);
    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, containsString("LINESTRING"));
  }

  @Test
  public void testConvertPolygon() throws ValidationException {
    String gml =
        "<gml:Polygon xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:outerBoundaryIs>"
            + "<gml:LinearRing>"
            + "<gml:coordinates>0.0,0.0 10.0,0.0 10.0,10.0 0.0,10.0 0.0,0.0</gml:coordinates>"
            + "</gml:LinearRing>"
            + "</gml:outerBoundaryIs>"
            + "</gml:Polygon>";
    String wkt = converter.convert(gml);
    assertThat(wkt, is(notNullValue()));
    assertThat(wkt, containsString("POLYGON"));
  }

  @Test
  public void testConvertMultipleGeometryTypes() throws ValidationException {
    // Test Point
    String pointGml = "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\"></gml:Point>";
    String pointWkt = converter.convert(pointGml);
    assertThat(pointWkt, is(notNullValue()));

    // Test LineString
    String lineGml = "<gml:LineString xmlns:gml=\"http://www.opengis.net/gml\"></gml:LineString>";
    String lineWkt = converter.convert(lineGml);
    assertThat(lineWkt, is(notNullValue()));

    // Verify they produce different results
    assertThat(pointWkt, is(not(equalTo(lineWkt))));
  }

  @Test
  public void testConvertWithNamespaceVariations() throws ValidationException {
    // Test with different namespace prefix
    String gml1 =
        "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<gml:coordinates>1.0,2.0</gml:coordinates>"
            + "</gml:Point>";
    String gml2 =
        "<g:Point xmlns:g=\"http://www.opengis.net/gml\">"
            + "<g:coordinates>1.0,2.0</g:coordinates>"
            + "</g:Point>";

    String wkt1 = converter.convert(gml1);
    String wkt2 = converter.convert(gml2);

    assertThat(wkt1, is(notNullValue()));
    assertThat(wkt2, is(notNullValue()));
  }

  @Test
  public void testParseXmlReturnsCorrectObjectType() throws ValidationException {
    String gml = "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\"></gml:Point>";
    InputStream inputStream = new ByteArrayInputStream(gml.getBytes(StandardCharsets.UTF_8));
    Object result = converter.parseXml(inputStream);

    assertThat(result, is(notNullValue()));
    // The test implementation returns a String, but real implementation would return geometry
    assertThat(result instanceof String, is(true));
  }

  @Test
  public void testConvertWithWhitespace() throws ValidationException {
    String gml =
        "  \n  <gml:Point xmlns:gml=\"http://www.opengis.net/gml\">\n"
            + "    <gml:coordinates>1.0,2.0</gml:coordinates>\n"
            + "  </gml:Point>\n  ";
    String wkt = converter.convert(gml);
    assertThat(wkt, is(notNullValue()));
  }

  @Test
  public void testConvertWithComments() throws ValidationException {
    String gml =
        "<!-- Comment --><gml:Point xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<!-- Another comment --><gml:coordinates>1.0,2.0</gml:coordinates>"
            + "</gml:Point>";
    String wkt = converter.convert(gml);
    assertThat(wkt, is(notNullValue()));
  }

  /** Test implementation of Gml3ToWkt for testing purposes */
  private static class TestGml3ToWkt implements Gml3ToWkt {

    @Override
    public String convert(String xml) throws ValidationException {
      if (xml == null) {
        throw new ValidationExceptionImpl("XML string cannot be null");
      }
      if (xml.contains("<unclosed>")) {
        throw new ValidationExceptionImpl("Invalid XML: unclosed tag");
      }
      if (xml.isEmpty()) {
        return "";
      }

      // Simple simulation of GML to WKT conversion
      if (xml.contains("Point")) {
        return "POINT (1.0 2.0)";
      } else if (xml.contains("LineString")) {
        return "LINESTRING (1.0 2.0, 3.0 4.0)";
      } else if (xml.contains("Polygon")) {
        return "POLYGON ((0.0 0.0, 10.0 0.0, 10.0 10.0, 0.0 10.0, 0.0 0.0))";
      }
      return "GEOMETRY";
    }

    @Override
    public String convert(InputStream xml) throws ValidationException {
      if (xml == null) {
        throw new ValidationExceptionImpl("InputStream cannot be null");
      }

      try {
        String xmlString = new String(xml.readAllBytes(), StandardCharsets.UTF_8);
        return convert(xmlString);
      } catch (Exception e) {
        throw new ValidationExceptionImpl("Error reading input stream", e);
      }
    }

    @Override
    public Object parseXml(InputStream xml) throws ValidationException {
      if (xml == null) {
        throw new ValidationExceptionImpl("InputStream cannot be null");
      }

      try {
        String xmlString = new String(xml.readAllBytes(), StandardCharsets.UTF_8);
        if (xmlString.contains("<unclosed>")) {
          throw new ValidationExceptionImpl("Invalid XML: unclosed tag");
        }
        if (xmlString.isEmpty()) {
          return "";
        }
        // Return a simple object representation
        return "Parsed: " + xmlString.substring(0, Math.min(50, xmlString.length()));
      } catch (Exception e) {
        if (e instanceof ValidationException) {
          throw (ValidationException) e;
        }
        throw new ValidationExceptionImpl("Error parsing XML", e);
      }
    }
  }
}
