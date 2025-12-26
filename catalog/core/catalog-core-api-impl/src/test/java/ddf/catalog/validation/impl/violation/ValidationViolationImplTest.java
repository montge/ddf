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
package ddf.catalog.validation.impl.violation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ddf.catalog.validation.violation.ValidationViolation.Severity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ValidationViolationImplTest {

  @Test
  void testConstructorWithValidParameters() {
    Set<String> attributes = new HashSet<>();
    attributes.add("title");

    ValidationViolationImpl violation =
        new ValidationViolationImpl(attributes, "Invalid title", Severity.ERROR);

    assertThat(violation.getAttributes(), containsInAnyOrder("title"));
    assertThat(violation.getMessage(), is("Invalid title"));
    assertThat(violation.getSeverity(), is(Severity.ERROR));
  }

  @Test
  void testConstructorWithMultipleAttributes() {
    Set<String> attributes = new HashSet<>();
    attributes.add("title");
    attributes.add("description");
    attributes.add("author");

    ValidationViolationImpl violation =
        new ValidationViolationImpl(attributes, "Validation error", Severity.WARNING);

    assertThat(violation.getAttributes(), containsInAnyOrder("title", "description", "author"));
  }

  @Test
  void testConstructorWithNullAttributesThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ValidationViolationImpl(null, "message", Severity.ERROR));
  }

  @Test
  void testConstructorWithEmptyAttributesThrowsException() {
    Set<String> emptyAttributes = new HashSet<>();

    assertThrows(
        IllegalArgumentException.class,
        () -> new ValidationViolationImpl(emptyAttributes, "message", Severity.ERROR));
  }

  @Test
  void testConstructorWithNullMessageThrowsException() {
    Set<String> attributes = Collections.singleton("attr");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ValidationViolationImpl(attributes, null, Severity.ERROR));
  }

  @Test
  void testConstructorWithEmptyMessageThrowsException() {
    Set<String> attributes = Collections.singleton("attr");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ValidationViolationImpl(attributes, "", Severity.ERROR));
  }

  @Test
  void testConstructorWithBlankMessageThrowsException() {
    Set<String> attributes = Collections.singleton("attr");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ValidationViolationImpl(attributes, "   ", Severity.ERROR));
  }

  @Test
  void testConstructorWithNullSeverityThrowsException() {
    Set<String> attributes = Collections.singleton("attr");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ValidationViolationImpl(attributes, "message", null));
  }

  @Test
  void testGetAttributesReturnsUnmodifiableSet() {
    Set<String> attributes = new HashSet<>();
    attributes.add("attr");

    ValidationViolationImpl violation =
        new ValidationViolationImpl(attributes, "message", Severity.ERROR);

    Set<String> returnedAttributes = violation.getAttributes();

    assertThrows(UnsupportedOperationException.class, () -> returnedAttributes.add("newAttr"));
  }

  @Test
  void testSeverityWarning() {
    Set<String> attributes = Collections.singleton("attr");

    ValidationViolationImpl violation =
        new ValidationViolationImpl(attributes, "message", Severity.WARNING);

    assertThat(violation.getSeverity(), is(Severity.WARNING));
  }

  @Test
  void testSeverityError() {
    Set<String> attributes = Collections.singleton("attr");

    ValidationViolationImpl violation =
        new ValidationViolationImpl(attributes, "message", Severity.ERROR);

    assertThat(violation.getSeverity(), is(Severity.ERROR));
  }

  @Test
  void testEqualsWithSameObject() {
    Set<String> attributes = Collections.singleton("attr");
    ValidationViolationImpl violation =
        new ValidationViolationImpl(attributes, "message", Severity.ERROR);

    assertThat(violation.equals(violation), is(true));
  }

  @Test
  void testEqualsWithEqualObjects() {
    Set<String> attributes1 = Collections.singleton("attr");
    Set<String> attributes2 = Collections.singleton("attr");

    ValidationViolationImpl violation1 =
        new ValidationViolationImpl(attributes1, "message", Severity.ERROR);
    ValidationViolationImpl violation2 =
        new ValidationViolationImpl(attributes2, "message", Severity.ERROR);

    assertThat(violation1, equalTo(violation2));
  }

  @Test
  void testEqualsWithDifferentAttributes() {
    Set<String> attributes1 = Collections.singleton("attr1");
    Set<String> attributes2 = Collections.singleton("attr2");

    ValidationViolationImpl violation1 =
        new ValidationViolationImpl(attributes1, "message", Severity.ERROR);
    ValidationViolationImpl violation2 =
        new ValidationViolationImpl(attributes2, "message", Severity.ERROR);

    assertThat(violation1, is(not(equalTo(violation2))));
  }

  @Test
  void testEqualsWithDifferentMessage() {
    Set<String> attributes = Collections.singleton("attr");

    ValidationViolationImpl violation1 =
        new ValidationViolationImpl(attributes, "message1", Severity.ERROR);
    ValidationViolationImpl violation2 =
        new ValidationViolationImpl(attributes, "message2", Severity.ERROR);

    assertThat(violation1, is(not(equalTo(violation2))));
  }

  @Test
  void testEqualsWithDifferentSeverity() {
    Set<String> attributes = Collections.singleton("attr");

    ValidationViolationImpl violation1 =
        new ValidationViolationImpl(attributes, "message", Severity.ERROR);
    ValidationViolationImpl violation2 =
        new ValidationViolationImpl(attributes, "message", Severity.WARNING);

    assertThat(violation1, is(not(equalTo(violation2))));
  }

  @Test
  void testEqualsWithNull() {
    Set<String> attributes = Collections.singleton("attr");
    ValidationViolationImpl violation =
        new ValidationViolationImpl(attributes, "message", Severity.ERROR);

    assertThat(violation.equals(null), is(false));
  }

  @Test
  void testEqualsWithDifferentClass() {
    Set<String> attributes = Collections.singleton("attr");
    ValidationViolationImpl violation =
        new ValidationViolationImpl(attributes, "message", Severity.ERROR);

    assertThat(violation.equals("not a violation"), is(false));
  }

  @Test
  void testHashCodeConsistentWithEquals() {
    Set<String> attributes1 = Collections.singleton("attr");
    Set<String> attributes2 = Collections.singleton("attr");

    ValidationViolationImpl violation1 =
        new ValidationViolationImpl(attributes1, "message", Severity.ERROR);
    ValidationViolationImpl violation2 =
        new ValidationViolationImpl(attributes2, "message", Severity.ERROR);

    assertThat(violation1.hashCode(), is(violation2.hashCode()));
  }

  @Test
  void testHashCodeDifferentForDifferentObjects() {
    Set<String> attributes = Collections.singleton("attr");

    ValidationViolationImpl violation1 =
        new ValidationViolationImpl(attributes, "message1", Severity.ERROR);
    ValidationViolationImpl violation2 =
        new ValidationViolationImpl(attributes, "message2", Severity.ERROR);

    assertThat(violation1.hashCode(), is(not(violation2.hashCode())));
  }
}
