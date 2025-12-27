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
import static org.hamcrest.Matchers.not;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

class XmlMetadataContentHandlerTest {

  private static final String UTF8 = StandardCharsets.UTF_8.toString();
  private XmlMetadataContentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new XmlMetadataContentHandler(UTF8, 1000);
  }

  @Test
  void testConstructor() {
    XmlMetadataContentHandler newHandler = new XmlMetadataContentHandler(UTF8, 500);

    assertThat(newHandler.isWriteLimitReached(), is(false));
  }

  @Test
  void testOkToWriteReturnsTrueWhenUnderLimit() {
    assertThat(handler.okToWrite(100), is(true));
  }

  @Test
  void testOkToWriteReturnsFalseWhenOverLimit() {
    handler = new XmlMetadataContentHandler(UTF8, 50);

    assertThat(handler.okToWrite(100), is(false));
  }

  @Test
  void testOkToWriteWithUnlimitedWriteLimit() {
    handler = new XmlMetadataContentHandler(UTF8, -1);

    assertThat(handler.okToWrite(1000000), is(true));
  }

  @Test
  void testIsWriteLimitReachedInitiallyFalse() {
    assertThat(handler.isWriteLimitReached(), is(false));
  }

  @Test
  void testIsWriteLimitReachedAfterExceedingLimit() {
    handler = new XmlMetadataContentHandler(UTF8, 10);
    handler.okToWrite(20);

    assertThat(handler.isWriteLimitReached(), is(true));
  }

  @Test
  void testCharactersWritesContent() throws SAXException {
    handler.startDocument();
    char[] chars = "test content".toCharArray();
    handler.characters(chars, 0, chars.length);

    String result = handler.toString();
    assertThat(result, containsString("test content"));
  }

  @Test
  void testCharactersIgnoredWhenInBody() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());
    char[] chars = "body content".toCharArray();
    handler.characters(chars, 0, chars.length);
    handler.endElement("", "body", "body");

    String result = handler.toString();
    assertThat(result, not(containsString("body content")));
  }

  @Test
  void testStartElementOutsideBody() throws SAXException {
    handler.startDocument();
    handler.startElement("", "head", "head", new AttributesImpl());

    String result = handler.toString();
    assertThat(result, containsString("head"));
  }

  @Test
  void testStartElementBodyTagSetsInBody() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());

    char[] chars = "should not appear".toCharArray();
    handler.characters(chars, 0, chars.length);

    String result = handler.toString();
    assertThat(result, not(containsString("should not appear")));
  }

  @Test
  void testEndElementBodyTagClearsInBody() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());
    handler.endElement("", "body", "body");

    char[] chars = "after body".toCharArray();
    handler.characters(chars, 0, chars.length);

    String result = handler.toString();
    assertThat(result, containsString("after body"));
  }

  @Test
  void testIgnorableWhitespaceWritesContent() throws SAXException {
    handler.startDocument();
    char[] whitespace = "   ".toCharArray();
    handler.ignorableWhitespace(whitespace, 0, whitespace.length);

    assertThat(handler.isWriteLimitReached(), is(false));
  }

  @Test
  void testIgnorableWhitespaceIgnoredWhenInBody() throws SAXException {
    handler.startDocument();
    handler.startElement("", "body", "body", new AttributesImpl());

    char[] whitespace = "   ".toCharArray();
    handler.ignorableWhitespace(whitespace, 0, whitespace.length);

    assertThat(handler.isWriteLimitReached(), is(false));
  }

  @Test
  void testWriteLimitEnforcedOnCharacters() throws SAXException {
    handler = new XmlMetadataContentHandler(UTF8, 5);
    handler.startDocument();

    char[] chars = "this is a long string".toCharArray();
    handler.characters(chars, 0, chars.length);

    assertThat(handler.isWriteLimitReached(), is(true));
  }

  @Test
  void testBodyTagCaseInsensitive() throws SAXException {
    handler.startDocument();
    handler.startElement("", "BODY", "BODY", new AttributesImpl());

    char[] chars = "should not appear".toCharArray();
    handler.characters(chars, 0, chars.length);

    String result = handler.toString();
    assertThat(result, not(containsString("should not appear")));
  }

  @Test
  void testEndElementOutsideBody() throws SAXException {
    handler.startDocument();
    handler.startElement("", "head", "head", new AttributesImpl());
    handler.endElement("", "head", "head");

    String result = handler.toString();
    assertThat(result, containsString("head"));
  }

  @Test
  void testToStringReturnsContent() throws SAXException {
    handler.startDocument();
    handler.startElement("", "meta", "meta", new AttributesImpl());
    handler.endElement("", "meta", "meta");
    handler.endDocument();

    String result = handler.toString();
    assertThat(result, containsString("meta"));
  }

  @Test
  void testZeroWriteLimit() {
    handler = new XmlMetadataContentHandler(UTF8, 0);

    assertThat(handler.okToWrite(1), is(false));
    assertThat(handler.isWriteLimitReached(), is(true));
  }
}
