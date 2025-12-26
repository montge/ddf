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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.junit.jupiter.api.Test;

class XPathCacheTest {

  @Test
  void testGetXPathReturnsNonNull() {
    XPath xPath = XPathCache.getXPath();

    assertThat(xPath, is(notNullValue()));
  }

  @Test
  void testGetXPathReturnsSameInstance() {
    XPath xPath1 = XPathCache.getXPath();
    XPath xPath2 = XPathCache.getXPath();

    assertThat(xPath1, is(sameInstance(xPath2)));
  }

  @Test
  void testGetCompiledExpressionReturnsValidExpression() throws Exception {
    XPathExpression expression = XPathCache.getCompiledExpression("/root/element");

    assertThat(expression, is(notNullValue()));
  }

  @Test
  void testGetCompiledExpressionCachesExpression() throws Exception {
    String xpath = "/test/path";

    XPathExpression expression1 = XPathCache.getCompiledExpression(xpath);
    XPathExpression expression2 = XPathCache.getCompiledExpression(xpath);

    assertThat(expression1, is(sameInstance(expression2)));
  }

  @Test
  void testGetCompiledExpressionWithDifferentExpressionsReturnsDifferentInstances()
      throws Exception {
    XPathExpression expression1 = XPathCache.getCompiledExpression("/path/one");
    XPathExpression expression2 = XPathCache.getCompiledExpression("/path/two");

    assertThat(expression1, is(notNullValue()));
    assertThat(expression2, is(notNullValue()));
  }

  @Test
  void testGetCompiledExpressionWithInvalidXPathThrowsException() {
    assertThrows(
        XPathExpressionException.class, () -> XPathCache.getCompiledExpression("[invalid"));
  }

  @Test
  void testGetCompiledExpressionWithNullThrowsException() {
    assertThrows(NullPointerException.class, () -> XPathCache.getCompiledExpression(null));
  }

  @Test
  void testGetNamespaceResolverReturnsNonNull() {
    NamespaceContext resolver = XPathCache.getNamespaceResolver();

    assertThat(resolver, is(notNullValue()));
  }

  @Test
  void testGetNamespaceResolverReturnsSameInstance() {
    NamespaceContext resolver1 = XPathCache.getNamespaceResolver();
    NamespaceContext resolver2 = XPathCache.getNamespaceResolver();

    assertThat(resolver1, is(sameInstance(resolver2)));
  }

  @Test
  void testSetNamespaceResolver() {
    NamespaceResolver customResolver = new NamespaceResolver();

    XPathCache.setNamespaceResolver(customResolver);
    NamespaceContext result = XPathCache.getNamespaceResolver();

    assertThat(result, is(sameInstance(customResolver)));
  }

  @Test
  void testGetNamespaceResolverReturnsNamespaceResolver() {
    NamespaceContext resolver = XPathCache.getNamespaceResolver();

    assertThat(resolver, is(instanceOf(NamespaceResolver.class)));
  }
}
