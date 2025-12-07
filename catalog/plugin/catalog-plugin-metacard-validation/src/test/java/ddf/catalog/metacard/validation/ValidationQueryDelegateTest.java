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
package ddf.catalog.metacard.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import ddf.catalog.data.types.Validation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ValidationQueryDelegateTest {

  private ValidationQueryDelegate errorsDelegate;

  private ValidationQueryDelegate warningsDelegate;

  @Before
  public void setUp() {
    errorsDelegate = new ValidationQueryDelegate(Validation.VALIDATION_ERRORS);
    warningsDelegate = new ValidationQueryDelegate(Validation.VALIDATION_WARNINGS);
  }

  @Test
  public void testPropertyIsEqualToWithMatchingPropertyName() {
    assertThat(
        errorsDelegate.propertyIsEqualTo(Validation.VALIDATION_ERRORS, "test", true), is(true));
    assertThat(
        warningsDelegate.propertyIsEqualTo(Validation.VALIDATION_WARNINGS, "test", false),
        is(true));
  }

  @Test
  public void testPropertyIsEqualToWithNonMatchingPropertyName() {
    assertThat(errorsDelegate.propertyIsEqualTo("some-other-property", "test", true), is(false));
    assertThat(warningsDelegate.propertyIsEqualTo("some-other-property", "test", false), is(false));
    assertThat(
        errorsDelegate.propertyIsEqualTo(Validation.VALIDATION_WARNINGS, "test", true), is(false));
    assertThat(
        warningsDelegate.propertyIsEqualTo(Validation.VALIDATION_ERRORS, "test", false), is(false));
  }

  @Test
  public void testPropertyIsNullWithMatchingPropertyName() {
    assertThat(errorsDelegate.propertyIsNull(Validation.VALIDATION_ERRORS), is(true));
    assertThat(warningsDelegate.propertyIsNull(Validation.VALIDATION_WARNINGS), is(true));
  }

  @Test
  public void testPropertyIsNullWithNonMatchingPropertyName() {
    assertThat(errorsDelegate.propertyIsNull("some-other-property"), is(false));
    assertThat(warningsDelegate.propertyIsNull("some-other-property"), is(false));
    assertThat(errorsDelegate.propertyIsNull(Validation.VALIDATION_WARNINGS), is(false));
    assertThat(warningsDelegate.propertyIsNull(Validation.VALIDATION_ERRORS), is(false));
  }

  @Test
  public void testPropertyIsLikeWithMatchingPropertyName() {
    assertThat(
        errorsDelegate.propertyIsLike(Validation.VALIDATION_ERRORS, "*test*", true), is(true));
    assertThat(
        warningsDelegate.propertyIsLike(Validation.VALIDATION_WARNINGS, "*test*", false), is(true));
  }

  @Test
  public void testPropertyIsLikeWithNonMatchingPropertyName() {
    assertThat(errorsDelegate.propertyIsLike("some-other-property", "*test*", true), is(false));
    assertThat(warningsDelegate.propertyIsLike("some-other-property", "*test*", false), is(false));
    assertThat(
        errorsDelegate.propertyIsLike(Validation.VALIDATION_WARNINGS, "*test*", true), is(false));
    assertThat(
        warningsDelegate.propertyIsLike(Validation.VALIDATION_ERRORS, "*test*", false), is(false));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithMatchingArgument() {
    List<Object> arguments = Arrays.asList(Validation.VALIDATION_ERRORS, "other-arg");
    assertThat(errorsDelegate.propertyIsEqualTo("function", arguments, "literal"), is(true));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithNonMatchingArguments() {
    List<Object> arguments = Arrays.asList("arg1", "arg2");
    assertThat(errorsDelegate.propertyIsEqualTo("function", arguments, "literal"), is(false));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithNullArguments() {
    assertThat(errorsDelegate.propertyIsEqualTo("function", null, "literal"), is(false));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithEmptyArguments() {
    assertThat(
        errorsDelegate.propertyIsEqualTo("function", Collections.emptyList(), "literal"),
        is(false));
  }

  @Test
  public void testAndOperationWithAllFalse() {
    List<Boolean> operands = Arrays.asList(false, false, false);
    assertThat(errorsDelegate.and(operands), is(false));
  }

  @Test
  public void testAndOperationWithSomeTrue() {
    List<Boolean> operands = Arrays.asList(false, true, false);
    assertThat(errorsDelegate.and(operands), is(true));
  }

  @Test
  public void testAndOperationWithAllTrue() {
    List<Boolean> operands = Arrays.asList(true, true, true);
    assertThat(errorsDelegate.and(operands), is(true));
  }

  @Test
  public void testAndOperationWithEmptyList() {
    assertThat(errorsDelegate.and(Collections.emptyList()), is(false));
  }

  @Test
  public void testAndOperationWithSingleTrue() {
    assertThat(errorsDelegate.and(Collections.singletonList(true)), is(true));
  }

  @Test
  public void testAndOperationWithSingleFalse() {
    assertThat(errorsDelegate.and(Collections.singletonList(false)), is(false));
  }

  @Test
  public void testOrOperationWithAllFalse() {
    List<Boolean> operands = Arrays.asList(false, false, false);
    assertThat(errorsDelegate.or(operands), is(false));
  }

  @Test
  public void testOrOperationWithSomeTrue() {
    List<Boolean> operands = Arrays.asList(false, true, false);
    assertThat(errorsDelegate.or(operands), is(true));
  }

  @Test
  public void testOrOperationWithAllTrue() {
    List<Boolean> operands = Arrays.asList(true, true, true);
    assertThat(errorsDelegate.or(operands), is(true));
  }

  @Test
  public void testOrOperationWithEmptyList() {
    assertThat(errorsDelegate.or(Collections.emptyList()), is(false));
  }

  @Test
  public void testOrOperationWithSingleTrue() {
    assertThat(errorsDelegate.or(Collections.singletonList(true)), is(true));
  }

  @Test
  public void testOrOperationWithSingleFalse() {
    assertThat(errorsDelegate.or(Collections.singletonList(false)), is(false));
  }

  @Test
  public void testNotOperationWithTrue() {
    assertThat(errorsDelegate.not(true), is(true));
  }

  @Test
  public void testNotOperationWithFalse() {
    assertThat(errorsDelegate.not(false), is(false));
  }

  @Test
  public void testDefaultOperationReturnsFailse() {
    assertThat(
        errorsDelegate.defaultOperation("property", "literal", String.class, null), is(false));
  }

  @Test
  public void testDefaultOperationWithDifferentTypes() {
    assertThat(errorsDelegate.defaultOperation("property", 123, Integer.class, null), is(false));
    assertThat(errorsDelegate.defaultOperation("property", true, Boolean.class, null), is(false));
    assertThat(errorsDelegate.defaultOperation("property", 45.6, Double.class, null), is(false));
  }

  @Test
  public void testWarningsDelegateIndependentlyFromErrorsDelegate() {
    // Ensure that two different delegates work independently
    assertThat(
        errorsDelegate.propertyIsEqualTo(Validation.VALIDATION_ERRORS, "test", true), is(true));
    assertThat(
        warningsDelegate.propertyIsEqualTo(Validation.VALIDATION_ERRORS, "test", true), is(false));

    assertThat(
        warningsDelegate.propertyIsEqualTo(Validation.VALIDATION_WARNINGS, "test", true), is(true));
    assertThat(
        errorsDelegate.propertyIsEqualTo(Validation.VALIDATION_WARNINGS, "test", true), is(false));
  }

  @Test
  public void testCaseSensitivityParameter() {
    // PropertyIsEqualTo case sensitive
    assertThat(
        errorsDelegate.propertyIsEqualTo(Validation.VALIDATION_ERRORS, "test", true), is(true));
    assertThat(
        errorsDelegate.propertyIsEqualTo(Validation.VALIDATION_ERRORS, "test", false), is(true));

    // PropertyIsLike case sensitive
    assertThat(
        errorsDelegate.propertyIsLike(Validation.VALIDATION_ERRORS, "*pattern*", true), is(true));
    assertThat(
        errorsDelegate.propertyIsLike(Validation.VALIDATION_ERRORS, "*pattern*", false), is(true));
  }
}
