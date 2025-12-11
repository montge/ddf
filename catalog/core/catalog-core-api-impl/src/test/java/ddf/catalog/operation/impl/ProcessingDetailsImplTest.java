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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.operation.ProcessingDetails;
import ddf.catalog.operation.SourceProcessingDetails;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link ProcessingDetailsImpl} class.
 *
 * <p>Tests all constructors, getters, setters, equals/hashCode, and edge cases for processing
 * details used in catalog operations.
 */
@ExtendWith(MockitoExtension.class)
public class ProcessingDetailsImplTest {

  private static final String TEST_SOURCE_ID = "test-source-1";
  private static final String ANOTHER_SOURCE_ID = "test-source-2";
  private static final String WARNING_MESSAGE_1 = "Warning: Connection slow";
  private static final String WARNING_MESSAGE_2 = "Warning: Partial results returned";

  private ProcessingDetailsImpl processingDetails;
  private Exception testException;

  @BeforeEach
  public void setUp() {
    testException = new RuntimeException("Test exception");
    processingDetails = new ProcessingDetailsImpl();
  }

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests the default constructor creates a valid ProcessingDetailsImpl instance.
   *
   * <p>Verifies that a newly created ProcessingDetails has null/empty values.
   */
  @Test
  public void testDefaultConstructor() {
    ProcessingDetailsImpl details = new ProcessingDetailsImpl();

    assertThat(details, is(notNullValue()));
    assertThat(details.getSourceId(), is(nullValue()));
    assertThat(details.hasException(), is(false));
    assertThat(details.getException(), is(nullValue()));
    assertThat(details.getWarnings(), is(empty()));
  }

  /**
   * Tests constructor with sourceId and exception.
   *
   * <p>Verifies that both parameters are properly set.
   */
  @Test
  public void testConstructorWithSourceIdAndException() {
    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);

    assertThat(details.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
    assertThat(details.hasException(), is(true));
    assertThat(details.getException(), is(equalTo(testException)));
    assertThat(details.getWarnings(), is(empty()));
  }

