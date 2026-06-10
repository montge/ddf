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
package org.codice.ddf.catalog.plugin.expiration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Core;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.plugin.PluginExecutionException;
import ddf.catalog.plugin.StopProcessingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExpirationDatePluginTest {

  private static final String DATE_FORMAT = "MM-dd-yyyy HH:mm:ss.SSS";

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormat.forPattern(DATE_FORMAT);

  private static final DateTime CREATED_DATE =
      DATE_TIME_FORMATTER.parseDateTime("09-01-1990 00:00:00.000");

  private static final DateTime ORIG_EXPIRATION_DATE =
      DATE_TIME_FORMATTER.parseDateTime("09-10-1990 00:00:00.000");

  private static final int DAYS = 10;

  private ExpirationDatePlugin expirationDatePlugin;

  @Mock private CreateRequest mockCreateRequest;

  @BeforeEach
  public void setup() {
    expirationDatePlugin = new ExpirationDatePlugin();
    expirationDatePlugin.setOffsetFromCreatedDate(DAYS);
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the empty expiration date is not overwritten when the overwriteIfBlank option
   * is not selected, and remains unchanged.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testEmptyExpiration() throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(false);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 1;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    assertThatExpirationIsEmpty(metacards.get(0));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the empty expiration date is overwritten with the newly calculated expiration
   * date based on the configurable offset in days, since the overwriteIfBlank option is selected.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testEmptyExpirationOverwriteIfBlank()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 1;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    assertThatExpirationIsNewOffset(metacards.get(0));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the empty expiration date is not overwritten when the overwriteIfBlank option
   * is not selected, and remains unchanged. This test additionally sets the overwriteIfExists
   * option to verify it does not interfere with the overwriteIfBlank option.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testEmptyExpirationOverwriteIfExists()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(false);
    expirationDatePlugin.setOverwriteIfExists(true);

    int size = 1;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    assertThatExpirationIsEmpty(metacards.get(0));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the empty expiration date is overwritten with the newly calculated expiration
   * date based on the configurable offset in days, since the overwriteIfBlank option is selected.
   * This test additionally sets the overwriteIfExists option to verify it does not interfere with
   * the overwriteIfBlank option.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testEmptyExpirationOverwriteIfBlankOverwriteIfExists()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(true);

    int size = 1;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    assertThatExpirationIsNewOffset(metacards.get(0));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the existing, non-empty expiration date is not overwritten when the
   * overwriteIfExists option is not selected, and remains unchanged.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testExistingExpiration() throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(false);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 1;
    when(mockCreateRequest.getMetacards()).thenReturn(createMockMetacardsWithExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    assertThatExpirationIsUnchanged(metacards.get(0));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the existing, non-empty expiration date is not overwritten when the
   * overwriteIfExists option is not selected, and remains unchanged. This test additionally sets
   * the overwriteIfBlank option to verify it does not interfere with the overwriteIfExists option.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testExistingExpirationOverwriteIfBlank()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 1;
    when(mockCreateRequest.getMetacards()).thenReturn(createMockMetacardsWithExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    assertThatExpirationIsUnchanged(metacards.get(0));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the existing, non-empty expiration date is overwritten with the newly
   * calculated expiration date based on the configurable offset in days, since the
   * overwriteIfExists option is selected.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testExistingExpirationOverwriteIfExists()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(false);
    expirationDatePlugin.setOverwriteIfExists(true);

    int size = 1;
    when(mockCreateRequest.getMetacards()).thenReturn(createMockMetacardsWithExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    assertThatExpirationIsNewOffset(metacards.get(0));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the existing, non-empty expiration date is overwritten with the newly
   * calculated expiration date based on the configurable offset in days, since the
   * overwriteIfExists option is selected. This test additionally sets the overwriteIfBlank option
   * to verify it does not interfere with the overwriteIfExists option.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testExistingExpirationOverwriteIfBlankOverwriteIfExists()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(true);

    int size = 1;
    when(mockCreateRequest.getMetacards()).thenReturn(createMockMetacardsWithExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    assertThatExpirationIsNewOffset(metacards.get(0));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the empty expiration date is not overwritten when the overwriteIfBlank option
   * is not selected, and remains unchanged. This test uses multiple metacards for ingest
   * processing.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testEmptyExpirationMultipleMetacards()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(false);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 5;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));
    mockCreateRequest.getMetacards().stream().forEach(m -> assertThatExpirationIsEmpty(m));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the empty expiration date is overwritten with the newly calculated expiration
   * date based on the configurable offset in days, since the overwriteIfBlank option is selected.
   * This test uses multiple metacards for ingest processing.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testEmptyExpirationOverwriteIfBlankMultipleMetacards()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 5;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    assertThat(mockCreateRequest.getMetacards(), hasSize(size));
    mockCreateRequest.getMetacards().stream().forEach(m -> assertThatExpirationIsNewOffset(m));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the existing, non-empty expiration date is not overwritten when the
   * overwriteIfExists option is not selected, and remains unchanged. This test uses multiple
   * metacards for ingest processing.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testExistingExpirationMultipleMetacards()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(false);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 5;
    when(mockCreateRequest.getMetacards()).thenReturn(createMockMetacardsWithExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    assertThat(mockCreateRequest.getMetacards(), hasSize(size));
    mockCreateRequest.getMetacards().stream().forEach(m -> assertThatExpirationIsUnchanged(m));
  }

  /**
   * Tests {@link ExpirationDatePlugin#process(CreateRequest)}
   *
   * <p>Verifies that the existing, non-empty expiration date is overwritten with the newly
   * calculated expiration date based on the configurable offset in days, since the
   * overwriteIfExists option is selected. This test uses multiple metacards for ingest processing.
   *
   * @throws PluginExecutionException
   * @throws StopProcessingException
   */
  @Test
  public void testExistingExpirationOverwriteIfExistsMultipleMetacards()
      throws PluginExecutionException, StopProcessingException {

    // Items configured via the admin console
    expirationDatePlugin.setOverwriteIfBlank(false);
    expirationDatePlugin.setOverwriteIfExists(true);

    int size = 5;
    when(mockCreateRequest.getMetacards()).thenReturn(createMockMetacardsWithExpirationDate(size));

    // Perform Test
    expirationDatePlugin.process(mockCreateRequest);

    // Verify
    assertThat(mockCreateRequest.getMetacards(), hasSize(size));
    mockCreateRequest.getMetacards().stream().forEach(m -> assertThatExpirationIsNewOffset(m));
  }

  private List<Metacard> createMockMetacardsWithNoExpirationDate(int number) {

    List<Metacard> mockMetacards = new ArrayList(number);

    for (int i = 0; i < number; i++) {
      Metacard mockMetacard = new MetacardImpl();
      Attribute id = new AttributeImpl(Metacard.ID, Integer.toString(i));
      mockMetacard.setAttribute(id);
      Attribute title = new AttributeImpl(Metacard.TITLE, Integer.toString(i));
      mockMetacard.setAttribute(title);
      Attribute createdDate = new AttributeImpl(Core.METACARD_CREATED, CREATED_DATE.toDate());
      mockMetacard.setAttribute(createdDate);
      mockMetacards.add(mockMetacard);
    }

    return mockMetacards;
  }

  private List<Metacard> createMockMetacardsWithExpirationDate(int number) {

    List<Metacard> mockMetacards = new ArrayList(number);

    for (int i = 0; i < number; i++) {
      Metacard mockMetacard = new MetacardImpl();
      Attribute id = new AttributeImpl(Metacard.ID, Integer.toString(i));
      mockMetacard.setAttribute(id);
      Attribute title = new AttributeImpl(Metacard.TITLE, Integer.toString(i));
      mockMetacard.setAttribute(title);
      Attribute createdDate = new AttributeImpl(Core.METACARD_CREATED, CREATED_DATE.toDate());
      mockMetacard.setAttribute(createdDate);
      Attribute expirationDate =
          new AttributeImpl(Metacard.EXPIRATION, ORIG_EXPIRATION_DATE.toDate());
      mockMetacard.setAttribute(expirationDate);
      mockMetacards.add(mockMetacard);
    }

    return mockMetacards;
  }

  private void assertThatExpirationIsEmpty(Metacard metacard) {
    assertThat(metacard, notNullValue());
    assertThat(metacard.getExpirationDate(), nullValue());
  }

  private void assertThatExpirationIsUnchanged(Metacard metacard) {
    assertThat(metacard, notNullValue());
    DateTime unchangedExpirationDate = new DateTime(metacard.getExpirationDate());
    assertThat(unchangedExpirationDate.equals(ORIG_EXPIRATION_DATE), is(true));
  }

  private void assertThatExpirationIsNewOffset(Metacard metacard) {
    assertThat(metacard, notNullValue());
    DateTime newExpirationDate = new DateTime(metacard.getExpirationDate());
    assertThat(newExpirationDate.equals(CREATED_DATE.plusDays(DAYS)), is(true));
  }

  @Test
  public void testProcessUpdateRequest() throws PluginExecutionException, StopProcessingException {
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    ddf.catalog.operation.UpdateRequest updateRequest =
        mock(ddf.catalog.operation.UpdateRequest.class);

    ddf.catalog.operation.UpdateRequest result = expirationDatePlugin.process(updateRequest);

    // UpdateRequest should pass through unchanged
    assertThat(result, is(updateRequest));
  }

  @Test
  public void testProcessDeleteRequest() throws PluginExecutionException, StopProcessingException {
    ddf.catalog.operation.DeleteRequest deleteRequest =
        mock(ddf.catalog.operation.DeleteRequest.class);

    ddf.catalog.operation.DeleteRequest result = expirationDatePlugin.process(deleteRequest);

    // DeleteRequest should pass through unchanged
    assertThat(result, is(deleteRequest));
  }

  @Test
  public void testProcessNullCreateRequest() {
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    assertThrows(
        PluginExecutionException.class, () -> expirationDatePlugin.process((CreateRequest) null));
  }

  @Test
  public void testProcessCreateRequestWithNullMetacards() {
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    when(mockCreateRequest.getMetacards()).thenReturn(null);

    assertThrows(
        PluginExecutionException.class, () -> expirationDatePlugin.process(mockCreateRequest));
  }

  @Test
  public void testProcessCreateRequestWithEmptyMetacardsList() {
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    when(mockCreateRequest.getMetacards()).thenReturn(new ArrayList<>());

    assertThrows(
        PluginExecutionException.class, () -> expirationDatePlugin.process(mockCreateRequest));
  }

  @Test
  public void testMetacardWithoutCreatedDate()
      throws PluginExecutionException, StopProcessingException {
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, "test-id"));
    metacard.setAttribute(new AttributeImpl(Metacard.TITLE, "test-title"));
    // No created date set

    when(mockCreateRequest.getMetacards()).thenReturn(Collections.singletonList(metacard));

    expirationDatePlugin.process(mockCreateRequest);

    // Should set expiration date based on current time
    assertThat(metacard.getExpirationDate(), notNullValue());
  }

  @Test
  public void testMetacardWithNonDateCreatedAttribute()
      throws PluginExecutionException, StopProcessingException {
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, "test-id"));
    metacard.setAttribute(new AttributeImpl(Metacard.TITLE, "test-title"));
    // Set created date to non-Date type (should be handled gracefully)
    metacard.setAttribute(new AttributeImpl(Core.METACARD_CREATED, "not-a-date"));

    when(mockCreateRequest.getMetacards()).thenReturn(Collections.singletonList(metacard));

    expirationDatePlugin.process(mockCreateRequest);

    // Should set expiration date based on current time (fallback)
    assertThat(metacard.getExpirationDate(), notNullValue());
  }

  @Test
  public void testNegativeOffsetFromCreatedDate()
      throws PluginExecutionException, StopProcessingException {
    expirationDatePlugin.setOffsetFromCreatedDate(-10);
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 1;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    expirationDatePlugin.process(mockCreateRequest);

    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));

    Metacard metacard = metacards.get(0);
    assertThat(metacard.getExpirationDate(), notNullValue());

    DateTime expirationDate = new DateTime(metacard.getExpirationDate());
    // Expiration should be 10 days before created date
    assertThat(expirationDate.equals(CREATED_DATE.minusDays(10)), is(true));
  }

  @Test
  public void testZeroOffsetFromCreatedDate()
      throws PluginExecutionException, StopProcessingException {
    expirationDatePlugin.setOffsetFromCreatedDate(0);
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 1;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    expirationDatePlugin.process(mockCreateRequest);

    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));

    Metacard metacard = metacards.get(0);
    assertThat(metacard.getExpirationDate(), notNullValue());

    DateTime expirationDate = new DateTime(metacard.getExpirationDate());
    // Expiration should equal created date
    assertThat(expirationDate.equals(CREATED_DATE), is(true));
  }

  @Test
  public void testLargeOffsetFromCreatedDate()
      throws PluginExecutionException, StopProcessingException {
    expirationDatePlugin.setOffsetFromCreatedDate(3650); // 10 years
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    int size = 1;
    when(mockCreateRequest.getMetacards())
        .thenReturn(createMockMetacardsWithNoExpirationDate(size));

    expirationDatePlugin.process(mockCreateRequest);

    List<Metacard> metacards = mockCreateRequest.getMetacards();
    assertThat(metacards, hasSize(size));

    Metacard metacard = metacards.get(0);
    assertThat(metacard.getExpirationDate(), notNullValue());

    DateTime expirationDate = new DateTime(metacard.getExpirationDate());
    assertThat(expirationDate.equals(CREATED_DATE.plusDays(3650)), is(true));
  }

  @Test
  public void testSettersAreLogged() throws PluginExecutionException, StopProcessingException {
    // Setters can be called multiple times; the last value set must be the one applied.
    expirationDatePlugin.setOffsetFromCreatedDate(5);
    expirationDatePlugin.setOverwriteIfBlank(false);
    expirationDatePlugin.setOverwriteIfExists(true);

    expirationDatePlugin.setOffsetFromCreatedDate(15);
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    when(mockCreateRequest.getMetacards()).thenReturn(createMockMetacardsWithNoExpirationDate(1));

    expirationDatePlugin.process(mockCreateRequest);

    // The blank expiration is overwritten using the most recently set offset (15 days).
    Metacard metacard = mockCreateRequest.getMetacards().get(0);
    assertThat(metacard.getExpirationDate(), notNullValue());
    DateTime expirationDate = new DateTime(metacard.getExpirationDate());
    assertThat(expirationDate.equals(CREATED_DATE.plusDays(15)), is(true));
  }

  @Test
  public void testMixedMetacardsWithAndWithoutExpirationDates()
      throws PluginExecutionException, StopProcessingException {
    expirationDatePlugin.setOverwriteIfBlank(true);
    expirationDatePlugin.setOverwriteIfExists(false);

    List<Metacard> metacards = new ArrayList<>();

    // Metacard with no expiration
    Metacard metacard1 = new MetacardImpl();
    metacard1.setAttribute(new AttributeImpl(Metacard.ID, "1"));
    metacard1.setAttribute(new AttributeImpl(Core.METACARD_CREATED, CREATED_DATE.toDate()));
    metacards.add(metacard1);

    // Metacard with existing expiration
    Metacard metacard2 = new MetacardImpl();
    metacard2.setAttribute(new AttributeImpl(Metacard.ID, "2"));
    metacard2.setAttribute(new AttributeImpl(Core.METACARD_CREATED, CREATED_DATE.toDate()));
    metacard2.setAttribute(new AttributeImpl(Metacard.EXPIRATION, ORIG_EXPIRATION_DATE.toDate()));
    metacards.add(metacard2);

    // Metacard with no expiration
    Metacard metacard3 = new MetacardImpl();
    metacard3.setAttribute(new AttributeImpl(Metacard.ID, "3"));
    metacard3.setAttribute(new AttributeImpl(Core.METACARD_CREATED, CREATED_DATE.toDate()));
    metacards.add(metacard3);

    when(mockCreateRequest.getMetacards()).thenReturn(metacards);

    expirationDatePlugin.process(mockCreateRequest);

    // Verify first and third have new expiration, second is unchanged
    assertThatExpirationIsNewOffset(metacard1);
    assertThatExpirationIsUnchanged(metacard2);
    assertThatExpirationIsNewOffset(metacard3);
  }
}
