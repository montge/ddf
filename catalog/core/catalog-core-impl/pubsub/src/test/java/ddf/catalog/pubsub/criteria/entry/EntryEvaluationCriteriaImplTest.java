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
package ddf.catalog.pubsub.criteria.entry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class EntryEvaluationCriteriaImplTest {

  private static final String TEST_ID = "test-id-123";
  private static final String TEST_INPUT_ID = "input-id-456";

  @Test
  void testConstructor() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(TEST_ID, TEST_INPUT_ID);

    assertThat(criteria, is(notNullValue()));
  }

  @Test
  void testImplementsInterface() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(TEST_ID, TEST_INPUT_ID);

    assertThat(criteria, instanceOf(EntryEvaluationCriteria.class));
  }

  @Test
  void testGetId() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(TEST_ID, TEST_INPUT_ID);

    assertThat(criteria.getId(), is(TEST_ID));
  }

  @Test
  void testGetInputId() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(TEST_ID, TEST_INPUT_ID);

    assertThat(criteria.getInputId(), is(TEST_INPUT_ID));
  }

  @Test
  void testWithNullId() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(null, TEST_INPUT_ID);

    assertThat(criteria.getId(), is(nullValue()));
    assertThat(criteria.getInputId(), is(TEST_INPUT_ID));
  }

  @Test
  void testWithNullInputId() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(TEST_ID, null);

    assertThat(criteria.getId(), is(TEST_ID));
    assertThat(criteria.getInputId(), is(nullValue()));
  }

  @Test
  void testWithBothNullIds() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(null, null);

    assertThat(criteria.getId(), is(nullValue()));
    assertThat(criteria.getInputId(), is(nullValue()));
  }

  @Test
  void testWithSameIds() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(TEST_ID, TEST_ID);

    assertThat(criteria.getId(), is(criteria.getInputId()));
  }

  @Test
  void testWithEmptyId() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl("", TEST_INPUT_ID);

    assertThat(criteria.getId(), is(""));
  }

  @Test
  void testWithEmptyInputId() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(TEST_ID, "");

    assertThat(criteria.getInputId(), is(""));
  }

  @Test
  void testWithUuidFormatIds() {
    String uuidId = "550e8400-e29b-41d4-a716-446655440000";
    String uuidInputId = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";

    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(uuidId, uuidInputId);

    assertThat(criteria.getId(), is(uuidId));
    assertThat(criteria.getInputId(), is(uuidInputId));
  }

  @Test
  void testWithLongIds() {
    String longId = "a".repeat(1000);
    String longInputId = "b".repeat(1000);

    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(longId, longInputId);

    assertThat(criteria.getId().length(), is(1000));
    assertThat(criteria.getInputId().length(), is(1000));
  }

  @Test
  void testWithSpecialCharacters() {
    String specialId = "id-with!@#$%^&*()_+=";
    String specialInputId = "input-with<>{}[]|\\";

    EntryEvaluationCriteriaImpl criteria =
        new EntryEvaluationCriteriaImpl(specialId, specialInputId);

    assertThat(criteria.getId(), is(specialId));
    assertThat(criteria.getInputId(), is(specialInputId));
  }

  @Test
  void testWithWhitespace() {
    String whitespaceId = "  id with spaces  ";
    String whitespaceInputId = "\ttabbed input\t";

    EntryEvaluationCriteriaImpl criteria =
        new EntryEvaluationCriteriaImpl(whitespaceId, whitespaceInputId);

    assertThat(criteria.getId(), is(whitespaceId));
    assertThat(criteria.getInputId(), is(whitespaceInputId));
  }

  @Test
  void testMultipleInstancesAreIndependent() {
    EntryEvaluationCriteriaImpl criteria1 = new EntryEvaluationCriteriaImpl("id1", "input1");
    EntryEvaluationCriteriaImpl criteria2 = new EntryEvaluationCriteriaImpl("id2", "input2");

    assertThat(criteria1.getId(), is("id1"));
    assertThat(criteria2.getId(), is("id2"));
    assertThat(criteria1.getInputId(), is("input1"));
    assertThat(criteria2.getInputId(), is("input2"));
  }
}
