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
package ddf.catalog.filter.delegate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.filter.ArgumentBuilder;
import ddf.catalog.filter.AttributeBuilder;
import ddf.catalog.filter.BufferedSpatialExpressionBuilder;
import ddf.catalog.filter.ContextualExpressionBuilder;
import ddf.catalog.filter.EqualityExpressionBuilder;
import ddf.catalog.filter.ExpressionBuilder;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.NumericalExpressionBuilder;
import ddf.catalog.filter.NumericalRangeExpressionBuilder;
import ddf.catalog.filter.SpatialExpressionBuilder;
import ddf.catalog.filter.TemporalInstantExpressionBuilder;
import ddf.catalog.filter.TemporalRangeExpressionBuilder;
import ddf.catalog.filter.XPathBasicBuilder;
import ddf.catalog.filter.XPathBuilder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.geotools.api.filter.And;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.Not;
import org.geotools.api.filter.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CopyFilterDelegateTest {

  private static final String PROPERTY_NAME = "testProperty";
  private static final String TEST_WKT = "POINT(0 0)";
  private static final String TEST_PATTERN = "test*";
  private static final String TEST_LITERAL = "testLiteral";
  private static final String TEST_XPATH = "/test/path";

  @Mock private FilterBuilder filterBuilder;
  @Mock private AttributeBuilder attributeBuilder;
  @Mock private ExpressionBuilder expressionBuilder;
  @Mock private EqualityExpressionBuilder equalityBuilder;
  @Mock private NumericalExpressionBuilder numericalBuilder;
  @Mock private NumericalRangeExpressionBuilder rangeBuilder;
  @Mock private ContextualExpressionBuilder contextualBuilder;
  @Mock private SpatialExpressionBuilder spatialBuilder;
  @Mock private BufferedSpatialExpressionBuilder bufferedSpatialBuilder;
  @Mock private TemporalInstantExpressionBuilder temporalInstantBuilder;
  @Mock private TemporalRangeExpressionBuilder temporalRangeBuilder;
  @Mock private XPathBuilder xPathBuilder;
  @Mock private XPathBasicBuilder xPathBasicBuilder;
  @Mock private ArgumentBuilder argumentBuilder;
  @Mock private Filter mockFilter;
  @Mock private And mockAnd;
  @Mock private Or mockOr;
  @Mock private Not mockNot;

  private CopyFilterDelegate delegate;

  @BeforeEach
  void setUp() {
    // Setup the builder chain for attribute operations
    when(filterBuilder.attribute(anyString())).thenReturn(attributeBuilder);
    when(attributeBuilder.is()).thenReturn(expressionBuilder);
    when(attributeBuilder.equalTo()).thenReturn(equalityBuilder);
    when(attributeBuilder.notEqualTo()).thenReturn(equalityBuilder);
    when(attributeBuilder.greaterThan()).thenReturn(numericalBuilder);
    when(attributeBuilder.greaterThanOrEqualTo()).thenReturn(numericalBuilder);
    when(attributeBuilder.lessThan()).thenReturn(numericalBuilder);
    when(attributeBuilder.lessThanOrEqualTo()).thenReturn(numericalBuilder);
    when(attributeBuilder.between()).thenReturn(rangeBuilder);

    // Setup expression builder chains
    when(expressionBuilder.empty()).thenReturn(mockFilter);
    when(expressionBuilder.like()).thenReturn(contextualBuilder);
    when(expressionBuilder.beyond()).thenReturn(bufferedSpatialBuilder);
    when(expressionBuilder.containing()).thenReturn(spatialBuilder);
    when(expressionBuilder.withinBuffer()).thenReturn(bufferedSpatialBuilder);
    when(expressionBuilder.intersecting()).thenReturn(spatialBuilder);
    when(expressionBuilder.within()).thenReturn(spatialBuilder);
    when(expressionBuilder.after()).thenReturn(temporalInstantBuilder);
    when(expressionBuilder.before()).thenReturn(temporalInstantBuilder);
    when(expressionBuilder.during()).thenReturn(temporalRangeBuilder);

    // Setup equality builder
    when(equalityBuilder.text(anyString())).thenReturn(mockFilter);
    when(equalityBuilder.number(anyInt())).thenReturn(mockFilter);
    when(equalityBuilder.number(anyShort())).thenReturn(mockFilter);
    when(equalityBuilder.number(anyLong())).thenReturn(mockFilter);
    when(equalityBuilder.number(anyFloat())).thenReturn(mockFilter);
    when(equalityBuilder.number(anyDouble())).thenReturn(mockFilter);
    when(equalityBuilder.date(any(Date.class))).thenReturn(mockFilter);
    when(equalityBuilder.bool(anyBoolean())).thenReturn(mockFilter);
    when(equalityBuilder.bytes(any(byte[].class))).thenReturn(mockFilter);

    // Setup numerical builder
    when(numericalBuilder.number(anyInt())).thenReturn(mockFilter);
    when(numericalBuilder.number(anyShort())).thenReturn(mockFilter);
    when(numericalBuilder.number(anyLong())).thenReturn(mockFilter);
    when(numericalBuilder.number(anyFloat())).thenReturn(mockFilter);
    when(numericalBuilder.number(anyDouble())).thenReturn(mockFilter);

    // Setup range builder
    when(rangeBuilder.numbers(anyInt(), anyInt())).thenReturn(mockFilter);
    when(rangeBuilder.numbers(anyShort(), anyShort())).thenReturn(mockFilter);
    when(rangeBuilder.numbers(anyLong(), anyLong())).thenReturn(mockFilter);
    when(rangeBuilder.numbers(anyFloat(), anyFloat())).thenReturn(mockFilter);
    when(rangeBuilder.numbers(anyDouble(), anyDouble())).thenReturn(mockFilter);

    // Setup contextual builder
    when(contextualBuilder.text(anyString())).thenReturn(mockFilter);
    when(contextualBuilder.caseSensitiveText(anyString())).thenReturn(mockFilter);
    when(contextualBuilder.fuzzyText(anyString())).thenReturn(mockFilter);

    // Setup spatial builders
    when(spatialBuilder.wkt(anyString())).thenReturn(mockFilter);
    when(bufferedSpatialBuilder.wkt(anyString(), anyDouble())).thenReturn(mockFilter);

    // Setup temporal builders
    when(temporalInstantBuilder.date(any(Date.class))).thenReturn(mockFilter);
    when(temporalRangeBuilder.dates(any(Date.class), any(Date.class))).thenReturn(mockFilter);
    when(temporalRangeBuilder.last(anyLong())).thenReturn(mockFilter);

    // Setup xpath builder
    when(filterBuilder.xpath(anyString())).thenReturn(xPathBuilder);
    when(xPathBuilder.exists()).thenReturn(mockFilter);
    when(xPathBuilder.is()).thenReturn(xPathBasicBuilder);
    when(xPathBasicBuilder.like()).thenReturn(contextualBuilder);

    // Setup function builder
    when(filterBuilder.function(anyString())).thenReturn(argumentBuilder);
    when(argumentBuilder.attributeArg(anyString())).thenReturn(argumentBuilder);
    when(argumentBuilder.objArg(any())).thenReturn(argumentBuilder);
    when(argumentBuilder.equalTo()).thenReturn(equalityBuilder);

    // Setup logical operators
    when(filterBuilder.not(any(Filter.class))).thenReturn(mockNot);
    when(filterBuilder.allOf(any(List.class))).thenReturn(mockAnd);
    when(filterBuilder.anyOf(any(List.class))).thenReturn(mockOr);

    delegate = new CopyFilterDelegate(filterBuilder);
  }

  // Logical operators tests
  @Test
  public void testInclude() {
    Filter result = delegate.include();
    assertThat(result, is(Filter.INCLUDE));
  }

  @Test
  public void testExclude() {
    Filter result = delegate.exclude();
    assertThat(result, is(Filter.EXCLUDE));
  }

  @Test
  public void testNot() {
    Filter result = delegate.not(mockFilter);
    assertThat(result, is(mockNot));
    verify(filterBuilder).not(mockFilter);
  }

  @Test
  public void testAnd() {
    List<Filter> filters = Arrays.asList(mockFilter, mockFilter);
    Filter result = delegate.and(filters);
    assertThat(result, is(mockAnd));
    verify(filterBuilder).allOf(filters);
  }

  @Test
  public void testOr() {
    List<Filter> filters = Arrays.asList(mockFilter, mockFilter);
    Filter result = delegate.or(filters);
    assertThat(result, is(mockOr));
    verify(filterBuilder).anyOf(filters);
  }

  // PropertyIsNull test
  @Test
  public void testPropertyIsNull() {
    Filter result = delegate.propertyIsNull(PROPERTY_NAME);
    assertThat(result, is(notNullValue()));
  }

  // PropertyIsLike tests
  @Test
  public void testPropertyIsLikeCaseSensitive() {
    Filter result = delegate.propertyIsLike(PROPERTY_NAME, TEST_PATTERN, true);
    assertThat(result, is(notNullValue()));
    verify(contextualBuilder).caseSensitiveText(TEST_PATTERN);
  }

  @Test
  public void testPropertyIsLikeCaseInsensitive() {
    Filter result = delegate.propertyIsLike(PROPERTY_NAME, TEST_PATTERN, false);
    assertThat(result, is(notNullValue()));
    verify(contextualBuilder).text(TEST_PATTERN);
  }

  @Test
  public void testPropertyIsFuzzy() {
    Filter result = delegate.propertyIsFuzzy(PROPERTY_NAME, TEST_LITERAL);
    assertThat(result, is(notNullValue()));
    verify(contextualBuilder).fuzzyText(TEST_LITERAL);
  }

  // PropertyIsEqualTo tests
  @Test
  public void testPropertyIsEqualToString() {
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, TEST_LITERAL, true);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToDate() {
    Date date = new Date();
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, date);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToDateRange() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsEqualTo(PROPERTY_NAME, new Date(), new Date()));
  }

  @Test
  public void testPropertyIsEqualToInt() {
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToShort() {
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, (short) 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToLong() {
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, 42L);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToDouble() {
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, 42.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFloat() {
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, 42.0f);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToBytes() {
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, new byte[] {1, 2, 3});
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToBoolean() {
    Filter result = delegate.propertyIsEqualTo(PROPERTY_NAME, true);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToObject() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsEqualTo(PROPERTY_NAME, new Object()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithStringLiteral() {
    Filter result =
        delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), "stringLiteral");
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithBooleanLiteral() {
    Filter result =
        delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), Boolean.TRUE);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithLongLiteral() {
    Filter result = delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), 100L);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithIntegerLiteral() {
    Filter result = delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), 100);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithDoubleLiteral() {
    Filter result = delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), 100.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithFloatLiteral() {
    Filter result = delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), 100.0f);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithShortLiteral() {
    Filter result =
        delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), (short) 100);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithDateLiteral() {
    Filter result =
        delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), new Date());
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsEqualToFunctionWithUnsupportedLiteral() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsEqualTo("testFunc", Arrays.asList("attr", "val1"), new Object()));
  }

  // PropertyIsNotEqualTo tests
  @Test
  public void testPropertyIsNotEqualToString() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, TEST_LITERAL, true);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToDate() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, new Date());
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToDateRange() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsNotEqualTo(PROPERTY_NAME, new Date(), new Date()));
  }

  @Test
  public void testPropertyIsNotEqualToInt() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToShort() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, (short) 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToLong() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, 42L);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToDouble() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, 42.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToFloat() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, 42.0f);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToBytes() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, new byte[] {1, 2, 3});
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToBoolean() {
    Filter result = delegate.propertyIsNotEqualTo(PROPERTY_NAME, true);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsNotEqualToObject() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsNotEqualTo(PROPERTY_NAME, new Object()));
  }

  // PropertyIsGreaterThan tests
  @Test
  public void testPropertyIsGreaterThanString() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsGreaterThan(PROPERTY_NAME, TEST_LITERAL));
  }

  @Test
  public void testPropertyIsGreaterThanDate() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsGreaterThan(PROPERTY_NAME, new Date()));
  }

  @Test
  public void testPropertyIsGreaterThanInt() {
    Filter result = delegate.propertyIsGreaterThan(PROPERTY_NAME, 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanShort() {
    Filter result = delegate.propertyIsGreaterThan(PROPERTY_NAME, (short) 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanLong() {
    Filter result = delegate.propertyIsGreaterThan(PROPERTY_NAME, 42L);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanDouble() {
    Filter result = delegate.propertyIsGreaterThan(PROPERTY_NAME, 42.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanFloat() {
    Filter result = delegate.propertyIsGreaterThan(PROPERTY_NAME, 42.0f);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanObject() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsGreaterThan(PROPERTY_NAME, new Object()));
  }

  // PropertyIsGreaterThanOrEqualTo tests
  @Test
  public void testPropertyIsGreaterThanOrEqualToString() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsGreaterThanOrEqualTo(PROPERTY_NAME, TEST_LITERAL));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToDate() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsGreaterThanOrEqualTo(PROPERTY_NAME, new Date()));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToInt() {
    Filter result = delegate.propertyIsGreaterThanOrEqualTo(PROPERTY_NAME, 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToShort() {
    Filter result = delegate.propertyIsGreaterThanOrEqualTo(PROPERTY_NAME, (short) 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToLong() {
    Filter result = delegate.propertyIsGreaterThanOrEqualTo(PROPERTY_NAME, 42L);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToDouble() {
    Filter result = delegate.propertyIsGreaterThanOrEqualTo(PROPERTY_NAME, 42.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToFloat() {
    Filter result = delegate.propertyIsGreaterThanOrEqualTo(PROPERTY_NAME, 42.0f);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsGreaterThanOrEqualToObject() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsGreaterThanOrEqualTo(PROPERTY_NAME, new Object()));
  }

  // PropertyIsLessThan tests
  @Test
  public void testPropertyIsLessThanString() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsLessThan(PROPERTY_NAME, TEST_LITERAL));
  }

  @Test
  public void testPropertyIsLessThanDate() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsLessThan(PROPERTY_NAME, new Date()));
  }

  @Test
  public void testPropertyIsLessThanInt() {
    Filter result = delegate.propertyIsLessThan(PROPERTY_NAME, 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanShort() {
    Filter result = delegate.propertyIsLessThan(PROPERTY_NAME, (short) 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanLong() {
    Filter result = delegate.propertyIsLessThan(PROPERTY_NAME, 42L);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanDouble() {
    Filter result = delegate.propertyIsLessThan(PROPERTY_NAME, 42.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanFloat() {
    Filter result = delegate.propertyIsLessThan(PROPERTY_NAME, 42.0f);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanObject() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsLessThan(PROPERTY_NAME, new Object()));
  }

  // PropertyIsLessThanOrEqualTo tests
  @Test
  public void testPropertyIsLessThanOrEqualToString() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsLessThanOrEqualTo(PROPERTY_NAME, TEST_LITERAL));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToDate() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsLessThanOrEqualTo(PROPERTY_NAME, new Date()));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToInt() {
    Filter result = delegate.propertyIsLessThanOrEqualTo(PROPERTY_NAME, 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToShort() {
    Filter result = delegate.propertyIsLessThanOrEqualTo(PROPERTY_NAME, (short) 42);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToLong() {
    Filter result = delegate.propertyIsLessThanOrEqualTo(PROPERTY_NAME, 42L);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToDouble() {
    Filter result = delegate.propertyIsLessThanOrEqualTo(PROPERTY_NAME, 42.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToFloat() {
    Filter result = delegate.propertyIsLessThanOrEqualTo(PROPERTY_NAME, 42.0f);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsLessThanOrEqualToObject() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsLessThanOrEqualTo(PROPERTY_NAME, new Object()));
  }

  // PropertyIsBetween tests
  @Test
  public void testPropertyIsBetweenString() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsBetween(PROPERTY_NAME, "a", "z"));
  }

  @Test
  public void testPropertyIsBetweenDate() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsBetween(PROPERTY_NAME, new Date(), new Date()));
  }

  @Test
  public void testPropertyIsBetweenInt() {
    Filter result = delegate.propertyIsBetween(PROPERTY_NAME, 1, 100);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsBetweenShort() {
    Filter result = delegate.propertyIsBetween(PROPERTY_NAME, (short) 1, (short) 100);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsBetweenLong() {
    Filter result = delegate.propertyIsBetween(PROPERTY_NAME, 1L, 100L);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsBetweenFloat() {
    Filter result = delegate.propertyIsBetween(PROPERTY_NAME, 1.0f, 100.0f);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsBetweenDouble() {
    Filter result = delegate.propertyIsBetween(PROPERTY_NAME, 1.0, 100.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testPropertyIsBetweenObject() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> delegate.propertyIsBetween(PROPERTY_NAME, new Object(), new Object()));
  }

  // XPath tests
  @Test
  public void testXpathExists() {
    Filter result = delegate.xpathExists(TEST_XPATH);
    assertThat(result, is(notNullValue()));
    verify(filterBuilder).xpath(TEST_XPATH);
    verify(xPathBuilder).exists();
  }

  @Test
  public void testXpathIsLikeCaseSensitive() {
    Filter result = delegate.xpathIsLike(TEST_XPATH, TEST_PATTERN, true);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testXpathIsLikeCaseInsensitive() {
    Filter result = delegate.xpathIsLike(TEST_XPATH, TEST_PATTERN, false);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testXpathIsFuzzy() {
    Filter result = delegate.xpathIsFuzzy(TEST_XPATH, TEST_LITERAL);
    assertThat(result, is(notNullValue()));
  }

  // Spatial filter tests
  @Test
  public void testBeyond() {
    Filter result = delegate.beyond(PROPERTY_NAME, TEST_WKT, 100.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testContains() {
    Filter result = delegate.contains(PROPERTY_NAME, TEST_WKT);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testDwithin() {
    Filter result = delegate.dwithin(PROPERTY_NAME, TEST_WKT, 100.0);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testIntersects() {
    Filter result = delegate.intersects(PROPERTY_NAME, TEST_WKT);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testWithin() {
    Filter result = delegate.within(PROPERTY_NAME, TEST_WKT);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testCrosses() {
    assertThrows(
        UnsupportedOperationException.class, () -> delegate.crosses(PROPERTY_NAME, TEST_WKT));
  }

  @Test
  public void testDisjoint() {
    assertThrows(
        UnsupportedOperationException.class, () -> delegate.disjoint(PROPERTY_NAME, TEST_WKT));
  }

  @Test
  public void testOverlaps() {
    assertThrows(
        UnsupportedOperationException.class, () -> delegate.overlaps(PROPERTY_NAME, TEST_WKT));
  }

  @Test
  public void testTouches() {
    assertThrows(
        UnsupportedOperationException.class, () -> delegate.touches(PROPERTY_NAME, TEST_WKT));
  }

  // Temporal filter tests
  @Test
  public void testAfter() {
    Date date = new Date();
    Filter result = delegate.after(PROPERTY_NAME, date);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testBefore() {
    Date date = new Date();
    Filter result = delegate.before(PROPERTY_NAME, date);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testDuring() {
    Date start = new Date();
    Date end = new Date();
    Filter result = delegate.during(PROPERTY_NAME, start, end);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testRelative() {
    Filter result = delegate.relative(PROPERTY_NAME, 3600000L);
    assertThat(result, is(notNullValue()));
  }
}
