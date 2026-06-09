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
package org.codice.ddf.spatial.kml.transformer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeImpl;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link KmlStyleMapEntryImpl}. */
public class KmlStyleMapEntryImplTest {

  @Test
  public void testConstructorWithParameters() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("testAttr", "testValue", "#testStyle");

    assertThat(entry.getAttributeName(), is("testAttr"));
    assertThat(entry.getAttributeValue(), is("testValue"));
    assertThat(entry.getStyleUrl(), is("#testStyle"));
  }

  @Test
  public void testSettersAndGetters() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl();

    entry.setAttributeName("name");
    entry.setAttributeValue("value");
    entry.setStyleUrl("#style");

    assertThat(entry.getAttributeName(), is("name"));
    assertThat(entry.getAttributeValue(), is("value"));
    assertThat(entry.getStyleUrl(), is("#style"));
  }

  @Test
  public void testMetacardMatchWithSourceId() {
    KmlStyleMapEntryImpl entry =
        new KmlStyleMapEntryImpl(Metacard.SOURCE_ID, "test-source", "#style");

    Metacard metacard = mock(Metacard.class);
    when(metacard.getSourceId()).thenReturn("test-source");

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithSourceIdNoMatch() {
    KmlStyleMapEntryImpl entry =
        new KmlStyleMapEntryImpl(Metacard.SOURCE_ID, "test-source", "#style");

    Metacard metacard = mock(Metacard.class);
    when(metacard.getSourceId()).thenReturn("different-source");

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(false));
  }

  @Test
  public void testMetacardMatchWithStringAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("title", "Test Title", "#style");

    Metacard metacard =
        createMockMetacard("title", "Test Title", AttributeType.AttributeFormat.STRING);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithStringAttributeNoMatch() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("title", "Expected Title", "#style");

    Metacard metacard =
        createMockMetacard("title", "Different Title", AttributeType.AttributeFormat.STRING);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(false));
  }

  @Test
  public void testMetacardMatchWithBooleanAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("flagAttr", "true", "#style");

    Metacard metacard = createMockMetacard("flagAttr", true, AttributeType.AttributeFormat.BOOLEAN);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithBooleanAttributeFalse() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("flagAttr", "false", "#style");

    Metacard metacard =
        createMockMetacard("flagAttr", false, AttributeType.AttributeFormat.BOOLEAN);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithIntegerAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("count", "42", "#style");

    Metacard metacard = createMockMetacard("count", 42, AttributeType.AttributeFormat.INTEGER);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithIntegerAttributeNoMatch() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("count", "42", "#style");

    Metacard metacard = createMockMetacard("count", 100, AttributeType.AttributeFormat.INTEGER);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(false));
  }

  @Test
  public void testMetacardMatchWithLongAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("bigNumber", "1000000", "#style");

    Metacard metacard =
        createMockMetacard("bigNumber", 1000000L, AttributeType.AttributeFormat.LONG);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithShortAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("smallNumber", "10", "#style");

    Metacard metacard =
        createMockMetacard("smallNumber", (short) 10, AttributeType.AttributeFormat.SHORT);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithFloatAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("floatValue", "3.14", "#style");

    Metacard metacard =
        createMockMetacard("floatValue", 3.14f, AttributeType.AttributeFormat.FLOAT);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithDoubleAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("doubleValue", "3.141592", "#style");

    Metacard metacard =
        createMockMetacard("doubleValue", 3.141592, AttributeType.AttributeFormat.DOUBLE);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithDateAttribute() throws Exception {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date testDate = dateFormat.parse("2023-01-15T10:30:00");

    KmlStyleMapEntryImpl entry =
        new KmlStyleMapEntryImpl("created", "2023-01-15T10:30:00", "#style");

    Metacard metacard = createMockMetacard("created", testDate, AttributeType.AttributeFormat.DATE);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithXmlAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("xmlData", "<data>test</data>", "#style");

    Metacard metacard =
        createMockMetacard("xmlData", "<data>test</data>", AttributeType.AttributeFormat.XML);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithGeometryAttribute() {
    String wkt = "POINT(-117.15 32.72)";
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("location", wkt, "#style");

    Metacard metacard = createMockMetacard("location", wkt, AttributeType.AttributeFormat.GEOMETRY);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(true));
  }

  @Test
  public void testMetacardMatchWithNullAttribute() {
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("missingAttr", "value", "#style");

    Metacard metacard = mock(Metacard.class);
    when(metacard.getAttribute("missingAttr")).thenReturn(null);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(false));
  }

  @Test
  public void testMetacardMatchWithBinaryAttribute() {
    // Binary attributes should return false (unsupported)
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("binaryData", "data", "#style");

    Metacard metacard =
        createMockMetacard(
            "binaryData", new byte[] {1, 2, 3}, AttributeType.AttributeFormat.BINARY);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(false));
  }

  @Test
  public void testMetacardMatchWithObjectAttribute() {
    // Object attributes should return false (unsupported)
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("objectData", "data", "#style");

    Metacard metacard =
        createMockMetacard("objectData", new Object(), AttributeType.AttributeFormat.OBJECT);

    boolean matches = entry.metacardMatch(metacard);
    assertThat(matches, is(false));
  }

  @Test
  public void testInit() {
    // init() only logs a debug message; verify it completes without throwing.
    KmlStyleMapEntryImpl entry = new KmlStyleMapEntryImpl("attr", "value", "#style");
    assertDoesNotThrow(entry::init);
  }

  private Metacard createMockMetacard(
      String attributeName, Object value, AttributeType.AttributeFormat format) {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    AttributeDescriptor descriptor = mock(AttributeDescriptor.class);
    AttributeType attributeType = mock(AttributeType.class);
    Attribute attribute = new AttributeImpl(attributeName, (Serializable) value);

    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacard.getAttribute(attributeName)).thenReturn(attribute);
    when(metacardType.getAttributeDescriptor(attributeName)).thenReturn(descriptor);
    when(descriptor.getType()).thenReturn(attributeType);
    when(attributeType.getAttributeFormat()).thenReturn(format);

    return metacard;
  }
}
