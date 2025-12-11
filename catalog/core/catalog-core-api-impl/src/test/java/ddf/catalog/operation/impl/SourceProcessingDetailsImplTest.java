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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ddf.catalog.operation.SourceProcessingDetails;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link SourceProcessingDetailsImpl} class.
 *
 * <p>Tests constructors, getters, setters, equals/hashCode, and edge cases for source processing
 * details.
 */
@ExtendWith(MockitoExtension.class)
public class SourceProcessingDetailsImplTest {

  private static final String WARNING_1 = "Warning: Connection slow";
  private static final String WARNING_2 = "Warning: Partial results";
  private static final String WARNING_3 = "Warning: Timeout approaching";

  private SourceProcessingDetailsImpl details;

  @BeforeEach
  public void setUp() {
    details = new SourceProcessingDetailsImpl();
  }

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests the default constructor creates valid SourceProcessingDetailsImpl.
   *
   * <p>Verifies that warnings list is empty by default.
   */
  @Test
  public void testDefaultConstructor() {
    SourceProcessingDetailsImpl defaultDetails = new SourceProcessingDetailsImpl();

    assertThat(defaultDetails, is(notNullValue()));
    assertThat(defaultDetails.getWarnings(), is(notNullValue()));
    assertThat(defaultDetails.getWarnings(), is(empty()));
  }

  /**
   * Tests constructor with warnings list.
   *
   * <p>Verifies that warnings are properly stored.
   */
  @Test
  public void testConstructorWithWarnings() {
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);
    SourceProcessingDetailsImpl detailsWithWarnings = new SourceProcessingDetailsImpl(warnings);

