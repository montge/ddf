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
package ddf.catalog.transformer.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.transformer.api.PrintWriterProvider;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.codice.ddf.parser.Parser;
import org.codice.ddf.parser.ParserConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetacardMarshallerImplTest {

  @Mock private Parser parser;

  @Mock private PrintWriterProvider writerProvider;

  @Mock private ParserConfigurator parserConfigurator;

  private MetacardMarshallerImpl marshaller;

  private EscapingPrintWriter printWriter;

  @Before
  public void setUp() {
    when(parser.configureParser(any(), any())).thenReturn(parserConfigurator);
    when(parserConfigurator.setHandler(any())).thenReturn(parserConfigurator);

    printWriter = new EscapingPrintWriter(new java.io.StringWriter());
    when(writerProvider.build(Metacard.class)).thenReturn(printWriter);

    marshaller = new MetacardMarshallerImpl(parser, writerProvider);
  }

  @Test
  public void testMarshalBasicMetacard() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id-123");
    metacard.setTitle("Test Title");
    metacard.setSourceId("test-source");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("test-id-123"));
    assertThat(result, containsString("Test Title"));
    assertThat(result, containsString("test-source"));
    assertThat(result, containsString("metacard"));
  }

  @Test
  public void testMarshalWithXmlDeclaration() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    String result = marshaller.marshal(metacard, Collections.emptyMap());

    // Default should include XML declaration
    assertThat(result, containsString("<?xml"));
  }

  @Test
  public void testMarshalOmitXmlDeclaration() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put(MetacardMarshallerImpl.OMIT_XML_DECL, Boolean.TRUE);

    String result = marshaller.marshal(metacard, arguments);

    assertThat(result, not(containsString("<?xml")));
    assertThat(result, containsString("metacard"));
  }

  @Test
  public void testMarshalWithStringAttribute() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setAttribute("description", "Test description");

    String result = marshaller.marshal(metacard);

    assertThat(result, containsString("description"));
    assertThat(result, containsString("Test description"));
  }

  @Test
  public void testMarshalWithDateAttribute() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    Date now = new Date();
    metacard.setCreatedDate(now);

    String result = marshaller.marshal(metacard);

    assertThat(result, containsString("created"));
    // Should contain date in ISO format
    assertThat(result, containsString("T"));
  }

  @Test
  public void testMarshalWithNumericAttributes() throws Exception {
    Set<ddf.catalog.data.AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("count", true, true, true, false, BasicTypes.INTEGER_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("price", true, true, true, false, BasicTypes.DOUBLE_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("flag", true, true, true, false, BasicTypes.BOOLEAN_TYPE));

    MetacardType type = new MetacardTypeImpl("test-type", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute("count", 42);
    metacard.setAttribute("price", 99.99);
    metacard.setAttribute("flag", true);

    String result = marshaller.marshal(metacard);

    assertThat(result, containsString("count"));
    assertThat(result, containsString("42"));
    assertThat(result, containsString("price"));
    assertThat(result, containsString("99.99"));
    assertThat(result, containsString("flag"));
    assertThat(result, containsString("true"));
  }

  @Test
  public void testMarshalWithBinaryAttribute() throws Exception {
    Set<ddf.catalog.data.AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl("data", true, true, true, false, BasicTypes.BINARY_TYPE));

    MetacardType type = new MetacardTypeImpl("test-type", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    byte[] binaryData = new byte[] {1, 2, 3, 4, 5};
    metacard.setAttribute("data", binaryData);

    String result = marshaller.marshal(metacard);

    // Binary data should be Base64 encoded
    assertThat(result, containsString("data"));
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testMarshalWithNullId() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test Title");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("Test Title"));
  }

  @Test
  public void testMarshalWithBlankSourceId() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setSourceId("");

    String result = marshaller.marshal(metacard);

    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("test-id"));
  }

  @Test
  public void testMarshalWithNullArguments() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    String result = marshaller.marshal(metacard, null);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testMarshalWithCustomMetacardType() throws Exception {
    Set<ddf.catalog.data.AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.add(
        new AttributeDescriptorImpl(
            "custom-field", true, true, true, false, BasicTypes.STRING_TYPE));

    MetacardType type = new MetacardTypeImpl("custom-type", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setId("test-id");
    metacard.setAttribute("custom-field", "custom value");

    String result = marshaller.marshal(metacard);

    assertThat(result, containsString("custom-type"));
    assertThat(result, containsString("custom-field"));
    assertThat(result, containsString("custom value"));
  }

  @Test
  public void testMarshalSkipsIdAttribute() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id-456");

    String result = marshaller.marshal(metacard);

    // The id should appear as an attribute on the metacard element, not as a separate element
    assertThat(result, containsString("gml:id"));
    assertThat(result, containsString("test-id-456"));
  }
}