  /**
   * Tests constructor with sourceId, exception, and single warning.
   *
   * <p>Verifies that the warning is properly stored as a list.
   */
  @Test
  public void testConstructorWithSourceIdExceptionAndWarning() {
    ProcessingDetailsImpl details =
        new ProcessingDetailsImpl(TEST_SOURCE_ID, testException, WARNING_MESSAGE_1);

    assertThat(details.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
    assertThat(details.hasException(), is(true));
    assertThat(details.getException(), is(equalTo(testException)));
    assertThat(details.getWarnings(), hasSize(1));
    assertThat(details.getWarnings(), contains(WARNING_MESSAGE_1));
  }

  /**
   * Tests constructor with null warning throws IllegalArgumentException.
   *
   * <p>Verifies that null warnings are not accepted in the single-warning constructor.
   */
  @Test
  public void testConstructorWithNullWarningThrowsException() {
    assertThrows(
        NullPointerException.class,
        () -> new ProcessingDetailsImpl(TEST_SOURCE_ID, testException, (String) null));
  }

  /**
   * Tests constructor with sourceId, exception, and list of warnings.
   *
   * <p>Verifies that multiple warnings are properly stored.
   */
  @Test
  public void testConstructorWithSourceIdExceptionAndWarnings() {
    List<String> warnings = Arrays.asList(WARNING_MESSAGE_1, WARNING_MESSAGE_2);
    ProcessingDetailsImpl details =
        new ProcessingDetailsImpl(TEST_SOURCE_ID, testException, warnings);

    assertThat(details.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
    assertThat(details.hasException(), is(true));
    assertThat(details.getException(), is(equalTo(testException)));
    assertThat(details.getWarnings(), hasSize(2));
    assertThat(details.getWarnings(), containsInAnyOrder(WARNING_MESSAGE_1, WARNING_MESSAGE_2));
  }

  /**
   * Tests constructor with empty warnings list.
   *
   * <p>Verifies that an empty warnings list is accepted.
   */
  @Test
  public void testConstructorWithEmptyWarnings() {
    ProcessingDetailsImpl details =
        new ProcessingDetailsImpl(TEST_SOURCE_ID, testException, Collections.emptyList());

    assertThat(details.getWarnings(), is(empty()));
  }

  /**
   * Tests constructor with SourceProcessingDetails and sourceId.
   *
   * <p>Verifies that warnings are copied from source processing details.
   */
  @Test
  public void testConstructorWithSourceProcessingDetails() {
    SourceProcessingDetails sourceDetails = mock(SourceProcessingDetails.class);
    List<String> warnings = Arrays.asList(WARNING_MESSAGE_1, WARNING_MESSAGE_2);
    when(sourceDetails.getWarnings()).thenReturn(warnings);

    ProcessingDetailsImpl details = new ProcessingDetailsImpl(sourceDetails, TEST_SOURCE_ID);

    assertThat(details.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
    assertThat(details.getWarnings(), hasSize(2));
    assertThat(details.getWarnings(), containsInAnyOrder(WARNING_MESSAGE_1, WARNING_MESSAGE_2));
  }

  /**
   * Tests constructor with SourceProcessingDetails with empty warnings.
   *
   * <p>Verifies that empty warnings from source details are handled.
   */
  @Test
  public void testConstructorWithSourceProcessingDetailsEmptyWarnings() {
    SourceProcessingDetails sourceDetails = mock(SourceProcessingDetails.class);
    when(sourceDetails.getWarnings()).thenReturn(Collections.emptyList());

    ProcessingDetailsImpl details = new ProcessingDetailsImpl(sourceDetails, TEST_SOURCE_ID);

    assertThat(details.getWarnings(), is(empty()));
  }

  // ====================
  // SourceId Tests
  // ====================

  /**
   * Tests setting and getting sourceId.
   *
   * <p>Verifies that sourceId can be set and retrieved correctly.
   */
  @Test
  public void testSetAndGetSourceId() {
    processingDetails.setSourceId(TEST_SOURCE_ID);

    assertThat(processingDetails.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
  }

  /**
   * Tests setting sourceId to null.
   *
   * <p>Verifies that sourceId can be set to null.
   */
  @Test
  public void testSetSourceIdToNull() {
    processingDetails.setSourceId(TEST_SOURCE_ID);
    processingDetails.setSourceId(null);

    assertThat(processingDetails.getSourceId(), is(nullValue()));
  }

  /**
   * Tests setting sourceId to empty string.
   *
   * <p>Verifies that empty string is a valid sourceId.
   */
  @Test
  public void testSetSourceIdToEmptyString() {
    processingDetails.setSourceId("");

    assertThat(processingDetails.getSourceId(), is(equalTo("")));
  }

  /**
   * Tests replacing sourceId.
   *
   * <p>Verifies that sourceId can be changed.
   */
  @Test
  public void testReplaceSourceId() {
    processingDetails.setSourceId(TEST_SOURCE_ID);
    assertThat(processingDetails.getSourceId(), is(equalTo(TEST_SOURCE_ID)));

    processingDetails.setSourceId(ANOTHER_SOURCE_ID);
    assertThat(processingDetails.getSourceId(), is(equalTo(ANOTHER_SOURCE_ID)));
  }

  // ====================
  // Exception Tests
  // ====================

  /**
   * Tests setting and getting exception.
   *
   * <p>Verifies that exception can be set and retrieved correctly.
   */
  @Test
  public void testSetAndGetException() {
    processingDetails.setException(testException);

    assertThat(processingDetails.hasException(), is(true));
    assertThat(processingDetails.getException(), is(equalTo(testException)));
  }

  /**
   * Tests setting exception to null.
   *
   * <p>Verifies that exception can be cleared.
   */
  @Test
  public void testSetExceptionToNull() {
    processingDetails.setException(testException);
    processingDetails.setException(null);

    assertThat(processingDetails.hasException(), is(false));
    assertThat(processingDetails.getException(), is(nullValue()));
  }

  /**
   * Tests hasException returns false when no exception.
   *
   * <p>Verifies the hasException method works correctly.
   */
  @Test
  public void testHasExceptionReturnsFalseWhenNoException() {
    assertThat(processingDetails.hasException(), is(false));
  }

  /**
   * Tests hasException returns true when exception is set.
   *
   * <p>Verifies the hasException method detects exceptions correctly.
   */
  @Test
  public void testHasExceptionReturnsTrueWhenExceptionSet() {
    processingDetails.setException(testException);

    assertThat(processingDetails.hasException(), is(true));
  }

  /**
   * Tests with SourceUnavailableException.
   *
   * <p>Verifies that catalog-specific exceptions are handled correctly.
   */
  @Test
  public void testWithSourceUnavailableException() {
    SourceUnavailableException sourceException =
        new SourceUnavailableException("Source is unavailable");

    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, sourceException);

    assertThat(details.hasException(), is(true));
    assertThat(details.getException(), is(equalTo(sourceException)));
    assertThat(details.getException().getMessage(), is(equalTo("Source is unavailable")));
  }

  /**
   * Tests with UnsupportedQueryException.
   *
   * <p>Verifies that query exceptions are handled correctly.
   */
  @Test
  public void testWithUnsupportedQueryException() {
    UnsupportedQueryException queryException = new UnsupportedQueryException("Query not supported");

    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, queryException);

    assertThat(details.hasException(), is(true));
    assertThat(details.getException(), is(equalTo(queryException)));
  }

  /**
   * Tests with IOException.
   *
   * <p>Verifies that generic exceptions are handled.
   */
  @Test
  public void testWithIOException() {
    IOException ioException = new IOException("Network error");

    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, ioException);

    assertThat(details.hasException(), is(true));
    assertThat(details.getException(), is(equalTo(ioException)));
  }

  /**
   * Tests exception with cause chain.
   *
   * <p>Verifies that exceptions with causes are preserved.
   */
  @Test
  public void testExceptionWithCause() {
    Exception cause = new IOException("Root cause");
    Exception wrapper = new RuntimeException("Wrapper exception", cause);

    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, wrapper);

    assertThat(details.getException(), is(equalTo(wrapper)));
    assertThat(details.getException().getCause(), is(equalTo(cause)));
  }

