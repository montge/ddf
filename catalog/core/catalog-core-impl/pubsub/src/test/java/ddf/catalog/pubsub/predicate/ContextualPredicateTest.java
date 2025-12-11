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
package ddf.catalog.pubsub.predicate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Test;

public class ContextualPredicateTest {

  @Test
  public void testConstructorWithBasicSearchPhrase() {
    String searchPhrase = "test search";
    ContextualPredicate predicate = new ContextualPredicate(searchPhrase, false, false, null);

    assertThat(predicate.getSearchPhrase(), is(notNullValue()));
    assertFalse(predicate.isFuzzy());
    assertFalse(predicate.isCaseSensitive());
    assertFalse(predicate.hasTextPaths());
  }

  @Test
  public void testConstructorWithFuzzySearch() {
    String searchPhrase = "test";
    ContextualPredicate predicate = new ContextualPredicate(searchPhrase, true, false, null);

    assertTrue(predicate.isFuzzy());
    assertThat(predicate.getSearchPhrase(), containsString("~"));
  }

  @Test
  public void testConstructorWithCaseSensitiveSearch() {
    String searchPhrase = "TestSearch";
    ContextualPredicate predicate = new ContextualPredicate(searchPhrase, false, true, null);

    assertTrue(predicate.isCaseSensitive());
  }

  @Test
  public void testConstructorWithTextPaths() {
    String searchPhrase = "test";
    Collection<String> textPaths = Arrays.asList("//title", "//description");
    ContextualPredicate predicate = new ContextualPredicate(searchPhrase, false, false, textPaths);

    assertTrue(predicate.hasTextPaths());
    assertThat(predicate.getTextPaths().size(), is(equalTo(2)));
  }

  @Test
  public void testConstructorWithEmptyTextPaths() {
    String searchPhrase = "test";
    Collection<String> textPaths = Arrays.asList();
    ContextualPredicate predicate = new ContextualPredicate(searchPhrase, false, false, textPaths);

    assertFalse(predicate.hasTextPaths());
  }

  @Test
  public void testConstructorWithNullTextPaths() {
    String searchPhrase = "test";
    ContextualPredicate predicate = new ContextualPredicate(searchPhrase, false, false, null);

    assertFalse(predicate.hasTextPaths());
  }

  @Test
  public void testNormalizePhraseWithBasicText() {
    String input = "  test search  ";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, is(equalTo("test search")));
  }

  @Test
  public void testNormalizePhraseWithQuotedPhrase() {
    String input = "\"exact phrase\"";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertTrue(result.contains("exact phrase"));
  }

  @Test
  public void testNormalizePhraseWithFuzzySearch() {
    String input = "test";
    String result = ContextualPredicate.normalizePhrase(input, true);

    assertThat(result, containsString("~"));
  }

  @Test
  public void testNormalizePhraseWithBooleanOperatorAnd() {
    String input = "cat and dog";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, containsString("AND"));
  }

  @Test
  public void testNormalizePhraseWithBooleanOperatorOr() {
    String input = "cat or dog";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, containsString("OR"));
  }

  @Test
  public void testNormalizePhraseWithBooleanOperatorNot() {
    String input = "test not example";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, containsString("NOT"));
  }

  @Test
  public void testNormalizePhraseWithAmpersand() {
    String input = "cat & dog";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, containsString("AND"));
  }

  @Test
  public void testNormalizePhraseWithPipe() {
    String input = "cat | dog";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, containsString("OR"));
  }

  @Test
  public void testNormalizePhraseWithSpecialCharacters() {
    String input = "test+special-chars";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, is(notNullValue()));
    // Special characters should be escaped
    assertThat(result, containsString("\\+"));
  }

  @Test
  public void testNormalizePhraseWithWildcard() {
    String input = "test*";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, containsString("*"));
  }

  @Test
  public void testNormalizePhraseWithEmptyString() {
    String input = "";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, is(equalTo("")));
  }

  @Test
  public void testNormalizePhraseWithNullString() {
    String input = null;
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, is(equalTo("")));
  }

  @Test
  public void testNormalizePhraseWithMultipleQuotedPhrases() {
    String input = "\"phrase one\" and \"phrase two\"";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, containsString("phrase one"));
    assertThat(result, containsString("phrase two"));
    assertThat(result, containsString("AND"));
  }

  @Test
  public void testNormalizePhraseWithFuzzyAndQuotes() {
    String input = "\"exact phrase\" fuzzy";
    String result = ContextualPredicate.normalizePhrase(input, true);

    assertThat(result, containsString("exact phrase"));
    assertThat(result, containsString("fuzzy~"));
  }

  @Test
  public void testNormalizePhraseWithParentheses() {
    String input = "(test OR example)";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, containsString("("));
    assertThat(result, containsString(")"));
    assertThat(result, containsString("OR"));
  }

  @Test
  public void testNormalizePhraseWithFuzzyAndParentheses() {
    String input = "(test)";
    String result = ContextualPredicate.normalizePhrase(input, true);

    assertThat(result, containsString("test~"));
  }

  @Test
  public void testNormalizePhraseWithTrailingQuote() {
    String input = "test\"";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertTrue(result.endsWith("\""));
  }

  @Test
  public void testNormalizePhraseEscapesBackslashProperly() {
    String input = "test\\+special";
    String result = ContextualPredicate.normalizePhrase(input, false);

    assertThat(result, is(notNullValue()));
    // Already escaped characters should not be double-escaped
    assertThat(result, containsString("\\+"));
  }

  @Test
  public void testIsContextualWithNonEmptyString() {
    assertTrue(ContextualPredicate.isContextual("test"));
  }

  @Test
  public void testIsContextualWithEmptyString() {
    assertFalse(ContextualPredicate.isContextual(""));
  }

  @Test
  public void testGetSearchPhrase() {
    String searchPhrase = "test search";
    ContextualPredicate predicate = new ContextualPredicate(searchPhrase, false, false, null);

    assertThat(predicate.getSearchPhrase(), is(notNullValue()));
  }

  @Test
  public void testGetTextPaths() {
    Collection<String> textPaths = Arrays.asList("//title", "//description");
    ContextualPredicate predicate = new ContextualPredicate("test", false, false, textPaths);

    Collection<String> retrievedPaths = predicate.getTextPaths();
    assertThat(retrievedPaths, is(notNullValue()));
    assertThat(retrievedPaths.size(), is(equalTo(2)));
  }

  @Test
  public void testToString() {
    String searchPhrase = "test";
    Collection<String> textPaths = Arrays.asList("//title");
    ContextualPredicate predicate = new ContextualPredicate(searchPhrase, true, true, textPaths);

    String result = predicate.toString();

    assertThat(result, is(notNullValue()));
  }
}
