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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class ASTNodeSubclassesTest {

  // KeywordASTNode tests

  @Test
  void testKeywordASTNodeGetKeyword() {
    KeywordASTNode node = new KeywordASTNode("test keyword");

    assertThat(node.getKeyword(), is("test keyword"));
  }

  @Test
  void testKeywordASTNodeIsKeyword() {
    KeywordASTNode node = new KeywordASTNode("test");

    assertThat(node.isKeyword(), is(true));
  }

  @Test
  void testKeywordASTNodeIsNotOperator() {
    KeywordASTNode node = new KeywordASTNode("test");

    assertThat(node.isOperator(), is(false));
  }

  @Test
  void testKeywordASTNodeGetOperatorReturnsNull() {
    KeywordASTNode node = new KeywordASTNode("test");

    assertThat(node.getOperator(), is(nullValue()));
  }

  @Test
  void testKeywordASTNodeIsPhraseStartDelimiter() {
    KeywordASTNode node = new KeywordASTNode("test");

    assertThat(node.isPhraseStartDelimiter(), is(false));
  }

  @Test
  void testKeywordASTNodeToString() {
    KeywordASTNode node = new KeywordASTNode("search term");

    assertThat(node.toString(), containsString("keyword"));
    assertThat(node.toString(), containsString("search term"));
  }

  @Test
  void testKeywordASTNodeWithEmptyKeyword() {
    KeywordASTNode node = new KeywordASTNode("");

    assertThat(node.getKeyword(), is(""));
    assertThat(node.isKeyword(), is(true));
  }

  // PhraseDelimiterASTNode tests

  @Test
  void testPhraseDelimiterASTNodeGetKeywordReturnsNull() {
    PhraseDelimiterASTNode node = new PhraseDelimiterASTNode();

    assertThat(node.getKeyword(), is(nullValue()));
  }

  @Test
  void testPhraseDelimiterASTNodeGetOperatorReturnsNull() {
    PhraseDelimiterASTNode node = new PhraseDelimiterASTNode();

    assertThat(node.getOperator(), is(nullValue()));
  }

  @Test
  void testPhraseDelimiterASTNodeIsNotKeyword() {
    PhraseDelimiterASTNode node = new PhraseDelimiterASTNode();

    assertThat(node.isKeyword(), is(false));
  }

  @Test
  void testPhraseDelimiterASTNodeIsNotOperator() {
    PhraseDelimiterASTNode node = new PhraseDelimiterASTNode();

    assertThat(node.isOperator(), is(false));
  }

  @Test
  void testPhraseDelimiterASTNodeIsPhraseStartDelimiter() {
    PhraseDelimiterASTNode node = new PhraseDelimiterASTNode();

    assertThat(node.isPhraseStartDelimiter(), is(true));
  }

  @Test
  void testPhraseDelimiterASTNodeToString() {
    PhraseDelimiterASTNode node = new PhraseDelimiterASTNode();

    assertThat(node.toString(), containsString("phrase"));
    assertThat(node.toString(), containsString("Delimiter"));
  }

  // OperatorASTNode tests

  @Test
  void testOperatorASTNodeWithOperatorEnum() {
    OperatorASTNode node = new OperatorASTNode(ASTNode.Operator.AND, null, null);

    assertThat(node.getOperator(), is(ASTNode.Operator.AND));
  }

  @Test
  void testOperatorASTNodeIsOperator() {
    OperatorASTNode node = new OperatorASTNode(ASTNode.Operator.OR, null, null);

    assertThat(node.isOperator(), is(true));
  }

  @Test
  void testOperatorASTNodeIsNotKeyword() {
    OperatorASTNode node = new OperatorASTNode(ASTNode.Operator.NOT, null, null);

    assertThat(node.isKeyword(), is(false));
  }

  @Test
  void testOperatorASTNodeGetKeywordReturnsNull() {
    OperatorASTNode node = new OperatorASTNode(ASTNode.Operator.AND, null, null);

    assertThat(node.getKeyword(), is(nullValue()));
  }

  @Test
  void testOperatorASTNodeIsPhraseStartDelimiter() {
    OperatorASTNode node = new OperatorASTNode(ASTNode.Operator.AND, null, null);

    assertThat(node.isPhraseStartDelimiter(), is(false));
  }

  @Test
  void testOperatorASTNodeToString() {
    OperatorASTNode node = new OperatorASTNode(ASTNode.Operator.OR, null, null);

    assertThat(node.toString(), containsString("Operator"));
    assertThat(node.toString(), containsString("OR"));
  }

  @Test
  void testOperatorASTNodeWithLeftAndRightChildren() {
    KeywordASTNode left = new KeywordASTNode("left");
    KeywordASTNode right = new KeywordASTNode("right");
    OperatorASTNode node = new OperatorASTNode(ASTNode.Operator.AND, left, right);

    assertThat(node.getOperator(), is(ASTNode.Operator.AND));
    assertThat(node.left(), is(left));
    assertThat(node.right(), is(right));
  }

  // ASTNode.Operator enum tests

  @Test
  void testOperatorEnumAndValue() {
    assertThat(ASTNode.Operator.AND.name(), is("AND"));
  }

  @Test
  void testOperatorEnumOrValue() {
    assertThat(ASTNode.Operator.OR.name(), is("OR"));
  }

  @Test
  void testOperatorEnumNotValue() {
    assertThat(ASTNode.Operator.NOT.name(), is("NOT"));
  }

  @Test
  void testOperatorEnumValueCount() {
    assertThat(ASTNode.Operator.values().length, is(3));
  }
}