    assertThat(detailsWithWarnings.getWarnings(), hasSize(2));
    assertThat(detailsWithWarnings.getWarnings(), containsInAnyOrder(WARNING_1, WARNING_2));
  }

  /**
   * Tests constructor with single warning.
   *
   * <p>Verifies that a single warning is properly stored.
   */
  @Test
  public void testConstructorWithSingleWarning() {
    List<String> warnings = Collections.singletonList(WARNING_1);
    SourceProcessingDetailsImpl detailsWithWarning = new SourceProcessingDetailsImpl(warnings);

    assertThat(detailsWithWarning.getWarnings(), hasSize(1));
    assertThat(detailsWithWarning.getWarnings(), contains(WARNING_1));
  }

  /**
   * Tests constructor with empty warnings list.
   *
   * <p>Verifies that empty warnings list is accepted.
   */
  @Test
  public void testConstructorWithEmptyWarnings() {
    SourceProcessingDetailsImpl detailsWithEmpty =
        new SourceProcessingDetailsImpl(Collections.emptyList());

    assertThat(detailsWithEmpty.getWarnings(), is(empty()));
  }

  /**
   * Tests constructor with null warnings throws IllegalArgumentException.
   *
   * <p>Verifies that null warnings are not accepted.
   */
  @Test
  public void testConstructorWithNullWarningsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new SourceProcessingDetailsImpl(null));
  }

  /**
   * Tests constructor with many warnings.
   *
   * <p>Verifies that large lists of warnings are handled correctly.
   */
  @Test
  public void testConstructorWithManyWarnings() {
    List<String> manyWarnings =
        Arrays.asList(WARNING_1, WARNING_2, WARNING_3, "Warning 4", "Warning 5");
    SourceProcessingDetailsImpl detailsWithMany = new SourceProcessingDetailsImpl(manyWarnings);

    assertThat(detailsWithMany.getWarnings(), hasSize(5));
  }

  // ====================
  // Getter Tests
  // ====================

  /**
   * Tests getWarnings returns the correct list.
   *
   * <p>Verifies that warnings can be retrieved correctly.
   */
  @Test
  public void testGetWarnings() {
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);
    details.setWarnings(warnings);

    List<String> retrieved = details.getWarnings();

    assertThat(retrieved, hasSize(2));
    assertThat(retrieved, containsInAnyOrder(WARNING_1, WARNING_2));
  }

  /**
   * Tests getWarnings returns empty list when no warnings.
   *
   * <p>Verifies that default state has empty warnings.
   */
  @Test
  public void testGetWarningsWhenEmpty() {
    assertThat(details.getWarnings(), is(empty()));
  }

  // ====================
  // Setter Tests
  // ====================

  /**
   * Tests setWarnings with valid list.
   *
   * <p>Verifies that warnings can be set correctly.
   */
  @Test
  public void testSetWarnings() {
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);
    details.setWarnings(warnings);

    assertThat(details.getWarnings(), hasSize(2));
    assertThat(details.getWarnings(), containsInAnyOrder(WARNING_1, WARNING_2));
  }

  /**
   * Tests setWarnings with empty list.
   *
   * <p>Verifies that warnings can be cleared with empty list.
   */
  @Test
  public void testSetWarningsToEmpty() {
    details.setWarnings(Arrays.asList(WARNING_1, WARNING_2));
    details.setWarnings(Collections.emptyList());

    assertThat(details.getWarnings(), is(empty()));
  }

  /**
   * Tests setWarnings with null throws IllegalArgumentException.
   *
   * <p>Verifies that null warnings are not accepted in setter.
   */
  @Test
  public void testSetWarningsToNullThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> details.setWarnings(null));
  }

  /**
   * Tests replacing warnings.
   *
   * <p>Verifies that warnings can be replaced with new list.
   */
  @Test
  public void testReplaceWarnings() {
    List<String> firstWarnings = Arrays.asList(WARNING_1, WARNING_2);
    List<String> secondWarnings = Collections.singletonList(WARNING_3);

    details.setWarnings(firstWarnings);
    assertThat(details.getWarnings(), hasSize(2));

    details.setWarnings(secondWarnings);
    assertThat(details.getWarnings(), hasSize(1));
    assertThat(details.getWarnings(), contains(WARNING_3));
  }

  // ====================
  // Equals and HashCode Tests
  // ====================

  /**
   * Tests equals with same object.
   *
   * <p>Verifies that an object equals itself.
   */
  @Test
  public void testEqualsWithSameObject() {
    details.setWarnings(Arrays.asList(WARNING_1, WARNING_2));

    assertThat(details.equals(details), is(true));
  }

  /**
   * Tests equals with equivalent objects.
   *
   * <p>Verifies that two objects with same warnings are equal.
   */
  @Test
  public void testEqualsWithEquivalentObjects() {
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);
    SourceProcessingDetailsImpl details1 = new SourceProcessingDetailsImpl(warnings);
    SourceProcessingDetailsImpl details2 = new SourceProcessingDetailsImpl(warnings);

    assertThat(details1.equals(details2), is(true));
    assertThat(details2.equals(details1), is(true));
  }

  /**
   * Tests equals with different warning order.
   *
   * <p>Verifies that order doesn't matter for equality (uses containsAll).
   */
  @Test
  public void testEqualsWithDifferentWarningOrder() {
    SourceProcessingDetailsImpl details1 =
        new SourceProcessingDetailsImpl(Arrays.asList(WARNING_1, WARNING_2));
    SourceProcessingDetailsImpl details2 =
        new SourceProcessingDetailsImpl(Arrays.asList(WARNING_2, WARNING_1));

    // Based on implementation, equality checks size and containsAll, so order doesn't matter
    assertThat(details1.equals(details2), is(true));
  }

  /**
   * Tests equals with different warnings.
   *
   * <p>Verifies that different warnings make objects unequal.
   */
  @Test
  public void testEqualsWithDifferentWarnings() {
    SourceProcessingDetailsImpl details1 =
        new SourceProcessingDetailsImpl(Collections.singletonList(WARNING_1));
    SourceProcessingDetailsImpl details2 =
        new SourceProcessingDetailsImpl(Collections.singletonList(WARNING_2));

    assertThat(details1.equals(details2), is(false));
  }

  /**
   * Tests equals with different warning counts.
   *
   * <p>Verifies that different number of warnings makes objects unequal.
   */
  @Test
  public void testEqualsWithDifferentWarningCounts() {
    SourceProcessingDetailsImpl details1 =
        new SourceProcessingDetailsImpl(Collections.singletonList(WARNING_1));
    SourceProcessingDetailsImpl details2 =
        new SourceProcessingDetailsImpl(Arrays.asList(WARNING_1, WARNING_2));

    assertThat(details1.equals(details2), is(false));
  }

  /**
   * Tests equals with null.
   *
   * <p>Verifies that equals handles null gracefully.
   */
  @Test
  public void testEqualsWithNull() {
    details.setWarnings(Collections.singletonList(WARNING_1));

    assertThat(details.equals(null), is(false));
  }

  /**
   * Tests equals with different class.
   *
   * <p>Verifies that equals returns false for different types.
   */
  @Test
  public void testEqualsWithDifferentClass() {
    details.setWarnings(Collections.singletonList(WARNING_1));
    String differentObject = "not a SourceProcessingDetails";

    assertThat(details.equals(differentObject), is(false));
  }

  /**
   * Tests equals with empty warnings.
   *
   * <p>Verifies that two objects with empty warnings are equal.
   */
  @Test
  public void testEqualsWithEmptyWarnings() {
    SourceProcessingDetailsImpl details1 = new SourceProcessingDetailsImpl();
    SourceProcessingDetailsImpl details2 = new SourceProcessingDetailsImpl();

    assertThat(details1.equals(details2), is(true));
  }

  /**
   * Tests hashCode consistency with equals.
   *
   * <p>Verifies that equal objects have equal hashCodes.
   */
  @Test
  public void testHashCodeConsistencyWithEquals() {
    List<String> warnings = Arrays.asList(WARNING_1, WARNING_2);
    SourceProcessingDetailsImpl details1 = new SourceProcessingDetailsImpl(warnings);
    SourceProcessingDetailsImpl details2 = new SourceProcessingDetailsImpl(warnings);

    assertThat(details1.equals(details2), is(true));
    assertThat(details1.hashCode(), is(equalTo(details2.hashCode())));
  }

  /**
   * Tests hashCode with different warnings.
   *
   * <p>Verifies that different warnings produce different hashCodes (usually).
   */
  @Test
  public void testHashCodeWithDifferentWarnings() {
    SourceProcessingDetailsImpl details1 =
        new SourceProcessingDetailsImpl(Collections.singletonList(WARNING_1));
    SourceProcessingDetailsImpl details2 =
        new SourceProcessingDetailsImpl(Collections.singletonList(WARNING_2));

    // Different warnings should produce different hashCodes (highly likely but not guaranteed)
    assertThat(details1.hashCode(), is(not(equalTo(details2.hashCode()))));
  }

  /**
   * Tests hashCode consistency across calls.
   *
   * <p>Verifies that multiple hashCode calls return the same value.
   */
  @Test
  public void testHashCodeConsistency() {
    details.setWarnings(Arrays.asList(WARNING_1, WARNING_2));

    int hash1 = details.hashCode();
    int hash2 = details.hashCode();

    assertThat(hash1, is(equalTo(hash2)));
  }

  /**
   * Tests hashCode with empty warnings.
   *
   * <p>Verifies that empty warnings produce consistent hashCode.
   */
  @Test
  public void testHashCodeWithEmptyWarnings() {
    SourceProcessingDetailsImpl details1 = new SourceProcessingDetailsImpl();
    SourceProcessingDetailsImpl details2 = new SourceProcessingDetailsImpl();

    assertThat(details1.hashCode(), is(equalTo(details2.hashCode())));
  }

  // ====================
  // Interface Compliance Tests
  // ====================

  /**
   * Tests SourceProcessingDetails interface compliance.
   *
   * <p>Verifies that SourceProcessingDetailsImpl properly implements interface.
   */
  @Test
  public void testSourceProcessingDetailsInterfaceCompliance() {
    SourceProcessingDetails interfaceRef =
        new SourceProcessingDetailsImpl(Collections.singletonList(WARNING_1));

    assertThat(interfaceRef, is(notNullValue()));
    assertThat(interfaceRef.getWarnings(), hasSize(1));
  }

  // ====================
  // Edge Case Tests
  // ====================

  /**
   * Tests with very long warning message.
   *
   * <p>Verifies that long warning strings are handled correctly.
   */
  @Test
  public void testWithVeryLongWarning() {
    StringBuilder longWarning = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longWarning.append("Very long warning message. ");
    }

    SourceProcessingDetailsImpl detailsWithLong =
        new SourceProcessingDetailsImpl(Collections.singletonList(longWarning.toString()));

    assertThat(detailsWithLong.getWarnings(), hasSize(1));
    assertThat(detailsWithLong.getWarnings().get(0), is(equalTo(longWarning.toString())));
  }

  /**
   * Tests with empty string warning.
   *
   * <p>Verifies that empty string warnings are accepted.
   */
  @Test
  public void testWithEmptyStringWarning() {
    SourceProcessingDetailsImpl detailsWithEmpty =
        new SourceProcessingDetailsImpl(Collections.singletonList(""));

    assertThat(detailsWithEmpty.getWarnings(), hasSize(1));
    assertThat(detailsWithEmpty.getWarnings().get(0), is(equalTo("")));
  }

  /**
   * Tests with special characters in warnings.
   *
   * <p>Verifies that warnings with special characters are handled.
   */
  @Test
  public void testWithSpecialCharactersInWarnings() {
    String specialWarning = "Warning: Special chars !@#$%^&*(){}[]<>?/\\|~`";
    SourceProcessingDetailsImpl detailsWithSpecial =
        new SourceProcessingDetailsImpl(Collections.singletonList(specialWarning));

    assertThat(detailsWithSpecial.getWarnings().get(0), is(equalTo(specialWarning)));
  }

  /**
   * Tests with Unicode characters in warnings.
   *
   * <p>Verifies that warnings with Unicode are handled correctly.
   */
  @Test
  public void testWithUnicodeInWarnings() {
    String unicodeWarning = "Warning: Données non disponibles \u4E2D\u6587";
    SourceProcessingDetailsImpl detailsWithUnicode =
        new SourceProcessingDetailsImpl(Collections.singletonList(unicodeWarning));

    assertThat(detailsWithUnicode.getWarnings().get(0), is(equalTo(unicodeWarning)));
  }

  /**
   * Tests with duplicate warnings.
   *
   * <p>Verifies that duplicate warnings are preserved.
   */
  @Test
  public void testWithDuplicateWarnings() {
    List<String> duplicates = Arrays.asList(WARNING_1, WARNING_1, WARNING_2);
    SourceProcessingDetailsImpl detailsWithDuplicates = new SourceProcessingDetailsImpl(duplicates);

    assertThat(detailsWithDuplicates.getWarnings(), hasSize(3));
  }
}
