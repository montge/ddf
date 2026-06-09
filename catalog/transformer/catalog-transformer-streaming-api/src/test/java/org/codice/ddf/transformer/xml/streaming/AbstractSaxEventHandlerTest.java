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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.AttributeImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class AbstractSaxEventHandlerTest {

  private TestAbstractSaxEventHandler handler;

  @BeforeEach
  public void setUp() {
    handler = new TestAbstractSaxEventHandler();
  }

  @Test
  public void testSetDocumentLocatorDoesNotThrowException() {
    Locator locator = mock(Locator.class);
    assertDoesNotThrow(() -> handler.setDocumentLocator(locator));
  }

  @Test
  public void testSetDocumentLocatorWithNullDoesNotThrowException() {
    assertDoesNotThrow(() -> handler.setDocumentLocator(null));
  }

  @Test
  public void testStartDocumentDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.startDocument());
  }

  @Test
  public void testEndDocumentDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.endDocument());
  }

  @Test
  public void testStartPrefixMappingDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.startPrefixMapping("prefix", "http://example.com/ns"));
  }

  @Test
  public void testStartPrefixMappingWithNullPrefixDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.startPrefixMapping(null, "http://example.com/ns"));
  }

  @Test
  public void testStartPrefixMappingWithNullUriDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.startPrefixMapping("prefix", null));
  }

  @Test
  public void testStartPrefixMappingWithEmptyStringsDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.startPrefixMapping("", ""));
  }

  @Test
  public void testEndPrefixMappingDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.endPrefixMapping("prefix"));
  }

  @Test
  public void testEndPrefixMappingWithNullDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.endPrefixMapping(null));
  }

  @Test
  public void testEndPrefixMappingWithEmptyStringDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.endPrefixMapping(""));
  }

  @Test
  public void testIgnorableWhitespaceDoesNotThrowException() throws SAXException {
    char[] ch = "   \t\n".toCharArray();
    assertDoesNotThrow(() -> handler.ignorableWhitespace(ch, 0, ch.length));
  }

  @Test
  public void testIgnorableWhitespaceWithEmptyArrayDoesNotThrowException() throws SAXException {
    char[] ch = new char[0];
    assertDoesNotThrow(() -> handler.ignorableWhitespace(ch, 0, 0));
  }

  @Test
  public void testIgnorableWhitespaceWithPartialArrayDoesNotThrowException() throws SAXException {
    char[] ch = "   test   ".toCharArray();
    assertDoesNotThrow(() -> handler.ignorableWhitespace(ch, 3, 4));
  }

  @Test
  public void testProcessingInstructionDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(
        () ->
            handler.processingInstruction(
                "xml-stylesheet", "href=\"style.xsl\" type=\"text/xsl\""));
  }

  @Test
  public void testProcessingInstructionWithNullTargetDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.processingInstruction(null, "data"));
  }

  @Test
  public void testProcessingInstructionWithNullDataDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.processingInstruction("target", null));
  }

  @Test
  public void testProcessingInstructionWithEmptyStringsDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.processingInstruction("", ""));
  }

  @Test
  public void testSkippedEntityDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.skippedEntity("nbsp"));
  }

  @Test
  public void testSkippedEntityWithNullDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.skippedEntity(null));
  }

  @Test
  public void testSkippedEntityWithEmptyStringDoesNotThrowException() throws SAXException {
    assertDoesNotThrow(() -> handler.skippedEntity(""));
  }

  @Test
  public void testImplementsSaxEventHandlerInterface() {
    assertThat(handler instanceof SaxEventHandler, is(true));
  }

  @Test
  public void testGetAttributesIsImplemented() {
    List<Attribute> attributes = handler.getAttributes();
    assertThat(attributes, is(notNullValue()));
  }

  @Test
  public void testGetSupportedAttributeDescriptorsIsImplemented() {
    Set<AttributeDescriptor> descriptors = handler.getSupportedAttributeDescriptors();
    assertThat(descriptors, is(notNullValue()));
  }

  @Test
  public void testStartElementMustBeImplementedBySubclass() throws SAXException {
    // This test verifies that the abstract class requires startElement to be implemented
    // The test implementation provides an empty implementation
    Attributes attributes = mock(Attributes.class);
    assertDoesNotThrow(
        () -> handler.startElement("http://example.com/ns", "localName", "qName", attributes));
  }

  @Test
  public void testEndElementMustBeImplementedBySubclass() throws SAXException {
    // This test verifies that the abstract class requires endElement to be implemented
    // The test implementation provides an empty implementation
    assertDoesNotThrow(() -> handler.endElement("http://example.com/ns", "localName", "qName"));
  }

  @Test
  public void testCharactersMustBeImplementedBySubclass() throws SAXException {
    // This test verifies that the abstract class requires characters to be implemented
    // The test implementation provides an empty implementation
    char[] ch = "test".toCharArray();
    assertDoesNotThrow(() -> handler.characters(ch, 0, ch.length));
  }

  @Test
  public void testMultipleCallsToDefaultMethodsDoNotThrowException() throws SAXException {
    // Test multiple sequential calls to default implementations
    assertDoesNotThrow(
        () -> {
          handler.setDocumentLocator(mock(Locator.class));
          handler.startDocument();
          handler.startPrefixMapping("ns1", "http://example.com/ns1");
          handler.startPrefixMapping("ns2", "http://example.com/ns2");
          handler.ignorableWhitespace("   ".toCharArray(), 0, 3);
          handler.processingInstruction("pi1", "data1");
          handler.processingInstruction("pi2", "data2");
          handler.skippedEntity("entity1");
          handler.skippedEntity("entity2");
          handler.endPrefixMapping("ns1");
          handler.endPrefixMapping("ns2");
          handler.endDocument();
        });
  }

  @Test
  public void testCompleteDocumentParsingSequence() throws SAXException {
    // Test a typical SAX parsing sequence
    assertDoesNotThrow(
        () -> {
          handler.setDocumentLocator(mock(Locator.class));
          handler.startDocument();
          handler.startPrefixMapping("ns", "http://example.com/ns");
          handler.startElement("http://example.com/ns", "root", "ns:root", mock(Attributes.class));
          handler.characters("text".toCharArray(), 0, 4);
          handler.endElement("http://example.com/ns", "root", "ns:root");
          handler.endPrefixMapping("ns");
          handler.endDocument();
        });
  }

  @Test
  public void testSubclassCanAddAttributes() {
    handler.addAttribute(new AttributeImpl("title", "Test Title"));
    handler.addAttribute(new AttributeImpl("description", "Test Description"));

    List<Attribute> attributes = handler.getAttributes();
    assertThat(attributes, hasSize(2));
  }

  @Test
  public void testSubclassCanAddDescriptors() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);

    handler.addDescriptor(descriptor1);
    handler.addDescriptor(descriptor2);

    Set<AttributeDescriptor> descriptors = handler.getSupportedAttributeDescriptors();
    assertThat(descriptors, hasSize(2));
  }

  /** Test implementation of AbstractSaxEventHandler for testing purposes */
  private static class TestAbstractSaxEventHandler extends AbstractSaxEventHandler {

    private List<Attribute> attributes = new ArrayList<>();
    private Set<AttributeDescriptor> descriptors = new HashSet<>();

    public void addAttribute(Attribute attribute) {
      attributes.add(attribute);
    }

    public void addDescriptor(AttributeDescriptor descriptor) {
      descriptors.add(descriptor);
    }

    @Override
    public List<Attribute> getAttributes() {
      return attributes;
    }

    @Override
    public Set<AttributeDescriptor> getSupportedAttributeDescriptors() {
      return descriptors;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
        throws SAXException {
      // Empty implementation for testing
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      // Empty implementation for testing
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      // Empty implementation for testing
    }
  }
}
