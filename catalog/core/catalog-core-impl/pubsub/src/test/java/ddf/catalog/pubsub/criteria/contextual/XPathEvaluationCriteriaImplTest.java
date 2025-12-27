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
package ddf.catalog.pubsub.criteria.contextual;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

class XPathEvaluationCriteriaImplTest {

  private static final String TEST_XPATH = "/root/element";
  private static final String SAMPLE_XML = "<root><element>value</element></root>";

  private Document document;

  @BeforeEach
  void setUp() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    document = builder.parse(new InputSource(new StringReader(SAMPLE_XML)));
  }

  @Test
  void testConstructor() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, TEST_XPATH);

    assertThat(criteria, is(notNullValue()));
  }

  @Test
  void testImplementsInterface() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, TEST_XPATH);

    assertThat(criteria, instanceOf(XPathEvaluationCriteria.class));
  }

  @Test
  void testGetDocument() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, TEST_XPATH);

    assertThat(criteria.getDocument(), is(sameInstance(document)));
  }

  @Test
  void testGetXPath() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, TEST_XPATH);

    assertThat(criteria.getXPath(), is(TEST_XPATH));
  }

  @Test
  void testWithNullDocument() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(null, TEST_XPATH);

    assertThat(criteria.getDocument(), is(nullValue()));
    assertThat(criteria.getXPath(), is(TEST_XPATH));
  }

  @Test
  void testWithNullXPath() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, null);

    assertThat(criteria.getDocument(), is(sameInstance(document)));
    assertThat(criteria.getXPath(), is(nullValue()));
  }

  @Test
  void testWithBothNull() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(null, null);

    assertThat(criteria.getDocument(), is(nullValue()));
    assertThat(criteria.getXPath(), is(nullValue()));
  }

  @Test
  void testWithEmptyXPath() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, "");

    assertThat(criteria.getXPath(), is(""));
  }

  @Test
  void testWithComplexXPath() {
    String complexXPath = "//element[@attribute='value']/child[position()=1]";
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, complexXPath);

    assertThat(criteria.getXPath(), is(complexXPath));
  }

  @Test
  void testWithNamespaceXPath() {
    String nsXPath = "/ns:root/ns:element";
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, nsXPath);

    assertThat(criteria.getXPath(), is(nsXPath));
  }

  @Test
  void testDocumentIsSameReference() {
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, TEST_XPATH);

    Document retrieved1 = criteria.getDocument();
    Document retrieved2 = criteria.getDocument();

    assertThat(retrieved1, is(sameInstance(retrieved2)));
  }

  @Test
  void testMultipleInstancesAreIndependent() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document2 =
        builder.parse(new InputSource(new StringReader("<other><data>text</data></other>")));

    XPathEvaluationCriteriaImpl criteria1 =
        new XPathEvaluationCriteriaImpl(document, "/root/element");
    XPathEvaluationCriteriaImpl criteria2 =
        new XPathEvaluationCriteriaImpl(document2, "/other/data");

    assertThat(criteria1.getDocument(), is(sameInstance(document)));
    assertThat(criteria2.getDocument(), is(sameInstance(document2)));
    assertThat(criteria1.getXPath(), is("/root/element"));
    assertThat(criteria2.getXPath(), is("/other/data"));
  }

  @Test
  void testWithXPathContainingPredicates() {
    String predicateXPath = "/root/element[text()='value' and @attr='test']";
    XPathEvaluationCriteriaImpl criteria =
        new XPathEvaluationCriteriaImpl(document, predicateXPath);

    assertThat(criteria.getXPath(), is(predicateXPath));
  }

  @Test
  void testWithXPathContainingFunctions() {
    String functionXPath = "//element[contains(text(), 'partial')]";
    XPathEvaluationCriteriaImpl criteria = new XPathEvaluationCriteriaImpl(document, functionXPath);

    assertThat(criteria.getXPath(), is(functionXPath));
  }
}
