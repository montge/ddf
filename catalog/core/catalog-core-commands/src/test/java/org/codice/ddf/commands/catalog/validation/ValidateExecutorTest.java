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
package org.codice.ddf.commands.catalog.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.util.Describable;
import ddf.catalog.validation.MetacardValidator;
import ddf.catalog.validation.ValidationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class ValidateExecutorTest {

  @Test
  void testExecuteWithEmptyMetacardList() throws ExecutionException, InterruptedException {
    List<Metacard> metacards = Collections.emptyList();
    List<MetacardValidator> validators = Collections.emptyList();

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, is(empty()));
  }

  @Test
  void testExecuteWithNoValidators() throws ExecutionException, InterruptedException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test Metacard");
    List<Metacard> metacards = Collections.singletonList(metacard);
    List<MetacardValidator> validators = Collections.emptyList();

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, hasSize(1));
    assertThat(reports.get(0).getId(), is("Test Metacard"));
    assertThat(reports.get(0).getEntries(), is(empty()));
  }

  @Test
  void testExecuteWithPassingValidator() throws ExecutionException, InterruptedException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Valid Metacard");
    List<Metacard> metacards = Collections.singletonList(metacard);

    MetacardValidator validator = mock(MetacardValidator.class);
    List<MetacardValidator> validators = Collections.singletonList(validator);

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, hasSize(1));
    assertThat(reports.get(0).getEntries(), is(empty()));
  }

  @Test
  void testExecuteWithFailingValidator()
      throws ExecutionException, InterruptedException, ValidationException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Invalid Metacard");
    List<Metacard> metacards = Collections.singletonList(metacard);

    MetacardValidator validator = mock(MetacardValidator.class);
    ValidationException exception = mock(ValidationException.class);
    when(exception.getErrors()).thenReturn(Arrays.asList("Error 1", "Error 2"));
    when(exception.getWarnings()).thenReturn(Collections.emptyList());
    doThrow(exception).when(validator).validate(any(Metacard.class));
    List<MetacardValidator> validators = Collections.singletonList(validator);

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, hasSize(1));
    assertThat(reports.get(0).getEntries(), hasSize(1));
    assertThat(reports.get(0).getEntries().get(0).getErrors(), hasSize(2));
  }

  @Test
  void testExecuteWithValidatorReturningWarnings()
      throws ExecutionException, InterruptedException, ValidationException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Warning Metacard");
    List<Metacard> metacards = Collections.singletonList(metacard);

    MetacardValidator validator = mock(MetacardValidator.class);
    ValidationException exception = mock(ValidationException.class);
    when(exception.getErrors()).thenReturn(Collections.emptyList());
    when(exception.getWarnings()).thenReturn(Arrays.asList("Warning 1"));
    doThrow(exception).when(validator).validate(any(Metacard.class));
    List<MetacardValidator> validators = Collections.singletonList(validator);

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, hasSize(1));
    assertThat(reports.get(0).getEntries().get(0).getWarnings(), hasSize(1));
  }

  @Test
  void testExecuteWithDescribableValidator()
      throws ExecutionException, InterruptedException, ValidationException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test Metacard");
    List<Metacard> metacards = Collections.singletonList(metacard);

    MetacardValidator validator =
        mock(
            MetacardValidator.class,
            org.mockito.Mockito.withSettings().extraInterfaces(Describable.class));
    when(((Describable) validator).getId()).thenReturn("custom-validator-id");

    ValidationException exception = mock(ValidationException.class);
    when(exception.getErrors()).thenReturn(Arrays.asList("Error"));
    when(exception.getWarnings()).thenReturn(Collections.emptyList());
    doThrow(exception).when(validator).validate(any(Metacard.class));
    List<MetacardValidator> validators = Collections.singletonList(validator);

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, hasSize(1));
    assertThat(reports.get(0).getEntries().get(0).getValidatorName(), is("custom-validator-id"));
  }

  @Test
  void testExecuteWithMultipleMetacards() throws ExecutionException, InterruptedException {
    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setTitle("Metacard 1");
    MetacardImpl metacard2 = new MetacardImpl();
    metacard2.setTitle("Metacard 2");
    MetacardImpl metacard3 = new MetacardImpl();
    metacard3.setTitle("Metacard 3");
    List<Metacard> metacards = Arrays.asList(metacard1, metacard2, metacard3);
    List<MetacardValidator> validators = Collections.emptyList();

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, hasSize(3));
  }

  @Test
  void testExecuteWithMultipleValidators()
      throws ExecutionException, InterruptedException, ValidationException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test Metacard");
    List<Metacard> metacards = Collections.singletonList(metacard);

    MetacardValidator validator1 = mock(MetacardValidator.class);
    MetacardValidator validator2 = mock(MetacardValidator.class);

    ValidationException exception1 = mock(ValidationException.class);
    when(exception1.getErrors()).thenReturn(Arrays.asList("Error from validator 1"));
    when(exception1.getWarnings()).thenReturn(Collections.emptyList());
    doThrow(exception1).when(validator1).validate(any(Metacard.class));

    ValidationException exception2 = mock(ValidationException.class);
    when(exception2.getErrors()).thenReturn(Collections.emptyList());
    when(exception2.getWarnings()).thenReturn(Arrays.asList("Warning from validator 2"));
    doThrow(exception2).when(validator2).validate(any(Metacard.class));

    List<MetacardValidator> validators = Arrays.asList(validator1, validator2);

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, hasSize(1));
    assertThat(reports.get(0).getEntries(), hasSize(2));
  }

  @Test
  void testExecuteWithNullErrors()
      throws ExecutionException, InterruptedException, ValidationException {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Null Errors Metacard");
    List<Metacard> metacards = Collections.singletonList(metacard);

    MetacardValidator validator = mock(MetacardValidator.class);
    ValidationException exception = mock(ValidationException.class);
    when(exception.getErrors()).thenReturn(null);
    when(exception.getWarnings()).thenReturn(null);
    doThrow(exception).when(validator).validate(any(Metacard.class));
    List<MetacardValidator> validators = Collections.singletonList(validator);

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, hasSize(1));
    assertThat(reports.get(0).getEntries().get(0).getErrors(), is(empty()));
    assertThat(reports.get(0).getEntries().get(0).getWarnings(), is(empty()));
  }

  @Test
  void testExecuteReturnsNotNullReports() throws ExecutionException, InterruptedException {
    List<Metacard> metacards = Collections.emptyList();
    List<MetacardValidator> validators = Collections.emptyList();

    List<ValidateReport> reports = ValidateExecutor.execute(metacards, validators);

    assertThat(reports, is(notNullValue()));
  }
}
