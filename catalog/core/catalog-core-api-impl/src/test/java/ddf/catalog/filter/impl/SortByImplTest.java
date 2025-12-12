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
package ddf.catalog.filter.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import org.geotools.api.filter.expression.PropertyName;
import org.geotools.api.filter.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test suite for {@link SortByImpl} class.
 *
 * <p>Tests all constructors, getters, and toString method.
 */
@ExtendWith(MockitoExtension.class)
public class SortByImplTest {

  private static final String PROPERTY_NAME = "modified";
  private static final String PROPERTY_NAME_2 = "title";

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with property name string and sort order string.
   *
   * <p>Verifies basic construction with string parameters.
   */
  @Test
  public void testConstructorWithPropertyNameStringAndSortOrderString() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, "ASCENDING");

    assertThat(sortBy, is(notNullValue()));
    assertThat(sortBy.getPropertyName(), is(notNullValue()));
    assertThat(sortBy.getPropertyName().getPropertyName(), is(equalTo(PROPERTY_NAME)));
    assertThat(sortBy.getSortOrder(), is(equalTo(SortOrder.ASCENDING)));
  }

  /**
   * Tests constructor with property name string and SortOrder enum.
   *
   * <p>Verifies construction with enum parameter.
   */
  @Test
  public void testConstructorWithPropertyNameStringAndSortOrder() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, SortOrder.DESCENDING);

    assertThat(sortBy.getPropertyName().getPropertyName(), is(equalTo(PROPERTY_NAME)));
    assertThat(sortBy.getSortOrder(), is(equalTo(SortOrder.DESCENDING)));
  }

  /**
   * Tests constructor with PropertyName object and SortOrder enum.
   *
   * <p>Verifies construction with PropertyName object.
   */
  @Test
  public void testConstructorWithPropertyNameObjectAndSortOrder() {
    PropertyName propertyName = mock(PropertyName.class);

    SortByImpl sortBy = new SortByImpl(propertyName, SortOrder.ASCENDING);

    assertThat(sortBy.getPropertyName(), is(equalTo(propertyName)));
    assertThat(sortBy.getSortOrder(), is(equalTo(SortOrder.ASCENDING)));
  }

  /**
   * Tests constructor with null property name string.
   *
   * <p>Verifies null property name is accepted.
   */
  @Test
  public void testConstructorWithNullPropertyNameString() {
    SortByImpl sortBy = new SortByImpl((String) null, SortOrder.ASCENDING);

    assertThat(sortBy.getPropertyName(), is(notNullValue()));
    assertThat(sortBy.getSortOrder(), is(equalTo(SortOrder.ASCENDING)));
  }

  /**
   * Tests constructor with null PropertyName object.
   *
   * <p>Verifies null PropertyName object is accepted.
   */
  @Test
  public void testConstructorWithNullPropertyNameObject() {
    SortByImpl sortBy = new SortByImpl((PropertyName) null, SortOrder.DESCENDING);

    assertThat(sortBy.getPropertyName(), is(equalTo(null)));
    assertThat(sortBy.getSortOrder(), is(equalTo(SortOrder.DESCENDING)));
  }

  /**
   * Tests constructor with null SortOrder.
   *
   * <p>Verifies null sort order is accepted.
   */
  @Test
  public void testConstructorWithNullSortOrder() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, (SortOrder) null);

    assertThat(sortBy.getPropertyName(), is(notNullValue()));
    assertThat(sortBy.getSortOrder(), is(equalTo(null)));
  }

  // ====================
  // Getter Tests
  // ====================

  /**
   * Tests getPropertyName returns correct property name.
   *
   * <p>Verifies property name retrieval.
   */
  @Test
  public void testGetPropertyName() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, SortOrder.ASCENDING);

    PropertyName propertyName = sortBy.getPropertyName();

    assertThat(propertyName, is(notNullValue()));
    assertThat(propertyName.getPropertyName(), is(equalTo(PROPERTY_NAME)));
  }

  /**
   * Tests getSortOrder returns correct sort order.
   *
   * <p>Verifies sort order retrieval.
   */
  @Test
  public void testGetSortOrder() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, SortOrder.DESCENDING);

    assertThat(sortBy.getSortOrder(), is(equalTo(SortOrder.DESCENDING)));
  }

  /**
   * Tests getPropertyName with different property names.
   *
   * <p>Verifies various property names.
   */
  @Test
  public void testGetPropertyNameWithDifferentNames() {
    SortByImpl sortBy1 = new SortByImpl(PROPERTY_NAME, SortOrder.ASCENDING);
    assertThat(sortBy1.getPropertyName().getPropertyName(), is(equalTo(PROPERTY_NAME)));

    SortByImpl sortBy2 = new SortByImpl(PROPERTY_NAME_2, SortOrder.ASCENDING);
    assertThat(sortBy2.getPropertyName().getPropertyName(), is(equalTo(PROPERTY_NAME_2)));

    SortByImpl sortBy3 = new SortByImpl("created", SortOrder.ASCENDING);
    assertThat(sortBy3.getPropertyName().getPropertyName(), is(equalTo("created")));
  }

  /**
   * Tests getSortOrder with different sort orders.
   *
   * <p>Verifies both sort order types.
   */
  @Test
  public void testGetSortOrderWithDifferentOrders() {
    SortByImpl ascending = new SortByImpl(PROPERTY_NAME, SortOrder.ASCENDING);
    assertThat(ascending.getSortOrder(), is(equalTo(SortOrder.ASCENDING)));

    SortByImpl descending = new SortByImpl(PROPERTY_NAME, SortOrder.DESCENDING);
    assertThat(descending.getSortOrder(), is(equalTo(SortOrder.DESCENDING)));
  }

  // ====================
  // ToString Tests
  // ====================

  /**
   * Tests toString returns non-null value.
   *
   * <p>Verifies toString implementation.
   */
  @Test
  public void testToString() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, SortOrder.ASCENDING);

    String result = sortBy.toString();

    assertThat(result, is(notNullValue()));
  }

  /**
   * Tests toString contains property name reference.
   *
   * <p>Verifies toString includes propertyName field.
   */
  @Test
  public void testToStringContainsPropertyNameReference() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, SortOrder.ASCENDING);

    String result = sortBy.toString();

    assertThat(result, containsString("propertyName="));
  }

  /**
   * Tests toString contains sort order.
   *
   * <p>Verifies toString includes sort order.
   */
  @Test
  public void testToStringContainsSortOrder() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, SortOrder.DESCENDING);

    String result = sortBy.toString();

    assertThat(result, containsString("DESCENDING"));
  }

  /**
   * Tests toString contains class name.
   *
   * <p>Verifies toString includes class identifier.
   */
  @Test
  public void testToStringContainsClassName() {
    SortByImpl sortBy = new SortByImpl(PROPERTY_NAME, SortOrder.ASCENDING);

    String result = sortBy.toString();

    assertThat(result, containsString("SortByImpl"));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests ascending sort.
   *
   * <p>Simulates sorting in ascending order.
   */
  @Test
  public void testAscendingSort() {
    SortByImpl sortBy = new SortByImpl("modified", SortOrder.ASCENDING);

    assertThat(sortBy.getPropertyName().getPropertyName(), is(equalTo("modified")));
    assertThat(sortBy.getSortOrder(), is(equalTo(SortOrder.ASCENDING)));
  }

  /**
   * Tests descending sort.
   *
   * <p>Simulates sorting in descending order.
   */
  @Test
  public void testDescendingSort() {
    SortByImpl sortBy = new SortByImpl("created", SortOrder.DESCENDING);

    assertThat(sortBy.getPropertyName().getPropertyName(), is(equalTo("created")));
    assertThat(sortBy.getSortOrder(), is(equalTo(SortOrder.DESCENDING)));
  }

  /**
   * Tests multiple sort criteria.
   *
   * <p>Simulates multiple sort by instances.
   */
  @Test
  public void testMultipleSortCriteria() {
    SortByImpl sort1 = new SortByImpl("title", SortOrder.ASCENDING);
    SortByImpl sort2 = new SortByImpl("modified", SortOrder.DESCENDING);
    SortByImpl sort3 = new SortByImpl("relevance", SortOrder.DESCENDING);

    assertThat(sort1.getPropertyName().getPropertyName(), is(equalTo("title")));
    assertThat(sort1.getSortOrder(), is(equalTo(SortOrder.ASCENDING)));

    assertThat(sort2.getPropertyName().getPropertyName(), is(equalTo("modified")));
    assertThat(sort2.getSortOrder(), is(equalTo(SortOrder.DESCENDING)));

    assertThat(sort3.getPropertyName().getPropertyName(), is(equalTo("relevance")));
    assertThat(sort3.getSortOrder(), is(equalTo(SortOrder.DESCENDING)));
  }

  /**
   * Tests sort by with qualified property names.
   *
   * <p>Verifies qualified property name handling.
   */
  @Test
  public void testSortByWithQualifiedPropertyNames() {
    SortByImpl sortBy1 = new SortByImpl("metacard.modified", SortOrder.ASCENDING);
    assertThat(sortBy1.getPropertyName().getPropertyName(), is(equalTo("metacard.modified")));

    SortByImpl sortBy2 = new SortByImpl("ext.custom.field", SortOrder.DESCENDING);
    assertThat(sortBy2.getPropertyName().getPropertyName(), is(equalTo("ext.custom.field")));
  }

  /**
   * Tests sort by with common catalog fields.
   *
   * <p>Simulates sorting by common metadata fields.
   */
  @Test
  public void testSortByCommonFields() {
    SortByImpl sortByTitle = new SortByImpl("title", SortOrder.ASCENDING);
    assertThat(sortByTitle.getPropertyName().getPropertyName(), is(equalTo("title")));

    SortByImpl sortByModified = new SortByImpl("modified", SortOrder.DESCENDING);
    assertThat(sortByModified.getPropertyName().getPropertyName(), is(equalTo("modified")));

    SortByImpl sortByCreated = new SortByImpl("created", SortOrder.DESCENDING);
    assertThat(sortByCreated.getPropertyName().getPropertyName(), is(equalTo("created")));

    SortByImpl sortByRelevance = new SortByImpl("relevance", SortOrder.DESCENDING);
    assertThat(sortByRelevance.getPropertyName().getPropertyName(), is(equalTo("relevance")));
  }
}
