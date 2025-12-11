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
package ddf.catalog.validation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.Sets;
import ddf.catalog.validation.AttributeValidator;
import ddf.catalog.validation.AttributeValidatorRegistry;
import ddf.catalog.validation.impl.validator.PastDateValidator;
import ddf.catalog.validation.impl.validator.PatternValidator;
import ddf.catalog.validation.impl.validator.SizeValidator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AttributeValidatorRegistryImplAdditionalTest {

  private AttributeValidatorRegistry registry;

  @BeforeEach
  public void setup() {
    registry = new AttributeValidatorRegistryImpl();
  }

  @Test
  public void testGetValidatorsReturnsUnmodifiableSet() {
    registry.registerValidators("title", Sets.newHashSet(new SizeValidator(1, 10)));

    Set<AttributeValidator> validators = registry.getValidators("title");
    assertThat(validators, hasSize(1));

    try {
      validators.add(new PatternValidator("\\d+"));
      // If we get here, the set was modifiable (fail)
      assertThat("Expected UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      // Expected - the set should be unmodifiable
      assertThat(true, is(true));
    }
  }

  @Test
  public void testGetValidatorsForNonExistentAttribute() {
    Set<AttributeValidator> validators = registry.getValidators("nonexistent");

    assertThat(validators, empty());
  }

  @Test
  public void testGetValidatorsWithNullAttributeName() {
    assertThrows(IllegalArgumentException.class, () -> registry.getValidators(null));
  }

  @Test
  public void testDeregisterValidatorsWithNullAttributeName() {
    assertThrows(IllegalArgumentException.class, () -> registry.deregisterValidators(null));
  }

  @Test
  public void testDeregisterNonExistentAttribute() {
    // Should not throw exception
    registry.deregisterValidators("nonexistent");

    Set<AttributeValidator> validators = registry.getValidators("nonexistent");
    assertThat(validators, empty());
  }

  @Test
  public void testRegisterValidatorsThenDeregisterThenReregister() {
    SizeValidator sizeValidator = new SizeValidator(1, 10);
    registry.registerValidators("title", Sets.newHashSet(sizeValidator));
    assertThat(registry.getValidators("title"), hasSize(1));

    registry.deregisterValidators("title");
    assertThat(registry.getValidators("title"), empty());

    registry.registerValidators("title", Sets.newHashSet(sizeValidator));
    assertThat(registry.getValidators("title"), hasSize(1));
  }

  @Test
  public void testConcurrentRegistration() throws InterruptedException {
    final int threadCount = 10;
    Thread[] threads = new Thread[threadCount];

    for (int i = 0; i < threadCount; i++) {
      final int index = i;
      threads[i] =
          new Thread(
              () -> {
                registry.registerValidators(
                    "attr" + index, Sets.newHashSet(new SizeValidator(1, 10)));
              });
    }

    for (Thread thread : threads) {
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    // Verify all attributes were registered
    for (int i = 0; i < threadCount; i++) {
      assertThat(registry.getValidators("attr" + i), hasSize(1));
    }
  }

  @Test
  public void testRegisterMultipleValidatorsInOneCall() {
    SizeValidator sizeValidator = new SizeValidator(1, 10);
    PatternValidator patternValidator = new PatternValidator("\\d+");
    PastDateValidator pastDateValidator = PastDateValidator.getInstance();

    registry.registerValidators(
        "title", Sets.newHashSet(sizeValidator, patternValidator, pastDateValidator));

    assertThat(registry.getValidators("title"), hasSize(3));
  }

  @Test
  public void testRegisterValidatorsForSameAttributeMultipleTimes() {
    SizeValidator sizeValidator1 = new SizeValidator(1, 10);
    registry.registerValidators("title", Sets.newHashSet(sizeValidator1));
    assertThat(registry.getValidators("title"), hasSize(1));

    PatternValidator patternValidator = new PatternValidator("\\d+");
    registry.registerValidators("title", Sets.newHashSet(patternValidator));
    assertThat(registry.getValidators("title"), hasSize(2));

    SizeValidator sizeValidator2 = new SizeValidator(5, 20);
    registry.registerValidators("title", Sets.newHashSet(sizeValidator2));
    assertThat(registry.getValidators("title"), hasSize(3));
  }

  @Test
  public void testDeregisterOneAttributeDoesNotAffectOthers() {
    registry.registerValidators("title", Sets.newHashSet(new SizeValidator(1, 10)));
    registry.registerValidators("description", Sets.newHashSet(new SizeValidator(1, 100)));
    registry.registerValidators("modified", Sets.newHashSet(PastDateValidator.getInstance()));

    assertThat(registry.getValidators("title"), hasSize(1));
    assertThat(registry.getValidators("description"), hasSize(1));
    assertThat(registry.getValidators("modified"), hasSize(1));

    registry.deregisterValidators("title");

    assertThat(registry.getValidators("title"), empty());
    assertThat(registry.getValidators("description"), hasSize(1));
    assertThat(registry.getValidators("modified"), hasSize(1));
  }

  @Test
  public void testGetValidatorsAfterDeregisterReturnsEmptySet() {
    registry.registerValidators("title", Sets.newHashSet(new SizeValidator(1, 10)));
    registry.deregisterValidators("title");

    Set<AttributeValidator> validators = registry.getValidators("title");
    assertThat(validators, empty());

    // Should still return unmodifiable empty set
    try {
      validators.add(new PatternValidator("\\d+"));
      assertThat("Expected UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      assertThat(true, is(true));
    }
  }

  @Test
  public void testRegisterSameValidatorInstanceMultipleTimes() {
    SizeValidator sizeValidator = new SizeValidator(1, 10);

    registry.registerValidators("title", Sets.newHashSet(sizeValidator));
    assertThat(registry.getValidators("title"), hasSize(1));

    // Register the same instance again
    registry.registerValidators("title", Sets.newHashSet(sizeValidator));

    // Should still be 1 because Set doesn't allow duplicates
    assertThat(registry.getValidators("title"), hasSize(1));
  }

  @Test
  public void testRegisterValidatorsWithEmptyAttributeName() {
    // Empty string is technically valid (though not recommended)
    registry.registerValidators("", Sets.newHashSet(new SizeValidator(1, 10)));

    assertThat(registry.getValidators(""), hasSize(1));
  }

  @Test
  public void testRegisterValidatorsWithNullValidators() {
    assertThrows(IllegalArgumentException.class, () -> registry.registerValidators("title", null));
  }

  @Test
  public void testMultipleAttributesWithSameValidator() {
    SizeValidator sizeValidator = new SizeValidator(1, 10);

    registry.registerValidators("title", Sets.newHashSet(sizeValidator));
    registry.registerValidators("description", Sets.newHashSet(sizeValidator));

    assertThat(registry.getValidators("title"), hasSize(1));
    assertThat(registry.getValidators("description"), hasSize(1));
  }

  @Test
  public void testValidatorRegistryIsolation() {
    AttributeValidatorRegistry registry1 = new AttributeValidatorRegistryImpl();
    AttributeValidatorRegistry registry2 = new AttributeValidatorRegistryImpl();

    registry1.registerValidators("title", Sets.newHashSet(new SizeValidator(1, 10)));

    assertThat(registry1.getValidators("title"), hasSize(1));
    assertThat(registry2.getValidators("title"), empty());
  }
}
