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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import ddf.catalog.operation.Operation;
import ddf.catalog.operation.Query;
import ddf.catalog.operation.QueryRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Comprehensive test suite for {@link QueryRequestImpl} class.
 *
 * <p>Tests all constructors, getters, properties handling (from OperationImpl), and edge cases.
 * Also validates the Operation interface implementation.
 */
@RunWith(MockitoJUnitRunner.class)
public class QueryRequestImplTest {

  private static final String SOURCE_ID_1 = "source-1";
  private static final String SOURCE_ID_2 = "source-2";
  private static final String SOURCE_ID_3 = "source-3";

  @Mock private Query mockQuery;

  private QueryRequestImpl queryRequest;
  private Map<String, Serializable> properties;

  @Before
  public void setUp() {
    properties = new HashMap<>();
    properties.put("key1", "value1");
    properties.put("key2", 42);
  }

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with Query only.
   *
   * <p>Verifies basic construction with minimal parameters.
   */
  @Test
  public void testConstructorWithQueryOnly() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery);

    assertThat(request, is(notNullValue()));
    assertThat(request.getQuery(), is(equalTo(mockQuery)));
    assertThat(request.isEnterprise(), is(false));
    assertThat(request.getSourceIds(), is(nullValue()));
    assertThat(request.hasProperties(), is(false));
  }

  /**
   * Tests constructor with Query and properties.
   *
   * <p>Verifies properties are properly initialized.
   */
  @Test
  public void testConstructorWithQueryAndProperties() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    assertThat(request.getQuery(), is(equalTo(mockQuery)));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("key1"), is(equalTo("value1")));
    assertThat(request.getPropertyValue("key2"), is(equalTo(42)));
    assertThat(request.isEnterprise(), is(false));
  }

  /**
   * Tests constructor with Query and source IDs.
   *
   * <p>Verifies source IDs are properly stored.
   */
  @Test
  public void testConstructorWithQueryAndSourceIds() {
    List<String> sourceIds = Arrays.asList(SOURCE_ID_1, SOURCE_ID_2);
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, sourceIds);

    assertThat(request.getQuery(), is(equalTo(mockQuery)));
    assertThat(request.getSourceIds(), is(notNullValue()));
    assertThat(request.getSourceIds(), hasSize(2));
    assertThat(request.getSourceIds(), containsInAnyOrder(SOURCE_ID_1, SOURCE_ID_2));
    assertThat(request.isEnterprise(), is(false));
  }

  /**
   * Tests constructor with Query and enterprise flag.
   *
   * <p>Verifies enterprise mode is properly set.
   */
  @Test
  public void testConstructorWithQueryAndEnterpriseFlag() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, true);

    assertThat(request.getQuery(), is(equalTo(mockQuery)));
    assertThat(request.isEnterprise(), is(true));
    assertThat(request.getSourceIds(), is(nullValue()));
  }

  /**
   * Tests full constructor with all parameters.
   *
   * <p>Verifies complete initialization with Query, enterprise flag, sources, and properties.
   */
  @Test
  public void testFullConstructor() {
    List<String> sourceIds = Arrays.asList(SOURCE_ID_1, SOURCE_ID_2, SOURCE_ID_3);
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, false, sourceIds, properties);

    assertThat(request.getQuery(), is(equalTo(mockQuery)));
    assertThat(request.isEnterprise(), is(false));
    assertThat(request.getSourceIds(), hasSize(3));
    assertThat(request.getSourceIds(), containsInAnyOrder(SOURCE_ID_1, SOURCE_ID_2, SOURCE_ID_3));
    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("key1"), is(equalTo("value1")));
  }

  /**
   * Tests constructor with Set of source IDs.
   *
   * <p>Verifies that when source IDs are already a Set, it's used directly.
   */
  @Test
  public void testConstructorWithSetOfSourceIds() {
    Set<String> sourceSet = new HashSet<>(Arrays.asList(SOURCE_ID_1, SOURCE_ID_2));
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, false, sourceSet, null);

    assertThat(request.getSourceIds(), is(equalTo(sourceSet)));
  }

  /**
   * Tests constructor with null source IDs.
   *
   * <p>Verifies that null source IDs are handled correctly.
   */
  @Test
  public void testConstructorWithNullSourceIds() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, false, null, properties);

    assertThat(request.getSourceIds(), is(nullValue()));
  }

  /**
   * Tests constructor with empty source IDs list.
   *
   * <p>Verifies that empty source list is converted to empty Set.
   */
  @Test
  public void testConstructorWithEmptySourceIds() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, new ArrayList<>());

    assertThat(request.getSourceIds(), is(notNullValue()));
    assertThat(request.getSourceIds(), is(empty()));
  }

  /**
   * Tests constructor with null properties.
   *
   * <p>Verifies that null properties are handled (OperationImpl initializes empty map).
   */
  @Test
  public void testConstructorWithNullProperties() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, (Map<String, Serializable>) null);

    assertThat(request.hasProperties(), is(false));
    assertThat(request.getProperties(), is(notNullValue()));
  }

  /**
   * Tests constructor with null Query.
   *
   * <p>Verifies that null Query is accepted (validation may be done elsewhere).
   */
  @Test
  public void testConstructorWithNullQuery() {
    QueryRequestImpl request = new QueryRequestImpl(null);

    assertThat(request, is(notNullValue()));
    assertThat(request.getQuery(), is(nullValue()));
  }

  // ====================
  // Query Tests
  // ====================

  /**
   * Tests getQuery returns correct Query.
   *
   * <p>Verifies Query retrieval.
   */
  @Test
  public void testGetQuery() {
    when(mockQuery.getPageSize()).thenReturn(10);
    QueryRequestImpl request = new QueryRequestImpl(mockQuery);

    Query retrieved = request.getQuery();

    assertThat(retrieved, is(equalTo(mockQuery)));
    assertThat(retrieved.getPageSize(), is(equalTo(10)));
  }

  // ====================
  // Enterprise Mode Tests
  // ====================

  /**
   * Tests isEnterprise returns false by default.
   *
   * <p>Verifies default enterprise mode.
   */
  @Test
  public void testIsEnterpriseDefaultsFalse() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery);

    assertThat(request.isEnterprise(), is(false));
  }

  /**
   * Tests isEnterprise returns true when set.
   *
   * <p>Verifies enterprise mode can be enabled.
   */
  @Test
  public void testIsEnterpriseWhenTrue() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, true);

    assertThat(request.isEnterprise(), is(true));
  }

  /**
   * Tests enterprise mode with source IDs.
   *
   * <p>Verifies enterprise mode and source IDs can coexist (though unusual).
   */
  @Test
  public void testEnterpriseModeWithSourceIds() {
    List<String> sourceIds = Arrays.asList(SOURCE_ID_1);
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, true, sourceIds, null);

    assertThat(request.isEnterprise(), is(true));
    assertThat(request.getSourceIds(), hasSize(1));
  }

  // ====================
  // Source IDs Tests
  // ====================

  /**
   * Tests getSourceIds with single source.
   *
   * <p>Verifies single source handling.
   */
  @Test
  public void testGetSourceIdsWithSingleSource() {
    List<String> sourceIds = Arrays.asList(SOURCE_ID_1);
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, sourceIds);

    assertThat(request.getSourceIds(), hasSize(1));
    assertThat(request.getSourceIds(), containsInAnyOrder(SOURCE_ID_1));
  }

  /**
   * Tests getSourceIds with multiple sources.
   *
   * <p>Verifies multiple source handling.
   */
  @Test
  public void testGetSourceIdsWithMultipleSources() {
    List<String> sourceIds = Arrays.asList(SOURCE_ID_1, SOURCE_ID_2, SOURCE_ID_3);
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, sourceIds);

    assertThat(request.getSourceIds(), hasSize(3));
    assertThat(request.getSourceIds(), containsInAnyOrder(SOURCE_ID_1, SOURCE_ID_2, SOURCE_ID_3));
  }

  /**
   * Tests source IDs with duplicates.
   *
   * <p>Verifies that duplicates are removed (Set behavior).
   */
  @Test
  public void testSourceIdsWithDuplicates() {
    List<String> sourceIdsWithDupes = Arrays.asList(SOURCE_ID_1, SOURCE_ID_2, SOURCE_ID_1);
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, sourceIdsWithDupes);

    // Set should remove duplicates
    assertThat(request.getSourceIds(), hasSize(2));
    assertThat(request.getSourceIds(), containsInAnyOrder(SOURCE_ID_1, SOURCE_ID_2));
  }

  // ====================
  // Properties Tests (from OperationImpl)
  // ====================

  /**
   * Tests hasProperties returns true when properties exist.
   *
   * <p>Verifies property existence check.
   */
  @Test
  public void testHasPropertiesReturnsTrue() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    assertThat(request.hasProperties(), is(true));
  }

  /**
   * Tests hasProperties returns false when no properties.
   *
   * <p>Verifies empty properties check.
   */
  @Test
  public void testHasPropertiesReturnsFalse() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery);

    assertThat(request.hasProperties(), is(false));
  }

  /**
   * Tests getPropertyValue retrieves correct value.
   *
   * <p>Verifies individual property retrieval.
   */
  @Test
  public void testGetPropertyValue() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    assertThat(request.getPropertyValue("key1"), is(equalTo("value1")));
    assertThat(request.getPropertyValue("key2"), is(equalTo(42)));
  }

  /**
   * Tests getPropertyValue with non-existent key returns null.
   *
   * <p>Verifies missing property handling.
   */
  @Test
  public void testGetPropertyValueNonExistent() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    assertThat(request.getPropertyValue("nonexistent"), is(nullValue()));
  }

  /**
   * Tests containsPropertyName returns true for existing property.
   *
   * <p>Verifies property name check.
   */
  @Test
  public void testContainsPropertyNameReturnsTrue() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    assertThat(request.containsPropertyName("key1"), is(true));
  }

  /**
   * Tests containsPropertyName returns false for missing property.
   *
   * <p>Verifies property absence check.
   */
  @Test
  public void testContainsPropertyNameReturnsFalse() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    assertThat(request.containsPropertyName("missing"), is(false));
  }

  /**
   * Tests getPropertyNames returns all property keys.
   *
   * <p>Verifies retrieval of all property names.
   */
  @Test
  public void testGetPropertyNames() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    Set<String> propertyNames = request.getPropertyNames();

    assertThat(propertyNames, hasSize(2));
    assertThat(propertyNames, containsInAnyOrder("key1", "key2"));
  }

  /**
   * Tests getProperties returns properties map.
   *
   * <p>Verifies full properties map retrieval.
   */
  @Test
  public void testGetProperties() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    Map<String, Serializable> retrieved = request.getProperties();

    assertThat(retrieved, is(notNullValue()));
    assertThat(retrieved.size(), is(equalTo(2)));
    assertThat(retrieved.get("key1"), is(equalTo("value1")));
    assertThat(retrieved.get("key2"), is(equalTo(42)));
  }

  /**
   * Tests properties with various serializable types.
   *
   * <p>Verifies that different serializable types are handled.
   */
  @Test
  public void testPropertiesWithVariousTypes() {
    Map<String, Serializable> variedProperties = new HashMap<>();
    variedProperties.put("string", "text");
    variedProperties.put("integer", 100);
    variedProperties.put("long", 1000L);
    variedProperties.put("boolean", true);
    variedProperties.put("double", 3.14);

    QueryRequestImpl request = new QueryRequestImpl(mockQuery, variedProperties);

    assertThat(request.getPropertyValue("string"), is(equalTo("text")));
    assertThat(request.getPropertyValue("integer"), is(equalTo(100)));
    assertThat(request.getPropertyValue("long"), is(equalTo(1000L)));
    assertThat(request.getPropertyValue("boolean"), is(equalTo(true)));
    assertThat(request.getPropertyValue("double"), is(equalTo(3.14)));
  }

  /**
   * Tests property with null value.
   *
   * <p>Verifies that null property values are supported.
   */
  @Test
  public void testPropertyWithNullValue() {
    Map<String, Serializable> propsWithNull = new HashMap<>();
    propsWithNull.put("nullKey", null);

    QueryRequestImpl request = new QueryRequestImpl(mockQuery, propsWithNull);

    assertThat(request.containsPropertyName("nullKey"), is(true));
    assertThat(request.getPropertyValue("nullKey"), is(nullValue()));
  }

  // ====================
  // ToString Test
  // ====================

  /**
   * Tests toString returns non-null value.
   *
   * <p>Verifies toString implementation.
   */
  @Test
  public void testToString() {
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, properties);

    String stringValue = request.toString();

    assertThat(stringValue, is(notNullValue()));
  }

  // ====================
  // Interface Compliance Tests
  // ====================

  /**
   * Tests QueryRequest interface compliance.
   *
   * <p>Verifies proper interface implementation.
   */
  @Test
  public void testQueryRequestInterfaceCompliance() {
    QueryRequest request = new QueryRequestImpl(mockQuery);

    assertThat(request, is(notNullValue()));
    assertThat(request.getQuery(), is(equalTo(mockQuery)));
    assertThat(request.isEnterprise(), is(false));
  }

  /**
   * Tests Operation interface compliance.
   *
   * <p>Verifies proper Operation interface implementation.
   */
  @Test
  public void testOperationInterfaceCompliance() {
    Operation operation = new QueryRequestImpl(mockQuery, properties);

    assertThat(operation, is(notNullValue()));
    assertThat(operation.hasProperties(), is(true));
    assertThat(operation.getPropertyValue("key1"), is(equalTo("value1")));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete federated query request.
   *
   * <p>Simulates a real federated query with multiple sources.
   */
  @Test
  public void testFederatedQueryRequest() {
    when(mockQuery.getPageSize()).thenReturn(20);

    List<String> sourceIds = Arrays.asList(SOURCE_ID_1, SOURCE_ID_2);
    Map<String, Serializable> props = new HashMap<>();
    props.put("timeout", 30000);

    QueryRequestImpl request = new QueryRequestImpl(mockQuery, false, sourceIds, props);

    assertThat(request.getQuery().getPageSize(), is(equalTo(20)));
    assertThat(request.isEnterprise(), is(false));
    assertThat(request.getSourceIds(), hasSize(2));
    assertThat(request.getPropertyValue("timeout"), is(equalTo(30000)));
  }

  /**
   * Tests enterprise query request.
   *
   * <p>Simulates an enterprise-wide query.
   */
  @Test
  public void testEnterpriseQueryRequest() {
    when(mockQuery.getPageSize()).thenReturn(50);

    QueryRequestImpl request = new QueryRequestImpl(mockQuery, true);

    assertThat(request.getQuery().getPageSize(), is(equalTo(50)));
    assertThat(request.isEnterprise(), is(true));
    assertThat(request.getSourceIds(), is(nullValue()));
  }

  /**
   * Tests single source query request.
   *
   * <p>Simulates querying a specific source.
   */
  @Test
  public void testSingleSourceQueryRequest() {
    List<String> singleSource = Arrays.asList(SOURCE_ID_1);
    QueryRequestImpl request = new QueryRequestImpl(mockQuery, singleSource);

    assertThat(request.getSourceIds(), hasSize(1));
    assertThat(request.getSourceIds().iterator().next(), is(equalTo(SOURCE_ID_1)));
    assertThat(request.isEnterprise(), is(false));
  }
}
