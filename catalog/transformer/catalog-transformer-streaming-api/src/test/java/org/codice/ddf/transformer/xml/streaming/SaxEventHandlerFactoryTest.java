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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.AttributeImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SaxEventHandlerFactoryTest {

  private TestSaxEventHandlerFactory factory;

  @Before
  public void setUp() {
    factory = new TestSaxEventHandlerFactory();
  }

  @Test
  public void testGetNewSaxEventHandler() {
    SaxEventHandler handler = factory.getNewSaxEventHandler();
    assertThat(handler, is(notNullValue()));
  }

  @Test
  public void testGetNewSaxEventHandlerReturnsNewInstanceEachTime() {
    SaxEventHandler handler1 = factory.getNewSaxEventHandler();
    SaxEventHandler handler2 = factory.getNewSaxEventHandler();

    assertThat(handler1, is(notNullValue()));
    assertThat(handler2, is(notNullValue()));
    assertThat(handler1, is(not(sameInstance(handler2))));
  }

  @Test
  public void testGetNewSaxEventHandlerReturnsFunctionalHandler() throws Exception {
    SaxEventHandler handler = factory.getNewSaxEventHandler();

    // Test that handler implements all ContentHandler methods without throwing exceptions
    handler.setDocumentLocator(mock(Locator.class));
    handler.startDocument();
    handler.startPrefixMapping("prefix", "http://example.com/ns");
    handler.startElement("http://example.com/ns", "localName", "qName", mock(Attributes.class));
    handler.characters("test".toCharArray(), 0, 4);
    handler.endElement("http://example.com/ns", "localName", "qName");
    handler.endPrefixMapping("prefix");
    handler.endDocument();
    handler.ignorableWhitespace("   ".toCharArray(), 0, 3);
    handler.processingInstruction("target", "data");
    handler.skippedEntity("entity");

    // Verify handler has attributes and descriptors
    assertThat(handler.getAttributes(), is(notNullValue()));
    assertThat(handler.getSupportedAttributeDescriptors(), is(notNullValue()));
  }

  @Test
  public void testFactoryImplementsDescribable() {
    // Factory extends Describable, so it should have these methods
    assertThat(factory.getVersion(), is(notNullValue()));
    assertThat(factory.getId(), is(notNullValue()));
    assertThat(factory.getTitle(), is(notNullValue()));
    assertThat(factory.getDescription(), is(notNullValue()));
    assertThat(factory.getOrganization(), is(notNullValue()));
  }

  @Test
  public void testFactoryDescribableValues() {
    assertThat(factory.getVersion(), is(equalTo("1.0")));
    assertThat(factory.getId(), is(equalTo("test-factory")));
    assertThat(factory.getTitle(), is(equalTo("Test Factory")));
    assertThat(factory.getDescription(), is(equalTo("Test factory description")));
    assertThat(factory.getOrganization(), is(equalTo("Test Organization")));
  }

  @Test
  public void testMultipleHandlersHaveIndependentState() {
    SaxEventHandler handler1 = factory.getNewSaxEventHandler();
    SaxEventHandler handler2 = factory.getNewSaxEventHandler();

    // Add attribute to first handler via test implementation
    if (handler1 instanceof TestSaxEventHandler) {
      ((TestSaxEventHandler) handler1).addAttribute(new AttributeImpl("test", "value1"));
    }

    // Verify handlers have independent state
    assertThat(handler1.getAttributes().size(), is(1));
    assertThat(handler2.getAttributes().size(), is(0));
  }

  /** Test implementation of SaxEventHandlerFactory for testing purposes */
  private static class TestSaxEventHandlerFactory implements SaxEventHandlerFactory {

    @Override
    public SaxEventHandler getNewSaxEventHandler() {
      return new TestSaxEventHandler();
    }

    @Override
    public String getVersion() {
      return "1.0";
    }

    @Override
    public String getId() {
      return "test-factory";
    }

    @Override
    public String getTitle() {
      return "Test Factory";
    }

    @Override
    public String getDescription() {
      return "Test factory description";
    }

    @Override
    public String getOrganization() {
      return "Test Organization";
    }
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
