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
package ddf.catalog.transformer.metacard.geojson;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.geo.formatter.CompositeGeometry;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoJsonMetacardTransformerEnhancedTest {

  private static final JSONParser PARSER = new JSONParser();

  private GeoJsonMetacardTransformer transformer;

  @Before
  public void setUp() {
    transformer = new GeoJsonMetacardTransformer();
  }

  @Test
  public void testTransformWithAllBasicTypes() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("string-attr", BasicTypes.STRING_TYPE));
    descriptors.add(createDescriptor("int-attr", BasicTypes.INTEGER_TYPE));
    descriptors.add(createDescriptor("long-attr", BasicTypes.LONG_TYPE));
    descriptors.add(createDescriptor("short-attr", BasicTypes.SHORT_TYPE));
    descriptors.add(createDescriptor("float-attr", BasicTypes.FLOAT_TYPE));
    descriptors.add(createDescriptor("double-attr", BasicTypes.DOUBLE_TYPE));
    descriptors.add(createDescriptor("boolean-attr", BasicTypes.BOOLEAN_TYPE));
    descriptors.add(createDescriptor("date-attr", BasicTypes.DATE_TYPE));
    descriptors.add(createDescriptor("binary-attr", BasicTypes.BINARY_TYPE));
    descriptors.add(createDescriptor("xml-attr", BasicTypes.XML_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);

    metacard.setAttribute(new AttributeImpl("string-attr", "test"));
    metacard.setAttribute(new AttributeImpl("int-attr", 42));
    metacard.setAttribute(new AttributeImpl("long-attr", 123456789L));
    metacard.setAttribute(new AttributeImpl("short-attr", (short) 10));
    metacard.setAttribute(new AttributeImpl("float-attr", 3.14f));
    metacard.setAttribute(new AttributeImpl("double-attr", 2.718281828));
    metacard.setAttribute(new AttributeImpl("boolean-attr", true));
    metacard.setAttribute(new AttributeImpl("date-attr", new Date(1609459200000L)));
    metacard.setAttribute(new AttributeImpl("binary-attr", new byte[] {1, 2, 3}));
    metacard.setAttribute(new AttributeImpl("xml-attr", "<test>data</test>"));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    assertThat(properties.get("string-attr"), is("test"));
    assertThat(properties.get("int-attr"), is("42"));
    assertThat(properties.get("long-attr"), is("123456789"));
    assertThat(properties.get("short-attr"), is("10"));
    assertThat(properties.get("boolean-attr"), is(true));
    assertThat(properties.get("xml-attr"), is("<test>data</test>"));
  }

  @Test
  public void testTransformWithNullValues() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("nullable", BasicTypes.STRING_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    assertThat(properties.containsKey("nullable"), is(false));
  }

  @Test
  public void testTransformWithObjectAttribute() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("object-attr", BasicTypes.OBJECT_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setAttribute(new AttributeImpl("object-attr", new java.util.HashMap<>()));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    // Object attributes should be null in output
    assertThat(properties.get("object-attr"), is(nullValue()));
  }

  @Test
  public void testTransformWithEmptySourceId() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");
    metacard.setSourceId("");

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    assertThat(properties.containsKey("source-id"), is(false));
  }

  @Test
  public void testTransformWithNullSourceId() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");
    metacard.setSourceId(null);

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    assertThat(properties.containsKey("source-id"), is(false));
  }

  @Test
  public void testTransformWithValidSourceId() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");
    metacard.setSourceId("test-source");

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    assertThat(properties.get("source-id"), is("test-source"));
  }

  @Test
  public void testTransformFeatureType() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    assertThat(json.get("type"), is("Feature"));
  }

  @Test
  public void testTransformIncludesPropertiesObject() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    assertThat(json.get("properties"), is(notNullValue()));
    assertThat(json.get("properties") instanceof Map, is(true));
  }

  @Test
  public void testTransformIncludesGeometryKey() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    assertThat(json.containsKey(CompositeGeometry.GEOMETRY_KEY), is(true));
  }

  @Test
  public void testTransformWithMultiValuedStringAttribute() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "multi-string", true, false, false, true, BasicTypes.STRING_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setAttribute(
        new AttributeImpl("multi-string", (Serializable) Arrays.asList("a", "b", "c")));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    List<String> values = (List<String>) properties.get("multi-string");
    assertThat(values.size(), is(3));
    assertThat(values.get(0), is("a"));
    assertThat(values.get(1), is("b"));
    assertThat(values.get(2), is("c"));
  }

  @Test
  public void testTransformWithMultiValuedIntegerAttribute() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "multi-int", true, false, false, true, BasicTypes.INTEGER_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setAttribute(new AttributeImpl("multi-int", (Serializable) Arrays.asList(1, 2, 3)));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    List<String> values = (List<String>) properties.get("multi-int");
    assertThat(values.size(), is(3));
    assertThat(values.get(0), is("1"));
    assertThat(values.get(1), is("2"));
    assertThat(values.get(2), is("3"));
  }

  @Test
  public void testTransformWithSingleValuedAttributeAsList() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("single-value", BasicTypes.STRING_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setAttribute(new AttributeImpl("single-value", "test"));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    // Single-valued should be returned as string, not array
    assertThat(properties.get("single-value"), is("test"));
  }

  @Test
  public void testTransformWithEmptyBinaryData() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("binary", BasicTypes.BINARY_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setAttribute(new AttributeImpl("binary", new byte[] {}));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    assertThat(properties.get("binary"), is(""));
  }

  @Test
  public void testTransformDateFormatting() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("created", BasicTypes.DATE_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    Date testDate = new Date(1609459200000L); // 2021-01-01T00:00:00.000Z
    metacard.setAttribute(new AttributeImpl("created", testDate));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    String dateString = (String) properties.get("created");
    assertThat(dateString, is(notNullValue()));
    // Should be in ISO 8601 format
    assertThat(dateString, is("2021-01-01T00:00:00.000+0000"));
  }

  @Test
  public void testConvertToJSONStaticMethod() throws CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");
    metacard.setId("test-id");

    JSONObject json = GeoJsonMetacardTransformer.convertToJSON(metacard);

    assertThat(json, is(notNullValue()));
    assertThat(json.get("type"), is("Feature"));
    assertThat(json.get("properties"), is(notNullValue()));
  }

  @Test(expected = CatalogTransformerException.class)
  public void testConvertToJSONWithNullMetacard() throws CatalogTransformerException {
    GeoJsonMetacardTransformer.convertToJSON(null);
  }

  @Test
  public void testTransformWithArguments() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    Map<String, Serializable> arguments = Collections.singletonMap("key", "value");
    BinaryContent content = transformer.transform(metacard, arguments);

    assertThat(content, is(notNullValue()));
    assertThat(content.getMimeTypeValue(), is("application/json"));
  }

  @Test
  public void testTransformMimeType() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    BinaryContent content = transformer.transform(metacard, null);

    assertThat(content.getMimeType().getPrimaryType(), is("application"));
    assertThat(content.getMimeType().getSubType(), is("json"));
  }

  @Test
  public void testToStringMethod() {
    String result = transformer.toString();

    assertThat(result, is(notNullValue()));
    assertThat(result.contains("MetacardTransformer"), is(true));
    assertThat(result.contains("geojson"), is(true));
  }

  @Test
  public void testTransformWithCustomMetacardType() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("custom-field", BasicTypes.STRING_TYPE));

    MetacardType customType = new MetacardTypeImpl("custom-type", descriptors);
    MetacardImpl metacard = new MetacardImpl(customType);
    metacard.setAttribute(new AttributeImpl("custom-field", "custom value"));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    assertThat(properties.get("custom-field"), is("custom value"));
    assertThat(properties.get(MetacardType.METACARD_TYPE), is("custom-type"));
  }

  @Test(expected = CatalogTransformerException.class)
  public void testTransformWithInvalidGeometry() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setLocation("INVALID WKT");
    metacard.setTitle("Test");

    transformer.transform(metacard, null);
  }

  @Test
  public void testTransformWithNullAttributeValue() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("nullable", BasicTypes.STRING_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setAttribute(new AttributeImpl("nullable", (Serializable) null));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    // Null values should not be included
    assertThat(properties.containsKey("nullable"), is(false));
  }

  @Test
  public void testTransformBinaryDataBase64Encoding() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(createDescriptor("data", BasicTypes.BINARY_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    byte[] binaryData = new byte[] {1, 2, 3, 4, 5};
    metacard.setAttribute(new AttributeImpl("data", binaryData));

    BinaryContent content = transformer.transform(metacard, null);
    String jsonText = new String(content.getByteArray());
    JSONObject json = (JSONObject) PARSER.parse(jsonText);

    Map properties = (Map) json.get("properties");
    String base64 = (String) properties.get("data");
    assertThat(base64, is(notNullValue()));
    // Verify it's base64 encoded
    assertThat(base64, is("AQIDBAU="));
  }

  private AttributeDescriptor createDescriptor(
      String name, ddf.catalog.data.AttributeType<?> type) {
    return new AttributeDescriptorImpl(name, false, false, false, false, type);
  }
}
