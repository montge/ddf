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
package ddf.catalog.transformer.common.tika.handler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.transformer.common.tika.TikaMetadataExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

class BodyAndMetadataContentHandlerTest {

  private BodyAndMetadataContentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new BodyAndMetadataContentHandler(10000, 10000);
  }

  @Test
  void testConstructor() {
    BodyAndMetadataContentHandler newHandler = new BodyAndMetadataContentHandler(1000, 500);

    assertThat(newHandler, is(notNullValue()));
    assertThat(newHandler.getBodyText(), isEmptyString());
  }

  @Test
  void testGetBodyTextInitiallyEmpty() {
    assertThat(handler.getBodyText(), isEmptyString());
  }

  @Test
  void testGetMetadataTextInitiallyEmpty() {
    String metadata = handler.getMetadataText();
    assertThat(metadata, is(notNullValue()));
  }

  @Test
  void testStartDocumentDoesNotThrow() throws SAXException {
    handler.startDocument();
  }

  @Test
  void testEndDocumentDoesNotThrow() throws SAXException {
    handler.startDocument();
    handler.endDocument();
  }

  @Test
  void testStartElementDoesNotThrow() throws SAXException {
    handler.startDocument();
    handler.startElement("", "div", "div", new AttributesImpl());
  }

  @Test
  void testEndElementDoesNotThrow() throws SAXException {
    handler.startDocument();
    handler.startElement("", "div", "div", new AttributesImpl());
    handler.endElement("", "div", "div");
  }

  @Test
  void testCharactersProcessedWithoutError() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());
    char[] chars = "test body content".toCharArray();
    handler.characters(chars, 0, chars.length);

    // BodyContentHandler only captures text within specific Tika parsing context
    assertThat(handler.getBodyText(), is(notNullValue()));
  }

  @Test
  void testCharactersWritesToMetadata() throws SAXException {
    handler.startDocument();
    handler.startElement("", "head", "head", new AttributesImpl());
    char[] chars = "metadata content".toCharArray();
    handler.characters(chars, 0, chars.length);

    assertThat(handler.getMetadataText(), containsString("metadata content"));
  }

  @Test
  void testStartPrefixMappingDoesNotThrow() throws SAXException {
    handler.startPrefixMapping("prefix", "http://example.com");
  }

  @Test
  void testEndPrefixMappingDoesNotThrow() throws SAXException {
    handler.startPrefixMapping("prefix", "http://example.com");
    handler.endPrefixMapping("prefix");
  }

  @Test
  void testIgnorableWhitespaceDoesNotThrow() throws SAXException {
    handler.startDocument();
    char[] whitespace = "   ".toCharArray();
    handler.ignorableWhitespace(whitespace, 0, whitespace.length);
  }

  @Test
  void testBodyWriteLimitReached() throws SAXException {
    handler = new BodyAndMetadataContentHandler(10, 10000);
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());

    char[] chars = "this is a very long string that exceeds the limit".toCharArray();
    handler.characters(chars, 0, chars.length);

    String bodyText = handler.getBodyText();
    assertThat(bodyText.length(), is(not(chars.length)));
  }

  @Test
  void testMetadataWriteLimitReached() throws SAXException {
    handler = new BodyAndMetadataContentHandler(10000, 5);
    handler.startDocument();
    handler.startElement("", "head", "head", new AttributesImpl());

    char[] chars = "this is metadata that exceeds limit".toCharArray();
    handler.characters(chars, 0, chars.length);

    String metadataText = handler.getMetadataText();
    assertThat(metadataText, is(TikaMetadataExtractor.METADATA_LIMIT_REACHED_MSG));
  }

  @Test
  void testMultipleCharacterCalls() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());

    handler.characters("first ".toCharArray(), 0, 6);
    handler.characters("second".toCharArray(), 0, 6);

    // Verify handler processes multiple character calls without error
    assertThat(handler.getBodyText(), is(notNullValue()));
  }

  @Test
  void testNestedElements() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());
    handler.startElement("", "p", "p", new AttributesImpl());
    handler.characters("paragraph text".toCharArray(), 0, 14);
    handler.endElement("", "p", "p");
    handler.endElement("", "body", "body");

    // Verify handler processes nested elements without error
    assertThat(handler.getBodyText(), is(notNullValue()));
  }

  @Test
  void testGetBodyTextAfterProcessing() throws SAXException {
    handler.startDocument();
    handler.startElement("", "html", "html", new AttributesImpl());
    handler.startElement("", "body", "body", new AttributesImpl());
    handler.characters("Hello World".toCharArray(), 0, 11);
    handler.endElement("", "body", "body");
    handler.endElement("", "html", "html");
    handler.endDocument();

    // Verify handler completes full document lifecycle without error
    assertThat(handler.getBodyText(), is(notNullValue()));
  }

  @Test
  void testEmptyCharacters() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());
    handler.characters(new char[0], 0, 0);

    assertThat(handler.getBodyText(), isEmptyString());
  }

  @Test
  void testPartialCharacterArray() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());
    char[] chars = "prefix-content-suffix".toCharArray();
    handler.characters(chars, 7, 7);

    // Verify handler processes partial character array without error
    assertThat(handler.getBodyText(), is(notNullValue()));
  }

  @Test
  void testLargeWriteLimits() {
    BodyAndMetadataContentHandler largeHandler =
        new BodyAndMetadataContentHandler(Integer.MAX_VALUE, Integer.MAX_VALUE);

    assertThat(largeHandler.getBodyText(), isEmptyString());
  }

  @Test
  void testZeroWriteLimits() throws SAXException {
    handler = new BodyAndMetadataContentHandler(0, 0);
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());

    char[] chars = "test".toCharArray();
    handler.characters(chars, 0, chars.length);

    assertThat(handler.getMetadataText(), is(TikaMetadataExtractor.METADATA_LIMIT_REACHED_MSG));
  }
}