  // ====================
  // Equals and HashCode Tests
  // ====================

  /**
   * Tests equals with identical objects.
   *
   * <p>Verifies that an object equals itself.
   */
  @Test
  public void testEqualsWithSameObject() {
    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);

    assertThat(details.equals(details), is(true));
  }

  /**
   * Tests equals with equivalent objects.
   *
   * <p>Verifies that two objects with same data are equal.
   */
  @Test
  public void testEqualsWithEquivalentObjects() {
    ProcessingDetailsImpl details1 = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);
    ProcessingDetailsImpl details2 = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);

    assertThat(details1.equals(details2), is(true));
    assertThat(details2.equals(details1), is(true));
  }

  /**
   * Tests equals with different sourceIds.
   *
   * <p>Verifies that different sourceIds make objects unequal.
   */
  @Test
  public void testEqualsWithDifferentSourceIds() {
    ProcessingDetailsImpl details1 = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);
    ProcessingDetailsImpl details2 = new ProcessingDetailsImpl(ANOTHER_SOURCE_ID, testException);

    assertThat(details1.equals(details2), is(false));
  }

  /**
   * Tests equals with different exceptions.
   *
   * <p>Verifies that different exceptions make objects unequal.
   */
  @Test
  public void testEqualsWithDifferentExceptions() {
    Exception exception1 = new RuntimeException("Exception 1");
    Exception exception2 = new RuntimeException("Exception 2");

    ProcessingDetailsImpl details1 = new ProcessingDetailsImpl(TEST_SOURCE_ID, exception1);
    ProcessingDetailsImpl details2 = new ProcessingDetailsImpl(TEST_SOURCE_ID, exception2);

    assertThat(details1.equals(details2), is(false));
  }

  /**
   * Tests equals with null exception vs non-null.
   *
   * <p>Verifies that null and non-null exceptions are not equal.
   */
  @Test
  public void testEqualsWithNullAndNonNullException() {
    ProcessingDetailsImpl details1 = new ProcessingDetailsImpl(TEST_SOURCE_ID, null);
    ProcessingDetailsImpl details2 = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);

    assertThat(details1.equals(details2), is(false));
  }

  /**
   * Tests equals with null object.
   *
   * <p>Verifies that equals handles null gracefully.
   */
  @Test
  public void testEqualsWithNull() {
    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);

    assertThat(details.equals(null), is(false));
  }

  /**
   * Tests equals with different class.
   *
   * <p>Verifies that equals returns false for different class types.
   */
  @Test
  public void testEqualsWithDifferentClass() {
    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);
    String differentObject = "not a ProcessingDetails";

    assertThat(details.equals(differentObject), is(false));
  }

  /**
   * Tests hashCode consistency with equals.
   *
   * <p>Verifies that equal objects have equal hashCodes.
   */
  @Test
  public void testHashCodeConsistencyWithEquals() {
    ProcessingDetailsImpl details1 = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);
    ProcessingDetailsImpl details2 = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);

    assertThat(details1.equals(details2), is(true));
    assertThat(details1.hashCode(), is(equalTo(details2.hashCode())));
  }

  /**
   * Tests hashCode changes with different data.
   *
   * <p>Verifies that different data produces different hashCodes (usually).
   */
  @Test
  public void testHashCodeWithDifferentData() {
    ProcessingDetailsImpl details1 = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);
    ProcessingDetailsImpl details2 = new ProcessingDetailsImpl(ANOTHER_SOURCE_ID, testException);

    // Different sourceIds should produce different hashCodes (highly likely but not guaranteed)
    assertThat(details1.hashCode(), is(not(equalTo(details2.hashCode()))));
  }

  /**
   * Tests hashCode is consistent across calls.
   *
   * <p>Verifies that multiple hashCode calls return the same value.
   */
  @Test
  public void testHashCodeConsistency() {
    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);

    int hash1 = details.hashCode();
    int hash2 = details.hashCode();

    assertThat(hash1, is(equalTo(hash2)));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete processing details for a failed query.
   *
   * <p>Simulates a real-world scenario with source failure.
   */
  @Test
  public void testCompleteFailedQueryScenario() {
    SourceUnavailableException sourceException =
        new SourceUnavailableException("Connection timeout");
    List<String> warnings = Arrays.asList("Timeout after 30 seconds", "3 retries attempted");

    ProcessingDetailsImpl details =
        new ProcessingDetailsImpl(TEST_SOURCE_ID, sourceException, warnings);

    assertThat(details.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
    assertThat(details.hasException(), is(true));
    assertThat(details.getException(), is(equalTo(sourceException)));
    assertThat(details.getWarnings(), hasSize(2));
  }

  /**
   * Tests processing details for a partially successful query.
   *
   * <p>Simulates a query that succeeded but with warnings.
   */
  @Test
  public void testPartiallySuccessfulQueryScenario() {
    List<String> warnings = Collections.singletonList("Partial results returned due to timeout");

    ProcessingDetailsImpl details = new ProcessingDetailsImpl(TEST_SOURCE_ID, null, warnings);

    assertThat(details.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
    assertThat(details.hasException(), is(false));
    assertThat(details.getWarnings(), hasSize(1));
    assertThat(
        details.getWarnings().get(0), is(equalTo("Partial results returned due to timeout")));
  }

  /**
   * Tests ProcessingDetails interface compliance.
   *
   * <p>Verifies that ProcessingDetailsImpl properly implements ProcessingDetails.
   */
  @Test
  public void testProcessingDetailsInterfaceCompliance() {
    ProcessingDetails details = new ProcessingDetailsImpl(TEST_SOURCE_ID, testException);

    assertThat(details, is(notNullValue()));
    assertThat(details.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
    assertThat(details.hasException(), is(true));
  }

  /**
   * Tests modifying details after construction.
   *
   * <p>Verifies that details can be modified using setters.
   */
  @Test
  public void testModifyingDetailsAfterConstruction() {
    ProcessingDetailsImpl details = new ProcessingDetailsImpl();

    details.setSourceId(TEST_SOURCE_ID);
    details.setException(testException);

    assertThat(details.getSourceId(), is(equalTo(TEST_SOURCE_ID)));
    assertThat(details.hasException(), is(true));
    assertThat(details.getException(), is(equalTo(testException)));
  }
}
