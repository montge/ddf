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
package org.codice.ddf.opensearch.endpoint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengis.filter.Filter;

/** Tests for KeywordFilterGenerator */
@RunWith(MockitoJUnitRunner.class)
public class KeywordFilterGeneratorTest {

  private FilterBuilder filterBuilder;

  private KeywordFilterGenerator filterGenerator;

  @Before
  public void setUp() {
    filterBuilder = new GeotoolsFilterBuilder();
    filterGenerator = new KeywordFilterGenerator(filterBuilder);
  }

  @Test
  public void testGetFilterFromKeywordNode() {
    ASTNode keywordNode = new KeywordASTNode("search term");

    Filter filter = filterGenerator.getFilterFromASTNode(keywordNode);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromKeywordNodeWithXPath() {
    ASTNode keywordNode = new KeywordASTNode("search term");

    Filter filter = filterGenerator.getFilterFromASTNode(keywordNode, "/xpath/selector");

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromKeywordNodeWithEmptyXPath() {
    ASTNode keywordNode = new KeywordASTNode("search term");

    Filter filter = filterGenerator.getFilterFromASTNode(keywordNode, "");

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromAndOperatorNode() {
    ASTNode leftNode = new KeywordASTNode("left term");
    ASTNode rightNode = new KeywordASTNode("right term");
    ASTNode andNode = new OperatorASTNode(ASTNode.Operator.AND, leftNode, rightNode);

    Filter filter = filterGenerator.getFilterFromASTNode(andNode);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromOrOperatorNode() {
    ASTNode leftNode = new KeywordASTNode("left term");
    ASTNode rightNode = new KeywordASTNode("right term");
    ASTNode orNode = new OperatorASTNode(ASTNode.Operator.OR, leftNode, rightNode);

    Filter filter = filterGenerator.getFilterFromASTNode(orNode);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromNotOperatorNode() {
    ASTNode leftNode = new KeywordASTNode("left term");
    ASTNode rightNode = new KeywordASTNode("right term");
    ASTNode notNode = new OperatorASTNode(ASTNode.Operator.NOT, leftNode, rightNode);

    Filter filter = filterGenerator.getFilterFromASTNode(notNode);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromNestedOperatorNodes() {
    ASTNode keyword1 = new KeywordASTNode("term1");
    ASTNode keyword2 = new KeywordASTNode("term2");
    ASTNode keyword3 = new KeywordASTNode("term3");

    ASTNode andNode = new OperatorASTNode(ASTNode.Operator.AND, keyword1, keyword2);
    ASTNode orNode = new OperatorASTNode(ASTNode.Operator.OR, andNode, keyword3);

    Filter filter = filterGenerator.getFilterFromASTNode(orNode);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromComplexExpression() {
    // (term1 AND term2) OR (term3 NOT term4)
    ASTNode keyword1 = new KeywordASTNode("term1");
    ASTNode keyword2 = new KeywordASTNode("term2");
    ASTNode keyword3 = new KeywordASTNode("term3");
    ASTNode keyword4 = new KeywordASTNode("term4");

    ASTNode andNode = new OperatorASTNode(ASTNode.Operator.AND, keyword1, keyword2);
    ASTNode notNode = new OperatorASTNode(ASTNode.Operator.NOT, keyword3, keyword4);
    ASTNode orNode = new OperatorASTNode(ASTNode.Operator.OR, andNode, notNode);

    Filter filter = filterGenerator.getFilterFromASTNode(orNode);

    assertThat(filter, notNullValue());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetFilterFromNullNode() {
    filterGenerator.getFilterFromASTNode(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetFilterWithNullFilterBuilder() {
    KeywordFilterGenerator badGenerator = new KeywordFilterGenerator(null);
    ASTNode keywordNode = new KeywordASTNode("term");

    badGenerator.getFilterFromASTNode(keywordNode);
  }

  @Test
  public void testGetFilterWithXPathSelector() {
    ASTNode keyword1 = new KeywordASTNode("term1");
    ASTNode keyword2 = new KeywordASTNode("term2");
    ASTNode andNode = new OperatorASTNode(ASTNode.Operator.AND, keyword1, keyword2);

    Filter filter = filterGenerator.getFilterFromASTNode(andNode, "/custom/xpath");

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromSingleKeyword() {
    ASTNode singleKeyword = new KeywordASTNode("singleterm");

    Filter filter = filterGenerator.getFilterFromASTNode(singleKeyword);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromKeywordWithSpecialCharacters() {
    ASTNode keywordNode = new KeywordASTNode("term-with-dash");

    Filter filter = filterGenerator.getFilterFromASTNode(keywordNode);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromKeywordWithQuotes() {
    ASTNode keywordNode = new KeywordASTNode("\"quoted term\"");

    Filter filter = filterGenerator.getFilterFromASTNode(keywordNode);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromMultipleAndOperations() {
    ASTNode keyword1 = new KeywordASTNode("term1");
    ASTNode keyword2 = new KeywordASTNode("term2");
    ASTNode keyword3 = new KeywordASTNode("term3");
    ASTNode keyword4 = new KeywordASTNode("term4");

    ASTNode and1 = new OperatorASTNode(ASTNode.Operator.AND, keyword1, keyword2);
    ASTNode and2 = new OperatorASTNode(ASTNode.Operator.AND, keyword3, keyword4);
    ASTNode finalAnd = new OperatorASTNode(ASTNode.Operator.AND, and1, and2);

    Filter filter = filterGenerator.getFilterFromASTNode(finalAnd);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromMultipleOrOperations() {
    ASTNode keyword1 = new KeywordASTNode("term1");
    ASTNode keyword2 = new KeywordASTNode("term2");
    ASTNode keyword3 = new KeywordASTNode("term3");
    ASTNode keyword4 = new KeywordASTNode("term4");

    ASTNode or1 = new OperatorASTNode(ASTNode.Operator.OR, keyword1, keyword2);
    ASTNode or2 = new OperatorASTNode(ASTNode.Operator.OR, keyword3, keyword4);
    ASTNode finalOr = new OperatorASTNode(ASTNode.Operator.OR, or1, or2);

    Filter filter = filterGenerator.getFilterFromASTNode(finalOr);

    assertThat(filter, notNullValue());
  }

  @Test
  public void testGetFilterFromDeepNesting() {
    ASTNode keyword1 = new KeywordASTNode("term1");
    ASTNode keyword2 = new KeywordASTNode("term2");

    ASTNode level1 = new OperatorASTNode(ASTNode.Operator.AND, keyword1, keyword2);
    ASTNode level2 = new OperatorASTNode(ASTNode.Operator.OR, level1, new KeywordASTNode("term3"));
    ASTNode level3 = new OperatorASTNode(ASTNode.Operator.AND, level2, new KeywordASTNode("term4"));
    ASTNode level4 = new OperatorASTNode(ASTNode.Operator.OR, level3, new KeywordASTNode("term5"));

    Filter filter = filterGenerator.getFilterFromASTNode(level4);

    assertThat(filter, notNullValue());
  }
}
