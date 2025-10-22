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
package ddf.catalog.transform.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.BinaryContentImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.codice.ddf.parser.Parser;
import org.codice.ddf.parser.ParserConfigurator;
import org.codice.ddf.parser.ParserException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.xmlpull.v1.XmlPullParserException;

@RunWith(MockitoJUnitRunner.class)
public class MetacardMarshallerImplTest {

  @Mock private Parser mockParser;

  @Mock private ParserConfigurator mockConfigurator;

  private MetacardMarshallerImpl marshaller;

  private PrintWriterProviderImpl writerProvider;

  @Before
  public void setUp() {
    writerProvider = new PrintWriterProviderImpl();
    when(mockParser.configureParser(anyList(), any(ClassLoader.class)))
        .thenReturn(mockConfigurator);
    when(mockConfigurator.setHandler(any())).thenReturn(mockConfigurator);
    marshaller = new MetacardMarshallerImpl(mockParser, writerProvider);
  }

  @Test
  public void testMarshalBasicMetacard()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id-123");
    metacard.setTitle("Test Title");
    metacard.setSourceId("test-source");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<?xml version='1.0' encoding='UTF-8'?>"));
    assertThat(result, containsString("<metacard"));
    assertThat(result, containsString("gml:id=\"test-id-123\""));
    assertThat(result, containsString("<source>test-source</source>"));
    assertThat(result, containsString("<type>ddf.metacard</type>"));
  }

  @Test
  public void testMarshalMetacardWithoutId()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test Title");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<metacard"));
    assertThat(result, not(containsString("gml:id=")));
  }

  @Test
  public void testMarshalMetacardWithoutSource()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setTitle("Test Title");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, not(containsString("<source>")));
  }

  @Test
  public void testMarshalWithOmitXmlDeclaration()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setTitle("Test Title");

    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put(MetacardMarshallerImpl.OMIT_XML_DECL, Boolean.TRUE);

    String result = marshaller.marshal(metacard, arguments);

    assertThat(result, is(notNullValue()));
    assertThat(result, not(containsString("<?xml version")));
    assertThat(result, containsString("<metacard"));
  }

  @Test
  public void testMarshalWithOmitXmlDeclarationFalse()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setTitle("Test Title");

    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put(MetacardMarshallerImpl.OMIT_XML_DECL, Boolean.FALSE);

    String result = marshaller.marshal(metacard, arguments);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<?xml version='1.0' encoding='UTF-8'?>"));
  }

  @Test
  public void testMarshalMetacardWithStringAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-string", false, false, false, false, BasicTypes.STRING_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-string", "test value"));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<string name=\"custom-string\">"));
    assertThat(result, containsString("<value>test value</value>"));
  }

  @Test
  public void testMarshalMetacardWithIntegerAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-int", false, false, false, false, BasicTypes.INTEGER_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-int", 42));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<int name=\"custom-int\">"));
    assertThat(result, containsString("<value>42</value>"));
  }

  @Test
  public void testMarshalMetacardWithBooleanAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-bool", false, false, false, false, BasicTypes.BOOLEAN_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-bool", true));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<boolean name=\"custom-bool\">"));
    assertThat(result, containsString("<value>true</value>"));
  }

  @Test
  public void testMarshalMetacardWithDateAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-date", false, false, false, false, BasicTypes.DATE_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    Date testDate = new Date(1609459200000L); // 2021-01-01T00:00:00.000Z
    metacard.setAttribute(new AttributeImpl("custom-date", testDate));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<dateTime name=\"custom-date\">"));
    assertThat(result, containsString("<value>2021-01-01T00:00:00.000+0000</value>"));
  }

  @Test
  public void testMarshalMetacardWithDoubleAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-double", false, false, false, false, BasicTypes.DOUBLE_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-double", 3.14159));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<double name=\"custom-double\">"));
    assertThat(result, containsString("<value>3.14159</value>"));
  }

  @Test
  public void testMarshalMetacardWithFloatAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-float", false, false, false, false, BasicTypes.FLOAT_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-float", 2.5f));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<float name=\"custom-float\">"));
    assertThat(result, containsString("<value>2.5</value>"));
  }

  @Test
  public void testMarshalMetacardWithLongAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-long", false, false, false, false, BasicTypes.LONG_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-long", 9876543210L));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<long name=\"custom-long\">"));
    assertThat(result, containsString("<value>9876543210</value>"));
  }

  @Test
  public void testMarshalMetacardWithShortAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-short", false, false, false, false, BasicTypes.SHORT_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-short", (short) 123));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<short name=\"custom-short\">"));
    assertThat(result, containsString("<value>123</value>"));
  }

  @Test
  public void testMarshalMetacardWithBinaryAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-binary", false, false, false, false, BasicTypes.BINARY_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    byte[] binaryData = new byte[] {1, 2, 3, 4, 5};
    metacard.setAttribute(new AttributeImpl("custom-binary", binaryData));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<base64Binary name=\"custom-binary\">"));
    assertThat(result, containsString("<value>AQIDBAU=</value>"));
  }

  @Test
  public void testMarshalMetacardWithXmlAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("custom-xml", false, false, false, false, BasicTypes.XML_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-xml", "<?xml version=\"1.0\"?><test/>"));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<stringxml name=\"custom-xml\">"));
    assertThat(result, containsString("<test/>"));
    assertThat(result, not(containsString("<?xml version=\"1.0\"?>")));
  }

  @Test
  public void testMarshalMetacardWithMultipleValues()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "multi-string", true, false, false, false, BasicTypes.STRING_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(
        new AttributeImpl("multi-string", java.util.Arrays.asList("value1", "value2", "value3")));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<string name=\"multi-string\">"));
    assertThat(result, containsString("<value>value1</value>"));
    assertThat(result, containsString("<value>value2</value>"));
    assertThat(result, containsString("<value>value3</value>"));
  }

  @Test
  public void testMarshalMetacardWithNullAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "null-attr", false, false, false, false, BasicTypes.STRING_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, not(containsString("null-attr")));
  }

  @Test
  public void testMarshalMetacardSkipsIdAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    // ID should be in gml:id attribute, not as a separate element
    assertThat(result, containsString("gml:id=\"test-id\""));
    assertThat(result, not(containsString("<string name=\"id\">")));
  }

  @Test
  public void testMarshalWithEmptyArguments()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    String result = marshaller.marshal(metacard, Collections.emptyMap());

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<?xml version='1.0' encoding='UTF-8'?>"));
  }

  @Test
  public void testMarshalWithNullArguments()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    String result = marshaller.marshal(metacard, null);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<?xml version='1.0' encoding='UTF-8'?>"));
  }

  @Test
  public void testMarshalMetacardWithGeometryAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException, ParserException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("custom-geo", false, false, false, false, BasicTypes.GEO_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute(new AttributeImpl("custom-geo", "POINT (1 2)"));

    // Mock the geometry transformation
    String gmlXml =
        "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\" gml:id=\"custom-geo\"><gml:pos>2.0 1.0</gml:pos></gml:Point>";
    BinaryContent mockBinaryContent =
        new BinaryContentImpl(
            new ByteArrayInputStream(gmlXml.getBytes(StandardCharsets.UTF_8)),
            AbstractXmlTransformer.MIME_TYPE);

    when(mockParser.marshal(any(), any(), any())).thenAnswer(invocation -> null);

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    // Geometry attributes are handled specially and don't follow standard pattern
    assertThat(result, not(containsString("<geometry name=\"custom-geo\">")));
  }

  @Test
  public void testMarshalMetacardWithCustomTypeName()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    MetacardType type = new MetacardTypeImpl("my-custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<type>my-custom-type</type>"));
  }

  @Test
  public void testMarshalMetacardWithBlankTypeName()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    MetacardType type = new MetacardTypeImpl("", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<type>ddf.metacard</type>"));
  }

  @Test
  public void testMarshalMetacardWithObjectAttribute()
      throws XmlPullParserException, IOException, CatalogTransformerException {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-object", false, false, false, false, BasicTypes.OBJECT_TYPE));
    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);

    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    HashMap<String, String> testObject = new HashMap<>();
    testObject.put("key1", "value1");
    metacard.setAttribute(new AttributeImpl("custom-object", testObject));

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("<object name=\"custom-object\">"));
    // Object should be base64 encoded
    assertThat(result, containsString("<value>"));
  }
}
