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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
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

public class SaxEventHandlerTest {

  private TestSaxEventHandler handler;

  @BeforeEach
  public void setUp() {
    handler = new TestSaxEventHandler();
  }

  @Test
  public void testGetAttributes() {
    List<Attribute> attributes = handler.getAttributes();
    assertThat(attributes, is(notNullValue()));
  }

  @Test
  public void testGetAttributesReturnsEmptyListInitially() {
    List<Attribute> attributes = handler.getAttributes();
    assertThat(attributes, is(empty()));
  }

  @Test
  public void testGetAttributesReturnsAddedAttributes() {
    handler.addAttribute(new AttributeImpl("test", "value"));
    List<Attribute> attributes = handler.getAttributes();
    assertThat(attributes, hasSize(1));
  }

  @Test
  public void testGetSupportedAttributeDescriptors() {
    Set<AttributeDescriptor> descriptors = handler.getSupportedAttributeDescriptors();
    assertThat(descriptors, is(notNullValue()));
  }

  @Test
  public void testGetSupportedAttributeDescriptorsReturnsEmptySetInitially() {
    Set<AttributeDescriptor> descriptors = handler.getSupportedAttributeDescriptors();
    assertThat(descriptors, is(empty()));
  }

  @Test
  public void testGetSupportedAttributeDescriptorsReturnsAddedDescriptors() {
    AttributeDescriptor descriptor = mock(AttributeDescriptor.class);
    handler.addDescriptor(descriptor);
    Set<AttributeDescriptor> descriptors = handler.getSupportedAttributeDescriptors();
    assertThat(descriptors, hasSize(1));
    assertThat(descriptors, contains(descriptor));
  }

  @Test
  public void testSetDocumentLocator() throws Exception {
    Locator locator = mock(Locator.class);
    assertDoesNotThrow(() -> handler.setDocumentLocator(locator));
  }

  @Test
  public void testStartDocument() throws Exception {
    assertDoesNotThrow(() -> handler.startDocument());
  }

  @Test
  public void testEndDocument() throws Exception {
    assertDoesNotThrow(() -> handler.endDocument());
  }

  @Test
  public void testStartPrefixMapping() throws Exception {
    assertDoesNotThrow(() -> handler.startPrefixMapping("prefix", "http://example.com/ns"));
  }

  @Test
  public void testEndPrefixMapping() throws Exception {
    assertDoesNotThrow(() -> handler.endPrefixMapping("prefix"));
  }

  @Test
  public void testStartElement() throws Exception {
    Attributes attributes = mock(Attributes.class);
    assertDoesNotThrow(
        () -> handler.startElement("http://example.com/ns", "localName", "qName", attributes));
  }

  @Test
  public void testEndElement() throws Exception {
    assertDoesNotThrow(() -> handler.endElement("http://example.com/ns", "localName", "qName"));
  }

  @Test
  public void testCharacters() throws Exception {
    char[] ch = "test".toCharArray();
    assertDoesNotThrow(() -> handler.characters(ch, 0, ch.length));
  }

  @Test
  public void testIgnorableWhitespace() throws Exception {
    char[] ch = "   ".toCharArray();
    assertDoesNotThrow(() -> handler.ignorableWhitespace(ch, 0, ch.length));
  }

  @Test
  public void testProcessingInstruction() throws Exception {
    assertDoesNotThrow(() -> handler.processingInstruction("target", "data"));
  }

  @Test
  public void testSkippedEntity() throws Exception {
    assertDoesNotThrow(() -> handler.skippedEntity("entity"));
  }

  @Test
  public void testMultipleAttributes() {
    handler.addAttribute(new AttributeImpl("attr1", "value1"));
    handler.addAttribute(new AttributeImpl("attr2", "value2"));
    handler.addAttribute(new AttributeImpl("attr3", "value3"));

    List<Attribute> attributes = handler.getAttributes();
    assertThat(attributes, hasSize(3));
  }

  @Test
  public void testMultipleDescriptors() {
    AttributeDescriptor descriptor1 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor2 = mock(AttributeDescriptor.class);
    AttributeDescriptor descriptor3 = mock(AttributeDescriptor.class);

    handler.addDescriptor(descriptor1);
    handler.addDescriptor(descriptor2);
    handler.addDescriptor(descriptor3);

    Set<AttributeDescriptor> descriptors = handler.getSupportedAttributeDescriptors();
    assertThat(descriptors, hasSize(3));
  }

  /** Test implementation of SaxEventHandler for testing purposes */
  private static class TestSaxEventHandler implements SaxEventHandler {

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
    public void setDocumentLocator(Locator locator) {}

    @Override
    public void startDocument() throws SAXException {}

    @Override
    public void endDocument() throws SAXException {}

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {}

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {}

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
        throws SAXException {}

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {}

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {}

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

    @Override
    public void processingInstruction(String target, String data) throws SAXException {}

    @Override
    public void skippedEntity(String name) throws SAXException {}
  }
}
