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
package ddf.catalog.source.solr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ddf.catalog.filter.impl.SimpleFilterDelegate.ComparisonPropertyOperation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PropertyExistsDelegateTest {

  @Test
  public void testConstructorWithVarargs() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property1", "property2");
    assertTrue(delegate.comparisonOperation("property1", null, null, null));
    assertTrue(delegate.comparisonOperation("property2", null, null, null));
    assertFalse(delegate.comparisonOperation("property3", null, null, null));
  }

  @Test
  public void testConstructorWithEmptyVarargs() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate();
    assertFalse(delegate.comparisonOperation("anyProperty", null, null, null));
  }

  @Test
  public void testConstructorWithList() {
    List<String> propertyNames = Arrays.asList("prop1", "prop2", "prop3");
    PropertyExistsDelegate delegate = new PropertyExistsDelegate(propertyNames);
    assertTrue(delegate.comparisonOperation("prop1", null, null, null));
    assertTrue(delegate.comparisonOperation("prop2", null, null, null));
    assertTrue(delegate.comparisonOperation("prop3", null, null, null));
    assertFalse(delegate.comparisonOperation("prop4", null, null, null));
  }

  @Test
  public void testConstructorWithEmptyList() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate(Collections.emptyList());
    assertFalse(delegate.comparisonOperation("anyProperty", null, null, null));
  }

  @Test
  public void testDefaultOperationReturnsFalse() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertFalse(delegate.defaultOperation("property", "literal", String.class, null));
    assertFalse(delegate.defaultOperation(null, null, null, null));
  }

  @Test
  public void testAndWithAllTrue() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertTrue(delegate.and(Arrays.asList(true, true, true)));
  }

  @Test
  public void testAndWithSomeTrue() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertTrue(delegate.and(Arrays.asList(true, false, false)));
    assertTrue(delegate.and(Arrays.asList(false, true, false)));
    assertTrue(delegate.and(Arrays.asList(false, false, true)));
  }

  @Test
  public void testAndWithAllFalse() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertFalse(delegate.and(Arrays.asList(false, false, false)));
  }

  @Test
  public void testAndWithEmptyList() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertFalse(delegate.and(Collections.emptyList()));
  }

  @Test
  public void testOrWithAllTrue() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertTrue(delegate.or(Arrays.asList(true, true, true)));
  }

  @Test
  public void testOrWithSomeTrue() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertTrue(delegate.or(Arrays.asList(true, false, false)));
    assertTrue(delegate.or(Arrays.asList(false, true, false)));
    assertTrue(delegate.or(Arrays.asList(false, false, true)));
  }

  @Test
  public void testOrWithAllFalse() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertFalse(delegate.or(Arrays.asList(false, false, false)));
  }

  @Test
  public void testOrWithEmptyList() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertFalse(delegate.or(Collections.emptyList()));
  }

  @Test
  public void testNotWithTrue() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertTrue(delegate.not(true));
  }

  @Test
  public void testNotWithFalse() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertFalse(delegate.not(false));
  }

  @Test
  public void testComparisonOperationWithMatchingProperty() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("title", "description");
    assertTrue(
        delegate.comparisonOperation(
            "title", "value", String.class, ComparisonPropertyOperation.IS_EQUAL_TO));
    assertTrue(
        delegate.comparisonOperation(
            "description", "value", String.class, ComparisonPropertyOperation.IS_EQUAL_TO));
  }

  @Test
  public void testComparisonOperationWithNonMatchingProperty() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("title", "description");
    assertFalse(
        delegate.comparisonOperation(
            "author", "value", String.class, ComparisonPropertyOperation.IS_EQUAL_TO));
  }

  @Test
  public void testComparisonOperationWithNullLiteral() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertTrue(delegate.comparisonOperation("property", null, null, null));
  }

  @Test
  public void testComparisonOperationWithDifferentLiteralTypes() {
    PropertyExistsDelegate delegate = new PropertyExistsDelegate("property");
    assertTrue(delegate.comparisonOperation("property", 123, Integer.class, null));
    assertTrue(delegate.comparisonOperation("property", 45.67, Double.class, null));
    assertTrue(delegate.comparisonOperation("property", true, Boolean.class, null));
  }
}
