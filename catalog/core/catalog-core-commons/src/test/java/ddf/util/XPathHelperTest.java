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
package ddf.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XPathHelperTest {

  private static final String SIMPLE_XML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><child attr=\"value\">text</child></root>";

  private static final String NAMESPACE_XML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
          + "<root xmlns:ns=\"http://example.com/ns\">"
          + "<ns:child>namespaced text</ns:child>"
          + "</root>";

  private XPathHelper helper;

  @Before
  public void setUp() throws Exception {
    helper = new XPathHelper(SIMPLE_XML);
  }

  @Test
  public void testDefaultConstructor() {
    XPathHelper defaultHelper = new XPathHelper();
    assertThat(defaultHelper, is(notNullValue()));
  }

  @Test
  public void testStringConstructor() throws Exception {
    XPathHelper stringHelper = new XPathHelper(SIMPLE_XML);
    assertThat(stringHelper, is(notNullValue()));
    assertThat(stringHelper.getDocument(), is(notNullValue()));
  }

  @Test
  public void testDocumentConstructor() throws Exception {
    Document doc = createDocument(SIMPLE_XML);
    XPathHelper docHelper = new XPathHelper(doc);
    assertThat(docHelper, is(notNullValue()));
    assertThat(docHelper.getDocument(), is(notNullValue()));
  }

  @Test
  public void testDocumentConstructorWithCloneAndNormalizeTrue() throws Exception {
    Document doc = createDocument(SIMPLE_XML);
    XPathHelper docHelper = new XPathHelper(doc, true);
    assertThat(docHelper, is(notNullValue()));
    assertThat(docHelper.getDocument(), is(notNullValue()));
  }

  @Test
  public void testDocumentConstructorWithCloneAndNormalizeFalse() throws Exception {
    Document doc = createDocument(SIMPLE_XML);
    XPathHelper docHelper = new XPathHelper(doc, false);
    assertThat(docHelper, is(notNullValue()));
    assertThat(docHelper.getDocument(), is(doc));
  }

  @Test
  public void testGetDocument() throws Exception {
    Document doc = helper.getDocument();
    assertThat(doc, is(notNullValue()));
    assertThat(doc.getDocumentElement().getNodeName(), is("root"));
  }

  @Test
  public void testPrintWithNode() throws Exception {
    Document doc = helper.getDocument();
    Node rootNode = doc.getDocumentElement();
    String result = XPathHelper.print(rootNode);
    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("root"));
    assertThat(result, containsString("child"));
  }

  @Test
  public void testPrintWithNullNode() {
    String result = XPathHelper.print(null);
    assertThat(result, is(nullValue()));
  }

  @Test
  public void testPrintWithEncoding() throws Exception {
    Document doc = helper.getDocument();
    Node rootNode = doc.getDocumentElement();
    String result = XPathHelper.print(rootNode, "UTF-8");
    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("root"));
  }

  @Test
  public void testPrintWithDocument() throws Exception {
    Document doc = helper.getDocument();
    String result = XPathHelper.print(doc);
    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("root"));
  }

  @Test
  public void testPrintWithAttributeNode() throws Exception {
    Document doc = helper.getDocument();
    Node childNode = doc.getElementsByTagName("child").item(0);
    Node attrNode = childNode.getAttributes().getNamedItem("attr");
    String result = XPathHelper.print(attrNode);
    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("value"));
  }

  @Test
  public void testXmlToString() throws Exception {
    Document doc = helper.getDocument();
    String result = XPathHelper.xmlToString(doc);
    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("root"));
  }

  @Test
  public void testPrintWithXmlDeclarationAndIndent() throws Exception {
    String result = helper.print("yes", "yes");
    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("root"));
  }

  @Test
  public void testPrintWithNoXmlDeclaration() throws Exception {
    String result = helper.print("no", "no");
    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("root"));
  }

  @Test
  public void testEvaluateWithNamespaceContext() throws Exception {
    Map<String, String> namespaces = new HashMap<>();
    namespaces.put("ns", "http://example.com/ns");
    NamespaceMapImpl nsContext = new NamespaceMapImpl(namespaces);

    XPathHelper nsHelper = new XPathHelper(NAMESPACE_XML);
    String result = nsHelper.evaluate("//ns:child/text()", nsContext);
    assertThat(result, is(notNullValue()));
    assertThat(result, is("namespaced text"));
  }

  @Test
  public void testEvaluateWithReturnType() throws Exception {
    Map<String, String> namespaces = new HashMap<>();
    NamespaceMapImpl nsContext = new NamespaceMapImpl(namespaces);
    Object result = helper.evaluate("/root/child/text()", XPathConstants.STRING, nsContext);
    assertThat(result, is(notNullValue()));
    assertThat(result.toString(), is("text"));
  }

  @Test
  public void testEvaluateReturnsNode() throws Exception {
    Map<String, String> namespaces = new HashMap<>();
    NamespaceMapImpl nsContext = new NamespaceMapImpl(namespaces);
    Object result = helper.evaluate("/root/child", XPathConstants.NODE, nsContext);
    assertThat(result, is(notNullValue()));
    assertThat(result instanceof Node, is(true));
  }

  private Document createDocument(String xml)
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
  }
}
