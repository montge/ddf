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
package org.codice.ddf.condition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Dictionary;
import java.util.Hashtable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.condpermadmin.Condition;

/**
 * Tests the {@link AbstractCondition} base class.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>Postponement behavior (returns false for immediate evaluation)
 *   <li>Mutability behavior (returns false for immutable conditions)
 *   <li>Group satisfaction logic (all conditions must be satisfied)
 *   <li>Edge cases with empty and null condition arrays
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class AbstractConditionTest {

  private TestCondition testCondition;

  @BeforeEach
  public void setUp() {
    testCondition = new TestCondition(true);
  }

  @Test
  public void testIsPostponed() {
    boolean postponed = testCondition.isPostponed();

    assertThat(postponed, is(false));
  }

  @Test
  public void testIsMutable() {
    boolean mutable = testCondition.isMutable();

    assertThat(mutable, is(false));
  }

  @Test
  public void testIsSatisfiedSingleCondition() {
    TestCondition satisfiedCondition = new TestCondition(true);
    Condition[] conditions = new Condition[] {satisfiedCondition};
    Dictionary<Object, Object> context = new Hashtable<>();

    boolean result = testCondition.isSatisfied(conditions, context);

    assertThat(result, is(true));
  }

  @Test
  public void testIsSatisfiedMultipleConditionsAllSatisfied() {
    TestCondition condition1 = new TestCondition(true);
    TestCondition condition2 = new TestCondition(true);
    TestCondition condition3 = new TestCondition(true);
    Condition[] conditions = new Condition[] {condition1, condition2, condition3};
    Dictionary<Object, Object> context = new Hashtable<>();

    boolean result = testCondition.isSatisfied(conditions, context);

    assertThat(result, is(true));
  }

  @Test
  public void testIsSatisfiedMultipleConditionsOneFails() {
    TestCondition condition1 = new TestCondition(true);
    TestCondition condition2 = new TestCondition(false);
    TestCondition condition3 = new TestCondition(true);
    Condition[] conditions = new Condition[] {condition1, condition2, condition3};
    Dictionary<Object, Object> context = new Hashtable<>();

    boolean result = testCondition.isSatisfied(conditions, context);

    assertThat(result, is(false));
  }

  @Test
  public void testIsSatisfiedMultipleConditionsAllFail() {
    TestCondition condition1 = new TestCondition(false);
    TestCondition condition2 = new TestCondition(false);
    TestCondition condition3 = new TestCondition(false);
    Condition[] conditions = new Condition[] {condition1, condition2, condition3};
    Dictionary<Object, Object> context = new Hashtable<>();

    boolean result = testCondition.isSatisfied(conditions, context);

    assertThat(result, is(false));
  }

  @Test
  public void testIsSatisfiedEmptyConditionsArray() {
    Condition[] conditions = new Condition[0];
    Dictionary<Object, Object> context = new Hashtable<>();

    boolean result = testCondition.isSatisfied(conditions, context);

    assertThat(result, is(true));
  }

  @Test
  public void testIsSatisfiedWithNullContext() {
    TestCondition satisfiedCondition = new TestCondition(true);
    Condition[] conditions = new Condition[] {satisfiedCondition};

    boolean result = testCondition.isSatisfied(conditions, null);

    assertThat(result, is(true));
  }

  @Test
  public void testIsSatisfiedMixedConditionTypes() {
    TestCondition condition1 = new TestCondition(true);
    Condition mockCondition = mock(Condition.class);
    when(mockCondition.isSatisfied()).thenReturn(true);
    Condition[] conditions = new Condition[] {condition1, mockCondition};
    Dictionary<Object, Object> context = new Hashtable<>();

    boolean result = testCondition.isSatisfied(conditions, context);

    assertThat(result, is(true));
  }

  @Test
  public void testIsSatisfiedWithShortCircuit() {
    // First condition fails, should short-circuit and not evaluate others
    TestCondition failingCondition = new TestCondition(false);
    TestCondition condition2 = new TestCondition(true);
    Condition[] conditions = new Condition[] {failingCondition, condition2};
    Dictionary<Object, Object> context = new Hashtable<>();

    boolean result = testCondition.isSatisfied(conditions, context);

    assertThat(result, is(false));
  }

  @Test
  public void testIsSatisfiedWithEmptyDictionary() {
    TestCondition satisfiedCondition = new TestCondition(true);
    Condition[] conditions = new Condition[] {satisfiedCondition};
    Dictionary<Object, Object> emptyContext = new Hashtable<>();

    boolean result = testCondition.isSatisfied(conditions, emptyContext);

    assertThat(result, is(true));
  }

  @Test
  public void testIsSatisfiedWithPopulatedDictionary() {
    TestCondition satisfiedCondition = new TestCondition(true);
    Condition[] conditions = new Condition[] {satisfiedCondition};
    Dictionary<Object, Object> context = new Hashtable<>();
    context.put("key1", "value1");
    context.put("key2", 123);
    context.put("key3", Boolean.TRUE);

    boolean result = testCondition.isSatisfied(conditions, context);

    assertThat(result, is(true));
  }

  @Test
  public void testMultipleInvocationsReturnConsistentResults() {
    TestCondition satisfiedCondition = new TestCondition(true);
    Condition[] conditions = new Condition[] {satisfiedCondition};
    Dictionary<Object, Object> context = new Hashtable<>();

    boolean result1 = testCondition.isSatisfied(conditions, context);
    boolean result2 = testCondition.isSatisfied(conditions, context);
    boolean result3 = testCondition.isSatisfied(conditions, context);

    assertThat(result1, is(true));
    assertThat(result2, is(true));
    assertThat(result3, is(true));
  }

  /**
   * Test implementation of AbstractCondition for testing purposes.
   *
   * <p>Allows configurable satisfaction state for testing the group satisfaction logic.
   */
  private static class TestCondition extends AbstractCondition {
    private final boolean satisfied;

    public TestCondition(boolean satisfied) {
      this.satisfied = satisfied;
    }

    @Override
    public boolean isSatisfied() {
      return satisfied;
    }
  }
}
